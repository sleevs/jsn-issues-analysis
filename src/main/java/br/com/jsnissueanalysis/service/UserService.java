package br.com.jsnissueanalysis.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import br.com.jsnissueanalysis.dto.UserDto;
import reactor.core.publisher.Mono;

@Service
public class UserService {


    public Mono<UserDto> getUser(WebClient webClient, StringBuilder uriBase, String repositoryToken) {
        return webClient.get()
                .uri(uriBase.toString()) // Make sure this is the correct endpoint for user details
                .headers(headers -> headers.setBearerAuth(repositoryToken))
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
