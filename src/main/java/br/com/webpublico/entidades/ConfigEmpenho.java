package br.com.webpublico.entidades;

import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoReconhecimentoObrigacaoPagar;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 20/06/13
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Empenho de Despesa")
public class ConfigEmpenho extends ConfiguracaoEvento implements Serializable {

    @OneToMany(mappedBy = "configEmpenho", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Tabelavel(campoSelecionado = true)
    @Etiqueta(value = "Conta de Despesa")
    private List<ConfigEmpenhoContaDesp> configEmpenhoContaDespesas;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Reconhecimento")
    @Obrigatorio
    private TipoReconhecimentoObrigacaoPagar tipoReconhecimento;
    @Transient
    @Etiqueta("Conta de Despesa")
    private Conta contaDespesa;
    @Obrigatorio
    @Etiqueta("Tipo de Despesa")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private TipoContaDespesa tipoContaDespesa;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Subtipo de Despesa")
    @Pesquisavel
    private SubTipoDespesa subTipoDespesa;

    public ConfigEmpenho() {
        this.configEmpenhoContaDespesas = Lists.newArrayList();
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

    public List<ConfigEmpenhoContaDesp> getConfigEmpenhoContaDespesas() {
        return configEmpenhoContaDespesas;
    }

    public void setConfigEmpenhoContaDespesas(List<ConfigEmpenhoContaDesp> configEmpenhoContaDespesas) {
        this.configEmpenhoContaDespesas = configEmpenhoContaDespesas;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public SubTipoDespesa getSubTipoDespesa() {
        return subTipoDespesa;
    }

    public void setSubTipoDespesa(SubTipoDespesa subTipoDespesa) {
        this.subTipoDespesa = subTipoDespesa;
    }
}
