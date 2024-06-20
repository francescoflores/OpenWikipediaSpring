package com.open.openwikipedia.common.model.dto;

import java.util.List;
import com.open.openwikipedia.common.model.Paragraph;
import com.open.openwikipedia.common.model.Table;
import lombok.Data;

@Data
public class ArticleResponse {
    private String title;
    private String url;
    private Table infoTable;
    private List<Paragraph> paragraphs;

}
