package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Feira;
import br.com.webpublico.entidades.Feirante;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class FeiranteFacade extends AbstractFacade<Feirante> {

    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private FeiraFacade feiraFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public FeiranteFacade() {
        super(Feirante.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Feirante recuperar(Object id) {
        Feirante entity = super.recuperar(id);
        Hibernate.initialize(entity.getFeiras());
        return entity;
    }

    @Override
    public void salvarNovo(Feirante entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(Feirante.class, "codigo"));
        }
        super.salvarNovo(entity);
    }

    public List<Feirante> buscarFeirante(Feira feira, String parte) {
        String sql = " select fr.* from feirante fr " +
            "           inner join feirantefeira ff on ff.feirante_id = fr.id " +
            "           inner join pessoafisica pf on pf.id = fr.pessoafisica_id " +
            "           where ff.feira_id = :idFeira " +
            "               and (lower(pf.nome) like :parte " +
            "                   or replace(replace(pf.cpf,'.',''),'-','') like :parte " +
            "                   or pf.cpf like :parte) ";
        Query q = em.createNativeQuery(sql, Feirante.class);
        q.setParameter("idFeira", feira.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public FeiraFacade getFeiraFacade() {
        return feiraFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }
}
