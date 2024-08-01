package com.waterloorocketry.airbrakeplugin;

import com.waterloorocketry.airbrakeplugin.airbrake.Airbrakes;

import com.waterloorocketry.airbrakeplugin.airbrake.SimulatedAirbrakes;
import com.waterloorocketry.airbrakeplugin.controller.AlwaysOpenController;
import com.waterloorocketry.airbrakeplugin.controller.Controller;
import com.waterloorocketry.airbrakeplugin.controller.PIDController;
import com.waterloorocketry.airbrakeplugin.simulated.Noise;
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
    // Create new FlightDataType to hold trajpred apogee output even though its not a flight data, this allows us to graph it
    private static final FlightDataType predictedApogee = FlightDataType.getType("predictedApogee", "predictedApogee", UnitGroup.UNITS_DISTANCE);
    private static final ArrayList<FlightDataType> types = new ArrayList<FlightDataType>();
    private static final double TARGET_APOGEE = 8000; //m

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
        types.add(predictedApogee);
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

        // Use either PID or always-open controller depending on the configuration setting
        if (isAlwaysOpen()) {
            controller = new AlwaysOpenController(getAlwaysOpenExt());
        } else {
            controller = new PIDController((float) getTargetApogee(), (float) getKp(), (float) getKi(), (float) getKd(), (float) getISatmax());
        }

        Airbrakes airbrakes = new SimulatedAirbrakes();
        Noise noise = isNoisy() ? new Noise(getStddevPositionZ(), getStddevVelocityX(), getStddevVelocityY(), getStddevVelocityZ()) : null;
        conditions.getSimulationListenerList().add(new AirbrakePluginSimulationListener(airbrakes, controller, noise));
    }

    //
    // Getter/setters for all the values that are user-adjustable via the plugin config panel
    // The setters are used indirectly in AirbrakePluginConfigurator
    //

    public boolean isAlwaysOpen() {
        return config.getBoolean("alwaysOpen", false);
    }

    public void setAlwaysOpen(boolean value) {
        config.put("alwaysOpen", value);
        fireChangeEvent();
    }

    public boolean isNoisy() {
        return config.getBoolean("noisy", false);
    }

    public void setNoisy(boolean value) {
        config.put("noisy", value);
        fireChangeEvent();
    }

    public double getStddevPositionZ() {
        return config.getDouble("stddevPositionZ", 10.0);
    }

    public void setStddevPositionZ(double value) {
        config.put("stddevPositionZ", value);
        fireChangeEvent();
    }

    public double getStddevVelocityX() {
        return config.getDouble("stddevVelocityX", 0.5);
    }

    public void setStddevVelocityX(double value) {
        config.put("stddevVelocityX", value);
        fireChangeEvent();
    }

    public double getStddevVelocityY() {
        return config.getDouble("stddevVelocityY", 0.5);
    }

    public void setStddevVelocityY(double value) {
        config.put("stddevVelocityY", value);
        fireChangeEvent();
    }

    public double getStddevVelocityZ() {
        return config.getDouble("stddevVelocityZ", 2.0);
    }

    public void setStddevVelocityZ(double value) {
        config.put("stddevVelocityZ", value);
        fireChangeEvent();
    }


    public double getTargetApogee() {
        return config.getDouble("targetApogee", 10000.0);
    }

    public void setTargetApogee(double targetApogee) {
        config.put("targetApogee", targetApogee);
        fireChangeEvent();
    }

    public double getKp() {
        return config.getDouble("Kp", 0.0);
    }

    public void setKp(double Kp) {
        config.put("Kp", Kp);
        fireChangeEvent();
    }

    public double getKi() {
        return config.getDouble("Ki", 0.0);
    }

    public void setKi(double Ki) {
        config.put("Ki", Ki);
        fireChangeEvent();
    }

    public double getKd() {
        return config.getDouble("Kd", 0.0);
    }

    public void setKd(double Kd) {
        config.put("Kd", Kd);
        fireChangeEvent();
    }
    public double getISatmax() {
        return config.getDouble("ISatmax", 10.0);
    }

    public void setISatmax(double v) {
        config.put("ISatmax", v);
        fireChangeEvent();
    }

    public double getAlwaysOpenExt() {
        return config.getDouble("alwaysOpenExt", 0.0);
    }

    public void setAlwaysOpenExt(double ext) {
        config.put("alwaysOpenExt", ext);
        fireChangeEvent();
    }
}