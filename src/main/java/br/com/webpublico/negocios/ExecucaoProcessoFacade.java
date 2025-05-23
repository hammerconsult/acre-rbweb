package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AgrupadorSolicitacaoEmpenho;
import br.com.webpublico.entidadesauxiliares.ExecucaoProcessoItemVO;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoVo;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
public class ExecucaoProcessoFacade extends AbstractFacade<ExecucaoProcesso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigGrupoMaterialFacade configGrupoMaterialFacade;
    @EJB
    private ConfigDespesaBensFacade configGrupoBemFacade;
    @EJB
    private AssociacaoGrupoObjetoCompraGrupoMaterialFacade associcaoGrupoMaterialFacade;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade associacaoGrupoBemFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    @EJB
    private ItemPropostaFornecedorFacade itemPropostaFornecedorFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @EJB
    private SaldoProcessoLicitatorioFacade saldoProcessoLicitatorioFacade;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoSolicitacaoMaterialFacade;
    @EJB
    private DotacaoProcessoCompraFacade dotacaoProcessoCompraFacade;

    public ExecucaoProcessoFacade() {
        super(ExecucaoProcesso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExecucaoProcesso recuperarComDependencias(Object id) {
        ExecucaoProcesso entity = super.recuperar(id);
        Hibernate.initialize(entity.getItens());
        Hibernate.initialize(entity.getReservas());
        Hibernate.initialize(entity.getEmpenhos());
        for (ExecucaoProcessoReserva reserva : entity.getReservas()) {
            Hibernate.initialize(reserva.getFontes());
            for (ExecucaoProcessoFonte fonte : reserva.getFontes()) {
                Hibernate.initialize(fonte.getItens());
            }
        }
        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        return entity;
    }

    public ExecucaoProcesso recuperarComDependenciasItens(Object id) {
        ExecucaoProcesso entity = super.recuperar(id);
        Hibernate.initialize(entity.getItens());
        return entity;
    }

    @Override
    public void remover(ExecucaoProcesso entity) {
        estornarSaldoReservadoPorLicitacao(entity);
        dotacaoSolicitacaoMaterialFacade.removerDotacaoSolicitacaoCompraFonte(entity.getProcessoCompra(), entity.getId());
        super.remover(entity);
    }

    @Override
    public ExecucaoProcesso salvarRetornando(ExecucaoProcesso entity) {
        entity = em.merge(entity);
        List<AgrupadorSolicitacaoEmpenho> agrupadores = criarAgrupadores(entity);
        gerarSolicitacaoEmpenho(entity, agrupadores);
        dotacaoSolicitacaoMaterialFacade.gerarReservaSolicitcaoCompraExecucaoProcesso(entity);
        gerarSaldoReservadoPorLicitacao(entity);
        return entity;
    }

    public void atribuirGrupoContaDespesaItemExecucao(List<ExecucaoProcessoItem> itens) {
        for (ExecucaoProcessoItem item : itens) {
            atribuirGrupoContaDespesa(item);
        }
    }

    public void atribuirGrupoContaDespesaItemFonte(List<ExecucaoProcessoFonteItem> itens) {
        for (ExecucaoProcessoFonteItem item : itens) {
            atribuirGrupoContaDespesa(item.getExecucaoProcessoItem());
        }
    }

    public void atribuirGrupoContaDespesa(ExecucaoProcessoItem item) {
        if (item.getObjetoCompra() != null) {
            item.getObjetoCompra().setGrupoContaDespesa(objetoCompraFacade.criarGrupoContaDespesa(
                item.getObjetoCompra().getTipoObjetoCompra(),
                item.getObjetoCompra().getGrupoObjetoCompra()));
        }
    }

    public void atribuirGrupoContaDespesa(ExecucaoProcessoItemVO item) {
        if (item.getObjetoCompra() != null) {
            item.getObjetoCompra().setGrupoContaDespesa(objetoCompraFacade.criarGrupoContaDespesa(
                item.getObjetoCompra().getTipoObjetoCompra(),
                item.getObjetoCompra().getGrupoObjetoCompra()));
        }
    }

    public List<AgrupadorSolicitacaoEmpenho> criarAgrupadores(ExecucaoProcesso entity) {
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();
        agrupadores.addAll(criarAgrupadorSolicitacaoEmpenhoGrupoMaterial(entity));
        agrupadores.addAll(criarAgrupadorSolicitacaoEmpenhoGrupoBem(entity));
        agrupadores.addAll(criarAgrupadorSolicitacaoEmpenhoSemGrupo(entity));
        return agrupadores;
    }


    public HierarquiaOrganizacional recuperarHierarquiaDaUnidade(ExecucaoProcesso entity) {
        if (entity.getUnidadeOrcamentaria() != null) {
            return hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
                entity.getUnidadeOrcamentaria(),
                new Date());
        }
        return null;
    }

    public Map<GrupoMaterial, List<ExecucaoProcessoFonteItem>> montarMapGrupoMaterialItemDotacao(ExecucaoProcesso entity) {
        Map<GrupoMaterial, List<ExecucaoProcessoFonteItem>> mapItens = Maps.newHashMap();
        for (ExecucaoProcessoReserva exTipo : entity.getReservas()) {
            if (exTipo.getTipoObjetoCompra().isMaterialConsumo()) {
                for (ExecucaoProcessoFonte exFonte : exTipo.getFontes()) {
                    for (ExecucaoProcessoFonteItem itemDotacao : exFonte.getItens()) {
                        if ((itemDotacao.getQuantidade().compareTo(BigDecimal.ZERO) > 0
                            || itemDotacao.getValorTotal().compareTo(BigDecimal.ZERO) > 0)) {
                            GrupoObjetoCompra grupoObjetoCompra = itemDotacao.getExecucaoProcessoItem().getObjetoCompra().getGrupoObjetoCompra();
                            GrupoMaterial grupoMaterial = getGrupoMaterial(entity, grupoObjetoCompra);
                            if (mapItens.containsKey(grupoMaterial)) {
                                mapItens.get(grupoMaterial).add(itemDotacao);
                            } else {
                                mapItens.put(grupoMaterial, Lists.newArrayList(itemDotacao));
                            }
                        }
                    }
                }
            }
        }
        return mapItens;
    }

    public List<AgrupadorSolicitacaoEmpenho> criarAgrupadorSolicitacaoEmpenhoSemGrupo(ExecucaoProcesso entity) {
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();
        for (ExecucaoProcessoReserva reserva : entity.getReservas()) {
            if (!reserva.isObjetoCompraConsumoOrPermanenteMovel()) {
                for (ExecucaoProcessoFonte fonte : reserva.getFontes()) {
                    for (ExecucaoProcessoFonteItem item : fonte.getItens()) {
                        AgrupadorSolicitacaoEmpenho agrupador = novoAgrupadorSolicitacaoEmpenho(fonte, Lists.<Conta>newArrayList());
                        adicionarItemDotacaoNoAgrupador(agrupadores, item, Lists.<Conta>newArrayList(), agrupador);
                    }
                }
            }
        }
        return agrupadores;
    }

    public List<AgrupadorSolicitacaoEmpenho> criarAgrupadorSolicitacaoEmpenhoGrupoMaterial(ExecucaoProcesso entity) {
        Map<GrupoMaterial, List<ExecucaoProcessoFonteItem>> mapItens = montarMapGrupoMaterialItemDotacao(entity);
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();

        for (Map.Entry<GrupoMaterial, List<ExecucaoProcessoFonteItem>> entry : mapItens.entrySet()) {
            for (ExecucaoProcessoFonteItem itemDotacao : entry.getValue()) {

                ExecucaoProcessoFonte exFonte = itemDotacao.getExecucaoProcessoFonte();
                Conta contaDespesa = exFonte.getFonteDespesaORC().getDespesaORC().getContaDeDespesa();
                List<Conta> contas = configGrupoMaterialFacade.buscarContasDespesaPorGrupoMaterial(entry.getKey(), entity.getDataLancamento(), contaDespesa);
                if (contas == null || contas.isEmpty()) {
                    throw new ExcecaoNegocioGenerica("Nenhuma conta de despesa configurada para o grupo material: " + entry.getKey() + " desdobrada da conta " + contaDespesa + ".");
                }

                AgrupadorSolicitacaoEmpenho agrupador = novoAgrupadorSolicitacaoEmpenho(exFonte, contas);
                agrupador.setGrupoMaterial(entry.getKey());
                if (agrupador.hasGrupoComMaisUmaConta()) {
                    throw new ExcecaoNegocioGenerica("A configuração para conta de despesa retornou mais de uma conta para o grupo material: " + entry.getKey() + ".");
                }
                adicionarItemDotacaoNoAgrupador(agrupadores, itemDotacao, contas, agrupador);
            }
        }
        ordernarItensAgrupadorSolicitacaoEmpenho(agrupadores);
        return agrupadores;
    }

    public Map<GrupoBem, List<ExecucaoProcessoFonteItem>> montarMapGrupoBemItemDotacao(ExecucaoProcesso entity) {
        Map<GrupoBem, List<ExecucaoProcessoFonteItem>> mapItens = Maps.newHashMap();
        for (ExecucaoProcessoReserva reserva : entity.getReservas()) {
            if (reserva.getTipoObjetoCompra().isMaterialPermanente()) {
                for (ExecucaoProcessoFonte fonte : reserva.getFontes()) {

                    for (ExecucaoProcessoFonteItem itemDotacao : fonte.getItens()) {
                        if (itemDotacao.hasQuantidade() || itemDotacao.hasValor()) {

                            GrupoObjetoCompra grupoObjetoCompra = itemDotacao.getExecucaoProcessoItem().getObjetoCompra().getGrupoObjetoCompra();
                            GrupoBem grupoBem = getGrupoBem(entity, grupoObjetoCompra);

                            if (mapItens.containsKey(grupoBem)) {
                                mapItens.get(grupoBem).add(itemDotacao);
                            } else {
                                mapItens.put(grupoBem, Lists.newArrayList(itemDotacao));
                            }
                        }
                    }
                }
            }
        }
        return mapItens;
    }

    public List<AgrupadorSolicitacaoEmpenho> criarAgrupadorSolicitacaoEmpenhoGrupoBem(ExecucaoProcesso entity) {
        Map<GrupoBem, List<ExecucaoProcessoFonteItem>> mapItens = montarMapGrupoBemItemDotacao(entity);
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();

        for (Map.Entry<GrupoBem, List<ExecucaoProcessoFonteItem>> entry : mapItens.entrySet()) {
            for (ExecucaoProcessoFonteItem itemDotacao : entry.getValue()) {

                ExecucaoProcessoFonte exFonte = itemDotacao.getExecucaoProcessoFonte();
                Conta contaDespesa = exFonte.getFonteDespesaORC().getDespesaORC().getContaDeDespesa();
                List<Conta> contas = configGrupoBemFacade.buscarContasPorGrupoBem(entry.getKey(), entity.getDataLancamento(), contaDespesa);
                if (contas == null || contas.isEmpty()) {
                    throw new ExcecaoNegocioGenerica("Nenhuma conta de despesa configurada para o grupo patrimonial: " + entry.getKey() + " desdobrada da conta " + contaDespesa + ".");
                }

                AgrupadorSolicitacaoEmpenho agrupador = novoAgrupadorSolicitacaoEmpenho(exFonte, contas);
                agrupador.setGrupoBem(entry.getKey());
                if (agrupador.hasGrupoComMaisUmaConta()) {
                    throw new ExcecaoNegocioGenerica("A configuração para conta de despesa retornou mais de uma conta para o grupo patrimonial: " + entry.getKey() + ".");
                }
                adicionarItemDotacaoNoAgrupador(agrupadores, itemDotacao, contas, agrupador);
            }
        }
        ordernarItensAgrupadorSolicitacaoEmpenho(agrupadores);
        return agrupadores;
    }

    private void adicionarItemDotacaoNoAgrupador(List<AgrupadorSolicitacaoEmpenho> agrupadores, ExecucaoProcessoFonteItem itemDotacao, List<Conta> contas, AgrupadorSolicitacaoEmpenho agrupador) {
        Conta contaDespesa = null;
        if (!contas.isEmpty()) {
            contaDespesa = contas.get(0);
        }
        agrupador.setContaDesdobrada(contaDespesa);
        if (!agrupadores.contains(agrupador)) {
            itemDotacao.setContaDespesa(contaDespesa);
            Util.adicionarObjetoEmLista(agrupador.getItensDotacaoExecucaoProcesso(), itemDotacao);
            agrupador.setValor(itemDotacao.getValorTotal());
            Util.adicionarObjetoEmLista(agrupadores, agrupador);
        } else {
            for (AgrupadorSolicitacaoEmpenho agrupadorExistente : agrupadores) {
                if (agrupador.equals(agrupadorExistente)) {
                    agrupador = agrupadorExistente;
                }
            }
            itemDotacao.setContaDespesa(contaDespesa);
            Util.adicionarObjetoEmLista(agrupador.getItensDotacaoExecucaoProcesso(), itemDotacao);
            agrupador.setValor(agrupador.getValor().add(itemDotacao.getValorTotal()));
            Util.adicionarObjetoEmLista(agrupadores, agrupador);
        }
    }

    private GrupoMaterial getGrupoMaterial(ExecucaoProcesso entity, GrupoObjetoCompra grupoObjetoCompra) {
        try {
            return associcaoGrupoMaterialFacade.buscarAssociacaoPorGrupoObjetoCompraVigente(grupoObjetoCompra, entity.getDataLancamento()).getGrupoMaterial();
        } catch (Exception e) {
            throw e;
        }
    }

    private GrupoBem getGrupoBem(ExecucaoProcesso entity, GrupoObjetoCompra grupoObjetoCompra) {
        try {
            return associacaoGrupoBemFacade.recuperarAssociacaoDoGrupoObjetoCompra(grupoObjetoCompra, entity.getDataLancamento()).getGrupoBem();
        } catch (Exception e) {
            throw e;
        }
    }

    public void ordernarItensAgrupadorSolicitacaoEmpenho(List<AgrupadorSolicitacaoEmpenho> agrupadores) {
        for (AgrupadorSolicitacaoEmpenho agrupador : agrupadores) {
            Collections.sort(agrupador.getItensDotacaoExecucao());
        }
    }

    public AgrupadorSolicitacaoEmpenho novoAgrupadorSolicitacaoEmpenho(ExecucaoProcessoFonte fonte, List<Conta> contas) {
        AgrupadorSolicitacaoEmpenho agrupador = new AgrupadorSolicitacaoEmpenho(fonte.getFonteDespesaORC(), contas);
        agrupador.setGerarReserva(fonte.getGeraReserva());
        agrupador.setClasseCredor(fonte.getExecucaoProcessoReserva().getClasseCredor());
        agrupador.setIdOrigemSaldoOrcamentario(fonte.getId());
        agrupador.setClasseOrigemSaldoOrcamentario(fonte.getClass().getSimpleName());
        agrupador.setTipoObjetoCompra(fonte.getExecucaoProcessoReserva().getTipoObjetoCompra());
        return agrupador;
    }

    public BigDecimal getValorEstornado(Long idProcesso, ItemProcessoDeCompra itemProcCompra) {
        String sql = " " +
            " select coalesce(sum(itemest.valortotal),0) from execucaoprocessoempestitem itemest " +
            "   inner join execucaoprocessoitem itemex on itemex.id = itemest.execucaoprocessoitem_id " +
            "   inner join execucaoprocesso ex on ex.id = itemex.execucaoprocesso_id " +
            "   left join execucaoprocessoata exata on exata.execucaoprocesso_id = ex.id " +
            "   left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
            "   left join licitacao l on l.id = ata.licitacao_id " +
            "   left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
            "   left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id " +
            "  where coalesce(ata.id, disp.id) = :idProcesso " +
            "   and itemex.itemprocessocompra_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", idProcesso);
        q.setParameter("idItem", itemProcCompra.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public ExecucaoProcesso recuperarUltimaExecucao(Long idProcesso) {
        String sql = " select ex.* from execucaoprocesso ex " +
            "           left join execucaoprocessoata exata on exata.execucaoprocesso_id = ex.id " +
            "           left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
            "       where coalesce(exata.ataregistropreco_id, exdisp.dispensalicitacao_id) = :idProcesso " +
            "       order by ex.id desc ";
        Query q = em.createNativeQuery(sql, ExecucaoProcesso.class);
        q.setParameter("idProcesso", idProcesso);
        q.setMaxResults(1);
        try {
            return (ExecucaoProcesso) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public BigDecimal getValorEmRequisicao(ExecucaoProcessoItem item) {
        String sql = " " +
            " select coalesce(sum(item.valortotal),0) as valortotal " +
            " from itemrequisicaodecompra item " +
            " where item.execucaoprocessoitem_id = :idItemExecucao  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemExecucao", item.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return resultado.get(0);
    }

    public BigDecimal getValorEmRequisicaoEstorno(ExecucaoProcessoItem item) {
        String sql =
            "  select coalesce(sum(itemest.quantidade * itemreq.valorunitario), 0) as valortotal " +
                " from itemrequisicaocompraest itemest " +
                "   inner join itemrequisicaodecompra itemreq on itemreq.id = itemest.itemrequisicaocompra_id " +
                " where itemreq.execucaoprocessoitem_id = :idItemExecucao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemExecucao", item.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return resultado.get(0);
    }

    public ExecucaoProcessoFonteItem buscarItemFonte(ExecucaoProcesso execucao, FonteDespesaORC fonte, ExecucaoProcessoItem item) {
        String sql = " select itemfonte.* from execucaoprocessofonteitem itemfonte " +
            "           inner join execucaoprocessofonte exfonte on exfonte.id = itemfonte.execucaoprocessofonte_id " +
            "           inner join execucaoprocessoreserva exres on exres.id = exfonte.execucaoprocessoreserva_id " +
            "          where exres.execucaoprocesso_id = :idExecucao " +
            "           and exfonte.fontedespesaorc_id = :idFonte " +
            "           and itemfonte.execucaoprocessoitem_id = :idItem ";
        Query q = em.createNativeQuery(sql, ExecucaoProcessoFonteItem.class);
        q.setParameter("idExecucao", execucao.getId());
        q.setParameter("idFonte", fonte.getId());
        q.setParameter("idItem", item.getId());
        try {
            return (ExecucaoProcessoFonteItem) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<ExecucaoProcessoFonte> buscarExecucaoFontePorProcessoCompra(ProcessoDeCompra processoCompra) {
        String sql = " select exfonte.* from execucaoprocessofonte exfonte " +
            "           inner join execucaoprocessoreserva exres on exres.id = exfonte.execucaoprocessoreserva_id " +
            "           inner join execucaoprocesso ex on ex.id = exres.execucaoprocesso_id " +
            "           left join execucaoprocessoata exata on exata.execucaoprocesso_id = ex.id " +
            "           left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
            "           left join licitacao l on l.id = ata.licitacao_id " +
            "           left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
            "           left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id " +
            "      where coalesce(l.processodecompra_id, disp.processodecompra_id) = :idProcessoCompra ";
        Query q = em.createNativeQuery(sql, ExecucaoProcessoFonte.class);
        q.setParameter("idProcessoCompra", processoCompra.getId());
        return q.getResultList();
    }

    private void gerarSolicitacaoEmpenho(ExecucaoProcesso entity, List<AgrupadorSolicitacaoEmpenho> agrupadoresSolicitacaoEmpenho) {
        for (AgrupadorSolicitacaoEmpenho agrupador : agrupadoresSolicitacaoEmpenho) {
            SolicitacaoEmpenhoVo solicitacaoEmpenhoVo = criarVoSolicitacaoEmpenho(entity, agrupador);
            SolicitacaoEmpenho solicitacaoEmpenho = solicitacaoEmpenhoFacade.gerarSolicitacaoEmpenhoSalvando(solicitacaoEmpenhoVo);
            criarExecucaoProcessoEmpenho(entity, solicitacaoEmpenho);
        }
    }

    public SolicitacaoEmpenhoVo criarVoSolicitacaoEmpenho(ExecucaoProcesso entity, AgrupadorSolicitacaoEmpenho agrupador) {
        FonteDespesaORC fonteDespesaORC = agrupador.getFonteDespesaORC();
        UnidadeOrganizacional unidadeOrganizacional = fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional();
        String historico = "Solicitação gerada através da Execução da " + entity.getTipoExecucao().getDescricao() + ": " + entity.getDescricaoProcesso()
            + " em: " + DataUtil.getDataFormatada(entity.getDataLancamento())
            + ". Execução nº " + entity.getNumero() + ". Dotação: " + fonteDespesaORC + " - " + Util.formataValor(entity.getValor());

        SolicitacaoEmpenhoVo solEmpVo = new SolicitacaoEmpenhoVo();
        solEmpVo.setValor(agrupador.getValor());
        solEmpVo.setUnidadeOrganizacional(unidadeOrganizacional);
        solEmpVo.setHistoricoContabil(null);
        solEmpVo.setFonteDespesaOrc(fonteDespesaORC);
        solEmpVo.setDespesaOrc(fonteDespesaORC.getDespesaORC());
        solEmpVo.setDataSolicitacao(entity.getDataLancamento());
        solEmpVo.setContaDespesaDesdobrada(agrupador.getContaDesdobrada());
        solEmpVo.setComplementoHistorico(historico);
        solEmpVo.setFornecedor(entity.getFornecedor());
        solEmpVo.setGerarReserva(agrupador.getGerarReserva());
        solEmpVo.setClasseCredor(agrupador.getClasseCredor());
        solEmpVo.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        solEmpVo.setOrigemSocilicitacao(entity.getTipoExecucao().isAta() ? OrigemSolicitacaoEmpenho.ATA_REGISTRO_PRECO : OrigemSolicitacaoEmpenho.DISPENSA_LICITACAO_INEXIGIBILIDADE);
        return solEmpVo;
    }

    private void gerarSaldoReservadoPorLicitacao(ExecucaoProcesso entity) {
        for (ExecucaoProcessoReserva reserva : entity.getReservas()) {
            for (ExecucaoProcessoFonte fonte : reserva.getFontes()) {
                if (fonte.getGeraReserva()) {
                    SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                        fonte.getFonteDespesaORC(),
                        OperacaoORC.RESERVADO_POR_LICITACAO,
                        TipoOperacaoORC.NORMAL,
                        fonte.getValor(),
                        entity.getDataLancamento(),
                        fonte.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                        fonte.getId().toString(),
                        fonte.getClass().getSimpleName(),
                        entity.getNumeroProcesso(),
                        getHistoricoExecucao(fonte.getFonteDespesaORC(), entity, reserva.getClasseCredor())
                    );
                    saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
                }
            }
        }
    }

    private void estornarSaldoReservadoPorLicitacao(ExecucaoProcesso entity) {
        for (ExecucaoProcessoReserva reserva : entity.getReservas()) {
            for (ExecucaoProcessoFonte fonte : reserva.getFontes()) {
                if (fonte.getGeraReserva() && validarValorColunaReservadoPorLicitacao(fonte)) {
                    SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                        fonte.getFonteDespesaORC(),
                        OperacaoORC.RESERVADO_POR_LICITACAO,
                        TipoOperacaoORC.ESTORNO,
                        fonte.getValor(),
                        entity.getDataLancamento(),
                        fonte.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                        fonte.getId().toString(),
                        fonte.getClass().getSimpleName(),
                        entity.getNumeroProcesso(),
                        getHistoricoExecucao(fonte.getFonteDespesaORC(), entity, reserva.getClasseCredor())
                    );
                    saldoFonteDespesaORCFacade.gerarSaldoOrcamentarioSemRealizarValidacao(vo);
                }
            }
        }
    }

    public String getHistoricoExecucao(FonteDespesaORC fonteDespesaORC, ExecucaoProcesso entity, ClasseCredor classeCredor) {
        return "Execução nº " + entity.getNumero() + " | " +
            entity.getTipoExecucao().getDescricao() + ": " + entity.getNumeroProcesso() + " | " + DataUtil.getDataFormatada(entity.getDataLancamento()) + " | " + entity.getFornecedor() + " | " + classeCredor + " | " +
            " Unidade Administrativa: " + entity.getUnidadeAdministrativa() + " | " +
            " Elemento de Despesa: " + fonteDespesaORC.getDespesaORC() + "/" + fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " | " +
            " Fonte de Recurso: " + fonteDespesaORC.getDescricaoFonteDeRecurso().trim() + ",";
    }

    private ExecucaoProcessoEmpenho criarExecucaoProcessoEmpenho(ExecucaoProcesso entity, SolicitacaoEmpenho solEmp) {
        ExecucaoProcessoEmpenho exEmpenho = new ExecucaoProcessoEmpenho();
        exEmpenho.setExecucaoProcesso(entity);
        exEmpenho.setSolicitacaoEmpenho(solEmp);
        exEmpenho = em.merge(exEmpenho);
        return exEmpenho;
    }

    private boolean validarValorColunaReservadoPorLicitacao(ExecucaoProcessoFonte fonte) {
        return saldoFonteDespesaORCFacade.validarValorColunaIndividual(
            fonte.getValor(),
            fonte.getFonteDespesaORC(),
            sistemaFacade.getDataOperacao(),
            fonte.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
            OperacaoORC.RESERVADO_POR_LICITACAO);
    }

    public ExecucaoProcessoEmpenho buscarExecucaoProcessoEmpenhoPorSolicitacaoEmpenho(SolicitacaoEmpenho solEmp) {
        String sql = " select exEmp.* from execucaoprocessoempenho exEmp " +
            "          where exEmp.solicitacaoempenho_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql, ExecucaoProcessoEmpenho.class);
        q.setParameter("idSolicitacao", solEmp.getId());
        try {
            return (ExecucaoProcessoEmpenho) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<ExecucaoProcessoEmpenho> buscarExecucaoProcessoEmpenhoPorAta(AtaRegistroPreco ataRegistroPreco) {
        String sql = " select exEmp.* from execucaoprocessoempenho exEmp " +
            "          where exEmp.execucaoprocesso_id = :idAta ";
        Query q = em.createNativeQuery(sql, ExecucaoProcessoEmpenho.class);
        q.setParameter("idAta", ataRegistroPreco.getId());
        return q.getResultList();
    }

    public ExecucaoProcessoEmpenho buscarExecucaoProcessoEmpenhoPorEmpenho(Long idEmpenho) {
        String sql = " select exemp.* from execucaoprocessoempenho exemp" +
            "          where exemp.empenho_id = :idEmpenho ";
        Query q = em.createNativeQuery(sql, ExecucaoProcessoEmpenho.class);
        q.setParameter("idEmpenho", idEmpenho);
        try {
            return (ExecucaoProcessoEmpenho) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Object[] recuperarIdENumeroExecucaoProcesso(SolicitacaoEmpenho solicitacaoEmpenho) {
        String sql = " select distinct " +
            "           ex.id, " +
            "           ex.numero," +
            "           ex.tipoexecucao " +
            "         from execucaoprocesso ex " +
            "       inner join execucaoprocessoempenho exemp on exemp.execucaoprocesso_id = ex.id " +
            "      where exemp.solicitacaoEmpenho_id = :idSolEmp ";
        Query q = em.createNativeQuery(sql);
        try {
            q.setParameter("idSolEmp", solicitacaoEmpenho.getId());
            return (Object[]) q.getSingleResult();
        } catch (NoResultException | NullPointerException e) {
            return null;
        }
    }

    public List<ExecucaoProcesso> buscarExecucoesProcesso(String parte) {
        String sql = " select ex.* " +
            "  from Execucaoprocesso ex " +
            "   inner join pessoa p on ex.fornecedor_id = p.id " +
            "   left join pessoafisica pf on p.id = pf.id " +
            "   left join pessoajuridica pj on p.id = pj.id " +
            "   left join execucaoprocessoata exata on exata.execucaoprocesso_id = ex.id " +
            "   left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
            "   left join licitacao l on l.id = ata.licitacao_id " +
            "   left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
            "   left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id " +
            "   where (ata.numero like :parte " +
            "           or ex.numero like :parte " +
            "           or disp.numerodadispensa like :parte " +
            "           or lower(disp.resumodoobjeto) like :parte " +
            "           or lower(l.resumodoobjeto) like :parte " +
            "           or lower(pf.nome) like :parte " +
            "           or lower(pj.razaosocial) like :parte " +
            "           or replace(replace(pf.cpf,'.',''),'-','') like :parte " +
            "           or pf.cpf like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''), '/', '') like :parte " +
            "           or pj.cnpj like :parte) " +
            "  order by ex.numero ";
        Query q = em.createNativeQuery(sql, ExecucaoProcesso.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<Empenho> buscarEmpenhosExecucao(ExecucaoProcesso execucaoProcesso) {
        String sql = " select emp.* from empenho emp" +
            "          inner join execucaoprocessoempenho exemp on exemp.empenho_id = emp.id " +
            "          where exemp.execucaoprocesso_id = :idExecucao ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idExecucao", execucaoProcesso.getId());
        return q.getResultList();
    }

    public List<ExecucaoProcesso> buscarExecucoesPorAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        String sql = " " +
            "  select ex.* from execucaoprocesso ex " +
            "   inner join execucaoprocessoata exata on exata.execucaoprocesso_id = ex.id " +
            "  where exata.ataregistropreco_id = :idProcesso ";
        Query q = em.createNativeQuery(sql, ExecucaoProcesso.class);
        q.setParameter("idProcesso", ataRegistroPreco.getId());
        return q.getResultList();
    }

    public List<ExecucaoProcesso> buscarExecucoesPorDispensaLicitacao(DispensaDeLicitacao dispensa) {
        String sql = " " +
            "  select ex.* from execucaoprocesso ex " +
            "   inner join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
            "  where exdisp.dispensalicitacao_id = :idProcesso ";
        Query q = em.createNativeQuery(sql, ExecucaoProcesso.class);
        q.setParameter("idProcesso", dispensa.getId());
        return q.getResultList();
    }

    public Integer getProximoNumero(ExecucaoProcesso entity) {
        if (entity.getTipoExecucao().isAta()) {
            return Util.getProximoNumero(buscarExecucoesPorAtaRegistroPreco(entity.getAtaRegistroPreco()));
        }
        return Util.getProximoNumero(buscarExecucoesPorDispensaLicitacao(entity.getDispensaLicitacao()));
    }

    public FormaEntregaExecucao buscarFormaEntregaExecucao(SolicitacaoEmpenho solicitacaoEmpenho) {
        String hql = " " +
            "  select ex.formaEntrega from ExecucaoProcessoEmpenho exEmp " +
            "   inner join exEmp.execucaoProcesso ex " +
            "  where exEmp.solicitacaoEmpenho = :solicitacaoEmp ";
        Query q = em.createQuery(hql, FormaEntregaExecucao.class);
        q.setParameter("solicitacaoEmp", solicitacaoEmpenho);
        try {
            return (FormaEntregaExecucao) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public SolicitacaoEmpenhoFacade getSolicitacaoEmpenhoFacade() {
        return solicitacaoEmpenhoFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public AtaRegistroPrecoFacade getAtaRegistroPrecoFacade() {
        return ataRegistroPrecoFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public DispensaDeLicitacaoFacade getDispensaDeLicitacaoFacade() {
        return dispensaDeLicitacaoFacade;
    }

    public ItemPropostaFornecedorFacade getItemPropostaFornecedorFacade() {
        return itemPropostaFornecedorFacade;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }

    public SaldoProcessoLicitatorioFacade getSaldoProcessoLicitatorioFacade() {
        return saldoProcessoLicitatorioFacade;
    }

    public AlteracaoContratualFacade getAlteracaoContratualFacade() {
        return alteracaoContratualFacade;
    }

    public DotacaoSolicitacaoMaterialFacade getDotacaoSolicitacaoMaterialFacade() {
        return dotacaoSolicitacaoMaterialFacade;
    }

    public DotacaoProcessoCompraFacade getDotacaoProcessoCompraFacade() {
        return dotacaoProcessoCompraFacade;
    }
}

