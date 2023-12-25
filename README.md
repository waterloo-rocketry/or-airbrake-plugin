# Airbrake plugin
An OpenRocket plugin adding airbrake behaviour in simulations.

### Build
Download and place the [latest](https://github.com/openrocket/openrocket/tags) OpenRocket jar in the root directory.

Build the plugin jar using Gradle.

### Run
To use plugin, place the plugin jar in the OpenRocket plugins folder, most likely found at ```/home/user/.openrocket/Plugins``` or ```C:\Users\user\AppData\Roaming\OpenRocket\Plugins```.

The plugin will be available to add to simulations via ``` New Simulation > Simulation Options > Add extension > Waterloo Rocketry > Airbrakes```.

Alternatively, use the Gradle "run" task to automatically build the jar and run OpenRocket together.