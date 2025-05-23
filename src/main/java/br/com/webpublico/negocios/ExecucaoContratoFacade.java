package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AgrupadorSolicitacaoEmpenho;
import br.com.webpublico.entidadesauxiliares.SaldoItemContratoOrigemVO;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoVo;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
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
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by mga on 17/11/2017.
 */
@Stateless
public class ExecucaoContratoFacade extends AbstractFacade<ExecucaoContrato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EmpenhoEstornoFacade empenhoEstornoFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoSolicitacaoMaterialFacade;
    @EJB
    private DotacaoProcessoCompraFacade dotacaoProcessoCompraFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ReprocessamentoSaldoContratoFacade reprocessamentoSaldoContratoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ExecucaoContratoEstornoFacade execucaoContratoEstornoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
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
    private SolicitacaoMaterialExternoFacade solicitacaoMaterialExternoFacade;
    @EJB
    private SolicitacaoEmpenhoEstornoFacade solicitacaoEmpenhoEstornoFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;
    @EJB
    private AjusteContratoFacade ajusteContratoFacade;
    @EJB
    private SaldoItemContratoFacade saldoItemContratoFacade;

    public ExecucaoContratoFacade() {
        super(ExecucaoContrato.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExecucaoContrato recuperarComDependencias(Object id) {
        ExecucaoContrato entity = super.recuperar(id);
        if (entity != null) {
            Hibernate.initialize(entity.getItens());
            Hibernate.initialize(entity.getReservas());
            Hibernate.initialize(entity.getEmpenhos());
            recuperarEmpenhosExecucaoContrato(entity);
            for (ExecucaoContratoTipo execucaoContratoTipo : entity.getReservas()) {
                if (execucaoContratoTipo != null) {
                    Hibernate.initialize(execucaoContratoTipo.getFontes());
                    for (ExecucaoContratoTipoFonte fonte : execucaoContratoTipo.getFontes()) {
                        Hibernate.initialize(fonte.getItens());
                    }
                }
            }
            if (entity.getDetentorArquivoComposicao() != null) {
                Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
            }
        }
        atribuirHierarquiaExecucao(entity);
        return entity;
    }

    public ExecucaoContrato recuperarComDependenciasReservaEmpenho(Object id) {
        ExecucaoContrato entity = super.recuperar(id);
        if (entity != null) {
            Hibernate.initialize(entity.getReservas());
            Hibernate.initialize(entity.getEmpenhos());
            for (ExecucaoContratoTipo execucaoContratoTipo : entity.getReservas()) {
                if (execucaoContratoTipo != null) {
                    Hibernate.initialize(execucaoContratoTipo.getFontes());
                }
            }
        }
        return entity;
    }

    public ExecucaoContrato recuperarComDependenciasItens(Object id) {
        ExecucaoContrato entity = super.recuperar(id);
        Hibernate.initialize(entity.getItens());
        atribuirHierarquiaExecucao(entity);
        return entity;
    }

    public ExecucaoContrato recuperarComDependenciasExecucaoEmpenho(Object id) {
        ExecucaoContrato entity = super.recuperar(id);
        if (entity != null) {
            Hibernate.initialize(entity.getEmpenhos());
            recuperarEmpenhosExecucaoContrato(entity);
            atribuirHierarquiaExecucao(entity);
        }
        return entity;
    }

    @Override
    public void remover(ExecucaoContrato entity) {
        estornarSaldoReservadoPorLicitacao(entity);
        removerDotacaoSolicitacaoCompraFonte(entity);
        removerSolicitacaoEmpenho(entity);
        super.remover(entity);
    }

    private void removerSolicitacaoEmpenho(ExecucaoContrato entity) {
        for (ExecucaoContratoEmpenho exEmp : entity.getEmpenhos()) {
            SolicitacaoEmpenho solEmp = exEmp.getSolicitacaoEmpenho();
            if (solEmp.getEmpenho() == null) {
                exEmp.setSolicitacaoEmpenho(null);
                em.remove(em.find(SolicitacaoEmpenho.class, solEmp.getId()));
            } else {
                solEmp.setContrato(null);
                em.merge(solEmp);
            }
        }
    }

    private void removerDotacaoSolicitacaoCompraFonte(ExecucaoContrato entity) {
        ProcessoDeCompra processoCompra = entity.getContrato().getObjetoAdequado().getProcessoDeCompra();
        if (processoCompra != null) {
            dotacaoSolicitacaoMaterialFacade.removerDotacaoSolicitacaoCompraFonte(processoCompra, entity.getId());
        }
    }

    public ExecucaoContrato salvarExecucaoContrato(ExecucaoContrato entity) {
        try {
            Contrato contrato = entity.getContrato();
            contratoFacade.diminuirSaldoAtualContrato(entity.getValor(), contrato);

            entity = em.merge(entity);
            entity = recuperarComDependencias(entity.getId());
            gerarMovimentoItemContrato(entity);

            List<AgrupadorSolicitacaoEmpenho> agrupadores = criarAgrupadores(entity);
            gerarSolicitacaoEmpenho(entity, agrupadores);
            dotacaoSolicitacaoMaterialFacade.gerarReservaSolicitcaoCompraExecucaoContrato(entity);
            gerarSaldoReservadoPorLicitacao(entity);
            return entity;
        } catch (Exception e) {
            logger.error("salvarExecucaoContrato()", e);
            throw e;
        }
    }

    public List<AgrupadorSolicitacaoEmpenho> criarAgrupadores(ExecucaoContrato entity) {
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();
        agrupadores.addAll(criarAgrupadorSolicitacaoEmpenhoGrupoMaterial(entity));
        agrupadores.addAll(criarAgrupadorSolicitacaoEmpenhoGrupoBem(entity));
        agrupadores.addAll(criarAgrupadorSolicitacaoEmpenhoSemGrupo(entity));
        return agrupadores;
    }

    private void atribuirHierarquiaExecucao(ExecucaoContrato entity) {
        if (entity.getUnidadeOrcamentaria() != null) {
            HierarquiaOrganizacional hoOrcamentaria = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), entity.getUnidadeOrcamentaria(), new Date());
            entity.setHierarquiaOrcamentaria(hoOrcamentaria);
        }
    }

    public void recuperarEmpenhosExecucaoContrato(ExecucaoContrato execucaoContrato) {
        for (ExecucaoContratoEmpenho execucao : execucaoContrato.getEmpenhos()) {
            if (execucao.getEmpenho() != null) {
                List<EmpenhoEstorno> estornos = Lists.newArrayList();
                estornos.addAll(empenhoEstornoFacade.buscarEstornoEmpenho(execucao.getEmpenho()));
                estornos.addAll(empenhoEstornoFacade.buscarCancelamentoRestoEmpenhoOriginal(execucao.getEmpenho()));
                execucao.setEstornosEmpenho(estornos);
            }
        }
    }

    public Map<GrupoMaterial, List<ExecucaoContratoItemDotacao>> montarMapGrupoMaterialItemDotacao(ExecucaoContrato execucaoContrato) {
        Map<GrupoMaterial, List<ExecucaoContratoItemDotacao>> mapItens = Maps.newHashMap();
        for (ExecucaoContratoTipo exTipo : execucaoContrato.getReservas()) {
            if (exTipo.getTipoObjetoCompra().isMaterialConsumo()) {
                for (ExecucaoContratoTipoFonte exFonte : exTipo.getFontes()) {
                    for (ExecucaoContratoItemDotacao itemDotacao : exFonte.getItens()) {
                        if ((itemDotacao.getQuantidade().compareTo(BigDecimal.ZERO) > 0
                            || itemDotacao.getValorTotal().compareTo(BigDecimal.ZERO) > 0)) {
                            GrupoObjetoCompra grupoObjetoCompra = itemDotacao.getExecucaoContratoItem().getItemContrato().getItemAdequado().getObjetoCompra().getGrupoObjetoCompra();
                            GrupoMaterial grupoMaterial = getGrupoMaterial(execucaoContrato, grupoObjetoCompra);
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

    public List<AgrupadorSolicitacaoEmpenho> criarAgrupadorSolicitacaoEmpenhoSemGrupo(ExecucaoContrato execucaoContrato) {
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();
        for (ExecucaoContratoTipo exTipo : execucaoContrato.getReservas()) {
            if (!exTipo.isObjetoCompraConsumoOrPermanenteMovel()) {
                for (ExecucaoContratoTipoFonte exFonte : exTipo.getFontes()) {
                    for (ExecucaoContratoItemDotacao item : exFonte.getItens()) {
                        AgrupadorSolicitacaoEmpenho agrupador = novoAgrupadorSolicitacaoEmpenho(exFonte, Lists.<Conta>newArrayList());
                        adicionarItemDotacaoNoAgrupador(agrupadores, item, Lists.<Conta>newArrayList(), agrupador);
                    }
                }
            }
        }
        return agrupadores;
    }

    public List<AgrupadorSolicitacaoEmpenho> criarAgrupadorSolicitacaoEmpenhoGrupoMaterial(ExecucaoContrato execucaoContrato) {
        Map<GrupoMaterial, List<ExecucaoContratoItemDotacao>> mapItens = montarMapGrupoMaterialItemDotacao(execucaoContrato);
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();

        for (Map.Entry<GrupoMaterial, List<ExecucaoContratoItemDotacao>> entry : mapItens.entrySet()) {
            for (ExecucaoContratoItemDotacao itemDotacao : entry.getValue()) {

                ExecucaoContratoTipoFonte execucaoContratoTipoFonte = itemDotacao.getExecucaoContratoTipoFonte();
                Conta contaDespesa = execucaoContratoTipoFonte.getFonteDespesaORC().getDespesaORC().getContaDeDespesa();
                List<Conta> contas = configGrupoMaterialFacade.buscarContasDespesaPorGrupoMaterial(entry.getKey(), execucaoContrato.getDataLancamento(), contaDespesa);
                if (contas == null || contas.isEmpty()) {
                    throw new ExcecaoNegocioGenerica("Nenhuma conta de despesa configurada para o grupo material: " + entry.getKey() + " desdobrada da conta " + contaDespesa + ".");
                }

                AgrupadorSolicitacaoEmpenho agrupador = novoAgrupadorSolicitacaoEmpenho(execucaoContratoTipoFonte, contas);
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

    public Map<GrupoBem, List<ExecucaoContratoItemDotacao>> montarMapGrupoBemItemDotacao(ExecucaoContrato execucaoContrato) {
        Map<GrupoBem, List<ExecucaoContratoItemDotacao>> mapItens = Maps.newHashMap();
        for (ExecucaoContratoTipo exTipo : execucaoContrato.getReservas()) {
            if (exTipo.getTipoObjetoCompra().isMaterialPermanente()) {
                for (ExecucaoContratoTipoFonte exFonte : exTipo.getFontes()) {

                    for (ExecucaoContratoItemDotacao itemDotacao : exFonte.getItens()) {
                        if (itemDotacao.hasQuantidade() || itemDotacao.hasValor()) {

                            GrupoObjetoCompra grupoObjetoCompra = itemDotacao.getExecucaoContratoItem().getItemContrato().getItemAdequado().getObjetoCompra().getGrupoObjetoCompra();
                            GrupoBem grupoBem = getGrupoBem(execucaoContrato, grupoObjetoCompra);

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

    public List<AgrupadorSolicitacaoEmpenho> criarAgrupadorSolicitacaoEmpenhoGrupoBem(ExecucaoContrato execucaoContrato) {
        Map<GrupoBem, List<ExecucaoContratoItemDotacao>> mapItens = montarMapGrupoBemItemDotacao(execucaoContrato);
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();

        for (Map.Entry<GrupoBem, List<ExecucaoContratoItemDotacao>> entry : mapItens.entrySet()) {
            for (ExecucaoContratoItemDotacao itemDotacao : entry.getValue()) {

                ExecucaoContratoTipoFonte execucaoContratoTipoFonte = itemDotacao.getExecucaoContratoTipoFonte();
                Conta contaDespesa = execucaoContratoTipoFonte.getFonteDespesaORC().getDespesaORC().getContaDeDespesa();
                List<Conta> contas = configGrupoBemFacade.buscarContasPorGrupoBem(entry.getKey(), execucaoContrato.getDataLancamento(), contaDespesa);
                if (contas == null || contas.isEmpty()) {
                    throw new ExcecaoNegocioGenerica("Nenhuma conta de despesa configurada para o grupo patrimonial: " + entry.getKey() + " desdobrada da conta " + contaDespesa + ".");
                }

                AgrupadorSolicitacaoEmpenho agrupador = novoAgrupadorSolicitacaoEmpenho(execucaoContratoTipoFonte, contas);
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

    private void adicionarItemDotacaoNoAgrupador(List<AgrupadorSolicitacaoEmpenho> agrupadores, ExecucaoContratoItemDotacao itemDotacao, List<Conta> contas, AgrupadorSolicitacaoEmpenho agrupador) {
        Conta contaDespesa = null;
        if (!contas.isEmpty()) {
            contaDespesa = contas.get(0);
        }
        agrupador.setContaDesdobrada(contaDespesa);
        if (!agrupadores.contains(agrupador)) {
            itemDotacao.setContaDespesa(contaDespesa);
            Util.adicionarObjetoEmLista(agrupador.getItensDotacaoExecucao(), itemDotacao);
            agrupador.setValor(itemDotacao.getValorTotal());
            Util.adicionarObjetoEmLista(agrupadores, agrupador);
        } else {
            for (AgrupadorSolicitacaoEmpenho agrupadorExistente : agrupadores) {
                if (agrupador.equals(agrupadorExistente)) {
                    agrupador = agrupadorExistente;
                }
            }
            itemDotacao.setContaDespesa(contaDespesa);
            Util.adicionarObjetoEmLista(agrupador.getItensDotacaoExecucao(), itemDotacao);
            agrupador.setValor(agrupador.getValor().add(itemDotacao.getValorTotal()));
            Util.adicionarObjetoEmLista(agrupadores, agrupador);
        }
    }

    private GrupoMaterial getGrupoMaterial(ExecucaoContrato execucaoContrato, GrupoObjetoCompra grupoObjetoCompra) {
        try {
            return associcaoGrupoMaterialFacade.buscarAssociacaoPorGrupoObjetoCompraVigente(grupoObjetoCompra, execucaoContrato.getDataLancamento()).getGrupoMaterial();
        } catch (Exception e) {
            throw e;
        }
    }

    private GrupoBem getGrupoBem(ExecucaoContrato execucaoContrato, GrupoObjetoCompra grupoObjetoCompra) {
        try {
            return associacaoGrupoBemFacade.recuperarAssociacaoDoGrupoObjetoCompra(grupoObjetoCompra, execucaoContrato.getDataLancamento()).getGrupoBem();
        } catch (Exception e) {
            throw e;
        }
    }

    public void atribuirGrupoContaDespesa(List<ExecucaoContratoItemDotacao> itens) {
        for (ExecucaoContratoItemDotacao itemDotacao : itens) {
            contratoFacade.atribuirGrupoContaDespesa(itemDotacao.getExecucaoContratoItem().getItemContrato());
        }
    }

    public void ordernarItensAgrupadorSolicitacaoEmpenho(List<AgrupadorSolicitacaoEmpenho> agrupadores) {
        for (AgrupadorSolicitacaoEmpenho agrupador : agrupadores) {
            Collections.sort(agrupador.getItensDotacaoExecucao());
        }
    }

    public AgrupadorSolicitacaoEmpenho novoAgrupadorSolicitacaoEmpenho(ExecucaoContratoTipoFonte exFonte, List<Conta> contas) {
        AgrupadorSolicitacaoEmpenho agrupador = new AgrupadorSolicitacaoEmpenho(exFonte.getFonteDespesaORC(), contas);
        agrupador.setGerarReserva(exFonte.getGerarReserva());
        agrupador.setClasseCredor(exFonte.getExecucaoContratoTipo().getClasseCredor());
        agrupador.setIdOrigemSaldoOrcamentario(exFonte.getId());
        agrupador.setClasseOrigemSaldoOrcamentario(exFonte.getClass().getSimpleName());
        agrupador.setTipoObjetoCompra(exFonte.getExecucaoContratoTipo().getTipoObjetoCompra());
        return agrupador;
    }

    public ExecucaoContrato criarExecucaoContrato(Contrato contrato, SaldoItemContratoOrigemVO saldoItem) {
        ExecucaoContrato novaExecucao = criarNovaExecucaoContrato(contrato, saldoItem);
        for (ItemContrato itemContrato : contrato.getItens()) {
            List<SaldoItemContratoOrigemVO> origensExecucao = getMovimentosOrigemExecucao(contrato, saldoItem);
            for (SaldoItemContratoOrigemVO origem : origensExecucao) {
                for (SaldoItemContrato itemSaldo : origem.getItensSaldo()) {

                    if (itemContrato.equals(itemSaldo.getItemContrato())) {
                        ExecucaoContratoItem execucaoContratoItem = criarExecucaoContratoItem(novaExecucao, itemContrato, itemSaldo);
                        if (itemContrato.getTipoControle().isTipoControlePorQuantidade()) {
                            Util.adicionarObjetoEmLista(novaExecucao.getItensQuantidade(), execucaoContratoItem);
                            continue;
                        }
                        Util.adicionarObjetoEmLista(novaExecucao.getItensValor(), execucaoContratoItem);
                    }
                }
            }
        }
        if (novaExecucao.hasItens()) {
            Collections.sort(novaExecucao.getItensQuantidade());
            Collections.sort(novaExecucao.getItensValor());
        }
        return novaExecucao;
    }

    public SaldoItemContratoOrigemVO recuperarMovimentoOrigemExecucao(ExecucaoContrato entity) {
        if (entity.getOrigem() != null && entity.getIdOrigem() != null) {
            SaldoItemContratoOrigemVO movOrigem = new SaldoItemContratoOrigemVO();
            movOrigem.setOrigem(entity.getOrigem());
            movOrigem.setIdMovimentoOrigem(entity.getIdOrigem());

            switch (movOrigem.getOrigem()) {
                case CONTRATO:
                    movOrigem.setNumeroAnoTermo(entity.getContrato().getNumeroAnoTermo());
                    return movOrigem;
                case ADITIVO:
                case APOSTILAMENTO:
                    AlteracaoContratual alteracaoCot = em.find(AlteracaoContratual.class, movOrigem.getIdMovimentoOrigem());
                    if (alteracaoCot != null) {
                        movOrigem.setNumeroAnoTermo(alteracaoCot.getNumeroAnoTermo());
                    }
                    return movOrigem;
            }
        }
        return null;
    }

    private List<SaldoItemContratoOrigemVO> getMovimentosOrigemExecucao(Contrato contrato, SaldoItemContratoOrigemVO saldoOrigem) {
        List<SaldoItemContratoOrigemVO> movimentos = saldoOrigem == null
            ? saldoItemContratoFacade.buscarSaldoOrigemVO(contrato, SubTipoSaldoItemContrato.EXECUCAO)
            : Lists.newArrayList(saldoOrigem);
        Collections.sort(movimentos);
        return movimentos;
    }

    private ExecucaoContrato criarNovaExecucaoContrato(Contrato contrato, SaldoItemContratoOrigemVO movOrigem) {
        ExecucaoContrato novaExecucao = new ExecucaoContrato();

        novaExecucao.setContrato(contrato);
        novaExecucao.setNumero(novaExecucao.getProximoNumero(contrato.getExecucoes()));
        novaExecucao.setOrigem(movOrigem.getOrigem());
        novaExecucao.setIdOrigem(movOrigem.getIdMovimentoOrigem());
        novaExecucao.setOperacaoOrigem(movOrigem.getOperacao());
        novaExecucao.setItens(Lists.<ExecucaoContratoItem>newArrayList());
        novaExecucao.setReservas(Lists.<ExecucaoContratoTipo>newArrayList());
        return novaExecucao;
    }

    private ExecucaoContratoItem criarExecucaoContratoItem(ExecucaoContrato novaExecucao, ItemContrato itemContrato, SaldoItemContrato saldoItem) {
        ExecucaoContratoItem novoItemExec = new ExecucaoContratoItem();
        novoItemExec.setExecucaoContrato(novaExecucao);
        novoItemExec.setItemContrato(itemContrato);

        novoItemExec.setQuantidadeDisponivel(saldoItem.getSaldoQuantidade());
        novoItemExec.setQuantidadeUtilizada(novoItemExec.isExecucaoPorValor() ? BigDecimal.ONE : saldoItem.getSaldoQuantidade());

        novoItemExec.setValorDisponivel(saldoItem.getSaldoValor());
        novoItemExec.setValorUnitario(saldoItem.getValorUnitario());
        novoItemExec.setValorTotal(novoItemExec.getValorDisponivel());
        return novoItemExec;
    }

    public void gerarMovimentoItemContrato(ExecucaoContrato entity) {
        for (ExecucaoContratoItem itemExecucao : entity.getItens()) {
            saldoItemContratoFacade.gerarSaldoExecucao(itemExecucao, sistemaFacade.getDataOperacao());
        }
    }

    public ExecucaoContrato salvarExecucaoContratoAndamento(ExecucaoContrato entity) {
        try {
            ExecucaoContratoEstorno estorno = getExecucaoContratoEstorno(entity);
            entity = em.merge(entity);
            vincularSolicitacaoEmpenhoNaExecucao(entity);
            atribuirSaldoContrato(entity);
            criarMovimentoItemContratoExecucaoAndamento(entity, estorno);
            return entity;
        } catch (Exception e) {
            logger.error("salvarExecucaoContratoAndamentento()", e);
            throw e;
        }
    }

    private ExecucaoContratoEstorno getExecucaoContratoEstorno(ExecucaoContrato entity) {
        ExecucaoContratoEstorno estorno = null;
        if (entity.hasEstornosEmpenho()) {
            estorno = entity.getEstornosExecucao().get(0);
            if (estorno.getNumero() == null) {
                estorno.setNumero(singletonGeradorCodigo.getProximoCodigo(ExecucaoContratoEstorno.class, "numero"));
            }
            estorno.setExecucaoContrato(entity);
        }
        return estorno;
    }

    public void atribuirSaldoContrato(ExecucaoContrato entity) {
        entity = recuperarComDependenciasExecucaoEmpenho(entity.getId());
        BigDecimal estornoExecucao = entity.getValorEstornado().add(entity.getValorCancelamentoRestoPagar());
        BigDecimal valorMovimento = entity.getValorEmpenhado().subtract(estornoExecucao);

        Contrato contrato = entity.getContrato();
        BigDecimal saldoContrato = contrato.getSaldoAtualContrato().subtract(valorMovimento);
        saldoContrato = saldoContrato.compareTo(BigDecimal.ZERO) > 0 ? saldoContrato : BigDecimal.ZERO;
        contrato.setSaldoAtualContrato(saldoContrato);
    }

    private void criarMovimentoItemContratoExecucaoAndamento(ExecucaoContrato execucao, ExecucaoContratoEstorno estornoExecucao) {
        for (ExecucaoContratoItem itemExecucao : execucao.getItens()) {
            saldoItemContratoFacade.gerarSaldoExecucao(itemExecucao, sistemaFacade.getDataOperacao());
        }
        if (estornoExecucao != null) {
            for (ExecucaoContratoEmpenhoEstorno execucaoEstorno : estornoExecucao.getEstornosEmpenho()) {
                for (ExecucaoContratoEmpenhoEstornoItem item : execucaoEstorno.getItens()) {
                    saldoItemContratoFacade.gerarSaldoExecucaoEstorno(item, sistemaFacade.getDataOperacao());
                }
            }
        }
    }

    private void gerarSolicitacaoEmpenho(ExecucaoContrato execucaoContrato, List<AgrupadorSolicitacaoEmpenho> agrupadores) {
        for (AgrupadorSolicitacaoEmpenho agrupador : agrupadores) {
            SolicitacaoEmpenhoVo solicitacaoEmpenhoVo = criarVoSolicitacaoEmpenho(execucaoContrato, agrupador);
            SolicitacaoEmpenho solicitacaoEmpenho = solicitacaoEmpenhoFacade.gerarSolicitacaoEmpenhoSalvando(solicitacaoEmpenhoVo);
            criarExecucaoContratoEmpenho(execucaoContrato, solicitacaoEmpenho);
        }
    }

    public SolicitacaoEmpenhoVo criarVoSolicitacaoEmpenho(ExecucaoContrato execucaoContrato, AgrupadorSolicitacaoEmpenho agrupador) {
        Contrato contrato = execucaoContrato.getContrato();
        FonteDespesaORC fonteDespesaORC = agrupador.getFonteDespesaORC();
        UnidadeOrganizacional unidadeOrganizacional = fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional();
        String historico = "Solicitação gerada através do contrato: " + contrato + " em: " + DataUtil.getDataFormatada(execucaoContrato.getDataLancamento())
            + ". Execução do Contrato nº " + execucaoContrato.getNumero() + ". Dotação: " + fonteDespesaORC + " - " + Util.formataValor(execucaoContrato.getValor());

        SolicitacaoEmpenhoVo solicitacaoEmpenhoVo = new SolicitacaoEmpenhoVo();
        solicitacaoEmpenhoVo.setValor(agrupador.getValor());
        solicitacaoEmpenhoVo.setUnidadeOrganizacional(unidadeOrganizacional);
        solicitacaoEmpenhoVo.setTipoEmpenho(execucaoContrato.getFormaEntrega().isUnica() ? TipoEmpenho.ORDINARIO : null);
        solicitacaoEmpenhoVo.setHistoricoContabil(null);
        solicitacaoEmpenhoVo.setFonteDespesaOrc(fonteDespesaORC);
        solicitacaoEmpenhoVo.setDespesaOrc(fonteDespesaORC.getDespesaORC());
        solicitacaoEmpenhoVo.setDataSolicitacao(execucaoContrato.getDataLancamento());
        solicitacaoEmpenhoVo.setContaDespesaDesdobrada(agrupador.getContaDesdobrada());
        solicitacaoEmpenhoVo.setComplementoHistorico(historico);
        solicitacaoEmpenhoVo.setFornecedor(contrato.getContratado());
        solicitacaoEmpenhoVo.setGerarReserva(agrupador.getGerarReserva());
        solicitacaoEmpenhoVo.setClasseCredor(agrupador.getClasseCredor());
        solicitacaoEmpenhoVo.setContrato(contrato);
        solicitacaoEmpenhoVo.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        solicitacaoEmpenhoVo.setOrigemSocilicitacao(OrigemSolicitacaoEmpenho.CONTRATO);
        return solicitacaoEmpenhoVo;
    }

    private void gerarSaldoReservadoPorLicitacao(ExecucaoContrato entity) {
        for (ExecucaoContratoTipo reserva : entity.getReservas()) {
            for (ExecucaoContratoTipoFonte fonte : reserva.getFontes()) {
                if (fonte.getGerarReserva()) {
                    SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                        fonte.getFonteDespesaORC(),
                        OperacaoORC.RESERVADO_POR_LICITACAO,
                        TipoOperacaoORC.NORMAL,
                        fonte.getValor(),
                        entity.getDataLancamento(),
                        fonte.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                        fonte.getId().toString(),
                        fonte.getClass().getSimpleName(),
                        fonte.getExecucaoContratoTipo().getExecucaoContrato().getContrato().getNumeroAnoTermo(),
                        getHistoricoExecucaoContrato(fonte.getFonteDespesaORC(), entity, reserva.getClasseCredor())
                    );
                    saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
                }
            }
        }
    }

    private void estornarSaldoReservadoPorLicitacao(ExecucaoContrato entity) {
        for (ExecucaoContratoTipo reserva : entity.getReservas()) {

            Integer ano = DataUtil.getAno(reserva.getExecucaoContrato().getDataLancamento());
            if (ano.equals(sistemaFacade.getExercicioCorrente().getAno())) {

                for (ExecucaoContratoTipoFonte exFonte : reserva.getFontes()) {
                    Empenho empenho = getEmpenhoPorFonteDespesaOrcExecucao(entity, exFonte);
                    if (exFonte.getGerarReserva() && empenho == null && validarValorColunaReservadoPorLicitacao(exFonte)) {
                        SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                            exFonte.getFonteDespesaORC(),
                            OperacaoORC.RESERVADO_POR_LICITACAO,
                            TipoOperacaoORC.ESTORNO,
                            exFonte.getValor(),
                            entity.getDataLancamento(),
                            exFonte.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                            exFonte.getId().toString(),
                            exFonte.getClass().getSimpleName(),
                            exFonte.getExecucaoContratoTipo().getExecucaoContrato().getContrato().getNumeroAnoTermo(),
                            getHistoricoExecucaoContrato(exFonte.getFonteDespesaORC(), entity, exFonte.getExecucaoContratoTipo().getClasseCredor())
                        );
                        saldoFonteDespesaORCFacade.gerarSaldoOrcamentarioSemRealizarValidacao(vo);
                    }
                }
            }
        }
    }

    private Empenho getEmpenhoPorFonteDespesaOrcExecucao(ExecucaoContrato entity, ExecucaoContratoTipoFonte exFonte) {
        Empenho empenho = null;
        for (ExecucaoContratoEmpenho exEmp : entity.getEmpenhos()) {
            if (exEmp.getEmpenho() != null
                && exEmp.getEmpenho().getFonteDespesaORC().equals(exFonte.getFonteDespesaORC())
                && exEmp.getEmpenho().getValor().equals(exFonte.getValor())) {
                empenho = exEmp.getEmpenho();
                break;
            }
        }
        return empenho;
    }

    public String getHistoricoExecucaoContrato(FonteDespesaORC fonteDespesaORC, ExecucaoContrato entity, ClasseCredor classeCredor) {
        Contrato contrato = entity.getContrato();
        return
            "Execução nº " + entity.getNumero() + " | " +
                " Contrato: " + contrato.getNumeroContratoAno() + " | " + contrato.getNumeroAnoTermo() + " | " + DataUtil.getDataFormatada(contrato.getDataLancamento()) + " | " +
                contrato.getContratado() + " | " + classeCredor + " | " +
                " Unidade Administrativa: " + contrato.getUnidadeAdministrativa() + " | " +
                " Elemento de Despesa: " + fonteDespesaORC.getDespesaORC() + "/" + fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " | " +
                " Fonte de Recurso: " + fonteDespesaORC.getDescricaoFonteDeRecurso().trim() + ",";
    }

    private void criarExecucaoContratoEmpenho(ExecucaoContrato execucaoContrato, SolicitacaoEmpenho solicitacaoEmpenho) {
        ExecucaoContratoEmpenho execucaoContratoEmpenho = new ExecucaoContratoEmpenho();
        execucaoContratoEmpenho.setExecucaoContrato(execucaoContrato);
        execucaoContratoEmpenho.setSolicitacaoEmpenho(solicitacaoEmpenho);
        em.merge(execucaoContratoEmpenho);
    }

    private boolean validarValorColunaReservadoPorLicitacao(ExecucaoContratoTipoFonte fonte) {
        return saldoFonteDespesaORCFacade.validarValorColunaIndividual(
            fonte.getValor(),
            fonte.getFonteDespesaORC(),
            sistemaFacade.getDataOperacao(),
            fonte.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
            OperacaoORC.RESERVADO_POR_LICITACAO);
    }

    private void vincularSolicitacaoEmpenhoNaExecucao(ExecucaoContrato execucaoContrato) {
        for (ExecucaoContratoEmpenho execContratoEmp : execucaoContrato.getEmpenhos()) {
            Empenho empenho = execContratoEmp.getEmpenho();
            if (empenho != null) {
                if (empenho.getContrato() == null) {
                    empenho.setContrato(execucaoContrato.getContrato());
                    em.merge(empenho);
                }
                SolicitacaoEmpenho solicitacaoEmpenho = solicitacaoEmpenhoFacade.recuperarSolicitacaoEmpenhoPorEmpenho(empenho);
                if (solicitacaoEmpenho == null) {
                    empenho = empenhoFacade.recuperarDetalhamento(empenho.getId());
                    solicitacaoEmpenho = new SolicitacaoEmpenho(empenho);
                }
                execContratoEmp.setSolicitacaoEmpenho(solicitacaoEmpenho);
                em.merge(execContratoEmp);
            }
        }
    }

    public ExecucaoContrato criarEstruturaReservaExecucaoAndamento(ExecucaoContrato execucaoContrato) {
        execucaoContrato.setReservas(Lists.<ExecucaoContratoTipo>newArrayList());
        execucaoContrato.setEstornosExecucao(Lists.<ExecucaoContratoEstorno>newArrayList());
        Map<FonteDespesaORC, ExecucaoContratoTipoFonte> mapFonteDespesaOrcExFonte = null;
        HashMap<TipoObjetoCompra, List<ExecucaoContratoItem>> mapaItensPorTipoObjetoCompra = execucaoContrato.agruparItensPorTipoObjetoCompra();
        Map<TipoObjetoCompra, List<Empenho>> mapTipoObjetoEmpenho = preencherMapaTipoObjetoCompraEmpenhos(execucaoContrato);
        boolean verificarTipoObjetoCompra = true;

        ExecucaoContratoEstorno execucaoContratoEstorno = novaExecucaoContratoEstorno(execucaoContrato);

        for (Map.Entry<TipoObjetoCompra, List<Empenho>> entryEmpenho : mapTipoObjetoEmpenho.entrySet()) {
            TipoObjetoCompra tipoObjetoCompra = entryEmpenho.getKey();
            ExecucaoContratoTipo novaExTipo = novaExecucaoContratoTipo(execucaoContrato, tipoObjetoCompra);

            List<ExecucaoContratoItem> itensExecucao = mapaItensPorTipoObjetoCompra.get(tipoObjetoCompra);
            if (novaExTipo.getTipoObjetoCompra().equals(tipoObjetoCompra)) {
                for (Empenho empenho : entryEmpenho.getValue()) {
                    novaExTipo.setClasseCredor(empenho.getClasseCredor());
                    ExecucaoContratoTipoFonte novoExTipoFonte = novaExecucaoContratoTipoFonte(mapFonteDespesaOrcExFonte, novaExTipo, empenho);

                    if (itensExecucao == null || itensExecucao.isEmpty()) {
                        itensExecucao = execucaoContrato.getItens();
                        verificarTipoObjetoCompra = false;
                    }
                    BigDecimal valorTotalItemPorTipoOC = getValorTotalItemExecucao(itensExecucao);
                    criarEstruturaEstornoExecucao(execucaoContrato, execucaoContratoEstorno, itensExecucao, empenho, tipoObjetoCompra, verificarTipoObjetoCompra);

                    for (ExecucaoContratoItem itemExecucao : itensExecucao) {
                        if (verificarTipoObjetoCompra) {
                            if (itemExecucao.getItemContrato().getItemAdequado().getObjetoCompra().getTipoObjetoCompra().equals(tipoObjetoCompra)) {
                                criarExecucaoContratoItemDotacao(novoExTipoFonte, valorTotalItemPorTipoOC, itemExecucao);
                            }
                        } else {
                            criarExecucaoContratoItemDotacao(novoExTipoFonte, valorTotalItemPorTipoOC, itemExecucao);
                        }
                        Util.adicionarObjetoEmLista(novaExTipo.getFontes(), novoExTipoFonte);
                        mapFonteDespesaOrcExFonte = new HashMap<>();
                        mapFonteDespesaOrcExFonte.put(novoExTipoFonte.getFonteDespesaORC(), novoExTipoFonte);
                    }
                }
            }
            novaExTipo.setValor(novaExTipo.getValorTotalReservado());
        }
        return execucaoContrato;
    }

    private ExecucaoContratoTipoFonte novaExecucaoContratoTipoFonte(Map<FonteDespesaORC, ExecucaoContratoTipoFonte> mapFonteDespesaOrcExFonte, ExecucaoContratoTipo novaExTipo, Empenho empenho) {
        ExecucaoContratoTipoFonte novoExTipoFonte = new ExecucaoContratoTipoFonte();
        novoExTipoFonte.setExecucaoContratoTipo(novaExTipo);
        novoExTipoFonte.setFonteDespesaORC(empenho.getFonteDespesaORC());
        novoExTipoFonte.setGerarReserva(false);

        if (mapFonteDespesaOrcExFonte != null && mapFonteDespesaOrcExFonte.containsKey(novoExTipoFonte.getFonteDespesaORC())) {
            novoExTipoFonte = mapFonteDespesaOrcExFonte.get(novoExTipoFonte.getFonteDespesaORC());
            novoExTipoFonte.setItens(Lists.<ExecucaoContratoItemDotacao>newArrayList());
            novoExTipoFonte.setValor(novoExTipoFonte.getValor().add(empenho.getValor()));
        } else {
            novoExTipoFonte.setValor(empenho.getValor());
        }
        Util.adicionarObjetoEmLista(novaExTipo.getFontes(), novoExTipoFonte);
        return novoExTipoFonte;
    }

    private ExecucaoContratoTipo novaExecucaoContratoTipo(ExecucaoContrato execucaoContrato, TipoObjetoCompra tipoObjetoCompra) {
        ExecucaoContratoTipo novoExTipo = new ExecucaoContratoTipo();
        novoExTipo.setExecucaoContrato(execucaoContrato);
        novoExTipo.setTipoObjetoCompra(tipoObjetoCompra);
        Util.adicionarObjetoEmLista(execucaoContrato.getReservas(), novoExTipo);
        return novoExTipo;
    }

    private void criarExecucaoContratoItemDotacao(ExecucaoContratoTipoFonte novoExTipoFonte, BigDecimal valorTotalItemPorTipoOC, ExecucaoContratoItem itemExecucao) {
        ExecucaoContratoItemDotacao itemDotacao = new ExecucaoContratoItemDotacao();
        itemDotacao.setExecucaoContratoTipoFonte(novoExTipoFonte);
        itemDotacao.setExecucaoContratoItem(itemExecucao);

        if (valorTotalItemPorTipoOC.compareTo(novoExTipoFonte.getValor()) >= 0) {
            BigDecimal diferenca = valorTotalItemPorTipoOC.subtract(novoExTipoFonte.getValor());
            BigDecimal percentualDiferenca = diferenca.divide(valorTotalItemPorTipoOC, 8, RoundingMode.HALF_EVEN).multiply(new BigDecimal("100"));
            BigDecimal percentualPorItem = itemExecucao.getValorTotal().multiply(percentualDiferenca).divide(new BigDecimal("100"), 8, RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN);

            if (itemExecucao.isExecucaoPorQuantidade()) {
                itemDotacao.setValorTotal(percentualPorItem.compareTo(BigDecimal.ZERO) > 0 ? itemExecucao.getValorTotal().subtract(percentualPorItem) : itemExecucao.getValorTotal());
                itemDotacao.setValorUnitario(itemExecucao.getValorUnitario());

                BigDecimal quantidade = itemDotacao.getValorTotal().divide(itemDotacao.getValorUnitario(), 8, RoundingMode.FLOOR);
                itemDotacao.setQuantidade(quantidade.setScale(2, RoundingMode.HALF_EVEN));
            } else {
                itemDotacao.setValorTotal(percentualPorItem.compareTo(BigDecimal.ZERO) > 0 ? itemExecucao.getValorTotal().subtract(percentualPorItem) : itemExecucao.getValorTotal());
                itemDotacao.setValorUnitario(itemDotacao.getValorTotal());
            }
        } else {
            itemDotacao.setQuantidade(itemExecucao.isExecucaoPorQuantidade() ? itemExecucao.getQuantidadeUtilizada() : BigDecimal.ZERO);
            itemDotacao.setValorUnitario(itemExecucao.isExecucaoPorQuantidade() ? itemExecucao.getValorUnitario() : itemExecucao.getValorTotal());
            itemDotacao.setValorTotal(itemExecucao.getValorTotal());
            if (itemExecucao.isExecucaoPorQuantidade()) {
                itemDotacao.calcularValorTotal();
            }
        }

        Util.adicionarObjetoEmLista(novoExTipoFonte.getItens(), itemDotacao);
    }

    private void criarEstruturaEstornoExecucao(ExecucaoContrato execucaoContrato, ExecucaoContratoEstorno execucaoContratoEstorno, List<ExecucaoContratoItem> itensExecucao, Empenho empenho, TipoObjetoCompra tipoObjetoCompra, boolean verificarTipoObjetoCompra) {
        if (execucaoContratoEstorno != null) {
            for (ExecucaoContratoEmpenho exEmp : execucaoContrato.getEmpenhos()) {
                for (EmpenhoEstorno empenhoEstorno : exEmp.getEstornosEmpenho()) {
                    if (empenhoEstorno.getEmpenho().equals(empenho)) {
                        ExecucaoContratoEmpenhoEstorno execucaoContratoEmpenhoEstorno = novaExecucaoContratoEmpenhoEstorno(execucaoContratoEstorno, empenho, empenhoEstorno);
                        BigDecimal valorTotalItemPorTipoOC = getValorTotalItemExecucao(itensExecucao);
                        for (ExecucaoContratoItem itemExecucao : itensExecucao) {
                            if (verificarTipoObjetoCompra) {
                                if (itemExecucao.getItemContrato().getItemAdequado().getObjetoCompra().getTipoObjetoCompra().equals(tipoObjetoCompra)) {
                                    criarExecucaoContratoEmpenhoEstornoItem(empenhoEstorno, execucaoContratoEmpenhoEstorno, valorTotalItemPorTipoOC, itemExecucao);
                                }
                            } else {
                                criarExecucaoContratoEmpenhoEstornoItem(empenhoEstorno, execucaoContratoEmpenhoEstorno, valorTotalItemPorTipoOC, itemExecucao);
                            }
                        }
                        Util.adicionarObjetoEmLista(execucaoContratoEstorno.getEstornosEmpenho(), execucaoContratoEmpenhoEstorno);
                    }
                }
                execucaoContratoEstorno.setValorTotal(execucaoContratoEstorno.getValorTotalEmpenhoEstono());
            }
            Util.adicionarObjetoEmLista(execucaoContrato.getEstornosExecucao(), execucaoContratoEstorno);

            atribuirDiferencaItemEstornoComValorTotal(execucaoContratoEstorno);
        }
    }

    public void atribuirDiferencaItemEstornoComValorTotal(ExecucaoContratoEstorno execucaoContratoEstorno) {
        for (ExecucaoContratoEmpenhoEstorno est : execucaoContratoEstorno.getEstornosEmpenho()) {
            BigDecimal diferencaItemComEstorno = execucaoContratoEstorno.getValorTotal().subtract(est.getValorTotalItens());
            if (diferencaItemComEstorno.compareTo(BigDecimal.ZERO) != 0) {
                ExecucaoContratoEmpenhoEstornoItem ultimoItem = est.getItens().get(est.getItens().size() - 1);
                boolean valorTotalItensIsMenor = est.getValorTotalItens().compareTo(execucaoContratoEstorno.getValorTotal()) < 0;
                ultimoItem.setValorTotal(valorTotalItensIsMenor
                    ? ultimoItem.getValorTotal().add(diferencaItemComEstorno)
                    : ultimoItem.getValorTotal().subtract(diferencaItemComEstorno.negate()));
                if (ultimoItem.getExecucaoContratoItem().isExecucaoPorQuantidade()) {
                    BigDecimal quantidade = ultimoItem.getValorTotal().divide(ultimoItem.getValorUnitario(), 8, RoundingMode.FLOOR);
                    ultimoItem.setQuantidade(quantidade.setScale(2, RoundingMode.HALF_EVEN));
                }
            }
        }
    }

    private ExecucaoContratoEstorno novaExecucaoContratoEstorno(ExecucaoContrato execucaoContrato) {
        ExecucaoContratoEstorno execucaoContratoEstorno = null;
        if (execucaoContrato.hasEstornosEmpenho()) {
            execucaoContratoEstorno = new ExecucaoContratoEstorno();
            execucaoContratoEstorno.setDataLancamento(execucaoContrato.getDataLancamento());
            execucaoContratoEstorno.setExecucaoContrato(execucaoContrato);
            execucaoContratoEstorno.setEstornosEmpenho(Lists.<ExecucaoContratoEmpenhoEstorno>newArrayList());
        }
        return execucaoContratoEstorno;
    }

    private ExecucaoContratoEmpenhoEstorno novaExecucaoContratoEmpenhoEstorno(ExecucaoContratoEstorno exContEst, Empenho empenho, EmpenhoEstorno empenhoEst) {
        ExecucaoContratoEmpenhoEstorno novaExecContEmpEstorno = new ExecucaoContratoEmpenhoEstorno();
        novaExecContEmpEstorno.setExecucaoContratoEstorno(exContEst);
        novaExecContEmpEstorno.setItens(Lists.<ExecucaoContratoEmpenhoEstornoItem>newArrayList());
        novaExecContEmpEstorno.setValor(empenhoEst.getValor());

        SolicitacaoEmpenho solEmp = solicitacaoEmpenhoFacade.recuperarSolicitacaoEmpenhoPorEmpenho(empenho);

        SolicitacaoEmpenhoEstorno novaSolEmp = new SolicitacaoEmpenhoEstorno(solEmp, empenho, OrigemSolicitacaoEmpenho.CONTRATO);
        novaSolEmp.setDataSolicitacao(sistemaFacade.getDataOperacao());
        novaSolEmp.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        novaSolEmp.setValor(empenhoEst.getValor());
        novaSolEmp.setDataSolicitacao(empenho.getDataEmpenho());
        novaSolEmp.setEmpenhoEstorno(empenhoEst);
        novaSolEmp.setHistorico(solicitacaoEmpenhoEstornoFacade.getHistoricoSolicitacaoEmpenho(novaSolEmp.getClasseCredor(), novaSolEmp.getFonteDespesaORC(), exContEst.getExecucaoContrato()));
        novaExecContEmpEstorno.setSolicitacaoEmpenhoEstorno(novaSolEmp);
        return novaExecContEmpEstorno;
    }

    private void criarExecucaoContratoEmpenhoEstornoItem(EmpenhoEstorno empenhoEstorno, ExecucaoContratoEmpenhoEstorno execucaoContratoEmpenhoEstorno, BigDecimal valorTotalItemPorTipoOC, ExecucaoContratoItem itemExecucao) {
        BigDecimal diferenca = valorTotalItemPorTipoOC.subtract(empenhoEstorno.getValor());
        BigDecimal percentualDiferenca = diferenca.divide(valorTotalItemPorTipoOC, 8, RoundingMode.HALF_EVEN).multiply(new BigDecimal("100"));
        BigDecimal percentualPorItem = itemExecucao.getValorTotal().multiply(percentualDiferenca).divide(new BigDecimal("100"), 8, RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN);

        ExecucaoContratoEmpenhoEstornoItem itemEstorno = new ExecucaoContratoEmpenhoEstornoItem();
        itemEstorno.setExecucaoContratoEmpenhoEst(execucaoContratoEmpenhoEstorno);
        itemEstorno.setExecucaoContratoItem(itemExecucao);

        if (itemExecucao.isExecucaoPorQuantidade()) {
            itemEstorno.setValorTotal(percentualPorItem.compareTo(BigDecimal.ZERO) > 0 ? itemExecucao.getValorTotal().subtract(percentualPorItem) : itemExecucao.getValorTotal());
            itemEstorno.setValorUnitario(itemExecucao.getValorUnitario());

            BigDecimal quantidade = itemEstorno.getValorTotal().divide(itemEstorno.getValorUnitario(), 8, RoundingMode.FLOOR);
            itemEstorno.setQuantidade(quantidade.setScale(2, RoundingMode.HALF_EVEN));
        } else {
            itemEstorno.setValorTotal(percentualPorItem.compareTo(BigDecimal.ZERO) > 0 ? itemExecucao.getValorTotal().subtract(percentualPorItem) : itemExecucao.getValorTotal());
            itemEstorno.setValorUnitario(itemEstorno.getValorTotal());
        }
        Util.adicionarObjetoEmLista(execucaoContratoEmpenhoEstorno.getItens(), itemEstorno);
    }

    private BigDecimal getValorTotalItemExecucao(List<ExecucaoContratoItem> itensExecucao) {
        BigDecimal total = BigDecimal.ZERO;
        for (ExecucaoContratoItem item : itensExecucao) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    private Map<TipoObjetoCompra, List<Empenho>> preencherMapaTipoObjetoCompraEmpenhos(ExecucaoContrato execucaoContrato) {
        Map<TipoObjetoCompra, List<Empenho>> mapTipoObjetoEmpenho = Maps.newHashMap();
        for (ExecucaoContratoEmpenho exEmp : execucaoContrato.getEmpenhos()) {
            TipoObjetoCompra tipoObjetoCompra = TipoContaDespesa.getTipoObjetoCompra(exEmp.getEmpenho().getTipoContaDespesa());
            if (!mapTipoObjetoEmpenho.containsKey(tipoObjetoCompra)) {
                mapTipoObjetoEmpenho.put(tipoObjetoCompra, Lists.newArrayList(exEmp.getEmpenho()));
            } else {
                List<Empenho> empenhos = mapTipoObjetoEmpenho.get(tipoObjetoCompra);
                empenhos.add(exEmp.getEmpenho());
                mapTipoObjetoEmpenho.put(tipoObjetoCompra, empenhos);
            }
        }
        return mapTipoObjetoEmpenho;
    }

    public Boolean isEmpenhoReajustePosExecucao(Empenho empenho) {
        String sql = " select ex.* from execucaocontrato ex " +
            "           inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id " +
            "           where exemp.empenho_id = :idEmpenho " +
            "           and ex.operacaoorigem = :operacaoPosExec ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEmpenho", empenho.getId());
        q.setParameter("operacaoPosExec", OperacaoSaldoItemContrato.POS_EXECUCAO.name());
        return !q.getResultList().isEmpty();
    }

    public BigDecimal buscarValorExecutadoPorTipoObjetoCompra(Contrato contrato, TipoObjetoCompra tipoObjetoCompra) {
        String sql = " select coalesce(sum(exitem.valortotal),0) as valor_executado from execucaocontratoitem exitem " +
            "           inner join itemcontrato ic on ic.id = exitem.itemcontrato_id " +
            "           left join itemcontratoitempropfornec icpf on ic.id = icpf.itemcontrato_id " +
            "           left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "           left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "           left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            "           left join itemcontratoitempropdisp icpd on ic.id = icpd.itemcontrato_id " +
            "           left join itempropostafornedisp ipfd on icpd.itempropfornecdispensa_id = ipfd.id " +
            "           left join itemprocessodecompra ipc on coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) = ipc.id " +
            "           left join itemsolicitacao its on ipc.itemsolicitacaomaterial_id = its.id " +
            "           left join itemcontratoitemsolext icse on ic.id = icse.itemcontrato_id " +
            "           left join itemsolicitacaoexterno ise on icse.itemsolicitacaoexterno_id = ise.id " +
            "           left join itemcontratovigente icv on ic.id = icv.itemcontrato_id " +
            "           left join itemcotacao itcot on itcot.id = icv.itemcotacao_id " +
            "           left join objetocompra obj on obj.id = coalesce(ic.objetocompracontrato_id, its.objetocompra_id, ise.objetocompra_id, itcot.objetocompra_id) " +
            "        where ic.contrato_id = :idContrato " +
            "           and obj.tipoobjetocompra = :tipoObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("tipoObjeto", tipoObjetoCompra.name());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public List<ExecucaoContrato> buscarExecucaoContratoComSaldoAEstornar(String parte) {
        String sql = " " +
            "select distinct * from (" +
            " select ex.* from execucaocontrato ex " +
            "   inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id " +
            "   left join empenho emp on emp.id = exemp.empenho_id " +
            "   left join empenho emprest on emprest.empenho_id = emp.id " +
            "   inner join contrato c on c.id = ex.contrato_id " +
            "   inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "   inner join pessoa p on c.contratado_id = p.id " +
            "   left join pessoafisica pf on p.id = pf.id " +
            "   left join pessoajuridica pj on p.id = pj.id " +
            "   where (c.numerotermo like :parte " +
            "           or c.numerocontrato like :parte " +
            "           or e.ano like :parte " +
            "           or lower(pf.nome) like :parte " +
            "           or lower(pj.razaosocial) like :parte " +
            "           or replace(replace(pf.cpf,'.',''),'-','') like :parte " +
            "           or pf.cpf like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''), '/', '') like :parte " +
            "           or pj.cnpj like :parte" +
            "           or to_char(c.numerotermo) || '/' || to_char(e.ano) like :numeroAno" +
            "           or to_char(c.numerotermo) || '/' || to_char(e.ano)  || ' ' || ex.numero like :numeroAno" +
            "           or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :numeroAno " +
            "         or ex.valor like :numeroAno or replace(ex.valor, '.', ',') like :numeroAno) " +
            "  order by e.ano desc, c.numerotermo desc" +
            ") ";
        Query q = em.createNativeQuery(sql, ExecucaoContrato.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("numeroAno", parte.trim() + "%");
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<ExecucaoContratoTipoFonte> buscarExecucaoContratoFontePorContrato(Contrato contrato) {
        String sql = " " +
            " select distinct exfonte.* from execucaocontratotipofonte exfonte " +
            "   inner join execucaocontratotipo extipo on extipo.id = exfonte.execucaocontratotipo_id " +
            "   inner join execucaocontrato ex on ex.id = extipo.execucaocontrato_id  " +
            " where ex.contrato_id = :idContrato ";
        Query q = em.createNativeQuery(sql, ExecucaoContratoTipoFonte.class);
        q.setParameter("idContrato", contrato.getId());
        return q.getResultList();
    }

    public ExecucaoContratoEmpenho buscarExecucaoContratoEmpenhoPorSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        String sql = " select exemp.* from execucaocontratoempenho exemp " +
            "          where exemp.solicitacaoempenho_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql, ExecucaoContratoEmpenho.class);
        q.setParameter("idSolicitacao", solicitacaoEmpenho.getId());
        try {
            return (ExecucaoContratoEmpenho) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public ExecucaoContratoEmpenho buscarExecucaoContratoEmpenhoPorEmpenho(Empenho empenho) {
        String sql = " select exemp.* from execucaocontratoempenho exemp" +
            "          where exemp.empenho_id = :idEmpenho ";
        Query q = em.createNativeQuery(sql, ExecucaoContratoEmpenho.class);
        q.setParameter("idEmpenho", empenho.getId());
        try {
            return (ExecucaoContratoEmpenho) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public FormaEntregaExecucao buscarFormaEntregaExecucao(SolicitacaoEmpenho solicitacaoEmpenho) {
        String hql = " select ex.formaEntrega from ExecucaoContratoEmpenho exEmp " +
            "          inner join exEmp.execucaoContrato ex " +
            "          where exEmp.solicitacaoEmpenho = :solicitacaoEmp ";
        Query q = em.createQuery(hql, FormaEntregaExecucao.class);
        q.setParameter("solicitacaoEmp", solicitacaoEmpenho);
        try {
            return (FormaEntregaExecucao) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<ExecucaoContrato> buscarExecucaoContratoComEstornoEmpenho() {
        String sql = " select distinct ex.* from execucaocontrato ex " +
            "inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id " +
            "inner join empenho emp on emp.id = exemp.empenho_id " +
            "inner join empenhoestorno est on est.empenho_id = emp.id ";
        Query q = em.createNativeQuery(sql, ExecucaoContrato.class);
        return q.getResultList();
    }

    public ExecucaoContratoItemDotacao buscarItemDotacao(ExecucaoContrato execucao, FonteDespesaORC fonte, ExecucaoContratoItem item) {
        String sql = " select itemdot.* from execucaocontratoitemdot itemdot " +
            "         inner join execucaocontratotipofonte exfonte on exfonte.id = itemdot.execucaocontratotipofonte_id " +
            "         inner join execucaocontratotipo extipo on extipo.id = exfonte.execucaocontratotipo_id " +
            "where extipo.execucaocontrato_id = :idExecucao " +
            "and exfonte.fontedespesaorc_id = :idFonte " +
            "and itemdot.execucaocontratoitem_id = :idItem ";
        Query q = em.createNativeQuery(sql, ExecucaoContratoItemDotacao.class);
        q.setParameter("idExecucao", execucao.getId());
        q.setParameter("idFonte", fonte.getId());
        q.setParameter("idItem", item.getId());
        try {
            return (ExecucaoContratoItemDotacao) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public BigDecimal getValorItemContratoDotacao(ExecucaoContrato execucao, FonteDespesaORC fonte, ExecucaoContratoItem item) {
        String sql = " " +
            "  select coalesce(sum(itemdot.valortotal),0) from execucaocontratoitemdot itemdot " +
            "    inner join execucaocontratotipofonte exfonte on exfonte.id = itemdot.execucaocontratotipofonte_id " +
            "    inner join execucaocontratotipo extipo on extipo.id = exfonte.execucaocontratotipo_id " +
            "   where extipo.execucaocontrato_id = :idExecucao " +
            "    and exfonte.fontedespesaorc_id = :idFonte " +
            "    and itemdot.execucaocontratoitem_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", execucao.getId());
        q.setParameter("idFonte", fonte.getId());
        q.setParameter("idItem", item.getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return item.getValorTotal();
        }
    }

    public List<Empenho> buscarEmpenhosExecucao(ExecucaoContrato execucaoContrato) {
        String sql = " select emp.* from empenho emp" +
            "          inner join execucaocontratoempenho exemp on exemp.empenho_id = emp.id " +
            "          where exemp.execucaocontrato_id = :idExecucao " +
            "          order by emp.numero ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idExecucao", execucaoContrato.getId());
        return q.getResultList();
    }

    public List<ExecucaoContratoEmpenho> buscarExecucaoContratoEmpenhoPorContrato(Contrato contrato) {
        String sql = " select exemp.* from execucaocontratoempenho exemp" +
            "           inner join execucaocontrato ex on ex.id = exemp.execucaocontrato_id " +
            "          where ex.contrato_id = :idContrato ";
        Query q = em.createNativeQuery(sql, ExecucaoContratoEmpenho.class);
        q.setParameter("idContrato", contrato.getId());
        return q.getResultList();
    }

    public Date buscarDataPrimeiraExecucaoContrato(Contrato contrato) {
        String sql = " select min(sol.datasolicitacao) as datasolicitacao from execucaocontrato ex " +
            "  inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id " +
            "  inner join solicitacaoempenho sol on sol.id = exemp.solicitacaoempenho_id " +
            "where ex.contrato_id =:idContrato ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", contrato.getId());
        try {
            return (Date) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<ExecucaoContrato> buscarExecucoesContratoEmpenhadas(Contrato contrato) {
        String sql = " select ec.* from execucaocontrato ec " +
            "         where ec.contrato_id = :id_contrato " +
            "         and exists (select 1 from execucaocontratoempenho ece " +
            "               where ece.execucaocontrato_id = ec.id" +
            "                 and ece.empenho_id is not null) ";
        Query q = em.createNativeQuery(sql, ExecucaoContrato.class);
        q.setParameter("id_contrato", contrato.getId());
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public List<ExecucaoContratoItem> buscarItensExecucao(Long idExecucao) {
        try {
            String sql = " select item.* from execucaocontratoitem item where item.execucaocontrato_id = :idExecucao ";
            Query q = em.createNativeQuery(sql, ExecucaoContratoItem.class);
            q.setParameter("idExecucao", idExecucao);
            return q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<ExecucaoContratoItem> buscarItensExecucaoContratoEmpenhado(ItemContrato itemContrato) {
        try {
            String sql = " select item.* from execucaocontratoitem item " +
                "           inner join execucaocontrato ex on ex.id = item.execucaocontrato_id  " +
                "           inner join execucaocontratoempenho exemp on ex.id = exemp.execucaocontrato_id" +
                "           inner join empenho emp on emp.id = exemp.empenho_id " +
                "          where item.itemcontrato_id = :idItemContrato ";
            Query q = em.createNativeQuery(sql, ExecucaoContratoItem.class);
            q.setParameter("idItemContrato", itemContrato.getId());
            return q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<ExecucaoContrato> buscarExecucaoContratoPorRequisicaoCompra(Long idRequisicao) {
        String sql = " select ex.* from execucaocontrato ex " +
            "          inner join requisicaocompraexecucao reqex on ex.id = reqex.execucaocontrato_id " +
            "          where reqex.requisicaocompra_id = :idRequisicao " +
            "          order by ex.numero ";
        Query q = em.createNativeQuery(sql, ExecucaoContrato.class);
        q.setParameter("idRequisicao", idRequisicao);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public Object[] recuperarIdENumeroExecucaoContrato(SolicitacaoEmpenho solicitacaoEmpenho) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select distinct ec.id, ec.numero ")
            .append(" from ExecucaoContrato ec ")
            .append("  inner join ExecucaoContratoEmpenho mv on mv.EXECUCAOCONTRATO_ID = ec.id ")
            .append(" where mv.solicitacaoEmpenho_id = :solicitacaoEmpenhoId ");
        Query q = em.createNativeQuery(sb.toString());
        try {
            q.setParameter("solicitacaoEmpenhoId", solicitacaoEmpenho.getId());
            return (Object[]) q.getSingleResult();
        } catch (NoResultException | NullPointerException e) {
            return null;
        }
    }

    public List<ExecucaoContrato> buscarExecucoesPorOrigem(Long idOrigem) {
        String sql = "select e.* from execucaocontrato e " +
            " where e.idorigem = :idOrigem ";
        Query q = em.createNativeQuery(sql, ExecucaoContrato.class);
        q.setParameter("idOrigem", idOrigem);
        try {
            return q.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public BigDecimal getValorEstornado(ExecucaoContrato execucaoContrato) {
        String sql = " select coalesce(sum(est.valor),0) from execucaocontratoempenhoest est " +
            "           inner join execucaocontratoestorno exest on exest.id = est.execucaocontratoestorno_id " +
            "           where exest.execucaocontrato_id = :idExecucao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", execucaoContrato.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }


    public BigDecimal getQuantidadeExecucaoOriginal(ItemContrato itemContrato) {
        String sql = " select coalesce(sum(item.quantidadeutilizada),0) from execucaocontratoitem item " +
            "           inner join execucaocontrato ex on ex.id = item.execucaocontrato_id  " +
            "           where item.itemcontrato_id = :idItemCont " +
            "           and ex.origem = :origemContrato ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemCont", itemContrato.getId());
        q.setParameter("origemContrato", OrigemSaldoItemContrato.CONTRATO.name());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getQuantidadeEstornadaExecucaoOriginal(ItemContrato itemContrato) {
        String sql = " select coalesce(sum(item.quantidade),0) from execucaocontratoempestitem item " +
            "           inner join execucaocontratoitem exitem on exitem.id = item.execucaocontratoitem_id  " +
            "           inner join execucaocontrato ex on ex.id = exitem.execucaocontrato_id  " +
            "           where exitem.itemcontrato_id = :idItemCont " +
            "           and ex.origem = :origemContrato ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemCont", itemContrato.getId());
        q.setParameter("origemContrato", OrigemSaldoItemContrato.CONTRATO.name());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorExecutadoOriginal(ItemContrato itemContrato) {
        String sql = " select coalesce(sum(item.valortotal),0) from execucaocontratoitem item " +
            "           inner join execucaocontrato ex on ex.id = item.execucaocontrato_id  " +
            "           where item.itemcontrato_id = :idItemCont " +
            "           and ex.origem = :origemContrato ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemCont", itemContrato.getId());
        q.setParameter("origemContrato", OrigemSaldoItemContrato.CONTRATO.name());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorEstornadoExecucaoOriginal(ItemContrato itemContrato) {
        String sql = " select coalesce(sum(item.valortotal),0) from execucaocontratoempestitem item " +
            "           inner join execucaocontratoitem exitem on exitem.id = item.execucaocontratoitem_id  " +
            "           inner join execucaocontrato ex on ex.id = exitem.execucaocontrato_id  " +
            "           where exitem.itemcontrato_id = :idItemCont " +
            "           and ex.origem = :origemContrato ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemCont", itemContrato.getId());
        q.setParameter("origemContrato", OrigemSaldoItemContrato.CONTRATO.name());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public Boolean hasOrigemExecucaoComRequisicao(Long idOrigem) {
        String sql = "select e.* from execucaocontrato e " +
            "inner join requisicaocompraexecucao rce on rce.execucaocontrato_id = e.id " +
            " where e.idorigem = :idOrigem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idOrigem", idOrigem);
        return !Util.isListNullOrEmpty(q.getResultList());
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public EmpenhoEstornoFacade getEmpenhoEstornoFacade() {
        return empenhoEstornoFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public DotacaoSolicitacaoMaterialFacade getDotacaoSolicitacaoMaterialFacade() {
        return dotacaoSolicitacaoMaterialFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ReprocessamentoSaldoContratoFacade getReprocessamentoSaldoContratoFacade() {
        return reprocessamentoSaldoContratoFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
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

    public SolicitacaoMaterialExternoFacade getSolicitacaoMaterialExternoFacade() {
        return solicitacaoMaterialExternoFacade;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }

    public AlteracaoContratualFacade getAlteracaoContratualFacade() {
        return alteracaoContratualFacade;
    }

    public ExecucaoContratoEstornoFacade getExecucaoContratoEstornoFacade() {
        return execucaoContratoEstornoFacade;
    }

    public SaldoItemContratoFacade getSaldoItemContratoFacade() {
        return saldoItemContratoFacade;
    }

    public AjusteContratoFacade getAjusteContratoFacade() {
        return ajusteContratoFacade;
    }

    public DotacaoProcessoCompraFacade getDotacaoProcessoCompraFacade() {
        return dotacaoProcessoCompraFacade;
    }
}
