package com.bjike.goddess.user.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.user.to.SmsCodeParameterTO;
import com.bjike.goddess.user.to.UserRegisterTO;

/**
 * 注册业务接口
 *
 * @Author: [liguiqin]
 * @Date: [2017-03-11 16:38]
 * @Description: [ ]
 * @Version: [1.0.0]
 * @Copy: [com.bjike]
 */
public interface UserRegisterAPI {

    /**
     * 验证并注册
     *
     * @param registerTO
     * @throws SerException
     */
    void verifyCodeAndReg(UserRegisterTO registerTO) throws SerException;

    /**
     * 注册验证手机并发生验证码
     *
     * @param phone
     */
    void verifyAndSendCode(String phone) throws SerException;


    /**
     * 获取发送短信验证码
     * tanghaixiang
     * @return
     * @throws SerException
     */
    default String sendSmsVerifyCode(SmsCodeParameterTO smsCodeParameterTO ) throws SerException {
        return null;
    }

    /**
     * 校验手机短信验证码
     * tanghaixiang
     * @param phone 手机号
     * @param code 验证码
     * @return
     * @throws SerException
     */
    default Boolean verifyCode(String phone ,String code) throws SerException {
        return null;
    }

}
