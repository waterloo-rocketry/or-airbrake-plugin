# Airbrake Plugin
An OpenRocket plugin adding airbrake behaviour in simulations.

At each simulation timestep, the plugin calculates airbrake extension given a Controller. Then, it calculates and overrides the simulation drag force given the current airbrake extension and dynamic pressure.

## Setup
### Requirements
JDK 17 is required.

### Build
1. Clone this repo.
2. Download and place the [latest](https://github.com/openrocket/openrocket/tags) OpenRocket jar in the root directory. The jar must be named `OpenRocket-23.xx.jar`, replacing xx with the appropriate version.
3. Build the plugin jar to `target/` using Gradle: `./gradlew jar` or run jar task via IDE.

### Usage
**Option 1 (gradle run):** Use `./gradlew run` (or use an IDE navigator) to automatically build the jar then run OpenRocket. (This runs OpenRocket using Java via the jar in this folder.).

**Option 2 (manual):** Place the plugin jar in the OpenRocket plugins folder, most likely ```/home/user/.openrocket/Plugins``` or ```C:\Users\user\AppData\Roaming\OpenRocket\Plugins```.


---

**OpenRocket must be restarted to load/reload the plugin.**

Add the plugin to a simulation via ``` New Simulation > Simulation Options > Add extension > Waterloo Rocketry > Airbrakes```. The plugin will now be in effect during this simulation.

Currently, there is no settings interface; the plugin is hard-coded to run the PID Controller.
