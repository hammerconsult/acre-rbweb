/*
 * Codigo gerado automaticamente em Thu Dec 01 17:13:45 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios.tributario;

import br.com.webpublico.entidades.ItemPropostaFornecedor;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.LicitacaoFornecedor;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PregaoFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class HabilitacaoPregaoFacade extends AbstractFacade<Licitacao> {

    private static final Logger logger = LoggerFactory.getLogger(HabilitacaoPregaoFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PregaoFacade pregaoFacade;

    public HabilitacaoPregaoFacade() {
        super(Licitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Licitacao recuperar(Object id) {
        Licitacao l = super.recuperar(id);
        l.getFornecedores().size();
        l.getListaDeStatusLicitacao().size();
        for (LicitacaoFornecedor lf : l.getFornecedores()) {
            lf.getDocumentosFornecedor().size();
        }
        l.getDocumentosProcesso().size();
        return l;
    }

    public PregaoFacade getPregaoFacade() {
        return pregaoFacade;
    }

    public List<LicitacaoFornecedor> recuperarLicitacaoFornecedorDe(List<Pessoa> fornecedores, Licitacao licitacao) {
        String hql = "     select lf from LicitacaoFornecedor  lf" +
            " inner join lf.empresa e" +
            " inner join lf.licitacao l" +
            "      where e in :fornecedores" +
            "        and l = :licitacao";
        Query q = em.createQuery(hql);
        q.setParameter("fornecedores", fornecedores);
        q.setParameter("licitacao", licitacao);
        List<LicitacaoFornecedor> resultado = q.getResultList();
        for (LicitacaoFornecedor lf : resultado) {
            lf.getDocumentosFornecedor().size();
        }
        return resultado;
    }

    public void salvarLicitacaoComItens(Licitacao entity, LicitacaoFornecedor lf) {
        try {
            getEntityManager().merge(entity);
            if (entity.isPregao()) {
                salvarItensDaHabilitacaoDoPregao(lf.getEmpresa().getItensPropostaFornecedor());
            }
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    public void salvarItensDaHabilitacaoDoPregao(List<ItemPropostaFornecedor> ipfs) {
        for (ItemPropostaFornecedor ipf : ipfs) {
            try {
                getEntityManager().merge(ipf.getItemProcessoDeCompra());
                if (ipf.getItemPregao() != null) {
                    getEntityManager().merge(ipf.getItemPregao());
                }
            } catch (Exception ex) {
                logger.error("Problema ao alterar", ex);
            }
        }
    }
}
