package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class SolicitacaoItbiOnlineDocumento extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private SolicitacaoItbiOnline solicitacaoItbiOnline;
    @ManyToOne
    private TramiteSolicitacaoItbiOnline tramiteSolicitacaoItbiOnline;
    @ManyToOne
    private ParametrosITBIDocumento parametrosITBIDocumento;
    private String descricao;
    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo documento;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public SolicitacaoItbiOnlineDocumento() {
        super();
        this.dataRegistro = new Date();
    }

    public SolicitacaoItbiOnlineDocumento(SolicitacaoItbiOnline solicitacaoItbiOnline,
                                          ParametrosITBIDocumento parametrosITBIDocumento,
                                          String descricao) {
        this();
        this.solicitacaoItbiOnline = solicitacaoItbiOnline;
        this.parametrosITBIDocumento = parametrosITBIDocumento;
        this.descricao = descricao;
        this.dataRegistro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoItbiOnline getSolicitacaoItbiOnline() {
        return solicitacaoItbiOnline;
    }

    public void setSolicitacaoItbiOnline(SolicitacaoItbiOnline solicitacaoItbiOnline) {
        this.solicitacaoItbiOnline = solicitacaoItbiOnline;
    }

    public TramiteSolicitacaoItbiOnline getTramiteSolicitacaoItbiOnline() {
        return tramiteSolicitacaoItbiOnline;
    }

    public void setTramiteSolicitacaoItbiOnline(TramiteSolicitacaoItbiOnline tramiteSolicitacaoItbiOnline) {
        this.tramiteSolicitacaoItbiOnline = tramiteSolicitacaoItbiOnline;
    }

    public ParametrosITBIDocumento getParametrosITBIDocumento() {
        return parametrosITBIDocumento;
    }

    public void setParametrosITBIDocumento(ParametrosITBIDocumento parametrosITBIDocumento) {
        this.parametrosITBIDocumento = parametrosITBIDocumento;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Arquivo getDocumento() {
        return documento;
    }

    public void setDocumento(Arquivo documento) {
        this.documento = documento;
    }
}
