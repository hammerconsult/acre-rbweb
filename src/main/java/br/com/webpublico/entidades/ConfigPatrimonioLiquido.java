package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoPatrimonioLiquido;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by mga on 18/10/2017.
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Patrimônio Líquido")
public class ConfigPatrimonioLiquido extends ConfiguracaoEvento{

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Operação Patrimônio Líquido")
    private OperacaoPatrimonioLiquido operacaoPatrimonioLiquido;

    public OperacaoPatrimonioLiquido getOperacaoPatrimonioLiquido() {
        return operacaoPatrimonioLiquido;
    }

    public void setOperacaoPatrimonioLiquido(OperacaoPatrimonioLiquido operacaoPatrimonioLiquido) {
        this.operacaoPatrimonioLiquido = operacaoPatrimonioLiquido;
    }
}
