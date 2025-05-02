package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.LinhaEventoSimplesNacionalNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.enums.NaturezaEventoSimplesNacional;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
public class LinhaEventoSimplesNacional extends SuperEntidade implements NfseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private EventoSimplesNacional eventoSimplesNacional;

    private Integer numeroLinha;

    private String erro;

    private String cnpj;

    @Enumerated(EnumType.STRING)
    private NaturezaEventoSimplesNacional natureza;

    private Integer codigoEvento;

    @Temporal(TemporalType.DATE)
    private Date dataFatoMotivador;

    @Temporal(TemporalType.DATE)
    private Date dataEfeito;

    private String numeroProcessoJudicial;

    private String numeroProcessoAdministrativo;

    private String observacoes;

    private Integer codigoUA;

    private String codigoUF;

    private Integer codigoMunicipio;

    @Temporal(TemporalType.DATE)
    private Date dataOcorrencia;

    @Temporal(TemporalType.TIME)
    private Date horaOcorrencia;

    private Integer numeroOpcao;

    @ManyToOne
    private TipoEventoSimplesNacional tipoEventoSimplesNacional;

    @ManyToOne
    private CadastroEconomico cadastroEconomico;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoSimplesNacional getEventoSimplesNacional() {
        return eventoSimplesNacional;
    }

    public void setEventoSimplesNacional(EventoSimplesNacional eventoSimplesNacional) {
        this.eventoSimplesNacional = eventoSimplesNacional;
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

    public NaturezaEventoSimplesNacional getNatureza() {
        return natureza;
    }

    public void setNatureza(NaturezaEventoSimplesNacional natureza) {
        this.natureza = natureza;
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

    public Date getHoraOcorrencia() {
        return horaOcorrencia;
    }

    public void setHoraOcorrencia(Date horaOcorrencia) {
        this.horaOcorrencia = horaOcorrencia;
    }

    public Integer getNumeroOpcao() {
        return numeroOpcao;
    }

    public void setNumeroOpcao(Integer numeroOpcao) {
        this.numeroOpcao = numeroOpcao;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public TipoEventoSimplesNacional getTipoEventoSimplesNacional() {
        return tipoEventoSimplesNacional;
    }

    public void setTipoEventoSimplesNacional(TipoEventoSimplesNacional tipoEventoSimplesNacional) {
        this.tipoEventoSimplesNacional = tipoEventoSimplesNacional;
    }


    @Override
    public LinhaEventoSimplesNacionalNfseDTO toNfseDto() {
        LinhaEventoSimplesNacionalNfseDTO linhaEventoSimplesNacionalNfseDTO = new LinhaEventoSimplesNacionalNfseDTO();
        linhaEventoSimplesNacionalNfseDTO.setId(id);
        linhaEventoSimplesNacionalNfseDTO.setCnpj(cnpj);
        linhaEventoSimplesNacionalNfseDTO.setCodigoEvento(codigoEvento);
        linhaEventoSimplesNacionalNfseDTO.setCodigoMunicipio(codigoMunicipio);
        linhaEventoSimplesNacionalNfseDTO.setCodigoUA(codigoUA);
        linhaEventoSimplesNacionalNfseDTO.setCodigoUF(codigoUF);
        linhaEventoSimplesNacionalNfseDTO.setDataEfeito(dataEfeito);
        linhaEventoSimplesNacionalNfseDTO.setDataFatoMotivador(dataFatoMotivador);
        linhaEventoSimplesNacionalNfseDTO.setErro(erro);
        linhaEventoSimplesNacionalNfseDTO.setDataOcorrencia(dataOcorrencia);
        linhaEventoSimplesNacionalNfseDTO.setEventoSimplesNacionalNfseDTO(eventoSimplesNacional.toNfseDto());
        linhaEventoSimplesNacionalNfseDTO.setNaturezaEventoSimplesNacionalNfseDTO(natureza.toDto());
        linhaEventoSimplesNacionalNfseDTO.setNumeroLinha(numeroLinha);
        linhaEventoSimplesNacionalNfseDTO.setNumeroOpcao(numeroOpcao);
        linhaEventoSimplesNacionalNfseDTO.setNumeroProcessoAdministrativo(numeroProcessoAdministrativo);
        linhaEventoSimplesNacionalNfseDTO.setNumeroProcessoJudicial(numeroProcessoJudicial);
        linhaEventoSimplesNacionalNfseDTO.setObservacoes(observacoes);
        linhaEventoSimplesNacionalNfseDTO.setTipoEventoSimplesNacionalNfseDTO(tipoEventoSimplesNacional.toNfseDto());
        return linhaEventoSimplesNacionalNfseDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LinhaEventoSimplesNacional that = (LinhaEventoSimplesNacional) o;
        return Objects.equals(numeroLinha, that.numeroLinha) &&
            Objects.equals(erro, that.erro) &&
            Objects.equals(cnpj, that.cnpj) &&
            natureza == that.natureza &&
            Objects.equals(codigoEvento, that.codigoEvento) &&
            Objects.equals(dataFatoMotivador, that.dataFatoMotivador) &&
            Objects.equals(dataEfeito, that.dataEfeito) &&
            Objects.equals(numeroProcessoJudicial, that.numeroProcessoJudicial) &&
            Objects.equals(numeroProcessoAdministrativo, that.numeroProcessoAdministrativo) &&
            Objects.equals(observacoes, that.observacoes) &&
            Objects.equals(codigoUA, that.codigoUA) &&
            Objects.equals(codigoUF, that.codigoUF) &&
            Objects.equals(codigoMunicipio, that.codigoMunicipio) &&
            Objects.equals(dataOcorrencia, that.dataOcorrencia) &&
            Objects.equals(horaOcorrencia, that.horaOcorrencia) &&
            Objects.equals(numeroOpcao, that.numeroOpcao) &&
            Objects.equals(tipoEventoSimplesNacional, that.tipoEventoSimplesNacional) &&
            Objects.equals(cadastroEconomico, that.cadastroEconomico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numeroLinha, erro, cnpj, natureza, codigoEvento, dataFatoMotivador, dataEfeito, numeroProcessoJudicial, numeroProcessoAdministrativo, observacoes, codigoUA, codigoUF, codigoMunicipio, dataOcorrencia, horaOcorrencia, numeroOpcao, tipoEventoSimplesNacional, cadastroEconomico);
    }
}
