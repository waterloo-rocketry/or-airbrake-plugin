
package net.sf.openrocket.AirbrakePlugin;

import net.sf.openrocket.plugin.Plugin;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtensionProvider;


@Plugin
public class AirbrakePluginProvider extends AbstractSimulationExtensionProvider {
	/**
	 * Lists the plugin in the simulation options.
	 */
	public AirbrakePluginProvider() {
		super(AirbrakePlugin.class, "Wloo Rocketry", "Airbrakes");
	}
	
}