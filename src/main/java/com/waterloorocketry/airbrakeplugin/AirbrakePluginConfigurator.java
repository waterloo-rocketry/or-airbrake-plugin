package com.waterloorocketry.airbrakeplugin;

import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.gui.SpinnerEditor;
import net.sf.openrocket.gui.adaptors.IntegerModel;
import net.sf.openrocket.gui.components.BasicSlider;
import net.sf.openrocket.plugin.Plugin;
import net.sf.openrocket.simulation.extension.AbstractSwingSimulationExtensionConfigurator;

import javax.swing.*;

/**
 * Create the plugin's config menu. Needs @Plugin for OR to recognize it.
 */
@Plugin
public class AirbrakePluginConfigurator extends AbstractSwingSimulationExtensionConfigurator<AirbrakePlugin> {
    private JPanel panel;
    private AirbrakePlugin extension;

    public AirbrakePluginConfigurator() {
        super(AirbrakePlugin.class);
    }

    @Override
    protected JComponent getConfigurationComponent(AirbrakePlugin extension, Simulation simulation, JPanel panel) {
        this.panel = panel;
        this.extension = extension;

        return panel;
    }

}
