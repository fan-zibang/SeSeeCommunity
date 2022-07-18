package com.fanzibang.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanzibang.community.pojo.PrivateLetter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateLetterMapper extends BaseMapper<PrivateLetter> {
    List<PrivateLetter> getPrivateLetterList(@Param("current") Integer current, @Param("size") Integer size, @Param("userId") Long userId);

    int updateStatus(@Param("list") List<Long> list, @Param("status") Integer status);
}
