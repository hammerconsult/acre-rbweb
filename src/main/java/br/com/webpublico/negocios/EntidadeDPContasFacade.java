/*
 * Codigo gerado automaticamente em Fri Sep 02 14:51:15 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EntidadeDPContas;
import br.com.webpublico.entidades.ItemEntidadeDPContas;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Stateless
public class EntidadeDPContasFacade extends AbstractFacade<EntidadeDPContas> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public EntidadeDPContasFacade() {
        super(EntidadeDPContas.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public EntidadeDPContas recuperar(Object id) {
        EntidadeDPContas entidade = em.find(EntidadeDPContas.class, id);
        entidade.getItensEntidaDPContas().size();
        Collections.sort(entidade.getItensEntidaDPContas());
        for (ItemEntidadeDPContas iec : entidade.getItensEntidaDPContas()) {
            iec.getItemEntidadeUnidadeOrganizacional().size();
            Collections.sort(iec.getItemEntidadeUnidadeOrganizacional());
        }

        return entidade;
    }

    public EntidadeDPContas recuperarEntidadeDPContasVigenteParaCategoria(CategoriaDeclaracaoPrestacaoContas cat, Date dataOperacao) {
        String hql = " select e from EntidadeDPContas e" +
            "  inner join e.declaracaoPrestacaoContas dpc" +
            "  where dpc.categoriaDeclaracaoPrestacaoContas = :cat" +
            "    and :dataOperacao between e.inicioVigencia and coalesce(e.finalVigencia, :dataOperacao)";
        Query q = em.createQuery(hql);
        q.setParameter("cat", cat);
        q.setParameter("dataOperacao", dataOperacao, TemporalType.DATE);
        q.setMaxResults(1);
        try {
            return (EntidadeDPContas) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<ItemEntidadeDPContas> buscarEntidadesConfiguracaoEntidadeDPContasVigentePorCategoria(CategoriaDeclaracaoPrestacaoContas categoria, Date dataOperacao, String parte) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select item.* from ENTIDADEDPCONTAS entidadedp ");
        sql.append(" inner join declaracaoPrestacaoContas declaracao on entidadedp.DECLARACAOPRESTACAOCONTAS_ID = declaracao.id ");
        sql.append(" inner join ItemEntidadeDPContas item on item.ENTIDADEDPCONTAS_ID = entidadedp.id ");
        sql.append(" inner join entidade entidade on item.ENTIDADE_ID = entidade.id ");
        sql.append(" where declaracao.CATEGORIADECLARACAO  = :categoria ");
        sql.append(" and :dataOperacao between entidadedp.INICIOVIGENCIA and coalesce(entidadedp.FINALVIGENCIA, :dataOperacao) ");
        sql.append(" and lower(entidade.nome) like :filtro  ");
        Query q = em.createNativeQuery(sql.toString(), ItemEntidadeDPContas.class);
        q.setParameter("categoria", categoria.name());
        q.setParameter("dataOperacao", dataOperacao);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public List<ItemEntidadeDPContas> recuperarItensEntidadeDPContasVigenteParaCategoria(CategoriaDeclaracaoPrestacaoContas cat, Date dataOperacao) {
        String hql = " select item from ItemEntidadeDPContas item " +
            " inner join item.entidadeDPContas e" +
            "  inner join e.declaracaoPrestacaoContas dpc" +
            "  where dpc.categoriaDeclaracaoPrestacaoContas = :cat" +
            "    and :dataOperacao between e.inicioVigencia and coalesce(e.finalVigencia, :dataOperacao)";
        Query q = em.createQuery(hql);
        q.setParameter("cat", cat);
        q.setParameter("dataOperacao", dataOperacao, TemporalType.DATE);

        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (List<ItemEntidadeDPContas>) resultList;
        }
        return Lists.newArrayList();
    }
}
