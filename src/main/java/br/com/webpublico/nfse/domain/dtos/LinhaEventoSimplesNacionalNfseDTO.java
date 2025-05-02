package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.NaturezaEventoSimplesNacionalNfseDTO;

import java.util.Date;

public class LinhaEventoSimplesNacionalNfseDTO implements NfseDTO {

    private Long id;

    private EventoSimplesNacionalNfseDTO eventoSimplesNacionalNfseDTO;

    private Integer numeroLinha;

    private String erro;

    private String cnpj;

    private NaturezaEventoSimplesNacionalNfseDTO naturezaEventoSimplesNacionalNfseDTO;

    private Integer codigoEvento;

    private Date dataFatoMotivador;

    private Date dataEfeito;

    private String numeroProcessoJudicial;

    private String numeroProcessoAdministrativo;

    private String observacoes;

    private Integer codigoUA;

    private String codigoUF;

    private Integer codigoMunicipio;

    private Date dataOcorrencia;

    private Integer numeroOpcao;

    private PrestadorServicoNfseDTO prestadorServicoNfseDTO;

    private TipoEventoSimplesNacionalNfseDTO tipoEventoSimplesNacionalNfseDTO;


    public LinhaEventoSimplesNacionalNfseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoSimplesNacionalNfseDTO getEventoSimplesNacionalNfseDTO() {
        return eventoSimplesNacionalNfseDTO;
    }

    public void setEventoSimplesNacionalNfseDTO(EventoSimplesNacionalNfseDTO eventoSimplesNacionalNfseDTO) {
        this.eventoSimplesNacionalNfseDTO = eventoSimplesNacionalNfseDTO;
    }

    public Integer getNumeroLinha() {
        return numeroLinha;
    }

    public void setNumeroLinha(Integer numeroLinha) {
        this.numeroLinha = numeroLinha;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public NaturezaEventoSimplesNacionalNfseDTO getNaturezaEventoSimplesNacionalNfseDTO() {
        return naturezaEventoSimplesNacionalNfseDTO;
    }

    public void setNaturezaEventoSimplesNacionalNfseDTO(NaturezaEventoSimplesNacionalNfseDTO naturezaEventoSimplesNacionalNfseDTO) {
        this.naturezaEventoSimplesNacionalNfseDTO = naturezaEventoSimplesNacionalNfseDTO;
    }

    public Integer getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(Integer codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public Date getDataFatoMotivador() {
        return dataFatoMotivador;
    }

    public void setDataFatoMotivador(Date dataFatoMotivador) {
        this.dataFatoMotivador = dataFatoMotivador;
    }

    public Date getDataEfeito() {
        return dataEfeito;
    }

    public void setDataEfeito(Date dataEfeito) {
        this.dataEfeito = dataEfeito;
    }

    public String getNumeroProcessoJudicial() {
        return numeroProcessoJudicial;
    }

    public void setNumeroProcessoJudicial(String numeroProcessoJudicial) {
        this.numeroProcessoJudicial = numeroProcessoJudicial;
    }

    public String getNumeroProcessoAdministrativo() {
        return numeroProcessoAdministrativo;
    }

    public void setNumeroProcessoAdministrativo(String numeroProcessoAdministrativo) {
        this.numeroProcessoAdministrativo = numeroProcessoAdministrativo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Integer getCodigoUA() {
        return codigoUA;
    }

    public void setCodigoUA(Integer codigoUA) {
        this.codigoUA = codigoUA;
    }

    public String getCodigoUF() {
        return codigoUF;
    }

    public void setCodigoUF(String codigoUF) {
        this.codigoUF = codigoUF;
    }

    public Integer getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(Integer codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public Integer getNumeroOpcao() {
        return numeroOpcao;
    }

    public void setNumeroOpcao(Integer numeroOpcao) {
        this.numeroOpcao = numeroOpcao;
    }

    public PrestadorServicoNfseDTO getPrestadorServicoNfseDTO() {
        return prestadorServicoNfseDTO;
    }

    public void setPrestadorServicoNfseDTO(PrestadorServicoNfseDTO prestadorServicoNfseDTO) {
        this.prestadorServicoNfseDTO = prestadorServicoNfseDTO;
    }

    public TipoEventoSimplesNacionalNfseDTO getTipoEventoSimplesNacionalNfseDTO() {
        return tipoEventoSimplesNacionalNfseDTO;
    }

    public void setTipoEventoSimplesNacionalNfseDTO(TipoEventoSimplesNacionalNfseDTO tipoEventoSimplesNacionalNfseDTO) {
        this.tipoEventoSimplesNacionalNfseDTO = tipoEventoSimplesNacionalNfseDTO;
    }
}
