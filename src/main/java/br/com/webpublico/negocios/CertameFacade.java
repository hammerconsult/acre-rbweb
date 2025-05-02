/*
 * Codigo gerado automaticamente em Fri Jan 20 10:46:54 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoItemCertame;
import br.com.webpublico.enums.SituacaoItemProcessoDeCompra;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;
import java.util.Collections;

@Stateless
public class CertameFacade extends AbstractFacade<Certame> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PropostaFornecedorFacade propostaFornecedorFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ItemPropostaFornecedorFacade itemPropostaFornecedorFacade;
    @EJB
    private AmparoLegalFacade amparoLegalFacade;

    public CertameFacade() {
        super(Certame.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Certame recuperar(Object id) {
        Certame certame = super.recuperar(id);
        Hibernate.initialize(certame.getListaItemCertame());
        Collections.sort(certame.getListaItemCertame());
        return certame;
    }

    public Certame recuperarCertameDaLicitacao(Licitacao licitacao) {
        Certame certame = (Certame) em.createQuery("from Certame where licitacao = :lic").setParameter("lic", licitacao).getSingleResult();
        certame = this.recuperar(certame.getId());
        return certame;
    }

    @Override
    public void remover(Certame entity) {
        atribuirNullJustificativaAndPrecoMaiorDescontoItemProposta(entity);
        super.remover(entity);
    }

    private void atribuirNullJustificativaAndPrecoMaiorDescontoItemProposta(Certame entity) {
        if (entity.isTipoApuracaoPrecoLote()) {
            entity.getListaItemCertame()
                .stream()
                .map(itemCert -> itemPropostaFornecedorFacade.buscarPorLoteProposta(itemCert.getLotePropostaFornecedorVencedor()))
                .flatMap(Collection::stream)
                .forEach(itemProp -> {
                    itemProp.setJustificativa(null);
                    if (entity.isMaiorDesconto()) {
                        itemProp.setPreco(null);
                    }
                    em.merge(itemProp);
                });
        }
    }

    public Certame salvarRetornando(Certame entity, LotePropostaFornecedor lotePropostaVencedor) {
        definirItemProcessoCompraComoFrustradoParaItemNaoCotado(entity);
        if (lotePropostaVencedor != null) {
            lotePropostaVencedor.setValor(lotePropostaVencedor.getValorTotalItens());
            em.merge(lotePropostaVencedor);
        }
        return super.salvarRetornando(entity);
    }

    public void salvarItemPropostaFornecedor(ItemPropostaFornecedor entity) {
        em.merge(entity);
    }

    public void definirItemProcessoCompraComoFrustradoParaItemNaoCotado(Certame certame) {
        for (ItemCertame itemCertame : certame.getListaItemCertame()) {
            if (SituacaoItemCertame.NAO_COTADO.equals(itemCertame.getSituacaoItemCertame())) {
                if (certame.isTipoApuracaoPrecoItem()) {
                    definirItemCertameItemProcessoComoFrustadoQuandoNaoCotado(itemCertame);

                } else if (certame.isTipoApuracaoPrecoLote()) {
                    definirItemCertameLoteProcessoComoFrustadoQuandoNaoCotado(itemCertame);
                }
            }
        }
    }

    private void definirItemCertameLoteProcessoComoFrustadoQuandoNaoCotado(ItemCertame itemCertame) {
        LoteProcessoDeCompra loteProcessoDeCompra = em.find(LoteProcessoDeCompra.class, itemCertame.getItemCertameLoteProcesso().getLoteProcessoDeCompra().getId());
        for (ItemProcessoDeCompra itemProcessoDeCompra : loteProcessoDeCompra.getItensProcessoDeCompra()) {
            if (isVerificarItemCotadoComSituacaoDiferenteDe(itemProcessoDeCompra, SituacaoItemCertame.NAO_COTADO)) {
                itemProcessoDeCompra.setSituacaoItemProcessoDeCompra(SituacaoItemProcessoDeCompra.FRUSTRADO);
                em.merge(itemProcessoDeCompra);
            }
        }
    }

    private void definirItemCertameItemProcessoComoFrustadoQuandoNaoCotado(ItemCertame itemCertame) {
        if (isVerificarItemCotadoComSituacaoDiferenteDe(itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra(), SituacaoItemCertame.NAO_COTADO)) {
            itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra().setSituacaoItemProcessoDeCompra(SituacaoItemProcessoDeCompra.FRUSTRADO);
            em.merge(itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra());
        }
    }

    public boolean licitacaoTemVinculoEmUmCertame(Licitacao licitacao) {
        String sql = " SELECT * FROM CERTAME WHERE LICITACAO_ID = :lic";

        Query q = em.createNativeQuery(sql, Certame.class);
        q.setParameter("lic", licitacao.getId());

        return q.getResultList() != null && !q.getResultList().isEmpty();
    }

    public boolean isVerificarItemCotadoComSituacaoDiferenteDe(ItemProcessoDeCompra itemProcessoDeCompra, SituacaoItemCertame situacaoItemCertame) {
        String sql = "SELECT ITEMCERT.SITUACAOITEMCERTAME FROM PROPOSTAFORNECEDOR PROPOSTA " +
            "JOIN ITEMPROPFORNEC ITEM ON ITEM.PROPOSTAFORNECEDOR_ID = PROPOSTA.ID " +
            "JOIN LICITACAO LIC ON LIC.ID = PROPOSTA.LICITACAO_ID " +
            "JOIN ITEMCERTAMEITEMPRO ITEMCERTPRO ON ITEMCERTPRO.ITEMPROPOSTAVENCEDOR_ID = ITEM.ID " +
            "JOIN ITEMCERTAME ITEMCERT ON ITEMCERTPRO.ITEMCERTAME_ID = ITEMCERT.ID " +
            "JOIN ITEMPROCESSODECOMPRA ITEMPROCESSO ON ITEMCERTPRO.ITEMPROCESSODECOMPRA_ID = ITEMPROCESSO.ID " +
            "WHERE ITEMPROCESSO.ID = :itemProcessoDeCompra " +
            "AND ITEMCERT.SITUACAOITEMCERTAME = :situacaoItemCertame ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("itemProcessoDeCompra", itemProcessoDeCompra.getId());
        q.setParameter("situacaoItemCertame", situacaoItemCertame.name());
        return !q.getResultList().isEmpty();
    }

    public boolean isPossuiCertame(Licitacao licitacao) {
        String sql = " SELECT proposta.id AS proposta " +
            " FROM propostafornecedor proposta " +
            " INNER JOIN licitacao lic ON lic.id = proposta.licitacao_id " +
            " INNER JOIN certame mapa ON mapa.licitacao_id = lic.id " +
            " WHERE lic.id       = :licitacao " +
            " UNION ALL " +
            " SELECT proposta.id AS proposta " +
            " FROM propostafornecedor proposta " +
            " INNER JOIN licitacao lic ON lic.id = proposta.licitacao_id " +
            " INNER JOIN mapacomtecprec tecprec ON tecprec.licitacao_id = lic.id " +
            " WHERE lic.id       = :licitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("licitacao", licitacao.getId());
        return !q.getResultList().isEmpty();
    }

    public ItemPropostaFornecedorFacade getItemPropostaFornecedorFacade() {
        return itemPropostaFornecedorFacade;
    }

    public ProcessoDeCompraFacade getProcessoDeCompraFacade() {
        return processoDeCompraFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public PropostaFornecedorFacade getPropostaFornecedorFacade() {
        return propostaFornecedorFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public AmparoLegalFacade getAmparoLegalFacade() {
        return amparoLegalFacade;
    }
}
