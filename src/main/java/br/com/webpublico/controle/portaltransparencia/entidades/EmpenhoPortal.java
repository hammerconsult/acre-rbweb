package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class EmpenhoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Empenho")
    private Empenho empenho;

    public EmpenhoPortal() {
    }

    public EmpenhoPortal(Empenho empenho) {
        this.empenho = empenho;
        super.setTipo(TipoObjetoPortalTransparencia.EMPENHO);
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    @Override
    public String toString() {
        return empenho.toString();
    }
}
