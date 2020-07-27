package publisher_subscriber_bcm4java.fr.sorbonne_u;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.CVMState;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.CVMMyDebug;
import publisher_subscriber_bcm4java.fr.sorbonne_u.multi_jvm.Assembler;
import publisher_subscriber_bcm4java.fr.sorbonne_u.multi_jvm.AssemblerWithScenario;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.Scenario;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.parser.ScenarioParser;

public class CVM1 extends AbstractCVM {
	private Scenario scenario;
	private CVM1() throws Exception {
		super();
	}

	public CVM1(Scenario todo) throws Exception{
		super();
		this.scenario = todo;
	}

	@Override
	public void	deploy() throws Exception {
		assert	!this.deploymentDone() ;

		// ==================================================
		// Configuration phase
		// ==================================================

//		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PUBLIHSING) ;
//		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING) ;
//		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.COMPONENT_DEPLOYMENT) ;
//		AbstractCVM.DEBUG_MODE.add(CVMMyDebug.INTENSE_LOGGING) ;
//		AbstractCVM.DEBUG_MODE.add(CVMMyDebug.PRINTING_INTENSE) ;
//		AbstractCVM.DEBUG_MODE.add(CVMMyDebug.SIMPLE_LOGGING) ;

		String assemblerURI = AbstractComponent.createComponent(
			AssemblerWithScenario.class.getCanonicalName(),
			new Object[]{
				thisJVMURI,
				thisJVMURI + Assembler.ASSEMBLER_SUFFIXE,
				scenario
			}
		);
		assert this.isDeployedComponent(assemblerURI);
		// ==================================================
		// Deployment done
		// ==================================================

		super.deploy();
		assert	this.deploymentDone() ;
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
	}

	@Override
	public void start() throws Exception {
		super.start();
	}

	public static void	main(String[] args) {
		try {
			// TODO :: Parse scenario file
			Scenario scenario = ScenarioParser.readScenario("mix9");
			CVM1 a = new CVM1(scenario) ;
			a.startStandardLifeCycle(10000L) ;
//			// Publishers lasts arround 5000 ms with 1000 waiting for subscription
			Thread.sleep(10000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
