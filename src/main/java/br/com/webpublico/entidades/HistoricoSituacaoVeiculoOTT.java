package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoVeiculoOTT;
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
@Table(name = "HISTORICOSITVEICULOOTT")
@Entity
@Audited
public class HistoricoSituacaoVeiculoOTT extends SuperEntidade implements Comparable<HistoricoSituacaoVeiculoOTT> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VeiculoOperadoraTecnologiaTransporte veiculoOtt;
    @ManyToOne
    private UsuarioSistema usuarioAlteracao;
    @Enumerated(EnumType.STRING)
    private SituacaoVeiculoOTT situacaoVeiculoOTT;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;
    private String motivo;

    public HistoricoSituacaoVeiculoOTT() {
    }

    public HistoricoSituacaoVeiculoOTT(VeiculoOperadoraTecnologiaTransporte veiculo, UsuarioSistema usuario) {
        this.veiculoOtt = veiculo;
        this.dataOperacao = new Date();
        this.usuarioAlteracao = usuario;
        this.situacaoVeiculoOTT = veiculo.getSituacaoVeiculoOTT();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VeiculoOperadoraTecnologiaTransporte getVeiculoOtt() {
        return veiculoOtt;
    }

    public void setVeiculoOtt(VeiculoOperadoraTecnologiaTransporte veiculoOtt) {
        this.veiculoOtt = veiculoOtt;
    }

    public UsuarioSistema getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(UsuarioSistema usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public SituacaoVeiculoOTT getSituacaoVeiculoOTT() {
        return situacaoVeiculoOTT;
    }

    public void setSituacaoVeiculoOTT(SituacaoVeiculoOTT situacaoVeiculoOTT) {
        this.situacaoVeiculoOTT = situacaoVeiculoOTT;
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
    public int compareTo(HistoricoSituacaoVeiculoOTT o) {
        return this.dataOperacao.compareTo(o.getDataOperacao());
    }
}
