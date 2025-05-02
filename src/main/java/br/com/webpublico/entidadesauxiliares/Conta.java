/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.PlanoDeContas;
import br.com.webpublico.enums.TipoContaContabil;
import br.com.webpublico.util.anotacoes.Obrigatorio;

import javax.persistence.*;
import java.util.Date;

/**
 * @author gustavo
 */
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OrderBy
    @Obrigatorio
    private String codigo;
    @Obrigatorio
    private String rubrica;
    @Obrigatorio
    private String descricao;
    private String funcao;
    @Enumerated(EnumType.STRING)
    private TipoContaContabil tipoContaContabil;
    private Boolean ativa;
    @ManyToOne
    private Conta superior;
    @ManyToOne
    private PlanoDeContas planoDeContas;
    private Boolean permitirDesdobramento;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public Conta() {

    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNivel() {
        if (superior == null) {
            return 1;
        }
        return 1 + superior.getNivel();
    }

    public PlanoDeContas getPlanoDeContas() {
        return planoDeContas;
    }

    public void setPlanoDeContas(PlanoDeContas planoDeContas) {
        this.planoDeContas = planoDeContas;
    }

    public Conta getSuperior() {
        return superior;
    }

    public void setSuperior(Conta superior) {
        this.superior = superior;
    }

    public Boolean getPermitirDesdobramento() {
        return permitirDesdobramento;
    }

    public void setPermitirDesdobramento(Boolean permitirDesdobramento) {
        this.permitirDesdobramento = permitirDesdobramento;
    }

    public String getRubrica() {
        return rubrica;
    }

    public void setRubrica(String rubrica) {
        this.rubrica = rubrica;
    }

    public TipoContaContabil getTipoContaContabil() {
        return tipoContaContabil;
    }

    public void setTipoContaContabil(TipoContaContabil tipoContaContabil) {
        this.tipoContaContabil = tipoContaContabil;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Conta other = (Conta) obj;
        if (this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    public boolean equalsID(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Conta other = (Conta) obj;
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
        return codigo + " - " + descricao;
    }


}
