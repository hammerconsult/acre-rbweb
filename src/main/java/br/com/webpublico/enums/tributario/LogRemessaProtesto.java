package br.com.webpublico.enums.tributario;

import br.com.webpublico.entidades.RemessaProtesto;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class LogRemessaProtesto extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private RemessaProtesto remessaProtesto;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLog;
    @ManyToOne
    private UsuarioSistema usuarioLog;

    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespostaLogValidacao> validacoes;

    public LogRemessaProtesto() {
        this.validacoes = Lists.newArrayList();
    }

    public LogRemessaProtesto(RemessaProtesto remessa) {
        this();
        this.remessaProtesto = remessa;
        this.dataLog = remessa.getEnvioRemessa();
        this.usuarioLog = remessa.getResponsavelRemessa();
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

    public Date getDataLog() {
        return dataLog;
    }

    public void setDataLog(Date dataLog) {
        this.dataLog = dataLog;
    }

    public UsuarioSistema getUsuarioLog() {
        return usuarioLog;
    }

    public void setUsuarioLog(UsuarioSistema usuarioLog) {
        this.usuarioLog = usuarioLog;
    }

    public List<RespostaLogValidacao> getValidacoes() {
        return validacoes;
    }

    public void setValidacoes(List<RespostaLogValidacao> validacoes) {
        this.validacoes = validacoes;
    }
}
