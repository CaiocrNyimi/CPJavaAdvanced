package br.com.fiap.bank_api.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Conta {

    SimpleDateFormat dataFormatada = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");

    private Long id;
    private int agencia;
    private String cpf;
    private String nome;
    private String aberturaConta;
    private double saldo;
    private boolean ativa;
    private String tipoConta;

    public Conta(Long id, int agencia, String cpf, String nome, String aberturaConta, double saldoInicial,
            boolean ativa, String tipoConta) {
        this.id = Math.abs(new Random().nextLong());
        this.agencia = agencia;
        this.cpf = cpf;
        this.nome = nome;
        this.aberturaConta = dataFormatada.format(new Date());
        this.saldo = saldoInicial;
        this.ativa = ativa;
        this.tipoConta = tipoConta;
    }

    public Long getId() {
        return id;
    }



    public int getAgencia() {
        return agencia;
    }



    public String getCpf() {
        return cpf;
    }



    public String getNome() {
        return nome;
    }



    public String getAberturaConta() {
        return aberturaConta;
    }

    public double setSaldo(double saldo) {
        return this.saldo = saldo;
    }
    
    public double getSaldo() {
        return saldo;
    }

    public boolean setAtiva(boolean ativa) {
        return this.ativa = ativa;
    }

    public boolean isAtiva() {
        return ativa;
    }



    public String getTipoConta() {
        return tipoConta;
    }

    public void validar() throws IllegalArgumentException {
        if (cpf == null || cpf.isEmpty()) {
            throw new IllegalArgumentException("O CPF não pode ser nulo ou vazio.");
        }
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser nulo ou vazio.");
        }
        if (saldo < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
        }
        if (!tipoConta.equals("corrente") && !tipoConta.equals("poupança") && !tipoConta.equals("salário")) {
            throw new IllegalArgumentException("O tipo de conta precisa ser corrente, poupança ou salário.");
        }
    }
}