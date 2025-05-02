package br.com.webpublico.nfse.domain.dtos;


import java.io.Serializable;

/**
 * Created by william on 13/09/17.
 */
public class ConfiguracaoGeralNfseDTO implements Serializable {

    private Long id;
    private Boolean obrigatorioAidfe;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getObrigatorioAidfe() {
        return obrigatorioAidfe;
    }

    public void setObrigatorioAidfe(Boolean obrigatorioAidfe) {
        this.obrigatorioAidfe = obrigatorioAidfe;
    }

}
