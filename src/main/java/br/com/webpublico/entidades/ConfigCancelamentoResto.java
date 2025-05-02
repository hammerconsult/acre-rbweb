package br.com.webpublico.entidades;

import br.com.webpublico.enums.PatrimonioLiquido;
import br.com.webpublico.enums.TipoEmpenhoEstorno;
import br.com.webpublico.enums.TipoRestosProcessado;
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
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 19/11/14
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Cancelamento Restos a Pagar")
public class ConfigCancelamentoResto extends ConfiguracaoEvento implements Serializable {

    @OneToMany(mappedBy = "configCancelamentoResto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Tabelavel
    @Etiqueta("Conta")
    private List<ConfigCancRestoContaDesp> configCancelamentoRestoContaDespesas;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Tipo de Resto")
    private TipoRestosProcessado tipoRestosProcessados;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Cancelamento/Prescrição")
    @Pesquisavel
    @Tabelavel
    private TipoEmpenhoEstorno cancelamentoPrescricao;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Patrimonio Líquido")
    private br.com.webpublico.enums.PatrimonioLiquido patrimonioLiquido;

    public ConfigCancelamentoResto() {
        this.configCancelamentoRestoContaDespesas = new ArrayList<>();
    }

    public List<ConfigCancRestoContaDesp> getConfigCancelamentoRestoContaDespesas() {
        return configCancelamentoRestoContaDespesas;
    }

    public void setConfigCancelamentoRestoContaDespesas(List<ConfigCancRestoContaDesp> configCancelamentoRestoContaDespesas) {
        this.configCancelamentoRestoContaDespesas = configCancelamentoRestoContaDespesas;
    }

    public TipoRestosProcessado getTipoRestosProcessados() {
        return tipoRestosProcessados;
    }

    public void setTipoRestosProcessados(TipoRestosProcessado tipoRestosProcessados) {
        this.tipoRestosProcessados = tipoRestosProcessados;
    }

    public TipoEmpenhoEstorno getCancelamentoPrescricao() {
        return cancelamentoPrescricao;
    }

    public void setCancelamentoPrescricao(TipoEmpenhoEstorno tipoEmpenhoEstorno) {
        this.cancelamentoPrescricao = tipoEmpenhoEstorno;
    }

    public br.com.webpublico.enums.PatrimonioLiquido getPatrimonioLiquido() {
        return patrimonioLiquido;
    }

    public void setPatrimonioLiquido(PatrimonioLiquido patrimonioLiquido) {
        this.patrimonioLiquido = patrimonioLiquido;
    }
}
