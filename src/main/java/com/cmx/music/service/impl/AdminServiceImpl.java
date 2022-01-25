package com.cmx.music.service.impl;

import com.cmx.music.dao.AdminMapper;
import com.cmx.music.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public boolean veritypasswd(String name, String password) {

        return adminMapper.verifyPassword(name, password) > 0;
    }
}
