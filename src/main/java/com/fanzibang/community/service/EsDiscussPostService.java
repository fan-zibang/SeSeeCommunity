package com.fanzibang.community.service;

import com.fanzibang.community.pojo.DiscussPost;
import com.fanzibang.community.vo.DiscussPostDetailVo;
import org.springframework.data.domain.Page;

public interface EsDiscussPostService {

    DiscussPost save(DiscussPost discussPost);

    Page<DiscussPostDetailVo> search(String keyword, Integer current, Integer size, Integer topicId,Integer sort);

}
