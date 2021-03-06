package com.bjike.goddess.user.service;

import com.aliyuncs.exceptions.ClientException;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.api.type.Status;
import com.bjike.goddess.common.jpa.utils.PasswordHash;
import com.bjike.goddess.common.utils.regex.Validator;
import com.bjike.goddess.user.bo.UserBO;
import com.bjike.goddess.user.entity.User;
import com.bjike.goddess.user.enums.UserType;
import com.bjike.goddess.user.session.auth_code.AuthCodeSession;
import com.bjike.goddess.user.to.SmsCodeParameterTO;
import com.bjike.goddess.user.to.UserRegisterTO;
import com.bjike.goddess.user.utils.SeqUtil;
import com.bjike.goddess.user.utils.SmsCodeUtil;
import com.bjike.goddess.user.vo.SmsReceiveCodeVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户注册业务实现
 *
 * @Author: [liguiqin]
 * @Date: [2016-11-23 15:47]
 * @Description: ]
 * @Version: [1.0.0]
 * @Copy: [com.bjike]
 */
@CacheConfig(cacheNames = "userSerCache")
@Service
public class UserRegisterSerImpl implements UserRegisterSer {

    @Autowired
    private UserSer userSer;


    @Override
    public void verifyAndSendCode(String phone) throws SerException {

        if (null == userSer.findByPhone(phone)) {
            //generateCode()
            String code = "123456";
            phone = "13457910241";
            AuthCodeSession.put(phone, code);

        } else {
            throw new SerException("该手机号码已注册！");

        }
    }

    @Transactional
    @Override
    public void verifyCodeAndReg(UserRegisterTO registerTO) throws SerException {


        if (registerTO.getPassword().equals(registerTO.getRePassword())) {
            if (!Validator.isPassword(registerTO.getPassword())) {
                throw new SerException("密码过于简单！");
            }
        } else {
            throw new SerException("输入密码不一致！");
        }
        saveUserByDTO(registerTO);
    }

    /**
     * 通过用户注册数据传输实体保存用户
     *
     * @param registerTO
     * @throws SerException
     */
    private void saveUserByDTO(UserRegisterTO registerTO) throws SerException {
        try {
            UserBO bo = userSer.findByUsername(registerTO.getUsername());
            if (null == bo) {
                String employeeNumber = userSer.maxUserEmpNumber();
                String sysNO = userSer.findByMaxField("systemNO", User.class);
                User user = new User();
                user.setUsername(registerTO.getUsername());
                user.setPassword(PasswordHash.createHash(registerTO.getPassword()));
                user.setUserType(UserType.ADMIN);
                user.setCreateTime(LocalDateTime.now());
                user.setStatus(Status.THAW);
                user.setEmployeeNumber(SeqUtil.generateEmp(employeeNumber));
                user.setSystemNO(SeqUtil.generateSys(sysNO));
                userSer.save(user);
            } else {
                throw new SerException(registerTO.getUsername() + "已被注册!");
            }

        } catch (Exception e) {
            throw new SerException(e.getMessage());
        }
    }


    @Override
    public String sendSmsVerifyCode(SmsCodeParameterTO smsCodeParameterTO) throws SerException {
        if (StringUtils.isBlank(smsCodeParameterTO.getPhoneNumber())) {
            throw new SerException("手机号不能为空");
        }
        if (null != userSer.findByPhone(smsCodeParameterTO.getPhoneNumber())) {
            throw new SerException("改手机号已经注册");
        }
        String code = "";
        SmsReceiveCodeVO smsReceiveCodeVO = new SmsReceiveCodeVO();
        try {
            //调用阿里的短信
            smsReceiveCodeVO = SmsCodeUtil.mainEnter(smsCodeParameterTO);
            if( null != smsReceiveCodeVO ){
                code = smsReceiveCodeVO.getCode();
                if (StringUtils.isNotBlank(code) && "ok".equals(code.toLowerCase())) {
                    //说明发送成功
                    Pattern expression = Pattern.compile("[0-9]{" + Integer.parseInt(smsCodeParameterTO.getRandomNum()) + "}");//创建匹配模式
                    Matcher matcher2 = expression.matcher( smsReceiveCodeVO.getContent() );//通过匹配模式得到匹配器
                    if (matcher2.find()) {
                        String smsCode = matcher2.group();
                        //将验证码存session，这里使用5分钟失效
                        AuthCodeSession.put(smsCodeParameterTO.getPhoneNumber(), smsCode);
                    }
                }
            }
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return code;
    }

    @Override
    public Boolean verifyCode(String phone, String code) throws SerException {
        Boolean verifyFlag = false;
        String sessionCode = AuthCodeSession.get( phone );
        if( StringUtils.isBlank( code ) || StringUtils.isBlank( sessionCode)){
            throw new SerException("验证码不正确");
        }else if( code.equals( sessionCode)){
            verifyFlag = true;
        }else if( !code.equals( sessionCode) ){
            throw new SerException("验证码不正确");
        }
        return verifyFlag;
    }
}
