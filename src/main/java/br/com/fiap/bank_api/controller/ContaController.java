package br.com.fiap.bank_api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.fiap.bank_api.model.Conta;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private Logger log = LoggerFactory.getLogger(getClass());
    private List<Conta> contas = new ArrayList<>();

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Conta conta) {
        try {
            conta.validar();
            log.info("Cadastrando conta de " + conta.getNome());
            contas.add(conta);
            return ResponseEntity.status(201).body(conta);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao cadastrar conta: " + e.getMessage());
            return handleException(e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Conta>> getAll() {
        log.info("Listando todas as contas cadastradas");
        return ResponseEntity.ok(contas);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        log.info("Buscando conta de id " + id);
        try {
            Conta conta = getConta(id);
            return ResponseEntity.ok(conta);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<?> getByCpf(@PathVariable String cpf) {
        log.info("Buscando conta com CPF: {}", cpf);
        try {
            Conta conta = contas.stream()
                    .filter(c -> c.getCpf().equals(cpf))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Conta com CPF: " + cpf + " não encontrada."));
            return ResponseEntity.ok(conta);
        } catch (ResponseStatusException e) {
            log.error("Erro ao buscar conta por CPF: {}", e.getMessage());
            return handleException(e);
        }
    }

    @PutMapping("/encerrar/{id}")
    public ResponseEntity<?> encerrar(@PathVariable Long id) {
        try {
            Conta conta = getConta(id);
            conta.setAtiva(false);
            log.info("Conta de id " + id + " encerrada");
            return ResponseEntity.ok(conta);
        } catch (ResponseStatusException e) {
            log.error("Erro ao encerrar conta: " + e.getMessage());
            return handleException(e);
        }
    }

    @PostMapping("/deposito")
    public ResponseEntity<?> deposito(@RequestBody Map<String, Object> request) {
        try {
            Long id = Long.valueOf(request.get("id").toString());
            double valor = Double.valueOf(request.get("valor").toString());
            Conta conta = getConta(id);

            validarValor(valor);
            conta.setSaldo(conta.getSaldo() + valor);
            return ResponseEntity.ok(conta);

        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/saque")
    public ResponseEntity<?> saque(@RequestBody Map<String, Object> request) {
        try {
            Long id = Long.valueOf(request.get("id").toString());
            double valor = Double.valueOf(request.get("valor").toString());
            Conta conta = getConta(id);

            validarValor(valor);
            verificarSaldo(conta, valor);
            conta.setSaldo(conta.getSaldo() - valor);
            return ResponseEntity.ok(conta);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/pix")
    public ResponseEntity<?> pix(@RequestBody Map<String, Object> request) {
        try {
            Long idOrigem = Long.valueOf(request.get("idOrigem").toString());
            Long idDestino = Long.valueOf(request.get("idDestino").toString());
            double valor = Double.valueOf(request.get("valor").toString());
            Conta origem = getConta(idOrigem);
            Conta destino = getConta(idDestino);

            validarValor(valor);
            verificarSaldo(origem, valor);
            origem.setSaldo(origem.getSaldo() - valor);
            destino.setSaldo(destino.getSaldo() + valor);

            return ResponseEntity.ok(origem);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private Conta getConta(Long id) {
        return contas.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
    }

    private void validarValor(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
    }

    private void verificarSaldo(Conta conta, double valor) {
        if (conta.getSaldo() < valor) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
    }

    private ResponseEntity<?> handleException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}