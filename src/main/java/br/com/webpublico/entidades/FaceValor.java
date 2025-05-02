package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity
@Audited
public class FaceValor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private Face face;
    private BigDecimal valor;
    @ManyToOne
    private Quadra quadra;
    @Transient
    private Long criadoEm;

    public FaceValor() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FaceValor faceValor = (FaceValor) o;

        if (exercicio != null ? !exercicio.equals(faceValor.exercicio) : faceValor.exercicio != null) return false;
        if (face != null ? !face.equals(faceValor.face) : faceValor.face != null) return false;
        return !(quadra != null ? !quadra.equals(faceValor.quadra) : faceValor.quadra != null);

    }

    @Override
    public int hashCode() {
        int result = exercicio != null ? exercicio.hashCode() : 0;
        result = 31 * result + (face != null ? face.hashCode() : 0);
        result = 31 * result + (quadra != null ? quadra.hashCode() : 0);
        return result;
    }
}
