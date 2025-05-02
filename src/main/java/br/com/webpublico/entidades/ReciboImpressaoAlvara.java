/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Impressão de Alvara")
@Audited
@Entity
public class ReciboImpressaoAlvara implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long sequencia;
    @ManyToOne
    private Alvara alvara;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataImpressao;
    private String motivo;
    @Transient
    private Long criadoEm;
    private String nomeResposavel;
    private String cpfResposavel;
    private String rgResposavel;
    private String telefoneResposavel;

    public ReciboImpressaoAlvara() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alvara getAlvara() {
        return alvara;
    }

    public void setAlvara(Alvara alvara) {
        this.alvara = alvara;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataImpressao() {
        return dataImpressao;
    }

    public void setDataImpressao(Date dataImpressao) {
        this.dataImpressao = dataImpressao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public String getNomeResposavel() {
        return nomeResposavel;
    }

    public void setNomeResposavel(String nomeResposavel) {
        this.nomeResposavel = nomeResposavel;
    }

    public String getCpfResposavel() {
        return cpfResposavel;
    }

    public void setCpfResposavel(String cpfResposavel) {
        this.cpfResposavel = cpfResposavel;
    }

    public String getRgResposavel() {
        return rgResposavel;
    }

    public void setRgResposavel(String rgResposavel) {
        this.rgResposavel = rgResposavel;
    }

    public String getTelefoneResposavel() {
        return telefoneResposavel;
    }

    public void setTelefoneResposavel(String telefoneResposavel) {
        this.telefoneResposavel = telefoneResposavel;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
