package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAlienacao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 08/12/14
 * Time: 09:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Aprovação de Alienação de Bem Móvel")
public class AprovacaoAlienacao extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Aprovação")
    @Temporal(TemporalType.DATE)
    private Date dataEfetivacao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Responsável")
    @ManyToOne
    private UsuarioSistema responsavel;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Solicitação de Alienação")
    @ManyToOne
    private SolicitacaoAlienacao solicitacaoAlienacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoAlienacao situacaoEfetivacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    private Boolean alterarTipoGrupoBem;

    @Invisivel
    @OneToMany(mappedBy = "aprovacaoAlienacao", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ItemAprovacaoAlienacao> listaItensAprovacaoAlienacao;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "aprovacaoAlienacao")
    private List<AprovacaoAlienacaoAtoLegal> itensAtoLegal;

    public AprovacaoAlienacao() {
        super();
        this.alterarTipoGrupoBem = Boolean.FALSE;
        this.listaItensAprovacaoAlienacao = new ArrayList<>();
        this.itensAtoLegal = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public SolicitacaoAlienacao getSolicitacaoAlienacao() {
        return solicitacaoAlienacao;
    }

    public void setSolicitacaoAlienacao(SolicitacaoAlienacao solicitacaoAlienacao) {
        this.solicitacaoAlienacao = solicitacaoAlienacao;
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

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public SituacaoAlienacao getSituacaoEfetivacao() {
        return situacaoEfetivacao;
    }

    public void setSituacaoEfetivacao(SituacaoAlienacao situacaoEfetivacao) {
        this.situacaoEfetivacao = situacaoEfetivacao;
    }

    public List<ItemAprovacaoAlienacao> getListaItensAprovacaoAlienacao() {
        return listaItensAprovacaoAlienacao;
    }

    public void setListaItensAprovacaoAlienacao(List<ItemAprovacaoAlienacao> listaItensAprovacaoAlienacao) {
        this.listaItensAprovacaoAlienacao = listaItensAprovacaoAlienacao;
    }

    public Boolean getAlterarTipoGrupoBem() {
        return alterarTipoGrupoBem;
    }

    public void setAlterarTipoGrupoBem(Boolean alterarTipoGrupoBem) {
        this.alterarTipoGrupoBem = alterarTipoGrupoBem;
    }

    public List<AprovacaoAlienacaoAtoLegal> getItensAtoLegal() {
        return itensAtoLegal;
    }

    public void setItensAtoLegal(List<AprovacaoAlienacaoAtoLegal> itensAtoLegal) {
        this.itensAtoLegal = itensAtoLegal;
    }

    public Boolean getAprovada() {
        return SituacaoAlienacao.APROVADA.equals(situacaoEfetivacao);
    }

    public Boolean getRejeitada() {
        return SituacaoAlienacao.REJEITADA.equals(situacaoEfetivacao);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao + " - " + DataUtil.getDataFormatada(dataEfetivacao);
    }

    public boolean isSolicitacaoSelecionada() {
        return solicitacaoAlienacao != null;
    }

}
