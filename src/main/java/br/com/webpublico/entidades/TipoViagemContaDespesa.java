package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoViagem;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 11/09/14
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta(value = "Configuração de Tipo de Vigem/Conta de Despesa")
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class TipoViagemContaDespesa extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfigTipoViagemContaDespesa configTipoViagemContaDesp;
    @ManyToOne
    private ContaDespesa contaDespesa;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Viagem")
    @Obrigatorio
    private TipoViagem tipoViagem;
    @Transient
    private Boolean erroDuranteProcessamento;
    @Transient
    private String mensagemDeErro;

    public TipoViagemContaDespesa() {
    }

    public TipoViagemContaDespesa(ConfigTipoViagemContaDespesa configTipoViagemContaDespesa, ContaDespesa contaDespesa, TipoViagem tipoViagem) {
        this.configTipoViagemContaDesp = configTipoViagemContaDespesa;
        this.contaDespesa = contaDespesa;
        this.tipoViagem = tipoViagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfigTipoViagemContaDespesa getConfigTipoViagemContaDesp() {
        return configTipoViagemContaDesp;
    }

    public void setConfigTipoViagemContaDesp(ConfigTipoViagemContaDespesa configTipoViagemContaDesp) {
        this.configTipoViagemContaDesp = configTipoViagemContaDesp;
    }

    public ContaDespesa getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(ContaDespesa contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public TipoViagem getTipoViagem() {
        return tipoViagem;
    }

    public void setTipoViagem(TipoViagem tipoViagem) {
        this.tipoViagem = tipoViagem;
    }

    public Boolean getErroDuranteProcessamento() {
        return erroDuranteProcessamento != null ? erroDuranteProcessamento : Boolean.FALSE;
    }

    public void setErroDuranteProcessamento(Boolean erroDuranteProcessamento) {
        this.erroDuranteProcessamento = erroDuranteProcessamento;
    }

    public String getMensagemDeErro() {
        return mensagemDeErro;
    }

    public void setMensagemDeErro(String mensagemDeErro) {
        this.mensagemDeErro = mensagemDeErro;
    }

    @Override
    public String toString() {
        return "Tipo de Viagem: <b>" + tipoViagem.getDescricao() + "</b> - Conta de Despesa: <b>" + contaDespesa.toString() + "</b>";
    }
}
