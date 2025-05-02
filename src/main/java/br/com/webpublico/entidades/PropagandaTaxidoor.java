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
@Etiqueta("Propaganda Taxidoor")
public class PropagandaTaxidoor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VeiculoPermissionario veiculoPermissionario;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLancamento;
    /*
     * No sistema antigo do rbtrans, salvava-se somente o nome do usuário que
     * fez determinada alteração. Devido os usuário não terem sido migrados até
     * o momento, neste atributo será armazenado o nome dos mesmos. Ou seja,
     * este atributo deve conter o nome do usuário responsável por ter lançado
     * esta taxa.
     */
    private String usuarioQueLancou;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Etiqueta("Status Pagamento")
    @Enumerated(EnumType.STRING)
    private StatusPagamentoBaixa statusPagamento;

    public PropagandaTaxidoor() {
        this.criadoEm = System.nanoTime();
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuarioQueLancou() {
        return usuarioQueLancou;
    }

    public void setUsuarioQueLancou(String usuarioQueLancou) {
        this.usuarioQueLancou = usuarioQueLancou;
    }

    public VeiculoPermissionario getVeiculoPermissionario() {
        return veiculoPermissionario;
    }

    public void setVeiculoPermissionario(VeiculoPermissionario veiculoPermissionario) {
        this.veiculoPermissionario = veiculoPermissionario;
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
        return this.getClass().getAnnotation(Etiqueta.class).value() + " id = " + this.getId();
    }
}
