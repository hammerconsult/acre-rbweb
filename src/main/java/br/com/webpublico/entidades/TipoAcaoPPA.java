/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAcao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author venon
 */
@Entity

@GrupoDiagrama(nome = "PPA")
@Audited
@Etiqueta("Tipo de Ação")
public class TipoAcaoPPA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Código")
    @Obrigatorio
    @Pesquisavel
    private Integer codigo;
    @Tabelavel
    @Etiqueta("Descrição")
    @Obrigatorio
    @Pesquisavel
    private String descricao;
    @Tabelavel
    @Etiqueta("Nomenclatura TCE")
    @Obrigatorio
    @Pesquisavel
    private String nomenclaturaTCE;
    @Etiqueta("Tipo Ação")
    @Enumerated(EnumType.STRING)
    private TipoAcao tipoAcao;
    @Transient
    private Long criadoEm;

    public TipoAcaoPPA() {
        criadoEm = System.nanoTime();
    }

    public TipoAcaoPPA(Integer codigo, String descricao, TipoAcao tipoAcao) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipoAcao = tipoAcao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomenclaturaTCE() {
        return nomenclaturaTCE;
    }

    public void setNomenclaturaTCE(String nomenclaturaTCE) {
        this.nomenclaturaTCE = nomenclaturaTCE;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoAcao getTipoAcao() {
        return tipoAcao;
    }

    public void setTipoAcao(TipoAcao tipoAcao) {
        this.tipoAcao = tipoAcao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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
        return codigo + " - " + descricao;
    }

    public String toStringAutoComplete() {
        String retorno = toString();
        return retorno.length() > 68 ? retorno.substring(0, 65) + "..." : retorno;
    }
}
