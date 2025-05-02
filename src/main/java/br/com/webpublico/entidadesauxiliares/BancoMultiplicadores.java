package br.com.webpublico.entidadesauxiliares;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HardRock on 29/05/2017.
 */
public class BancoMultiplicadores {

    private String curso;
    private String modulo;
    private String habilidade;
    private String totalHoras;
    private String instrutor;
    private String matricula;
    private String contrato;
    private String participante;
    private String cargo;
    private String cargaHoraria;
    private String lotacaoFuncional;
    private String telefone;
    private String email;
    private List<BancoMultiplicadores> habilidades;
    private List<BancoMultiplicadores> instrutores;
    private List<BancoMultiplicadores> modulos;

    public BancoMultiplicadores() {
        habilidades = new ArrayList<>();
        instrutores = new ArrayList<>();
        modulos = new ArrayList<>();
    }

    public List<BancoMultiplicadores> getModulos() {
        return modulos;
    }

    public void setModulos(List<BancoMultiplicadores> modulos) {
        this.modulos = modulos;
    }

    public List<BancoMultiplicadores> getInstrutores() {
        return instrutores;
    }

    public void setInstrutores(List<BancoMultiplicadores> instrutores) {
        this.instrutores = instrutores;
    }

    public List<BancoMultiplicadores> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<BancoMultiplicadores> habilidades) {
        this.habilidades = habilidades;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getHabilidade() {
        return habilidade;
    }

    public void setHabilidade(String habilidade) {
        this.habilidade = habilidade;
    }

    public String getTotalHoras() {
        return totalHoras;
    }

    public void setTotalHoras(String totalHoras) {
        this.totalHoras = totalHoras;
    }

    public String getInstrutor() {
        return instrutor;
    }

    public void setInstrutor(String instrutor) {
        this.instrutor = instrutor;
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

    public String getParticipante() {
        return participante;
    }

    public void setParticipante(String participante) {
        this.participante = participante;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(String cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public String getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(String lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
