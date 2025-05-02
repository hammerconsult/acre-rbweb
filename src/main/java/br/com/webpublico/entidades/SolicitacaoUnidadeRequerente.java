package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoSolicitacaoUnidadeRequerente;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 02/08/19.
 */

@Audited
@Etiqueta("Solicitação Unidade Requerente")
@Entity
@Table(name = "SOLICITACAOUNIDREQUERENTE")
public class SolicitacaoUnidadeRequerente extends SuperEntidade {

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
    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    private Date dataSolicitacao;

    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoSolicitacaoUnidadeRequerente situacao;

    @Tabelavel
    @Etiqueta("Usuário")
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Distribuidora")
    private UnidadeDistribuidora unidadeDistribuidora;

    @Etiqueta("Unidades Requerentes")
    @Invisivel
    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitacaoUnidadeRequerenteUnidade> unidadesRequerentes;

    @Transient
    @Tabelavel
    @Etiqueta("Unidade Distribuidora")
    private String descricaoUnidadeDistribuidora;

    public SolicitacaoUnidadeRequerente() {
        unidadesRequerentes = Lists.newArrayList();
    }

    public SolicitacaoUnidadeRequerente(SolicitacaoUnidadeRequerente solicitacaoUnidadeRequerente, HierarquiaOrganizacional hierarquiaOrganizacional) {
        setId(solicitacaoUnidadeRequerente.getId());
        setCodigo(solicitacaoUnidadeRequerente.getCodigo());
        setDataSolicitacao(solicitacaoUnidadeRequerente.getDataSolicitacao());
        setSituacao(solicitacaoUnidadeRequerente.getSituacao());
        setUsuarioSistema(solicitacaoUnidadeRequerente.getUsuarioSistema());
        setDescricaoUnidadeDistribuidora(hierarquiaOrganizacional.toString());
    }

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

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public UnidadeDistribuidora getUnidadeDistribuidora() {
        return unidadeDistribuidora;
    }

    public void setUnidadeDistribuidora(UnidadeDistribuidora unidadeDistribuidora) {
        this.unidadeDistribuidora = unidadeDistribuidora;
    }

    public List<SolicitacaoUnidadeRequerenteUnidade> getUnidadesRequerentes() {
        return unidadesRequerentes;
    }

    public void setUnidadesRequerentes(List<SolicitacaoUnidadeRequerenteUnidade> unidadesRequerentes) {
        this.unidadesRequerentes = unidadesRequerentes;
    }

    public SituacaoSolicitacaoUnidadeRequerente getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSolicitacaoUnidadeRequerente situacao) {
        this.situacao = situacao;
    }

    public String getDescricaoUnidadeDistribuidora() {
        return descricaoUnidadeDistribuidora;
    }

    public void setDescricaoUnidadeDistribuidora(String descricaoUnidadeDistribuidora) {
        this.descricaoUnidadeDistribuidora = descricaoUnidadeDistribuidora;
    }

    public boolean isEfetivada() {
        return SituacaoSolicitacaoUnidadeRequerente.EFETIVADA.equals(this.situacao);
    }

    public boolean isElaboracao() {
        return SituacaoSolicitacaoUnidadeRequerente.EM_ELABORACAO.equals(this.situacao);
    }

    @Override
    public String toString() {
        try {
            return "Nº " + getCodigo() +
                " - " + DataUtil.getDataFormatada(getDataSolicitacao()) +
                ", Distribuidora:  " + this.getUnidadeDistribuidora().getHierarquiaOrganizacional();
        } catch (NullPointerException e) {
            return "";
        }
    }
}
