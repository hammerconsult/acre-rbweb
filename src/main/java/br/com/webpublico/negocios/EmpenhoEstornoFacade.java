/*
 * Codigo gerado automaticamente em Tue Dec 13 16:19:47 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.Web;
import br.com.webpublico.controle.portaltransparencia.entidades.EmpenhoEstornoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.PatrimonioLiquido;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.contabil.TipoEmpenhoDTO;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Stateless
public class EmpenhoEstornoFacade extends SuperFacadeContabil<EmpenhoEstorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private CotaOrcamentariaFacade cotaOrcamentariaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigEmpenhoFacade configEmpenhoFacade;
    @EJB
    private ConfigCancelamentoRestoFacade configCancelamentoRestoFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ObrigacaoAPagarFacade obrigacaoAPagarFacade;
    @EJB
    private DesdobramentoObrigacaoAPagarFacade desdobramentoObrigacaoAPagarFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SaldoDividaPublicaFacade saldoDividaPublicaFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    @EJB
    private ExecucaoContratoEstornoFacade execucaoContratoEstornoFacade;
    @EJB
    private SolicitacaoLiquidacaoEstornoFacade solicitacaoLiquidacaoEstornoFacade;
    @EJB
    private SolicitacaoEmpenhoEstornoFacade solicitacaoEmpenhoEstornoFacade;
    @EJB
    private DesdobramentoEmpenhoFacade desdobramentoEmpenhoFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoSolicitacaoMaterialFacade;
    @EJB
    private ExecucaoProcessoFacade execucaoProcessoFacade;

    public EmpenhoEstornoFacade() {
        super(EmpenhoEstorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public EmpenhoEstorno recuperar(Object id) {
        EmpenhoEstorno entity = super.recuperar(id);
        entity.getDesdobramentos().size();
        return entity;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }


    public void gerarNumeroEstornoEmpenho(EmpenhoEstorno entity) {
        entity.setNumero(singletonGeradorCodigoContabil.getNumeroEmpenho(entity.getEmpenho().getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataEstorno()));
    }

    private void gerarMovimentoDespesaORC(EmpenhoEstorno entity) {
        if (entity.isEmpenhoEstornoNormal()) {
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                entity.getEmpenho().getFonteDespesaORC(),
                OperacaoORC.EMPENHO,
                TipoOperacaoORC.ESTORNO,
                entity.getValor(),
                DataUtil.getDataComHoraAtual(entity.getDataEstorno()),
                entity.getUnidadeOrganizacional(),
                entity.getId().toString(),
                entity.getClass().getSimpleName(),
                entity.getNumero(),
                entity.getHistoricoRazao()
            );
            MovimentoDespesaORC mdorc = saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
            entity.setMovimentoDespesaORC(mdorc);
        }
    }

    @Override
    public void salvar(EmpenhoEstorno entity) {
        entity.gerarHistoricos();
        empenhoFacade.getPortalTransparenciaNovoFacade().salvarPortal(new EmpenhoEstornoPortal(entity));
        em.merge(entity);
    }

    public EmpenhoEstorno salvarNovoEstorno(EmpenhoEstorno entity, SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno) {
        try {
            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataEstorno());
                if (empenhoFacade.getSingletonConcorrenciaContabil().isDisponivel(entity.getEmpenho())) {
                    empenhoFacade.getSingletonConcorrenciaContabil().bloquear(entity.getEmpenho());

                    validarEmpenho(entity);
                    validarHierarquia(entity);

                    gerarSaldoDividaPublica(entity);
                    movimentarEmpenho(entity);
                    validarCotaOrcamentaria(entity);
                    alterarSituacaoDiaria(entity);
                    entity = salvarRegistroGerandoHistorico(entity);
                    gerarMovimentoDespesaORC(entity);
                    estornarExecucao(entity, solicitacaoEmpenhoEstorno);
                    atribuirEstornoEmpenhoNaSolicitacaoEstorno(entity, solicitacaoEmpenhoEstorno);
                    contabilizarEstornoEmpenho(entity);
                    empenhoFacade.getPortalTransparenciaNovoFacade().salvarPortal(new EmpenhoEstornoPortal(entity));
                    empenhoFacade.getSingletonConcorrenciaContabil().desbloquear(entity.getEmpenho());
                } else {
                    throw new ExcecaoNegocioGenerica("O Empenho " + entity.getEmpenho() + " está sendo utilizado por outro usuário. Caso o problema persistir, selecione novamente o Empenho.");
                }
            }
            return entity;
        } catch (Exception e) {
            entity.getEmpenho().setSaldo(entity.getEmpenho().getSaldo().add(entity.getValor()));
            empenhoFacade.getSingletonConcorrenciaContabil().desbloquear(entity.getEmpenho());
            throw e;
        }
    }

    private void alterarSituacaoDiaria(EmpenhoEstorno entity) {
        if (entity.getEmpenho().getPropostaConcessaoDiaria() != null && entity.getEmpenho().getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            entity.getEmpenho().getPropostaConcessaoDiaria().setSituacao(SituacaoDiaria.INDEFERIDO);
            em.merge(entity.getEmpenho().getPropostaConcessaoDiaria());
        }
    }

    private void gerarSaldoDividaPublica(EmpenhoEstorno entity) {
        if (entity.getEmpenho().getDividaPublica() != null) {
            ConfiguracaoContabil configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
            try {
                if (hasContaDeDespesaConfiguradaParaGerarSaldoDividaPublica(entity.getEmpenho(), configuracaoContabil)) {
                    saldoDividaPublicaFacade.gerarMovimento(entity.getDataEstorno(),
                        entity.getValor(),
                        entity.getUnidadeOrganizacional(),
                        entity.getEmpenho().getDividaPublica(),
                        OperacaoMovimentoDividaPublica.EMPENHO,
                        true,
                        OperacaoDiarioDividaPublica.EMPENHO,
                        Intervalo.CURTO_PRAZO,
                        entity.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao(),
                        true);
                }
            } catch (Exception e) {
                throw new ExcecaoNegocioGenerica(e.getMessage());
            }
        }
    }

    private boolean hasContaDeDespesaConfiguradaParaGerarSaldoDividaPublica(Empenho entity, ConfiguracaoContabil configuracaoContabil) {
        for (ConfiguracaoContabilContaDespesa configuracaoContabilContaDespesa : configuracaoContabil.getContasDespesa()) {
            if (entity.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo().startsWith(configuracaoContabilContaDespesa.getContaDespesa().getCodigoSemZerosAoFinal())) {
                return true;
            }
        }
        return false;
    }

    private void estornarExecucao(EmpenhoEstorno entity, SolicitacaoEmpenhoEstorno solEmpEst) {
        if (entity.getEmpenho().isEmpenhoIntegracaoContrato() && solEmpEst != null) {
            contratoFacade.aumentarSaldoAtualContrato(entity.getValor(), entity.getEmpenho().getContrato());
            gerarSaldoReservadoPorLicitacaoPorExecucao(entity, entity.getEmpenho().getContrato().getObjetoAdequado().getProcessoDeCompra());
        }
        if (entity.getEmpenho().isEmpenhoIntegracaoExecucaoProcesso() && solEmpEst != null) {
            gerarSaldoReservadoPorLicitacaoPorExecucao(entity, entity.getEmpenho().getExecucaoProcesso().getProcessoCompra());
        }
    }


    private void atribuirEstornoEmpenhoNaSolicitacaoEstorno(EmpenhoEstorno entity, SolicitacaoEmpenhoEstorno solEmpEst) {
        if (solEmpEst != null) {
            solEmpEst.setEmpenhoEstorno(entity);
            em.merge(solEmpEst);
        }
    }

    private void gerarSaldoReservadoPorLicitacaoPorExecucao(EmpenhoEstorno entity, ProcessoDeCompra processoCompra) {
        if (entity.isEmpenhoEstornoNormal()
            && entity.getEmpenho().getSolicitacaoEmpenho() != null
            && processoCompra != null) {

            Exercicio exercicioSolEmp = empenhoFacade.getExercicioFacade().getExercicioPorAno(DataUtil.getAno(entity.getEmpenho().getSolicitacaoEmpenho().getDataSolicitacao()));
            if (sistemaFacade.getExercicioCorrente().equals(exercicioSolEmp) && !entity.getEmpenho().getSolicitacaoEmpenho().getGerarReserva()) {
                gerarSaldoReservadoPorLicitacao(entity);
            }
        }
    }

    private void gerarSaldoReservadoPorLicitacao(EmpenhoEstorno entity) {
        if (entity.isEmpenhoEstornoNormal()) {
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                entity.getEmpenho().getFonteDespesaORC(),
                OperacaoORC.RESERVADO_POR_LICITACAO,
                TipoOperacaoORC.NORMAL,
                entity.getValor(),
                DataUtil.getDataComHoraAtual(entity.getDataEstorno()),
                entity.getUnidadeOrganizacional(),
                entity.getId().toString(),
                entity.getClass().getSimpleName(),
                entity.getNumero(),
                entity.getHistoricoRazao()
            );
            MovimentoDespesaORC mdorc = saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
            entity.setMovimentoDespesaORC(mdorc);
        }
    }

    public BigDecimal getSaldoFonteDespesaORC(EmpenhoEstorno selecionado) {
        try {
            if (selecionado.getEmpenho().getFonteDespesaORC() != null) {
                return saldoFonteDespesaORCFacade.saldoRealPorFonte(
                    selecionado.getEmpenho().getFonteDespesaORC(),
                    selecionado.getDataEstorno(),
                    selecionado.getUnidadeOrganizacional());
            }
            return BigDecimal.ZERO;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(" Erro ao recuperar o saldo " + ex.getMessage());
        }
    }

    public void salvarNovoParaFechamentoExercicio(PrescricaoEmpenho prescricaoEmpenho) {
        EmpenhoEstorno entity = prescricaoEmpenho.getEmpenhoEstorno();
        try {
            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                validarEmpenhoFechamentoExercicio(entity);
//                atribuirMovimentoDespesaOrcFechamento(prescricaoEmpenho);
                Empenho empenho = empenhoFacade.recuperarComFind(entity.getEmpenho().getId());
                empenho.setSaldo(empenho.getSaldo().subtract(entity.getValor()));
                em.merge(empenho);
                entity = salvarRegistroGerandoHistorico(entity);
                contabilizarEmpenhoEstornoParaFechamentoDeExercicio(entity);
            }
        } catch (ValidacaoException ve) {
            throw ve;
        } catch (Exception e) {
            entity.getEmpenho().setSaldo(entity.getEmpenho().getSaldo().add(entity.getValor()));
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    private EmpenhoEstorno salvarRegistroGerandoHistorico(EmpenhoEstorno entity) {
        if (entity.getId() == null) {
            gerarNumeroEstornoEmpenho(entity);
            entity.gerarHistoricos();
            em.persist(entity);
        } else {
            entity.gerarHistoricos();
            entity = em.merge(entity);
        }
        return entity;
    }

    private void atribuirMovimentoDespesaOrcFechamento(PrescricaoEmpenho prescricaoEmpenho) {
        EmpenhoEstorno entity = prescricaoEmpenho.getEmpenhoEstorno();
        OperacaoORC operacao = prescricaoEmpenho.isProcessado() ? OperacaoORC.EMPENHORESTO_PROCESSADO : OperacaoORC.EMPENHORESTO_NAO_PROCESSADO;
        SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
            entity.getEmpenho().getFonteDespesaORC(),
            operacao,
            TipoOperacaoORC.ESTORNO,
            prescricaoEmpenho.getValor(),
            entity.getDataEstorno(),
            entity.getUnidadeOrganizacional(),
            entity.getId().toString(),
            entity.getClass().getSimpleName(),
            entity.getNumero(),
            entity.getHistoricoRazao()
        );
        MovimentoDespesaORC mdorc = saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
        entity.setMovimentoDespesaORC(mdorc);
    }

    private void atribuirMovimentoDespesaOrc(EmpenhoEstorno entity) {
        SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
            entity.getEmpenho().getFonteDespesaORC(),
            OperacaoORC.EMPENHO,
            TipoOperacaoORC.ESTORNO,
            entity.getValor(),
            entity.getDataEstorno(),
            entity.getUnidadeOrganizacional(),
            entity.getId().toString(),
            entity.getClass().getSimpleName(),
            entity.getNumero(),
            entity.getHistoricoRazao());
        MovimentoDespesaORC mdorc = saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
        entity.setMovimentoDespesaORC(mdorc);
    }

    private void validarHierarquia(EmpenhoEstorno entity) {
        hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataEstorno());
    }

    private void movimentarEmpenho(EmpenhoEstorno entity) {
        Empenho empenho = empenhoFacade.recuperar(entity.getEmpenho().getId());
        if (empenho.getSaldo().compareTo(entity.getValor()) < 0) {
            throw new ValidacaoException("O movimento já foi salvo por outro usuário! Atualize a página e tente novamente.");
        }
        if (entity.isEstornoEmpenhoDeObrigacaoPagar()) {
            for (EmpenhoObrigacaoPagar empenhoOp : empenho.getObrigacoesPagar()) {

                ObrigacaoAPagar obrigacaoPagar = obrigacaoAPagarFacade.recuperar(empenhoOp.getObrigacaoAPagar().getId());

                for (DesdobramentoEmpenhoEstorno desdEstorno : entity.getDesdobramentos()) {
                    empenho = movimentarSaldoDesdobramentoEmpenho(empenho, desdEstorno);
                    obrigacaoPagar = movimentarSaldoDesdobramentoObrigacaoPagar(obrigacaoPagar, desdEstorno);
                }
                if (obrigacaoPagar.isObrigacaoPagarAntesEmpenho()) {
                    obrigacaoPagar.setSaldoEmpenho(obrigacaoPagar.getSaldoEmpenho().add(entity.getValorTotalDetalhamento()));
                    empenho.setSaldoObrigacaoPagarAntesEmp(empenho.getSaldoObrigacaoPagarAntesEmp().subtract(entity.getValorTotalDetalhamento()));
                }
                em.merge(obrigacaoPagar);
            }
        } else if (!entity.getEmpenho().getDesdobramentos().isEmpty()) {
            for (DesdobramentoEmpenhoEstorno desdEstorno : entity.getDesdobramentos()) {
                empenho = movimentarSaldoDesdobramentoEmpenho(empenho, desdEstorno);
            }
        }
        empenho.setSaldo(empenho.getSaldo().subtract(entity.getValor()));
        em.merge(empenho);

        entity.setEmpenho(empenho);
    }

    private ObrigacaoAPagar movimentarSaldoDesdobramentoObrigacaoPagar(ObrigacaoAPagar obrigacaoPagar, DesdobramentoEmpenhoEstorno desdEstorno) {
        for (DesdobramentoObrigacaoPagar desdObrigacao : obrigacaoPagar.getDesdobramentos()) {
            if (desdEstorno.getConta().equals(desdObrigacao.getConta())) {
                desdObrigacao.setSaldo(desdObrigacao.getSaldo().add(desdEstorno.getValor()));
            }
        }
        return obrigacaoPagar;
    }

    private Empenho movimentarSaldoDesdobramentoEmpenho(Empenho empenho, DesdobramentoEmpenhoEstorno desdEstorno) {
        for (DesdobramentoEmpenho desdEmpenho : empenho.getDesdobramentos()) {
            if (desdEstorno.getConta().equals(desdEmpenho.getConta())) {
                desdEmpenho.setSaldo(desdEmpenho.getSaldo().subtract(desdEstorno.getValor()));
            }
        }
        return empenho;
    }

    private void validarCotaOrcamentaria(EmpenhoEstorno entity) {
        if (entity.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.NORMAL)) {
            Calendar data = Calendar.getInstance();
            data.setTime(entity.getDataEstorno());
            int mes = (data.get(Calendar.MONTH)) + 1;
            cotaOrcamentariaFacade.diminuirValorUtilizadoCotaOrcamentaria(entity.getEmpenho().getFonteDespesaORC(), mes, entity.getValor());
        }
    }

    private void validarEmpenho(EmpenhoEstorno entity) {
        ValidacaoException ve = new ValidacaoException();
        if (entity.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo valor deve ser maior que zero(0).");
        }
        if (entity.getEmpenho() != null) {
            validarSaldoEmpenhoEstornoEmpenho(entity, ve);
            validarDataEmpenho(entity, ve);
        }
        ve.lancarException();
    }

    private void validarEmpenhoFechamentoExercicio(EmpenhoEstorno entity) {
        ValidacaoException ve = new ValidacaoException();
        if (entity.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo valor deve ser maior que zero(0).");
        }
        if (entity.getEmpenho() != null) {
            validarSaldoEmpenhoResto(entity, ve);
            validarSaldoEmpenhoNormal(entity, ve);
            validarDataEmpenho(entity, ve);
        }
        ve.lancarException();
    }

    private void validarSaldoEmpenhoNormal(EmpenhoEstorno entity, ValidacaoException ve) {
        if (entity.getEmpenho().isEmpenhoNormal()) {
            validarSaldoEmpenhoEstornoEmpenho(entity, ve);
        }
    }

    private void validarSaldoEmpenhoEstornoEmpenho(EmpenhoEstorno entity, ValidacaoException ve) {
        if (entity.getEmpenho().getSaldo().compareTo(entity.getValor()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de <b> " + Util.formataValor(entity.getValor()) + "</b> é maior que o saldo de <b> " + Util.formataValor(entity.getEmpenho().getSaldo()) + " </b> disponível no Empenho " + entity.getEmpenho());
        }
    }

    private void validarSaldoEmpenhoResto(EmpenhoEstorno entity, ValidacaoException ve) {
        if (entity.getEmpenho().isEmpenhoRestoPagar()) {
            validarSaldoNaoProcessado(entity, ve);
            validarSaldoProcessado(entity, ve);
        }
    }

    private void validarDataEmpenho(EmpenhoEstorno entity, ValidacaoException ve) {
        if (DataUtil.dataSemHorario(entity.getDataEstorno()).before(DataUtil.dataSemHorario(entity.getEmpenho().getDataEmpenho()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A data do estorno de empenho (" + DataUtil.getDataFormatada(entity.getDataEstorno()) + ") deve ser maior ou igual a data do empenho. Data do empenho: <b>" + DataUtil.getDataFormatada(entity.getEmpenho().getDataEmpenho()) + "</b>.");
        }
    }


    private void validarSaldoProcessado(EmpenhoEstorno entity, ValidacaoException ve) {
        if (TipoRestosProcessado.PROCESSADOS.equals(entity.getEmpenho().getTipoRestosProcessados())) {
            BigDecimal saldo = BigDecimal.ZERO;
            List<Liquidacao> liquidacaos = empenhoFacade.buscarLiquidacoesPorEmpenho(entity.getEmpenho());
            for (Liquidacao liquidacao : liquidacaos) {
                saldo = saldo.add(liquidacao.getSaldo());
            }
            if (saldo.compareTo(entity.getValor()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de <b> " + Util.formataValor(entity.getValor()) + "</b> é maior que o saldo de <b> " + Util.formataValor(saldo) + " </b> disponível das liquidações do Empenho " + entity.getEmpenho());
            }
        }
    }

    private void validarSaldoNaoProcessado(EmpenhoEstorno entity, ValidacaoException ve) {
        if (TipoRestosProcessado.NAO_PROCESSADOS.equals(entity.getEmpenho().getTipoRestosProcessados())) {
            validarSaldoEmpenhoEstornoEmpenho(entity, ve);
        }
    }

    public List<EmpenhoEstorno> buscarEmpenhoEstornoPorUnidadesExercicio(List<HierarquiaOrganizacional> listaUnidades, Exercicio exercicio, CategoriaOrcamentaria categoria) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }
        String sql = "SELECT est "
            + " FROM EmpenhoEstorno est"
            + " WHERE  est.empenho.exercicio.id = :exercicio"
            + " and est.unidadeOrganizacional in ( :unidades )" +
            " and est.categoriaOrcamentaria = :categoria";
        Query q = em.createQuery(sql, EmpenhoEstorno.class);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("unidades", unidades);
        q.setParameter("categoria", categoria);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public EventoContabil buscarEventoContabil(EmpenhoEstorno entity) {
        Preconditions.checkNotNull(entity.getEmpenho(), " Empenho está nulo.");
        Preconditions.checkNotNull(entity.getEmpenho().getDespesaORC(), " A despesa do empenho está nula.");

        Conta contaDespesa = entity.getEmpenho().getDespesaORC().getContaDeDespesa();
        Preconditions.checkNotNull(contaDespesa, " Conta de despesa não recuperada da despesaOrc.");
        if (!entity.isEmpenhoEstornoResto()) {
            return buscarEventoContabilCategoriaNormal(entity, contaDespesa);
        } else {
            return buscarEventoContabilCategoriaRestoPagar(entity, contaDespesa);
        }
    }

    private EventoContabil buscarEventoContabilCategoriaRestoPagar(EmpenhoEstorno entity, Conta contaDespesa) {
        EventoContabil eventoContabil = null;
        ConfigCancelamentoResto configuracao = configCancelamentoRestoFacade.recuperarEventoPorContaDespesaAndTipoEmpenhoEstorno(
            contaDespesa,
            TipoLancamento.NORMAL,
            entity.getDataEstorno(),
            entity.getEmpenho().getTipoRestosProcessados(),
            entity.getTipoEmpenhoEstorno(),
            PatrimonioLiquido.buscarPorNatureza(entidadeFacade.recuperarEntidadePorUnidadeOrcamentaria(entity.getUnidadeOrganizacional()).getTipoUnidade()));
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    private EventoContabil buscarEventoContabilCategoriaNormal(EmpenhoEstorno entity, Conta contaDespesa) {
        EventoContabil eventoContabil = null;
        ConfigEmpenho configuracao = null;
        if (!configuracaoContabilFacade.configuracaoContabilVigente().getBuscarEventoEmpDiferenteObrig()) {
            configuracao = configEmpenhoFacade.buscarCDEEmpenho(
                contaDespesa,
                TipoLancamento.ESTORNO,
                entity.getDataEstorno(),
                entity.getEmpenho().getTipoReconhecimento(),
                entity.getEmpenho().getTipoContaDespesa(),
                entity.getEmpenho().getSubTipoDespesa());
        } else {
            configuracao = configEmpenhoFacade.buscarCDEEmpenho(
                contaDespesa,
                TipoLancamento.ESTORNO,
                entity.getDataEstorno(),
                TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA,
                entity.getEmpenho().getTipoContaDespesa(),
                entity.getEmpenho().getSubTipoDespesa());
        }
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    public EventoContabil buscarEventoContabilPorDesdobramento(DesdobramentoEmpenhoEstorno desdobramento) {
        EventoContabil eventoContabil = null;
        ConfigEmpenho configuracao = configEmpenhoFacade.buscarCDEEmpenho(
            desdobramento.getConta(),
            TipoLancamento.ESTORNO,
            desdobramento.getEmpenhoEstorno().getDataEstorno(),
            desdobramento.getEmpenhoEstorno().getEmpenho().getTipoReconhecimento(),
            desdobramento.getEmpenhoEstorno().getEmpenho().getTipoContaDespesa(),
            desdobramento.getEmpenhoEstorno().getEmpenho().getSubTipoDespesa());
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    private void contabilizarEmpenhoEstornoNormal(EmpenhoEstorno entity) {
        try {
            EventoContabil eventoContabil = buscarEventoContabilCategoriaNormal(entity, getContaDespesaEmpenho(entity));
            if (eventoContabil != null && eventoContabil.getId() != null) {
                entity.setEventoContabil(eventoContabil);
                entity.gerarHistoricos();
                contabilizacaoFacade.contabilizar(getParametroEvento(entity));
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private Conta getContaDespesaEmpenho(EmpenhoEstorno entity) {
        Preconditions.checkNotNull(entity.getEmpenho(), "Empenho não foi preenchido.");
        Preconditions.checkNotNull(entity.getEmpenho().getDespesaORC(), "Despesa orc não foi preenchida.");
        Preconditions.checkNotNull(entity.getEmpenho().getDespesaORC().getProvisaoPPADespesa(), "Provisão Despesa não foi preenchida.");

        Conta contaDeDespesa = entity.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa();
        Preconditions.checkNotNull(contaDeDespesa, "A conta de despesa do empenho não foi preenchida.");
        return contaDeDespesa;
    }

    public ParametroEvento getParametroEventoDesdobramento(EntidadeContabil entidadeContabil) {
        try {
            DesdobramentoEmpenhoEstorno desdobramento = (DesdobramentoEmpenhoEstorno) entidadeContabil;
            ParametroEvento parametroEvento = criarParametroEventoDesdobramento(desdobramento);
            ItemParametroEvento item = criarItemParametroEvento(desdobramento.getValor(), parametroEvento);
            List<ObjetoParametro> objetos = criarObjetosParametros(desdobramento.getEmpenhoEstorno(), desdobramento, item);
            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    public ParametroEvento getParametroEvento(EntidadeContabil entidadeContabil) {
        try {
            EmpenhoEstorno entity = (EmpenhoEstorno) entidadeContabil;
            ParametroEvento parametroEvento = criarParametroEvento(entity);

            ItemParametroEvento item = criarItemParametroEvento(entity.getValor(), parametroEvento);
            List<ObjetoParametro> objetos = criarObjetosParametros(entity, null, item);

            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    private List<ObjetoParametro> criarObjetosParametros(EmpenhoEstorno entity, DesdobramentoEmpenhoEstorno desdobramentoEmpenhoEstorno, ItemParametroEvento item) {
        List<ObjetoParametro> objetos = Lists.newArrayList();
        if (desdobramentoEmpenhoEstorno == null) {
            objetos.add(new ObjetoParametro(entity, item));
            objetos.add(new ObjetoParametro(getContaDespesaEmpenho(entity), item));
        } else {
            objetos.add(new ObjetoParametro(desdobramentoEmpenhoEstorno, item));
            objetos.add(new ObjetoParametro(desdobramentoEmpenhoEstorno.getConta(), item));
        }
        objetos.add(new ObjetoParametro(entity.getEmpenho().getClasseCredor(), item));
        objetos.add(new ObjetoParametro(entity.getEmpenho().getFornecedor(), item));
        if (entity.isEmpenhoEstornoNormal()) {
            objetos.add(new ObjetoParametro(entity.getEmpenho(), item));
            objetos.add(new ObjetoParametro(entity.getEmpenho().getFonteDespesaORC(), item));
            objetos.add(new ObjetoParametro(entity.getEmpenho().getDespesaORC().getProvisaoPPADespesa(), item));
            objetos.add(new ObjetoParametro(entity.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos(), item));
        }
        return objetos;
    }

    private ParametroEvento criarParametroEvento(EmpenhoEstorno entity) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataEstorno());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setClasseOrigem(EmpenhoEstorno.class.getSimpleName());
        parametroEvento.setIdOrigem(entity.getId().toString());
        parametroEvento.setExercicio(entity.getEmpenho().getExercicio());
        return parametroEvento;
    }

    private ItemParametroEvento criarItemParametroEvento(BigDecimal valor, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(valor);
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    public void contabilizarEmpenhoEstornoParaFechamentoDeExercicio(EmpenhoEstorno entity) {
        if (entity.getEventoContabil().getId() != null) {
            entity.gerarHistoricos();
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataEstorno());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(entity.getEventoContabil());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());

            parametroEvento.setExercicio(entity.getEmpenho().getExercicio());
            parametroEvento.setTipoBalancete(TipoBalancete.APURACAO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> listaObj = new ArrayList<ObjetoParametro>();
            listaObj.add(new ObjetoParametro(entity, item));
            listaObj.add(new ObjetoParametro(((ContaDespesa) entity.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa()), item));
            listaObj.add(new ObjetoParametro(entity.getEmpenho().getClasseCredor(), item));
            item.setObjetoParametros(listaObj);
            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Informe o Evento Contábil");
        }
    }

    private ParametroEvento criarParametroEventoDesdobramento(DesdobramentoEmpenhoEstorno desdobramento) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(desdobramento.getEmpenhoEstorno().getHistoricoRazao());
        parametroEvento.setDataEvento(desdobramento.getEmpenhoEstorno().getDataEstorno());
        parametroEvento.setUnidadeOrganizacional(desdobramento.getEmpenhoEstorno().getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(desdobramento.getEventoContabil());
        parametroEvento.setClasseOrigem(DesdobramentoEmpenhoEstorno.class.getSimpleName());
        parametroEvento.setIdOrigem(desdobramento.getId() == null ? null : desdobramento.getId().toString());
        parametroEvento.setExercicio(desdobramento.getConta().getExercicio());
        return parametroEvento;
    }

    private void contabilizarEmpenhoEstornoResto(EmpenhoEstorno entity) {
        EventoContabil eventoContabil = buscarEventoContabilCategoriaRestoPagar(entity, getContaDespesaEmpenho(entity));
        if (eventoContabil != null) {
            entity.setEventoContabil(eventoContabil);
            entity.gerarHistoricos();
            contabilizacaoFacade.contabilizar(getParametroEvento(entity));
        }
    }

    private void contabilizarEstornoEmpenho(EmpenhoEstorno entity) {
        if (!Hibernate.isInitialized(entity.getDesdobramentos())) {
            entity = recuperar(entity.getId());
        }
        if (entity.isEmpenhoEstornoNormal()) {
            contabilizarEmpenhoEstornoNormal(entity);
        } else {
            contabilizarEmpenhoEstornoResto(entity);
        }
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarEstornoEmpenho((EmpenhoEstorno) entidadeContabil);
    }

    public List<EmpenhoEstorno> buscarEstornoEmpenhoPorObrigacaoPagar(ObrigacaoAPagar obrigacaoAPagar) {
        String sql = " select est.* from empenhoestorno est " +
            "         inner join empenho e on e.id = est.empenho_id " +
            "         inner join empenhoobrigacaopagar eop on eop.empenho_id = e.id " +
            " where eop.obrigacaoapagar_id = :idObrigacao ";
        Query q = em.createNativeQuery(sql, EmpenhoEstorno.class);
        q.setParameter("idObrigacao", obrigacaoAPagar.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public ObrigacaoAPagarFacade getObrigacaoAPagarFacade() {
        return obrigacaoAPagarFacade;
    }

    public DesdobramentoObrigacaoAPagarFacade getDesdobramentoObrigacaoAPagarFacade() {
        return desdobramentoObrigacaoAPagarFacade;
    }

    public SaldoDividaPublicaFacade getSaldoDividaPublicaFacade() {
        return saldoDividaPublicaFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(EmpenhoEstorno.class, filtros);
        consulta.incluirJoinsComplementar(" inner join empenho emp on obj.empenho_id = emp.id ");
        consulta.incluirJoinsOrcamentoDespesa(" emp.fontedespesaorc_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "emp.fornecedor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "emp.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    public BigDecimal getValorEstornado(Empenho empenho, CategoriaOrcamentaria categoriaOrcamentaria) {
        String sql = " " +
            " select " +
            "   coalesce(sum(est.valor), 0) as total " +
            " from empenhoestorno est " +
            " where est.empenho_id = :idEmpenho " +
            "   and est.categoriaOrcamentaria = :categoriaOrcamentaria ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEmpenho", empenho.getId());
        q.setParameter("categoriaOrcamentaria", categoriaOrcamentaria.name());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getCancelamentoRestoPagarEmpenhoOriginal(Empenho empenho) {
        try {
            String sql = " " +
                " select " +
                "   coalesce(sum(est.valor), 0) as total " +
                " from empenhoestorno est " +
                "   inner join empenho resto on resto.id = est.empenho_id " +
                "   inner join empenho original on original.id = resto.empenho_id " +
                " where original.id = :idEmpenho ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idEmpenho", empenho.getId());
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public List<EmpenhoEstorno> buscarEstornoEmpenho(Empenho empenho) {
        try {
            String sql = " select est.* from empenhoestorno est where est.empenho_id = :idEmpenho ";
            Query q = em.createNativeQuery(sql, EmpenhoEstorno.class);
            q.setParameter("idEmpenho", empenho.getId());
            return q.getResultList();
        } catch (NoResultException e) {
            return Lists.newArrayList();
        }
    }

    public List<EmpenhoEstorno> buscarCancelamentoRestoEmpenhoOriginal(Empenho empenho) {
        try {
            String sql = " select est.* from empenhoestorno est " +
                "   inner join empenho original on original.id = est.empenho_id " +
                "   inner join empenho resto on resto.id = original.empenho_id " +
                " where resto.id = :idEmpenho ";
            Query q = em.createNativeQuery(sql, EmpenhoEstorno.class);
            q.setParameter("idEmpenho", empenho.getId());
            return q.getResultList();
        } catch (NoResultException e) {
            return Lists.newArrayList();
        }
    }

    public List<NotaExecucaoOrcamentaria> buscarNotaEstornoEmpenho(String condicao, CategoriaOrcamentaria categoriaOrcamentaria, String nomeDaNota) {
        String sql = " SELECT " +
            "    nota.numero||'/'||exerc.ano as numero, " +
            "    nota.dataestorno as data_EMPENHO, " +
            "    nota.HISTORICONOTA as historico_emp, " +
            "    e.tipoempenho as tipo, " +
            "    TRIM(vworg.CODIGO) AS CD_ORGAO, " +
            "    TRIM(vworg.DESCRICAO) AS DESC_ORGAO, " +
            "    TRIM(vw.CODIGO) AS CD_UNIDADE, " +
            "    TRIM(vw.DESCRICAO) AS DESC_UNIDADE, " +
            "    f.codigo || '.' || sf.codigo || '.' || p.codigo || '.' || tpa.codigo || a.codigo || '.' || sub.codigo as CD_PROG_TRABALHO, " +
            "    ct_desp.codigo as elemento, " +
            "    ct_desp.descricao as especificao_despesa, " +
            "    coalesce(pf.nome, pj.razaosocial) as nome_pessoa, " +
            "    formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " +
            "    fonte_r.codigo || ' - ' || trim(fonte_r.descricao)  || ' (' || cdest.codigo || ')' as desc_destinacao, " +
            "    nota.valor as valor, " +
            "    RPAD(FRT_EXTENSO_MONETARIO(nota.VALOR)||' ',600,'* ') AS VALOR_EXTENSO, " +
            "    COALESCE(ENDERECO.LOGRADOURO,'Pessoa não possui Logradouro') AS LOGRADOURO, " +
            "    coalesce(endereco.localidade,'Pessoa não possui Localidade') as localidade, " +
            "    A.DESCRICAO AS DESC_ACAO, " +
            "    coalesce(lic.numero, disp.numeroDaDispensa, regext.numeromodalidade)  || ' - Processo Licitatório ' || coalesce(lic.numerolicitacao, disp.numerodadispensa, regext.numeroregistro) || ' - ' || to_char(coalesce(lic.emitidaem, disp.datadadispensa, regext.dataregistrocarona),'dd/MM/YYYY') as modalidade, " +
            "    e.MODALIDADELICITACAO as MODALIDADELICITACAO, " +
            "    classe.codigo || ' - ' || classe.descricao as desc_classepessoa, " +
            (categoriaOrcamentaria.isResto()
                ? " COALESCE(nota.SALDODISPONIVEL,0) AS SALDO_ANTERIOR, COALESCE(nota.SALDODISPONIVEL,0) + nota.valor AS SALDO_POSTERIOR, "
                : " COALESCE(E.VALOR ,0) AS VALOR_EMPENHADO, COALESCE(E.SALDO, 0) AS SALDO_ESTORNAR, "
            ) +
            "    COALESCE(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " +
            "    COALESCE(ENDERECO.uf,'sem UF ') AS UF, " +
            "    COALESCE(SUBSTR(ENDERECO.CEP,1,5)||'-'||SUBSTR(ENDERECO.CEP,6,3),'sem CEP') AS CEP, " +
            "    vw.subordinada_id as idUnidadeOrcamentaria, " +
            "    nota.id as idNota " +
            " FROM empenhoestorno nota " +
            " INNER JOIN empenho e on nota.empenho_id = e.id " +
            (categoriaOrcamentaria.isResto() ? " inner join empenho emp_original on e.empenho_id = emp_original.id " : "") +
            " INNER JOIN fontedespesaorc FONTE ON e.fontedespesaorc_id = fonte.id " +
            " INNER JOIN despesaorc DESPESA ON fonte.despesaorc_id = despesa.id " +
            " INNER JOIN provisaoppadespesa PROVISAO ON despesa.provisaoppadespesa_id= provisao.id " +
            " INNER JOIN SUBACAOPPA SUB ON SUB.ID = provisao.subacaoppa_id " +
            " inner join provisaoppafonte ppf on ppf.id = fonte.PROVISAOPPAFONTE_ID " +
            " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            " INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            " INNER JOIN programappa P ON P.ID = A.programa_id " +
            " INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            " INNER JOIN CONTA CT_DESP ON provisao.contadedespesa_id = CT_DESP.ID " +
            " inner join exercicio exerc on e.exercicio_id = exerc.id " +
            " inner join vwhierarquiaorcamentaria vw on nota.unidadeorganizacional_id  = vw.subordinada_id " +
            " inner join vwhierarquiaorcamentaria vworg on vw.superior_id  = vworg.subordinada_id " +
            " INNER JOIN PESSOA Pes ON e.fornecedor_id = Pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " left join enderecocorreio endereco on pes.enderecoprincipal_id = endereco.id " +
            " left join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id " +
            " left join conta cdest on cdest.id = cd.id " +
            " left join fontederecursos fonte_r on fonte_r.id = cd.fontederecursos_id " +
            (categoriaOrcamentaria.isResto() ? "  and fonte_r.exercicio_id = emp_original.exercicio_id " : "  and fonte_r.exercicio_id = exerc.id ") +
            " left join contrato contrato on e.contrato_id = contrato.id " +
            " left join conlicitacao conlic on conlic.contrato_id = contrato.id " +
            " left join licitacao lic on lic.id = conlic.licitacao_id " +
            " left join condispensalicitacao conDisp on condisp.contrato_id = contrato.id " +
            " left join dispensadelicitacao disp on disp.id = condisp.dispensadelicitacao_id " +
            " left join CONREGPRECOEXTERNO conReg on conreg.contrato_id = contrato.id " +
            " left join REGISTROSOLMATEXT regExt on regExt.id = conreg.regsolmatext_id " +
            " left join classecredor classe on e.classecredor_id = classe.id " +
            " where e.categoriaorcamentaria = :categoriaOrcamentaria " +
            " and trunc(nota.dataestorno) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(nota.dataestorno)) " +
            " AND trunc(nota.dataestorno) BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, trunc(nota.dataestorno)) " +
            condicao;
        Query q = em.createNativeQuery(sql);
        q.setParameter("categoriaOrcamentaria", categoriaOrcamentaria.name());
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nota = new NotaExecucaoOrcamentaria();
                nota.setNomeDaNota(nomeDaNota);
                nota.setNumeroDocumento((String) obj[0]);
                nota.setData(DataUtil.getDataFormatada((Date) obj[1]));
                nota.setHistorico((String) obj[2]);
                nota.setTipoEmpenho(obj[3] != null ? TipoEmpenhoDTO.valueOf((String) obj[3]).getDescricao() : "");
                nota.setCodigoOrgao((String) obj[4]);
                nota.setDescricaoOrgao((String) obj[5]);
                nota.setCodigoUnidade((String) obj[6]);
                nota.setDescricaoUnidade((String) obj[7]);
                nota.setCodigoProgramaTrabalho((String) obj[8]);
                nota.setNaturezaDespesa((String) obj[9]);
                nota.setEspecificacaoDespesa((String) obj[10]);
                nota.setNomePessoa((String) obj[11]);
                nota.setCpfCnpj((String) obj[12]);
                nota.setDescricaoDestinacao((String) obj[13]);
                nota.setValorEstorno((BigDecimal) obj[14]);
                nota.setValorPorExtenso((String) obj[15]);
                nota.setLogradouro((String) obj[16]);
                nota.setLocalidade((String) obj[17]);
                nota.setDescricaoProjetoAtividade((String) obj[18]);
                nota.setModalidadeLicitacao(obj[20] != null ? ModalidadeLicitacaoEmpenho.valueOf((String) obj[20]).getDescricao() + " " + (String) obj[19] : "");
                nota.setClassePessoa((String) obj[21]);
                if (categoriaOrcamentaria.isResto()) {
                    nota.setSaldoAnterior((BigDecimal) obj[22]);
                } else {
                    nota.setValor((BigDecimal) obj[22]);
                }
                nota.setSaldoAtual((BigDecimal) obj[23]);
                nota.setBairro((String) obj[24]);
                nota.setUf((String) obj[25]);
                nota.setCep((String) obj[26]);
                nota.setIdUnidadeOrcamentaria((BigDecimal) obj[27]);
                nota.setId(((BigDecimal) obj[28]).longValue());
                retorno.add(nota);
            }
        }
        return retorno;
    }

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }

    public ExecucaoContratoEstornoFacade getExecucaoContratoEstornoFacade() {
        return execucaoContratoEstornoFacade;
    }

    public SolicitacaoLiquidacaoEstornoFacade getSolicitacaoLiquidacaoEstornoFacade() {
        return solicitacaoLiquidacaoEstornoFacade;
    }

    public SolicitacaoEmpenhoEstornoFacade getSolicitacaoEmpenhoEstornoFacade() {
        return solicitacaoEmpenhoEstornoFacade;
    }

    public DesdobramentoEmpenhoFacade getDesdobramentoEmpenhoFacade() {
        return desdobramentoEmpenhoFacade;
    }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    public ExecucaoProcessoFacade getExecucaoProcessoFacade() {
        return execucaoProcessoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void verificarSeEmpenhoPossuiObrigacaoPagarDepoisEmpenho(EmpenhoEstorno selecionado) {
        ObrigacaoAPagar obrigacaoPagar = getObrigacaoAPagarFacade().buscarObrigacaoPagarPorEmpenho(selecionado.getEmpenho());
        if (obrigacaoPagar != null && selecionado.getEmpenho().getSaldoObrigacaoPagarDepoisEmp().compareTo(BigDecimal.ZERO) > 0
            && selecionado.getEmpenho().getSaldoDisponivelEmpenhoComObrigacao().compareTo(BigDecimal.ZERO) == 0) {
            FacesUtil.addAtencao("Esse empenho possui obrigação a pagar com reconhecimento " +
                " depois do empenho. Para estornar todo o saldo do empenho, estorne primeiro a obrigação a pagar.");
        }
    }

    public void definirEventoContabil(EmpenhoEstorno selecionado) {
        selecionado.setEventoContabil(null);
        EventoContabil eventoContabil = buscarEventoContabil(selecionado);
        if (eventoContabil != null) {
            selecionado.setEventoContabil(eventoContabil);
        }
    }
}

