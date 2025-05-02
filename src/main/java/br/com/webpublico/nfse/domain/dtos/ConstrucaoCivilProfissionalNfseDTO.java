package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.TipoProfissionalNfseDTO;

public class ConstrucaoCivilProfissionalNfseDTO implements NfseDTO {

    private Long id;

    private PessoaNfseDTO profissional;

    private TipoProfissionalNfseDTO tipoProfissional;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaNfseDTO getProfissional() {
        return profissional;
    }

    public void setProfissional(PessoaNfseDTO profissional) {
        this.profissional = profissional;
    }

    public TipoProfissionalNfseDTO getTipoProfissional() {
        return tipoProfissional;
    }

    public void setTipoProfissional(TipoProfissionalNfseDTO tipoProfissional) {
        this.tipoProfissional = tipoProfissional;
    }
}
