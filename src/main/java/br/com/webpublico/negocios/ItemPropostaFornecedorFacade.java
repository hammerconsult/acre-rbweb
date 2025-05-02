/*
 * Codigo gerado automaticamente em Thu Dec 01 14:14:38 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemProcessoDeCompra;
import br.com.webpublico.entidades.ItemPropostaFornecedor;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.LotePropostaFornecedor;
import br.com.webpublico.enums.SituacaoItemCertame;
import br.com.webpublico.enums.TipoApuracaoLicitacao;
import br.com.webpublico.enums.TipoAvaliacaoLicitacao;
import br.com.webpublico.enums.TipoControleLicitacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ItemPropostaFornecedorFacade extends AbstractFacade<ItemPropostaFornecedor> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemPropostaFornecedorFacade() {
        super(ItemPropostaFornecedor.class);
    }

    public List<ItemProcessoDeCompra> recuperaItensProcessoDeCompra() {
        String hql = "from ItemProcessoDeCompra item order by item.id";
        Query consulta = em.createQuery(hql);
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<ItemProcessoDeCompra>();
        }
    }

    public List<ItemPropostaFornecedor> buscarPorLicitacao(Licitacao licitacao) {
        String sql = "select item                             "
            + "         from ItemPropostaFornecedor item      "
            + "   inner join item.lotePropostaFornecedor lote "
            + "   inner join lote.propostaFornecedor proposta "
            + "   inner join proposta.licitacao lic           "
            + "        where lic = :licitacao                 ";
        Query q = em.createQuery(sql);
        q.setParameter("licitacao", licitacao);
        if (q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhum item proposta fornecedor encontrado para licitação " + licitacao);
        }
        return q.getResultList();
    }

    public List<ItemPropostaFornecedor> buscarPorLoteProposta(LotePropostaFornecedor lote) {
        String sql = " select item.* from itempropfornec item      "
            + "        where item.lotepropostafornecedor_id = :idLote                 ";
        Query q = em.createNativeQuery(sql, ItemPropostaFornecedor.class);
        q.setParameter("idLote", lote.getId());
        if (q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhum item proposta fornecedor encontrado para o lote " + lote);
        }
        return q.getResultList();
    }

    public List<ItemPropostaFornecedor> buscarItemPropostaFornecedorVencedorDoPregao(Licitacao licitacao) {
        String hqlPregao = null;
        if (licitacao.getTipoApuracao().equals(TipoApuracaoLicitacao.ITEM)) {
            hqlPregao = "select itemprop"
                + " from ItemPregao item, ItemPropostaFornecedor itemprop"
                + " inner join item.itemPregaoItemProcesso itpre"
                + " inner join item.itemPregaoLanceVencedor iplv"
                + " inner join iplv.lancePregao lance"
                + " inner join lance.propostaFornecedor prop"
                + " inner join itpre.itemProcessoDeCompra itemprocesso"
                + " inner join item.pregao pregao"
                + " inner join pregao.licitacao lic"
                + "      where lic = :licitacao"
                + "        and iplv.status = 'VENCEDOR' "
                + "        and itemprop.propostaFornecedor = prop"
                + "        and itemprop.itemProcessoDeCompra = itemprocesso";
        } else {
            hqlPregao = "  select itemprop"
                + " from ItemPregao item, LotePropostaFornecedor loteprop, ItemPropostaFornecedor itemprop"
                + " inner join item.itemPregaoLoteProcesso lotepre"
                + " inner join lotepre.loteProcessoDeCompra lote"
                + " inner join item.itemPregaoLanceVencedor iplv"
                + " inner join iplv.lancePregao lance"
                + " inner join lance.propostaFornecedor prop"
                + " inner join item.pregao pregao"
                + " inner join pregao.licitacao lic"
                + " where lic = :licitacao"
                + "   and iplv.status = 'VENCEDOR'"
                + "   and loteprop.loteProcessoDeCompra = lote"
                + "   and loteprop.propostaFornecedor = prop"
                + "   and itemprop.lotePropostaFornecedor = loteprop";
        }

        Query consultaDoPregao = em.createQuery(hqlPregao);
        consultaDoPregao.setParameter("licitacao", licitacao);
        try {
            return consultaDoPregao.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<ItemPropostaFornecedor> buscarItemPropostaFornecedorVencedorDoCertame(Licitacao licitacao) {
        String hqlCertame = null;
        if (licitacao.getTipoApuracao().equals(TipoApuracaoLicitacao.ITEM)) {
            hqlCertame = "select itemprop"
                + " from ItemCertame item"
                + " inner join item.certame certame"
                + " inner join certame.licitacao lic"
                + "  left join item.itemCertameItemProcesso itemce"
                + "  left join itemce.itemPropostaVencedor itemprop"
                + "      where item.situacaoItemCertame = 'VENCEDOR'"
                + "        and lic = :licitacao";
        } else {
            hqlCertame = "select itemlote from ItemCertame item, ItemPropostaFornecedor itemlote"
                + " inner join item.certame certame"
                + " inner join certame.licitacao lic"
                + " left join item.itemCertameLoteProcesso lotece"
                + " left join lotece.lotePropFornecedorVencedor loteprop"
                + "     where item.situacaoItemCertame = 'VENCEDOR'"
                + "       and itemlote.lotePropostaFornecedor = loteprop"
                + "       and lic = :licitacao ";
        }
        Query consultaDoCertame = em.createQuery(hqlCertame);
        consultaDoCertame.setParameter("licitacao", licitacao);
        try {
            return consultaDoCertame.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public BigDecimal getValorUnitarioVencedorDoItemPropostaFornecedor(ItemPropostaFornecedor ipf) {
        BigDecimal valorUnitario;
        Licitacao lic = ipf.getPropostaFornecedor().getLicitacao();
        if (lic.isPregao() || lic.isRDCPregao()) {  // PREGÃO
            return getValorUnitarioVencedorDoItemPropostaFornecedorQuandoPregao(ipf);
        }
        valorUnitario = getValorUnitarioVencedorDoItemPropostaFornecedorQuandoCertame(ipf); // MAPA COMPARATIVO NORMAL
        if (valorUnitario != null && valorUnitario.compareTo(BigDecimal.ZERO) > 0) {
            return valorUnitario;
        }
        valorUnitario = getValorUnitarioVencedorDoItemPropostaFornecedorQuandoMapaComparativoTecnicaPreco(ipf); // MAPA COMPARATIVO TECNICA PREÇO
        return valorUnitario;
    }

    private BigDecimal getValorUnitarioVencedorDoItemPropostaFornecedorQuandoCertame(ItemPropostaFornecedor ipf) {
        String sql = "";
        Licitacao lic = ipf.getPropostaFornecedor().getLicitacao();
        if (lic.getTipoApuracao().isPorItem()) {
            sql = " select case " +
                "           when lic.tipoavaliacao = :maior_desconto " +
                "               then case " +
                "                        when ic.tipocontrole = :controle_valor then ism.preco " +
                "                        else round(ism.preco - ((ipf.percentualdesconto / 100) * ism.preco), 2) end " +
                "           else ipf.preco end as valor_unitario " +
                " from itemcertame item " +
                "         inner join certame on certame.id = item.certame_id " +
                "         inner join licitacao lic on lic.id = certame.licitacao_id " +
                "         inner join itemcertameitempro itemce on item.id = itemce.itemcertame_id " +
                "         inner join itempropfornec ipf on ipf.id = itemce.itempropostavencedor_id " +
                "         inner join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id " +
                "         inner join itemsolicitacao ism on ism.id = ipc.itemsolicitacaomaterial_id " +
                "         inner join itemcotacao ic on ic.id = ism.itemcotacao_id " +
                " where item.situacaoitemcertame = :vencedor " +
                "  and ipf.id = :idIitemProp " +
                "  and lic.id = :idLicitacao ";
        } else {
            sql = "select distinct ipf.preco valor_unitario " +
                "from itemcertame item " +
                "         inner join certame on certame.id = item.certame_id " +
                "         inner join licitacao lic on lic.id = certame.licitacao_id " +
                "         inner join itemcertamelotepro itemce on itemce.itemcertame_id = item.id " +
                "         inner join lotepropfornec lpf on lpf.id = itemce.lotepropfornecedorvencedor_id " +
                "         inner join itempropfornec ipf on ipf.lotepropostafornecedor_id = lpf.id " +
                "         inner join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id " +
                "         inner join itemsolicitacao ism on ism.id = ipc.itemsolicitacaomaterial_id " +
                "         inner join itemcotacao ic on ic.id = ism.itemcotacao_id " +
                " where item.situacaoitemcertame = :vencedor " +
                "  and ipf.id = :idIitemProp " +
                "  and lic.id = :idLicitacao ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLicitacao", lic.getId());
        q.setParameter("idIitemProp", ipf.getId());
        q.setParameter("vencedor", SituacaoItemCertame.VENCEDOR.name());
        if (ipf.getPropostaFornecedor().getLicitacao().getTipoApuracao().isPorItem()) {
            q.setParameter("maior_desconto", TipoAvaliacaoLicitacao.MAIOR_DESCONTO.name());
            q.setParameter("controle_valor", TipoControleLicitacao.VALOR.name());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getValorUnitarioVencedorDoItemPropostaFornecedorQuandoMapaComparativoTecnicaPreco(ItemPropostaFornecedor ipf) {
        String hqlCertame = "";
        Licitacao lic = ipf.getPropostaFornecedor().getLicitacao();
        if (lic.getTipoApuracao().isPorItem()) {
            hqlCertame = "select itemprop.preco"
                + " from ItemMapaComparativoTecnicaPreco item"
                + " inner join item.mapaComTecnicaPreco certame"
                + " inner join certame.licitacao lic"
                + "  left join item.itemMapaComparativoTecnicaPrecoItemProcesso itemce"
                + "  left join itemce.itemPropostaVencedor itemprop"
                + "  inner join itemprop.propostaFornecedor prop"
                + "      where item.situacaoItem = 'VENCEDOR'"
                + "        and itemprop = :ipf"
                + "        and lic = :licitacao";
        } else {
            hqlCertame = "select itemlote.preco from ItemMapaComparativoTecnicaPreco item, ItemPropostaFornecedor itemlote"
                + " inner join item.mapaComTecnicaPreco certame"
                + " inner join certame.licitacao lic"
                + " inner join itemlote.propostaFornecedor prop"
                + " left join item.itemMapaComparativoTecnicaPrecoLoteProcesso lotece"
                + " left join lotece.lotePropostaVencedor loteprop"
                + "     where item.situacaoItem = 'VENCEDOR'"
                + "       and itemlote.lotePropostaFornecedor = loteprop"
                + "       and itemlote = :ipf"
                + "       and lic = :licitacao";
        }
        Query consultaDoCertame = em.createQuery(hqlCertame);
        consultaDoCertame.setParameter("licitacao", lic);
        consultaDoCertame.setParameter("ipf", ipf);
        try {
            return (BigDecimal) consultaDoCertame.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getValorUnitarioVencedorDoItemPropostaFornecedorQuandoPregao(ItemPropostaFornecedor ipf) {
        String sql = "";
        if (ipf.getPropostaFornecedor().getLicitacao().getTipoApuracao().isPorItem()) {
            sql = "select " +
                "    case when l.tipoavaliacao = :maior_desconto " +
                "      then case ic.tipocontrole " +
                "        when :controle_valor then its.preco else round(its.preco - ((iplv.percentualdesconto / 100) * its.preco), 2) end " +
                "    else iplv.valor end     as valor_unitario " +
                " from itempregao ip " +
                "  inner join pregao p on p.id = ip.pregao_id" +
                "  inner join licitacao l on l.id = p.licitacao_id " +
                "  inner join itempregaolancevencedor iplv on iplv.id = ip.ITEMPREGAOLANCEVENCEDOR_ID " +
                "  inner join itpreitpro ipip on ipip.itempregao_id = ip.id " +
                "  inner join itemprocessodecompra ipc on ipc.id = ipip.itemprocessodecompra_id" +
                "  inner join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id " +
                "  inner join itemcotacao ic on ic.id = its.itemcotacao_id " +
                "where p.licitacao_id = :id_licitacao " +
                "  and ipc.id = :id_itemprocessocompra ";
        } else {
            if (ipf.getPropostaFornecedor().getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
                sql = "select distinct " +
                    "    case when l.tipoavaliacao = :maior_desconto " +
                    "      then case ic.tipocontrole " +
                    "        when :controle_valor then its.preco else iplip.valor end " +
                    "    else iplv.valor end     as valor_unitario " +
                    "   from itempregao ip " +
                    "  inner join pregao p on p.id = ip.pregao_id " +
                    "  inner join itempregaolancevencedor iplv on iplv.id = ip.ITEMPREGAOLANCEVENCEDOR_ID " +
                    "  inner join licitacao l on l.id = p.licitacao_id " +
                    "  inner join itprelotpro iplp on iplp.itempregao_id = ip.id " +
                    "  left join itempregaoloteitemprocesso iplip on iplip.itempregaoloteprocesso_id = iplp.id " +
                    "  left join itemprocessodecompra ipc on ipc.loteprocessodecompra_id = iplp.loteprocessodecompra_id " +
                    "  left join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id " +
                    "  left join itemcotacao ic on ic.id = its.itemcotacao_id " +
                    " where p.licitacao_id = :id_licitacao" +
                    "  and ipc.id = :id_itemprocessocompra ";
            } else {
                sql = "select distinct " +
                    "          case when ic.tipocontrole = 'QUANTIDADE' then iplip.valor else " +
                    "          case when (ipc.quantidade = 0 or ipc.quantidade is null) then 1*iplip.valor else ipc.quantidade*iplip.valor end end as valor " +
                    "   from itempregao ip " +
                    "  inner join pregao p on p.id = ip.pregao_id " +
                    "  inner join itempregaolancevencedor iplv on iplv.id = ip.ITEMPREGAOLANCEVENCEDOR_ID " +
                    "  left join itprelotpro iplp on iplp.itempregao_id = ip.id " +
                    "  left join itempregaoloteitemprocesso iplip on iplip.itempregaoloteprocesso_id = iplp.id " +
                    "  left join itemprocessodecompra ipc on ipc.id = iplip.itemprocessodecompra_id" +
                    "  left join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id " +
                    "  left join itemcotacao ic on ic.id = its.itemcotacao_id " +
                    " where p.licitacao_id = :id_licitacao" +
                    "  and iplip.itemprocessodecompra_id = :id_itemprocessocompra ";
            }
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_licitacao", ipf.getPropostaFornecedor().getLicitacao().getId());
        q.setParameter("id_itemprocessocompra", ipf.getItemProcessoDeCompra().getId());
        if (ipf.getPropostaFornecedor().getLicitacao().getTipoApuracao().isPorItem()
            || ipf.getPropostaFornecedor().getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            q.setParameter("maior_desconto", TipoAvaliacaoLicitacao.MAIOR_DESCONTO.name());
            q.setParameter("controle_valor", TipoControleLicitacao.VALOR.name());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }
}
