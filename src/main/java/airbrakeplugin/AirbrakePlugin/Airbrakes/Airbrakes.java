package airbrakeplugin.AirbrakePlugin.Airbrakes;

public class Airbrakes {
    private final double cfdDataConstant = 1;

    public Airbrakes() {
    }

    /**
     * Calculate and return the drag force of the rocket given the current airbrake extension and flight conditions.
     * @param velocity
     * @param airbrakeExt
     * @return Rocket drag force
     */
    public double calculateDragForce(double velocity, double airbrakeExt) {
        return velocity + cfdDataConstant;
    }
}
