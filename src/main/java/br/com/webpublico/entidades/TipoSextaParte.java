package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 27/07/2016.
 */
@Audited
@Entity
@Etiqueta("Tipo de Sexta Parte")
public class TipoSextaParte extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Pesquisavel
    @Tabelavel
    private Long codigo;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Length(maximo = 70)
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Evento FP")
    @Pesquisavel
    @Tabelavel
    private EventoFP eventoFP;
    @OneToMany(mappedBy = "tipoSextaParte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoSextaPartePeriodoExcludente> periodosExcludente;
    @OneToMany(mappedBy = "tipoSextaParte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoSextaParteUnidadeOrganizacional> unidadesOganizacional;

    public TipoSextaParte() {
        super();
        periodosExcludente = Lists.newArrayList();
        unidadesOganizacional = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public List<TipoSextaPartePeriodoExcludente> getPeriodosExcludente() {
        return periodosExcludente;
    }

    public void setPeriodosExcludente(List<TipoSextaPartePeriodoExcludente> periodosExcludente) {
        this.periodosExcludente = periodosExcludente;
    }

    public List<TipoSextaParteUnidadeOrganizacional> getUnidadesOganizacional() {
        return unidadesOganizacional;
    }

    public void setUnidadesOganizacional(List<TipoSextaParteUnidadeOrganizacional> unidadesOganizacional) {
        this.unidadesOganizacional = unidadesOganizacional;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public boolean hasPeriodoExcludenteConcomitante(TipoSextaPartePeriodoExcludente tipo) {
        if (getPeriodosExcludente() != null && !getPeriodosExcludente().isEmpty()) {
            for (TipoSextaPartePeriodoExcludente item : getPeriodosExcludente()) {

            }
        }
        return false;
    }
}
