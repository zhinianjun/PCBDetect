package com.example.pcbclient.entity;

import lombok.Data;

/**
 * 尺寸
 */
@Data
public class Size {
    /**
     * 宽
     */
    private String width;
    /**
     * 高
     */
    private String height;
    /**
     * 深度
     */
    private String depth;
}