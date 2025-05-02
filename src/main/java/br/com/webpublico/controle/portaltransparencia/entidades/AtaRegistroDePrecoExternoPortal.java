package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.AtaRegistroPreco;
import br.com.webpublico.entidades.RegistroSolicitacaoMaterialExterno;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;

/**
 * Created by renat on 29/04/2019.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
@Table(name = "ATAREGISTROEXTERNOPORTAL")
public class AtaRegistroDePrecoExternoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @JoinColumn(name = "REGISTROSOLMATEXT_ID")
    private RegistroSolicitacaoMaterialExterno registroSolicitacaoMaterialExterno;

    public AtaRegistroDePrecoExternoPortal() {
    }

    public AtaRegistroDePrecoExternoPortal(RegistroSolicitacaoMaterialExterno registroSolicitacaoMaterialExterno) {
        this.registroSolicitacaoMaterialExterno = registroSolicitacaoMaterialExterno;
        super.setTipo(TipoObjetoPortalTransparencia.ATA_REGISTRO_PRECO_EXTERNO);
    }

    public RegistroSolicitacaoMaterialExterno getRegistroSolicitacaoMaterialExterno() {
        return registroSolicitacaoMaterialExterno;
    }

    public void setRegistroSolicitacaoMaterialExterno(RegistroSolicitacaoMaterialExterno registroSolicitacaoMaterialExterno) {
        this.registroSolicitacaoMaterialExterno = registroSolicitacaoMaterialExterno;
    }

    @Override
    public String toString() {
        return registroSolicitacaoMaterialExterno.toString();
    }
}
