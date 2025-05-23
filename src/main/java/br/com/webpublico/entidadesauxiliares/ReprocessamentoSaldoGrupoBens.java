package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Edi on 24/04/2015.
 */
public class ReprocessamentoSaldoGrupoBens {
    private Date dataMovimento;
    private BigDecimal valor;
    private GrupoBem grupoBem;
    private GrupoMaterial grupoMaterial;
    private UnidadeOrganizacional unidadeOrganizacional;
    private TipoGrupo tipoGrupo;
    private TipoEstoque tipoEstoque;
    private TipoOperacaoBensMoveis tipoOperacaoBensMoveis;
    private TipoOperacaoBensImoveis tipoOperacaoBensImoveis;
    private TipoOperacaoBensIntangiveis tipoOperacaoBensIntangiveis;
    private TipoOperacaoBensEstoque tipoOperacaoBensEstoque;
    private TipoLancamento tipoLancamento;
    private boolean receitaRealizada;
    private Long idOrigem;

    public ReprocessamentoSaldoGrupoBens() {
        receitaRealizada = false;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public TipoOperacaoBensMoveis getTipoOperacaoBensMoveis() {
        return tipoOperacaoBensMoveis;
    }

    public void setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis tipoOperacaoBensMoveis) {
        this.tipoOperacaoBensMoveis = tipoOperacaoBensMoveis;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public TipoOperacaoBensImoveis getTipoOperacaoBensImoveis() {
        return tipoOperacaoBensImoveis;
    }

    public void setTipoOperacaoBensImoveis(TipoOperacaoBensImoveis tipoOperacaoBensImoveis) {
        this.tipoOperacaoBensImoveis = tipoOperacaoBensImoveis;
    }

    public TipoOperacaoBensIntangiveis getTipoOperacaoBensIntangiveis() {
        return tipoOperacaoBensIntangiveis;
    }

    public void setTipoOperacaoBensIntangiveis(TipoOperacaoBensIntangiveis tipoOperacaoBensIntangiveis) {
        this.tipoOperacaoBensIntangiveis = tipoOperacaoBensIntangiveis;
    }

    public TipoOperacaoBensEstoque getTipoOperacaoBensEstoque() {
        return tipoOperacaoBensEstoque;
    }

    public void setTipoOperacaoBensEstoque(TipoOperacaoBensEstoque tipoOperacaoBensEstoque) {
        this.tipoOperacaoBensEstoque = tipoOperacaoBensEstoque;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public boolean isReceitaRealizada() {
        return receitaRealizada;
    }

    public void setReceitaRealizada(boolean receitaRealizada) {
        this.receitaRealizada = receitaRealizada;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }
}
