package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoInvestimento;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by mateus on 19/10/17.
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Investimento")
public class ConfigInvestimento extends ConfiguracaoEvento {

    @Tabelavel
    @Etiqueta("Operação")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private OperacaoInvestimento operacaoInvestimento;

    public ConfigInvestimento() {
    }

    public OperacaoInvestimento getOperacaoInvestimento() {
        return operacaoInvestimento;
    }

    public void setOperacaoInvestimento(OperacaoInvestimento operacaoInvestimento) {
        this.operacaoInvestimento = operacaoInvestimento;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
