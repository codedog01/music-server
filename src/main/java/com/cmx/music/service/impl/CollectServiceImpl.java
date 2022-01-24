package com.cmx.music.service.impl;

import com.cmx.music.dao.CollectMapper;
import com.cmx.music.pojo.Collect;
import com.cmx.music.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectServiceImpl implements CollectService {
    @Autowired
    private CollectMapper collectMapper;

    @Override
    public boolean addCollection(Collect collect) {
        return collectMapper.insertSelective(collect) > 0 ? true:false;
    }

    @Override
    public boolean existSongId(Integer userId, Integer songId) {
        return collectMapper.existSongId(userId, songId)>0 ? true:false;
    }

    @Override
    public boolean updateCollectMsg(Collect collect) {
        return collectMapper.updateCollectMsg(collect) >0 ?true:false;
    }

    @Override
    public boolean deleteCollect(Integer userId, Integer songId) {
        return collectMapper.deleteCollect(userId, songId) >0 ?true:false;
    }

    @Override
    public List<Collect> allCollect()

    {
        return collectMapper.allCollect();
    }

    @Override
    public List<Collect> collectionOfUser(Integer userId)

    {
        return collectMapper.collectionOfUser(userId);
    }
}
