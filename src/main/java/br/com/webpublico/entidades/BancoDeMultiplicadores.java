package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by carlos on 25/08/15.
 */
@Audited
@Entity
@Etiqueta("Banco de Multiplicadores")
public class BancoDeMultiplicadores implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Matrícula")
    private MatriculaFP matriculaFP;
    @ManyToOne
    @Etiqueta("Capacitação")
    private EventoDeRH eventoDeRH;
    @ManyToOne
    @Etiqueta("Lotação Funcional")
    private LotacaoFuncional lotacaoFuncional;

    public BancoDeMultiplicadores() {
    }

    public EventoDeRH getEventoDeRH() {
        return eventoDeRH;
    }

    public void setEventoDeRH(EventoDeRH eventoDeRH) {
        this.eventoDeRH = eventoDeRH;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public MatriculaFP getMatriculaFP() {
        return matriculaFP;
    }

    public void setMatriculaFP(MatriculaFP matriculaFP) {
        this.matriculaFP = matriculaFP;
    }

    @Override
    public String toString() {
        return matriculaFP.toString();
    }
}
