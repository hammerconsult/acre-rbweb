package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;
import java.util.Date;

public class FichaFinanceiraFPServidorAndAposentadoVO extends FichaFinanceiraFPCamposVO {

    private Date dataNomeacao;
    private Date dataPosse;
    private Date dataRescisao;
    private Date dataAdmissao;
    private Date exoneracao;
    private Date aposentadoria;
    private BigDecimal DepIR;
    private BigDecimal DepSalFam;
    private Long idVinculo;
    private Long idPessoa;
    private String modalidadeDeContrato;
    private String progressao;
    private String tabela;
    private String orgao;
    private String codProgressao;
    private String CodCategoria;
    private String pcs;

    // Campos exclusivos do servidor
    private Date reintegracao;
    private Date prorrogacao;
    private String conta;
    private Date creditadoEm;

    public FichaFinanceiraFPServidorAndAposentadoVO() {
    }

    public Date getDataNomeacao() {
        return dataNomeacao;
    }

    public void setDataNomeacao(Date dataNomeacao) {
        this.dataNomeacao = dataNomeacao;
    }

    public Date getDataPosse() {
        return dataPosse;
    }

    public void setDataPosse(Date dataPosse) {
        this.dataPosse = dataPosse;
    }

    public Date getDataRescisao() {
        return dataRescisao;
    }

    public void setDataRescisao(Date dataRescisao) {
        this.dataRescisao = dataRescisao;
    }

    public Date getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(Date dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public Date getExoneracao() {
        return exoneracao;
    }

    public void setExoneracao(Date exoneracao) {
        this.exoneracao = exoneracao;
    }

    public Date getAposentadoria() {
        return aposentadoria;
    }

    public void setAposentadoria(Date aposentadoria) {
        this.aposentadoria = aposentadoria;
    }

    public BigDecimal getDepIR() {
        return DepIR;
    }

    public void setDepIR(BigDecimal depIR) {
        DepIR = depIR;
    }

    public BigDecimal getDepSalFam() {
        return DepSalFam;
    }

    public void setDepSalFam(BigDecimal depSalFam) {
        DepSalFam = depSalFam;
    }

    public String getModalidadeDeContrato() {
        return modalidadeDeContrato;
    }

    public void setModalidadeDeContrato(String modalidadeDeContrato) {
        this.modalidadeDeContrato = modalidadeDeContrato;
    }

    public String getProgressao() {
        return progressao;
    }

    public void setProgressao(String progressao) {
        this.progressao = progressao;
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getCodProgressao() {
        return codProgressao;
    }

    public void setCodProgressao(String codProgressao) {
        this.codProgressao = codProgressao;
    }

    public String getCodCategoria() {
        return CodCategoria;
    }

    public void setCodCategoria(String codCategoria) {
        CodCategoria = codCategoria;
    }

    public String getPcs() {
        return pcs;
    }

    public void setPcs(String pcs) {
        this.pcs = pcs;
    }

    public Date getReintegracao() {
        return reintegracao;
    }

    public void setReintegracao(Date reintegracao) {
        this.reintegracao = reintegracao;
    }

    public Date getProrrogacao() {
        return prorrogacao;
    }

    public void setProrrogacao(Date prorrogacao) {
        this.prorrogacao = prorrogacao;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public Date getCreditadoEm() {
        return creditadoEm;
    }

    public void setCreditadoEm(Date creditadoEm) {
        this.creditadoEm = creditadoEm;
    }

    public Long getIdVinculo() {
        return idVinculo;
    }

    public void setIdVinculo(Long idVinculo) {
        this.idVinculo = idVinculo;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }
}
