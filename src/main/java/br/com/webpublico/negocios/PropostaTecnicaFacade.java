package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 01/09/14
 * Time: 18:15
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class PropostaTecnicaFacade extends AbstractFacade<PropostaTecnica> {

    private static final Logger logger = LoggerFactory.getLogger(PropostaTecnicaFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private MapaComparativoTecnicaPrecoFacade tecnicaPrecoFacade;

    public PropostaTecnicaFacade() {
        super(PropostaTecnica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public SolicitacaoMaterialFacade getSolicitacaoMaterialFacade() {
        return solicitacaoMaterialFacade;
    }

    public MapaComparativoTecnicaPrecoFacade getTecnicaPrecoFacade() {
        return tecnicaPrecoFacade;
    }

    @Override
    public PropostaTecnica recuperar(Object id) {
        PropostaTecnica pt = super.recuperar(id);
        pt.getItens().size();

        if (pt.getItens() != null && !pt.getItens().isEmpty()) {
            for (ItemPropostaTecnica itemPropostaTecnica : pt.getItens()) {
                itemPropostaTecnica.getItemCriterioTecnica().getPontos().size();
            }
        }
        pt.ordenarListas();
        return pt;
    }

    public void salvarPropostaEFornecedorClassificado(PropostaTecnica entity, LicitacaoFornecedor lf) {
        try {
            getEntityManager().merge(entity);
            getEntityManager().merge(lf);
        } catch (Exception ex) {
            logger.error("Problema ao salvar", ex);
        }
    }

    public void inabilitarFornecedor(LicitacaoFornecedor fornecedor) {
        try {
            fornecedor.setClassificacaoTecnica(TipoClassificacaoFornecedor.INABILITADO);
            getEntityManager().merge(fornecedor);
        } catch (Exception ex) {
            logger.error("Problema ao excluir", ex);
        }
    }

    public boolean fornecedorJaFezPropostaTecnica(Pessoa fornecedor, Licitacao licitacao) {
        return em.createNativeQuery("SELECT * FROM PROPOSTATECNICA WHERE FORNECEDOR_ID = :fo AND LICITACAO_ID = :li").setParameter("fo", fornecedor.getId()).setParameter("li", licitacao.getId()).getResultList().isEmpty();
    }

    public PropostaTecnica recuperarPropostaDoFornecedorPorLicitacao(Pessoa fornecedor, Licitacao licitacao) {
        return (PropostaTecnica) em.createNativeQuery("SELECT * FROM PROPOSTATECNICA WHERE FORNECEDOR_ID = :fo AND LICITACAO_ID = :li", PropostaTecnica.class).setParameter("fo", fornecedor.getId()).setParameter("li", licitacao.getId()).getSingleResult();
    }

    public BigDecimal recuperarNotaTecnicaDoFornecedor(Pessoa fornecedor, Licitacao licitacao) {
        String hql = "  select pt "
            + "       from PropostaTecnica pt "
            + " inner join pt.licitacao lic "
            + "      where pt.fornecedor = :fornecedor "
            + "        and lic = :licitacao ";

        Query q = em.createQuery(hql);
        q.setParameter("fornecedor", fornecedor);
        q.setParameter("licitacao", licitacao);

        try {
            PropostaTecnica pt = (PropostaTecnica) q.getSingleResult();
            return pt.getNotaTecnica();

        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public List<PropostaTecnica> buscarPorLicitacao(Licitacao licitacao) {
        List<PropostaTecnica> propostas = Lists.newArrayList();
        propostas = em.createQuery("from PropostaTecnica where licitacao = :lic ").setParameter("lic", licitacao).getResultList();
        for (PropostaTecnica proposta : propostas) {
            proposta.getItens().size();
        }
        return propostas;
    }
}
