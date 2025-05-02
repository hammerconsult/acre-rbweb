
package br.com.webpublico.negocios.tributario.auxiliares;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiltroConsultaDebitos implements Serializable {

    private String cpf;
    private String tipoConsulta;
    private Long idCadastro;
    private Integer exercicioInicial;
    private Integer exercicioFinal;
    private Long idDivida;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private List<BigDecimal> idsCadastros;
    protected Integer inicio;
    protected Integer max;

    public FiltroConsultaDebitos() {
    }

    public FiltroConsultaDebitos(String cpf, Integer inicio, Integer max) {
        this.cpf = cpf;
        this.inicio = inicio;
        this.max = max;
    }

    public FiltroConsultaDebitos(String cpf) {
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }

    public Integer getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Integer exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Integer getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Integer exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Long getIdDivida() {
        return idDivida;
    }

    public void setIdDivida(Long idDivida) {
        this.idDivida = idDivida;
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Integer getInicio() {
        return inicio;
    }

    public void setInicio(Integer inicio) {
        this.inicio = inicio;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public List<BigDecimal> getIdsCadastros() {
        return idsCadastros;
    }

    public void setIdsCadastros(List<BigDecimal> idsCadastros) {
        this.idsCadastros = idsCadastros;
    }
}
