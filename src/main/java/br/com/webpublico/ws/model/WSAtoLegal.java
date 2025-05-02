package br.com.webpublico.ws.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Wellington on 18/08/2015.
 */
public class WSAtoLegal implements Serializable {

    private Long id;
    private String propositoAtoLegal;
    private String tipoAtoLegal;
    private String esferaDoGoverno;
    private String veiculoDePublicacao;
    private String numero;
    private Date dataPromulgacao;
    private Date dataPublicacao;
    private Integer exercicio;
    private String nome;
    private Date dataValidade;
    private Date dataEnvio;
    private Integer numeroPublicacao;
    private String fundamentacaoLegal;
    private String descricaoConformeDo;
    private Date dataEmissao;


    public WSAtoLegal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPropositoAtoLegal() {
        return propositoAtoLegal;
    }

    public void setPropositoAtoLegal(String propositoAtoLegal) {
        this.propositoAtoLegal = propositoAtoLegal;
    }

    public String getTipoAtoLegal() {
        return tipoAtoLegal;
    }

    public void setTipoAtoLegal(String tipoAtoLegal) {
        this.tipoAtoLegal = tipoAtoLegal;
    }

    public String getEsferaDoGoverno() {
        return esferaDoGoverno;
    }

    public void setEsferaDoGoverno(String esferaDoGoverno) {
        this.esferaDoGoverno = esferaDoGoverno;
    }

    public String getVeiculoDePublicacao() {
        return veiculoDePublicacao;
    }

    public void setVeiculoDePublicacao(String veiculoDePublicacao) {
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataPromulgacao() {
        return dataPromulgacao;
    }

    public void setDataPromulgacao(Date dataPromulgacao) {
        this.dataPromulgacao = dataPromulgacao;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public Integer getNumeroPublicacao() {
        return numeroPublicacao;
    }

    public void setNumeroPublicacao(Integer numeroPublicacao) {
        this.numeroPublicacao = numeroPublicacao;
    }

    public String getFundamentacaoLegal() {
        return fundamentacaoLegal;
    }

    public void setFundamentacaoLegal(String fundamentacaoLegal) {
        this.fundamentacaoLegal = fundamentacaoLegal;
    }

    public String getDescricaoConformeDo() {
        return descricaoConformeDo;
    }

    public void setDescricaoConformeDo(String descricaoConformeDo) {
        this.descricaoConformeDo = descricaoConformeDo;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }
}
