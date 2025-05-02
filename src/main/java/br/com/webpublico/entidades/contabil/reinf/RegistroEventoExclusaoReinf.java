package br.com.webpublico.entidades.contabil.reinf;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class RegistroEventoExclusaoReinf extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Enumerated(EnumType.STRING)
    private Mes mes;
    @ManyToOne
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    private TipoArquivoReinf tipoArquivo;
    private String numeroRecibo;


    public RegistroEventoExclusaoReinf() {

    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoArquivoReinf getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(TipoArquivoReinf tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public String getNumeroRecibo() {
        return numeroRecibo;
    }

    public void setNumeroRecibo(String numeroRecibo) {
        this.numeroRecibo = numeroRecibo;
    }
}
