package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by tharlyson on 07/11/19.
 */
@Audited
@Entity
@Etiqueta("Processo Rotas")
public class ProcessoRota implements Serializable, Comparable<ProcessoRota> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Etiqueta("Prazo")
    private double prazo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Data Registro")
    private Date dataRegistro;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo Prazo")
    private TipoPrazo tipoPrazo;
    @ManyToOne
    @Etiqueta("Processo")
    private Processo processo;
    @Transient
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Etiqueta("Tramitado")
    private Boolean tramitado;

    private Integer indice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public double getPrazo() {
        return prazo;
    }

    public void setPrazo(double prazo) {
        this.prazo = prazo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public TipoPrazo getTipoPrazo() {
        return tipoPrazo;
    }

    public void setTipoPrazo(TipoPrazo tipoPrazo) {
        this.tipoPrazo = tipoPrazo;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
    }

    public Boolean getTramitado() {
        return tramitado != null ? tramitado : Boolean.FALSE;
    }

    public void setTramitado(Boolean tramitado) {
        this.tramitado = tramitado;
    }

    @Override
    public int compareTo(ProcessoRota o) {
        if (o.getIndice() != null && getIndice() != null){
         return getIndice().compareTo(o.getIndice());
        }
        return 0;
    }
}
