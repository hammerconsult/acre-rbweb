/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoSaidaMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Renato
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Saída de Material por Desincorporação")
@Table(name = "SAIDAMATDESINCORPORACAO")
public class SaidaMaterialDesincorporacao extends SaidaMaterial {

    @Override
    public TipoSaidaMaterial getTipoDestaSaida() {
        return TipoSaidaMaterial.DESINCORPORACAO;
    }
}
