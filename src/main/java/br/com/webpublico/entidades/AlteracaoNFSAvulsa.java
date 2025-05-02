/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Renato
 */
@Entity
@Audited
@Etiqueta("Alteração de Nota Fiscal Avulsa")
public class AlteracaoNFSAvulsa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data de Alteração")
    private Date dataAlteracao;
    private String motivo;
    @Transient
    private Long criadoEm;
    @OneToOne(cascade = CascadeType.ALL)
    @Tabelavel
    @Etiqueta("Nota Fiscal Avulsa")
    private NFSAvulsa nFSAvulsa;
    @Transient
    @Tabelavel
    @Etiqueta("Prestador")
    private String prestadorParaLista;
    @Transient
    @Tabelavel
    @Etiqueta("Tomador")
    private String tomadorParaLista;
    @Transient
    private Exercicio exercicio;

    public AlteracaoNFSAvulsa(Long id, NFSAvulsa nfs, Exercicio ex, Pessoa prestador, Pessoa tomador, Date data) {
        this.id = id;
        this.nFSAvulsa = nfs;
        this.nFSAvulsa.setExercicio(ex);
        this.prestadorParaLista = prestador != null ? this.nFSAvulsa.getPrestador().getNomeCpfCnpj() : this.nFSAvulsa.getCmcPrestador().getCmcNomeCpfCnpj();
        this.tomadorParaLista = tomador != null ? this.nFSAvulsa.getTomador().getNomeCpfCnpj() : this.nFSAvulsa.getCmcTomador().getCmcNomeCpfCnpj();
        this.dataAlteracao = data;
    }


    public AlteracaoNFSAvulsa() {
        criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
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

    public NFSAvulsa getNFSAvulsa() {
        return nFSAvulsa;
    }

    public void setNFSAvulsa(NFSAvulsa nFSAvulsa) {
        this.nFSAvulsa = nFSAvulsa;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
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
        return "br.com.webpublico.entidades.AlteracaoNFSAvulsa[ id=" + id + " ]";
    }
}
