package br.com.webpublico.entidades;

import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Fabio on 09/10/2018.
 */
@Entity
@Audited
@Table(name = "JUNTACOMERCIALPJ")
public class JuntaComercialPessoaJuridica extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaJuridica pessoaJuridica;
    private String numeroProtocoloJuntaComercial;
    private String numeroProtocoloViabilidade;
    private String nire;
    @Temporal(TemporalType.DATE)
    private Date dataNire;
    private String numeroProcesso;
    private String requerente;
    private String ato;
    @ManyToOne
    private NaturezaJuridica naturezaJuridica;
    @Temporal(TemporalType.DATE)
    private Date assinadoEm;
    @OneToMany(mappedBy = "juntaComercial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoRedeSimPessoaJuridica> eventosRedeSim;

    public JuntaComercialPessoaJuridica() {
        this.eventosRedeSim = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public String getNumeroProtocoloJuntaComercial() {
        return numeroProtocoloJuntaComercial;
    }

    public void setNumeroProtocoloJuntaComercial(String numeroProtocoloJuntaComercial) {
        this.numeroProtocoloJuntaComercial = numeroProtocoloJuntaComercial;
    }

    public String getNumeroProtocoloViabilidade() {
        return numeroProtocoloViabilidade;
    }

    public void setNumeroProtocoloViabilidade(String numeroProtocoloViabilidade) {
        this.numeroProtocoloViabilidade = numeroProtocoloViabilidade;
    }

    public String getNire() {
        return nire;
    }

    public void setNire(String nire) {
        this.nire = nire;
    }

    public Date getDataNire() {
        return dataNire;
    }

    public void setDataNire(Date dataNire) {
        this.dataNire = dataNire;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getRequerente() {
        return requerente;
    }

    public void setRequerente(String requerente) {
        this.requerente = requerente;
    }

    public String getAto() {
        return ato;
    }

    public void setAto(String ato) {
        this.ato = ato;
    }

    public NaturezaJuridica getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(NaturezaJuridica naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public Date getAssinadoEm() {
        return assinadoEm;
    }

    public void setAssinadoEm(Date assinadoEm) {
        this.assinadoEm = assinadoEm;
    }

    public List<EventoRedeSimPessoaJuridica> getEventosRedeSim() {
        return eventosRedeSim;
    }

    public void setEventosRedeSim(List<EventoRedeSimPessoaJuridica> eventosRedeSim) {
        this.eventosRedeSim = eventosRedeSim;
    }
}
