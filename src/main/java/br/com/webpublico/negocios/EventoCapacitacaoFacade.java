package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Capacitacao;
import br.com.webpublico.entidades.ModuloCapacitacao;
import br.com.webpublico.enums.SituacaoCapacitacao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

/**
 * Created by AndreGustavo on 17/10/2014.
 */
@Stateless
public class EventoCapacitacaoFacade extends AbstractFacade<Capacitacao> {
    @EJB
    private HabilidadeFacade habilidadeFacade;

    @EJB
    private MetodoCientificoFacade metodoCientificoFacade;

    @EJB
    private MetodologiaFacade metodologiaFacade;

    @EJB
    private AreaFormacaoFacade areaFormacaoFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public EventoCapacitacaoFacade() {
        super(Capacitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Capacitacao recuperar(Object id) {
        Capacitacao capacitacao = super.recuperar(id);
        if (capacitacao != null) {
            capacitacao.getModulos().size();
            capacitacao.getHabilidades().size();
            capacitacao.getMetodoCientifCapacitacaos().size();
            Collections.sort(capacitacao.getInscricoes());

            for (ModuloCapacitacao obj : capacitacao.getModulos()) {
                obj.getInstrutores().size();
                obj.getPresencas().size();
                obj.getMetodologiaModulos().size();
            }
        }

        return capacitacao;
    }

    public List<Capacitacao> listaEventosCapacitacao(String parte) {
        StringBuilder hql = new StringBuilder(" select evento from Capacitacao evento ");
        hql.append(" where lower(trim(evento.nome)) like :nomeEvento ");
        Query q = em.createQuery(hql.toString());
        q.setParameter("nomeEvento", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Capacitacao> buscarEventosCapacitacaoPorSituacaoEmAndamento(String parte, SituacaoCapacitacao situacaoCapacitacao) {
        StringBuilder hql = new StringBuilder(" select evento from Capacitacao evento ");
        hql.append(" where lower(trim(evento.nome)) like :nomeEvento and evento.situacao = :situacaoCapacitacao");
        Query q = em.createQuery(hql.toString());
        q.setParameter("nomeEvento", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("situacaoCapacitacao", situacaoCapacitacao);
        return q.getResultList();
    }

    public List<ModuloCapacitacao> listaModuloCapacitacao(String parte, Capacitacao capacitacao) {
        String sql = " SELECT MC.* FROM MODULOCAPACITACAO MC "
                + " INNER JOIN CAPACITACAO C ON C.ID = MC.CAPACITACAO_ID "
                + " WHERE C.ID = :capacitacao_id "
                + " AND MC.NOMEMODULO LIKE :nomeModulo";
        Query q = em.createNativeQuery(sql, ModuloCapacitacao.class);
        q.setParameter("nomeModulo", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("capacitacao_id", capacitacao.getId());

        return q.getResultList();
    }

    public HabilidadeFacade getHabilidadeFacade() {
        return habilidadeFacade;
    }

    public AreaFormacaoFacade getAreaFormacaoFacade() {
        return areaFormacaoFacade;
    }

    public MetodoCientificoFacade getMetodoCientificoFacade() {
        return metodoCientificoFacade;
    }

    public MetodologiaFacade getMetodologiaFacade() {
        return metodologiaFacade;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }
}
