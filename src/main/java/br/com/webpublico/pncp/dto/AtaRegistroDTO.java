package br.com.webpublico.pncp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

public class AtaRegistroDTO {

    @JsonIgnore
    private Long id;

    @JsonIgnore
    private String idPncp;

    @JsonIgnore
    private String sequencialIdPncp;

    @NotBlank(message = "O campo 'cnpj' é obrigatório")
    @NotEmpty(message = "O campo 'cnpj' não pode estar vazio")
    @Size(max = 14, message = "O 'cnpj' deve ter no máximo 14 caracteres")
    private String cnpj;

    @NotNull(message = "O campo 'anoCompra' é obrigatório")
    private Integer anoCompra;


    @NotNull(message = "O campo 'sequencialCompra' é obrigatório")
    private String sequencialCompra;

    @NotBlank(message = "O campo 'numeroAtaRegistroPreco' é obrigatório")
    @NotEmpty(message = "O campo 'numeroAtaRegistroPreco' não pode estar vazio")
    @Size(max = 50, message = "O 'numeroAtaRegistroPreco' deve ter no máximo 50 caracteres")
    private String numeroAtaRegistroPreco;

    @NotNull(message = "O campo 'anoAta' é obrigatório")
    private Integer anoAta;


    @NotBlank(message = "O campo 'dataAssinatura' é obrigatório")
    private Date dataAssinatura;

    @NotBlank(message = "O campo 'dataVigenciaInicio' é obrigatório")
    private Date dataVigenciaInicio;

    private Date dataVigenciaFim;

    @JsonIgnore
    private String mensagemDeErros = "";

    @JsonIgnore
    private String codigoUnidadeCompradora;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdPncp() {
        return idPncp;
    }

    public void setIdPncp(String idPncp) {
        this.idPncp = idPncp;
    }

    public String getSequencialIdPncp() {
        return sequencialIdPncp;
    }

    public void setSequencialIdPncp(String sequencialIdPncp) {
        this.sequencialIdPncp = sequencialIdPncp;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Integer getAnoCompra() {
        return anoCompra;
    }

    public void setAnoCompra(Integer anoCompra) {
        this.anoCompra = anoCompra;
    }

    public String getSequencialCompra() {
        return sequencialCompra;
    }

    public void setSequencialCompra(String sequencialCompra) {
        this.sequencialCompra = sequencialCompra;
    }

    public String getNumeroAtaRegistroPreco() {
        return numeroAtaRegistroPreco;
    }

    public void setNumeroAtaRegistroPreco(String numeroAtaRegistroPreco) {
        this.numeroAtaRegistroPreco = numeroAtaRegistroPreco;
    }

    public Integer getAnoAta() {
        return anoAta;
    }

    public void setAnoAta(Integer anoAta) {
        this.anoAta = anoAta;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public Date getDataVigenciaInicio() {
        return dataVigenciaInicio;
    }

    public void setDataVigenciaInicio(Date dataVigenciaInicio) {
        this.dataVigenciaInicio = dataVigenciaInicio;
    }

    public Date getDataVigenciaFim() {
        return dataVigenciaFim;
    }

    public void setDataVigenciaFim(Date dataVigenciaFim) {
        this.dataVigenciaFim = dataVigenciaFim;
    }

    public String getMensagemDeErros() {
        return mensagemDeErros;
    }

    public void setMensagemDeErros(String mensagemDeErros) {
        this.mensagemDeErros = mensagemDeErros;
    }

    public String getCodigoUnidadeCompradora() {
        return codigoUnidadeCompradora;
    }

    public void setCodigoUnidadeCompradora(String codigoUnidadeCompradora) {
        this.codigoUnidadeCompradora = codigoUnidadeCompradora;
    }
}
