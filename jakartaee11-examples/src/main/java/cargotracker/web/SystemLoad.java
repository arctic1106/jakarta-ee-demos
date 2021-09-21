package cargotracker.web;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.management.OperatingSystemMXBean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import cargotracker.web.view.SystemLoadData;

@Named
@ViewScoped
public class SystemLoad implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int MAX_SAMPLES = 20;
    private static final OperatingSystemMXBean OPERATING_SYSTEM =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final MemoryMXBean MEMORY = ManagementFactory.getMemoryMXBean();

    private final List<SystemLoadData> samples = new ArrayList<>();

    @PostConstruct
    public void init() {
        refresh();
    }

    public void refresh() {
        if (samples.size() == MAX_SAMPLES) {
            samples.removeFirst();
        }

        long heapMax = MEMORY.getHeapMemoryUsage().getMax();
        long heapUsed = MEMORY.getHeapMemoryUsage().getUsed();
        double memoryUsage = heapMax > 0 ? heapUsed * 100.0 / heapMax : 0;

        double cpuLoad = OPERATING_SYSTEM.getCpuLoad() * 100;
        if (!Double.isFinite(cpuLoad) || cpuLoad < 0) {
            cpuLoad = 0;
        }

        samples.add(new SystemLoadData(java.time.LocalDateTime.now(), cpuLoad, memoryUsage));
    }

    public List<SystemLoadData> getSamples() {
        return Collections.unmodifiableList(samples);
    }
}
