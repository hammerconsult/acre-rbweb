package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Audited
public class ProcessoDeProtestoArquivo extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoDeProtesto processoDeProtesto;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;

    public ProcessoDeProtestoArquivo() {
        super();
    }

    public ProcessoDeProtesto getProcessoDeProtesto() {
        return processoDeProtesto;
    }

    public void setProcessoDeProtesto(ProcessoDeProtesto processoDeProtesto) {
        this.processoDeProtesto = processoDeProtesto;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return arquivo.getNome();
    }
}
