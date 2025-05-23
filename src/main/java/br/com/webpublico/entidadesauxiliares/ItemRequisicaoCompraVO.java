package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoDescontoItemRequisicao;
import br.com.webpublico.util.Util;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ItemRequisicaoCompraVO implements Serializable, Comparable<ItemRequisicaoCompraVO> {

    private Long id;
    private Integer numero;
    private Integer numeroItemProcesso;
    private ObjetoCompra objetoCompra;
    private String descricaoComplementar;
    private UnidadeMedida unidadeMedida;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private RequisicaoDeCompra requisicaoDeCompra;
    private DerivacaoObjetoCompraComponente derivacaoComponente;
    private BigDecimal valorUnitarioDesdobrado;
    private BigDecimal percentualDesconto;
    private BigDecimal valorDescontoUnitario;
    private BigDecimal valorDescontoTotal;
    private BigDecimal quantidadeDisponivel;
    private BigDecimal valorTotalItemEntrada;
    private Boolean arredondaValorTotal;
    private Material material;
    private Long criadoEm;
    private TipoDescontoItemRequisicao tipoDesconto;
    private List<ItemRequisicaoCompraExecucaoVO> itensRequisicaoExecucao;
    private Boolean editando;
    private TipoControleLicitacao tipoControle;
    private List<ItemRequisicaoCompraVO> itensDesdobrados;

    public ItemRequisicaoCompraVO() {
        editando = false;
        itensRequisicaoExecucao = Lists.newArrayList();
        itensDesdobrados = Lists.newArrayList();
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        valorUnitarioDesdobrado = BigDecimal.ZERO;
        percentualDesconto = BigDecimal.ZERO;
        valorDescontoUnitario = BigDecimal.ZERO;
        valorDescontoTotal = BigDecimal.ZERO;
        quantidadeDisponivel = BigDecimal.ZERO;
        valorTotalItemEntrada = BigDecimal.ZERO;
        arredondaValorTotal = Boolean.FALSE;
        tipoDesconto = TipoDescontoItemRequisicao.NAO_ARREDONDAR;
        criadoEm = System.nanoTime();
    }

    public static ItemRequisicaoCompraVO toObjeto(ItemRequisicaoDeCompra itemReq) {
        ItemRequisicaoCompraVO novoItemVO = new ItemRequisicaoCompraVO();
        novoItemVO.setId(itemReq.getId());
        novoItemVO.setNumero(itemReq.getNumero());
        novoItemVO.setNumeroItemProcesso(itemReq.getNumeroItemProcesso());
        novoItemVO.setCriadoEm(System.nanoTime());
        novoItemVO.setRequisicaoDeCompra(itemReq.getRequisicaoDeCompra());
        novoItemVO.setQuantidade(itemReq.getQuantidade());
        novoItemVO.setValorUnitario(itemReq.getValorUnitario());
        novoItemVO.setValorUnitarioDesdobrado(itemReq.getValorUnitarioDesdobrado());
        novoItemVO.setPercentualDesconto(itemReq.getPercentualDesconto());
        novoItemVO.setValorTotal(itemReq.getValorTotal());
        novoItemVO.setObjetoCompra(itemReq.getObjetoCompra());
        novoItemVO.setUnidadeMedida(itemReq.getUnidadeMedida());
        novoItemVO.setDerivacaoComponente(itemReq.getDerivacaoComponente());
        novoItemVO.setDescricaoComplementar(itemReq.getDescricaoComplementar());
        novoItemVO.setArredondaValorTotal(itemReq.getArredondaValorTotal());
        novoItemVO.setTipoDesconto(itemReq.getTipoDesconto());
        novoItemVO.setValorDescontoUnitario(itemReq.getValorDescontoUnitario());
        novoItemVO.setValorDescontoTotal(itemReq.getValorDescontoTotal());
        novoItemVO.setTipoControle(itemReq.getTipoControle());
        return novoItemVO;
    }

    public Boolean getEditando() {
        return editando;
    }

    public void setEditando(Boolean editando) {
        this.editando = editando;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public List<ItemRequisicaoCompraVO> getItensDesdobrados() {
        return itensDesdobrados;
    }

    public void setItensDesdobrados(List<ItemRequisicaoCompraVO> itensDesdobrados) {
        this.itensDesdobrados = itensDesdobrados;
    }

    public List<ItemRequisicaoCompraVO> getItensDesdobradosPorEmpenho(ItemRequisicaoCompraExecucaoVO itemReqExecVO) {
        return this.itensDesdobrados.stream()
            .filter(itemReqVO -> itemReqExecVO.getIdItemExecucao().equals(itemReqVO.getIdItemExecucao()))
            .collect(Collectors.toList());
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public DerivacaoObjetoCompraComponente getDerivacaoComponente() {
        return derivacaoComponente;
    }

    public void setDerivacaoComponente(DerivacaoObjetoCompraComponente derivacaoComponente) {
        this.derivacaoComponente = derivacaoComponente;
    }

    public Boolean isControleQuantidade() {
        return tipoControle.isTipoControlePorQuantidade();
    }

    public Boolean isControleValor() {
        return tipoControle.isTipoControlePorValor();
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal preco) {
        this.valorUnitario = preco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getQuantidade() {
        if (quantidade == null) {
            return BigDecimal.ZERO;
        }
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public RequisicaoDeCompra getRequisicaoDeCompra() {
        return requisicaoDeCompra;
    }

    public void setRequisicaoDeCompra(RequisicaoDeCompra requisicaoDeCompra) {
        this.requisicaoDeCompra = requisicaoDeCompra;
    }

    public boolean isDerivacaoObjetoCompra() {
        return derivacaoComponente != null;
    }

    @Override
    public String toString() {
        return "Nrº " + numero + " - " + getDescricao();
    }

    public String getDescricao() {
        try {
            return objetoCompra.getDescricao();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getValorTotal() {
        if (valorTotal == null) {
            return BigDecimal.ZERO;
        }
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<ItemRequisicaoCompraExecucaoVO> getItensRequisicaoExecucao() {
        return itensRequisicaoExecucao;
    }

    public void setItensRequisicaoExecucao(List<ItemRequisicaoCompraExecucaoVO> itensDesdobrados) {
        this.itensRequisicaoExecucao = itensDesdobrados;
    }

    public ItemContrato getItemContrato() {
        Optional<ItemRequisicaoCompraExecucaoVO> first = itensRequisicaoExecucao.stream()
            .filter(exec -> exec.getExecucaoContratoItem() != null)
            .findAny();
        return first.map(ItemRequisicaoCompraExecucaoVO::getItemContrato).orElse(null);
    }

    public ExecucaoContratoItem getExecucaoContratoItem() {
        Optional<ItemRequisicaoCompraExecucaoVO> first = itensRequisicaoExecucao.stream()
            .filter(exec -> exec.getExecucaoContratoItem() != null)
            .findAny();
        return first.map(ItemRequisicaoCompraExecucaoVO::getExecucaoContratoItem).orElse(null);
    }

    public ExecucaoProcessoItem getExecucaoProcessoItem() {
        Optional<ItemRequisicaoCompraExecucaoVO> first = itensRequisicaoExecucao.stream()
            .filter(exec -> exec.getExecucaoProcessoItem() != null)
            .findAny();
        return first.map(ItemRequisicaoCompraExecucaoVO::getExecucaoProcessoItem).orElse(null);
    }

    public ItemReconhecimentoDivida getItemReconhecimentoDivida() {
        Optional<ItemRequisicaoCompraExecucaoVO> first = itensRequisicaoExecucao.stream()
            .filter(exec -> exec.getItemReconhecimentoDivida() != null)
            .findAny();
        return first.map(ItemRequisicaoCompraExecucaoVO::getItemReconhecimentoDivida).orElse(null);
    }

    public Boolean getArredondaValorTotal() {
        return arredondaValorTotal;
    }

    public void setArredondaValorTotal(Boolean arredondaValorTotal) {
        this.arredondaValorTotal = arredondaValorTotal;
    }

    public TipoDescontoItemRequisicao getTipoDesconto() {
        return tipoDesconto;
    }

    public void setTipoDesconto(TipoDescontoItemRequisicao tipoDescontoValorDesconto) {
        this.tipoDesconto = tipoDescontoValorDesconto;
    }

    public boolean hasItensRequisicaoExecucao() {
        return itensRequisicaoExecucao != null && !itensRequisicaoExecucao.isEmpty();
    }

    public boolean hasItensDesdobrados() {
        return !Util.isListNullOrEmpty(itensDesdobrados);
    }

    public boolean hasApenasUmItemRequisicaoExecucao() {
        return hasItensRequisicaoExecucao() && itensRequisicaoExecucao.size() == 1;
    }

    public boolean isNaoArredondarValorDesconto() {
        return tipoDesconto.isNaoArredondar();
    }

    public boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorUnitario() {
        return valorUnitario != null && valorUnitario.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasDiferencaRequisicaoComEntrada() {
        return getValorTotal().compareTo(getValorTotalItemEntrada()) != 0;
    }

    public boolean hasValorUnitarioDesdobrado() {
        return valorUnitarioDesdobrado != null && valorUnitarioDesdobrado.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasPercentualDesconto() {
        return percentualDesconto != null && percentualDesconto.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public BigDecimal getValorUnitarioDesdobrado() {
        return valorUnitarioDesdobrado;
    }

    public void setValorUnitarioDesdobrado(BigDecimal valorUnitarioOriginal) {
        this.valorUnitarioDesdobrado = valorUnitarioOriginal;
    }

    public BigDecimal getValorDescontoUnitario() {
        if (valorDescontoUnitario == null) {
            return BigDecimal.ZERO;
        }
        return valorDescontoUnitario;
    }

    public void setValorDescontoUnitario(BigDecimal valorDesconto) {
        this.valorDescontoUnitario = valorDesconto;
    }

    public BigDecimal getValorDescontoTotal() {
        if (valorDescontoTotal == null) {
            return BigDecimal.ZERO;
        }
        return valorDescontoTotal;
    }

    public void setValorDescontoTotal(BigDecimal valorDescontoTotal) {
        this.valorDescontoTotal = valorDescontoTotal;
    }

    public RoundingMode getRoundigModeValorTotal() {
        try {
            return getArredondaValorTotal() ? RoundingMode.HALF_EVEN : RoundingMode.FLOOR;
        } catch (Exception e) {
            return RoundingMode.HALF_EVEN;
        }
    }

    public RoundingMode getRoundigModeValorUnitario() {
        try {
            return tipoDesconto.isArredondar() ? RoundingMode.HALF_EVEN : RoundingMode.FLOOR;
        } catch (Exception e) {
            return RoundingMode.HALF_EVEN;
        }
    }

    public int getScaleUnitario() {
        try {
            if (requisicaoDeCompra.getTipoObjetoCompra().isMaterialPermanente() && tipoDesconto.isNaoArredondar()) {
                return TipoDescontoItemRequisicao.MATERIAL_PERMANTENTE.getScale();
            }
            return tipoDesconto.getScale();
        } catch (Exception e) {
            return TipoDescontoItemRequisicao.ARREDONDAR.getScale();
        }
    }

    public void calcularValoresItemRequisicaoAndItemExecucao() {
        if (requisicaoDeCompra.isTipoContrato() && hasApenasUmItemRequisicaoExecucao()) {
            ItemRequisicaoCompraExecucaoVO itemReqExecucao = itensRequisicaoExecucao.get(0);
            itemReqExecucao.setQuantidade(hasQuantidade() ? quantidade : BigDecimal.ZERO);
            itemReqExecucao.setValorUnitario(valorUnitario);

            BigDecimal valorTotal = itemReqExecucao.getQuantidade().multiply(itemReqExecucao.getValorUnitario());
            itemReqExecucao.setValorTotal(valorTotal.setScale(2, RoundingMode.HALF_EVEN));
        }
        calcularValorTotal();
    }

    public void calcularValorTotal() {
        setValorTotal(BigDecimal.ZERO);
        if (hasQuantidade() && hasValorUnitario()) {
            BigDecimal valorTotal = quantidade.multiply(valorUnitario);
            setValorTotal(valorTotal.setScale(2, RoundingMode.HALF_EVEN));

            if (requisicaoDeCompra.isLicitacaoMaiorDesconto()) {
                BigDecimal subtract = getValorTotalDesdobrado().subtract(getValorDescontoTotal());
                valorTotal = subtract.setScale(2, getRoundigModeValorTotal());
                setValorTotal(valorTotal);
            }
        }
    }

    public void atribuirValorUnitario() {
        if (hasValorUnitarioDesdobrado()) {
            if (isControleQuantidade()) {
                setValorUnitario(getValorUnitarioDesdobrado());
                return;
            }
            setValorUnitario(getValorUnitarioComDesconto().setScale(getScaleUnitario(), RoundingMode.FLOOR));
        }
    }

    public BigDecimal getValorUnitarioComDesconto() {
        if (hasValorUnitarioDesdobrado()) {
            return getValorUnitarioDesdobrado().subtract(getValorDescontoUnitario());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getQuantidadeDisponivelItemExecucao() {
        BigDecimal quantidade = BigDecimal.ZERO;
        for (ItemRequisicaoCompraExecucaoVO item : itensRequisicaoExecucao) {
            if (!item.getAjusteValor()) {
                quantidade = quantidade.add(item.getQuantidadeDisponivel());
            }
        }
        return quantidade;
    }

    public BigDecimal getValorTotalItemExecucao() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemRequisicaoCompraExecucaoVO item : itensRequisicaoExecucao) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    public BigDecimal getQuantidadeTotalItemExecucao() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemRequisicaoCompraExecucaoVO item : itensRequisicaoExecucao) {
            if (!item.getAjusteValor()) {
                total = total.add(item.getQuantidade());
            }
        }
        return total;
    }

    public BigDecimal getValorUnitarioTotalItemExecucao() {
        BigDecimal valorUnitario = BigDecimal.ZERO;
        Map<BigDecimal, ItemRequisicaoCompraExecucaoVO> map = new HashMap<>();
        for (ItemRequisicaoCompraExecucaoVO item : itensRequisicaoExecucao) {
            if (!map.containsKey(item.getValorUnitario())) {
                map.put(item.getValorUnitario(), item);
            }
        }
        for (Map.Entry<BigDecimal, ItemRequisicaoCompraExecucaoVO> entry : map.entrySet()) {
            valorUnitario = valorUnitario.add(entry.getKey());
        }
        return valorUnitario;
    }

    public BigDecimal getValorDescontoUnitarioPorTipoDesconto(TipoDescontoItemRequisicao tipoDesconto) {
        if (hasQuantidade() && hasValorUnitarioDesdobrado()) {
            if (tipoDesconto.isNaoArredondar()) {
                return getValorDescontoSemArredondar();
            } else {
                return getValorDescontoSemArredondar().setScale(getScaleUnitario(), getRoundigModeValorUnitario());
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorDescontoTotalPorTipoDesconto(TipoDescontoItemRequisicao tipoDesconto) {
        if (hasQuantidade() && hasValorUnitarioDesdobrado()) {
            return getQuantidade().multiply(getValorDescontoUnitarioPorTipoDesconto(tipoDesconto)).setScale(2, getRoundigModeValorUnitario());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorDescontoSemArredondar() {
        BigDecimal valorDesconto = BigDecimal.ZERO;
        if (!isControleQuantidade() && hasValorUnitarioDesdobrado() && hasPercentualDesconto()) {
            valorDesconto = valorUnitarioDesdobrado.multiply(percentualDesconto).divide(new BigDecimal("100"), 10, RoundingMode.FLOOR);
        }
        return valorDesconto;
    }

    public BigDecimal getValorTotalItemDesdobrado() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemRequisicaoCompraVO itemReq : getItensDesdobrados()) {
            total = total.add(itemReq.getValorTotal());
        }
        return total;
    }

    public void calcularValorTotalItemEntradaPorCompra() {
        if (hasQuantidade() && hasValorUnitario()) {
            setValorTotalItemEntrada(getQuantidade().multiply(getValorUnitario()).setScale(2, getRoundigModeValorTotal()));
        }
    }

    public BigDecimal getValorTotalDesdobrado() {
        if (hasQuantidade() && hasValorUnitarioDesdobrado()) {
            return quantidade.multiply(valorUnitarioDesdobrado).setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getPercentualDescontoDaNota() {
        if (hasQuantidade() && hasValorUnitarioDesdobrado()) {
            BigDecimal descontoTotal = getValorDescontoTotal();
            return descontoTotal.multiply(new BigDecimal("100")).divide(getValorTotalDesdobrado(), 10, RoundingMode.FLOOR);
        }
        return BigDecimal.ZERO;
    }

    public Long getIdItemExecucao() {
        if (getExecucaoContratoItem() != null) {
            return getExecucaoContratoItem().getId();
        } else if (getExecucaoProcessoItem() != null) {
            return getExecucaoProcessoItem().getId();
        }
        return getItemReconhecimentoDivida().getId();
    }

    public BigDecimal getValorTotalItemEntrada() {
        return valorTotalItemEntrada;
    }

    public void setValorTotalItemEntrada(BigDecimal valorTotalItemEntrada) {
        this.valorTotalItemEntrada = valorTotalItemEntrada;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Integer getNumeroItemProcesso() {
        return numeroItemProcesso;
    }

    public void setNumeroItemProcesso(Integer numeroItemProcesso) {
        this.numeroItemProcesso = numeroItemProcesso;
    }

    public Integer getNumeroLoteProcesso() {
        try {
            if (getItemContrato() != null) {
                return getItemContrato().getItemAdequado().getNumeroLote();
            } else if (getExecucaoProcessoItem() != null) {
                return getExecucaoProcessoItem().getItemProcessoCompra().getNumeroLote();
            }
            return 1;
        } catch (Exception e) {
            return null;
        }
    }


    public void ordenarItensDesdobradosPorNumero() {
        if (hasItensDesdobrados()) {
            itensDesdobrados.sort(new Comparator<ItemRequisicaoCompraVO>() {
                @Override
                public int compare(ItemRequisicaoCompraVO o1, ItemRequisicaoCompraVO o2) {
                    return ComparisonChain.start()
                        .compare(o1.getNumero(), o2.getNumero())
                        .compare(o1.getNumeroLoteProcesso(), o2.getNumeroLoteProcesso())
                        .compare(o1.getNumeroItemProcesso(), o2.getNumeroItemProcesso())
                        .result();
                }
            });
        }
    }

    public void ordernarItensDesdobradosPorEmpenho() {
        itensDesdobrados.sort(new Comparator<ItemRequisicaoCompraVO>() {
            @Override
            public int compare(ItemRequisicaoCompraVO o1, ItemRequisicaoCompraVO o2) {
                try {
                    Empenho emp1 = o1.getItensRequisicaoExecucao().get(0).getRequisicaoEmpenhoVO().getEmpenho();
                    Empenho emp2 = o2.getItensRequisicaoExecucao().get(0).getRequisicaoEmpenhoVO().getEmpenho();
                    return ComparisonChain.start().compare(emp1.getNumero(), emp2.getNumero()).result();
                } catch (ArrayIndexOutOfBoundsException e) {
                    return 0;
                }
            }
        });
    }

    @Override
    public int compareTo(ItemRequisicaoCompraVO o) {
        if (o.getNumeroLoteProcesso() != null && getNumeroLoteProcesso() != null
            && o.getNumeroItemProcesso() != null && getNumeroItemProcesso() != null)
            return ComparisonChain.start().compare(getNumeroLoteProcesso(), o.getNumeroLoteProcesso())
                .compare(getNumeroItemProcesso(), o.getNumeroItemProcesso()).result();

        return ComparisonChain.start().compare(o.getNumero(), getNumero()).result();
    }
}
