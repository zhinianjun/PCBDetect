package com.example.pcbclient.entity;

import lombok.Data;
/**
 * 器件
 */
@Data
public class Device {
    /**
     * id
     */
    private int id;
    /**
     * 名称
     */
    private String name;
    /**
     * 正面方向
     */
    private String positive;
    /**
     * 条码
     */
    private String barCode;

    /**
     * 角度
     */
    private String angle;

    /**
     * 忽略色彩
     */

    private String ignoreColor;
    /**
     * 边界框
     */

    private BndBox bndBox;
    /**
     * 比较文本行
     */
    private String compareTextLine;
}