package br.com.jsnissueanalysis.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.jsnissueanalysis.dto.ContributorDto;
import br.com.jsnissueanalysis.dto.IssueDto;
import br.com.jsnissueanalysis.dto.ResponseDto;
import br.com.jsnissueanalysis.dto.UserDto;
import br.com.jsnissueanalysis.service.ContextService;
import br.com.jsnissueanalysis.service.GitHubService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    @CircuitBreaker( name ="requestIssues" ,fallbackMethod="fallbackMethod")
    public Mono<ResponseDto> requestIssues(){

    return gitHubService.processamentoRequest();

    }

    public String fallbackMethod(){
        return "Ocorreu um erro a realizar a solicitação";
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
