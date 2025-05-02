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
@Etiqueta("Baixa Motorista Auxiliar")
public class BaixaMotoristaAuxiliar implements Serializable {

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
    @Etiqueta("Motivo")
    private String motivo;
    @Etiqueta("Usuário Sistema")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Etiqueta("Motorista Auxiliar")
    @OneToOne
    private MotoristaAuxiliar motoristaAuxiliar;
    @Transient
    private Date vencimentoBaixa;
    /*Todo ano deve ser realizada a operação de baixa automática de todos os motoristas auxiliares de táxi e moto-táxi.
     Nesses casos, o atributo automática deve ser TRUE*/
    private Boolean automatica;
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

    @OneToOne
    private CalculoRBTrans calculoRBTrans;

    public BaixaMotoristaAuxiliar() {
        this.criadoEm = System.nanoTime();
    }

    public Date getVencimentoBaixa() {
        return vencimentoBaixa;
    }

    public void setVencimentoBaixa(Date vencimentoBaixa) {
        this.vencimentoBaixa = vencimentoBaixa;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getUsuarioLegado() {
        return usuarioLegado;
    }

    public void setUsuarioLegado(String usuarioLegado) {
        this.usuarioLegado = usuarioLegado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
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

    public MotoristaAuxiliar getMotoristaAuxiliar() {
        return motoristaAuxiliar;
    }

    public void setMotoristaAuxiliar(MotoristaAuxiliar motoristaAuxiliar) {
        this.motoristaAuxiliar = motoristaAuxiliar;
    }

    public CalculoRBTrans getCalculoRBTrans() {
        return calculoRBTrans;
    }

    public void setCalculoRBTrans(CalculoRBTrans calculoRBTrans) {
        this.calculoRBTrans = calculoRBTrans;
    }

    public Boolean getAutomatica() {
        return automatica;
    }

    public void setAutomatica(Boolean automatica) {
        this.automatica = automatica;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
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
        return "br.com.webpublico.entidades.BaixaMotoristaAuxiliar[ id=" + id + " ]";
    }

}
