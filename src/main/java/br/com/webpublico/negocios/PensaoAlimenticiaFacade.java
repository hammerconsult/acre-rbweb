/*
 * Codigo gerado automaticamente em Wed Feb 15 08:57:52 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.FiltroBaseFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.singletons.SingletonGeradorCodigoRH;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class PensaoAlimenticiaFacade extends AbstractFacade<PensaoAlimenticia> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;

    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private BaseFPFacade baseFPFacade;
    @EJB
    private SingletonGeradorCodigoRH singletonGeradorCodigoRH;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public PensaoAlimenticiaFacade() {
        super(PensaoAlimenticia.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean existeInstituidor(PensaoAlimenticia pensaoAlimenticia) {
        String hql = " from PensaoAlimenticia a where a.vinculoFP_id = :instituidor ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("instituidor", pensaoAlimenticia.getVinculoFP());

        List<PensaoAlimenticia> lista = new ArrayList<>();
        lista = q.getResultList();

        if (lista.contains(pensaoAlimenticia)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public List<PensaoAlimenticia> buscarPensoesAlimenticiasPorBeneficiario(PessoaFisica beneficiario, Date dataOperacao) {
        String hql = "select pensao from PensaoAlimenticia pensao " +
            " inner join pensao.beneficiosPensaoAlimenticia beneficio " +
            " where beneficio.beneficiario = :beneficiario " +
            " and :dataVigencia between beneficio.inicioVigencia and coalesce(beneficio.finalVigencia, :dataVigencia)";
        Query q = getEntityManager().createQuery(hql, PensaoAlimenticia.class);
        q.setParameter("beneficiario", beneficiario);
        q.setParameter("dataVigencia", dataOperacao);

        return q.getResultList();
    }

    public List<PensaoAlimenticia> buscarPensoesAlimenticiasPorInstituidor(PessoaFisica instituidor) {
        String hql = "select pensao from PensaoAlimenticia pensao " +
            " inner join pensao.beneficiosPensaoAlimenticia beneficio " +
            " where pensao.vinculoFP.matriculaFP.pessoa = :instituidor " +
            " and :dataVigencia between beneficio.inicioVigencia and coalesce(beneficio.finalVigencia, :dataVigencia)";
        Query q = getEntityManager().createQuery(hql, PensaoAlimenticia.class);
        q.setParameter("instituidor", instituidor);
        q.setParameter("dataVigencia", sistemaFacade.getDataOperacao());
        return q.getResultList();
    }


    public List<BeneficioPensaoAlimenticia> buscarBeneficiarioPensaoAlimenticiaPorAnoAndVinculo(VinculoFP vinculoFP, Exercicio exercicio) {
        String hql = "select distinct new BeneficioPensaoAlimenticia(beneficio.beneficiario, beneficio.eventoFP, beneficio.grauParentesco) from PensaoAlimenticia pensao " +
            " inner join pensao.beneficiosPensaoAlimenticia beneficio " +
            " where pensao.vinculoFP = :instituidor " +
            " and :ano between extract(year from beneficio.inicioVigencia) and coalesce(extract(year from beneficio.finalVigencia), :ano)";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("instituidor", vinculoFP);
        q.setParameter("ano", exercicio.getAno());
        return q.getResultList();
    }

    public List<BeneficioPensaoAlimenticia> buscarBeneficiariosPensaoAlimenticiaPorAnoAndPessoa(PessoaFisica pf, Exercicio exercicio) {
        String hql = "select distinct new BeneficioPensaoAlimenticia(beneficio.beneficiario, beneficio.eventoFP, beneficio.grauParentesco) from PensaoAlimenticia pensao " +
            " inner join pensao.beneficiosPensaoAlimenticia beneficio " +
            " where pensao.vinculoFP.matriculaFP.pessoa = :pf " +
            " and :ano between extract(year from beneficio.inicioVigencia) and coalesce(extract(year from beneficio.finalVigencia), :ano)" +
            " ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("pf", pf);
        q.setParameter("ano", exercicio.getAno());

        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<BeneficioPensaoAlimenticia> buscarBeneficioPensaoAlimenticiaVigentePorPessoa(PessoaFisica pf, Date dataOperacao) {
        String hql = "select distinct new BeneficioPensaoAlimenticia(beneficio.beneficiario, beneficio.contaCorrenteBancaria) from PensaoAlimenticia pensao " +
            " inner join pensao.beneficiosPensaoAlimenticia beneficio " +
            " where beneficio.beneficiario = :pf " +
            " and :dataOperacao between beneficio.inicioVigencia and coalesce(beneficio.finalVigencia, :dataOperacao)" +
            " ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("pf", pf);
        q.setParameter("dataOperacao", dataOperacao);

        return q.getResultList();
    }

    public List<BeneficioPensaoAlimenticia> buscarBeneficiarioPensaoAlimenticiaVigentePorVinculo(VinculoFP vinculoFP, CompetenciaFP competenciaFP) {
        LocalDate dataReferencia = LocalDate.of(competenciaFP.getExercicio().getAno(),
            competenciaFP.getMes().getNumeroMes(), 1);
        Date data = Date.from(dataReferencia.atStartOfDay(ZoneId.systemDefault()).toInstant());

        String hql = "select beneficio from PensaoAlimenticia pensao " +
            " inner join pensao.beneficiosPensaoAlimenticia beneficio " +
            " where pensao.vinculoFP = :instituidor " +
            " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between to_date(to_char(beneficio.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "       and to_date(to_char(coalesce(beneficio.finalVigencia, :data),'mm/yyyy'),'mm/yyyy')";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("instituidor", vinculoFP);
        q.setParameter("data", data);
        return q.getResultList();
    }

    public List<ValorPensaoAlimenticia> buscarPensaoAlimenticiaVigentePorBeneficiarioPensaoAlimenticia(BeneficioPensaoAlimenticia beneficioPensaoAlimenticia, Date data) {
        String hql = "select valorPensao from BeneficioPensaoAlimenticia beneficio " +
            " inner join beneficio.valoresPensaoAlimenticia valorPensao " +
            " where valorPensao.beneficioPensaoAlimenticia = :beneficioPensaoAlimenticia " +
            " and :data between valorPensao.inicioVigencia and coalesce(valorPensao.finalVigencia, :data)";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("beneficioPensaoAlimenticia", beneficioPensaoAlimenticia);
        q.setParameter("data", data);

        return q.getResultList();
    }


    @Override
    public PensaoAlimenticia recuperar(Object obj) {
        PensaoAlimenticia pensao = em.find(PensaoAlimenticia.class, obj);
        Hibernate.initialize(pensao.getBeneficiosPensaoAlimenticia());
        for (BeneficioPensaoAlimenticia ben : pensao.getBeneficiosPensaoAlimenticia()) {
            for (ValorPensaoAlimenticia valorPensaoAlimenticia : ben.getValoresPensaoAlimenticia()) {
                if (valorPensaoAlimenticia.getBaseFP() != null) {
                    Hibernate.initialize(valorPensaoAlimenticia.getBaseFP().getItensBasesFPs());
                }
            }

        }
        return pensao;
    }

    public List<PensaoAlimenticia> recuperarPensaoPorInstituidor(VinculoFP vinculoFP) {
        try {
            String hql = " from PensaoAlimenticia a where a.vinculoFP = :instituidor ";
            Query q = getEntityManager().createQuery(hql);
            q.setParameter("instituidor", vinculoFP);
            List<PensaoAlimenticia> pensaoAlimenticia = Lists.newArrayList();
            if (!q.getResultList().isEmpty()) {
                pensaoAlimenticia = q.getResultList();
                for (PensaoAlimenticia alimenticia : pensaoAlimenticia) {
                    for (BeneficioPensaoAlimenticia ben : alimenticia.getBeneficiosPensaoAlimenticia()) {
                        ben.getValoresPensaoAlimenticia().size();
                    }
                }
            }
            return pensaoAlimenticia;
        } catch (Exception e) {
            logger.error("Ocorreu um problema ao recuperar a pensão por instituidor");
            return null;
        }
    }

    public List<PensaoAlimenticia> buscarPensoesVigentesPorInstituidor(VinculoFP vinculoFP, Date dataReferencia) {
        try {
            String hql = " select distinct pensao from PensaoAlimenticia pensao " +
                " inner join pensao.beneficiosPensaoAlimenticia beneficio " +
                " where pensao.vinculoFP = :instituidor and :dataReferencia between beneficio.inicioVigencia and coalesce(beneficio.finalVigencia, :dataReferencia) ";
            Query q = getEntityManager().createQuery(hql);
            q.setParameter("instituidor", vinculoFP);
            q.setParameter("dataReferencia", dataReferencia);
            List<PensaoAlimenticia> pensoesAlimenticias = q.getResultList();
            if (!pensoesAlimenticias.isEmpty()) {
                for (PensaoAlimenticia alimenticia : pensoesAlimenticias) {
                    for (BeneficioPensaoAlimenticia ben : alimenticia.getBeneficiosPensaoAlimenticia()) {
                        ben.getValoresPensaoAlimenticia().size();
                    }
                }
            }
            return pensoesAlimenticias;
        } catch (Exception e) {
            logger.error("Ocorreu um problema ao recuperar a pensão por instituidor: {}", e);
            return null;
        }
    }

    @Override
    public void salvarNovo(PensaoAlimenticia entity) {
        salvarBase(entity);
        super.salvarNovo(entity);
    }


    @Override
    public void salvar(PensaoAlimenticia entity) {
        salvarBase(entity);
        super.salvar(entity);
    }

    public void salvarBase(PensaoAlimenticia entity) {
        for (BeneficioPensaoAlimenticia beneficioPensaoAlimenticia : entity.getBeneficiosPensaoAlimenticia()) {
            for (ValorPensaoAlimenticia valorPensaoAlimenticia : beneficioPensaoAlimenticia.getValoresPensaoAlimenticia()) {
                if (valorPensaoAlimenticia.getBaseFP() != null) {
                    valorPensaoAlimenticia.getBaseFP().setFiltroBaseFP(FiltroBaseFP.PENSAO_ALIMENTICIA);
                    valorPensaoAlimenticia.setBaseFP(em.merge(valorPensaoAlimenticia.getBaseFP()));
                }
            }

        }
    }

    public ContaCorrenteBancPessoaFacade getContaCorrenteBancPessoaFacade() {
        return contaCorrenteBancPessoaFacade;
    }

    public BigDecimal buscarValorEventoFPsPorMes(Pessoa p, VinculoFP vinculoFP, Mes mes, Integer ano, List<TipoFolhaDePagamento> tiposFolha, EventoFP verba) {


        String folhas = getSqlFromTipoFolha(tiposFolha);
        String sql = " SELECT (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                    "
            + "INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                             "
            + "INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                               "
            + "INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                      "
            + "INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                     "
            + "     WHERE fp.ano = :ano                                                                        "
            + "       AND fp.mes = :mes                                                                        "
            + "       AND m.pessoa_id = :pessoa_id                                                             ";
        if (verba != null) {
            sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
        }
        sql += "       AND v.id = :vinculo_id                                                                   "
            + folhas
            + "       AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige              "
            + "                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id      "
            + "                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id     "
            + "                                WHERE me.descricao = :descricaoModulo                           "
            + "                                  AND ige.operacaoformula = :adicao                             "
            + "                                  ))                         "
            + " -                                                                                              "
            + "    (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                           "
            + " INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                            "
            + " INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                              "
            + " INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                     "
            + " INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                    "
            + "      WHERE fp.ano = :ano                                                                       "
            + "        AND fp.mes = :mes                                                                       "
            + "        AND m.pessoa_id = :pessoa_id                                                            ";
        if (verba != null) {
            sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
        }
        sql += "       AND v.id = :vinculo_id                                                                   "
            + folhas
            + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige             "
            + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id     "
            + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id    "
            + "                                 WHERE me.descricao = :descricaoModulo                          "
            + "                                   AND ige.operacaoformula = :subtracao                         "
            + "                                   )) AS resultado FROM dual ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("vinculo_id", vinculoFP.getId());
        if (verba != null) {
            q.setParameter("eventofp_id", verba.getId());
        }
        q.setParameter("descricaoModulo", "IRRF");
        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
        int i = 0;
        for (TipoFolhaDePagamento tpf : tiposFolha) {
            q.setParameter("tipo_folha_" + i, tpf.name());
            i++;
        }

        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }


    public BigDecimal buscarValorEventoFPsAnual(Pessoa p, VinculoFP vinculoFP, Integer ano, List<TipoFolhaDePagamento> tiposFolha, EventoFP verba) {
        String folhas = getSqlFromTipoFolha(tiposFolha);

        String sql = " SELECT (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                    "
            + "INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                             "
            + "INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                               "
            + "INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                      "
            + "INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                     "
            + "     WHERE fp.ano = :ano                                                                        "
            + "       AND v.id = :vinculo_id                                                                   "
            + "       AND m.pessoa_id = :pessoa_id                                                             ";
        if (verba != null) {
            sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
        }

        sql += folhas
            + "       AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige              "
            + "                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id      "
            + "                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id     "
            + "                                WHERE me.descricao = :descricaoModulo                           "
            + "                                  AND ige.operacaoformula = :adicao                             "
            + "                                  ))                         "
            + " -                                                                                              "
            + "    (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                           "
            + " INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                            "
            + " INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                              "
            + " INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                     "
            + " INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                    "
            + "      WHERE fp.ano = :ano                                                                       "
            + "       AND v.id = :vinculo_id                                                                   "
            + "        AND m.pessoa_id = :pessoa_id                                                            ";
        if (verba != null) {
            sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
        }

        sql += folhas
            + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige             "
            + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id     "
            + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id    "
            + "                                 WHERE me.descricao = :descricaoModulo                          "
            + "                                   AND ige.operacaoformula = :subtracao                         "
            + "                                   )) AS resultado FROM dual ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("vinculo_id", vinculoFP.getId());
        if (verba != null) {
            q.setParameter("eventofp_id", verba.getId());
        }

        q.setParameter("descricaoModulo", "IRRF");
        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
        int i = 0;
        for (TipoFolhaDePagamento tpf : tiposFolha) {
            q.setParameter("tipo_folha_" + i, tpf.name());
            i++;
        }

        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private String getSqlFromTipoFolha(List<TipoFolhaDePagamento> tiposFolha) {
        String folhas = "and (";

        for (int i = 0; i < tiposFolha.size(); i++) {
            folhas += " fp.tipoFolhaDePagamento = :tipo_folha_" + i + " or";
        }
        folhas = folhas.substring(0, folhas.length() - 2);
        folhas += ")";
        return folhas;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public EventoFPFacade getEventoFPFacade() {
        return eventoFPFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public BaseFPFacade getBaseFPFacade() {
        return baseFPFacade;
    }

    public SingletonGeradorCodigoRH getSingletonGeradorCodigoRH() {
        return singletonGeradorCodigoRH;
    }

    public ReferenciaFPFacade getReferenciaFPFacade() {
        return referenciaFPFacade;
    }

}
