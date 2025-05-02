package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by carlos on 13/06/17.
 */
public class RelatorioServidoresAtivosPorCargoVO {
    private String matricula;
    private String cnt;
    private String servidor;
    private Date dataNomeacao;
    private String modalidade;
    private String cargo;
    private String codigoDoCargo;
    private String status;
    private String numeroAto;
    private BigDecimal anoAto;
    private String codigoOpcao;
    private String percentual;
    private String recDesc;
    private String recCodigo;
    private String codigoRec;
    private String codeGrupo;
    private String orgao;
    private String referenciaEnquadramento;
    private BigDecimal valorVencimentoBase;
    private Date publicacaoAtoLegal;

    public RelatorioServidoresAtivosPorCargoVO() {
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public Date getDataNomeacao() {
        return dataNomeacao;
    }

    public void setDataNomeacao(Date dataNomeacao) {
        this.dataNomeacao = dataNomeacao;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCodigoDoCargo() {
        return codigoDoCargo;
    }

    public void setCodigoDoCargo(String codigoDoCargo) {
        this.codigoDoCargo = codigoDoCargo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNumeroAto() {
        return numeroAto;
    }

    public void setNumeroAto(String numeroAto) {
        this.numeroAto = numeroAto;
    }

    public BigDecimal getAnoAto() {
        return anoAto;
    }

    public void setAnoAto(BigDecimal anoAto) {
        this.anoAto = anoAto;
    }

    public String getCodigoOpcao() {
        return codigoOpcao;
    }

    public void setCodigoOpcao(String codigoOpcao) {
        this.codigoOpcao = codigoOpcao;
    }

    public String getPercentual() {
        return percentual;
    }

    public void setPercentual(String percentual) {
        this.percentual = percentual;
    }

    public String getRecDesc() {
        return recDesc;
    }

    public void setRecDesc(String recDesc) {
        this.recDesc = recDesc;
    }

    public String getRecCodigo() {
        return recCodigo;
    }

    public void setRecCodigo(String recCodigo) {
        this.recCodigo = recCodigo;
    }

    public String getCodigoRec() {
        return codigoRec;
    }

    public void setCodigoRec(String codigoRec) {
        this.codigoRec = codigoRec;
    }

    public String getCodeGrupo() {
        return codeGrupo;
    }

    public void setCodeGrupo(String codeGrupo) {
        this.codeGrupo = codeGrupo;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getReferenciaEnquadramento() {
        return referenciaEnquadramento;
    }

    public void setReferenciaEnquadramento(String referenciaEnquadramento) {
        this.referenciaEnquadramento = referenciaEnquadramento;
    }

    public BigDecimal getValorVencimentoBase() {
        return valorVencimentoBase;
    }

    public void setValorVencimentoBase(BigDecimal valorVencimentoBase) {
        this.valorVencimentoBase = valorVencimentoBase;
    }

    public Date getPublicacaoAtoLegal() {
        return publicacaoAtoLegal;
    }

    public void setPublicacaoAtoLegal(Date publicacaoAtoLegal) {
        this.publicacaoAtoLegal = publicacaoAtoLegal;
    }
}
