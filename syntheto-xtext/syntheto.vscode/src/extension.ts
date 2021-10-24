'use strict';

import * as path from 'path';
import * as os from 'os';

import {Trace} from 'vscode-jsonrpc';
import * as vscode from 'vscode';
import { commands, window, workspace, ExtensionContext, Uri } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions } from 'vscode-languageclient';
import { RENOBSerializer } from './serializer';
import { MNBKernel } from './kernel';
import { registerCommands } from './commands';

export function activate(context: ExtensionContext) {
    // The server is a locally installed in src/syntheto
    let launcher = os.platform() === 'win32' ? 'syntheto-standalone.bat' : 'syntheto-standalone';
    let script = context.asAbsolutePath(path.join('src', 'syntheto', 'bin', launcher));

    let serverOptions: ServerOptions = {
        run : { command: script },
        debug: { command: script, args: [], options: { env: createDebugEnv() } }
    };
    
    let clientOptions: LanguageClientOptions = {
        documentSelector: ['syntheto'],
        outputChannelName: 'Syntheto',
        synchronize: {
            fileEvents: workspace.createFileSystemWatcher('**/*.synth')
        }
    };
    let orange = window.createOutputChannel("Syntheto");
    clientOptions.outputChannel = orange;
    // Create the language client and start the client.
    let lc = new LanguageClient('Syntheto', serverOptions, clientOptions);
    // let orange = window.createOutputChannel("Syntheto-Command");
    
    //var disposable2 = commands.registerCommand("midas.a.proxy", async () => {
        /*
        let activeEditor = window.activeTextEditor;
        if (!activeEditor || !activeEditor.document || activeEditor.document.languageId !== 'syntheto') {
            return;
        }

        if (activeEditor.document.uri instanceof Uri) {
            commands.executeCommand("syntheto.a", activeEditor.document.uri.toString());
        }
        */
    //   orange.appendLine("COMMAND - VSCODE - processing command a");
       /*let activeEditor = window.activeTextEditor;
        if (!activeEditor || !activeEditor.document || activeEditor.document.languageId !== 'syntheto') {
            orange.appendLine("COMMAND - VSCODE - something is missing to execute command a");
            return;
        }*/

    //    let result = await commands.executeCommand("midas.a", "some parameter");
        // commands.executeCommand("midas.a", "some parameter");
    //    orange.appendLine("COMMAND - VSCODE - response arrived to command a");
    //    orange.appendLine("COMMAND - VSCODE - result: " + result);
    //})
    //context.subscriptions.push(disposable2);
    
    // enable tracing (.Off, .Messages, Verbose)
    lc.trace = Trace.Messages;
    
    let disposable = lc.start();
    
    // Push the disposable to the context's subscriptions so that the 
    // client can be deactivated on extension deactivation
    context.subscriptions.push(disposable);

    context.subscriptions.push(new MNBKernel());
    context.subscriptions.push(workspace.registerNotebookSerializer('mnb', new RENOBSerializer(), {
        transientOutputs: true,
		transientCellMetadata: {
			inputCollapsed: true,
			outputCollapsed: true,
		}
	}));

    context.subscriptions.push(registerCommands(orange));

    // workspace.onDidOpenNotebookDocument(event => {
    //     throw Error("we got event");
    // });
}

function createDebugEnv() {
    return Object.assign({
        JAVA_OPTS:"-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n,quiet=y"
    }, process.env)
}