package com.example.blankingmanage.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blankingmanage.entity.User;
import com.example.blankingmanage.mapper.UserMapper;
import com.example.blankingmanage.model.UserSaveModel;
import com.example.blankingmanage.model.UserUpModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 代码大兵Jack
 * @since 2026-01-28
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements IService<User> {
    // 🌟 从配置文件注入盐值
    @Value("${secure.pwd-salt}")
    private String salt;

    //获取盐值
    public String  getSalt(String info) {
        String pwd = SaSecureUtil.sha256BySalt(info, salt);
        return pwd;
    }
    public boolean saveUser(UserSaveModel userModel) {
        User user = new User();
        //入库前拦截明文，替换为 SHA256 密文
        String pwd = this.getSalt(userModel.getPassword());
        BeanUtils.copyProperties(userModel, user);
        user.setPassword(pwd);
        return this.save(user);
    }
    public boolean updateUser(UserUpModel userModel) {
        User user = this.getById(userModel.getId());
        if (userModel.getAddress() != null) {

        }
        if (userModel.getPhone() != null) {

        }
        if (userModel.getAvatar() != null) {

        }
        if (userModel.getRealName() != null) {
            user.setRealName(userModel.getRealName());
        }
        return this.updateById(user);
    }

}
