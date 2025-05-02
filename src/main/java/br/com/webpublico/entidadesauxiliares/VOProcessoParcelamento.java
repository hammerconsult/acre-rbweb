package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoParcelamento;

/**
 * Created by Fabio on 20/07/2018.
 */
public class VOProcessoParcelamento {

    private Long id;
    private Long numero;
    private Long ano;
    private SituacaoParcelamento situacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Long getAno() {
        return ano;
    }

    public void setAno(Long ano) {
        this.ano = ano;
    }

    public SituacaoParcelamento getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoParcelamento situacao) {
        this.situacao = situacao;
    }
}
