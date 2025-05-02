/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Etiqueta(value = "Célula De Informação")
public class CelulaDeInformacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private String codigo;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Início de Vigência")
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Final de Vigência")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @OneToMany(mappedBy = "celulaDeInformacao", cascade = CascadeType.ALL)
    private List<CampoCelulaDeInformacao> camposCelulaDeInformacao;

    public List<CampoCelulaDeInformacao> getCamposCelulaDeInformacao() {
        return camposCelulaDeInformacao;
    }

    public CelulaDeInformacao() {
        this.camposCelulaDeInformacao = Lists.newArrayList();
    }

    public CelulaDeInformacao(String codigo, String descricao, Date inicioVigencia, Date finalVigencia, List<CampoCelulaDeInformacao> camposCelulaDeInformacao) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.camposCelulaDeInformacao = camposCelulaDeInformacao;
    }

    public void setCamposCelulaDeInformacao(List<CampoCelulaDeInformacao> camposCelulaDeInformacao) {
        this.camposCelulaDeInformacao = camposCelulaDeInformacao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CelulaDeInformacao other = (CelulaDeInformacao) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
