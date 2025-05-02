package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.EmpenhoEstorno;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class EmpenhoEstornoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Empenho Estorno")
    private EmpenhoEstorno empenhoEstorno;

    public EmpenhoEstornoPortal() {
    }

    public EmpenhoEstornoPortal(EmpenhoEstorno empenhoEstorno) {
        this.empenhoEstorno = empenhoEstorno;
        super.setTipo(TipoObjetoPortalTransparencia.EMPENHO_ESTORNO);
    }

    public EmpenhoEstorno getEmpenhoEstorno() {
        return empenhoEstorno;
    }

    public void setEmpenhoEstorno(EmpenhoEstorno empenhoEstorno) {
        this.empenhoEstorno = empenhoEstorno;
    }

    @Override
    public String toString() {
        return empenhoEstorno.toString();
    }
}
