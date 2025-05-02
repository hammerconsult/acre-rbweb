package br.com.webpublico.nfse.domain.dtos;

public class BaixarXMLNfseDTO {

    private Tipo tipo;
    private ConsultaGenericaNfseDTO consultaGenericaNfseDTO;
    private String inscricao;

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public ConsultaGenericaNfseDTO getConsultaGenericaNfseDTO() {
        return consultaGenericaNfseDTO;
    }

    public void setConsultaGenericaNfseDTO(ConsultaGenericaNfseDTO consultaGenericaNfseDTO) {
        this.consultaGenericaNfseDTO = consultaGenericaNfseDTO;
    }

    public String getInscricao() {
        return inscricao;
    }

    public void setInscricao(String inscricao) {
        this.inscricao = inscricao;
    }

    public enum Tipo {
        NORMAL, ENVELOPE_SOAP;
    }
}
