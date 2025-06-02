package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BensEstoqueVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.ItemMovimentoEstoque;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 31/03/15
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class IntegradorMateriaisContabilFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private TransferenciaBensEstoqueFacade transferenciaBensEstoqueFacade;
    @EJB
    private BensEstoqueFacade bensEstoqueFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    public void contabilzarEntradaPorTransferencia(EntradaTransferenciaMaterial entradaMaterial) throws ExcecaoNegocioGenerica {
        for (ItemEntradaMaterial itemEntradaMaterial : entradaMaterial.getItens()) {
            TransferenciaBensEstoque transfBens = novaTransferenciaBensEstoque(itemEntradaMaterial, itemEntradaMaterial.getHistoricoRazaoTransferencia(), false);
            transfBens = transferenciaBensEstoqueFacade.salvarTransferencia(transfBens);
            gerarSaidaMaterialContabilTransfBensEstoque(entradaMaterial.getSaidaRequisicaoMaterial(), transfBens);
            gerarEntradaMaterialContabilTransfBensEstoque(entradaMaterial, transfBens, TipoEntradaMaterial.TRANSFERENCIA);
        }
    }

    public void contabilizarEntradaPorIncorporacao(EntradaIncorporacaoMaterial entradaIncorporacao) throws ExcecaoNegocioGenerica {
        for (ItemEntradaMaterial itemEntradaMaterial : entradaIncorporacao.getItens()) {
            BensEstoque bensEstoque = bensEstoqueFacade.salvarBensEstoque(criarNovoBensEstoqueIncorporacao(entradaIncorporacao, itemEntradaMaterial, TipoOperacaoBensEstoque.INCORPORACAO_BENS_ESTOQUE, itemEntradaMaterial.getHistoricoRazaoIncorporacao(), false));
            gerarEntradaMaterialContabilBensEstoque(entradaIncorporacao, bensEstoque, TipoEntradaMaterial.INCORPORACAO);
        }
    }

    public void contabilizarDiferencaLevantamentoBensEstoque(List<BensEstoque> bensEstoque, EfetivacaoLevantamentoEstoque efetivacaoLevantamento) {
        try {
            for (BensEstoque bemEstoque : bensEstoque) {
                bemEstoque.setHistorico(efetivacaoLevantamento.getHistoricoRazao(bemEstoque.getOperacoesBensEstoque()));
                bensEstoqueFacade.salvarBensEstoque(bemEstoque);
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Ocorreu um erro ao contabilizar direfença de levantamento de bens de estoque.");
        }
    }

    public void contabilizarSaidaPorConsumo(SaidaMaterial saidaMaterial) throws ExcecaoNegocioGenerica {
        ordenarItensSaida(saidaMaterial);
        List<AgrupadorItensSaida> itensAgrupados = agruparListaItensSaida(saidaMaterial.getListaDeItemSaidaMaterial(), saidaMaterial.getDataSaida());
        for (AgrupadorItensSaida itemSaidaMaterial : itensAgrupados) {
            BensEstoque bensEstoque = criarNovoBensEstoque(
                itemSaidaMaterial,
                TipoOperacaoBensEstoque.BAIXA_BENS_ESTOQUE_POR_CONSUMO,
                saidaMaterial.getHistoricoRazao(itemSaidaMaterial, "Saída por Consumo"));
            bensEstoque = bensEstoqueFacade.salvarBensEstoque(bensEstoque);
            gerarSaidaMaterialContabilBensEstoque(saidaMaterial, bensEstoque);
        }
    }

    public void contabilizarSaidaDireta(SaidaMaterial saidaMaterial) throws ExcecaoNegocioGenerica {
        ordenarItensSaida(saidaMaterial);
        List<AgrupadorItensSaida> itensAgrupados = agruparListaItensSaida(saidaMaterial.getListaDeItemSaidaMaterial(), saidaMaterial.getDataSaida());
        for (AgrupadorItensSaida itemSaidaMaterial : itensAgrupados) {
            BensEstoque bensEstoque = criarNovoBensEstoque(
                itemSaidaMaterial,
                TipoOperacaoBensEstoque.BAIXA_BENS_ESTOQUE_POR_CONSUMO,
                saidaMaterial.getHistoricoRazao(itemSaidaMaterial, "Saída Direta"));
            bensEstoque = bensEstoqueFacade.salvarBensEstoque(bensEstoque);
            gerarSaidaMaterialContabilBensEstoque(saidaMaterial, bensEstoque);
        }
    }

    public void contabilizarSaidaPorDesincorporacao(SaidaMaterial saidaMaterial) throws ExcecaoNegocioGenerica {
        ordenarItensSaida(saidaMaterial);
        List<AgrupadorItensSaida> itensAgrupados = agruparListaItensSaida(saidaMaterial.getListaDeItemSaidaMaterial(), saidaMaterial.getDataSaida());
        for (AgrupadorItensSaida itemSaidaMaterial : itensAgrupados) {
            BensEstoque bensEstoque = criarNovoBensEstoque(
                itemSaidaMaterial,
                TipoOperacaoBensEstoque.BAIXA_BENS_ESTOQUE_POR_DESINCORPORACAO,
                saidaMaterial.getHistoricoRazao(itemSaidaMaterial, "Saída por Desincorporação"));
            bensEstoque = bensEstoqueFacade.salvarBensEstoque(bensEstoque);
            gerarSaidaMaterialContabilBensEstoque(saidaMaterial, bensEstoque);
        }
    }

    private void ordenarItensSaida(SaidaMaterial saidaMaterial) {
        Collections.sort(saidaMaterial.getListaDeItemSaidaMaterial());
    }

    public void contabilizarProducaoBensEstoque(Producao producao, String historico, Boolean estorno) throws ExcecaoNegocioGenerica {
        for (ItemProduzido itemProduzido : producao.getItemsProduzidos()) {
            bensEstoqueFacade.salvarNovo(criarNovoBensEstoqueEntradaMaterialProducao(itemProduzido, TipoOperacaoBensEstoque.INCORPORACAO_BENS_ESTOQUE, historico, estorno));
            for (InsumoAplicado insumoAplicado : itemProduzido.getInsumosAplicados()) {
                bensEstoqueFacade.salvarNovo(criarNovoBensEstoqueSaidaMaterialProducao(insumoAplicado, TipoOperacaoBensEstoque.BAIXA_BENS_ESTOQUE_POR_CONSUMO, historico, estorno));
            }
        }
    }

    private BensEstoque criarNovoBensEstoque(AgrupadorItensSaida itemSaida, TipoOperacaoBensEstoque operacao, String historico) {
        BensEstoque bensEstoque = new BensEstoque();
        bensEstoque.setDataBem(itemSaida.getDataMovimento());
        bensEstoque.setExercicio(exercicioFacade.getExercicioPorAno(DataUtil.getAno(itemSaida.getDataMovimento())));
        bensEstoque.setOperacoesBensEstoque(operacao);
        bensEstoque.setUnidadeOrganizacional(itemSaida.getUnidadeOrcamentaria());
        bensEstoque.setTipoBaixaBens(itemSaida.getTipoBaixaBens());
        bensEstoque.setGrupoMaterial(itemSaida.getMaterial().getGrupo());
        bensEstoque.setTipoEstoque(itemSaida.getTipoEstoque());
        bensEstoque.setTipoLancamento(itemSaida.ehEstorno() ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        bensEstoque.setHistorico(historico);
        bensEstoque.setValor(itemSaida.getValor());
        return bensEstoque;
    }

    private BensEstoque criarNovoBensEstoqueSaidaMaterialProducao(InsumoAplicado insumoAplicado, TipoOperacaoBensEstoque operacaoBensEstoque, String historico, Boolean estorno) {
        BensEstoque bensEstoque = new BensEstoque();
        bensEstoque.setDataBem(insumoAplicado.getItemProduzido().getProducao().getDataProducao());
        bensEstoque.setExercicio(exercicioFacade.getExercicioPorAno(DataUtil.getAno(insumoAplicado.getItemProduzido().getProducao().getDataProducao())));
        bensEstoque.setOperacoesBensEstoque(operacaoBensEstoque);
        bensEstoque.setPessoa(null);
        bensEstoque.setUnidadeOrganizacional(insumoAplicado.getItemProduzido().getProducao().getUnidadeOrganizacional());
        bensEstoque.setTipoBaixaBens(null);
        bensEstoque.setGrupoMaterial(insumoAplicado.getInsumoProducao().getMaterial().getGrupo());
        bensEstoque.setTipoIngresso(null);
        bensEstoque.setTipoEstoque(insumoAplicado.getInsumoProducao().getLocalEstoque().getTipoEstoque());
        bensEstoque.setTipoLancamento(estorno ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        bensEstoque.setHistorico(historico);
        bensEstoque.setHistoricoRazao(historico);
        bensEstoque.setHistoricoNota(historico);
        bensEstoque.setValor(insumoAplicado.getInsumoProducao().getItemSaidaMaterial().getValorTotal().setScale(2, RoundingMode.HALF_EVEN));
        return bensEstoque;
    }

    private BensEstoque criarNovoBensEstoqueEntradaMaterialProducao(ItemProduzido itemProduzido, TipoOperacaoBensEstoque operacaoBensEstoque, String historico, Boolean estorno) {
        BensEstoque bensEstoque = new BensEstoque();
        bensEstoque.setDataBem(itemProduzido.getProducao().getDataProducao());
        bensEstoque.setExercicio(exercicioFacade.getExercicioPorAno(DataUtil.getAno(itemProduzido.getProducao().getDataProducao())));
        bensEstoque.setOperacoesBensEstoque(operacaoBensEstoque);
        bensEstoque.setPessoa(null);
        bensEstoque.setUnidadeOrganizacional(itemProduzido.getLocalEstoqueOrcamentario().getUnidadeOrcamentaria());
        bensEstoque.setTipoBaixaBens(null);
        bensEstoque.setGrupoMaterial(itemProduzido.getMaterial().getGrupo());
        bensEstoque.setTipoIngresso(itemProduzido.getProducao().getEntradaGerada().getTipoIngresso());
        bensEstoque.setTipoEstoque(itemProduzido.getTipoEstoque());
        bensEstoque.setTipoLancamento(estorno ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        bensEstoque.setHistorico(historico);
        bensEstoque.setHistoricoRazao(historico);
        bensEstoque.setHistoricoNota(historico);
        bensEstoque.setValor(itemProduzido.getValorFinanceiroTotal().setScale(2, RoundingMode.FLOOR));
        return bensEstoque;
    }


    private BensEstoque criarNovoBensEstoqueIncorporacao(EntradaIncorporacaoMaterial entradaIncorporacaoMaterial, ItemEntradaMaterial itemEntradaMaterial, TipoOperacaoBensEstoque operacaoBensEstoque, String historico, Boolean estorno) {
        BensEstoque bensEstoque = new BensEstoque();
        bensEstoque.setDataBem(itemEntradaMaterial.getDataMovimento());
        bensEstoque.setOperacoesBensEstoque(operacaoBensEstoque);
        bensEstoque.setExercicio(exercicioFacade.getExercicioPorAno(DataUtil.getAno(itemEntradaMaterial.getDataMovimento())));
        bensEstoque.setPessoa(entradaIncorporacaoMaterial.getPessoa());
        bensEstoque.setUnidadeOrganizacional(itemEntradaMaterial.getUnidadeOrcamentaria());
        bensEstoque.setTipoBaixaBens(null);
        bensEstoque.setGrupoMaterial(itemEntradaMaterial.getMaterial().getGrupo());
        bensEstoque.setTipoIngresso(entradaIncorporacaoMaterial.getTipoIngresso());
        bensEstoque.setTipoEstoque(itemEntradaMaterial.getTipoEstoque());
        bensEstoque.setTipoLancamento(estorno ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        bensEstoque.setHistorico(historico);
        bensEstoque.setHistoricoRazao(historico);
        bensEstoque.setHistoricoNota(historico);
        bensEstoque.setValor(itemEntradaMaterial.getValorTotal().setScale(2, RoundingMode.HALF_EVEN));
        return bensEstoque;
    }

    public BensEstoque novoBensEstoque(BensEstoqueVO bensEstoqueVO) {
        BensEstoque bensEstoque = new BensEstoque();
        bensEstoque.setDataBem(bensEstoqueVO.getDataBem());
        bensEstoque.setOperacoesBensEstoque(bensEstoqueVO.getOperacoesBensEstoque());
        bensEstoque.setExercicio(exercicioFacade.getExercicioPorAno(DataUtil.getAno(bensEstoqueVO.getDataBem())));
        bensEstoque.setUnidadeOrganizacional(bensEstoqueVO.getUnidadeOrganizacional());
        bensEstoque.setGrupoMaterial(bensEstoqueVO.getGrupoMaterial());
        bensEstoque.setTipoEstoque(bensEstoqueVO.getTipoEstoque());
        bensEstoque.setTipoLancamento(bensEstoqueVO.getTipoLancamento());
        bensEstoque.setHistorico(bensEstoqueVO.getHistorico());
        bensEstoque.setValor(bensEstoqueVO.getValor());
        return bensEstoque;
    }

    private TransferenciaBensEstoque novaTransferenciaBensEstoque(ItemEntradaMaterial itemEntrada, String historico, Boolean estorno) {
        TransferenciaBensEstoque transferencia = new TransferenciaBensEstoque();
        transferencia.setDataTransferencia(itemEntrada.getDataMovimento());
        transferencia.setExercicio(exercicioFacade.getExercicioPorAno(DataUtil.getAno(itemEntrada.getDataMovimento())));
        transferencia.setTipoTransferencia(TipoTransferenciaBensEstoque.TRANSFERENCIA);
        transferencia.setTipoLancamento(estorno ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        transferencia.setHistorico(historico);
        transferencia.setValor(itemEntrada.getValorTotal().setScale(2, RoundingMode.HALF_EVEN));

        ItemSaidaMaterial itemSaida = itemEntrada.getItemSaidaMaterial();
        transferencia.setOperacaoOrigem(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA);
        transferencia.setTipoEstoqueOrigem(itemSaida.getTipoEstoque());
        transferencia.setGrupoMaterial(itemSaida.getMaterial().getGrupo());
        transferencia.setUnidadeOrgOrigem(itemSaida.getUnidadeOrcamentaria());

        transferencia.setOperacaoDestino(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA);
        transferencia.setTipoEstoqueDestino(itemEntrada.getTipoEstoque());
        transferencia.setGrupoMaterialDestino(itemEntrada.getMaterial().getGrupo());
        transferencia.setUnidadeOrgDestino(itemEntrada.getUnidadeOrcamentaria());

        return transferencia;
    }

    private void gerarSaidaMaterialContabilBensEstoque(SaidaMaterial saidaMaterial, BensEstoque bensEstoque) {
        SaidaMaterialContabil saida = new SaidaMaterialContabil();
        saida.setSaidaMaterial(saidaMaterial);
        saida.setTipoSaidaMaterial(saidaMaterial.getTipoDestaSaida());
        saida.setBensEstoque(bensEstoque);
        em.persist(saida);
    }

    private void gerarSaidaMaterialContabilTransfBensEstoque(SaidaMaterial saidaMaterial, TransferenciaBensEstoque transfBensEstoque) {
        SaidaMaterialContabil saida = new SaidaMaterialContabil();
        saida.setSaidaMaterial(saidaMaterial);
        saida.setTipoSaidaMaterial(saidaMaterial.getTipoDestaSaida());
        saida.setTransferenciaBensEstoque(transfBensEstoque);
        em.persist(saida);
    }

    private void gerarEntradaMaterialContabilBensEstoque(EntradaMaterial entradaMaterial, BensEstoque bensEstoque, TipoEntradaMaterial tipo) {
        EntradaMaterialContabil entrada = new EntradaMaterialContabil();
        entrada.setEntradaMaterial(entradaMaterial);
        entrada.setTipoEntradaMaterial(tipo);
        entrada.setBensEstoque(bensEstoque);
        em.persist(entrada);
    }

    private void gerarEntradaMaterialContabilTransfBensEstoque(EntradaMaterial entradaMaterial, TransferenciaBensEstoque transfBensEstoque, TipoEntradaMaterial tipo) {
        EntradaMaterialContabil entrada = new EntradaMaterialContabil();
        entrada.setEntradaMaterial(entradaMaterial);
        entrada.setTipoEntradaMaterial(tipo);
        entrada.setTransferenciaBensEstoque(transfBensEstoque);
        em.persist(entrada);
    }

    public static List<AgrupadorItensSaida> agruparListaItensSaida(List<? extends ItemSaidaMaterial> itensSaida, Date dataOperacao) {
        List<AgrupadorItensSaida> retorno = new ArrayList<>();
        for (ItemSaidaMaterial item : itensSaida) {
            AgrupadorItensSaida itemAgrupado = new AgrupadorItensSaida(item, dataOperacao);
            if (retorno.contains(itemAgrupado)) {
                retorno.get(retorno.indexOf(itemAgrupado)).adicionarValor(item);
            } else {
                itemAgrupado.adicionarValor(item);
                retorno.add(itemAgrupado);
            }
        }
        return retorno;
    }

    public static class AgrupadorItensSaida<T extends ItemSaidaMaterial> implements ItemMovimentoEstoque {

        private BigDecimal valor = BigDecimal.ZERO;
        private List<T> itensSaida;
        private Date dataOperacao;
        private ItemSaidaMaterial itemSaidaMaterial;

        public AgrupadorItensSaida(ItemSaidaMaterial itemSaida, Date dataOperacao) {
            this.itensSaida = Lists.newArrayList();
            this.dataOperacao = dataOperacao;
            this.itemSaidaMaterial = itemSaida;
        }

        public void adicionarValor(T itemSaida) {
            this.valor = this.valor.add(itemSaida.getValorTotal());
            this.itensSaida.add(itemSaida);
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }

        public List<T> getItensSaida() {
            return itensSaida;
        }

        public void setItensSaida(List<T> itensSaida) {
            this.itensSaida = itensSaida;
        }

        public Date getDataOperacao() {
            return dataOperacao;
        }

        public void setDataOperacao(Date dataOperacao) {
            this.dataOperacao = dataOperacao;
        }

        public ItemSaidaMaterial getItemSaidaMaterial() {
            return itemSaidaMaterial;
        }

        public void setItemSaidaMaterial(ItemSaidaMaterial itemSaidaMaterial) {
            this.itemSaidaMaterial = itemSaidaMaterial;
        }

        @Override
        public Long getIdOrigem() {
            return itemSaidaMaterial.getSaidaMaterial().getId();
        }

        @Override
        public Integer getNumeroItem() {
            return itemSaidaMaterial.getNumeroItem();
        }

        @Override
        public Date getDataMovimento() {
            return this.itemSaidaMaterial.getDataMovimento();
        }

        @Override
        public BigDecimal getValorTotal() {
            return getQuantidade().multiply(this.itemSaidaMaterial.getValorUnitario());
        }

        @Override
        public BigDecimal getQuantidade() {
            return itemSaidaMaterial.getQuantidade();
        }

        @Override
        public BigDecimal getValorUnitario() {
            return itemSaidaMaterial.getValorUnitario();
        }

        @Override
        public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario() {
            return this.itemSaidaMaterial.getLocalEstoqueOrcamentario();
        }

        @Override
        public TipoEstoque getTipoEstoque() {
            return itemSaidaMaterial.getTipoEstoque();
        }

        @Override
        public String getDescricaoDaOperacao() {
            return itemSaidaMaterial.getDescricaoDaOperacao();
        }

        @Override
        public TipoOperacao getTipoOperacao() {
            return TipoOperacao.DEBITO;
        }

        @Override
        public Material getMaterial() {
            return this.itemSaidaMaterial.getMaterial();
        }

        @Override
        public LoteMaterial getLoteMaterial() {
            return this.itemSaidaMaterial.getLoteMaterial();
        }

        @Override
        public UnidadeOrganizacional getUnidadeOrcamentaria() {
            return itemSaidaMaterial.getUnidadeOrcamentaria();
        }

        @Override
        public TipoBaixaBens getTipoBaixaBens() {
            return itemSaidaMaterial.getTipoBaixaBens();
        }

        @Override
        public TipoIngresso getTipoIngressoBens() {
            return itemSaidaMaterial.getTipoIngressoBens();
        }

        @Override
        public Boolean ehEstorno() {
            return itemSaidaMaterial.ehEstorno();
        }

        @Override
        public boolean equals(Object obj) {
            boolean igual = false;
            ItemMovimentoEstoque itemSaida = (ItemMovimentoEstoque) obj;
            if (itemSaida == null) {
                return false;
            } else {
                if (this.getUnidadeOrcamentaria().equals(itemSaida.getLocalEstoqueOrcamentario().getUnidadeOrcamentaria())
                    && this.getMaterial().getGrupo().equals(itemSaida.getMaterial().getGrupo())
                    && this.getTipoEstoque().equals(itemSaida.getTipoEstoque())) {
                    igual = true;
                }
            }
            return igual;
        }

        @Override
        public String toString() {
            return "Tipo de Estoque: " + getTipoEstoque() + " Grupo Material: " + getMaterial().getGrupo() +
                " Unidade Orçamentária: " + getUnidadeOrcamentaria();
        }
    }
}
