package br.com.webpublico.entidades.contabil.reinf;

import br.com.webpublico.entidades.ReceitaExtra;
import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ReceitaExtraReinf extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ReceitaExtra receitaExtra;
    @ManyToOne
    private RegistroEventoRetencaoReinf registro;

    public ReceitaExtraReinf() {

    }

    public ReceitaExtraReinf(ReceitaExtra receitaExtra, RegistroEventoRetencaoReinf registro) {
        this.receitaExtra = receitaExtra;
        this.registro = registro;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReceitaExtra getReceitaExtra() {
        return receitaExtra;
    }

    public void setReceitaExtra(ReceitaExtra receitaExtra) {
        this.receitaExtra = receitaExtra;
    }

    public RegistroEventoRetencaoReinf getRegistro() {
        return registro;
    }

    public void setRegistro(RegistroEventoRetencaoReinf registro) {
        this.registro = registro;
    }
}
