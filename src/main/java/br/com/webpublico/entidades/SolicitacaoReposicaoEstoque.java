/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Solicitação de Reposição de Estoque")
@Table(name = "SOLICITACAOREPESTOQUE")
public class SolicitacaoReposicaoEstoque extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Etiqueta("Número")
    @Tabelavel
    @Obrigatorio
    private Long numero;

    @Pesquisavel
    @Etiqueta("Data")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Obrigatorio
    private Date dataSolicitacao;

    @Pesquisavel
    @ManyToOne
    @Etiqueta("Usuário")
    @Tabelavel
    @Obrigatorio
    private UsuarioSistema usuarioSistema;

    @Pesquisavel
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Itens Solicitados")
    @OneToMany(mappedBy = "solicitacaoRepEstoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSolicitacaoReposicaoEstoque> itensSolicitados;

    public SolicitacaoReposicaoEstoque() {
        this.itensSolicitados = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public List<ItemSolicitacaoReposicaoEstoque> getItensSolicitados() {
        return itensSolicitados;
    }

    public void setItensSolicitados(List<ItemSolicitacaoReposicaoEstoque> itensSolicitados) {
        this.itensSolicitados = itensSolicitados;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    @Override
    public String toString() {
        return "Solicitação de Reposição de Estoque N°: "+numero;
    }
}
