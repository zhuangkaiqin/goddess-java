package com.bjike.goddess.archive.bo;

import com.bjike.goddess.common.api.bo.BaseBO;
import com.bjike.goddess.common.api.type.Status;

/**
 * 劳动关系类型业务传输对象
 *
 * @Author: [ dengjunren ]
 * @Date: [ 2017-04-12 11:05 ]
 * @Description: [ 劳动关系类型业务传输对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class LaborRelationBO extends BaseBO {

    /**
     * 类型名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private Status status;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}