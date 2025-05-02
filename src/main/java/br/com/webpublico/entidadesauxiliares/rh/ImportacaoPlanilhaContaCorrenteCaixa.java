package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModalidadeConta;
import com.beust.jcommander.internal.Lists;

import java.util.List;

/**
 * @Author peixe on 31/03/2017  14:04.
 */
public class ImportacaoPlanilhaContaCorrenteCaixa {
    private String cpf;
    private String nome;
    private PessoaFisica pessoaFisica;
    private String agencia;
    private Agencia agenciaObjeto;
    private Banco banco;
    private String conta;
    private ModalidadeConta modalidadeConta;
    private List<VinculoFP> vinculos;
    private ContaCorrenteBancaria contaCorrenteBancaria;

    public ImportacaoPlanilhaContaCorrenteCaixa() {
        vinculos = Lists.newArrayList();
    }

    public ImportacaoPlanilhaContaCorrenteCaixa(String cpf, String nome, PessoaFisica pessoaFisica) {
        this.cpf = cpf;
        this.nome = nome;
        this.pessoaFisica = pessoaFisica;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public ModalidadeConta getModalidadeConta() {
        return modalidadeConta;
    }

    public void setModalidadeConta(ModalidadeConta modalidadeConta) {
        this.modalidadeConta = modalidadeConta;
    }

    public Agencia getAgenciaObjeto() {
        return agenciaObjeto;
    }

    public void setAgenciaObjeto(Agencia agenciaObjeto) {
        this.agenciaObjeto = agenciaObjeto;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public List<VinculoFP> getVinculos() {
        return vinculos;
    }

    public void setVinculos(List<VinculoFP> vinculos) {
        this.vinculos = vinculos;
    }

    public ContaCorrenteBancaria getContaCorrenteBancaria() {
        return contaCorrenteBancaria;
    }

    public void setContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        this.contaCorrenteBancaria = contaCorrenteBancaria;
    }
}
