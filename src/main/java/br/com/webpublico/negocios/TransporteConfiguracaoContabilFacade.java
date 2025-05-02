package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteTransporteConfiguracaoContabil;
import br.com.webpublico.entidadesauxiliares.TransporteConfiguracaoCLPLCP;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.enums.ClasseDaConta;
import br.com.webpublico.enums.EntidadeOCC;
import br.com.webpublico.enums.SituacaoConta;
import br.com.webpublico.enums.TipoConfiguracaoContabil;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by HardRock on 26/12/2016.
 */
@Stateless
public class TransporteConfiguracaoContabilFacade extends AbstractFacade<TransporteConfiguracaoContabil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TransporteConfiguracaoContabilFacade() {
        super(TransporteConfiguracaoContabil.class);
    }

    @EJB
    private ConfigEmpenhoFacade configEmpenhoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private CLPFacade clpFacade;
    @EJB
    private ItemEventoCLPFacade itemEventoCLPFacade;
    @EJB
    private PlanoDeContasFacade planoDeContasFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private AberturaFechamentoExercicioFacade aberturaFechamentoExercicioFacade;
    @EJB
    private ConfigReceitaRealizadaFacade configReceitaRealizadaFacade;
    @EJB
    private ConfigCreditoReceberFacade configCreditoReceberFacade;
    @EJB
    private ConfigDividaAtivaContabilFacade configDividaAtivaContabilFacade;
    @EJB
    private OrigemOCCFacade origemOCCFacade;

    @Override
    public TransporteConfiguracaoContabil recuperar(Object id) {
        TransporteConfiguracaoContabil obj = super.recuperar(id);
        if (obj != null) {
            obj.getTransporteConfiguracoesOCCs().size();
            obj.getTransporteConfiguracoesCDEs().size();
            obj.getTransporteConfiguracoesCLPs().size();
            obj.getTransporteConfiguracoesLCPs().size();
            obj.getTransporteConfiguracoesGruposPatrimoniais().size();
            obj.getTransporteConfiguracoesGruposMateriais().size();
            obj.getTiposConfiguracoesContabeis().size();
            obj.getTransporteConfiguracoesTipoViagem().size();
            obj.getTransporteConfiguracoesTipoPessoa().size();
            obj.getTransporteConfiguracoesTiposObjetoCompra().size();
            obj.getTransporteConfiguracoesCLPLCPs().size();
            obj.getTransporteGruposOrcamentarios().size();
        }
        return obj;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteTransporteConfiguracaoContabil> transportarConfiguracao(AssistenteTransporteConfiguracaoContabil assistente) {
        AsyncResult<AssistenteTransporteConfiguracaoContabil> result = new AsyncResult<>(assistente);
        TransporteConfiguracaoContabil selecionado = assistente.getTransporteConfiguracaoContabil();
        assistente.getAssistenteBarraProgresso().addMensagem("TRANSPORTANDO CONFIGURAÇÕES DE " + Util.getAnoDaData(selecionado.getDataTransporte()) + " PARA " + selecionado.getExercicioDestino());
        transportar(selecionado, assistente);

        assistente.getAssistenteBarraProgresso().addMensagem("GERANDO RELATÓRIO DE ERROS");
        selecionado = em.merge(selecionado);

        montarMapaLog(assistente, selecionado);
        assistente.getAssistenteBarraProgresso().addMensagem("PROCESSO FINALIZADO");
        return result;
    }

    private void transportar(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistente) {

        for (TransporteTipoConfiguracaoContabil tipoConfiguracao : selecionado.getTiposConfiguracoesContabeis()) {
            switch (tipoConfiguracao.getTipoConfiguracaoContabil()) {
                case CLP_LCP:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CLP_LCP);
                    transportarConfiguracaoCLPItemEventoCLPAndLCP(selecionado, assistente);
                    break;
                case UNIDADE_FONTE_CONTA_FINANCEIRA:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.UNIDADE_FONTE_CONTA_FINANCEIRA);
                    transportarUnidadeAndFonteContaFinanceira(selecionado, assistente);
                    break;
                case UNIDADE_GESTORA:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.UNIDADE_GESTORA);
                    transportarUnidadeGestora(selecionado, assistente);
                    break;
                case CDE_OBRIGACAO_PAGAR:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_OBRIGACAO_PAGAR);
                    transportarCDEObrigacaoAPagar(selecionado, assistente, false);
                    break;
                case CDE_EMPENHO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_EMPENHO);
                    transportarCDEEmpenho(selecionado, assistente, false);
                    break;
                case CDE_EMPENHO_RESTO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_EMPENHO_RESTO);
                    transportarCDEEmpenhoResto(selecionado, assistente, false);
                    break;
                case CDE_CANCELAMENTO_RESTO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_CANCELAMENTO_RESTO);
                    transportarCDECancelamentoResto(selecionado, assistente, false);
                    break;
                case CDE_LIQUIDACAO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_LIQUIDACAO);
                    transportarCDELiquidacao(selecionado, assistente, false);
                    break;
                case CDE_LIQUIDACAO_RESTO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_LIQUIDACAO_RESTO);
                    transportarCDELiquidacaoResto(selecionado, assistente, false);
                    break;
                case CDE_PAGAMENTO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_PAGAMENTO);
                    transportarCDEPagamento(selecionado, assistente, false);
                    break;
                case CDE_PAGAMENTO_RESTO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_PAGAMENTO_RESTO);
                    transportarCDEPagamentoResto(selecionado, assistente, false);
                    break;
                case CDE_RECEITA_REALIZADA:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_RECEITA_REALIZADA);
                    transportarCDEReceitaRealizada(selecionado, assistente, false);
                    break;
                case CDE_CREDITO_RECEBER:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_CREDITO_RECEBER);
                    transportarCDECreditoReceber(selecionado, assistente, false);
                    break;
                case CDE_DIVIDA_ATIVA:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_DIVIDA_ATIVA);
                    transportarCDEDividaAtiva(selecionado, assistente, false);
                    break;
                case CDE_INVESTIMENTO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_INVESTIMENTO);
                    transportarCDEInvestimento(selecionado, assistente, false);
                    break;
                case CDE_PATRIMONIO_LIQUIDO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CDE_PATRIMONIO_LIQUIDO);
                    transportarCDEPatrimonioLiquido(selecionado, assistente, false);
                    break;
                case OCC_CONTA:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.OCC_CONTA);
                    transportarConfiguracaoOCCConta(selecionado, assistente, false);
                    break;
                case OCC_BANCO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.OCC_BANCO);
                    transportarConfiguracaoOCCBanco(selecionado, assistente, false);
                    break;
                case OCC_CLASSE_CREDOR:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.OCC_CLASSE_CREDOR);
                    transportarConfiguracaoOCCClassePessoa(selecionado, assistente, false);
                    break;
                case OCC_GRUPO_PATRIMONIAL:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.OCC_GRUPO_PATRIMONIAL);
                    transportarConfiguracaoOCCGrupoPatrimonial(selecionado, assistente, false);
                    break;
                case OCC_GRUPO_MATERIAL:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.OCC_GRUPO_MATERIAL);
                    transportarConfiguracaoOCCGrupoMaterial(selecionado, assistente, false);
                    break;
                case OCC_NATUREZA_DIVIDA_PUBLICA:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.OCC_NATUREZA_DIVIDA_PUBLICA);
                    transportarConfiguracaoOCCNaturezaDividaPublica(selecionado, assistente, false);
                    break;
                case OCC_TIPO_PASSIVO_ATUARIAL:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.OCC_TIPO_PASSIVO_ATUARIAL);
                    transportarConfiguracaoOCCTipoPassivoAtuarial(selecionado, assistente, false);
                    break;
                case OCC_ORIGEM_RECURSO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.OCC_ORIGEM_RECURSO);
                    transportarConfiguracaoOCCOrigemRecurso(selecionado, assistente, false);
                    break;
                case CONFIGURACAO_CONTA_DESPESA_GRUPO_PATRIMONIAL:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CONFIGURACAO_CONTA_DESPESA_GRUPO_PATRIMONIAL);
                    transportarConfiguracaoContaDespesaGrupoPatrimonial(selecionado, assistente, false);
                    break;
                case CONFIGURACAO_CONTA_DESPESA_GRUPO_MATERIAL:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CONFIGURACAO_CONTA_DESPESA_GRUPO_MATERIAL);
                    transportarConfiguracaoContaDespesaGrupoMaterial(selecionado, assistente, false);
                    break;
                case CONFIGURACAO_CONTA_DESPESA_TIPO_VIAGEM:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CONFIGURACAO_CONTA_DESPESA_TIPO_VIAGEM);
                    transportarConfiguracaoContaDespesaTipoViagem(selecionado, assistente, false);
                    break;
                case CONFIGURACAO_CONTA_DESPESA_TIPO_PESSOA:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CONFIGURACAO_CONTA_DESPESA_TIPO_PESSOA);
                    transportarConfiguracaoContaDespesaTipoPessoa(selecionado, assistente, false);
                    break;
                case CONFIGURACAO_CONTA_DESPESA_TIPO_OBJETO_COMPRA:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.CONFIGURACAO_CONTA_DESPESA_TIPO_OBJETO_COMPRA);
                    transportarConfiguracaoContaDespesaTipoObjetoCompra(selecionado, assistente, false);
                    break;
                case GRUPO_ORCAMENTARIO:
                    assistente.setTipoConfiguracaoContabil(TipoConfiguracaoContabil.GRUPO_ORCAMENTARIO);
                    transportarGruposOrcamentarios(selecionado, assistente, false);
                    break;
            }
        }
    }

    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 30)
    public List<TransporteConfiguracaoCLPLCP> simularTransporteCLPItemEventoCLPAndLCP(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracoesCLP(selecionado, assistenteTransporte, true);
    }

    private List<TransporteConfiguracaoCLPLCP> transportarConfiguracoesCLP(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<TransporteConfiguracaoCLPLCP> configuracoes = buscarCLP(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());

        List<CLP> clps = new ArrayList<>();
        List<LCP> lcps = Lists.newArrayList();
        for (TransporteConfiguracaoCLPLCP obj : configuracoes) {
            try {
                transportarCLPItemEventoCLPAndLCP(selecionado, obj, assistenteTransporte, clps, lcps, isSimulacao);
            } catch (ExcecaoNegocioGenerica e) {
                assistente.addMensagem(e.getMessage());
                obj.setErroDuranteProcessamento(true);
                obj.setMensagemDeErro(e.getMessage());
                continue;
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void transportarConfiguracaoCLPItemEventoCLPAndLCP(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        transportarConfiguracoesCLP(selecionado, assistenteTransporte, false);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void transportarCLPItemEventoCLPAndLCP(TransporteConfiguracaoContabil selecionado, TransporteConfiguracaoCLPLCP obj, AssistenteTransporteConfiguracaoContabil assistenteTranposrte, List<CLP> clps, List<LCP> lcps, boolean isSimulacao) {
        CLP novaCLP = transportarCLP(selecionado, obj, assistenteTranposrte.getTipoConfiguracaoContabil(), clps, isSimulacao);
        ItemEventoCLP novoItemEventoCLP = transportarItemEventoCLP(selecionado, obj, assistenteTranposrte.getTipoConfiguracaoContabil(), novaCLP, isSimulacao);
        transportarLCP(selecionado, obj, novaCLP, assistenteTranposrte.getTipoConfiguracaoContabil(), lcps);
        if (!isSimulacao) {
            novaCLP = (CLP) salvarObjeto(novaCLP);
        }
        Util.adicionarObjetoEmLista(clps, novaCLP);
        novoItemEventoCLP.setClp(novaCLP);
        if (!isSimulacao) {
            salvarObjeto(novoItemEventoCLP);
        }
    }

    private void transportarLCP(TransporteConfiguracaoContabil selecionado, TransporteConfiguracaoCLPLCP obj, CLP novaCLP, TipoConfiguracaoContabil tipoConfiguracao, List<LCP> lcps) {
        for (LCP lcpOrigem : obj.getClp().getLcps()) {
            try {
                boolean hasLcpAdicionada = false;
                for (LCP lcp : lcps) {
                    if (lcp.getClp().getCodigo().equals(novaCLP.getCodigo()) &&
                        lcp.getClp().getExercicio().equals(selecionado.getExercicioDestino()) &&
                        lcp.getCodigo().equals(lcpOrigem.getCodigo())) {
                        hasLcpAdicionada = true;
                    }
                }
                if (!hasLcpAdicionada) {
                    LCP novaLCP = (LCP) Util.clonarEmNiveis(lcpOrigem, 1);
                    novaLCP.setId(null);
                    novaLCP.setClp(novaCLP);

                    novaLCP.setContaCredito(getContaContabil(lcpOrigem.getContaCredito(), selecionado, tipoConfiguracao, lcpOrigem));
                    novaLCP.setContaCreditoInterEstado(getContaContabil(lcpOrigem.getContaCreditoInterEstado(), selecionado, tipoConfiguracao, lcpOrigem));
                    novaLCP.setContaCreditoInterMunicipal(getContaContabil(lcpOrigem.getContaCreditoInterMunicipal(), selecionado, tipoConfiguracao, lcpOrigem));
                    novaLCP.setContaCreditoInterUniao(getContaContabil(lcpOrigem.getContaCreditoInterUniao(), selecionado, tipoConfiguracao, lcpOrigem));
                    novaLCP.setContaCreditoIntra(getContaContabil(lcpOrigem.getContaCreditoIntra(), selecionado, tipoConfiguracao, lcpOrigem));

                    novaLCP.setContaDebito(getContaContabil(lcpOrigem.getContaDebito(), selecionado, tipoConfiguracao, lcpOrigem));
                    novaLCP.setContaDebitoInterEstado(getContaContabil(lcpOrigem.getContaDebitoInterEstado(), selecionado, tipoConfiguracao, lcpOrigem));
                    novaLCP.setContaDebitoInterMunicipal(getContaContabil(lcpOrigem.getContaDebitoInterMunicipal(), selecionado, tipoConfiguracao, lcpOrigem));
                    novaLCP.setContaDebitoInterUniao(getContaContabil(lcpOrigem.getContaDebitoInterUniao(), selecionado, tipoConfiguracao, lcpOrigem));
                    novaLCP.setContaDebitoIntra(getContaContabil(lcpOrigem.getContaDebitoIntra(), selecionado, tipoConfiguracao, lcpOrigem));
                    lcps.add(novaLCP);
                    novaCLP.getLcps().add(novaLCP);
                }
            } catch (Exception e) {
                obj.setMensagemDeErro("LCP: " + lcpOrigem + " " + e.getMessage());
                selecionado.getTransporteConfiguracoesLCPs().add
                    (new TransporteConfiguracaoLCP(tipoConfiguracao, selecionado, lcpOrigem, "LCP: " + lcpOrigem + " " + e.getMessage()));
                throw new ExcecaoNegocioGenerica(e.getMessage());
            }
        }
    }

    private CLP transportarCLP(TransporteConfiguracaoContabil selecionado, TransporteConfiguracaoCLPLCP obj, TipoConfiguracaoContabil tipoConfiguracao, List<CLP> clps, boolean isSimulacao) {
        try {
            if (!isSimulacao) {
                encerrarVigenciaCLP(selecionado.getDataTransporte(), obj);
            }
            return transportarCLP(selecionado, getInicioVigencia(selecionado), obj, clps);
        } catch (Exception e) {
            obj.setMensagemDeErro("CLP: " + obj.getClp() + " " + e.getMessage());
            selecionado.getTransporteConfiguracoesCLPs().add(
                new TransporteConfiguracaoCLP(tipoConfiguracao, selecionado, obj.getClp(), "CLP: " + obj.getClp() + " " + e.getMessage()));
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private ItemEventoCLP transportarItemEventoCLP(TransporteConfiguracaoContabil selecionado, TransporteConfiguracaoCLPLCP obj, TipoConfiguracaoContabil tipoConfiguracao, CLP novaCLP, boolean isSimulacao) {
        try {
            return transportarItemEventoCLP(selecionado.getDataTransporte(), obj, novaCLP, isSimulacao);
        } catch (Exception e) {
            obj.setMensagemDeErro("CLP: " + obj.getClp() + " " + e.getMessage());
            selecionado.getTransporteConfiguracoesCLPs().add(new TransporteConfiguracaoCLP(
                tipoConfiguracao, selecionado, obj.getClp(), "CLP: " + obj.getClp() + " " + e.getMessage()));
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void encerrarVigenciaCLP(Date finalVigencia, TransporteConfiguracaoCLPLCP obj) {
        obj.getClp().setFimVigencia(finalVigencia);
        obj.setClp((CLP) salvarObjeto(obj.getClp()));
    }

    private CLP transportarCLP(TransporteConfiguracaoContabil selecionado, Date inicioVigencia, TransporteConfiguracaoCLPLCP obj, List<CLP> clps) {
        try {
            for (CLP clp : clps) {
                if (clp.getExercicio().equals(selecionado.getExercicioDestino()) && clp.getCodigo().equals(obj.getClp().getCodigo())) {
                    return clp;
                }
            }
            CLP novaCLP = (CLP) Util.clonarEmNiveis(obj.getClp(), 1);
            novaCLP.setId(null);
            novaCLP.setDescricao(obj.getClp().getNome());
            novaCLP.setInicioVigencia(inicioVigencia);
            novaCLP.setFimVigencia(null);
            novaCLP.setExercicio(selecionado.getExercicioDestino());
            novaCLP.setLcps(new ArrayList<LCP>());
            return novaCLP;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao transportar CLP.");
        }
    }

    private ItemEventoCLP transportarItemEventoCLP(Date finalVigencia, TransporteConfiguracaoCLPLCP obj, CLP novaCLP, boolean isSimulacao) {
        if (obj.getItemEventoCLP() != null) {
            obj.getItemEventoCLP().setDataVigencia(finalVigencia);
            if (!isSimulacao) {
                obj.setItemEventoCLP((ItemEventoCLP) salvarObjeto(obj.getItemEventoCLP()));
            }

            ItemEventoCLP novoItemEventoCLP = (ItemEventoCLP) Util.clonarEmNiveis(obj.getItemEventoCLP(), 1);
            novoItemEventoCLP.setId(null);
            novoItemEventoCLP.setClp(novaCLP);
            novoItemEventoCLP.setDataVigencia(null);
            return novoItemEventoCLP;
        }
        throw new ExcecaoNegocioGenerica("CLP não encontrada para gerar o item evento clp.");
    }

    public List<TransporteSaldoFinanceiro> simularTransporteUnidadesContaFinanceira(TransporteConfiguracaoContabil selecionado,
                                                                                    AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        List<TransporteSaldoFinanceiro> unidadesDaConta = buscarUnidadesDaContaFinanceira(selecionado.getDataTransporte());
        for (TransporteSaldoFinanceiro transporteSaldoFinanceiro : unidadesDaConta) {
            movimentarUnidadeSubConta(selecionado.getExercicioDestino(), transporteSaldoFinanceiro, assistenteTransporte, true);
        }
        return unidadesDaConta;
    }

    public List<TransporteSaldoFinanceiro> simularTransporteFontesContaFinanceira(TransporteConfiguracaoContabil selecionado,
                                                                                  AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        List<TransporteSaldoFinanceiro> fontesDaConta = buscarFontesDaContaFinanceira(selecionado.getDataTransporte());
        for (TransporteSaldoFinanceiro transporteSaldoFinanceiro : fontesDaConta) {
            movimentarFonteSubConta(selecionado.getExercicioDestino(), transporteSaldoFinanceiro, assistenteTransporte, true);
        }
        return fontesDaConta;
    }

    private void transportarUnidadeAndFonteContaFinanceira(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<TransporteSaldoFinanceiro> unidadesDaConta = buscarUnidadesDaContaFinanceira(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, unidadesDaConta.size());

        List<TransporteSaldoFinanceiro> fontesDaConta = buscarFontesDaContaFinanceira(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, unidadesDaConta.size() + fontesDaConta.size());

        for (TransporteSaldoFinanceiro transporteSaldoFinanceiro : unidadesDaConta) {
            movimentarUnidadeSubConta(selecionado.getExercicioDestino(), transporteSaldoFinanceiro, assistenteTransporte, false);
            contarRegistroProcessado(assistente);
        }
        for (TransporteSaldoFinanceiro transporteSaldoFinanceiro : fontesDaConta) {
            movimentarFonteSubConta(selecionado.getExercicioDestino(), transporteSaldoFinanceiro, assistenteTransporte, false);
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
    }

    private void transportarUnidadeGestora(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<UnidadeGestora> unidadesGestoras = buscarUnidadesGestoras(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, unidadesGestoras.size());

        for (UnidadeGestora unidadeGestora : unidadesGestoras) {
            movimentarUnidadeGestora(selecionado.getExercicioDestino(), unidadeGestora);
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
    }

    private void contarRegistroProcessado(BarraProgressoAssistente assistente) {
        assistente.setProcessados(assistente.getProcessados() + 1);
    }

    private void movimentarUnidadeSubConta(Exercicio proximoExercicio, TransporteSaldoFinanceiro transporteSaldoFinanceiro, AssistenteTransporteConfiguracaoContabil assistente, boolean isSimulacao) {
        SubConta conta = transporteSaldoFinanceiro.getConta();
        UnidadeOrganizacional unidadeOrganizacional = transporteSaldoFinanceiro.getUnidadeOrganizacional();
        if (conta.verificarUnidadeExistente(unidadeOrganizacional, proximoExercicio)) {
            SubContaUniOrg subContaUniOrg = new SubContaUniOrg();
            subContaUniOrg.setSubConta(conta);
            subContaUniOrg.setUnidadeOrganizacional(unidadeOrganizacional);
            subContaUniOrg.setExercicio(proximoExercicio);
            if (!isSimulacao) {
                em.persist(subContaUniOrg);
            }
        } else {
            String conteudo = "Já existe uma unidade : " + unidadeOrganizacional.getDescricao() + " para a conta " + conta.getCodigo() + " para o  exercício de " + assistente.getExercicio().getAno() + ".";
            assistente.getAssistenteBarraProgresso().addMensagem(conteudo);
            transporteSaldoFinanceiro.setErroDuranteProcessamento(true);
            transporteSaldoFinanceiro.setMensagemDeErro(conteudo);
        }
    }

    private void movimentarFonteSubConta(Exercicio exercicioDestino, TransporteSaldoFinanceiro transporteSaldoFinanceiro, AssistenteTransporteConfiguracaoContabil assistente, boolean isSimulacao) {
        SubContaFonteRec subContaFonteRec = new SubContaFonteRec();
        subContaFonteRec.setSubConta(transporteSaldoFinanceiro.getConta());
        Conta contaDeDestinacao = buscarContaDeDestinacao(transporteSaldoFinanceiro.getContaDeDestinacao(), exercicioDestino);
        if (!hasContaDeDestinacao(assistente, transporteSaldoFinanceiro.getConta(), transporteSaldoFinanceiro.getContaDeDestinacao(), contaDeDestinacao)) {
            subContaFonteRec.setContaDeDestinacao((ContaDeDestinacao) contaDeDestinacao);
            subContaFonteRec.setFonteDeRecursos(((ContaDeDestinacao) contaDeDestinacao).getFonteDeRecursos());
            if (!isSimulacao) {
                em.persist(subContaFonteRec);
            }
        } else {
            String conteudo = "Já existe uma conta de destinação com o código: " + transporteSaldoFinanceiro.getContaDeDestinacao().getCodigo() + " para a conta " + transporteSaldoFinanceiro.getConta().getCodigo() + " para o  exercício de " + assistente.getExercicio().getAno() + ".";
            assistente.getAssistenteBarraProgresso().addMensagem(conteudo);
            transporteSaldoFinanceiro.setErroDuranteProcessamento(true);
            transporteSaldoFinanceiro.setMensagemDeErro(conteudo);
        }
    }

    private void movimentarUnidadeGestora(Exercicio exercicioDestino, UnidadeGestora unidadeGestora) {
        UnidadeGestora unidadeGestoraNova = (UnidadeGestora) Util.clonarObjeto(unidadeGestora);
        unidadeGestoraNova.setId(null);
        unidadeGestoraNova.setExercicio(exercicioDestino);
        unidadeGestoraNova = em.merge(unidadeGestoraNova);
        for (UnidadeOrganizacional unidade : buscarUnidadesDaUnidadeGestoras(unidadeGestora)) {
            UnidadeGestoraUnidadeOrganizacional unidadeGestoraUnidadeOrganizacional = new UnidadeGestoraUnidadeOrganizacional();
            unidadeGestoraUnidadeOrganizacional.setUnidadeGestora(unidadeGestoraNova);
            unidadeGestoraUnidadeOrganizacional.setUnidadeOrganizacional(unidade);
            Util.adicionarObjetoEmLista(unidadeGestoraNova.getUnidadeGestoraUnidadesOrganizacionais(), unidadeGestoraUnidadeOrganizacional);
            em.persist(unidadeGestoraUnidadeOrganizacional);
        }
    }

    public List<ConfigObrigacaoAPagar> simularTransporteCDEObrigacaoAPagar(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDEObrigacaoAPagar(selecionado, assistenteTransporte, true);
    }

    private List<ConfigObrigacaoAPagar> transportarCDEObrigacaoAPagar(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigObrigacaoAPagar> configuracoes = buscarConfiguracaoObrigacoesAPagar(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());
        Map<String, ConfigObrigacaoAPagar> mapConfiguracoes = new HashMap<>();
        for (ConfigObrigacaoAPagar configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigObrigacaoAPagar) salvarObjeto(configOrigem);
                }
                criarCDEObrigacaoAPagar(selecionado, assistenteTransporte, configOrigem, mapConfiguracoes, isSimulacao);
            } catch (Exception e) {
                configOrigem.setErroDuranteProcessamento(true);
                configOrigem.setMensagemDeErro(e.getMessage());
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    public List<ConfigEmpenho> simularTransporteCDEEmpenho(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDEEmpenho(selecionado, assistenteTransporte, true);
    }

    private List<ConfigEmpenho> transportarCDEEmpenho(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigEmpenho> configuracoes = buscarConfiguracaoEmpenho(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());
        Map<String, ConfigEmpenho> mapConfiguracoes = new HashMap<>();
        for (ConfigEmpenho configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigEmpenho) salvarObjeto(configOrigem);
                }
                criarCDEEmpenho(selecionado, assistenteTransporte, configOrigem, mapConfiguracoes, isSimulacao);
            } catch (Exception e) {
                configOrigem.setErroDuranteProcessamento(true);
                configOrigem.setMensagemDeErro(e.getMessage());
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void adicionarQuantidadeRegistroParaProcessar(TipoConfiguracaoContabil tipoConfiguracao, BarraProgressoAssistente assistente, int size) {
        assistente.setTotal(size);
        assistente.addMensagem(getMensagemLogInfo(tipoConfiguracao));
    }

    private void criarCDEObrigacaoAPagar(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, ConfigObrigacaoAPagar configOrigem, Map<String, ConfigObrigacaoAPagar> configuracoes, boolean isSimulacao) {
        Conta contaDestino = getContaCDE(configOrigem.getConta(), selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem);
        if (configuracoes.get(configOrigem.getTipoContaDespesa().name() + configOrigem.getSubTipoDespesa().name() + configOrigem.getTipoReconhecimento().name() + configOrigem.getEventoContabil().getId() + contaDestino) == null) {
            ConfigObrigacaoAPagar novaConfig = (ConfigObrigacaoAPagar) Util.clonarEmNiveis(configOrigem, 1);
            novaConfig.setId(null);
            novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
            novaConfig.setFimVigencia(null);
            if (contaDestino != null) {
                novaConfig.setConta(contaDestino);
                if (!isSimulacao) {
                    novaConfig = (ConfigObrigacaoAPagar) salvarObjeto(novaConfig);
                }
                configuracoes.put(configOrigem.getTipoContaDespesa().name() + configOrigem.getSubTipoDespesa().name() + configOrigem.getTipoReconhecimento().name() + configOrigem.getEventoContabil().getId() + contaDestino, novaConfig);
            }
        }
    }

    private void criarCDEEmpenho(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, ConfigEmpenho configOrigem, Map<String, ConfigEmpenho> configuracoes, boolean isSimulacao) {
        Conta contaDestino = getContaCDE(configOrigem.getConfigEmpenhoContaDespesas().get(0).getContaDespesa(), selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem);
        if (configuracoes.get(configOrigem.getTipoContaDespesa().name() + configOrigem.getSubTipoDespesa().name() + configOrigem.getTipoReconhecimento().name() + configOrigem.getEventoContabil().getId() + contaDestino) == null) {
            ConfigEmpenho novaConfig = (ConfigEmpenho) Util.clonarEmNiveis(configOrigem, 1);
            novaConfig.setId(null);
            novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
            novaConfig.setFimVigencia(null);
            novaConfig.setConfigEmpenhoContaDespesas(new ArrayList<ConfigEmpenhoContaDesp>());
            adicionarContaDespesaCDEEmpenho(selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem, novaConfig);
            if (!novaConfig.getConfigEmpenhoContaDespesas().isEmpty()) {
                if (!isSimulacao) {
                    novaConfig = (ConfigEmpenho) salvarObjeto(novaConfig);
                }
                configuracoes.put(configOrigem.getTipoContaDespesa().name() + configOrigem.getSubTipoDespesa().name() + configOrigem.getTipoReconhecimento().name() + configOrigem.getEventoContabil().getId() + contaDestino, novaConfig);
            }
        }
    }

    private void adicionarContaDespesaCDEEmpenho(TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, ConfigEmpenho configOrigem, ConfigEmpenho novaConfig) {
        for (ConfigEmpenhoContaDesp configDesp : configOrigem.getConfigEmpenhoContaDespesas()) {
            Conta contaDestino = getContaCDE(configDesp.getContaDespesa(), selecionado, tipoConfiguracao, configOrigem);
            if (contaDestino != null) {
                ConfigEmpenhoContaDesp novaConfigDesp = new ConfigEmpenhoContaDesp();
                novaConfigDesp.setConfigEmpenho(novaConfig);
                novaConfigDesp.setContaDespesa(contaDestino);
                novaConfig.getConfigEmpenhoContaDespesas().add(novaConfigDesp);
            }
        }
    }

    public List<ConfigEmpenhoRestoPagar> simularTransporteCDEEmpenhoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDEEmpenhoResto(selecionado, assistenteTransporte, true);
    }

    private List<ConfigEmpenhoRestoPagar> transportarCDEEmpenhoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigEmpenhoRestoPagar> configuracoes = buscarConfiguracaoEmpenhoResto(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());
        Map<String, ConfigEmpenhoRestoPagar> mapConfiguracoes = new HashMap<>();
        for (ConfigEmpenhoRestoPagar configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigEmpenhoRestoPagar) salvarObjeto(configOrigem);
                }
                criarCDEEmpenhoResto(selecionado, assistenteTransporte, configOrigem, mapConfiguracoes, isSimulacao);
            } catch (Exception e) {
                configOrigem.setErroDuranteProcessamento(true);
                configOrigem.setMensagemDeErro(e.getMessage());
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarCDEEmpenhoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, ConfigEmpenhoRestoPagar configOrigem, Map<String, ConfigEmpenhoRestoPagar> configuracoes, boolean isSimulacao) {
        Conta contaDestino = getContaCDE(configOrigem.getConfigEmpenhoRestoContaDespesas().get(0).getContaDespesa(), selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem);
        if (configuracoes.get(configOrigem.getTipoRestosProcessados().name() + configOrigem.getEventoContabil().getId() + contaDestino) == null) {
            ConfigEmpenhoRestoPagar novaConfig = (ConfigEmpenhoRestoPagar) Util.clonarEmNiveis(configOrigem, 1);
            novaConfig.setId(null);
            novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
            novaConfig.setFimVigencia(null);
            novaConfig.setConfigEmpenhoRestoContaDespesas(new ArrayList<ConfigEmpenhoRestoContaDesp>());
            adicionarContaDespesaCDEEmpenhoResto(selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem, novaConfig);
            if (!novaConfig.getConfigEmpenhoRestoContaDespesas().isEmpty()) {
                if (!isSimulacao) {
                    novaConfig = (ConfigEmpenhoRestoPagar) salvarObjeto(novaConfig);
                }
                configuracoes.put(configOrigem.getTipoRestosProcessados().name() + configOrigem.getEventoContabil().getId() + contaDestino, novaConfig);
            }
        }
    }

    private void adicionarContaDespesaCDEEmpenhoResto(TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, ConfigEmpenhoRestoPagar configOrigem, ConfigEmpenhoRestoPagar novaConfig) {
        for (ConfigEmpenhoRestoContaDesp configDesp : configOrigem.getConfigEmpenhoRestoContaDespesas()) {
            Conta contaDestino = getContaCDE(configDesp.getContaDespesa(), selecionado, tipoConfiguracao, configOrigem);
            if (contaDestino != null) {
                ConfigEmpenhoRestoContaDesp novaConfigDesp = new ConfigEmpenhoRestoContaDesp();
                novaConfigDesp.setConfigEmpenhoResto(novaConfig);
                novaConfigDesp.setContaDespesa(contaDestino);
                novaConfig.getConfigEmpenhoRestoContaDespesas().add(novaConfigDesp);
            }
        }
    }

    public List<ConfigCancelamentoResto> simularTransporteCDECancelamentoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDECancelamentoResto(selecionado, assistenteTransporte, true);
    }

    private List<ConfigCancelamentoResto> transportarCDECancelamentoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigCancelamentoResto> configuracoes = buscarConfiguracaoCancelamentoResto(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());
        Map<String, ConfigCancelamentoResto> mapConfiguracoes = new HashMap<>();
        for (ConfigCancelamentoResto configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigCancelamentoResto) salvarObjeto(configOrigem);
                }
                criarCDECancelamentoResto(selecionado, assistenteTransporte, configOrigem, mapConfiguracoes, isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarCDECancelamentoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, ConfigCancelamentoResto configOrigem, Map<String, ConfigCancelamentoResto> configuracoes, boolean isSimulacao) {
        Conta contaDestino = getContaCDE(configOrigem.getConfigCancelamentoRestoContaDespesas().get(0).getContaDespesa(), selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem);
        if (configuracoes.get(configOrigem.getTipoRestosProcessados().name() + configOrigem.getCancelamentoPrescricao().name() + configOrigem.getPatrimonioLiquido().name() + configOrigem.getEventoContabil().getId() + contaDestino) == null) {
            ConfigCancelamentoResto novaConfig = (ConfigCancelamentoResto) Util.clonarEmNiveis(configOrigem, 1);
            novaConfig.setId(null);
            novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
            novaConfig.setFimVigencia(null);
            novaConfig.setConfigCancelamentoRestoContaDespesas(new ArrayList<ConfigCancRestoContaDesp>());

            adicionarContaDespesaCDECancelamentoResto(selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem, novaConfig);
            if (!novaConfig.getConfigCancelamentoRestoContaDespesas().isEmpty()) {
                if (!isSimulacao) {
                    novaConfig = (ConfigCancelamentoResto) salvarObjeto(novaConfig);
                }
                configuracoes.put(configOrigem.getTipoRestosProcessados().name() + configOrigem.getCancelamentoPrescricao().name() + configOrigem.getPatrimonioLiquido().name() + configOrigem.getEventoContabil().getId() + contaDestino, novaConfig);
            }
        }
    }

    private void adicionarContaDespesaCDECancelamentoResto(TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, ConfigCancelamentoResto configOrigem, ConfigCancelamentoResto novaConfig) {
        for (ConfigCancRestoContaDesp configDesp : configOrigem.getConfigCancelamentoRestoContaDespesas()) {
            Conta contaDestino = getContaCDE(configDesp.getContaDespesa(), selecionado, tipoConfiguracao, configOrigem);
            if (contaDestino != null) {
                ConfigCancRestoContaDesp novaConfigDesp = new ConfigCancRestoContaDesp();
                novaConfigDesp.setConfigCancelamentoResto(novaConfig);
                novaConfigDesp.setContaDespesa(contaDestino);
                novaConfig.getConfigCancelamentoRestoContaDespesas().add(novaConfigDesp);
            }
        }
    }

    public List<ConfigLiquidacao> simularTransporteCDELiquidacao(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDELiquidacao(selecionado, assistenteTransporte, true);
    }

    private List<ConfigLiquidacao> transportarCDELiquidacao(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistenteBarra = assistenteTransporte.getAssistenteBarraProgresso();
        assistenteBarra.inicializa();
        List<ConfigLiquidacao> configuracoes = buscarConfiguracaoLiquidacao(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistenteBarra, configuracoes.size());
        Map<String, ConfigLiquidacao> mapConfiguracoes = new HashMap<>();
        for (ConfigLiquidacao configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigLiquidacao) salvarObjeto(configOrigem);
                }
                criarCDELiquidacao(selecionado, assistenteTransporte, configOrigem, mapConfiguracoes, isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistenteBarra);
        }
        assistenteBarra.finaliza();
        return configuracoes;
    }

    private void criarCDELiquidacao(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, ConfigLiquidacao configOrigem, Map<String, ConfigLiquidacao> configuracoes, boolean isSimulacao) {
        Conta contaDestino = getContaCDE(configOrigem.getConfigLiquidacaoContaDespesas().get(0).getContaDespesa(), selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem);
        if (configuracoes.get(configOrigem.getSubTipoDespesa().name() + configOrigem.getTipoReconhecimento().name() + configOrigem.getEventoContabil().getId() + contaDestino) == null) {
            ConfigLiquidacao novaConfig = (ConfigLiquidacao) Util.clonarEmNiveis(configOrigem, 1);
            novaConfig.setId(null);
            novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
            novaConfig.setFimVigencia(null);
            novaConfig.setConfigLiquidacaoContaDespesas(new ArrayList<ConfigLiquidacaoContaDesp>());
            adiconarContaDespesaCDELiquidacao(selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem, novaConfig);
            if (!novaConfig.getConfigLiquidacaoContaDespesas().isEmpty()) {
                if (!isSimulacao) {
                    novaConfig = (ConfigLiquidacao) salvarObjeto(novaConfig);
                }
                configuracoes.put(configOrigem.getSubTipoDespesa().name() + configOrigem.getTipoReconhecimento().name() + configOrigem.getEventoContabil().getId() + contaDestino, novaConfig);
            }
        }
    }

    private void adiconarContaDespesaCDELiquidacao(TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, ConfigLiquidacao configOrigem, ConfigLiquidacao novaConfig) {
        for (ConfigLiquidacaoContaDesp configDesp : configOrigem.getConfigLiquidacaoContaDespesas()) {
            Conta contaDestino = getContaCDE(configDesp.getContaDespesa(), selecionado, tipoConfiguracao, configOrigem);

            if (contaDestino != null) {
                ConfigLiquidacaoContaDesp novaConfigDesp = new ConfigLiquidacaoContaDesp();
                novaConfigDesp.setConfigLiquidacao(novaConfig);
                novaConfigDesp.setContaDespesa(contaDestino);
                novaConfig.getConfigLiquidacaoContaDespesas().add(novaConfigDesp);
            }
        }
    }

    public List<ConfigLiquidacaoResPagar> simularTransporteCDELiquidacaoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDELiquidacaoResto(selecionado, assistenteTransporte, true);
    }

    private List<ConfigLiquidacaoResPagar> transportarCDELiquidacaoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigLiquidacaoResPagar> configuracoes = buscarConfiguracaoLiquidacaoResto(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());
        Map<String, ConfigLiquidacaoResPagar> mapConfiguracoes = new HashMap<>();
        for (ConfigLiquidacaoResPagar configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigLiquidacaoResPagar) salvarObjeto(configOrigem);
                }

                criarCDELiquidacaoResto(selecionado, assistenteTransporte, configOrigem, mapConfiguracoes, isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarCDELiquidacaoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, ConfigLiquidacaoResPagar configOrigem, Map<String, ConfigLiquidacaoResPagar> configuracoes, boolean isSimulacao) {
        Conta contaDestino = getContaCDE(configOrigem.getConfigLiqResPagContDesp().get(0).getContaDespesa(), selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem);
        if (configuracoes.get(configOrigem.getSubTipoDespesa().name() + configOrigem.getEventoContabil().getId() + contaDestino) == null) {
            ConfigLiquidacaoResPagar novaConfig = (ConfigLiquidacaoResPagar) Util.clonarEmNiveis(configOrigem, 1);
            novaConfig.setId(null);
            novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
            novaConfig.setFimVigencia(null);
            novaConfig.setConfigLiqResPagContDesp(new ArrayList<ConfigLiqResPagContDesp>());

            adicionarContaDespesaCDELiquidacaoResto(selecionado, configOrigem, novaConfig, assistenteTransporte.getTipoConfiguracaoContabil());
            if (!novaConfig.getConfigLiqResPagContDesp().isEmpty()) {
                if (!isSimulacao) {
                    novaConfig = (ConfigLiquidacaoResPagar) salvarObjeto(novaConfig);
                }
                configuracoes.put(configOrigem.getSubTipoDespesa().name() + configOrigem.getEventoContabil().getId() + contaDestino, novaConfig);
            }
        }
    }

    private void adicionarContaDespesaCDELiquidacaoResto(TransporteConfiguracaoContabil selecionado, ConfigLiquidacaoResPagar configOrigem, ConfigLiquidacaoResPagar novaConfig, TipoConfiguracaoContabil tipoConfiguracao) {
        for (ConfigLiqResPagContDesp configDesp : configOrigem.getConfigLiqResPagContDesp()) {
            Conta contaDestino = getContaCDE(configDesp.getContaDespesa(), selecionado, tipoConfiguracao, configOrigem);
            if (contaDestino != null) {
                ConfigLiqResPagContDesp novaConfigDesp = new ConfigLiqResPagContDesp();
                novaConfigDesp.setConfigLiquidacaoResPagar(novaConfig);
                novaConfigDesp.setContaDespesa(contaDestino);
                novaConfig.getConfigLiqResPagContDesp().add(novaConfigDesp);
            }
        }
    }

    public List<ConfigPagamento> simularTransportarCDEPagamento(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDEPagamento(selecionado, assistenteTransporte, true);
    }

    private List<ConfigPagamento> transportarCDEPagamento(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigPagamento> configuracoes = buscarConfiguracaoPagamento(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());
        Map<String, ConfigPagamento> mapConfiguracoes = new HashMap<>();
        for (ConfigPagamento configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigPagamento) salvarObjeto(configOrigem);
                }
                criarCDEPagamento(selecionado, assistenteTransporte, configOrigem, mapConfiguracoes, isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarCDEPagamento(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, ConfigPagamento configOrigem, Map<String, ConfigPagamento> configuracoes, boolean isSimulacao) {
        Conta contaDestino = getContaCDE(configOrigem.getConfigPagamentoContaDespesas().get(0).getContaDespesa(), selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem);
        if (configuracoes.get(configOrigem.getTipoContaDespesa().name() + configOrigem.getEventoContabil().getId() + contaDestino) == null) {
            ConfigPagamento novaConfig = (ConfigPagamento) Util.clonarEmNiveis(configOrigem, 1);
            novaConfig.setId(null);
            novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
            novaConfig.setFimVigencia(null);
            novaConfig.setConfigPagamentoContaDespesas(new ArrayList<ConfigPagamentoContaDesp>());

            adicionarContaDespesaCDEPagamento(selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem, novaConfig);
            if (!novaConfig.getConfigPagamentoContaDespesas().isEmpty()) {
                if (!isSimulacao) {
                    novaConfig = (ConfigPagamento) salvarObjeto(novaConfig);
                }
                configuracoes.put(configOrigem.getTipoContaDespesa().name() + configOrigem.getEventoContabil().getId() + contaDestino, novaConfig);
            }
        }
    }

    private void adicionarContaDespesaCDEPagamento(TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, ConfigPagamento configOrigem, ConfigPagamento novaConfig) {
        for (ConfigPagamentoContaDesp configDesp : configOrigem.getConfigPagamentoContaDespesas()) {
            Conta contaDestino = getContaCDE(configDesp.getContaDespesa(), selecionado, tipoConfiguracao, configOrigem);
            if (contaDestino != null) {
                ConfigPagamentoContaDesp novaConfigDesp = new ConfigPagamentoContaDesp();
                novaConfigDesp.setConfigPagamento(novaConfig);
                novaConfigDesp.setContaDespesa(contaDestino);
                novaConfig.getConfigPagamentoContaDespesas().add(novaConfigDesp);
            }
        }
    }

    public List<ConfigPagamentoRestoPagar> simularTransporteCDEPagamentoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDEPagamentoResto(selecionado, assistenteTransporte, true);
    }

    private List<ConfigPagamentoRestoPagar> transportarCDEPagamentoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigPagamentoRestoPagar> configuracoes = buscarConfiguracaoPagamentoResto(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());
        Map<String, ConfigPagamentoRestoPagar> mapConfiguracoes = new HashMap<>();
        for (ConfigPagamentoRestoPagar configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigPagamentoRestoPagar) salvarObjeto(configOrigem);
                }

                criarCDEPagamentoResto(selecionado, assistenteTransporte, configOrigem, mapConfiguracoes, isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarCDEPagamentoResto(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, ConfigPagamentoRestoPagar configOrigem, Map<String, ConfigPagamentoRestoPagar> configuracoes, boolean isSimulacao) {
        Conta contaDestino = getContaCDE(configOrigem.getConfigPagResPagContDesp().get(0).getContaDespesa(), selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem);
        if (configuracoes.get(configOrigem.getTipoRestosProcessados().name() + configOrigem.getTipoContaDespesa().name() + configOrigem.getEventoContabil().getId() + contaDestino) == null) {
            ConfigPagamentoRestoPagar novaConfig = (ConfigPagamentoRestoPagar) Util.clonarEmNiveis(configOrigem, 1);
            novaConfig.setId(null);
            novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
            novaConfig.setFimVigencia(null);
            novaConfig.setConfigPagResPagContDesp(new ArrayList<ConfigPagResPagContDesp>());

            adicionarContaDespesaCDEPagamentoResto(selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem, novaConfig);
            if (!novaConfig.getConfigPagResPagContDesp().isEmpty()) {
                if (!isSimulacao) {
                    novaConfig = (ConfigPagamentoRestoPagar) salvarObjeto(novaConfig);
                }
                configuracoes.put(configOrigem.getTipoRestosProcessados().name() + configOrigem.getTipoContaDespesa().name() + configOrigem.getEventoContabil().getId() + contaDestino, novaConfig);
            }
        }
    }

    private void adicionarContaDespesaCDEPagamentoResto(TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, ConfigPagamentoRestoPagar configOrigem, ConfigPagamentoRestoPagar novaConfig) {
        for (ConfigPagResPagContDesp configDesp : configOrigem.getConfigPagResPagContDesp()) {
            Conta contaDestino = getContaCDE(configDesp.getContaDespesa(), selecionado, tipoConfiguracao, configOrigem);
            if (contaDestino != null) {
                ConfigPagResPagContDesp novaConfigDesp = new ConfigPagResPagContDesp();
                novaConfigDesp.setConfigPagamentoRestoPagar(novaConfig);
                novaConfigDesp.setContaDespesa(contaDestino);
                novaConfig.getConfigPagResPagContDesp().add(novaConfigDesp);
            }
        }
    }

    public List<ConfigReceitaRealizada> simularTransporteCDEReceitaRealizada(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDEReceitaRealizada(selecionado, assistenteTransporte, true);
    }

    private List<ConfigReceitaRealizada> transportarCDEReceitaRealizada(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigReceitaRealizada> configuracoes = buscarConfiguracaoReceitaRealizada(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());

        Map<String, ConfigReceitaRealizada> mapConfiguracoes = new HashMap<>();
        for (ConfigReceitaRealizada configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigReceitaRealizada) salvarObjeto(configOrigem);
                }
                criarCDEReceitaRealizada(selecionado, assistenteTransporte, configOrigem, mapConfiguracoes, isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarCDEReceitaRealizada(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, ConfigReceitaRealizada configOrigem, Map<String, ConfigReceitaRealizada> configuracoes, boolean isSimulacao) {
        Conta contaDestino = getContaCDE(configOrigem.getConfigRecRealizadaContaRecs().get(0).getContaReceita(), selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem);
        if (configuracoes.get(configOrigem.getOperacaoReceitaRealizada().name() + configOrigem.getEventoContabil().getId() + contaDestino) == null) {
            ConfigReceitaRealizada novaConfig = (ConfigReceitaRealizada) Util.clonarEmNiveis(configOrigem, 1);
            novaConfig.setId(null);
            novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
            novaConfig.setFimVigencia(null);
            novaConfig.setConfigRecRealizadaContaRecs(new ArrayList<ConfigRecRealizadaContaRec>());
            adicionarContaReceitaCDEReceitaRealizada(selecionado, assistenteTransporte, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem, novaConfig);
            if (!novaConfig.getConfigRecRealizadaContaRecs().isEmpty()) {
                if (!isSimulacao) {
                    novaConfig = (ConfigReceitaRealizada) salvarObjeto(novaConfig);
                }
                configuracoes.put(configOrigem.getOperacaoReceitaRealizada().name() + configOrigem.getEventoContabil().getId() + contaDestino, novaConfig);
            }
        }
    }

    private void adicionarContaReceitaCDEReceitaRealizada(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, TipoConfiguracaoContabil tipoConfiguracao, ConfigReceitaRealizada configOrigem, ConfigReceitaRealizada novaConfig) {
        for (ConfigRecRealizadaContaRec configContaRec : configOrigem.getConfigRecRealizadaContaRecs()) {
            Conta contaDestino = getContaCDE(configContaRec.getContaReceita(), selecionado, tipoConfiguracao, configOrigem);

            if (contaDestino != null) {
                ConfigReceitaRealizada configExistente = configReceitaRealizadaFacade.recuperarConfigReceitaRealizada(configOrigem, novaConfig, contaDestino);
                if (configExistente == null) {
                    ConfigRecRealizadaContaRec novaConfigDesp = new ConfigRecRealizadaContaRec();
                    novaConfigDesp.setConfigReceitaRealizada(novaConfig);
                    novaConfigDesp.setContaReceita(contaDestino);
                    novaConfig.getConfigRecRealizadaContaRecs().add(novaConfigDesp);
                }
            }
        }
    }

    public List<ConfigCreditoReceber> simularTransporteCDECreditoReceber(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDECreditoReceber(selecionado, assistenteTransporte, true);
    }

    private List<ConfigCreditoReceber> transportarCDECreditoReceber(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigCreditoReceber> configuracoes = buscarConfiguracaoCreditoReceber(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());
        Map<String, ConfigCreditoReceber> mapConfiguracoes = new HashMap<>();
        for (ConfigCreditoReceber configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigCreditoReceber) salvarObjeto(configOrigem);
                }
                criarCDECreditoReceber(selecionado, assistenteTransporte, configOrigem, mapConfiguracoes, isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarCDECreditoReceber(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, ConfigCreditoReceber configOrigem, Map<String, ConfigCreditoReceber> configuracoes, boolean isSimulacao) {
        Conta contaDestino = getContaCDE(configOrigem.getContaReceita(), selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem);
        if (configuracoes.get(configOrigem.getOperacaoCreditoReceber().name() + configOrigem.getTiposCredito().name() + configOrigem.getEventoContabil().getId() + contaDestino) == null) {
            ConfigCreditoReceber novaConfig = (ConfigCreditoReceber) Util.clonarEmNiveis(configOrigem, 1);
            novaConfig.setId(null);
            novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
            novaConfig.setFimVigencia(null);
            if (contaDestino != null) {
                ConfigCreditoReceber configExistente = configCreditoReceberFacade.recuperarConfigCreditoReceber(configOrigem, novaConfig, contaDestino);
                if (configExistente == null) {
                    novaConfig.setContaReceita((ContaReceita) contaDestino);
                    if (!isSimulacao) {
                        novaConfig = (ConfigCreditoReceber) salvarObjeto(novaConfig);
                    }
                    configuracoes.put(configOrigem.getOperacaoCreditoReceber().name() + configOrigem.getTiposCredito().name() + configOrigem.getEventoContabil().getId() + contaDestino, novaConfig);
                }
            }
        }
    }

    public List<ConfigDividaAtivaContabil> simularTransporteCDEDividaAtiva(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDEDividaAtiva(selecionado, assistenteTransporte, true);
    }

    private List<ConfigDividaAtivaContabil> transportarCDEDividaAtiva(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigDividaAtivaContabil> configuracoes = buscarConfiguracaoDividaAtiva(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());
        Map<String, ConfigDividaAtivaContabil> mapConfiguracoes = new HashMap<>();
        for (ConfigDividaAtivaContabil configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigDividaAtivaContabil) salvarObjeto(configOrigem);
                }
                criarCDEDividaAtiva(selecionado, assistenteTransporte, configOrigem, mapConfiguracoes, isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarCDEDividaAtiva(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, ConfigDividaAtivaContabil configOrigem, Map<String, ConfigDividaAtivaContabil> configuracoes, boolean isSimulacao) {
        Conta contaDestino = getContaCDE(configOrigem.getContaReceita(), selecionado, assistenteTransporte.getTipoConfiguracaoContabil(), configOrigem);
        if (configuracoes.get(configOrigem.getOperacaoDividaAtiva().name() + configOrigem.getTiposCredito().name() + configOrigem.getEventoContabil().getId() + contaDestino) == null) {
            ConfigDividaAtivaContabil novaConfig = (ConfigDividaAtivaContabil) Util.clonarEmNiveis(configOrigem, 1);
            novaConfig.setId(null);
            novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
            novaConfig.setFimVigencia(null);
            if (contaDestino != null) {
                ConfigDividaAtivaContabil configExistente = configDividaAtivaContabilFacade.recuperarConfigDividaAtivaContabil(configOrigem, novaConfig, contaDestino);
                if (configExistente == null) {
                    novaConfig.setContaReceita((ContaReceita) contaDestino);
                    if (!isSimulacao) {
                        novaConfig = (ConfigDividaAtivaContabil) salvarObjeto(novaConfig);
                    }
                    configuracoes.put(configOrigem.getOperacaoDividaAtiva().name() + configOrigem.getTiposCredito().name() + configOrigem.getEventoContabil().getId() + contaDestino, novaConfig);
                }
            }
        }
    }

    public List<ConfigInvestimento> simularTransporteCDEInvestimento(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDEInvestimento(selecionado, assistenteTransporte, true);
    }

    private List<ConfigInvestimento> transportarCDEInvestimento(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigInvestimento> configuracoes = buscarConfiguracaoInvestimento(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());

        for (ConfigInvestimento configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigInvestimento) salvarObjeto(configOrigem);
                }
                criarCDEInvestimento(selecionado, configOrigem, isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarCDEInvestimento(TransporteConfiguracaoContabil selecionado, ConfigInvestimento configOrigem, boolean isSimulacao) {
        ConfigInvestimento novaConfig = (ConfigInvestimento) Util.clonarEmNiveis(configOrigem, 1);
        novaConfig.setId(null);
        novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
        novaConfig.setFimVigencia(null);
        if (!isSimulacao) {
            salvarObjeto(novaConfig);
        }
    }

    public List<ConfigPatrimonioLiquido> simularTransporteCDEPatrimonioLiquido(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarCDEPatrimonioLiquido(selecionado, assistenteTransporte, true);
    }

    private List<ConfigPatrimonioLiquido> transportarCDEPatrimonioLiquido(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigPatrimonioLiquido> configuracoes = buscarConfiguracaoPatrimonioLiquido(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());

        for (ConfigPatrimonioLiquido configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigPatrimonioLiquido) salvarObjeto(configOrigem);
                }
                criarCDEPatrimonioLiquido(selecionado, configOrigem, isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarCDEPatrimonioLiquido(TransporteConfiguracaoContabil selecionado, ConfigPatrimonioLiquido configOrigem, boolean isSimulacao) {
        ConfigPatrimonioLiquido novaConfig = (ConfigPatrimonioLiquido) Util.clonarEmNiveis(configOrigem, 1);
        novaConfig.setId(null);
        novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
        novaConfig.setFimVigencia(null);
        if (!isSimulacao) {
            salvarObjeto(novaConfig);
        }
    }


    private String getMensagemErroContaNaoEncontrada(Conta contaOrigem, Exercicio exercicio) {
        return "Não foi possível recuperar a conta " + contaOrigem + " para o exercício de " + exercicio.getAno() + ".";
    }

    private String getMensagemLogInfo(TipoConfiguracaoContabil tipoConfiguracao) {
        return "TRANSPORTANDO CONFIGURAÇÕES DE " + tipoConfiguracao.getDescricao().toUpperCase();
    }

    public List<OCCConta> simularTransporteConfiguracaoOCCConta(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoOCCConta(selecionado, assistenteTransporte, true);
    }

    private List<OCCConta> transportarConfiguracaoOCCConta(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<OCCConta> occs = Lists.newArrayList();
        if (!assistenteTransporte.getQueryOccConta().isEmpty()) {
            occs = buscarOCCConta(selecionado.getDataTransporte(), assistenteTransporte.getQueryOccConta());
        }
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, occs.size());

        for (OCCConta occOrigem : occs) {
            try {
                occOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    occOrigem = (OCCConta) salvarObjeto(occOrigem);
                }
                OCCConta novaOCCConta = (OCCConta) Util.clonarEmNiveis(occOrigem, 1);
                novaOCCConta = (OCCConta) preencherOrigemContaContabil(selecionado, novaOCCConta, occOrigem, assistenteTransporte.getTipoConfiguracaoContabil());

                Conta contaDestino = getContaOCC(selecionado, occOrigem, novaOCCConta, assistenteTransporte.getTipoConfiguracaoContabil());
                if (contaDestino != null && verificarContasContabeis(novaOCCConta, occOrigem, selecionado, assistenteTransporte.getTipoConfiguracaoContabil())) {
                    novaOCCConta.setContaOrigem(contaDestino);
                    OrigemContaContabil configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorConta(novaOCCConta, selecionado.getDataTransporte());
                    if (configuracaoEncontrada == null && !isSimulacao) {
                        salvarObjeto(novaOCCConta);
                    }
                }
            } catch (Exception e) {
                occOrigem.setMensagemDeErro(e.getMessage());
                occOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, occOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return occs;
    }

    public List<OCCBanco> simularTransporteConfiguracaoOCCBanco(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoOCCBanco(selecionado, assistenteTransporte, true);
    }

    private List<OCCBanco> transportarConfiguracaoOCCBanco(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<OCCBanco> occs = buscarOCCBanco(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, occs.size());

        for (OCCBanco occOrigem : occs) {
            try {
                occOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    occOrigem = (OCCBanco) salvarObjeto(occOrigem);
                }
                OCCBanco novaOCCBanco = (OCCBanco) Util.clonarEmNiveis(occOrigem, 1);

                novaOCCBanco = (OCCBanco) preencherOrigemContaContabil(selecionado, novaOCCBanco, occOrigem, assistenteTransporte.getTipoConfiguracaoContabil());
                if (verificarContasContabeis(novaOCCBanco, occOrigem, selecionado, assistenteTransporte.getTipoConfiguracaoContabil()) && !isSimulacao) {
                    salvarObjeto(novaOCCBanco);
                }
            } catch (Exception e) {
                occOrigem.setMensagemDeErro(e.getMessage());
                occOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, occOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return occs;
    }

    public List<OccClassePessoa> simularTransporteConfiguracaoOCCClassePessoa(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoOCCClassePessoa(selecionado, assistenteTransporte, true);
    }

    private List<OccClassePessoa> transportarConfiguracaoOCCClassePessoa(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<OccClassePessoa> occs = buscarOCCClassePessoa(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, occs.size());

        for (OccClassePessoa occOrigem : occs) {
            try {
                occOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    occOrigem = (OccClassePessoa) salvarObjeto(occOrigem);
                }
                OccClassePessoa novaOCCClasse = (OccClassePessoa) Util.clonarEmNiveis(occOrigem, 1);
                novaOCCClasse = (OccClassePessoa) preencherOrigemContaContabil(selecionado, novaOCCClasse, occOrigem, assistenteTransporte.getTipoConfiguracaoContabil());
                if (verificarContasContabeis(novaOCCClasse, occOrigem, selecionado, assistenteTransporte.getTipoConfiguracaoContabil()) && !isSimulacao) {
                    salvarObjeto(novaOCCClasse);
                }
            } catch (Exception e) {
                occOrigem.setMensagemDeErro(e.getMessage());
                occOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, occOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return occs;
    }

    public List<OCCGrupoBem> simularTransporteConfiguracaoOCCGrupoPatrimonial(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoOCCGrupoPatrimonial(selecionado, assistenteTransporte, true);
    }

    private List<OCCGrupoBem> transportarConfiguracaoOCCGrupoPatrimonial(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<OCCGrupoBem> occs = buscarOCCGrupoPatrimonial(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, occs.size());

        for (OCCGrupoBem occOrigem : occs) {
            try {
                occOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    occOrigem = (OCCGrupoBem) salvarObjeto(occOrigem);
                }
                OCCGrupoBem novaOCCGrupoPatrimonial = (OCCGrupoBem) Util.clonarEmNiveis(occOrigem, 1);
                novaOCCGrupoPatrimonial = (OCCGrupoBem) preencherOrigemContaContabil(selecionado, novaOCCGrupoPatrimonial, occOrigem, assistenteTransporte.getTipoConfiguracaoContabil());
                if (verificarContasContabeis(novaOCCGrupoPatrimonial, occOrigem, selecionado, assistenteTransporte.getTipoConfiguracaoContabil()) && !isSimulacao) {
                    salvarObjeto(novaOCCGrupoPatrimonial);
                }
            } catch (Exception e) {
                occOrigem.setMensagemDeErro(e.getMessage());
                occOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, occOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return occs;
    }

    public List<OCCGrupoMaterial> simularTransporteConfiguracaoOCCGrupoMaterial(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoOCCGrupoMaterial(selecionado, assistenteTransporte, true);
    }

    private List<OCCGrupoMaterial> transportarConfiguracaoOCCGrupoMaterial(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<OCCGrupoMaterial> occs = buscarOCCGrupoMaterial(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, occs.size());

        for (OCCGrupoMaterial occOrigem : occs) {
            try {
                occOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    occOrigem = (OCCGrupoMaterial) salvarObjeto(occOrigem);
                }
                OCCGrupoMaterial novaOCCGrupoMaterial = (OCCGrupoMaterial) Util.clonarEmNiveis(occOrigem, 1);
                novaOCCGrupoMaterial = (OCCGrupoMaterial) preencherOrigemContaContabil(selecionado, novaOCCGrupoMaterial, occOrigem, assistenteTransporte.getTipoConfiguracaoContabil());
                if (verificarContasContabeis(novaOCCGrupoMaterial, occOrigem, selecionado, assistenteTransporte.getTipoConfiguracaoContabil()) && !isSimulacao) {
                    salvarObjeto(novaOCCGrupoMaterial);
                }
            } catch (Exception e) {
                occOrigem.setMensagemDeErro(e.getMessage());
                occOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, occOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return occs;
    }

    public List<OCCNaturezaDividaPublica> simularTransporteConfiguracaoOCCNaturezaDividaPublica(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoOCCNaturezaDividaPublica(selecionado, assistenteTransporte, true);
    }

    private List<OCCNaturezaDividaPublica> transportarConfiguracaoOCCNaturezaDividaPublica(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<OCCNaturezaDividaPublica> occs = buscarOCCNaturezaDividaPublica(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, occs.size());

        for (OCCNaturezaDividaPublica occOrigem : occs) {
            try {
                occOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    occOrigem = (OCCNaturezaDividaPublica) salvarObjeto(occOrigem);
                }
                OCCNaturezaDividaPublica novaOCCDivida = (OCCNaturezaDividaPublica) Util.clonarEmNiveis(occOrigem, 1);

                novaOCCDivida = (OCCNaturezaDividaPublica) preencherOrigemContaContabil(selecionado, novaOCCDivida, occOrigem, assistenteTransporte.getTipoConfiguracaoContabil());
                if (verificarContasContabeis(novaOCCDivida, occOrigem, selecionado, assistenteTransporte.getTipoConfiguracaoContabil()) && !isSimulacao) {
                    salvarObjeto(novaOCCDivida);
                }
            } catch (Exception e) {
                occOrigem.setMensagemDeErro(e.getMessage());
                occOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, occOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return occs;
    }

    public List<OCCTipoPassivoAtuarial> simularTransporteConfiguracaoOCCTipoPassivoAtuarial(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoOCCTipoPassivoAtuarial(selecionado, assistenteTransporte, true);
    }

    private List<OCCTipoPassivoAtuarial> transportarConfiguracaoOCCTipoPassivoAtuarial(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<OCCTipoPassivoAtuarial> occs = buscarOCCTipoPassivoAtuarial(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, occs.size());

        for (OCCTipoPassivoAtuarial occOrigem : occs) {
            try {
                occOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    occOrigem = (OCCTipoPassivoAtuarial) salvarObjeto(occOrigem);
                }
                OCCTipoPassivoAtuarial novaOCCTipoPassivo = (OCCTipoPassivoAtuarial) Util.clonarEmNiveis(occOrigem, 1);

                novaOCCTipoPassivo = (OCCTipoPassivoAtuarial) preencherOrigemContaContabil(selecionado, novaOCCTipoPassivo, occOrigem, assistenteTransporte.getTipoConfiguracaoContabil());
                if (verificarContasContabeis(novaOCCTipoPassivo, occOrigem, selecionado, assistenteTransporte.getTipoConfiguracaoContabil()) && !isSimulacao) {
                    salvarObjeto(novaOCCTipoPassivo);
                }
            } catch (Exception e) {
                occOrigem.setMensagemDeErro(e.getMessage());
                occOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, occOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return occs;
    }

    public List<OCCOrigemRecurso> simularTransporteConfiguracaoOCCOrigemRecurso(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoOCCOrigemRecurso(selecionado, assistenteTransporte, true);
    }

    private List<OCCOrigemRecurso> transportarConfiguracaoOCCOrigemRecurso(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<OCCOrigemRecurso> occs = buscarOCCOrigemRecurso(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, occs.size());

        for (OCCOrigemRecurso occOrigem : occs) {
            try {
                occOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    occOrigem = (OCCOrigemRecurso) salvarObjeto(occOrigem);
                }
                OCCOrigemRecurso novaOCCTipoPassivo = (OCCOrigemRecurso) Util.clonarEmNiveis(occOrigem, 1);

                novaOCCTipoPassivo = (OCCOrigemRecurso) preencherOrigemContaContabil(selecionado, novaOCCTipoPassivo, occOrigem, assistenteTransporte.getTipoConfiguracaoContabil());
                if (verificarContasContabeis(novaOCCTipoPassivo, occOrigem, selecionado, assistenteTransporte.getTipoConfiguracaoContabil()) && !isSimulacao) {
                    salvarObjeto(novaOCCTipoPassivo);
                }
            } catch (Exception e) {
                occOrigem.setMensagemDeErro(e.getMessage());
                occOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, occOrigem, e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return occs;
    }

    public List<ConfigDespesaBens> simularTransporteConfiguracaoContaDespesaGrupoPatrimonial(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoContaDespesaGrupoPatrimonial(selecionado, assistenteTransporte, true);
    }

    private List<ConfigDespesaBens> transportarConfiguracaoContaDespesaGrupoPatrimonial(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigDespesaBens> configuracoes = buscarConfiguracaoContaDespeasGrupoPatrimonial(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());

        for (ConfigDespesaBens configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigDespesaBens) salvarObjeto(configOrigem);
                }
                criarNovaConfiguracaoGrupoPatrimonial(selecionado, configOrigem, assistenteTransporte.getTipoConfiguracaoContabil(), isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesGruposPatrimoniais().add(new TransporteConfiguracaoGrupoPatrimonial(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem.getGrupoBem(), e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarNovaConfiguracaoGrupoPatrimonial(TransporteConfiguracaoContabil selecionado, ConfigDespesaBens configOrigem, TipoConfiguracaoContabil tipoConfiguracao, boolean isSimulacao) {
        ConfigDespesaBens novaConfig = (ConfigDespesaBens) Util.clonarEmNiveis(configOrigem, 1);
        novaConfig.setId(null);
        novaConfig.setFimVigencia(null);
        novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
        Conta contaDestino = getContaDespesaGrupoPatrimonial(selecionado, tipoConfiguracao, configOrigem);
        if (contaDestino != null && !isSimulacao) {
            novaConfig.setContaDespesa(contaDestino);
            salvarObjeto(novaConfig);
        }
    }

    public List<ConfigGrupoMaterial> simularTransporteConfiguracaoContaDespesaGrupoMaterial(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoContaDespesaGrupoMaterial(selecionado, assistenteTransporte, true);
    }

    private List<ConfigGrupoMaterial> transportarConfiguracaoContaDespesaGrupoMaterial(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigGrupoMaterial> configuracoes = buscarConfiguracaoContaDespeasGrupoMaterial(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());

        for (ConfigGrupoMaterial configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigGrupoMaterial) salvarObjeto(configOrigem);
                }
                criarNovaConfiguracaoGrupoMaterial(selecionado, configOrigem, assistenteTransporte.getTipoConfiguracaoContabil(), isSimulacao);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesGruposMateriais().add(new TransporteConfiguracaoGrupoMaterial(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem.getGrupoMaterial(), e.getMessage()));
            }
            contarRegistroProcessado(assistente);
        }
        assistente.finaliza();
        return configuracoes;
    }

    public List<ConfigTipoViagemContaDespesa> simularTransporteConfiguracaoContaDespesaTipoViagem(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoContaDespesaTipoViagem(selecionado, assistenteTransporte, true);
    }

    private List<ConfigTipoViagemContaDespesa> transportarConfiguracaoContaDespesaTipoViagem(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigTipoViagemContaDespesa> configuracoes = buscarConfiguracaoContaDespesaTipoViagem(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());

        for (ConfigTipoViagemContaDespesa configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigTipoViagemContaDespesa) salvarObjeto(configOrigem);
                }
                criarConfiguracaoContaDespesaTipoViagem(selecionado, configOrigem, assistenteTransporte.getTipoConfiguracaoContabil(), isSimulacao);
                contarRegistroProcessado(assistente);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesTipoViagem().add(new TransporteConfiguracaoTipoViagem(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem.getListaContaDespesa().get(0).getTipoViagem(), e.getMessage()));
            }
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarConfiguracaoContaDespesaTipoViagem(TransporteConfiguracaoContabil selecionado, ConfigTipoViagemContaDespesa configOrigem, TipoConfiguracaoContabil tipoConfiguracao, boolean isSimulacao) {
        ConfigTipoViagemContaDespesa novaConfig = (ConfigTipoViagemContaDespesa) Util.clonarEmNiveis(configOrigem, 1);
        novaConfig.setId(null);
        novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
        novaConfig.setFimVigencia(null);
        novaConfig.setListaContaDespesa(new ArrayList<TipoViagemContaDespesa>());
        adicionarContaDespesaConfiguracaoContaDespesaTipoViagem(selecionado, configOrigem, tipoConfiguracao, novaConfig);
        if (!novaConfig.getListaContaDespesa().isEmpty() && !isSimulacao) {
            salvarObjeto(novaConfig);
        }
    }

    private void adicionarContaDespesaConfiguracaoContaDespesaTipoViagem(TransporteConfiguracaoContabil selecionado, ConfigTipoViagemContaDespesa configOrigem, TipoConfiguracaoContabil tipoConfiguracao, ConfigTipoViagemContaDespesa novaConfig) {
        for (TipoViagemContaDespesa configDesp : configOrigem.getListaContaDespesa()) {
            ContaDespesa contaDestino = (ContaDespesa) getContaDespesaTipoViagem(selecionado, tipoConfiguracao, configDesp, configOrigem);
            if (contaDestino != null) {
                TipoViagemContaDespesa novaConfigDespesa = new TipoViagemContaDespesa();
                novaConfigDespesa.setConfigTipoViagemContaDesp(novaConfig);
                novaConfigDespesa.setTipoViagem(configDesp.getTipoViagem());
                novaConfigDespesa.setContaDespesa(contaDestino);
                novaConfig.getListaContaDespesa().add(novaConfigDespesa);
            }
        }
    }

    public List<TransporteGrupoOrcamentario> simularTransporteGruposOrcamentarios(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarGruposOrcamentarios(selecionado, assistenteTransporte, true);
    }

    private List<TransporteGrupoOrcamentario> transportarGruposOrcamentarios(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<GrupoOrcamentario> grupos = buscarGruposOrcamentarios(getExercicioPorAno(DataUtil.getAno(selecionado.getDataTransporte())));
        List<TransporteGrupoOrcamentario> transportes = Lists.newArrayList();
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, grupos.size());
        for (GrupoOrcamentario grupoOrcamentario : grupos) {
            try {
                criarNovoGrupoOrcamentario(selecionado, grupoOrcamentario, isSimulacao);
                transportes.add(new TransporteGrupoOrcamentario(grupoOrcamentario, selecionado, ""));
                contarRegistroProcessado(assistente);
            } catch (Exception e) {
                TransporteGrupoOrcamentario transporte = new TransporteGrupoOrcamentario(grupoOrcamentario, selecionado, e.getMessage());
                transportes.add(transporte);
                if (!isSimulacao) {
                    selecionado.getTransporteGruposOrcamentarios().add(transporte);
                }
            }
        }
        assistente.finaliza();
        return transportes;
    }

    private GrupoOrcamentario criarNovoGrupoOrcamentario(TransporteConfiguracaoContabil selecionado, GrupoOrcamentario grupoOrigem, boolean isSimulacao) {
        GrupoOrcamentario novoGrupo = new GrupoOrcamentario();
        novoGrupo.setExercicio(selecionado.getExercicioDestino());
        novoGrupo.setEntidade(grupoOrigem.getEntidade());
        novoGrupo.setCodigo(grupoOrigem.getCodigo());
        novoGrupo.setDescricao(grupoOrigem.getDescricao());
        if (grupoOrigem.getFonteDeRecursos() != null) {
            novoGrupo.setFonteDeRecursos(buscarFonteDeRecursoEquivalente(grupoOrigem.getFonteDeRecursos(), selecionado.getExercicioDestino()));
        }
        novoGrupo.setUnidade(grupoOrigem.getUnidade());
        novoGrupo.setOrgao(grupoOrigem.getOrgao());
        if (grupoOrigem.getNaturezaDespesa() != null) {
            novoGrupo.setNaturezaDespesa(buscarContaDeDespesaEquivalente(grupoOrigem.getNaturezaDespesa(), selecionado.getExercicioDestino()));
        }
        if (!isSimulacao) {
            salvarObjeto(novoGrupo);
        }
        return novoGrupo;
    }

    private FonteDeRecursos buscarFonteDeRecursoEquivalente(FonteDeRecursos fonteDeRecursos, Exercicio exercicioDestino) {
        List<FonteDeRecursos> fontes = fonteDeRecursosFacade.buscarFontesDeRecursoEquivalentesPorId(fonteDeRecursos, exercicioDestino);
        if (!fontes.isEmpty()) {
            return fontes.get(0);
        }
        return null;
    }

    private Conta buscarContaDeDespesaEquivalente(Conta contaDeDespesa, Exercicio exercicioDestino) {
        List<Conta> contas = contaFacade.recuperarContasDespesaEquivalentesPorId(contaDeDespesa, exercicioDestino);
        if (!contas.isEmpty()) {
            return contas.get(0);
        }
        return null;
    }


    public List<ConfigTipoObjetoCompra> simularTransporteConfiguracaoContaDespesaTipoObjetoCompra(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoContaDespesaTipoObjetoCompra(selecionado, assistenteTransporte, true);
    }

    private List<ConfigTipoObjetoCompra> transportarConfiguracaoContaDespesaTipoObjetoCompra(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigTipoObjetoCompra> configuracoes = buscarConfiguracoesTipoObjetoCompra(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());

        for (ConfigTipoObjetoCompra configOrigem : configuracoes) {
            try {
                configOrigem.setFimVigencia(selecionado.getDataTransporte());
                if (!isSimulacao) {
                    configOrigem = (ConfigTipoObjetoCompra) salvarObjeto(configOrigem);
                }

                criarNovaConfiguracaoTipoObjetoCompra(selecionado, configOrigem, isSimulacao);
                contarRegistroProcessado(assistente);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesTiposObjetoCompra().add(new TransporteConfiguracaoTipoObjetoCompra(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem.getTipoObjetoCompra(), configOrigem.getSubtipoObjetoCompra(), e.getMessage()));
            }
        }
        assistente.finaliza();
        return configuracoes;
    }


    private void criarNovaConfiguracaoTipoObjetoCompra(TransporteConfiguracaoContabil selecionado, ConfigTipoObjetoCompra configOrigem, boolean isSimulacao) {
        ConfigTipoObjetoCompra novaConfig = (ConfigTipoObjetoCompra) Util.clonarEmNiveis(configOrigem, 1);
        novaConfig.setId(null);
        novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
        novaConfig.setFimVigencia(null);
        novaConfig.setContasDespesa(Lists.<TipoObjetoCompraContaDespesa>newArrayList());
        adicionarContaDespesaConfiguracaoContaDespesaTipoObjetoCompra(selecionado, configOrigem, novaConfig);
        if (!isSimulacao && !novaConfig.getContasDespesa().isEmpty()) {
            salvarObjeto(novaConfig);
        }
    }

    private void adicionarContaDespesaConfiguracaoContaDespesaTipoObjetoCompra(TransporteConfiguracaoContabil selecionado, ConfigTipoObjetoCompra configOrigem, ConfigTipoObjetoCompra novaConfig) {
        for (TipoObjetoCompraContaDespesa configDesp : configOrigem.getContasDespesa()) {
            ContaDespesa contaDestino = (ContaDespesa) buscarContaDespesa(configDesp.getContaDespesa(), selecionado.getExercicioDestino());
            if (contaDestino != null) {
                TipoObjetoCompraContaDespesa novaConfigDespesa = new TipoObjetoCompraContaDespesa();
                novaConfigDespesa.setConfigTipoObjetoCompra(novaConfig);
                novaConfigDespesa.setContaDespesa(contaDestino);
                novaConfig.getContasDespesa().add(novaConfigDespesa);
            }
        }
    }

    public List<ConfigContaDespTipoPessoa> simularTransporteConfiguracaoContaDespesaTipoPessoa(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte) {
        return transportarConfiguracaoContaDespesaTipoPessoa(selecionado, assistenteTransporte, true);
    }

    private List<ConfigContaDespTipoPessoa> transportarConfiguracaoContaDespesaTipoPessoa(TransporteConfiguracaoContabil selecionado, AssistenteTransporteConfiguracaoContabil assistenteTransporte, boolean isSimulacao) {
        BarraProgressoAssistente assistente = assistenteTransporte.getAssistenteBarraProgresso();
        assistente.inicializa();
        List<ConfigContaDespTipoPessoa> configuracoes = buscarConfiguracaoContaDespeasTipoPessoa(selecionado.getDataTransporte());
        adicionarQuantidadeRegistroParaProcessar(assistenteTransporte.getTipoConfiguracaoContabil(), assistente, configuracoes.size());

        for (ConfigContaDespTipoPessoa configOrigem : configuracoes) {
            try {
                criarNovaConfiguracaoTipoPessoa(selecionado, configOrigem, assistenteTransporte.getTipoConfiguracaoContabil(), isSimulacao);
                contarRegistroProcessado(assistente);
            } catch (Exception e) {
                configOrigem.setMensagemDeErro(e.getMessage());
                configOrigem.setErroDuranteProcessamento(true);
                selecionado.getTransporteConfiguracoesTipoPessoa().add(new TransporteConfiguracaoTipoPessoa(
                    assistenteTransporte.getTipoConfiguracaoContabil(), selecionado, configOrigem.getTipoPessoa(), e.getMessage()));
            }
        }
        assistente.finaliza();
        return configuracoes;
    }

    private void criarNovaConfiguracaoTipoPessoa(TransporteConfiguracaoContabil selecionado, ConfigContaDespTipoPessoa configOrigem, TipoConfiguracaoContabil tipoConfiguracao, boolean isSimulacao) {
        ConfigContaDespTipoPessoa novaConfig = (ConfigContaDespTipoPessoa) Util.clonarEmNiveis(configOrigem, 1);
        novaConfig.setId(null);
        novaConfig.setExercicio(selecionado.getExercicioDestino());
        Conta contaDestino = getContaDespesaTipoPessoa(selecionado, tipoConfiguracao, configOrigem);
        if (contaDestino != null && !isSimulacao) {
            novaConfig.setContaDespesa(contaDestino);
            salvarObjeto(novaConfig);
        }
    }

    private Conta getContaDespesaTipoPessoa(TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, ConfigContaDespTipoPessoa configOrigem) {
        try {
            Conta contaDestino = buscarContaDespesa(configOrigem.getContaDespesa(), selecionado.getExercicioDestino());
            if (contaDestino != null) {
                return contaDestino;
            }
            configOrigem.setErroDuranteProcessamento(true);
            String conteudo = getMensagemErroContaNaoEncontrada(configOrigem.getContaDespesa(), selecionado.getExercicioDestino());
            configOrigem.setMensagemDeErro(conteudo);
            selecionado.getTransporteConfiguracoesTipoPessoa().add(new TransporteConfiguracaoTipoPessoa(tipoConfiguracao, selecionado, configOrigem.getTipoPessoa(), conteudo));
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
        return null;
    }

    private Conta getContaDespesaGrupoPatrimonial(TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, ConfigDespesaBens configOrigem) {
        try {
            Conta contaDestino = buscarContaDespesa(configOrigem.getContaDespesa(), selecionado.getExercicioDestino());
            if (contaDestino != null) {
                return contaDestino;
            }
            String conteudo = getMensagemErroContaNaoEncontrada(configOrigem.getContaDespesa(), selecionado.getExercicioDestino());
            configOrigem.setMensagemDeErro(conteudo);
            configOrigem.setErroDuranteProcessamento(true);
            selecionado.getTransporteConfiguracoesGruposPatrimoniais().add(new TransporteConfiguracaoGrupoPatrimonial(tipoConfiguracao, selecionado, configOrigem.getGrupoBem(), conteudo));
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
        return null;
    }

    private Conta getContaDespesaGrupoMaterial(TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, ConfigGrupoMaterial configOrigem) {
        try {
            Conta contaDestino = buscarContaDespesa(configOrigem.getContaDespesa(), selecionado.getExercicioDestino());
            if (contaDestino != null) {
                return contaDestino;
            }
            String conteudo = getMensagemErroContaNaoEncontrada(configOrigem.getContaDespesa(), selecionado.getExercicioDestino());
            configOrigem.setMensagemDeErro(conteudo);
            configOrigem.setErroDuranteProcessamento(true);
            selecionado.getTransporteConfiguracoesGruposMateriais().add(new TransporteConfiguracaoGrupoMaterial(tipoConfiguracao, selecionado, configOrigem.getGrupoMaterial(), conteudo));
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
        return null;
    }

    private Conta getContaDespesaTipoViagem(TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, TipoViagemContaDespesa configOrigem, ConfigTipoViagemContaDespesa configuracao) {
        try {
            Conta contaDestino = buscarContaDespesa(configOrigem.getContaDespesa(), selecionado.getExercicioDestino());
            if (contaDestino != null) {
                return contaDestino;
            }
            configuracao.setErroDuranteProcessamento(true);
            String conteudo = getMensagemErroContaNaoEncontrada(configOrigem.getContaDespesa(), selecionado.getExercicioDestino());
            configuracao.setMensagemDeErro(conteudo);
            selecionado.getTransporteConfiguracoesTipoViagem().add(new TransporteConfiguracaoTipoViagem(tipoConfiguracao, selecionado, configOrigem.getTipoViagem(), conteudo));
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
        return null;
    }

    private void criarNovaConfiguracaoGrupoMaterial(TransporteConfiguracaoContabil selecionado, ConfigGrupoMaterial configOrigem, TipoConfiguracaoContabil tipoConfiguracao, boolean isSimulacao) {
        ConfigGrupoMaterial novaConfig = (ConfigGrupoMaterial) Util.clonarEmNiveis(configOrigem, 1);
        novaConfig.setId(null);
        novaConfig.setFimVigencia(null);
        novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
        ContaDespesa contaDestino = (ContaDespesa) getContaDespesaGrupoMaterial(selecionado, tipoConfiguracao, configOrigem);
        if (contaDestino != null && !isSimulacao) {
            novaConfig.setContaDespesa(contaDestino);
            salvarObjeto(novaConfig);
        }
    }

    private Conta getContaCDE(Conta contaOrigem, TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, ConfiguracaoEvento configOrigem) {
        Conta contaDestino;
        if (TipoConfiguracaoContabil.CDE_RECEITA_REALIZADA.equals(tipoConfiguracao)
            || TipoConfiguracaoContabil.CDE_CREDITO_RECEBER.equals(tipoConfiguracao)
            || TipoConfiguracaoContabil.CDE_DIVIDA_ATIVA.equals(tipoConfiguracao)) {
            contaDestino = buscarContaReceita(contaOrigem, selecionado.getExercicioDestino());
        } else {
            contaDestino = buscarContaDespesa(contaOrigem, selecionado.getExercicioDestino());
        }
        if (contaDestino != null) {
            return contaDestino;
        }
        configOrigem.setErroDuranteProcessamento(true);
        configOrigem.setMensagemDeErro(getMensagemErroContaNaoEncontrada(contaOrigem, selecionado.getExercicioDestino()));
        selecionado.getTransporteConfiguracoesCDEs().add(new TransporteConfiguracaoCDE(
            tipoConfiguracao,
            selecionado,
            configOrigem,
            getMensagemErroContaNaoEncontrada(contaOrigem, selecionado.getExercicioDestino())));
        return null;
    }

    private boolean verificarContasContabeis(OrigemContaContabil novaConfig, OrigemContaContabil occOrigem, TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao) {
        if (novaConfig.getContaContabil() == null
            && novaConfig.getContaInterEstado() == null
            && novaConfig.getContaInterMunicipal() == null
            && novaConfig.getContaInterUniao() == null
            && novaConfig.getContaIntra() == null) {
            occOrigem.setErroDuranteProcessamento(true);
            String conteudo = "Conta contábil de origem não encontrada para recuperar a conta equivalente.";
            occOrigem.setMensagemDeErro(conteudo);
            selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(tipoConfiguracao, selecionado, occOrigem, conteudo));
            return false;
        }
        return true;
    }

    private boolean hasContaDeDestinacao(AssistenteTransporteConfiguracaoContabil assistente, SubConta conta, Conta contaOrigem, Conta contaDestino) {
        if (contaDestino == null) {
            String conteudo = "Não foi possível recuperar a conta de destinação com o código: " + contaOrigem.getCodigo() + " para o  exercício de " + assistente.getExercicio().getAno() + ".";
            assistente.getAssistenteBarraProgresso().addMensagem(conteudo);
            return false;
        }
        return conta.hasContaDeDestinacaoAdicionada((ContaDeDestinacao) contaDestino);
    }

    private OrigemContaContabil preencherOrigemContaContabil(TransporteConfiguracaoContabil selecionado, OrigemContaContabil novaConfig, OrigemContaContabil occOrigem, TipoConfiguracaoContabil tipoConfiguracao) {
        novaConfig.setId(null);
        novaConfig.setInicioVigencia(getInicioVigencia(selecionado));
        novaConfig.setFimVigencia(null);
        novaConfig.setOrigem(null);

        novaConfig.setContaContabil((ContaContabil) getContaContabilOCC(occOrigem.getContaContabil(), selecionado, tipoConfiguracao, occOrigem));
        novaConfig.setContaInterEstado((ContaContabil) getContaContabilOCC(occOrigem.getContaInterEstado(), selecionado, tipoConfiguracao, occOrigem));
        novaConfig.setContaInterMunicipal((ContaContabil) getContaContabilOCC(occOrigem.getContaInterMunicipal(), selecionado, tipoConfiguracao, occOrigem));
        novaConfig.setContaInterUniao((ContaContabil) getContaContabilOCC(occOrigem.getContaInterUniao(), selecionado, tipoConfiguracao, occOrigem));
        novaConfig.setContaIntra((ContaContabil) getContaContabilOCC(occOrigem.getContaIntra(), selecionado, tipoConfiguracao, occOrigem));
        return novaConfig;
    }

    private Conta getContaOCC(TransporteConfiguracaoContabil selecionado, OCCConta occOrigem, OCCConta novaOCC, TipoConfiguracaoContabil tipoConfiguracao) {
        EntidadeOCC entidadeOCC = novaOCC.getTagOCC().getEntidadeOCC();
        Conta conta = null;
        if (EntidadeOCC.CONTACONTABIL.equals(entidadeOCC)) {
            conta = getContaContabilOCC(occOrigem.getContaOrigem(), selecionado, tipoConfiguracao, occOrigem);
        }
        if (EntidadeOCC.CONTADESPESA.equals(entidadeOCC)) {
            conta = getContaDespesaOCC(occOrigem.getContaOrigem(), selecionado, tipoConfiguracao, occOrigem);
        }
        if (EntidadeOCC.CONTARECEITA.equals(entidadeOCC)) {
            conta = getContaReceitaOCC(occOrigem.getContaOrigem(), selecionado, tipoConfiguracao, occOrigem);
        }
        if (EntidadeOCC.CONTAEXTRAORCAMENTARIA.equals(entidadeOCC)) {
            conta = getContaExtraorcamentariaOCC(occOrigem.getContaOrigem(), selecionado, tipoConfiguracao, occOrigem);
        }
        if (EntidadeOCC.DESTINACAO.equals(entidadeOCC)) {
            conta = getContaDestinacaoOCC(occOrigem.getContaOrigem(), selecionado, tipoConfiguracao, occOrigem);
        }
        if (conta != null) {
            return conta;
        }
        return null;
    }


    private Object salvarObjeto(Object object) {
        return em.merge(object);
    }

    public List<TransporteConfiguracaoCLPLCP> buscarCLP(Date dataTransporte) {
        String sql = " select clp.id as idClp, it.id as idItemEvento from clp  " +
            "   inner join itemeventoclp it on clp.id = it.clp_id  " +
            "   inner join eventocontabil ec on it.eventocontabil_id = ec.id  " +
            "   where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(clp.iniciovigencia) and COALESCE(trunc(clp.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy'))" +
            "   order by ec.codigo asc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        List<TransporteConfiguracaoCLPLCP> toReturn = new ArrayList<>();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            TransporteConfiguracaoCLPLCP item = new TransporteConfiguracaoCLPLCP();
            item.setClp(clpFacade.recuperar(((BigDecimal) obj[0]).longValue()));
            item.setItemEventoCLP(obj[1] != null ? itemEventoCLPFacade.recuperar(((BigDecimal) obj[1]).longValue()) : null);
            toReturn.add(item);
        }
        return toReturn;
    }

    private String getSqlUnidadesContasFinanceiras() {
        return " SELECT sub.id, " +
            "       SUBUNID.UNIDADEORGANIZACIONAL_ID " +
            "  FROM contabancariaentidade cbe " +
            "  INNER JOIN subconta sub   ON cbe.id = sub.contabancariaentidade_id " +
            "  INNER JOIN subcontauniorg subunid ON sub.id = subunid.subconta_id " +
            "  WHERE subunid.exercicio_id = :exercicio " +
            "  and CBE.SITUACAO = :situacao ";
    }

    public List<TransporteSaldoFinanceiro> buscarUnidadesDaContaFinanceira(Date dataTransporte) {
        Exercicio exercicio = getExercicioPorAno(DataUtil.getAno(dataTransporte));
        String sql = getSqlUnidadesContasFinanceiras();
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("situacao", SituacaoConta.ATIVO.name());
        List<TransporteSaldoFinanceiro> retorno = Lists.newArrayList();
        for (Object[] objeto : (List<Object[]>) consulta.getResultList()) {
            TransporteSaldoFinanceiro item = new TransporteSaldoFinanceiro();
            item.setConta(em.find(SubConta.class, ((BigDecimal) objeto[0]).longValue()));
            item.setUnidadeOrganizacional(em.find(UnidadeOrganizacional.class, ((BigDecimal) objeto[1]).longValue()));
            retorno.add(item);
        }
        return retorno;
    }

    private String getSqlContasDeDestinacaoContasFinanceiras() {
        return " SELECT sub.id, " +
            "       cdest.ID as cdest_id" +
            "  FROM contabancariaentidade cbe " +
            "  INNER JOIN subconta sub   ON cbe.id = sub.contabancariaentidade_id " +
            "  INNER JOIN SUBCONTAFONTEREC subfont ON sub.id = subfont.subconta_id " +
            "  inner join conta cdest on SUBFONT.contaDeDestinacao_ID = cdest.id " +
            "  WHERE cdest.exercicio_id = :exercicio " +
            "  and CBE.SITUACAO = :situacao ";
    }

    public List<TransporteSaldoFinanceiro> buscarFontesDaContaFinanceira(Date dataTransporte) {
        Exercicio exercicio = getExercicioPorAno(DataUtil.getAno(dataTransporte));
        String sql = getSqlContasDeDestinacaoContasFinanceiras();
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("situacao", SituacaoConta.ATIVO.name());
        List<TransporteSaldoFinanceiro> retorno = Lists.newArrayList();
        for (Object[] objeto : (List<Object[]>) consulta.getResultList()) {
            TransporteSaldoFinanceiro item = new TransporteSaldoFinanceiro();
            item.setConta(em.find(SubConta.class, ((BigDecimal) objeto[0]).longValue()));
            item.setContaDeDestinacao(em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[1]).longValue()));
            retorno.add(item);
        }
        return retorno;
    }

    public List<UnidadeGestora> buscarUnidadesGestoras(Date dataTransporte) {
        Exercicio exercicio = getExercicioPorAno(DataUtil.getAno(dataTransporte));
        Query q = em.createNativeQuery(" select * from unidadegestora ug where ug.exercicio_id = :exercicio ", UnidadeGestora.class);
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    private List<UnidadeOrganizacional> buscarUnidadesDaUnidadeGestoras(UnidadeGestora unidadeGestora) {
        Query q = em.createNativeQuery(" select un.* from UGESTORAUORGANIZACIONAL ugun " +
            " inner join unidadeorganizacional un on un.id = ugun.unidadeorganizacional_id" +
            " where ugun.unidadegestora_id = :ug_id ", UnidadeOrganizacional.class);
        q.setParameter("ug_id", unidadeGestora.getId());
        return q.getResultList();
    }

    public Exercicio getExercicioPorAno(Integer ano) {
        return exercicioFacade.getExercicioPorAno(ano);
    }

    private List<ConfigObrigacaoAPagar> buscarConfiguracaoObrigacoesAPagar(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join ConfigObrigacaoAPagar config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigObrigacaoAPagar.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigEmpenho> buscarConfiguracaoEmpenho(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join configempenho config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigEmpenho.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigEmpenhoRestoPagar> buscarConfiguracaoEmpenhoResto(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join configempenhorestopagar config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigEmpenhoRestoPagar.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigCancelamentoResto> buscarConfiguracaoCancelamentoResto(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join configcancelamentoresto config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigCancelamentoResto.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigLiquidacao> buscarConfiguracaoLiquidacao(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join configliquidacao config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigLiquidacao.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigLiquidacaoResPagar> buscarConfiguracaoLiquidacaoResto(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join configliquidacaorespagar config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigLiquidacaoResPagar.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigPagamento> buscarConfiguracaoPagamento(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join configpagamento config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigPagamento.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigPagamentoRestoPagar> buscarConfiguracaoPagamentoResto(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join configpagamentorestopagar config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigPagamentoRestoPagar.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigReceitaRealizada> buscarConfiguracaoReceitaRealizada(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join configreceitarealizada config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigReceitaRealizada.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigCreditoReceber> buscarConfiguracaoCreditoReceber(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join ConfigCreditoReceber config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigCreditoReceber.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigDividaAtivaContabil> buscarConfiguracaoDividaAtiva(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join ConfigDividaAtivaContabil config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigDividaAtivaContabil.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigInvestimento> buscarConfiguracaoInvestimento(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join ConfigInvestimento config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigInvestimento.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigPatrimonioLiquido> buscarConfiguracaoPatrimonioLiquido(Date dataTransporte) {
        String sql = " select ce.*, config.* from configuracaoevento ce "
            + "        inner join ConfigPatrimonioLiquido config on config.id = ce.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(ce.iniciovigencia) and COALESCE(trunc(ce.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigPatrimonioLiquido.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<OCCConta> buscarOCCConta(Date dataTransporte, String consulta) {
        Query q = em.createNativeQuery(consulta, OCCConta.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<OCCBanco> buscarOCCBanco(Date dataTransporte) {
        String sql = " select b.*, occ.* from occbanco b " +
            "           inner join subconta sub on sub.id = b.subconta_id " +
            "           inner join origemcontacontabil occ on occ.id = b.id " +
            "           inner join tagocc tag on tag.id = occ.tagocc_id  " +
            "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(occ.iniciovigencia) and COALESCE(trunc(occ.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, OCCBanco.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<OccClassePessoa> buscarOCCClassePessoa(Date dataTransporte) {
        String sql = " select c.*, occ.* " +
            "           from occclassepessoa c " +
            "           inner join classecredor cc on cc.id = c.classepessoa_id " +
            "           inner join origemcontacontabil occ on occ.id = c.id " +
            "           inner join tagocc tag on tag.id = occ.tagocc_id  " +
            "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN occ.iniciovigencia and COALESCE(occ.fimVigencia, to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, OccClassePessoa.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<OCCGrupoBem> buscarOCCGrupoPatrimonial(Date dataTransporte) {
        String sql = " select og.*, occ.* " +
            "           from occgrupobem og " +
            "           inner join grupobem gb on gb.id = og.grupobem_id " +
            "           inner join origemcontacontabil occ on occ.id = og.id " +
            "           inner join tagocc tag on tag.id = occ.tagocc_id  " +
            "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(occ.iniciovigencia) and COALESCE(trunc(occ.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, OCCGrupoBem.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<OCCGrupoMaterial> buscarOCCGrupoMaterial(Date dataTransporte) {
        String sql = " select og.*, occ.* " +
            "           from occgrupomaterial og " +
            "           inner join grupomaterial gb on gb.id = og.grupomaterial_id " +
            "           inner join origemcontacontabil occ on occ.id = og.id " +
            "           inner join tagocc tag on tag.id = occ.tagocc_id  " +
            "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(occ.iniciovigencia) and COALESCE(trunc(occ.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, OCCGrupoMaterial.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<OCCNaturezaDividaPublica> buscarOCCNaturezaDividaPublica(Date dataTransporte) {
        String sql = " select nat.*, occ.* " +
            "            from occnaturezadividapublica nat " +
            "            inner join categoriadividapublica cat on cat.id = nat.categoriadividapublica_id " +
            "            inner join origemcontacontabil occ on occ.id = nat.id " +
            "            inner join tagocc tag on tag.id = occ.tagocc_id " +
            "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(occ.iniciovigencia) and COALESCE(trunc(occ.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, OCCNaturezaDividaPublica.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<OCCTipoPassivoAtuarial> buscarOCCTipoPassivoAtuarial(Date dataTransporte) {
        String sql = " select o.*, occ.* " +
            "            from occtipopassivoatuarial o  " +
            "            inner join tipopassivoatuarial tipo on tipo.id = o.tipopassivoatuarial_id " +
            "            inner join origemcontacontabil occ on occ.id = o.id " +
            "            inner join tagocc tag on tag.id = occ.tagocc_id " +
            "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(occ.iniciovigencia) and COALESCE(trunc(occ.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, OCCTipoPassivoAtuarial.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<OCCOrigemRecurso> buscarOCCOrigemRecurso(Date dataTransporte) {
        String sql = " select o.*, occ.* " +
            "            from occorigemrecurso o  " +
            "            inner join origemcontacontabil occ on occ.id = o.id " +
            "            inner join tagocc tag on tag.id = occ.tagocc_id " +
            "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(occ.iniciovigencia) and COALESCE(trunc(occ.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, OCCOrigemRecurso.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }


    private List<ConfigDespesaBens> buscarConfiguracaoContaDespeasGrupoPatrimonial(Date dataTransporte) {
        String sql = " select config.* " +
            "           from configdespesabens config " +
            "           inner join grupobem g on g.id = config.grupobem_id " +
            "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(config.iniciovigencia) and COALESCE(trunc(config.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigDespesaBens.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigGrupoMaterial> buscarConfiguracaoContaDespeasGrupoMaterial(Date dataTransporte) {
        String sql = " select config.* " +
            "           from configgrupomaterial config " +
            "           inner join grupomaterial g on g.id = config.grupomaterial_id " +
            "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(config.iniciovigencia) and COALESCE(trunc(config.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigGrupoMaterial.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigTipoViagemContaDespesa> buscarConfiguracaoContaDespesaTipoViagem(Date dataTransporte) {
        String sql = " select distinct config.* " +
            "from configtipoviagemcontadesp config " +
            "inner join tipoviagemcontadespesa tipo on tipo.configtipoviagemcontadesp_id = config.id "
            + "         where to_date(:dataTransporte,'dd/MM/yyyy') BETWEEN trunc(config.iniciovigencia) and COALESCE(trunc(config.fimVigencia), to_date(:dataTransporte, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigTipoViagemContaDespesa.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigContaDespTipoPessoa> buscarConfiguracaoContaDespeasTipoPessoa(Date dataTransporte) {
        String sql = " select config.* " +
            "           from configcontadesptipopessoa config " +
            "           inner join exercicio ex on ex.id = config.exercicio_id " +
            "           inner join conta c on c.id = config.contadespesa_id " +
            "         where config.exercicio_id = :idExercicio ";

        Query q = em.createNativeQuery(sql, ConfigContaDespTipoPessoa.class);
        q.setParameter("idExercicio", getExercicioPorAno(DataUtil.getAno(dataTransporte)));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<ConfigTipoObjetoCompra> buscarConfiguracoesTipoObjetoCompra(Date dataTransporte) {
        String sql = "   select " +
            "     config.* " +
            "   from configtipoobjetocompra config " +
            "   where to_date(:dataTransporte,'dd/MM/yyyy') between config.iniciovigencia and coalesce(config.fimvigencia, to_date(:dataTransporte,'dd/MM/yyyy')) ";
        ;
        Query q = em.createNativeQuery(sql, ConfigTipoObjetoCompra.class);
        q.setParameter("dataTransporte", DataUtil.getDataFormatada(dataTransporte));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<GrupoOrcamentario> buscarGruposOrcamentarios(Exercicio exercicio) {
        String sql = "   select go.* " +
            "   from GrupoOrcamentario go " +
            "   where go.exercicio_id = :exercicio ";
        Query q = em.createNativeQuery(sql, GrupoOrcamentario.class);
        q.setParameter("exercicio", exercicio.getId());
        List<GrupoOrcamentario> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado;
        }
        return Lists.newArrayList();
    }


    private Conta buscarContaDeDestinacao(Conta contaDeDestinacao, Exercicio proximoExercicio) {
        List<Conta> contasEquivalentes = contaFacade.recuperarContasDestinacaoEquivalentesPorId(contaDeDestinacao, proximoExercicio);
        if (!contasEquivalentes.isEmpty()) {
            return contasEquivalentes.get(0);
        }
        return null;
    }

    private Conta buscarContaDespesa(Conta conta, Exercicio exercicioDestino) {
        List<Conta> contas = contaFacade.recuperarContasDespesaEquivalentesPorId(conta, exercicioDestino);
        if (contas != null && !contas.isEmpty()) {
            return contas.get(0);
        }
        return null;
    }

    private Conta buscarContaReceita(Conta conta, Exercicio exercicioDestino) {
        List<Conta> contas = contaFacade.recuperarContaReceitaEquivalentePorId(conta, exercicioDestino);
        if (contas != null && !contas.isEmpty()) {
            return contas.get(0);
        }
        return null;
    }

    private Conta getContaContabil(Conta contaOrigem, TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, LCP lcpOrigem) {
        String conteudo = "";
        Conta conta = null;
        try {
            if (contaOrigem == null) {
                conteudo = "Conta contábil de origem não encontrada para recuperar a conta equivalente.";
            } else {
                List<ContaContabil> contas = planoDeContasFacade.getContaFacade().recuperarContaContabilEquivalentePorCodigo(contaOrigem.getCodigo(), selecionado.getExercicioDestino());
                if (contas.isEmpty()) {
                    conteudo = "Nenhuma conta equivalente foi encontrada para a conta " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesLCPs().add(new TransporteConfiguracaoLCP(tipoConfiguracao, selecionado, lcpOrigem, conteudo));
                }
                if (contas.size() > 1) {
                    conteudo = "Foi encontrado mais que 1(UMA) conta equivalente para a conta " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesLCPs().add(new TransporteConfiguracaoLCP(tipoConfiguracao, selecionado, lcpOrigem, conteudo));
                }
                if (contas.size() == 1) {
                    conta = contas.get(0);
                }
            }
            return conta;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(conteudo);
        }
    }

    private Conta getContaContabilOCC(Conta contaOrigem, TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, OrigemContaContabil occOrigem) {
        String conteudo = "";
        Conta conta = null;
        try {
            if (contaOrigem != null) {
                List<ContaContabil> contas = planoDeContasFacade.getContaFacade().recuperarContaContabilEquivalentePorCodigo(contaOrigem.getCodigo(), selecionado.getExercicioDestino());
                if (contas.isEmpty()) {
                    occOrigem.setErroDuranteProcessamento(true);
                    conteudo = "Nenhuma conta equivalente foi encontrada para a conta " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(tipoConfiguracao, selecionado, occOrigem, conteudo));
                }
                if (contas.size() > 1) {
                    occOrigem.setErroDuranteProcessamento(true);
                    conteudo = "Foi encontrado mais que 1(UMA) conta equivalente para a conta " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(tipoConfiguracao, selecionado, occOrigem, conteudo));
                }
                if (contas.size() == 1) {
                    conta = contas.get(0);
                }
            }
            return conta;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(conteudo);
        }
    }

    private Conta getContaDespesaOCC(Conta contaOrigem, TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, OrigemContaContabil occOrigem) {
        String conteudo = "";
        Conta conta = null;
        try {
            if (contaOrigem != null) {
                List<Conta> contas = planoDeContasFacade.getContaFacade().recuperarContasDespesaEquivalentesPorId(contaOrigem, selecionado.getExercicioDestino());
                if (contas.isEmpty()) {
                    occOrigem.setErroDuranteProcessamento(true);
                    conteudo = "Nenhuma conta equivalente foi encontrada para a conta de despesa " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(tipoConfiguracao, selecionado, occOrigem, conteudo));
                }
                if (contas.size() > 1) {
                    occOrigem.setErroDuranteProcessamento(true);
                    conteudo = "Foi encontrado mais que 1(UMA) conta equivalente para a conta de despesa " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(tipoConfiguracao, selecionado, occOrigem, conteudo));
                }
                if (contas.size() == 1) {
                    conta = contas.get(0);
                }
            }
            return conta;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(conteudo);
        }
    }

    private Conta getContaReceitaOCC(Conta contaOrigem, TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, OrigemContaContabil occOrigem) {
        String conteudo = "";
        Conta conta = null;
        try {
            if (contaOrigem != null) {
                List<Conta> contas = planoDeContasFacade.getContaFacade().recuperarContaReceitaEquivalentePorId(contaOrigem, selecionado.getExercicioDestino());
                if (contas.isEmpty()) {
                    occOrigem.setErroDuranteProcessamento(true);
                    conteudo = "Nenhuma conta equivalente foi encontrada para a conta de receita " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(tipoConfiguracao, selecionado, occOrigem, conteudo));
                }
                if (contas.size() > 1) {
                    occOrigem.setErroDuranteProcessamento(true);
                    conteudo = "Foi encontrado mais que 1(UMA) conta equivalente para a conta de receita " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(tipoConfiguracao, selecionado, occOrigem, conteudo));
                }
                if (contas.size() == 1) {
                    conta = contas.get(0);
                }
            }
            return conta;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(conteudo);
        }
    }

    private Conta getContaExtraorcamentariaOCC(Conta contaOrigem, TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, OrigemContaContabil occOrigem) {
        String conteudo = "";
        Conta conta = null;
        try {
            if (contaOrigem != null) {
                List<ContaExtraorcamentaria> contas = planoDeContasFacade.getContaFacade().recuperarContaExtraEquivalentePorId(contaOrigem, selecionado.getExercicioDestino());
                if (contas.isEmpty()) {
                    occOrigem.setErroDuranteProcessamento(true);
                    conteudo = "Nenhuma conta equivalente foi encontrada para a conta extraorçamentária " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(tipoConfiguracao, selecionado, occOrigem, conteudo));
                }
                if (contas.size() > 1) {
                    occOrigem.setErroDuranteProcessamento(true);
                    conteudo = "Foi encontrado mais que 1(UMA) conta equivalente para a conta extraorçamentária " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(tipoConfiguracao, selecionado, occOrigem, conteudo));
                }
                if (contas.size() == 1) {
                    conta = contas.get(0);
                }
            }
            return conta;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(conteudo);
        }
    }

    private Conta getContaDestinacaoOCC(Conta contaOrigem, TransporteConfiguracaoContabil selecionado, TipoConfiguracaoContabil tipoConfiguracao, OrigemContaContabil occOrigem) {
        String conteudo = "";
        Conta conta = null;
        try {
            if (contaOrigem != null) {
                List<Conta> contas = planoDeContasFacade.getContaFacade().recuperarContasDestinacaoEquivalentesPorId(contaOrigem, selecionado.getExercicioDestino());
                if (contas.isEmpty()) {
                    occOrigem.setErroDuranteProcessamento(true);
                    conteudo = "Nenhuma conta equivalente foi encontrada para a conta de destinação " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(tipoConfiguracao, selecionado, occOrigem, conteudo));
                }
                if (contas.size() > 1) {
                    occOrigem.setErroDuranteProcessamento(true);
                    conteudo = "Foi encontrado mais que 1(UMA) conta equivalente para a conta de destinação " + contaOrigem + " no exercício de " + selecionado.getExercicioDestino().getAno() + ".";
                    selecionado.getTransporteConfiguracoesOCCs().add(new TransporteConfiguracaoOCC(tipoConfiguracao, selecionado, occOrigem, conteudo));
                }
                if (contas.size() == 1) {
                    conta = contas.get(0);
                }
            }
            return conta;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(conteudo);
        }
    }

    private Date getInicioVigencia(TransporteConfiguracaoContabil selecionado) {
        return DataUtil.getPrimeiroDiaAno(selecionado.getExercicioDestino().getAno());
    }


    private String getCabecalhoHTMLParaLogsPDF(AssistenteTransporteConfiguracaoContabil assistente) {
        return "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"

            + " <head>\n"
            + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
            + " <title>"
            + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">"
            + " </title>"
            + " </head>\n"

            + " <body style='font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 10px;'>"
            + "<div style='border: 1px solid black;text-align: left; padding : 3px;'>\n"
            + " <table>" + "<tr>"
            + " <td><img src='" + assistente.getImagem() + "' alt='Smiley face' height='80' width='75' /></td>   "
            + " <td><b> PREFEITURA MUNICIPAL DE RIO BRANCO <br/>\n"
            + "         MUNICÍPIO DE RIO BRANCO<br/>\n"
            + "         INCONSISTÊNCIAS NO TRANSPORTE DE CONFIGURAÇÃO CONTÁBIL</b></td>\n"
            + "</tr>" + "</table>"
            + "</div>\n"

            + "<div style='border: 1px solid black;text-align: left; margin-top: -1px; padding : 3px;'>\n"
            + "<ul>"
            + "<li>Data de Transporte: <b style='margin-left: 12px'>" + Util.formatterDiaMesAno.format(assistente.getDataTransporte()) + "</b></li>"
            + "<li>Exercício de Destino: <b>" + assistente.getExercicio().getAno() + "</b></li>"
            + "<li>Usuário Responsável: <b>" + assistente.getUsuarioSistema().getNome() + "</b></li>"
            + "</ul>\n";
    }

    private String getConteudoHTMLParaLogPDF(AssistenteTransporteConfiguracaoContabil assistente) {
        String log = "";
        for (Map.Entry<TipoConfiguracaoContabil, HashSet<String>> mapa : assistente.getMapaMensagens().entrySet()) {
            log += "<div style='text-align: left;text-decoration: underline'>"
                + "<b>" + mapa.getKey().toString().toUpperCase() + "</b>"
                + "</div>"
                + "<div style='text-align: left'>";
            for (String mensagem : mapa.getValue()) {
                log += "<b>-    </b>" + mensagem + "</br>";
            }
            log += "</div></br>";
        }
        log += " </body>"
            + " </html>"
            + "</div></br>";
        log += "<div style='text-align: left;font-color: blue'>"
            + "OBS: Para mais detalhes do erro, acesse o visualizar do transporte."
            + "</div>";
        return log;
    }

    public void montarMapaLog(AssistenteTransporteConfiguracaoContabil assistente, TransporteConfiguracaoContabil selecionado) {
        assistente.setMapaMensagens(Maps.<TipoConfiguracaoContabil, HashSet<String>>newHashMap());
        adicionarConteudoLogOCC(assistente, selecionado);
        adicionarConteudoLogCDE(assistente, selecionado);
        adicionarConteudoLogCLP(assistente, selecionado);
        adicionarConteudoLogLCP(assistente, selecionado);
        adicionarConteudoGrupoLogPatrimonial(assistente, selecionado);
        adicionarConteudoLogGrupoMaterial(assistente, selecionado);
        String log = getCabecalhoHTMLParaLogsPDF(assistente) + getConteudoHTMLParaLogPDF(assistente);
        assistente.setConteudoLog(log);
    }

    private void adicionarConteudoLogGrupoMaterial(AssistenteTransporteConfiguracaoContabil assistente, TransporteConfiguracaoContabil selecionado) {
        for (TransporteConfiguracaoGrupoMaterial gm : selecionado.getTransporteConfiguracoesGruposMateriais()) {
            if (assistente.getMapaMensagens().containsKey(gm.getTipoConfiguracaoContabil())) {
                for (Map.Entry<TipoConfiguracaoContabil, HashSet<String>> log : assistente.getMapaMensagens().entrySet()) {
                    if (log.getKey().equals(gm.getTipoConfiguracaoContabil())) {
                        assistente.getMapaMensagens().get(gm.getTipoConfiguracaoContabil()).add(gm.getLog());
                    }
                }
            } else {
                assistente.getMapaMensagens().put(gm.getTipoConfiguracaoContabil(), new HashSet<String>());
                assistente.getMapaMensagens().get(gm.getTipoConfiguracaoContabil()).add(gm.getLog());
            }
        }
    }

    private void adicionarConteudoGrupoLogPatrimonial(AssistenteTransporteConfiguracaoContabil assistente, TransporteConfiguracaoContabil selecionado) {
        for (TransporteConfiguracaoGrupoPatrimonial gp : selecionado.getTransporteConfiguracoesGruposPatrimoniais()) {
            if (assistente.getMapaMensagens().containsKey(gp.getTipoConfiguracaoContabil())) {
                for (Map.Entry<TipoConfiguracaoContabil, HashSet<String>> log : assistente.getMapaMensagens().entrySet()) {
                    if (log.getKey().equals(gp.getTipoConfiguracaoContabil())) {
                        assistente.getMapaMensagens().get(gp.getTipoConfiguracaoContabil()).add(gp.getLog());
                    }
                }
            } else {
                assistente.getMapaMensagens().put(gp.getTipoConfiguracaoContabil(), new HashSet<String>());
                assistente.getMapaMensagens().get(gp.getTipoConfiguracaoContabil()).add(gp.getLog());
            }
        }
    }

    private void adicionarConteudoLogLCP(AssistenteTransporteConfiguracaoContabil assistente, TransporteConfiguracaoContabil selecionado) {
        for (TransporteConfiguracaoLCP lcp : selecionado.getTransporteConfiguracoesLCPs()) {
            if (assistente.getMapaMensagens().containsKey(lcp.getTipoConfiguracaoContabil())) {
                for (Map.Entry<TipoConfiguracaoContabil, HashSet<String>> log : assistente.getMapaMensagens().entrySet()) {
                    if (log.getKey().equals(lcp.getTipoConfiguracaoContabil())) {
                        assistente.getMapaMensagens().get(lcp.getTipoConfiguracaoContabil()).add(lcp.getLog());
                    }
                }
            } else {
                assistente.getMapaMensagens().put(lcp.getTipoConfiguracaoContabil(), new HashSet<String>());
                assistente.getMapaMensagens().get(lcp.getTipoConfiguracaoContabil()).add(lcp.getLog());
            }
        }
    }

    private void adicionarConteudoLogCLP(AssistenteTransporteConfiguracaoContabil assistente, TransporteConfiguracaoContabil selecionado) {
        for (TransporteConfiguracaoCLP clp : selecionado.getTransporteConfiguracoesCLPs()) {
            if (assistente.getMapaMensagens().containsKey(clp.getTipoConfiguracaoContabil())) {
                for (Map.Entry<TipoConfiguracaoContabil, HashSet<String>> log : assistente.getMapaMensagens().entrySet()) {
                    if (log.getKey().equals(clp.getTipoConfiguracaoContabil())) {
                        assistente.getMapaMensagens().get(clp.getTipoConfiguracaoContabil()).add(clp.getLog());
                    }
                }
            } else {
                assistente.getMapaMensagens().put(clp.getTipoConfiguracaoContabil(), new HashSet<String>());
                assistente.getMapaMensagens().get(clp.getTipoConfiguracaoContabil()).add(clp.getLog());
            }
        }
    }

    private void adicionarConteudoLogCDE(AssistenteTransporteConfiguracaoContabil assistente, TransporteConfiguracaoContabil selecionado) {
        for (TransporteConfiguracaoCDE cde : selecionado.getTransporteConfiguracoesCDEs()) {
            if (assistente.getMapaMensagens().containsKey(cde.getTipoConfiguracaoContabil())) {
                for (Map.Entry<TipoConfiguracaoContabil, HashSet<String>> log : assistente.getMapaMensagens().entrySet()) {
                    if (log.getKey().equals(cde.getTipoConfiguracaoContabil())) {
                        assistente.getMapaMensagens().get(cde.getTipoConfiguracaoContabil()).add(cde.getLog());
                    }
                }
            } else {
                assistente.getMapaMensagens().put(cde.getTipoConfiguracaoContabil(), new HashSet<String>());
                assistente.getMapaMensagens().get(cde.getTipoConfiguracaoContabil()).add(cde.getLog());
            }
        }
    }

    private void adicionarConteudoLogOCC(AssistenteTransporteConfiguracaoContabil assistente, TransporteConfiguracaoContabil selecionado) {
        for (TransporteConfiguracaoOCC occ : selecionado.getTransporteConfiguracoesOCCs()) {
            if (assistente.getMapaMensagens().containsKey(occ.getTipoConfiguracaoContabil())) {
                for (Map.Entry<TipoConfiguracaoContabil, HashSet<String>> log : assistente.getMapaMensagens().entrySet()) {
                    if (log.getKey().equals(occ.getTipoConfiguracaoContabil())) {
                        assistente.getMapaMensagens().get(occ.getTipoConfiguracaoContabil()).add(occ.getLog());
                    }
                }
            } else {
                assistente.getMapaMensagens().put(occ.getTipoConfiguracaoContabil(), new HashSet<String>());
                assistente.getMapaMensagens().get(occ.getTipoConfiguracaoContabil()).add(occ.getLog());
            }
        }
    }

    public void validarPlanoContas(Exercicio exercicio, ValidacaoException ve) {
        String msg = "O exercício de " + exercicio.getAno() + " não possui um Plano de Contas ";
        try {
            if (!planoDeContasFacade.verificarSeExistePlanoContaEspecificoParaExercicio(exercicio, ClasseDaConta.CONTABIL)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(msg + ClasseDaConta.CONTABIL.getDescricao() + ".");
            }
            if (!planoDeContasFacade.verificarSeExistePlanoContaEspecificoParaExercicio(exercicio, ClasseDaConta.DESTINACAO)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(msg + ClasseDaConta.DESTINACAO.getDescricao() + ".");
            }
            if (!planoDeContasFacade.verificarSeExistePlanoContaEspecificoParaExercicio(exercicio, ClasseDaConta.EXTRAORCAMENTARIA)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(msg + ClasseDaConta.EXTRAORCAMENTARIA.getDescricao() + ".");
            }
            if (!planoDeContasFacade.verificarSeExistePlanoContaEspecificoParaExercicio(exercicio, ClasseDaConta.DESPESA)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(msg + ClasseDaConta.DESPESA.getDescricao() + ".");
            }
            if (!planoDeContasFacade.verificarSeExistePlanoContaEspecificoParaExercicio(exercicio, ClasseDaConta.RECEITA)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(msg + ClasseDaConta.RECEITA.getDescricao() + ".");
            }
        } catch (Exception ex) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Erro ao recuperar plano de contas para o exercício de " + exercicio.getAno() + ".");
        }
    }

    public boolean hasTransportePeloTipo(TransporteConfiguracaoContabil transporteConfiguracaoContabil, TipoConfiguracaoContabil tipoConfiguracaoContabil) {
        String sql = " select 1 from TransporteConfiguracaoContabil tcc " +
            " inner join TRANSPORTECONFIGTIPOCONTABIL tctc on tcc.id = TCTC.TRANSPORTECONFIGCONTABIL_ID " +
            " where tcc.exerciciodestino_id = :exercicioDestino " +
            " and TCTC.TIPOCONFIGURACAOCONTABIL = :tipoConfig ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicioDestino", transporteConfiguracaoContabil.getExercicioDestino().getId());
        q.setParameter("tipoConfig", tipoConfiguracaoContabil.name());
        return !q.getResultList().isEmpty();
    }
}
