package br.com.jsnissueanalysis.service;

import br.com.jsnissueanalysis.dto.ContributorDto;

public class ContextService {

    private Strategy strategy;

    public ContextService(Strategy strategy){
        this.strategy = strategy;
    }

    public void operation(ContributorDto dto){
        strategy.algorithm(dto); 
    }
    
}
