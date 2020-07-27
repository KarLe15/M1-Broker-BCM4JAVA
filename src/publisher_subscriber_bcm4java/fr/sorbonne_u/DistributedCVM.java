package publisher_subscriber_bcm4java.fr.sorbonne_u;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import publisher_subscriber_bcm4java.fr.sorbonne_u.multi_jvm.Assembler;
import publisher_subscriber_bcm4java.fr.sorbonne_u.multi_jvm.MultiJvmAssembler;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.Scenario;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.parser.ScenarioParser;

import java.util.Arrays;
import java.util.List;

public class			DistributedCVM
        extends		AbstractDistributedCVM
{


    protected static final List<String> scenarios = Arrays.asList(
            "default",       "mix1",          "mix2",          "mix3",
            "mix4",          "mix5",          "mix6",          "mix7",
            "mix8",          "mix9",          "scen_mix_new",  "scen_mix_new1",
            "scen_mix_new2", "scen_mix_new3", "scen_mix_new4",
            "scen_mix_new5", "scen_mix_new6", "scen_mix_new7",
            "test1",         "test2",         "test3",         "test4",
            "test5",         "test6"
    );

    public				DistributedCVM(String[] args, int xLayout, int yLayout)
            throws Exception
    {
        super(args, xLayout, yLayout);
    }

    @Override
    public void			initialise() throws Exception
    {
        // debugging mode configuration; comment and uncomment the line to see
        // the difference
//		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PUBLIHSING) ;
//		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING) ;
//		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.COMPONENT_DEPLOYMENT) ;

        super.initialise() ;
        // any other application-specific initialisation must be put here

    }

    @Override
    public void			instantiateAndPublish() throws Exception
    {
        // ici création de tous les assembleurs
        if (! scenarios.contains(thisJVMURI)){
            System.out.println("Scenario Unknown ... " + thisJVMURI) ;
        } else {
            Scenario scenario = ScenarioParser.readScenario(thisJVMURI);
            String assemblerURI = AbstractComponent.createComponent(
                    MultiJvmAssembler.class.getCanonicalName(),
                    new Object[]{
                        thisJVMURI,
                        thisJVMURI + Assembler.ASSEMBLER_SUFFIXE,
                        scenario,
                        this.configurationParameters.getJvmURIs()
                    }
            );
            assert this.isDeployedComponent(assemblerURI);
        }
        super.instantiateAndPublish();
    }

    @Override
    public void			interconnect() throws Exception {
        assert	this.isIntantiatedAndPublished() ;
        super.interconnect();
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    public void execute() throws Exception {
        if (this.configurationParameters.getJvmURIs().length > 1) {
            MultiJvmAssembler mja =
                    (MultiJvmAssembler) this.uri2component.get(thisJVMURI + Assembler.ASSEMBLER_SUFFIXE);
            mja.connectBetweenBrokers();
        }
        super.execute();
    }

    /**
     * @see fr.sorbonne_u.components.cvm.AbstractDistributedCVM#finalise()
     */
    @Override
    public void			finalise() throws Exception {
        super.finalise() ;
    }

    /**
     * @see fr.sorbonne_u.components.cvm.AbstractDistributedCVM#shutdown()
     */
    @Override
    public void			shutdown() throws Exception {
        super.shutdown();
    }

    public static void	main(String[] args)
    {
        try {
            DistributedCVM da  = new DistributedCVM(args, 2, 5) ;
            da.startStandardLifeCycle(15000L) ;
            Thread.sleep(10000L) ;
            System.exit(0) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }
}
//-----------------------------------------------------------------------------
