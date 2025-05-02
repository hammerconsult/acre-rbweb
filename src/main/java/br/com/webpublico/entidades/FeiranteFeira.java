package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Feiras")
public class FeiranteFeira extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Feirante")
    @ManyToOne
    private Feirante feirante;

    @Etiqueta("Feira")
    @Obrigatorio
    @ManyToOne
    private Feira feira;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Feirante getFeirante() {
        return feirante;
    }

    public void setFeirante(Feirante feirante) {
        this.feirante = feirante;
    }

    public Feira getFeira() {
        return feira;
    }

    public void setFeira(Feira feira) {
        this.feira = feira;
    }

    @Override
    public String toString() {
        return feira.toString();
    }
}
