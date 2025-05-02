/*
 * Codigo gerado automaticamente em Tue Jan 10 14:40:47 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrupoExportacao;
import br.com.webpublico.entidades.ModuloExportacao;
import br.com.webpublico.enums.SefipNomeReduzido;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class GrupoExportacaoFacade extends AbstractFacade<GrupoExportacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoExportacaoFacade() {
        super(GrupoExportacao.class);
    }

    @Override
    public GrupoExportacao recuperar(Object id) {
        GrupoExportacao grupo = em.find(GrupoExportacao.class, id);
        grupo.getItensGruposExportacoes().size();
        grupo.getItensGrupoExportacaoContabil().size();
        return grupo;
    }

    public GrupoExportacao recuperaGrupoExportacaoPorCodigo(Long codigo) {
        Query q = em.createQuery(" from GrupoExportacao g where g.codigo = :parametro ");
        q.setMaxResults(1);
        q.setParameter("parametro", codigo);

        if (!q.getResultList().isEmpty()) {
            GrupoExportacao grupoExportacao = (GrupoExportacao) q.getSingleResult();
            return recuperar(grupoExportacao.getId());
        }
        return null;
    }

    public GrupoExportacao recuperaGrupoExportacaoPorCodigoEModuloExportacao(Long codigo, ModuloExportacao modulo) {
        Query q = em.createQuery(" from GrupoExportacao g where g.codigo = :parametro and g.moduloExportacao = :modulo");
        q.setParameter("parametro", codigo);
        q.setParameter("modulo", modulo);
        List<GrupoExportacao> grupos = q.getResultList();
        if (grupos != null && grupos.size() > 1) {
            throw new ExcecaoNegocioGenerica("Existe mais de um Grupo de Exportação para o codigo "+ codigo);
        }
        if(grupos== null || (grupos!= null &&  grupos.isEmpty()) ){
            throw new ExcecaoNegocioGenerica("Nenhum Grupo de Exportação para o código "+ codigo);
        }
        return grupos.get(0);
    }

    public GrupoExportacao recuperaGrupoExportacaoPorNomeReduzido(SefipNomeReduzido sefipNomeReduzido) {
        Query q = em.createQuery(" from GrupoExportacao g where g.nomeReduzido = :parametro ");
        q.setMaxResults(1);
        q.setParameter("parametro", sefipNomeReduzido.name());

        if (!q.getResultList().isEmpty()) {
            GrupoExportacao grupoExportacao = (GrupoExportacao) q.getSingleResult();
            return recuperar(grupoExportacao.getId());
        }
        return null;
    }
}
