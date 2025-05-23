package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoItemVo;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoLoquidacaoVo;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoVo;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class ExecucaoContratoEstornoFacade extends AbstractFacade<ExecucaoContratoEstorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private SaldoItemContratoFacade saldoItemContratoFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private RequisicaoCompraEstornoFacade requisicaoCompraEstornoFacade;
    @EJB
    private ReprocessamentoSaldoContratoFacade reprocessamentoSaldoContratoFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private SolicitacaoEmpenhoEstornoFacade solicitacaoEmpenhoEstornoFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
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

    public ExecucaoContratoEstornoFacade() {
        super(ExecucaoContratoEstorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ExecucaoContratoEstorno recuperar(Object id) {
        ExecucaoContratoEstorno entity = super.recuperar(id);
        Hibernate.initialize(entity.getEstornosEmpenho());
        for (ExecucaoContratoEmpenhoEstorno estorno : entity.getEstornosEmpenho()) {
            Hibernate.initialize(estorno.getItens());
            Hibernate.initialize(estorno.getSolicitacoesLiquidacaoEstorno());
        }
        return entity;
    }

    @Override
    public void remover(ExecucaoContratoEstorno entity) {
        estornarSolicitacaoEmpenho(entity, false);
        super.remover(entity);
    }

    @Override
    public ExecucaoContratoEstorno salvarRetornando(ExecucaoContratoEstorno entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(ExecucaoContratoEstorno.class, "numero"));
        }
        entity = em.merge(entity);
        movimentarItensContrato(entity);
        estornarSolicitacaoEmpenho(entity, true);
        return entity;
    }

    private void movimentarItensContrato(ExecucaoContratoEstorno entity) {
        for (ExecucaoContratoEmpenhoEstorno exEstorno : entity.getEstornosEmpenho()) {
            for (ExecucaoContratoEmpenhoEstornoItem itemEstornoExecucao : exEstorno.getItens()) {
                saldoItemContratoFacade.gerarSaldoExecucaoEstorno(itemEstornoExecucao, sistemaFacade.getDataOperacao());
            }
        }
    }

    private void estornarSolicitacaoEmpenho(ExecucaoContratoEstorno entity, boolean isEstornar) {
        for (ExecucaoContratoEmpenhoEstorno exEstorno : entity.getEstornosEmpenho()) {
            if (exEstorno.getSolicitacaoEmpenhoEstorno().isSolicitacaoEstornoPorSolicitacaoEmpenho()) {
                SolicitacaoEmpenho solEmp = exEstorno.getSolicitacaoEmpenhoEstorno().getSolicitacaoEmpenho();
                solEmp.setEstornada(isEstornar);
                em.merge(solEmp);

                if (isEstornar) {
                    contratoFacade.aumentarSaldoAtualContrato(exEstorno.getValor(), entity.getExecucaoContrato().getContrato());
                    gerarSaldoReservadoPorLicitacao(exEstorno, entity, TipoOperacaoORC.ESTORNO);
                } else {
                    contratoFacade.diminuirSaldoAtualContrato(exEstorno.getValor(), entity.getExecucaoContrato().getContrato());
                    gerarSaldoReservadoPorLicitacao(exEstorno, entity, TipoOperacaoORC.NORMAL);
                }
            }
        }
    }

    private void gerarSaldoReservadoPorLicitacao(ExecucaoContratoEmpenhoEstorno exEst, ExecucaoContratoEstorno entity, TipoOperacaoORC operacao) {
        Contrato contrato = entity.getExecucaoContrato().getContrato();
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
                contrato.getNumeroAnoTermo(),
                getHistoricoSaldo(exEst, contrato, entity)
            );
            saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
        }
    }

    public String getHistoricoSaldo(ExecucaoContratoEmpenhoEstorno exEst, Contrato contrato, ExecucaoContratoEstorno entity) {
        SolicitacaoEmpenhoEstorno solEst = exEst.getSolicitacaoEmpenhoEstorno();
        FonteDespesaORC fonteDespesaORC = solEst.getFonteDespesaORC();
        return "Estorno nª " + entity.getNumero() + " | " +
            " Execução nº " + entity.getExecucaoContrato().getNumero() + " | " +
            "Contrato: " + contrato.getNumeroContratoAno() + " | " + contrato.getNumeroAnoTermo() + " | " + DataUtil.getDataFormatada(contrato.getDataLancamento()) + " | " +
            contrato.getContratado() + " | " + solEst.getSolicitacaoEmpenho().getClasseCredor() + " |" +
            " Unidade Administrativa: " + contrato.getUnidadeAdministrativa() + " | " +
            " Elemento de Despesa: " + fonteDespesaORC.getDespesaORC() + "/" + fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " | " +
            " Fonte de Recurso: " + fonteDespesaORC.getDescricaoFonteDeRecurso().trim() + ",";
    }

    public BigDecimal getQuantidadeEstornada(ExecucaoContratoItem item) {
        String sql = " select coalesce(sum(item.quantidade),0) from execucaocontratoempestitem item where item.execucaocontratoitem_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", item.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorEstornado(ExecucaoContratoItem item) {
        String sql = " select coalesce(sum(item.valortotal),0) from execucaocontratoempestitem item where item.execucaocontratoitem_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", item.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorEstornado(FonteDespesaORC fonte, ExecucaoContratoItem item) {
        String sql = " select coalesce(sum(itemest.valortotal), 0) as valor " +
            "from execucaocontratoempestitem itemest " +
            "         inner join execucaocontratoempenhoest exsol on exsol.id = itemest.execucaocontratoempenhoest_id " +
            "         inner join solicitacaoempenhoestorno solest on solest.id = exsol.solicitacaoempenhoestorno_id " +
            "         inner join solicitacaoempenho solemp on solemp.id = solest.solicitacaoempenho_id " +
            "where solemp.fontedespesaorc_id = :idFonte " +
            "  and itemest.execucaocontratoitem_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idFonte", fonte.getId());
        q.setParameter("idItem", item.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }


    public BigDecimal buscarValorEstornadoPorTipoObjetoCompra(Contrato contrato, TipoObjetoCompra tipoObjetoCompra) {
        String sql = " select coalesce(sum(item.valortotal),0) as valor_estornado from execucaocontratoempestitem item " +
            "           inner join execucaocontratoitem exitem on exitem.id = item.execucaocontratoitem_id" +
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

    public BigDecimal recuperarValorSolicitacaoEstornoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho, ExecucaoContrato execucaoContrato) {
        String sql = " select coalesce(sum(sol.valor),0) as total  " +
            "  from execucaocontratoestorno exEst  " +
            "  inner join execucaocontratoempenhoest est on est.execucaocontratoestorno_id = exEst.id  " +
            "  inner join solicitacaoempenhoestorno sol on sol.id = est.solicitacaoempenhoestorno_id  " +
            "  where sol.empenho_id is null  " +
            "   and exEst.execucaocontrato_id = :idExecucao " +
            "   and sol.solicitacaoempenho_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", execucaoContrato.getId());
        q.setParameter("idSolicitacao", solicitacaoEmpenho.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal recuperarValorSolicitacaoEstornoEmpenhoAguardandoEstornoEmpenho(Empenho empenho, ExecucaoContrato execucaoContrato) {
        String sql = " select coalesce(sum(sol.valor),0) - coalesce(sum(sel.valor),0) as total  " +
            "  from execucaocontratoestorno exEst  " +
            "  inner join execucaocontratoempenhoest est on est.execucaocontratoestorno_id = exEst.id  " +
            "  inner join solicitacaoempenhoestorno sol on sol.id = est.solicitacaoempenhoestorno_id  " +
            "  left join execucaocontratoliquidest ecle on ecle.execucaocontratoempenhoest_id = est.id " +
            "  left join solicitacaoliquidacaoest sel on sel.id = ecle.solicitacaoliquidacaoest_id " +
            "  where sol.empenhoestorno_id is null  " +
            "  and exEst.execucaocontrato_id = :idExecucao " +
            "  and sol.empenho_id = :idEmpenho ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", execucaoContrato.getId());
        q.setParameter("idEmpenho", empenho.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal recuperarValorSolicitacaoEstornoLiquidacaoAguardandoEstornoLiquidacao(Liquidacao liquidacao, ExecucaoContrato execucaoContrato) {
        String sql = " select coalesce(sum(sol.valor),0) as total from execucaocontratoestorno exEst " +
            "          inner join execucaocontratoempenhoest est on est.execucaocontratoestorno_id = exEst.id " +
            "          inner join execucaocontratoliquidest ecle on ecle.execucaocontratoempenhoest_id = est.id " +
            "          inner join solicitacaoliquidacaoest sol on sol.id = ecle.solicitacaoliquidacaoest_id " +
            "          where sol.liquidacaoestorno_id is null " +
            "          and exEst.execucaocontrato_id = :idExecucao " +
            "          and sol.liquidacao_id = :idLiquidacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", execucaoContrato.getId());
        q.setParameter("idLiquidacao", liquidacao.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public List<ExecucaoContratoEmpenhoEstorno> buscarExecucaoContratoEstornoAguardandoEstornoEmpenho(Contrato contrato, ExecucaoContrato execucaoCont) {
        String sql = " select exsol.* from execucaocontratoempenhoest exsol  " +
            "           inner join solicitacaoempenhoestorno sol on exsol.solicitacaoempenhoestorno_id = sol.id " +
            "           inner join execucaocontratoestorno est on est.id = exsol.execucaocontratoestorno_id " +
            "           inner join execucaocontrato ex on ex.id = est.execucaocontrato_id " +
            "          where ex.contrato_id = :idContrato " +
            "           and sol.empenhoestorno_id is null " +
            "           and sol.empenho_id is not null ";
        sql += execucaoCont != null && execucaoCont.getId() != null ? " and ex.id = :idExecucao " : "";
        Query q = em.createNativeQuery(sql, ExecucaoContratoEmpenhoEstorno.class);
        q.setParameter("idContrato", contrato.getId());
        if (execucaoCont != null && execucaoCont.getId() != null) {
            q.setParameter("idExecucao", execucaoCont.getId());
        }
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public void verificarSeExisteSolicitacaoEstornoExecucaoPendentes(ExecucaoContrato execucaoContrato) {
        ValidacaoException ve = new ValidacaoException();
        List<ExecucaoContratoEmpenhoEstorno> solicitacoes = buscarExecucaoContratoEstornoAguardandoEstornoEmpenho(execucaoContrato.getContrato(), execucaoContrato);
        if (!Util.isListNullOrEmpty(solicitacoes)) {
            solicitacoes.stream()
                .map(exEmpEst -> "Para continuar é necessário estornar a solicitação de estorno de execução " + exEmpEst.getSolicitacaoEmpenhoEstorno() +
                    ", referente a execução nº " + exEmpEst.getExecucaoContratoEstorno().getExecucaoContrato().getNumero() +
                    ", " + Util.linkBlack("/execucao-contrato-estorno/ver/" + exEmpEst.getExecucaoContratoEstorno().getId() + "/", "Clique no link para visualizar."))
                .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        }
        ve.lancarException();
    }

    public SolicitacaoEmpenhoEstornoVo criarSolicitacaoEmpenhoEstornoVoPorEmpenho(ExecucaoContratoEstorno selecionado, Empenho empenho, SolicitacaoEmpenho solicitacaoEmpenho) {
        BigDecimal valorSolEmpenhoExecutado = getValorEmpenhoExecutado(empenho, selecionado);
        SolicitacaoEmpenhoEstornoVo solEmpVo = new SolicitacaoEmpenhoEstornoVo(SolicitacaoEmpenhoEstornoVo.TipoEstornoExecucao.EMPENHO);
        solEmpVo.setEmpenho(empenho);
        solEmpVo.setSolicitacaoEmpenho(solicitacaoEmpenho);
        solEmpVo.setCategoria(empenho.getCategoriaOrcamentaria());
        solEmpVo.setSaldo(empenho.getSaldo().subtract(valorSolEmpenhoExecutado));
        solEmpVo.setLiquidacoes(Lists.<SolicitacaoEmpenhoEstornoLoquidacaoVo>newArrayList());
        if (solEmpVo.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            solEmpVo.setSaldo(BigDecimal.ZERO);
        }
        criarSolicitacaoEstornoLiquidacaoVo(empenho, solEmpVo, selecionado);
        return solEmpVo;
    }

    public BigDecimal getValorEmpenhoExecutado(Empenho empenho, ExecucaoContratoEstorno selecionado) {
        BigDecimal valorSolEmpSalvo = recuperarValorSolicitacaoEstornoEmpenhoAguardandoEstornoEmpenho(empenho, selecionado.getExecucaoContrato());
        BigDecimal valor = BigDecimal.ZERO;
        for (ExecucaoContratoEmpenhoEstorno execucao : selecionado.getEstornosEmpenho()) {
            if (empenho.equals(execucao.getSolicitacaoEmpenhoEstorno().getEmpenho())) {
                valor = valor.add(execucao.getSolicitacaoEmpenhoEstorno().getValor());
            }
        }
        return valorSolEmpSalvo.add(valor);
    }

    private void criarSolicitacaoEstornoLiquidacaoVo(Empenho empenho, SolicitacaoEmpenhoEstornoVo solEmpVo, ExecucaoContratoEstorno selecionado) {
        List<Liquidacao> liquidacoes = getLiquidacaoFacade().buscarPorEmpenho(empenho);
        for (Liquidacao liquidacao : liquidacoes) {
            BigDecimal valorSolLiquidacaoExecutado = getValorSolLiquidacaoExecutado(liquidacao, selecionado);

            SolicitacaoEmpenhoEstornoLoquidacaoVo solLiqVo = new SolicitacaoEmpenhoEstornoLoquidacaoVo();
            solLiqVo.setVoExecucaoContratoEstorno(solEmpVo);
            solLiqVo.setLiquidacao(liquidacao);
            solLiqVo.setSelecionado(false);
            solLiqVo.setSaldo(liquidacao.getSaldo().subtract(valorSolLiquidacaoExecutado));

            List<Pagamento> pagamentos = getPagamentoFacade().buscarPagamentoPorLiquidacaoAndSituacoes(liquidacao, StatusPagamento.getStatusDiferenteAberto());
            for (Pagamento pagamento : pagamentos) {
                solLiqVo.setValorTotalPagamento(solLiqVo.getValorTotalPagamento().add(pagamento.getValor()));
            }
            solLiqVo.setPagamentos(pagamentos);
            solEmpVo.getLiquidacoes().add(solLiqVo);
        }
    }

    public BigDecimal getValorSolLiquidacaoExecutado(Liquidacao liquidacao, ExecucaoContratoEstorno selecionado) {
        BigDecimal valorSolLiquidacaoBanco = recuperarValorSolicitacaoEstornoLiquidacaoAguardandoEstornoLiquidacao(liquidacao, selecionado.getExecucaoContrato());
        BigDecimal valorSolLiquidacaoEmExecucao = BigDecimal.ZERO;
        for (ExecucaoContratoEmpenhoEstorno execucao : selecionado.getEstornosEmpenho()) {
            for (ExecucaoContratoLiquidacaoEstorno exLiqEst : execucao.getSolicitacoesLiquidacaoEstorno()) {
                if (liquidacao.equals(exLiqEst.getSolicitacaoLiquidacaoEst().getLiquidacao())) {
                    valorSolLiquidacaoEmExecucao = valorSolLiquidacaoEmExecucao.add(exLiqEst.getSolicitacaoLiquidacaoEst().getValor());
                }
            }
        }
        return valorSolLiquidacaoBanco.add(valorSolLiquidacaoEmExecucao);
    }

    public List<SolicitacaoEmpenhoEstornoItemVo> criarItensEstorno(ExecucaoContratoEstorno selecionado, SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo, List<ExecucaoContratoItem> itensExecucao) {
        List<SolicitacaoEmpenhoEstornoItemVo> retorno = Lists.newArrayList();
        Map<TipoObjetoCompra, List<ExecucaoContratoItem>> map = criarMapTipoObjetoCompraItensExecucao(itensExecucao);
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
                    for (ExecucaoContratoItem item : map.get(TipoObjetoCompra.PERMANENTE_MOVEL)) {
                        if (contaMovel != null) {
                            GrupoBem grupoBem = getConfigGrupoBemFacade().buscarGrupoBemPorContaDespesa(getSistemaFacade().getDataOperacao(), contaMovel);
                            GrupoObjetoCompraGrupoBem associacaoGrupoBem = getAssociacaoGrupoBemFacade().buscarAssociacaoPorGrupoObjetoCompraVigente(item.getObjetoCompra().getGrupoObjetoCompra(), new Date());
                            if (grupoBem.equals(associacaoGrupoBem.getGrupoBem())) {
                                Util.adicionarObjetoEmLista(retorno, criarExecucaoContratoEmpenhoEstornoItemVO(item, selecionado, solicitacaoEmpenhoEstornoVo));
                            }
                        } else {
                            Util.adicionarObjetoEmLista(retorno, criarExecucaoContratoEmpenhoEstornoItemVO(item, selecionado, solicitacaoEmpenhoEstornoVo));
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

                    for (ExecucaoContratoItem item : map.get(TipoObjetoCompra.CONSUMO)) {
                        if (contaEstoque != null) {
                            GrupoMaterial grupoMaterial = getConfigGrupoMaterialFacade().buscarGrupoPorConta(contaEstoque);
                            AssociacaoGrupoObjetoCompraGrupoMaterial associocaoGrupoMat = getAssocicaoGrupoMaterialFacade().buscarAssociacaoPorGrupoObjetoCompraVigente(item.getObjetoCompra().getGrupoObjetoCompra(), new Date());
                            if (grupoMaterial.equals(associocaoGrupoMat.getGrupoMaterial())) {
                                Util.adicionarObjetoEmLista(retorno, criarExecucaoContratoEmpenhoEstornoItemVO(item, selecionado, solicitacaoEmpenhoEstornoVo));
                            }
                        } else {
                            Util.adicionarObjetoEmLista(retorno, criarExecucaoContratoEmpenhoEstornoItemVO(item, selecionado, solicitacaoEmpenhoEstornoVo));
                        }
                    }
                    break;
                case BEM_IMOVEL:
                    List<ExecucaoContratoItem> itensImovel = map.get(TipoObjetoCompra.PERMANENTE_IMOVEL);
                    if (itensImovel != null) {
                        for (ExecucaoContratoItem item : itensImovel) {
                            Util.adicionarObjetoEmLista(retorno, criarExecucaoContratoEmpenhoEstornoItemVO(item, selecionado, solicitacaoEmpenhoEstornoVo));
                        }
                    }
                    break;
                default:
                    List<ExecucaoContratoItem> itensServico = map.get(TipoObjetoCompra.SERVICO);
                    if (itensServico != null) {
                        for (ExecucaoContratoItem item : itensServico) {
                            Util.adicionarObjetoEmLista(retorno, criarExecucaoContratoEmpenhoEstornoItemVO(item, selecionado, solicitacaoEmpenhoEstornoVo));
                        }
                    }
                    break;
            }
        } else {
            for (ExecucaoContratoItem item : itensExecucao) {
                Util.adicionarObjetoEmLista(retorno, criarExecucaoContratoEmpenhoEstornoItemVO(item, selecionado, solicitacaoEmpenhoEstornoVo));
            }
        }
        return retorno;
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

    @NotNull
    private Map<TipoObjetoCompra, List<ExecucaoContratoItem>> criarMapTipoObjetoCompraItensExecucao(List<ExecucaoContratoItem> itensExecucao) {
        Map<TipoObjetoCompra, List<ExecucaoContratoItem>> map = new HashMap<>();
        for (ExecucaoContratoItem item : itensExecucao) {
            TipoObjetoCompra tipoObjetoCompra = item.getObjetoCompra().getTipoObjetoCompra();
            if (!map.containsKey(tipoObjetoCompra)) {
                map.put(tipoObjetoCompra, Lists.newArrayList(item));
            } else {
                map.get(tipoObjetoCompra).add(item);
            }
        }
        return map;
    }

    public SolicitacaoEmpenhoEstornoItemVo criarExecucaoContratoEmpenhoEstornoItemVO(ExecucaoContratoItem itemEx, ExecucaoContratoEstorno selecionado, SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo) {
        SolicitacaoEmpenhoEstornoItemVo novoItemVo = new SolicitacaoEmpenhoEstornoItemVo();
        novoItemVo.setExecucaoContratoItem(itemEx);
        novoItemVo.setValorEstornadoExecucao(getValorEstornado(itemEx).add(getValorAdicionadoEstorno(itemEx, selecionado, solicitacaoEmpenhoEstornoVo)));
        novoItemVo.setValorEmRequisicao(getRequisicaoDeCompraFacade().getValorEmRequisicao(itemEx));
        novoItemVo.setValorEstornoRequisicao(getRequisicaoCompraEstornoFacade().getValorEstornado(itemEx));
        novoItemVo.setValorEmExecucao(itemEx.getValorTotal());
        novoItemVo.setQuantidade(itemEx.isExecucaoPorQuantidade() ? BigDecimal.ZERO : BigDecimal.ONE);
        novoItemVo.setValorUnitario(itemEx.isExecucaoPorQuantidade() ? itemEx.getValorUnitario() : novoItemVo.getSaldoDisponivel());
        novoItemVo.setValorTotal(novoItemVo.getSaldoDisponivel());
        if (itemEx.isExecucaoPorQuantidade()) {
            novoItemVo.calcularQuantidade();
        }

        if (solicitacaoEmpenhoEstornoVo.isTipoEstornoSolicitacaoEmpenho()) {
            BigDecimal valorEstornado = getValorEstornado(solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getFonteDespesaORC(), itemEx);
            novoItemVo.setValorEstornadoExecucao(valorEstornado.add(getValorAdicionadoEstorno(itemEx, selecionado, solicitacaoEmpenhoEstornoVo)));

            novoItemVo.setValorEmExecucao(getExecucaoContratoFacade().getValorItemContratoDotacao(selecionado.getExecucaoContrato(), solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getFonteDespesaORC(), itemEx));
            novoItemVo.setValorTotal(novoItemVo.getSaldoDisponivel());
            novoItemVo.calcularQuantidade();
        }
        return novoItemVo;
    }

    private BigDecimal getValorAdicionadoEstorno(ExecucaoContratoItem itemExecucao, ExecucaoContratoEstorno selecionado, SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo) {
        BigDecimal total = BigDecimal.ZERO;
        if (hasEstornosLancados(selecionado)) {
            for (ExecucaoContratoEmpenhoEstorno exEmpEst : selecionado.getEstornosEmpenho()) {
                for (ExecucaoContratoEmpenhoEstornoItem itemEstorno : exEmpEst.getItens()) {
                    if (itemEstorno.getExecucaoContratoItem().equals(itemExecucao)
                        && exEmpEst.getSolicitacaoEmpenhoEstorno().getSolicitacaoEmpenho().getFonteDespesaORC().equals(solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getFonteDespesaORC())) {
                        total = total.add(itemEstorno.getValorTotal());
                    }
                }
            }
        }
        return total;
    }

    public boolean hasEstornosLancados(ExecucaoContratoEstorno selecionado) {
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

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }

    public RequisicaoCompraEstornoFacade getRequisicaoCompraEstornoFacade() {
        return requisicaoCompraEstornoFacade;
    }

    public ReprocessamentoSaldoContratoFacade getReprocessamentoSaldoContratoFacade() {
        return reprocessamentoSaldoContratoFacade;
    }

    public LiquidacaoFacade getLiquidacaoFacade() {
        return liquidacaoFacade;
    }

    public SolicitacaoEmpenhoEstornoFacade getSolicitacaoEmpenhoEstornoFacade() {
        return solicitacaoEmpenhoEstornoFacade;
    }

    public PagamentoFacade getPagamentoFacade() {
        return pagamentoFacade;
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
