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
@Etiqueta("Transporte Configuração LCP")
public class TransporteConfiguracaoLCP extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Transporte")
    private TipoConfiguracaoContabil tipoConfiguracaoContabil;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Transporte Configuração Contábil")
    private TransporteConfiguracaoContabil transporteConfigContabil;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("LCP")
    private LCP lcp;

    @Obrigatorio
    @Etiqueta("Log")
    private String log;

    public TransporteConfiguracaoLCP() {
        super();
    }

    public TransporteConfiguracaoLCP(TipoConfiguracaoContabil tipoConfiguracaoContabil, TransporteConfiguracaoContabil transporteConfigContabil, LCP lcp, String log) {
        this.tipoConfiguracaoContabil = tipoConfiguracaoContabil;
        this.transporteConfigContabil = transporteConfigContabil;
        this.lcp = lcp;
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

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public LCP getLcp() {
        return lcp;
    }

    public void setLcp(LCP lcp) {
        this.lcp = lcp;
    }

    @Override
    public String toString() {
        try {
            return this.lcp.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
