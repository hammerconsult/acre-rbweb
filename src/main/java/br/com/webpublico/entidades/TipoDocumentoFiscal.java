/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Situacao;
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
 * @author claudio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
@Audited
@Entity
@Etiqueta("Tipo de Documento Fiscal")
public class TipoDocumentoFiscal extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Código")
    private String codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;

    @Etiqueta("Obrigar Chave de Acesso na Liquidação?")
    private Boolean obrigarChaveDeAcesso;

    @Etiqueta("Validar competência")
    private Boolean validarCopetencia;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Situação")
    @Tabelavel
    @Pesquisavel
    private Situacao situacao;

    public TipoDocumentoFiscal() {
        super();
        validarCopetencia = true;
        obrigarChaveDeAcesso = false;
        situacao = Situacao.ATIVO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getObrigarChaveDeAcesso() {
        return obrigarChaveDeAcesso;
    }

    public void setObrigarChaveDeAcesso(Boolean obrigarChaveDeAcesso) {
        this.obrigarChaveDeAcesso = obrigarChaveDeAcesso;
    }

    public Boolean getValidarCopetencia() {
        return validarCopetencia;
    }

    public void setValidarCopetencia(Boolean validarCopetencia) {
        this.validarCopetencia = validarCopetencia;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao + " - " + situacao.getDescricao();
    }
}
