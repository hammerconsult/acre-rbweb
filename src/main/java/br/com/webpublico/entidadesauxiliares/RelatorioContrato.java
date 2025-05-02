package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.webreportdto.dto.administrativo.RelatorioContratoDTO;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RelatorioContrato {

    private String unidadeAdministrativa;
    private String unidadeOrcamentaria;
    private String contrato;
    private String processo;
    private String numeroContrato;
    private Date inicio;
    private Date termino;
    private String modalidade;
    private String objeto;
    private Long idContrato;
    private String fornecedor;
    private String formaPagamento;
    private String fiscalContrato;
    private String responsavelUnidade;
    private BigDecimal valorContrato;
    private BigDecimal valorAtualContrato;
    private BigDecimal valorEmpenhado;
    private Boolean selecionado;
    private List<RelatorioContratoExecucao> execucoes;
    private List<RelatorioContratoAditivo> aditivos;

    public RelatorioContrato() {
        valorContrato = BigDecimal.ZERO;
        execucoes = Lists.newArrayList();
        aditivos = Lists.newArrayList();
    }

    public static List<RelatorioContratoDTO> contratosToDto (List<RelatorioContrato> contratos) {
        List<RelatorioContratoDTO> retorno = Lists.newArrayList();
        for (RelatorioContrato contrato : contratos) {
            retorno.add(contratoToDto(contrato));
        }
        return retorno;
    }

    public static RelatorioContratoDTO contratoToDto (RelatorioContrato contrato) {
        RelatorioContratoDTO retorno = new RelatorioContratoDTO();
        retorno.setUnidadeAdministrativa(contrato.getUnidadeAdministrativa());
        retorno.setUnidadeOrcamentaria(contrato.getUnidadeOrcamentaria());
        retorno.setContrato(contrato.getContrato());
        retorno.setProcesso(contrato.getProcesso());
        retorno.setNumeroContrato(contrato.getNumeroContrato());
        retorno.setInicio(contrato.getInicio());
        retorno.setTermino(contrato.getTermino());
        retorno.setModalidade(contrato.getModalidade());
        retorno.setObjeto(contrato.getObjeto());
        retorno.setIdContrato(contrato.getIdContrato());
        retorno.setFornecedor(contrato.getFornecedor());
        retorno.setFormaPagamento(contrato.getFormaPagamento());
        retorno.setFiscalContrato(contrato.getFiscalContrato());
        retorno.setResponsavelUnidade(contrato.getResponsavelUnidade());
        retorno.setValorContrato(contrato.getValorContrato());
        retorno.setValorAtualContrato(contrato.getValorAtualContrato());
        retorno.setValorEmpenhado(contrato.getValorEmpenhado());
        retorno.setSelecionado(contrato.getSelecionado());
        retorno.setExecucoes(RelatorioContratoExecucao.execucoesToDto(contrato.getExecucoes()));
        retorno.setAditivos(RelatorioContratoAditivo.aditivosToDto(contrato.getAditivos()));
        return retorno;
    }

    public String getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(String unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public String getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(String unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public BigDecimal getValorContrato() {
        return valorContrato;
    }

    public void setValorContrato(BigDecimal valorContrato) {
        this.valorContrato = valorContrato;
    }

    public List<RelatorioContratoExecucao> getExecucoes() {
        return execucoes;
    }

    public void setExecucoes(List<RelatorioContratoExecucao> execucoes) {
        this.execucoes = execucoes;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getFiscalContrato() {
        return fiscalContrato;
    }

    public void setFiscalContrato(String fiscalContrato) {
        this.fiscalContrato = fiscalContrato;
    }

    public String getResponsavelUnidade() {
        return responsavelUnidade;
    }

    public void setResponsavelUnidade(String responsavelUnidade) {
        this.responsavelUnidade = responsavelUnidade;
    }

    public List<RelatorioContratoAditivo> getAditivos() {
        return aditivos;
    }

    public void setAditivos(List<RelatorioContratoAditivo> aditivos) {
        this.aditivos = aditivos;
    }

    public Boolean getSelecionado() {
        return selecionado == null ? false : selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public BigDecimal getValorAtualContrato() {
        return valorAtualContrato;
    }

    public void setValorAtualContrato(BigDecimal valorAtualContrato) {
        this.valorAtualContrato = valorAtualContrato;
    }

    public BigDecimal getValorEmpenhado() {
        return valorEmpenhado;
    }

    public void setValorEmpenhado(BigDecimal valorEmpenhado) {
        this.valorEmpenhado = valorEmpenhado;
    }
}
