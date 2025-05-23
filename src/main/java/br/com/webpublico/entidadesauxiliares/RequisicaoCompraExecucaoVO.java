package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.enums.TipoRequisicaoCompra;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RequisicaoCompraExecucaoVO implements Comparable<RequisicaoCompraExecucaoVO> {

    private Long id;
    private String numero;
    private Date dataLancamento;
    private BigDecimal valor;
    private String unidadeOrcamentaria;
    private TipoRequisicaoCompra tipoProcesso;
    private TipoObjetoCompra tipoObjetoCompra;
    private Boolean selecionado;
    private List<RequisicaoCompraEmpenhoVO> empenhosVO;

    public RequisicaoCompraExecucaoVO() {
        selecionado = false;
        empenhosVO = Lists.newArrayList();
    }

    public List<RequisicaoCompraEmpenhoVO> getEmpenhosVO() {
        return empenhosVO;
    }


    public void setEmpenhosVO(List<RequisicaoCompraEmpenhoVO> empenhosVO) {
        this.empenhosVO = empenhosVO;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoRequisicaoCompra getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoRequisicaoCompra tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public String getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(String unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public BigDecimal getValorTotalEmpenhos() {
        BigDecimal total = BigDecimal.ZERO;
        for (RequisicaoCompraEmpenhoVO empVO : empenhosVO) {
            total = total.add(empVO.getEmpenho().getValor());
        }
        return total;
    }

    @Override
    public String toString() {
        try {
            return "Nº " + numero + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + Util.formataValor(valor);
        } catch (NullPointerException e) {
            return "";
        }
    }

    @Override
    public int compareTo(RequisicaoCompraExecucaoVO o) {
        if (getNumero() != null && o.getNumero() != null) {
            return ComparisonChain.start().compare(Integer.valueOf(getNumero()), Integer.valueOf(o.getNumero())).result();
        }
        return 0;
    }
}
