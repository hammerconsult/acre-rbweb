/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Claudio
 */
@Entity
@Audited
@Etiqueta(value = "Arquivo Pis/Pasep")
public class ArquivoPisPasep extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta(value = "Sequência")
    @Pesquisavel
    private Long sequencia;
    @Tabelavel
    @Etiqueta(value = "Conta Bancária")
    @ManyToOne
    @Pesquisavel
    private ContaBancariaEntidade contaBancariaEntidade;
    @Tabelavel
    @Etiqueta(value = "Usuário")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Tabelavel
    @Etiqueta(value = "Data")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataGerado;
    private String conteudo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataGerado() {
        return dataGerado;
    }

    public void setDataGerado(Date dataGerado) {
        this.dataGerado = dataGerado;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ArquivoPisPasep[ id=" + id + " ]";
    }

}
