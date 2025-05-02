package br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil;


import br.com.webpublico.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
public class SaldoGrupoMaterialDTO {
    private Long id;
    private LocalDate data;
    private BigDecimal valor;
    private Long idUnidadeOrganizacional;
    private TipoEstoque tipoEstoque;
    private NaturezaTipoGrupoMaterial naturezaTipoGrupoMaterial;
    private TipoOperacaoBensEstoque tipoOperacaoBensEstoque;
    private TipoLancamento tipoLancamento;
    private TipoOperacao tipoOperacao;
    private Boolean validarSaldo;
    private Long idGrupoMaterial;
    private Long idOrigem;

    public SaldoGrupoMaterialDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getIdUnidadeOrganizacional() {
        return idUnidadeOrganizacional;
    }

    public void setIdUnidadeOrganizacional(Long idUnidadeOrganizacional) {
        this.idUnidadeOrganizacional = idUnidadeOrganizacional;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public NaturezaTipoGrupoMaterial getNaturezaTipoGrupoMaterial() {
        return naturezaTipoGrupoMaterial;
    }

    public void setNaturezaTipoGrupoMaterial(NaturezaTipoGrupoMaterial naturezaTipoGrupoMaterial) {
        this.naturezaTipoGrupoMaterial = naturezaTipoGrupoMaterial;
    }

    public TipoOperacaoBensEstoque getTipoOperacaoBensEstoque() {
        return tipoOperacaoBensEstoque;
    }

    public void setTipoOperacaoBensEstoque(TipoOperacaoBensEstoque tipoOperacaoBensEstoque) {
        this.tipoOperacaoBensEstoque = tipoOperacaoBensEstoque;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public Boolean getValidarSaldo() {
        return validarSaldo;
    }

    public void setValidarSaldo(Boolean validarSaldo) {
        this.validarSaldo = validarSaldo;
    }

    public Long getIdGrupoMaterial() {
        return idGrupoMaterial;
    }

    public void setIdGrupoMaterial(Long idGrupoMaterial) {
        this.idGrupoMaterial = idGrupoMaterial;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }
}

