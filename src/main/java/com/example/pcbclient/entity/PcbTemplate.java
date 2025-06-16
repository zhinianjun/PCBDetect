package com.example.pcbclient.entity;

import lombok.Data;
import java.util.List;
/**
 * PCB模板
 */
@Data
public class PcbTemplate {
    /**
     * 模板图片
     */
    private String templateImage;
    /**
     * 检测模型
     */
    private String model;
    /**
     * 尺寸
     */
    private Size size;
    /**
     * 器件列表
     */
    private List<Device> deviceList ;
}
