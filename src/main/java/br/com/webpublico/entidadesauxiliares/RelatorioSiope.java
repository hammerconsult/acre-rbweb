package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LotacaoFuncional;
import br.com.webpublico.enums.TipoDeCargo;

import java.math.BigDecimal;

/**
 * Created by William on 08/02/2018.
 */
public class RelatorioSiope {

    private String mes;
    private String cpf;
    private String nome;
    private String matricula;
    private String codigoInep;
    private String unidade;
    private LotacaoFuncional lotacaoFuncional;
    private Integer cargaHoraria;
    private String categoriaProfissionalSiope;
    private String codigoCategoria;
    private String codigoServicoSiope;
    private String tipoServicoSiope;
    private BigDecimal vencimentoBase;
    private BigDecimal outrasReceitas;
    private BigDecimal valorBruto;
    private TipoDeCargo tipoDeCargo;
    private BigDecimal codigoModalidade;
    private String segmentoAtuacao;

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoInep() {
        return codigoInep;
    }

    public void setCodigoInep(String codigoInep) {
        this.codigoInep = codigoInep;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }


    public String getCategoriaProfissionalSiope() {
        return categoriaProfissionalSiope;
    }

    public void setCategoriaProfissionalSiope(String categoriaProfissionalSiope) {
        this.categoriaProfissionalSiope = categoriaProfissionalSiope;
    }

    public String getCodigoCategoria() {
        return codigoCategoria;
    }

    public void setCodigoCategoria(String codigoCategoria) {
        this.codigoCategoria = codigoCategoria;
    }

    public String getCodigoServicoSiope() {
        return codigoServicoSiope;
    }

    public void setCodigoServicoSiope(String codigoServicoSiope) {
        this.codigoServicoSiope = codigoServicoSiope;
    }

    public String getTipoServicoSiope() {
        return tipoServicoSiope;
    }

    public void setTipoServicoSiope(String tipoServicoSiope) {
        this.tipoServicoSiope = tipoServicoSiope;
    }

    public BigDecimal getVencimentoBase() {
        return vencimentoBase;
    }

    public void setVencimentoBase(BigDecimal vencimentoBase) {
        this.vencimentoBase = vencimentoBase;
    }

    public BigDecimal getOutrasReceitas() {
        return outrasReceitas;
    }

    public void setOutrasReceitas(BigDecimal outrasReceitas) {
        this.outrasReceitas = outrasReceitas;
    }

    public BigDecimal getValorBruto() {
        return valorBruto;
    }

    public void setValorBruto(BigDecimal valorBruto) {
        this.valorBruto = valorBruto;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public TipoDeCargo getTipoDeCargo() {
        return tipoDeCargo;
    }

    public void setTipoDeCargo(TipoDeCargo tipoDeCargo) {
        this.tipoDeCargo = tipoDeCargo;
    }

    public BigDecimal getCodigoModalidade() {
        return codigoModalidade;
    }

    public void setCodigoModalidade(BigDecimal codigoModalidade) {
        this.codigoModalidade = codigoModalidade;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getSegmentoAtuacao() {
        return segmentoAtuacao;
    }

    public void setSegmentoAtuacao(String segmentoAtuacao) {
        this.segmentoAtuacao = segmentoAtuacao;
    }
}
