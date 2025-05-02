/*
 * Codigo gerado automaticamente em Tue May 24 13:55:42 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoInventarioEstoque;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class InventarioEstoqueFacade extends AbstractFacade<InventarioEstoque> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private EstoqueFacade estoqueFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public InventarioEstoqueFacade() {
        super(InventarioEstoque.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InventarioEstoque recuperarInventarioEstoque(Long id) {
        String hql = "select ie from InventarioEstoque ie where ie.id = :id";
        Query q = em.createQuery(hql);
        q.setParameter("id", id);
        q.setMaxResults(1);
        return (InventarioEstoque) q.getSingleResult();
    }

    public List<ItemInventarioEstoque> recuperarItensInventario(InventarioEstoque ie) {
        Query q = em.createQuery("select iie from ItemInventarioEstoque iie"
            + " inner join iie.inventarioEstoque ie"
            + " where ie = :param");
        q.setParameter("param", ie);
        return q.getResultList();
    }

    public List<LoteItemInventario> recuperarLotesItemInventario(ItemInventarioEstoque iie) {
        Query q = em.createQuery("select lii from LoteItemInventario lii"
            + " inner join lii.itemInventarioEstoque iie"
            + " where iie = :param");
        q.setParameter("param", iie);
        return q.getResultList();
    }

    private EntradaInventarioMaterial gerarNovaEntrada(InventarioEstoque inventarioEstoque) {
        EntradaInventarioMaterial entradaInventarioMaterial = new EntradaInventarioMaterial();
        entradaInventarioMaterial.setDataEntrada(inventarioEstoque.getEncerradoEm());
        entradaInventarioMaterial.setResponsavel(inventarioEstoque.getUsuarioSistema().getPessoaFisica());
        entradaInventarioMaterial.setNumero(singletonGeradorCodigo.getProximoCodigo(EntradaInventarioMaterial.class, "numero"));
        entradaInventarioMaterial.setInventarioEstoque(inventarioEstoque);
        entradaInventarioMaterial.setItens(new ArrayList<ItemEntradaMaterial>());
        return entradaInventarioMaterial;
    }

    private SaidaInventarioMaterial gerarNovaSaida(InventarioEstoque inventarioEstoque) {
        SaidaInventarioMaterial saidaInventarioMaterial = new SaidaInventarioMaterial();
        saidaInventarioMaterial.setDataSaida(inventarioEstoque.getEncerradoEm());
        saidaInventarioMaterial.setUsuario(inventarioEstoque.getUsuarioSistema());
        saidaInventarioMaterial.setNumero(singletonGeradorCodigo.getProximoCodigo(SaidaInventarioMaterial.class, "numero"));
        saidaInventarioMaterial.setInventarioEstoque(inventarioEstoque);
        saidaInventarioMaterial.setListaDeItemSaidaMaterial(new ArrayList<ItemSaidaMaterial>());
        return saidaInventarioMaterial;
    }

    public InventarioEstoque concluirInventario(InventarioEstoque selecionado) throws Exception {
        selecionado.setSituacaoInventario(SituacaoInventarioEstoque.CONCLUIDO);
        ConsolidadorDeEstoque consolidadorDeEstoque = getNovoConsolidadorSemReservaEstoque(selecionado);
        bloquearEstoquesItemInventarioEstoque(selecionado.getItensInventarioEstoque(), consolidadorDeEstoque);
        return em.merge(selecionado);
    }

    private ConsolidadorDeEstoque getNovoConsolidadorSemReservaEstoque(InventarioEstoque selecionado) {
        ConsolidadorDeEstoque consolidadorDeEstoque = localEstoqueFacade.getNovoConsolidadorSemReservaEstoque(
            selecionado.getLocalEstoque(),
            selecionado.materialSet(),
            selecionado.getIniciadoEm());
        if (consolidadorDeEstoque != null) {
            return consolidadorDeEstoque;
        }
        throw new ExcecaoNegocioGenerica("Consolidador de estoque não encontrado para os parâmetros informaods.");
    }

    public InventarioEstoque salvarInventario(InventarioEstoque selecionado) throws Exception {
        return em.merge(selecionado);
    }

    public void bloquearEstoqueItemInventarioEstoque(ItemInventarioEstoque itemInventarioEstoque, ConsolidadorDeEstoque consolidadorDeEstoque) {
        List<Estoque> estoques = consolidadorDeEstoque.getEstoques(itemInventarioEstoque.getMaterial());
        for (Estoque e : estoques) {
            e.setBloqueado(Boolean.TRUE);
            estoqueFacade.salvar(e);
        }
    }

    @Override
    public void remover(InventarioEstoque entity) {
        if (entity.isInventarioConcluido()) {
            desbloquearEstoque(entity);
        }
        super.remover(entity);
    }

    public void desbloquearEstoque(InventarioEstoque selecionado) {
        ConsolidadorDeEstoque consolidadorDeEstoque = getNovoConsolidadorSemReservaEstoque(selecionado);
        for (ItemInventarioEstoque itemInventarioEstoque : selecionado.getItensInventarioEstoque()) {
            List<Estoque> estoques = consolidadorDeEstoque.getEstoques(itemInventarioEstoque.getMaterial());
            for (Estoque e : estoques) {
                e.setBloqueado(Boolean.FALSE);
                estoqueFacade.salvar(e);
            }
        }
    }

    public void desbloquearEstoqueItemInventarioEstoque(ItemInventarioEstoque itemInventarioEstoque, ConsolidadorDeEstoque consolidadorDeEstoque) {
        List<Estoque> estoques = consolidadorDeEstoque.getEstoques(itemInventarioEstoque.getMaterial());
        for (Estoque e : estoques) {
            e.setBloqueado(Boolean.FALSE);
            estoqueFacade.salvar(e);
        }
    }

    public void bloquearPermanentementeEstoqueItemInventarioEstoque(ItemInventarioEstoque itemInventarioEstoque, ConsolidadorDeEstoque consolidadorDeEstoque) {
        List<Estoque> estoques = consolidadorDeEstoque.getEstoques(itemInventarioEstoque.getMaterial());
        for (Estoque e : estoques) {
            e.setLiberadoEm(new Date());
            estoqueFacade.salvar(e);
        }
    }

    private void bloquearEstoquesItemInventarioEstoque(List<ItemInventarioEstoque> itensInventario, ConsolidadorDeEstoque consolidadorDeEstoque) {
        if (itensInventario == null) {
            return;
        }

        for (ItemInventarioEstoque iieDaVez : itensInventario) {
            bloquearEstoqueItemInventarioEstoque(iieDaVez, consolidadorDeEstoque);
        }
    }

    private void desbloquearEstoquesBloqueados(List<ItemInventarioEstoque> itensInventario, ConsolidadorDeEstoque consolidadorDeEstoque) {
        if (itensInventario == null) {
            return;
        }

        for (ItemInventarioEstoque iieDaVez : itensInventario) {
            desbloquearEstoqueItemInventarioEstoque(iieDaVez, consolidadorDeEstoque);
        }
    }

    private void bloquearEstoquesPermanentemente(List<ItemInventarioEstoque> itensInventario, ConsolidadorDeEstoque consolidadorDeEstoque) {
        if (itensInventario == null) {
            return;
        }

        for (ItemInventarioEstoque iieDaVez : itensInventario) {
            desbloquearEstoqueItemInventarioEstoque(iieDaVez, consolidadorDeEstoque);
            bloquearPermanentementeEstoqueItemInventarioEstoque(iieDaVez, consolidadorDeEstoque);
        }
    }

    public List<InventarioEstoque> recuperarInventariosAbertosParaLocal(InventarioEstoque esteSelecionado) {
        Query q = em.createQuery("from InventarioEstoque ie"
            + " where ie.iniciadoEm < :dataInicial"
            + "   and (ie.encerradoEm is null or ie.encerradoEm > :dataInicial)"
            + "   and ie.localEstoque = :localEstoque");
        q.setParameter("dataInicial", esteSelecionado.getIniciadoEm(), TemporalType.TIMESTAMP);
//        q.setParameter("agora", new Date(), TemporalType.TIMESTAMP);
        q.setParameter("localEstoque", esteSelecionado.getLocalEstoque());
        q.setMaxResults(5);
        try {
            return q.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public InventarioEstoque recuperaUltimaProducaoComDataPosteriorAoArgumento(Date data) {
        String hql = "    select ie "
            + "               from InventarioEstoque ie "
            + "             where ie.iniciadoEm > :data "
            + "                 and ie.iniciadoEm = ( select max(inv.iniciadoEm)  "
            + "                                                            from InventarioEstoque inv "
            + "                                                          where inv.iniciadoEm > :data)";

        Query q = em.createQuery(hql);
        q.setParameter("data", data, TemporalType.TIMESTAMP);

        if (!q.getResultList().isEmpty()) {
            return (InventarioEstoque) q.getSingleResult();
        }

        return null;
    }

    public boolean isInventarioConcluido(Long id) {
        String sql = "select ie.* from InventarioEstoque ie where ie.id = :id and ie.situacaoInventario = :concluido ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        q.setParameter("concluido", SituacaoInventarioEstoque.CONCLUIDO.name());
        return !q.getResultList().isEmpty();
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public EstoqueFacade getEstoqueFacade() {
        return estoqueFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }
}
