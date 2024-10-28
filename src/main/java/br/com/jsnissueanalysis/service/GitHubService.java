package br.com.jsnissueanalysis.service;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.jsnissueanalysis.dto.RequestDto;
import br.com.jsnissueanalysis.dto.ResponseDto;
import br.com.jsnissueanalysis.dto.UserDto;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class GitHubService  {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private final String GITHUB_API = "https://api.github.com";
    private WebClient webClient;
    private String token ;
    private String webhookUrl;
    //private String webhookUrl = "https://webhook.site/your-webhook-url"; 

    @Autowired
    private ContributorService contributorService;
    @Autowired
    private IssuesService issuesService;
    @Autowired
    private UserService userService;
   
    public GitHubService(WebClient.Builder webClientBuilder  ){
        this.webClient = webClientBuilder.baseUrl(GITHUB_API).build();
   

    }

    public StringBuilder gernerateUri(RequestDto request){
        this.token = request.getToken();
        this.webhookUrl = request.getWebhookUrl();

        String slash = "/";
        StringBuilder uriBase = new StringBuilder ("/repos/");
        uriBase.append(request.getUsername().concat(slash).concat(request.getRepository()));
        return uriBase;
     
    }
    public Mono<ResponseDto> sendNow(RequestDto request){
        
        
        return Mono.zip(
           
            userService.getUser(webClient,gernerateUri(request),token),
            contributorService.getContributors(webClient, gernerateUri(request), token).collectList(),
            issuesService.getIssues(webClient, gernerateUri(request), token).collectList()
          
    )
    .map(tuple -> {
        
        ResponseDto responseDto = new ResponseDto();
        responseDto.setUser(tuple.getT1());          
        responseDto.setContributor(tuple.getT2());   
        responseDto.setIssues(tuple.getT3());        
        return responseDto;
    })
    .doOnError(e -> System.out.println("Erro ocorreu em : " + e.getMessage())); 
}
 




 public Mono<ResponseDto> sendAfter(RequestDto request) {
        return Mono.zip(
                userService.getUser(webClient, gernerateUri(request), token),
                contributorService.getContributors(webClient, gernerateUri(request), token).collectList(),
                issuesService.getIssues(webClient, gernerateUri(request), token).collectList()
        )
        .flatMap(tuple -> {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setUser(tuple.getT1());
            responseDto.setContributor(tuple.getT2());
            responseDto.setIssues(tuple.getT3());

            // Schedule envia para o  webhook depois de  um dia 1
            return Mono.<ResponseDto>create(sink -> {
                scheduler.schedule(() -> {
                    sendToWebhook(responseDto)
                            .subscribe(
                                unused -> sink.success(responseDto), 
                                sink::error // trata o erro
                            );
                }, 10, TimeUnit.SECONDS);
            });
        })
        .subscribeOn(Schedulers.boundedElastic()) // usa  bounded elastic schdeule para bloquear  a operações
        .doOnError(e -> System.out.println("Erro ocorreu em: " + e.getMessage()));
    }

    private Mono<Void> sendToWebhook(ResponseDto responseDto) {
        return webClient.post()
                .uri(webhookUrl)
                .bodyValue(responseDto)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.out.println("Falha ao tentar enviar para o webhook: " + e.getMessage()));
    }







}