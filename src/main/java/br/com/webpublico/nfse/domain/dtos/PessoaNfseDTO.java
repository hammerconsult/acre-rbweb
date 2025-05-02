package br.com.webpublico.nfse.domain.dtos;

/**
 * Created by rodolfo on 19/10/17.
 */
public class PessoaNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private Long id;
    private DadosPessoaisNfseDTO dadosPessoais;


    public PessoaNfseDTO() {
    }

    public PessoaNfseDTO(Long id, br.com.webpublico.nfse.domain.dtos.DadosPessoaisNfseDTO dadosPessoais) {
        this.id = id;
        this.dadosPessoais = dadosPessoais;
    }

    public DadosPessoaisNfseDTO getDadosPessoais() {
        return dadosPessoais;
    }

    public void setDadosPessoais(DadosPessoaisNfseDTO dadosPessoais) {
        this.dadosPessoais = dadosPessoais;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PessoaNfseDTO{" +
            "dadosPessoais=" + dadosPessoais +
            '}';
    }
}
