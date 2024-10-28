package br.com.jsnissueanalysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestDto {
    
    @JsonProperty("usernam")
    private String username;
    @JsonProperty("repository")
    private String repository;
    @JsonProperty("webhook url")
    private String webhookUrl;
    @JsonProperty("repo authorization token")
    private String token;

    public RequestDto(){

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

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
}
