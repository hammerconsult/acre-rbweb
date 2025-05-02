package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 26/09/13
 * Time: 10:22
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Inscrição de Restos a Pagar")
public class ConfigEmpenhoRestoPagar extends ConfiguracaoEvento implements Serializable {

    @OneToMany(mappedBy = "configEmpenhoResto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Tabelavel
    @Etiqueta("Configuração de Empenho Restos a Pagar")
    private List<ConfigEmpenhoRestoContaDesp> configEmpenhoRestoContaDespesas;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Tipo de Resto")
    private TipoRestosProcessado tipoRestosProcessados;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Em Liquidação?")
    private Boolean emLiquidacao;

    public ConfigEmpenhoRestoPagar() {
        configEmpenhoRestoContaDespesas = Lists.newArrayList();
        emLiquidacao = Boolean.FALSE;
    }

    public List<ConfigEmpenhoRestoContaDesp> getConfigEmpenhoRestoContaDespesas() {
        return configEmpenhoRestoContaDespesas;
    }

    public void setConfigEmpenhoRestoContaDespesas(List<ConfigEmpenhoRestoContaDesp> configEmpenhoRestoContaDespesas) {
        this.configEmpenhoRestoContaDespesas = configEmpenhoRestoContaDespesas;
    }

    public TipoRestosProcessado getTipoRestosProcessados() {
        return tipoRestosProcessados;
    }

    public void setTipoRestosProcessados(TipoRestosProcessado tipoRestosProcessados) {
        this.tipoRestosProcessados = tipoRestosProcessados;
    }

    public Boolean getEmLiquidacao() {
        return emLiquidacao == null ? Boolean.FALSE : emLiquidacao;
    }

    public void setEmLiquidacao(Boolean emLiquidacao) {
        this.emLiquidacao = emLiquidacao;
    }
}
