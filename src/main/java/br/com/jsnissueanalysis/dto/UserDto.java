package br.com.jsnissueanalysis.dto;

public class UserDto {

    private String name;
    private String repositor;

    // Constructor
    public UserDto(){
        
    }
    public UserDto(String name, String repositor) {
        this.name = name;
        this.repositor = repositor;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getRepository() {
        return repositor;
    }
    
}
