package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOperacaoBensMoveis;
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
@Etiqueta("Configuração de Bens Móveis")
public class ConfigBensMoveis extends ConfiguracaoEvento implements Serializable {

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    @Etiqueta(value = "Operação de Bens Móveis")
    @Enumerated(EnumType.STRING)
    private TipoOperacaoBensMoveis operacaoBensMoveis;

    public TipoOperacaoBensMoveis getOperacaoBensMoveis() {
        return operacaoBensMoveis;
    }

    public void setOperacaoBensMoveis(TipoOperacaoBensMoveis operacaoBensMoveis) {
        this.operacaoBensMoveis = operacaoBensMoveis;
    }
}
