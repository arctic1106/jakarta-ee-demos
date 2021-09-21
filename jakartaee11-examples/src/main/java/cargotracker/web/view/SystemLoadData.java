package cargotracker.web.view;

import java.io.Serializable;
import java.time.LocalDateTime;

public record SystemLoadData(LocalDateTime time, double cpuLoad, double memoryUsage) implements Serializable {
}
