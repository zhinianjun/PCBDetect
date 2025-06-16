package com.example.pcbclient;

import com.example.pcbclient.entity.Device;
import com.example.pcbclient.entity.PcbTemplate;
import com.example.pcbclient.mq.RepOperationMq;
import com.alibaba.fastjson.JSONObject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.util.HashMap;

public class SetTemplateController {
    public static PcbTemplate currentTemplate;
    /**
     * 当前模板图像
     */
    public static Image currentTemplateImage = null;
    /**
     * 模板器件哈希表
     */
    public static HashMap<Integer, Device> templeDetectHashMap = new HashMap<>();

    /**
     * 模板选择下拉框
     */
    @FXML
    private ComboBox templateCombobox;

    @FXML
    public void onSetTemplate() {
        // 设置模板操作发送数据
        HashMap dataMap = new HashMap<String, Object>();
        dataMap.put("operateCode", 1);
        dataMap.put("data",templateCombobox.getValue());
        //调用发送命令方法发送数据
        JSONObject jsonObject = RepOperationMq.sendObjCommand(dataMap);
        JSONObject data = jsonObject.getJSONObject("data");
        // 解析模板数据
        parseTemplate(data);
        // 返回信息通过Alert提示框显示
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("信息");
        alert.setHeaderText("设置模板返回信息");
        alert.setContentText(data.getString("image"));
        alert.showAndWait();
        // 关闭当前窗口
        Stage sb = (Stage) templateCombobox.getScene().getWindow();//use any one object
        sb.close();
    }
    /**
     * 解析模板数据
     * @param jsonObject
     */
    private void parseTemplate(JSONObject jsonObject) {
        // 获取data字段的JSON对象
        JSONObject data = jsonObject.getJSONObject("json");
        // 获取模板图像的路径
        String templateImagePath = jsonObject.getString("image");
        // 将JSON对象转换为pcbTemplateResult对象
        currentTemplate = data.toJavaObject(PcbTemplate.class);
        // 加载模板图像
        Image image = new Image("file:" + templateImagePath);
        // 将模板图像存储在currentTemplateImage变量中
        currentTemplateImage = image;
        templeDetectHashMap.clear();
        for (Device device : currentTemplate.getDeviceList()) {
            // 将模板中的设备信息存储在templeDetectHashMap中，键为设备ID，值为设备对象
            templeDetectHashMap.put(device.getId(), device);
        }
    }
}
