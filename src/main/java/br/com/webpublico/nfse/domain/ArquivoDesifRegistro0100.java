package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ArquivoDesifRegistro0100 extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Long linha;
    @ManyToOne
    private ArquivoDesif arquivoDesif;
    private String conta;
    private String desdobramento;
    private String nome;
    private String descricao;
    private String contaSuperior;
    @ManyToOne
    private Cosif cosif;
    @ManyToOne
    private CodigoTributacao codigoTributacao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLinha() {
        return linha;
    }

    public void setLinha(Long linha) {
        this.linha = linha;
    }

    public ArquivoDesif getArquivoDesif() {
        return arquivoDesif;
    }

    public void setArquivoDesif(ArquivoDesif arquivoDesif) {
        this.arquivoDesif = arquivoDesif;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(String desdobramento) {
        this.desdobramento = desdobramento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getContaSuperior() {
        return contaSuperior;
    }

    public void setContaSuperior(String contaSuperior) {
        this.contaSuperior = contaSuperior;
    }

    public Cosif getCosif() {
        return cosif;
    }

    public void setCosif(Cosif cosif) {
        this.cosif = cosif;
    }

    public CodigoTributacao getCodigoTributacao() {
        return codigoTributacao;
    }

    public void setCodigoTributacao(CodigoTributacao codigoTributacao) {
        this.codigoTributacao = codigoTributacao;
    }
}
