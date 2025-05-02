package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemInscricaoDividaAtiva;

/**
 * Created by Fabio on 23/05/2018.
 */
public class VOItemCertidaoDividaAtiva {

    private Long idCertidao;
    private Long idItemCertidao;
    private Long idItemInscricao;
    private ItemInscricaoDividaAtiva.Situacao situacaoItemInscricao;

    public Long getIdCertidao() {
        return idCertidao;
    }

    public void setIdCertidao(Long idCertidao) {
        this.idCertidao = idCertidao;
    }

    public Long getIdItemInscricao() {
        return idItemInscricao;
    }

    public void setIdItemInscricao(Long idItemInscricao) {
        this.idItemInscricao = idItemInscricao;
    }

    public ItemInscricaoDividaAtiva.Situacao getSituacaoItemInscricao() {
        return situacaoItemInscricao;
    }

    public void setSituacaoItemInscricao(ItemInscricaoDividaAtiva.Situacao situacaoItemInscricao) {
        this.situacaoItemInscricao = situacaoItemInscricao;
    }

    public Long getIdItemCertidao() {
        return idItemCertidao;
    }

    public void setIdItemCertidao(Long idItemCertidao) {
        this.idItemCertidao = idItemCertidao;
    }
}
