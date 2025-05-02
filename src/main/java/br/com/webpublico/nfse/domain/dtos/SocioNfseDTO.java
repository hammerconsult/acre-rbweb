package br.com.webpublico.nfse.domain.dtos;

/**
 * Created by rodolfo on 27/10/17.
 */
public class SocioNfseDTO implements NfseDTO {

    private Double proporcao;
    private PessoaNfseDTO socio;

    public SocioNfseDTO() {
    }

    public SocioNfseDTO(Double proporcao, PessoaNfseDTO socio) {
        this.proporcao = proporcao;
        this.socio = socio;
    }

    public Double getProporcao() {
        return proporcao;
    }

    public void setProporcao(Double proporcao) {
        this.proporcao = proporcao;
    }

    public PessoaNfseDTO getSocio() {
        return socio;
    }

    public void setSocio(PessoaNfseDTO socio) {
        this.socio = socio;
    }
}
