package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RelatorioServidoresPorVerbas implements Serializable {

    private String codigo;
    private String descricao;
    private String nome;
    private String matricula;
    private String contrato;
    private String cargo;
    private String verba;
    private BigDecimal valorReferencia;
    private BigDecimal valor;
    private Date direito;
    private String tipoEvento;
    private String referencia;
    private String codigoVerba;
    private String descricaoVerba;
    private Long idVinculo;
    private Long idItemFicha;

    public String getVerba() {
        return verba;
    }

    public void setVerba(String verba) {
        this.verba = verba;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public BigDecimal getValorReferencia() {
        return valorReferencia;
    }

    public void setValorReferencia(BigDecimal valorReferencia) {
        this.valorReferencia = valorReferencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDireito() {
        return direito;
    }

    public void setDireito(Date direito) {
        this.direito = direito;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getCodigoVerba() {
        return codigoVerba;
    }

    public void setCodigoVerba(String codigoVerba) {
        this.codigoVerba = codigoVerba;
    }

    public String getDescricaoVerba() {
        return descricaoVerba;
    }

    public void setDescricaoVerba(String descricaoVerba) {
        this.descricaoVerba = descricaoVerba;
    }

    public Long getIdVinculo() {
        return idVinculo;
    }

    public void setIdVinculo(Long idVinculo) {
        this.idVinculo = idVinculo;
    }
}
