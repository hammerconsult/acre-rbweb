/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import org.hibernate.envers.Audited;

@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Pagamento")
public class ConfigPagamento extends ConfiguracaoEvento implements Serializable {

    @OneToMany(mappedBy = "configPagamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Etiqueta(value = "Configuração de Pagamento/Conta de Despesa")
    @Tabelavel
    private List<ConfigPagamentoContaDesp> configPagamentoContaDespesas;
    @Obrigatorio
    @Etiqueta("Tipo de Despesa")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private TipoContaDespesa tipoContaDespesa;

    public ConfigPagamento() {
        this.configPagamentoContaDespesas = new ArrayList<ConfigPagamentoContaDesp>();
    }

    public List<ConfigPagamentoContaDesp> getConfigPagamentoContaDespesas() {
        return configPagamentoContaDespesas;
    }

    public void setConfigPagamentoContaDespesas(List<ConfigPagamentoContaDesp> configPagamentoContaDespesas) {
        this.configPagamentoContaDespesas = configPagamentoContaDespesas;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }
}
