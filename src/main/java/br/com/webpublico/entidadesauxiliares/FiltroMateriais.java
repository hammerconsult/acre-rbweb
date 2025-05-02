package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.ConsolidadoDetalhado;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;

import java.util.Date;

/**
 * Created by mga on 14/02/2018.
 */
public class FiltroMateriais {

    private ApresentacaoRelatorio apresentacaoRelatorio;
    private ConsolidadoDetalhado tipoRelatorio;
    private TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional;
    private TipoEstoque tipoEstoque;
    private LocalEstoque localEstoque;
    private Material material;
    private GrupoMaterial grupoMaterial;
    private GrupoObjetoCompra grupoObjetoCompra;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private Date dataInicial;
    private Date dataFinal;
    private Date dataReferencia;
    private Licitacao licitacao;
    private String clausulas;
    private String filtros;

    public FiltroMateriais() {
        inicializarFiltros();
    }

    public void limparFiltros() {
        inicializarFiltros();
    }

    private void inicializarFiltros() {
        apresentacaoRelatorio = ApresentacaoRelatorio.CONSOLIDADO;
        tipoRelatorio = ConsolidadoDetalhado.CONSOLIDADO;
        tipoHierarquiaOrganizacional = TipoHierarquiaOrganizacional.ADMINISTRATIVA;
        dataInicial = new Date();
        dataFinal = new Date();
        dataReferencia = new Date();
        hierarquiaAdministrativa = null;
        hierarquiaOrcamentaria = null;
        grupoMaterial = null;
        localEstoque = null;
        tipoEstoque = null;
        licitacao = null;
        clausulas = "";
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public ConsolidadoDetalhado getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(ConsolidadoDetalhado tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public ApresentacaoRelatorio getApresentacaoRelatorio() {
        return apresentacaoRelatorio;
    }

    public void setApresentacaoRelatorio(ApresentacaoRelatorio apresentacaoRelatorio) {
        this.apresentacaoRelatorio = apresentacaoRelatorio;
    }

    public TipoHierarquiaOrganizacional getTipoHierarquiaOrganizacional() {
        return tipoHierarquiaOrganizacional;
    }

    public void setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        this.tipoHierarquiaOrganizacional = tipoHierarquiaOrganizacional;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public boolean isTipoHierarquiaOrcamentaria() {
        return this.tipoHierarquiaOrganizacional != null && TipoHierarquiaOrganizacional.ORCAMENTARIA.equals(this.tipoHierarquiaOrganizacional);
    }

    public boolean isTipoHierarquiaAdministrativa() {
        return this.tipoHierarquiaOrganizacional != null && TipoHierarquiaOrganizacional.ADMINISTRATIVA.equals(this.tipoHierarquiaOrganizacional);
    }

    public String getClausulas() {
        return clausulas;
    }

    public void setClausulas(String clausulas) {
        this.clausulas = clausulas;
    }
}
