package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;

public class SubstituicaoNfseDTO implements Serializable {

    private CancelamentoNfseDTO cancelar;
    private RpsNfseDTO rpsSubstituido;
    private NotaFiscalNfseDTO notaFiscal;

    public SubstituicaoNfseDTO() {
    }

    public SubstituicaoNfseDTO(CancelamentoNfseDTO cancelar, br.com.webpublico.nfse.domain.dtos.RpsNfseDTO rpsSubstituido, br.com.webpublico.nfse.domain.dtos.NotaFiscalNfseDTO notaFiscal) {
        this.cancelar = cancelar;
        this.rpsSubstituido = rpsSubstituido;
        this.notaFiscal = notaFiscal;
    }

    public CancelamentoNfseDTO getCancelar() {
        return cancelar;
    }

    public void setCancelar(CancelamentoNfseDTO cancelar) {
        this.cancelar = cancelar;
    }

    public RpsNfseDTO getRpsSubstituido() {
        return rpsSubstituido;
    }

    public void setRpsSubstituido(RpsNfseDTO rpsSubstituido) {
        this.rpsSubstituido = rpsSubstituido;
    }

    public NotaFiscalNfseDTO getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(NotaFiscalNfseDTO notaFiscal) {
        this.notaFiscal = notaFiscal;
    }
}
