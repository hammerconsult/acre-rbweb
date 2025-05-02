/*
 * Codigo gerado automaticamente em Wed May 18 15:16:08 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
public class PoliticaDeEstoqueFacade extends AbstractFacade<PoliticaDeEstoque> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EstoqueFacade estoqueFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PoliticaDeEstoqueFacade() {
        super(PoliticaDeEstoque.class);
    }

    public Boolean jaExistePoliticaCadastradaMaterialUnidade(PoliticaDeEstoque politicaDeEstoque) {
        String hql = "from PoliticaDeEstoque as pe where"
                + " pe.material = :material and"
                + " pe.unidadeOrganizacional = :unidadeOrganizacional";
        Query q = em.createQuery(hql);
        q.setParameter("material", politicaDeEstoque.getMaterial());
        q.setParameter("unidadeOrganizacional", politicaDeEstoque.getUnidadeOrganizacional());
        List<PoliticaDeEstoque> lista = q.getResultList();
        if (lista.contains(politicaDeEstoque)) {
            return Boolean.FALSE;
        } else {
            return !q.getResultList().isEmpty();
        }
    }

    /**
     * Retorna o estoque máximo cadastrado para um determinado material em uma unidade
     *
     * @param material              indica o material no qual o estoque máximo será recuperado
     * @param unidadeOrganizacional indica a unidade organizacional na qual o estoque máximo será recuperado
     * @return null caso não haja estoque máximo cadastrado para o material na unidade.
     */
    public BigDecimal recuperarEstoqueMaximo(Material material, UnidadeOrganizacional unidadeOrganizacional) {
        BigDecimal valor = null;
        String hql = "select pde.estoqueMaximo from PoliticaDeEstoque pde"
                + " where pde.material = :material"
                + " and  pde.unidadeOrganizacional = :unidadeOrganizacional";
        Query q = em.createQuery(hql);
        q.setParameter("material", material);
        q.setParameter("unidadeOrganizacional", unidadeOrganizacional);
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException ex) {
            return valor;
        }
    }

    public PoliticaDeEstoque recuperarPoliticaDeEstoqueComBaseNoMaterialEUnidade(Material material, UnidadeOrganizacional uo) {
        String hql = " from PoliticaDeEstoque pde "
                + "   where pde.material = :material "
                + "     and pde.unidadeOrganizacional = :uo ";

        Query q = em.createQuery(hql);
        q.setParameter("material", material);
        q.setParameter("uo", uo);

        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (PoliticaDeEstoque) q.getSingleResult();
        }
    }

    private void validarPoliticaDeEstoque(Material m, BigDecimal quantidade, PoliticaDeEstoque politicaDeEstoque, BigDecimal quantidadeEmEstoque) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();

        if (politicaDeEstoque != null) {
            if (politicaDeEstoque.getEstoqueMinimo() != null && politicaDeEstoque.getNivelDeNotificacao().equals(PoliticaDeEstoque.TipoNivelNotificacao.ESTOQUE_MINIMO)) {
                if (quantidadeEmEstoque.subtract(quantidade).compareTo(politicaDeEstoque.getEstoqueMinimo()) <= 0) {
                    FacesUtil.addAtencao("O estoque mínimo do item " + m.getDescricao().toUpperCase() + " foi atingido. Estoque mínimo: " + politicaDeEstoque.getEstoqueMinimo());
                }
            }

            if (politicaDeEstoque.getPontoDeReposicao() != null && politicaDeEstoque.getNivelDeNotificacao().equals(PoliticaDeEstoque.TipoNivelNotificacao.PONTO_REPOSICAO)) {
                if (quantidadeEmEstoque.subtract(quantidade).compareTo(politicaDeEstoque.getPontoDeReposicao()) <= 0) {
                    FacesUtil.addAtencao("O ponto de reposição do item " + m.getDescricao().toUpperCase() + " foi atingido. Ponto de reposição: " + politicaDeEstoque.getPontoDeReposicao());
                }
            }

            if (politicaDeEstoque.getSaidaMaxima() != null && quantidade.compareTo(politicaDeEstoque.getSaidaMaxima()) > 0) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A quantidade do item " + m.getDescricao().toUpperCase() + " é maior que a quantidade máxima de saída permitida pela política de estoque. Saída máxima: " + politicaDeEstoque.getSaidaMaxima());
            }

            if (ve.temMensagens()) {
                throw ve;
            }
        }
    }

    public void validarPoliticaDeEstoqueAprovacaoMaterial(ConsolidadorDeEstoque consolidador, Map<Material, BigDecimal> mapaDeItensAprovados, UnidadeOrganizacional uo) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        PoliticaDeEstoque politicaDeEstoque = null;

        for (Map.Entry<Material, BigDecimal> entry : mapaDeItensAprovados.entrySet()) {
            if (politicaDeEstoque == null || !politicaDeEstoque.getMaterial().equals(entry.getKey())) {
                politicaDeEstoque = recuperarPoliticaDeEstoqueComBaseNoMaterialEUnidade(entry.getKey(), uo);
            }

            try {
                validarPoliticaDeEstoque(entry.getKey(), entry.getValue(), politicaDeEstoque, consolidador.getFisicoConsolidado(entry.getKey()));
            } catch (ValidacaoException ex) {
                ve.getMensagens().addAll(ex.getMensagens());
                ve.validou = false;
            }
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void validarPoliticaDeEstoqueSaidaMaterial(ConsolidadorDeEstoque consolidador, Map<LocalEstoqueOrcamentario, Map<Material, BigDecimal>> mapa, Date dataOperacao) {
        ValidacaoException ve = new ValidacaoException();
        PoliticaDeEstoque politicaDeEstoque = null;
        Estoque estoque;

        for (Map.Entry<LocalEstoqueOrcamentario, Map<Material, BigDecimal>> localEntry : mapa.entrySet()) {
            for (Map.Entry<Material, BigDecimal> materialEntry : localEntry.getValue().entrySet()) {
                if (politicaDeEstoque == null
                        || !politicaDeEstoque.getMaterial().equals(materialEntry.getKey())
                        || !politicaDeEstoque.getUnidadeOrganizacional().equals(localEntry.getKey().getLocalEstoque().getUnidadeOrganizacional())) {
                    politicaDeEstoque = recuperarPoliticaDeEstoqueComBaseNoMaterialEUnidade(materialEntry.getKey(), localEntry.getKey().getLocalEstoque().getUnidadeOrganizacional());
                }

                try {
                    estoque = consolidador != null ? consolidador.getEstoque(materialEntry.getKey(), localEntry.getKey()) : estoqueFacade.recuperarEstoque(localEntry.getKey(), materialEntry.getKey(), dataOperacao);

                    if (estoque != null) {
                        validarPoliticaDeEstoque(materialEntry.getKey(), materialEntry.getValue(), politicaDeEstoque, estoque.getFisico());
                    }
                } catch (ValidacaoException ex) {
                    ve.getMensagens().addAll(ex.getMensagens());
                } catch (OperacaoEstoqueException opex) {
                    ve.adicionarMensagemError(SummaryMessages.OPERACAO_REALIZADA, opex.getMessage());
                }
            }
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }
}
