package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoSolicitacaoITBI;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class TramiteSolicitacaoItbiOnline extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SolicitacaoItbiOnline solicitacaoItbiOnline;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private String usuario;
    @Enumerated(EnumType.STRING)
    private SituacaoSolicitacaoITBI situacaoSolicitacaoITBI;
    private String observacao;
    @ManyToOne
    private UsuarioSistema auditorFiscal;
    @OneToOne
    private Arquivo arquivo;
    private BigDecimal valorAvaliado;
    @OneToMany(mappedBy = "tramiteSolicitacaoItbiOnline", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitacaoItbiOnlineDocumento> documentos;

    public TramiteSolicitacaoItbiOnline() {
        super();
        this.dataRegistro = new Date();
        this.documentos = Lists.newArrayList();
    }

    @Override
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

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public SituacaoSolicitacaoITBI getSituacaoSolicitacaoITBI() {
        return situacaoSolicitacaoITBI;
    }

    public void setSituacaoSolicitacaoITBI(SituacaoSolicitacaoITBI situacaoSolicitacaoITBI) {
        this.situacaoSolicitacaoITBI = situacaoSolicitacaoITBI;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public UsuarioSistema getAuditorFiscal() {
        return auditorFiscal;
    }

    public void setAuditorFiscal(UsuarioSistema auditorFiscal) {
        this.auditorFiscal = auditorFiscal;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public BigDecimal getValorAvaliado() {
        return valorAvaliado;
    }

    public void setValorAvaliado(BigDecimal valorAvaliado) {
        this.valorAvaliado = valorAvaliado;
    }

    public List<SolicitacaoItbiOnlineDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<SolicitacaoItbiOnlineDocumento> documentos) {
        this.documentos = documentos;
    }

    public boolean isDesignada() {
        return SituacaoSolicitacaoITBI.DESIGNADA.equals(this.situacaoSolicitacaoITBI);
    }

    public boolean isIndeferida() {
        return SituacaoSolicitacaoITBI.INDEFERIDA.equals(this.situacaoSolicitacaoITBI);
    }

    public boolean isAvaliada() {
        return SituacaoSolicitacaoITBI.AVALIADA.equals(this.situacaoSolicitacaoITBI);
    }

    public boolean isHomologada() {
        return SituacaoSolicitacaoITBI.HOMOLOGADA.equals(this.situacaoSolicitacaoITBI);
    }

    public boolean isIndeferidaOrAvaliadaOrHomologada() {
        return isIndeferida() || isAvaliada() || isHomologada();
    }
}
