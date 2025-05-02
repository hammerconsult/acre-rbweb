package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.TipoMovimentoMensalNfseDTO;

import java.io.Serializable;
import java.util.Date;


public class LivroFiscalNfseDTO implements Serializable, NfseDTO {

    private Long id;
    private Integer numero;
    private Integer mesInicial;
    private Integer mesFinal;
    private Integer exercicio;
    private TipoMovimentoMensalNfseDTO tipoMovimento;
    private PrestadorServicoNfseDTO prestador;
    private Date abertura;
    private Date encerramento;

    public LivroFiscalNfseDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Integer mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Integer getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Integer mesFinal) {
        this.mesFinal = mesFinal;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public TipoMovimentoMensalNfseDTO getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoMensalNfseDTO tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public PrestadorServicoNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PrestadorServicoNfseDTO prestador) {
        this.prestador = prestador;
    }

    public Date getAbertura() {
        return abertura;
    }

    public void setAbertura(Date abertura) {
        this.abertura = abertura;
    }

    public Date getEncerramento() {
        return encerramento;
    }

    public void setEncerramento(Date encerramento) {
        this.encerramento = encerramento;
    }
}
