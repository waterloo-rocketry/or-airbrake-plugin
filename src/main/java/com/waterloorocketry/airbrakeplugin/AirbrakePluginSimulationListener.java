package com.waterloorocketry.airbrakeplugin;

import com.waterloorocketry.airbrakeplugin.airbrake.Airbrakes;
import com.waterloorocketry.airbrakeplugin.controller.Controller;

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
			double[] data = {
					status.getRocketPosition().x,
					status.getRocketPosition().y,
					status.getRocketPosition().z,
					status.getRocketVelocity().x,
					status.getRocketVelocity().y,
					status.getRocketVelocity().z,
					status.getRocketOrientationQuaternion().getX(),
					status.getRocketOrientationQuaternion().getY(),
					status.getRocketOrientationQuaternion().getZ(),
					status.getRocketOrientationQuaternion().getW()
			};

			double ext = controller.calculateTargetExt(data, status.getSimulationTime());
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

		final double velocity = flightData.getLast(FlightDataType.TYPE_VELOCITY_Z);
		final double airbrakeExt = flightData.getLast(airbrakeExtDataType);

		// Calculate and override cd. No coast check here since it's done in preStep
		if (!Double.isNaN(airbrakeExt)) {
			forces.setCDaxial(airbrakes.calculateCD(airbrakeExt));
		}
		//System.out.println("cd " + forces.getCD());

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