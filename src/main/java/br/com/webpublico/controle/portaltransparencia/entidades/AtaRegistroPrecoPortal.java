package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.AtaRegistroPreco;
import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 29/04/2019.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class AtaRegistroPrecoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private AtaRegistroPreco ataRegistroPreco;

    public AtaRegistroPrecoPortal() {
    }

    public AtaRegistroPrecoPortal(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
        super.setTipo(TipoObjetoPortalTransparencia.ATA_REGISTRO_PRECO);
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    @Override
    public String toString() {
        return ataRegistroPreco.toString();
    }
}
