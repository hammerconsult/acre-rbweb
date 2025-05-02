package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoArt;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Desenvolvimento on 31/03/2016.
 */
@Entity
@Audited
@Etiqueta("Técnico/Fiscal da Obra")
public class ObraTecnicoFiscal extends SuperEntidade implements ValidadorVigencia, Comparable<ObraTecnicoFiscal> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Obra obra;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private ResponsavelTecnicoFiscal tecnicoFiscal;

    @Etiqueta("Tipo de ART/RRT")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoArt tipoArt;

    @Etiqueta("ART/RRT")
    @Obrigatorio
    private String artRrt;

    @Etiqueta("Início de Vigência")
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;

    @Etiqueta("Fim de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;

    public ObraTecnicoFiscal() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }

    public ResponsavelTecnicoFiscal getTecnicoFiscal() {
        return tecnicoFiscal;
    }

    public void setTecnicoFiscal(ResponsavelTecnicoFiscal tecnicoFiscal) {
        this.tecnicoFiscal = tecnicoFiscal;
    }

    public TipoArt getTipoArt() {
        return tipoArt;
    }

    public void setTipoArt(TipoArt tipoArt) {
        this.tipoArt = tipoArt;
    }

    public String getArtRrt() {
        return artRrt;
    }

    public void setArtRrt(String artRrt) {
        this.artRrt = artRrt;
    }

    @Override
    public Date getInicioVigencia() {
        return this.inicioVigencia;
    }

    @Override
    public void setInicioVigencia(Date data) {
        this.inicioVigencia = data;
    }

    @Override
    public Date getFimVigencia() {
        return this.fimVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.fimVigencia = data;
    }

    @Override
    public int compareTo(ObraTecnicoFiscal o) {
        try {
            return this.getInicioVigencia().compareTo(o.getInicioVigencia());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return tecnicoFiscal.toString();
    }
}
