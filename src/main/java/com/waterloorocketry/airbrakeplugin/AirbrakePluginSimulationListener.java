package com.waterloorocketry.airbrakeplugin;

import com.waterloorocketry.airbrakeplugin.airbrake.Airbrakes;
import com.waterloorocketry.airbrakeplugin.controller.Controller;
import com.waterloorocketry.airbrakeplugin.controller.TrajectoryPrediction;

import net.sf.openrocket.aerodynamics.AerodynamicForces;
import net.sf.openrocket.simulation.*;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;
import net.sf.openrocket.unit.UnitGroup;

/**
 * Connect to a simulation and listen for various events during the simulation.
 */
public class AirbrakePluginSimulationListener extends AbstractSimulationListener {
	private final Airbrakes airbrakes;
	private final Controller controller;
	private final FlightDataType airbrakeExtDataType = FlightDataType.getType("airbrakeExt", "airbrakeExt", UnitGroup.UNITS_RELATIVE);
	private boolean burnout = false;
	private double ext = 0.0;

	public AirbrakePluginSimulationListener(Airbrakes airbrakes, Controller controller) {
		super();
		this.airbrakes = airbrakes;
		this.controller = controller;
	}

	/**
	 * Runs before each timestep.
	 * @param status
	 * @return
	 */
	@Override
	public boolean preStep(SimulationStatus status) {
		FlightDataBranch flightData = status.getFlightData();

		// Only run controller during coast phase. If not in coast, still set ext to 0 (better than NaN)
		if (burnout && !status.isApogeeReached()) {
			Controller.RocketState data = new Controller.RocketState();

			data.positionX = status.getRocketPosition().x;
			data.positionY = status.getRocketPosition().y;
			data.positionZ = status.getRocketPosition().z;
			data.velocityX = status.getRocketVelocity().x;
			data.velocityY = status.getRocketVelocity().y;
			data.velocityZ = status.getRocketVelocity().z;
			data.orientationX = status.getRocketOrientationQuaternion().getX();
			data.orientationY = status.getRocketOrientationQuaternion().getY();
			data.orientationZ = status.getRocketOrientationQuaternion().getZ();
			data.orientationW = status.getRocketOrientationQuaternion().getW();

			ext = controller.calculateTargetExt(data, status.getSimulationTime(), ext);
			if (!(0.0 <= ext && ext <= 1.0)) {
				throw new IndexOutOfBoundsException("airbrakes extension amount was not from 0 to 1");
			}
			flightData.setValue(airbrakeExtDataType, ext);
		} else {
			flightData.setValue(airbrakeExtDataType, 0);
		}

		return true;
	}

	/**
	 * Overrides the coefficient of drag after the aerodynamic calculations are done each timestep.
	 */
	@Override
	public AerodynamicForces postAerodynamicCalculation(SimulationStatus status, AerodynamicForces forces) throws SimulationException {
		// Get latest flight conditions and airbrake extension
		FlightDataBranch flightData = status.getFlightData();

		final double altitude = flightData.getLast(FlightDataType.TYPE_ALTITUDE);
		final double velocity = flightData.getLast(FlightDataType.TYPE_VELOCITY_Z);
		final double airbrakeExt = flightData.getLast(airbrakeExtDataType);
		
		// Override CD only during coast, and until velocity is too small for the drag tabulation to be accurate
		if (!status.isApogeeReached() && velocity > 23.5) {
			double calculatedCd = TrajectoryPrediction.interpolate_cd(airbrakeExt, velocity, altitude);
			forces.setCDaxial(calculatedCd);
		}
		System.out.println("cd " + forces.getCD());

		return forces;
	}

	// TODO: do burnout in a nicer way than practically being global var?
	/**
	 * Called when motor burnout occurs. This is handled as an event.
	 * @param status
	 * @param event
	 * @return
	 */
	@Override
	public boolean handleFlightEvent(SimulationStatus status, FlightEvent event) {
		if (event.getType() == FlightEvent.Type.BURNOUT) {
			burnout = true;
		}
		return true;
	}
}