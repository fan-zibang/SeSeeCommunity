package com.fanzibang.community.service;

import java.util.Date;

public interface DataService {
    Long setUV(String ip);

    Boolean setDAU(Long id);

    Long getUV(Date startTime, Date endTime);

    Long getDAU(Date startTime, Date endTime);
}
