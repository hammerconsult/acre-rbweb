package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Log Alvará Rede Sim")
public class LogAlvaraRedeSim extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private ProcessoCalculoAlvara processoCalculoAlvara;
    @ManyToOne(cascade = CascadeType.ALL)
    private ArquivoLogAlvaraRedeSim arquivo;
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    private String ipMaquina;
    private Long idAlvara;
    private Long idDam;

    public LogAlvaraRedeSim() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public ProcessoCalculoAlvara getProcessoCalculoAlvara() {
        return processoCalculoAlvara;
    }

    public void setProcessoCalculoAlvara(ProcessoCalculoAlvara processoCalculoAlvara) {
        this.processoCalculoAlvara = processoCalculoAlvara;
    }

    public ArquivoLogAlvaraRedeSim getArquivo() {
        return arquivo;
    }

    public void setArquivo(ArquivoLogAlvaraRedeSim arquivo) {
        this.arquivo = arquivo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getIpMaquina() {
        return ipMaquina;
    }

    public void setIpMaquina(String ipMaquina) {
        this.ipMaquina = ipMaquina;
    }

    public Long getIdAlvara() {
        return idAlvara;
    }

    public void setIdAlvara(Long idAlvara) {
        this.idAlvara = idAlvara;
    }

    public Long getIdDam() {
        return idDam;
    }

    public void setIdDam(Long idDam) {
        this.idDam = idDam;
    }
}
