package com.msproject.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.msproject.dao.UserDOMapper;
import com.msproject.dao.UserPasswordDOMapper;
import com.msproject.dataobject.UserDO;
import com.msproject.dataobject.UserPasswordDO;
import com.msproject.error.BusinessException;
import com.msproject.error.EmBusinessError;
import com.msproject.service.UserService;
import com.msproject.service.model.UserModel;
import com.msproject.validator.ValidationResult;
import com.msproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;
    @Autowired
    private ValidatorImpl validator;

    //获取用户信息
    @Override
    public UserModel getUserById(Integer id) {
        //调用userDOMapper获取对应用户dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if(userDO==null){
            return null;
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        return convertFromDataObject(userDO,userPasswordDO);
    }

    //用户注册
    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        ValidationResult validationResult = validator.validate(userModel);
        if(validationResult.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,validationResult.getErrMsg());
        }

        UserDO userDO = convertFromDo(userModel);
        try {
            userDOMapper.insertSelective(userDO);
        }catch (Exception ex){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户已存在");
        }
        userModel.setId(userDO.getId());
        UserPasswordDO userPasswordDO = convertFromDoPassword(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
        return;
    }

    //用户登录
    @Override
    public UserModel validLogin(String tel, String encryptpassword) throws BusinessException {
        //取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(tel);
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = this.convertFromDataObject(userDO,userPasswordDO);
        String userPassword = userModel.getEncryptPassword();

        if(!StringUtils.equals(encryptpassword,userPassword)){
            throw new BusinessException(EmBusinessError.USER_ERROR);
        }

        return userModel;
    }

    public UserDO convertFromDo(UserModel userModel) throws BusinessException {
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }

    private UserPasswordDO convertFromDoPassword(UserModel userModel) throws BusinessException {
        if(userModel == null || userModel.getEncryptPassword()==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setUserId(userModel.getId());
        userPasswordDO.setEncryptPassword(userModel.getEncryptPassword());
        return userPasswordDO;
    }


    public UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO==null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);
        if(userPasswordDO!=null) {
            userModel.setEncryptPassword(userPasswordDO.getEncryptPassword());
        }
        return userModel;
    }
}
