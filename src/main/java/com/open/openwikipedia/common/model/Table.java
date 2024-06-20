package com.open.openwikipedia.common.model;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Table {
    private String caption;
    private Map<String, List<Object>> rows;

    public Table(Map<String, List<Object>> rows) {
        this.rows = rows;
    }

    public Table(String caption, Map<String, List<Object>> rows) {
        this.caption = caption;
        this.rows = rows;
    }
}
