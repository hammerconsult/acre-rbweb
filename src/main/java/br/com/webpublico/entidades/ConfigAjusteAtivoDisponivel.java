/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAjusteDisponivel;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author reidocrime
 */

@Table(name = "CONFIGAJUSTEATIVODISP")
@Entity

@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@Etiqueta("Configuração de Ajuste em Ativo Disponível")
public class ConfigAjusteAtivoDisponivel extends ConfiguracaoEvento implements Serializable {

    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Tipo Ajuste em Ativo Disponível")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoAjusteDisponivel tipoAjusteDisponivel;

    public TipoAjusteDisponivel getTipoAjusteDisponivel() {
        return tipoAjusteDisponivel;
    }

    public void setTipoAjusteDisponivel(TipoAjusteDisponivel tipoAjusteDisponivel) {
        this.tipoAjusteDisponivel = tipoAjusteDisponivel;
    }
}
