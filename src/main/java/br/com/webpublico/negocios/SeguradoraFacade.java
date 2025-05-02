package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Seguradora;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 02/01/14
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SeguradoraFacade extends AbstractFacade<Seguradora> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SeguradoraFacade() {
        super(Seguradora.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Seguradora> buscarSeguradoras(String partes) {
        String sql = "select seg.* " +
                "     from Seguradora seg " +
                "     inner join pessoa p on p.id = seg.pessoa_id " +
                "     left join pessoafisica pf on pf.id = p.id " +
                "     left join pessoajuridica pj on pj.id = p.id " +
                "     where (lower(pf.NOME) like :partes  " +
                "            OR lower(pj.RAZAOSOCIAL) like :partes " +
                "            OR replace(replace(pf.CPF,'.',''),'-','') like :partes " +
                "            OR replace(replace(replace(pj.CNPJ,'.',''),'-',''),'/','') like :partes " +
                "           )";

        Query q = em.createNativeQuery(sql, Seguradora.class);
        return q.getResultList();
    }
}

