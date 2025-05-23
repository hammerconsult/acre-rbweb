package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SubTipoSaldoItemContrato;
import br.com.webpublico.enums.TipoAquisicaoContrato;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoSaldoItemContrato;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 02/10/14
 * Time: 08:48
 * To change this template use File | Settings | File Templates.cao
 */
@Stateless
public class ItemContratoFacade extends AbstractFacade<ItemContrato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ItemPropostaFornecedorFacade itemPropostaFornecedorFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemContratoFacade() {
        super(ItemContrato.class);
    }

    public BigDecimal getValorUnitario(ItemContrato ic) {
        if (ic.getContrato().isDeLicitacao()) {
            return itemPropostaFornecedorFacade.getValorUnitarioVencedorDoItemPropostaFornecedor(ic.getItemPropostaFornecedor());
        }
        if (ic.getContrato().isDeIrp()) {
            if (ic.getControleValor() && ic.getContrato().getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
                return ic.getItemContratoItemIRP().getItemParticipanteIRP().getValor();
            }
            return itemPropostaFornecedorFacade.getValorUnitarioVencedorDoItemPropostaFornecedor(ic.getItemPropostaFornecedor());
        }
        if (ic.getContrato().isContratoVigente()) {
            return ic.getValorUnitario();
        }
        if (ic.getContrato().isDeDispensaDeLicitacao()) {
            return ic.getItemPropostaFornecedorDispensa().getPreco();
        }
        if (ic.getContrato().isDeProcedimentosAuxiliares()) {
            return ic.getItemPropostaFornecedor().getPreco();
        }
        if (ic.getContrato().isDeRegistroDePrecoExterno()) {
            return ic.getItemSolicitacaoExterno().getValorUnitario();
        }
        if (ic.getContrato().isDeAdesaoAtaRegistroPrecoInterna()) {
            return ic.getValorUnitario();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorLicitadoItem(ItemContrato ic) {
        if (ic != null && ic.getQuantidadeLicitada() != null) {
            return ic.getQuantidadeLicitada().multiply(getValorUnitario(ic)).setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getQuantidadeOrValorUtilizadoEmOutrosContratos(ItemContrato ic) {
        BigDecimal valorRescindido = recuperarQuantidadeOrValorRescindido(ic);
        BigDecimal valorExecutadoSemContrato = BigDecimal.ZERO;
        BigDecimal valorOutrosCont = BigDecimal.ZERO;

        if (ic.getContrato().isDeLicitacao()) {
            valorExecutadoSemContrato = recuperarQuantidadeOrValorExecucaoSemContrato(ic);
            valorOutrosCont = recuperarQuantidadeOrValorUtilizadoEmOutrosContratosLicitacao(ic);
        }
        if (ic.getContrato().isDeProcedimentosAuxiliares()) {
            valorOutrosCont = recuperarQuantidadeOrValorUtilizadoEmOutrosContratosLicitacao(ic);
        }
        if (ic.getContrato().isDeDispensaDeLicitacao()) {
            valorExecutadoSemContrato = recuperarQuantidadeOrValorExecucaoSemContrato(ic);
            valorOutrosCont = recuperarQuantidadeOrValorUtilizadoEmOutrosContratosDispensaLicitacao(ic);
        }
        if (ic.getContrato().isDeIrp()) {
            valorExecutadoSemContrato = recuperarQuantidadeOrValorExecucaoSemContrato(ic);
            valorOutrosCont = recuperarQuantidadeOrValorUtilizadoEmOutrosContratosIRP(ic);
        }
        if (ic.getContrato().isDeAdesaoAtaRegistroPrecoInterna()) {
            valorOutrosCont = recuperarQuantidadeOrValorUtilizadoEmOutrosContratosAdesaoInterna(ic);
        }
        if (ic.getContrato().isDeRegistroDePrecoExterno()) {
            valorOutrosCont = recuperarQuantidadeOrValorUtilizadoEmOutrosContratosRegistroPrecoExterno(ic);
        }
        return valorOutrosCont.add(valorExecutadoSemContrato).subtract(valorRescindido);
    }

    public BigDecimal getQuantidadeEmRequisicao(ItemContrato ic) {
        if (ic.getContrato().isProcessoLicitatorio()) {
            return recuperarQuantidadeEmRequisicaoLicitacao(ic);
        }
        if (ic.getContrato().isDeDispensaDeLicitacao()) {
            return recuperarQuantidadeEmRequisicaoDispensa(ic);
        }
        if (ic.getContrato().isDeRegistroDePrecoExterno()) {
            return recuperarQuantidadeEmRequisicaoRegistroPrecoExterno(ic);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getQuantidadeEntregue(ItemContrato ic) {
        if (ic.getContrato().isProcessoLicitatorio()) {
            return recuperarQuantidadeEntregueLicitacao(ic);
        }
        if (ic.getContrato().isDeDispensaDeLicitacao()) {
            return recuperarQuantidadeEntregueDispensa(ic);
        }
        if (ic.getContrato().isDeRegistroDePrecoExterno()) {
            return recuperarQuantidadeEntregueRegistroPrecoExterno(ic);
        }
        return BigDecimal.ZERO;
    }

    public Boolean isUtilizadoEmOutroContratoPorValor(ItemContrato ic) {
        if (ic.getContrato().isProcessoLicitatorio()) {
            return isUtilizadoEmOutroContratoLicitacaoPorValor(ic.getItemAdequado().getItemProcessoCompra());
        }
        if (ic.getContrato().isDeDispensaDeLicitacao()) {
            return isUtilizadoEmOutroContratoDispensaPorValor(ic.getItemPropostaFornecedorDispensa());
        }
        if (ic.getContrato().isDeRegistroDePrecoExterno()) {
            return isUtilizadoEmOutroContratoAtaExternaPorValor(ic.getItemSolicitacaoExterno());
        }
        return false;
    }

    public BigDecimal recuperarQuantidadeOrValorUtilizadoEmOutrosContratosLicitacao(ItemPropostaFornecedor itemProposta) {
        String sql;
        sql = "  select coalesce(case when ic.tipocontrole = :controleQuantidade " +
            "                  then sum(ic.quantidadetotalcontrato) " +
            "                  else sum(ic.valortotal) end, 0) " +
            "  from itemcontrato ic " +
            "   left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "   left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "   left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "   left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            " where ipf.id = :idItemProposta " +
            " and ipf.itemprocessodecompra_id = :idIpc " +
            " group by ic.tipocontrole ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idIpc", itemProposta.getItemProcessoDeCompra().getId());
        q.setParameter("idItemProposta", itemProposta.getId());
        q.setParameter("controleQuantidade", TipoControleLicitacao.QUANTIDADE.name());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeOrValorUtilizadoEmOutrosContratosLicitacao(ItemContrato itemContrato) {
        String sql = " " +
            "  select coalesce(case when ic.tipocontrole = :controleQuantidade " +
            "                  then sum(ic.quantidadetotalcontrato) " +
            "                  else sum(ic.valortotal) end, 0) " +
            "  from itemcontrato ic " +
            "   inner join contrato c on c.id = ic.contrato_id  " +
            "   inner join itemcontratoitempropfornec icif on icif.itemcontrato_id = ic.id " +
            "   inner join itempropfornec ipf on ipf.id = icif.itempropostafornecedor_id " +
            "  where ipf.itemprocessodecompra_id = :idIpc " +
            "  and c.tipoaquisicao = :tipoAquisicao ";
        if (itemContrato.getContrato() != null && itemContrato.getContrato().getId() != null) {
            sql += " and ic.contrato_id <> :contrato";
        }
        sql += " group by ic.tipocontrole ";
        Query q = em.createNativeQuery(sql);
        if (itemContrato.getContrato() != null && itemContrato.getContrato().getId() != null) {
            q.setParameter("contrato", itemContrato.getContrato().getId());
        }
        q.setParameter("idIpc", itemContrato.getItemPropostaFornecedor().getItemProcessoDeCompra().getId());
        q.setParameter("controleQuantidade", TipoControleLicitacao.QUANTIDADE.name());
        q.setParameter("tipoAquisicao", itemContrato.getContrato().getTipoAquisicao().name());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeOrValorUtilizadoEmOutrosContratosDispensaLicitacao(ItemContrato ic) {
        Contrato contrato = ic.getContrato();
        String sql = " " +
            "  select coalesce(case when ic.tipocontrole = :controleQuantidade " +
            "                  then sum(ic.quantidadetotalcontrato) " +
            "                  else sum(ic.valortotal) end, 0) " +
            "  from itemcontrato ic " +
            "    inner join contrato c on c.id = ic.contrato_id  " +
            "    inner join itemcontratoitempropdisp itemDisp on itemDisp.itemcontrato_id = ic.id " +
            "  where itemDisp.itempropfornecdispensa_id = :ipfd " +
            "  and c.tipoaquisicao = :tipoAquisicao ";
        if (contrato != null && contrato.getId() != null) {
            sql += " and ic.contrato_id <> :contrato";
        }
        sql += " group by ic.tipocontrole ";
        Query q = em.createNativeQuery(sql);
        if (contrato != null && contrato.getId() != null) {
            q.setParameter("contrato", contrato.getId());
        }
        q.setParameter("ipfd", ic.getItemPropostaFornecedorDispensa().getId());
        q.setParameter("controleQuantidade", TipoControleLicitacao.QUANTIDADE.name());
        q.setParameter("tipoAquisicao", TipoAquisicaoContrato.DISPENSA_DE_LICITACAO.name());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeOrValorUtilizadoEmOutrosContratosAdesaoInterna(ItemContrato itemContrato) {
        Contrato contrato = itemContrato.getContrato();
        String sql = " " +
            "  select coalesce(case when ic.tipocontrole = :controleQuantidade " +
            "                  then sum(ic.quantidadetotalcontrato) " +
            "                  else sum(ic.valortotal) end, 0) " +
            "  from itemcontrato ic " +
            "    inner join contrato c on c.id = ic.contrato_id  " +
            "    inner join itemcontratoadesaoataint icad on icad.itemcontrato_id = ic.id " +
            "    inner join itemsolicitacaoexterno ise on ise.id = icad.itemsolicitacaoexterno_id " +
            "  where ise.itemprocessocompra_id = :ipc " +
            "    and ise.id = :idItemSol " +
            "    and c.tipoaquisicao = :tipoAquisicao ";
        if (contrato != null && contrato.getId() != null) {
            sql += " and ic.contrato_id <> :contrato";
        }
        sql += " group by ic.tipocontrole ";
        Query q = em.createNativeQuery(sql);
        if (contrato != null && contrato.getId() != null) {
            q.setParameter("contrato", contrato.getId());
        }
        q.setParameter("controleQuantidade", TipoControleLicitacao.QUANTIDADE.name());
        q.setParameter("tipoAquisicao", TipoAquisicaoContrato.ADESAO_ATA_REGISTRO_PRECO_INTERNA.name());
        q.setParameter("ipc", itemContrato.getItemAdequado().getItemProcessoCompra().getId());
        q.setParameter("idItemSol", itemContrato.getItemContratoAdesaoAtaInt().getItemSolicitacaoExterno().getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeOrValorUtilizadoEmOutrosContratosRegistroPrecoExterno(ItemContrato ic) {
        Contrato contrato = ic.getContrato();
        String sql = " " +
            "  select coalesce(case when ic.tipocontrole = :controleQuantidade " +
            "                  then sum(ic.quantidadetotalcontrato) " +
            "                  else sum(ic.valortotal) end, 0) " +
            "  from itemcontrato ic " +
            "   inner join contrato c on c.id = ic.contrato_id  " +
            "   inner join itemcontratoitemsolext ise on ise.itemcontrato_id = ic.id " +
            "  where ise.itemsolicitacaoexterno_id = :idItemSolExt " +
            "   and c.tipoaquisicao = :tipoAquisicao ";
        if (contrato != null && contrato.getId() != null) {
            sql += " and ic.contrato_id <> :contrato";
        }
        sql += " group by ic.tipocontrole ";
        Query q = em.createNativeQuery(sql);
        if (contrato != null && contrato.getId() != null) {
            q.setParameter("contrato", contrato.getId());
        }
        q.setParameter("controleQuantidade", TipoControleLicitacao.QUANTIDADE.name());
        q.setParameter("tipoAquisicao", TipoAquisicaoContrato.REGISTRO_DE_PRECO_EXTERNO.name());
        q.setParameter("idItemSolExt", ic.getItemSolicitacaoExterno().getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeOrValorUtilizadoEmOutrosContratosIRP(ItemContrato itemContrato) {
        Contrato contrato = itemContrato.getContrato();
        String sql = " " +
            "  select coalesce(case when ic.tipocontrole = :controleQuantidade " +
            "                  then sum(ic.quantidadetotalcontrato) " +
            "                  else sum(ic.valortotal) end, 0) " +
            "  from itemcontrato ic " +
            "    inner join contrato c on c.id = ic.contrato_id  " +
            "    inner join itemcontratoitemirp icirp on icirp.itemcontrato_id = ic.id " +
            "    inner join itempropfornec ipf on ipf.id = icirp.itempropostafornecedor_id " +
            "  where ipf.itemprocessodecompra_id = :idIpc " +
            "    and icirp.itemparticipanteirp_id = :itemIrp " +
            "    and c.tipoaquisicao = :tipoAquisicao ";
        if (contrato != null && contrato.getId() != null) {
            sql += " and ic.contrato_id <> :contrato";
        }
        sql += " group by ic.tipocontrole ";
        Query q = em.createNativeQuery(sql);
        if (contrato != null && contrato.getId() != null) {
            q.setParameter("contrato", contrato.getId());
        }
        q.setParameter("controleQuantidade", TipoControleLicitacao.QUANTIDADE.name());
        q.setParameter("tipoAquisicao", TipoAquisicaoContrato.INTENCAO_REGISTRO_PRECO.name());
        q.setParameter("idIpc", itemContrato.getItemPropostaFornecedor().getItemProcessoDeCompra().getId());
        q.setParameter("itemIrp", itemContrato.getItemContratoItemIRP().getItemParticipanteIRP().getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeOrValorExecucaoSemContrato(ItemContrato ic) {
        if (ic.getItemAdequado().getItemProcessoCompra() == null) {
            return BigDecimal.ZERO;
        }
        String sql = "" +
            "  select coalesce(case when ic.tipocontrole = :controleQuantidade " +
            "                  then vw.quantidadeExecutada - vw.quantidadeEstornada " +
            "                  else vw.valorExecutado - vw.valorEstornado end, 0) as resultado " +
            "   from vwsaldoprocessosemcontrato vw " +
            " inner join itemprocessodecompra ipc on ipc.id = vw.idItemProcesso " +
            " inner join itemsolicitacao isc on isc.id = ipc.itemsolicitacaomaterial_id  " +
            " inner join itemcotacao ic on ic.id = isc.itemcotacao_id " +
            "   where vw.idItemProcesso = :idIpc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idIpc", ic.getItemAdequado().getItemProcessoCompra().getId());
        q.setParameter("controleQuantidade", TipoControleLicitacao.QUANTIDADE.name());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeEmRequisicaoLicitacao(ItemContrato ic) {
        String sql = " " +
            "   select coalesce(sum(qtde_requisicao), 0) " +
            "       from (select coalesce(sum(irce.quantidade), 0) as qtde_requisicao " +
            "             from itemrequisicaocompraexec irce " +
            "               inner join execucaocontratoitem exitem on exitem.id = irce.execucaocontratoitem_id " +
            "               inner join itemcontrato ic on ic.id = exitem.itemcontrato_id " +
            "               left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "               left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "               left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "               left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id,iata.itempropostafornecedor_id) = ipf.id " +
            "      where ipf.id = :idItemProposta " +
            "      union all " +
            "      select coalesce(sum(item.quantidade), 0) *-1 as qtde_requisicao " +
            "      from itemrequisicaocompraest item " +
            "               inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = item.id " +
            "               inner join execucaocontratoitem exitem on exitem.id = irce.execucaocontratoitem_id " +
            "               inner join itemcontrato ic on ic.id = exitem.itemcontrato_id " +
            "               left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "               left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "               left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "               left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            "      where ipf.id = :idItemProposta) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemProposta", ic.getItemPropostaFornecedor().getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeEmRequisicaoDispensa(ItemContrato ic) {
        String sql = " " +
                "   select coalesce(sum(irce.quantidade),0) from itemrequisicaocompraexec irce " +
                "    inner join execucaocontratoitem exitem on exitem.id = irce.execucaocontratoitem_id " +
                "    inner join itemcontrato ic on ic.id = exitem.itemcontrato_id " +
                "    inner join itemcontratoitempropdisp itemDisp on itemDisp.itemcontrato_id = ic.id " +
                "  where itemDisp.itempropfornecdispensa_id = :idItemProposta ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemProposta", ic.getItemPropostaFornecedorDispensa().getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeEmRequisicaoRegistroPrecoExterno(ItemContrato ic) {
        String sql = " " +
            "   select coalesce(sum(irce.quantidade),0) from itemrequisicaocompraexec irce " +
            "   inner join execucaocontratoitem exitem on exitem.id = irce.execucaocontratoitem_id " +
            "   inner join itemcontrato ic on ic.id = exitem.itemcontrato_id " +
            "   inner join itemcontratoitemsolext ise on ise.itemcontrato_id = ic.id " +
            " where ise.itemsolicitacaoexterno_id = :idItemSolExt";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemSolExt", ic.getItemSolicitacaoExterno().getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeEntregueLicitacao(ItemContrato ic) {
        String sql = "" +
            " select coalesce(sum(quantidade), 0) as qtde_entregue from ( " +
            "   select distinct iem.quantidade as quantidade" +
            "   from ItemEntradaMaterial iem " +
            "   inner join itemcompramaterial icm on icm.itementradamaterial_id = iem.id " +
            "   inner join itemrequisicaodecompra irc on irc.id = icm.itemrequisicaodecompra_id " +
            "   inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = irc.id " +
            "   inner join execucaocontratoitem exitem on exitem.id = irce.execucaocontratoitem_id " +
            "   inner join itemcontrato ic on ic.id = exitem.itemcontrato_id " +
            "   left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "   left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "   left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "   left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            " where ipf.id = :idItemProposta ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemProposta", ic.getItemPropostaFornecedor().getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeEntregueDispensa(ItemContrato ic) {
            String sql = "" +
            " select coalesce(sum(quantidade), 0) as qtde_entregue from ( " +
            "   select distinct iem.quantidade as quantidade" +
            "   from ItemEntradaMaterial iem " +
            "   inner join itemcompramaterial icm on icm.itementradamaterial_id = iem.id " +
            "   inner join itemrequisicaodecompra irc on irc.id = icm.itemrequisicaodecompra_id " +
            "   inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = irc.id " +
            "   inner join execucaocontratoitem exitem on exitem.id = irce.execucaocontratoitem_id " +
            "   inner join itemcontrato ic on ic.id = exitem.itemcontrato_id " +
            "   inner join itemcontratoitempropdisp itemDisp on itemDisp.itemcontrato_id = ic.id " +
            " where itemDisp.itempropfornecdispensa_id = :idItemProposta) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemProposta", ic.getItemPropostaFornecedorDispensa().getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeEntregueRegistroPrecoExterno(ItemContrato ic) {
        String sql = "" +
            " select coalesce(sum(quantidade), 0) as qtde_entregue from ( " +
            "   select distinct iem.quantidade as quantidade" +
            "   inner join itemcompramaterial icm on icm.itementradamaterial_id = iem.id " +
            "   inner join itemrequisicaodecompra irc on irc.id = icm.itemrequisicaodecompra_id " +
            "   inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = irc.id " +
            "   inner join execucaocontratoitem exitem on exitem.id = irce.execucaocontratoitem_id " +
            "   inner join itemcontrato ic on ic.id = exitem.itemcontrato_id " +
            "   inner join itemcontratoitemsolext ise on ise.itemcontrato_id = ic.id " +
            " where ise.itemsolicitacaoexterno_id = :idItemSolExt) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemSolExt", ic.getItemSolicitacaoExterno().getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeOrValorUtilizadoEmOutrosContratosLicitacao(ItemProcessoDeCompra itemProcesso, Licitacao licitacao) {
        String sql = " " +
            "  select coalesce(case when ic.tipocontrole = :controleQuantidade " +
            "                  then sum(ic.quantidadetotalcontrato) " +
            "                  else sum(ic.valortotal) end, 0) " +
            "  from itemcontrato ic " +
            "  inner join contrato c on c.id = ic.contrato_id " +
            "  inner join conlicitacao cl on cl.contrato_id = c.id " +
            "  left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "  left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "  left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "  left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            "  where ipf.itemprocessodecompra_id = :idIpc " +
            "   and cl.licitacao_id = :idLicitacao ";
        sql += " group by ic.tipocontrole ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idIpc", itemProcesso.getId());
        q.setParameter("idLicitacao", licitacao.getId());
        q.setParameter("controleQuantidade", TipoControleLicitacao.QUANTIDADE.name());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarQuantidadeOrValorRescindido(ItemContrato itemCont) {
        if (itemCont.getItemAdequado().getItemProcessoCompra() == null) {
            return BigDecimal.ZERO;
        }
        String sql = " " +
            "  select coalesce(case when ic.tipocontrole = :controleQuantidade " +
            "                  then sum(mov.quantidade) " +
            "                  else sum(mov.valortotal) end, 0) as rescindido " +
            "   from itemcontrato ic " +
            "         inner join movimentoitemcontrato mov on mov.itemcontrato_id = ic.id " +
            "         inner join contrato c on c.id = ic.contrato_id " +
            "         left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "         left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "         left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "         left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
            "         left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id " +
            "         left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            "         inner join itemprocessodecompra ipc on ipc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
            "  where ipc.id in :idIpc " +
            "   and c.tipoaquisicao = :tipoAquisicao " +
            "   and mov.tipo = :tipoMovRescisao " +
            "   and mov.subtipo = :subTipoMovExec ";
        if (itemCont.getContrato() != null && itemCont.getContrato().getId() != null) {
            sql += " and c.id <> :idContrato ";
        }
        sql += " group by ic.tipocontrole ";
        Query q = em.createNativeQuery(sql);
        if (itemCont.getContrato() != null && itemCont.getContrato().getId() != null) {
            q.setParameter("idContrato", itemCont.getContrato().getId());
        }
        q.setParameter("idIpc", itemCont.getItemAdequado().getItemProcessoCompra().getId());
        q.setParameter("tipoAquisicao", itemCont.getContrato().getTipoAquisicao().name());
        q.setParameter("tipoMovRescisao", TipoSaldoItemContrato.RESCISAO.name());
        q.setParameter("subTipoMovExec", SubTipoSaldoItemContrato.EXECUCAO.name());
        q.setParameter("controleQuantidade", TipoControleLicitacao.QUANTIDADE.name());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public Boolean isUtilizadoEmOutroContratoLicitacaoPorValor(ItemProcessoDeCompra itemProcesso) {
        String sql = " " +
            "  select ic.* from itemcontrato ic " +
            "   left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "   left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "   left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "   inner join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            "  where ipf.itemprocessodecompra_id = :idIpc " +
            "   and ic.tipocontrole = :controleValor ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idIpc", itemProcesso.getId());
        q.setParameter("controleValor", TipoControleLicitacao.VALOR.name());
        return !q.getResultList().isEmpty();
    }

    public Boolean isUtilizadoEmOutroContratoDispensaPorValor(ItemPropostaFornecedorDispensa itemProcesso) {
        String sql = " " +
            "  select ic.* from itemcontrato ic " +
            "    inner join itemcontratoitempropdisp itemDisp on itemDisp.itemcontrato_id = ic.id " +
            "  where itemDisp.itempropfornecdispensa_id = :idItemProposta " +
            "  and ic.tipocontrole = :controleValor ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemProposta", itemProcesso.getId());
        q.setParameter("controleValor", TipoControleLicitacao.VALOR.name());
        return !q.getResultList().isEmpty();
    }

    public Boolean isUtilizadoEmOutroContratoAtaExternaPorValor(ItemSolicitacaoExterno itemProcesso) {
        String sql = "" +
            " select ic.* from itemcontrato ic " +
            "   inner join itemcontratoitemsolext ise on ise.itemcontrato_id = ic.id " +
            " where ise.itemsolicitacaoexterno_id = :idItemSolExt" +
            " and ic.tipocontrole = :controleValor ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemSolExt", itemProcesso.getId());
        q.setParameter("controleValor", TipoControleLicitacao.VALOR.name());
        return !q.getResultList().isEmpty();
    }

    public boolean getUtilizadoProcesso(ItemContrato itemContrato) {
        String sql = "select id from (" +
            "   select ic.id from itemcontrato ic  " +
            "   inner join execucaocontratoitem item on ic.id = item.itemcontrato_id " +
            "   where ic.id = :idItem " +
            "   union all " +
            "   select ic.id from itemcontrato ic " +
            "   inner join  movimentoalteracaocontitem item on ic.id = item.itemcontrato_id " +
            "   where ic.id = :idItem) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", itemContrato.getId());
        return !q.getResultList().isEmpty();
    }

    public ObjetoCompra getObjetoCompraContrato(Long idItemContrato) {
        String sql = " " +
            "   select oc.* from objetocompra oc " +
            "   inner join itemcontrato ic on ic.objetocompracontrato_id = oc.id " +
            " where ic.id = :idItem ";
        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("idItem", idItemContrato);
        try {
            return (ObjetoCompra) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
