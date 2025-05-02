package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Liquidacao;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class LiquidacaoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Liquidação")
    private Liquidacao liquidacao;

    public LiquidacaoPortal() {
    }

    public LiquidacaoPortal(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
        super.setTipo(TipoObjetoPortalTransparencia.LIQUIDACAO);
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    @Override
    public String toString() {
        return liquidacao.toString();
    }
}
