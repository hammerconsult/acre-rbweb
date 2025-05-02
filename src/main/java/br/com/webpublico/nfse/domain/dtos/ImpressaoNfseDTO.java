package br.com.webpublico.nfse.domain.dtos;

/**
 * Created by rodolfo on 15/02/18.
 */
public class ImpressaoNfseDTO {

    private NotaFiscalNfseDTO notaFiscal;
    private String conteudo;

    public ImpressaoNfseDTO() {
    }

    public NotaFiscalNfseDTO getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(NotaFiscalNfseDTO notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
