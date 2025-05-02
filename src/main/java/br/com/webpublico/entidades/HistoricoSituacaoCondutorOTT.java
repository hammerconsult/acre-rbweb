package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCondutorOTT;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Histórico de Situações do Condutor da OTT")
@Table(name = "HISTORICOSITCONDUTOROTT")
@Entity
@Audited
public class HistoricoSituacaoCondutorOTT extends SuperEntidade implements Comparable<HistoricoSituacaoCondutorOTT> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CondutorOperadoraTecnologiaTransporte condutorOtt;
    @ManyToOne
    private UsuarioSistema usuarioAlteracao;
    @Enumerated(EnumType.STRING)
    private SituacaoCondutorOTT situacaoCondutorOTT;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;
    private String motivo;

    public HistoricoSituacaoCondutorOTT() {
    }

    public HistoricoSituacaoCondutorOTT(CondutorOperadoraTecnologiaTransporte condutor, UsuarioSistema usuario) {
        this.condutorOtt = condutor;
        this.dataOperacao = new Date();
        this.usuarioAlteracao = usuario;
        this.situacaoCondutorOTT = condutor.getSituacaoCondutorOTT();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CondutorOperadoraTecnologiaTransporte getCondutorOtt() {
        return condutorOtt;
    }

    public void setCondutorOtt(CondutorOperadoraTecnologiaTransporte condutorOtt) {
        this.condutorOtt = condutorOtt;
    }

    public UsuarioSistema getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(UsuarioSistema usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public SituacaoCondutorOTT getSituacaoCondutorOTT() {
        return situacaoCondutorOTT;
    }

    public void setSituacaoCondutorOTT(SituacaoCondutorOTT situacaoCondutorOTT) {
        this.situacaoCondutorOTT = situacaoCondutorOTT;
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
    public int compareTo(HistoricoSituacaoCondutorOTT o) {
        return this.dataOperacao.compareTo(o.getDataOperacao());
    }
}
