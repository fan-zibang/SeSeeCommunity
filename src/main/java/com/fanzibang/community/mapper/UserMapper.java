package com.fanzibang.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanzibang.community.pojo.User;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface UserMapper extends BaseMapper<User> {
}
