package br.com.jsnissueanalysis.dto;

import java.util.List;

public class IssueDto {
    private String title;
    private String author;
    private List<String> labels;

    // Constructor
    public IssueDto(String title, String author, List<String> labels) {
        this.title = title;
        this.author = author;
        this.labels = labels;
    }

    // Getters and setters (optional, depending on usage)
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getLabels() {
        return labels;
    }
}
