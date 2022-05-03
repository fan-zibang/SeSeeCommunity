package com.fanzibang.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanzibang.community.pojo.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}
