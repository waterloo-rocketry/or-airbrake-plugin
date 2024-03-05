package com.waterloorocketry.airbrakeplugin;

import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.gui.SpinnerEditor;
import net.sf.openrocket.gui.adaptors.BooleanModel;
import net.sf.openrocket.gui.adaptors.DoubleModel;
import net.sf.openrocket.gui.adaptors.IntegerModel;
import net.sf.openrocket.gui.components.BasicSlider;
import net.sf.openrocket.gui.components.UnitSelector;
import net.sf.openrocket.plugin.Plugin;
import net.sf.openrocket.simulation.extension.AbstractSwingSimulationExtensionConfigurator;
import net.sf.openrocket.unit.UnitGroup;

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

        // ------------------ Add alwaysOpen mode checkbox ------------------
        panel.add(new JLabel("Enable Always-Open mode (no PID):"));

        // Passing `extension` into BooleanModel allows this config panel to
        // use the getter/setter for AlwaysOpen which are defined in AirbrakePlugin
        final BooleanModel alwaysOpen = new BooleanModel(extension, "AlwaysOpen");

        JCheckBox checkbox = new JCheckBox(alwaysOpen);
        panel.add(checkbox, "wrap");

        // ------------------ Add target apogee input box ------------------
        DoubleModel m = new DoubleModel(extension, "TargetApogee", UnitGroup.UNITS_DISTANCE, 0);

        panel.add(new JLabel("Target apogee:"));

        JSpinner spin = new JSpinner(m.getSpinnerModel());
        spin.setEditor(new SpinnerEditor(spin));
        panel.add(spin, "w 65lp!");

        return panel;
    }
}
