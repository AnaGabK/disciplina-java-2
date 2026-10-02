package br.edu.utfpr;

import br.edu.utfpr.model.FormaPagamento;
import br.edu.utfpr.model.Status;
import br.edu.utfpr.model.Venda;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VendaCsv {

    @CsvBindByName(column = "num_venda")
    private String numero;
    @CsvCustomBindByName(column = "data_venda", converter = LocalDateConverter.class)
    private LocalDate dataVenda;
    @CsvCustomBindByName(column = "data_entrega", converter = LocalDateConverter.class)
    private LocalDate dataEntrega;
    @CsvBindByName(column = "regiao")
    private String regiao;
    @CsvBindByName(column = "uf")
    private String estado;
    @CsvBindByName(column = "gestor")
    private String gerente;
    @CsvBindByName(column = "vendedor")
    private String vendedor;
    @CsvBindByName(column = "departamento")
    private String departamento;
    @CsvCustomBindByName(column = "forma_pagamento", converter = FormaPagamentoConverter.class)
    private FormaPagamento formaPagamento;
    @CsvBindByName(column = "valor_venda")
    private BigDecimal valor;
    @CsvCustomBindByName(column = "status", converter = StatusConverter.class)
    private Status status;

    public Venda toVenda() {
        return new Venda(numero, dataVenda, dataEntrega, regiao, estado, gerente,
                vendedor, departamento, formaPagamento, valor, status);
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getGerente() {
        return gerente;
    }

    public void setGerente(String gerente) {
        this.gerente = gerente;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
