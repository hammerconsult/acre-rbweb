package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AndreGustavo on 14/10/2014.
 */
@Entity
@Audited
@Etiqueta("Módulo de Capacitação")
public class ModuloCapacitacao extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta(value = "Data de Início")
    private Date dataInicioModulo;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta(value = "Data de Término")
    private Date dataFinalModulo;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta(value = "Hora de Início")
    private Date horaInicio;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta(value = "Hora de Término")
    private Date horaFim;
    @Tabelavel
    @Etiqueta(value = "Nome do Módulo")
    private String nomeModulo;
    private String local;
    @Tabelavel
    @Etiqueta(value = "Carga Horária")
    private Integer cargaHoraria;
    @ManyToOne
    @Tabelavel
    private Capacitacao capacitacao;
    @OneToMany(mappedBy = "moduloCapacitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstrutorModulo> instrutores;
    @OneToMany(mappedBy = "moduloCapacitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetodologiaModulo> metodologiaModulos;
    @OneToMany(mappedBy = "moduloCapacitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PresencaModulo> presencas;

    public ModuloCapacitacao() {
        instrutores = new ArrayList<>();
        metodologiaModulos = new ArrayList<>();
        presencas = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicioModulo() {
        return dataInicioModulo;
    }

    public void setDataInicioModulo(Date dataInicioModulo) {
        this.dataInicioModulo = dataInicioModulo;
    }

    public Date getDataFinalModulo() {
        return dataFinalModulo;
    }

    public void setDataFinalModulo(Date dataFinalModulo) {
        this.dataFinalModulo = dataFinalModulo;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(Date horaFim) {
        this.horaFim = horaFim;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public Capacitacao getCapacitacao() {
        return capacitacao;
    }

    public void setCapacitacao(Capacitacao capacitacao) {
        this.capacitacao = capacitacao;
    }

    public List<InstrutorModulo> getInstrutores() {
        return instrutores;
    }

    public void setInstrutores(List<InstrutorModulo> instrutores) {
        this.instrutores = instrutores;
    }

    public List<MetodologiaModulo> getMetodologiaModulos() {
        return metodologiaModulos;
    }

    public void setMetodologiaModulos(List<MetodologiaModulo> metodologiaModulos) {
        this.metodologiaModulos = metodologiaModulos;
    }

    public List<PresencaModulo> getPresencas() {
        return presencas;
    }

    public void setPresencas(List<PresencaModulo> presencas) {
        this.presencas = presencas;
    }

    public String getNomeModulo() {
        return nomeModulo;
    }

    public void setNomeModulo(String nomeModulo) {
        this.nomeModulo = nomeModulo;
    }

    @Override
    public String toString() {
        return nomeModulo;
    }
}
