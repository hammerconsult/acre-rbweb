package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusProcessoEnglobado;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@GrupoDiagrama(nome = "Protocolo")
@Etiqueta("Protocolo/Processo Englobado")
@Entity
@Audited
public class ProcessoEnglobado extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Processo")
    private Processo processo;

    @ManyToOne
    @Etiqueta("Processo Englobado")
    private Processo englobado;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Data Registro")
    private Date dataRegistro;

    @Etiqueta("Motivo")
    private String motivo;

    @Etiqueta("Motivo Desmenbramento")
    private String motivoDesmembramento;
    @Enumerated(EnumType.STRING)

    @Etiqueta("Status")
    private StatusProcessoEnglobado status;

    @Etiqueta("Data do Desmembramento")
    private Date dataDesmembramento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public Processo getEnglobado() {
        return englobado;
    }

    public void setEnglobado(Processo englobado) {
        this.englobado = englobado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public StatusProcessoEnglobado getStatus() {
        return status;
    }

    public void setStatus(StatusProcessoEnglobado status) {
        this.status = status;
    }

    public String getMotivoDesmembramento() {
        return motivoDesmembramento;
    }

    public void setMotivoDesmembramento(String motivoDesmembramento) {
        this.motivoDesmembramento = motivoDesmembramento;
    }

    public Date getDataDesmembramento() {
        return dataDesmembramento;
    }

    public void setDataDesmembramento(Date dataDesmembramento) {
        this.dataDesmembramento = dataDesmembramento;
    }
}
