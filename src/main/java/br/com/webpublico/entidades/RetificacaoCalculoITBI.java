package br.com.webpublico.entidades;

import javax.persistence.*;
import java.util.Date;

@Entity
public class RetificacaoCalculoITBI extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer numeroRetificacao;
    private String motivoRetificacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRetificacao;
    @ManyToOne
    private ProcessoCalculoITBI processoCalculoITBI;
    @ManyToOne
    private UsuarioSistema usuarioRetificacao;

    public RetificacaoCalculoITBI() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroRetificacao() {
        return numeroRetificacao;
    }

    public void setNumeroRetificacao(Integer numeroRetificacao) {
        this.numeroRetificacao = numeroRetificacao;
    }

    public String getMotivoRetificacao() {
        return motivoRetificacao;
    }

    public void setMotivoRetificacao(String motivoRetificacao) {
        this.motivoRetificacao = motivoRetificacao;
    }

    public Date getDataRetificacao() {
        return dataRetificacao;
    }

    public void setDataRetificacao(Date dataRetificacao) {
        this.dataRetificacao = dataRetificacao;
    }

    public ProcessoCalculoITBI getProcessoCalculoITBI() {
        return processoCalculoITBI;
    }

    public void setProcessoCalculoITBI(ProcessoCalculoITBI processoCalculoITBI) {
        this.processoCalculoITBI = processoCalculoITBI;
    }

    public UsuarioSistema getUsuarioRetificacao() {
        return usuarioRetificacao;
    }

    public void setUsuarioRetificacao(UsuarioSistema usuarioRetificacao) {
        this.usuarioRetificacao = usuarioRetificacao;
    }
}
