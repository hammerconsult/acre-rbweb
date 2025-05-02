package br.com.webpublico.negocios.administrativo;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoItemVo;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoVo;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class ExecucaoProcessoEstornoFacade extends AbstractFacade<ExecucaoProcessoEstorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private ExecucaoProcessoFacade execucaoProcessoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SolicitacaoEmpenhoEstornoFacade solicitacaoEmpenhoEstornoFacade;
    @EJB
    private ConfigGrupoMaterialFacade configGrupoMaterialFacade;
    @EJB
    private ConfigDespesaBensFacade configGrupoBemFacade;
    @EJB
    private AssociacaoGrupoObjetoCompraGrupoMaterialFacade associcaoGrupoMaterialFacade;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade associacaoGrupoBemFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;

    public ExecucaoProcessoEstornoFacade() {
        super(ExecucaoProcessoEstorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ExecucaoProcessoEstorno recuperar(Object id) {
        ExecucaoProcessoEstorno entity = super.recuperar(id);
        Hibernate.initialize(entity.getEstornosEmpenho());
        for (ExecucaoProcessoEmpenhoEstorno estorno : entity.getEstornosEmpenho()) {
            Hibernate.initialize(estorno.getItens());
        }
        return entity;
    }

    @Override
    public void remover(ExecucaoProcessoEstorno entity) {
        estornarSolicitacaoEmpenho(entity, false);
        super.remover(entity);
    }

    @Override
    public ExecucaoProcessoEstorno salvarRetornando(ExecucaoProcessoEstorno entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(ExecucaoProcessoEstorno.class, "numero"));
        }
        entity = em.merge(entity);
        estornarSolicitacaoEmpenho(entity, true);
        return entity;
    }

    private void estornarSolicitacaoEmpenho(ExecucaoProcessoEstorno entity, boolean isEstornar) {
        for (ExecucaoProcessoEmpenhoEstorno exEstorno : entity.getEstornosEmpenho()) {
            if (exEstorno.getSolicitacaoEmpenhoEstorno().isSolicitacaoEstornoPorSolicitacaoEmpenho()) {
                SolicitacaoEmpenho solEmp = exEstorno.getSolicitacaoEmpenhoEstorno().getSolicitacaoEmpenho();
                solEmp.setEstornada(isEstornar);
                em.merge(solEmp);
                if (isEstornar) {
                    gerarSaldoReservadoPorLicitacao(exEstorno, entity, TipoOperacaoORC.ESTORNO);
                } else {
                    gerarSaldoReservadoPorLicitacao(exEstorno, entity, TipoOperacaoORC.NORMAL);
                }
            }
        }
    }

    private void gerarSaldoReservadoPorLicitacao(ExecucaoProcessoEmpenhoEstorno exEst, ExecucaoProcessoEstorno entity, TipoOperacaoORC operacao) {
        SolicitacaoEmpenhoEstorno solEst = exEst.getSolicitacaoEmpenhoEstorno();
        if (solEst.getSolicitacaoEmpenho().getGerarReserva()) {
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                solEst.getFonteDespesaORC(),
                OperacaoORC.RESERVADO_POR_LICITACAO,
                operacao,
                solEst.getValor(),
                entity.getDataLancamento(),
                solEst.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                exEst.getId().toString(),
                exEst.getClass().getSimpleName(),
                entity.getExecucaoProcesso().getNumeroAnoProcesso(),
                gerarHistoricoSaldoFonteDespesaOrc(exEst, entity)
            );
            saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
        }
    }

    public String gerarHistoricoSaldoFonteDespesaOrc(ExecucaoProcessoEmpenhoEstorno exEst, ExecucaoProcessoEstorno entity) {
        ExecucaoProcesso execucaoProcesso = entity.getExecucaoProcesso();
        FonteDespesaORC fonteDespesaORC = exEst.getSolicitacaoEmpenhoEstorno().getFonteDespesaORC();
        return "Estorno nª " + entity.getNumero() + " | " +
            " Execução nº " + execucaoProcesso.getNumero() + " | " +
            execucaoProcesso.getTipoExecucao().getDescricao() + " " + execucaoProcesso.getNumeroAnoProcesso() + " | " +
            DataUtil.getDataFormatada(execucaoProcesso.getDataLancamento()) + " | " + execucaoProcesso.getFornecedor() + " | " + exEst.getSolicitacaoEmpenhoEstorno().getSolicitacaoEmpenho().getClasseCredor() + " |" +
            " Unidade Administrativa: " + execucaoProcesso.getUnidadeAdministrativa() + " | " +
            " Elemento de Despesa: " + fonteDespesaORC.getDespesaORC() + "/" + fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " | " +
            " Fonte de Recurso: " + fonteDespesaORC.getDescricaoFonteDeRecurso().trim() + ",";
    }

    public BigDecimal getQuantidadeEstornada(ExecucaoProcessoItem item) {
        String sql = " select coalesce(sum(item.quantidade),0) from execucaoprocessoempestitem item where item.execucaoprocessoitem_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", item.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorEstornado(ExecucaoProcessoItem item) {
        String sql = " select coalesce(sum(item.valortotal),0) from execucaoprocessoempestitem item where item.execucaoprocessoitem_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", item.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorEstornado(FonteDespesaORC fonte, ExecucaoProcessoItem item) {
        String sql = " select coalesce(sum(itemest.valortotal), 0) as valor from execucaoprocessoempestitem itemest " +
            "         inner join execucaoprocessoempenhoest exsol on exsol.id = itemest.execucaoprocessoempenhoest_id " +
            "         inner join solicitacaoempenhoestorno solest on solest.id = exsol.solicitacaoempenhoestorno_id " +
            "         inner join solicitacaoempenho solemp on solemp.id = solest.solicitacaoempenho_id " +
            "      where solemp.fontedespesaorc_id = :idFonte " +
            "       and itemest.execucaoprocessoitem_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idFonte", fonte.getId());
        q.setParameter("idItem", item.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorSolicitacaoEstornoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho, ExecucaoProcesso execucaoProcesso) {
        String sql = " select coalesce(sum(sol.valor),0) as total from execucaoprocessoestorno exEst  " +
            "       inner join execucaoprocessoempenhoest est on est.execucaoprocessoestorno_id = exEst.id  " +
            "       inner join solicitacaoempenhoestorno sol on sol.id = est.solicitacaoempenhoestorno_id  " +
            "      where sol.empenho_id is null  " +
            "       and exEst.execucaoprocesso_id = :idExecucao " +
            "       and sol.solicitacaoempenho_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", execucaoProcesso.getId());
        q.setParameter("idSolicitacao", solicitacaoEmpenho.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorSolicitacaoEstornoEmpenhoAguardandoEstornoEmpenho(Empenho empenho, ExecucaoProcesso execucaoProcesso) {
        String sql = " select coalesce(sum(sol.valor),0) - coalesce(sum(sol.valor),0) as total from execucaoprocessoestorno exEst  " +
            "       inner join execucaoprocessoempenhoest est on est.execucaoprocessoestorno_id = exEst.id  " +
            "       inner join solicitacaoempenhoestorno sol on sol.id = est.solicitacaoempenhoestorno_id  " +
            "      where sol.empenhoestorno_id is null  " +
            "       and exEst.execucaoprocesso_id = :idExecucao " +
            "       and sol.empenho_id = :idEmpenho ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", execucaoProcesso.getId());
        q.setParameter("idEmpenho", empenho.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public List<SolicitacaoEmpenhoEstorno> buscarSolicitacoesEmpenhoEstorno(ExecucaoProcesso execucaoProcesso) {
        String sql = " select sol.* from solicitacaoempenhoestorno sol " +
            "           inner join execucaoprocessoempenhoest exsol on exsol.solicitacaoempenhoestorno_id = sol.id " +
            "           inner join execucaoprocessoestorno est on est.id = exsol.execucaoprocessoestorno_id " +
            "          where est.execucaoprocesso_id = :idExecucao " +
            "           and sol.empenhoestorno_id is null" +
            "           and sol.empenho_id is not null ";
        Query q = em.createNativeQuery(sql, SolicitacaoEmpenhoEstorno.class);
        q.setParameter("idExecucao", execucaoProcesso.getId());
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public SolicitacaoEmpenhoEstornoVo criarSolicitacaoEmpenhoEstornoVo(ExecucaoProcessoEstorno selecionado, Empenho empenho, SolicitacaoEmpenho solicitacaoEmpenho) {
        BigDecimal valorSolEmpenhoExecutado = getValorEmpenhoExecutado(empenho, selecionado);
        SolicitacaoEmpenhoEstornoVo solEmpVo = new SolicitacaoEmpenhoEstornoVo(SolicitacaoEmpenhoEstornoVo.TipoEstornoExecucao.EMPENHO);
        solEmpVo.setEmpenho(empenho);
        solEmpVo.setSolicitacaoEmpenho(solicitacaoEmpenho);
        solEmpVo.setCategoria(empenho.getCategoriaOrcamentaria());
        solEmpVo.setSaldo(empenho.getSaldo().subtract(valorSolEmpenhoExecutado));
        if (solEmpVo.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            solEmpVo.setSaldo(BigDecimal.ZERO);
        }
        return solEmpVo;
    }

    public BigDecimal getValorEmpenhoExecutado(Empenho empenho, ExecucaoProcessoEstorno selecionado) {
        BigDecimal valorSolEmpSalvo = getValorSolicitacaoEstornoEmpenhoAguardandoEstornoEmpenho(empenho, selecionado.getExecucaoProcesso());
        BigDecimal valor = BigDecimal.ZERO;
        for (ExecucaoProcessoEmpenhoEstorno execucao : selecionado.getEstornosEmpenho()) {
            if (empenho.equals(execucao.getSolicitacaoEmpenhoEstorno().getEmpenho())) {
                valor = valor.add(execucao.getSolicitacaoEmpenhoEstorno().getValor());
            }
        }
        return valorSolEmpSalvo.add(valor);
    }

    public void criarItensEstorno(ExecucaoProcessoEstorno selecionado, SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo, List<ExecucaoProcessoItem> itensExecucao) {
        solicitacaoEmpenhoEstornoVo.setItens(Lists.newArrayList());
        Map<TipoObjetoCompra, List<ExecucaoProcessoItem>> map = criarMapTipoObjetoCompraItensExecucao(itensExecucao);
        TipoContaDespesa tipoContaDespesa = getTipoContaDespesa(solicitacaoEmpenhoEstornoVo);
        if (tipoContaDespesa != null) {

            switch (Objects.requireNonNull(tipoContaDespesa)) {
                case BEM_MOVEL:
                    Conta contaMovel;
                    if (solicitacaoEmpenhoEstornoVo.isTipoEstornoEmpenho()) {
                        List<Conta> contas = getEmpenhoFacade().buscarContaDesdobradaPorEmpenho("", solicitacaoEmpenhoEstornoVo.getEmpenho());
                        contaMovel = !Util.isListNullOrEmpty(contas) ? contas.get(0) : null;
                    } else {
                        contaMovel = solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getContaDespesaDesdobrada();
                    }
                    for (ExecucaoProcessoItem item : map.get(TipoObjetoCompra.PERMANENTE_MOVEL)) {
                        if (contaMovel != null) {
                            GrupoBem grupoBem = getConfigGrupoBemFacade().buscarGrupoBemPorContaDespesa(getSistemaFacade().getDataOperacao(), contaMovel);
                            GrupoObjetoCompraGrupoBem associacaoGrupoBem = getAssociacaoGrupoBemFacade().buscarAssociacaoPorGrupoObjetoCompraVigente(item.getObjetoCompra().getGrupoObjetoCompra(), new Date());
                            if (grupoBem.equals(associacaoGrupoBem.getGrupoBem())) {
                                criarExecucaoProcessoEmpenhoEstornoItemVO(item, solicitacaoEmpenhoEstornoVo, selecionado);
                            }
                        } else {
                            criarExecucaoProcessoEmpenhoEstornoItemVO(item, solicitacaoEmpenhoEstornoVo, selecionado);
                        }
                    }
                    break;
                case BEM_ESTOQUE:
                case VARIACAO_PATRIMONIAL_DIMINUTIVA:
                    Conta contaEstoque;
                    if (solicitacaoEmpenhoEstornoVo.isTipoEstornoEmpenho()) {
                        List<Conta> contas = getEmpenhoFacade().buscarContaDesdobradaPorEmpenho("", solicitacaoEmpenhoEstornoVo.getEmpenho());
                        contaEstoque = !Util.isListNullOrEmpty(contas) ? contas.get(0) : null;
                    } else {
                        contaEstoque = solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getContaDespesaDesdobrada();
                    }

                    for (ExecucaoProcessoItem item : map.get(TipoObjetoCompra.CONSUMO)) {
                        if (contaEstoque != null) {
                            GrupoMaterial grupoMaterial = getConfigGrupoMaterialFacade().buscarGrupoPorConta(contaEstoque);
                            AssociacaoGrupoObjetoCompraGrupoMaterial associocaoGrupoMat = getAssocicaoGrupoMaterialFacade().buscarAssociacaoPorGrupoObjetoCompraVigente(item.getObjetoCompra().getGrupoObjetoCompra(), new Date());
                            if (grupoMaterial.equals(associocaoGrupoMat.getGrupoMaterial())) {
                                criarExecucaoProcessoEmpenhoEstornoItemVO(item, solicitacaoEmpenhoEstornoVo, selecionado);
                            }
                        } else {
                            criarExecucaoProcessoEmpenhoEstornoItemVO(item, solicitacaoEmpenhoEstornoVo, selecionado);
                        }
                    }
                    break;
                case BEM_IMOVEL:
                    List<ExecucaoProcessoItem> itensImovel = map.get(TipoObjetoCompra.PERMANENTE_IMOVEL);
                    if (itensImovel != null) {
                        for (ExecucaoProcessoItem item : itensImovel) {
                            criarExecucaoProcessoEmpenhoEstornoItemVO(item, solicitacaoEmpenhoEstornoVo, selecionado);
                        }
                    }
                    break;
                default:
                    List<ExecucaoProcessoItem> itensServico = map.get(TipoObjetoCompra.SERVICO);
                    if (itensServico != null) {
                        for (ExecucaoProcessoItem item : itensServico) {
                            criarExecucaoProcessoEmpenhoEstornoItemVO(item, solicitacaoEmpenhoEstornoVo, selecionado);
                        }
                    }
                    break;
            }
        } else {
            for (ExecucaoProcessoItem item : itensExecucao) {
                criarExecucaoProcessoEmpenhoEstornoItemVO(item, solicitacaoEmpenhoEstornoVo, selecionado);
            }
        }
    }

    private Map<TipoObjetoCompra, List<ExecucaoProcessoItem>> criarMapTipoObjetoCompraItensExecucao(List<ExecucaoProcessoItem> itensExecucao) {
        Map<TipoObjetoCompra, List<ExecucaoProcessoItem>> map = new HashMap<>();
        for (ExecucaoProcessoItem item : itensExecucao) {
            TipoObjetoCompra tipoObjetoCompra = item.getObjetoCompra().getTipoObjetoCompra();
            if (!map.containsKey(tipoObjetoCompra)) {
                map.put(tipoObjetoCompra, Lists.newArrayList(item));
            } else {
                map.get(tipoObjetoCompra).add(item);
            }
        }
        return map;
    }

    private TipoContaDespesa getTipoContaDespesa(SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo) {
        TipoContaDespesa tipoContaDespesa = solicitacaoEmpenhoEstornoVo.isTipoEstornoEmpenho()
            ? solicitacaoEmpenhoEstornoVo.getEmpenho().getTipoContaDespesa()
            : solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getFonteDespesaORC().getTipoContaDespesa();

        if (tipoContaDespesa != null && tipoContaDespesa.isNaoAplicavel()) {
            List<TipoContaDespesa> tiposConta = getEmpenhoFacade().getContaFacade().buscarTiposContasDespesaNosFilhosDaConta((ContaDespesa) solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getFonteDespesaORC().getContaDeDespesa());
            for (TipoContaDespesa tipo : tiposConta) {
                if (tipo.isEstoque() || tipo.isBemMovel() || tipo.isBemImovel() || tipo.isServicoTerceiro()) {
                    tipoContaDespesa = tipo;
                    break;
                }
            }
        }
        return tipoContaDespesa;
    }

    private void criarExecucaoProcessoEmpenhoEstornoItemVO(ExecucaoProcessoItem itemEx, SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo, ExecucaoProcessoEstorno selecionado) {
        SolicitacaoEmpenhoEstornoItemVo novoItemVo = new SolicitacaoEmpenhoEstornoItemVo();
        novoItemVo.setExecucaoProcessoItem(itemEx);
        novoItemVo.setValorEstornadoExecucao(getValorEstornado(itemEx).add(getValorAdicionadoEstorno(itemEx, solicitacaoEmpenhoEstornoVo, selecionado)));
        novoItemVo.setValorEmRequisicao(getExecucaoProcessoFacade().getValorEmRequisicao(itemEx));
        novoItemVo.setValorEstornoRequisicao(getExecucaoProcessoFacade().getValorEmRequisicaoEstorno(itemEx));
        novoItemVo.setValorEmExecucao(itemEx.getValorTotal());
        novoItemVo.setQuantidade(itemEx.isExecucaoPorQuantidade() ? BigDecimal.ZERO : BigDecimal.ONE);
        novoItemVo.setValorUnitario(itemEx.isExecucaoPorQuantidade() ? itemEx.getValorUnitario() : novoItemVo.getSaldoDisponivel());
        novoItemVo.setValorTotal(novoItemVo.getSaldoDisponivel());
        if (itemEx.isExecucaoPorQuantidade()) {
            novoItemVo.calcularQuantidade();
        }

        if (solicitacaoEmpenhoEstornoVo.isTipoEstornoSolicitacaoEmpenho()) {
            ExecucaoProcessoFonteItem itemFonte = getExecucaoProcessoFacade().buscarItemFonte(selecionado.getExecucaoProcesso(), solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getFonteDespesaORC(), itemEx);
            if (itemFonte != null
                && itemFonte.getExecucaoProcessoFonte().getFonteDespesaORC().equals(solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getFonteDespesaORC())) {

                BigDecimal valorEstornado = getValorEstornado(solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getFonteDespesaORC(), itemEx);
                novoItemVo.setValorEstornadoExecucao(valorEstornado.add(getValorAdicionadoEstorno(itemEx, solicitacaoEmpenhoEstornoVo, selecionado)));

                novoItemVo.setValorEmExecucao(itemFonte.getValorTotal());
                novoItemVo.setValorTotal(novoItemVo.getSaldoDisponivel());
                novoItemVo.calcularQuantidade();
                Util.adicionarObjetoEmLista(solicitacaoEmpenhoEstornoVo.getItens(), novoItemVo);
            }
        } else {
            Util.adicionarObjetoEmLista(solicitacaoEmpenhoEstornoVo.getItens(), novoItemVo);
        }
    }

    private BigDecimal getValorAdicionadoEstorno(ExecucaoProcessoItem itemExecucao, SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo, ExecucaoProcessoEstorno selecionado) {
        BigDecimal total = BigDecimal.ZERO;
        if (hasEstornosLancados(selecionado)) {
            for (ExecucaoProcessoEmpenhoEstorno exEmpEst : selecionado.getEstornosEmpenho()) {
                for (ExecucaoProcessoEmpenhoEstornoItem itemEstorno : exEmpEst.getItens()) {
                    if (itemEstorno.getExecucaoProcessoItem().equals(itemExecucao)
                        && exEmpEst.getSolicitacaoEmpenhoEstorno().getSolicitacaoEmpenho().getFonteDespesaORC().equals(solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getFonteDespesaORC())) {
                        total = total.add(itemEstorno.getValorTotal());
                    }
                }
            }
        }
        return total;
    }

    public boolean hasEstornosLancados(ExecucaoProcessoEstorno selecionado) {
        if (selecionado != null) {
            return selecionado.getEstornosEmpenho() != null
                && !selecionado.getEstornosEmpenho().isEmpty();
        }
        return false;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public SolicitacaoEmpenhoFacade getSolicitacaoEmpenhoFacade() {
        return solicitacaoEmpenhoFacade;
    }

    public ExecucaoProcessoFacade getExecucaoProcessoFacade() {
        return execucaoProcessoFacade;
    }

    public SolicitacaoEmpenhoEstornoFacade getSolicitacaoEmpenhoEstornoFacade() {
        return solicitacaoEmpenhoEstornoFacade;
    }

    public ConfigGrupoMaterialFacade getConfigGrupoMaterialFacade() {
        return configGrupoMaterialFacade;
    }

    public AssociacaoGrupoObjetoCompraGrupoMaterialFacade getAssocicaoGrupoMaterialFacade() {
        return associcaoGrupoMaterialFacade;
    }

    public ConfigDespesaBensFacade getConfigGrupoBemFacade() {
        return configGrupoBemFacade;
    }

    public GrupoObjetoCompraGrupoBemFacade getAssociacaoGrupoBemFacade() {
        return associacaoGrupoBemFacade;
    }
}
