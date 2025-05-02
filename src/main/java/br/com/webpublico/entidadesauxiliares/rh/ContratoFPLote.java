package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.PessoaFisica;

import java.util.Date;

/**
 * @Author peixe on 12/04/2017  14:18.
 */
public class ContratoFPLote {

    private Integer numero;
    private String matricula;
    private String nome;
    private String cpf;
    private String contrato;
    private PessoaFisica pessoaFisica;
    private Date admissao;
    private Date vigencia;
    private String cargo;
    private String codigo;
    private String codigoLotacaoExercicio;
    private String codigoLotacaoFolha;
    private Integer cargaHoraria;

    public ContratoFPLote() {
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

    public Date getVigencia() {
        return vigencia;
    }

    public void setVigencia(Date vigencia) {
        this.vigencia = vigencia;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Date getAdmissao() {
        return admissao;
    }

    public void setAdmissao(Date admissao) {
        this.admissao = admissao;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCodigoLotacaoExercicio() {
        return codigoLotacaoExercicio;
    }

    public void setCodigoLotacaoExercicio(String codigoLotacaoExercicio) {
        this.codigoLotacaoExercicio = codigoLotacaoExercicio;
    }

    public String getCodigoLotacaoFolha() {
        return codigoLotacaoFolha;
    }

    public void setCodigoLotacaoFolha(String codigoLotacaoFolha) {
        this.codigoLotacaoFolha = codigoLotacaoFolha;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    @Override
    public String toString() {
        return "ContratoFPLote{" +
            "numero=" + numero +
            ", nome='" + nome + '\'' +
            ", cpf='" + cpf + '\'' +
            ", pessoaFisica=" + pessoaFisica +
            ", admissao=" + admissao +
            ", vigencia=" + vigencia +
            ", cargo='" + cargo + '\'' +
            ", codigo='" + codigo + '\'' +
            ", codigoLotacaoExercicio='" + codigoLotacaoExercicio + '\'' +
            ", codigoLotacaoFolha='" + codigoLotacaoFolha + '\'' +
            ", cargaHoraria=" + cargaHoraria +
            '}';
    }
}
