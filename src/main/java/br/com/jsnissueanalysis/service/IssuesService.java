package br.com.jsnissueanalysis.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.jsnissueanalysis.dto.IssueDto;

import reactor.core.publisher.Flux;


@Service
public class IssuesService  {



public Flux<IssueDto> getIssues(WebClient webClient, StringBuilder uriBase, String repositoryToken) {
    return webClient.get()
            .uri(uriBase.append("/issues").toString())
            .headers(headers -> headers.setBearerAuth(repositoryToken))
            .retrieve()
            .bodyToFlux(Object.class)
            .doOnError(e -> System.out.println("Error occurred: " + e.getMessage()))
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



}