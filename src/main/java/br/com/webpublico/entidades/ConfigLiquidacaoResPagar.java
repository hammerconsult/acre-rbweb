/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoReconhecimentoObrigacaoPagar;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Liquidação de Resto a Pagar")
public class ConfigLiquidacaoResPagar extends ConfiguracaoEvento implements Serializable {

    @OneToMany(mappedBy = "configLiquidacaoResPagar", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Tabelavel(campoSelecionado = true)
    @Etiqueta(value = "Configuração de Liquidação de Resto a Pagar/Conta de Despesa")
    private List<ConfigLiqResPagContDesp> configLiqResPagContDesp;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta(value = "Sub-Tipo de Despesa")
    @Pesquisavel
    private SubTipoDespesa subTipoDespesa;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Reconhecimento")
    private TipoReconhecimentoObrigacaoPagar tipoReconhecimento;

    public ConfigLiquidacaoResPagar() {
        super();
        this.configLiqResPagContDesp = Lists.newArrayList();
    }

    public List<ConfigLiqResPagContDesp> getConfigLiqResPagContDesp() {
        return configLiqResPagContDesp;
    }

    public void setConfigLiqResPagContDesp(List<ConfigLiqResPagContDesp> configLiqResPagContDesp) {
        this.configLiqResPagContDesp = configLiqResPagContDesp;
    }

    public SubTipoDespesa getSubTipoDespesa() {
        return subTipoDespesa;
    }

    public void setSubTipoDespesa(SubTipoDespesa subTipoDespesa) {
        this.subTipoDespesa = subTipoDespesa;
    }

    public TipoReconhecimentoObrigacaoPagar getTipoReconhecimento() {
        return tipoReconhecimento;
    }

    public void setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar tipoReconhecimento) {
        this.tipoReconhecimento = tipoReconhecimento;
    }
}
