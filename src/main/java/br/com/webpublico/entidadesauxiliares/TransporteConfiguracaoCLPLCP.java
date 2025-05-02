package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CLP;
import br.com.webpublico.entidades.ItemEventoCLP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.TransporteConfiguracaoContabil;
import br.com.webpublico.enums.TipoConfiguracaoContabil;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;

import javax.persistence.*;

/**
 * Created by HardRock on 03/01/2017.
 */
@GrupoDiagrama(nome = "Contabil")
@Entity
@Etiqueta("Transporte Configuração CLP/LCP")
public class TransporteConfiguracaoCLPLCP extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("CLP")
    @ManyToOne
    private CLP clp;
    @Obrigatorio
    @Etiqueta("Item Evento CLP")
    @ManyToOne
    private ItemEventoCLP itemEventoCLP;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Transporte")
    private TipoConfiguracaoContabil tipoConfiguracaoContabil;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Transporte Configuração Contábil")
    private TransporteConfiguracaoContabil transporteConfigContabil;
    @Transient
    private Boolean erroDuranteProcessamento;
    @Transient
    private String mensagemDeErro;

    public TransporteConfiguracaoCLPLCP() {
        erroDuranteProcessamento = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CLP getClp() {
        return clp;
    }

    public void setClp(CLP clp) {
        this.clp = clp;
    }

    public ItemEventoCLP getItemEventoCLP() {
        return itemEventoCLP;
    }

    public void setItemEventoCLP(ItemEventoCLP itemEventoCLP) {
        this.itemEventoCLP = itemEventoCLP;
    }

    public TipoConfiguracaoContabil getTipoConfiguracaoContabil() {
        return tipoConfiguracaoContabil;
    }

    public void setTipoConfiguracaoContabil(TipoConfiguracaoContabil tipoConfiguracaoContabil) {
        this.tipoConfiguracaoContabil = tipoConfiguracaoContabil;
    }

    public TransporteConfiguracaoContabil getTransporteConfigContabil() {
        return transporteConfigContabil;
    }

    public void setTransporteConfigContabil(TransporteConfiguracaoContabil transporteConfigContabil) {
        this.transporteConfigContabil = transporteConfigContabil;
    }

    public Boolean getErroDuranteProcessamento() {
        return erroDuranteProcessamento;
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
