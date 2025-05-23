/*
 * Codigo gerado automaticamente em Sat Jul 02 10:03:07 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.EnquadramentoFuncionalLoteItem;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import javax.ejb.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
public class EnquadramentoFuncionalFacade extends AbstractFacade<EnquadramentoFuncional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;

    public EnquadramentoFuncionalFacade() {
        super(EnquadramentoFuncional.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void remover(EnquadramentoFuncional ef) {
        ContratoFP contratoFP = ef.getContratoServidor();
        contratoFP = contratoFPFacade.recuperarParaTelaEnquadramentoFuncional(contratoFP.getId());
        contratoFP.removerEnquadramentoFuncional(ef);
        contratoFP.abrirVigenciaUltimoEnquadramentoFuncional();
        contratoFPFacade.salvar(contratoFP);
    }

    public void remove(EnquadramentoFuncional ef) {
        super.remover(ef);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public EnquadramentoFuncional recuperar(Object id) {
        EnquadramentoFuncional ef = em.find(EnquadramentoFuncional.class, id);
        registrarBackupAsync(ef);
        return ef;
    }

    @Asynchronous
    public void registrarBackupAsync(EnquadramentoFuncional ef) {
        if (ef != null) {
            EnquadramentoFuncionalHist efHist = ef.getEnquadramentoFuncionalHist() == null ? new EnquadramentoFuncionalHist() : ef.getEnquadramentoFuncionalHist();
            EnquadramentoFuncional.copiaPropriedadesEAssociaHistorico(ef, efHist);
            em.merge(ef);
        }
    }

    public void registrarBackupSync(EnquadramentoFuncional ef) {
        if (ef != null && ef.getId() != null) {
            EnquadramentoFuncionalHist efHist = ef.getEnquadramentoFuncionalHist() == null ? new EnquadramentoFuncionalHist() : ef.getEnquadramentoFuncionalHist();
            EnquadramentoFuncional.copiaPropriedadesEAssociaHistorico(ef, efHist);
            em.merge(ef);
        }
    }

    @Override
    public void salvar(EnquadramentoFuncional entity) {
        entity = getEnquadramentoFuncionalComHistorico(entity);
        super.salvar(entity);
    }

    public EnquadramentoFuncional getEnquadramentoFuncionalComHistorico(EnquadramentoFuncional enquadramentoFuncional) {
        EnquadramentoFuncional enquadramentoFuncionalAtualmentePersistido = getEnquadramentoFuncionalAtualmentePersistido(enquadramentoFuncional);
        enquadramentoFuncional.criarOrAtualizarAndAssociarHistorico(enquadramentoFuncionalAtualmentePersistido);
        return enquadramentoFuncional;
    }

    private EnquadramentoFuncional getEnquadramentoFuncionalAtualmentePersistido(EnquadramentoFuncional enquadramentoFuncional) {
        return em.find(EnquadramentoFuncional.class, enquadramentoFuncional.getId());
    }

    public EnquadramentoFuncional recuperaEnquadramentoFuncionalVigentePorContratoServidor(ContratoFP contratoServidor, EnquadramentoFuncional enquadramento) {
        try {
            Query q;
            //se for um novo enquadramento
            if (enquadramento == null) {
                q = em.createQuery(" from EnquadramentoFuncional obj where obj.contratoServidor = :contrato and "
                    + " obj.finalVigencia is null ");
            } else {
                q = em.createQuery(" from EnquadramentoFuncional obj where obj.contratoServidor = :contrato and "
                    + " obj.finalVigencia is null and obj.inicioVigencia <= :parametroData ");
                q.setParameter("parametroData", enquadramento.getInicioVigencia(), TemporalType.DATE);
            }
            q.setParameter("contrato", contratoServidor);
            q.setMaxResults(1);
            return (EnquadramentoFuncional) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public EnquadramentoFuncional recuperaEnquadramentoVigente(ContratoFP contratoFP) {
        Query q = em.createQuery("from EnquadramentoFuncional af where af.contratoServidor = :contratoFP and :data between af.inicioVigencia and coalesce(af.finalVigencia,:data) order by af.inicioVigencia desc");
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setMaxResults(1);
        try {
            return (EnquadramentoFuncional) q.getSingleResult();
        } catch (NoResultException nr) {
            return null;
        }

    }

    public EnquadramentoFuncional recuperaEnquadramentoFuncionalVigente(ContratoFP contratoFP) {
        Query q = em.createQuery("from EnquadramentoFuncional af where af.contratoServidor = :contratoFP and :dataVigencia >= af.inicioVigencia "
            + " and :dataVigencia <= coalesce(af.finalVigencia,:dataVigencia) ");
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setMaxResults(1);
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (EnquadramentoFuncional) q.getSingleResult();
        } catch (NoResultException nr) {
            nr.printStackTrace();
            return null;
        }

    }

    public EnquadramentoFuncional recuperaEnquadramentoFuncionalVigenteSiprev(VinculoFP contratoFP, Date data) {
        Query q = em.createQuery("from EnquadramentoFuncional af where af.contratoServidor = :contratoFP and :dataVigencia >= af.inicioVigencia "
            + " and :dataVigencia <= coalesce(af.finalVigencia,:dataVigencia) ");
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(data));
        q.setMaxResults(1);
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (EnquadramentoFuncional) q.getSingleResult();
        } catch (NoResultException nr) {
            nr.printStackTrace();
            return null;
        }

    }


    public void salvarNovo(EnquadramentoFuncional entity, EnquadramentoFuncional vigente, ProvimentoFP provimentoFP) {
        try {
            if (vigente != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(entity.getInicioVigencia());
                c.add(Calendar.DATE, -1);
                vigente.setFinalVigencia(c.getTime());
                vigente.setDataCadastroFinalVigencia(entity.getDataCadastro());
                em.merge(vigente);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
            }
            if (provimentoFP.getTipoProvimento() != null) {
                provimentoFP = salvarProvimento(provimentoFP);
                entity.setProvimentoFP(provimentoFP);
            }
            em.persist(entity);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Problemas ao Salvar", "Erro: " + e));
        }
    }

    public List<EnquadramentoFuncional> recuperaEnquadramentosPorContratoEData(ContratoFP s, Date data) {
        StringBuilder hql = new StringBuilder();
        hql.append("select enquadramento from EnquadramentoFuncional enquadramento inner join enquadramento.contratoServidor contrato");
        hql.append(" inner join enquadramento.categoriaPCS categoria");
        hql.append(" inner join enquadramento.progressaoPCS progressao");
        hql.append(" inner join contrato.matriculaFP matricula");
        hql.append(" inner join matricula.pessoa pessoa");
        hql.append(" where contrato = :contratofp");
        hql.append(" and coalesce(enquadramento.finalVigencia, :dataProvimento) <= :dataProvimento");
        hql.append(" order by enquadramento.finalVigencia desc ");
        Query q = em.createQuery(hql.toString());
        q.setParameter("contratofp", s);
        q.setParameter("dataProvimento", data);
        List<EnquadramentoFuncional> lista = q.getResultList();
        for (EnquadramentoFuncional e : lista) {
            EnquadramentoPCS epcs = enquadramentoPCSFacade.recuperaUltimoValor(e.getCategoriaPCS(), e.getProgressaoPCS(), e.getInicioVigencia(), e.getFinalVigencia());
            if (epcs != null) {
                e.setVencimentoBase(epcs.getVencimentoBase());
            }
        }
        return q.getResultList();
    }

    public List<EnquadramentoFuncional> recuperaEnquadramentoContratoFP(ContratoFP contratoFP) {
        Query q = em.createQuery("from EnquadramentoFuncional ef " +
            " where ef.contratoServidor.id = :contratoFP " +
            " order by ef.inicioVigencia");
        q.setParameter("contratoFP", contratoFP.getId());
        return q.getResultList();
    }

    public List<EnquadramentoFuncional> buscarEnquadramentoFuncionalProgressao(ContratoFP contratoFP) {
        String sql = " select enquadramento.* from EnquadramentoFuncional enquadramento " +
            " inner join PROVIMENTOFP provimento on enquadramento.PROVIMENTOFP_ID = provimento.id " +
            " inner join TIPOPROVIMENTO tipoprovimento on provimento.TIPOPROVIMENTO_ID = tipoprovimento.id " +
            " where enquadramento.contratoservidor_id = :contrato and tipoprovimento.codigo = :codigo";
        Query q = em.createNativeQuery(sql, EnquadramentoFuncional.class);
        q.setParameter("contrato", contratoFP.getId());
        q.setParameter("codigo", TipoProvimento.PROVIMENTO_PROGRESSAO);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public void salvarNovoEnquadramentoFuncional(EnquadramentoFuncional novoEnquadramentoFuncional, EnquadramentoFuncional enquadramentoFuncionalVigente, ProvimentoFP provimentoFP, String observacao, AtoLegal atoLegal, CategoriaPCS categoriaPCS, ProgressaoPCS progressaoPCS) {

        TipoProvimento tipoProvimento = tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.PROVIMENTO_PROGRESSAO);

        if (tipoProvimento == null) {
            FacesUtil.addMessageWarn("Formulario:msgs", "Atenção!", "Tipo de provimento para progressão salarial não encontrado!");
            return;
        }
        try {
            provimentoFP.setVinculoFP(novoEnquadramentoFuncional.getContratoServidor());
            provimentoFP.setTipoProvimento(tipoProvimento);
            provimentoFP.setDataProvimento(novoEnquadramentoFuncional.getInicioVigencia());
            provimentoFP.setAtoLegal(atoLegal);
            provimentoFP.setObservacao(observacao);

            provimentoFP = em.merge(provimentoFP);
            novoEnquadramentoFuncional.setProvimentoFP(provimentoFP);
            salvar(enquadramentoFuncionalVigente);
            em.merge(novoEnquadramentoFuncional);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public EnquadramentoFuncional recuperaEnquadramentoFuncionalVigente(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();
        hql.append("select distinct new " + new EnquadramentoFuncional().getClass().getCanonicalName());
        hql.append("(enquadramentoFuncional.id,enquadramentoFuncional.inicioVigencia, ");
        hql.append(" enquadramentoFuncional.finalVigencia,enquadramentoFuncional.progressaoPCS,");
        hql.append(" enquadramentoFuncional.categoriaPCS, enquadramentoFuncional.contratoServidor,");
        hql.append(" enquadramentoPCS.vencimentoBase, enquadramentoFuncional.dataCadastro,");
        hql.append(" enquadramentoFuncional.dataCadastroFinalVigencia) ");
        hql.append(" from EnquadramentoPCS enquadramentoPCS, EnquadramentoFuncional enquadramentoFuncional ");
        hql.append(" inner join enquadramentoFuncional.categoriaPCS categoria ");
        hql.append(" inner join enquadramentoFuncional.progressaoPCS progressao ");
        hql.append(" where enquadramentoPCS.categoriaPCS = categoria ");
        hql.append("   and enquadramentoPCS.progressaoPCS = progressao");
        hql.append("   and enquadramentoFuncional.contratoServidor = :contrato");
        hql.append("   and to_date(:dataProvimento, 'dd/MM/yyyy') between enquadramentoFuncional.inicioVigencia ");
        hql.append("   and coalesce(enquadramentoFuncional.finalVigencia, to_date(:dataProvimento, 'dd/MM/yyyy'))");
        hql.append("   and to_date(:dataProvimento, 'dd/MM/yyyy')  between enquadramentoPCS.inicioVigencia and coalesce(enquadramentoPCS.finalVigencia, to_date(:dataProvimento,'dd/MM/yyyy') )) ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("dataProvimento", DataUtil.getDataFormatada(dataProvimento));
        q.setParameter("contrato", contratoFP);

        List<EnquadramentoFuncional> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return new EnquadramentoFuncional();
        }
        return lista.get(0);
    }


    public EnquadramentoFuncional buscarEnquadramentoFuncionalPorData(ContratoFP contratoFP, Date dataReferencia) {
        StringBuilder hql = new StringBuilder();
        hql.append("select enquadramentoFuncional  ");
        hql.append(" from EnquadramentoFuncional enquadramentoFuncional ");
        hql.append(" where  ");
        hql.append("   enquadramentoFuncional.contratoServidor = :contrato");
        hql.append("   and :dataProvimento between enquadramentoFuncional.inicioVigencia and  coalesce(enquadramentoFuncional.finalVigencia,:dataProvimento) ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("dataProvimento", dataReferencia, TemporalType.DATE);
        q.setParameter("contrato", contratoFP);

//        Util u = new Util();
//        u.imprimeSQL(hql.toString(), q);

        List<EnquadramentoFuncional> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return new EnquadramentoFuncional();
        }
        return lista.get(0);
    }


    public EnquadramentoFuncional recuperaEnquadramentoFuncionalPorContratoEData(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();

        hql.append("select enquadramentoFuncional from EnquadramentoPCS enquadramentoPCS, EnquadramentoFuncional enquadramentoFuncional ");
        hql.append(" inner join enquadramentoFuncional.categoriaPCS categoria ");
        hql.append(" inner join enquadramentoFuncional.progressaoPCS progressao ");
        hql.append(" where enquadramentoPCS.categoriaPCS = categoria ");
        hql.append("   and enquadramentoPCS.progressaoPCS = progressao");
        hql.append("   and enquadramentoFuncional.contratoServidor = :contrato");
        hql.append("   and :dataProvimento between enquadramentoFuncional.inicioVigencia ");
        hql.append("   and coalesce(enquadramentoFuncional.finalVigencia, :dataProvimento)");
        hql.append("   and :dataOperacao between enquadramentoPCS.inicioVigencia ");
        hql.append("   and coalesce(enquadramentoPCS.finalVigencia, :dataOperacao)");

        Query q = em.createQuery(hql.toString());
        q.setParameter("dataProvimento", dataProvimento, TemporalType.DATE);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao(), TemporalType.DATE);
        q.setParameter("contrato", contratoFP);

        List<EnquadramentoFuncional> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public EnquadramentoFuncional recuperaEnquadramentoFuncionalPorContratoEDataProvimentoEDataVigencia(ContratoFP contratoFP, Date dataProvimento, Date dataVigencia) {
        StringBuilder hql = new StringBuilder();

        hql.append("select enquadramentoFuncional from EnquadramentoPCS enquadramentoPCS, EnquadramentoFuncional enquadramentoFuncional ");
        hql.append(" inner join enquadramentoFuncional.categoriaPCS categoria ");
        hql.append(" inner join enquadramentoFuncional.progressaoPCS progressao ");
        hql.append(" where enquadramentoPCS.categoriaPCS = categoria ");
        hql.append("   and enquadramentoPCS.progressaoPCS = progressao");
        hql.append("   and enquadramentoFuncional.contratoServidor = :contrato");
        hql.append("   and :dataProvimento between enquadramentoFuncional.inicioVigencia ");
        hql.append("   and coalesce(enquadramentoFuncional.finalVigencia, :dataProvimento)");
        hql.append("   and :dataOperacao between enquadramentoPCS.inicioVigencia ");
        hql.append("   and coalesce(enquadramentoPCS.finalVigencia, :dataOperacao)");

        Query q = em.createQuery(hql.toString());
        q.setParameter("dataProvimento", dataProvimento, TemporalType.DATE);
        q.setParameter("dataOperacao", dataVigencia, TemporalType.DATE);
        q.setParameter("contrato", contratoFP);

        List<EnquadramentoFuncional> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public EnquadramentoFuncional recuperaUltimoEnquadramentoFuncionalQueConsideraProgressaoAutomaticaPorContrato(ContratoFP contratoFP, Date dataVigencia) {

        String hql = "select enquadramentoFuncional from EnquadramentoPCS enquadramentoPCS, EnquadramentoFuncional enquadramentoFuncional " +
            " inner join enquadramentoFuncional.categoriaPCS categoria " +
            " inner join enquadramentoFuncional.progressaoPCS progressao " +
            " where enquadramentoPCS.categoriaPCS = categoria " +
            "   and enquadramentoPCS.progressaoPCS = progressao" +
            "   and enquadramentoFuncional.contratoServidor = :contrato" +
            "   and :dataOperacao between enquadramentoPCS.inicioVigencia " +
            "   and coalesce(enquadramentoPCS.finalVigencia, :dataOperacao)" +
            "   and enquadramentoFuncional.consideraParaProgAutomat = true " +
            " order by enquadramentoFuncional.inicioVigencia desc ";

        Query q = em.createQuery(hql);
        q.setParameter("dataOperacao", dataVigencia, TemporalType.DATE);
        q.setParameter("contrato", contratoFP);

        List<EnquadramentoFuncional> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }



    public EnquadramentoFuncional recuperaUltimoEnquadramentoFuncionalComValor(ContratoFP contratoFP) {
        StringBuilder hql = new StringBuilder();
        hql.append("select distinct new " + new EnquadramentoFuncional().getClass().getCanonicalName());
        hql.append("(enquadramentoFuncional.id,enquadramentoFuncional.inicioVigencia, ");
        hql.append(" enquadramentoFuncional.finalVigencia,enquadramentoFuncional.progressaoPCS,");
        hql.append(" enquadramentoFuncional.categoriaPCS, enquadramentoFuncional.contratoServidor,");
        hql.append(" enquadramentoPCS.vencimentoBase, enquadramentoFuncional.dataCadastro,");
        hql.append(" enquadramentoFuncional.dataCadastroFinalVigencia) ");
        hql.append(" from EnquadramentoPCS enquadramentoPCS, EnquadramentoFuncional enquadramentoFuncional ");
        hql.append(" inner join enquadramentoFuncional.categoriaPCS categoria ");
        hql.append(" inner join enquadramentoFuncional.progressaoPCS progressao ");
        hql.append(" where enquadramentoPCS.categoriaPCS = categoria ");
        hql.append("   and enquadramentoPCS.progressaoPCS = progressao");
        hql.append("   and enquadramentoFuncional.contratoServidor = :contrato");
        hql.append("   and enquadramentoFuncional.inicioVigencia = (select max(eq.inicioVigencia) from EnquadramentoFuncional eq where eq.contratoServidor.id = enquadramentoFuncional.contratoServidor.id) ");
        hql.append("   and coalesce(enquadramentoFuncional.finalVigencia,current_date) between enquadramentoPCS.inicioVigencia and coalesce(enquadramentoPCS.finalVigencia,(coalesce(enquadramentoFuncional.finalVigencia,current_date))) ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        List<EnquadramentoFuncional> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public void removerPromocao(Promocao promocao) {
        EnquadramentoFuncional efAnterior = promocao.getEnquadramentoAnterior();
        efAnterior.setDataCadastroFinalVigencia(null);
        efAnterior.setFinalVigencia(null);
        this.em.merge(efAnterior);

        EnquadramentoFuncional efARemover = promocao.getEnquadramentoNovo();

        this.em.remove(this.em.find(Promocao.class, promocao.getId()));
        this.em.remove(this.em.find(EnquadramentoFuncional.class, efARemover.getId()));
        this.em.remove(this.em.find(ProvimentoFP.class, efARemover.getProvimentoFP().getId()));
    }

    public BigDecimal getValorEnquadramentoFuncional(Long idCategoria, Long idProgressao, Date dataReferencia) {
        String sql = " select e.vencimentobase                  " +
            "   from enquadramentopcs e                " +
            "  where categoriapcs_id  = :categoria_id  " +
            "    and progressaopcs_id = :progressao_id " +
            "     and :dataReferencia between e.inicioVigencia and coalesce(e.finalvigencia,:dataReferencia)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("categoria_id", idCategoria);
        q.setParameter("progressao_id", idProgressao);
        q.setParameter("dataReferencia", dataReferencia);
        q.setMaxResults(1);
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public FichaFinanceiraFPFacade getFichaFinanceiraFPFacade() {
        return fichaFinanceiraFPFacade;
    }

    public void setFichaFinanceiraFPFacade(FichaFinanceiraFPFacade fichaFinanceiraFPFacade) {
        this.fichaFinanceiraFPFacade = fichaFinanceiraFPFacade;
    }

    public Map<VinculoFP, Map<EnquadramentoFuncional, Boolean>> processarItensParaExclusao(Date dataCadastro, Date inicioVigencia) {
        //select * from enquadramentofuncional where DATACADASTRO >= '23/02/2016' and INICIOVIGENCIA <= '22/02/2016';

        Query q = em.createQuery("from EnquadramentoFuncional where dataCadastro >= :dataCadastro and inicioVigencia <= :inicioVigencia and finalVigencia is null");
        q.setParameter("dataCadastro", dataCadastro);
        q.setParameter("inicioVigencia", inicioVigencia);
        List<EnquadramentoFuncional> resultados = q.getResultList();
        Map<VinculoFP, Map<EnquadramentoFuncional, Boolean>> enquadramentos = Maps.newTreeMap();
        if (resultados != null && !resultados.isEmpty()) {
            return classificarEnquadramentos(enquadramentos, resultados);
        }
        return null;
    }

    private Map<VinculoFP, Map<EnquadramentoFuncional, Boolean>> classificarEnquadramentos(Map<VinculoFP, Map<EnquadramentoFuncional, Boolean>> enquadramentos, List<EnquadramentoFuncional> resultados) {
        for (EnquadramentoFuncional aSerExcluido : resultados) {
            DateTime dataInicioVigenciaVigente = new DateTime(aSerExcluido.getInicioVigencia());
            dataInicioVigenciaVigente = dataInicioVigenciaVigente.minusDays(1);
            EnquadramentoFuncional antePenultimo = recuperaEnquadramentoFuncionalPorContratoEData(aSerExcluido.getContratoServidor(), dataInicioVigenciaVigente.toDate());
            if (antePenultimo != null && antePenultimo.getId() != null) {
                Map<EnquadramentoFuncional, Boolean> enquadramentosVinculo = Maps.newLinkedHashMap();
                enquadramentosVinculo.put(aSerExcluido, true);
                enquadramentosVinculo.put(antePenultimo, false);
                enquadramentos.put(aSerExcluido.getContratoServidor(), enquadramentosVinculo);
            }
        }
        return enquadramentos;
    }

    public void deletarRegistros(Map<VinculoFP, Map<EnquadramentoFuncional, Boolean>> vinculoFPEnquadramentos) {
        for (Map.Entry<VinculoFP, Map<EnquadramentoFuncional, Boolean>> vinculoFPMapEntry : vinculoFPEnquadramentos.entrySet()) {
            for (Map.Entry<EnquadramentoFuncional, Boolean> enquadramentoFuncional : vinculoFPMapEntry.getValue().entrySet()) {
                if (enquadramentoFuncional.getValue()) {
                    remover(enquadramentoFuncional.getKey());
                } else {
                    enquadramentoFuncional.getKey().setFimVigencia(null);
                    em.merge(enquadramentoFuncional.getKey());
                }
            }

        }
    }

    public ProvimentoFP salvarProvimento(ProvimentoFP provimentoFP) {
        return em.merge(provimentoFP);
    }

    public TipoProvimentoFacade getTipoProvimentoFacade() {
        return tipoProvimentoFacade;
    }

    public ProvimentoFPFacade getProvimentoFPFacade() {
        return provimentoFPFacade;
    }

    public List<EnquadramentoFuncional> buscarEnquadramentosPorCargo(Cargo cargo, Date dataOperacao) {
        String hql = "select ef from EnquadramentoFuncional ef " +
            "    join ef.contratoServidor cont " +
            "    join cont.cargo cargo " +
            "    where :dataOperacao between ef.inicioVigencia and coalesce(ef.finalVigencia, :dataOperacao) ";
        if (cargo != null) {
            hql += " and cargo.id = :cargo ";
        }
        Query q = em.createQuery(hql);
        if (cargo != null) {
            q.setParameter("cargo", cargo.getId());
        }
        q.setParameter("dataOperacao", dataOperacao);
        return q.getResultList();
    }

    public List<EnquadramentoFuncional> buscarEnquadramentosPorCategoriaAndProgressaoSuperior(CategoriaPCS categoria, ProgressaoPCS progressao, Cargo cargo, Date dataOperacao) {

        String hql = "select ef from EnquadramentoFuncional ef " +
            "    join ef.categoriaPCS cat" +
            "    join ef.progressaoPCS prog " +
            "    join ef.contratoServidor cont " +
            "    join cont.cargo cargo " +
            "   where cat.superior.id = :categoria " +
            "     and prog.superior.id  = :progressao " +
            "     and :dataOperacao between ef.inicioVigencia and coalesce(ef.finalVigencia, :dataOperacao) ";
        if (cargo != null) {
            hql += " and cargo.id = :cargo ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("categoria", categoria.getId());
        q.setParameter("progressao", progressao.getId());
        if (cargo != null) {
            q.setParameter("cargo", cargo.getId());
        }
        q.setParameter("dataOperacao", dataOperacao);
        return q.getResultList();
    }

    public List<EnquadramentoFuncional> recuperarEnquadramentoContratoFP(ContratoFP contratoFP) {
        String sql = "select ef.* from enquadramentofuncional ef " +
            " inner join contratofp contrato on ef.contratoservidor_id = contrato.id " +
            " where contrato.id = :contratoFP " +
            " order by ef.iniciovigencia ";
        Query q = em.createNativeQuery(sql, EnquadramentoFuncional.class);
        q.setParameter("contratoFP", contratoFP.getId());
        List enquadramentos = q.getResultList();
        if (enquadramentos!= null && !enquadramentos.isEmpty()) {
            return enquadramentos;
        }
        return Lists.newArrayList();
    }

    public EnquadramentoFuncionalLoteItem getEnquadramentoFuncionalLoteItem(EnquadramentoFuncional
                                                                                enquadramentoFuncional) {
        String sql = "select item.* from EnquadramentoFuncionalLoteItem item " +
            " where ENQUADRAMENTOFUNCNOVO_ID = :idEnquadramento";
        Query q = em.createNativeQuery(sql, EnquadramentoFuncionalLoteItem.class);
        q.setParameter("idEnquadramento", enquadramentoFuncional.getId());
        List result = q.getResultList();
        if (!result.isEmpty()) {
            return (EnquadramentoFuncionalLoteItem) result.get(0);
        }
        return null;
    }

    public void removerEnquadramentoFuncionalLoteItem(EnquadramentoFuncionalLoteItem enquadramentoFuncionalLoteItem) {
        String sql = "delete EnquadramentoFuncionalLoteItem " +
            " where id = :idEnquadramento";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idEnquadramento", enquadramentoFuncionalLoteItem.getId());
        q.executeUpdate();
    }
}
