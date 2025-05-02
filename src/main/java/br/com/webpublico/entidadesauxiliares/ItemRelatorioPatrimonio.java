package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mateus on 29/05/18.
 */
public class ItemRelatorioPatrimonio {
    private BigDecimal codigo;
    private Date data;
    private Date dataLancamento;
    private String usuario;
    private String administrativa;
    private String orcamentaria;
    private String grupo;
    private BigDecimal valorResidual;
    private BigDecimal vidaUtilEmAnos;
    private String bem;
    private BigDecimal valorBem;
    private BigDecimal valorReducao;
    private String situacao;
    private String tipoLancamento;

    public ItemRelatorioPatrimonio() {
    }

    public BigDecimal getCodigo() {
        return codigo;
    }

    public void setCodigo(BigDecimal codigo) {
        this.codigo = codigo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public BigDecimal getValorResidual() {
        return valorResidual;
    }

    public void setValorResidual(BigDecimal valorResidual) {
        this.valorResidual = valorResidual;
    }

    public BigDecimal getVidaUtilEmAnos() {
        return vidaUtilEmAnos;
    }

    public void setVidaUtilEmAnos(BigDecimal vidaUtilEmAnos) {
        this.vidaUtilEmAnos = vidaUtilEmAnos;
    }

    public String getBem() {
        return bem;
    }

    public void setBem(String bem) {
        this.bem = bem;
    }

    public BigDecimal getValorBem() {
        return valorBem;
    }

    public void setValorBem(BigDecimal valorBem) {
        this.valorBem = valorBem;
    }

    public BigDecimal getValorReducao() {
        return valorReducao;
    }

    public void setValorReducao(BigDecimal valorReducao) {
        this.valorReducao = valorReducao;
    }


    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }
}
