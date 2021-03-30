package org.gravel.elements.gui;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserStatus;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;


@Getter @AllArgsConstructor
public class SystemInfo {


    private final Gui gui;

    public void printInfo() {
        while (true) {
            Runtime runtime = Runtime.getRuntime();

            NumberFormat format = NumberFormat.getInstance();

            StringBuilder sb = new StringBuilder();
            long maxMemory = runtime.maxMemory();
            long allocatedMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();

            sb.append("Free Memory: " + format.format(freeMemory / 1024 / 1024) + " MB\n");
            sb.append("Allocated Memory: " + format.format(allocatedMemory / 1024 / 1024) + " MB\n");
            sb.append("Max Memory: " + format.format(maxMemory / 1024 / 1024) + " MB\n");
            sb.append("Memory Usage: " + format.format((allocatedMemory - freeMemory) / 1024 / 1024) + "/" + format.format(maxMemory / 1024 / 1024) + " MB (" + Math.round((double) (allocatedMemory - freeMemory) / (double) (maxMemory) * 100) + "%)\n");
            sb.append("\n");

            try {
                com.sun.management.OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                double processLoad = operatingSystemMXBean.getProcessCpuLoad();
                double systemLoad = operatingSystemMXBean.getSystemCpuLoad();
                int processors = runtime.availableProcessors();

                sb.append("Available Processors: " + processors + "\n");
                sb.append("Process CPU Load: " + Math.round(processLoad * 100) + "%\n");
                sb.append("System CPU Load: " + Math.round(systemLoad * 100) + "%\n");
                this.gui.getSysText().setText(sb.toString());

                StringBuilder stringBuilder = new StringBuilder();
                for (GravelUser user : GravelAPI.getInstance().getUserManager().getUsers()) {
                    if (user.getStatus().equals(UserStatus.OFFLINE)) {
                        continue;
                    }

                    stringBuilder.append("@" + user.getAccount().getName()).append(" (" + user.getStatus() + ") ").append("[" + user.getFriends().size() + " Friends]").append("\n");
                }
                this.gui.getClientText().setText(stringBuilder.toString());
            } catch (Exception ignore) {}

            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
