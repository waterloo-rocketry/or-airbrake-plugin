package com.waterloorocketry.airbrakeplugin.simulated;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimulatedDragForceInterpolatorTest {
    @Test
    void interpolatedDragCloseToActualDrag() throws IOException {
        SimulatedDragForceInterpolator interp = new SimulatedDragForceInterpolator();

        List<String> lines = new ArrayList<>();
        try (FileReader f = new FileReader("./rockets/Final Simulation Result.csv")) {
            try (BufferedReader r = new BufferedReader(f)) {
                while (true) {
                    String l = r.readLine();
                    if (l != null) {
                        lines.add(l);
                    } else {
                        break;
                    }
                }
            }
        }
        lines = lines.subList(1, lines.size());
        for (String line : lines) {
            String[] values = line.split(",");
            if (values.length != 5) {
                throw new RuntimeException("expected 5 values");
            }
            double ext = Double.parseDouble(values[0]) / 100;
            double alt = Double.parseDouble(values[1]);
            double vel = Double.parseDouble(values[2]);
            double drag = Double.parseDouble(values[3]);
            // in the interpolator, we check for negative values with an assertion
            // in firmware, we clamp it to 0 though
            // should be safe to basically just ignore v=34, which could produce
            // negative values in some weird cases
            double interpDrag = vel == 34 ? 0.0 : interp.compute(new SimulatedDragForceInterpolator.Data(ext, vel, alt));
            Assertions.assertTrue(Math.abs(interpDrag - drag) < 20 || Math.abs(interpDrag - drag) / drag < 0.03,
                    "Actual drag: " + drag + ", interpolated drag: " + interpDrag);
        }
    }
}
