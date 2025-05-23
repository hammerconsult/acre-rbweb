package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAlienacao;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 04/11/14
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Solicitação de Alienação")
public class SolicitacaoAlienacao extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    @CodigoGeradoAoConcluir
    private Long codigo;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data")
    private Date dataSolicitacao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoAlienacao situacao;

    @Etiqueta("Motivo da Rejeição")
    private String motivoRejeicao;

    @Invisivel
    @OneToMany(mappedBy = "solicitacaoAlienacao")
    private List<ItemSolicitacaoAlienacao> itensLoteSolicitacaoAlienacao;

    @Obrigatorio
    @Etiqueta("Tipo de Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Etiqueta("Unidade Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeAdministrativa;

    @Etiqueta("Unidade Administrativa")
    @Tabelavel
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Version
    private Long versao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataVersao;


    public SolicitacaoAlienacao() {
        dataVersao = new Date();
        situacao = SituacaoAlienacao.EM_ELABORACAO;
        itensLoteSolicitacaoAlienacao = new ArrayList();
    }

    public static SituacaoAlienacao[] situacoesParaEfetivacao() {
        SituacaoAlienacao situacoes[] = {SituacaoAlienacao.REJEITADA, SituacaoAlienacao.APROVADA};
        return situacoes;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public Date getDataVersao() {
        return dataVersao;
    }

    public void setDataVersao(Date dataVersao) {
        this.dataVersao = dataVersao;
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

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
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

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ItemSolicitacaoAlienacao> getItensLoteSolicitacaoAlienacao() {
        return itensLoteSolicitacaoAlienacao;
    }

    public void setItensLoteSolicitacaoAlienacao(List<ItemSolicitacaoAlienacao> itensLoteSolicitacaoAlienacao) {
        this.itensLoteSolicitacaoAlienacao = itensLoteSolicitacaoAlienacao;
    }

    public SituacaoAlienacao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAlienacao situacao) {
        this.situacao = situacao;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao + " - " + DataUtil.getDataFormatada(this.dataSolicitacao);
    }

    public Boolean foiRejeitada() {
        return SituacaoAlienacao.REJEITADA.equals(situacao);
    }

    public Boolean emElaboracao() {
        return SituacaoAlienacao.EM_ELABORACAO.equals(situacao);
    }
}
