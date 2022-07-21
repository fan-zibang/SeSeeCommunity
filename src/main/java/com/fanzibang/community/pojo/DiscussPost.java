package com.fanzibang.community.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "discusspost")
public class DiscussPost {

    @Id
    private Long id;
    @Field(type = FieldType.Long)
    private Long userId;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;
    private Byte type;
    private Byte status;
    private Long commentCount;
    private Double score;
    @Field(type = FieldType.Integer)
    private Integer topicId;
    @Field(type = FieldType.Long)
    private Long createTime;

}
