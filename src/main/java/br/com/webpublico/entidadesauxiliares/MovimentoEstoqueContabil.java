package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class MovimentoEstoqueContabil {

    private Long id;
    private Date data;
    private Long numero;
    private String descricao;
    private TipoMovimentoMaterial tipo;
    private TipoOperacao tipoOperacao;
    private TipoLancamento tipoLancamento;
    private Boolean restoPagar;
    private BigDecimal valor;
    private BigDecimal credito;
    private BigDecimal debito;
    private BigDecimal saldoAnterior;
    private Long numeroEntradaLevantamento;
    private Date dataEntradaLevantamento;
    private List<VOLiquidacaoDocumentoFiscal> documentosFiscais;
    private Boolean documentoFiscalIntegrado;

    public MovimentoEstoqueContabil() {
        valor = BigDecimal.ZERO;
        credito = BigDecimal.ZERO;
        debito = BigDecimal.ZERO;
        saldoAnterior = BigDecimal.ZERO;
        restoPagar = Boolean.FALSE;
        documentoFiscalIntegrado = Boolean.FALSE;
        documentosFiscais = Lists.newArrayList();
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public Boolean getDocumentoFiscalIntegrado() {
        return documentoFiscalIntegrado;
    }

    public void setDocumentoFiscalIntegrado(Boolean documentoFiscalIntegrado) {
        this.documentoFiscalIntegrado = documentoFiscalIntegrado;
    }

    public List<VOLiquidacaoDocumentoFiscal> getDocumentosFiscais() {
        return documentosFiscais;
    }

    public void setDocumentosFiscais(List<VOLiquidacaoDocumentoFiscal> documentosFiscais) {
        this.documentosFiscais = documentosFiscais;
    }

    public Boolean getRestoPagar() {
        return restoPagar;
    }

    public void setRestoPagar(Boolean restoPagar) {
        this.restoPagar = restoPagar;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public Date getDataEntradaLevantamento() {
        return dataEntradaLevantamento;
    }

    public void setDataEntradaLevantamento(Date dataEntradaLevantamento) {
        this.dataEntradaLevantamento = dataEntradaLevantamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getNumeroEntradaLevantamento() {
        return numeroEntradaLevantamento;
    }

    public void setNumeroEntradaLevantamento(Long numeroEntradaLevantamento) {
        this.numeroEntradaLevantamento = numeroEntradaLevantamento;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public TipoMovimentoMaterial getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentoMaterial tipo) {
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public String getDescricaoEntradaLevantamento() {
        if (numeroEntradaLevantamento != null && dataEntradaLevantamento != null) {
            return "Entrada por Compra " + numeroEntradaLevantamento + " - " + DataUtil.getDataFormatada(dataEntradaLevantamento);
        }
        return "";
    }

    public String getLinkLiquidacao() {
        if (tipoLancamento.isNormal()) {
            return restoPagar ? "/liquidacao/resto-a-pagar/ver/" : "/liquidacao/ver/";
        }
        return restoPagar ? "/liquidacao-estorno/resto-a-pagar/ver/" : "/liquidacao-estorno/ver/";
    }

    public boolean hasDocumentosFiscais() {
        return documentosFiscais != null && !documentosFiscais.isEmpty();
    }

    public boolean isEntradaPorCompra() {
        return TipoMovimentoMaterial.ENTRADA_COMPRA.equals(tipo);
    }

    public enum TipoMovimentoMaterial {
        ENTRADA_COMPRA("Entrada por Compra", "/entrada-por-compra/ver/"),
        ENTRADA_LEVANTAMENTO("Entrada por Levantamento", "/efetivacao-levantamento-estoque/ver/"),
        ENTRADA_TRANSFERENCIA("Entrada por Transferência", "/entrada-por-transferencia/ver/"),
        ENTRADA_INCORPORACAO("Entrada por Incorporação", "/entrada-por-incorporacao/ver/"),
        SAIDA_DEVOLUCAO("Saída por Devolução", "/saida-por-devolucao/ver/"),
        SAIDA_CONSUMO("Saída por Consumo", "/saida-por-consumo/ver/"),
        SAIDA_TRANSFERENCIA("Saída por Transferência", "/saida-por-transferencia/ver/"),
        SAIDA_PRODUCAO("Saída por Produção", ""),
        SAIDA_DESINCORPORACAO("Saída por Desincorporação", "/saida-material-por-desincorporacao/ver/"),
        SAIDA_INVENTARIO("Saída por Inventário", ""),
        SAIDA_DIRETA("Saída Direta", "/saida-direta/ver/"),
        LIQUIDACAO("Liquidação", "/liquidacao/ver/"),
        LIQUIDACAO_RESTO("Liquidação de Resto a Pagar", "/liquidacao/resto-a-pagar/ver/"),
        BENS_ESTOQUE("Bens de Estoque", "/contabil/bens-estoque/ver/"),
        TRANSF_BENS_ESTOQUE("Transferência Bens de Estoque", "/contabil/transferencia-bens-estoque/ver/");
        private String descricao;
        private String link;

        TipoMovimentoMaterial(String descricao, String link) {
            this.descricao = descricao;
            this.link = link;
        }

        public String getLink() {
            return link;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
