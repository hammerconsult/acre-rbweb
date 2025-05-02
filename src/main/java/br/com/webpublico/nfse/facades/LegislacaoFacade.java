package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.Legislacao;
import br.com.webpublico.nfse.domain.dtos.LegislacaoDTO;
import br.com.webpublico.nfse.exceptions.NfseValidacaoException;
import br.com.webpublico.nfse.util.NfseValidacaoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by wellington on 29/08/17.
 */
@Stateless
public class LegislacaoFacade extends AbstractFacade<Legislacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PesquisaGenericaNfseFacade pesquisaGenericaNfseFacade;

    public LegislacaoFacade() {
        super(Legislacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasOrdemRegistrada(Legislacao legislacao) {
        String sql = " select 1 from legislacao " +
            " where legislacao.ordemexibicao =:ordemexibicao ";
        if (legislacao.getId() != null) {
            sql += " and legislacao.id <> :id ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("ordemexibicao", legislacao.getOrdemExibicao());
        if (legislacao.getId() != null) {
            q.setParameter("id", legislacao.getId());
        }
        return !q.getResultList().isEmpty();
    }

    @Override
    public Legislacao recuperar(Object id) {
        Legislacao legislacao = super.recuperar(id);
        if (legislacao.getDetentorArquivoComposicao() != null) {
            if (legislacao.getDetentorArquivoComposicao().getArquivoComposicao() != null) {
                legislacao.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getPartes().size();
            }
        }
        return legislacao;
    }

    public List<Legislacao> buscarLegislacaoParaExibicao() {
        String hql = " select l from Legislacao l " +
            "  inner join l.tipoLegislacao tl " +
            " where l.habilitarExibicao =:verdadeiro " +
            "   and tl.habilitarExibicao =:verdadeiro " +
            " order by tl.ordemExibicao, l.ordemExibicao ";
        Query q = em.createQuery(hql);
        q.setParameter("verdadeiro", true);
        return q.getResultList();
    }

    public Legislacao salvarRetornando(Legislacao legislacao) {
        validarLegislacao(legislacao);
        legislacao = em.merge(legislacao);
        return legislacao;
    }

    private void validarLegislacao(Legislacao legislacao) throws NfseValidacaoException {
        NfseValidacaoUtil.validarCampos(legislacao);
        NfseValidacaoException ve = new NfseValidacaoException();
        if (hasOrdemRegistrada(legislacao)) {
            ve.adicionarMensagem("A ordem de exibição digitada já está registrada.");
        }
        ve.lancarException();
    }

    public List<Legislacao> getAll() {
        return lista();
    }

    public Legislacao getById(Long id) {
        return recuperar(id);
    }


    public Page<LegislacaoDTO> pesquisar(Pageable pageable, String searchFields, String query) throws UnsupportedEncodingException {
        Page<Legislacao> legislacoes = pesquisaGenericaNfseFacade.pesquisar(pageable, "Legislacao", searchFields, query);
        return new PageImpl(Legislacao.toListLegislacaoDTO(legislacoes.getContent()), pageable, legislacoes.getTotalElements());
    }

}
