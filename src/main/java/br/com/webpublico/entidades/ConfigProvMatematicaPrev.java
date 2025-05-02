/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOperacaoAtuarial;
import br.com.webpublico.enums.TipoPlano;
import br.com.webpublico.enums.TipoProvisao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
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
@Etiqueta("Configuração de Provisão Matemática")
public class ConfigProvMatematicaPrev extends ConfiguracaoEvento implements Serializable {

    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Tipo de Operação")
    @Obrigatorio
    @Tabelavel
    private TipoOperacaoAtuarial tipoOperacaoAtuarial;
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Tipo de Plano")
    @Obrigatorio
    @Tabelavel
    private TipoPlano tipoPlano;
    @Obrigatorio
    @Etiqueta(value = "Tipo de Provisão")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    private TipoProvisao tipoProvisao;

    public TipoOperacaoAtuarial getTipoOperacaoAtuarial() {
        return tipoOperacaoAtuarial;
    }

    public void setTipoOperacaoAtuarial(TipoOperacaoAtuarial tipoOperacaoAtuarial) {
        this.tipoOperacaoAtuarial = tipoOperacaoAtuarial;
    }

    public TipoPlano getTipoPlano() {
        return tipoPlano;
    }

    public void setTipoPlano(TipoPlano tipoPlano) {
        this.tipoPlano = tipoPlano;
    }

    public TipoProvisao getTipoProvisao() {
        return tipoProvisao;
    }

    public void setTipoProvisao(TipoProvisao tipoProvisao) {
        this.tipoProvisao = tipoProvisao;
    }
}
