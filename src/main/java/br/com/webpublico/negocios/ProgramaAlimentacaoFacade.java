package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ProgramaAlimentacao;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ProgramaAlimentacaoFacade extends AbstractFacade<ProgramaAlimentacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ConvenioReceitaFacade convenioReceitaFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ProgramaAlimentacaoFacade() {
        super(ProgramaAlimentacao.class);
    }

    @Override
    public ProgramaAlimentacao recuperar(Object id) {
        ProgramaAlimentacao entity = super.recuperar(id);
        Hibernate.initialize(entity.getLocaisEstoque());
        return entity;
    }

    @Override
    public void salvarNovo(ProgramaAlimentacao entity) {
        if (entity.getNumero() == null){
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(ProgramaAlimentacao.class, "numero"));
        }
        super.salvarNovo(entity);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConvenioReceitaFacade getConvenioReceitaFacade() {
        return convenioReceitaFacade;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<ProgramaAlimentacao> buscarPorNome(String parte) {
        String sql = " SELECT PROG.* FROM PROGRAMAALIMENTACAO PROG " +
            "      WHERE (LOWER(PROG.NOME) LIKE :param ) " ;
        Query q = em.createNativeQuery(sql, ProgramaAlimentacao.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }
}
