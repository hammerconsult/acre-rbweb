package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.ReceitaORCEstorno;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class ReceitaOrcEstornoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private ReceitaORCEstorno receitaORCEstorno;

    public ReceitaOrcEstornoPortal() {
    }

    public ReceitaOrcEstornoPortal(ReceitaORCEstorno receitaORCEstorno) {
        this.receitaORCEstorno = receitaORCEstorno;
        super.setTipo(TipoObjetoPortalTransparencia.RECEITA_REALIZADA_ESTORNO);
    }

    public ReceitaORCEstorno getReceitaORCEstorno() {
        return receitaORCEstorno;
    }

    public void setReceitaORCEstorno(ReceitaORCEstorno receitaORCEstorno) {
        this.receitaORCEstorno = receitaORCEstorno;
    }

    @Override
    public String toString() {
        return receitaORCEstorno.toString();
    }
}
