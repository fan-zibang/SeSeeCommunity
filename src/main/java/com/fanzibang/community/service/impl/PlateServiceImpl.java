package com.fanzibang.community.service.impl;

import com.fanzibang.community.mapper.PlateMapper;
import com.fanzibang.community.pojo.Plate;
import com.fanzibang.community.service.PlateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlateServiceImpl implements PlateService {

    @Autowired
    private PlateMapper plateMapper;

    @Override
    public Plate getPlateById(Integer plateId) {
        return plateMapper.selectById(plateId);
    }

}
