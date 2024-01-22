package com.waterloorocketry.airbrakeplugin;

import com.waterloorocketry.airbrakeplugin.airbrake.Airbrakes;

import com.waterloorocketry.airbrakeplugin.controller.PIDController;
import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.simulation.FlightDataType;
import net.sf.openrocket.unit.UnitGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Initialize the plugin.
 */
public class AirbrakePlugin extends AbstractSimulationExtension {
    @Override
    public String getName()
    {
        return "Airbrakes";
    }

    // Create new FlightDataType to hold airbrake extension percentage
    private static final FlightDataType airbrakeExt = FlightDataType.getType("airbrakeExt", "airbrakeExt", UnitGroup.UNITS_RELATIVE);
    private static final ArrayList<FlightDataType> types = new ArrayList<FlightDataType>();

    /**
     * Initialize the new airbrakeExt datatype we created by returning it here
     * @return
     */
    @Override
    public List<FlightDataType> getFlightDataTypes() {
        return types;
    }

    AirbrakePlugin() {
        types.add(airbrakeExt);
    }

    /**
     * Initialize this extension before simulations by adding the simulation listener.
     * @param conditions
     * @throws SimulationException
     */
    @Override
    public void initialize(SimulationConditions conditions) throws SimulationException
    {
        conditions.getSimulationListenerList().add(new AirbrakePluginSimulationListener(new Airbrakes(), new PIDController(9000)));
    }
}