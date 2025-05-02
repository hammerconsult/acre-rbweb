package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoReconhecimentoDivida;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Histórico do Reconhecimento da Dívida")
@Table(name = "HISTORICORECONHECIMENTODIV")
public class HistoricoReconhecimentoDivida extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Reconhecimento de Dívida do Exercício")
    private ReconhecimentoDivida reconhecimentoDivida;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @Obrigatorio
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data do Histórico")
    private Date dataHistorico;
    @Enumerated(EnumType.STRING)
    @Etiqueta("De")
    private SituacaoReconhecimentoDivida situacaoDe;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Para")
    private SituacaoReconhecimentoDivida situacaoPara;

    public HistoricoReconhecimentoDivida() {
        super();
    }

    public HistoricoReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida, UsuarioSistema usuarioSistema, Date dataHistorico, SituacaoReconhecimentoDivida situacaoDe, SituacaoReconhecimentoDivida situacaoPara) {
        super();
        this.reconhecimentoDivida = reconhecimentoDivida;
        this.usuarioSistema = usuarioSistema;
        this.dataHistorico = dataHistorico;
        this.situacaoDe = situacaoDe;
        this.situacaoPara = situacaoPara;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReconhecimentoDivida getReconhecimentoDivida() {
        return reconhecimentoDivida;
    }

    public void setReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        this.reconhecimentoDivida = reconhecimentoDivida;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataHistorico() {
        return dataHistorico;
    }

    public void setDataHistorico(Date dataHistorico) {
        this.dataHistorico = dataHistorico;
    }

    public SituacaoReconhecimentoDivida getSituacaoDe() {
        return situacaoDe;
    }

    public void setSituacaoDe(SituacaoReconhecimentoDivida situacaoDe) {
        this.situacaoDe = situacaoDe;
    }

    public SituacaoReconhecimentoDivida getSituacaoPara() {
        return situacaoPara;
    }

    public void setSituacaoPara(SituacaoReconhecimentoDivida situacaoPara) {
        this.situacaoPara = situacaoPara;
    }
}
