package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOperacaoBensMoveis;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 18/02/14
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Configuração de Transferência de Bens Móveis")
public class ConfigTransfBensMoveis extends ConfiguracaoEvento implements Serializable {

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Operação")
    private TipoOperacaoBensMoveis tipoOperacaoBensMoveis;

    public ConfigTransfBensMoveis() {
    }

    public TipoOperacaoBensMoveis getTipoOperacaoBensMoveis() {
        return tipoOperacaoBensMoveis;
    }

    public void setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis tipoOperacaoBensMoveis) {
        this.tipoOperacaoBensMoveis = tipoOperacaoBensMoveis;
    }
}
