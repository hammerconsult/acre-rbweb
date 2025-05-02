package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoConfiguracaoContabil;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;

import javax.persistence.*;

/**
 * Created by HardRock on 26/12/2016.
 */
@GrupoDiagrama(nome = "Contabil")
@Entity
@Etiqueta("Transporte Configuração CDE")
public class TransporteConfiguracaoCDE extends SuperEntidade {

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
    @Etiqueta("Configuração de Evento")
    private ConfiguracaoEvento configuracaoEvento;

    @Obrigatorio
    @Etiqueta("Log")
    private String log;

    public TransporteConfiguracaoCDE() {
        super();
    }

    public TransporteConfiguracaoCDE(TipoConfiguracaoContabil tipoConfiguracaoContabil, TransporteConfiguracaoContabil transporteConfigContabil, ConfiguracaoEvento configuracaoEvento, String log) {
        this.tipoConfiguracaoContabil = tipoConfiguracaoContabil;
        this.transporteConfigContabil = transporteConfigContabil;
        this.configuracaoEvento = configuracaoEvento;
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

    public ConfiguracaoEvento getConfiguracaoEvento() {
        return configuracaoEvento;
    }

    public void setConfiguracaoEvento(ConfiguracaoEvento configuracaoEvento) {
        this.configuracaoEvento = configuracaoEvento;
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
            return this.configuracaoEvento.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
