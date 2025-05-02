package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoSolicitacaoUnidadeRequerente;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mga on 02/08/19.
 */

@Audited
@Etiqueta("Efetivação Unidade Requerente")
@Entity
@Table(name = "EFETIVACAOUNIDADEREQUERENT")
public class EfetivacaoUnidadeRequerente extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;

    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data")
    private Date dataEfetivacao;

    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Situação")
    private SituacaoSolicitacaoUnidadeRequerente situacao;

    @Tabelavel
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Solicitação")
    private SolicitacaoUnidadeRequerente solicitacao;

    @Transient
    @Tabelavel
    @Etiqueta("Solicitação")
    private String descricaoSolicitacao;

    public EfetivacaoUnidadeRequerente() {
    }

    public EfetivacaoUnidadeRequerente(EfetivacaoUnidadeRequerente efetivacao, HierarquiaOrganizacional hierarquiaOrganizacional) {
        setId(efetivacao.getId());
        setCodigo(efetivacao.getCodigo());
        setDataEfetivacao(efetivacao.getDataEfetivacao());
        setSituacao(efetivacao.getSituacao());
        setUsuarioSistema(efetivacao.getUsuarioSistema());
        setDescricaoSolicitacao(geDescricaoSolicitacao(efetivacao, hierarquiaOrganizacional));
    }

    private String geDescricaoSolicitacao(EfetivacaoUnidadeRequerente efetivacao, HierarquiaOrganizacional hierarquiaOrganizacional) {
        return "Nº " + efetivacao.getSolicitacao().getCodigo() + " - " + DataUtil.getDataFormatada(efetivacao.getSolicitacao().getDataSolicitacao()) +
            ", Distribuidora:  " + hierarquiaOrganizacional.toString();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public SituacaoSolicitacaoUnidadeRequerente getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSolicitacaoUnidadeRequerente situacao) {
        this.situacao = situacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public SolicitacaoUnidadeRequerente getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoUnidadeRequerente solicitacao) {
        this.solicitacao = solicitacao;
    }

    public String getDescricaoSolicitacao() {
        return descricaoSolicitacao;
    }

    public void setDescricaoSolicitacao(String descricaoSolicitacao) {
        this.descricaoSolicitacao = descricaoSolicitacao;
    }

    @Override
    public String toString() {
        try {
            return solicitacao.toString();
        } catch (NullPointerException e) {
            return "";
        }
    }
}
