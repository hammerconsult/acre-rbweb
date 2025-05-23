package br.com.webpublico.negocios.rh.cadastrofuncional;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.SituacaoContratoFPBkp;
import br.com.webpublico.entidades.rh.cadastrofuncional.ReprocessamentoSituacaoFuncional;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Stateless
public class ReprocessamentoSituacaoFuncionalFacade extends AbstractFacade<ReprocessamentoSituacaoFuncional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private SituacaoContratoFPFacade situacaoContratoFPFacade;
    @EJB
    private ReprocessamentoSituacaoFuncionalFacade reprocessamentoSituacaoFuncionalFacade;
    @EJB
    private ReintegracaoFacade reintegracaoFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;

    public ReprocessamentoSituacaoFuncionalFacade() {
        super(ReprocessamentoSituacaoFuncional.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<VinculoFP> getVinculoPPPorLotacao(HierarquiaOrganizacional hierarquia, Boolean somenteAtivos) {
        String sql = "select distinct obj from VinculoFP obj, ContratoFP c, HierarquiaOrganizacional ho " +
            "             inner join obj.unidadeOrganizacional unidade " +
            "             inner join c.lotacaoFuncionals lotacao  " +
            "             where  lotacao.unidadeOrganizacional = ho.subordinada" +
            "             and ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA' " +
            "             and c.id = obj.id and ho.codigo like :hierarquia ";
        if (somenteAtivos) {
            sql += " and sysdate between obj.inicioVigencia and coalesce(obj.finalVigencia, sysdate)";
        }

        Query q = em.createQuery(sql);
        q.setParameter("hierarquia", hierarquia.getCodigoSemZerosFinais() + "%");
        List resultList = q.getResultList();
        List<VinculoFP> vinculoFP = Lists.newArrayList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    @Override
    public ReprocessamentoSituacaoFuncional recuperar(Object id) {
        ReprocessamentoSituacaoFuncional reprocessamento = super.recuperar(id);
        Hibernate.initialize(reprocessamento.getReprocessamentoVinculo());
        return reprocessamento;
    }

    public List<SituacaoContratoFPBkp> getSituacoesAnteriores(VinculoFP vinculo) {
        String sql = "select * from SituacaoContratoFPBkp where CONTRATOFP_ID = :idVinculo";
        Query q = em.createNativeQuery(sql, SituacaoContratoFPBkp.class);
        q.setParameter("idVinculo", vinculo.getId());
        return q.getResultList();
    }

    public boolean hasSituacaoAnteriorGerada(VinculoFP vinculo) {
        String sql = "select * from SituacaoContratoFPBkp where CONTRATOFP_ID = :idVinculo";
        Query q = em.createNativeQuery(sql, SituacaoContratoFPBkp.class);
        q.setParameter("idVinculo", vinculo.getId());
        return !q.getResultList().isEmpty();
    }

    public void reprocessarSituacoesFuncionais(VinculoFP vinculo, SituacaoFuncional situacaoExonerado, SituacaoFuncional situacaoCedidoADesposicao, SituacaoFuncional situacaoAfastado, SituacaoFuncional situacaoFerias, SituacaoFuncional situacaoExercicio, SituacaoFuncional situacaoAposentado) {
        List<ExoneracaoRescisao> exoneracaoRescisa = exoneracaoRescisaoFacade.recuperarExoneracaoRescisaoPorVinculoFP(vinculo.getId());
        List<CedenciaContratoFP> cedenciaContratoFPS = cedenciaContratoFPFacade.buscarCedenciaContratoFP(vinculo.getId());
        List<Afastamento> afastamentos = afastamentoFacade.recuperarTodosAfastamentos(vinculo.getId());
        List<ConcessaoFeriasLicenca> ferias = concessaoFeriasLicencaFacade.findConcessaoFeriasLincencaByServidor(vinculo.getId(), TipoPeriodoAquisitivo.FERIAS);
        List<ConcessaoFeriasLicenca> licencas = concessaoFeriasLicencaFacade.findConcessaoFeriasLincencaByServidor(vinculo.getId(), TipoPeriodoAquisitivo.LICENCA);
        Aposentadoria aposentadoria = null;
        if (vinculo instanceof ContratoFP) {
            aposentadoria = aposentadoriaFacade.recuperaAposentadoriaPorContratoFP((ContratoFP) vinculo);
        }
        criarSituacaoFuncional(vinculo.getId(), exoneracaoRescisa, cedenciaContratoFPS, afastamentos, ferias, licencas, aposentadoria, situacaoExonerado, situacaoCedidoADesposicao, situacaoAfastado, situacaoFerias, situacaoExercicio, situacaoAposentado);
    }

    private void criarSituacaoFuncional(Long
                                            idServidor, List<ExoneracaoRescisao> exoneracaoRescisa, List<CedenciaContratoFP> cedenciaContratoFPS, List<Afastamento> afastamentos,
                                        List<ConcessaoFeriasLicenca> ferias, List<ConcessaoFeriasLicenca> licencas, Aposentadoria aposentadoria, SituacaoFuncional situacaoExonerado, SituacaoFuncional situacaoCedidoADesposicao, SituacaoFuncional situacaoAfastado, SituacaoFuncional situacaoFerias, SituacaoFuncional situacaoExercicio, SituacaoFuncional situacaoAposentado) {
        List<SituacaoContratoFP> situacoes = Lists.newArrayList();
        ContratoFP contrato = contratoFPFacade.recuperarContratoComSituacaoFuncional(idServidor);
        if (exoneracaoRescisa != null) {
            for (ExoneracaoRescisao exoneracaoRescisao : exoneracaoRescisa) {
                Date fim = null;
                if (aposentadoria != null) {
                    if (DataUtil.adicionaDias(exoneracaoRescisao.getDataRescisao(), 1).compareTo(aposentadoria.getInicioVigencia()) == 0) {
                        continue;
                    }
                    if (aposentadoria.getInicioVigencia().after(DataUtil.adicionaDias(exoneracaoRescisao.getDataRescisao(), 1))) {
                        fim = aposentadoria.getInicioVigencia();
                    }
                }
                Reintegracao reintegracao = reintegracaoFacade.buscarReintegracaoParaContrato(idServidor, exoneracaoRescisao.getId());
                if (reintegracao != null) {
                    if (reintegracao.getInicioEfeitosFinanceiros() != null && reintegracao.getInicioEfeitosFinanceiros().after(exoneracaoRescisao.getDataRescisao())) {
                        fim = reintegracao.getInicioEfeitosFinanceiros();
                    } else if (reintegracao.getDataReintegracao().after(exoneracaoRescisao.getDataRescisao())) {
                        fim = reintegracao.getDataReintegracao();
                    }
                }
                situacoes.add(criarSituacaoFuncional(situacaoExonerado, contrato, DataUtil.adicionaDias(exoneracaoRescisao.getDataRescisao(), 1), fim != null ? DataUtil.removerDias(fim, 1) : null));
            }
        }

        if (ferias != null) {
            for (ConcessaoFeriasLicenca concessaoFeriasLicenca : ferias) {
                situacoes.add(criarSituacaoFuncional(situacaoFerias, contrato, concessaoFeriasLicenca.getInicioVigencia(), concessaoFeriasLicenca.getFimVigencia()));
            }
        }

        if (licencas != null) {
            for (ConcessaoFeriasLicenca concessaoFeriasLicenca : licencas) {
                situacoes.add(criarSituacaoFuncional(situacaoAfastado, contrato, concessaoFeriasLicenca.getInicioVigencia(), concessaoFeriasLicenca.getFimVigencia()));
            }
        }

        if (afastamentos != null) {
            for (Afastamento afastamento : afastamentos) {
                if ((ferias != null && !ferias.isEmpty()) || (licencas != null && !licencas.isEmpty())) {
                    boolean podeAdicionarAfastamento = true;
                    if (ferias != null) {
                        Collections.sort(ferias);
                        for (ConcessaoFeriasLicenca concessao : ferias) {
                            if (afastamento.getInicio().before(concessao.getInicioVigencia()) &&
                                (afastamento.getTermino().after(concessao.getInicioVigencia()) && afastamento.getTermino().before(concessao.getFimVigencia()))) {
                                afastamento.setTermino(DataUtil.removerDias(concessao.getInicioVigencia(), 1));
                            }
                            if ((afastamento.getInicio().after(concessao.getInicioVigencia())) && afastamento.getInicio().before(concessao.getFimVigencia())
                                && afastamento.getTermino().after(concessao.getFimVigencia())) {
                                afastamento.setInicio(DataUtil.adicionaDias(concessao.getFimVigencia(), 1));
                            }
                            if (afastamento.getInicio().before(concessao.getInicioVigencia()) && afastamento.getTermino().after(concessao.getFimVigencia())) {
                                situacoes.add(criarSituacaoFuncional(situacaoAfastado, contrato, afastamento.getInicio(), DataUtil.removerDias(concessao.getInicioVigencia(), 1)));
                                situacoes.add(criarSituacaoFuncional(situacaoAfastado, contrato, DataUtil.adicionaDias(concessao.getDataFinal(), 1), afastamento.getTermino()));
                                podeAdicionarAfastamento = false;
                                break;
                            }
                            if (afastamento.getInicio().compareTo(concessao.getInicioVigencia()) <= 0 && afastamento.getTermino().after(concessao.getFimVigencia())) {
                                situacoes.add(criarSituacaoFuncional(situacaoAfastado, contrato, DataUtil.adicionaDias(concessao.getDataFinal(), 1), afastamento.getTermino()));
                                podeAdicionarAfastamento = false;
                                break;
                            }
                            if (afastamento.getInicio().after(concessao.getInicioVigencia()) && afastamento.getTermino().before(concessao.getFimVigencia())) {
                                podeAdicionarAfastamento = false;
                            }
                        }
                    }
                    if (licencas != null) {
                        Collections.sort(licencas);
                        for (ConcessaoFeriasLicenca concessao : licencas) {
                            if (afastamento.getInicio().before(concessao.getInicioVigencia()) &&
                                (afastamento.getTermino().after(concessao.getInicioVigencia()) && afastamento.getTermino().before(concessao.getFimVigencia()))) {
                                afastamento.setTermino(DataUtil.removerDias(concessao.getInicioVigencia(), 1));
                            }
                            if ((afastamento.getInicio().after(concessao.getInicioVigencia())) && afastamento.getInicio().before(concessao.getFimVigencia())
                                && afastamento.getTermino().after(concessao.getFimVigencia())) {
                                afastamento.setInicio(DataUtil.adicionaDias(concessao.getFimVigencia(), 1));
                            }
                            if (afastamento.getInicio().before(concessao.getInicioVigencia()) && afastamento.getTermino().after(concessao.getFimVigencia())) {
                                situacoes.add(criarSituacaoFuncional(situacaoAfastado, contrato, afastamento.getInicio(), DataUtil.removerDias(concessao.getInicioVigencia(), 1)));
                                situacoes.add(criarSituacaoFuncional(situacaoAfastado, contrato, DataUtil.adicionaDias(concessao.getDataFinal(), 1), afastamento.getTermino()));
                                podeAdicionarAfastamento = false;
                                break;
                            }
                            if (afastamento.getInicio().compareTo(concessao.getInicioVigencia()) <= 0 && afastamento.getTermino().after(concessao.getFimVigencia())) {
                                situacoes.add(criarSituacaoFuncional(situacaoAfastado, contrato, DataUtil.adicionaDias(concessao.getDataFinal(), 1), afastamento.getTermino()));
                                podeAdicionarAfastamento = false;
                                break;
                            }
                            if (afastamento.getInicio().after(concessao.getInicioVigencia()) && afastamento.getTermino().before(concessao.getFimVigencia())) {
                                podeAdicionarAfastamento = false;
                            }
                        }
                    }

                    if (podeAdicionarAfastamento) {
                        situacoes.add(criarSituacaoFuncional(situacaoAfastado, contrato, afastamento.getInicio(), afastamento.getTermino()));
                    }
                } else {
                    situacoes.add(criarSituacaoFuncional(situacaoAfastado, contrato, afastamento.getInicio(), afastamento.getTermino()));
                }
            }
            checarAfastamentoConflitanteConcessaoFeriasLicensa(situacoes);
        }

        if (cedenciaContratoFPS != null) {
            for (CedenciaContratoFP cedencia : cedenciaContratoFPS) {
                if (afastamentos != null) {
                    boolean podeCriarSituacaoCedencia = true;
                    for (Afastamento afastamento : afastamentos) {
                        if (DataUtil.dataSemHorario(afastamento.getInicio()).compareTo(DataUtil.dataSemHorario(cedencia.getDataCessao())) != 0) {
                            podeCriarSituacaoCedencia = false;
                        }
                    }
                    if (podeCriarSituacaoCedencia) {
                        situacoes.add(criarSituacaoFuncional(situacaoCedidoADesposicao, contrato, cedencia.getDataCessao(), cedencia.getDataRetorno()));
                    }
                } else {
                    situacoes.add(criarSituacaoFuncional(situacaoCedidoADesposicao, contrato, cedencia.getDataCessao(), cedencia.getDataRetorno()));
                }
            }
        }

        if (aposentadoria != null) {
            situacoes.add(criarSituacaoFuncional(situacaoAposentado, contrato, aposentadoria.getInicioVigencia(), aposentadoria.getFinalVigencia()));
        }

        montarSituacoesOriginais(contrato);
        contrato.getSituacaoFuncionals().clear();
        if (!situacoes.isEmpty()) {
            contrato.getSituacaoFuncionals().addAll(situacoes);
            montarSituacoesExercicio(contrato, situacaoExercicio);
        } else {
            situacoes.add(criarSituacaoFuncional(situacaoExercicio, contrato, null, contrato.getFinalVigencia()));
            contrato.getSituacaoFuncionals().addAll(situacoes);
        }
        contratoFPFacade.salvaRetornando(contrato);
    }

    private void checarAfastamentoConflitanteConcessaoFeriasLicensa(List<SituacaoContratoFP> situacoes) {
        List<SituacaoContratoFP> afastamentos = Lists.newArrayList();
        List<SituacaoContratoFP> ferias = Lists.newArrayList();
        organizarSituacoesFuncionais(situacoes, afastamentos, ferias);
        for (SituacaoContratoFP feriasLicensa : ferias) {
            for (SituacaoContratoFP afastamento : afastamentos) {
                if (afastamento.getInicioVigencia().before(feriasLicensa.getInicioVigencia()) &&
                    afastamento.getFinalVigencia().after(feriasLicensa.getFinalVigencia())) {
                    afastamento.setFinalVigencia(DataUtil.removerDias(feriasLicensa.getInicioVigencia(), 1));
                }
            }
        }
    }

    private void organizarSituacoesFuncionais(List<SituacaoContratoFP> situacoes, List<SituacaoContratoFP> afastamentos, List<SituacaoContratoFP> concessoes) {
        for (SituacaoContratoFP situacao : situacoes) {
            if (SituacaoFuncional.AFASTADO_LICENCIADO.equals(situacao.getSituacaoFuncional().getCodigo())) {
                afastamentos.add(situacao);
            }
            if (SituacaoFuncional.EM_FERIAS.equals(situacao.getSituacaoFuncional().getCodigo())) {
                concessoes.add(situacao);
            }
        }
    }

    private void montarSituacoesOriginais(ContratoFP contrato) {
        if (!reprocessamentoSituacaoFuncionalFacade.hasSituacaoAnteriorGerada(contrato)) {
            contrato.getSituacaoFuncionalsBkp().clear();
            for (SituacaoContratoFP situacaoFuncional : contrato.getSituacaoFuncionals()) {
                SituacaoContratoFPBkp bkp = new SituacaoContratoFPBkp();
                bkp.setContratoFP(situacaoFuncional.getContratoFP());
                bkp.setInicioVigencia(situacaoFuncional.getInicioVigencia());
                bkp.setFinalVigencia(situacaoFuncional.getFinalVigencia());
                bkp.setSituacaoFuncional(situacaoFuncional.getSituacaoFuncional());
                bkp.setRotinaResponsavelAlteracao(situacaoFuncional.getRotinaResponsavelAlteracao());
                contrato.getSituacaoFuncionalsBkp().add(bkp);
            }
        }
    }

    private void montarSituacoesExercicio(ContratoFP contrato, SituacaoFuncional situacaoExercicio) {
        List<SituacaoContratoFP> situacoes = contrato.getSituacaoFuncionals();
        Collections.sort(situacoes);
        SituacaoContratoFP situacaoAnterior = null;
        List<SituacaoContratoFP> itemSituacaoExercicio = Lists.newArrayList();
        for (SituacaoContratoFP situacoe : situacoes) {
            if (situacoe.getSituacaoFuncional().getCodigo().equals(SituacaoFuncional.ATIVO_PARA_FOLHA)) {
                continue;
            }

            Date inicioVigenciaAtualMenosUmDia = DataUtil.removerDias(situacoes.get(0).getInicioVigencia(), 1);
            SituacaoContratoFP novaSituacaoExercicio = null;

            if (situacaoAnterior != null) {
                if (situacaoAnterior.getFinalVigencia() != null &&
                    situacaoAnterior.getFinalVigencia().compareTo(inicioVigenciaAtualMenosUmDia) != 0) {
                    Date inicio = DataUtil.adicionaDias(situacaoAnterior.getFinalVigencia(), 1);
                    Date fim = DataUtil.removerDias(situacoe.getInicioVigencia(), 1);

                    if (inicio.compareTo(fim) > 0) {
                        situacaoAnterior = situacoe;
                        continue;
                    }

                    novaSituacaoExercicio = criarSituacaoFuncional(situacaoExercicio, contrato, inicio, fim);
                    itemSituacaoExercicio.add(novaSituacaoExercicio);
                }
            }
            situacaoAnterior = situacoe;
        }
        Collections.sort(situacoes);
        situacoes.add(criarSituacaoFuncional(situacaoExercicio, contrato, null, DataUtil.removerDias(situacoes.get(0).getInicioVigencia(), 1)));
        contrato.getSituacaoFuncionals().addAll(itemSituacaoExercicio);
        SituacaoContratoFP ultimaSituacaoCriada;

        if (!contrato.getSituacaoFuncionals().isEmpty()) {
            Collections.sort(situacoes);
            ultimaSituacaoCriada = contrato.getSituacaoFuncionals().get(contrato.getSituacaoFuncionals().size() - 1);
            if (!SituacaoFuncional.ATIVO_PARA_FOLHA.equals(ultimaSituacaoCriada.getSituacaoFuncional().getCodigo()) && ultimaSituacaoCriada.getFinalVigencia() != null) {
                if (contrato.getFinalVigencia() == null || contrato.getFinalVigencia().after(ultimaSituacaoCriada.getFinalVigencia())) {
                    situacoes.add(criarSituacaoFuncional(situacaoExercicio, contrato, DataUtil.adicionaDias(ultimaSituacaoCriada.getFinalVigencia(), 1),
                        contrato.getFinalVigencia()));
                }
            }
        }
    }

    private SituacaoContratoFP criarSituacaoFuncional(SituacaoFuncional situacao, ContratoFP contrato, Date inicio, Date fim) {
        SituacaoContratoFP situacaoContratoFP = new SituacaoContratoFP();
        if (inicio == null) {
            situacaoContratoFP.setInicioVigencia(contrato.getInicioVigencia());
        } else {
            situacaoContratoFP.setInicioVigencia(inicio);
        }
        situacaoContratoFP.setFinalVigencia(fim);
        situacaoContratoFP.setContratoFP(contrato);
        situacaoContratoFP.setSituacaoFuncional(situacao);
        return situacaoContratoFP;
    }

}



