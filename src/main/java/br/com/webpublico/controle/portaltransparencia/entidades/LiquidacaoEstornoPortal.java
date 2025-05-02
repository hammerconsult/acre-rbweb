package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.LiquidacaoEstorno;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class LiquidacaoEstornoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Liquidação Estorno")
    private LiquidacaoEstorno liquidacaoEstorno;

    public LiquidacaoEstornoPortal() {
    }

    public LiquidacaoEstornoPortal(LiquidacaoEstorno liquidacaoEstorno) {
        this.liquidacaoEstorno = liquidacaoEstorno;
        super.setTipo(TipoObjetoPortalTransparencia.LIQUIDACAO_ESTORNO);
    }

    public LiquidacaoEstorno getLiquidacaoEstorno() {
        return liquidacaoEstorno;
    }

    public void setLiquidacaoEstorno(LiquidacaoEstorno liquidacaoEstorno) {
        this.liquidacaoEstorno = liquidacaoEstorno;
    }

    @Override
    public String toString() {
        return liquidacaoEstorno.toString();
    }
}
