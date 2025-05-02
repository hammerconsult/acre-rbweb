/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Unidade de Medida")
public class UnidadeMedida extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;

    @Obrigatorio
    @Etiqueta("Sigla")
    private String sigla;

    @Obrigatorio
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Máscara Quantidade")
    private TipoMascaraUnidadeMedida mascaraQuantidade;

    @Obrigatorio
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Máscara Valor Unitário")
    private TipoMascaraUnidadeMedida mascaraValorUnitario;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ativo")
    private Boolean ativo;

    public UnidadeMedida() {
        ativo = Boolean.TRUE;
    }

    public UnidadeMedida(String descricao, String sigla) {
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
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

    public TipoMascaraUnidadeMedida getMascaraQuantidade() {
        return mascaraQuantidade;
    }

    public void setMascaraQuantidade(TipoMascaraUnidadeMedida mascaraUnidadeMedida) {
        this.mascaraQuantidade = mascaraUnidadeMedida;
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        return mascaraValorUnitario;
    }

    public void setMascaraValorUnitario(TipoMascaraUnidadeMedida mascaraValorUnitario) {
        this.mascaraValorUnitario = mascaraValorUnitario;
    }

    public String getDescricaoComponenteTip(){
        if (!Strings.isNullOrEmpty(sigla)){
            return sigla;
        }
        return descricao;
    }

    public String getDescricaoAndSigla(){
        if (!Strings.isNullOrEmpty(sigla)){
            return descricao + " - " + sigla;
        }
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
