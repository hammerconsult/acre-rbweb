package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "PARAMETROFISCALIZACAODIV")
@Entity
@Audited
@Etiqueta("Parâmetros de Fiscalização Dívida")
public class ParametroFiscalizacaoDivida extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Divida divida;
    @ManyToOne
    private ParametroFiscalizacao parametroFiscalizacao;

    public ParametroFiscalizacaoDivida() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public ParametroFiscalizacao getParametroFiscalizacao() {
        return parametroFiscalizacao;
    }

    public void setParametroFiscalizacao(ParametroFiscalizacao parametroFiscalizacao) {
        this.parametroFiscalizacao = parametroFiscalizacao;
    }

    @Override
    public String toString() {
        return "ParametroFiscalizacao: " + parametroFiscalizacao.getExercicio().getAno() +
               "Dívida: " + divida.getDescricao();
    }
}
