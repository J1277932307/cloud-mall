package xyz.jiang.cloud.mall.user.service.impl;

import org.springframework.stereotype.Service;
import xyz.jiang.cloud.mall.common.exception.MallException;
import xyz.jiang.cloud.mall.common.exception.MallExceptionEnum;
import xyz.jiang.cloud.mall.common.utils.MD5Utils;
import xyz.jiang.cloud.mall.user.model.dao.UserMapper;
import xyz.jiang.cloud.mall.user.model.pojo.User;
import xyz.jiang.cloud.mall.user.service.UserService;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;

/*
 * 描述：UserService实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;
    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }

    @Override
    public void register(String userName, String password) throws MallException {
        //是否存在重名
        User user = userMapper.selectByName(userName);
        if (user != null) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }
        //写入数据库
        User newUser = new User();
        newUser.setUsername(userName);
        try {
            newUser.setPassword(MD5Utils.getMD5Str(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        int count = userMapper.insertSelective(newUser);
        if (count == 0) {
            throw new MallException(MallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String userName, String password) throws MallException {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        User user = userMapper.selectLogin(userName, md5Password);
        if (null == user) {
            throw new MallException(MallExceptionEnum.WRONG_PASSWORD);
        }

        return user;
    }

    @Override
    public void updateInformation(User user) throws MallException {
        //更新个性签名
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 1) {
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user) {
        return user.getRole().equals(2);
    }
}
