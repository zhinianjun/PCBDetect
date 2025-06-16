package com.example.pcbclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

public class SysInfoController {
    @FXML
    private Label cpuLabel;
    @FXML
    private Label memoryLabel;
    @FXML
    private Label diskLabel;

    private void showSysInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

        String cpuInfo = String.format("CPU 核心数: %d", osBean.getAvailableProcessors());
        cpuLabel.setText(cpuInfo);

        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        String memoryInfo = String.format("最大内存: %d MB, 总内存: %d MB, 可用内存: %d MB", maxMemory, totalMemory, freeMemory);
        memoryLabel.setText(memoryInfo);

        long totalSpace = new java.io.File("/").getTotalSpace() / (1024 * 1024);
        long freeSpace = new java.io.File("/").getFreeSpace() / (1024 * 1024);
        String diskInfo = String.format("总磁盘空间: %d MB, 可用磁盘空间: %d MB", totalSpace, freeSpace);
        diskLabel.setText(diskInfo);
    }
    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        showSysInfo();
    }

    /**
     * 刷新按钮点击事件
     * @param event
     */

    @FXML
    void onRefresh(ActionEvent event) {
        showSysInfo();
    }
}
