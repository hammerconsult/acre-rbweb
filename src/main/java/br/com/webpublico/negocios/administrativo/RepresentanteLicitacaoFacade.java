package br.com.webpublico.negocios.administrativo;

import br.com.webpublico.entidades.RepresentanteLicitacao;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by carlos on 25/04/17.
 */
@Stateless
public class RepresentanteLicitacaoFacade extends AbstractFacade<RepresentanteLicitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RepresentanteLicitacaoFacade() {
        super(RepresentanteLicitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(RepresentanteLicitacao entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(RepresentanteLicitacao entity) {
        super.salvarNovo(entity);
    }

    public List<RepresentanteLicitacao> buscarRepresentanteLicitacao(String filtro) {
        String sql = "select r.* from RepresentanteLicitacao r where ((lower(r.nome) like :filtro) or (r.cpf like :filtro))";
        Query q = em.createNativeQuery(sql, RepresentanteLicitacao.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public Boolean verificarDuplicidadeCpf(RepresentanteLicitacao p) {

        String hql = "";

        if (p.getId() != null) {
            hql = "from RepresentanteLicitacao p where replace(replace(p.cpf,'.',''),'-','') = :cpf and p != :representante ";
        } else {
            hql = "from RepresentanteLicitacao p where replace(replace(p.cpf,'.',''),'-','') = :cpf ";
        }
        Query q = em.createQuery(hql, RepresentanteLicitacao.class);
        q.setParameter("cpf", p.getCpf().replace(".", "").replaceAll("-", ""));

        if (p.getId() != null) {
            q.setParameter("representante", p);
        }

        return q.getResultList().isEmpty();
    }

    public RepresentanteLicitacao buscarRepresentanteLicitacaoPorCpf(String filtro) {
        RepresentanteLicitacao representante = new RepresentanteLicitacao();
        String sql = " SELECT * FROM representantelicitacao r WHERE replace(replace(r.cpf,'.',''),'-','') = :filtro ";
        Query q = em.createNativeQuery(sql, RepresentanteLicitacao.class);
        q.setParameter("filtro", filtro.replace(".", "").replaceAll("-", ""));
        q.setMaxResults(1);
        try {
            return  (RepresentanteLicitacao) q.getSingleResult();
        } catch (NoResultException e) {
            return representante;
        }
    }

    public RepresentanteLicitacao buscarRepresentanteLicitacaoPorCpfOrNome(String cpf, String nome) {
        String sql = " SELECT * FROM representantelicitacao r " +
            "          WHERE (upper(r.nome) = :nome or replace(replace(r.cpf,'.',''),'-','') = :cpf) ";
        Query q = em.createNativeQuery(sql, RepresentanteLicitacao.class);
        q.setParameter("nome", nome.toUpperCase().trim());
        q.setParameter("cpf", cpf.toUpperCase().trim());
        q.setMaxResults(1);
        try {
            return  (RepresentanteLicitacao) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
