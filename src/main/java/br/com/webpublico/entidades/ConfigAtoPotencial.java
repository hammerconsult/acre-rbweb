package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAtoPotencial;
import br.com.webpublico.enums.TipoOperacaoAtoPotencial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity

@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@Etiqueta("Configuração de Ato Potencial")
public class ConfigAtoPotencial extends ConfiguracaoEvento implements Serializable {

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Ato Potencial")
    @Tabelavel
    @Pesquisavel
    private TipoAtoPotencial tipoAtoPotencial;
    @Etiqueta("Operação")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private TipoOperacaoAtoPotencial tipoOperacaoAtoPotencial;

    public ConfigAtoPotencial() {
        super();
    }

    public TipoOperacaoAtoPotencial getTipoOperacaoAtoPotencial() {
        return tipoOperacaoAtoPotencial;
    }

    public void setTipoOperacaoAtoPotencial(TipoOperacaoAtoPotencial tipoOperacaoAtoPotencial) {
        this.tipoOperacaoAtoPotencial = tipoOperacaoAtoPotencial;
    }

    public TipoAtoPotencial getTipoAtoPotencial() {
        return tipoAtoPotencial;
    }

    public void setTipoAtoPotencial(TipoAtoPotencial tipoAtoPotencial) {
        this.tipoAtoPotencial = tipoAtoPotencial;
    }
}
