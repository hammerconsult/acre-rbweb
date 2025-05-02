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
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Vistoria Veículo")
public class VistoriaVeiculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Realizada em")
    private Date realizadaEm;
    @Etiqueta("Status Pagamento")
    @Enumerated(EnumType.STRING)
    private StatusPagamentoBaixa statusPagamento;
    @Etiqueta("Usuario Sistema")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private VeiculoPermissionario veiculoPermissionario;
    @Invisivel
    @Transient
    private Long criadoEm;
    @OneToOne
    private CalculoRBTrans calculoRBTrans;

    /*
     * No sistema antigo do rbtrans, salvava-se somente o nome do usuário que fez determinada alteração.
     * Devido os usuário não terem sido migrados até o momento, neste atributo será armazenado o nome dos mesmos.
     * Ou seja, este atributo deve conter o nome do usuário responsável por ter lançado esta taxa.
     */
    private String usuarioQueLancou;

    public VistoriaVeiculo() {
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRealizadaEm() {
        return realizadaEm;
    }

    public void setRealizadaEm(Date realizadaEm) {
        this.realizadaEm = realizadaEm;
    }

    public StatusPagamentoBaixa getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamentoBaixa statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public VeiculoPermissionario getVeiculoPermissionario() {
        return veiculoPermissionario;
    }

    public void setVeiculoPermissionario(VeiculoPermissionario veiculoPermissionario) {
        this.veiculoPermissionario = veiculoPermissionario;
    }

    public String getUsuarioQueLancou() {
        return usuarioQueLancou;
    }

    public void setUsuarioQueLancou(String usuarioQueLancou) {
        this.usuarioQueLancou = usuarioQueLancou;
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

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.VistoriaVeiculo[ id=" + id + " ]";
    }
}
