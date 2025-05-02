/*
 * Codigo gerado automaticamente em Mon Dec 12 14:41:06 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Cotacao;
import br.com.webpublico.entidades.UnidadeExterna;
import br.com.webpublico.enums.SituacaoCadastralPessoa;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class UnidadeExternaFacade extends AbstractFacade<UnidadeExterna> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UnidadeExternaFacade() {
        super(UnidadeExterna.class);
    }

    public boolean existeUnidadeExterna(UnidadeExterna unidade) {
        Query q = em.createQuery(" from UnidadeExterna un where un.pessoaJuridica = :pessoa"
                + " and un.esferaGoverno = :esfera");
        q.setParameter("esfera", unidade.getEsferaGoverno());
        q.setParameter("pessoa", unidade.getPessoaJuridica());

        List<UnidadeExterna> lista = q.getResultList();

        if (lista.contains(unidade)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public List<UnidadeExterna> listaFiltrandoPessoaJuridicaEsferaGoverno(String s) {
        try {
            Query q = em.createQuery(" select unidade from UnidadeExterna unidade "
                    + " inner join unidade.pessoaJuridica pessoa "
                    + " inner join unidade.esferaGoverno esfera "
                    + " where lower(pessoa.razaoSocial) like :parametro "
                    + " or lower(esfera.nome) like :parametro ");
            q.setParameter("parametro", "%" + s.toLowerCase() + "%");
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<UnidadeExterna> listaFiltrandoPessoaJuridicaAtivaEsferaGoverno(String s) {
        try {
            Query q = em.createQuery(" select unidade from UnidadeExterna unidade "
                + " inner join unidade.pessoaJuridica pessoa "
                + " inner join unidade.esferaGoverno esfera "
                + " where (lower(pessoa.razaoSocial) like :parametro "
                + " or lower(esfera.nome) like :parametro) "
                + " and pessoa.situacaoCadastralPessoa = :ativo ");
            q.setParameter("parametro", "%" + s.toLowerCase() + "%");
            q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<UnidadeExterna> listaUnidadeExternaDiferenteDeEsferaGovernoPrivada(String filtro) {
        try {
            String sql = "" +
                " select unidade.* from UNIDADEEXTERNA unidade " +
                " inner join pessoaJuridica pessoa on unidade.PESSOAJURIDICA_ID = pessoa.id " +
                " inner join ESFERAGOVERNO esfera on unidade.ESFERAGOVERNO_ID = esfera.id " +
                " where lower(esfera.nome) in ('municipal', 'estadual', 'federal') " +
                "       and (lower(pessoa.RAZAOSOCIAL) like :parametro " +
                "       or lower(esfera.nome) like :parametro)";
            Query q = em.createNativeQuery(sql, UnidadeExterna.class);
            q.setParameter("parametro", "%" + filtro.toLowerCase().trim() + "%");
            return q.getResultList();

        } catch (Exception e) {
            return null;
        }
    }
}
