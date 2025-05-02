/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.envers.Audited;

/**
 *
 * @author terminal4
 */
@Entity

@Audited
@Etiqueta("Situação do Parecer")
public class SituacaoTramite implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Nome")
    @Pesquisavel
    private String nome;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Parar Prazo")
    private Boolean paraPrazo;
    @Etiqueta("Enviar E-mail")
    private Boolean enviaEmail;
    @Etiqueta("Conteúdo do E-mail")
    private String conteudoEmail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConteudoEmail() {
        return conteudoEmail;
    }

    public void setConteudoEmail(String conteudoEmail) {
        this.conteudoEmail = conteudoEmail;
    }

    public Boolean getEnviaEmail() {
        return enviaEmail;
    }

    public void setEnviaEmail(Boolean enviaEmail) {
        this.enviaEmail = enviaEmail;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getParaPrazo() {
        return paraPrazo != null ? paraPrazo : Boolean.FALSE;
    }

    public void setParaPrazo(Boolean paraPrazo) {
        this.paraPrazo = paraPrazo;
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
        if (!(object instanceof SituacaoTramite)) {
            return false;
        }
        SituacaoTramite other = (SituacaoTramite) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome;
    }
}
