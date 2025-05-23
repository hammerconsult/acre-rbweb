package br.com.webpublico.entidades.rh.previdencia;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author peixe on 20/01/2016  08:53.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RH Previdência")
@Etiqueta("Processo Reajuste Previdência")
public class ReajusteAplicado extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @OneToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Temporal(TemporalType.TIMESTAMP)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Aplicado Em")
    private Date aplicadoEm;
    @ManyToOne
    @Etiqueta("Exercício de Referência")
    private Exercicio exercicioReferencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início Vigência dos Reajustes")
    private Date inicioVigenciaReajustes;

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

    public Date getAplicadoEm() {
        return aplicadoEm;
    }

    public void setAplicadoEm(Date aplicadoEm) {
        this.aplicadoEm = aplicadoEm;
    }

    public Exercicio getExercicioReferencia() {
        return exercicioReferencia;
    }

    public void setExercicioReferencia(Exercicio exercicioReferencia) {
        this.exercicioReferencia = exercicioReferencia;
    }

    public Date getInicioVigenciaReajustes() {
        return inicioVigenciaReajustes;
    }

    public void setInicioVigenciaReajustes(Date inicioVigenciaReajustes) {
        this.inicioVigenciaReajustes = inicioVigenciaReajustes;
    }
}
