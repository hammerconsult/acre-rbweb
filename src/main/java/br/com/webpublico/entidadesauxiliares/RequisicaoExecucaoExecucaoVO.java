package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ExecucaoContrato;
import br.com.webpublico.entidades.ExecucaoProcesso;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoRequisicaoCompra;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RequisicaoExecucaoExecucaoVO {

    private Long id;
    private Integer numero;
    private Date dataLancamento;
    private BigDecimal valorExecucao;
    private UnidadeOrganizacional unidadeOrganizacional;
    private TipoRequisicaoCompra tipoExecucao;
    private ExecucaoContrato execucaoContrato;
    private ExecucaoProcesso execucaoProcesso;
    private Boolean selecionado;
    private List<EmpenhoVO> empenhos;

    public RequisicaoExecucaoExecucaoVO() {
        selecionado = false;
    }

    public RequisicaoExecucaoExecucaoVO(ExecucaoProcesso execucao) {
        numero = execucao.getNumero();
        dataLancamento = execucao.getDataLancamento();
        unidadeOrganizacional = execucao.getUnidadeOrcamentaria();
        valorExecucao = execucao.getValor();
        execucaoProcesso = execucao;
        selecionado = false;
        empenhos = Lists.newArrayList();
    }

    public RequisicaoExecucaoExecucaoVO(ExecucaoContrato execucao) {
        numero = execucao.getNumero();
        dataLancamento = execucao.getDataLancamento();
        unidadeOrganizacional = execucao.getUnidadeOrcamentaria();
        valorExecucao = execucao.getValor();
        execucaoContrato = execucao;
        selecionado = false;
        empenhos = Lists.newArrayList();
    }

    public List<EmpenhoVO> getEmpenhos() {
        return empenhos;
    }

    public void setEmpenhos(List<EmpenhoVO> empenhos) {
        this.empenhos = empenhos;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public ExecucaoProcesso getExecucaoProcesso() {
        return execucaoProcesso;
    }

    public void setExecucaoProcesso(ExecucaoProcesso execucaoProcesso) {
        this.execucaoProcesso = execucaoProcesso;
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

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public BigDecimal getValorExecucao() {
        return valorExecucao;
    }

    public void setValorExecucao(BigDecimal valorExecucao) {
        this.valorExecucao = valorExecucao;
    }

    public TipoRequisicaoCompra getTipoExecucao() {
        return tipoExecucao;
    }

    public void setTipoExecucao(TipoRequisicaoCompra tipoExecucao) {
        this.tipoExecucao = tipoExecucao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValorTotalEmpenhos() {
        BigDecimal total = BigDecimal.ZERO;
        for (EmpenhoVO empVO : empenhos) {
            total = total.add(empVO.getEmpenho().getValor());
        }
        return total;
    }

    @Override
    public String toString() {
        try {
            return "Nº " + numero + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + Util.formataValor(valorExecucao);
        } catch (NullPointerException e) {
            return "";
        }
    }
}
