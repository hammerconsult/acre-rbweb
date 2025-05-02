package br.com.webpublico.negocios.administrativo;

import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.RetiradaEdital;
import br.com.webpublico.enums.TipoSituacaoLicitacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LicitacaoFacade;
import br.com.webpublico.negocios.PessoaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by hudson on 02/10/2015.
 */

@Stateless
public class RetiradaEditalFacade extends AbstractFacade<RetiradaEdital> {

    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PessoaFacade pessoaFacade;


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RetiradaEditalFacade() {
        super(RetiradaEdital.class);
    }

    public void salvar(List<RetiradaEdital> retiradaEditalList, List<RetiradaEdital> retiradaEditalListExcluidos) {
        for (RetiradaEdital retiradaEdital : retiradaEditalList) {
            em.merge(retiradaEdital);
        }
        for (RetiradaEdital retiradaEdital : retiradaEditalListExcluidos) {
            em.remove(em.merge(retiradaEdital));
        }
    }

    @Override
    public void remover(RetiradaEdital retiradaEdital) {
        super.remover(retiradaEdital);
    }

    @Override
    public RetiradaEdital recuperar(Object id) {
        RetiradaEdital re = super.recuperar(id);
        return re;
    }

    public List<RetiradaEdital> buscarRetiradaEdital(Licitacao licitacao) {
        String sql = "select re.* from retiradaedital re " +
            " inner join licitacao l on l.id = re.LICITACAO_ID" +
            " where l.id = :idLicitacao";
        Query q = em.createNativeQuery(sql, RetiradaEdital.class);
        q.setParameter("idLicitacao", licitacao.getId());
        return q.getResultList();
    }

    public List<Licitacao> buscarLicitacaoPorStatusEmAndamento(String parte) {
        String sql = "select l.* from licitacao l " +
            "inner join STATUSLICITACAO sl on sl.LICITACAO_ID = l.ID " +
            "where sl.TIPOSITUACAOLICITACAO = :andamento " +
            "AND l.MODALIDADELICITACAO LIKE :parte ";
        Query q = em.createNativeQuery(sql,Licitacao.class);
        q.setParameter("andamento", TipoSituacaoLicitacao.ANDAMENTO.name());
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

}
