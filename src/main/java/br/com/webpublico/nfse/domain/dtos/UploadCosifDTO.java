package br.com.webpublico.nfse.domain.dtos;

public class UploadCosifDTO {
    private String file;
    private PrestadorServicoNfseDTO prestador;

    public UploadCosifDTO() {
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public PrestadorServicoNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PrestadorServicoNfseDTO prestador) {
        this.prestador = prestador;
    }
}
