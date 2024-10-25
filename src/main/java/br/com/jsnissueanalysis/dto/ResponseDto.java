package br.com.jsnissueanalysis.dto;

import java.util.List;


public class ResponseDto {

    UserDto user;
    List<ContributorDto> contributor;
    List<IssueDto> issues;

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

    
   

    
    
}
