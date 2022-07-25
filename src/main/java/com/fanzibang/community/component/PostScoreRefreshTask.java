package com.fanzibang.community.component;

import com.fanzibang.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PostScoreRefreshTask {

    @Autowired
    private DiscussPostService discussPostService;

    @Scheduled(cron = "0 13 1 * * ?")
    private void refreshPostScore() {
        discussPostService.refreshPostScore();
    }

}
