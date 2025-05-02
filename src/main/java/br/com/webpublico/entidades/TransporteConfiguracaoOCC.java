package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoConfiguracaoContabil;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;

import javax.persistence.*;

/**
 * Created by HardRock on 26/12/2016.
 */
@GrupoDiagrama(nome = "Contabil")
@Entity
@Etiqueta("Transporte Configuração OCC")
public class TransporteConfiguracaoOCC extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Configuração")
    private TipoConfiguracaoContabil tipoConfiguracaoContabil;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Transporte Configuração Contábil")
    private TransporteConfiguracaoContabil transporteConfigContabil;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Origem Conta Contábil")
    private OrigemContaContabil origemContaContabil;

    @Obrigatorio
    @Etiqueta("Log")
    private String log;

    public TransporteConfiguracaoOCC() {
        super();
    }

    public TransporteConfiguracaoOCC(TipoConfiguracaoContabil tipoConfiguracaoContabil, TransporteConfiguracaoContabil transporteConfigContabil, OrigemContaContabil origemContaContabil, String log) {
        this.tipoConfiguracaoContabil = tipoConfiguracaoContabil;
        this.transporteConfigContabil = transporteConfigContabil;
        this.origemContaContabil = origemContaContabil;
        this.log = log;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public OrigemContaContabil getOrigemContaContabil() {
        return origemContaContabil;
    }

    public void setOrigemContaContabil(OrigemContaContabil origemContaContabil) {
        this.origemContaContabil = origemContaContabil;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public String toString() {
        try {
            return this.origemContaContabil.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
