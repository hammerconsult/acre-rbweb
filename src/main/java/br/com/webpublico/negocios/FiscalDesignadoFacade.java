package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FiscalDesignado;
import br.com.webpublico.entidades.UsuarioSistema;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class FiscalDesignadoFacade extends AbstractFacade<FiscalDesignado> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FiscalDesignadoFacade() {
        super(FiscalDesignado.class);
    }

    public List<UsuarioSistema> buscarFiltrandoFiscaisDesignadosPorNome(String parte) {
        String sql = " SELECT DISTINCT usu.* " +
            " FROM FiscalDesignado f " +
            "  INNER JOIN USUARIOSISTEMA usu ON usu.id = f.usuariosistema_id " +
            "  INNER JOIN PESSOAFISICA pf ON pf.id = usu.pessoafisica_id " +
            " WHERE lower(pf.NOME) LIKE :parte ";
        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<UsuarioSistema> completarFiscalDesignado(String filtro) {
        String sql = " select distinct us.* " +
            "   from fiscaldesignado fd " +
            "  inner join usuariosistema us on us.id = fd.usuariosistema_id " +
            "  left join pessoafisica pf on us.pessoafisica_id = pf.id " +
            "where trim(lower(us.login)) like :parte or " +
            "      trim(lower(pf.cpf)) like :parte or " +
            "      trim(lower(pf.nome)) like :parte ";
        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("parte", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }
}
