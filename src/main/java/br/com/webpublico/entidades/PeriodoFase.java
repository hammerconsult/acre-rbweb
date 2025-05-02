package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 15/10/13
 * Time: 14:58
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Segurança")
@Entity
@Audited
@Etiqueta("Período Fase")
public class PeriodoFase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Obrigatorio
    private Fase fase;
    @ManyToOne
    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Obrigatorio
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @OneToMany(mappedBy = "periodoFase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Unidades")
    private List<PeriodoFaseUnidade> periodoFaseUnidades;
    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Início da Vigência")
    @Obrigatorio
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Fim da Vigência")
    @Obrigatorio
    private Date fimVigencia;
    @Transient
    private Long criadoEm;

    public PeriodoFase() {
        periodoFaseUnidades = new ArrayList<>();
        criadoEm = System.nanoTime();
    }

    public PeriodoFase(Fase fase, Exercicio exercicio, List<PeriodoFaseUnidade> periodoFaseUnidades, String descricao, Date inicioVigencia, Date fimVigencia) {
        this.fase = fase;
        this.exercicio = exercicio;
        this.periodoFaseUnidades = periodoFaseUnidades;
        this.descricao = descricao;
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
        criadoEm = System.nanoTime();
    }

    public List<PeriodoFaseUnidade> getPeriodoFaseUnidades() {
        return periodoFaseUnidades;
    }

    public void setPeriodoFaseUnidades(List<PeriodoFaseUnidade> periodoFaseUnidades) {
        this.periodoFaseUnidades = periodoFaseUnidades;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Fase getFase() {
        return fase;
    }

    public void setFase(Fase fase) {
        this.fase = fase;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return fase.toString() + " - " + descricao + "(" + exercicio.getAno() + ")";
    }
}
