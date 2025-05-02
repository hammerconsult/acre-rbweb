package br.com.webpublico.seguranca.service;

import br.com.webpublico.entidades.UsuarioNodeServer;
import br.com.webpublico.entidades.UsuarioSistema;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by André on 27/01/2015.
 */
@Service
public class UsuarioNodeServerService extends AbstractCadastroService<UsuarioNodeServer> {
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void salvaUsuarioNodeServer(UsuarioNodeServer uns) {
        getEntityManager().merge(uns);
    }

    public UsuarioNodeServer recuperaUsuarioNode(UsuarioSistema usuario) {
        String hql = "from UsuarioNodeServer uns where uns.usuario = :usuario";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("usuario", usuario);

        List<UsuarioNodeServer> toReturn = q.getResultList();
        if (!toReturn.isEmpty()) {
            return toReturn.get(0);
        }
        return null;
    }

}
