package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.CriterioDesempate;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by venom on 18/11/14.
 */
@Entity
@Audited
public class DesempateConcurso extends SuperEntidade implements Serializable, Comparable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    private Concurso concurso;
    @Etiqueta("Ordem")
    private Integer ordem;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Critério")
    private CriterioDesempate criterioDesempate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public CriterioDesempate getCriterioDesempate() {
        return criterioDesempate;
    }

    public void setCriterioDesempate(CriterioDesempate criterioDesempate) {
        this.criterioDesempate = criterioDesempate;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    @Override
    public int compareTo(Object o) {
        return getOrdem().compareTo(((DesempateConcurso) o).getOrdem());
    }

    public boolean isCargoPublico(){
        return this.criterioDesempate.equals(CriterioDesempate.CARGO_PUBLICO);
    }

    public boolean isJurado(){
        return this.criterioDesempate.equals(CriterioDesempate.JURADO);
    }

    public boolean isMesario(){
        return this.criterioDesempate.equals(CriterioDesempate.MESARIO);
    }

    public boolean isDoador(){
        return this.criterioDesempate.equals(CriterioDesempate.DOADOR);
    }

    public boolean isIdade(){
        return this.criterioDesempate.equals(CriterioDesempate.IDADE);
    }

    public String getOrdemComIndicadorOrdinal(){
        return ordem+"º";
    }
}
