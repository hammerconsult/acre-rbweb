package br.com.webpublico.entidades;

import br.com.webpublico.enums.AnexoRGF;
import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.enums.Mes;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class NotaExplicativaRGF extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Mes mes;
    private String notaExplicativa;
    @Enumerated(EnumType.STRING)
    private AnexoRGF anexoRGF;
    @Enumerated(EnumType.STRING)
    private EsferaDoPoder esferaDoPoder;
    @ManyToOne
    private Exercicio exercicio;

    public NotaExplicativaRGF() {
    }

    public NotaExplicativaRGF(Mes mes) {
        this.mes = mes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public String getNotaExplicativa() {
        return notaExplicativa;
    }

    public void setNotaExplicativa(String notaExplicativa) {
        this.notaExplicativa = notaExplicativa;
    }

    public AnexoRGF getAnexoRGF() {
        return anexoRGF;
    }

    public void setAnexoRGF(AnexoRGF anexoRGF) {
        this.anexoRGF = anexoRGF;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}
