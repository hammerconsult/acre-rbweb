package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 06/09/13
 * Time: 10:30
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
public class ItensDetalhesErrosCalculo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VinculoFP vinculoFP;
    @ManyToOne
    private DetalhesCalculoRH detalhesCalculoRH;
    private String erros;

    public ItensDetalhesErrosCalculo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public DetalhesCalculoRH getDetalhesCalculoRH() {
        return detalhesCalculoRH;
    }

    public void setDetalhesCalculoRH(DetalhesCalculoRH detalhesCalculoRH) {
        this.detalhesCalculoRH = detalhesCalculoRH;
    }

    public String getErros() {
        return erros;
    }

    public void setErros(String erros) {
        this.erros = erros;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItensDetalhesErrosCalculo that = (ItensDetalhesErrosCalculo) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ItensDetalhesErrosCalculo{" +
                ", vinculoFP=" + vinculoFP +
                ", erros='" + erros + '\'' +
                '}';
    }
}
