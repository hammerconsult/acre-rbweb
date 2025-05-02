/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusPagamentoBaixa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Baixa de Veículo do Permissionário")
@Table(name = "BAIXAVEICULOPERM")
public class BaixaVeiculoPermissionario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Realizada Em")
    @Obrigatorio
    private Date realizadaEm;
    @Etiqueta("Status do Pagamento")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private StatusPagamentoBaixa statusPagamento;
    private String motivo;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @OneToOne
    private VeiculoPermissionario veiculoPermissionario;
    private String migracaoChave;
    @Invisivel
    @Transient
    private Long criadoEm;

    /*
     * No sistema antigo do rbtrans, salvava-se somente o nome do usuário que fez determinada alteração.
     * Devido os usuário não terem sido migrados até o momento, neste atributo será armazenado o nome dos mesmos.
     * Ou seja, este atributo deve conter o nome do usuário responsável por esta baixa.
     */
    private String usuarioLegado;
    @ManyToOne
    private CalculoRBTrans calculoRBTrans;

    public BaixaVeiculoPermissionario() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public StatusPagamentoBaixa getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamentoBaixa statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getRealizadaEm() {
        return realizadaEm;
    }

    public void setRealizadaEm(Date realizadaEm) {
        this.realizadaEm = realizadaEm;
    }

    public VeiculoPermissionario getVeiculoPermissionario() {
        return veiculoPermissionario;
    }

    public void setVeiculoPermissionario(VeiculoPermissionario veiculoPermissionario) {
        this.veiculoPermissionario = veiculoPermissionario;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public CalculoRBTrans getCalculoRBTrans() {
        return calculoRBTrans;
    }

    public void setCalculoRBTrans(CalculoRBTrans calculoRBTrans) {
        this.calculoRBTrans = calculoRBTrans;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public String getUsuarioLegado() {
        return usuarioLegado;
    }

    public void setUsuarioLegado(String usuarioLegado) {
        this.usuarioLegado = usuarioLegado;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "BaixaVeiculoPermissionario[ id=" + id + " ]";
    }

}
