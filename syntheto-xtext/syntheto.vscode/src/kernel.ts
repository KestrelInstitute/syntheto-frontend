import { disposeEmitNodes } from 'typescript';
import * as vscode from 'vscode';
import { commands, window, workspace, ExtensionContext, Uri } from 'vscode';

interface CellExecutionRequest {
	meta?: string;
	code: string;
	allCellContent?: string;
}

interface CellExecutionResponse {
	type: string;
	code?: string;
	message?: string;
}

export class MNBKernel {

	private readonly _controller: vscode.NotebookController;
	private _executionOrder = 0;
	constructor() {
		this._controller = vscode.notebooks.createNotebookController(
			'MNBKernel',
			'mnb',
			'MIDAS NoteBook',
		);
        //TODO to allow proper language support, you need to implement one...
		this._controller.supportedLanguages = ['syntheto'];
		this._controller.supportsExecutionOrder = true;
		this._controller.description = 'any description really';
		this._controller.executeHandler = this._executeAll.bind(this);
        

	}

	dispose(): void {
		this._controller.dispose();
	}

    private _getRemoteServerURL (): string {
        const config = vscode.workspace.getConfiguration('midas');
        return <string>config.get('remoteExecutionServerAddress');
    }

	private _executeAll(cells: vscode.NotebookCell[]): void {
		for (const cell of cells) {
			this._doExecuteCell(cell);
		}
	}

    /*
    This function is responsible for executing a single cell.
    It is very plain in its current form and any real usage should enhance it
    following best practices: https://code.visualstudio.com/api/extension-guides/notebook#best-practices
    */
	private async _doExecuteCell(cell: vscode.NotebookCell): Promise<void> {
		const exec = this._controller.createNotebookCellExecution(cell);
		exec.executionOrder = ++this._executionOrder;
		exec.start(Date.now());
		const request = <CellExecutionRequest> {code: cell.document.getText()};

        try{
			/*let url = this._getRemoteServerURL();
            let response = await superagent
                .post(this._getRemoteServerURL()+'/execute')
                .send({code:cell.document.getText()});
            
            console.log(response.body);
            let executionResponse = <BasicExecutionResponseBody>response.body;
            if (executionResponse.error) {
                throw new Error(executionResponse.error);
            } else {
                exec.replaceOutput(new vscode.NotebookCellOutput([vscode.NotebookCellOutputItem.text(<string>executionResponse.result)]));
                exec.end(true, Date.now());
            }*/

			// Send the content of all cells 
			const editor = window.activeNotebookEditor;
			if (!editor) {
				window.showErrorMessage('There is no active notebook open?!?!');
				return;
			}

			const notebook = editor.document;
			const cells = notebook.getCells();
			let synthetoFile = '';
			let keepGoing = true;
			cells.forEach(currCell => {
				if (currCell.kind === vscode.NotebookCellKind.Code &&
					keepGoing) {
					synthetoFile += currCell.document.getText();
					synthetoFile += '\n';
					if (currCell.index >= cell.index) {
						keepGoing = false;
					}
				} 
			});
			
			request.allCellContent = synthetoFile;			
			let result = <CellExecutionResponse><unknown> await commands.executeCommand('midas.a', request);

			// window.showWarningMessage("" + result.message);
			// window.showWarningMessage("" + result.code);

			// window.showInformationMessage("RTYPE: " + result.type);
			if(result.type === 'success') {
				// regular success execution response, show message
				exec.replaceOutput(new vscode.NotebookCellOutput([vscode.NotebookCellOutputItem.text(result.message || "empty response")]));
                exec.end(true, Date.now());
			} else if (result.type === 'transformation') {
				// transformation response, show message, put code response into a new markdown cell right below the running cell
				let editResult = await editor.edit(builder => {
					window.showInformationMessage("we are in!!!");
					let newCells = <vscode.NotebookCellData[]> [];
					// newCells.push(new vscode.NotebookCellData(vscode.NotebookCellKind.Code,"new code cell", 'syntheto'));
					newCells.push(new vscode.NotebookCellData(
						vscode.NotebookCellKind.Markup,
						'```\n' + (result.code || "here should be the\n transformed function code") + '\n```',
						'markdown'));
					builder.replaceCells(cell.index + 1, cell.index + 1, newCells);
				});
				if (!editResult) {
					window.showWarningMessage('was unable to correctly add new cell!');
				}
				exec.replaceOutput(new vscode.NotebookCellOutput([vscode.NotebookCellOutputItem.text(result.message || "no response")]));
                exec.end(true, Date.now());
			} else {
				// TODO: if we will have more types we need to handle their processing 
				exec.replaceOutput(new vscode.NotebookCellOutput([vscode.NotebookCellOutputItem.text(result.message || "no response")]));
                exec.end(false);
			}
        } catch (e) {
            exec.replaceOutput(new vscode.NotebookCellOutput([vscode.NotebookCellOutputItem.error(e)]));
            exec.end(false);
        }
	}
}