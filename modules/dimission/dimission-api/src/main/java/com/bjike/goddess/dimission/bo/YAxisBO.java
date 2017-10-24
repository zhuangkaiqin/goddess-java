package com.bjike.goddess.dimission.bo;

import com.bjike.goddess.common.api.bo.BaseBO;

/**
 * 转正图形展示纵坐标传输对象
 * @Author: [lijuntao]
 * @Date: [2017-09-09 15:32]
 * @Description: [转正图形展示纵坐标传输对象 ]
 * @Version: [1.0.0]
 * @Copy: [com.bjike]
 */
public class YAxisBO extends BaseBO{
    /**
     * 数据
     */
    private Integer[] data;

    public Integer[] getData() {
        return data;
    }

    public void setData(Integer[] data) {
        this.data = data;
    }
}
