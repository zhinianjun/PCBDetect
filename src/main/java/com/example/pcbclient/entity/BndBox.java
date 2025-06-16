package com.example.pcbclient.entity;

import lombok.Data;

/**
 * 边界框
 */
@Data
public class BndBox {
    /**
     * 最小x
     */
    private String xMin;
    /**
     * 最小y
     */
    private String yMin;

    /**
     * 最大x
     */
    private String xMax;
    /**
     * 最大y
     */
    private String yMax;
}
