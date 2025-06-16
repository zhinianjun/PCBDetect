package com.example.pcbclient;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.pcbclient.entity.BndBox;
import com.example.pcbclient.entity.Device;
import com.example.pcbclient.entity.DeviceDetectResult;
import com.example.pcbclient.entity.PcbDetectResult;
import com.example.pcbclient.mq.DetectResultSubThread;
import com.example.pcbclient.mq.RepOperationMq;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;

public class MonitorController {
    private boolean detectFlag = false;

    @FXML
    private ComboBox templateCombobox;
    /**
     * 检测按钮
     */
    @FXML
    private Button detectBtn;

    /**
     * PCB检测结果标签
     */
    @FXML
    private Label pcbResultLab;
    /**
     * 检测时间标签
     */
    @FXML
    private Label detectTimeLab;

    /**
     * 总数标签
     */
    @FXML
    private Label totalLab;
    /**
     * 良品标签
     */
    @FXML
    private Label goodLab;
    /**
     * 错误标签
     */
    @FXML
    private Label errorLab;
    /**
     * 器件编号标签
     */
    @FXML
    private Label deviceNoLab;
    /**
     * 器件类型标签
     */
    @FXML
    private Label deviceTypeLab;
    /**
     * 器件结果标签
     */
    @FXML
    private Label deviceResultLab;
    /**
     * 器件匹配标签
     */
    @FXML
    private Label deviceMatchLab;
    /**
     * 器件画布
     */
    @FXML
    private Canvas deviceCanvas;
    /**
     * 模板画布
     */
    @FXML
    private Canvas templeCanvas;
    /**
     * 检测画布
     */
    @FXML
    private Canvas pcbCanvas;

    /**
     * 点击器件事件
     *
     * @param event
     */
    private DetectResultSubThread detectResultSubThread;
    /**
     * 当前检测的图像
     */
    private Image currentImage = null;
    /**
     * 当前选中的设备ID
     */
    private int currentSelectDeviceId = -1;
    /**
     * 设备检测结果哈希表
     */
    private HashMap<Integer, DeviceDetectResult> deviceDetectHashMap = new HashMap<>();
    /**
     * 设备位置哈希表
     */
    private HashMap<Integer, List> devicePosMap = new HashMap<>();

    /**
     * 点击器件事件
     *
     * @param event
     */
    @FXML
    void onClickDevice(MouseEvent event) {
        Platform.runLater(() -> {
            if (currentImage == null)
                return;
            // 如果鼠标左键被按下
            if (event.getButton() == MouseButton.PRIMARY) {
                // 获取设备画布的图形上下文
                GraphicsContext gc = deviceCanvas.getGraphicsContext2D();
                double x = event.getX();
                double y = event.getY();
                // 遍历设备位置映射表中的每个设备
                for (Integer key : devicePosMap.keySet()) {
                    List<Integer> list = devicePosMap.get(key);
                    int x1 = list.get(0);
                    int y1 = list.get(1);
                    int w = list.get(2);
                    int h = list.get(3);
                    if (x > x1 && x < x1 + w && y > y1 && y < y1 + h) {
                        // 更新当前选中的设备ID
                        currentSelectDeviceId = key;
                        DeviceDetectResult deviceDetect = deviceDetectHashMap.get(key);
                        // 更新设备信息标签
                        deviceNoLab.setText("设备编号：" + deviceDetect.getId());
                        if (deviceDetect.getIsMatch().equals("True")) {
                            deviceResultLab.setTextFill(Color.GREEN);
                            deviceResultLab.setText("检测结果：通过");
                        } else {
                            deviceResultLab.setTextFill(Color.RED);
                            deviceResultLab.setText("检测结果：不通过");
                        }

                        deviceResultLab.setTextFill(Color.RED);
                        deviceTypeLab.setText("设备类型：" + deviceDetect.getName());
                        deviceMatchLab.setText("匹配度：" + deviceDetect.getMatchValue());
                        // 获取设备的边界框信息
                        List<Integer> bndBox = deviceDetect.getBndBox();
                        w = bndBox.get(2) - bndBox.get(0);
                        h = bndBox.get(3) - bndBox.get(1);
                        x = bndBox.get(0);
                        y = bndBox.get(1);

                        // 获取像素格式
                        WritablePixelFormat<IntBuffer> pixelFormat = WritablePixelFormat.getIntArgbInstance();
                        // 从当前图像中截取设备的子图像
                        PixelReader pixelReader = currentImage.getPixelReader();
                        // 创建一个缓冲区来存储像素数据
                        int[] buffer = new int[w * h];
                        // 使用getPixels方法获取指定区域的像素数据
                        pixelReader.getPixels((int) x, (int) y, w, h, pixelFormat, buffer, 0, w);
                        // 创建 WritableImage 并复制局部像素
                        WritableImage deviceImage = new WritableImage(w, h);
                        // 获取目标WritableImage的PixelWriter
                        PixelWriter pixelWriter = deviceImage.getPixelWriter();
                        // 将像素数据写入目标WritableImage
                        pixelWriter.setPixels(0, 0, w, h, pixelFormat, buffer, 0, w);
                        // 在设备画布上绘制子图像
                        gc.drawImage(deviceImage, 0, 0, deviceCanvas.getWidth(), deviceCanvas.getHeight());
                        break;
                    }
                }
                drawTemplateImage(currentSelectDeviceId);
            }
        });

    }
    public void exitThread(){
        detectResultSubThread.isExit = true;
    }
    /**
     * 初始化方法
     */
    @FXML
    public void initialize()   {
        RepOperationMq.createSocket();
        detectResultSubThread = new DetectResultSubThread(this);
        detectResultSubThread.start();
    }
    /**
     * 设置模板操作，设置后将服务端返回数据暂时通过Alert提示框像素
     */
    @FXML
    void onSetTemplate(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                PCBApplication.class.getResource("set-temp-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 130);
        Stage stage = new Stage();
        stage.setTitle("设置模板!");
        stage.setScene(scene);
        stage.show();
    }
    /**
     * 点击检测按钮事件
     *
     * @param event
     */
    @FXML
    void onDetect(ActionEvent event) {
        // 创建一个HashMap对象，用于存储操作命令
        HashMap startMap = new HashMap<String, Object>();
        // 根据检测标志的状态设置操作命令和提示框的内容
        if (detectFlag) {
            // 停止检测
            startMap.put("operateCode", 3);
        } else {
            // 开始检测
            startMap.put("operateCode", 2);
        }
        // 发送操作命令到消息队列，并接收返回的JSON对象
        JSONObject jsonObject = RepOperationMq.sendObjCommand(startMap);
        Integer code = jsonObject.getInteger("operateCode");
        Alert alert = null;
        if (code == 20) { // 如果返回的操作码为20，表示开始检测
            // 切换检测标志的状态
            detectFlag = !detectFlag;
            // 设置按钮的文本
            detectBtn.setText("停止检测");
            // 创建一个信息提示框
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("启动检测提示");
        } else if (code == 21) {
            // 如果返回的操作码为21，表示启动检测失败
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("启动检测提示");
        }else if (code == 30) {
            // 如果返回的操作码为30，表示停止检测
            // 切换检测标志的状态
            detectFlag = !detectFlag;
            // 设置按钮的文本
            detectBtn.setText("启动检测");
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("停止检测提示");
        }
        // 设置提示框的内容
        alert.setContentText(jsonObject.getString("data"));
        // 显示提示框
        alert.show();
    }

    /**
     * 显示检测信息
     * @param imagePath 图像路径
     * @param result 检测结果信息
     */
    private void showDetectResult(String imagePath, PcbDetectResult result) {
        //清理数据
        devicePosMap.clear();
        deviceDetectHashMap.clear();
        // 重置当前选中的设备ID
        currentSelectDeviceId = -1;
        // 设置检测时间标签的文本，显示当前时间
        detectTimeLab.setText("检测时间：" + DateUtil.format(DateUtil.date(), "yyyy-MM-dd HH:mm:ss"));
        // 获取检测画布的图形上下文
        GraphicsContext context = pcbCanvas.getGraphicsContext2D();
        // 加载检测结果中的图像
        currentImage = new Image("file:" + imagePath);

        // 获取图像的宽度和高度
        int imageWidth = (int) currentImage.getWidth();
        int imageHeight = (int) currentImage.getHeight();
        // 计算图像在画布上的缩放比例
        double scaleW = pcbCanvas.getWidth() / imageWidth;
        double scaleH = pcbCanvas.getHeight() / imageHeight;
        // 在画布上绘制图像，按比例缩放
        context.drawImage(currentImage, 0, 0, pcbCanvas.getWidth(), pcbCanvas.getHeight());
        // 设置画笔颜色为红色
        context.setStroke(Color.RED);
        // 设置画笔线宽为2
        context.setLineWidth(2);
        // 初始化正确和错误计数
        int goodCount = 0;
        int errorCount = 0;
        boolean flag = false;
        for (DeviceDetectResult deviceDetect : result.getDeviceList()) {
            // 将设备检测结果存储在deviceDetectHashMap中，键为设备ID，值为设备检测结果对象
            deviceDetectHashMap.put(deviceDetect.getId(), deviceDetect);
            // 获取设备检测结果的边界框信息
            List<Integer> bndBox = deviceDetect.getBndBox();
            double w = bndBox.get(2) - bndBox.get(0);
            double h = bndBox.get(3) - bndBox.get(1);
            double x = bndBox.get(0);
            double y = bndBox.get(1);
            // 计算边界框的缩放后的宽度、高度、X坐标和Y坐标
            double scaleWidth = w * scaleW;
            double scaleHeight = h * scaleH;
            double scaleX = x * scaleW;
            double scaleY = y * scaleH;
            List<Integer> scaleList = new ArrayList<>();
            scaleList.add((int) scaleX);
            scaleList.add((int) scaleY);
            scaleList.add((int) scaleWidth);
            scaleList.add((int) scaleHeight);
            // 将缩放后的边界框信息存储在devicePosMap中，键为设备ID，值为缩放后的边界框信息列表
            devicePosMap.put(deviceDetect.getId(), scaleList);
            if (deviceDetect.getIsMatch().equals("True")) {
                context.setStroke(Color.GREEN);
                goodCount++;
            } else {
                context.setStroke(Color.RED);
                errorCount++;
                // 如果flag为false，表示这是第一个检测到错误的设备
                if (!flag) {
                    // 将flag设置为true，表示已经处理过第一个错误设备
                    flag = true;
                    context.setStroke(Color.YELLOW);
                    deviceNoLab.setText("设备编号：" + deviceDetect.getId());
                    deviceResultLab.setText("检测结果：不通过");
                    deviceResultLab.setTextFill(Color.RED);
                    deviceTypeLab.setText("设备类型：" + deviceDetect.getName());
                    deviceMatchLab.setText("匹配度：" + deviceDetect.getMatchValue());

                    // 更新当前默认选中的设备ID，默认选中为错误器件
                    currentSelectDeviceId = deviceDetect.getId();

                }
            }

            //处理器件旋转绘制
            if (deviceDetect.getAngle().intValue() != 0) {
                // 创建一个临时画布，用于绘制旋转后的边界框
                Canvas tempCanvas = new Canvas(w + 2, h + 2);
                GraphicsContext tempGraphicsContext = tempCanvas.getGraphicsContext2D();
                // 根据设备检测结果的匹配状态设置画笔颜色
                if (deviceDetect.getIsMatch().equals("True")) {
                    tempGraphicsContext.setStroke(Color.GREEN);
                } else {
                    tempGraphicsContext.setStroke(Color.RED);
                }
                // 在临时画布上绘制边界框
                tempGraphicsContext.strokeRect(1, 1, scaleWidth, scaleHeight);
                // 旋转画布，根据设备检测结果的角度进行旋转
                tempGraphicsContext.rotate(Math.toRadians(deviceDetect.getAngle()));
                // 创建一个可写的图像，用于存储旋转后的边界框
                WritableImage tempWriteableImage = new WritableImage((int) scaleWidth + 10, (int) scaleHeight + 10);
                SnapshotParameters sp = new SnapshotParameters();
                sp.setFill(Color.TRANSPARENT);
                // 将临时画布的内容快照到可写图像中
                tempGraphicsContext.getCanvas().snapshot(sp, tempWriteableImage);
                // 在主画布上绘制旋转后的边界框图像
                context.drawImage((Image) tempWriteableImage, scaleX, scaleY);
            } else {
                // 如果设备检测结果的角度为0，直接在主画布上绘制边界框
                context.strokeRect(scaleX, scaleY, scaleWidth, scaleHeight);
            }

        }
        totalLab.setText("总数：" + result.getDeviceList().size());
        goodLab.setText("正确：" + goodCount);
        errorLab.setText("错误：" + errorCount);
        if (errorCount == 0) {
            DeviceDetectResult deviceDetect = result.getDeviceList().get(0);
            currentSelectDeviceId = deviceDetect.getId();
            context.setStroke(Color.YELLOW);
            deviceNoLab.setText("设备编号：" + deviceDetect.getId());
            deviceResultLab.setText("检测结果：不通过");
            deviceResultLab.setTextFill(Color.RED);
            deviceTypeLab.setText("设备类型：" + deviceDetect.getName());
            deviceMatchLab.setText("匹配度：" + deviceDetect.getMatchValue());

        } else {
            pcbResultLab.setTextFill(Color.RED);
            pcbResultLab.setText("FAIL");
        }
    }
    public void processDetectResult(String imagePath, PcbDetectResult result) {
        if(!detectFlag) return;
        Platform.runLater(() -> {
            showDetectResult(imagePath, result);
        });
    }
    /**
     * 绘制模板图像
     *
     * @param key 设备ID
     */
    private void drawTemplateImage(int key) {
        GraphicsContext gc = templeCanvas.getGraphicsContext2D();
        Device device = SetTemplateController.templeDetectHashMap.get(key);
        BndBox bndBox = device.getBndBox();
        double w = Double.parseDouble(bndBox.getXMax()) - Double.parseDouble(bndBox.getXMin());
        double h = Double.parseDouble(bndBox.getYMax()) - Double.parseDouble(bndBox.getYMin());
        double x = Double.parseDouble(bndBox.getXMin());
        double y = Double.parseDouble(bndBox.getYMin());
        // 从当前图像中截取设备的子图像
        BufferedImage bImage = SwingFXUtils.fromFXImage(SetTemplateController.currentTemplateImage, null);
        BufferedImage subImage = bImage.getSubimage((int) x, (int) y, (int) w, (int) h);
        Image tempImage = SwingFXUtils.toFXImage(subImage, null);
        // 在设备画布上绘制子图像
        gc.drawImage(tempImage, 0, 0, templeCanvas.getWidth(), templeCanvas.getHeight());
    }
}
