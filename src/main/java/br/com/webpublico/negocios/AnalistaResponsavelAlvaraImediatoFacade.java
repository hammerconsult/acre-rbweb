package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AnalistaResponsavelAlvaraImediato;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class AnalistaResponsavelAlvaraImediatoFacade extends AbstractFacade<AnalistaResponsavelAlvaraImediato> {

    @PersistenceContext
    private EntityManager em;

    public AnalistaResponsavelAlvaraImediatoFacade() {
        super(AnalistaResponsavelAlvaraImediato.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void preSave(AnalistaResponsavelAlvaraImediato analistaResponsavelAlvaraImediato) {
        analistaResponsavelAlvaraImediato.realizarValidacoes();
    }

    public List<AnalistaResponsavelAlvaraImediato> completarAnalistasAtivos(String parte) {
        String sql = " select analista.* " +
            " from analistaresponsavelalvaraimediato analista " +
            "         inner join usuariosistema us on analista.usuariosistema_id = us.id " +
            "         inner join pessoafisica pf on us.pessoafisica_id = pf.id " +
            " where (lower(pf.nome) like :parte or pf.cpf like :parte) " +
            " and analista.ativo = 1";
        Query q = em.createNativeQuery(sql, AnalistaResponsavelAlvaraImediato.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public AnalistaResponsavelAlvaraImediato buscarAnalistaPeloIdUsuarioSistema(Long idAnalista, Long idUsuarioSistema) {
        String sql = "select * from analistaresponsavelalvaraimediato " +
                "  where usuariosistema_id = :idUsuario ";
            if (idAnalista != null) {
                sql += "  and id <> :idAnalista";
            }
        Query q = em.createNativeQuery(sql, AnalistaResponsavelAlvaraImediato.class);
        q.setParameter("idUsuario", idUsuarioSistema);
        if (idAnalista != null) {
            q.setParameter("idAnalista", idAnalista);
        }
        List<AnalistaResponsavelAlvaraImediato> result = q.getResultList();
        if(!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }
}
