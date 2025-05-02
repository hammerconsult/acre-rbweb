package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConciliacaoBancaria;
import br.com.webpublico.enums.ConcedidaRecebida;
import br.com.webpublico.enums.MovimentacaoFinanceira;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 26/09/13
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ConciliacaoBancariaPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    private static final String CAMINHO_PADRAO_CONTA_BANCARIA_SUBCONTA = "subConta.contaBancariaEntidade.";
    private static final String CAMINHO_PADRAO_CONTA_BANCARIA_CONTA_FINANCEIRA = "contaFinanceira.contaBancariaEntidade.";

    @Override
    public void novo(ComponentSystemEvent evento) {
        super.novo(evento);
    }

    @Override
    public String getComplementoQuery() {
        String condicao = montaCondicao();
        String ordenar = montaOrdenacao();

        ConciliacaoBancariaControlador conciliacaoBancariaControlador = (ConciliacaoBancariaControlador) Util.getControladorPeloNome("conciliacaoBancariaControlador");

        String retorno = " where obj." + getNomeDaColuna(conciliacaoBancariaControlador.getSelecionado()) + "id in ( "
                + conciliacaoBancariaControlador.getSelecionado().getContaBancaria().getId() + " ) and " + condicao;

        if (conciliacaoBancariaControlador.getSelecionado().getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            //Movimentos Normais
            if (conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.PAGAMENTO_DE_RESTOS_A_PAGAR)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.PAGAMENTO)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.DESPESA_EXTRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.RECEITA_REALIZADA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.RECEITA_EXTRAORCAMENTARIA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_NORMAL)
                    //Movimentos Estornos
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DE_PAGAMENTO_DE_RESTOS_A_PAGAR)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DE_PAGAMENTO)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DA_DESPESA_EXTRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DE_RECEITA_REALIZADA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DE_RECEITA_EXTRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_ESTORNO)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.OPERACAO_DA_CONCILIACAO)) {
                retorno += " and obj.dataConciliacao is null ";
            }
            if (conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.TRANSFERENCIA_FINANCEIRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.LIBERACAO_DE_COTA_FINANCEIRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_TRANSFERENCIA_SALDO_CONTA_FINANCEIRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DA_TRANFERENCIA_FINANCEIRA_DE_MESMA_UNIDADE)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_LIBERACAO_SALDO_CONTA_FINANCEIRA)) {
                if (conciliacaoBancariaControlador.getSelecionado().getConcedidaRecebida().equals(ConcedidaRecebida.CONCEDIDA)) {
                    retorno += " and obj.dataConciliacao is null ";
                } else {
                    retorno += " and obj.recebida is null ";
                }
            }
        } else if (conciliacaoBancariaControlador.getSelecionado().getTipoLancamento().equals(TipoLancamento.ESTORNO)) {
            //Movimentos Normais
            if (conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.PAGAMENTO_DE_RESTOS_A_PAGAR)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.PAGAMENTO)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.DESPESA_EXTRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.RECEITA_REALIZADA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.RECEITA_EXTRAORCAMENTARIA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_NORMAL)
                    //Movimentos Estornos
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DE_PAGAMENTO_DE_RESTOS_A_PAGAR)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DE_PAGAMENTO)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DA_DESPESA_EXTRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DE_RECEITA_REALIZADA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DE_RECEITA_EXTRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_ESTORNO)) {
                retorno += " and obj.dataConciliacao is not null ";
            }
            if (conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.TRANSFERENCIA_FINANCEIRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.LIBERACAO_DE_COTA_FINANCEIRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_TRANSFERENCIA_SALDO_CONTA_FINANCEIRA)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DA_TRANFERENCIA_FINANCEIRA_DE_MESMA_UNIDADE)
                    || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_LIBERACAO_SALDO_CONTA_FINANCEIRA)) {
                if (conciliacaoBancariaControlador.getSelecionado().getConcedidaRecebida().equals(ConcedidaRecebida.CONCEDIDA)) {
                    retorno += " and obj.dataConciliacao is not null ";
                } else {
                    retorno += " and obj.recebida is not null ";
                }
            }
        }

        if (conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.PAGAMENTO_DE_RESTOS_A_PAGAR)
                || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DE_PAGAMENTO_DE_RESTOS_A_PAGAR)) {
            retorno += " and obj.categoriaOrcamentaria = 'RESTO' ";
        }

        if (conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.PAGAMENTO)
                || conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.ESTORNO_DE_PAGAMENTO)) {
            retorno += " and obj.categoriaOrcamentaria = 'NORMAL' ";
        }

        if (conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_NORMAL)) {
            retorno += " and obj.tipoLancamento = 'NORMAL' ";
        } else if (conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira().equals(MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_ESTORNO)) {
            retorno += " and obj.tipoLancamento = 'ESTORNO' ";
        }
        retorno += getAndCampoDataMovimento(conciliacaoBancariaControlador);
        return retorno + ordenar;
    }

    public String getAndCampoDataMovimento(ConciliacaoBancariaControlador conciliacaoBancariaControlador) {
        String sql = " and trunc(obj.";
        switch (conciliacaoBancariaControlador.getSelecionado().getMovimentacaoFinanceira()) {
            case PAGAMENTO:
            case PAGAMENTO_DE_RESTOS_A_PAGAR:
                sql += "dataPagamento";
                break;
            case ESTORNO_DE_PAGAMENTO:
            case ESTORNO_DE_PAGAMENTO_DE_RESTOS_A_PAGAR:
            case ESTORNO_DA_TRANFERENCIA_FINANCEIRA_DE_MESMA_UNIDADE:
            case ESTORNO_TRANSFERENCIA_SALDO_CONTA_FINANCEIRA:
            case ESTORNO_LIBERACAO_SALDO_CONTA_FINANCEIRA:
            case ESTORNO_DA_DESPESA_EXTRA:
            case ESTORNO_DE_RECEITA_REALIZADA:
            case ESTORNO_DE_RECEITA_EXTRA:
                sql +="dataEstorno";
                break;
            case DESPESA_EXTRA:
                sql += "dataPagto";
                break;
            case RECEITA_REALIZADA:
                sql +=  "dataLancamento";
                break;
            case RECEITA_EXTRAORCAMENTARIA:
                sql +=  "dataReceita";
                break;
            case LIBERACAO_DE_COTA_FINANCEIRA:
                sql += "dataLiberacao";
                break;
            case TRANSFERENCIA_FINANCEIRA:
            case TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE:
                sql += "dataTransferencia";
                break;
            case OPERACAO_DA_CONCILIACAO:
                sql += "data";
                break;
            case AJUSTE_ATIVO_DISPONIVEL_ESTORNO:
            case AJUSTE_ATIVO_DISPONIVEL_NORMAL:
                sql += "dataAjuste";
                break;
        }
        sql += ") <= to_date('" + DataUtil.getDataFormatada(conciliacaoBancariaControlador.getSelecionado().getDataConciliacao()) + "','dd/MM/yyyy') ";
        return sql;
    }

    private String getNomeDaColuna(ConciliacaoBancaria concilicaoBancaria) {
        MovimentacaoFinanceira movimentacaoFinanceira = concilicaoBancaria.getMovimentacaoFinanceira();
        if (movimentacaoFinanceira.equals(MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_NORMAL)
                || movimentacaoFinanceira.equals(MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_ESTORNO)) {
            return CAMINHO_PADRAO_CONTA_BANCARIA_CONTA_FINANCEIRA;
        }
        if (movimentacaoFinanceira.equals(MovimentacaoFinanceira.ESTORNO_DE_PAGAMENTO)
                || movimentacaoFinanceira.equals(MovimentacaoFinanceira.ESTORNO_DE_PAGAMENTO_DE_RESTOS_A_PAGAR)) {
            return "pagamento." + CAMINHO_PADRAO_CONTA_BANCARIA_SUBCONTA;
        }
        if (movimentacaoFinanceira.equals(MovimentacaoFinanceira.ESTORNO_DE_RECEITA_REALIZADA)) {
            return "ContaFinanceira.contaBancariaEntidade.";
        }
        if (movimentacaoFinanceira.equals(MovimentacaoFinanceira.ESTORNO_DA_DESPESA_EXTRA)) {
            return "pagamentoExtra." + CAMINHO_PADRAO_CONTA_BANCARIA_SUBCONTA;
        }
        if (movimentacaoFinanceira.equals(MovimentacaoFinanceira.LIBERACAO_DE_COTA_FINANCEIRA)) {
            if (concilicaoBancaria.getConcedidaRecebida().equals(ConcedidaRecebida.CONCEDIDA)) {
                return "solicitacaoCotaFinanceira.contaFinanceira.contaVinculada.contaBancariaEntidade.";
            } else {
                return "solicitacaoCotaFinanceira.contaFinanceira.contaBancariaEntidade.";
            }
        }
        if (movimentacaoFinanceira.equals(MovimentacaoFinanceira.ESTORNO_LIBERACAO_SALDO_CONTA_FINANCEIRA)) {
            if (concilicaoBancaria.getConcedidaRecebida().equals(ConcedidaRecebida.CONCEDIDA)) {
                return "liberacao.solicitacaoCotaFinanceira.contaFinanceira.contaVinculada.contaBancariaEntidade.";
            } else {
                return "liberacao.solicitacaoCotaFinanceira.contaFinanceira.contaBancariaEntidade.";
            }
        }
        if (movimentacaoFinanceira.equals(MovimentacaoFinanceira.ESTORNO_DE_RECEITA_EXTRA)) {
            return "receitaExtra." + CAMINHO_PADRAO_CONTA_BANCARIA_SUBCONTA;
        }
        if (movimentacaoFinanceira.equals(MovimentacaoFinanceira.ESTORNO_DA_DESPESA_EXTRA)) {
            return "pagamentoExtra." + CAMINHO_PADRAO_CONTA_BANCARIA_SUBCONTA;
        }
        if (movimentacaoFinanceira.equals(MovimentacaoFinanceira.ESTORNO_DA_TRANFERENCIA_FINANCEIRA_DE_MESMA_UNIDADE)) {
            if (concilicaoBancaria.getConcedidaRecebida().equals(ConcedidaRecebida.CONCEDIDA)) {
                return "transferenciaMesmaUnidade.subContaRetirada.contaBancariaEntidade.";
            } else {
                return "transferenciaMesmaUnidade.subContaDeposito.contaBancariaEntidade.";
            }
        }
        if (movimentacaoFinanceira.equals(MovimentacaoFinanceira.ESTORNO_TRANSFERENCIA_SALDO_CONTA_FINANCEIRA)) {
            if (concilicaoBancaria.getConcedidaRecebida().equals(ConcedidaRecebida.CONCEDIDA)) {
                return "transferencia.subContaRetirada.contaBancariaEntidade.";
            } else {
                return "transferencia.subContaDeposito.contaBancariaEntidade.";
            }
        }
        if (movimentacaoFinanceira.equals(MovimentacaoFinanceira.TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE)
                || movimentacaoFinanceira.equals(MovimentacaoFinanceira.TRANSFERENCIA_FINANCEIRA)) {
            if (concilicaoBancaria.getConcedidaRecebida().equals(ConcedidaRecebida.CONCEDIDA)) {
                return "subContaRetirada.contaBancariaEntidade.";
            } else {
                return "subContaDeposito.contaBancariaEntidade.";
            }
        }
        return CAMINHO_PADRAO_CONTA_BANCARIA_SUBCONTA;
    }

    @Override
    public String getKeySession() {
        return SESSION_KEY_PESQUISAGENERICO + classe.getSimpleName()+"-manual";
    }
}
