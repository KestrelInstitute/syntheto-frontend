/*
 * generated by Xtext 2.23.0
 */
package edu.vanderbilt.isis.midas


/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
class SynthetoStandaloneSetup extends SynthetoStandaloneSetupGenerated {

	def static void doSetup() {
		new SynthetoStandaloneSetup().createInjectorAndDoEMFRegistration()
	}
}
