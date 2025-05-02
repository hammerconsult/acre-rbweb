package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Feira;
import br.com.webpublico.entidades.Feirante;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class FeiraFacade  extends AbstractFacade<Feira> {

    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private FeiranteFacade feiranteFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public FeiraFacade() {
        super(Feira.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(Feira entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(Feira.class, "codigo"));
        }
        super.salvarNovo(entity);
    }

    public List<Feira> buscarFeira(String parte) {
        String sql = " SELECT F.* FROM FEIRA F " +
            "      WHERE F.NOME LIKE :param " +
            "      or to_char(F.NOME) LIKE :param ";
        Query q = em.createNativeQuery(sql, Feira.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public FeiranteFacade getFeiranteFacade() {
        return feiranteFacade;
    }
}
