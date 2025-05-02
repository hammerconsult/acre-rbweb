/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Leonardo
 */
@Entity
@Audited
@Etiqueta("Cancelamento de Nota Fiscal Avulsa")
public class NFSAvulsaCancelamento extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @OneToOne
    @Tabelavel
    @Etiqueta("Nota Fiscal Avulsa")
    private NFSAvulsa nfsAvulsa;
    @Etiqueta("Motivo")
    private String motivo;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel(ordemApresentacao = 8)
    @Etiqueta("Data do Cancelamento")
    private Date data;
    @ManyToOne
    private UsuarioSistema usuarioCancelamento;
    @Transient
    @Tabelavel
    @Etiqueta("Prestador")
    private String prestadorParaLista;
    @Transient
    @Tabelavel
    @Etiqueta("Tomador")
    private String tomadorParaLista;
    @Transient
    private Long criadoEm;


    public NFSAvulsaCancelamento(Long id, NFSAvulsa nfs, Exercicio ex, Pessoa prestador, Pessoa tomador, Date data) {
        this.id = id;
        this.nfsAvulsa = nfs;
        this.nfsAvulsa.setExercicio(ex);
        this.prestadorParaLista = prestador != null ? this.nfsAvulsa.getPrestador().getNomeCpfCnpj() : this.nfsAvulsa.getCmcPrestador().getCmcNomeCpfCnpj();
        this.tomadorParaLista = tomador != null ? this.nfsAvulsa.getTomador().getNomeCpfCnpj() : this.nfsAvulsa.getCmcTomador().getCmcNomeCpfCnpj();
        this.data = data;
    }

    public NFSAvulsaCancelamento() {
        this.criadoEm = System.nanoTime();
    }

    public UsuarioSistema getUsuarioCancelamento() {
        return usuarioCancelamento;
    }

    public void setUsuarioCancelamento(UsuarioSistema usuarioCancelamento) {
        this.usuarioCancelamento = usuarioCancelamento;
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public NFSAvulsa getNfsAvulsa() {
        return nfsAvulsa;
    }

    public void setNfsAvulsa(NFSAvulsa nfsAvulsa) {
        this.nfsAvulsa = nfsAvulsa;
    }

    @Override
    public String toString() {
        return "NFSAvulsaCancelamento{" +
                "nfsAvulsa=" + nfsAvulsa +
                ", id=" + id +
                '}';
    }
}
