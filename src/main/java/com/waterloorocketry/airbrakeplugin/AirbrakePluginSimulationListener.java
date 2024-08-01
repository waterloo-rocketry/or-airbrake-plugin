package com.waterloorocketry.airbrakeplugin;

import com.waterloorocketry.airbrakeplugin.airbrake.Airbrakes;
import com.waterloorocketry.airbrakeplugin.controller.Controller;
import com.waterloorocketry.airbrakeplugin.controller.TrajectoryPrediction;
import com.waterloorocketry.airbrakeplugin.simulated.Noise;
import net.sf.openrocket.aerodynamics.AerodynamicForces;
import net.sf.openrocket.aerodynamics.FlightConditions;
import net.sf.openrocket.simulation.FlightDataBranch;
import net.sf.openrocket.simulation.FlightDataType;
import net.sf.openrocket.simulation.FlightEvent;
import net.sf.openrocket.simulation.SimulationStatus;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;
import net.sf.openrocket.unit.UnitGroup;

/**
 * Connect to a simulation and listen for various events during the simulation.
 */
public class AirbrakePluginSimulationListener extends AbstractSimulationListener {
    private final Airbrakes airbrakes;
    private final Controller controller;
    private final Noise noise;
    public static final FlightDataType airbrakeExtDataType = FlightDataType.getType("airbrakeExt", "airbrakeExt", UnitGroup.UNITS_RELATIVE);
    public static final FlightDataType predictedApogeeDataType = FlightDataType.getType("predictedApogee", "predictedApogee", UnitGroup.UNITS_DISTANCE);
    private double ext = 0.0;

    public AirbrakePluginSimulationListener(Airbrakes airbrakes, Controller controller, Noise noise) {
        super();
        this.airbrakes = airbrakes;
        this.controller = controller;
        this.noise = noise;
    }

    // Airbrakes only allowed between 9s and while vertical velocity > 34 m/s
    private boolean isExtensionAllowed(SimulationStatus status) {
        return status.getSimulationTime() > 9 && status.getRocketVelocity().z > 34.0;
    }

    /**
     * Runs before each timestep.
     * @param status
     * @return
     */
    @Override
    public boolean preStep(SimulationStatus status) {
        FlightDataBranch flightData = status.getFlightData();
        Controller.RocketState data = new Controller.RocketState(status);

        // Add gaussian noise to the "measured" state data if enabled
        if (noise != null) {
            java.util.Random r = new java.util.Random();
            data.velocityX = r.nextGaussian(data.velocityX, noise.getStddevVelocityX());
            data.velocityY = r.nextGaussian(data.velocityY, noise.getStddevVelocityY());
            data.velocityZ = r.nextGaussian(data.velocityZ, noise.getStddevVelocityZ());
            data.positionZ = r.nextGaussian(data.positionZ, noise.getStddevPositionZ());
        }

        // Only run controller during coast phase. If not in coast, still set ext to 0 (better than NaN)
        if (isExtensionAllowed(status)) {
            ext = controller.calculateTargetExt(data, status.getSimulationTime(), ext);
            if (!(0.0 <= ext && ext <= 1.0)) {
                throw new IndexOutOfBoundsException("airbrakes extension amount was not from 0 to 1");
            }
            flightData.setValue(airbrakeExtDataType, ext);
        } else {
            flightData.setValue(airbrakeExtDataType, 0);
        }

        // This is solely for graphing trajectory prediction outputs
        flightData.setValue(predictedApogeeDataType, TrajectoryPrediction.get_max_altitude(data));

        return true;
    }

    /**
     * Flight conditions for the current timestep.
     */
    private FlightConditions flightConditions = null;

    // We can't look at status.getFlightData() for anything except extension instead because it would
    // apply to the last timestep
    @Override
    public FlightConditions postFlightConditions(SimulationStatus status, FlightConditions flightConditions) throws SimulationException {
        this.flightConditions = flightConditions;
        return flightConditions;
    }

    /**
     * Overrides the coefficient of drag after the aerodynamic calculations are done each timestep.
     */
    @Override
    public AerodynamicForces postAerodynamicCalculation(SimulationStatus status, AerodynamicForces forces) throws SimulationException {
        final double velocityZ = status.getRocketVelocity().z;

        // Override CD only during coast, and until velocity is too small for the drag tabulation to be accurate
        if (isExtensionAllowed(status)) {
            // Get latest flight conditions and airbrake extension
            FlightDataBranch flightData = status.getFlightData();
            final double airbrakeExt = flightData.getLast(airbrakeExtDataType);

            final double altitude = status.getRocketPosition().z;

            double dragForce = airbrakes.calculateDragForce(airbrakeExt, velocityZ, altitude);

            double velocity2 = status.getRocketVelocity().length2();
            double dynP = (0.5 * flightConditions.getAtmosphericConditions().getDensity() * velocity2);
            double refArea = flightConditions.getRefArea();
            double cDAxial = dragForce / dynP / refArea;

            // Note: this calculation isn't actually CDAxial, but it's necessary to override CDAxial
            // since OR uses CDAxial for its proceeding calculations. Experiments showed the diff between our
            // "CDAxial" and actual CDAxial (which accounts for AOA) is insignificant so this is fine.
            forces.setCDaxial(cDAxial);
        }

        return forces;
    }
}