/*
 * Codigo gerado automaticamente em Mon Oct 24 16:57:46 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.EnvioDadosRBPonto;
import br.com.webpublico.entidades.rh.ItemEnvioDadosRBPonto;
import br.com.webpublico.entidades.rh.configuracao.AutorizacaoUsuarioRH;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.webservices.ConfiguracaoWebServiceRH;
import br.com.webpublico.entidadesauxiliares.rh.integracaoponto.EditarFeriasDTO;
import br.com.webpublico.entidadesauxiliares.rh.integracaoponto.ExcluirFeriasDTO;
import br.com.webpublico.enums.ClasseAfastamento;
import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.enums.rh.TipoEnvioDadosRBPonto;
import br.com.webpublico.enums.rh.TipoInformacaoEnvioRBPonto;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.cadastrofuncional.ReprocessamentoSituacaoFuncionalFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoWSRHFacade;
import br.com.webpublico.negocios.rh.integracaoponto.AtualizacaoDadosRBPontoFacade;
import br.com.webpublico.negocios.rh.integracaoponto.EnvioDadosRBPontoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class ConcessaoFeriasLicencaFacade extends AbstractFacade<ConcessaoFeriasLicenca> {

    private static final Logger logger = LoggerFactory.getLogger(ConcessaoFeriasLicencaFacade.class);
    private static final Integer SITUACAO_EXONERADO = 6;
    private static final Integer SITUACAO_CEDIDO_A_DISPOSICAO = 4;
    private static final Integer SITUACAO_AFASTADO = 3;
    private static final Integer SITUACAO_FERIAS = 2;
    private static final Integer SITUACAO_EXERCICIO = 1;
    private static final Integer SITUACAO_APOSENTADO = 7;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private SituacaoContratoFPFacade situacaoContratoFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private EnvioDadosRBPontoFacade envioDadosRBPontoFacade;
    @EJB
    private ConfiguracaoWSRHFacade configuracaoWSRHFacade;
    @EJB
    private AtualizacaoDadosRBPontoFacade atualizacaoDadosRBPontoFacade;
    @EJB
    private ReprocessamentoSituacaoFuncionalFacade reprocessamentoSituacaoFuncionalFacade;

    public ConcessaoFeriasLicencaFacade() {
        super(ConcessaoFeriasLicenca.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ConcessaoFeriasLicenca> listaTipos(TipoPeriodoAquisitivo tipo) {
        String hql = "select a from ConcessaoFeriasLicenca a inner join a.periodoAquisitivoFL.baseCargo.basePeriodoAquisitivo base where base.tipoPeriodoAquisitivo = :tipo ";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public List<ConcessaoFeriasLicenca> listaConcessaoFeriasLicencaPorPeriodoAquisitivo(PeriodoAquisitivoFL periodo) {
        String hql = "select a from ConcessaoFeriasLicenca a inner join a.periodoAquisitivoFL periodo where periodo = :periodo ";
        Query q = em.createQuery(hql);
        q.setParameter("periodo", periodo);
        return q.getResultList();
    }

    public List<BaseCargo> listaBasesCargo() {
        String hql = " from BaseCargo a where :vigencia >= a.inicioVigencia and "
            + " :vigencia <= coalesce(a.finalVigencia,:vigencia) OR a.finalVigencia is null";
        Query q = em.createQuery(hql);
        q.setParameter("vigencia", new Date());
        return q.getResultList();
    }

    public boolean isTemPeriodoAquisitivoFL(BaseCargo b) {
        String hql = " from PeriodoAquisitivoFL a where a.baseCargo = :base";
        Query q = em.createQuery(hql);
        q.setParameter("base", b);
        return q.getResultList().isEmpty();
    }

    public void salvar(ConcessaoFeriasLicenca entity, Arquivo arquivo, FileUploadEvent fileUploadEvent) {
        try {
            Integer totalDias = DataUtil.diferencaDiasInteira(entity.getDataInicial(), entity.getDataFinal());
            totalDias++;
            totalDias += entity.getDiasAbonoPecuniario() == null ? 0 : entity.getDiasAbonoPecuniario();
            entity.setDias(totalDias);

            Integer diasConcedidos = 0;
            for (ConcessaoFeriasLicenca concessaoSalva : buscarConcessoesFeriasLicencaPorPeriodoAquisitivoFL(entity.getPeriodoAquisitivoFL())) {
                if (!entity.getId().equals(concessaoSalva.getId())) {
                    diasConcedidos += concessaoSalva.getDias();
                }
            }

            if (entity.getPeriodoAquisitivoFL().getSaldoDeDireito().equals(totalDias + diasConcedidos)) {
                entity.getPeriodoAquisitivoFL().setStatus(StatusPeriodoAquisitivo.CONCEDIDO);
            } else {
                entity.getPeriodoAquisitivoFL().setStatus(StatusPeriodoAquisitivo.PARCIAL);
            }

            // Na alteração pegue o saldo de direito
            Integer saldoAtual = entity.getPeriodoAquisitivoFL().getSaldoDeDireito();
            saldoAtual = saldoAtual - (totalDias + diasConcedidos);
            entity.getPeriodoAquisitivoFL().setSaldo(saldoAtual);
            entity.getPeriodoAquisitivoFL().setDataAtualizacao(new Date());
            entity.setDataCadastro(new Date());

            em.merge(entity.getPeriodoAquisitivoFL());

            if (arquivo.getId() != null && arquivoFacade.verificaSeExisteArquivo(arquivo)) {
                if (entity.getArquivo() == null) {
                    arquivoFacade.removerArquivo(arquivo);
                }
            }

            if (fileUploadEvent != null) {
                entity.setArquivo(arquivo);
                arquivoFacade.salvar(arquivo, fileUploadEvent);
            }

            entity = em.merge(entity);
            reprocessarSituacoesContrato(entity.getPeriodoAquisitivoFL().getContratoFP());
            ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente();
            if (configuracaoRH.getEnvioAutomaticoDadosPonto() && TipoPeriodoAquisitivo.FERIAS.equals(entity.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo())) {
                atualizarFeriasRBPonto(entity);
            }
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    private void atualizarFeriasRBPonto(ConcessaoFeriasLicenca concessaoFeriasLicenca) {
        ConfiguracaoWebServiceRH configuracaoWebServiceRH = configuracaoWSRHFacade.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.PONTO);
        EditarFeriasDTO editarFeriasDTO = new EditarFeriasDTO(concessaoFeriasLicenca);
        atualizacaoDadosRBPontoFacade.atualizarFerias(editarFeriasDTO, configuracaoWebServiceRH);
    }

    public void salvarNovo(ConcessaoFeriasLicenca entity, ConcessaoFeriasLicenca concessaoVigente, Arquivo arquivo, FileUploadEvent fileUploadEvent) {
        Integer totalDias;
        if (entity.getLicencaPremioIndenizada()) {
            totalDias = entity.getDiasPecunia();
            entity.setDias(0);
        } else {
            totalDias = DataUtil.diferencaDiasInteira(entity.getDataInicial(), entity.getDataFinal());
            totalDias++;
        }

        if (totalDias.compareTo(entity.getPeriodoAquisitivoFL().getSaldoDeDireito()) > 0) {
            totalDias = entity.getPeriodoAquisitivoFL().getSaldoDeDireito();
        }
        totalDias += entity.getDiasAbonoPecuniario() == null ? 0 : entity.getDiasAbonoPecuniario();
        if (!entity.getLicencaPremioIndenizada()) {
            entity.setDias(totalDias);
        }

        definirStatusDoPeriodoAquisitivo(totalDias, entity);
        definirSaldoPeriodoAquisitivo(totalDias, entity);
        entity.setDataCadastro(new Date());
        em.merge(entity.getPeriodoAquisitivoFL());

        if (validaSaldo(entity)) {
            if (concessaoVigente != null) {
                concessaoVigente.setDataCadastroFinalVigencia(entity.getDataCadastro());
                em.merge(concessaoVigente);
            }

            if (fileUploadEvent != null) {
                entity.setArquivo(arquivo);
                arquivoFacade.salvar(arquivo, fileUploadEvent);
            }
            ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente();
            if (configuracaoRH.getEnvioAutomaticoDadosPonto() == null || !configuracaoRH.getEnvioAutomaticoDadosPonto() || !TipoPeriodoAquisitivo.FERIAS.equals(entity.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo())) {
                entity.setTipoEnvioDadosRBPonto(TipoEnvioDadosRBPonto.NAO_ENVIADO);
            }
            entity = em.merge(entity);
            reprocessarSituacoesContrato(entity.getPeriodoAquisitivoFL().getContratoFP());

            if (configuracaoRH.getEnvioAutomaticoDadosPonto() && TipoPeriodoAquisitivo.FERIAS.equals(entity.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo())) {
                EnvioDadosRBPonto envioDadosRBPonto = new EnvioDadosRBPonto();
                envioDadosRBPonto.setDataInicial(entity.getDataInicial());
                envioDadosRBPonto.setDataFinal(entity.getDataFinal());
                envioDadosRBPonto.setContratoFP(entity.getPeriodoAquisitivoFL().getContratoFP());
                envioDadosRBPonto.setTipoInformacaoEnvioRBPonto(TipoInformacaoEnvioRBPonto.FERIAS);
                envioDadosRBPonto.setDataEnvio(SistemaFacade.getDataCorrente());
                envioDadosRBPonto.setUsuario(sistemaFacade.getUsuarioCorrente());

                ItemEnvioDadosRBPonto itemEnvioDadosRBPonto = new ItemEnvioDadosRBPonto();
                itemEnvioDadosRBPonto.setDataInicial(entity.getDataInicial());
                itemEnvioDadosRBPonto.setDataFinal(entity.getDataFinal());
                itemEnvioDadosRBPonto.setContratoFP(entity.getPeriodoAquisitivoFL().getContratoFP());
                itemEnvioDadosRBPonto.setEnvioDadosRBPonto(envioDadosRBPonto);
                itemEnvioDadosRBPonto.setIdentificador(entity.getId());
                envioDadosRBPonto.getItensEnvioDadosRBPontos().add(itemEnvioDadosRBPonto);
                em.persist(envioDadosRBPonto);
                envioDadosRBPontoFacade.integracaoPonto(envioDadosRBPonto, TipoEnvioDadosRBPonto.ENVIO_AUTOMATICO);
            }
        } else {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Não é possível conceder férias a um funcionário que já gozou deste benefício no mesmo período aquisitivo");
            throw ve;
        }
    }


    private void definirSaldoPeriodoAquisitivo(Integer totalDias, ConcessaoFeriasLicenca entity) {
        Integer saldoAtual = entity.getPeriodoAquisitivoFL().getSaldo();
        saldoAtual = saldoAtual - totalDias;
        entity.getPeriodoAquisitivoFL().setSaldo(saldoAtual);

        entity.getPeriodoAquisitivoFL().setDataAtualizacao(new Date());
    }

    private void definirStatusDoPeriodoAquisitivo(Integer totalDias, ConcessaoFeriasLicenca entity) {
        if (totalDias.compareTo(entity.getPeriodoAquisitivoFL().getSaldo()) == 0) {
            entity.getPeriodoAquisitivoFL().setStatus(StatusPeriodoAquisitivo.CONCEDIDO);
        } else {
            entity.getPeriodoAquisitivoFL().setStatus(StatusPeriodoAquisitivo.PARCIAL);
        }
    }

    private void validarVigenciasDasSituacoesFuncionais(SituacaoContratoFP novo) {
        temSituacaoComInicioVigenciaMaiorQueANovaSituacao(novo);
    }

    private void temSituacaoComInicioVigenciaMaiorQueANovaSituacao(SituacaoContratoFP novo) {
        ValidacaoException val = new ValidacaoException();
        List<Long> situacoes = new ArrayList<>();
        situacoes.add(SituacaoFuncional.ATIVO_PARA_FOLHA);
        situacoes.add(SituacaoFuncional.AFASTADO_LICENCIADO);
        List<SituacaoContratoFP> situacoesRecuperadas = buscarSituacoesPorPeriodoAndSituacoes(novo.getInicioVigencia(), novo.getFinalVigencia(), situacoes, novo.getContratoFP());
        for (SituacaoContratoFP situacao : situacoesRecuperadas) {
            String url = "<b><a href='" + FacesUtil.getRequestContextPath() + "/contratofp/editar/" + situacao.getContratoFP().getId() + "/' target='_blank'>Editar Contrato FP</a></b>";
            logger.debug("A situação funcional {} {} está com data superior a data de concessão, verifique as situações em " + url, situacao.getInicioVigencia(), situacao);
            val.adicionarMensagemDeOperacaoNaoPermitida("A situação funcional " + DataUtil.getDataFormatada(situacao.getInicioVigencia()) + " " + situacao.getSituacaoFuncional() + " está dentro do período da concessão, verifique as situações em " + url);
            throw val;
        }

    }

    private void alterarSituacaoAnterior(SituacaoContratoFP novo) {
        SituacaoContratoFP situacaoAnterior = buscarSituacaoAnterior(novo.getInicioVigencia(), novo.getContratoFP());
        if (situacaoAnterior != null) {
            if (SituacaoFuncional.ATIVO_PARA_FOLHA.equals(situacaoAnterior.getSituacaoFuncional().getCodigo())) {
                if (situacaoAnterior.getInicioVigencia().equals(novo.getInicioVigencia())) {
                    em.remove(situacaoAnterior);
                } else {
                    situacaoAnterior.setFinalVigencia(DataUtil.removerDias(novo.getInicioVigencia(), 1));
                    em.merge(situacaoAnterior);
                }
            } else {
                logger.debug("Caso não tratado! Verificar com o PO.");
            }
        } else {
            logger.error("Não foi encontrato a Situação Anterior para atribuir o fim de vigência.");
        }
    }

    private List<SituacaoContratoFP> buscarSituacoesPorPeriodoAndSituacao(Date dataInicial, Date dataFinal, Long situacaoFuncional, ContratoFP contratoFP) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select sit.* from situacaocontratofp sit ");
        sql.append(" inner join situacaofuncional sitfun on sit.situacaofuncional_id = sitfun.id ");
        sql.append(" where sitfun.codigo <> :codigo ");
        sql.append(" and (trunc(sit.inicioVigencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ");
        sql.append(" or trunc(sit.finalVigencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')) ");
        sql.append(" and sit.contratoFP_id = :contrato ");
        Query q = em.createNativeQuery(sql.toString(), SituacaoContratoFP.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("codigo", situacaoFuncional);
        q.setParameter("contrato", contratoFP.getId());
        return q.getResultList();
    }

    private List<SituacaoContratoFP> buscarSituacoesPorPeriodoAndSituacoes(Date dataInicial, Date dataFinal, List<Long> situacoes, ContratoFP contratoFP) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select sit.* from situacaocontratofp sit ");
        sql.append(" inner join situacaofuncional sitfun on sit.situacaofuncional_id = sitfun.id ");
        sql.append(" where sitfun.codigo not in (:situacoes) ");
        sql.append(" and (trunc(sit.inicioVigencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ");
        sql.append(" or trunc(sit.finalVigencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')) ");
        sql.append(" and sit.contratoFP_id = :contrato ");
        Query q = em.createNativeQuery(sql.toString(), SituacaoContratoFP.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("contrato", contratoFP.getId());
        q.setParameter("situacoes", situacoes);
        return q.getResultList();
    }

    private SituacaoContratoFP buscarSituacaoAnterior(Date dataInicial, ContratoFP contratoFP) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select sit.* from situacaocontratofp sit ");
        sql.append(" inner join situacaofuncional sitfun on sit.situacaofuncional_id = sitfun.id ");
        sql.append(" where trunc(sit.inicioVigencia) = (select max(trunc(sitCont.iniciovigencia)) from situacaocontratoFp sitCont ");
        sql.append("        where trunc(sitCont.iniciovigencia) <= to_date(:dataInicial, 'dd/mm/yyyy') and sitCont.contratoFp_id = sit.contratoFp_id)");
        sql.append(" and sit.contratoFP_id = :contrato ");
        Query q = em.createNativeQuery(sql.toString(), SituacaoContratoFP.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("contrato", contratoFP.getId());
        q.setMaxResults(1);
        try {
            return (SituacaoContratoFP) q.getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    private SituacaoContratoFP buscarSituacaoPosterior(Date dataFinal, ContratoFP contratoFP) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select sit.* from situacaocontratofp sit ");
        sql.append(" inner join situacaofuncional sitfun on sit.situacaofuncional_id = sitfun.id ");
        sql.append(" where trunc(sit.inicioVigencia) = (select min(trunc(sitCont.iniciovigencia)) from situacaocontratoFp sitCont ");
        sql.append("        where trunc(sitCont.finalvigencia) >= to_date(:dataFinal, 'dd/mm/yyyy') and sitCont.contratoFp_id = sit.contratoFp_id)");
        sql.append(" and sit.contratoFP_id = :contrato ");
        Query q = em.createNativeQuery(sql.toString(), SituacaoContratoFP.class);
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("contrato", contratoFP.getId());
        q.setMaxResults(1);
        try {
            return (SituacaoContratoFP) q.getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    private Boolean validaSaldo(ConcessaoFeriasLicenca concessao) {
        if (concessao.getPeriodoAquisitivoFL().getSaldo() < 0) {
            return false;
        } else {
            return true;
        }
    }

    public ConcessaoFeriasLicenca buscarConcessaoVigentePorTipo(ContratoFP contratoFP, TipoPeriodoAquisitivo tipo) {
        Query q = em.createQuery("from ConcessaoFeriasLicenca af where af.periodoAquisitivoFL.contratoFP = :contratoFP " +
            " and af.periodoAquisitivoFL.baseCargo.basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipo and af.dataCadastroFinalVigencia is null");
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("tipo", tipo);
        q.setMaxResults(1);
        try {
            return (ConcessaoFeriasLicenca) q.getSingleResult();
        } catch (NoResultException nr) {
            return null;
        }
    }

    public Integer recuperaDiasDeAfastamento(ContratoFP contrato, PeriodoAquisitivoFL periodo) {
        Integer totalDiasDeFaltas = 0;
        Query q = em.createQuery("from Afastamento a where a.contratoFP = :vinculo and"
            + " a.tipoAfastamento.classeAfastamento = :classe and (a.inicio between :dataInicio and :dataFim or a.termino between :dataInicio and :dataFim)");
        q.setParameter("vinculo", contrato);
        q.setParameter("classe", ClasseAfastamento.FALTA_INJUSTIFICADA);
        q.setParameter("dataInicio", periodo.getInicioVigencia());
        q.setParameter("dataFim", periodo.getFinalVigencia());
        for (Afastamento a : (List<Afastamento>) q.getResultList()) {
            Date dataMaiorInicio = DataUtil.retornaMaiorData(periodo.getInicioVigencia(), a.getInicio());
            Date dataMenorFinal = DataUtil.retornaMenorData(periodo.getFinalVigencia(), a.getTermino());

            Calendar cInicio = Calendar.getInstance();
            Calendar cFim = Calendar.getInstance();
            cInicio.setTime(dataMaiorInicio);
            cFim.setTime(dataMenorFinal);

            totalDiasDeFaltas += (cFim.get(Calendar.DAY_OF_MONTH) - cInicio.get(Calendar.DAY_OF_MONTH)) + 1;
        }
        logger.debug(" Total de Dias de Faltas:{0}", totalDiasDeFaltas);
        return totalDiasDeFaltas;
    }

    public void removerConcessaoLicencaPremio(ConcessaoFeriasLicenca entity) {
        Integer soma = entity.getDias();
        soma += entity.getPeriodoAquisitivoFL().getSaldo();

        if (soma.compareTo(0) > 0 && soma.compareTo(entity.getPeriodoAquisitivoFL().getSaldoDeDireito()) < 0) {
            entity.getPeriodoAquisitivoFL().setStatus(StatusPeriodoAquisitivo.PARCIAL);
        } else {
            entity.getPeriodoAquisitivoFL().setStatus(StatusPeriodoAquisitivo.NAO_CONCEDIDO);
        }
        entity.getPeriodoAquisitivoFL().setSaldo(soma);
        getEntityManager().merge(entity.getPeriodoAquisitivoFL());
        //Reverte 2 situacao funcionais, pq ao salvar um cencessão de férias ou licença premio o sistema adiciona 2 situações funcionais(uma em férias/licença e outra em exercicio)
        situacaoContratoFPFacade.reverterSituacaoFuncional(entity.getContratoFP());
        situacaoContratoFPFacade.reverterSituacaoFuncional(entity.getContratoFP());

        super.remover(entity);
    }

    public Integer recuperaTotalFaltasInjustificadasCalculadas(ConcessaoFeriasLicenca concessao, PeriodoAquisitivoFL periodoAquisitivoFL) {
        String hql = " select sum(coalesce(concessao.totalFaltasInjustificadas,0)) from ConcessaoFeriasLicenca concessao "
            + " inner join concessao.periodoAquisitivoFL periodo "
            + " where periodo = :parametroPeriodo ";

        if (concessao.getId() != null) {
            hql += " and concessao <> :parametroConcessao ";
        }

        Query q = em.createQuery(hql);
        q.setParameter("parametroPeriodo", periodoAquisitivoFL);

        if (concessao.getId() != null) {
            q.setParameter("parametroConcessao", concessao);
        }

        q.setMaxResults(1);

        if (q.getSingleResult() == null) {
            return 0;
        }

        try {
            return (Integer) q.getSingleResult();
        } catch (Exception e) {
            return ((Long) q.getSingleResult()).intValue();
        }
    }

    public List<MatriculaFP> listaFiltrandoX(String s, TipoPeriodoAquisitivo tipo, int inicio, int max) {
        String hql = " select concessao from ConcessaoFeriasLicenca concessao "
            + " inner join concessao.periodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " inner join periodo.baseCargo baseCargo "
            + " inner join baseCargo.basePeriodoAquisitivo basePeriodo "
            + " where basePeriodo.tipoPeriodoAquisitivo = :tipo "
            + " and ((lower(contrato.numero) like :filtro)"
            + " or (lower(matricula.matricula) like :filtro) "
            + " or (lower(pf.nome) like :filtro) "
            + " OR (('0'|| cast(extract (day from concessao.dataInicial) as string) "
            + " || replace('/'||'0'|| cast(extract (month from concessao.dataInicial) as string),'00','0') "
            + " ||'/'|| cast(extract (year from concessao.dataInicial) as string)) like :filtro) "
            + " OR (('0'|| cast(extract (day from concessao.dataFinal) as string) "
            + " || replace('/'||'0'|| cast(extract (month from concessao.dataFinal) as string),'00','0') "
            + " ||'/'|| cast(extract (year from concessao.dataFinal) as string)) like :filtro) "
            + " OR (('0'|| cast(extract (day from concessao.dataComunicacao) as string) "
            + " || replace('/'||'0'|| cast(extract (month from concessao.dataComunicacao) as string),'00','0') "
            + " ||'/'|| cast(extract (year from concessao.dataComunicacao) as string)) like :filtro)) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("tipo", tipo);
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<ConcessaoFeriasLicenca> listaFiltrandoServidorPorCompetencia(ContratoFP contratoFP, Date inicio, Date fim) {
        String sql = " select c.* from ConcessaoFeriasLicenca c "
            + " inner join PeriodoAquisitivoFL periodo on c.PERIODOAQUISITIVOFL_ID = periodo.id " +
            " WHERE (:inicio BETWEEN trunc(c.datainicial) AND trunc(c.datafinal) " +
            " or (:fim BETWEEN trunc(c.datainicial) AND trunc(c.datafinal)) " +
            " or (trunc(c.datainicial) between :inicio and :fim) " +
            " or (trunc(c.datafinal) between :inicio and :fim))" +
            " and periodo.CONTRATOFP_ID          = :contrato  ";
        Query q = em.createNativeQuery(sql, ConcessaoFeriasLicenca.class);
        q.setParameter("contrato", contratoFP.getId());
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public List<ConcessaoFeriasLicenca> recuperarConcessaoFeriasLincensaByServidorAndPeriodo(ContratoFP contratoFP, Date inicio, Date fim) {
        String sql = " SELECT cfl.* " +
            " FROM CONCESSAOFERIASLICENCA cfl " +
            " INNER JOIN PERIODOAQUISITIVOFL periodo ON periodo.ID = cfl.PERIODOAQUISITIVOFL_ID " +
            " INNER JOIN contratofp contrato ON contrato.id    = periodo.CONTRATOFP_ID " +
            " WHERE contrato.id = :contratoFP " +
            " AND EXTRACT(YEAR FROM CFL.DATAINICIAL) = EXTRACT(YEAR FROM :dataInicio) " +
            " AND EXTRACT(YEAR FROM CFL.DATAFINAL) = EXTRACT(YEAR FROM :dataInicio) " +
            " AND EXTRACT(YEAR FROM CFL.DATAINICIAL) = EXTRACT(YEAR FROM :dataFim) " +
            " AND EXTRACT(YEAR FROM CFL.DATAFINAL) = EXTRACT(YEAR FROM :dataFim) " +
            " ORDER BY cfl.DATAINICIAL DESC ";
        Query q = em.createNativeQuery(sql, ConcessaoFeriasLicenca.class);
        q.setParameter("contratoFP", contratoFP.getId());
        q.setParameter("dataInicio", inicio);
        q.setParameter("dataFim", fim);
        try {
            return q.getResultList();
        } catch (NoResultException nr) {
            return null;
        }
    }

    public List<ConcessaoFeriasLicenca> findConcessaoFeriasLincencaByServidor(Long idVinculoFP, TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        Query q = em.createQuery("from ConcessaoFeriasLicenca af where af.periodoAquisitivoFL.contratoFP.id = :contratoFP"
            + " and af.periodoAquisitivoFL.baseCargo.basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipo order by af.dataInicial desc");
        q.setParameter("contratoFP", idVinculoFP);
        q.setParameter("tipo", tipoPeriodoAquisitivo);
        return q.getResultList();
    }

    @Override
    public ConcessaoFeriasLicenca recuperar(Object id) {
        ConcessaoFeriasLicenca c = super.recuperar(id);
        if (c.getArquivo() != null) {
            c.getArquivo().getPartes().size();
        }
        return c;
    }

    public boolean isEstaEmFeriasByMesEAno(VinculoFP vinculoFP, Integer mes, Integer ano) {
        Query q = em.createQuery(" select concessao from ConcessaoFeriasLicenca concessao "
            + " inner join concessao.periodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contratofp "
            + " where contratofp.id = :parametro and "
            + " extract(month from concessao.dataInicial) = :mes and extract(year from concessao.dataInicial) = :ano " +
            "");
        DateTime data = new DateTime();
        data = data.withYear(ano);
        data = data.withMonthOfYear(mes);
        q.setParameter("parametro", vinculoFP.getId());
        q.setParameter("mes", data.getMonthOfYear());
        q.setParameter("ano", data.getYear());
        return !q.getResultList().isEmpty();
    }

    public ConcessaoFeriasLicenca recuperaConcessaoFeriasLicencaDaProgramacao(ProgramacaoLicencaPremio programacaoLicencaPremio) {
        Query q = em.createQuery("select concessao from ConcessaoFeriasLicenca concessao where concessao.programacaoLicencaPremio  = :programacao");

        q.setParameter("programacao", programacaoLicencaPremio);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConcessaoFeriasLicenca) q.getResultList().get(0);
        }
        return null;
    }

    public boolean existeConcessaoFeriasLicencaPorPeriodoAquisitivoFL(PeriodoAquisitivoFL periodo) {
        try {
            return buscarConcessaoFeriasLicencaPorPeriodoAquisitivoFL(periodo) != null;
        } catch (ExcecaoNegocioGenerica eng) {
            return false;
        }
    }

    public ConcessaoFeriasLicenca buscarConcessaoFeriasLicencaPorPeriodoAquisitivoFL(PeriodoAquisitivoFL periodo) throws ExcecaoNegocioGenerica {
        try {
            return (ConcessaoFeriasLicenca) em.createQuery("select concessao from ConcessaoFeriasLicenca concessao where concessao.periodoAquisitivoFL = :pe").setParameter("pe", periodo).getSingleResult();
        } catch (NoResultException nre) {
            throw new ExcecaoNegocioGenerica("Nenhuma concessão de férias encontrada para o período: " + periodo.toString());
        }
    }

    public List<ConcessaoFeriasLicenca> buscarConcessoesFeriasLicencaPorPeriodoAquisitivoFL(PeriodoAquisitivoFL periodo) {
        Query q = em.createQuery("from ConcessaoFeriasLicenca af where af.periodoAquisitivoFL = :periodo"
            + " order by af.dataInicial desc");
        q.setParameter("periodo", periodo);
        List<ConcessaoFeriasLicenca> concessoes = q.getResultList();
        if (concessoes != null && !concessoes.isEmpty()) {
            return concessoes;
        }
        return Lists.newArrayList();
    }

    public Integer buscarQuantidadeDeFeriasPorData(Date inicio) {
        Integer total = 0;
        String hql = "select count(concessao) from ConcessaoFeriasLicenca concessao where concessao.dataCadastro = :data ";
        Query q = em.createQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        Long bg = (Long) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    private void excluirConcessaoDeFerias(ConcessaoFeriasLicenca concessaoParaExcluir) throws Exception {
        int diasConcedidos = 0;
        VinculoFP vinculo = concessaoParaExcluir.getPeriodoAquisitivoFL().getContratoFP();
        for (ConcessaoFeriasLicenca concessaoSalva : buscarConcessoesFeriasLicencaPorPeriodoAquisitivoFL(concessaoParaExcluir.getPeriodoAquisitivoFL())) {
            if (!concessaoParaExcluir.getId().equals(concessaoSalva.getId())) {
                diasConcedidos += concessaoSalva.getDias();
            }
        }
        int diasParaAtualizar = concessaoParaExcluir.getPeriodoAquisitivoFL().getSaldoDeDireito() - diasConcedidos;

        if (diasParaAtualizar == concessaoParaExcluir.getPeriodoAquisitivoFL().getSaldoDeDireito() ||
            diasParaAtualizar > concessaoParaExcluir.getPeriodoAquisitivoFL().getSaldoDeDireito()) {
            concessaoParaExcluir.getPeriodoAquisitivoFL().setStatus(StatusPeriodoAquisitivo.NAO_CONCEDIDO);
            concessaoParaExcluir.getPeriodoAquisitivoFL().setSaldo(concessaoParaExcluir.getPeriodoAquisitivoFL().getSaldoDeDireito());
        } else if (diasParaAtualizar <= 0) {
            concessaoParaExcluir.getPeriodoAquisitivoFL().setStatus(StatusPeriodoAquisitivo.CONCEDIDO);
            concessaoParaExcluir.getPeriodoAquisitivoFL().setSaldo(0);
        } else {
            concessaoParaExcluir.getPeriodoAquisitivoFL().setStatus(StatusPeriodoAquisitivo.PARCIAL);
            concessaoParaExcluir.getPeriodoAquisitivoFL().setSaldo(diasParaAtualizar);
        }
        getPeriodoAquisitivoFLFacade().salvar(concessaoParaExcluir.getPeriodoAquisitivoFL());
        concessaoParaExcluir = em.find(ConcessaoFeriasLicenca.class, concessaoParaExcluir.getId());

        em.merge(concessaoParaExcluir.getPeriodoAquisitivoFL());
        em.remove(concessaoParaExcluir);
        reprocessarSituacoesContrato(vinculo);

    }

    private void reprocessarSituacoesContrato(VinculoFP vinculo){
        SituacaoFuncional situacaoExonerado = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(SITUACAO_EXONERADO);
        SituacaoFuncional situacaoCedidoADesposicao = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(SITUACAO_CEDIDO_A_DISPOSICAO);
        SituacaoFuncional situacaoAfastado = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(SITUACAO_AFASTADO);
        SituacaoFuncional situacaoFerias = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(SITUACAO_FERIAS);
        SituacaoFuncional situacaoExercicio = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(SITUACAO_EXERCICIO);
        SituacaoFuncional situacaoAposentado = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(SITUACAO_APOSENTADO);
        reprocessamentoSituacaoFuncionalFacade.reprocessarSituacoesFuncionais(vinculo, situacaoExonerado, situacaoCedidoADesposicao, situacaoAfastado, situacaoFerias, situacaoExercicio, situacaoAposentado);
    }

    public void realizarTratativasParaExclusaoDeConcessao(ConcessaoFeriasLicenca concessaoFeriasLicenca) {
        if (!concessaoFeriasLicenca.getLicencaPremioIndenizada()) {
            int dia = 01;
            int mes = DataUtil.getMes(concessaoFeriasLicenca.getDataInicial());
            int ano = DataUtil.getAno(concessaoFeriasLicenca.getDataInicial());
            Date dataValidacao = DataUtil.montaData(dia, mes, ano).getTime();

            try {
                UsuarioSistema usuario = usuarioSistemaFacade.recuperarAutorizacaoUsuarioRH(sistemaFacade.getUsuarioCorrente().getId());
                Boolean validarFolhaDePagamento = true;
                for (AutorizacaoUsuarioRH autorizacaoUsuarioRH : usuario.getAutorizacaoUsuarioRH()) {
                    if (TipoAutorizacaoRH.PERMITIR_EXCLUIR_CONCESSAO_FOLHA_PROCESSADA.equals(autorizacaoUsuarioRH.getTipoAutorizacaoRH())) {
                        validarFolhaDePagamento = false;
                        break;
                    }

                }

                if (validarFolhaDePagamento) {
                    if (TipoPeriodoAquisitivo.FERIAS.equals(concessaoFeriasLicenca.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo())) {
                        if (!contratoFPFacade.podeExcluir(concessaoFeriasLicenca.getContratoFP(), dataValidacao)) {
                            ValidacaoException ex = new ValidacaoException();
                            ex.adicionarMensagemDeOperacaoNaoRealizada("Não é permitido excluir esta concessão, já foi processada folha de pagamento após a realização desta concessão.");
                            throw ex;
                        }
                    }
                }

            } catch (NullPointerException np) {
                logger.error("problmeas para validar se o periodo possui folha processada " + np.getMessage());
            }
        }
        try {
            excluirConcessaoDeFerias(concessaoFeriasLicenca);
            ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente();
            if (configuracaoRH.getEnvioAutomaticoDadosPonto() && TipoPeriodoAquisitivo.FERIAS.equals(concessaoFeriasLicenca.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo())) {
                excluirFeriasRBPonto(concessaoFeriasLicenca);
            }
        } catch (Exception e) {
            logger.error("Erro", e);
            ValidacaoException ex = new ValidacaoException();
            ex.adicionarMensagemDeOperacaoNaoRealizada("Não foi possível realizar a ação solicitada. Verifique as informações e tente novamente.");
            ex.adicionarMensagemDeOperacaoNaoRealizada("Se o problema persistir entre em contato com o suporte. erro: ");
            throw ex;
        }
    }

    private void excluirFeriasRBPonto(ConcessaoFeriasLicenca concessaoFeriasLicenca) {
        ExcluirFeriasDTO excluirFeriasDTO = new ExcluirFeriasDTO(concessaoFeriasLicenca.getId().toString());
        ConfiguracaoWebServiceRH configuracaoWebServiceRH = configuracaoWSRHFacade.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.PONTO);
        atualizacaoDadosRBPontoFacade.excluirFerias(excluirFeriasDTO, configuracaoWebServiceRH);
    }

    public PeriodoAquisitivoFLFacade getPeriodoAquisitivoFLFacade() {
        return periodoAquisitivoFLFacade;
    }

    public ConcessaoFeriasLicenca buscarConcessaoPorContratoMesAndAno(Long idContrato, Integer mes, Integer ano) {
        String sql = " select * from concessaoferiaslicenca concessao " +
            "inner join periodoaquisitivofl periodo on concessao.periodoaquisitivofl_id = periodo.id " +
            "where periodo.contratofp_id = :idContrato " +
            "and concessao.mes = :mes and concessao.ano = :ano ";

        Query q = em.createNativeQuery(sql, ConcessaoFeriasLicenca.class);
        q.setParameter("idContrato", idContrato);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);

        List<ConcessaoFeriasLicenca> concessoes = q.getResultList();
        return (concessoes != null && !concessoes.isEmpty()) ? concessoes.get(0) : null;
    }
}
