/*
 * Codigo gerado automaticamente em Fri Nov 11 10:09:29 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.VeiculoDePublicacao;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class VeiculoDePublicacaoFacade extends AbstractFacade<VeiculoDePublicacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VeiculoDePublicacaoFacade() {
        super(VeiculoDePublicacao.class);
    }

    @Override
    public VeiculoDePublicacao recuperar(Object id) {
        VeiculoDePublicacao v = em.find(VeiculoDePublicacao.class, id);
        v.getListaContrato().size();
        return v;
    }

    public List<VeiculoDePublicacao> buscarVeiculosPorNome(String nomeVeiculo) {
        if (nomeVeiculo == null || nomeVeiculo.isEmpty()) return Lists.newArrayList();
        String sql = " select vdp.* " +
            " from veiculodepublicacao vdp " +
            " where lower(vdp.nome) like :nomeVeiculo " +
            " order by vdp.id desc ";
        Query q = em.createNativeQuery(sql, VeiculoDePublicacao.class);
        q.setParameter("nomeVeiculo", "%" + nomeVeiculo.toLowerCase().trim() + "%");
        return q.getResultList();
    }
}
