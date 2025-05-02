package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@GrupoDiagrama(nome = "Alvara")
public class AlvaraHorarioFuncionamento extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Alvara alvara;
    @OneToOne(cascade = CascadeType.MERGE)
    private HorarioFuncionamento horarioFuncionamento;

    public AlvaraHorarioFuncionamento() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alvara getAlvara() {
        return alvara;
    }

    public void setAlvara(Alvara alvara) {
        this.alvara = alvara;
    }

    public HorarioFuncionamento getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(HorarioFuncionamento horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }
}
