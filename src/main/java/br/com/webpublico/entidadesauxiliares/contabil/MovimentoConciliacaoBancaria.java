package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.contabil.ConciliacaoArquivoMovimentoSistema;
import br.com.webpublico.enums.TipoOperacaoConcilicaoBancaria;
import br.com.webpublico.interfaces.EnumComDescricao;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mateus on 01/08/17.
 */
public class MovimentoConciliacaoBancaria {
    private Date dataMovimento;
    private Date dataConciliacao;
    private String historico;
    private BigDecimal credito;
    private BigDecimal debito;
    private TipoOperacaoConcilicaoBancaria tipoOperacaoConciliacao;
    private Long movimentoId;
    private TipoMovimento tipoMovimento;
    private String subConta;
    private String numero;
    private Situacao situacao;
    private String numeroOBN600;

    public MovimentoConciliacaoBancaria() {
        credito = BigDecimal.ZERO;
        debito = BigDecimal.ZERO;
    }

    public MovimentoConciliacaoBancaria(ConciliacaoArquivoMovimentoSistema conciliacaoArquivoMovimentoSistema) {
        this.dataMovimento = conciliacaoArquivoMovimentoSistema.getDataMovimento();
        this.dataConciliacao = conciliacaoArquivoMovimentoSistema.getDataConciliacao();
        this.historico = conciliacaoArquivoMovimentoSistema.getHistorico();
        this.credito = conciliacaoArquivoMovimentoSistema.getValor();
        this.tipoOperacaoConciliacao = conciliacaoArquivoMovimentoSistema.getTipoOperacaoConciliacao();
        this.movimentoId = conciliacaoArquivoMovimentoSistema.getMovimentoId();
        this.tipoMovimento = conciliacaoArquivoMovimentoSistema.getTipoMovimento();
        this.subConta = conciliacaoArquivoMovimentoSistema.getSubConta();
        this.numero = conciliacaoArquivoMovimentoSistema.getNumero();
        this.situacao = conciliacaoArquivoMovimentoSistema.getSituacao();
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
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

    public TipoOperacaoConcilicaoBancaria getTipoOperacaoConciliacao() {
        return tipoOperacaoConciliacao;
    }

    public void setTipoOperacaoConciliacao(TipoOperacaoConcilicaoBancaria tipoOperacaoConciliacao) {
        this.tipoOperacaoConciliacao = tipoOperacaoConciliacao;
    }

    public Long getMovimentoId() {
        return movimentoId;
    }

    public void setMovimentoId(Long movimentoId) {
        this.movimentoId = movimentoId;
    }

    public TipoMovimento getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimento tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getSubConta() {
        return subConta;
    }

    public void setSubConta(String subConta) {
        this.subConta = subConta;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public String getNumeroOBN600() {
        return numeroOBN600;
    }

    public void setNumeroOBN600(String numeroOBN600) {
        this.numeroOBN600 = numeroOBN600;
    }

    public enum TipoMovimento implements EnumComDescricao {
        AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO("Ajuste Ativo Disponível Aumentativo", "/ajuste-ativo-disponivel/"),
        AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO("Ajuste Ativo Disponível Diminutivo", "/ajuste-ativo-disponivel/"),
        DESPESA_EXTRA("Despesa Extraorçamentária", "/despesa-extra/"),
        ESTORNO_AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO("Estorno de Ajuste Ativo Disponível Aumentativo", "/ajuste-ativo-disponivel/"),
        ESTORNO_AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO("Estorno de Ajuste Ativo Disponível Diminutivo", "/ajuste-ativo-disponivel/"),
        ESTORNO_DESPESA_EXTRA("Estorno da Despesa Extraorçamentária", "/estorno-despesa-extra/"),
        ESTORNO_LIBERACAO_FINANCEIRA("Estorno de Liberação Financeira", "/estorno-liberacao-financeira/"),
        ESTORNO_LIBERACAO_FINANCEIRA_REPASSE("Estorno de Liberação Financeira", "/estorno-liberacao-financeira/"),
        ESTORNO_PAGAMENTO("Estorno de Pagamento", "/pagamento-estorno/"),
        ESTORNO_PAGAMENTO_RESTO("Estorno de Pagamento de Resto", "/pagamento-estorno/resto-a-pagar/"),
        ESTORNO_RECEITA_EXTRA("Estorno de Receita Extraorçamentária", "/receita-extra-estorno/"),
        ESTORNO_RECEITA_REALIZADA("Estorno de Receita Realizada", "/receita-realizada-estorno/"),
        ESTORNO_TRANSFERENCIA_FINANCEIRA_DEPOSITO("Estorno de Transferência Financeira de Depósito", "/estorno-transferencia-financeira/"),
        ESTORNO_TRANSFERENCIA_FINANCEIRA_RETIRADA("Estorno de Transferência Financeira de Retirada", "/estorno-transferencia-financeira/"),
        LANCCONCILIACAOBANCARIA("Pendência Bancária", "/conciliacao-bancaria/"),
        LIBERACAO_FINANCEIRA("Liberação Financeira", "/liberacao-financeira/"),
        LIBERACAO_FINANCEIRA_REPASSE("Liberação Financeira", "/liberacao-financeira/"),
        PAGAMENTO("Pagamento", "/pagamento/"),
        PAGAMENTO_RESTO("Pagamento de Resto", "/pagamento/resto-a-pagar/"),
        RECEITA_EXTRA("Receita Extraorçamentária", "/receita-extra/"),
        RECEITA_REALIZADA("Receita Realizada", "/receita-realizada/"),
        TRANSFERENCIA_FINANCEIRA_DEPOSITO("Transferência Financeira de Depósito", "/transferencia-financeira/"),
        TRANSFERENCIA_FINANCEIRA_RETIRADA("Transferência Financeira de Retirada", "/transferencia-financeira/");
        private String descricao;
        private String caminhoPadrao;

        TipoMovimento(String descricao, String caminhoPadrao) {
            this.descricao = descricao;
            this.caminhoPadrao = caminhoPadrao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        public String getCaminhoPadrao() {
            return caminhoPadrao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum Situacao implements EnumComDescricao {
        ABERTO("Aberto"),
        NAO_APLICAVEL("Não Aplicável"),
        PAGO("Pago");
        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
