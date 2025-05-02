/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author major
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Receita Realizada")
public class ConfigReceitaRealizada extends ConfiguracaoEvento implements Serializable {

    @OneToMany(mappedBy = "configReceitaRealizada", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Tabelavel(campoSelecionado = true)
    @Etiqueta(value = "Conta de Receita")
    private List<ConfigRecRealizadaContaRec> configRecRealizadaContaRecs;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Operação")
    private OperacaoReceita operacaoReceitaRealizada;

    public ConfigReceitaRealizada() {
        configRecRealizadaContaRecs = new ArrayList<>();
    }

    public List<ConfigRecRealizadaContaRec> getConfigRecRealizadaContaRecs() {
        return configRecRealizadaContaRecs;
    }

    public void setConfigRecRealizadaContaRecs(List<ConfigRecRealizadaContaRec> configRecRealizadaContaRecs) {
        this.configRecRealizadaContaRecs = configRecRealizadaContaRecs;
    }

    public OperacaoReceita getOperacaoReceitaRealizada() {
        return operacaoReceitaRealizada;
    }

    public void setOperacaoReceitaRealizada(OperacaoReceita operacaoReceitaRealizada) {
        this.operacaoReceitaRealizada = operacaoReceitaRealizada;
    }
}
