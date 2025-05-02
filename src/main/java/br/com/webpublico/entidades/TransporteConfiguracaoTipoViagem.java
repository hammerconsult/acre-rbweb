package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoConfiguracaoContabil;
import br.com.webpublico.enums.TipoViagem;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;

import javax.persistence.*;

@GrupoDiagrama(nome = "Contabil")
@Entity
@Table(name = "TRANSPORTECONFIGTIPOVIAGEM")
@Etiqueta("Transporte Configuração Tipo Viagem")
public class TransporteConfiguracaoTipoViagem extends SuperEntidade {

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

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Viagem")
    @Obrigatorio
    private TipoViagem tipoViagem;

    @Obrigatorio
    @Etiqueta("Log")
    private String log;

    public TransporteConfiguracaoTipoViagem() {
        super();
    }

    public TransporteConfiguracaoTipoViagem(TipoConfiguracaoContabil tipoConfiguracaoContabil, TransporteConfiguracaoContabil transporteConfigContabil, TipoViagem tipoViagem, String log) {
        this.tipoConfiguracaoContabil = tipoConfiguracaoContabil;
        this.transporteConfigContabil = transporteConfigContabil;
        this.tipoViagem = tipoViagem;
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

    public TipoViagem getTipoViagem() {
        return tipoViagem;
    }

    public void setTipoViagem(TipoViagem tipoViagem) {
        this.tipoViagem = tipoViagem;
    }

    @Override
    public String toString() {
        try {
            return this.tipoViagem.getDescricao();
        } catch (Exception ex) {
            return "";
        }
    }
}
