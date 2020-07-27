package publisher_subscriber_bcm4java.fr.sorbonne_u;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.CVMMyDebug;
import publisher_subscriber_bcm4java.fr.sorbonne_u.multi_jvm.Assembler;

public class CVM extends AbstractCVM {
	private String assemblerURI;
	public CVM() throws Exception {
		super();
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
		AbstractCVM.DEBUG_MODE.add(CVMMyDebug.INTENSE_LOGGING) ;
//		AbstractCVM.DEBUG_MODE.add(CVMMyDebug.PRINTING_INTENSE) ;
		AbstractCVM.DEBUG_MODE.add(CVMMyDebug.SIMPLE_LOGGING) ;

		this.assemblerURI = AbstractComponent.createComponent(
			Assembler.class.getCanonicalName(),
			new Object[]{
				thisJVMURI
			}
		);

		// ==================================================
		// Deployment done
		// ==================================================

		super.deploy();
		assert	this.deploymentDone() ;
	}


	public static void	main(String[] args) {
		try {
			CVM a = new CVM() ;
			a.startStandardLifeCycle(20000L) ;
			// Publishers lasts arround 5000 ms with 1000 waiting for subscription
			Thread.sleep(6000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
