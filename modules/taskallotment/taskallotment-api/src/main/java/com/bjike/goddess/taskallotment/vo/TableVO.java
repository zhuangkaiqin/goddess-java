package com.bjike.goddess.taskallotment.vo;

import com.bjike.goddess.taskallotment.bo.NodeBO;
import com.bjike.goddess.taskallotment.enums.Status;

import java.util.List;

/**
 * 项目表表现层对象
 *
 * @Author: [ chenjunhao ]
 * @Date: [ 2017-09-14 11:58 ]
 * @Description: [ 项目表表现层对象 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class TableVO {

    /**
     * id
     */
    private String id;
    /**
     * 表名称
     */
    private String name;

    /**
     * 状态
     */
    private Status status;

    /**
     * 任务节点名称集合
     */
    private List<NodeBO> nodeS;

    public List<NodeBO> getNodeS() {
        return nodeS;
    }

    public void setNodeS(List<NodeBO> nodeS) {
        this.nodeS = nodeS;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}