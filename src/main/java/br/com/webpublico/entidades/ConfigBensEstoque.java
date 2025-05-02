package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOperacaoBensEstoque;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 05/02/14
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */


@Entity

@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@Etiqueta("Configuração de Bens Estoque")
public class ConfigBensEstoque extends ConfiguracaoEvento implements Serializable {

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    @Etiqueta(value = "Operação de Bens Estoque")
    @Enumerated(EnumType.STRING)
    private TipoOperacaoBensEstoque operacaoBensEstoque;

    public TipoOperacaoBensEstoque getOperacaoBensEstoque() {
        return operacaoBensEstoque;
    }

    public void setOperacaoBensEstoque(TipoOperacaoBensEstoque operacaoBensEstoque) {
        this.operacaoBensEstoque = operacaoBensEstoque;
    }
}
