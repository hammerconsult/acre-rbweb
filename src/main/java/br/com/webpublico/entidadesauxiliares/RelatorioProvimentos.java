package br.com.webpublico.entidadesauxiliares;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mateus on 02/12/2014.
 */
public class RelatorioProvimentos {

    private String codigoUnidadeOrganizacional;
    private String unidadeOrganizacional;
    private Date dataProvimento;
    private String tipo;
    private String enquadramento;
    private String cargo;
    private String modalidade;
    private String opcao;
    private Date dataExoneracao;
    private String contrato;
    private String nomeServidor;
    private String situacaoFuncional;
    private List<DetalhamentoProvimento> listaDetalhamentoProvimento;

    public RelatorioProvimentos() {
        this.listaDetalhamentoProvimento = new ArrayList<>();
    }

    public String getCodigoUnidadeOrganizacional() {
        return codigoUnidadeOrganizacional;
    }

    public void setCodigoUnidadeOrganizacional(String codigoUnidadeOrganizacional) {
        this.codigoUnidadeOrganizacional = codigoUnidadeOrganizacional;
    }

    public String getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(String unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Date getDataProvimento() {
        return dataProvimento;
    }

    public void setDataProvimento(Date dataProvimento) {
        this.dataProvimento = dataProvimento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEnquadramento() {
        return enquadramento;
    }

    public void setEnquadramento(String enquadramento) {
        this.enquadramento = enquadramento;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getOpcao() {
        return opcao;
    }

    public void setOpcao(String opcao) {
        this.opcao = opcao;
    }

    public Date getDataExoneracao() {
        return dataExoneracao;
    }

    public void setDataExoneracao(Date dataExoneracao) {
        this.dataExoneracao = dataExoneracao;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getNomeServidor() {
        return nomeServidor;
    }

    public void setNomeServidor(String nomeServidor) {
        this.nomeServidor = nomeServidor;
    }

    public String getSituacaoFuncional() {
        return situacaoFuncional;
    }

    public void setSituacaoFuncional(String situacaoFuncional) {
        this.situacaoFuncional = situacaoFuncional;
    }

    public List<DetalhamentoProvimento> getListaDetalhamentoProvimento() {
        return listaDetalhamentoProvimento;
    }

    public void setListaDetalhamentoProvimento(List<DetalhamentoProvimento> listaDetalhamentoProvimento) {
        this.listaDetalhamentoProvimento = listaDetalhamentoProvimento;
    }
}
