package br.com.jsnissueanalysis.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class ResponseDto {

    private UserDto user;
    private List<ContributorDto> contributor;
    private List<IssueDto> issues;
    @JsonIgnore
    private String erro ;

    public ResponseDto(){}

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<ContributorDto> getContributor() {
        return contributor;
    }

    public void setContributor(List<ContributorDto> contributor) {
        this.contributor = contributor;
    }

    public List<IssueDto> getIssues() {
        return issues;
    }

    public void setIssues(List<IssueDto> issues) {
        this.issues = issues;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    
   

    
    
}
