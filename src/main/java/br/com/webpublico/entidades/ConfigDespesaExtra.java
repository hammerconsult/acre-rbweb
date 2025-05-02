/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Major
 */
@Entity

@Audited
@Etiqueta("Configuração Despesa Extra")
public class ConfigDespesaExtra extends ConfiguracaoEvento implements Serializable {

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo Conta Extra")
    private TipoContaExtraorcamentaria tipoContaExtraorcamentaria;

    public TipoContaExtraorcamentaria getTipoContaExtraorcamentaria() {
        return tipoContaExtraorcamentaria;
    }

    public void setTipoContaExtraorcamentaria(TipoContaExtraorcamentaria tipoContaExtraorcamentaria) {
        this.tipoContaExtraorcamentaria = tipoContaExtraorcamentaria;
    }
}
