package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 22/03/2018.
 */
public class RelatorioEntradaMateriaisPorIncorporacao {

    private Long numero;
    private Long numeroItem;
    private Date dataMovimento;
    private String responsavel;
    private String pessoa;
    private String unidadeAdministrativa;
    private String tipoIngresso;

    private String material;
    private String localEstoque;
    private String unidadeOrcamentaria;
    private String lote;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private String unidadeMedida;
    private List<RelatorioEntradaMateriaisPorIncorporacao> itensEntrada;

    public RelatorioEntradaMateriaisPorIncorporacao() {
        itensEntrada = Lists.newArrayList();
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(String unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public String getTipoIngresso() {
        return tipoIngresso;
    }

    public void setTipoIngresso(String tipoIngresso) {
        this.tipoIngresso = tipoIngresso;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(String localEstoque) {
        this.localEstoque = localEstoque;
    }

    public String getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(String unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<RelatorioEntradaMateriaisPorIncorporacao> getItensEntrada() {
        return itensEntrada;
    }

    public void setItensEntrada(List<RelatorioEntradaMateriaisPorIncorporacao> itensEntrada) {
        this.itensEntrada = itensEntrada;
    }

    public Long getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Long numeroItem) {
        this.numeroItem = numeroItem;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }
}
