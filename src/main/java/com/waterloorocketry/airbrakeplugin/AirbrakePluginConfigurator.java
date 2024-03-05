package com.waterloorocketry.airbrakeplugin;

import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.gui.SpinnerEditor;
import net.sf.openrocket.gui.adaptors.BooleanModel;
import net.sf.openrocket.gui.adaptors.DoubleModel;
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

        // Add input rows. Max values are somewhat arbitrary for those that have no actual max
        addRow("Target apogee", "TargetApogee", UnitGroup.UNITS_DISTANCE, 0.0, 100000);

        addRow("Kp", "Kp", UnitGroup.UNITS_COEFFICIENT, 0.0, 100);
        addRow("Ki", "Ki", UnitGroup.UNITS_COEFFICIENT, 0.0, 100);
        addRow("Kd", "Kd", UnitGroup.UNITS_COEFFICIENT, 0.0, 100);

        // Extension must be within 0-100%
        addRow("Always-open extension %", "AlwaysOpenExt", UnitGroup.UNITS_RELATIVE, 0.0, 1);

        return panel;
    }

    /**
     * Helper to build an input box + spinner + unit selector. Copied from OR examples
     * @param prompt for the JLabel
     * @param fieldName exact name used in the getter/setter for this var
     * @param units
     * @param min
     * @param max
     */
    private void addRow(String prompt, String fieldName, UnitGroup units, double min, double max) {
        panel.add(new JLabel(prompt + ":"));

        DoubleModel m = new DoubleModel(extension, fieldName, units, min, max);

        JSpinner spin = new JSpinner(m.getSpinnerModel());
        spin.setEditor(new SpinnerEditor(spin));
        panel.add(spin, "w 65lp!");

        UnitSelector unit = new UnitSelector(m);
        panel.add(unit, "w 25, wrap");
    }
}
