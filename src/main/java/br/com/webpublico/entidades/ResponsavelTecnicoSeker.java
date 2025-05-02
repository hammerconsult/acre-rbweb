/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@GrupoDiagrama(nome = "Tributário")
@Entity
@Audited
public class ResponsavelTecnicoSeker implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "IdResponsavelTecnico")
    Long id;
    String nome;
    String crea;
    String especializacao;
    String uf;
    @Column(name = "FK_especializacaoID")
    Integer especializacaoId;
    Integer orgao;
    String field8;
    String field9;

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

    public String getCrea() {
        return crea;
    }

    public void setCrea(String crea) {
        this.crea = crea;
    }

    public String getEspecializacao() {
        return especializacao;
    }

    public void setEspecializacao(String especializacao) {
        this.especializacao = especializacao;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Integer getEspecializacaoId() {
        return especializacaoId;
    }

    public void setEspecializacaoId(Integer especializacaoId) {
        this.especializacaoId = especializacaoId;
    }

    public Integer getOrgao() {
        return orgao;
    }

    public void setOrgao(Integer orgao) {
        this.orgao = orgao;
    }

    public String getField8() {
        return field8;
    }

    public void setField8(String field8) {
        this.field8 = field8;
    }

    public String getField9() {
        return field9;
    }

    public void setField9(String field9) {
        this.field9 = field9;
    }
}
