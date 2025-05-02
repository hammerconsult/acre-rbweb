/*
 * Codigo gerado automaticamente em Wed Nov 16 15:51:50 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
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
import java.util.List;

@Stateless
public class ProcessoDeCompraFacade extends AbstractFacade<ProcessoDeCompra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoSolicitacaoMaterialFacade;

    public ProcessoDeCompraFacade() {
        super(ProcessoDeCompra.class);
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

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }

    public DispensaDeLicitacaoFacade getDispensaDeLicitacaoFacade() {
        return dispensaDeLicitacaoFacade;
    }

    @Override
    public ProcessoDeCompra recuperar(Object id) {
        ProcessoDeCompra pc = super.recuperar(id);
        Hibernate.initialize(pc.getLotesProcessoDeCompra());
        for (LoteProcessoDeCompra loteProcesso : pc.getLotesProcessoDeCompra()) {
            Hibernate.initialize(loteProcesso.getItensProcessoDeCompra());
        }
        Hibernate.initialize(pc.getLicitacoes());
        return pc;
    }

    public ItemProcessoDeCompra buscarItensPropostaFornecedorPorItemProcessoDeCompra(Object id) {
        ItemProcessoDeCompra itemProcessoDeCompra = em.find(ItemProcessoDeCompra.class, id);
        itemProcessoDeCompra.getItensPropostaFornecedor().size();
        return itemProcessoDeCompra;
    }

    public Integer getProximoNumero() {
        Query q = em.createQuery("select max(numero) from " + ProcessoDeCompra.class.getSimpleName());
        q.setMaxResults(1);
        if (q.getSingleResult() == null) {
            return 1;
        } else {
            return (Integer) q.getSingleResult() + 1;
        }

    }

    public boolean recuperaCodigoExistente(ProcessoDeCompra processo) {
        String hql = "select numero from " + ProcessoDeCompra.class.getSimpleName() + " where numero= :numero";
        if (processo.getId() != null) {
            hql += " and id != :id";
        }

        Query query = em.createQuery(hql);

        query.setParameter("numero", processo.getNumero());
        if (processo.getId() != null) {
            query.setParameter("id", processo.getId());
        }
        try {
            if (query.getSingleResult() != null) {
                return true;
            }
        } catch (NoResultException e) {
            return false;
        }
        return false;
    }

    public List<LoteProcessoDeCompra> recuperarLotesProcesso(ProcessoDeCompra processoCompra) {
        String sql = " select lote.* from loteprocessodecompra lote where lote.processodecompra_id = :idProcesso ";
        Query query = em.createNativeQuery(sql, LoteProcessoDeCompra.class);
        query.setParameter("idProcesso", processoCompra);
        try {
            List<LoteProcessoDeCompra> lotes = query.getResultList();
            for (LoteProcessoDeCompra lpdc : lotes) {
                Hibernate.initialize(lpdc.getItensProcessoDeCompra());
            }
            return lotes;
        } catch (NoResultException ex) {
            return new ArrayList<>();
        }
    }

    public List<ItemProcessoDeCompra> recuperarItensDoLote(LoteProcessoDeCompra loteSelecionado) {
        String hql = " from " + ItemProcessoDeCompra.class.getSimpleName() + " item where item.loteProcessoDeCompra = :lote ";
        Query query = em.createQuery(hql);
        query.setParameter("lote", loteSelecionado);
        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            return new ArrayList<>();
        }
    }

    public List<ItemPropostaFornecedor> recuperarItemPropostaDoItemProcessoDeCompra(ItemProcessoDeCompra itemProcessoDeCompra) {
        String hql = " from " + ItemPropostaFornecedor.class.getSimpleName() + " item where item.itemProcessoDeCompra = :item ";
        Query query = em.createQuery(hql);
        query.setParameter("item", itemProcessoDeCompra);
        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            return new ArrayList<>();
        }
    }

    public boolean verificarVinculoComDispensaLicitacao(ProcessoDeCompra processo) {
        String sql = " select * from dispensadelicitacao " +
            "          where processodecompra_id = :idProcesso ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("idProcesso", processo.getId());
        query.setMaxResults(1);
        try {
            return query.getSingleResult() != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    public boolean verificarVinculoComLicitacao(ProcessoDeCompra processo) {
        String sql = " select l.* from licitacao l"
            + " where l.processodecompra_id = :idProcesso ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("idProcesso", processo.getId());
        query.setMaxResults(1);
        try {
            return query.getSingleResult() != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    public boolean hasProcessoComReservaDotacao(ProcessoDeCompra processo) {
        String sql = " select dsm.* from dotsolmat dsm " +
            "          inner join solicitacaomaterial sol on sol.id = dsm.solicitacaomaterial_id " +
            "          inner join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
            "          where pc.id = :idProcesso ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("idProcesso", processo.getId());
        query.setMaxResults(1);
        try {
            return query.getSingleResult() != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    public List<ProcessoDeCompra> buscarProcessoCompraSemProcessoLicitario(String parte, TipoProcessoDeCompra tipoProcessoDeCompra, UsuarioSistema usuarioSistema) {
        String sql = " select p.*  from processodecompra p  " +
            "           inner join exercicio e on e.id = p.exercicio_id  " +
            "          where (to_char(e.ano) like :ano_numero_descricao or to_char(p.numero) like :ano_numero_descricao or lower(p.descricao) like :ano_numero_descricao)  " +
            "           and p.tipoprocessodecompra = :tipo_processo_compra  " +
            "           and exists (select 1 from usuariounidadeorganizacio u_un  " +
            "               where u_un.usuariosistema_id = :id_usuario  " +
            "                 and u_un.unidadeorganizacional_id = p.unidadeorganizacional_id  " +
            "                 and u_un.gestorlicitacao = :gestor_licitacao) " +
            "           and not exists (select 1 from licitacao lic where lic.processodecompra_id = p.id)  " +
            "           and not exists (select 1 from dispensadelicitacao dl where dl.processodecompra_id = p.id)  " +
            "         order by e.ano desc, p.numero desc ";
        Query q = em.createNativeQuery(sql, ProcessoDeCompra.class);
        q.setParameter("ano_numero_descricao", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipo_processo_compra", tipoProcessoDeCompra.name());
        q.setParameter("id_usuario", usuarioSistema.getId());
        q.setParameter("gestor_licitacao", Boolean.TRUE);
        return q.getResultList();
    }

    public List<ItemProcessoDeCompra> getItensDoFornecedorNaLicitacao(Licitacao licitacao, Pessoa fornecedor) {
        String hql = " select ipc from ItemProcessoDeCompra ipc" +
            "  inner join ipc.itensPropostaFornecedor ipf" +
            "  inner join ipf.propostaFornecedor pf" +
            "  inner join ipc.loteProcessoDeCompra lpc" +
            "  inner join lpc.processoDeCompra pc" +
            "  inner join pc.licitacoes lic" +
            "       where lic = :licitacao" +
            "         and pf.fornecedor = :fornecedor order by ipc.loteProcessoDeCompra.numero, ipc.numero";


        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        q.setParameter("fornecedor", fornecedor);

        return q.getResultList();
    }

    // Vencedor - Empatado - Frustrado - Inexequivel
    public SituacaoItemCertame getStatusDoItemProcessoDeCompraNoCertame(Licitacao l, Pessoa p, ItemProcessoDeCompra ipc) {
        String hql = "";
        if (l.getTipoApuracao().equals(TipoApuracaoLicitacao.ITEM)) {
            hql = "select item.situacaoItemCertame"
                + " from ItemCertame item"
                + " inner join item.certame certame"
                + " inner join certame.licitacao lic"
                + "  left join item.itemCertameItemProcesso itemce"
                + "  left join itemce.itemPropostaVencedor itemprop"
                + " inner join itemprop.propostaFornecedor prop"
                + "      where itemprop.itemProcessoDeCompra = :ipc"
                + "        and prop.fornecedor = :contratado"
                + "        and lic = :licitacao";
        } else {
            hql = "select item.situacaoItemCertame from ItemCertame item, ItemPropostaFornecedor itemlote"
                + " inner join item.certame certame"
                + " inner join certame.licitacao lic"
                + " inner join itemlote.propostaFornecedor prop"
                + " left join item.itemCertameLoteProcesso lotece"
                + " left join lotece.lotePropFornecedorVencedor loteprop"
                + " inner join loteprop.itens ipfs"
                + " inner join ipfs.itemProcessoDeCompra ipc"
                + "     where itemlote.lotePropostaFornecedor = loteprop"
                + "        and prop.fornecedor = :contratado"
                + "        and lic = :licitacao"
                + "        and ipc = :ipc";
        }
        Query q = em.createQuery(hql);
        q.setParameter("contratado", p);
        q.setParameter("licitacao", l);
        q.setParameter("ipc", ipc);
        q.setMaxResults(1);
        try {
            return (SituacaoItemCertame) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    // Ativo - Cancelado - Declinado - Inexequível - Vencedor
    public StatusLancePregao getStatusDoItemProcessoDeCompraNoPregao(Licitacao l, Pessoa p, ItemProcessoDeCompra ipc) {
        String hql = "";

        if (l.getTipoApuracao().equals(TipoApuracaoLicitacao.ITEM)) {
            hql = " select iplv.status from ItemPregao ip" +
                "  inner join ip.pregao p" +
                "  inner join p.licitacao lic" +
                "  inner join ip.itemPregaoItemProcesso ipip" +
                "  inner join ipip.itemProcessoDeCompra ipc" +
                "  inner join ipc.itensPropostaFornecedor ipf" +
                "  inner join ipf.propostaFornecedor pf" +
                "  inner join ip.listaDeRodadaPregao rp" +
                "  inner join ip.itemPregaoLanceVencedor iplv " +
                "  inner join iplv.lancePregao lp" +
                "       where lic = :licitacao" +
                "         and lp.propostaFornecedor = pf" +
                "         and pf.fornecedor = :contratado"
                + "       and ipc = :ipc" +
                "    order by rp.numero desc";
        } else {
            hql = " select iplv.status from ItemPregao ip" +
                "  inner join ip.pregao p" +
                "  inner join p.licitacao lic" +
                "  inner join ip.itemPregaoLoteProcesso iplp" +
                "  inner join iplp.loteProcessoDeCompra lpc" +
                "  inner join lpc.itensProcessoDeCompra ipc" +
                "  inner join ipc.itensPropostaFornecedor ipf" +
                "  inner join ipf.propostaFornecedor pf" +
                "  inner join ip.listaDeRodadaPregao rp" +
                "  inner join ip.itemPregaoLanceVencedor iplv" +
                "  inner join iplv.lancePregao lp" +
                "       where lic = :licitacao" +
                "         and lp.propostaFornecedor = pf" +
                "         and pf.fornecedor = :contratado" +
                "         and ipc = :ipc" +
                "    order by rp.numero desc";
        }
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", l);
        q.setParameter("contratado", p);
        q.setParameter("ipc", ipc);
        q.setMaxResults(1);
        try {
            return (StatusLancePregao) q.getSingleResult();
        } catch (NoResultException nre) {
            return StatusLancePregao.NAO_REALIZADO;
        }
    }

    // Habilitado - Habilitado com Ressalva - Inabilitado - Desclassificado - Ausente
    public TipoClassificacaoFornecedor getSituacaoDoFornecedorDoItemNoCertame(Licitacao l, Pessoa p, ItemProcessoDeCompra ipc) {
        String hql = "";

        hql = "select lf.tipoClassificacaoFornecedor from Licitacao lic"
            + " inner join lic.fornecedores lf" +
            "   inner join lf.empresa emp" +
            "   where emp  = :contratado"
            + "        and lic = :licitacao";

        Query q = em.createQuery(hql);
        q.setParameter("contratado", p);
        q.setParameter("licitacao", l);

        try {
            return (TipoClassificacaoFornecedor) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    // Habilitado - Habilitado com Ressalva - Inabilitado - Desclassificado - Ausente
    public TipoClassificacaoFornecedor getSituacaoDoFornecedorDoItemNoPregao(Licitacao l, Pessoa p, ItemProcessoDeCompra ipc) {
        String hql = "";

        if (l.getTipoApuracao().equals(TipoApuracaoLicitacao.ITEM)) {
            hql = " select ip.statusFornecedorVencedor from ItemPregao ip" +
                "  inner join ip.pregao p" +
                "  inner join p.licitacao lic" +
                "  inner join ip.itemPregaoItemProcesso ipip" +
                "  inner join ipip.itemProcessoDeCompra ipc" +
                "  inner join ipc.itensPropostaFornecedor ipf" +
                "  inner join ipf.propostaFornecedor pf" +
                "       where lic = :licitacao" +
                "         and pf.fornecedor = :contratado" +
                "         and ipc = :ipc";
        } else {
            hql = " select ip.statusFornecedorVencedor from ItemPregao ip" +
                "  inner join ip.pregao p" +
                "  inner join p.licitacao lic" +
                "  inner join ip.itemPregaoLoteProcesso iplp" +
                "  inner join iplp.loteProcessoDeCompra lpc" +
                "  inner join lpc.itensProcessoDeCompra ipc" +
                "  inner join ipc.itensPropostaFornecedor ipf" +
                "  inner join ipf.propostaFornecedor pf" +
                "  inner join ip.itemPregaoLanceVencedor lpv" +
                "       where lic = :licitacao" +
                "         and pf.fornecedor = :contratado" +
                "         and ipc = :ipc";
        }

        Query q = em.createQuery(hql);
        q.setParameter("licitacao", l);
        q.setParameter("contratado", p);
        q.setParameter("ipc", ipc);

        try {
            return (TipoClassificacaoFornecedor) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public SolicitacaoMaterial recuperarSolicitacaoMaterial(ProcessoDeCompra processoDeCompra) {
        String sql = "select distinct solicitacaomaterial.*\n" +
            "   from processodecompra\n" +
            "  inner join loteprocessodecompra on loteprocessodecompra.processodecompra_id = processodecompra.id\n" +
            "  inner join itemprocessodecompra on itemprocessodecompra.loteprocessodecompra_id = loteprocessodecompra.id\n" +
            "  inner join itemsolicitacao on itemprocessodecompra.itemsolicitacaomaterial_id = itemsolicitacao.id\n" +
            "  inner join lotesolicitacaomaterial on itemsolicitacao.lotesolicitacaomaterial_id = lotesolicitacaomaterial.id\n" +
            "  inner join solicitacaomaterial on lotesolicitacaomaterial.solicitacaomaterial_id = solicitacaomaterial.id\n" +
            "where processodecompra.id = :processo_id ";
        Query q = em.createNativeQuery(sql, SolicitacaoMaterial.class);
        q.setParameter("processo_id", processoDeCompra.getId());
        return !q.getResultList().isEmpty() ? (SolicitacaoMaterial) q.getResultList().get(0) : null;
    }

    public ProcessoDeCompra recuperarProcessoCompraPorSolicitacao(Long idSolicitacao) {
        String sql = " select pc.* from processodecompra pc where pc.solicitacaomaterial_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql, ProcessoDeCompra.class);
        q.setParameter("idSolicitacao", idSolicitacao);
        return q.getResultList() != null && !q.getResultList().isEmpty() ? (ProcessoDeCompra) q.getSingleResult() : null;
    }

    public List<ProcessoDeCompra> buscarProcessosDeCompraPorUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        String sql = "select pc.* \n" +
            "   from processodecompra pc\n " +
            "where exists(select 1\n" +
            "                from hierarquiaorgresp \n" +
            "               inner join vwhierarquiaadministrativa vwadm   on vwadm.id = hierarquiaorgresp.hierarquiaorgadm_id \n" +
            "               inner join vwhierarquiaorcamentaria vworc     on vworc.id = hierarquiaorgresp.hierarquiaorgorc_id \n" +
            "             where :dataatual between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia,:dataatual) \n" +
            "               and :dataatual between vworc.iniciovigencia and coalesce(vworc.fimvigencia,:dataatual) \n" +
            "               and :dataatual between hierarquiaorgresp.datainicio and coalesce(hierarquiaorgresp.datafim,:dataatual)\n" +
            "               and vwadm.subordinada_id = pc.unidadeorganizacional_id\n" +
            "               and vworc.subordinada_id = :unidade_orcamentaria) " +
            "  and (exists(select 1 from licitacao lic where lic.processodecompra_id = pc.id) or exists(select 1 from dispensadelicitacao disp where disp.processodecompra_id = pc.id))";
        Query q = em.createNativeQuery(sql, ProcessoDeCompra.class);
        q.setParameter("dataatual", sistemaFacade.getDataOperacao());
        q.setParameter("unidade_orcamentaria", unidadeOrcamentaria.getId());
        return q.getResultList();
    }

    @Override
    public ProcessoDeCompra salvarRetornando(ProcessoDeCompra entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(getProximoNumero());
        }
        return super.salvarRetornando(entity);
    }

    public List<LoteProcessoDeCompra> buscarLotesProcessoCompra(ProcessoDeCompra processoDeCompra) {
        String sql = "select lote.* from loteprocessodecompra lote " +
            "         where lote.processodecompra_id = :idLote " +
            "         order by lote.numero ";
        Query q = em.createNativeQuery(sql, LoteProcessoDeCompra.class);
        q.setParameter("idLote", processoDeCompra.getId());
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return Lists.newArrayList();
        }
    }

    public List<ItemProcessoDeCompra> buscarItensProcessoCompraPorLote(LoteProcessoDeCompra loteProcessoDeCompra) {
        String sql = "select item.* from  itemprocessodecompra item " +
            "         where item.loteprocessodecompra_id = :idLote " +
            "         order by item.numero ";
        Query q = em.createNativeQuery(sql, ItemProcessoDeCompra.class);
        q.setParameter("idLote", loteProcessoDeCompra.getId());
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return Lists.newArrayList();
        }
    }

    public List<ItemProcessoDeCompra> buscarItensProcessoCompra(ProcessoDeCompra processoDeCompra) {
        String sql = "select itempro.* from loteprocessodecompra lotepro " +
            "         join itemprocessodecompra itempro on itempro.loteprocessodecompra_id = lotepro.id " +
            "         where lotepro.processodecompra_id = :idProcesso ";
        Query q = em.createNativeQuery(sql, ItemProcessoDeCompra.class);
        q.setParameter("idProcesso", processoDeCompra.getId());
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return Lists.newArrayList();
        }
    }

    public TipoControleLicitacao getTipoControleProcesso(LoteProcessoDeCompra lote) {
        String hql = " select ic.tipoControle from ItemProcessoDeCompra ipc " +
            "           inner join ipc.itemSolicitacaoMaterial ism " +
            "           inner join ism.itemCotacao ic " +
            "          where ipc.loteProcessoDeCompra = :lote ";
        Query q = em.createQuery(hql, TipoControleLicitacao.class);
        q.setParameter("lote", lote);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (TipoControleLicitacao) q.getSingleResult();
    }

    public Long recuperarIdLicitacaoOrDispensa(ProcessoDeCompra processo) {
        String sql = " select case when disp.id is not null then disp.id else lic.id end as id_processo " +
            "          from processodecompra pc" +
            "           left join dispensadelicitacao disp on disp.processodecompra_id = pc.id  " +
            "           left join licitacao lic on lic.processodecompra_id = pc.id  " +
            "          where pc.id = :idProcesso ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("idProcesso", processo.getId());
        query.setMaxResults(1);
        try {
            return ((BigDecimal )query.getSingleResult()).longValue();
        } catch (NoResultException e) {
            return null;
        }
    }

    public SolicitacaoMaterialFacade getSolicitacaoMaterialFacade() {
        return solicitacaoMaterialFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public DotacaoSolicitacaoMaterialFacade getDotacaoSolicitacaoMaterialFacade() {
        return dotacaoSolicitacaoMaterialFacade;
    }
}
