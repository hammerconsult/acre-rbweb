package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoBaixaPatrimonial;
import br.com.webpublico.enums.TipoBaixa;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 10/06/14
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
@Etiqueta("Solicitação de Baixa Patrimonial")
@Table(name = "SOLICITABAIXAPATRIMONIAL")
public class SolicitacaoBaixaPatrimonial extends SuperEntidade implements PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Etiqueta("Código")
    @CodigoGeradoAoConcluir
    @Pesquisavel
    @Tabelavel
    private Long codigo;

    @Etiqueta("Data da Solicitação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataSolicitacao;

    @Etiqueta("Usuário")
    @Obrigatorio
    @Tabelavel
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @Transient
    @Etiqueta("Unidade Administrativa")
    private HierarquiaOrganizacional hierarquiaAdministrativa;

    @ManyToOne
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeAdministrativa;

    @Transient
    @Etiqueta("Unidade Orçamentária")
    private HierarquiaOrganizacional hierarquiaOrcamentaria;


    @Etiqueta("Unidade Orçamentária")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Etiqueta("Tipo da Baixa")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private TipoBaixa tipoBaixa;

    @Etiqueta("Motivo da Baixa")
    @Obrigatorio
    private String motivo;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoBaixaPatrimonial situacao;

    @Etiqueta("Tipo de Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @Invisivel
    @OneToMany(mappedBy = "solicitacaoBaixa", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ItemSolicitacaoBaixaPatrimonial> listaItemSolicitacao;

    @ManyToOne
    @Etiqueta("Efetivação de Alienação")
    private LeilaoAlienacao leilaoAlienacao;

    @ManyToOne
    @Etiqueta("Efetivação de Incorporação")
    private EfetivacaoSolicitacaoIncorporacaoMovel efetivacaoIncorporacao;

    @ManyToOne
    @Etiqueta("Aquisição")
    private Aquisicao aquisicao;

    @Version
    private Long versao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataVersao;

    public SolicitacaoBaixaPatrimonial() {
        super();
        dataVersao = new Date();
        this.listaItemSolicitacao = Lists.newArrayList();
        this.setSituacao(SituacaoBaixaPatrimonial.EM_ELABORACAO);
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

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public List<ItemSolicitacaoBaixaPatrimonial> getListaItemSolicitacao() {
        return listaItemSolicitacao;
    }

    public void setListaItemSolicitacao(List<ItemSolicitacaoBaixaPatrimonial> listaItemSolicitacao) {
        this.listaItemSolicitacao = listaItemSolicitacao;
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

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        if (hierarquiaAdministrativa !=null){
            unidadeAdministrativa = hierarquiaAdministrativa.getSubordinada();
        }
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public TipoBaixa getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(TipoBaixa tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public SituacaoBaixaPatrimonial getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoBaixaPatrimonial situacao) {
        this.situacao = situacao;
    }

    public LeilaoAlienacao getLeilaoAlienacao() {
        return leilaoAlienacao;
    }

    public void setLeilaoAlienacao(LeilaoAlienacao leilaoAlienacao) {
        this.leilaoAlienacao = leilaoAlienacao;
    }

    public EfetivacaoSolicitacaoIncorporacaoMovel getEfetivacaoIncorporacao() {
        return efetivacaoIncorporacao;
    }

    public void setEfetivacaoIncorporacao(EfetivacaoSolicitacaoIncorporacaoMovel efetivacaoIncoporacao) {
        this.efetivacaoIncorporacao = efetivacaoIncoporacao;
    }

    public Aquisicao getAquisicao() {
        return aquisicao;
    }

    public void setAquisicao(Aquisicao aquisicao) {
        this.aquisicao = aquisicao;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + motivo + " - " + DataUtil.getDataFormatada(dataSolicitacao) + " - " + tipoBaixa.getDescricao();
        } catch (NullPointerException ex) {
            return "";
        }
    }

    public Boolean botaoEdita() {
        return SituacaoBaixaPatrimonial.EM_ELABORACAO.equals(situacao);
    }

    public Boolean botaoConcluir() {
        return SituacaoBaixaPatrimonial.EM_ELABORACAO.equals(situacao);
    }

    public boolean isTipoBaixaAlienacao() {
        return TipoBaixa.ALIENACAO.equals(this.getTipoBaixa());
    }

    public boolean isBaixaPorIncorporacaoIndevida() {
        return TipoBaixa.INCORPORACAO_INDEVIDA.equals(this.getTipoBaixa());
    }

    public boolean isBaixaPorAquisicaoIndevida() {
        return TipoBaixa.AQUISICAO_INDEVIDA.equals(this.getTipoBaixa());
    }
}
