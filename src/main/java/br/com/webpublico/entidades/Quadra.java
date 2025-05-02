/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity

@Audited
public class Quadra implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Invisivel
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código Interno")
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Setor")
    private Setor setor;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Número da Quadra")
    private String codigo;
    @Tabelavel
    @Column(length = 70)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição (Município)")
    private String descricao;
    @Tabelavel
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Loteamento")
    private Loteamento loteamento;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição (Loteamento)")
    private String descricaoLoteamento;



    public Quadra(Loteamento loteamento, String descricao) {
        this.loteamento = loteamento;
        this.descricao = descricao;
    }

    public Quadra() {
    }

    public Loteamento getLoteamento() {
        return loteamento;
    }

    public void setLoteamento(Loteamento loteamento) {
        this.loteamento = loteamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public String getCodigo() {
        return codigo;
    }

    public Integer getCodigoToInteger(){
        return Integer.valueOf(codigo);
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoString() {
        return StringUtils.leftPad(String.valueOf(codigo), 4, "0");
    }

    public String getDescricaoLoteamento() {
        return descricaoLoteamento;
    }

    public void setDescricaoLoteamento(String descricaoLoteamento) {
        this.descricaoLoteamento = descricaoLoteamento;
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
        if (!(object instanceof Quadra)) {
            return false;
        }
        Quadra other = (Quadra) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        if (descricao != null) {
            sb.append(" Descrição: ").append(descricao);
        }
        if (codigo != null) {
            sb.append(" Cód. Municipal: ").append(codigo);
        }
        if (descricaoLoteamento != null) {
            sb.append(" Cód. Loteamento: ").append(descricaoLoteamento);
        }

        return sb.toString();
    }
}
