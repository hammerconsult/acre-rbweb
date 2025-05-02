package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.TipoAquisicaoBem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VOVerificacaoBemMovel implements Serializable {

    private String codigoVerificacao;
    private Date inicioVerificao;
    private Date dataConclusao;
    private String descricao;
    private String situacao;

    private String registrobem;
    private String registroAnterior;
    private String descricaoBem;
    private String grupoBem;
    private String localizacao;
    private String administrativa;
    private String orcamentaria;
    private String marca;
    private String modelo;
    private String estadoConservacao;
    private String tipoAquisicao;
    private Date dataAquisicao;
    private BigDecimal valorOriginal;

    public VOVerificacaoBemMovel(Object[] object) {
        preencher(object);
    }

    public void preencher(Object[] obj) {

        this.setDataConclusao((Date) obj[0]);
        this.setRegistrobem((String) obj[1]);
        this.setRegistroAnterior((String) obj[2]);
        this.setDescricaoBem((String) obj[3]);
        this.setGrupoBem((String) obj[4]);
        this.setLocalizacao((String) obj[5]);
        this.setAdministrativa((String) obj[6]);
        this.setOrcamentaria((String) obj[7]);
        this.setMarca((String) obj[8]);
        this.setModelo((String) obj[9]);
        this.setEstadoConservacao(EstadoConservacaoBem.valueOf((String) obj[10]).getDescricao());
        this.setTipoAquisicao(TipoAquisicaoBem.valueOf((String) obj[11]).getDescricao());
        this.setDataAquisicao((Date) obj[12]);
        this.setValorOriginal((BigDecimal) obj[13]);
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public Date getInicioVerificao() {
        return inicioVerificao;
    }

    public void setInicioVerificao(Date inicioVerificao) {
        this.inicioVerificao = inicioVerificao;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getRegistrobem() {
        return registrobem;
    }

    public void setRegistrobem(String registrobem) {
        this.registrobem = registrobem;
    }

    public String getRegistroAnterior() {
        return registroAnterior;
    }

    public void setRegistroAnterior(String registroAnterior) {
        this.registroAnterior = registroAnterior;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public String getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(String grupoBem) {
        this.grupoBem = grupoBem;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getAdministrativa() {
        return administrativa;
    }

    public void setAdministrativa(String administrativa) {
        this.administrativa = administrativa;
    }

    public String getOrcamentaria() {
        return orcamentaria;
    }

    public void setOrcamentaria(String orcamentaria) {
        this.orcamentaria = orcamentaria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getEstadoConservacao() {
        return estadoConservacao;
    }

    public void setEstadoConservacao(String estadoConservacao) {
        this.estadoConservacao = estadoConservacao;
    }

    public String getTipoAquisicao() {
        return tipoAquisicao;
    }

    public void setTipoAquisicao(String tipoAquisicao) {
        this.tipoAquisicao = tipoAquisicao;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }
}
