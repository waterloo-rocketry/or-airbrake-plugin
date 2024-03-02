package com.waterloorocketry.airbrakeplugin;

import com.waterloorocketry.airbrakeplugin.airbrake.Airbrakes;

import com.waterloorocketry.airbrakeplugin.airbrake.LinearInterpAirbrakes;
import com.waterloorocketry.airbrakeplugin.controller.AlwaysOpenController;
import com.waterloorocketry.airbrakeplugin.controller.Controller;
import com.waterloorocketry.airbrakeplugin.controller.PIDController;
import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.simulation.FlightDataType;
import net.sf.openrocket.unit.UnitGroup;

import javax.naming.ldap.Control;
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
    private static final double TARGET_APOGEE = 7000; //m

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
        Controller controller;

        // Use either PID or always-open scontroller depending on the configuration setting
        if (isAlwaysOpen()) {
            controller = new AlwaysOpenController();
        } else {
            controller = new PIDController(TARGET_APOGEE);
        }

        Airbrakes airbrakes = new LinearInterpAirbrakes(new double[] { 0.0, 1.0 }, new double[] { 0.5, 1.5 });
        conditions.getSimulationListenerList().add(new AirbrakePluginSimulationListener(airbrakes, controller));
    }

    /**
     * Getter method for always-on mode
     * @return whether always-on mode is enabled for airbrakes
     */
    public boolean isAlwaysOpen() {
        return config.getBoolean("alwaysOpen", false);
    }

    /**
     * Setter method for always-on mode. This is used by the plugin configurator panel.
     * @param value enable/disable airbrakes always-on mode
     */
    public void setAlwaysOpen(boolean value) {
        config.put("alwaysOpen", value);
        fireChangeEvent();
    }
}