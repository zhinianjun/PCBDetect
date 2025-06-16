package com.example.pcbclient.entity;

import lombok.Data;

import java.util.List;


/**
 * 器件检测结果
 */
@Data
public class DeviceDetectResult {
    /**
     * 器件ID
     */
    private int id;
    /**
     * 错误类型
     */
    private String wrongType;
    /**
     * 是否匹配
     */
    private String isMatch;
    /**
     * 器件外接矩形左上角坐标和右下角坐标
     */
    private List<Integer> bndBox;
    /**
     * 器件名称
     */
    private String name;
    /**
     * 匹配度
     */
    private Double matchValue;
    /**
     * 正级否
     */
    private String positive;
    /**
     * 条码
     */
    private String barCode;
    /**
     * 角度
     */
    private Double angle;
    
}

