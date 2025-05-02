package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoProcesso;
import br.com.webpublico.enums.TipoProcessoTramite;

import java.util.Date;

public class VoProcesso {

    private Long id;
    private Integer numero;
    private Integer ano;
    private Date dataRegistro;
    private Date dataUltimoTramite;
    private String nomeAutorRequerente;
    private String assunto;
    private SituacaoProcesso situacao;
    private String nomeAssuntoProcesso;
    private String pessoa;
    private TipoProcessoTramite tipoProcessoTramite;
    private String unidadeAtual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getDataUltimoTramite() {
        return dataUltimoTramite;
    }

    public void setDataUltimoTramite(Date dataUltimoTramite) {
        this.dataUltimoTramite = dataUltimoTramite;
    }

    public String getNomeAutorRequerente() {
        return nomeAutorRequerente;
    }

    public void setNomeAutorRequerente(String nomeAutorRequerente) {
        this.nomeAutorRequerente = nomeAutorRequerente;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public SituacaoProcesso getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcesso situacao) {
        this.situacao = situacao;
    }

    public String getNomeAssuntoProcesso() {
        return nomeAssuntoProcesso;
    }

    public void setNomeAssuntoProcesso(String nomeAssuntoProcesso) {
        this.nomeAssuntoProcesso = nomeAssuntoProcesso;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public TipoProcessoTramite getTipoProcessoTramite() {
        return tipoProcessoTramite;
    }

    public void setTipoProcessoTramite(TipoProcessoTramite tipoProcessoTramite) {
        this.tipoProcessoTramite = tipoProcessoTramite;
    }

    public String getUnidadeAtual() {
        return unidadeAtual;
    }

    public void setUnidadeAtual(String unidadeAtual) {
        this.unidadeAtual = unidadeAtual;
    }
}
