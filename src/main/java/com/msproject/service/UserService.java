package com.msproject.service;

import com.msproject.dataobject.UserDO;
import com.msproject.dataobject.UserPasswordDO;
import com.msproject.error.BusinessException;
import com.msproject.service.model.UserModel;

public interface UserService {
    //通过用户id获取用户对象
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessException;
    UserModel validLogin(String tel,String encryptpassword) throws BusinessException;
}
