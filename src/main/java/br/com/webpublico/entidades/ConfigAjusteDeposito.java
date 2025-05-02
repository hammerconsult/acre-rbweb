/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAjuste;
import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author wiplash
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Ajuste em Depósito")
public class ConfigAjusteDeposito extends ConfiguracaoEvento implements Serializable {

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Ajuste")
    private TipoAjuste tipoAjuste;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo Conta Extra")
    private TipoContaExtraorcamentaria tipoContaExtraorcamentaria;

    public TipoAjuste getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(TipoAjuste tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public TipoContaExtraorcamentaria getTipoContaExtraorcamentaria() {
        return tipoContaExtraorcamentaria;
    }

    public void setTipoContaExtraorcamentaria(TipoContaExtraorcamentaria tipoContaExtraorcamentaria) {
        this.tipoContaExtraorcamentaria = tipoContaExtraorcamentaria;
    }
}
