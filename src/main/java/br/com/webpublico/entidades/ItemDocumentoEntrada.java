/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@GrupoDiagrama(nome = "Material")
@Audited
@Entity

@Inheritance(strategy = InheritanceType.JOINED)
public class ItemDocumentoEntrada extends ItemDocumentoFiscal {
    @ManyToOne
    private DocumentoEntrada documentoEntrada;

}
