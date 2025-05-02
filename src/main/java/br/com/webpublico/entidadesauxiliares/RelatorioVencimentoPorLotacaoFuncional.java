package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LotacaoFuncional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by William on 05/04/2017.
 */
public class RelatorioVencimentoPorLotacaoFuncional {

    private BigDecimal vinculoId;
    private String matricula;
    private String nome;
    private String cpf;
    private Date dataAdmissao;
    private BigDecimal cargaHoraria;
    private String tipoCargo;
    private String cargo;
    private String unidade;
    private BigDecimal vencimentoBase;
    private BigDecimal vencimentoBruto;
    private BigDecimal valorLiquido;

    private BigDecimal outrasReceitas;
    private BigDecimal valorDesconto;
    private LotacaoFuncional lotacaoFuncional;
    private String nomeGrupo;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public BigDecimal getVencimentoBase() {
        return vencimentoBase;
    }

    public void setVencimentoBase(BigDecimal vencimentoBase) {
        this.vencimentoBase = vencimentoBase;
    }

    public BigDecimal getVencimentoBruto() {
        return vencimentoBruto;
    }

    public void setVencimentoBruto(BigDecimal vencimentoBruto) {
        this.vencimentoBruto = vencimentoBruto;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public BigDecimal getVinculoId() {
        return vinculoId;
    }

    public void setVinculoId(BigDecimal vinculoId) {
        this.vinculoId = vinculoId;
    }

    public BigDecimal getValorLiquido() {
        return vencimentoBruto.subtract(valorDesconto);
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public Date getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(Date dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public BigDecimal getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(BigDecimal cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public String getTipoCargo() {
        return tipoCargo;
    }

    public void setTipoCargo(String tipoCargo) {
        this.tipoCargo = tipoCargo;
    }

    public BigDecimal getOutrasReceitas() {
        return outrasReceitas;
    }

    public void setOutrasReceitas(BigDecimal outrasReceitas) {
        this.outrasReceitas = outrasReceitas;
    }
}

