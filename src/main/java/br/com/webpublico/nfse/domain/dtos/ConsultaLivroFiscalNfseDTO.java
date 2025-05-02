package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.TipoMovimentoMensalNfseDTO;

public class ConsultaLivroFiscalNfseDTO {

    private Long prestadorId;
    private Integer exercicioInicial;
    private Integer exercicioFinal;
    private Integer mesInicial;
    private Integer mesFinal;
    private TipoMovimentoMensalNfseDTO tipoMovimento;
    private boolean buscarDocumentos;
    private boolean consideraEncerramento;

    public Long getPrestadorId() {
        return prestadorId;
    }

    public void setPrestadorId(Long prestadorId) {
        this.prestadorId = prestadorId;
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

    public TipoMovimentoMensalNfseDTO getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoMensalNfseDTO tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public boolean isBuscarDocumentos() {
        return buscarDocumentos;
    }

    public void setBuscarDocumentos(boolean buscarDocumentos) {
        this.buscarDocumentos = buscarDocumentos;
    }

    public boolean isConsideraEncerramento() {
        return consideraEncerramento;
    }

    public void setConsideraEncerramento(boolean consideraEncerramento) {
        this.consideraEncerramento = consideraEncerramento;
    }
}
