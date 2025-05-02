package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Acompanhamento da meta física e financeira")
public class MedicaoProvisaoPPA extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    private ProvisaoPPA provisaoPPA;
    private BigDecimal acumulada;
    private String historico;
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    public MedicaoProvisaoPPA() {
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

    public ProvisaoPPA getProvisaoPPA() {
        return provisaoPPA;
    }

    public void setProvisaoPPA(ProvisaoPPA provisaoPPA) {
        this.provisaoPPA = provisaoPPA;
    }

    public BigDecimal getAcumulada() {
        return acumulada;
    }

    public void setAcumulada(BigDecimal acumulada) {
        this.acumulada = acumulada;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
