package com.fanzibang.community.service;

import java.util.Date;
import java.util.List;

public interface DataService {
    Long setUV(String ip);

    Boolean setDAU(Long id);

    Long getUV(Date startTime, Date endTime);

    Long getDAU(Date startTime, Date endTime);

    void setHotWord(String keyword);

    List<String> getHotWord();

}
