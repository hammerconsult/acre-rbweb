package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Compromissario;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HardRock on 12/04/2017.
 */
@Stateless
public class CompromissarioFacade extends AbstractFacade<Compromissario> {

    @PersistenceContext(unitName = "webpublicoPU")
    protected EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CompromissarioFacade() {
        super(Compromissario.class);
    }

    public List<Compromissario> buscarFiltrandoCompromissarioPorNome(String parte) {
        String sql = " " +
            "    select * from compromissario c " +
            "  INNER JOIN PESSOA p ON p.id = c.COMPROMISSARIO_ID " +
            "  LEFT JOIN PESSOAFISICA pf ON pf.id = p.ID " +
            "  LEFT JOIN PESSOAJURIDICA pj ON pj.id = p.id " +
            "WHERE (replace(replace(pf.cpf, '.', ''), '-', '') like :parte " +
            "       OR upper(pf.NOME) like :parte " +
            "       OR replace(replace(replace(pj.cnpj, '.', ''), '-', ''), '/', '') like :parte " +
            "       OR upper(pj.RAZAOSOCIAL) like :parte ) ";
        Query q = em.createNativeQuery(sql, Compromissario.class);
        q.setParameter("parte", "%" + parte.toUpperCase().trim() + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }


}
