package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensMoveis;

import java.math.BigDecimal;
import java.util.Date;

public class MovimentacaoBemContabilMovimento {

    private Long id;
    private String numero;
    private Date data;
    private TipoMovimentoBemContabil tipoMovimento;
    private CategoriaOrcamentaria categoriaOrcamentaria;
    private TipoLancamento tipoLancamento;
    private TipoOperacaoBensMoveis tipoOperacaoBensMoveis;
    private BigDecimal credito;
    private BigDecimal debito;

    public MovimentacaoBemContabilMovimento() {
        credito = BigDecimal.ZERO;
        debito = BigDecimal.ZERO;
        categoriaOrcamentaria = CategoriaOrcamentaria.NORMAL;
        tipoLancamento = TipoLancamento.NORMAL;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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

    public TipoMovimentoBemContabil getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoBemContabil tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public TipoOperacaoBensMoveis getTipoOperacaoBensMoveis() {
        return tipoOperacaoBensMoveis;
    }

    public void setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis tipoOperacaoBensMoveis) {
        this.tipoOperacaoBensMoveis = tipoOperacaoBensMoveis;
    }

    public BigDecimal getValorAtual() {
        return credito.subtract(debito);
    }

    public String getDescricaoMovimento() {
        if (isMovimentoBensMoveis() || isMovimentoTransfBensMoveis()) {
            return tipoOperacaoBensMoveis.getDescricao() + " - " + tipoLancamento.getDescricao();

        } else if (isMovimentoLiquidacao() || isMovimentoLiquidacaoEstorno()) {
            return "<b><i>" + tipoMovimento.getDescricao() + "</i></b> - " + tipoOperacaoBensMoveis.getDescricao();
        }
        return tipoMovimento.getDescricao();
    }

    public String getCaminhoNavegacao() {
        if (isMovimentoLiquidacao()) {
            if (categoriaOrcamentaria.isNormal()) {
                return "/liquidacao/ver/";
            }
            return "/liquidacao/resto-a-pagar/ver/";

        } else if (isMovimentoLiquidacaoEstorno()) {
            if (categoriaOrcamentaria.isNormal()) {
                return "/liquidacao-estorno/ver/";
            }
            return "/liquidacao-estorno/resto-a-pagar/ver/";
        }
        return tipoMovimento.getUrl();
    }

    public boolean isMovimentoBensMoveis() {
        return TipoMovimentoBemContabil.BENS_MOVEIS.equals(tipoMovimento);
    }

    public boolean isMovimentoTransfBensMoveis() {
        return TipoMovimentoBemContabil.TRANSF_BENS_MOVEIS.equals(tipoMovimento);
    }

    public boolean isMovimentoLiquidacao() {
        return TipoMovimentoBemContabil.LIQUIDACAO.equals(tipoMovimento);
    }

    public boolean isMovimentoLiquidacaoEstorno() {
        return TipoMovimentoBemContabil.LIQUIDACAO_ESTORNO.equals(tipoMovimento);
    }

    public enum TipoMovimentoBemContabil {
        ITEMAQUISICAO("Aquisição de Bens", "/efetivacao-aquisicao-bem-movel/ver/"),
        ITEMAQUISICAO_ESTORNO("Estorno de Aquisição de Bens", "/estorno-aquisicao/bem-movel/ver/"),
        EFETIVACAOLEVANTAMENTOBEM("Levantamento de Bens", "/efetivacao-de-levantamento-de-bem/ver/"),
        EFETIVACAODEPRECIACAOBEM("Depreciação do Levantamento de Bens", "/efetivacao-de-levantamento-de-bem/ver/"),
        INCORPORACAOBEM("Incorporação de Bens", "/efetivacao-solicitacao-incorporacao-movel/ver/"),
        DEPRECIACAO("Depreciação de Bens", "/depreciacao-movel/ver/"),
        ESTORNOREDUCAOVALORBEM("Estorno Depreciação de Bens", "/depreciacao-movel/ver/"),
        AMORTIZACAO("Amortização de Bens", "/amortizacao-movel/ver/"),
        EFETIVACAOREAVALICAOBEM("Reavaliação de Bens", "/efetivacao-de-reavaliacao-de-bem-movel/ver/"),
        EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO("Ajuste de Bens Original Aumentativo", "/efetivacao-ajuste-bem-movel-original/ver/"),
        EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA("Ajuste de Bens Original Aumentativo - Empresa Pública", "/efetivacao-ajuste-bem-movel-original/ver/"),
        EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO("Ajuste de Bens Original Diminutivo", "/efetivacao-ajuste-bem-movel-original/ver/"),
        EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA("Ajuste de Bens Original Diminutivo - Empresa Pública", "/efetivacao-ajuste-bem-movel-original/ver/"),
        EFETIVACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO("Ajuste de Bens Depreciação Aumentativo", "/efetivacao-ajuste-bem-movel-original/ver/"),
        EFETIVACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO("Ajuste de Bens Depreciação Diminutivo", "/efetivacao-ajuste-bem-movel-original/ver/"),
        EFETIVACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO("Ajuste de Bens Amortização Aumentativo - Empresa Pública", "/efetivacao-ajuste-bem-movel-original/ver/"),
        EFETIVACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO("Ajuste de Bens Amortização Diminutivo - Empresa Pública", "/efetivacao-ajuste-bem-movel-original/ver/"),
        EFETIVACAOTRANSFERENCIABEM("Transferência de Bens", "/efetivacao-de-transferencia-de-bem-movel/ver/"),
        TRANSFERENCIADEPRECIACAORECEBIDA("Transferência Depreciação de Bens", "/efetivacao-de-transferencia-de-bem-movel/ver/"),
        TRANSFERENCIABEMCONCEDIDA("Transferência de Bens", "/efetivacao-de-transferencia-de-bem-movel/ver/"),
        TRANSFERENCIADEPRECIACAOCONCEDIDA("Transferência de Bens", "/efetivacao-de-transferencia-de-bem-movel/ver/"),
        BENS_MOVEIS("Bens Móveis", "/contabil/bens-moveis/ver/"),
        TRANSF_BENS_MOVEIS("Transferência de Bens Móveis", "/contabil/transferencia-bens-moveis/ver/"),
        LIQUIDACAO("Liquidação", ""),
        LIQUIDACAO_ESTORNO("Estorno de Liquidação", "");
        private String descricao;
        private String url;

        TipoMovimentoBemContabil(String descricao, String url) {
            this.descricao = descricao;
            this.url = url;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getUrl() {
            return url;
        }
    }
}
