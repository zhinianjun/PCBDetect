package com.example.pcbclient.entity;

import lombok.Data;

import java.util.List;
@Data
public class PcbDetectResult {
    /**
     * 模板名称
     */
    private String template;
    /**
     * 器件检测结果
     */
    private List<DeviceDetectResult> deviceList;

}
