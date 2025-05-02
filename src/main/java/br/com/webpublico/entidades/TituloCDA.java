/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.envers.Audited;

/**
 *
 * @author Arthur
 */
@Audited
@Entity
public class TituloCDA extends BemCDA implements Serializable {

    private static final long serialVersionUID = 1L;
    private String descricao;
    private Integer quantidade;
    private String localizacao;
    @Temporal(TemporalType.DATE)
    private Date dataLocalizacao;
    private String complemento;

    public TituloCDA() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Date getDataLocalizacao() {
        return dataLocalizacao;
    }

    public void setDataLocalizacao(Date dataLocalizacao) {
        this.dataLocalizacao = dataLocalizacao;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

}
