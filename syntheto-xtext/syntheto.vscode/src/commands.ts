import * as vscode from 'vscode';
import * as fs from 'fs';


export function registerCommands(logger: vscode.OutputChannel): vscode.Disposable {
    logger.appendLine('register commands');
    const subscriptions: vscode.Disposable[] = [];

    subscriptions.push(vscode.commands.registerCommand('midas.exportSyntheto', async () => {
        logger.appendLine('command - export Syntheto');

        const notebook = vscode.workspace.notebookDocuments[0]; //TODO cannot get active notebook :/
        const cells = notebook.getCells();
        let synthetoFile = '';
        cells.forEach(cell => {
            if (cell.kind === vscode.NotebookCellKind.Code) {
                synthetoFile += cell.document.getText();
                synthetoFile += '\n';
            }
        });
        const outputInfo = await vscode.window.showSaveDialog({filters:{'Syntheto':['synth']}, title: 'Save your project into a singular Syntheto file'});
        if (outputInfo?.fsPath) {
            fs.writeFileSync(outputInfo.fsPath, synthetoFile, "utf8");
            vscode.window.showInformationMessage('File exported successfully...');
        }
	}));

    return vscode.Disposable.from(...subscriptions);
}