/*
 * generated by Xtext 2.23.0
 */
package edu.vanderbilt.isis.midas.tests

import com.google.inject.Inject
import edu.vanderbilt.isis.midas.syntheto.Program
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.xtext.IGrammarAccess
import org.eclipse.xtext.GrammarToDot
import java.io.FileWriter 

@ExtendWith(InjectionExtension)
@InjectWith(SynthetoInjectorProvider)
class SynthetoParsingTest {
	@Inject
	ParseHelper<Program> parseHelper

	@Test
	def void loadModel() {

	}

	@Inject extension IGrammarAccess
	@Inject extension GrammarToDot

	@Test def void visualizeGrammar() {
		var code = grammar.draw
		var myWriter = new FileWriter("syntheto.dot")
		myWriter.write(code)
		myWriter.close
	}

}
