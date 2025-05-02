package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 27/07/2016.
 */
@Stateless
public class TipoSextaParteFacade extends AbstractFacade<TipoSextaParte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public TipoSextaParteFacade() {
        super(TipoSextaParte.class);
    }

    @Override
    public void salvarNovo(TipoSextaParte entity) {
        entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(TipoSextaParte.class, "codigo"));
        super.salvarNovo(entity);
    }

    public List<TipoSextaParte> buscarTipoSextaParte(UnidadeOrganizacional unidadeOrganizacional, String filtro) {
        Query q = em.createQuery(" from TipoSextaParte tsp " +
            "where (str(tsp.codigo) like :filtro or lower(tsp.descricao) like :filtro)" +
            " and (exists (select 1 from TipoSextaParteUnidadeOrganizacional tspuo " +
            "             where tspuo.tipoSextaParte = tsp" +
            "               and tspuo.unidadeOrganizacional = :unidadeOrganizacional)" +
            "       or tsp not in (select tipoUn.tipoSextaParte from TipoSextaParteUnidadeOrganizacional tipoUn " +
            "                       where tipoUn.tipoSextaParte = tsp)) "
            + " order by tsp.codigo ");
        q.setParameter("unidadeOrganizacional", unidadeOrganizacional);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoFPFacade getEventoFPFacade() {
        return eventoFPFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public TipoSextaParte recuperar(Object id) {
        TipoSextaParte tipoSextaParte = super.recuperar(id);
        Hibernate.initialize(tipoSextaParte.getPeriodosExcludente());
        Hibernate.initialize(tipoSextaParte.getUnidadesOganizacional());
        return tipoSextaParte;
    }

    public List<PeriodoExcludenteDTO> buscarPeriodoExcludente(VinculoFP vinculoFP, TipoSextaParte tipoSextaParte) {
        if (vinculoFP == null || tipoSextaParte == null) {
            return Lists.newArrayList();
        }
        List<PeriodoExcludenteDTO> periodoExcludenteList = new LinkedList<>();
        tipoSextaParte = recuperar(tipoSextaParte.getId());

        Date vinculoInicioVigencia = vinculoFP.getInicioVigencia();
        Date vinculoFimVigencia = vinculoFP.getFinalVigencia() != null ? vinculoFP.getFinalVigencia() : new Date();

        for (TipoSextaPartePeriodoExcludente tipoSextaPartePeriodoExcludente : tipoSextaParte.getPeriodosExcludente()) {
            if (!((vinculoInicioVigencia.compareTo(tipoSextaPartePeriodoExcludente.getInicio()) <= 0 &&
                vinculoFimVigencia.compareTo(tipoSextaPartePeriodoExcludente.getFim()) >= 0) ||
                (vinculoInicioVigencia.compareTo(tipoSextaPartePeriodoExcludente.getInicio()) >= 0 &&
                    vinculoInicioVigencia.compareTo(tipoSextaPartePeriodoExcludente.getFim()) <= 0) ||
                (vinculoFimVigencia.compareTo(tipoSextaPartePeriodoExcludente.getInicio()) >= 0 &&
                    vinculoFimVigencia.compareTo(tipoSextaPartePeriodoExcludente.getFim()) <= 0))) {
                continue;
            }

            PeriodoExcludenteDTO periodoExcludenteDTO = new PeriodoExcludenteDTO();
            periodoExcludenteDTO.setInicio(vinculoInicioVigencia.after(tipoSextaPartePeriodoExcludente.getInicio())
                ? vinculoInicioVigencia : tipoSextaPartePeriodoExcludente.getInicio());
            periodoExcludenteDTO.setFim(vinculoFimVigencia.before(tipoSextaPartePeriodoExcludente.getFim())
                ? vinculoFimVigencia : tipoSextaPartePeriodoExcludente.getFim());
            periodoExcludenteDTO.setDias(DataUtil.getDias(periodoExcludenteDTO.getInicio(),
                periodoExcludenteDTO.getFim()));
            periodoExcludenteDTO.setObservacao(tipoSextaPartePeriodoExcludente.getObservacao());
            periodoExcludenteList.add(periodoExcludenteDTO);
        }
        return periodoExcludenteList;
    }
}
