package br.com.jsnissueanalysis.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.jsnissueanalysis.dto.ContributorDto;
import br.com.jsnissueanalysis.dto.IssueDto;
import br.com.jsnissueanalysis.dto.ResponseDto;
import br.com.jsnissueanalysis.dto.UserDto;
import br.com.jsnissueanalysis.service.ContextService;
import br.com.jsnissueanalysis.service.GitHubService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class IssueController {

    private GitHubService gitHubService;
    private ContextService contextService;

    public IssueController(GitHubService gitHubService){
        this.gitHubService = gitHubService;
    }


    @PostMapping
    public Mono<ResponseDto> get(){

    return gitHubService.processamentoRequest();

    }

    @PostMapping(value="/get-contributor")
    public Flux<ContributorDto> getContributors(){

    return gitHubService.getContributors();

    }



    @PostMapping(value="/get-issues")
    public Flux<IssueDto> gerIssues(){

    return gitHubService.getIssues();

    }



    @PostMapping(value="/get-user")
    public Mono<UserDto> gerUser(){

    return gitHubService.getUser();

    }
    
}
