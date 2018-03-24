package com.bjike.goddess.user.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.user.service.UserLoginSer;
import com.bjike.goddess.user.to.UserLoginTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @Author: [liguiqin]
 * @Date: [2017-03-11 15:23]
 * @Description: [ ]
 * @Version: [1.0.0]
 * @Copy: [com.bjike]
 */
@Service("userLoginApiImpl")
public class UserLoginApiImpl implements UserLoginAPI {
    @Autowired(required = false)
    private UserLoginSer userLoginSer;

    @Override
    public String login(UserLoginTO loginTO) throws SerException {
        return userLoginSer.login(loginTO);
    }

    @Override
    public String logins(UserLoginTO loginTO) throws SerException {
        return userLoginSer.logins(loginTO);
    }

    @Override
    public Boolean signOut(String token) throws SerException {
        return userLoginSer.signOut(token);
    }
}
