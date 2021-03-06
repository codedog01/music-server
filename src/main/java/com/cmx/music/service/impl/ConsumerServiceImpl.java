package com.cmx.music.service.impl;

import com.cmx.music.dao.ConsumerMapper;
import com.cmx.music.pojo.Consumer;
import com.cmx.music.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Autowired
    private ConsumerMapper consumerMapper;

    @Override
    public boolean addUser(Consumer consumer) {
        return consumerMapper.insertSelective(consumer) >0 ?true:false;
    }

    @Override
    public boolean updateUserMsg(Consumer consumer) {
        return consumerMapper.updateUserMsg(consumer) >0 ?true:false;
    }

    @Override
    public boolean updateUserAvator(Consumer consumer) {

        return consumerMapper.updateUserAvator(consumer) > 0;
    }

    @Override
    public boolean existUser(String username) {
        return consumerMapper.existUsername(username) > 0;
    }

    @Override
    public boolean veritypasswd(String username, String password) {
        return consumerMapper.verifyPassword(username, password) != null;
    }

//    删除用户
    @Override
    public boolean deleteUser(Integer id) {
        return consumerMapper.deleteUser(id) > 0;
    }

    @Override
    public List<Consumer> allUser() {
        return consumerMapper.allUser();
    }

    @Override
    public List<Consumer> userOfId(Integer id) {

        return consumerMapper.userOfId(id);
    }

    @Override
    public List<Consumer> loginStatus(String username) {

        return consumerMapper.loginStatus(username);
    }
}
