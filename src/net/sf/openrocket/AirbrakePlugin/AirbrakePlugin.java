package net.sf.openrocket.AirbrakePlugin;

import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;

/**
 * Initialize the plugin.
 */
public class AirbrakePlugin extends AbstractSimulationExtension
{
    @Override
    public String getName() 
    {
        return "Airbrakes";
    }

    /**
     * Initialize this extension before simulations by adding the simulation listener.
     * @param conditions
     * @throws SimulationException
     */
    @Override
    public void initialize(SimulationConditions conditions) throws SimulationException
    {
        conditions.getSimulationListenerList().add(new AirbrakePluginSimulationListener());
    }
}