package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOperacaoBensEstoque;
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
 * User: Desenvolvimento
 * Date: 06/03/14
 * Time: 09:08
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Configuração de Transferência de Bens de Estoque")
public class ConfigTransfEstoque extends ConfiguracaoEvento implements Serializable {


    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação")
    private TipoOperacaoBensEstoque tipoOperacaoBensEstoque;

    public ConfigTransfEstoque() {
    }

    public TipoOperacaoBensEstoque getTipoOperacaoBensEstoque() {
        return tipoOperacaoBensEstoque;
    }

    public void setTipoOperacaoBensEstoque(TipoOperacaoBensEstoque tipoOperacaoBensEstoque) {
        this.tipoOperacaoBensEstoque = tipoOperacaoBensEstoque;
    }
}

