/*
 * Codigo gerado automaticamente em Fri Sep 02 14:51:15 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BaseFP;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.ItemBaseFP;
import br.com.webpublico.entidades.ItemFichaFinanceiraFP;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoFP;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class BaseFPFacade extends AbstractFacade<BaseFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public BaseFPFacade() {
        super(BaseFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    @Override
    public BaseFP recuperar(Object id) {
        BaseFP bfp = em.find(BaseFP.class, id);
        bfp.getItensBasesFPs().size();

        return bfp;
    }

    public void duplicarBase(String codigoBase, String novoCodigo) {
        try {
            BaseFP b = getBaseByCodigo(codigoBase);
            if (b != null) {
                BaseFP bnew = getBaseByCodigo(novoCodigo);
                if (bnew != null) return;
                BaseFP novaBase = new BaseFP();
                novaBase.setCodigo(novoCodigo);
                novaBase.setFiltroBaseFP(FiltroBaseFP.NORMAL);
                novaBase.setDescricao("BASE 1/3 FÉRIAS");
                for (ItemBaseFP itemBaseFP : b.getItensBasesFPs()) {
                    novaBase.getItensBasesFPs().add(new ItemBaseFP(novaBase, itemBaseFP.getEventoFP(), itemBaseFP.getOperacaoFormula(), itemBaseFP.getTipoValor(), new Date()));
                }
                em.persist(novaBase);
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao duplicar base", e);
        }
    }

    public void isereBase1071() {
        Integer[] eve = {
//                101, 102, 103, 104,
//                105, 118, 122, 127, 133,
//                135, 139, 146, 153, 155,
//                163, 180, 187, 193, 220,
//                227, 228, 229, 231, 232,
//                234, 237, 239, 242, 244,
//                250, 251, 252, 253, 273,
//                276, 291, 292, 302, 303,
//                308, 309, 313, 314, 315,
//                316, 317, 318, 319, 321,
//                325, 387
            101, 102, 103, 104, 105, 112, 118,
            122, 127, 133, 135, 139, 144, 145,
            146, 151, 153, 155, 163, 180, 187,
            193, 220, 227, 228, 229, 231, 232,
            234, 237, 239, 242, 244, 250, 251,
            252, 253, 273, 276, 291, 292, 302,
            303, 308, 309, 313, 314, 315, 316,
            317, 318, 319, 321, 325, 345, 352,
            353, 357, 358, 359, 363, 370, 371,
            384, 386, 387, 388, 389, 390, 629,
            395
        };
        BaseFP b = getBaseByCodigo("1071");
        for (Integer i : eve) {
            ItemBaseFP it = new ItemBaseFP();
            EventoFP e = eventoFPFacade.recuperaEvento(i + "", TipoExecucaoEP.FOLHA);
            if (!temEventoNaBase(e, b)) {
                it.setBaseFP(b);
                it.setEventoFP(e);
                it.setOperacaoFormula(e.getTipoEventoFP().equals(TipoEventoFP.VANTAGEM) ? OperacaoFormula.ADICAO : OperacaoFormula.SUBTRACAO);
                it.setDataRegistro(new Date());
                it.setTipoValor(TipoValor.NORMAL);
                b.getItensBasesFPs().add(it);
                //System.out.println(it);

            }
        }
        em.merge(b);
    }

    private boolean temEventoNaBase(EventoFP e, BaseFP b) {
        for (ItemBaseFP item : b.getItensBasesFPs()) {
            if (item.getEventoFP().equals(e)) {
                return true;
            }
        }
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public boolean existeCodigoDaBaseFP(BaseFP base) {
        String hql = " from BaseFP base where lower(trim(base.codigo)) = :codigoParametro ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigoParametro", base.getCodigo().trim().toLowerCase());

        List<BaseFP> lista = q.getResultList();

        if (lista.contains(base)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public boolean existeCodigo(String codigo) {
        String hql = " from BaseFP base where lower(trim(base.codigo)) = :codigoParametro ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigoParametro", codigo.trim().toLowerCase());
        return (!q.getResultList().isEmpty());

    }

    public BaseFP getBaseByCodigo(String codigo) {
        String hql = " from BaseFP base where lower(trim(base.codigo)) = :codigoParametro ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigoParametro", codigo);

        List<BaseFP> lista = q.getResultList();
        if (lista.isEmpty()) {
            return null;
        }
        BaseFP b = lista.get(0);
        b.getItensBasesFPs().size();
        return b;
    }

    public List<BaseFP> buscarBasePFsPorCodigoOrDescricao(String filtro) {
        String sql = " select base.* from baseFP base where base.codigo like :filtro or lower(base.descricao) like :filtro ";

        Query q = em.createNativeQuery(sql, BaseFP.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setMaxResults(10);

        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public List<BaseFP> buscarBasePFsPorCodigoOrDescricaoAndFiltro(String filtro, FiltroBaseFP filtroBaseFP) {
        String sql = " select base.* from baseFP base " +
            " where base.filtroBaseFP = :filtroBaseFP " +
            "   and (base.codigo like :filtro or lower(base.descricao) like :filtro) ";

        Query q = em.createNativeQuery(sql, BaseFP.class);
        q.setParameter("filtroBaseFP", filtroBaseFP.name());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public BaseFP getBaseByCodigoEFiltro(String codigo, FiltroBaseFP filtroBaseFP) {
        if (codigo.trim().equals("")) {
            return null;
        }
        String hql = " from BaseFP base where cast(trim(base.codigo) as integer) = :codigoParametro and base.filtroBaseFP = :filtro";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigoParametro", Integer.parseInt(codigo));
        q.setParameter("filtro", filtroBaseFP);
        List<BaseFP> lista = q.getResultList();
        if (lista.isEmpty()) {
            return null;
        }
        BaseFP b = lista.get(0);
        b.getItensBasesFPs().size();
        return b;
    }

    public String getCodigoBaseFP() {
        String hql = " select max(cast(base.codigo as integer)) from BaseFP base";

        Query q = getEntityManager().createQuery(hql);
        if (q.getResultList().isEmpty()) {
            return 10000 + "";
        }
        Integer b = (Integer) q.getResultList().get(0);
        if (b == null) {
            return 10000 + "";
        }
        b++;
        return b + "";
    }

    public List<BaseFP> listaBaseFPs() {
        StringBuilder builder = new StringBuilder();
        builder.append("select b from BaseFP b ");
        builder.append(" inner join fetch b.itensBasesFPs i ");
        builder.append(" inner join fetch i.eventoFP e ");
        builder.append(" where e.ativo is true order by b");
        Query q = em.createQuery(builder.toString());
        return q.getResultList();
    }

    public List<BaseFP> buscarBasesComItens() {
        List<BaseFP> bases = Lists.newLinkedList();
        String sql = "select * from ( " +
            "select distinct b.* " +
            "from basefp b " +
            "inner join itembasefp i on b.id = i.basefp_id " +
            "inner join eventofp e on i.eventofp_id = e.id " +
            "where e.tipoexecucaoep = :tipo and e.ativo = 1) dados " +
            "order by dados.codigo";
        Query q = em.createNativeQuery(sql, BaseFP.class);
        q.setParameter("tipo", TipoExecucaoEP.RPA.name());
        for (BaseFP b : (List<BaseFP>) q.getResultList()) {
            b = recuperar(b.getId());
            bases.add(b);
        }
        return bases;
    }

    public List<BaseFP> listaBasesPorFiltroBase(FiltroBaseFP filtro) {
        StringBuilder builder = new StringBuilder();
        builder.append("select distinct b from BaseFP b ");
        builder.append(" where b.filtroBaseFP = :filtro order by b.codigo ");
        Query q = em.createQuery(builder.toString());

        q.setParameter("filtro", filtro);
        List<BaseFP> list = new ArrayList<>();
        if (!q.getResultList().isEmpty()) {
            for (BaseFP b : (List<BaseFP>) q.getResultList()) {
                b.getItensBasesFPs().size();
                list.add(b);
            }
        }
        return list;
    }

    public BigDecimal recuperarValoresBase(List<ItemFichaFinanceiraFP> listaItemFichaFinanceiraFP) {
        return recuperarValoresBase(listaItemFichaFinanceiraFP, buscarConfiguracaoFPVigente());
    }

    public BigDecimal recuperarValoresBase(List<ItemFichaFinanceiraFP> listaItemFichaFinanceiraFP, ConfiguracaoFP configuracaoFP) {
        BigDecimal totalBaseConsignacao = BigDecimal.ZERO;
        if (!listaItemFichaFinanceiraFP.isEmpty()) {
            BaseFP b = configuracaoFP.getBaseMargemConsignavel();
            for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : listaItemFichaFinanceiraFP) {
                if (isPodeSomarEventoBase(itemFichaFinanceiraFP, b.getItensBasesFPs())) {
                    totalBaseConsignacao = totalBaseConsignacao.add(getValorItemFichaFinanceiraDaLista(itemFichaFinanceiraFP, b.getItensBasesFPs()));
                }
            }
        }
        return totalBaseConsignacao;
    }

    public BigDecimal recurperarValoresEuConsigoMais(List<ItemFichaFinanceiraFP> listaItemFichaFinanceiraFP, ConfiguracaoFP configuracaoFP) {
        BigDecimal totalBaseEuConsigoMais = BigDecimal.ZERO;
        if (!listaItemFichaFinanceiraFP.isEmpty()) {
            BaseFP b = configuracaoFP.getBaseMargemEuConsigoMais();
            for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : listaItemFichaFinanceiraFP) {
                if (isPodeSomarEventoBase(itemFichaFinanceiraFP, b.getItensBasesFPs())) {
                    totalBaseEuConsigoMais = totalBaseEuConsigoMais.add(getValorItemFichaFinanceiraDaLista(itemFichaFinanceiraFP, b.getItensBasesFPs()));
                }
            }
        }
        return totalBaseEuConsigoMais;
    }

    public ConfiguracaoFP buscarConfiguracaoFPVigente() {
        ConfiguracaoFP configuracaoFP = configuracaoRHFacade.buscarConfiguracaoFPVigente(configuracaoRHFacade.getSistemaFacade().getDataOperacao());
        if (configuracaoFP != null && configuracaoFP.getId() != null) {
            configuracaoFP = em.find(ConfiguracaoFP.class, configuracaoFP.getId());
            Hibernate.initialize(configuracaoFP.getBaseMargemConsignavel().getItensBasesFPs());
            Hibernate.initialize(configuracaoFP.getBaseMargemEuConsigoMais().getItensBasesFPs());
        }
        return configuracaoRHFacade.buscarConfiguracaoFPVigente(configuracaoRHFacade.getSistemaFacade().getDataOperacao());
    }

    private boolean isPodeSomarEventoBase(ItemFichaFinanceiraFP itemFichaFinanceiraFP, List<ItemBaseFP> itensBase) {
        for (ItemBaseFP item : itensBase) {
            if (itemFichaFinanceiraFP.getEventoFP().equals(item.getEventoFP())) {
                if (TipoCalculoFP.RETROATIVO.equals(itemFichaFinanceiraFP.getTipoCalculoFP()) && !item.getSomaValorRetroativo()) {
                    return false;
                }
                return true;
            }
        }

        return false;
    }

    private BigDecimal getValorItemFichaFinanceiraDaLista(ItemFichaFinanceiraFP item, List<ItemBaseFP> itensBasesFPs) {
        for (ItemBaseFP itemBase : itensBasesFPs) {
            if (itemBase.getEventoFP().equals(item.getEventoFP())) {
                if (OperacaoFormula.ADICAO.equals(itemBase.getOperacaoFormula()) && TipoEventoFP.VANTAGEM.equals(item.getTipoEventoFP())) {
                    return item.getValor();
                }
                if (OperacaoFormula.SUBTRACAO.equals(itemBase.getOperacaoFormula()) && TipoEventoFP.DESCONTO.equals(item.getTipoEventoFP())) {
                    return item.getValor().negate();
                }
            }
        }

        return BigDecimal.ZERO;
    }
}
