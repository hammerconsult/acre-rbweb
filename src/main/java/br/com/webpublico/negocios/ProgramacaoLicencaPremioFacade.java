package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.PeriodoAquisitivoFL;
import br.com.webpublico.entidades.ProgramacaoLicencaPremio;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ProgramacaoLicencaPremioFacade extends AbstractFacade<ProgramacaoLicencaPremio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ProgramacaoLicencaPremioFacade() {
        super(ProgramacaoLicencaPremio.class);
    }

    public List<ProgramacaoLicencaPremio> recuperaProgramacao(PeriodoAquisitivoFL periodoAquisitivoFL) {
        String hql = "from ProgramacaoLicencaPremio plp where plp.periodoAquisitivoFL = :periodoAquisitivoFL";
        Query q = em.createQuery(hql);

        q.setParameter("periodoAquisitivoFL", periodoAquisitivoFL);

        return q.getResultList();
    }

    public boolean existeOutraProgramacaoNoMesmoPeriodo(ProgramacaoLicencaPremio programacaoLicencaPremio) {
        String hql = "select p from ProgramacaoLicencaPremio p " +
            " where p.periodoAquisitivoFL = :periodoAquisitivoFL " +
            " and (p.inicioVigencia between :inicioVigencia and :finalVigencia " +
            " or p.finalVigencia between :inicioVigencia and :finalVigencia " +
            " or :inicioVigencia between p.inicioVigencia and p.finalVigencia " +
            " or :finalVigencia between p.inicioVigencia and p.finalVigencia)";

        if (programacaoLicencaPremio.getId() != null) {
            hql += " and p.id <> :programacaoLicencaPremio";
        }

        Query q = em.createQuery(hql);
        q.setParameter("periodoAquisitivoFL", programacaoLicencaPremio.getPeriodoAquisitivoFL());
        q.setParameter("inicioVigencia", programacaoLicencaPremio.getInicioVigencia());
        q.setParameter("finalVigencia", programacaoLicencaPremio.getFinalVigencia());
        if (programacaoLicencaPremio.getId() != null) {
            q.setParameter("programacaoLicencaPremio", programacaoLicencaPremio.getId());
        }

        return !q.getResultList().isEmpty();
    }

    public ProgramacaoLicencaPremio recuperaUltimaProgramacao(PeriodoAquisitivoFL periodoAquisitivoFL) {
        String hql = "select p from ProgramacaoLicencaPremio p " +
            " where p.periodoAquisitivoFL = :periodoAquisitivoFL " +
            " order by p.inicioVigencia desc";

        Query q = em.createQuery(hql);
        q.setParameter("periodoAquisitivoFL", periodoAquisitivoFL);

        List<ProgramacaoLicencaPremio> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public ProgramacaoLicencaPremio recuperaProgramacaoProximaAnterior(ProgramacaoLicencaPremio programacaoLicencaPremio, ProgramacaoLicencaPremio.ProgramacaoDesejada programacaoDesejada) {
        String hql = "select p from ProgramacaoLicencaPremio p " +
            " where p.periodoAquisitivoFL = :periodoAquisitivoFL ";


        if (programacaoDesejada.equals(ProgramacaoLicencaPremio.ProgramacaoDesejada.ANTERIOR)) {
            hql += " and p.finalVigencia < :inicioVigenciaAtual order by p.inicioVigencia desc";
        } else {
            hql += " and p.inicioVigencia > :finalVigenciaAtual order by p.inicioVigencia asc";
        }

        Query q = em.createQuery(hql);
        q.setParameter("periodoAquisitivoFL", programacaoLicencaPremio.getPeriodoAquisitivoFL());
        if (programacaoDesejada.equals(ProgramacaoLicencaPremio.ProgramacaoDesejada.ANTERIOR)) {
            q.setParameter("inicioVigenciaAtual", programacaoLicencaPremio.getInicioVigencia());
        } else {
            q.setParameter("finalVigenciaAtual", programacaoLicencaPremio.getFinalVigencia());
        }

        List<ProgramacaoLicencaPremio> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public List<ProgramacaoLicencaPremio> recuperaProgramacoes(ContratoFP contratoFP) {
        String hql = "from ProgramacaoLicencaPremio plp " +
            " where plp.periodoAquisitivoFL.contratoFP = :contratoFP";

        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", contratoFP);
        return q.getResultList();
    }

    public List<ProgramacaoLicencaPremio> buscarProgramacoesPorPeriodoAquisitivo(ContratoFP contratoFP, PeriodoAquisitivoFL periodoAquisitivoFL) {
        String sql = " select PLP.* from PROGRAMACAOLICENCAPREMIO PLP " +
            "              inner join PERIODOAQUISITIVOFL PA on PA.ID = PLP.PERIODOAQUISITIVOFL_ID " +
            "              inner join CONTRATOFP C on C.ID = PA.CONTRATOFP_ID " +
            "              where PA.ID = :periodoAquisitivoFL and C.ID = :contratoFP";
        Query q = em.createNativeQuery(sql, ProgramacaoLicencaPremio.class);
        q.setParameter("periodoAquisitivoFL", periodoAquisitivoFL.getId());
        q.setParameter("contratoFP", contratoFP.getId());
        return q.getResultList();
    }

    public List<ProgramacaoLicencaPremio> recuperaProgramacaoAuditoria(ProgramacaoLicencaPremio programacaoLicencaPremio) {
        String sql = "select p.inicioVigencia, p.finalVigencia from ProgramacaoLicencaPremio_AUD p " +
            " where p.id = :programacaoLicencaPremio " +
            " and p.rev < (select max(prog.rev) from ProgramacaoLicencaPremio_aud prog where prog.id = p.id )" +
            " order by p.rev desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("programacaoLicencaPremio", programacaoLicencaPremio.getId());

        List<ProgramacaoLicencaPremio> toReturn = new ArrayList<>();

        List<Object[]> elementos = q.getResultList();
        for (Object[] elem : elementos) {
            ProgramacaoLicencaPremio prog = new ProgramacaoLicencaPremio();
            prog.setInicioVigencia((Date) elem[0]);
            prog.setFinalVigencia((Date) elem[1]);
            toReturn.add(prog);
        }
        return toReturn;
    }


    public List<ProgramacaoLicencaPremio> buscarProgramacaoLicencaPremioPorPeriodoAquisitivo(PeriodoAquisitivoFL periodo) {
        String sql = "select * from PROGRAMACAOLICENCAPREMIO where PERIODOAQUISITIVOFL_ID = :periodo";
        Query q = em.createNativeQuery(sql, ProgramacaoLicencaPremio.class);
        q.setParameter("periodo", periodo.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }
}
