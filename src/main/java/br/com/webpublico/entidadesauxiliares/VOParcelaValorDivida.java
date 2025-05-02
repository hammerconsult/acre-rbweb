package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoParcela;

import java.util.Date;

/**
 * Created by Fabio on 17/05/2018.
 */
public class VOParcelaValorDivida {

    private Long idParcela;
    private SituacaoParcela situacaoParcela;
    private Date vencimento;

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }
}
