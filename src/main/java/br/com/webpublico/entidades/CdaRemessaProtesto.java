package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class CdaRemessaProtesto extends SuperEntidade implements Comparable<CdaRemessaProtesto> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private RemessaProtesto remessaProtesto;
    @ManyToOne
    private CertidaoDividaAtiva cda;
    private String codigoProcesso;
    private String cadastroProcesso;
    private String nossoNumero;
    private String usuarioUltimaAtualizacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date ultimaAtualizacao;
    private String situacaoProtesto;
    @Transient
    private List<ResultadoParcela> parcelasDaCda;
    @OneToMany(mappedBy = "cdaRemessaProtesto", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<LogCdaRemessaProtesto> logs;

    public CdaRemessaProtesto() {
        this.parcelasDaCda = Lists.newArrayList();
        this.logs = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RemessaProtesto getRemessaProtesto() {
        return remessaProtesto;
    }

    public void setRemessaProtesto(RemessaProtesto remessaProtesto) {
        this.remessaProtesto = remessaProtesto;
    }

    public String getCodigoProcesso() {
        return codigoProcesso;
    }

    public void setCodigoProcesso(String codigoProcesso) {
        this.codigoProcesso = codigoProcesso;
    }

    public String getCadastroProcesso() {
        return cadastroProcesso;
    }

    public void setCadastroProcesso(String cadastroProcesso) {
        this.cadastroProcesso = cadastroProcesso;
    }

    public String getUsuarioUltimaAtualizacao() {
        return usuarioUltimaAtualizacao;
    }

    public void setUsuarioUltimaAtualizacao(String usuarioUltimaAtualizacao) {
        this.usuarioUltimaAtualizacao = usuarioUltimaAtualizacao;
    }

    public Date getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(Date ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public String getSituacaoProtesto() {
        return situacaoProtesto;
    }

    public void setSituacaoProtesto(String situacaoProtesto) {
        this.situacaoProtesto = situacaoProtesto;
    }

    public String getNossoNumero() {
        return nossoNumero;
    }

    public void setNossoNumero(String nossoNumero) {
        this.nossoNumero = nossoNumero;
    }

    public CertidaoDividaAtiva getCda() {
        return cda;
    }

    public void setCda(CertidaoDividaAtiva cda) {
        this.cda = cda;
    }

    public List<ResultadoParcela> getParcelasDaCda() {
        return parcelasDaCda;
    }

    public void setParcelasDaCda(List<ResultadoParcela> parcelasDaCda) {
        this.parcelasDaCda = parcelasDaCda;
    }

    public List<LogCdaRemessaProtesto> getLogs() {
        return logs;
    }

    public void setLogs(List<LogCdaRemessaProtesto> logs) {
        this.logs = logs;
    }

    @Override
    public int compareTo(CdaRemessaProtesto o) {
        int i = this.getCodigoProcesso().compareTo(o.getCodigoProcesso());
        if (i == 0 && this.getCda() != null && o.getCda() != null) {
            i = this.getCda().getExercicio().compareTo(o.getCda().getExercicio());
        }
        if (i == 0 && this.getCda() != null && o.getCda() != null) {
            i = this.getCda().getNumero().compareTo(o.getCda().getNumero());
        }
        return i;
    }
}
