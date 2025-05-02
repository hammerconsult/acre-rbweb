package br.com.webpublico.entidades;

import br.com.webpublico.enums.ConcedidaRecebida;
import br.com.webpublico.enums.MovimentacaoFinanceira;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 24/09/13
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "ConciliacaoBancaria")
@Etiqueta("Operação da Concilia")
public class ConciliacaoBancaria implements Serializable {

    private final String CAMPO_DATA_CONCILIACAO_CONCEDIDA = "dataConciliacao";
    private final String CAMPO_DATA_CONCILIACAO_RECEBIDA = "recebida";
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    private TipoLancamento tipoLancamento;
    private Date dataConciliacao;
    private Date recebida;
    private ContaBancariaEntidade contaBancaria;
    private MovimentacaoFinanceira movimentacaoFinanceira;
    private ConcedidaRecebida concedidaRecebida;

    public ConciliacaoBancaria() {
        this.tipoLancamento = TipoLancamento.NORMAL;
        this.dataConciliacao = new Date();
        this.concedidaRecebida = ConcedidaRecebida.CONCEDIDA;
    }

    public ConciliacaoBancaria(TipoLancamento tipoLancamento, Date dataConciliacao, Date recebida, ContaBancariaEntidade contaBancaria, MovimentacaoFinanceira movimentacaoFinanceira) {
        this.tipoLancamento = tipoLancamento;
        this.dataConciliacao = dataConciliacao;
        this.recebida = recebida;
        this.contaBancaria = contaBancaria;
        this.movimentacaoFinanceira = movimentacaoFinanceira;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public Date getRecebida() {
        return recebida;
    }

    public void setRecebida(Date recebida) {
        this.recebida = recebida;
    }

    public ContaBancariaEntidade getContaBancaria() {
        return contaBancaria;
    }

    public void setContaBancaria(ContaBancariaEntidade contaBancaria) {
        this.contaBancaria = contaBancaria;
    }

    public MovimentacaoFinanceira getMovimentacaoFinanceira() {
        return movimentacaoFinanceira;
    }

    public void setMovimentacaoFinanceira(MovimentacaoFinanceira movimentacaoFinanceira) {
        this.movimentacaoFinanceira = movimentacaoFinanceira;
    }

    public ConcedidaRecebida getConcedidaRecebida() {
        return concedidaRecebida;
    }

    public void setConcedidaRecebida(ConcedidaRecebida concedidaRecebida) {
        this.concedidaRecebida = concedidaRecebida;
    }

    public String getCampoDataConcilicao() {
        if (!possuiDuasDatasConciliacao()) {
            return CAMPO_DATA_CONCILIACAO_CONCEDIDA;
        } else {
            if (concedidaRecebida.equals(ConcedidaRecebida.CONCEDIDA)) {
                return CAMPO_DATA_CONCILIACAO_CONCEDIDA;
            } else {
                return CAMPO_DATA_CONCILIACAO_RECEBIDA;
            }
        }
    }

    public String getCampoDataMovimento() {
        switch (movimentacaoFinanceira) {
            case PAGAMENTO:
            case PAGAMENTO_DE_RESTOS_A_PAGAR:
                return "dataPagamento";
            case ESTORNO_DE_PAGAMENTO:
            case ESTORNO_DE_PAGAMENTO_DE_RESTOS_A_PAGAR:
            case ESTORNO_DA_TRANFERENCIA_FINANCEIRA_DE_MESMA_UNIDADE:
            case ESTORNO_TRANSFERENCIA_SALDO_CONTA_FINANCEIRA:
            case ESTORNO_LIBERACAO_SALDO_CONTA_FINANCEIRA:
            case ESTORNO_DA_DESPESA_EXTRA:
            case ESTORNO_DE_RECEITA_REALIZADA:
            case ESTORNO_DE_RECEITA_EXTRA:
                return "dataEstorno";
            case DESPESA_EXTRA:
                return "dataPagto";
            case RECEITA_REALIZADA:
                return "dataLancamento";
            case RECEITA_EXTRAORCAMENTARIA:
                return "dataReceita";
            case LIBERACAO_DE_COTA_FINANCEIRA:
                return "dataLiberacao";
            case TRANSFERENCIA_FINANCEIRA:
            case TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE:
                return "dataTransferencia";
            case OPERACAO_DA_CONCILIACAO:
                return "data";
            case AJUSTE_ATIVO_DISPONIVEL_ESTORNO:
            case AJUSTE_ATIVO_DISPONIVEL_NORMAL:
                return "dataAjuste";
        }
        return "";
    }

    public String getCampoUnidadeMovimento() {
        switch (movimentacaoFinanceira) {
            case PAGAMENTO:
            case PAGAMENTO_DE_RESTOS_A_PAGAR:
            case ESTORNO_DE_PAGAMENTO:
            case ESTORNO_DE_PAGAMENTO_DE_RESTOS_A_PAGAR:
            case ESTORNO_DA_TRANFERENCIA_FINANCEIRA_DE_MESMA_UNIDADE:
            case ESTORNO_TRANSFERENCIA_SALDO_CONTA_FINANCEIRA:
            case ESTORNO_LIBERACAO_SALDO_CONTA_FINANCEIRA:
            case ESTORNO_DA_DESPESA_EXTRA:
            case ESTORNO_DE_RECEITA_REALIZADA:
            case ESTORNO_DE_RECEITA_EXTRA:
            case OPERACAO_DA_CONCILIACAO:
            case TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE:
            case DESPESA_EXTRA:
            case RECEITA_REALIZADA:
            case RECEITA_EXTRAORCAMENTARIA:
            case LIBERACAO_DE_COTA_FINANCEIRA:
            case AJUSTE_ATIVO_DISPONIVEL_ESTORNO:
            case AJUSTE_ATIVO_DISPONIVEL_NORMAL:
                return "unidadeOrganizacional";
            case TRANSFERENCIA_FINANCEIRA:
                if (ConcedidaRecebida.CONCEDIDA.equals(concedidaRecebida)) {
                    return "unidadeOrgConcedida";
                } else {
                    return "unidadeOrganizacional";
                }
        }
        return "";
    }

    public Boolean possuiDuasDatasConciliacao() {
        if (this.getMovimentacaoFinanceira() != null) {
            if (this.getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.TRANSFERENCIA_FINANCEIRA)
                || this.getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE)
                || this.getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.LIBERACAO_DE_COTA_FINANCEIRA)
                || this.getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DA_TRANFERENCIA_FINANCEIRA_DE_MESMA_UNIDADE)
                || this.getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_LIBERACAO_SALDO_CONTA_FINANCEIRA)
                || this.getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_TRANSFERENCIA_SALDO_CONTA_FINANCEIRA)) {
                return true;
            }
        }
        return false;
    }


}
