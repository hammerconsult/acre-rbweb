package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.ConvenioDespesa;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 17/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class ConvenioDespesaPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private ConvenioDespesa convenioDespesa;

    public ConvenioDespesaPortal() {
    }

    public ConvenioDespesaPortal(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
        super.setTipo(TipoObjetoPortalTransparencia.CONVENIO_DESPESA);
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
    }

    @Override
    public String toString() {
        return convenioDespesa.toString();
    }
}
