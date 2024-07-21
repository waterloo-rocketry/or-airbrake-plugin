package com.waterloorocketry.airbrakeplugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.waterloorocketry.airbrakeplugin.airbrake.Airbrakes;
import com.waterloorocketry.airbrakeplugin.airbrake.SimulatedAirbrakes;
import com.waterloorocketry.airbrakeplugin.controller.Controller;
import com.waterloorocketry.airbrakeplugin.controller.PIDController;
import net.sf.openrocket.database.Databases;
import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.file.GeneralRocketLoader;
import net.sf.openrocket.gui.plot.PlotConfiguration;
import net.sf.openrocket.gui.plot.SimulationPlotDialog;
import net.sf.openrocket.gui.util.SwingPreferences;
import net.sf.openrocket.plugin.PluginModule;
import net.sf.openrocket.simulation.FlightDataType;
import net.sf.openrocket.simulation.FlightEvent;
import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.listeners.SimulationListener;
import net.sf.openrocket.startup.Application;
import net.sf.openrocket.startup.GuiModule;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PidTuner {
    /**
     * File to load rocket from
     */
    private static final String ROCKET_FILE = "./rockets/c31a.ork";
    /**
     * File to load rocket thrust curve data from
     */
    private static final String THRUST_CURVE_FILE = "./rockets/Kismet_v4_C2-2.rse";

    public static void main(String[] args) throws Exception {
        initializeOpenRocket();
        List<File> thrustCurveFiles = new ArrayList<>(getOpenRocketPreferences().getUserThrustCurveFiles());
        thrustCurveFiles.add(new File(THRUST_CURVE_FILE));
        getOpenRocketPreferences().setUserThrustCurveFiles(thrustCurveFiles);

        File file = new File(ROCKET_FILE);
        GeneralRocketLoader loader = new GeneralRocketLoader(file);

        OpenRocketDocument doc = loader.load();

        Simulation simulation = new Simulation(doc, doc.getRocket());

        Controller controller = new PIDController(10000, 0.0, 0.0, 0.0);
        Airbrakes airbrakes = new SimulatedAirbrakes();
        SimulationListener listener = new AirbrakePluginSimulationListener(airbrakes, controller);

        simulation.simulate(listener);
        System.out.println("max altitude: " + simulation.getSimulatedData().getMaxAltitude());

        PlotConfiguration config = new PlotConfiguration("PID");

//        config.addPlotDataType(FlightDataType.TYPE_ALTITUDE, 0);
//        config.addPlotDataType(FlightDataType.TYPE_VELOCITY_Z);
//        config.addPlotDataType(FlightDataType.TYPE_ACCELERATION_Z);
        config.addPlotDataType(AirbrakePluginSimulationListener.airbrakeExtDataType);
        config.setEvent(FlightEvent.Type.IGNITION, true);
        config.setEvent(FlightEvent.Type.BURNOUT, true);
        config.setEvent(FlightEvent.Type.APOGEE, true);
        config.setEvent(FlightEvent.Type.RECOVERY_DEVICE_DEPLOYMENT, true);
        config.setEvent(FlightEvent.Type.STAGE_SEPARATION, true);
        config.setEvent(FlightEvent.Type.GROUND_HIT, true);
        config.setEvent(FlightEvent.Type.TUMBLE, true);
        config.setEvent(FlightEvent.Type.EXCEPTION, true);

        Frame frame = new Frame();
        frame.setVisible(true);
        Dialog dialog = new Dialog(frame);
        SimulationPlotDialog simDialog = SimulationPlotDialog.getPlot(dialog, simulation, config);
        simDialog.setSize(1000, 500);
        simDialog.setVisible(true);
    }

    /**
     * Inject required dependencies for OpenRocket, allowing us to run simulations
     * programmatically.
     * This runs the same code as for starting up a GUI version of OpenRocket, making it easier to make manual
     * simulation runs automatic.
     */
    private static void initializeOpenRocket() {
        GuiModule guiModule = new GuiModule();
        Module pluginModule = new PluginModule();
        Injector injector = Guice.createInjector(guiModule, pluginModule);
        Application.setInjector(injector);
        guiModule.startLoader();
        Databases.fakeMethod();
    }

    /**
     * Get the preferences of OpenRocket Swing
     * @return Preferences object
     */
    private static SwingPreferences getOpenRocketPreferences() {
        return (SwingPreferences) Application.getPreferences();
    }
}
