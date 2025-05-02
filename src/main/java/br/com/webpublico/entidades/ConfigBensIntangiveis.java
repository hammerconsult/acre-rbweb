package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOperacaoBensIntangiveis;
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
@Etiqueta("Configuração de Bens Intangíveis")
public class ConfigBensIntangiveis extends ConfiguracaoEvento implements Serializable {

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    @Etiqueta(value = "Operação de Bens Intangíveis")
    @Enumerated(EnumType.STRING)
    private TipoOperacaoBensIntangiveis operacaoBensIntangiveis;

    public TipoOperacaoBensIntangiveis getOperacaoBensIntangiveis() {
        return operacaoBensIntangiveis;
    }

    public void setOperacaoBensIntangiveis(TipoOperacaoBensIntangiveis operacaoBensIntangiveis) {
        this.operacaoBensIntangiveis = operacaoBensIntangiveis;
    }
}

