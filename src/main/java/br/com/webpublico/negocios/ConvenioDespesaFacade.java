/*
 * Codigo gerado automaticamente em Wed Apr 04 13:59:50 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.ConvenioDespesaPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OrigemSolicitacaoEmpenho;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoEmpenho;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ConvenioDespesaFacade extends AbstractFacade<ConvenioDespesa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private PeriodicidadeFacade periodicidadeFacade;
    @EJB
    private UnidadeMedidaFacade unidadeMedidaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private OcorrenciaConvenioDespFacade ocorrenciaConvenioDespFacade;
    @EJB
    private CategoriaDespesaFacade categoriaDespesaFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private IntervenienteFacade intervenienteFacade;
    @EJB
    private TipoExecucaoFacade tipoExecucaoFacade;
    @EJB
    private TipoIntervenienteFacade tipoIntervenienteFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private EntidadeBeneficiariaFacade entidadeBeneficiariaFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private EmendaFacade emendaFacade;
    @EJB
    private VereadorFacade vereadorFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConvenioDespesaFacade() {
        super(ConvenioDespesa.class);
    }


    public String retornaUltimoNumeroConvenioDespesa() {
        String sql = "SELECT conv.numconvenio FROM conveniodespesa conv "
            + "ORDER BY conv.numconvenio DESC";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return "0";
        } else {
            String retorno = (String) q.getSingleResult();
            if (retorno == null) {
                return "0";
            } else {
                return retorno;
            }
        }
    }

    public Long retornaUltimoNumeroMaisUm() {
        Query q = em.createNativeQuery("SELECT coalesce(max(to_number(numconvenio)), 0) + 1 AS codigo FROM ConvenioDespesa");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public ConvenioDespesa salvar(ConvenioDespesa entity, List<AndamentoConvenio> andamentos, List<AndamentoConvenio> andamentosRemovidos) throws ExcecaoNegocioGenerica {
        List<AndamentoConvenioDespesa> removidos = new ArrayList<AndamentoConvenioDespesa>();
        try {

            for (AndamentoConvenio ac : andamentos) {
                if (ac.getId() == null) {
                    em.persist(ac);
                    AndamentoConvenioDespesa acr = new AndamentoConvenioDespesa();
                    acr.setAndamentoConvenio(ac);
                    acr.setConvenioDespesa(entity);
                    entity.getAndamentoConvenioDespesa().add(acr);
                }
            }

            for (AndamentoConvenio ac : andamentosRemovidos) {
                for (AndamentoConvenioDespesa acr : entity.getAndamentoConvenioDespesa()) {
                    if (ac.getId().equals(acr.getAndamentoConvenio().getId())) {
                        removidos.add(acr);
                    }
                }
            }

            if (!removidos.isEmpty()) {
                entity.getAndamentoConvenioDespesa().removeAll(removidos);
            }
            entity = em.merge(entity);
            for (AndamentoConvenio ac : andamentosRemovidos) {
                ac.setInterveniente(null);
                ac.setTipoInterveniente(null);
                ac = em.merge(ac);
                em.remove(ac);
            }
            salvarPortal(entity);
            gerarSolicitacaoEmpenho(entity);
            return entity;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível salva o Convênio de Despesa, entre em contato com o suporte do sistema. Detalhes do erro: " + e.getMessage());
        }
    }

    private void salvarPortal(ConvenioDespesa entity) {
        portalTransparenciaNovoFacade.salvarPortal(new ConvenioDespesaPortal(entity));
    }

    public ConvenioDespesa salvarNovoConvenio(ConvenioDespesa entity, List<AndamentoConvenio> andamentos) throws ExcecaoNegocioGenerica {
        try {
            for (AndamentoConvenio ac : andamentos) {
                em.persist(ac);
                AndamentoConvenioDespesa acr = new AndamentoConvenioDespesa();
                acr.setAndamentoConvenio(ac);
                acr.setConvenioDespesa(entity);
//            em.persist(acr);
                entity.getAndamentoConvenioDespesa().add(acr);
            }
            entity.setNumConvenio(retornaUltimoNumeroMaisUm() + " ");
            em.persist(entity);
            salvarPortal(entity);
            gerarSolicitacaoEmpenho(entity);
            return entity;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível salva o Convênio de Despesa, entre em contato com o suporte do sistema. Detalhes do erro: " + e.getMessage());
        }
    }

    public void gerarSolicitacaoEmpenhoConvenioDespesa(ConvenioDespesa convenio, DespesaExercConvenio despesa) {
        try {
            ConvenioDespSolicEmpenho solicitacao = new ConvenioDespSolicEmpenho();
            String historicoSolicitacao = "Solicitação de Empenho gerada para o Convênio de Despesa: N° " + convenio.getNumConvenio() + " na data: " + DataUtil.getDataFormatada(convenio.getDataVigenciaInicial());
            Boolean reverva = false;
            solicitacao.setSolicitacaoEmpenho(solicitacaoEmpenhoFacade.gerarSolicitacaoEmpenhoSalvando(
                despesa.getValor(),
                despesa.getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                TipoEmpenho.ORDINARIO, null,
                despesa.getFonteDespesaORC(),
                despesa.getDespesaORC(),
                UtilRH.getDataOperacao(),
                despesa.getDespesaORC().getContaDeDespesa(),
                historicoSolicitacao,
                convenio.getEntidadeBeneficiaria().getPessoaJuridica(),
                reverva, null,
                convenio.getClasseCredor(),
                null,
                OrigemSolicitacaoEmpenho.CONVENIO_DESPESA,
                convenio.getNumero()));
            solicitacao.setConvenioDespesa(convenio);
            solicitacao.setDespesaExercConvenio(despesa);
            solicitacao.setSubTipoDespesa(SubTipoDespesa.NAO_APLICAVEL);
            em.persist(solicitacao);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(" Solicitação de empenho não gerada, entre em contato com o suporte do sistema. Detalhes do erro: " + ex.getMessage());
        }
    }

    private void gerarSolicitacaoEmpenho(ConvenioDespesa selecionado) {
        for (DespesaExercConvenio despesa : selecionado.getDespesaExercConvenios()) {
            if (despesa != null && despesa.getGerarSolicitacaoEmpenho()) {
                if (despesa.getId() == null || !hasSolicitacaoEmpenho(despesa)) {
                    gerarSolicitacaoEmpenhoConvenioDespesa(selecionado, despesa);
                }
            }
        }
    }


    public boolean hasSolicitacaoEmpenho(DespesaExercConvenio despesa) {
        String sql = " select * from ConvenioDespSolicEmpenho where DespesaExercConvenio_id = :despesa ";
        Query q = em.createNativeQuery(sql, ConvenioDespSolicEmpenho.class);
        q.setParameter("despesa", despesa.getId());
        return !q.getResultList().isEmpty();
    }

    @Override
    public ConvenioDespesa recuperar(Object id) {
        ConvenioDespesa cd = em.find(ConvenioDespesa.class, id);
        Hibernate.initialize(cd.getAndamentoConvenioDespesa());
        Hibernate.initialize(cd.getConvenioDespIntervenientes());
        Hibernate.initialize(cd.getCronogramaDesembolsos());
        Hibernate.initialize(cd.getDespesaExercConvenios());
        Hibernate.initialize(cd.getPlanoAplicacoes());
        Hibernate.initialize(cd.getTramites());
        Hibernate.initialize(cd.getPrestacaoContas());
        Hibernate.initialize(cd.getAditivosConvenioDespesas());
        Hibernate.initialize(cd.getConvenioDespesaLiberacoes());
        for (DespesaExercConvenio despesaExercConvenio : cd.getDespesaExercConvenios()) {
            Hibernate.initialize(despesaExercConvenio.getSolicitacoes());
        }
        for (PlanoAplicacao pa : cd.getPlanoAplicacoes()) {
            Hibernate.initialize(pa.getListaCategoriaDespesas());
            Hibernate.initialize(pa.getListaElementosDespesa());
        }
        if (cd.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(cd.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return cd;
    }

    public AndamentoConvenio recuperarAndamento(Object id) {
        AndamentoConvenio ac = em.find(AndamentoConvenio.class, id);
        return ac;
    }

    public List<Empenho> listaEmpenhoPorConvenioDespesa(ConvenioDespesa c) {
        String sql = " SELECT EMP.* FROM EMPENHO EMP " +
            " INNER JOIN CONVENIODESPESA C ON EMP.CONVENIODESPESA_ID = C.ID "
            + " WHERE C.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), Empenho.class);
        q.setParameter("param", c.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<EmpenhoEstorno> listaEmpenhoEstornoPorConvenioDespesa(ConvenioDespesa c) {
        String sql = " SELECT EMPEST.* FROM EMPENHO EMP "
            + " INNER JOIN EMPENHOESTORNO EMPEST ON EMPEST.EMPENHO_ID = EMP.ID "
            + " INNER JOIN CONVENIODESPESA C ON EMP.CONVENIODESPESA_ID = C.ID "
            + " WHERE C.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), EmpenhoEstorno.class);
        q.setParameter("param", c.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Liquidacao> listaLiquidacaoPorConvenioDespesa(ConvenioDespesa c) {
        String sql = " SELECT LIQ.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN CONVENIODESPESA C ON EMP.CONVENIODESPESA_ID = C.ID "
            + " WHERE C.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), Liquidacao.class);
        q.setParameter("param", c.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<LiquidacaoEstorno> listaLiquidacaoEstornoPorConvenioDespesa(ConvenioDespesa c) {
        String sql = " SELECT LIQEST.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN LIQUIDACAOESTORNO LIQEST ON LIQEST.LIQUIDACAO_ID = LIQ.ID "
            + " INNER JOIN CONVENIODESPESA C ON EMP.CONVENIODESPESA_ID = C.ID "
            + " WHERE C.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), LiquidacaoEstorno.class);
        q.setParameter("param", c.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Pagamento> listaPagamentoPorConvenioDespesa(ConvenioDespesa c) {
        String sql = " SELECT PAG.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN PAGAMENTO PAG ON PAG.LIQUIDACAO_ID = LIQ.ID "
            + " INNER JOIN CONVENIODESPESA C ON EMP.CONVENIODESPESA_ID = C.ID "
            + " WHERE C.ID = :param "
            + " AND DATAPAGAMENTO IS NOT NULL";
        Query q = getEntityManager().createNativeQuery(sql.toString(), Pagamento.class);
        q.setParameter("param", c.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<PagamentoEstorno> listaPagamentoEstornoPorConvenioDespesa(ConvenioDespesa c) {
        String sql = " SELECT PAGEST.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN PAGAMENTO PAG ON PAG.LIQUIDACAO_ID = LIQ.ID "
            + " INNER JOIN PAGAMENTOESTORNO PAGEST ON PAGEST.PAGAMENTO_ID = PAG.ID "
            + " INNER JOIN CONVENIODESPESA C ON EMP.CONVENIODESPESA_ID = C.ID "
            + " WHERE C.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), PagamentoEstorno.class);
        q.setParameter("param", c.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public PeriodicidadeFacade getPeriodicidadeFacade() {
        return periodicidadeFacade;
    }

    public UnidadeMedidaFacade getUnidadeMedidaFacade() {
        return unidadeMedidaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public OcorrenciaConvenioDespFacade getOcorrenciaConvenioDespFacade() {
        return ocorrenciaConvenioDespFacade;
    }

    public CategoriaDespesaFacade getCategoriaDespesaFacade() {
        return categoriaDespesaFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public IntervenienteFacade getIntervenienteFacade() {
        return intervenienteFacade;
    }

    public void setIntervenienteFacade(IntervenienteFacade intervenienteFacade) {
        this.intervenienteFacade = intervenienteFacade;
    }

    public TipoExecucaoFacade getTipoExecucaoFacade() {
        return tipoExecucaoFacade;
    }

    public void setTipoExecucaoFacade(TipoExecucaoFacade tipoExecucaoFacade) {
        this.tipoExecucaoFacade = tipoExecucaoFacade;
    }

    public TipoIntervenienteFacade getTipoIntervenienteFacade() {
        return tipoIntervenienteFacade;
    }

    public void setTipoIntervenienteFacade(TipoIntervenienteFacade tipoIntervenienteFacade) {
        this.tipoIntervenienteFacade = tipoIntervenienteFacade;
    }

    public HierarquiaOrganizacionalFacadeOLD getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public EntidadeBeneficiariaFacade getEntidadeBeneficiariaFacade() {
        return entidadeBeneficiariaFacade;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public ContaCorrenteBancariaFacade getContaCorrenteBancariaFacade() {
        return contaCorrenteBancariaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public EmendaFacade getEmendaFacade() {
        return emendaFacade;
    }

    public VereadorFacade getVereadorFacade() {
        return vereadorFacade;
    }

    public void adicionaLiberacaoDoConvenio(Empenho empenho) {
        ConvenioDespesaLiberacao cdl = new ConvenioDespesaLiberacao();
        cdl.setConvenioDespesa(empenho.getConvenioDespesa());
        cdl.setDataLiberacaoRecurso(empenho.getDataEmpenho());
        cdl.setValorLiberadoConcedente(empenho.getValor());
        cdl.setValorLiberadoContrapartida(BigDecimal.ZERO);
        em.persist(cdl);
    }

    public List<ConvenioDespesa> recuperarTodosPorExercicioUnidades(List<HierarquiaOrganizacional> hieraquias) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : hieraquias) {
            unidades.add(lista.getSubordinada());
        }

        String hql = "select convenio from ConvenioDespesa convenio" +
            " left join convenio.despesaExercConvenios desp " +
            " where desp.despesaORC.provisaoPPADespesa.unidadeOrganizacional in (:unidades)";
        Query consulta = em.createQuery(hql, ConvenioDespesa.class);
        consulta.setParameter("unidades", unidades);
        List<ConvenioDespesa> resultList = consulta.getResultList();
        for (ConvenioDespesa convenioDespesa : resultList) {
            convenioDespesa.getConvenioDespesaLiberacoes().size();
            convenioDespesa.getPrestacaoContas().size();
            convenioDespesa.getAditivosConvenioDespesas().size();
        }
        if (resultList.isEmpty()) {
            return new ArrayList<>();
        } else {
            return resultList;
        }
    }

    public Object[] recuperarIdENumeroConvenioDespesa(SolicitacaoEmpenho solicitacaoEmpenho) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select cd.id, cd.numero ")
            .append(" from ConvenioDespesa cd ")
            .append("  inner join conveniodespsolicempenho cdse on cdse.conveniodespesa_id = cd.id ")
            .append(" where cdse.solicitacaoempenho_id = :solicitacaoEmpenhoId ");
        Query q = em.createNativeQuery(sb.toString());
        try {
            q.setParameter("solicitacaoEmpenhoId", solicitacaoEmpenho.getId());
            return (Object[]) q.getSingleResult();
        } catch (NoResultException | NullPointerException e) {
            return null;
        }
    }

    public BigDecimal buscarValorEmpenhadoPorConvenioDeDespesa(ConvenioDespesa convenioDespesa) {
        if (convenioDespesa == null || convenioDespesa.getId() == null) return BigDecimal.ZERO;
        String sql = "select coalesce(sum(dados.valor), 0) " +
            " from (select emp.valor as valor " +
            "      from empenho emp " +
            "      where emp.conveniodespesa_id = :idConvenio " +
            "      union all " +
            "      select est.valor * -1 as valor " +
            "      from empenhoestorno est " +
            "          inner join empenho emp on emp.id = est.empenho_id " +
            "      where emp.conveniodespesa_id = :idConvenio " +
            "     ) dados ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idConvenio", convenioDespesa.getId());
        return (BigDecimal) q.getResultList().get(0);
    }

    public boolean isConvenioDespesaVigente(Date dataReferencia, ConvenioDespesa convenioDespesa) {
        if (dataReferencia == null) return false;
        String sql = " select * from conveniodespesa " +
            " where id = :idConvenio " +
            "   and to_date(:dataReferencia, 'dd/MM/yyyy') between trunc(datavigenciainicial) and coalesce(trunc(datavigenciafinal), to_date(:dataReferencia, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idConvenio", convenioDespesa.getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        List<Object[]> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }
}
