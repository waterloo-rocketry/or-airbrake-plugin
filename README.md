# Airbrake Plugin
An OpenRocket plugin adding airbrake behaviour in simulations.

At each simulation timestep, the plugin calculates airbrake extension given a Controller. Then, it calculates and overrides the simulation drag force given the current airbrake extension and dynamic pressure.

## Setup
### Requirements
JDK 17 is required.

### Build
1. Clone this repo.
2. Download and place the [OpenRocket v23.09 jar](https://github.com/openrocket/openrocket/releases/tag/release-23.09) in the root directory. The jar must be named `OpenRocket-23.09.jar`.
3. Build the plugin jar to `target/` using Gradle: `./gradlew jar` or run jar task via IDE.

## Usage
**Option 1 (recommended):** Use `./gradlew run` (or use an IDE navigator) to automatically build the jar then run OpenRocket. (This option uses Java to run the OpenRocket jar in this folder.).

**Option 2 (manual):** Place the plugin jar in the OpenRocket plugins folder, most likely ```/home/user/.openrocket/Plugins``` or ```C:\Users\user\AppData\Roaming\OpenRocket\Plugins```. Then open OpenRocket as usual.


---

**OpenRocket must be restarted to load/reload the plugin.**
### Plugin Config in OpenRocket

Add the plugin to a simulation via ``` New Simulation > Simulation Options > Add extension > Waterloo Rocketry > Airbrakes```. The plugin will now be in effect during this simulation.

**Always-Open Mode:** Check the `Enable Always-Open Mode` box to disable PID control. Then, use `Always-Open %` to set the airbrake extension.

**PID Control Mode:** If `Enable Always-Open Mode` is not selected, the simulation will use PID control. Adjust PID constants with `Kp`, `Ki`, and `Kd`, and `Target apogee`.

### Notes
- Airbrakes only take effect during the coast phase of simulations (between motor burnout and apogee), and only if velocity is greater than 23.5 m/s. This is due to the drag tabulation starting at 23.5 m/s.
