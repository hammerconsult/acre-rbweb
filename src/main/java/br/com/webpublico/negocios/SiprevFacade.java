package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Siprev;
import br.com.webpublico.enums.Mes;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by William on 12/04/2016.
 */
@Stateless
public class SiprevFacade extends AbstractFacade<Siprev> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AverbacaoTempoServicoFacade averbacaoTempoServicoFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private DeclaracaoPrestacaoContasFacade declaracaoPrestacaoContasFacade;
    @EJB
    private EntidadeFacade entidadeFacade;

    public SiprevFacade() {
        super(Siprev.class);
    }

    public AverbacaoTempoServicoFacade getAverbacaoTempoServicoFacade() {
        return averbacaoTempoServicoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Siprev recuperar(Object id) {
        Siprev siprev = em.find(Siprev.class, id);
        if (siprev.getDetentorArquivoComposicao() != null) {
            if (siprev.getDetentorArquivoComposicao().getArquivosComposicao() != null) {
                siprev.getDetentorArquivoComposicao().getArquivosComposicao().size();
            }
        }
        siprev.getErros().size();
        return siprev;
    }

    public Siprev salvarSiprev(Siprev entity) {
        return em.merge(entity);
    }

    public Siprev recuperarSiprevGeradoExercicioAndAno(Exercicio exercicio, Mes mes) {
        String sql = "select siprev.* from siprev where mes = :mes and exercicio_id = :exercicio";
        Query q = em.createNativeQuery(sql, Siprev.class);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("mes", mes.name());
        if (!q.getResultList().isEmpty()) {
            return (Siprev) q.getResultList().get(0);
        }
        return null;
    }

    public UnidadeGestoraFacade getUnidadeGestoraFacade() {
        return unidadeGestoraFacade;
    }

    public DeclaracaoPrestacaoContasFacade getDeclaracaoPrestacaoContasFacade() {
        return declaracaoPrestacaoContasFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }
}
