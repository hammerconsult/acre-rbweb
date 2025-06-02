package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class FilaProcessamentoFolha extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VinculoFP vinculoFP;
    @ManyToOne
    private UsuarioSistema usuario;
    @ManyToOne
    private FolhaDePagamento folhaDePagamento;
    @Enumerated(EnumType.STRING)
    private SituacaoCalculoFP situacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date incluidoEm;
    @Temporal(TemporalType.TIMESTAMP)
    private Date finalizadoEm;
    @ManyToOne
    private DetalhesCalculoRH detalhesCalculoRH;

    public FilaProcessamentoFolha() {
        incluidoEm = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public SituacaoCalculoFP getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCalculoFP situacao) {
        this.situacao = situacao;
    }

    public Date getIncluidoEm() {
        return incluidoEm;
    }

    public void setIncluidoEm(Date incluidoEm) {
        this.incluidoEm = incluidoEm;
    }

    public Date getFinalizadoEm() {
        return finalizadoEm;
    }

    public void setFinalizadoEm(Date finalizadoEm) {
        this.finalizadoEm = finalizadoEm;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public DetalhesCalculoRH getDetalhesCalculoRH() {
        return detalhesCalculoRH;
    }

    public void setDetalhesCalculoRH(DetalhesCalculoRH detalhesCalculoRH) {
        this.detalhesCalculoRH = detalhesCalculoRH;
    }
}
