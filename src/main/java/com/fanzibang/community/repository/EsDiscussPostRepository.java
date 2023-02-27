package com.fanzibang.community.repository;

import com.fanzibang.community.pojo.DiscussPost;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsDiscussPostRepository extends ElasticsearchRepository<DiscussPost,Long> {
    Page<DiscussPost> findByTitleOrContent(String title, String content, Pageable pageable);
}
