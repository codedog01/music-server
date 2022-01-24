package com.cmx.music.service.impl;

import com.cmx.music.dao.RankMapper;
import com.cmx.music.pojo.Rank;
import com.cmx.music.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RankServiceImpl implements RankService {

    @Autowired
    private RankMapper rankMapper;

    @Override
    public int rankOfSongListId(Long songListId) {
        int i = rankMapper.selectScoreSum(songListId);
        int a = rankMapper.selectRankNum(songListId);
        return a == 0 ? 0 : i / a;
    }

    @Override
    public boolean addRank(Rank rank) {

        return rankMapper.insertSelective(rank) > 0;
    }
}
