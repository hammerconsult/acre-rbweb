package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.BensEstoque;
import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;
import br.com.webpublico.interfaces.AgrupadorLevantamentoEstoque;
import br.com.webpublico.util.Util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Desenvolvimento on 02/02/2017.
 */
public class AgrupadorGrupoLevantamentoEstoque implements Serializable, Comparable<AgrupadorGrupoLevantamentoEstoque>, AgrupadorLevantamentoEstoque {

    private BigDecimal saldoGrupoMaterial;
    private BigDecimal valorBensEstoqueContabil;
    private BigDecimal estoqueAtual;
    private BigDecimal quantidade;
    private BigDecimal valorLevantamento;
    private GrupoMaterial grupoMaterial;
    private TipoEstoque tipoEstoque;
    private BensEstoque bensEstoque;
    private Boolean contabilMaiorMateriais;
    private Boolean grupoExterno;

    public AgrupadorGrupoLevantamentoEstoque(AgrupadorLevantamentoEstoque item) {
        this.setGrupoMaterial(item.getGrupoMaterial());
        this.setTipoEstoque(item.getTipoEstoque());
        this.setGrupoExterno(item.getGrupoExterno());
        saldoGrupoMaterial = BigDecimal.ZERO;
        valorBensEstoqueContabil = BigDecimal.ZERO;
        estoqueAtual = BigDecimal.ZERO;
        quantidade = BigDecimal.ZERO;
        valorLevantamento = BigDecimal.ZERO;
        adicionarValor(item);
    }

    public void adicionarValor(AgrupadorLevantamentoEstoque item) {
        this.valorLevantamento = this.valorLevantamento.add(item.getValorTotal()).setScale(2, RoundingMode.HALF_EVEN);
        this.quantidade = this.quantidade.add(item.getQuantidade());
    }

    public BensEstoque getBensEstoque() {
        return bensEstoque;
    }

    public void setBensEstoque(BensEstoque bensEstoque) {
        this.bensEstoque = bensEstoque;
    }

    public BigDecimal getEstoqueAtual() {
        return estoqueAtual.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setEstoqueAtual(BigDecimal estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }

    public BigDecimal getValorTotal() {
        return valorLevantamento.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setValorLevantamento(BigDecimal valorLevantamento) {
        this.valorLevantamento = valorLevantamento;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public BigDecimal getSaldoGrupoMaterial() {
        BigDecimal saldoAtualizado = saldoGrupoMaterial.setScale(2, RoundingMode.HALF_EVEN);
        if (getValorBensEstoqueContabil() != null && getValorBensEstoqueContabil().compareTo(BigDecimal.ZERO) > 0) {
            saldoAtualizado = TipoOperacaoBensEstoque.getOperacoesDebitoIntegracaoMateriais().contains(bensEstoque.getOperacoesBensEstoque())
                ? saldoAtualizado.subtract(getValorBensEstoqueContabil())
                : saldoAtualizado.add(getValorBensEstoqueContabil());
        }
        return saldoAtualizado;
    }

    public void setSaldoGrupoMaterial(BigDecimal saldoGrupoMaterial) {
        this.saldoGrupoMaterial = saldoGrupoMaterial;
    }

    public Boolean getGrupoExterno() {
        return grupoExterno;
    }

    public void setGrupoExterno(Boolean grupoExterno) {
        this.grupoExterno = grupoExterno;
    }

    public BigDecimal getValorBensEstoqueContabil() {
        return valorBensEstoqueContabil;
    }

    public void setValorBensEstoqueContabil(BigDecimal valorBensEstoqueContabil) {
        this.valorBensEstoqueContabil = valorBensEstoqueContabil;
    }

    public BigDecimal getDiferenca() {
        return getSaldoGrupoMaterial().subtract(getValorTotal()).subtract(getEstoqueAtual());
    }

    public Boolean getContabilMaiorMateriais() {
        return contabilMaiorMateriais;
    }

    public void setContabilMaiorMateriais(Boolean contabilMaiorMateriais) {
        this.contabilMaiorMateriais = contabilMaiorMateriais;
    }

    public String getStyleRow() {
        if (hasDiferenca()) {
            return "vermelhonegrito";
        }
        if (hasBensEstoqueContabil()) {
            return "verdeescuro negrito";
        }
        return "verdenegrito";
    }

    public String getStyleRowGrupo() {
        if (getGrupoExterno()) {
            return "azulnegrito";
        }
        return getStyleRow();
    }

    public Boolean hasDiferenca() {
        return getDiferenca().compareTo(BigDecimal.ZERO) != 0;
    }

    public Boolean hasBensEstoqueContabil() {
        return bensEstoque != null;
    }

    @Override
    public boolean equals(Object o) {
        AgrupadorLevantamentoEstoque that = (AgrupadorLevantamentoEstoque) o;
        if (!grupoMaterial.equals(that.getGrupoMaterial())) return false;
        return tipoEstoque == that.getTipoEstoque();
    }

    @Override
    public int hashCode() {
        int result = grupoMaterial.hashCode();
        result = 31 * result + tipoEstoque.hashCode();
        return result;
    }

    @Override
    public int compareTo(AgrupadorGrupoLevantamentoEstoque o) {
        return this.getGrupoMaterial().getCodigo().compareTo(o.getGrupoMaterial().getCodigo());
    }

    public boolean isValorLevantametnoMaiorQueZero() {
        return getValorTotal() != null && getValorTotal().compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean isQuantidadeMaiorQueZero() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isSaldoGrupoMaterialMaiorQueZero() {
        return saldoGrupoMaterial != null && saldoGrupoMaterial.compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean isValorEstoqueMaiorQueZero() {
        return estoqueAtual != null && estoqueAtual.compareTo(BigDecimal.ZERO) != 0;
    }

    public String getQuantidadeFormatada() {
        return Util.formataQuandoDecimal(getQuantidade(), TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
    }
}
