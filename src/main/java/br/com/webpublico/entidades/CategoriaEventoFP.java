package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 24/02/14
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Etiqueta("Categoria PCS EventoFP")
public class CategoriaEventoFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Final da Vigência")
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    private Date finalVigencia;
    @ManyToOne
    @Etiqueta("Categoria PCCR")
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    private CategoriaPCS categoriaPCS;
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Evento Folha")
    private EventoFP eventoFP;
    @Transient
    private Long criadoEm = System.nanoTime();

    public CategoriaEventoFP() {
        criadoEm = System.nanoTime();
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
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

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public CategoriaPCS getCategoriaPCS() {
        return categoriaPCS;
    }

    public void setCategoriaPCS(CategoriaPCS categoriaPCS) {
        this.categoriaPCS = categoriaPCS;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return categoriaPCS + " - " + eventoFP;    //To change body of overridden methods use File | Settings | File Templates.
    }
}
