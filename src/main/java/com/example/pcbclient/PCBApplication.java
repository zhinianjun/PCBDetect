package com.example.pcbclient;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.SneakyThrows;

import java.io.IOException;

public class PCBApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PCBApplication.
                class.getResource("welcome-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 415);
        stage.setTitle("PCB检测系统");
        // 设置舞台为无边框样式
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}