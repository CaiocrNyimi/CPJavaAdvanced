package br.com.fiap.bank_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApresentacaoProjetoController {
    
    @GetMapping("/")
    public String apresentacao() {
        return "Bem-vindo ao projeto Bank API!\nEste projeto foi desenvolvido por Caio Cesar Rosa Nyimi.";
    }
}
