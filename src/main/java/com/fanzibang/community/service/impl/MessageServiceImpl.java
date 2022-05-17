package com.fanzibang.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanzibang.community.mapper.MessageMapper;
import com.fanzibang.community.pojo.Message;
import com.fanzibang.community.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

}
