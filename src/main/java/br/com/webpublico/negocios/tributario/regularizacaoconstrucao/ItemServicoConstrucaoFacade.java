package br.com.webpublico.negocios.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.FaixaDeValoresSCL;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ItemServicoConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ServicoConstrucao;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ItemServicoConstrucaoFacade extends AbstractFacade<ItemServicoConstrucao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ItemServicoConstrucaoFacade() {
        super(ItemServicoConstrucao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(ItemServicoConstrucao entity) {
        entity.setCodigo(recuperarProximoCodigo(entity));
        super.salvarNovo(entity);
    }

    public Integer recuperarProximoCodigo(ItemServicoConstrucao entity) {
        String sql = " select COALESCE (max(isc.codigo), 0) from ItemServicoConstrucao isc ";
        Query q = em.createNativeQuery(sql);

        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    public List<ItemServicoConstrucao> buscarItensServico() {
        Query q = em.createNativeQuery("select isc.* from ITEMSERVICOCONSTRUCAO isc ", ItemServicoConstrucao.class);
        return q.getResultList();
    }

    public FaixaDeValoresSCL buscarFaixaDestinoIgualFaixaOrigem(FaixaDeValoresSCL faixaOrigem, Exercicio exDestino) {
        String sql = "select fv.* from faixadevaloresscl fv " +
            " where fv.EXERCICIO_ID = :ex " +
            " and fv.tipomedida = :tipo " +
            " and fv.itemServicoConstrucao_id = :idItemServico " +
            " and fv.areaInicial " + (faixaOrigem.getAreaInicial() != null ? " = :areaInicial " : " is null") +
            " and fv.areaFinal " + (faixaOrigem.getAreaFinal() != null ? " = :areaFinal " : " is null") +
            " and fv.valorUfm " + (faixaOrigem.getValorUFM() != null ? " = :valorUfm " : " is null");
        Query q = em.createNativeQuery(sql, FaixaDeValoresSCL.class);
        q.setParameter("ex", exDestino.getId());
        q.setParameter("tipo", faixaOrigem.getTipoMedida().name());
        q.setParameter("idItemServico", faixaOrigem.getItemServicoConstrucao().getId());
        if (faixaOrigem.getAreaInicial() != null) q.setParameter("areaInicial", faixaOrigem.getAreaInicial());
        if (faixaOrigem.getAreaFinal() != null) q.setParameter("areaFinal", faixaOrigem.getAreaFinal());
        if (faixaOrigem.getValorUFM() != null) q.setParameter("valorUfm", faixaOrigem.getValorUFM());
        List<FaixaDeValoresSCL> result = q.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<ItemServicoConstrucao> buscarItemServicoPorServico(ServicoConstrucao servicoConstrucao) {
        Query q = em.createNativeQuery("select isc.* from ITEMSERVICOCONSTRUCAO isc where isc.SERVICOCONSTRUCAO_ID = :serv", ItemServicoConstrucao.class);
        q.setParameter("serv", servicoConstrucao.getId());
        List<ItemServicoConstrucao> itens = q.getResultList();
        for (ItemServicoConstrucao item : itens) {
            inicializar(item);
        }
        return itens;
    }

    public List<ItemServicoConstrucao> buscarItemServicoPorServico(ServicoConstrucao servicoConstrucao, String parte) {
        String sql = " select * from itemservicoconstrucao item " +
            " where item.servicoconstrucao_id = :servico" +
            " and lower(item.descricao) like :parte ";

        Query q = em.createNativeQuery(sql, ItemServicoConstrucao.class);
        q.setParameter("servico", servicoConstrucao.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");

        List<ItemServicoConstrucao> itens = q.getResultList();
        for (ItemServicoConstrucao item : itens) {
            inicializar(item);
        }
        return itens;
    }

    @Override
    public ItemServicoConstrucao recuperar(Object id) {
        ItemServicoConstrucao isc = super.recuperar(id);
        inicializar(isc);
        return isc;
    }

    public ItemServicoConstrucao inicializar(ItemServicoConstrucao itemServicoConstrucao) {
        em.refresh(itemServicoConstrucao);
        Hibernate.initialize(itemServicoConstrucao.getListaFaixaDeValoresSCL());
        return itemServicoConstrucao;
    }
}
