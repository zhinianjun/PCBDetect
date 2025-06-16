package com.example.pcbclient;

import com.alibaba.fastjson.JSONObject;
import com.example.pcbclient.mq.RepOperationMq;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class MainController {
    private Node monitorNode;
    private Node sysInfoNode;

    MonitorController monitorController;
    @FXML
    private BorderPane mainPane;
    @FXML
    private Button monitorBnt;
    @FXML
    private Button sysInfoBtn;
    @FXML
    private Label timeLab;

    @FXML
    private void onMonitor(javafx.event.ActionEvent event) {
        // 实时监控按钮点击事件
        mainPane.setCenter(monitorNode);
    }

    @FXML
    private void onSysInfo(javafx.event.ActionEvent event) {
        // 系统信息按钮点击事件
        mainPane.setCenter(sysInfoNode);
    }
    /**
     * 设置时间显示
     */
    public void setShowTime(){
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            timeLab.setText(LocalTime.now().format(formatter));
        }));

        // 设置动画循环次数（无限循环）
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    public void initialize() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PCBApplication.class.getResource("monitor-view.fxml"));
        monitorNode = fxmlLoader.load();
        monitorController = fxmlLoader.getController();
        fxmlLoader = new FXMLLoader(PCBApplication.class.getResource("sys-info-view.fxml"));
        sysInfoNode= fxmlLoader.load();
        setShowTime();
    }
    public void exitDetectThread(){
        monitorController.exitThread();
    }


}