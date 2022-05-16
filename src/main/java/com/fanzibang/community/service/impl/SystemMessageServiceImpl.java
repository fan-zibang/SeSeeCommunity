package com.fanzibang.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanzibang.community.mapper.SystemMessageMapper;
import com.fanzibang.community.pojo.SystemMessage;
import com.fanzibang.community.service.SystemMessageService;
import org.springframework.stereotype.Service;

@Service
public class SystemMessageServiceImpl extends ServiceImpl<SystemMessageMapper, SystemMessage> implements SystemMessageService {

}
