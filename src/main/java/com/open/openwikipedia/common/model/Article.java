package com.open.openwikipedia.common.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class Article {

    @Id
    private String id;

    @Indexed(unique = true)
    private String title;

    private List<Paragraph> paragraphs;

    private Table infoTable;

    private String url;

    private Long counter;

    public Article(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
