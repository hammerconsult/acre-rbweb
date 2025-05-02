/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Concursos")
@Etiqueta("Avaliação de Concursos")
public class AvaliacaoConcurso extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Concurso")
    private Concurso concurso;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Avaliação")
    private Date dataAvaliacao;
    @ManyToOne
    @Etiqueta("Usuário Sistema")
    private UsuarioSistema usuarioSistema;
    @Etiqueta("Aprovado?")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private Boolean aprovado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public Date getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Date dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Boolean getAprovado() {
        return aprovado;
    }

    public void setAprovado(Boolean aprovado) {
        this.aprovado = aprovado;
    }

    @Override
    public String toString() {
        return concurso.toStringParaAprovacao() + " - Aprovado? "+ Util.converterBooleanSimOuNao(aprovado);
    }
}
