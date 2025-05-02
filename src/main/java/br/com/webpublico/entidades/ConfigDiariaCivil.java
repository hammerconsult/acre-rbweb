/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoDiariaContabilizacao;
import br.com.webpublico.util.anotacoes.ErroReprocessamentoContabil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Usuario
 */
@Entity

@Audited
@Etiqueta("Configuração Diaria Cívil")
@Inheritance(strategy = InheritanceType.JOINED)
public class ConfigDiariaCivil extends ConfiguracaoEvento implements Serializable {

    @ErroReprocessamentoContabil
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Operação")
    @Tabelavel
    private OperacaoDiariaContabilizacao operacaoDiariaContabilizacao;

    public ConfigDiariaCivil() {
        super();
    }

    public OperacaoDiariaContabilizacao getOperacaoDiariaContabilizacao() {
        return operacaoDiariaContabilizacao;
    }

    public void setOperacaoDiariaContabilizacao(OperacaoDiariaContabilizacao operacaoDiariaContabilizacao) {
        this.operacaoDiariaContabilizacao = operacaoDiariaContabilizacao;
    }
}
