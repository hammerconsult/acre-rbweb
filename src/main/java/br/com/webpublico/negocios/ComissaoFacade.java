/*
 * Codigo gerado automaticamente em Wed Nov 09 08:54:07 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AtoDeComissao;
import br.com.webpublico.entidades.Comissao;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.MembroComissao;
import br.com.webpublico.enums.AtribuicaoComissao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ComissaoFacade extends AbstractFacade<Comissao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public ComissaoFacade() {
        super(Comissao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Comissao recuperar(Object id) {
        Comissao comissao = super.recuperar(id);
        comissao.getMembroComissao().size();
        return comissao;
    }

    public int buscarProximoCodigoDeComissao() {
        Query q = em.createQuery("select max(codigo) from " + Comissao.class.getSimpleName());
        q.setMaxResults(1);
        if (q.getSingleResult() == null) {
            return 1;
        } else {
            return (Integer) q.getSingleResult() + 1;
        }

    }

    public boolean existeCodigoDeComissaoRepetido(int cod) {
        Query query = em.createQuery("select codigo from " + Comissao.class.getSimpleName() + " where codigo = :cod");
        query.setParameter("cod", cod);
        try {
            if (query.getSingleResult() != null) {
                return true;
            }
        } catch (NoResultException e) {
            return false;
        }

        return false;
    }

    public List<Comissao> recuperaComissoesPorAtoLegal(AtoDeComissao atoDeComissao) {
        Query query = em.createQuery("from " + Comissao.class.getSimpleName() + " a where a.atoDeComissao= :atoDeComissaoParam");
        query.setParameter("atoDeComissaoParam", atoDeComissao);
        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            return new ArrayList<>();
        }
    }

    public List<MembroComissao> recuperarMembroComissaoAPartirDaLicitacao(Licitacao licitacao) {
        String sql = " SELECT "
            + "  MEM.* "
            + "FROM LICITACAO LIC "
            + "INNER JOIN COMISSAO COM ON COM.ID = LIC.COMISSAO_ID "
            + "INNER JOIN MEMBROCOMISSAO MEM ON MEM.COMISSAO_ID = COM.ID "
            + "WHERE LIC.ID = :idLicitacao "
            + "AND (MEM.ATRIBUICAOCOMISSAO = '" + AtribuicaoComissao.PRESIDENTE_PREGOEIRO.name() + "' "
            + "OR MEM.ATRIBUICAOCOMISSAO = '" + AtribuicaoComissao.MEMBRO_PREGOEIRO.name() + "' "
            + "OR MEM.ATRIBUICAOCOMISSAO = '" + AtribuicaoComissao.PREGOEIRO.name() + "' ) ";

        Query q = em.createNativeQuery(sql, MembroComissao.class);
        q.setParameter("idLicitacao", licitacao.getId());

        if (q.getResultList() != null || !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<Comissao> buscarComissaoVigente(String filtro) {
        String sql = " select * " +
            " from Comissao " +
            " where trunc(:dataAtual) between trunc(iniciovigencia) and coalesce(trunc(fimvigencia), trunc(:dataAtual)) " +
            "  and (lower(titulo) like :filtro or cast(codigo as varchar(20)) like :filtro) order by codigo";
        Query q = em.createNativeQuery(sql, Comissao.class);
        q.setParameter("dataAtual", sistemaFacade.getDataOperacao());
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        return q.getResultList();
    }
}
