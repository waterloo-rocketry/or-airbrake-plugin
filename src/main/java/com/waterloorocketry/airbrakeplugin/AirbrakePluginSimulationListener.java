package com.waterloorocketry.airbrakeplugin;

import com.waterloorocketry.airbrakeplugin.Airbrakes.Airbrakes;
import com.waterloorocketry.airbrakeplugin.Controllers.Controller;

import net.sf.openrocket.simulation.FlightDataBranch;
import net.sf.openrocket.simulation.FlightDataType;
import net.sf.openrocket.simulation.FlightEvent;
import net.sf.openrocket.simulation.SimulationStatus;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;
import net.sf.openrocket.unit.UnitGroup;

/**
 * Connect to a simulation and listen for various events during the simulation.
 */
public class AirbrakePluginSimulationListener extends AbstractSimulationListener {
	private final Airbrakes airbrakes;
	private final Controller controller;
	private boolean burnout = false;

	public AirbrakePluginSimulationListener(Airbrakes airbrakes, Controller controller) {
		super();
		this.airbrakes = airbrakes;
		this.controller = controller;
	}

	/**
	 * Called when the simulation starts.
	 * @param status Holds flight status data during simulation.
	 */
	@Override
	public void startSimulation(SimulationStatus status) {
		System.out.println("Starting simulation");
	}

	/**
	 * Runs before each timestep.
	 * @param status
	 * @return
	 */
	@Override
	public boolean preStep(SimulationStatus status) {
		// Call airbrake controller

		return true;
	}

	/**
	 * Called each timestep to override the sim's thrust before the sim can calculate it.
	 * Returning NaN makes the sim ignore this plugin and calculate thrust normally instead.
	 * @param status
	 * @return The thrust to override with, or NaN if we don't want to override.
	 */
	@Override
	public double preSimpleThrustCalculation(SimulationStatus status) {
		// Do nothing if rocket is not in coast stage
		if (!burnout || status.isApogeeReached()) {
			return Double.NaN;
		} else {
			// Get current flight conditions and airbrake extension
			FlightDataBranch flightData = status.getFlightData();

			final double velocity = flightData.getLast(FlightDataType.TYPE_VELOCITY_Z);
			final double airbrakeExt = flightData.getLast(FlightDataType.getType("airbrakeExt", "airbrakeExt", UnitGroup.UNITS_RELATIVE));

			// Calculate and override thrust given the current airbrake state
			final double thrust = airbrakes.calculateDragForce(controller, velocity, airbrakeExt);

			return thrust;
		}
	}

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