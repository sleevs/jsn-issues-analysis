package br.com.jsnissueanalysis.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.jsnissueanalysis.dto.ContributorDto;
import br.com.jsnissueanalysis.dto.IssueDto;
import br.com.jsnissueanalysis.dto.ResponseDto;
import br.com.jsnissueanalysis.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GitHubService implements Strategy {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String GITHUB_API = "https://api.github.com";
    private String github_token= "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";
    private WebClient webClient;

   
    public GitHubService(WebClient.Builder webClientBuilder ){
        this.webClient = webClientBuilder.baseUrl(GITHUB_API).build();

    }
    
    @Override
    public void algorithm(ContributorDto dto) {
       
        restTemplate.getForObject(GITHUB_API, null);
    }
    

    public Mono<ResponseDto> processamentoRequest(){
        return Mono.zip(
            getUser(),                  // Retrieve user details
            getContributors().collectList(), // Collect all contributors into a List
            getIssues().collectList()    // Collect all issues into a List
    )
    .map(tuple -> {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setUser(tuple.getT1());           // First element is UserDto
        responseDto.setContributor(tuple.getT2());   // Second element is List<ContributorDto>
        responseDto.setIssues(tuple.getT3());        // Third element is List<IssueDto>
        return responseDto;
    })
    .doOnError(e -> System.out.println("Error occurred: " + e.getMessage())); // Log any errors
}
 
public Flux<ContributorDto> getContributors() {
    return webClient.get()
            .uri("/repos/sleevs/ui-cad/contributors")
            .headers(headers -> headers.setBearerAuth(github_token))
            .retrieve()
            .bodyToFlux(Object.class)
            .doOnError(e -> System.out.println("Error occurred: " + e.getMessage())) // Log any errors
            .map(data -> {
                // Cast data to Map to access fields
                Map<String, Object> contributorData = (Map<String, Object>) data;
                
                // Extract contributor details
                String name = contributorData.get("login").toString(); // Get the login name
                String user = contributorData.get("html_url").toString(); // Get the URL of the user
                int contributions = (int) contributorData.get("contributions"); // Get the number of contributions

                return new ContributorDto(name, user, contributions);
            });
}


public Flux<IssueDto> getIssues() {
    return webClient.get()
            .uri("/repos/sleevs/ui-cad/issues")
            .headers(headers -> headers.setBearerAuth(github_token))
            .retrieve()
            .bodyToFlux(Object.class)
            .doOnError(e -> System.out.println("Error occurred: " + e.getMessage())) // Log any errors
            .map(data -> {
                
                Map<String, Object> issueData = (Map<String, Object>) data;
                
                String title = (String) issueData.get("title");
                String author = ((Map<String, Object>) issueData.get("user")).get("login").toString();
                List<String> labels = ((List<Map<String, Object>>) issueData.get("labels")).stream()
                        .map(label -> label.get("name").toString())
                        .collect(Collectors.toList());

                return new IssueDto(title, author, labels);
            });
}


    public Mono<UserDto> getUser() {
        return webClient.get()
                .uri("/repos/sleevs/ui-cad") // Make sure this is the correct endpoint for user details
                .headers(headers -> headers.setBearerAuth(github_token))
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(e -> System.out.println("Error occurred: " + e.getMessage())) // Log any errors
                .map(data -> {
                    // Cast data to Map to access fields
                    Map<String, Object> repoData = (Map<String, Object>) data;

                    String name = ((Map<String, Object>) repoData.get("owner")).get("login").toString();
                    String repositoryName = repoData.get("name").toString();

                    return new UserDto(name, repositoryName);
                });
    }





}