package com.cmx.music.service;

import com.cmx.music.pojo.Rank;

public interface RankService {

    int rankOfSongListId(Long songListId);

    boolean addRank(Rank rank);
}
