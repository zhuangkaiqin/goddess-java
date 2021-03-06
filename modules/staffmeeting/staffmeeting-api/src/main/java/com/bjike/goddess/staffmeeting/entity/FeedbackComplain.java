package com.bjike.goddess.staffmeeting.entity;

import com.bjike.goddess.common.api.entity.BaseEntity;
import com.bjike.goddess.common.api.type.Status;

import javax.persistence.*;


/**
 * 通告反馈投诉
 *
 * @Author: [ Jason ]
 * @Date: [ 2017-06-06 04:23 ]
 * @Description: [ 通告反馈投诉 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Entity
@Table(name = "staffmeeting_feedbackcomplain")
public class FeedbackComplain extends BaseEntity {

    /**
     * 会议纪要
     */
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.REFRESH)
    @JoinColumn(name = "meetingSummary_id", nullable = false, columnDefinition = "VARCHAR(36)   COMMENT '会议纪要'")
    private MeetingSummary meetingSummary;

    /**
     * 异议人
     */
    @Column(name = "dissentUser", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '异议人'")
    private String dissentUser;

    /**
     * 异议人员工编号
     */
    @Column(name = "dissentUserNum", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '异议人'")
    private String dissentUserNum;

    /**
     * 异议内容
     */
    @Column(name = "dissentContent", nullable = false, columnDefinition = "VARCHAR(255)   COMMENT '异议内容'")
    private String dissentContent;

    /**
     * 状态
     */
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT(2)  DEFAULT 0   COMMENT '异议内容'", insertable = false)
    private Status status;


    public MeetingSummary getMeetingSummary() {
        return meetingSummary;
    }

    public void setMeetingSummary(MeetingSummary meetingSummary) {
        this.meetingSummary = meetingSummary;
    }

    public String getDissentUser() {
        return dissentUser;
    }

    public void setDissentUser(String dissentUser) {
        this.dissentUser = dissentUser;
    }

    public String getDissentUserNum() {
        return dissentUserNum;
    }

    public void setDissentUserNum(String dissentUserNum) {
        this.dissentUserNum = dissentUserNum;
    }

    public String getDissentContent() {
        return dissentContent;
    }

    public void setDissentContent(String dissentContent) {
        this.dissentContent = dissentContent;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}