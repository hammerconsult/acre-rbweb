/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoDiariaContabilizacao;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity

@Audited
@Etiqueta("Configuração de Diária de Campo")
@Inheritance(strategy = InheritanceType.JOINED)
public class ConfigDiariaDeCampo extends ConfiguracaoEvento implements Serializable {

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação")
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    private OperacaoDiariaContabilizacao operacaoDiariaContabilizacao;

    public ConfigDiariaDeCampo() {
        super();
    }

    public OperacaoDiariaContabilizacao getOperacaoDiariaContabilizacao() {
        return operacaoDiariaContabilizacao;
    }

    public void setOperacaoDiariaContabilizacao(OperacaoDiariaContabilizacao operacaoDiariaContabilizacao) {
        this.operacaoDiariaContabilizacao = operacaoDiariaContabilizacao;
    }
}
