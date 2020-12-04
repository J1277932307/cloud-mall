package xyz.jiang.cloud.mall.user.service;


import xyz.jiang.cloud.mall.common.exception.MallException;
import xyz.jiang.cloud.mall.user.model.pojo.User;

public interface UserService {
    public User getUser();

    void register(String userName, String password) throws MallException;

    User login(String userName, String password) throws MallException;

    void updateInformation(User user) throws MallException;

    boolean checkAdminRole(User user);
}
