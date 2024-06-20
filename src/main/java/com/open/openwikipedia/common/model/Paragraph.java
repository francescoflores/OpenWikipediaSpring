package com.open.openwikipedia.common.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Paragraph {
    private String title;
    private String text;
    private List<String> images;
    private List<Table> tables;
    private int level;

    public Paragraph(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Paragraph(String title, String text, List<String> images, List<Table> tables, int level) {
        this.title = title;
        this.text = text;
        this.images = images;
        this.tables = tables;
        this.level = level;
    }
}
