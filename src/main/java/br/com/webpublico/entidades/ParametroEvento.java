/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author major
 */
@Entity

@Audited
public class ParametroEvento extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data")
    private Date dataEvento;
    @Obrigatorio
    @Etiqueta("Histórico")
    private String complementoHistorico;
    @OneToMany(mappedBy = "parametroEvento", cascade = CascadeType.ALL)
    private List<ItemParametroEvento> itensParametrosEvento;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Evento contábil")
    private EventoContabil eventoContabil;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Transient
    private Long criadoEm;
    @Obrigatorio
    @Etiqueta("Classe Origem fato gerador")
    private String classeOrigem;
    @Obrigatorio
    @Etiqueta("Id Origem fato gerador")
    private String idOrigem;
    @Enumerated(EnumType.STRING)
    private ComplementoId complementoId;
    @Transient
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo Balancete")
    private TipoBalancete tipoBalancete;

    public ParametroEvento() {
        criadoEm = System.nanoTime();
        itensParametrosEvento = new ArrayList<ItemParametroEvento>();
        complementoId = ComplementoId.NORMAL;
        tipoBalancete = TipoBalancete.MENSAL;
    }

    public ParametroEvento(Date dataEvento, String complementoHistorico, List<ItemParametroEvento> itensParametrosEvento, EventoContabil eventoContabil, UnidadeOrganizacional unidadeOrganizacional, Long criadoEm) {
        this.dataEvento = dataEvento;
        this.complementoHistorico = complementoHistorico;
        this.itensParametrosEvento = itensParametrosEvento;
        this.eventoContabil = eventoContabil;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.criadoEm = System.nanoTime();
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public List<ItemParametroEvento> getItensParametrosEvento() {
        return itensParametrosEvento;
    }

    public void setItensParametrosEvento(List<ItemParametroEvento> itensParametrosEvento) {
        this.itensParametrosEvento = itensParametrosEvento;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = (complementoHistorico.length() > 3000 ? complementoHistorico.substring(0, 3000) : complementoHistorico);
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(Date dataEvento) {
        this.dataEvento = dataEvento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClasseOrigem() {
        return classeOrigem;
    }

    public void setClasseOrigem(String classeOrigem) {
        this.classeOrigem = classeOrigem;
    }

    public String getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(String idOrigem) {
        this.idOrigem = idOrigem;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public ComplementoId getComplementoId() {
        return complementoId;
    }

    public void setComplementoId(ComplementoId complementoId) {
        this.complementoId = complementoId;
    }

    public TipoBalancete getTipoBalancete() {
        return tipoBalancete;
    }

    public void setTipoBalancete(TipoBalancete tipoBalancete) {
        this.tipoBalancete = tipoBalancete;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "ParametroEvento{" +
            "id=" + id +
            ", dataEvento=" + dataEvento +
            ", complementoHistorico='" + complementoHistorico + '\'' +
            ", eventoContabil=" + eventoContabil +
            ", unidadeOrganizacional=" + unidadeOrganizacional +
            ", criadoEm=" + criadoEm +
            ", classeOrigem='" + classeOrigem + '\'' +
            ", idOrigem='" + idOrigem + '\'' +
            ", complementoId=" + complementoId +
            ", exercicio=" + exercicio +
            ", tipoBalancete=" + tipoBalancete +
            '}';
    }

    public enum ComplementoId {
        NORMAL("0"),
        CONCEDIDO("1"),
        RECEBIDO("2");

        private String codigo;

        private ComplementoId(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }
    }

    public static boolean isTipoObjetoParametro(ObjetoParametro.TipoObjetoParametro tipoObjetoParametro, ObjetoParametro obj) {
        return obj != null && tipoObjetoParametro != null &&
            (ObjetoParametro.TipoObjetoParametro.AMBOS.equals(obj.getTipoObjetoParametro()) || tipoObjetoParametro.equals(obj.getTipoObjetoParametro()));
    }
}
