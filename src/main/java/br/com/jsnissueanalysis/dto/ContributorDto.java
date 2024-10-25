package br.com.jsnissueanalysis.dto;

public class ContributorDto {

    private String name;
    private String user;
    private int contributions;

    // Constructor
    public ContributorDto(String name, String user, int contributions) {
        this.name = name;
        this.user = user;
        this.contributions = contributions;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public int getContributions() {
        return contributions;
    }
    
    
}
