/*
 * Codigo gerado automaticamente em Wed May 09 09:45:26 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AverbacaoTempoServico;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.AverbacaoAgrupado;
import br.com.webpublico.enums.OrgaoEmpresa;
import br.com.webpublico.enums.rh.estudoatuarial.TipoRegimePrevidenciarioEstudoAtuarial;
import com.google.common.collect.Lists;

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
public class AverbacaoTempoServicoFacade extends AbstractFacade<AverbacaoTempoServico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ContratoFPFacade contratoFPFacade;

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public AverbacaoTempoServicoFacade() {
        super(AverbacaoTempoServico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Long getNumeroSequencial(ContratoFP contratoFP) {
        Query createQuery = em.createQuery("select count(a)from AverbacaoTempoServico a where a.contratoFP = :contrato");
        createQuery.setParameter("contrato", contratoFP);
        Long numero = (Long) createQuery.getSingleResult();
        numero++;
        return numero;
    }

    public List<AverbacaoTempoServico> listaFiltrandoX(String s, int inicio, int max) {
        String hql = " select averbacao from AverbacaoTempoServico averbacao "
            + " inner join averbacao.contratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " inner join averbacao.empregado empregado "
            + " left join averbacao.tipoDocumento tipoDocumento "
            + " where (lower(matricula.matricula) like :filtro) "
            + " or (lower(pf.nome) like :filtro) "
            + " or (lower(contrato.numero) like :filtro) "
            + " or (cast(averbacao.numero as string) like :filtro)"
            + " or (lower(empregado.razaoSocia) like :filtro)"
            + " or (lower(averbacao.cargo) like :filtro)"
            + " or (cast(tipoDocumento.codigo as string) like :filtro)"
            + " or (lower(tipoDocumento.descricao) like :filtro)";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public AverbacaoTempoServico averbacaoAposentado(ContratoFP contratoFP) {
        try {
            String sql = "SELECT * FROM averbacaotemposervico "
                + " WHERE aposentado IS NOT null "
                + " AND contratofp_id = :contratoFP";
            Query q = em.createNativeQuery(sql, AverbacaoTempoServico.class);
            q.setParameter("contratoFP", contratoFP.getId());
            List<AverbacaoTempoServico> ats = q.getResultList();
            if (ats == null || ats.isEmpty()) {
                return null;
            }
            return ats.get(ats.size() - 1);
        } catch (NoResultException nre) {
            return null;
        }


    }

    public List<AverbacaoTempoServico> averbacao(ContratoFP contratoFP) {
        try {
            String hql = "from AverbacaoTempoServico  where "
                + " contratoFP = :contratoFP";
            Query q = em.createQuery(hql);
            q.setParameter("contratoFP", contratoFP);
            List<AverbacaoTempoServico> ats = q.getResultList();
            if (ats == null || ats.isEmpty()) {
                return null;
            }
            return ats;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<AverbacaoTempoServico> buscarAverbacaoAposentado(ContratoFP contratoFP) {
        try {
            String hql = "from AverbacaoTempoServico  where "
                + " contratoFP = :contratoFP and aposentado = :aposentado";
            Query q = em.createQuery(hql);
            q.setParameter("contratoFP", contratoFP);
            q.setParameter("aposentado", true);
            List<AverbacaoTempoServico> ats = q.getResultList();
            if (ats == null || ats.isEmpty()) {
                return null;
            }
            return ats;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<AverbacaoTempoServico> buscarAverbacaoPorContratoFPAndData(VinculoFP vinculoFP, Date dataMaxima, Boolean isSextaParte) {
        String hql = "from AverbacaoTempoServico " +
                " where contratoFP.id = :contratoFP and inicioVigencia <= :dataMaxima ";
                if(isSextaParte != null) {
                    hql += " and sextaParte = :sextaParte ";
                }
                hql += " order by inicioVigencia";
        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", vinculoFP.getId());
        q.setParameter("dataMaxima", dataMaxima);
        if(isSextaParte != null) {
            q.setParameter("sextaParte", isSextaParte);
        }
        return q.getResultList();
    }

    public List<AverbacaoTempoServico> buscarAverbacaoPorContratoFPAndTipoRegimePrevidenciario(VinculoFP vinculoFP, Date dataMaxima, TipoRegimePrevidenciarioEstudoAtuarial tipoRegime) {
        String hql = "from AverbacaoTempoServico " +
            " where contratoFP.id = :contratoFP and inicioVigencia <= :dataMaxima " +
            "and tipoRegPrevidenciarioEstAtua = :tipoRegime" +
            " order by inicioVigencia";
        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", vinculoFP.getId());
        q.setParameter("dataMaxima", dataMaxima);
        q.setParameter("tipoRegime", tipoRegime);
        return q.getResultList();
    }

    public List<AverbacaoAgrupado> listaFiltrandoAgrupado(String s, int inicio, int max) {
        String hql = " select distinct contrato from AverbacaoTempoServico averbacao "
            + " inner join averbacao.contratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " inner join averbacao.empregado empregado "
            + " left join averbacao.tipoDocumento tipoDocumento "
            + " where (lower(matricula.matricula) like :filtro) "
            + " or (lower(pf.nome) like :filtro) "
            + " or (lower(contrato.numero) like :filtro) "
            + " or (cast(averbacao.numero as string) like :filtro)"
            + " or (lower(empregado.razaoSocial) like :filtro)"
            + " or (lower(averbacao.cargo) like :filtro)"
            + " or (cast(tipoDocumento.codigo as string) like :filtro)"
            + " or (lower(tipoDocumento.descricao) like :filtro)";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        List<ContratoFP> contratos = new ArrayList<>();
        List<AverbacaoAgrupado> averbacoes = new ArrayList<>();
        contratos.addAll(q.getResultList());
        for (ContratoFP contrato : contratos) {
            List<AverbacaoTempoServico> averba = new ArrayList<>();
            averba = averbacao(contrato);
            if (averba != null) {
                averbacoes.add(new AverbacaoAgrupado(contrato, averba));
            }
        }

        return averbacoes;
    }

    public List<AverbacaoTempoServico> averbacaoDeAposentado(ContratoFP contratoFP) {
        try {
            String hql = "from AverbacaoTempoServico " +
                " where contratoFP = :contratoFP " +
                " and aposentado is true";
            Query q = em.createQuery(hql);
            q.setParameter("contratoFP", contratoFP);
            List<AverbacaoTempoServico> ats = q.getResultList();
            if (ats == null || ats.isEmpty()) {
                return null;
            }
            return ats;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AverbacaoTempoServico buscarAverbacaoTempoServicoValidosSIPREV(ContratoFP contrato) {
        String sql = "select averbacao.* from AverbacaoTempoServico averbacao where (INICIOVIGENCIA is not null and FINALVIGENCIA is not null)" +
            "         and contratofp_id = :contrato";
        Query q = em.createNativeQuery(sql, AverbacaoTempoServico.class);
        q.setParameter("contrato", contrato.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (AverbacaoTempoServico) q.getResultList().get(0);
        }
        return null;
    }

    public Integer buscarTotalAverbadoPorContratoFPEmDias(ContratoFP contrato) {
        String sql = " SELECT sum( (ats.finalVigencia - ats.inicioVigencia)+1) as dias " +
            "FROM averbacaotemposervico ats " +
            "WHERE ats.contratofp_id = :CONTRATOFP_ID  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("CONTRATOFP_ID", contrato.getId());
        try {
            BigDecimal dias = (BigDecimal) q.getSingleResult();
            if (dias != null) {
                return dias.intValue();
            }
            return 0;
        } catch (NoResultException nre) {
            return 0;
        }
    }

    public List<AverbacaoTempoServico> buscarAverbacoesTempoServicoPorContrato(ContratoFP contrato) {
        String sql = " select averbacao.* from AverbacaoTempoServico averbacao " +
            " where contratofp_id = :contrato ";
        Query q = em.createNativeQuery(sql, AverbacaoTempoServico.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public AverbacaoTempoServico buscarPrimeiraAverbacaoTempoServicoPorContratoETipo(ContratoFP contrato, OrgaoEmpresa orgaoEmpresa) {
        String sql = " select averbacao.* " +
            " from AverbacaoTempoServico averbacao " +
            "         inner join vinculofp vinc on vinc.id = averbacao.contratofp_id " +
            "         inner join matriculafp mat on vinc.matriculafp_id = mat.id " +
            " where mat.matricula = :matricula " +
            "  and averbacao.orgaoempresa = :orgaoEmpresa " +
            " order by averbacao.iniciovigencia ";
        Query q = em.createNativeQuery(sql, AverbacaoTempoServico.class);
        q.setParameter("matricula", contrato.getMatriculaFP().getMatricula());
        q.setParameter("orgaoEmpresa", orgaoEmpresa.name());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (AverbacaoTempoServico) resultList.get(0);
        }
        return null;
    }
}
