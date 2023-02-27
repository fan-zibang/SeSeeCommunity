package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fanzibang.community.constant.EntityTypeConstant;
import com.fanzibang.community.pojo.Comment;
import com.fanzibang.community.pojo.DiscussPost;
import com.fanzibang.community.pojo.Topic;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.repository.EsDiscussPostRepository;
import com.fanzibang.community.service.*;
import com.fanzibang.community.vo.DiscussPostDetailVo;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EsDiscussPostServiceImpl implements EsDiscussPostService {

    @Autowired
    private EsDiscussPostRepository esDiscussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private DataService dataService;

    @Override
    public DiscussPost save(DiscussPost discussPost) {
        return esDiscussPostRepository.save(discussPost);
    }

    @Override
    public void deleteById(Long id) {
        esDiscussPostRepository.deleteById(id);
    }

    @Override
    public Page<DiscussPostDetailVo> search(String keyword, Integer current, Integer size, Integer topicId, Integer sort) {
        // 热词统计
        dataService.setHotWord(keyword);
        current = Optional.ofNullable(current).orElse(1);
        size = Optional.ofNullable(size).orElse(20);
        Pageable pageable = PageRequest.of(current-1, size);
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withPageable(pageable);
        if (StrUtil.isEmpty(keyword)) {
            queryBuilder.withQuery(QueryBuilders.matchAllQuery());
        } else {
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                    .withHighlightFields(new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"));
        }
        if (ObjectUtil.isNotNull(topicId)) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termQuery("topicId", topicId));
            queryBuilder.withFilter(boolQueryBuilder);
        }
        if (ObjectUtil.isNotNull(sort)) {
            switch (sort) {
                case 1:
                    queryBuilder.withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                            .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC));
                    break;
                case 2:
                    queryBuilder.withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC));
                    break;
            }
        }

        NativeSearchQuery searchQuery = queryBuilder.build();
        SearchHits<DiscussPost> postSearchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        if (postSearchHits.getTotalHits() <= 0) {
            return new PageImpl<>(Collections.emptyList(), pageable,0);
        }
        List<DiscussPostDetailVo> discussPostDetailVoList = copyList(postSearchHits.getSearchHits());
        return new PageImpl<>(discussPostDetailVoList, pageable, postSearchHits.getTotalHits());
    }

    private List<DiscussPostDetailVo> copyList(List<SearchHit<DiscussPost>> searchHits) {
        ArrayList<DiscussPostDetailVo> discussPostDetailVoList = new ArrayList<>();
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            DiscussPostDetailVo discussPostDetailVo = copy(searchHit.getContent(),1);
            if (ObjectUtil.isNotNull(searchHit.getHighlightFields().get("title"))) {
                discussPostDetailVo.setTitle(searchHit.getHighlightFields().get("title").get(0));
            }
            discussPostDetailVoList.add(discussPostDetailVo);
        }
        return discussPostDetailVoList;
    }

    private DiscussPostDetailVo copy(DiscussPost discussPost, Integer mode) {
        DiscussPostDetailVo discussPostDetailVo = new DiscussPostDetailVo();
        switch (mode) {
            case 1: BeanUtil.copyProperties(discussPost, discussPostDetailVo, "content"); break;
            case 2: BeanUtil.copyProperties(discussPost, discussPostDetailVo); break;
        }
        User user = userService.getById(discussPost.getUserId());
        if (ObjectUtil.isNotNull(user)) {
            discussPostDetailVo.setAuthor(user.getNickname());
        }
        long likeCount = likeService.getLikeCount(EntityTypeConstant.ENTITY_TYPE_POST, discussPost.getId());
        discussPostDetailVo.setLikeCount(likeCount);
        Topic topic = topicService.getTopicById(discussPost.getTopicId());
        if (ObjectUtil.isNotNull(topic)) {
            discussPostDetailVo.setTopic(topic.getName());
        }
        String createTime = DateUtil.date(discussPost.getCreateTime()).toString("yyyy-MM-dd HH:mm");
        discussPostDetailVo.setCreateTime(createTime);
        return discussPostDetailVo;
    }
}
