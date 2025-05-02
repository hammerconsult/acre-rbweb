package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by renat on 20/06/2016.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Etiqueta("Transporte de Grupo Orçamentário")
@Table(name = "TRANSPORTEGRUPOORC")
public class TransporteGrupoOrcamentario extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Grupo Orçamentário")
    private GrupoOrcamentario grupoOrcamentario;
    @ManyToOne
    private TransporteConfiguracaoContabil transporteConfigContabil;
    @Etiqueta("Log")
    private String log;

    public TransporteGrupoOrcamentario() {
        super();
    }

    public TransporteGrupoOrcamentario(GrupoOrcamentario grupoOrcamentario, TransporteConfiguracaoContabil transporteConfigContabil, String log) {
        this.grupoOrcamentario = grupoOrcamentario;
        this.transporteConfigContabil = transporteConfigContabil;
        this.log = log;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoOrcamentario getGrupoOrcamentario() {
        return grupoOrcamentario;
    }

    public void setGrupoOrcamentario(GrupoOrcamentario grupoOrcamentario) {
        this.grupoOrcamentario = grupoOrcamentario;
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

    @Override
    public String toString() {
        return "Grupo Orçamentário: " + grupoOrcamentario;
    }
}
