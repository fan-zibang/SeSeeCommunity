package com.fanzibang.community.controller;

import com.fanzibang.community.service.EsDiscussPostService;
import com.fanzibang.community.vo.DiscussPostDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/esDiscussPost")
public class EsDiscussPostController {

    @Autowired
    private EsDiscussPostService esDiscussPostService;

    @GetMapping("/search")
    public Page<DiscussPostDetailVo> search(String keyword, Integer current, Integer size, Integer topicId,
                                            @Valid @Min(value = 1, message = "排序方式")
                                          @Max(value = 2, message = "排序方式：1-热度；2-最新") Integer sort) {
        return esDiscussPostService.search(keyword, current, size, topicId, sort);
    }
}
