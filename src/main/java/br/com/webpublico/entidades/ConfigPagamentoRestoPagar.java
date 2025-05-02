/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

@Entity

@Audited
@Etiqueta("Configuração de Pagamento de Resto a Pagar")
public class ConfigPagamentoRestoPagar extends ConfiguracaoEvento implements Serializable {

    @OneToMany(mappedBy = "configPagamentoRestoPagar", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Etiqueta(value = "Configuração de Pagamento de Resto a Pagar/Conta de Despesa")
    @Tabelavel
    private List<ConfigPagResPagContDesp> configPagResPagContDesp;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Tipo de Restos Processados")
    private TipoRestosProcessado tipoRestosProcessados;
    @Transient
    private Long criadoEm;
    @Obrigatorio
    @Etiqueta("Tipo de Despesa")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    private TipoContaDespesa tipoContaDespesa;

    public ConfigPagamentoRestoPagar() {
        this.configPagResPagContDesp = new ArrayList<ConfigPagResPagContDesp>();
    }

    public List<ConfigPagResPagContDesp> getConfigPagResPagContDesp() {
        return configPagResPagContDesp;
    }

    public void setConfigPagResPagContDesp(List<ConfigPagResPagContDesp> configPagResPagContDesp) {
        this.configPagResPagContDesp = configPagResPagContDesp;
    }

    public TipoRestosProcessado getTipoRestosProcessados() {
        return tipoRestosProcessados;
    }

    public void setTipoRestosProcessados(TipoRestosProcessado tipoRestosProcessados) {
        this.tipoRestosProcessados = tipoRestosProcessados;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }
}
