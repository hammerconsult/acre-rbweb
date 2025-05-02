package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CondicaoDeOcupacao;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: MGA
 * Date: 02/09/14
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class CondicaoDeOcupacaoFacade extends AbstractFacade<CondicaoDeOcupacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CondicaoDeOcupacaoFacade() {
        super(CondicaoDeOcupacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CondicaoDeOcupacao recuperarPelaDescricao(String descricao) {
        try {
            return (CondicaoDeOcupacao) em.createNativeQuery("select * from condicaodeocupacao where lower(descricao) = :descricao", CondicaoDeOcupacao.class).setParameter("descricao", descricao.toLowerCase()).getSingleResult();
        } catch (NoResultException ex) {
            throw new ExcecaoNegocioGenerica("Não existe condição de ocupação com a descrição " + descricao + ".");
        }
    }

    private void validarRegrasDeNegocio(CondicaoDeOcupacao condicao) {
        ValidacaoException ve = new ValidacaoException();

        try {
            recuperarPelaDescricao(condicao.getDescricao());
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Já existe uma condição de ocupação com a descrição informada.");
        } catch (ExcecaoNegocioGenerica ex) {
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @Override
    public void salvar(CondicaoDeOcupacao condicao) {
        validarRegrasDeNegocio(condicao);
        super.salvar(condicao);
    }

    @Override
    public void salvarNovo(CondicaoDeOcupacao condicao) {
        validarRegrasDeNegocio(condicao);
        super.salvarNovo(condicao);
    }
}
