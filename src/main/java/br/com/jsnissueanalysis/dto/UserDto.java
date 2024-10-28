package br.com.jsnissueanalysis.dto;

public class UserDto {

    private String name;
    private String repositoryName;

    // Constructor
    public UserDto(){
        
    }
    public UserDto(String name, String repositoryName) {
        this.name = name;
        this.repositoryName = repositoryName;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getRepositoryName() {
        return repositoryName;
    }
    
}
