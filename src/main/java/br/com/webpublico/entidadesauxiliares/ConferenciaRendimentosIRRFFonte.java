package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Buzatto on 26/11/2015.
 */
public class ConferenciaRendimentosIRRFFonte implements Serializable {

    private Integer ano;
    private String matricula;
    private Integer contrato;
    private String cpf;
    private String nome;
    private String modalidade;
    private String situacaoFuncional;
    private List<ItemConferenciaIRRFFonte> itens;

    public ConferenciaRendimentosIRRFFonte() {
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Integer getContrato() {
        return contrato;
    }

    public void setContrato(Integer contrato) {
        this.contrato = contrato;
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

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getSituacaoFuncional() {
        return situacaoFuncional;
    }

    public void setSituacaoFuncional(String situacaoFuncional) {
        this.situacaoFuncional = situacaoFuncional;
    }

    public List<ItemConferenciaIRRFFonte> getItens() {
        return itens;
    }

    public void setItens(List<ItemConferenciaIRRFFonte> itens) {
        this.itens = itens;
    }
}
