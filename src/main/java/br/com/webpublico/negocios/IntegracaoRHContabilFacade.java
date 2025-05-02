package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamento;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.esocial.TipoRegimePrevidenciario;
import br.com.webpublico.enums.rh.integracao.TipoContabilizacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class IntegracaoRHContabilFacade extends AbstractFacade<IntegracaoRHContabil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private ObrigacaoAPagarFacade obrigacaoAPagarFacade;

    public IntegracaoRHContabilFacade() {
        super(IntegracaoRHContabil.class);
    }

    public IntegracaoRHContabil salvarIntegracao(IntegracaoRHContabil entity) {
        return em.merge(entity);
    }

    @Override
    public IntegracaoRHContabil recuperar(Object id) {
        IntegracaoRHContabil integracaoRHContabil = em.find(IntegracaoRHContabil.class, id);
        integracaoRHContabil.getFolhasDePagamento().size();
        integracaoRHContabil.getTipos().size();
        integracaoRHContabil.getItens().size();
        for (ItemIntegracaoRHContabil itemIntegracaoRHContabil : integracaoRHContabil.getItens()) {
            itemIntegracaoRHContabil.getServidores().size();
            itemIntegracaoRHContabil.getRetencoes().size();
            for (RetencaoIntegracaoRHContabil retencaoIntegracaoRHContabil : itemIntegracaoRHContabil.getRetencoes()) {
                retencaoIntegracaoRHContabil.getServidores().size();
            }
        }
        return integracaoRHContabil;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @Asynchronous
    public Future<IntegracaoRHContabil> finalizarIntegracao(AssistenteIntegracaoRHContabil assistenteAberturaFechamentoExercicio) {
        IntegracaoRHContabil entity = assistenteAberturaFechamentoExercicio.getIntegracaoRHContabil();
        try {
            BarraProgressoItens barraProgressoItens = assistenteAberturaFechamentoExercicio.getBarraProgressoItens();
            barraProgressoItens.inicializa();
            barraProgressoItens.setTotal(entity.getItens().size());
            entity.setSituacao(SituacaoIntegracaoRHContabil.PROCESSADO);
            entity = integrar(assistenteAberturaFechamentoExercicio, entity, barraProgressoItens);
        } catch (ValidacaoException ex) {
            logger.error("Erro ao finalizar a integração RH/Contábil: {}", ex);
            entity.setSituacao(SituacaoIntegracaoRHContabil.VALIDADO);
            assistenteAberturaFechamentoExercicio.getMensagens().add(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Erro ao finalizar a integração RH/Contábil: {}", ex);
            entity.setSituacao(SituacaoIntegracaoRHContabil.VALIDADO);
            assistenteAberturaFechamentoExercicio.getMensagens().add(ex.getMessage());
        } finally {
            if (!assistenteAberturaFechamentoExercicio.getMensagens().isEmpty()) {
                assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
                throw new ExcecaoNegocioGenerica("Erro ao realizar a integração da folha");
            }
        }
        assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
        return new AsyncResult<>(entity);
    }

    private IntegracaoRHContabil integrar(AssistenteIntegracaoRHContabil assistenteAberturaFechamentoExercicio, IntegracaoRHContabil entity, BarraProgressoItens barraProgressoItens) {
        entity = salvarRetornando(entity);
        for (ItemIntegracaoRHContabil itemIntegracaoRHContabil : entity.getItens()) {
            try {
                if (TipoContabilizacao.OBRIGACAO_A_APAGAR.equals(itemIntegracaoRHContabil.getTipoContabilizacao())) {
                    criarIntegracaoContabilObrigacaoAPagar(entity, itemIntegracaoRHContabil);
                } else {
                    criarIntegracaoContabilEmpenho(entity, itemIntegracaoRHContabil);
                }
                barraProgressoItens.setProcessados(barraProgressoItens.getProcessados() + 1);
            } catch (ValidacaoException ex) {
                logger.error("Erro ao integrar: {}", ex);
                assistenteAberturaFechamentoExercicio.getMensagens().add(ex.getMessage());
            } catch (ExcecaoNegocioGenerica ex) {
                logger.error("Erro ao integrar: {}", ex);
                assistenteAberturaFechamentoExercicio.getMensagens().add(ex.getMessage());
            } catch (Exception ex) {
                logger.error("Erro ao integrar: {}", ex);
                assistenteAberturaFechamentoExercicio.getMensagens().add(ex.getMessage());
            } finally {
                if (!assistenteAberturaFechamentoExercicio.getMensagens().isEmpty()) {
                    assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
                    throw new ExcecaoNegocioGenerica("Erro ao realizar a integração da folha");
                } else {
                    continue;
                }
            }
        }
        return entity;
    }

    public void criarIntegracaoContabilEmpenho(IntegracaoRHContabil entity, ItemIntegracaoRHContabil itemIntegracaoRHContabil) {
        Empenho empenho = new Empenho();
        empenho.setDataEmpenho(entity.getParametro().getDataEmpenho());
        empenho.setExercicio(entity.getExercicio());
        empenho.setUnidadeOrganizacional(itemIntegracaoRHContabil.getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional());
        empenho.setUnidadeOrganizacionalAdm(hierarquiaOrganizacionalFacade.getHierarquiaAdministrativaDaOrcamentaria(empenho.getUnidadeOrganizacional(), empenho.getDataEmpenho()).getSubordinada());
        empenho.setTipoEmpenho(TipoEmpenho.ORDINARIO);
        empenho.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
        empenho.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
        empenho.setDespesaORC(itemIntegracaoRHContabil.getDespesaORC());
        empenho.setContaDespesa((ContaDespesa) empenho.getDespesaORC().getContaDeDespesa());
        empenho.setTipoContaDespesa(itemIntegracaoRHContabil.getTipoContaDespesa());
        empenho.setSubTipoDespesa(getSubTipoDespesa(itemIntegracaoRHContabil));
        empenho.setFonteDespesaORC(itemIntegracaoRHContabil.getDespesaORC().getFonteDespesaORCDaFonte(itemIntegracaoRHContabil.getFonteDeRecursos()));
        empenho.setContaDeDestinacao(empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao());
        empenho.setFonteDeRecursos(itemIntegracaoRHContabil.getFonteDeRecursos());
        empenho.setExtensaoFonteRecurso(empenho.getFonteDespesaORC().getProvisaoPPAFonte().getExtensaoFonteRecurso());
        empenho.setFornecedor(itemIntegracaoRHContabil.getFornecedor());
        empenho.setClasseCredor(itemIntegracaoRHContabil.getClasseCredor());
        empenho.setComplementoHistorico(entity.getParametro().getHistorico());
        empenho.setValor(itemIntegracaoRHContabil.getValor());
        empenho.setSaldo(entity.isTipoSelecionado(TipoIntegradorRHContabil.LIQUIDACAO) ? BigDecimal.ZERO : itemIntegracaoRHContabil.getValor());
        empenho.setTransportado(Boolean.FALSE);
        empenho.setUsuarioSistema(entity.getUsuarioSistema());
        EventoContabil eventoContabil = empenhoFacade.buscarEventoContabil(empenho);
        if (eventoContabil != null) {
            empenho.setEventoContabil(eventoContabil);
        }
        if (itemIntegracaoRHContabil.getDesdobramento() != null) {
            empenho.setDesdobramentos(criarDesdrobramentoEmpenho(entity, empenho, itemIntegracaoRHContabil));
        }
        empenho.setEmpenhoEstornos(Lists.<EmpenhoEstorno>newArrayList());
        empenho.setLiquidacoes(Lists.<Liquidacao>newArrayList());
        empenho.setObrigacoesPagar(Lists.<EmpenhoObrigacaoPagar>newArrayList());
        empenho.setItemIntegracaoRHContabil(itemIntegracaoRHContabil);

        empenhoFacade.geraNumeroEmpenho(empenho);
        empenho = empenhoFacade.salvarEmpenho(empenho);

        criarIntegracaoContabilLiquidacao(entity, itemIntegracaoRHContabil, empenho);
    }

    private void criarIntegracaoContabilLiquidacao(IntegracaoRHContabil entity, ItemIntegracaoRHContabil itemIntegracaoRHContabil, Empenho empenho) {
        if (entity.isTipoSelecionado(TipoIntegradorRHContabil.LIQUIDACAO)) {
            Liquidacao liquidacao = new Liquidacao();
            liquidacao.setEmpenho(empenho);
            liquidacao.setDataLiquidacao(entity.getParametro().getDataLiquidacao());
            liquidacao.setDataRegistro(new Date());
            liquidacao.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO);
            liquidacao.setCategoriaOrcamentaria(empenho.getCategoriaOrcamentaria());
            liquidacao.setSaldo(entity.isTipoSelecionado(TipoIntegradorRHContabil.PAGAMENTO) ? BigDecimal.ZERO : empenho.getValor());
            liquidacao.setValor(empenho.getValor());
            liquidacao.setUnidadeOrganizacional(empenho.getUnidadeOrganizacional());
            liquidacao.setUnidadeOrganizacionalAdm(empenho.getUnidadeOrganizacionalAdm());
            liquidacao.setComplemento(empenho.getComplementoHistorico());
            liquidacao.setExercicio(empenho.getExercicio());
            liquidacao.setTransportado(Boolean.FALSE);
            liquidacao.setUsuarioSistema(entity.getUsuarioSistema());
            liquidacao.setDesdobramentos(criarDesdrobramentoLiquidacao(entity, liquidacao, empenho));
            liquidacao.setDoctoFiscais(criarDocumentoFiscalLiquidacao(entity, liquidacao));
            liquidacao = liquidacaoFacade.salvarNovaLiquidacao(liquidacao, Lists.newArrayList());
            criarIntegracaoContabilPagamento(entity, itemIntegracaoRHContabil, liquidacao);
        }
    }

    private void criarIntegracaoContabilPagamento(IntegracaoRHContabil entity, ItemIntegracaoRHContabil itemIntegracaoRHContabil, Liquidacao liquidacao) {
        if (entity.isTipoSelecionado(TipoIntegradorRHContabil.PAGAMENTO)) {
            Pagamento pagamento = new Pagamento();
            pagamento.setDataRegistro(liquidacao.getDataRegistro());
            pagamento.setPrevistoPara(entity.getParametro().getDataPrevisaoPagamento());
            pagamento.setExercicio(liquidacao.getExercicio());
            pagamento.setLiquidacao(liquidacao);
            pagamento.setUnidadeOrganizacionalAdm(liquidacao.getUnidadeOrganizacionalAdm());
            pagamento.setUnidadeOrganizacional(liquidacao.getUnidadeOrganizacional());
            pagamento.setStatus(StatusPagamento.ABERTO);
            pagamento.setCategoriaOrcamentaria(liquidacao.getCategoriaOrcamentaria());
            pagamento.setValor(liquidacao.getValor());
            pagamento.setValorFinal(liquidacao.getValor());
            pagamento.setSaldo(liquidacao.getValor());
            pagamento.setSaldoFinal(liquidacao.getValor());
            pagamento.setComplementoHistorico(liquidacao.getComplemento());
            pagamento.setTipoOperacaoPagto(entity.getParametro().getTipoOperacaoPagto());
            pagamento.setTipoDocPagto(entity.getParametro().getTipoDocPagto());
            pagamento.setFinalidadePagamento(entity.getParametro().getFinalidadePagamento());
            pagamento.setSubConta(itemIntegracaoRHContabil.getSubConta());
            ConfigPagamento configuracao = pagamentoFacade.getConfigPagamentoFacade().recuperaEventoPorContaDespesa((ContaDespesa) pagamento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), TipoLancamento.NORMAL, pagamento.getLiquidacao().getEmpenho().getTipoContaDespesa(), pagamento.getPrevistoPara());
            Preconditions.checkNotNull(configuracao, "Configuração contábil não encontrada para os parametros informados.");
            pagamento.setEventoContabil(configuracao.getEventoContabil());
            pagamento.setDesdobramentos(criarDesdrobramentoPagamento(pagamento, liquidacao));
            criarIntegracaoContabilRetencao(entity, itemIntegracaoRHContabil, pagamento);

            pagamentoFacade.salvarNovoPagto(pagamento);
        }
    }

    private void criarIntegracaoContabilRetencao(IntegracaoRHContabil entity, ItemIntegracaoRHContabil itemIntegracaoRHContabil, Pagamento pagamento) {
        pagamento.setRetencaoPgtos(new ArrayList<RetencaoPgto>());

        for (RetencaoIntegracaoRHContabil retencao : itemIntegracaoRHContabil.getRetencoes()) {
            pagamento.getRetencaoPgtos().addAll(criarRetencaoPagamento(entity, retencao, pagamento));
        }
        pagamento.definirValorFinalPagto();
    }

    private List<RetencaoPgto> criarRetencaoPagamento(IntegracaoRHContabil entity, RetencaoIntegracaoRHContabil retencao, Pagamento pagamento) {
        List<RetencaoPgto> retencoes = Lists.newArrayList();
        RetencaoPgto retencaoPgto = new RetencaoPgto();
        retencaoPgto.setValor(retencao.getValor());
        retencaoPgto.setTipoConsignacao(TipoConsignacao.EXERCICIO);
        retencaoPgto.setValor(retencao.getValor());
        retencaoPgto.setSaldo(retencao.getValor());
        retencaoPgto.setClasseCredor(retencao.getClasseCredor());
        retencaoPgto.setFonteDeRecursos(pagamento.getLiquidacao().getEmpenho().getFonteDeRecursos());
        retencaoPgto.setPessoa(retencao.getPessoa());
        retencaoPgto.setContaExtraorcamentaria(retencao.getContaExtraorcamentaria());
        retencaoPgto.setSubConta(retencao.getSubConta());
        retencaoPgto.setDataRetencao(pagamento.getDataPagamento());
        retencaoPgto.setComplementoHistorico("Retenção do servidor :");
        for (ServidorRetencaoRHContabil servidor : retencao.getServidores()) {
            retencaoPgto.setComplementoHistorico(servidor.getVinculoFP().toString() + " ; ");
        }
        retencaoPgto.setPagamento(pagamento);
        retencaoPgto.setUnidadeOrganizacional(pagamento.getUnidadeOrganizacional());
        retencaoPgto.setUsuarioSistema(entity.getUsuarioSistema());
        retencoes.add(retencaoPgto);
        return retencoes;
    }

    private List<LiquidacaoDoctoFiscal> criarDocumentoFiscalLiquidacao(IntegracaoRHContabil entity, Liquidacao liquidacao) {
        List<LiquidacaoDoctoFiscal> retorno = Lists.newArrayList();

        DoctoFiscalLiquidacao doctoFiscalLiquidacao = new DoctoFiscalLiquidacao();
        doctoFiscalLiquidacao.setValor(liquidacao.getValor());
        doctoFiscalLiquidacao.setSaldo(liquidacao.getValor());
        doctoFiscalLiquidacao.setDataDocto(liquidacao.getDataLiquidacao());
        doctoFiscalLiquidacao.setNumero(entity.getParametro().getNumeroDocumentoLiquidacao());
        doctoFiscalLiquidacao.setTipoDocumentoFiscal(entity.getParametro().getTipoDocumento());

        LiquidacaoDoctoFiscal liquidacaoDoctoFiscal = new LiquidacaoDoctoFiscal();
        liquidacaoDoctoFiscal.setLiquidacao(liquidacao);
        liquidacaoDoctoFiscal.setSaldo(liquidacao.getValor());
        liquidacaoDoctoFiscal.setValorLiquidado(liquidacao.getValor());
        liquidacaoDoctoFiscal.setDoctoFiscalLiquidacao(doctoFiscalLiquidacao);

        retorno.add(liquidacaoDoctoFiscal);
        return retorno;
    }

    private List<DesdobramentoPagamento> criarDesdrobramentoPagamento(Pagamento pagamento, Liquidacao liquidacao) {
        List<DesdobramentoPagamento> retorno = Lists.newArrayList();
        DesdobramentoPagamento desdobramento = new DesdobramentoPagamento();
        desdobramento.setPagamento(pagamento);
        desdobramento.setDesdobramento(liquidacao.getDesdobramentos().get(0));
        desdobramento.setValor(pagamento.getValor());
        desdobramento.setSaldo(pagamento.getValor());
        retorno.add(desdobramento);
        return retorno;
    }

    private List<Desdobramento> criarDesdrobramentoLiquidacao(IntegracaoRHContabil entity, Liquidacao liquidacao, Empenho empenho) {
        List<Desdobramento> retorno = Lists.newArrayList();
        Desdobramento desdobramento = new Desdobramento();
        desdobramento.setLiquidacao(liquidacao);
        desdobramento.setConta(empenho.getDesdobramentos().get(0).getConta());
        desdobramento.setValor(liquidacao.getValor());
        desdobramento.setSaldo(entity.isTipoSelecionado(TipoIntegradorRHContabil.PAGAMENTO) ? BigDecimal.ZERO : liquidacao.getValor());
        desdobramento.setDesdobramentoEmpenho(empenho.getDesdobramentos().get(0));
        EventoContabil eventoContabil = liquidacaoFacade.buscarEventoContabil(liquidacao, (ContaDespesa) desdobramento.getConta());
        if (eventoContabil != null) {
            desdobramento.setEventoContabil(eventoContabil);
        }
        retorno.add(desdobramento);
        return retorno;
    }

    private List<DesdobramentoEmpenho> criarDesdrobramentoEmpenho(IntegracaoRHContabil entity, Empenho empenho, ItemIntegracaoRHContabil itemIntegracaoRHContabil) {
        List<DesdobramentoEmpenho> retorno = Lists.newArrayList();
        DesdobramentoEmpenho desdobramento = new DesdobramentoEmpenho();
        desdobramento.setEmpenho(empenho);
        desdobramento.setConta(itemIntegracaoRHContabil.getDesdobramento());
        desdobramento.setValor(itemIntegracaoRHContabil.getValor());
        desdobramento.setSaldo(entity.isTipoSelecionado(TipoIntegradorRHContabil.LIQUIDACAO) ? BigDecimal.ZERO : itemIntegracaoRHContabil.getValor());
        retorno.add(desdobramento);
        return retorno;
    }

    private void buscarSaldoOrcamentario(IntegracaoRHContabil integracaoRHContabil, FonteItemIntegracaoRHContabil fonteItemIntegracaoRHContabil) {
        DespesaORC despesaORC = despesaORCFacade.recuperar(fonteItemIntegracaoRHContabil.getDespesaORC().getId());
        UnidadeOrganizacional unidadeOrganizacional = despesaORC.getProvisaoPPADespesa().getUnidadeOrganizacional();
        Date data = integracaoRHContabil.getDataRegistro();

        FonteDespesaORC fonteDespesaORC = despesaORC.getFonteDespesaORCDaFonte(fonteItemIntegracaoRHContabil.getFonteDeRecursos());
        SaldoFonteDespesaORC saldoFonteDespesaORC = saldoFonteDespesaORCFacade.buscarUltimoSaldoPorFonteDataAndUnidade(fonteDespesaORC, data, unidadeOrganizacional);
        if (saldoFonteDespesaORC != null) {
            fonteItemIntegracaoRHContabil.setSaldoDisponivel(saldoFonteDespesaORC.getSaldoAtual());
        } else {
            fonteItemIntegracaoRHContabil.setSaldoDisponivel(BigDecimal.ZERO);
        }

        if ((fonteItemIntegracaoRHContabil.getSaldoDisponivel().compareTo(BigDecimal.ZERO) <= 0) || fonteItemIntegracaoRHContabil.getSaldoDisponivel().compareTo(fonteItemIntegracaoRHContabil.getValor()) < 0) {
            integracaoRHContabil.getFontes().add(fonteItemIntegracaoRHContabil);
        }
    }

    public void criarIntegracaoContabilObrigacaoAPagar(IntegracaoRHContabil entity, ItemIntegracaoRHContabil itemIntegracaoRHContabil) {
        ObrigacaoAPagar obrigacaoAPagar = new ObrigacaoAPagar();
        obrigacaoAPagar.setItemIntegracaoRHContabil(itemIntegracaoRHContabil);
        obrigacaoAPagar.setDataLancamento(entity.getParametro().getDataObrigacao());
        obrigacaoAPagar.setExercicio(entity.getExercicio());
        obrigacaoAPagar.setDespesaORC(itemIntegracaoRHContabil.getDespesaORC());
        obrigacaoAPagar.setUnidadeOrganizacional(itemIntegracaoRHContabil.getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional());
        obrigacaoAPagar.setFonteDespesaORC(itemIntegracaoRHContabil.getDespesaORC().getFonteDespesaORCDaFonte(itemIntegracaoRHContabil.getFonteDeRecursos()));
        obrigacaoAPagar.setContaDeDestinacao(obrigacaoAPagar.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao());
        obrigacaoAPagar.setFonteDeRecursos(itemIntegracaoRHContabil.getFonteDeRecursos());
        obrigacaoAPagar.setPessoa(itemIntegracaoRHContabil.getFornecedor());
        obrigacaoAPagar.setClasseCredor(itemIntegracaoRHContabil.getClasseCredor());
        obrigacaoAPagar.setHistorico(entity.getParametro().getHistoricoObrigacao());
        obrigacaoAPagar.setValor(itemIntegracaoRHContabil.getValor());
        obrigacaoAPagar.setSaldo(itemIntegracaoRHContabil.getValor());
        obrigacaoAPagar.setSaldoEmpenho(itemIntegracaoRHContabil.getValor());
        obrigacaoAPagar.setTransportado(Boolean.FALSE);
        obrigacaoAPagar.setUsuarioSistema(entity.getUsuarioSistema());
        obrigacaoAPagar.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
        obrigacaoAPagar.setContaDespesa(itemIntegracaoRHContabil.getDespesaORC().getContaDeDespesa());
        obrigacaoAPagar.setTipoContaDespesa(itemIntegracaoRHContabil.getTipoContaDespesa());
        obrigacaoAPagar.setSubTipoDespesa(getSubTipoDespesa(itemIntegracaoRHContabil));
        obrigacaoAPagar.setDocumentosFiscais(criarDocumentosFiscaisObrigacaoAPagar(obrigacaoAPagar, entity));
        if (itemIntegracaoRHContabil.getDesdobramento() != null) {
            obrigacaoAPagar.setDesdobramentos(criarDesdrobramentoObrigacaoAPagar(obrigacaoAPagar, itemIntegracaoRHContabil));
        }
        obrigacaoAPagarFacade.salvarObrigacao(obrigacaoAPagar);
    }

    private List<DesdobramentoObrigacaoPagar> criarDesdrobramentoObrigacaoAPagar(ObrigacaoAPagar obrigacaoAPagar, ItemIntegracaoRHContabil itemIntegracaoRHContabil) {
        List<DesdobramentoObrigacaoPagar> retorno = Lists.newArrayList();
        DesdobramentoObrigacaoPagar desdobramento = new DesdobramentoObrigacaoPagar();
        desdobramento.setObrigacaoAPagar(obrigacaoAPagar);
        desdobramento.setConta(itemIntegracaoRHContabil.getDesdobramento());
        desdobramento.setValor(itemIntegracaoRHContabil.getValor());
        desdobramento.setSaldo(itemIntegracaoRHContabil.getValor());
        retorno.add(desdobramento);
        return retorno;
    }

    private List<ObrigacaoAPagarDoctoFiscal> criarDocumentosFiscaisObrigacaoAPagar(ObrigacaoAPagar obrigacaoAPagar, IntegracaoRHContabil entity) {
        DoctoFiscalLiquidacao doctoFiscalLiquidacao = new DoctoFiscalLiquidacao();
        doctoFiscalLiquidacao.setValor(obrigacaoAPagar.getValor());
        doctoFiscalLiquidacao.setSaldo(obrigacaoAPagar.getValor());
        doctoFiscalLiquidacao.setDataDocto(obrigacaoAPagar.getDataLancamento());
        doctoFiscalLiquidacao.setNumero(entity.getParametro().getNumeroDocumentoObrigacao());
        doctoFiscalLiquidacao.setTipoDocumentoFiscal(entity.getParametro().getTipoDocumentoObrigacao());

        ObrigacaoAPagarDoctoFiscal obrigacaoAPagarDoctoFiscal = new ObrigacaoAPagarDoctoFiscal();
        obrigacaoAPagarDoctoFiscal.setObrigacaoAPagar(obrigacaoAPagar);
        obrigacaoAPagarDoctoFiscal.setSaldo(obrigacaoAPagar.getValor());
        obrigacaoAPagarDoctoFiscal.setValor(obrigacaoAPagar.getValor());
        obrigacaoAPagarDoctoFiscal.setDocumentoFiscal(doctoFiscalLiquidacao);
        return Lists.newArrayList(obrigacaoAPagarDoctoFiscal);
    }

    private SubTipoDespesa getSubTipoDespesa(ItemIntegracaoRHContabil itemIntegracaoRHContabil) {
        if (TipoContaDespesa.VARIACAO_PATRIMONIAL_DIMINUTIVA.equals(itemIntegracaoRHContabil.getTipoContaDespesa())) {
            return SubTipoDespesa.NAO_APLICAVEL;
        }
        if (itemIntegracaoRHContabil.getTipoPrevidencia() != null) {
            if (TipoRegimePrevidenciario.RGPS.equals(itemIntegracaoRHContabil.getTipoPrevidencia())) {
                return SubTipoDespesa.RGPS;
            } else {
                return SubTipoDespesa.RPPS;
            }
        } else {
            return SubTipoDespesa.NAO_APLICAVEL;
        }
    }

    public String montarRelatorio(IntegracaoRHContabil integracaoRHContabil) {
        return getCabecalhoRelatorio(integracaoRHContabil) +
            " " +
            "<b> 02. EMPENHOS </b> " +
            montarTabelaItens(integracaoRHContabil, TipoContabilizacao.EMPENHO) +
            "</br> " +
            "</br> " +
            "<b> 03. OBRIGAÇÕES </b> " +
            montarTabelaItens(integracaoRHContabil, TipoContabilizacao.OBRIGACAO_A_APAGAR) +
            "</br> " +
            "</br> " +
            "</br> " +
            "</br> " +
            montarTabelaResumoPorFonte(integracaoRHContabil) +
            "</br> " +
            "</br> " +
            "</br> " +
            "</br> " +
            " " +
            getRodapeRelatorio(integracaoRHContabil) +
            "</html>";
    }

    private String montarTabelaResumoPorFonte(IntegracaoRHContabil selecionado) {
        StringBuilder retorno = new StringBuilder();
        retorno.append("</br> ");
        retorno.append("<div style='border: 1px solid black;'> ");
        retorno.append("<b>04. RESUMO POR FONTE</b> ");
        retorno.append("</div> ");
        retorno.append("</div>");
        retorno.append("</div> ");

        retorno.append("<table border='1' style='width:100%;text-align: center;border-top: 0px'>");
        if (selecionado.getItens().isEmpty()) {
            retorno.append("<tr>");
            retorno.append("<td> Nenhum item foi informado.</td>");
            retorno.append("</tr>");
        }
        retorno.append("<tr style=\"background: #d7e3e9;width:100%;\">");
        retorno.append("<td> FONTE </td>");
        retorno.append("<td> VALOR BRUTO</td>");
        retorno.append("<td> TOTAL RETENÇÃO</td>");
        retorno.append("<td> VALOR LÍQUIDO</td>");
        retorno.append("</tr>");
        for (FonteIntegracaoRHContabil fonte : selecionado.getValoresPorFonte()) {
            retorno.append("<tr >");
            retorno.append("<td> " + fonte.getFonteDeRecursos() + "</td>");
            retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(fonte.getValorBruto()) + "</div></td>");
            retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(fonte.getValorRetencao()) + "</div></td>");
            retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(fonte.getValorLiquido()) + "</div></td>");
            retorno.append("</tr>");
        }
        retorno.append("<tr >");
        retorno.append("<td> TOTAL </td>");
        retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(selecionado.getValorTotalBrutoPorFonte()) + "</div></td>");
        retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(selecionado.getValorTotalRetencaoPorFonte()) + "</div></td>");
        retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(selecionado.getValorTotalLiquidoPorFonte()) + "</div></td>");
        retorno.append("</tr>");
        retorno.append("</table>");
        return retorno.toString();
    }


    private String montarTabelaItens(IntegracaoRHContabil selecionado, TipoContabilizacao tipoContabilizacao) {
        StringBuilder retorno = new StringBuilder();
        retorno.append("<table border='1' style='width:100%;text-align: center;border-top: 0px'>");
        if (selecionado.getItens().isEmpty()) {
            retorno.append("<tr>");
            retorno.append("<td> Nenhum item foi informado.</td>");
            retorno.append("</tr>");
        }
        for (ItemIntegracaoRHContabil item : selecionado.getItens()) {
            if (item.getTipoContabilizacao().equals(tipoContabilizacao)) {
                retorno.append("<tr style=\"background: #d7e3e9;width:100%;\">");
                retorno.append("<td> DOTAÇÃO</td>");
                retorno.append("<td> CONTA </td>");
                retorno.append("<td> FONTE </td>");
                retorno.append("<td> FORNECEDOR</td>");
                retorno.append("<td> VALOR BRUTO</td>");
                retorno.append("<td> TOTAL RETENÇÃO</td>");
                retorno.append("<td> VALOR LÍQUIDO</td>");
                retorno.append("</tr>");
                DespesaORC despesaORC = item.getDespesaORC();
                retorno.append("<tr >");
                retorno.append("<td> " + despesaORC.getCodigo() + "</td>");
                retorno.append("<td> " + item.getDesdobramento().getCodigo() + "</td>");
                retorno.append("<td> " + item.getFonteDeRecursos().getCodigo() + "</td>");
                retorno.append("<td> " + item.getFornecedor() + "</td>");
                retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(item.getValor()) + "</div></td>");
                retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(item.getValorTotalRetencoes()) + "</div></td>");
                retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(item.getValor().subtract(item.getValorTotalRetencoes())) + "</div></td>");
                retorno.append("</tr>");

                retorno.append("<tr>");
                retorno.append("<td colspan=\"7\">");
                retorno.append("<table border='1' style='width:100%;text-align: center;border-top: 0px'>");
                retorno.append("<tr style='width:100%;'>");
                retorno.append("<td colspan=\"2\"> CONTA </td>");
                retorno.append("<td colspan=\"3\"> FORNECEDOR </td>");
                retorno.append("<td colspan=\"2\"> VALOR </td>");
                retorno.append("</tr>");
                for (RetencaoIntegracaoRHContabil retencao : item.getRetencoes()) {
                    retorno.append("<tr>");
                    retorno.append("<td colspan=\"2\" style='text-align: left'> " + retencao.getContaExtraorcamentaria() + "</td>");
                    retorno.append("<td colspan=\"3\" style='text-align: left'> " + retencao.getPessoa() + "</td>");
                    retorno.append("<td colspan=\"2\"> <div style='text-align: right'>" + Util.formataValor(retencao.getValor()) + "</div></td>");
                    retorno.append("</tr>");
                }
                if (item.getRetencoes().isEmpty()) {
                    retorno.append("<tr>");
                    retorno.append("<td colspan=\"7\"> Nenhuma retenção foi informado.</td>");
                    retorno.append("</tr>");
                }
                retorno.append("</table>");
                retorno.append("</td>");
                retorno.append("</tr>");
                retorno.append("<tr style=\"background: #6E95A6;width:100%;\">");
                retorno.append("<td colspan=\"7\"> </td>");
                retorno.append("</tr>");
            }
        }

        retorno.append("</table>");
        return retorno.toString();
    }


    public String montarRelatorioPorServidor(IntegracaoRHContabil integracaoRHContabil) {
        return getCabecalhoRelatorio(integracaoRHContabil) +
            " " +
            montarTabelaItensServidor(integracaoRHContabil) +
            "</br> " +
            "</br> " +
            "</br> " +
            "</br> " +
            " " +
            getRodapeRelatorio(integracaoRHContabil) +
            "</html>";
    }


    private String montarTabelaItensServidor(IntegracaoRHContabil selecionado) {
        StringBuilder retorno = new StringBuilder();
        retorno.append("<table border='1' style='width:100%;text-align: center;border-top: 0px'>");
        List<ServidorIntegracaoRHContabil> servidores = Lists.newArrayList();
        for (ItemIntegracaoRHContabil item : selecionado.getItens()) {
            for (ServidorIntegracaoRHContabil servidor : item.getServidores()) {
                ServidorIntegracaoRHContabil first = null;
                for (ServidorIntegracaoRHContabil servidorIntegracao : servidores) {
                    if (servidorIntegracao.getVinculoFP().getId().equals(servidor.getVinculoFP().getId())) {
                        first = servidorIntegracao;
                        break;
                    }
                }
                if (first == null) {
                    servidores.add(servidor);
                }
            }
        }

        for (ServidorIntegracaoRHContabil servidor : servidores) {
            servidor.setServidoresRetencao(new ArrayList<ServidorRetencaoRHContabil>());
            for (RetencaoIntegracaoRHContabil retencao : selecionado.getRetencoes()) {
                for (ServidorRetencaoRHContabil servidorRetencaoRHContabil : retencao.getServidores()) {
                    if (servidor.getVinculoFP().getId().equals(servidorRetencaoRHContabil.getVinculoFP().getId())) {
                        servidor.getServidoresRetencao().add(servidorRetencaoRHContabil);
                    }
                }
            }
            servidor.calcularValoresPorServidor();
        }

        for (ServidorIntegracaoRHContabil item : servidores) {
            BigDecimal valorTotal = BigDecimal.ZERO;
            for (ItemIntegracaoRHContabil itemDaVez : selecionado.getItens()) {
                for (ServidorIntegracaoRHContabil servidor : itemDaVez.getServidores()) {
                    if (!servidor.equals(item)
                        && item.getVinculoFP().getId().equals(servidor.getVinculoFP().getId())) {
                        valorTotal = valorTotal.add(servidor.getValor());
                    }
                }
            }

            retorno.append("<tr style=\"background: #d7e3e9;width:100%;\">");
            retorno.append("<td> SERVIDOR </td>");
            retorno.append("<td> DOTAÇÃO </td>");
            retorno.append("<td> VALOR BRUTO</td>");
            retorno.append("<td> TOTAL RETENÇÃO</td>");
            retorno.append("<td> VALOR LÍQUIDO</td>");
            retorno.append("</tr>");
            retorno.append("<tr >");
            retorno.append("<td> " + item.getVinculoFP().toString() + "</td>");
            retorno.append("<td> " + item.getItemIntegracaoRHContabil().getDespesaORC().getCodigo() + "</td>");
            retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(valorTotal) + "</div></td>");
            retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(item.getValorServidorRetencao()) + "</div></td>");
            retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(valorTotal.subtract(item.getValorServidorRetencao())) + "</div></td>");
            retorno.append("</tr>");

            retorno.append("<tr>");
            retorno.append("<td colspan=\"5\">");
            retorno.append("<table border='1' style='width:100%;text-align: center;border-top: 0px'>");
            retorno.append("<tr style='width:100%;'>");
            retorno.append("<td colspan=\"1\"> CONTA </td>");
            retorno.append("<td colspan=\"2\"> FORNECEDOR </td>");
            retorno.append("<td colspan=\"3\"> VALOR </td>");
            retorno.append("</tr>");
            for (ServidorRetencaoRHContabil retencao : item.getServidoresRetencao()) {
                retorno.append("<tr>");
                retorno.append("<td colspan=\"1\" style='text-align: left'> " + retencao.getRetencao().getContaExtraorcamentaria() + "</td>");
                retorno.append("<td colspan=\"3\" style='text-align: left'> " + retencao.getRetencao().getPessoa() + "</td>");
                retorno.append("<td colspan=\"2\"> <div style='text-align: right'>" + Util.formataValor(retencao.getValor()) + "</div></td>");
                retorno.append("</tr>");
            }
            if (item.getServidoresRetencao().isEmpty()) {
                retorno.append("<tr>");
                retorno.append("<td colspan=\"5\"> Nenhuma retenção foi informado.</td>");
                retorno.append("</tr>");
            }
            retorno.append("</table>");
            retorno.append("</td>");
            retorno.append("</tr>");
            retorno.append("<tr style=\"background: #6E95A6;width:100%;\">");
            retorno.append("<td colspan=\"5\"> </td>");
            retorno.append("</tr>");
        }

        retorno.append("</table>");
        return retorno.toString();
    }

    public String getRodapeRelatorio(IntegracaoRHContabil integracaoRHContabil) {
        return "<div style='font-size: 11px'> " +
            "Emitido por: " + sistemaFacade.getUsuarioCorrente() + ", em " + DataUtil.getDataFormatada(new Date()) + " às " + Util.hourToString(new Date()) + " </br> " +
            " Integração RH/Contábil " +
            "</div> ";
    }

    public String getCabecalhoRelatorio(IntegracaoRHContabil integracaoRHContabil) {
        HierarquiaOrganizacional orcamentaria = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        String nomePrefeitura = "Municipio de Rio Branco";
        String conteudoFicha = "<?xml version='1.0' encoding='iso-8859-1'?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>" +
            " <style type=\"text/css\">@page{size: A4 landscape ;}</style>" +
            "<style> " +
            "body{"
            + "font-family:arial; font-size: 12px"
            + "}" +
            "table,th,td " +
            "{ " +
            "border:1px solid black; " +
            "border-collapse:collapse " +
            "}" +
            "</style> " +
            "<div style='text-align:right; font-size: 12px'>" +
            " Exercício: " + integracaoRHContabil.getExercicio() +
            "</div> " +
            "</br>" +
            "<div style='border: 1px solid black;text-align:center'> " +
            "<b> " + nomePrefeitura.toUpperCase() + " </br> " +
            "</div> " +
            " " +
            "</br> " +
            " " +
            "<div style='border: 1px solid black;text-align:center'> " +
            "<b> INTEGRAÇÃO RH/CONTÁBIL </b> " +
            "</div> " +
            " " +
            " " +
            "</br> " +
            "<div style='border: 1px solid black;'> " +
            "<b>01. IDENTIFICAÇÃO</b> " +
            "</div> " +
            "<div style='border: 1px solid black;text-align:left;border-top: 0px'> " +
            orcamentaria.getCodigo() + " - " + orcamentaria.getDescricao() + "</br>" +
            "<div style='border-top: 1px solid black;'>" +
            "</div>" +
            "</div> " +
            "</br> " +
            " " +
            "<div style='border: 1px solid black;'> " +
            "<b>02. RELAÇÃO DE EMPENHO/ RETENÇÃO</b> " +
            "</div> " +
            "<div style='border: 1px solid black;text-align:left;border-top: 0px'> " +
            "Quantidade de Empenhos: " + integracaoRHContabil.getQuantidadeEmpenho() + "</br>" +
            "Total de Empenhos Bruto  : " + Util.formataValor(integracaoRHContabil.getValoTotalEmpenhadoBruto()) + "</br>" +
            "Total de Empenhos Líquido: " + Util.formataValor(integracaoRHContabil.getValoTotalEmpenhadoLiquido()) + "</br>" +
            "Total de Retenções : " + Util.formataValor(integracaoRHContabil.getValoTotalRetencoes()) + "</br>" +
            "<div style='border-top: 1px solid black;'>" +
            "</div>" +
            "</div> " +
            "</br> " +
            "<div style='border: 1px solid black;'> " +
            "<b>03. RELAÇÃO DE OBRIGAÇÕES</b> " +
            "</div> " +
            "<div style='border: 1px solid black;text-align:left;border-top: 0px'> " +
            "Quantidade de Obrigação: " + integracaoRHContabil.getQuantidadeObrigacaoPagar() + "</br>" +
            "Total de Obrigação  : " + Util.formataValor(integracaoRHContabil.getValorTotalObrigacoesAPagar()) + "</br>" +
            "<div style='border-top: 1px solid black;'>" +
            "</div>" +
            "</div> " +
            "</br> ";
        return conteudoFicha;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @Asynchronous
    public Future<IntegracaoRHContabil> inicializarSimulacao(AssistenteIntegracaoRHContabil assistenteAberturaFechamentoExercicio) {
        IntegracaoRHContabil entity = assistenteAberturaFechamentoExercicio.getIntegracaoRHContabil();
        BarraProgressoItens barraProgressoItens = assistenteAberturaFechamentoExercicio.getBarraProgressoItens();
        barraProgressoItens.inicializa();
        try {
            recuperarLancamentos(entity);
        } catch (ValidacaoException ve) {
            for (FacesMessage facesMessage : ve.getMensagens()) {
                assistenteAberturaFechamentoExercicio.getMensagens().add(facesMessage.getDetail());
            }
            assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
            throw ve;
        } catch (Exception e) {
            assistenteAberturaFechamentoExercicio.getMensagens().add(e.getMessage());
            assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
            logger.error("Erro ", e);
            throw e;
        }
        assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
        return new AsyncResult<>(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recuperarLancamentos(IntegracaoRHContabil integracaoRHContabil) {
        integracaoRHContabil.setItens(Lists.newArrayList());
        if (integracaoRHContabil.isTipoSelecionado(TipoIntegradorRHContabil.OBRIGACAO_A_PAGAR)) {
            integracaoRHContabil.getItens().addAll(recuperarItensIntegracaoPorTipo(integracaoRHContabil, TipoContabilizacao.OBRIGACAO_A_APAGAR));
        }
        if (integracaoRHContabil.isTipoSelecionado(TipoIntegradorRHContabil.EMPENHO)) {
            integracaoRHContabil.getItens().addAll(recuperarItensIntegracaoPorTipo(integracaoRHContabil, TipoContabilizacao.EMPENHO));
        }
        if (integracaoRHContabil.isTipoSelecionado(TipoIntegradorRHContabil.PAGAMENTO)) {
            List<RetencaoIntegracaoRHContabil> retencoes = buscarRetencoes(integracaoRHContabil, null);
            integracaoRHContabil.getRetencoesSemPagamento().addAll(retencoes);

            List<RetencaoIntegracaoRHContabil> praRemover = Lists.newArrayList();
            for (RetencaoIntegracaoRHContabil retencao : integracaoRHContabil.getRetencoesSemPagamento()) {
                ServidorIntegracaoRHContabil servidorIntegracao = integracaoRHContabil.getServidorIntegracao(retencao);
                if (servidorIntegracao != null) {
                    ItemIntegracaoRHContabil item = servidorIntegracao.getItemIntegracaoRHContabil();
                    RetencaoIntegracaoRHContabil first = null;
                    for (RetencaoIntegracaoRHContabil retencaoIntegracaoRHContabil : item.getRetencoes()) {
                        if (retencaoIntegracaoRHContabil.getContaExtraorcamentaria().getId().equals(retencao.getContaExtraorcamentaria().getId()) &&
                            retencaoIntegracaoRHContabil.getPessoa().getId().equals(retencao.getPessoa().getId())) {
                            first = retencaoIntegracaoRHContabil;
                            break;
                        }
                    }
                    if (first != null) {
                        first.getServidores().addAll(retencao.getServidores());
                        BigDecimal valor = BigDecimal.ZERO;
                        for (ServidorRetencaoRHContabil servidor : first.getServidores()) {
                            servidor.setRetencao(first);
                            valor = valor.add(servidor.getValor());
                        }
                        first.setValor(valor);
                    } else {
                        retencao.setSubConta(item.getSubConta());
                        retencao.setItemIntegracaoRHContabil(item);
                        item.getRetencoes().add(retencao);
                    }

                    praRemover.add(retencao);
                }
            }

            for (RetencaoIntegracaoRHContabil retencaoIntegracaoRHContabil : praRemover) {
                integracaoRHContabil.getRetencoesSemPagamento().remove(retencaoIntegracaoRHContabil);
            }
        }
        criarListasAuxiliares(integracaoRHContabil);
        ordenarItens(integracaoRHContabil);
    }

    private void ordenarItens(IntegracaoRHContabil integracaoRHContabil) {
        Collections.sort(integracaoRHContabil.getItens(), new Comparator<ItemIntegracaoRHContabil>() {
            @Override
            public int compare(ItemIntegracaoRHContabil o1, ItemIntegracaoRHContabil o2) {
                return o1.getDespesaORC().getCodigo().compareTo(o2.getDespesaORC().getCodigo());
            }
        });
    }

    public void criarListasAuxiliares(IntegracaoRHContabil integracaoRHContabil) {
        criarResumoPorDotacao(integracaoRHContabil);
        criarResumoPorFonte(integracaoRHContabil);
        criarListaPessoaClasse(integracaoRHContabil);
        criarListaContasDespesaTipoContaDespesa(integracaoRHContabil);
    }

    private void criarListaContasDespesaTipoContaDespesa(IntegracaoRHContabil integracaoRHContabil) {
        integracaoRHContabil.setContasTipos(new ArrayList<ContaDespesaTipoContaDespesaIntegracaoRHContabil>());

        for (ItemIntegracaoRHContabil item : integracaoRHContabil.getItens()) {
            ContaDespesaTipoContaDespesaIntegracaoRHContabil conta = new ContaDespesaTipoContaDespesaIntegracaoRHContabil(item.getDespesaORC().getContaDeDespesa(), item.getTipoContaDespesa());

            ContaDespesaTipoContaDespesaIntegracaoRHContabil first = null;
            for (ContaDespesaTipoContaDespesaIntegracaoRHContabil contaTipo : integracaoRHContabil.getContasTipos()) {
                if (contaTipo.getConta().getId().equals(conta.getConta().getId())) {
                    first = contaTipo;
                    break;
                }
            }
            if (first == null) {
                Util.adicionarObjetoEmLista(integracaoRHContabil.getContasTipos(), conta);
            }
        }
    }

    private void criarListaPessoaClasse(IntegracaoRHContabil integracaoRHContabil) {
        integracaoRHContabil.setPessoasClasses(new ArrayList<PessoaClasseIntegracaoRHContabil>());

        for (ItemIntegracaoRHContabil item : integracaoRHContabil.getItens()) {
            PessoaClasseIntegracaoRHContabil pessoa = new PessoaClasseIntegracaoRHContabil(item.getFornecedor(), item.getClasseCredor());

            PessoaClasseIntegracaoRHContabil first = null;
            for (PessoaClasseIntegracaoRHContabil pessoaClasseIntegracaoRHContabil : integracaoRHContabil.getPessoasClasses()) {
                if (pessoaClasseIntegracaoRHContabil.getPessoa().getId().equals(pessoa.getPessoa().getId())) {
                    first = pessoaClasseIntegracaoRHContabil;
                    break;
                }
            }
            if (first == null) {
                Util.adicionarObjetoEmLista(integracaoRHContabil.getPessoasClasses(), pessoa);
            }
        }
    }

    private void criarResumoPorDotacao(IntegracaoRHContabil integracaoRHContabil) {
        integracaoRHContabil.setFontes(new ArrayList<FonteItemIntegracaoRHContabil>());
        List<FonteItemIntegracaoRHContabil> fontes = Lists.newArrayList();
        for (ItemIntegracaoRHContabil item : integracaoRHContabil.getItens()) {
            FonteItemIntegracaoRHContabil fonte = new FonteItemIntegracaoRHContabil();
            fonte.setFonteDeRecursos(item.getFonteDeRecursos());
            fonte.setDespesaORC(item.getDespesaORC());
            fonte.setValor(item.getValor());

            FonteItemIntegracaoRHContabil first = null;
            for (FonteItemIntegracaoRHContabil fonteItemIntegracaoRHContabil : fontes) {
                if (fonteItemIntegracaoRHContabil.getFonteDeRecursos().getId().equals(fonte.getFonteDeRecursos().getId()) &&
                    fonteItemIntegracaoRHContabil.getDespesaORC().getId().equals(fonte.getDespesaORC().getId())) {
                    first = fonte;
                    break;
                }
            }
            if (first != null) {
                first.setValor(first.getValor().add(fonte.getValor()));
            } else {
                Util.adicionarObjetoEmLista(fontes, fonte);
            }
        }
        for (FonteItemIntegracaoRHContabil fonteItemIntegracaoRHContabil : fontes) {
            buscarSaldoOrcamentario(integracaoRHContabil, fonteItemIntegracaoRHContabil);
        }
    }

    private void criarResumoPorFonte(IntegracaoRHContabil integracaoRHContabil) {
        integracaoRHContabil.setValoresPorFonte(new ArrayList<FonteIntegracaoRHContabil>());

        for (ItemIntegracaoRHContabil item : integracaoRHContabil.getItens()) {
            FonteIntegracaoRHContabil fonte = new FonteIntegracaoRHContabil();
            fonte.setFonteDeRecursos(item.getFonteDeRecursos());
            fonte.setValorBruto(item.getValor());
            fonte.setValorRetencao(item.getValorTotalRetencoes());
            fonte.setValorLiquido(item.getValor().subtract(item.getValorTotalRetencoes()));

            FonteIntegracaoRHContabil first = null;
            for (FonteIntegracaoRHContabil fonteIntegracaoRHContabil : integracaoRHContabil.getValoresPorFonte()) {
                if (fonteIntegracaoRHContabil.getFonteDeRecursos().getId().equals(fonte.getFonteDeRecursos().getId())) {
                    first = fonteIntegracaoRHContabil;
                    break;
                }
            }

            if (first != null) {
                first.setValorBruto(first.getValorBruto().add(fonte.getValorBruto()));
                first.setValorRetencao(first.getValorRetencao().add(fonte.getValorRetencao()));
                first.setValorLiquido(first.getValorLiquido().add(fonte.getValorLiquido()));
            } else {
                Util.adicionarObjetoEmLista(integracaoRHContabil.getValoresPorFonte(), fonte);
            }
        }
    }

    public List<ItemIntegracaoRHContabil> recuperarItensIntegracaoPorTipo(IntegracaoRHContabil integracaoRHContabil, TipoContabilizacao tipoContabilizacao) {
        String sql = " select " +
            " dados.idFornecedor," +
            " dados.idClasse," +
            " dados.despesaorc," +
            " dados.fonte," +
            " dados.codigoSubConta," +
            " dados.vinculoId," +
            " dados.tipoPrevidencia, " +
            " dados.desdobramento, " +
            " dados.tipocontabilizacao, " +
            " dados.idFonteEventoFp, " +
            " sum((CASE when dados.operacaoformula = 'ADICAO' THEN dados.valor ELSE -dados.valor end)) AS valor" +
            " from (" +
            "   SELECT " +
            "                   replace(replace(replace((CASE WHEN juridica.cnpj IS NOT NULL " +
            "                                                        THEN juridica.cnpj " +
            "                                                 ELSE fisica.cpf END), '.', ''), '-', ''), '/', '') AS cnpj, " +
            "                   fonteevento.fornecedor_id as idFornecedor, " +
            "                   fonteevento.classecredor_id as idClasse, " +
            "                   (CASE WHEN juridica.cnpj IS NOT NULL " +
            "                                THEN juridica.cnpj " +
            "                         ELSE fisica.cpf END), " +
            "                   mat.matricula                                                               AS matricula, " +
            "                   despesaOrc.id                                                               as despesaorc, " +
            "                   fonteevento.desdobramento_id                                                as desdobramento, " +
            "                   case " +
            "                          when evento.tipoeventofp = 'VANTAGEM' and item.tipoeventofp = 'DESCONTO' " +
            "                                 THEN -item.valor " +
            "                          when evento.tipoeventofp = 'DESCONTO' and item.tipoeventofp = 'VANTAGEM' " +
            "                                 THEN item.valor " +
            "                          ELSE ITEM.VALOR " +
            "                          END as valor, " +
            "                   fonterecurso.id                                                         AS fonte, " +
            "                   vinculo.id                                                                  AS vinculoId, " +
            "                   CASE WHEN juridica.id IS NOT NULL " +
            "                               THEN 'PJ' " +
            "                        ELSE 'PF' END                                                               AS tipoFornecedor, " +
            "                   coalesce( " +
            "                       coalesce( " +
            "                           coalesce( " +
            "                               coalesce( " +
            "                                   coalesce( " +
            "                                       (SELECT tipo.tipoRegimePrevidenciario " +
            "                                        FROM previdenciavinculofp prev " +
            "                                                    INNER JOIN tipoprevidenciafp tipo ON prev.tipoprevidenciafp_id = tipo.id " +
            "                                        WHERE prev.contratofp_id = vinculo.id " +
            "                                          AND to_date(:data, 'dd/mm/yyyy') " +
            "                                               BETWEEN prev.iniciovigencia AND coalesce(prev.finalvigencia,to_date(:data, 'dd/mm/yyyy'))) , " +
            "                                       (SELECT tipo.tipoRegimePrevidenciario " +
            "                                        FROM previdenciavinculofp prev " +
            "                                                    INNER JOIN tipoprevidenciafp tipo ON prev.tipoprevidenciafp_id = tipo.id " +
            "                                        WHERE prev.contratofp_id = vinculo.id " +
            "                                          AND prev.iniciovigencia  = (SELECT max(prevv.iniciovigencia) FROM previdenciavinculofp prevv WHERE prevv.contratofp_id = vinculo.id))) , " +
            "                                   (select t.tipoRegimePrevidenciario " +
            "                                    from previdenciavinculofp prev " +
            "                                                inner join tipoprevidenciafp t on prev.tipoprevidenciafp_id = t.id " +
            "                                    where prev.contratofp_id = aposentado.contratofp_id and " +
            "                                                  prev.iniciovigencia = (select max(prevv.iniciovigencia) from previdenciavinculofp prevv where prevv.contratofp_id = aposentado.contratofp_id))), " +
            "                               (select t.tipoRegimePrevidenciario " +
            "                                from previdenciavinculofp prev " +
            "                                            inner join tipoprevidenciafp t on prev.tipoprevidenciafp_id = t.id " +
            "                                where prev.contratofp_id = pensao.contratofp_id and " +
            "                                              prev.iniciovigencia = (select max(prevv.iniciovigencia) from previdenciavinculofp prevv where prevv.contratofp_id = pensao.contratofp_id))  ), " +
            "                           (select t.tipoRegimePrevidenciario " +
            "                            from previdenciavinculofp prev " +
            "                                        inner join tipoprevidenciafp t on prev.tipoprevidenciafp_id = t.id " +
            "                            where prev.contratofp_id = ben.contratofp_id and " +
            "                                          prev.iniciovigencia = (select max(prevv.iniciovigencia) from previdenciavinculofp prevv where prevv.contratofp_id = ben.contratofp_id)) ), " +
            "                       'PREVIDENCIA_PROPRIA') " +
            "                          AS tipoPrevidencia, " +
            "                   fonteevento.tipoContribuicao, " +
            "                   fonteevento.operacaoformula, " +
            "                   subconta.id AS codigoSubConta, " +
            "                   vinculo.numero AS numeroContrato," +
            "                   fonteevento.tipocontabilizacao, " +
            "                   fonteevento.id as idFonteEventoFp " +
            "            FROM recursofp rec " +
            "           INNER JOIN fontesrecursofp fonter ON rec.id = fonter.recursofp_id " +
            "           INNER JOIN fonteeventofp fonteevento ON fonter.id = fonteevento.fontesrecursofp_id " +
            "           INNER JOIN fichafinanceirafp ficha ON rec.id = ficha.recursofp_id " +
            "           INNER JOIN vinculofp vinculo ON vinculo.id = ficha.vinculofp_id " +
            "           INNER JOIN itemfichafinanceirafp item ON ficha.id = item.fichafinanceirafp_id " +
            "           INNER JOIN matriculafp mat ON mat.id = vinculo.matriculafp_id " +
            "           INNER JOIN pessoafisica pf ON pf.id = mat.pessoa_id " +
            "           INNER JOIN pessoa p ON p.id = fonteevento.fornecedor_id " +
            "           LEFT JOIN pessoafisica fisica ON fisica.id = p.id " +
            "           LEFT JOIN pessoajuridica juridica ON juridica.id = p.id " +
            "           INNER JOIN folhadepagamento folha ON ficha.folhadepagamento_id = folha.id " +
            "           INNER JOIN eventofp evento ON fonteevento.eventofp_id = evento.id AND item.eventofp_id = evento.id " +
            "           INNER JOIN despesaorc despesaOrc ON fonteevento.despesaorc_id = despesaOrc.id " +
            "           INNER JOIN provisaoppadespesa provDesp ON despesaOrc.provisaoppadespesa_id = provdesp.id " +
            "           INNER JOIN conta desdobramento ON desdobramento.id = provdesp.contaDeDespesa_id " +
            "           INNER JOIN fontederecursos fonterecurso ON fonterecurso.id = fonteevento.fontederecursos_id " +
            "           LEFT JOIN subconta subconta ON subconta.id = fonteevento.subconta_id " +
            "           LEFT JOIN contabancariaentidade cb ON subconta.contabancariaentidade_id = cb.id " +
            "           LEFT JOIN conta conta ON conta.id = fonteevento.desdobramento_id " +
            "           left join aposentadoria aposentado on aposentado.id = vinculo.id " +
            "           left join pensionista pensionista on pensionista.id = vinculo.id " +
            "           left join pensao pensao on pensao.id = pensionista.pensao_id " +
            "           left join beneficiario ben on ben.id = vinculo.id " +
            "            WHERE folha.id in(" + getClausulaInIdFolha(integracaoRHContabil.getFolhasDePagamento()) + ")" +
            "              AND fonteevento.tipocontabilizacao = :tipo " +
            "              AND to_date(:data, 'dd/mm/yyyy') BETWEEN fonter.iniciovigencia AND coalesce(fonter.finalvigencia, to_date(:data,'dd/mm/yyyy')) " +
            "            ) dados " +
            " where dados.idFonteEventoFp not in (select distinct coalesce(item.fonteeventofp_id, 0) " +
            "                                from integracaorhcontabil irhc " +
            "                                    inner join itemintegracaorhcontabil item on item.integracaorhcontabil_id = irhc.id " +
            "                                where irhc.mes = :mes and irhc.exercicio_id = :idExercicio " +
            "                                )" +
            " group by dados.idFornecedor," +
            " dados.idClasse," +
            " dados.despesaorc," +
            " dados.fonte," +
            " dados.codigoSubConta," +
            " dados.vinculoId," +
            " dados.tipoPrevidencia, " +
            " dados.desdobramento, " +
            " dados.tipocontabilizacao, " +
            " dados.idFonteEventoFp ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", integracaoRHContabil.getMes().name());
        q.setParameter("idExercicio", integracaoRHContabil.getExercicio().getId());
        q.setParameter("tipo", tipoContabilizacao.name());
        q.setParameter("data", DataUtil.getDataFormatada(DataUtil.montaData(DataUtil.getDiasNoMes(integracaoRHContabil.getMes().getNumeroMes(), integracaoRHContabil.getExercicio().getAno()), integracaoRHContabil.getMes().getNumeroMesIniciandoEmZero(), integracaoRHContabil.getExercicio().getAno()).getTime()));
        List<Object[]> resultado = q.getResultList();
        List<ItemIntegracaoRHContabil> retorno = Lists.newArrayList();

        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                if (isValorZerado(((BigDecimal) obj[9]))) {
                    continue;
                }

                Pessoa fornecedor = em.find(Pessoa.class, ((Number) obj[0]).longValue());
                ClasseCredor classeCredor = null;
                if (obj[1] != null) {
                    classeCredor = em.find(ClasseCredor.class, ((Number) obj[1]).longValue());
                }
                DespesaORC despesaORC = em.find(DespesaORC.class, ((Number) obj[2]).longValue());
                FonteDeRecursos fonteDeRecursos = em.find(FonteDeRecursos.class, ((Number) obj[3]).longValue());
                SubConta subConta = null;
                if (obj[4] != null) {
                    subConta = em.find(SubConta.class, ((Number) obj[4]).longValue());
                }
                VinculoFP vinculoFP = em.find(VinculoFP.class, ((Number) obj[5]).longValue());
                String tipoPrevidencia = (String) obj[6];
                Conta desdobramento = em.find(Conta.class, ((Number) obj[7]).longValue());
                TipoContabilizacao tipo = TipoContabilizacao.valueOf((String) obj[8]);
                FonteEventoFP fonteEventoFP = em.find(FonteEventoFP.class, ((Number) obj[9]).longValue());
                BigDecimal valor = (BigDecimal) obj[10];

                String mensagem = ". Verificar as configurações de Integração do RH.";
                if (!integracaoRHContabil.getExercicio().equals(despesaORC.getExercicio())) {
                    throw new ExcecaoNegocioGenerica("O Exercício da despesa orçamentária " + despesaORC.getCodigoAdaptado() + " é " + despesaORC.getExercicio() + mensagem);
                }

                if (!integracaoRHContabil.getExercicio().equals(desdobramento.getExercicio())) {
                    throw new ExcecaoNegocioGenerica("O Exercício do Desdobramento " + desdobramento.getCodigo() + " é " + desdobramento.getExercicio() + " para a despesa orçamentária " + despesaORC.getCodigoAdaptado() + mensagem);
                }

                if (!integracaoRHContabil.getExercicio().equals(fonteDeRecursos.getExercicio())) {
                    throw new ExcecaoNegocioGenerica("O Exercício da Fonte de Recurso " + fonteDeRecursos.getCodigo() + " é " + fonteDeRecursos.getExercicio() + " para a despesa orçamentária " + despesaORC.getCodigoAdaptado() + mensagem);
                }

                ServidorIntegracaoRHContabil servidor = new ServidorIntegracaoRHContabil();
                servidor.setVinculoFP(vinculoFP);
                servidor.setValor(valor);

                ItemIntegracaoRHContabil first = null;
                for (ItemIntegracaoRHContabil item : retorno) {
                    if (item.getFornecedor().getId().equals(fornecedor.getId()) &&
                        item.getDespesaORC().getId().equals(despesaORC.getId()) &&
                        item.getFonteDeRecursos().getId().equals(fonteDeRecursos.getId()) &&
                        ((TipoContabilizacao.OBRIGACAO_A_APAGAR.equals(tipo) && subConta == null) || (TipoContabilizacao.EMPENHO.equals(tipo) && item.getSubConta().getId().equals(subConta.getId()))) &&
                        tipo.equals(item.getTipoContabilizacao()) &&
                        item.getDesdobramento().getId().equals(desdobramento.getId())) {
                        first = item;
                        break;
                    }
                }
                if (first != null) {
                    servidor.setItemIntegracaoRHContabil(first);
                    first.getServidores().add(servidor);
                    first.setValor(first.getValor().add(valor));
                    first.setFonteEventoFP(fonteEventoFP);
                } else {
                    ItemIntegracaoRHContabil item = new ItemIntegracaoRHContabil();
                    item.setTipoContabilizacao(tipo);
                    item.setFornecedor(fornecedor);
                    item.setClasseCredor(classeCredor);
                    item.setSubConta(subConta);
                    item.setIntegracaoRHContabil(integracaoRHContabil);
                    item.setFonteDeRecursos(fonteDeRecursos);
                    item.setDespesaORC(despesaORC);
                    item.setDesdobramento(desdobramento);
                    if (tipoPrevidencia != null) {
                        item.setTipoPrevidencia(TipoRegimePrevidenciario.valueOf(tipoPrevidencia));
                    }
                    servidor.setItemIntegracaoRHContabil(item);
                    item.getServidores().add(servidor);
                    item.setValor(item.getValor().add(valor));
                    item.setFonteEventoFP(fonteEventoFP);
                    retorno.add(item);
                }
            }
        }
        return retorno;
    }

    public List<RetencaoIntegracaoRHContabil> buscarRetencoes(IntegracaoRHContabil integracaoRHContabil, ItemIntegracaoRHContabil itemIntegracaoRHContabil) {
        String sql = "   SELECT contaextra," +
            "                                  x.credor," +
            "                                  vinculoId," +
            "                                  tipoContabilizacao," +
            "                                  sum(CASE WHEN x.operacao = 'ADICAO' THEN x.valor ELSE -x.valor END) AS valor," +
            "                                  x.codrecurso || ' - ' || x.descrecurso                              as recurso" +
            "                           FROM (" +
            "                                    SELECT conta.id                    AS contaextra," +
            "                                           fonteevento.operacaoformula AS operacao," +
            "                                           case" +
            "                                               when evento.tipoeventofp = 'VANTAGEM' and item.tipoeventofp = 'DESCONTO'" +
            "                                                   THEN -item.valor" +
            "                                               when evento.tipoeventofp = 'DESCONTO' and item.tipoeventofp = 'VANTAGEM'" +
            "                                                   THEN item.valor" +
            "                                               ELSE ITEM.VALOR" +
            "                                               END                     as valor," +
            "                                           replace(replace(replace((CASE" +
            "                                                                        WHEN juridica.cnpj IS NOT NULL" +
            "                                                                            THEN juridica.cnpj" +
            "                                                                        ELSE fisica.cpf END), '.', ''), '-', ''), '/'," +
            "                                                   '')                 AS documentoCredor," +
            "                                           vinculo.id                  AS vinculoId," +
            "                                           rec.codigo                     codrecurso," +
            "                                           rec.descricao                  descrecurso," +
            "                                           fonteevento.tipoContabilizacao tipoContabilizacao," +
            "                                           fonteevento.credor_id          credor" +
            "                                    FROM recursofp rec" +
            "                                             INNER JOIN fontesrecursofp fonter ON rec.id = fonter.recursofp_id" +
            "                                             INNER JOIN fonteeventofp fonteevento ON fonter.id = fonteevento.fontesrecursofp_id" +
            "                                             INNER JOIN fichafinanceirafp ficha ON rec.id = ficha.recursofp_id" +
            "                                             INNER JOIN vinculofp vinculo ON vinculo.id = ficha.vinculofp_id" +
            "                                             INNER JOIN itemfichafinanceirafp item ON ficha.id = item.fichafinanceirafp_id" +
            "                                             INNER JOIN matriculafp mat ON mat.id = vinculo.matriculafp_id" +
            "                                             INNER JOIN pessoafisica pf ON pf.id = mat.pessoa_id" +
            "                                             INNER JOIN pessoa p ON p.id = fonteevento.credor_id" +
            "                                             LEFT JOIN pessoafisica fisica ON fisica.id = p.id" +
            "                                             LEFT JOIN pessoajuridica juridica ON juridica.id = p.id" +
            "                                             INNER JOIN folhadepagamento folha ON ficha.folhadepagamento_id = folha.id" +
            "                                             INNER JOIN eventofp evento" +
            "                                                        ON fonteevento.eventofp_id = evento.id AND item.eventofp_id = evento.id" +
            "                                             INNER JOIN contaextraorcamentaria contaextra" +
            "                                                        ON fonteevento.contaextraorcamentaria_id = contaextra.id" +
            "                                             INNER JOIN conta conta ON conta.id = contaextra.id" +
            "       WHERE folha.id in(" + getClausulaInIdFolha(integracaoRHContabil.getFolhasDePagamento()) + ") " +
            "                                      AND to_date(:data, 'dd/mm/yyyy') BETWEEN fonter.iniciovigencia AND coalesce(fonter.finalvigencia, to_date(:data, 'dd/mm/yyyy'))" +
            "         AND fonteevento.tipocontabilizacao in  " + TipoContabilizacao.montarClausulaIn(Arrays.asList(TipoContabilizacao.RETENCAO_EXTRAORCAMENTARIA_CONSIGNACOES)) +
            "                                ) x" +
            "                           GROUP BY x.contaextra, x.documentoCredor, x.operacao, x.vinculoId, x.codrecurso," +
            "                                    x.descrecurso, x.credor, tipoContabilizacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", DataUtil.getDataFormatada(DataUtil.montaData(DataUtil.getDiasNoMes(integracaoRHContabil.getMes().getNumeroMes(), integracaoRHContabil.getExercicio().getAno()), integracaoRHContabil.getMes().getNumeroMesIniciandoEmZero(), integracaoRHContabil.getExercicio().getAno()).getTime()));
        List<Object[]> resultado = q.getResultList();
        List<RetencaoIntegracaoRHContabil> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            HashMap<String, RetencaoIntegracaoRHContabil> mapRetencoes = new HashMap<>();
            for (Object[] obj : resultado) {
                if (isValorZerado((BigDecimal) obj[4])) {
                    continue;
                }
                Conta conta = em.find(Conta.class, ((Number) obj[0]).longValue());
                Pessoa pessoa = em.find(Pessoa.class, ((Number) obj[1]).longValue());
                VinculoFP vinculoFP = em.find(VinculoFP.class, ((Number) obj[2]).longValue());
                String tipoContabilizacao = (String) obj[3];
                BigDecimal valor = (BigDecimal) obj[4];
                String recursoRH = (String) obj[5];

                String key = recursoRH.trim() + conta.getCodigo().trim() + pessoa.getNome().trim();
                if (!mapRetencoes.containsKey(key)) {
                    RetencaoIntegracaoRHContabil retencao = new RetencaoIntegracaoRHContabil();
                    retencao.setContaExtraorcamentaria(conta);
                    retencao.setPessoa(pessoa);
                    retencao.setTipoContabilizacao(TipoContabilizacao.valueOf(tipoContabilizacao));
                    retencao.setRecursoRH(recursoRH);
                    retencao.setValor(valor);
                    retencao.setIntegracaoRHContabil(integracaoRHContabil);
                    retencao.setSubConta(null);
                    retencao.getServidores().add(montarRetencaoServidor(vinculoFP, valor, retencao));
                    mapRetencoes.put(key, retencao);
                } else {
                    RetencaoIntegracaoRHContabil retencao = mapRetencoes.get(key);
                    retencao.setValor(retencao.getValor().add(valor));
                    retencao.getServidores().add(montarRetencaoServidor(vinculoFP, valor, retencao));
                    mapRetencoes.put(key, retencao);
                }
            }
            retorno.addAll(mapRetencoes.values());
        }
        return retorno;
    }

    private ServidorRetencaoRHContabil montarRetencaoServidor(VinculoFP vinculoFP, BigDecimal valor, RetencaoIntegracaoRHContabil retencao) {
        ServidorRetencaoRHContabil servidor = new ServidorRetencaoRHContabil();
        servidor.setVinculoFP(vinculoFP);
        servidor.setValor(valor);
        servidor.setRetencao(retencao);
        return servidor;
    }

    private String getClausulaInIdFolha(List<IntegracaoRHContabilFolha> folhas) {
        String codigos = "";
        for (IntegracaoRHContabilFolha cod : folhas) {
            codigos += "" + cod.getFolhaDePagamento().getId() + ",";
        }
        codigos = codigos.substring(0, codigos.length() - 1);
        return codigos;
    }

    private boolean isValorZerado(BigDecimal valor) {
        return valor == null || valor.compareTo(BigDecimal.ZERO) == 0;
    }

    public Exercicio getExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public Date getDataOperacao() {
        return sistemaFacade.getDataOperacao();
    }

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
