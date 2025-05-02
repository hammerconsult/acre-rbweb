/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.LiberacaoCotaFinanceiraControlador;
import br.com.webpublico.controle.PagamentoExtraControlador;
import br.com.webpublico.controle.TransferenciaContaFinanceiraControlador;
import br.com.webpublico.controle.TransferenciaMesmaUnidadeControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.obn350.*;
import com.google.common.collect.Lists;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author reidocrime
 */
@Stateless
public class ArquivoRetornoOBN350Facade extends AbstractFacade<ArquivoRetornoOBN350> implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private BorderoFacade borderoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private StringBuilder numerosPagamento;
    private StringBuilder numerosPagamentoExtra;
    private StringBuilder numerosTransferenciaFinanceira;
    private StringBuilder numerosTransferenciaMesmaUnidade;
    private StringBuilder numerosLiberacaoFinanceira;
    private int ano;
    private static final String NAO_AUTENTICADA = "Não Autenticada";
    private Set<String> ordensBancariasNaoPagas;
    private Set<String> ordensBancariasPagas;
    private Set<String> listaTodosOrdensBancarias;
    private Set<Date> listaOrdensBancariasParaDebito;

    public ArquivoRetornoOBN350Facade() {
        super(ArquivoRetornoOBN350.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ArquivoRetornoOBN350 recuperar(Object id) {
        ArquivoRetornoOBN350 arquivo = em.find(ArquivoRetornoOBN350.class, id);
        arquivo.getArquivo().getPartes().size();
        return arquivo;
    }

    public void salvarNovo(ArquivoRetornoOBN350 entity, LayoutObn350 layoutObn350, Arquivo arquivo, FileUploadEvent fileUploadEvent) {
        try {
            entity = em.merge(entity);
            entity.setSequencialArquivo(layoutObn350.getHeaderObn350().getNumeroRemessaConsecutiva());
            if (validarArquivoComMesmoSequencial(entity, arquivo)) {
                ano = layoutObn350.getAno();
                numerosPagamento = new StringBuilder("\'");
                numerosPagamentoExtra = new StringBuilder("\'");
                numerosTransferenciaFinanceira = new StringBuilder("\'");
                numerosTransferenciaMesmaUnidade = new StringBuilder("\'");
                numerosLiberacaoFinanceira = new StringBuilder("\'");
                recuperarNumeroMovimentosDatriagemDeOperacoesArquivo(layoutObn350);
                criarObjetoArquivoRetornoOBN350(entity);
                if (fileUploadEvent != null) {
                    entity.setArquivo(arquivo);
                    arquivoFacade.salvar(arquivo, fileUploadEvent);
                    salvarNovoMovimentos(entity, layoutObn350);
                }
            }
        } catch (Exception ex) {
            logger.error("Ocorreu um erro ao importar o arquivo obn350: {}", ex);
            throw ex;
        }
    }

    private void recuperarNumeroMovimentosDatriagemDeOperacoesArquivo(LayoutObn350 layoutObn350) {
        for (RegistroObn350TipoDois reg : layoutObn350.getRegistroObn350TipoDois()) {
            if (reg.getNumeroMovimento().startsWith(TipoMovimentoArquivoBancario.PAGAMENTO.getCodigo())) {
                numerosPagamento.append(reg.getNumeroLancamento());
                numerosPagamento.append("\',\'");
            } else if (reg.getNumeroMovimento().startsWith(TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA.getCodigo())) {
                numerosPagamentoExtra.append(reg.getNumeroLancamento());
                numerosPagamentoExtra.append("\',\'");
            } else if (reg.getNumeroMovimento().startsWith(TipoMovimentoArquivoBancario.TRANSFERENCIA_FINANCEIRA.getCodigo())) {
                numerosTransferenciaFinanceira.append(reg.getNumeroLancamento());
                numerosTransferenciaFinanceira.append("\',\'");
            } else if (reg.getNumeroMovimento().startsWith(TipoMovimentoArquivoBancario.TRANSFERENCIA_MESMA_UNIDADE.getCodigo())) {
                numerosTransferenciaMesmaUnidade.append(reg.getNumeroLancamento());
                numerosTransferenciaMesmaUnidade.append("\',\'");
            } else if (reg.getNumeroMovimento().startsWith(TipoMovimentoArquivoBancario.LIBERACAO.getCodigo())) {
                numerosLiberacaoFinanceira.append(reg.getNumeroLancamento());
                numerosLiberacaoFinanceira.append("\',\'");
            }
        }
    }

    private void criarObjetoArquivoRetornoOBN350(ArquivoRetornoOBN350 entity) {
        String msg = "";
        try {
            if (numerosPagamento.length() > 1) {
                msg = "Pagamentos";
                List<Pagamento> listaPagamentos = buscarPagamentoPorNumero(numerosPagamento.toString().substring(0, numerosPagamento.toString().length() - 2));
                for (Pagamento pagamento : listaPagamentos) {
                    entity.getPagamentos().add(new ArquivoOBN350Pagamento(pagamento, entity));
                }
            }
            if (numerosPagamentoExtra.length() > 1) {
                msg = "Despesa Extraorçamentária";
                List<PagamentoExtra> listaPagamentosExtras = buscarPagamentoExtraPorNumero(numerosPagamentoExtra.toString().substring(0, numerosPagamentoExtra.toString().length() - 2));
                for (PagamentoExtra pagamentoExtra : listaPagamentosExtras) {
                    entity.getPagamentosExtras().add(new ArquivoOBN350DespesaExtra(pagamentoExtra, entity));
                }
            }
            if (numerosTransferenciaFinanceira.length() > 1) {
                msg = "Transferência Financeira";
                List<TransferenciaContaFinanceira> listaTransferenciasFinanceiras = buscarTransferenciaFinanceiraPorNumero(numerosTransferenciaFinanceira.toString().substring(0, numerosTransferenciaFinanceira.toString().length() - 2));
                for (TransferenciaContaFinanceira transferenciaFinanceira : listaTransferenciasFinanceiras) {
                    entity.getTransferenciaContaFinanceiras().add(new ArquivoOBN350TransfFinanceira(entity, transferenciaFinanceira));
                }
            }
            if (numerosTransferenciaMesmaUnidade.length() > 1) {
                msg = "Transferência Financeira de Mesma Unidade";
                List<TransferenciaMesmaUnidade> listaTransferenciasMesmaUnidade = buscarTransferenciaMesmaUnidadePorNumero(numerosTransferenciaMesmaUnidade.toString().substring(0, numerosTransferenciaMesmaUnidade.toString().length() - 2));
                for (TransferenciaMesmaUnidade transferenciaMesmaUnidade : listaTransferenciasMesmaUnidade) {
                    entity.getTransferenciaMesmaUnidades().add(new ArquivoOBN350TransfMesmaUnid(entity, transferenciaMesmaUnidade));
                }
            }
            if (numerosLiberacaoFinanceira.length() > 1) {
                msg = "Liberação Financeira";
                List<LiberacaoCotaFinanceira> listaLiberacoesFinanceiras = buscarLiberacaoFinanceiraPorNumero(numerosLiberacaoFinanceira.toString().substring(0, numerosLiberacaoFinanceira.toString().length() - 2));
                for (LiberacaoCotaFinanceira liberacaoCotaFinanceira : listaLiberacoesFinanceiras) {
                    entity.getLiberacaoCotaFinanceiras().add(new ArquivoOBN350LiberacaoFinanc(entity, liberacaoCotaFinanceira));
                }
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao criar o arquivo de Retorno OBN350, na busca de " + msg + " " + ex.getMessage());
        }
    }

    public void salvarNovoMovimentos(ArquivoRetornoOBN350 entity, LayoutObn350 layoutObn350) {

        ordensBancariasNaoPagas = new HashSet<>();
        ordensBancariasPagas = new HashSet<>();
        listaTodosOrdensBancarias = new HashSet<String>();
        listaOrdensBancariasParaDebito = new HashSet<Date>();

        salvarPagamento(entity, layoutObn350);
        salvarDespesaExtra(entity, layoutObn350);
        salvarTransferenciaFinanceira(entity, layoutObn350);
        salvarTransferenciaMesmaUnidade(entity, layoutObn350);
        salvarLiberacaoFinanceira(entity, layoutObn350);
        atualizarSituacaoBordero();
        em.merge(entity);
    }

    //INICIA PROCESSO PAGAMENTO
    private void salvarPagamento(ArquivoRetornoOBN350 entity, LayoutObn350 layoutObn350) {
        for (ArquivoOBN350Pagamento arquivo : entity.getPagamentos()) {
            Pagamento pagamento = arquivo.getPagamento();
            RegistroObn350TipoDois registroObn350TipoDois = getRegistroOBN350TipoDoisPorTipoMovimentoArquivo(layoutObn350, pagamento.getNumeroPagamento(), TipoMovimentoArquivoBancario.PAGAMENTO);
            if (registroObn350TipoDois != null) {
                arquivo.setValidado(registroObn350TipoDois.getObteveSucesso());
                pagamento.setIdObn350(entity.getId());
                String numeroOrdemBancaria = pagamento.getNumDocumento();
                listaTodosOrdensBancarias.add(numeroOrdemBancaria);
                atualizarOrdemBancariaComPagamentoTipoDois(arquivo, pagamento, registroObn350TipoDois);
                pagamento = em.merge(pagamento);
                if (!layoutObn350.getRegistrosObn350TipoTres().isEmpty()) {
                    atualizarGuiaPagamentoTipoTres(layoutObn350, pagamento, arquivo);
                }
                if (!layoutObn350.getRegistroObn350TipoQuatro().isEmpty()) {
                    atualizarGuiaPagamentoTipoQuatro(layoutObn350, pagamento, arquivo);
                }
                if (!layoutObn350.getRegistroObn350TipoCinco().isEmpty()) {
                    atualizarGuiaPagamentoTipoCinco(layoutObn350, pagamento, arquivo);
                }
            }
        }
    }

    private void atualizarOrdemBancariaComPagamentoTipoDois(ArquivoOBN350Pagamento arquivo, Pagamento pagamento, RegistroObn350TipoDois registroObn350TipoDois) {
        String numeroOrdemBancaria = pagamento.getNumDocumento();
        if (registroObn350TipoDois.getObteveSucesso()) {
            pagamento.setSaldoFinal(pagamento.getSaldoFinal().subtract(pagamento.getValorFinal()));
            pagamento.setSaldo(pagamento.getSaldo().subtract(pagamento.getValor()));
            if (pagamento.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
                pagamento.setStatus(StatusPagamento.PAGO);
            }
            Date dataDebito = DataUtil.getDateParse(registroObn350TipoDois.getDataReferenciaDaRelacao());
            pagamento.setDataConciliacao(dataDebito);
            borderoFacade.atualizarSituacaoItemBorderoPagamento(pagamento, SituacaoItemBordero.PAGO);
            listaOrdensBancariasParaDebito.add(dataDebito);
            ordensBancariasPagas.add(numeroOrdemBancaria);
        } else {
            indeferirPagamento(arquivo, pagamento, registroObn350TipoDois.getObservacaoOb());
        }
    }

    private void indeferirPagamento(ArquivoOBN350Pagamento arquivo, Pagamento pagamento, String observacao) {
        if (pagamento.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            pagamento.setSaldoFinal(pagamento.getSaldoFinal().add(pagamento.getValorFinal()));
            pagamento.setSaldo(pagamento.getSaldo().add(pagamento.getValor()));
        }
        pagamento.setStatus(StatusPagamento.INDEFERIDO);
        pagamento.setDataConciliacao(null);
        notificarAndAtualizarPagamento(arquivo, pagamento, observacao);
    }

    private void notificarAndAtualizarPagamento(ArquivoOBN350Pagamento arquivo, Pagamento pagamento, String observacao) {
        criarNotificacaoPagamentoOBN350(pagamento, observacao);
        arquivo.setDescricaoErro(observacao);
        borderoFacade.atualizarSituacaoItemBorderoPagamento(pagamento, SituacaoItemBordero.INDEFIRIDO);
        ordensBancariasNaoPagas.add(pagamento.getNumDocumento());
        ordensBancariasPagas.remove(pagamento.getNumDocumento());
    }

    private void atualizarGuiaPagamentoTipoTres(LayoutObn350 layoutObn350, Pagamento pagamento, ArquivoOBN350Pagamento arquivo) {
        List<RegistroObn350TipoTres> registrosTipoTres = getRegistrosOBN350TipoTresPorTipoMovimentoArquivo(layoutObn350, pagamento.getNumeroPagamento(), TipoMovimentoArquivoBancario.PAGAMENTO);
        if (!registrosTipoTres.isEmpty()) {
            for (RegistroObn350TipoTres registroObn350TipoTres : registrosTipoTres) {
                for (GuiaPagamento guiaPagamento : pagamento.getGuiaPagamento()) {
                    String codigoReconhecimento = guiaPagamento.getGuiaGRU().getUgGestao().replace("/", "") + guiaPagamento.getGuiaGRU().getCodigoRecolhimento().replace("-", "");
                    String valorGuia = StringUtil.cortarOuCompletarEsquerda((guiaPagamento.getValorTotalPorGuia().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0");
                    String valorGuiaNoArquivo = registroObn350TipoTres.getValorLiquidoOrdemBacaria();
                    String codigoReconhecimentoNoArquivo = registroObn350TipoTres.getCodigoReconhecimento();
                    if (valorGuia.equals(valorGuiaNoArquivo) && codigoReconhecimento.equals(codigoReconhecimentoNoArquivo)) {
                        if (registroObn350TipoTres.getObteveSucesso()) {
                            guiaPagamento.setNumeroAutenticacao(registroObn350TipoTres.getNumeroAutenticacao());
                            guiaPagamento.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.DEFERIDA);
                        } else {
                            guiaPagamento.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.INDEFERIDA);
                            guiaPagamento.setNumeroAutenticacao(NAO_AUTENTICADA);
                            String observacao = " A Guia GRU retornou com erro. " + registroObn350TipoTres.getObservacaoOb().trim();
                            criarNotificacaoPagamentoOBN350(pagamento, observacao);
                            indeferirPagamento(arquivo, pagamento, observacao);
                        }
                    }
                }

            }
            pagamento = em.merge(pagamento);
        }
    }

    private void atualizarGuiaDespesaExtraTipoTres(LayoutObn350 layoutObn350, PagamentoExtra pagamentoExtra, ArquivoOBN350DespesaExtra arquivo) {
        List<RegistroObn350TipoTres> registrosTipoTres = getRegistrosOBN350TipoTresPorTipoMovimentoArquivo(layoutObn350, pagamentoExtra.getNumeroPagamento(), TipoMovimentoArquivoBancario.PAGAMENTO);
        if (!registrosTipoTres.isEmpty()) {
            for (RegistroObn350TipoTres registroObn350TipoTres : registrosTipoTres) {
                for (GuiaPagamentoExtra guiaPagamentoExtra : pagamentoExtra.getGuiaPagamentoExtras()) {
                    String codigoReconhecimento = guiaPagamentoExtra.getGuiaGRU().getUgGestao().replace("/", "") + guiaPagamentoExtra.getGuiaGRU().getCodigoRecolhimento().replace("-", "");
                    String valorGuia = StringUtil.cortarOuCompletarEsquerda((guiaPagamentoExtra.getValorTotalPorGuia().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0");
                    String valorGuiaNoArquivo = registroObn350TipoTres.getValorLiquidoOrdemBacaria();
                    String codigoReconhecimentoNoArquivo = registroObn350TipoTres.getCodigoReconhecimento();
                    if (valorGuia.equals(valorGuiaNoArquivo) && codigoReconhecimento.equals(codigoReconhecimentoNoArquivo)) {
                        if (registroObn350TipoTres.getObteveSucesso()) {
                            guiaPagamentoExtra.setNumeroAutenticacao(registroObn350TipoTres.getNumeroAutenticacao());
                            guiaPagamentoExtra.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.DEFERIDA);
                        } else {
                            guiaPagamentoExtra.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.INDEFERIDA);
                            guiaPagamentoExtra.setNumeroAutenticacao(NAO_AUTENTICADA);
                            String observacao = " A Guia GRU retornou com erro. " + registroObn350TipoTres.getObservacaoOb().trim();
                            criarNotificacaoPagamentoExtraOBN350(pagamentoExtra, observacao);
                            indeferirPagamentoExtra(arquivo, pagamentoExtra, observacao);
                        }
                    }
                }
            }
            pagamentoExtra = em.merge(pagamentoExtra);
        }
    }

    private void atualizarGuiaPagamentoTipoQuatro(LayoutObn350 layoutObn350, Pagamento pagamento, ArquivoOBN350Pagamento arquivo) {

        List<RegistroObn350TipoQuatro> listaRegistroTipoQuatro = getRegistrosOBN350TipoQuatroPorTipoMovimentoArquivo(layoutObn350, pagamento.getNumeroPagamento(), TipoMovimentoArquivoBancario.PAGAMENTO);
        if (!listaRegistroTipoQuatro.isEmpty()) {

            for (RegistroObn350TipoQuatro registroObn350TipoQuatro : listaRegistroTipoQuatro) {
                for (GuiaPagamento guiaPagamento : pagamento.getGuiaPagamento()) {
                    String codigoBarrasConvenio = "";
                    String codigoBarrasCaixa = "";
                    String codigoBarrasOutrosBancos = "";
                    String valorGuia = StringUtil.cortarOuCompletarEsquerda((guiaPagamento.getValorTotalPorGuia().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0");
                    String valorGuiaNoArquivo = registroObn350TipoQuatro.getValorLiquidoOrdemBacaria();

                    if (TipoDocPagto.FATURA.equals(guiaPagamento.getPagamento().getTipoDocPagto())) {
                        codigoBarrasCaixa = montarCodigoBarrasGuiaFaturaCaixa(guiaPagamento.getGuiaFatura().getCodigoBarra());
                        codigoBarrasOutrosBancos = montarCodigoBarrasGuiaFaturaOutrosBancos(guiaPagamento.getGuiaFatura().getCodigoBarra());
                    } else {
                        if (guiaPagamento.getGuiaConvenio() != null) {
                            codigoBarrasConvenio = montarCodigoBarrasGuiaConvenio(guiaPagamento.getGuiaConvenio().getCodigoBarra());
                        }
                        if (guiaPagamento.getGuiaGRU() != null) {
                            codigoBarrasConvenio = montarCodigoBarrasGuiaConvenio(guiaPagamento.getGuiaGRU().getCodigoBarra());
                        }
                    }
                    if (((TipoDocPagto.FATURA.equals(guiaPagamento.getPagamento().getTipoDocPagto())
                        && (codigoBarrasCaixa.equals(registroObn350TipoQuatro.getCodigoBarras()) || codigoBarrasOutrosBancos.equals(registroObn350TipoQuatro.getCodigoBarras()))) || codigoBarrasConvenio.equals(registroObn350TipoQuatro.getCodigoBarras()))
                        && valorGuia.equals(valorGuiaNoArquivo)) {
                        if (registroObn350TipoQuatro.getObteveSucesso()) {
                            guiaPagamento.setNumeroAutenticacao(registroObn350TipoQuatro.getNumeroAutenticacao());
                            guiaPagamento.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.DEFERIDA);
                        } else {
                            guiaPagamento.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.INDEFERIDA);
                            guiaPagamento.setNumeroAutenticacao(NAO_AUTENTICADA);
                            String observacao = " A Guia do tipo " + registroObn350TipoQuatro.getTipoDeFatura() + " retornou com erro. " + registroObn350TipoQuatro.getObservacaoOb().trim();
                            criarNotificacaoPagamentoOBN350(pagamento, observacao);
                            indeferirPagamento(arquivo, pagamento, observacao);
                        }
                    }
                }
            }
            pagamento = em.merge(pagamento);
        }
    }

    private void atualizarGuiaPagamentoTipoCinco(LayoutObn350 layoutObn350, Pagamento pagamento, ArquivoOBN350Pagamento arquivo) {

        List<RegistroObn350TipoCinco> listaRegistrosTipoCinco = getRegistrosOBN350TipoCincoPorTipoMovimentoArquivo(layoutObn350, pagamento.getNumeroPagamento(), TipoMovimentoArquivoBancario.PAGAMENTO);
        for (RegistroObn350TipoCinco registroObnTipoCinco : listaRegistrosTipoCinco) {

            for (GuiaPagamento guiaPagamento : pagamento.getGuiaPagamento()) {

                String valorGuia = StringUtil.cortarOuCompletarEsquerda((guiaPagamento.getValorTotalPorGuia().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0");
                String valorGuiaNoArquivo = registroObnTipoCinco.getValorFinalGuia();
                String identificadorGuia = getIdentificadorGuia(guiaPagamento);
                String identificadorGuiaNoArquivo = registroObnTipoCinco.getCodigoReceitaTributoGeral();

                if (identificadorGuia.equals(identificadorGuiaNoArquivo) && valorGuia.equals(valorGuiaNoArquivo)) {
                    if (registroObnTipoCinco.getObteveSucesso()) {
                        guiaPagamento.setNumeroAutenticacao(registroObnTipoCinco.getNumeroAutenticacao());
                        guiaPagamento.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.DEFERIDA);

                    } else {
                        guiaPagamento.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.INDEFERIDA);
                        guiaPagamento.setNumeroAutenticacao(NAO_AUTENTICADA);
                        String observacao = " A Guia do tipo " + registroObnTipoCinco.getTipoPagamento() + " retornou com erro, " + registroObnTipoCinco.getObservacaoOb().trim();
                        criarNotificacaoPagamentoOBN350(pagamento, observacao);
                        indeferirPagamento(arquivo, pagamento, observacao);
                    }
                }
            }
        }
        pagamento = em.merge(pagamento);
    }
    //FINALIZA PROCESSO PAGAMENTO


    //INICIA PROCESSO DESPESA EXTRA
    private void salvarDespesaExtra(ArquivoRetornoOBN350 entity, LayoutObn350 layoutObn350) {
        for (ArquivoOBN350DespesaExtra arquivo : entity.getPagamentosExtras()) {
            PagamentoExtra pagamentoextra = arquivo.getPagamentoextra();
            RegistroObn350TipoDois registroObn350TipoDois = getRegistroOBN350TipoDoisPorTipoMovimentoArquivo(layoutObn350, pagamentoextra.getNumero(), TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA);
            if (registroObn350TipoDois != null) {
                arquivo.setValidado(registroObn350TipoDois.getObteveSucesso());
                pagamentoextra.setIdObn350(entity.getId());
                String numeroOrdemBancaria = pagamentoextra.getNumeroPagamento();
                listaTodosOrdensBancarias.add(numeroOrdemBancaria);
                atualizarOrdemBancariaComPagamentoExtraTipoDois(arquivo, pagamentoextra, registroObn350TipoDois);
                pagamentoextra = em.merge(pagamentoextra);
                if (!layoutObn350.getRegistrosObn350TipoTres().isEmpty()) {
                    atualizarGuiaDespesaExtraTipoTres(layoutObn350, pagamentoextra, arquivo);
                }
                if (!layoutObn350.getRegistroObn350TipoQuatro().isEmpty()) {
                    atualizarGuiaPagamentoExtraTipoQuatro(layoutObn350, pagamentoextra, arquivo);
                }
                if (!layoutObn350.getRegistroObn350TipoCinco().isEmpty()) {
                    atualizarGuiaPagamentoExtraTipoCinco(layoutObn350, pagamentoextra, arquivo);
                }
            }
        }
    }

    private void atualizarOrdemBancariaComPagamentoExtraTipoDois(ArquivoOBN350DespesaExtra arquivo, PagamentoExtra pagamentoextra, RegistroObn350TipoDois registroObn350TipoDois) {
        String numeroOrdemBancaria = pagamentoextra.getNumeroPagamento();
        if (registroObn350TipoDois.getObteveSucesso()) {
            if (!StatusPagamento.PAGO.equals(pagamentoextra.getStatus())) {
                pagamentoextra.setStatus(StatusPagamento.PAGO);
                pagamentoextra.setSaldo(pagamentoextra.getSaldo().subtract(pagamentoextra.getValor()));
            }

            Pagamento pagamento = null;
            for (PagamentoReceitaExtra pr : pagamentoextra.getPagamentoReceitaExtras()) {
                if (pr.getReceitaExtra().getRetencaoPgto() != null) {
                    pagamento = pr.getReceitaExtra().getRetencaoPgto().getPagamento();
                    pagamento.setSaldo(pagamento.getSaldo().subtract(pr.getReceitaExtra().getValor()));
                    if (pagamento.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
                        pagamento.setStatus(StatusPagamento.PAGO);
                    }
                    em.merge(pagamento);
                }
            }
            Date dataDebito = DataUtil.getDateParse(registroObn350TipoDois.getDataReferenciaDaRelacao());
            pagamentoextra.setDataConciliacao(dataDebito);
            borderoFacade.atualizarSituacaoItemBorderoPagamentoExtra(pagamentoextra, SituacaoItemBordero.PAGO);
            listaOrdensBancariasParaDebito.add(dataDebito);
            ordensBancariasPagas.add(numeroOrdemBancaria);
        } else {
            indeferirPagamentoExtra(arquivo, pagamentoextra, registroObn350TipoDois.getObservacaoOb());
        }
    }

    private void indeferirPagamentoExtra(ArquivoOBN350DespesaExtra arquivo, PagamentoExtra pagamentoextra, String observacao) {
        if (pagamentoextra.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            pagamentoextra.setSaldo(pagamentoextra.getSaldo().add(pagamentoextra.getValor()));
        }
        pagamentoextra.setStatus(StatusPagamento.INDEFERIDO);
        pagamentoextra.setDataConciliacao(null);
        notificarAndAtualizarPagamentoExtra(arquivo, pagamentoextra, observacao);
    }

    private void notificarAndAtualizarPagamentoExtra(ArquivoOBN350DespesaExtra arquivo, PagamentoExtra pagamentoextra, String observacao) {
        criarNotificacaoPagamentoExtraOBN350(pagamentoextra, observacao);
        arquivo.setDescricaoErro(observacao);
        borderoFacade.atualizarSituacaoItemBorderoPagamentoExtra(pagamentoextra, SituacaoItemBordero.INDEFIRIDO);
        ordensBancariasNaoPagas.add(pagamentoextra.getNumeroPagamento());
    }

    private void atualizarGuiaPagamentoExtraTipoQuatro(LayoutObn350 layoutObn350, PagamentoExtra pagamentoExtra, ArquivoOBN350DespesaExtra arquivo) {

        List<RegistroObn350TipoQuatro> listaRegistroTipoQuatro = getRegistrosOBN350TipoQuatroPorTipoMovimentoArquivo(layoutObn350, pagamentoExtra.getNumero(), TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA);
        if (!listaRegistroTipoQuatro.isEmpty()) {

            for (RegistroObn350TipoQuatro registroObn350TipoQuatro : listaRegistroTipoQuatro) {
                for (GuiaPagamentoExtra guiaPagamentoExtra : pagamentoExtra.getGuiaPagamentoExtras()) {
                    String codigoBarrasConvenio = "";
                    String codigoBarrasCaixa = "";
                    String codigoBarrasOutrosBancos = "";
                    String valorGuia = StringUtil.cortarOuCompletarEsquerda((guiaPagamentoExtra.getValorTotalPorGuia().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0");
                    String valorGuiaNoArquivo = registroObn350TipoQuatro.getValorLiquidoOrdemBacaria();

                    if (TipoDocPagto.FATURA.equals(guiaPagamentoExtra.getPagamentoExtra().getTipoDocumento())) {
                        codigoBarrasCaixa = montarCodigoBarrasGuiaFaturaCaixa(guiaPagamentoExtra.getGuiaFatura().getCodigoBarra());
                        codigoBarrasOutrosBancos = montarCodigoBarrasGuiaFaturaOutrosBancos(guiaPagamentoExtra.getGuiaFatura().getCodigoBarra());
                    } else {
                        if (guiaPagamentoExtra.getGuiaConvenio() != null) {
                            codigoBarrasConvenio = montarCodigoBarrasGuiaConvenio(guiaPagamentoExtra.getGuiaConvenio().getCodigoBarra());
                        }
                        if (guiaPagamentoExtra.getGuiaGRU() != null) {
                            codigoBarrasConvenio = montarCodigoBarrasGuiaConvenio(guiaPagamentoExtra.getGuiaGRU().getCodigoBarra());
                        }
                    }
                    if (((TipoDocPagto.FATURA.equals(guiaPagamentoExtra.getPagamentoExtra().getTipoDocumento()) && (codigoBarrasCaixa.equals(registroObn350TipoQuatro.getCodigoBarras()) || codigoBarrasOutrosBancos.equals(registroObn350TipoQuatro.getCodigoBarras()))) || codigoBarrasConvenio.equals(registroObn350TipoQuatro.getCodigoBarras())) && valorGuia.equals(valorGuiaNoArquivo)) {

                        if (registroObn350TipoQuatro.getObteveSucesso()) {
                            guiaPagamentoExtra.setNumeroAutenticacao(registroObn350TipoQuatro.getNumeroAutenticacao());
                            guiaPagamentoExtra.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.DEFERIDA);
                        } else {
                            guiaPagamentoExtra.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.INDEFERIDA);
                            guiaPagamentoExtra.setNumeroAutenticacao(NAO_AUTENTICADA);
                            String observacao = " A Guia do tipo " + registroObn350TipoQuatro.getTipoDeFatura() + " retornou com erro. " + registroObn350TipoQuatro.getObservacaoOb().trim();
                            criarNotificacaoPagamentoExtraOBN350(pagamentoExtra, observacao);
                            indeferirPagamentoExtra(arquivo, pagamentoExtra, observacao);
                        }
                    }
                }
            }
            pagamentoExtra = em.merge(pagamentoExtra);
        }
    }

    private void atualizarGuiaPagamentoExtraTipoCinco(LayoutObn350 layoutObn350, PagamentoExtra pagamentoExtra, ArquivoOBN350DespesaExtra arquivo) {

        List<RegistroObn350TipoCinco> listaRegistrosTipoCinco = getRegistrosOBN350TipoCincoPorTipoMovimentoArquivo(layoutObn350, pagamentoExtra.getNumero(), TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA);

        for (RegistroObn350TipoCinco registroObnTipoCinco : listaRegistrosTipoCinco) {

            for (GuiaPagamentoExtra guiaPagamentoExtra : pagamentoExtra.getGuiaPagamentoExtras()) {

                String valorGuia = StringUtil.cortarOuCompletarEsquerda((guiaPagamentoExtra.getValorTotalPorGuia().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0");
                String valorGuiaNoArquivo = registroObnTipoCinco.getValorFinalGuia();
                String identificadorGuia = getIdentificadorGuia(guiaPagamentoExtra);
                String identificadorGuiaNoArquivo = registroObnTipoCinco.getCodigoReceitaTributoGeral();

                if (identificadorGuia.equals(identificadorGuiaNoArquivo) && valorGuia.equals(valorGuiaNoArquivo)) {
                    if (registroObnTipoCinco.getObteveSucesso()) {
                        guiaPagamentoExtra.setNumeroAutenticacao(registroObnTipoCinco.getNumeroAutenticacao());
                        guiaPagamentoExtra.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.DEFERIDA);

                    } else {
                        guiaPagamentoExtra.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.INDEFERIDA);
                        guiaPagamentoExtra.setNumeroAutenticacao(NAO_AUTENTICADA);
                        String observacao = " A Guia do tipo " + registroObnTipoCinco.getTipoPagamento() + " retornou com erro, " + registroObnTipoCinco.getObservacaoOb().trim();
                        criarNotificacaoPagamentoExtraOBN350(pagamentoExtra, observacao);
                        indeferirPagamentoExtra(arquivo, pagamentoExtra, observacao);
                    }
                }
            }
        }
        pagamentoExtra = em.merge(pagamentoExtra);
    }

    private String getIdentificadorGuia(Object guia) {
        String codigoTributoGuia = "";
        if (guia instanceof GuiaPagamentoExtra) {
            if (TipoDocPagto.GPS.equals(((GuiaPagamentoExtra) guia).getTipoDocumento())) {
                codigoTributoGuia = ((GuiaPagamentoExtra) guia).getGuiaGPS().getCodigoReceitaTributo();
            }
            if (TipoDocPagto.DARF.equals(((GuiaPagamentoExtra) guia).getTipoDocumento())) {
                codigoTributoGuia = ((GuiaPagamentoExtra) guia).getGuiaDARF().getCodigoReceitaTributo();
            }
            if (TipoDocPagto.DARF_SIMPLES.equals(((GuiaPagamentoExtra) guia).getTipoDocumento())) {
                codigoTributoGuia = ((GuiaPagamentoExtra) guia).getGuiaDARFSimples().getCodigoReceitaTributo();
            }
        } else {
            if (TipoDocPagto.GPS.equals(((GuiaPagamento) guia).getTipoDocumento())) {
                codigoTributoGuia = ((GuiaPagamento) guia).getGuiaGPS().getCodigoReceitaTributo();
            }
            if (TipoDocPagto.DARF.equals(((GuiaPagamento) guia).getTipoDocumento())) {
                codigoTributoGuia = ((GuiaPagamento) guia).getGuiaDARF().getCodigoReceitaTributo();
            }
            if (TipoDocPagto.DARF_SIMPLES.equals(((GuiaPagamento) guia).getTipoDocumento())) {
                codigoTributoGuia = ((GuiaPagamento) guia).getGuiaDARFSimples().getCodigoReceitaTributo();
            }
        }
        return StringUtil.cortarOuCompletarEsquerda(codigoTributoGuia, 6, "0");
    }
//    FINALIZA PROCESSOA DESPESA EXTRA


    //INICIA PROCESSO TRANSFERENCIA FINANCEIRA
    private void salvarTransferenciaFinanceira(ArquivoRetornoOBN350 entity, LayoutObn350 layoutObn350) {
        for (ArquivoOBN350TransfFinanceira arquivo : entity.getTransferenciaContaFinanceiras()) {
            TransferenciaContaFinanceira transferenciaContaFinanceira = arquivo.getTranferenciaconta();
            RegistroObn350TipoDois registroObn350TipoDois = getRegistroOBN350TipoDoisPorTipoMovimentoArquivo(layoutObn350, transferenciaContaFinanceira.getNumero(), TipoMovimentoArquivoBancario.TRANSFERENCIA_FINANCEIRA);
            arquivo.setValidado(registroObn350TipoDois.getObteveSucesso());

            String numeroBordero = registroObn350TipoDois.getNumeroBordero();
            listaTodosOrdensBancarias.add(numeroBordero);
            atualizarTransferenciaFinanceiraEOrdemBancaria(arquivo, transferenciaContaFinanceira, registroObn350TipoDois, numeroBordero);
            em.merge(transferenciaContaFinanceira);
        }
    }

    private void atualizarTransferenciaFinanceiraEOrdemBancaria(ArquivoOBN350TransfFinanceira arquivo, TransferenciaContaFinanceira transferenciaContaFinanceira, RegistroObn350TipoDois registroObn350TipoDois, String numeroBordero) {
        if (registroObn350TipoDois.getObteveSucesso()) {
            transferenciaContaFinanceira.setStatusPagamento(StatusPagamento.PAGO);
            borderoFacade.atualizaSituacaoBorderoItemTransferenciaFinanceira(numeroBordero, transferenciaContaFinanceira.getId(), transferenciaContaFinanceira.getExercicio().getId(), SituacaoItemBordero.PAGO);
            transferenciaContaFinanceira.setSaldo(transferenciaContaFinanceira.getSaldo().subtract(transferenciaContaFinanceira.getValor()));
            Date dataDebito = DataUtil.getDateParse(registroObn350TipoDois.getDataReferenciaDaRelacao());
            transferenciaContaFinanceira.setDataConciliacao(dataDebito);
            transferenciaContaFinanceira.setRecebida(dataDebito);
            listaOrdensBancariasParaDebito.add(dataDebito);
            ordensBancariasPagas.add(numeroBordero);
        } else {
            transferenciaContaFinanceira.setStatusPagamento(StatusPagamento.INDEFERIDO);
            transferenciaContaFinanceira.setDataConciliacao(null);
            transferenciaContaFinanceira.setRecebida(null);
            criarNotificacaoTransferenciaFinanceiraOBN350(transferenciaContaFinanceira, registroObn350TipoDois);
            arquivo.setDescricaoErro(registroObn350TipoDois.getObservacaoOb());
            ordensBancariasNaoPagas.add(numeroBordero);
        }
    }
//      FINALIZA PROCESSO DE TRANSFERENCIA


    //INICIA PROCESSO TRANSFERENCIA MESMA UNIDADE
    private void salvarTransferenciaMesmaUnidade(ArquivoRetornoOBN350 entity, LayoutObn350 layoutObn350) {
        for (ArquivoOBN350TransfMesmaUnid arquivo : entity.getTransferenciaMesmaUnidades()) {
            TransferenciaMesmaUnidade transferenciaMesmaUnidade = arquivo.getTransferenciamesmaund();
            RegistroObn350TipoDois registroObn350TipoDois = getRegistroOBN350TipoDoisPorTipoMovimentoArquivo(layoutObn350, transferenciaMesmaUnidade.getNumero(), TipoMovimentoArquivoBancario.TRANSFERENCIA_MESMA_UNIDADE);
            arquivo.setValidado(registroObn350TipoDois.getObteveSucesso());

            String numeroBordero = registroObn350TipoDois.getNumeroBordero();
            listaTodosOrdensBancarias.add(numeroBordero);
            atualizarTransferenciaMesmaUnidadeEOrdemBancaria(arquivo, transferenciaMesmaUnidade, registroObn350TipoDois, numeroBordero);
            em.merge(transferenciaMesmaUnidade);
        }
    }

    private void atualizarTransferenciaMesmaUnidadeEOrdemBancaria(ArquivoOBN350TransfMesmaUnid arquivo, TransferenciaMesmaUnidade transferenciaMesmaUnidade, RegistroObn350TipoDois registroObn350TipoDois, String numeroBordero) {
        if (registroObn350TipoDois.getObteveSucesso()) {
            transferenciaMesmaUnidade.setStatusPagamento(StatusPagamento.PAGO);
            borderoFacade.atualizaSituacaoBorderoItemTransferenciaMesmaUnidade(numeroBordero, transferenciaMesmaUnidade.getId(), transferenciaMesmaUnidade.getExercicio().getId(), SituacaoItemBordero.PAGO);
            transferenciaMesmaUnidade.setSaldo(transferenciaMesmaUnidade.getSaldo().subtract(transferenciaMesmaUnidade.getValor()));

            Date dataDebito = DataUtil.getDateParse(registroObn350TipoDois.getDataReferenciaDaRelacao());
            transferenciaMesmaUnidade.setDataConciliacao(dataDebito);
            transferenciaMesmaUnidade.setRecebida(dataDebito);
            listaOrdensBancariasParaDebito.add(dataDebito);
            ordensBancariasPagas.add(numeroBordero);
        } else {
            transferenciaMesmaUnidade.setStatusPagamento(StatusPagamento.INDEFERIDO);
            transferenciaMesmaUnidade.setDataConciliacao(null);
            transferenciaMesmaUnidade.setRecebida(null);
            criarNotificacaoTransferenciaMesmaUnidadeOBN350(transferenciaMesmaUnidade, registroObn350TipoDois);
            arquivo.setDescricaoErro(registroObn350TipoDois.getObservacaoOb());
            ordensBancariasNaoPagas.add(numeroBordero);
        }
    }
//    FINALIZA PROCESSO TRANSFERENCIA MESMA UNIDADE


    //INICIA PROCESSO LIBERACAO FINANCEIRA
    private void salvarLiberacaoFinanceira(ArquivoRetornoOBN350 entity, LayoutObn350 layoutObn350) {
        for (ArquivoOBN350LiberacaoFinanc arquivo : entity.getLiberacaoCotaFinanceiras()) {
            LiberacaoCotaFinanceira liberacaoCotaFinanceira = arquivo.getLiberacaofinanceira();
            RegistroObn350TipoDois registroObn350TipoDois = getRegistroOBN350TipoDoisPorTipoMovimentoArquivo(layoutObn350, liberacaoCotaFinanceira.getNumero(), TipoMovimentoArquivoBancario.LIBERACAO);
            arquivo.setValidado(registroObn350TipoDois.getObteveSucesso());

            String numeroBordero = registroObn350TipoDois.getNumeroBordero();
            listaTodosOrdensBancarias.add(numeroBordero);
            atualizarLiberacaoFinanceiraEOrdemBancaria(arquivo, liberacaoCotaFinanceira, registroObn350TipoDois, numeroBordero);
            em.merge(liberacaoCotaFinanceira);
        }
    }

    private void atualizarLiberacaoFinanceiraEOrdemBancaria(ArquivoOBN350LiberacaoFinanc arquivo, LiberacaoCotaFinanceira liberacaoCotaFinanceira, RegistroObn350TipoDois registroObn350TipoDois, String numeroBordero) {
        if (registroObn350TipoDois.getObteveSucesso()) {
            liberacaoCotaFinanceira.setStatusPagamento(StatusPagamento.PAGO);
            borderoFacade.atualizaSituacaoBorderoItemLiberacao(numeroBordero, liberacaoCotaFinanceira.getId(), liberacaoCotaFinanceira.getExercicio().getId(), SituacaoItemBordero.PAGO);
            liberacaoCotaFinanceira.setSaldo(liberacaoCotaFinanceira.getSaldo().subtract(liberacaoCotaFinanceira.getValor()));
            Date dataDebito = DataUtil.getDateParse(registroObn350TipoDois.getDataReferenciaDaRelacao());
            liberacaoCotaFinanceira.setDataConciliacao(dataDebito);
            liberacaoCotaFinanceira.setRecebida(dataDebito);
            listaOrdensBancariasParaDebito.add(dataDebito);
            ordensBancariasPagas.add(numeroBordero);
        } else {
            liberacaoCotaFinanceira.setStatusPagamento(StatusPagamento.INDEFERIDO);
            liberacaoCotaFinanceira.setDataConciliacao(null);
            liberacaoCotaFinanceira.setRecebida(null);
            criarNotificacaoLiberacaoFinanceiraOBN350(liberacaoCotaFinanceira, registroObn350TipoDois);
            arquivo.setDescricaoErro(registroObn350TipoDois.getObservacaoOb());
            ordensBancariasNaoPagas.add(numeroBordero);
        }
    }
//    FINALIZA PROCESSO LIBERACAO FINANCEIRA


    //    NOTIFICAÇÕES
    private void criarNotificacaoPagamentoOBN350(Pagamento pagamento, String observacaoOB) {
        String url;
        if (pagamento.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.NORMAL)) {
            url = "/pagamento/ver/" + pagamento.getId() + "/";
        } else {
            url = "/pagamento/resto-a-pagar/ver/" + pagamento.getId() + "/";
        }
        String mensagem = "O Pagamento Nº " + pagamento.getNumeroPagamento() + "/" + pagamento.getExercicio().getAno();
        salvarNotificacaoOBN350(mensagem, pagamento.getUnidadeOrganizacional(), url, observacaoOB);
    }

    private void criarNotificacaoPagamentoExtraOBN350(PagamentoExtra pagamentoextra, String observacaoOB) {
        PagamentoExtraControlador pagamentoControlador = (PagamentoExtraControlador) Util.getControladorPeloNome("pagamentoExtraControlador");
        String url = pagamentoControlador.getCaminhoPadrao() + "ver/" + pagamentoextra.getId() + "/";
        String mensagem = "A Despesa Extraorçamentária Nº " + pagamentoextra.getNumeroPagamento() + "/" + pagamentoextra.getExercicio().getAno();
        salvarNotificacaoOBN350(mensagem, pagamentoextra.getUnidadeOrganizacional(), url, observacaoOB);
    }

    private void criarNotificacaoTransferenciaFinanceiraOBN350(TransferenciaContaFinanceira transferenciaContaFinanceira, RegistroObn350TipoDois registroObn350TipoDois) {
        TransferenciaContaFinanceiraControlador transferenciaContaFinanceiraControlador = (TransferenciaContaFinanceiraControlador) Util.getControladorPeloNome("transferenciaContaFinanceiraControlador");
        String url = transferenciaContaFinanceiraControlador.getCaminhoPadrao() + "ver/" + transferenciaContaFinanceira.getId() + "/";
        String mensagem = "A Transferência Financeira Nº " + transferenciaContaFinanceira.getNumero() + "/" + transferenciaContaFinanceira.getExercicio().getAno();
        salvarNotificacaoOBN350(mensagem, transferenciaContaFinanceira.getUnidadeOrganizacional(), url, registroObn350TipoDois.getObservacaoOb());
    }

    private void criarNotificacaoTransferenciaMesmaUnidadeOBN350(TransferenciaMesmaUnidade transferenciaMesmaUnidade, RegistroObn350TipoDois registroObn350TipoDois) {
        TransferenciaMesmaUnidadeControlador transferenciaMesmaUnidadeControlador = (TransferenciaMesmaUnidadeControlador) Util.getControladorPeloNome("transferenciaMesmaUnidadeControlador");
        String url = transferenciaMesmaUnidadeControlador.getCaminhoPadrao() + "ver/" + transferenciaMesmaUnidade.getId() + "/";
        String mensagem = "A Transferência Financeira Mesma Unidade Nº " + transferenciaMesmaUnidade.getNumero() + "/" + transferenciaMesmaUnidade.getExercicio().getAno();
        salvarNotificacaoOBN350(mensagem, transferenciaMesmaUnidade.getUnidadeOrganizacional(), url, registroObn350TipoDois.getObservacaoOb());
    }

    private void criarNotificacaoLiberacaoFinanceiraOBN350(LiberacaoCotaFinanceira liberacaoCotaFinanceira, RegistroObn350TipoDois registroObn350TipoDois) {
        LiberacaoCotaFinanceiraControlador liberacaoCotaFinanceiraControlador = (LiberacaoCotaFinanceiraControlador) Util.getControladorPeloNome("liberacaoCotaFinanceiraControlador");
        String url = liberacaoCotaFinanceiraControlador.getCaminhoPadrao() + "ver/" + liberacaoCotaFinanceira.getId() + "/";
        String mensagem = "A Liberação Financeira Nº " + liberacaoCotaFinanceira.getNumero() + "/" + liberacaoCotaFinanceira.getExercicio().getAno();
        salvarNotificacaoOBN350(mensagem, liberacaoCotaFinanceira.getSolicitacaoCotaFinanceira().getUnidadeOrganizacional(), url, registroObn350TipoDois.getObservacaoOb());
    }

    private void salvarNotificacaoOBN350(String mensagem, UnidadeOrganizacional unidadeOrganizacional, String url, String observacaoOB) {
        Notificacao notificacao = new Notificacao();
        notificacao.setTitulo("Arquivo Bancário não Processado");
        notificacao.setDescricao(mensagem + " apresenta uma inconsistência. Detalhes: " + observacaoOB + ". ");
        notificacao.setGravidade(Notificacao.Gravidade.ERRO);
        notificacao.setLink(url);
        notificacao.setTipoNotificacao(TipoNotificacao.IMPORTACAO_ARQUIVO_OBN600);
        notificacao.setUnidadeOrganizacional(unidadeOrganizacional);
        NotificacaoService.getService().notificar(notificacao);
    }
//    FINAL DAS NOTIFICAÇÕES

    private RegistroObn350TipoDois getRegistroOBN350TipoDoisPorTipoMovimentoArquivo(LayoutObn350 layoutObn350, String numero, TipoMovimentoArquivoBancario tipoMovimentoArquivoBancario) {
        for (RegistroObn350TipoDois registroObn350 : layoutObn350.getRegistroObn350TipoDois()) {
            if (registroObn350.getTipoMovimentoArquivoBancario().equals(tipoMovimentoArquivoBancario)) {
                if (registroObn350.getNumeroLancamento().equals(numero)) {
                    return registroObn350;
                }
            }
        }
        return null;
    }

    private RegistroObn350TipoQuatro getRegistroOBN350TipoQuatroPorTipoMovimentoArquivo(LayoutObn350 layoutObn350, String numeroLancamento, TipoMovimentoArquivoBancario tipoMovimentoArquivoBancario) {
        for (RegistroObn350TipoQuatro registroObn350 : layoutObn350.getRegistroObn350TipoQuatro()) {
            if (registroObn350.getTipoMovimentoArquivoBancario().equals(tipoMovimentoArquivoBancario)) {
                if (registroObn350.getNumeroLancamento().equals(numeroLancamento)) {
                    return registroObn350;
                }
            }
        }
        return null;
    }

    private List<RegistroObn350TipoTres> getRegistrosOBN350TipoTresPorTipoMovimentoArquivo(LayoutObn350 layoutObn350, String numeroLancamento, TipoMovimentoArquivoBancario tipoMovimentoArquivoBancario) {
        List<RegistroObn350TipoTres> registrosTipoTres = Lists.newArrayList();
        for (RegistroObn350TipoTres registroObn350 : layoutObn350.getRegistrosObn350TipoTres()) {
            if (registroObn350.getTipoMovimentoArquivoBancario().equals(tipoMovimentoArquivoBancario)) {
                if (registroObn350.getNumeroLancamento().equals(numeroLancamento)) {
                    registrosTipoTres.add(registroObn350);
                }
            }
        }
        return registrosTipoTres;
    }


    private List<RegistroObn350TipoQuatro> getRegistrosOBN350TipoQuatroPorTipoMovimentoArquivo(LayoutObn350 layoutObn350, String numeroLancamento, TipoMovimentoArquivoBancario tipoMovimentoArquivoBancario) {
        List<RegistroObn350TipoQuatro> listaRegistroTipoQuatro = new ArrayList<>();
        for (RegistroObn350TipoQuatro registroObn350 : layoutObn350.getRegistroObn350TipoQuatro()) {
            if (registroObn350.getTipoMovimentoArquivoBancario().equals(tipoMovimentoArquivoBancario)) {
                if (registroObn350.getNumeroLancamento().equals(numeroLancamento)) {
                    listaRegistroTipoQuatro.add(registroObn350);
                }
            }
        }
        return listaRegistroTipoQuatro;
    }


    private RegistroObn350TipoCinco getRegistroOBN350TipoCincoPorTipoMovimentoArquivo(LayoutObn350 layoutObn350, String numero, TipoMovimentoArquivoBancario tipoMovimentoArquivoBancario) {

        for (RegistroObn350TipoCinco registroObn350 : layoutObn350.getRegistroObn350TipoCinco()) {
            if (registroObn350.getTipoMovimentoArquivoBancario().equals(tipoMovimentoArquivoBancario)) {
                if (registroObn350.getNumeroLancamento().equals(numero)) {
                    return registroObn350;
                }
            }
        }
        return null;
    }

    private List<RegistroObn350TipoCinco> getRegistrosOBN350TipoCincoPorTipoMovimentoArquivo(LayoutObn350 layoutObn350, String numero, TipoMovimentoArquivoBancario tipoMovimentoArquivoBancario) {
        List<RegistroObn350TipoCinco> listaRegistroTipoCinco = new ArrayList<>();

        for (RegistroObn350TipoCinco registroObn350 : layoutObn350.getRegistroObn350TipoCinco()) {
            if (registroObn350.getTipoMovimentoArquivoBancario().equals(tipoMovimentoArquivoBancario)) {
                if (registroObn350.getNumeroLancamento().equals(numero)) {
                    listaRegistroTipoCinco.add(registroObn350);
                }
            }
        }
        return listaRegistroTipoCinco;
    }

    private List<Pagamento> buscarPagamentoPorNumero(String ids) {
        try {
            String sql = "select " + "      p.* " + " from " + "   pagamento p " + " inner join exercicio ex on ex.id = p.exercicio_id" + " where p.numeropagamento in (" + ids.replace("'", "") + ")" + " and ex.ano= to_number(:ano)";
            Query q = em.createNativeQuery(sql, Pagamento.class);
            q.setParameter("ano", ano);

            List resultList = q.getResultList();
            return resultList;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar \"Pagamentos\" do arquivo bancário " + ex.getMessage());
        }
    }

    private List<PagamentoExtra> buscarPagamentoExtraPorNumero(String ids) {
        try {
            String sql = "  select " + "      p.* " + " from pagamentoextra p inner join exercicio ex on ex.id  = p.exercicio_id   " + " where p.numero in(" + ids.replace("'", "") + ")" + " and ex.ano= :ano";
            Query q = em.createNativeQuery(sql, PagamentoExtra.class);
            q.setParameter("ano", ano);

            return q.getResultList();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar \"Despesas Extras\" do arquivo bancário " + ex.getMessage());
        }
    }

    private List<TransferenciaMesmaUnidade> buscarTransferenciaMesmaUnidadePorNumero(String ids) {
        try {
            String sql = " select transf.* " + " from transferenciamesmaunidade transf " + " inner join exercicio ex on ex.id  = transf.exercicio_id  " + " where transf.numero in(" + ids.replace("'", "") + ") " + " and ex.ano= :ano ";
            Query q = em.createNativeQuery(sql, TransferenciaMesmaUnidade.class);
            q.setParameter("ano", ano);

            return q.getResultList();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar \"Tranferencia /Financeira Mesma Unidade\" do arquivo bancário " + ex.getMessage());
        }
    }

    private List<LiberacaoCotaFinanceira> buscarLiberacaoFinanceiraPorNumero(String ids) {
        try {
            String sql = " select lib.* " + " from LiberacaoCotaFinanceira lib " + " inner join exercicio ex on ex.id  = lib.exercicio_id  " + " where lib.numero in(" + ids.replace("'", "") + ")" + " and ex.ano = :ano";
            Query q = em.createNativeQuery(sql, LiberacaoCotaFinanceira.class);
            q.setParameter("ano", ano);

            return q.getResultList();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar \"Liberação Financeira\" do arquivo bancário " + ex.getMessage());
        }
    }

    private List<TransferenciaContaFinanceira> buscarTransferenciaFinanceiraPorNumero(String ids) {
        try {
            String sql = " select transf.* " + " from transferenciacontafinanc transf " + " inner join exercicio ex on ex.id  = transf.exercicio_id  " + " where transf.numero in (" + ids.replace("'", "") + ")" + " and ex.ano= :ano";
            Query q = em.createNativeQuery(sql, TransferenciaContaFinanceira.class);
            q.setParameter("ano", ano);

            return q.getResultList();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar \"Tranferência Financeira\" do arquivo bancário " + ex.getMessage());
        }
    }

    public Object recuperararLancamentosBorderoNumerosString(TipoLancamentoEmBordero tipoLancamentoEmBordero, Exercicio ex, String nmrBordero, String nmrLanc) {
        return borderoFacade.recuperaLacamentoDoBorderoNumeroString(tipoLancamentoEmBordero, ex, nmrBordero, nmrLanc);
    }

    public String getUltimoNumero() {
        String sql = " SELECT MAX(TO_NUMBER(A.SEQUENCIALARQUIVO)) + 1 AS NUMERO " + "      FROM ARQUIVORETORNOOBN350 A";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public boolean validarArquivoComMesmoSequencial(ArquivoRetornoOBN350 arquivoRetornoOBN350, Arquivo arquivo) {
        String sql = " select arq.* from arquivoretornoobn350 arq " + "      inner join arquivo a on a.id = arq.arquivo_id " + "        where arq.sequencialarquivo = :codigo and arq.exercicio_id = :exercicio ";
        if (arquivoRetornoOBN350.getId() != null) {
            sql += "     and arq.id <> :id ";
        }
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("codigo", arquivoRetornoOBN350.getSequencialArquivo());
        consulta.setParameter("exercicio", arquivoRetornoOBN350.getExercicio().getId());

        if (arquivoRetornoOBN350.getId() != null) {
            consulta.setParameter("id", arquivoRetornoOBN350.getId());
        }
        if (consulta.getResultList().isEmpty()) {
            return true;
        }
        throw new ExcecaoNegocioGenerica("O Arquivo OBN350 (" + arquivo.getDescricao() + ") Nº <b>" + arquivoRetornoOBN350.getSequencialArquivo() + "</b> já foi baixado no sistema.");
    }


    private void atualizarSituacaoBordero() {
        for (String numeroBordero : listaTodosOrdensBancarias) {
            if (!ordensBancariasNaoPagas.contains(numeroBordero)) {
                for (Date dataDebito : listaOrdensBancariasParaDebito) {
                    borderoFacade.atualizarSituacaoBordero(numeroBordero, UtilRH.getExercicio().getId(), SituacaoBordero.PAGO, dataDebito);
                }
            } else {
                borderoFacade.atualizarSituacaoBorderoIndeferido(numeroBordero, UtilRH.getExercicio().getId());
            }
        }
    }


    private String montarCodigoBarrasGuiaConvenio(String codigoBarra) {
        String retorno = "";
        codigoBarra = codigoBarra.replace(" ", "");
        retorno += codigoBarra.substring(0, 11);
        retorno += codigoBarra.substring(13, 24);
        retorno += codigoBarra.substring(26, 37);
        retorno += codigoBarra.substring(39, 50);
        return retorno;
    }

    private String montarCodigoBarrasGuiaFaturaCaixa(String codigoBarra) {
        String retorno = "";
        codigoBarra = codigoBarra.replace(" ", "");
        codigoBarra = codigoBarra.replace(".", "");
        retorno += codigoBarra.substring(0, 4);
        retorno += codigoBarra.substring(32, codigoBarra.length());
        return retorno;
    }

    private String montarCodigoBarrasGuiaFaturaOutrosBancos(String codigoBarra) {
        String retorno = "";
        codigoBarra = codigoBarra.replace(" ", "");
        codigoBarra = codigoBarra.replace(".", "");
        if (codigoBarra.length() != 44) {
            retorno += codigoBarra.substring(0, 4);
            retorno += codigoBarra.substring(32, codigoBarra.length());
            retorno += codigoBarra.substring(4, 9);
            retorno += codigoBarra.substring(10, 20);
            retorno += codigoBarra.substring(21, 31);
        } else {
            retorno = codigoBarra;
        }
        return retorno;
    }


    public void reprocessarGuias(ArquivoRetornoOBN350 entity, LayoutObn350 layoutObn350) {
        entity = em.find(ArquivoRetornoOBN350.class, entity.getId());
        entity.getPagamentos().size();
        entity.getPagamentosExtras().size();
        for (ArquivoOBN350Pagamento arquivo : entity.getPagamentos()) {
            Pagamento pagamento = arquivo.getPagamento();
            if (!layoutObn350.getRegistrosObn350TipoTres().isEmpty()) {
                atualizarGuiaPagamentoTipoTres(layoutObn350, pagamento, arquivo);
            }
            if (!layoutObn350.getRegistroObn350TipoQuatro().isEmpty()) {
                atualizarGuiaPagamentoTipoQuatro(layoutObn350, pagamento, arquivo);
            }
        }
        for (ArquivoOBN350DespesaExtra arquivo : entity.getPagamentosExtras()) {
            PagamentoExtra pagamentoextra = arquivo.getPagamentoextra();
            if (!layoutObn350.getRegistrosObn350TipoTres().isEmpty()) {
                atualizarGuiaDespesaExtraTipoTres(layoutObn350, pagamentoextra, arquivo);
            }
            if (!layoutObn350.getRegistroObn350TipoQuatro().isEmpty()) {
                atualizarGuiaPagamentoExtraTipoQuatro(layoutObn350, pagamentoextra, arquivo);
            }
        }
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }
}
