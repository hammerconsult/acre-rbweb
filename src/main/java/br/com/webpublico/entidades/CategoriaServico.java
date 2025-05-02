/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Jaime
 */
@GrupoDiagrama(nome = "CadastroEconomico")
@Entity

@Audited
@Etiqueta("Lista de Serviços")
public class CategoriaServico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Tabelavel
    @Obrigatorio
    private String codigo;
    @Tabelavel
    @Column(length = 70)
    @Obrigatorio
    private String nome;
    @Etiqueta("Alíquota para ISS Mensal")
    private BigDecimal aliquotaIssHomologado;
    @Etiqueta("Alíquota para ISS Fixo")
    private BigDecimal aliquotaIssFixo;
    @Etiqueta("Alíquota ISS Estimado")
    private BigDecimal aliquotaIssEstimado;

    public CategoriaServico() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getAliquotaIssEstimado() {
        return aliquotaIssEstimado;
    }

    public void setAliquotaIssEstimado(BigDecimal aliquotaIssEstimado) {
        this.aliquotaIssEstimado = aliquotaIssEstimado;
    }

    public BigDecimal getAliquotaIssFixo() {
        return aliquotaIssFixo;
    }

    public void setAliquotaIssFixo(BigDecimal aliquotaIssFixo) {
        this.aliquotaIssFixo = aliquotaIssFixo;
    }

    public BigDecimal getAliquotaIssHomologado() {
        return aliquotaIssHomologado;
    }

    public void setAliquotaIssHomologado(BigDecimal aliquotaIssHomologado) {
        this.aliquotaIssHomologado = aliquotaIssHomologado;
    }

    public CategoriaServico(String codigo, String nome, BigDecimal aliquotaIssHomologado, BigDecimal aliquotaIssFixo, BigDecimal aliquotaIssEstimado) {
        this.codigo = codigo;
        this.nome = nome;
        this.aliquotaIssHomologado = aliquotaIssHomologado;
        this.aliquotaIssFixo = aliquotaIssFixo;
        this.aliquotaIssEstimado = aliquotaIssEstimado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CategoriaServico)) {
            return false;
        }
        CategoriaServico other = (CategoriaServico) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome;
    }
}
