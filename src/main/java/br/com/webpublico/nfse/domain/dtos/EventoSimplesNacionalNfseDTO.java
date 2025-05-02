package br.com.webpublico.nfse.domain.dtos;

import java.util.Date;

public class EventoSimplesNacionalNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private String usuarioImportacao;
    private Date dataImportacao;

    public EventoSimplesNacionalNfseDTO() {
    }

    public String getUsuarioImportacao() {
        return usuarioImportacao;
    }

    public void setUsuarioImportacao(String usuarioImportacao) {
        this.usuarioImportacao = usuarioImportacao;
    }

    public Date getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(Date dataImportacao) {
        this.dataImportacao = dataImportacao;
    }
}
