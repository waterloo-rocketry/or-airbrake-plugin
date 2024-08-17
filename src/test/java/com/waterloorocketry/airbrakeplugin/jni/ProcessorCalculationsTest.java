package com.waterloorocketry.airbrakeplugin.jni;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessorCalculationsTest {
    @Test
    void interpolatedDragCloseToActualDrag() throws IOException {
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
            float ext = Float.parseFloat(values[0]) / 100;
            float alt = Float.parseFloat(values[1]);
            float vel = Float.parseFloat(values[2]);
            float drag = Float.parseFloat(values[3]);
            float interpDrag = ProcessorCalculations.interpolateDrag(ext, vel, alt - 295) * 42.5288206112F;
            Assertions.assertTrue(Math.abs(interpDrag - drag) < 20 || Math.abs(interpDrag - drag) / drag < 0.03,
                    "Actual drag: " + drag + ", interpolated drag: " + interpDrag + ", ext: " + ext + ", vel: " + vel + ", alt: " + alt);
        }
    }
}
