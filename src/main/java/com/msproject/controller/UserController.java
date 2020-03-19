package com.msproject.controller;

import com.msproject.controller.viewoj.UserVO;
import com.msproject.dao.UserDOMapper;
import com.msproject.dataobject.UserDO;
import com.msproject.dataobject.UserPasswordDO;
import com.msproject.error.BusinessException;
import com.msproject.error.EmBusinessError;
import com.msproject.response.CommonReturnType;
import com.msproject.service.UserService;
import com.msproject.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户登录
    @RequestMapping("/login")
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="telphone") String tel,@RequestParam(name="password") String password) throws BusinessException, NoSuchAlgorithmException {
        if(tel==null || password==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //验证用户名密码是否匹配
        UserModel userModel = userService.validLogin(tel,EncodeByMD5(password));

        //存用户登录信息
        this.httpServletRequest.getSession().setAttribute("LOGIN_SUCCESS",true);
        this.httpServletRequest.getSession().setAttribute("USER_INFO",userModel);

        return CommonReturnType.create(null);
    }


    //用户注册
    @RequestMapping("/register")
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="telphone") String tel,@RequestParam(name="codeotp") String code,
                                     @RequestParam(name="name") String name,@RequestParam(name="age") String age,
                                     @RequestParam(name="gender") String gender,@RequestParam(name="password") String password) throws BusinessException, NoSuchAlgorithmException {

        String InSessionCode = (String) this.httpServletRequest.getSession().getAttribute(tel);
        if(!com.alibaba.druid.util.StringUtils.equals(code,InSessionCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"验证码错误");
        }

        UserModel userModel = new UserModel();
        userModel.setTelphone(tel);
        userModel.setName(name);
        userModel.setAge(Integer.parseInt(age));
        userModel.setGender(Byte.valueOf(gender));
        userModel.setRegisterMode("byphone");
        userModel.setEncryptPassword(EncodeByMD5(password));

        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    public String EncodeByMD5(String str) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder encoder = Base64.getEncoder();
        String newStr = encoder.encodeToString(md5.digest(str.getBytes()));
        return newStr;
    }

    //生成验证码，用户注册
    @RequestMapping("/getotp")
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone") String tel){
        //生成otp
        Random random = new Random();
        int code = random.nextInt(8999);
        code+=1000;
        String otpcode = String.valueOf(code);
        //绑定手机号和opt
        httpServletRequest.getSession().setAttribute(tel,otpcode);
        System.out.println("tel = "+tel+"&otpcode="+otpcode);

        return CommonReturnType.create(null);
    }

    //返回用户信息
    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }

    public UserVO convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }
}
