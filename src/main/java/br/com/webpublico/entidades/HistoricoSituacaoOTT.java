package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoOTT;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Histórico de Situações da OTT")
@Entity
@Audited
 public class HistoricoSituacaoOTT extends SuperEntidade implements Comparable<HistoricoSituacaoOTT> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private OperadoraTecnologiaTransporte operadoraTecTransporte;
    @ManyToOne
    private UsuarioSistema usuarioAlteracao;
    @Enumerated(EnumType.STRING)
    private SituacaoOTT situacaoOTT;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;
    private String motivo;
    private Long idOperadoraPortal;

    public HistoricoSituacaoOTT() {
    }

    public HistoricoSituacaoOTT(OperadoraTecnologiaTransporte operadora, UsuarioSistema usuario) {
        this.operadoraTecTransporte = operadora;
        this.dataOperacao = new Date();
        this.usuarioAlteracao = usuario;
        this.situacaoOTT = operadora.getSituacao();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperadoraTecnologiaTransporte getOperadoraTecTransporte() {
        return operadoraTecTransporte;
    }

    public void setOperadoraTecTransporte(OperadoraTecnologiaTransporte operadoraTecTransporte) {
        this.operadoraTecTransporte = operadoraTecTransporte;
    }

    public UsuarioSistema getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(UsuarioSistema usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public SituacaoOTT getSituacaoOTT() {
        return situacaoOTT;
    }

    public void setSituacaoOTT(SituacaoOTT situacaoOTT) {
        this.situacaoOTT = situacaoOTT;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public int compareTo(HistoricoSituacaoOTT o) {
        return this.dataOperacao.compareTo(o.getDataOperacao());
    }

    public Long getIdOperadoraPortal() {
        return idOperadoraPortal;
    }

    public void setIdOperadoraPortal(Long idOperadoraPortal) {
        this.idOperadoraPortal = idOperadoraPortal;
    }
}
