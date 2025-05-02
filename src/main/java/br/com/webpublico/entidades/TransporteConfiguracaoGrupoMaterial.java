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
@Table(name = "TRANSPORTECONFIGGRUPOMATERIAL")
@Etiqueta("Transporte Configuração Grupo Patrimonial")
public class TransporteConfiguracaoGrupoMaterial extends SuperEntidade {

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
    @Etiqueta("Grupo Material")
    private GrupoMaterial grupoMaterial;

    @Obrigatorio
    @Etiqueta("Log")
    private String log;

    public TransporteConfiguracaoGrupoMaterial() {
        super();
    }

    public TransporteConfiguracaoGrupoMaterial(TipoConfiguracaoContabil tipoConfiguracaoContabil, TransporteConfiguracaoContabil transporteConfigContabil, GrupoMaterial grupoMaterial, String log) {
        this.tipoConfiguracaoContabil = tipoConfiguracaoContabil;
        this.transporteConfigContabil = transporteConfigContabil;
        this.grupoMaterial = grupoMaterial;
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

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    @Override
    public String toString() {
        try {
            return this.grupoMaterial.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
