package br.com.jsnissueanalysis.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.jsnissueanalysis.dto.ContributorDto;
import br.com.jsnissueanalysis.dto.ResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ContributorService  {

    public Flux<ContributorDto> getContributors(WebClient webClient, StringBuilder uriBase, String repositoryToken) {
        return webClient.get()
                .uri(uriBase.append("/contributors").toString())
                .headers(headers -> headers.setBearerAuth(repositoryToken))
                .retrieve()
                .bodyToFlux(Object.class)
                .doOnError(e -> System.out.println("Error occurred: " + e.getMessage()))
                .map(data -> {
                    // Cast data to Map to access fields
                    Map<String, Object> contributorData = (Map<String, Object>) data;

                    // Extract contributor details
                    String name = contributorData.get("login").toString();
                    String user = contributorData.get("html_url").toString();
                    int contributions = ((Number) contributorData.get("contributions")).intValue(); // Handle potential casting issues

                    return new ContributorDto(name, user, contributions);
                });
    }

   

}
