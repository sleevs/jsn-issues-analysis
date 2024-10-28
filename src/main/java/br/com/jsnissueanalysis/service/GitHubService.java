package br.com.jsnissueanalysis.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.jsnissueanalysis.dto.ContributorDto;
import br.com.jsnissueanalysis.dto.IssueDto;
import br.com.jsnissueanalysis.dto.ResponseDto;
import br.com.jsnissueanalysis.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class GitHubService  {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private final String GITHUB_API = "https://api.github.com";
    private String github_token= "";
    private WebClient webClient;
    private final String webhookUrl = "https://webhook.site/your-webhook-url"; // Your actual webhook URL

    @Autowired
    private ContributorService contributorService;
    @Autowired
    private IssuesService issuesService;
    @Autowired
    private UserService userService;
   
    public GitHubService(WebClient.Builder webClientBuilder ){
        this.webClient = webClientBuilder.baseUrl(GITHUB_API).build();

    }

    public StringBuilder gernerateUri(UserDto userDto){
        String slash = "/";
        StringBuilder uriBase = new StringBuilder ("/repos/");
        uriBase.append(userDto.getName().concat(slash).concat(userDto.getRepositoryName()));
        return uriBase;
     
    }
    public Mono<ResponseDto> processamentoRequest2(UserDto userDto){
        
        
        return Mono.zip(
           
            userService.getUser(webClient,gernerateUri(userDto),github_token),
            contributorService.getContributors(webClient, gernerateUri(userDto), github_token).collectList(),
            issuesService.getIssues(webClient, gernerateUri(userDto), github_token).collectList()
          
    )
    .map(tuple -> {
        
        ResponseDto responseDto = new ResponseDto();
        responseDto.setUser(tuple.getT1());          
        responseDto.setContributor(tuple.getT2());   
        responseDto.setIssues(tuple.getT3());        
        return responseDto;
    })
    .doOnError(e -> System.out.println("Error occurred: " + e.getMessage())); 
}
 




 public Mono<ResponseDto> processamentoRequest(UserDto userDto) {
        return Mono.zip(
                userService.getUser(webClient, gernerateUri(userDto), github_token),
                contributorService.getContributors(webClient, gernerateUri(userDto), github_token).collectList(),
                issuesService.getIssues(webClient, gernerateUri(userDto), github_token).collectList()
        )
        .flatMap(tuple -> {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setUser(tuple.getT1());
            responseDto.setContributor(tuple.getT2());
            responseDto.setIssues(tuple.getT3());

            // Schedule sending to webhook after a delay of 1 day
            return Mono.<ResponseDto>create(sink -> {
                scheduler.schedule(() -> {
                    sendToWebhook(responseDto)
                            .subscribe(
                                unused -> sink.success(responseDto), // Pass responseDto on success
                                sink::error // Handle errors
                            );
                }, 10, TimeUnit.SECONDS);
            });
        })
        .subscribeOn(Schedulers.boundedElastic()) // Use a bounded elastic scheduler for blocking operations
        .doOnError(e -> System.out.println("Error occurred: " + e.getMessage()));
    }

    private Mono<Void> sendToWebhook(ResponseDto responseDto) {
        return webClient.post()
                .uri(webhookUrl)
                .bodyValue(responseDto) // Assuming ResponseDto can be serialized to JSON
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.out.println("Failed to send to webhook: " + e.getMessage()));
    }







}