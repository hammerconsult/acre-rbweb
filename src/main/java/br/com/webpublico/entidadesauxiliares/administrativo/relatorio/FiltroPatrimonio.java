package br.com.webpublico.entidadesauxiliares.administrativo.relatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoReducaoValorBem;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wellington on 08/08/17.
 */
public class FiltroPatrimonio implements Serializable {

    private TipoBem tipoBem;
    private Date dataReferencia;
    private Date inicioVigencia;
    private Date fimVigencia;
    private PessoaFisica pessoaFisica;
    private HierarquiaOrganizacional hierarquiaAdm;
    private HierarquiaOrganizacional hierarquiaOrc;
    private String descricaoBem;
    private ObjetoCompra objetoCompra;
    private GrupoObjetoCompra grupoObjetoCompra;
    private GrupoBem grupoBem;
    private Date dataAquisicaoInicial;
    private Date dataAquisicaoFinal;
    private Integer mes;
    private Integer ano;
    private GrupoMaterial grupoMaterial;
    private Long idObjeto;
    private TipoReducaoValorBem tipoReducaoValorBem;
    private String filtros;

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public HierarquiaOrganizacional getHierarquiaAdm() {
        return hierarquiaAdm;
    }

    public void setHierarquiaAdm(HierarquiaOrganizacional hierarquiaAdm) {
        this.hierarquiaAdm = hierarquiaAdm;
    }

    public HierarquiaOrganizacional getHierarquiaOrc() {
        return hierarquiaOrc;
    }

    public void setHierarquiaOrc(HierarquiaOrganizacional hierarquiaOrc) {
        this.hierarquiaOrc = hierarquiaOrc;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public Date getDataAquisicaoInicial() {
        return dataAquisicaoInicial;
    }

    public void setDataAquisicaoInicial(Date dataAquisicaoInicial) {
        this.dataAquisicaoInicial = dataAquisicaoInicial;
    }

    public Date getDataAquisicaoFinal() {
        return dataAquisicaoFinal;
    }

    public void setDataAquisicaoFinal(Date dataAquisicaoFinal) {
        this.dataAquisicaoFinal = dataAquisicaoFinal;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public Long getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(Long idObjeto) {
        this.idObjeto = idObjeto;
    }

    public TipoReducaoValorBem getTipoReducaoValorBem() {
        return tipoReducaoValorBem;
    }

    public void setTipoReducaoValorBem(TipoReducaoValorBem tipoReducaoValorBem) {
        this.tipoReducaoValorBem = tipoReducaoValorBem;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }
}
