/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoConsignacao;
import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Claudio
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Receita Extra")
public class ConfigReceitaExtra extends ConfiguracaoEvento implements Serializable {

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Conta Extra")
    @Tabelavel
    @Pesquisavel
    private TipoContaExtraorcamentaria tipoContaExtraorcamentaria;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Consignação")
    @Tabelavel
    @Pesquisavel
    private TipoConsignacao tipoConsignacao;

    public TipoContaExtraorcamentaria getTipoContaExtraorcamentaria() {
        return tipoContaExtraorcamentaria;
    }

    public void setTipoContaExtraorcamentaria(TipoContaExtraorcamentaria tipoContaExtraorcamentaria) {
        this.tipoContaExtraorcamentaria = tipoContaExtraorcamentaria;
    }

    public TipoConsignacao getTipoConsignacao() {
        return tipoConsignacao;
    }

    public void setTipoConsignacao(TipoConsignacao tipoConsignacao) {
        this.tipoConsignacao = tipoConsignacao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
