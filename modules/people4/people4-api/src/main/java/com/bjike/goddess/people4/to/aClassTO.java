package com.bjike.goddess.people4.to;

import com.bjike.goddess.common.api.to.BaseTO;

/**
 * 测试班级
 *
 * @Author: [ Jianyangfeng ]
 * @Date: [ 2018-03-19 10:22 ]
 * @Description: [ 测试班级 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
public class aClassTO extends BaseTO {

    /**
     * 班级编号
     */
    private String ClaNum;

    /**
     * 专业名称
     */
    private String ClaType;


    public String getClaNum() {
        return ClaNum;
    }

    public void setClaNum(String ClaNum) {
        this.ClaNum = ClaNum;
    }

    public String getClaType() {
        return ClaType;
    }

    public void setClaType(String ClaType) {
        this.ClaType = ClaType;
    }
}