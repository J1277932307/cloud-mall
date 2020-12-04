package xyz.jiang.cloud.mall.user.controller;

import com.mysql.cj.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.jiang.cloud.mall.common.common.ApiRestResponse;
import xyz.jiang.cloud.mall.common.common.Constant;
import xyz.jiang.cloud.mall.common.exception.MallException;
import xyz.jiang.cloud.mall.common.exception.MallExceptionEnum;
import xyz.jiang.cloud.mall.user.model.pojo.User;
import xyz.jiang.cloud.mall.user.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/*
 * 描述：用户控制器
 */
@Controller
public class UserController {

    @Resource
    UserService userService;



    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("userName") String userName, @RequestParam("password") String password) throws MallException {
        //校验用户名是否为空
        if (StringUtils.isNullOrEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        //校验密码是否为空
        if (StringUtils.isNullOrEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        //校验密码长度
        if (password.length() < 8) {
            return ApiRestResponse.error(MallExceptionEnum.PASSWORD_TOO_SHORT);
        }

        userService.register(userName, password);

        return ApiRestResponse.success();
    }

    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws MallException {
        //校验用户名是否为空
        if (StringUtils.isNullOrEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        //校验密码是否为空
        if (StringUtils.isNullOrEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        //返回之前，将用户密码置空
        user.setPassword(null);
        session.setAttribute(Constant.MALL_USER,user);
        return ApiRestResponse.success(user);
    }

    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature) throws MallException {
        User currentUser = (User) session.getAttribute(Constant.MALL_USER);
        if (null == currentUser) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);

        return ApiRestResponse.success();
    }

    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session) {
        session.removeAttribute(Constant.MALL_USER);
        return ApiRestResponse.success();
    }

    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws MallException {
        //校验用户名是否为空
        if (StringUtils.isNullOrEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        //校验密码是否为空
        if (StringUtils.isNullOrEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        //校验是否是管理员
        if (userService.checkAdminRole(user)) {
            //返回之前，将用户密码置空
            user.setPassword(null);
            session.setAttribute(Constant.MALL_USER, user);
            return ApiRestResponse.success(user);
        } else {
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADDMIN);
        }

    }

    @PostMapping("/checkAdminRole")
    @ResponseBody
    public Boolean checkAdminRole(@RequestBody User user) {
        //检验是否是管理员
        return userService.checkAdminRole(user);
    }

    @GetMapping("/getUser")
    @ResponseBody
    public User getUser(HttpSession session) {
       User currentUser =  (User)session.getAttribute(Constant.MALL_USER);
        return currentUser;
    }

}
