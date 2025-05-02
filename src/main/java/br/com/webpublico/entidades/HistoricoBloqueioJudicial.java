package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
@Etiqueta("Histórico de Situações do Processo de Bloqueio Judicial de Débitos")
public class HistoricoBloqueioJudicial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private BloqueioJudicial bloqueioJudicial;
    @ManyToOne
    private UsuarioSistema usuarioHistorico;
    @Enumerated(EnumType.STRING)
    private SituacaoProcessoDebito situacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHistorico;

    public HistoricoBloqueioJudicial() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BloqueioJudicial getBloqueioJudicial() {
        return bloqueioJudicial;
    }

    public void setBloqueioJudicial(BloqueioJudicial bloqueioJudicial) {
        this.bloqueioJudicial = bloqueioJudicial;
    }

    public UsuarioSistema getUsuarioHistorico() {
        return usuarioHistorico;
    }

    public void setUsuarioHistorico(UsuarioSistema usuarioHistorico) {
        this.usuarioHistorico = usuarioHistorico;
    }

    public SituacaoProcessoDebito getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcessoDebito situacao) {
        this.situacao = situacao;
    }

    public Date getDataHistorico() {
        return dataHistorico;
    }

    public void setDataHistorico(Date dataHistorico) {
        this.dataHistorico = dataHistorico;
    }
}
