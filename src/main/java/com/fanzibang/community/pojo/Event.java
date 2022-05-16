package com.fanzibang.community.pojo;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Event {

    private String exchange; // 事件处理的交换机
    private String routingKey; // 事件处理的绑定键
    private Integer type; // 通知类型 0-系统 1-评论 2-关注 3-点赞
    private Long fromId; // 事件触发者
    private Long toId; // 事件接收者
    private Map<String, Object> data = new HashMap<>(); // 存储未来可能需要用到的数据

    public Event setExchange(String exchange) {
        this.exchange = exchange;
        return this;
    }

    public Event setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }

    public Event setType(Integer type) {
        this.type = type;
        return this;
    }

    public Event setFromId(Long fromId) {
        this.fromId = fromId;
        return this;
    }

    public Event setToId(Long toId) {
        this.toId = toId;
        return this;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
