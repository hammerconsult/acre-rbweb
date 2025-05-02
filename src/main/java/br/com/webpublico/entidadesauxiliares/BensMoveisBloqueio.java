package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.DataUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class BensMoveisBloqueio implements Serializable,Comparable<BensMoveisBloqueio> {

    private Long idBem;
    private String codigoAdministrativa;
    private String unidadeAdministrativa;
    private String codigoOrcamentaria;
    private String unidadeOrcamentaria;
    private String registroPatrimonial;
    private String descricao;
    private String bloqueadoPor;
    private String situacao;
    private Date dataBloqueio;
    private String movimentoTipoUm;
    private String movimentoTipoDois;
    private String movimentoTipoTres;

    public BensMoveisBloqueio() {
    }

    public String getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(String unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public String getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(String unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public String getRegistroPatrimonial() {
        return registroPatrimonial;
    }

    public void setRegistroPatrimonial(String registroPatrimonial) {
        this.registroPatrimonial = registroPatrimonial;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getBloqueadoPor() {
        return bloqueadoPor;
    }

    public void setBloqueadoPor(String bloqueadoPor) {
        this.bloqueadoPor = bloqueadoPor;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getCodigoAdministrativa() {
        return codigoAdministrativa;
    }

    public void setCodigoAdministrativa(String codigoAdministrativa) {
        this.codigoAdministrativa = codigoAdministrativa;
    }

    public String getCodigoOrcamentaria() {
        return codigoOrcamentaria;
    }

    public void setCodigoOrcamentaria(String codigoOrcamentaria) {
        this.codigoOrcamentaria = codigoOrcamentaria;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public String getMovimentoTipoUm() {
        return movimentoTipoUm;
    }

    public void setMovimentoTipoUm(String movimentoTipoUm) {
        this.movimentoTipoUm = movimentoTipoUm;
    }

    public String getMovimentoTipoDois() {
        return movimentoTipoDois;
    }

    public void setMovimentoTipoDois(String movimentoTipoDois) {
        this.movimentoTipoDois = movimentoTipoDois;
    }

    public String getMovimentoTipoTres() {
        return movimentoTipoTres;
    }

    public void setMovimentoTipoTres(String movimentoTipoTres) {
        this.movimentoTipoTres = movimentoTipoTres;
    }

    public Long getIdBem() {
        return idBem;
    }

    public void setIdBem(Long idBem) {
        this.idBem = idBem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BensMoveisBloqueio that = (BensMoveisBloqueio) o;
        return Objects.equals(idBem, that.idBem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBem);
    }

    @Override
    public int compareTo(BensMoveisBloqueio o) {
        return Long.valueOf(getRegistroPatrimonial()).compareTo(Long.valueOf(o.getRegistroPatrimonial()));
    }

    @Override
    public String toString() {
        String descricao;
        descricao = "O bem: " + getRegistroPatrimonial()
            + " foi movimentado no(a) " + getBloqueadoPor()
            + " em: " + DataUtil.getDataFormatada(getDataBloqueio())
            + " e encontra-se na situação de " + getSituacao() + ".";
        return descricao;
    }
}
