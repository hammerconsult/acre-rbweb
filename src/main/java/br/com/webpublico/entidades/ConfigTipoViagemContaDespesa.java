package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaDespesa;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 11/09/14
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta(value = "Configuração Tipo de Viagem/Conta de Despesa")
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Table(name = "CONFIGTIPOVIAGEMCONTADESP")
public class ConfigTipoViagemContaDespesa extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início de Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Fim de Vigência")
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;
    @Tabelavel
    @Etiqueta("Configuração de Tipo de Viagem/Conta de Despesa")
    @OneToMany(mappedBy = "configTipoViagemContaDesp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoViagemContaDespesa> listaContaDespesa;
    @Transient
    private Boolean erroDuranteProcessamento;
    @Transient
    private String mensagemDeErro;

    public ConfigTipoViagemContaDespesa() {
        listaContaDespesa = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public List<TipoViagemContaDespesa> getListaContaDespesa() {
        return listaContaDespesa;
    }

    public void setListaContaDespesa(List<TipoViagemContaDespesa> listaContaDespesa) {
        this.listaContaDespesa = listaContaDespesa;
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
}
