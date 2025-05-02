/*
 * Codigo gerado automaticamente em Tue Dec 13 12:09:35 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.licitacao.ItemPregaoLanceVencedor;
import br.com.webpublico.enums.SituacaoItemProcessoDeCompra;
import br.com.webpublico.enums.TipoApuracaoLicitacao;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoStatusItemPregao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.administrativo.dao.JdbcPregaoDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class PregaoFacade extends AbstractFacade<Pregao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PropostaFornecedorFacade propostaFornecedorFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    private SingletonGeradorId singletonGeradorId;
    private JdbcPregaoDAO jdbcPregaoDAO;

    public PregaoFacade() {
        super(Pregao.class);
        singletonGeradorId = (SingletonGeradorId) Util.getSpringBeanPeloNome("singletonGeradorId");
        jdbcPregaoDAO = (JdbcPregaoDAO) Util.getSpringBeanPeloNome("jdbcPregaoDAO");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Pregao recuperarPregaoPorLote(Object id) {
        Pregao pregao = super.recuperar(id);
        try {
            pregao.setListaDeItemPregao(buscarItensPregao(pregao));
            Hibernate.initialize(pregao.getListaDeIntencaoDeRecursos());
            for (ItemPregao itemPregao : pregao.getListaDeItemPregao()) {
                if (itemPregao.getItemPregaoLoteProcesso() != null) {
                    Hibernate.initialize(itemPregao.getItemPregaoLoteProcesso().getItensPregaoLoteItemProcesso());
                }
            }
            Util.ordenarListas(pregao.getListaDeItemPregao());
        } catch (Exception e) {
            logger.debug("recuperarPregao {}", e);
        }
        return pregao;
    }

    public Pregao recuperarPregao(Object id) {
        Pregao pregao = super.recuperar(id);
        try {
            pregao.setListaDeItemPregao(buscarItensPregao(pregao));
            Hibernate.initialize(pregao.getListaDeIntencaoDeRecursos());
            Util.ordenarListas(pregao.getListaDeItemPregao());
        } catch (Exception e) {
            logger.debug("recuperarPregao {}", e);
        }
        return pregao;
    }

    public ItemPregao recuperarItemPregao(Object id) {
        ItemPregao itemPregao = em.find(ItemPregao.class, id);
        try {
            Hibernate.initialize(itemPregao.getListaDeRodadaPregao());
        } catch (Exception e) {
            logger.debug("recuperarItemPregao {}", e);
        }
        return itemPregao;
    }

    public ItemPregao recuperarItemPregaoLoteProcesso(Object id) {
        ItemPregao itemPregao = em.find(ItemPregao.class, id);
        try {
            if (itemPregao.getItemPregaoLoteProcesso() !=null) {
                Hibernate.initialize(itemPregao.getItemPregaoLoteProcesso().getItensPregaoLoteItemProcesso());
            }
        } catch (Exception e) {
            logger.debug("recuperarItemPregaoLoteProcesso {}", e);
        }
        return itemPregao;
    }

    public ItemPregao buscarRodadasAndLancesPregao(ItemPregao itemPregao) {
        try {
            List<RodadaPregao> rodadas = buscarRodadasPregao(itemPregao);
            for (RodadaPregao rodada : rodadas) {
                rodada.setListaDeLancePregao(buscarLancesPregao(rodada));
            }
            itemPregao.setListaDeRodadaPregao(rodadas);
        } catch (Exception e) {
            logger.debug("recuperarLancePregao {}", e);
        }
        return itemPregao;
    }

    public void cancelarItensPregao(List<ItemPregao> itensNaoCotados, TipoStatusItemPregao tipoStatusItemPregao) {
        for (ItemPregao itemPregao : itensNaoCotados) {
            itemPregao.setTipoStatusItemPregao(tipoStatusItemPregao);
            itemPregao = em.merge(itemPregao);
            atualizarSituacaoDoItemProcessoDeCompra(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra(), SituacaoItemProcessoDeCompra.FRUSTRADO);
        }
    }

    public void anularItensPregao(ItemPregao itemPregao) {
        updateItemPregao(itemPregao);
        atualizarSituacaoDoItemProcessoDeCompra(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra(), SituacaoItemProcessoDeCompra.FRUSTRADO);
    }

    public void anularLotesPregao(ItemPregao itemPregao) {
        updateItemPregao(itemPregao);
        atualizarSituacaoDoLoteProcessoDeCompra(itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra(), SituacaoItemProcessoDeCompra.FRUSTRADO);
    }

    private void atualizarSituacaoDoLoteProcessoDeCompra(LoteProcessoDeCompra lpc, SituacaoItemProcessoDeCompra situacao) {
        lpc = em.find(LoteProcessoDeCompra.class, lpc.getId());
        for (ItemProcessoDeCompra ipdc : lpc.getItensProcessoDeCompra()) {
            if (ipdc != null) {
                ipdc.setSituacaoItemProcessoDeCompra(situacao);
                em.merge(ipdc);
            }
        }
    }

    public void atualizarItemProcessoDeCompraComItemPregao(ItemPregao itemPregao) {
        if (itemPregao.getTipoStatusItemPregao() != null) {
            switch (itemPregao.getTipoStatusItemPregao()) {
                case EM_ANDAMENTO:
                case FINALIZADO:
                    if (itemPregao.getItemPregaoItemProcesso() != null) {
                        atualizarSituacaoDoItemProcessoDeCompra(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra(), SituacaoItemProcessoDeCompra.AGUARDANDO);
                    } else {
                        atualizarSituacaoDoLoteProcessoDeCompra(itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra(), SituacaoItemProcessoDeCompra.AGUARDANDO);
                    }
                    break;
                default:
                    if (itemPregao.getItemPregaoItemProcesso() != null) {
                        atualizarSituacaoDoItemProcessoDeCompra(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra(), SituacaoItemProcessoDeCompra.FRUSTRADO);
                    } else {
                        atualizarSituacaoDoLoteProcessoDeCompra(itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra(), SituacaoItemProcessoDeCompra.FRUSTRADO);
                    }
                    break;
            }
        }
    }

    private void atualizarSituacaoDoItemProcessoDeCompra(ItemProcessoDeCompra ipdc, SituacaoItemProcessoDeCompra situacao) {
        if (ipdc != null) {
            ipdc.setSituacaoItemProcessoDeCompra(situacao);
            em.merge(ipdc);
        }
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Pregao salvarPregao(Pregao pregao) {
        String ip = SistemaFacade.obtemIp();
        UsuarioSistema usuarioSistema = sistemaFacade.getUsuarioCorrente();
        if (pregao.getId() == null) {
            jdbcPregaoDAO.insertPregao(pregao, ip, usuarioSistema);
        } else {
            jdbcPregaoDAO.updatePregao(pregao, ip, usuarioSistema);
        }
        return pregao;
    }

    public Pregao salvarItencaoRecurso(Pregao pregao) {
        pregao = em.merge(pregao);
        return pregao;
    }

    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public Pregao salvarPregaoPorLote(Pregao entity) {
        try {
            String ip = SistemaFacade.obtemIp();
            UsuarioSistema usuarioSistema = sistemaFacade.getUsuarioCorrente();
            for (ItemPregao itemPregao : entity.getListaDeItemPregao()) {
                for (ItemPregaoLoteItemProcesso itemLote : itemPregao.getItemPregaoLoteProcesso().getItensPregaoLoteItemProcesso()) {
                    if (itemLote != null && itemLote.getId() == null) {
                        jdbcPregaoDAO.insertItemPregaoLoteItemProcesso(itemLote, ip, usuarioSistema);
                    } else {
                        jdbcPregaoDAO.updateItemPregaoLoteItemProcesso(itemLote, ip, usuarioSistema);
                    }
                }
            }
            jdbcPregaoDAO.updatePregao(entity, ip, usuarioSistema);
            for (ItemPregao itemPregao : entity.getListaDeItemPregao()) {
                atualizarItemProcessoDeCompraComItemPregao(itemPregao);
            }
            return entity;
        } catch (Exception e) {
            logger.error("salvarPregaoPorLote {}", e);
        }
        return null;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public ItemPregao salvarItemPregaoPorItem(Pregao pregao, ItemPregao itemPregaoSelecionado) {
        try {
            inserirItensPregaoPorItem(pregao.getListaDeItemPregao(), pregao);
            for (ItemPregao itemPregao : pregao.getListaDeItemPregao()) {
                if (itemPregao.equals(itemPregaoSelecionado)) {
                    itemPregaoSelecionado = em.find(ItemPregao.class, itemPregao.getId());
                    Hibernate.initialize(itemPregaoSelecionado.getListaDeRodadaPregao());
                }
            }
            return itemPregaoSelecionado;
        } catch (Exception e) {
            logger.error("salvarItemPregaoPorItem {}", e);
        }
        return null;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public ItemPregao salvarItemPregaoPorLote(Pregao pregao, ItemPregao itemPregaoSelecionado) {
        try {
            inserirItensPregaoPorLote(pregao.getListaDeItemPregao(), pregao);
            for (ItemPregao itemPregao : pregao.getListaDeItemPregao()) {
                if (itemPregao.equals(itemPregaoSelecionado)) {
                    itemPregaoSelecionado = em.find(ItemPregao.class, itemPregao.getId());
                    Hibernate.initialize(itemPregaoSelecionado.getListaDeRodadaPregao());
                }
            }
            return itemPregaoSelecionado;
        } catch (Exception e) {
            logger.error("salvarItemPregaoPorLote {}", e);
        }
        return null;
    }

    public ItemPregao salvarItemRodadaEspecifica(ItemPregao item, RodadaPregao rodadaPregao) {
        String ip = SistemaFacade.obtemIp();
        UsuarioSistema usuarioSistema = sistemaFacade.getUsuarioCorrente();
        if (rodadaPregao.getId() == null) {
            rodadaPregao.setId(singletonGeradorId.getProximoId());
            jdbcPregaoDAO.insertRodadaPregao(rodadaPregao, ip, usuarioSistema);

            for (LancePregao lancePregao : rodadaPregao.getListaDeLancePregao()) {
                lancePregao.setRodadaPregao(rodadaPregao);
                jdbcPregaoDAO.insertLancePregao(lancePregao, ip, usuarioSistema);
            }
        } else {
            jdbcPregaoDAO.updateRodadaPregao(rodadaPregao, ip, usuarioSistema);
            for (LancePregao lancePregao : rodadaPregao.getListaDeLancePregao()) {
                jdbcPregaoDAO.updateLancePregao(lancePregao, ip, usuarioSistema);
            }
        }
        updateItemPregao(item);
        atualizarItemProcessoDeCompraComItemPregao(item);
        return item;
    }

    public ItemPregao updateItemPregao(ItemPregao item) {
        String ip = SistemaFacade.obtemIp();
        UsuarioSistema usuarioSistema = sistemaFacade.getUsuarioCorrente();
        if (item.getItemPregaoLanceVencedor() != null) {
            if (item.getItemPregaoLanceVencedor().getId() == null) {
                jdbcPregaoDAO.insertItemPregaoLanceVencedor(item.getItemPregaoLanceVencedor(), ip, usuarioSistema);
            } else {
                jdbcPregaoDAO.updateItemPregaoLanceVencedor(item.getItemPregaoLanceVencedor(), ip, usuarioSistema);
            }
        }
        jdbcPregaoDAO.updateItemPregao(item, ip, usuarioSistema);
        return item;
    }

    private void inserirItensPregaoPorItem(List<ItemPregao> itensPregao, Pregao pregao) {
        String ip = SistemaFacade.obtemIp();
        UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();
        for (ItemPregao itemPregao : itensPregao) {
            itemPregao.setPregao(pregao);
            itemPregao.setId(singletonGeradorId.getProximoId());
            if (itemPregao.getItemPregaoLanceVencedor() != null) {
                jdbcPregaoDAO.insertItemPregaoLanceVencedor(itemPregao.getItemPregaoLanceVencedor(), ip, usuarioCorrente);
            }
            jdbcPregaoDAO.insertItemPregao(itemPregao, ip, usuarioCorrente);

            jdbcPregaoDAO.insertItemPregaoItemProcesso(itemPregao.getItemPregaoItemProcesso(), ip, usuarioCorrente);
        }
    }

    private void inserirItensPregaoPorLote(List<ItemPregao> itensPregao, Pregao pregao) {
        String ip = SistemaFacade.obtemIp();
        UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();

        for (ItemPregao itemPregao : itensPregao) {
            itemPregao.setPregao(pregao);
            itemPregao.setId(singletonGeradorId.getProximoId());
            if (itemPregao.getItemPregaoLanceVencedor() != null) {
                jdbcPregaoDAO.insertItemPregaoLanceVencedor(itemPregao.getItemPregaoLanceVencedor(), ip, usuarioCorrente);
            }
            jdbcPregaoDAO.insertItemPregao(itemPregao, ip, usuarioCorrente);
            jdbcPregaoDAO.insertItemPregaoLoteProcesso(itemPregao.getItemPregaoLoteProcesso(), ip, usuarioCorrente);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public void excluirPregao(Pregao entity) {
        try {
            for (ItemPregao itemPregao : entity.getListaDeItemPregao()) {
                if (itemPregao.getItemPregaoItemProcesso() != null) {
                    atualizarSituacaoDoItemProcessoDeCompra(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra(), SituacaoItemProcessoDeCompra.AGUARDANDO);
                } else {
                    atualizarSituacaoDoLoteProcessoDeCompra(itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra(), SituacaoItemProcessoDeCompra.AGUARDANDO);
                }
            }
            em.remove(em.find(Pregao.class, entity.getId()));
        } catch (Exception ex) {
            logger.error("Erro ao remover pregão{}", ex);
        }
    }

    public void excluirRodadaAndLances(RodadaPregao rodadaPregao, ItemPregao itemPregao) {
        if (rodadaPregao.getId() != null) {
            String ip = SistemaFacade.obtemIp();
            UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();

            LancePregao lanceVencedor = rodadaPregao.getLanceVencedor();
            if (lanceVencedor != null) {
                ItemPregaoLanceVencedor iplv = itemPregao.getItemPregaoLanceVencedor();
                itemPregao.setItemPregaoLanceVencedor(null);
                jdbcPregaoDAO.updateItemPregao(itemPregao, ip, usuarioCorrente);
                jdbcPregaoDAO.deleteItemPregaoLanceVencedor(iplv, ip, usuarioCorrente);
            }
            for (LancePregao lancePregao : rodadaPregao.getListaDeLancePregao()) {
                jdbcPregaoDAO.deleteLancePregao(lancePregao, ip, usuarioCorrente);
            }
            jdbcPregaoDAO.deleteRodadaPregao(rodadaPregao, ip, usuarioCorrente);
        }
    }

    public List<ItemProcessoDeCompra> buscarItensProcessoCompraLicitacao(Licitacao licitacao) {
        String sql = " select ipc.* from itemprocessodecompra ipc " +
            " inner join loteprocessodecompra lpc on ipc.loteprocessodecompra_id = lpc.id " +
            " inner join processodecompra pc on lpc.processodecompra_id = pc.id " +
            " inner join licitacao lic on lic.processodecompra_id = pc.id " +
            " where lic.id = :idLicitacao " +
            " order by lpc.numero, ipc.numero ";
        Query q = em.createNativeQuery(sql, ItemProcessoDeCompra.class);
        q.setParameter("idLicitacao", licitacao.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<ItemPregao> recuperarListaDeItemPregao(Pregao pregao) {
        String hql = " from ItemPregao ip"
            + "   where ip.pregao = :param";

        Query q = em.createQuery(hql);
        q.setParameter("param", pregao);
        List<ItemPregao> lista = q.getResultList();

        for (ItemPregao itemPregao : lista) {
            itemPregao.getListaDeRodadaPregao().size();
            for (RodadaPregao rodadaPregao : itemPregao.getListaDeRodadaPregao()) {
                rodadaPregao.getListaDeLancePregao().size();

                for (LancePregao lancePregao : rodadaPregao.getListaDeLancePregao()) {
                    lancePregao.getPropostaFornecedor().getListaDeItensPropostaFornecedor().size();
                }
            }
            if (itemPregao.getItemPregaoLoteProcesso() != null) {
                itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getItensProcessoDeCompra().size();
            }
        }

        return lista;
    }

    public List<ItemPregaoLanceVencedor> buscarItensPregaoLanceVencedor(ItemPregao itemPregao) {
        String sql = " select item.* from itempregaolancevencedor item where item.itempregao_id = :idItemPregao";
        Query q = em.createNativeQuery(sql, ItemPregaoLanceVencedor.class);
        q.setParameter("idItemPregao", itemPregao.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<ItemPregao> buscarItensPregaoVencedorPorFornecedor(Licitacao licitacao, Pessoa fornecedor) {
        String sql = " " +
            " select ip.* from itempregaolancevencedor iplv " +
            "   inner join itempregao ip on ip.itempregaolancevencedor_id = iplv.id " +
            "   inner join pregao p on p.id = ip.pregao_id " +
            "   inner join lancepregao lance on lance.id = iplv.lancepregao_id " +
            "   inner join propostafornecedor pf on pf.id = lance.propostafornecedor_id " +
            " where pf.fornecedor_id = :idFornecedor " +
            " and p.licitacao_id = :idLicitacao ";
        Query q = em.createNativeQuery(sql, ItemPregao.class);
        q.setParameter("idFornecedor", fornecedor.getId());
        q.setParameter("idLicitacao", licitacao.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }

        return Lists.newArrayList();
    }

    public List<ItemPregao> buscarItensPregao(Pregao pregao) {
        String sql = " select ip.* from ItemPregao ip where ip.pregao_id = :param";
        Query q = em.createNativeQuery(sql, ItemPregao.class);
        q.setParameter("param", pregao.getId());
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PropostaFornecedor> buscarPropostaLancePregao(ItemPregao itemPregao) {
        String sql = " select distinct pf.* from propostafornecedor pf " +
            "               inner join lancepregao lp on lp.propostafornecedor_id = pf.id" +
            "               inner join rodadapregao rod on  rod.id = lp.rodadapregao_id " +
            "           where rod.itempregao_id = :idItemPregao ";
        Query q = em.createNativeQuery(sql, PropostaFornecedor.class);
        q.setParameter("idItemPregao", itemPregao.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public LancePregao buscarValorLanceUltimaRodada(PropostaFornecedor propostaFornecedor, ItemPregao itemPregao) {
        String sql = " select lp.* from lancepregao lp " +
            "          inner join rodadapregao rod on rod.id = lp.rodadapregao_id " +
            "           where rod.itempregao_id = :idItemPregao " +
            "           and lp.propostafornecedor_id = :idProposta ";
        sql += "      order by rod.numero desc ";
        Query q = em.createNativeQuery(sql, LancePregao.class);
        q.setParameter("idProposta", propostaFornecedor.getId());
        q.setParameter("idItemPregao", itemPregao.getId());
        q.setMaxResults(1);
        try {
            return (LancePregao) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("O fornecedor " + propostaFornecedor + " não possui lance para o item " + itemPregao.getNumeroItem());
        }
    }

    public List<ItemPregaoLoteItemProcesso> buscarItensPregaoLoteItemProcesso(ItemPregao itemPregao) {
        String sql = " select item.* from ItemPregaoLoteItemProcesso item " +
            "          inner join itprelotpro lote on lote.id = item.itempregaoloteprocesso_id " +
            "          where lote.itempregao_id = :idItemPregao";
        Query q = em.createNativeQuery(sql, ItemPregaoLoteItemProcesso.class);
        q.setParameter("idItemPregao", itemPregao.getId());
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<RodadaPregao> buscarRodadasPregao(ItemPregao itemPregao) {
        String sql = " select rod.* from rodadaPregao rod where rod.itempregao_id = :param";
        Query q = em.createNativeQuery(sql, RodadaPregao.class);
        q.setParameter("param", itemPregao.getId());
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<LancePregao> buscarLancesPregao(RodadaPregao rodadaPregao) {
        String sql = " select lance.* from lancepregao lance where lance.rodadapregao_id = :param";
        Query q = em.createNativeQuery(sql, LancePregao.class);
        q.setParameter("param", rodadaPregao.getId());
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<LancePregao> buscarLancesPregaoPorItemPregao(ItemPregao itemPregao) {
        String sql = " select lance.* from lancepregao lance " +
            "           inner join rodadapregao rod on rod.id = lance.rodadapregao_id" +
            "          where rod.itempregao_id = :idItem";
        Query q = em.createNativeQuery(sql, LancePregao.class);
        q.setParameter("idItem", itemPregao.getId());
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public RodadaPregao recuperarRodadaItemPregaoPorNumero(Integer numeroRodada, ItemPregao itemPregao) {
        String sql = " select rp.* from rodadapregao rp " +
            "          where rp.NUMERO = :numeroRodada " +
            "           and rp.ITEMPREGAO_ID = :idItem ";
        Query q = em.createNativeQuery(sql, RodadaPregao.class);
        q.setParameter("numeroRodada", numeroRodada);
        q.setParameter("idItem", itemPregao.getId());
        q.setMaxResults(1);
        try {
            return (RodadaPregao) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void atribuirValorPrimeiraPropostaNoLance(LancePregao lance, List<ItemPropostaFornecedor> itensProposta) {
        try {
            Licitacao licitacao = lance.getRodadaPregao().getItemPregao().getPregao().getLicitacao();
            for (ItemPropostaFornecedor ipf : itensProposta) {
                if (ipf.getPropostaFornecedor().getFornecedor().equals(lance.getPropostaFornecedor().getFornecedor())) {
                    BigDecimal valor = licitacao.getTipoAvaliacao().isMaiorDesconto() ? ipf.getPercentualDesconto() : ipf.getPreco();
                    lance.setValorPropostaInicial(valor);
                    lance.setMarca(ipf.getMarca());
                }
            }
        } catch (NullPointerException ex) {
            lance.setValorPropostaInicial(BigDecimal.ZERO);
        }
    }

    public BigDecimal criarItensPregaoLoteRetornandoPercentualDiferencaoLote(ItemPregao itemPregao, Pregao pregao) {
        itemPregao = buscarRodadasAndLancesPregao(itemPregao);
        List<ItemProcessoDeCompra> itens = processoDeCompraFacade.buscarItensProcessoCompraPorLote(itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra());

        if (itemPregao.getItemPregaoLoteProcesso().getItensPregaoLoteItemProcesso().isEmpty()) {
            for (ItemProcessoDeCompra ipc : itens) {
                ItemPregaoLoteItemProcesso novoItem = new ItemPregaoLoteItemProcesso();
                novoItem.setItemPregaoLoteProcesso(itemPregao.getItemPregaoLoteProcesso());
                novoItem.setItemProcessoDeCompra(ipc);
                Util.adicionarObjetoEmLista(itemPregao.getItemPregaoLoteProcesso().getItensPregaoLoteItemProcesso(), novoItem);
            }
        }
        BigDecimal valorLote = itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getValor();
        if (pregao.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            TipoControleLicitacao tipoControleProcesso = processoDeCompraFacade.getTipoControleProcesso(itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra());
            if (tipoControleProcesso != null && tipoControleProcesso.isTipoControlePorValor()) {
                itemPregao.getItemPregaoLanceVencedor().setValor(valorLote);
                itemPregao.getItemPregaoLanceVencedor().getLancePregao().setValor(valorLote);
            } else {
                BigDecimal valorDesconto = valorLote.multiply(itemPregao.getItemPregaoLanceVencedor().getPercentualDesconto()).divide(new BigDecimal("100"), 8, RoundingMode.HALF_EVEN);
                itemPregao.getItemPregaoLanceVencedor().setValor(valorLote.subtract(valorDesconto));
                itemPregao.getItemPregaoLanceVencedor().getLancePregao().setValor(valorLote.subtract(valorDesconto));
            }
        }
        BigDecimal valorNegociado = itemPregao.getItemPregaoLanceVencedor().getValor();
        BigDecimal diferenca = valorLote.subtract(valorNegociado);
        return diferenca.divide(valorLote, 8, RoundingMode.HALF_EVEN).multiply(new BigDecimal("100"));
    }

    public void aplicarPercentualParaItens(ItemPregao itemPregao, BigDecimal percentualDiferencaLote) {
        for (ItemPregaoLoteItemProcesso item : itemPregao.getItemPregaoLoteProcesso().getItensPregaoLoteItemProcesso()) {
            int scale = item.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida() != null
                ? item.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida().getMascaraValorUnitario().getQuantidadeCasasDecimais()
                : 2;
            BigDecimal valorUnitario = item.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPreco().setScale(scale, RoundingMode.HALF_EVEN);
            BigDecimal percentualPorItem = valorUnitario.multiply(percentualDiferencaLote).divide(new BigDecimal("100"), 8, RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN);
            item.setValor(percentualPorItem.compareTo(BigDecimal.ZERO) > 0 ? valorUnitario.subtract(percentualPorItem) : valorUnitario);
        }
    }

    public void validarRegrasItensLote(ItemPregao itemPregao) {
        ValidacaoException ve = new ValidacaoException();
        List<ItemPregaoLoteItemProcesso> itens = itemPregao.getItemPregaoLoteProcesso().getItensPregaoLoteItemProcesso();

        for (ItemPregaoLoteItemProcesso item : itens) {
            if (item != null && item.getValor() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Preço Unitário do item: <b>" + item.getItemProcessoDeCompra().getDescricao() + "</b> deve ser preenchido!");

            } else if (item.getValor().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do item de nº " + item.getItemProcessoDeCompra().getNumero() + " deve ser maior ou igual a Zero.");
            }
            if (item.getValor().compareTo(item.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPreco()) > 0
                && (item.getJustificativa() == null || "".equals(item.getJustificativa().trim()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do item de nº " + item.getItemProcessoDeCompra().getNumero() + " é maior que o valor cotado, portanto é necessário acrescentar a justificativa.");
            }
        }
        if (itemPregao.getItemPregaoLanceVencedor() != null
            && itemPregao.getItemPregaoLoteProcesso().getPrecoTotalLote().compareTo(itemPregao.getItemPregaoLanceVencedor().getValor()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O preço total dos itens do lote deve ser igual ao valor negociado com o fornecedor." +
                " Valor Negociado: <b> " + Util.formataValor(itemPregao.getItemPregaoLanceVencedor().getValor()) + "</b>" +
                " Valor Informado: <b> " + Util.formataValor(itemPregao.getItemPregaoLoteProcesso().getPrecoTotalLote()) + "</b>");
        }
        ve.lancarException();
    }

    public LancePregao recuperarLancePorRodadaAndProposta(RodadaPregao rodadaPregao, PropostaFornecedor proposta) {
        String sql = " select lp.* from  lancepregao lp " +
            "           where lp.rodadapregao_id = :idRodada " +
            "           and lp.propostafornecedor_id = :idProposta ";
        Query q = em.createNativeQuery(sql, LancePregao.class);
        q.setParameter("idRodada", rodadaPregao.getId());
        q.setParameter("idProposta", proposta.getId());
        q.setMaxResults(1);
        try {
            return (LancePregao) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public boolean verificarRelacaoDaLicitacaoEmUmPregao(Licitacao licitacao) {
        String hql = "from Pregao p where p.licitacao = :licitacao";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        if (q.getResultList() != null || !q.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }

    public Pregao recuperarPregaoAPartirDaLicitacao(Licitacao licitacao) {
        String hql = "from Pregao p where p.licitacao = :licitacao";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            Pregao pregao = (Pregao) resultList.get(0);
            Hibernate.initialize(pregao.getListaDeItemPregao());
            for (ItemPregao itemPregao : pregao.getListaDeItemPregao()) {
                if (itemPregao.getItemPregaoLoteProcesso() != null) {
                    Hibernate.initialize(itemPregao.getItemPregaoLoteProcesso().getItensPregaoLoteItemProcesso());
                }
            }
            return pregao;
        }
        return null;
    }

    public List<ItemPregao> recuperarListaDeItensPregao(Pregao pregao) {
        Query q = em.createQuery("from ItemPregao ip where ip.pregao = :pregao").setParameter("pregao", pregao);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public ItemPregao getItemPregaoDoItemPropostaFornecedor(ItemPropostaFornecedor ipf) {
        Licitacao lic = ipf.getPropostaFornecedor().getLicitacao();
        String hql = "";
        if (lic.getTipoApuracao().equals(TipoApuracaoLicitacao.ITEM)) {
            hql = " select ip from ItemPregao ip" +
                "  inner join ip.pregao p" +
                "  inner join p.licitacao lic" +
                "  inner join ip.itemPregaoItemProcesso ipip" +
                "  inner join ipip.itemProcessoDeCompra ipc" +
                "  inner join ipc.itensPropostaFornecedor ipf" +
                "  inner join ipf.propostaFornecedor pf" +
                "       where ipf = :ipf";
        } else {
            hql = " select ip from ItemPregao ip" +
                "  inner join ip.pregao p" +
                "  inner join p.licitacao lic" +
                "  inner join ip.itemPregaoLoteProcesso iplp" +
                "  inner join iplp.loteProcessoDeCompra lpc" +
                "  inner join lpc.itensProcessoDeCompra ipc" +
                "  inner join ipc.itensPropostaFornecedor ipf" +
                "  inner join ipf.propostaFornecedor pf" +
                "       where ipf = :ipf";
        }

        Query q = em.createQuery(hql);
        q.setParameter("ipf", ipf);
        try {
            return (ItemPregao) q.getResultList().get(0);
        } catch (NoResultException nre) {
            logger.error("ERRO AO RECUPERAR ITEMPREGAO PELO ITEMPROPOSTAFORNECEDOR {}", nre);
            return null;
        }
    }

    public ItemPregao getItemPregaoDoItemProcessoDeCompra(ItemProcessoDeCompra ipc) {
        String hql = " select ip from ItemPregao ip" +
            "  inner join ip.itemPregaoItemProcesso ipip " +
            " where ipip.itemProcessoDeCompra = :ipc ";
        Query q = em.createQuery(hql);
        q.setParameter("ipc", ipc);
        try {
            return (ItemPregao) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public ItemPregao getItemPregaoDoLoteProcessoDeCompra(LoteProcessoDeCompra lpc) {
        String hql = " select ip from ItemPregao ip" +
            "  inner join ip.itemPregaoLoteProcesso iplp " +
            " where iplp.loteProcessoDeCompra = :lpc ";
        Query q = em.createQuery(hql);
        q.setParameter("lpc", lpc);
        try {
            return (ItemPregao) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Boolean verificarSeItemPregaoPossuiPropostaFornecedor(ItemProcessoDeCompra itemProcessoDeCompra) {
        String sql = "select ipf.* from itempropfornec ipf where ipf.itemprocessodecompra_id = :idItem and (ipf.preco > 0 or ipf.PERCENTUALDESCONTO > 0) and ipf.marca is not null ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", itemProcessoDeCompra.getId());
        return !q.getResultList().isEmpty();
    }


    public Pregao buscarPregaoPoLicitacao(Licitacao licitacao) {
        String sql = " select p.* from pregao p where p.licitacao_id = :idLicitacao ";
        Query q = em.createNativeQuery(sql, Pregao.class);
        q.setParameter("idLicitacao", licitacao.getId());
        try {
            return (Pregao) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
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

    public Boolean hasLoteProcessoDeCompraFornecedor(LoteProcessoDeCompra loteProcessoDeCompra) {
        String sql = "select lpf.*" +
            " from LOTEPROPFORNEC lpf" +
            " inner join LOTEPROCESSODECOMPRA lpc on lpf.LOTEPROCESSODECOMPRA_ID = lpc.ID" +
            " where lpc.ID = :loteId" +
            " and (lpf.VALOR > 0 or lpf.PERCENTUALDESCONTO > 0)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("loteId", loteProcessoDeCompra.getId());
        return !q.getResultList().isEmpty();
    }
}
