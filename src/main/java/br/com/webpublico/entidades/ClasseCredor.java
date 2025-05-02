/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoClasseCredor;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.envers.Audited;

/**
 * @author major
 */
@Entity

@Audited
@Etiqueta("Classe de Pessoa")
public class ClasseCredor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Obrigatorio
    @Tabelavel
    private String codigo;
    @Etiqueta("Descrição")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo Classe de Pessoa")
    private TipoClasseCredor tipoClasseCredor;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoCadastralContabil ativoInativo;


    public ClasseCredor() {
        ativoInativo = SituacaoCadastralContabil.ATIVO;
    }

    public ClasseCredor(String codigo, String descricao, TipoClasseCredor tipoClasseCredor, OperacaoClasseCredor operacaoClasseCredor, SituacaoCadastralContabil ativoInativo) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipoClasseCredor = tipoClasseCredor;
        this.ativoInativo = ativoInativo;
    }

    public SituacaoCadastralContabil getAtivoInativo() {
        return ativoInativo;
    }

    public void setAtivoInativo(SituacaoCadastralContabil ativoInativo) {
        this.ativoInativo = ativoInativo;
    }

    public ClasseCredor(String descricao) {
        this.descricao = descricao;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoClasseCredor getTipoClasseCredor() {
        return tipoClasseCredor;
    }

    public void setTipoClasseCredor(TipoClasseCredor tipoClasseCredor) {
        this.tipoClasseCredor = tipoClasseCredor;
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
        if (!(object instanceof ClasseCredor)) {
            return false;
        }
        ClasseCredor other = (ClasseCredor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (codigo != null && descricao != null) {
            return retorno + codigo + " - " + descricao;
        }
        if (!"".equals(retorno)) {
            return retorno;
        }
        return descricao;
    }
}
