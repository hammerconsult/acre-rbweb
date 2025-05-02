package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mga on 05/04/2018.
 */
public class RelatorioSaidaMateriaisPorTransferencia {

    private Long numero;
    private Date dataSaida;
    private String usuario;
    private String retiradoPor;
    private String requisicao;
    private String unidadeAdmRequerente;
    private String requerente;
    private String localEstoqueOrigem;
    private String localEstoqueDestino;
    private String tipoBaixa;

    private Integer numeroItem;
    private String material;
    private String localEstoque;
    private String unidadeOrcamentaria;
    private String lote;
    private BigDecimal quantidadeAtender;
    private BigDecimal quantidadeEntregar;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal valorTotalCalculo;

    public RelatorioSaidaMateriaisPorTransferencia() {
        quantidadeAtender = BigDecimal.ZERO;
        quantidadeEntregar = BigDecimal.ZERO;
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
    }

    public String getRequisicao() {
        return requisicao;
    }

    public void setRequisicao(String requisicao) {
        this.requisicao = requisicao;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getRetiradoPor() {
        return retiradoPor;
    }

    public void setRetiradoPor(String retiradoPor) {
        this.retiradoPor = retiradoPor;
    }

    public String getUnidadeAdmRequerente() {
        return unidadeAdmRequerente;
    }

    public void setUnidadeAdmRequerente(String unidadeAdmRequerente) {
        this.unidadeAdmRequerente = unidadeAdmRequerente;
    }

    public String getRequerente() {
        return requerente;
    }

    public void setRequerente(String requerente) {
        this.requerente = requerente;
    }

    public String getLocalEstoqueOrigem() {
        return localEstoqueOrigem;
    }

    public void setLocalEstoqueOrigem(String localEstoqueOrigem) {
        this.localEstoqueOrigem = localEstoqueOrigem;
    }

    public String getLocalEstoqueDestino() {
        return localEstoqueDestino;
    }

    public void setLocalEstoqueDestino(String localEstoqueDestino) {
        this.localEstoqueDestino = localEstoqueDestino;
    }

    public String getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(String tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
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

    public BigDecimal getQuantidadeAtender() {
        return quantidadeAtender;
    }

    public void setQuantidadeAtender(BigDecimal quantidadeAtender) {
        this.quantidadeAtender = quantidadeAtender;
    }

    public BigDecimal getQuantidadeEntregar() {
        return quantidadeEntregar;
    }

    public void setQuantidadeEntregar(BigDecimal quantidadeEntregar) {
        this.quantidadeEntregar = quantidadeEntregar;
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

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }

    public BigDecimal getValorTotalCalculo() {
        return valorTotalCalculo;
    }

    public void setValorTotalCalculo(BigDecimal valorTotalCalculo) {
        this.valorTotalCalculo = valorTotalCalculo;
    }
}
