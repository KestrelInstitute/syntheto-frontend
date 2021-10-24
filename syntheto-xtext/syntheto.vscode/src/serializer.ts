import * as vscode from 'vscode';
interface RawNotebook {
	metadata?: any;
	cells: RawNotebookCell[];
}
interface RawNotebookCell {
	language: string;
	value: string;
	kind: vscode.NotebookCellKind;
	editable?: boolean;
	outputMime?: string;
	outputData?: string;
}

declare class TextDecoder {
	decode(data: Uint8Array): string;
}

declare class TextEncoder {
	encode(data: string): Uint8Array;
}

export class RENOBSerializer implements vscode.NotebookSerializer {

	private readonly _decoder = new TextDecoder();
	private readonly _encoder = new TextEncoder();

	deserializeNotebook(data: Uint8Array): vscode.NotebookData {
		let contents = '';
		try {
			contents = this._decoder.decode(data);
		} catch {
		}

		let raw: RawNotebook;
		try {
			raw = <RawNotebook>JSON.parse(contents);
		} catch {
			//?
			raw = {cells:[]};
		}

        //TODO we need to dig into the format a bit more
        //for now, kind 1 is markdown and 2 is code
		const cells = raw.cells.map(item => {
			const cell = new vscode.NotebookCellData(
			item.kind,
			item.value,
			item.language);
			if(item.outputData && item.outputMime) {
				cell.outputs = [new vscode.NotebookCellOutput([new vscode.NotebookCellOutputItem(this._encoder.encode(item.outputData), item.outputMime)])];
			}
			return cell;
		});

		return new vscode.NotebookData(cells);
	}

	serializeNotebook(data: vscode.NotebookData): Uint8Array {

		let content :RawNotebook = {cells: []};
		const notebook = vscode.workspace.notebookDocuments[0]; //TODO we suppose that only one notebook is open...
		// const notebook = vscode.window.activeNotebookEditor?.document;
		// if (!notebook) {
			// return this._encoder.encode("BAD CONTENT");
		// }
		const cells = notebook.getCells();
		for (let cell of cells) {
			const rawCell = <RawNotebookCell> {};
			rawCell.kind = cell.kind;
			rawCell.language = <string>cell.document.languageId;
			rawCell.value = cell.document.getText();
			rawCell.editable = true;


			if (cell.outputs.length > 0) {
				rawCell.outputMime = cell.outputs[0].items[0].mime
				rawCell.outputData = this._decoder.decode(cell.outputs[0].items[0].data);
			}
			
			content.cells.push(rawCell);
		}
		return this._encoder.encode(JSON.stringify(content, undefined, 2));
	}
}
