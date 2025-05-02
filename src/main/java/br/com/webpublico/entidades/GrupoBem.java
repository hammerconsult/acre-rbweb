/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoUtilizacaoBem;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@GrupoDiagrama(nome = "Patrimonial")
@Entity
@Etiqueta(value = "Grupo Patrimonial")
@Audited
public class GrupoBem extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Código")
    @Obrigatorio
    private String codigo;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;

    /**
     * Auto-relacionamento que possibilita a criação do número desejado de níveis.
     */
    @Etiqueta(value = "Superior")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private GrupoBem superior;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    private SituacaoCadastralContabil ativoInativo;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Tipo do Bem")
    private TipoBem tipoBem;

    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Tipo de Utilização de Bens")
    private TipoUtilizacaoBem tipoUtilizacaoBem;

    public GrupoBem() {
        this.ativoInativo = SituacaoCadastralContabil.ATIVO;
    }

    public GrupoBem(String codigo, String descricao, GrupoBem superior) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.superior = superior;
        this.ativoInativo = SituacaoCadastralContabil.ATIVO;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoBem getSuperior() {
        return superior;
    }

    public void setSuperior(GrupoBem superior) {
        this.superior = superior;
    }

    public SituacaoCadastralContabil getAtivoInativo() {
        return ativoInativo;
    }

    public void setAtivoInativo(SituacaoCadastralContabil ativoInativo) {
        this.ativoInativo = ativoInativo;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public TipoUtilizacaoBem getTipoUtilizacaoBem() {
        return tipoUtilizacaoBem;
    }

    public void setTipoUtilizacaoBem(TipoUtilizacaoBem tipoUtilizacaoBem) {
        this.tipoUtilizacaoBem = tipoUtilizacaoBem;
    }

    @Override
    public String toString() {
        try {
            if (codigo != null && !codigo.isEmpty()) {
                return codigo + " - " + descricao;
            }
            return descricao;
        } catch (NullPointerException ex) {
            return "";
        }
    }
}
