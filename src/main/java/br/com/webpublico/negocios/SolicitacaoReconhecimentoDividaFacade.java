package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AgrupadorSolicitacaoEmpenho;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoVo;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Stateless
public class SolicitacaoReconhecimentoDividaFacade extends AbstractFacade<SolicitacaoReconhecimentoDivida> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ReconhecimentoDividaFacade reconhecimentoDividaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private ConfigGrupoMaterialFacade configGrupoMaterialFacade;
    @EJB
    private ConfigDespesaBensFacade configGrupoBemFacade;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade associacaoGrupoBemFacade;
    @EJB
    private AssociacaoGrupoObjetoCompraGrupoMaterialFacade associcaoGrupoMaterialFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;

    public SolicitacaoReconhecimentoDividaFacade() {
        super(SolicitacaoReconhecimentoDivida.class);
    }

    @Override
    public SolicitacaoReconhecimentoDivida recuperar(Object id) {
        SolicitacaoReconhecimentoDivida solicitacao = em.find(SolicitacaoReconhecimentoDivida.class, id);
        Hibernate.initialize(solicitacao.getReconhecimentoDivida().getItens());
        Hibernate.initialize(solicitacao.getReconhecimentoDivida().getDocumentosHabilitacao());
        Hibernate.initialize(solicitacao.getReconhecimentoDivida().getPublicacoes());
        Hibernate.initialize(solicitacao.getReconhecimentoDivida().getPareceres());
        Hibernate.initialize(solicitacao.getReconhecimentoDivida().getHistoricos());
        for (ItemReconhecimentoDivida itemReconhecimentoDivida : solicitacao.getReconhecimentoDivida().getItens()) {
            Hibernate.initialize(itemReconhecimentoDivida.getDotacoes());
        }
        for (ParecerReconhecimentoDivida parecerReconhecimentoDivida : solicitacao.getReconhecimentoDivida().getPareceres()) {
            if (parecerReconhecimentoDivida.getDetentorArquivoComposicao() != null) {
                Hibernate.initialize(parecerReconhecimentoDivida.getDetentorArquivoComposicao().getArquivosComposicao());
            }
        }
        return solicitacao;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return recuperar(id);
    }

    @Override
    public void salvarNovo(SolicitacaoReconhecimentoDivida entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigoContabil.getNumeroSolicitacaoReconhecimentoDivida(entity.getReconhecimentoDivida().getExercicio()));
        }
        super.salvarNovo(entity);
    }

    public void salvarGerandoSolicitacao(SolicitacaoReconhecimentoDivida entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigoContabil.getNumeroSolicitacaoReconhecimentoDivida(entity.getReconhecimentoDivida().getExercicio()));
        }
        entity.setSituacao(SituacaoSolicitacaoReconhecimentoDivida.CONCLUIDO);
        entity = salvarRetornando(entity);
        List<AgrupadorSolicitacaoEmpenho> agrupadores = criarAgrupadores(entity);
        gerarSolicitacaoEmpenho(entity, agrupadores);
        gerarSaldoReservado(entity);
    }

    private void gerarSaldoReservado(SolicitacaoReconhecimentoDivida solicitacao) {
        ReconhecimentoDivida reconhecimento = solicitacao.getReconhecimentoDivida();
        for (ItemReconhecimentoDivida itemReconhecimentoDivida : reconhecimento.getItens()) {
            for (ItemReconhecimentoDividaDotacao dotacao : itemReconhecimentoDivida.getDotacoes()) {
                SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                    dotacao.getFonteDespesaORC(),
                    OperacaoORC.RESERVADO,
                    TipoOperacaoORC.NORMAL,
                    dotacao.getValorReservado(),
                    solicitacao.getDataCadastro(),
                    dotacao.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                    reconhecimento.getId().toString(),
                    reconhecimento.getClass().getSimpleName(),
                    reconhecimento.getNumero(),
                    getHistoricoReconhecimentoDivida(dotacao.getFonteDespesaORC(), dotacao.getItemReconhecimentoDivida().getReconhecimentoDivida(), solicitacao.getClasseCredor())
                );
                saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
            }
        }
    }

    private String getHistoricoReconhecimentoDivida(FonteDespesaORC fonteDespesaORC, ReconhecimentoDivida reconhecimento, ClasseCredor classeCredor) {
        return "Reconhecimento da Dívida do Exercício: " + reconhecimento.getNumero() + " | " +
            DataUtil.getDataFormatada(reconhecimento.getDataReconhecimento()) + " | " +
            reconhecimento.getFornecedor() + " | " + classeCredor + " | " +
            " Unidade Administrativa: " + reconhecimento.getUnidadeAdministrativa() + " | " +
            " Elemento de Despesa: " + fonteDespesaORC.getDespesaORC() + "/" + fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " | " +
            " Fonte de Recurso: " + fonteDespesaORC.getDescricaoFonteDeRecurso().trim() + ",";
    }

    private void gerarSolicitacaoEmpenho(SolicitacaoReconhecimentoDivida solicitacao, List<AgrupadorSolicitacaoEmpenho> agrupadoresSolicitacaoEmpenho) {
        for (AgrupadorSolicitacaoEmpenho agrupador : agrupadoresSolicitacaoEmpenho) {
            SolicitacaoEmpenhoVo solicitacaoEmpenhoVo = criarVoSolicitacaoEmpenho(solicitacao, agrupador);
            solicitacaoEmpenhoFacade.gerarSolicitacaoEmpenhoSalvando(solicitacaoEmpenhoVo);
        }
    }

    private List<AgrupadorSolicitacaoEmpenho> criarAgrupadores(SolicitacaoReconhecimentoDivida entity) {
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();
        agrupadores.addAll(criarAgrupadorSolicitacaoEmpenhoGrupoMaterial(entity));
        agrupadores.addAll(criarAgrupadorSolicitacaoEmpenhoGrupoBem(entity));
        agrupadores.addAll(criarAgrupadorSolicitacaoEmpenhoSemGrupo(entity));
        return agrupadores;
    }

    private List<AgrupadorSolicitacaoEmpenho> criarAgrupadorSolicitacaoEmpenhoSemGrupo(SolicitacaoReconhecimentoDivida solicitacao) {
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();
        for (ItemReconhecimentoDivida itemReconhecimentoDivida : solicitacao.getReconhecimentoDivida().getItens()) {
            if (!itemReconhecimentoDivida.isObjetoCompraConsumoOrPermanenteMovel()) {
                for (ItemReconhecimentoDividaDotacao dotacao : itemReconhecimentoDivida.getDotacoes()) {
                    AgrupadorSolicitacaoEmpenho agrupador = novoAgrupadorSolicitacaoEmpenho(solicitacao, dotacao, Lists.<Conta>newArrayList());
                    adicionarItemDotacaoNoAgrupador(agrupadores, dotacao, Lists.<Conta>newArrayList(), agrupador);
                }
            }
        }
        return agrupadores;
    }

    private AgrupadorSolicitacaoEmpenho novoAgrupadorSolicitacaoEmpenho(SolicitacaoReconhecimentoDivida solicitacao, ItemReconhecimentoDividaDotacao itemReconhecimentoDividaDotacao, List<Conta> contas) {
        AgrupadorSolicitacaoEmpenho agrupador = new AgrupadorSolicitacaoEmpenho(itemReconhecimentoDividaDotacao.getFonteDespesaORC(), contas);
        agrupador.setGerarReserva(true);
        agrupador.setClasseCredor(solicitacao.getClasseCredor());
        agrupador.setIdOrigemSaldoOrcamentario(itemReconhecimentoDividaDotacao.getId());
        agrupador.setClasseOrigemSaldoOrcamentario(itemReconhecimentoDividaDotacao.getClass().getSimpleName());
        agrupador.setTipoObjetoCompra(itemReconhecimentoDividaDotacao.getItemReconhecimentoDivida().getObjetoCompra().getTipoObjetoCompra());
        return agrupador;
    }

    private List<AgrupadorSolicitacaoEmpenho> criarAgrupadorSolicitacaoEmpenhoGrupoMaterial(SolicitacaoReconhecimentoDivida solicitacao) {
        Map<GrupoMaterial, List<ItemReconhecimentoDividaDotacao>> mapItens = montarMapGrupoMaterialItemDotacao(solicitacao);
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();

        for (Map.Entry<GrupoMaterial, List<ItemReconhecimentoDividaDotacao>> entry : mapItens.entrySet()) {
            for (ItemReconhecimentoDividaDotacao itemDotacao : entry.getValue()) {
                Conta contaDespesa = itemDotacao.getFonteDespesaORC().getDespesaORC().getContaDeDespesa();
                List<Conta> contas = configGrupoMaterialFacade.buscarContasDespesaPorGrupoMaterial(entry.getKey(), solicitacao.getDataCadastro(), contaDespesa);
                if (contas == null || contas.isEmpty()) {
                    throw new ExcecaoNegocioGenerica("Nenhuma conta de despesa configurada para o grupo material: " + entry.getKey() + " desdobrada da conta " + contaDespesa + ".");
                }
                AgrupadorSolicitacaoEmpenho agrupador = novoAgrupadorSolicitacaoEmpenho(solicitacao, itemDotacao, contas);
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

    private List<AgrupadorSolicitacaoEmpenho> criarAgrupadorSolicitacaoEmpenhoGrupoBem(SolicitacaoReconhecimentoDivida solicitacao) {
        Map<GrupoBem, List<ItemReconhecimentoDividaDotacao>> mapItens = montarMapGrupoBemItemDotacao(solicitacao);
        List<AgrupadorSolicitacaoEmpenho> agrupadores = Lists.newArrayList();

        for (Map.Entry<GrupoBem, List<ItemReconhecimentoDividaDotacao>> entry : mapItens.entrySet()) {
            for (ItemReconhecimentoDividaDotacao itemDotacao : entry.getValue()) {
                Conta contaDespesa = itemDotacao.getFonteDespesaORC().getDespesaORC().getContaDeDespesa();
                List<Conta> contas = configGrupoBemFacade.buscarContasPorGrupoBem(entry.getKey(), solicitacao.getDataCadastro(), contaDespesa);
                if (contas == null || contas.isEmpty()) {
                    throw new ExcecaoNegocioGenerica("Nenhuma conta de despesa configurada para o grupo patrimonial: " + entry.getKey() + " desdobrada da conta " + contaDespesa + ".");
                }
                AgrupadorSolicitacaoEmpenho agrupador = novoAgrupadorSolicitacaoEmpenho(solicitacao, itemDotacao, contas);
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

    private void ordernarItensAgrupadorSolicitacaoEmpenho(List<AgrupadorSolicitacaoEmpenho> agrupadores) {
        for (AgrupadorSolicitacaoEmpenho agrupador : agrupadores) {
            Collections.sort(agrupador.getItensDotacaoExecucao());
        }
    }

    private Map<GrupoBem, List<ItemReconhecimentoDividaDotacao>> montarMapGrupoBemItemDotacao(SolicitacaoReconhecimentoDivida solicitacao) {
        Map<GrupoBem, List<ItemReconhecimentoDividaDotacao>> mapItens = Maps.newHashMap();
        for (ItemReconhecimentoDivida itemReconhecimentoDivida : solicitacao.getReconhecimentoDivida().getItens()) {
            if (itemReconhecimentoDivida.getObjetoCompra().getTipoObjetoCompra().isMaterialPermanente()) {
                for (ItemReconhecimentoDividaDotacao dotacao : itemReconhecimentoDivida.getDotacoes()) {
                    if ((itemReconhecimentoDivida.getQuantidade().compareTo(BigDecimal.ZERO) > 0
                        || itemReconhecimentoDivida.getValorTotal().compareTo(BigDecimal.ZERO) > 0)) {

                        GrupoObjetoCompra grupoObjetoCompra = itemReconhecimentoDivida.getObjetoCompra().getGrupoObjetoCompra();
                        GrupoBem grupoBem = getGrupoBem(solicitacao, grupoObjetoCompra);

                        if (mapItens.containsKey(grupoBem)) {
                            mapItens.get(grupoBem).add(dotacao);
                        } else {
                            mapItens.put(grupoBem, Lists.newArrayList(dotacao));
                        }
                    }
                }
            }
        }
        return mapItens;
    }

    private void adicionarItemDotacaoNoAgrupador(List<AgrupadorSolicitacaoEmpenho> agrupadores, ItemReconhecimentoDividaDotacao itemDotacao, List<Conta> contas, AgrupadorSolicitacaoEmpenho agrupador) {
        Conta contaDespesa = null;
        if (!contas.isEmpty()) {
            contaDespesa = contas.get(0);
        }
        agrupador.setContaDesdobrada(contaDespesa);
        if (!agrupadores.contains(agrupador)) {
            Util.adicionarObjetoEmLista(agrupador.getItensDotacaoReconhecimento(), itemDotacao);
            agrupador.setValor(itemDotacao.getValorReservado());
            Util.adicionarObjetoEmLista(agrupadores, agrupador);
        } else {
            for (AgrupadorSolicitacaoEmpenho agrupadorExistente : agrupadores) {
                if (agrupador.equals(agrupadorExistente)) {
                    agrupador = agrupadorExistente;
                }
            }
            Util.adicionarObjetoEmLista(agrupador.getItensDotacaoReconhecimento(), itemDotacao);
            agrupador.setValor(agrupador.getValor().add(itemDotacao.getValorReservado()));
            Util.adicionarObjetoEmLista(agrupadores, agrupador);
        }
    }

    private Map<GrupoMaterial, List<ItemReconhecimentoDividaDotacao>> montarMapGrupoMaterialItemDotacao(SolicitacaoReconhecimentoDivida solicitacao) {
        Map<GrupoMaterial, List<ItemReconhecimentoDividaDotacao>> mapItens = Maps.newHashMap();
        for (ItemReconhecimentoDivida itemReconhecimentoDivida : solicitacao.getReconhecimentoDivida().getItens()) {
            if (itemReconhecimentoDivida.getObjetoCompra().getTipoObjetoCompra().isMaterialConsumo()) {
                for (ItemReconhecimentoDividaDotacao itemDotacao : itemReconhecimentoDivida.getDotacoes()) {
                    if ((itemReconhecimentoDivida.getQuantidade().compareTo(BigDecimal.ZERO) > 0
                        || itemReconhecimentoDivida.getValorTotal().compareTo(BigDecimal.ZERO) > 0)) {
                        GrupoObjetoCompra grupoObjetoCompra = itemReconhecimentoDivida.getObjetoCompra().getGrupoObjetoCompra();
                        GrupoMaterial grupoMaterial = getGrupoMaterial(solicitacao, grupoObjetoCompra);
                        if (mapItens.containsKey(grupoMaterial)) {
                            mapItens.get(grupoMaterial).add(itemDotacao);
                        } else {
                            mapItens.put(grupoMaterial, Lists.newArrayList(itemDotacao));
                        }
                    }
                }
            }
        }
        return mapItens;
    }

    private GrupoMaterial getGrupoMaterial(SolicitacaoReconhecimentoDivida solicitacao, GrupoObjetoCompra grupoObjetoCompra) {
        try {
            return associcaoGrupoMaterialFacade.buscarAssociacaoPorGrupoObjetoCompraVigente(grupoObjetoCompra, solicitacao.getDataCadastro()).getGrupoMaterial();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Nenhuma associação encontrada com grupo de objeto de compra: " + grupoObjetoCompra + ".");
        }
    }

    private GrupoBem getGrupoBem(SolicitacaoReconhecimentoDivida solicitacao, GrupoObjetoCompra grupoObjetoCompra) {
        try {
            return associacaoGrupoBemFacade.recuperarAssociacaoDoGrupoObjetoCompra(grupoObjetoCompra, solicitacao.getDataCadastro()).getGrupoBem();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Nenhuma associação encontrada com grupo de objeto de compra: " + grupoObjetoCompra + ".");
        }
    }

    private SolicitacaoEmpenhoVo criarVoSolicitacaoEmpenho(SolicitacaoReconhecimentoDivida entity, AgrupadorSolicitacaoEmpenho agrupador) {
        UnidadeOrganizacional unidadeOrganizacional = agrupador.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional();
        String historico = "Solicitação gerada através do Reconhecimento da Dívida do Exercício: " + entity.getReconhecimentoDivida().getNumero() + " em: " + DataUtil.getDataFormatada(entity.getReconhecimentoDivida().getDataReconhecimento())
            + ". Solicitação de Empenho/Reconhecimento da Dívida do Exercício nº " + entity.getNumero() + ". Dotação: " + agrupador.getFonteDespesaORC() + " - " + Util.formataValor(agrupador.getValor());

        SolicitacaoEmpenhoVo solicitacaoEmpenhoVo = new SolicitacaoEmpenhoVo();
        solicitacaoEmpenhoVo.setValor(agrupador.getValor());
        solicitacaoEmpenhoVo.setUnidadeOrganizacional(unidadeOrganizacional);
        solicitacaoEmpenhoVo.setTipoEmpenho(TipoEmpenho.ORDINARIO);
        solicitacaoEmpenhoVo.setHistoricoContabil(null);
        solicitacaoEmpenhoVo.setFonteDespesaOrc(agrupador.getFonteDespesaORC());
        solicitacaoEmpenhoVo.setDespesaOrc(agrupador.getFonteDespesaORC().getDespesaORC());
        solicitacaoEmpenhoVo.setDataSolicitacao(entity.getDataCadastro());
        solicitacaoEmpenhoVo.setContaDespesaDesdobrada(agrupador.getContaDesdobrada());
        solicitacaoEmpenhoVo.setComplementoHistorico(historico);
        solicitacaoEmpenhoVo.setFornecedor(entity.getReconhecimentoDivida().getFornecedor());
        solicitacaoEmpenhoVo.setGerarReserva(true);
        solicitacaoEmpenhoVo.setClasseCredor(entity.getClasseCredor());
        solicitacaoEmpenhoVo.setReconhecimentoDivida(entity.getReconhecimentoDivida());
        solicitacaoEmpenhoVo.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        solicitacaoEmpenhoVo.setOrigemSocilicitacao(OrigemSolicitacaoEmpenho.RECONHECIMENTO_DIVIDA_EXERCICIO);
        return solicitacaoEmpenhoVo;
    }

    public List<ReconhecimentoDivida> buscarReconhecimentosDaDivida(String filtros) {
        return reconhecimentoDividaFacade.buscarReconhecimentosDaDivida(filtros, sistemaFacade.getExercicioCorrente());
    }

    public ReconhecimentoDividaFacade getReconhecimentoDividaFacade() {
        return reconhecimentoDividaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
