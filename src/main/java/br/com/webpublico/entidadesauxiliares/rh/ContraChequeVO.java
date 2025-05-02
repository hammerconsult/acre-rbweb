package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.collect.Lists;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by tharlyson on 17/01/20.
 */
public class ContraChequeVO {

    private String nome;
    private String matricula;
    private String contrato;
    private String cpf;
    private Date dataNascimento;
    private Long idFicha;
    private Long idVinculo;
    private String modalidadeContrato;
    private String referencia;
    private Date inicioVigencia;
    private String lotacaoFolha;
    private Integer dependenteIR;
    private Integer dependenteSalarioFamilia;
    private String cargo;
    private Date dataAdmissao;
    private String tipoFolha;
    private String mesAno;
    private BigDecimal totalBruto;
    private BigDecimal totalDesconto;
    private BigDecimal totalLiquido;
    private BigDecimal baseFGTS;
    private BigDecimal baseIRRF;
    private BigDecimal basePREV;
    private String unidadeOrganizacional;
    private String banco;
    private String agencia;
    private String contaBancaria;
    private String autenticidade;
    private InputStream qrCode;
    private List<ContraChequeItensVO> contraChequeItens;

    public ContraChequeVO() {
        contraChequeItens = Lists.newArrayList();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Long getIdFicha() {
        return idFicha;
    }

    public void setIdFicha(Long idFicha) {
        this.idFicha = idFicha;
    }

    public String getModalidadeContrato() {
        return modalidadeContrato;
    }

    public void setModalidadeContrato(String modalidadeContrato) {
        this.modalidadeContrato = modalidadeContrato;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getLotacaoFolha() {
        return lotacaoFolha;
    }

    public void setLotacaoFolha(String lotacaoFolha) {
        this.lotacaoFolha = lotacaoFolha;
    }

    public Integer getDependenteIR() {
        return dependenteIR;
    }

    public void setDependenteIR(Integer dependenteIR) {
        this.dependenteIR = dependenteIR;
    }

    public Integer getDependenteSalarioFamilia() {
        return dependenteSalarioFamilia;
    }

    public void setDependenteSalarioFamilia(Integer dependenteSalarioFamilia) {
        this.dependenteSalarioFamilia = dependenteSalarioFamilia;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Date getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(Date dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(String unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getTipoFolha() {
        return tipoFolha;
    }

    public void setTipoFolha(String tipoFolha) {
        this.tipoFolha = tipoFolha;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(BigDecimal totalBruto) {
        this.totalBruto = totalBruto;
    }

    public BigDecimal getTotalDesconto() {
        return totalDesconto;
    }

    public void setTotalDesconto(BigDecimal totalDesconto) {
        this.totalDesconto = totalDesconto;
    }

    public BigDecimal getTotalLiquido() {
        return totalLiquido;
    }

    public void setTotalLiquido(BigDecimal totalLiquido) {
        this.totalLiquido = totalLiquido;
    }

    public BigDecimal getBaseFGTS() {
        return baseFGTS;
    }

    public void setBaseFGTS(BigDecimal baseFGTS) {
        this.baseFGTS = baseFGTS;
    }

    public BigDecimal getBaseIRRF() {
        return baseIRRF;
    }

    public void setBaseIRRF(BigDecimal baseIRRF) {
        this.baseIRRF = baseIRRF;
    }

    public BigDecimal getBasePREV() {
        return basePREV;
    }

    public void setBasePREV(BigDecimal basePREV) {
        this.basePREV = basePREV;
    }

    public List<ContraChequeItensVO> getContraChequeItens() {
        return contraChequeItens;
    }

    public void setContraChequeItens(List<ContraChequeItensVO> contraChequeItens) {
        this.contraChequeItens = contraChequeItens;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getContaBancaria() {
        return contaBancaria;
    }

    public void setContaBancaria(String contaBancaria) {
        this.contaBancaria = contaBancaria;
    }

    public String getMesAno() {
        return mesAno;
    }

    public void setMesAno(String mesAno) {
        this.mesAno = mesAno;
    }

    public String getAutenticidade() {
        return autenticidade;
    }

    public void setAutenticidade(String autenticidade) {
        this.autenticidade = autenticidade;
    }

    public InputStream getQrCode() {
        return qrCode;
    }

    public void setQrCode(InputStream qrCode) {
        this.qrCode = qrCode;
    }

    public Long getIdVinculo() {
        return idVinculo;
    }

    public void setIdVinculo(Long idVinculo) {
        this.idVinculo = idVinculo;
    }
}
