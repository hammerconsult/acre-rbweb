/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoDocumentoFiscal;
import br.com.webpublico.enums.Situacao;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author claudio
 */
@Stateless
public class TipoDocumentoFiscalFacade extends AbstractFacade<TipoDocumentoFiscal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TipoDocumentoFiscalFacade() {
        super(TipoDocumentoFiscal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(TipoDocumentoFiscal entity) {
        em.merge(entity);
    }

    public List<TipoDocumentoFiscal> buscarTiposDeDocumentosAtivos(String parte){
        String sql = " select obj.* from tipodocumentofiscal obj " +
            " where (upper(obj.codigo) like :parte or upper(obj.descricao) like :parte) " +
            " and obj.situacao = :ativo ";
        Query q = em.createNativeQuery(sql, TipoDocumentoFiscal.class);
        q.setParameter("parte", "%" + parte.toUpperCase() + "%");
        q.setParameter("ativo", Situacao.ATIVO.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        List<TipoDocumentoFiscal> resultado = q.getResultList();
        if (!resultado.isEmpty()){
            return resultado;
        }
        return Lists.newArrayList();

    }
}
