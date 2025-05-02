package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.nfse.domain.UserNfseCadastroEconomico;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class UserNfseCadastroEconomicoFacade extends AbstractFacade<UserNfseCadastroEconomico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public UserNfseCadastroEconomicoFacade() {
        super(UserNfseCadastroEconomico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<UserNfseCadastroEconomico> buscarUserNfseCadastroEconomicoPorUserResponsavel(UsuarioWeb user) {
        String sql = " with recursive recursive_user_ce (id, cadastroeconomico_id, usuarionfse_id) as ( " +
            " select s_user_ce.id, s_user_ce.cadastroeconomico_id, s_user_ce.usuarionfse_id " +
            "    from usernfsecadastroeconomico s_user_ce " +
            " where s_user_ce.usuarioresponsavel_id = :id_usuario_responsavel " +
            " union all " +
            " select user_ce.id, user_ce.cadastroeconomico_id, user_ce.usuarionfse_id " +
            "    from usernfsecadastroeconomico user_ce " +
            "  inner join recursive_user_ce r_user_ce on r_user_ce.usuarionfse_id = user_ce.usuarioresponsavel_id " +
            "                                        and r_user_ce.cadastroeconomico_id = user_ce.cadastroeconomico_id) " +
            " select * from recursive_user_ce ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("id_usuario_responsavel", user.getId());
        List<UserNfseCadastroEconomico> userNfseCadastroEconomicos = Lists.newArrayList();
        if (query.getResultList() != null && !query.getResultList().isEmpty()) {
            for (Object[] o : (List<Object[]>) query.getResultList()) {
                userNfseCadastroEconomicos.add(recuperar(((BigDecimal) o[0]).longValue()));
            }
        }
        return userNfseCadastroEconomicos;
    }
}
