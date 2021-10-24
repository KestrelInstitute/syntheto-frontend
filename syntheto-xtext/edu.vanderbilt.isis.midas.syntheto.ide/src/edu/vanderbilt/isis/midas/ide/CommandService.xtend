package edu.vanderbilt.isis.midas.ide;

import org.eclipse.xtext.ide.server.commands.IExecutableCommandService;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.generator.IGenerator2;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.gson.*;
import com.google.inject.Inject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IFileSystemAccess2

import edu.vanderbilt.isis.midas.generator.SynthetoGenerator
import com.google.inject.Provider
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.common.util.URI
import java.io.File
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.io.ByteArrayInputStream
import java.io.InputStream
import org.eclipse.xtext.resource.XtextResourceSet
import org.eclipse.xtext.resource.XtextResource

class CommandService implements IExecutableCommandService {

	var Logger LOG = Logger.getLogger(CommandService.getName());

//	@Inject	Provider<ResourceSet> rsp
	@Inject IGenerator2 generator;
	@Inject private XtextResourceSet rs;
	
	static var isTest = false;

	override initialize() {
		#["midas.a", "midas.b"]
	}

	override execute(ExecuteCommandParams params, ILanguageServerAccess access, CancelIndicator cancelIndicator) {
		/*if (params.command == "midas.a") {
			val uri = params.arguments.head as String
			if (uri !== null) {
				return access.doRead(uri) [
					return "Command A"
				].get
			} else {
				return "Param Uri Missing"
			}
		} else if (params.command == "midas.b") {
			return "Command B"
		}*/
				
		if (params.command == "midas.a") {
			rs.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);

			if (isTest) {
				return test(params, access, cancelIndicator);
			}
			
			var JsonObject object = params.arguments.head as JsonObject;
			var JsonPrimitive code = object.getAsJsonPrimitive("code");
			var JsonPrimitive allCellContent = object.getAsJsonPrimitive("allCellContent");

			var file1 = File.createTempFile("generated-syntheto-1-", ".synth")
			var file2 = File.createTempFile("generated-syntheto-2-", ".synth")			

			// stream1 contains current cell, stream2 contains all cells
			var stream1 = new ByteArrayInputStream(code.getAsString().getBytes(StandardCharsets.UTF_8));
			var stream2 = new ByteArrayInputStream(allCellContent.getAsString().getBytes(StandardCharsets.UTF_8));
											
			var resource1 = rs.createResource(URI.createURI("file:///" + file1.absolutePath))
			var resource2 = rs.createResource(URI.createURI("file:///" + file2.absolutePath))			
			
			resource1.load(stream1, rs.getLoadOptions())
			resource2.load(stream2, rs.getLoadOptions())
								
			var synthetoGenerator = generator as SynthetoGenerator;
			var JsonObject response = new JsonObject();
			
			if (!synthetoGenerator.checkResource(resource2, LOG)) {
				response.addProperty("message", "Constraint violations with resource");
				response.addProperty("type", "error");
				return response;
			}
			
			// For now, send all previous cells and the current cell to ACL2
			var result1 = synthetoGenerator.generate(resource2, LOG);
		    if (result1 == null) {
		        response.addProperty("type", "error");
		        response.addProperty("message", "Internal error");
		    } else {
			    response.addProperty("type", "success");
			    response.addProperty("code", result1);
			    response.addProperty("message", result1);
			}

			return response;
		}
		
		return "Bad Command"
	}
	
	def test(ExecuteCommandParams params, ILanguageServerAccess access, CancelIndicator cancelIndicator) {		
		var JsonObject response = new JsonObject();

		// Hard code paths to file. file2 refers to a function defined in file1.
		var uri1 = URI.createURI("file:///" + "/Users/daniel/work/temp/midas/file1.synth");
		var uri2 = URI.createURI("file:///" + "/Users/daniel/work/temp/midas/file2.synth");

		var resource1 = rs.getResource(uri1, true);
		var resource2 = rs.getResource(uri2, true);
		
		var synthetoGenerator = generator as SynthetoGenerator;

		if (!synthetoGenerator.checkResource(resource1, LOG)) {
			response.addProperty("message", "Constraint violations with resource1");
			return response;
		}

		if (!synthetoGenerator.checkResource(resource2, LOG)) {
			response.addProperty("message", "Constraint violations with resource2");
			return response;
		}

		var result1 = synthetoGenerator.generate(resource1, LOG);
		var result2 = synthetoGenerator.generate(resource2, LOG);

		response.addProperty("type", "success");
		response.addProperty("code", "dummyCode");
		response.addProperty("message", result1 + "\nResult 2: \n" + result2);
		return response;
	}

}