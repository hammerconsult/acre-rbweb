/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import net.bytebuddy.implementation.bind.annotation.Super;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Renato Qualquer documento que possua uma validade, seja ela informada
 *         descritivamente (this.validade) ou obrigando o usuário a informar um periodo
 *         (this.requerValidade), automaticamente a emissão será solicitada.
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Documento de Habilitação")
public class DoctoHabilitacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo Docto Habilitação")
    private TipoDoctoHabilitacao tipoDoctoHabilitacao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Requer Número")
    private Boolean requerNumero;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Requer Data de Emissão")
    private Boolean requerEmissao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Requer Validade")
    private Boolean requerValidade;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;

    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    public DoctoHabilitacao() {
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

    public Boolean getRequerEmissao() {
        return requerEmissao;
    }

    public void setRequerEmissao(Boolean requerEmissao) {
        this.requerEmissao = requerEmissao;
    }

    public Boolean getRequerNumero() {
        return requerNumero;
    }

    public void setRequerNumero(Boolean requerNumero) {
        this.requerNumero = requerNumero;
    }

    public Boolean getRequerValidade() {
        return requerValidade;
    }

    public void setRequerValidade(Boolean requerValidade) {
        this.requerValidade = requerValidade;
    }

    public TipoDoctoHabilitacao getTipoDoctoHabilitacao() {
        return tipoDoctoHabilitacao;
    }

    public void setTipoDoctoHabilitacao(TipoDoctoHabilitacao tipoDoctoHabilitacao) {
        this.tipoDoctoHabilitacao = tipoDoctoHabilitacao;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricaoReduzida() {
        if (descricao != null && descricao.length() > 100) {
            return descricao.substring(0, 99);
        }
        return descricao;
    }

    public String mostrarNoAutoComplete() {
        if (descricao != null && descricao.length() > 150) {
            return descricao.substring(0, 147) + "...";
        }
        return descricao;
    }
}
