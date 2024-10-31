package br.com.jsnissueanalysis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestNowDto extends RequestDto{


    @JsonProperty("usernam")
    private String username;
    @JsonProperty("repository")
    private String repository;
    @JsonProperty("repo authorization token")
    private String token;
    @JsonIgnore
    private String webhookUrl;

    public RequestNowDto(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    
    
}
