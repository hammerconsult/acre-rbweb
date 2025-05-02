package br.com.webpublico.entidades;

import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoReconhecimentoObrigacaoPagar;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 21/06/13
 * Time: 09:39
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Liquidação")
public class ConfigLiquidacao extends ConfiguracaoEvento implements Serializable {

    @OneToMany(mappedBy = "configLiquidacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Tabelavel(campoSelecionado = true)
    @Etiqueta(value = "Conta de Despesa")
    private List<ConfigLiquidacaoContaDesp> configLiquidacaoContaDespesas;
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
    @Transient
    @Etiqueta("Conta de Despesa")
    private Conta contaDespesa;

    public ConfigLiquidacao() {
        this.configLiquidacaoContaDespesas = new ArrayList<ConfigLiquidacaoContaDesp>();
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public TipoReconhecimentoObrigacaoPagar getTipoReconhecimento() {
        return tipoReconhecimento;
    }

    public void setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar tipoReconhecimento) {
        this.tipoReconhecimento = tipoReconhecimento;
    }

    public List<ConfigLiquidacaoContaDesp> getConfigLiquidacaoContaDespesas() {
        return configLiquidacaoContaDespesas;
    }

    public void setConfigLiquidacaoContaDespesas(List<ConfigLiquidacaoContaDesp> configLiquidacaoContaDespesas) {
        this.configLiquidacaoContaDespesas = configLiquidacaoContaDespesas;
    }

    public SubTipoDespesa getSubTipoDespesa() {
        return subTipoDespesa;
    }

    public void setSubTipoDespesa(SubTipoDespesa subTipoDespesa) {
        this.subTipoDespesa = subTipoDespesa;
    }
}
