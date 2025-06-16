package com.example.pcbclient;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WelcomeController{
    /**
     * 图片路径数组
     */
    @FXML
    private  ImageView imageView;
    @FXML
    private ProgressBar progressBar;

    private final String[] imagePaths = {
            "/images/pcb01.jpg",
            "/images/pcb02.jpg"
    };
    /**
     * 当前图像索引
     */
    private int currentImageIndex = 0;
    /**
     * 更新帧方法
     */
    private void updateFrame() {
        if(currentImageIndex < imagePaths.length) {
            // 加载当前图片
            String path = imagePaths[currentImageIndex];
            Image image = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
            imageView.setImage(image);
            currentImageIndex++;
            // 计算当前图片索引与图片路径数组长度的比例
            double progress = ((double) currentImageIndex) / ((double)imagePaths.length);
            System.out.println(progress);
            // 设置进度条的进度
            progressBar.setProgress(progress);
        }
    }
    /**
     * 加载主界面
     */
    @SneakyThrows
    private void loadMainView() {
        // 关闭当前窗口
        Stage primarystage = (Stage) progressBar.getScene().getWindow();
        primarystage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(PCBApplication.
                class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainController mainController = fxmlLoader.getController();
        Stage stage = new Stage();
        //绑定关闭窗口事件
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                mainController.exitDetectThread();
            }
        });
        stage.setTitle("PCB检测系统");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

    }/**
     * 设置时间轴动画
     */
    private void setTimeLine() {
        // 创建一个时间轴动画，每0.5秒调用一次updateImage方法
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        event -> updateFrame()));
        // 设置时间轴动画的循环次数为图片路径数
        timeline.setCycleCount(imagePaths.length+1);

        // 当时间轴动画完成时，调用loadMainView方法，进入主界面
        timeline.setOnFinished(event -> loadMainView());
        // 播放时间轴动画
        timeline.play();
    }

    /**
     * 初始化控制器类。此方法在FXML文件加载后自动调用。
     */
    @FXML
    private void initialize() {
        progressBar.setProgress(0);
        setTimeLine();
    }
}