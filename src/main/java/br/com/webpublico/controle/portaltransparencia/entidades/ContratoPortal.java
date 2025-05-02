package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 29/04/2019.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class ContratoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private Contrato contrato;

    public ContratoPortal() {
    }

    public ContratoPortal(Contrato contrato) {
        this.contrato = contrato;
        super.setTipo(TipoObjetoPortalTransparencia.CONTRATO);
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    @Override
    public String toString() {
        return contrato.toString();
    }
}
