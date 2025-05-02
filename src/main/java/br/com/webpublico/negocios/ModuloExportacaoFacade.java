/*
 * Codigo gerado automaticamente em Tue Jan 10 14:33:58 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ModuloExportacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ModuloExportacaoFacade extends AbstractFacade<ModuloExportacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ModuloExportacaoFacade() {
        super(ModuloExportacao.class);
    }

    public boolean existeCodigo(ModuloExportacao moduloExportacao) {
        String hql = " from ModuloExportacao modulo where modulo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", moduloExportacao.getCodigo());

        List<ModuloExportacao> lista = new ArrayList<ModuloExportacao>();
        lista = q.getResultList();

        if (lista.contains(moduloExportacao)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    @Override
    public ModuloExportacao recuperar(Object id) {
        ModuloExportacao me = super.recuperar(id);    //To change body of overridden methods use File | Settings | File Templates.
        me.getGruposExportacoes().size();
        return me;
    }

    public ModuloExportacao recuperaModuloExportacaoPorCodigo(Long codigo) {
        Query q = em.createQuery(" from ModuloExportacao g where g.codigo = :parametro ");
        q.setParameter("parametro", codigo);
        List<ModuloExportacao> modulos = q.getResultList();
        if(modulos != null && modulos.size() > 1){
            throw new ExcecaoNegocioGenerica("Existe mais de um Modulo de Exportação para o codigo "+ codigo);
        }
        if(modulos == null || (modulos!= null &&  modulos.isEmpty()) ){
            throw new ExcecaoNegocioGenerica("Nenhum Módulo de Exportação para o código "+ codigo);
        }
        return modulos.get(0);
    }
}
