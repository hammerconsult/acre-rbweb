package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class AlteracaoDataArbitramento extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private AcaoFiscal acaoFiscal;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAlteracao;
    private Date dataArbitramentoAnterior;
    private Date dataArbitramento;

    public AlteracaoDataArbitramento() {
        this.dataAlteracao = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public AcaoFiscal getAcaoFiscal() {
        return acaoFiscal;
    }

    public void setAcaoFiscal(AcaoFiscal acaoFiscal) {
        this.acaoFiscal = acaoFiscal;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public Date getDataArbitramentoAnterior() {
        return dataArbitramentoAnterior;
    }

    public void setDataArbitramentoAnterior(Date dataArbitramentoAnterior) {
        this.dataArbitramentoAnterior = dataArbitramentoAnterior;
    }

    public Date getDataArbitramento() {
        return dataArbitramento;
    }

    public void setDataArbitramento(Date dataArbitramento) {
        this.dataArbitramento = dataArbitramento;
    }
}
