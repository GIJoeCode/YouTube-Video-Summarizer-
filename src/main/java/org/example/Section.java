package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Section {
    private final String header;
    private final String paragraph;
    private final List<String> descriptions;

    public Section(String header, String paragraph, List<String> descriptions) {
        this.header = header;
        this.paragraph = paragraph;
        this.descriptions = descriptions == null ? Collections.emptyList() : new ArrayList<>(descriptions);
    }

    public String getHeader() {
        return header;
    }

    public String getParagraph() {
        return paragraph;
    }

    public List<String> getDescriptions() {
        return Collections.unmodifiableList(descriptions);
    }
}
