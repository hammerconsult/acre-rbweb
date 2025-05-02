package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoAnexoLicitacaoTipoDocumento;
import br.com.webpublico.entidades.TipoDocumentoAnexo;
import br.com.webpublico.enums.TipoDocumentoAnexoPNCP;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by hudson on 23/11/15.
 */

@Stateless
public class TipoDocumentoAnexoFacade extends AbstractFacade<TipoDocumentoAnexo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public TipoDocumentoAnexoFacade() {
        super(TipoDocumentoAnexo.class);
    }

    public List<TipoDocumentoAnexo> buscarTodosTipoDocumentosAnexo() {
        String sql = " select tipo.* from tipoDocumentoAnexo tipo where ativo = 1";
        Query q = em.createNativeQuery(sql, TipoDocumentoAnexo.class);
        try {
            return q.getResultList();
        }catch (NoResultException nre){
            return Lists.newArrayList();
        }
    }

    public boolean verificarAnexoComMesmaDescricao(TipoDocumentoAnexo tipoDocumentoAnexo) {
        String sql = " select t.* from tipoDocumentoAnexo t "
            + "                 where lower(trim(t.descricao)) = lower(trim(:descricao)) and "
            + "                       lower(trim(t.tipoAnexoPNCP)) = lower(trim(:tipoDocumentoAnexo)) ";
        sql += tipoDocumentoAnexo != null && tipoDocumentoAnexo.getId() != null? "   and t.id <> :id " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("descricao", tipoDocumentoAnexo.getDescricao().trim().toLowerCase());
        q.setParameter("tipoDocumentoAnexo", tipoDocumentoAnexo.getTipoAnexoPNCP().name());
        if (tipoDocumentoAnexo != null && tipoDocumentoAnexo.getId() != null) {
            q.setParameter("id", tipoDocumentoAnexo.getId());
        }
        return !q.getResultList().isEmpty();
    }

}
