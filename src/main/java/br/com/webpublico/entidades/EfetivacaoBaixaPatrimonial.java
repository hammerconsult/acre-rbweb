package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoBaixaPatrimonial;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
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
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta("Efetivação de Baixa Patrimonial")
@Entity
@Audited
public class EfetivacaoBaixaPatrimonial extends SuperEntidade implements PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Etiqueta("Código")
    @Pesquisavel
    @Tabelavel
    @CodigoGeradoAoConcluir
    private Long codigo;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Data da Efetivação")
    @Temporal(TemporalType.DATE)
    private Date dataEfetivacao;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Solicitação de Baixa")
    @OneToOne
    private ParecerBaixaPatrimonial parecerBaixaPatrimonial;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Usuário")
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Lista de Bens Baixados")
    @Invisivel
    @OneToMany(mappedBy = "efetivacaoBaixa")
    private List<ItemEfetivacaoBaixaPatrimonial> listaItemEfetivacao;

    @Etiqueta("Lista de Lote Alienação")
    @Invisivel
    @OneToMany(mappedBy = "efetivacaoBaixaPatrimonial")
    private List<EfetivacaoBaixaLote> listaLoteAlienacao;

    @Etiqueta("Ganhos/Perdas")
    @Invisivel
    @OneToMany(mappedBy = "efetivacaoBaixaPatrimonial")
    private List<GanhoPerdaPatrimonial> ganhosPerdasPatrimoniais;

    @Etiqueta("Apurações Valor Líquido Depreciação")
    @Invisivel
    @OneToMany(mappedBy = "efetivacaoBaixaPatrimonial")
    private List<ApuracaoValorLiquidoDepreciacao> apuracoesValorLiquidoDepreciacoes;

    @Etiqueta("Apurações Valor Líquido Amortização")
    @Invisivel
    @OneToMany(mappedBy = "efetivacaoBaixaPatrimonial")
    private List<ApuracaoValorLiquidoAmortizacao> apuracoesValorLiquidoAmortizacoes;

    @Etiqueta("Apurações Valor Líquido Exaustão")
    @Invisivel
    @OneToMany(mappedBy = "efetivacaoBaixaPatrimonial")
    private List<ApuracaoValorLiquidoExaustao> apuracoesValorLiquidoExaustoes;

    @Etiqueta("Apurações Valor Líquido Ajuste")
    @Invisivel
    @OneToMany(mappedBy = "efetivacaoBaixaPatrimonial")
    private List<ApuracaoValorLiquidoAjuste> apuracoesValorLiquidoAjustes;

    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Bem")
    private TipoBem tipoBem;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoBaixaPatrimonial situacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoBaixaPatrimonial")
    private List<LogErroEfetivacaoBaixaBem> logErrosEfetivacao;

    @Transient
    private Boolean simularContabilizacao;

    public EfetivacaoBaixaPatrimonial() {
        super();
        this.situacao = SituacaoBaixaPatrimonial.EM_ELABORACAO;
        this.listaLoteAlienacao = Lists.newArrayList();
        this.logErrosEfetivacao = Lists.newArrayList();
        this.ganhosPerdasPatrimoniais = Lists.newArrayList();
        this.apuracoesValorLiquidoDepreciacoes = Lists.newArrayList();
        this.apuracoesValorLiquidoAmortizacoes = Lists.newArrayList();
        this.apuracoesValorLiquidoExaustoes = Lists.newArrayList();
        this.apuracoesValorLiquidoAjustes = Lists.newArrayList();
    }

    public List<GanhoPerdaPatrimonial> getGanhosPerdasPatrimoniais() {
        return ganhosPerdasPatrimoniais;
    }

    public void setGanhosPerdasPatrimoniais(List<GanhoPerdaPatrimonial> ganhosPerdasPatrimoniais) {
        this.ganhosPerdasPatrimoniais = ganhosPerdasPatrimoniais;
    }

    public List<ApuracaoValorLiquidoDepreciacao> getApuracoesValorLiquidoDepreciacoes() {
        return apuracoesValorLiquidoDepreciacoes;
    }

    public void setApuracoesValorLiquidoDepreciacoes(List<ApuracaoValorLiquidoDepreciacao> apuracoesValorLiquidoDepreciacoes) {
        this.apuracoesValorLiquidoDepreciacoes = apuracoesValorLiquidoDepreciacoes;
    }

    public List<ApuracaoValorLiquidoAmortizacao> getApuracoesValorLiquidoAmortizacoes() {
        return apuracoesValorLiquidoAmortizacoes;
    }

    public void setApuracoesValorLiquidoAmortizacoes(List<ApuracaoValorLiquidoAmortizacao> apuracoesValorLiquidoAmortizacoes) {
        this.apuracoesValorLiquidoAmortizacoes = apuracoesValorLiquidoAmortizacoes;
    }

    public List<ApuracaoValorLiquidoExaustao> getApuracoesValorLiquidoExaustoes() {
        return apuracoesValorLiquidoExaustoes;
    }

    public void setApuracoesValorLiquidoExaustoes(List<ApuracaoValorLiquidoExaustao> apuracoesValorLiquidoExaustoes) {
        this.apuracoesValorLiquidoExaustoes = apuracoesValorLiquidoExaustoes;
    }

    public List<ApuracaoValorLiquidoAjuste> getApuracoesValorLiquidoAjustes() {
        return apuracoesValorLiquidoAjustes;
    }

    public void setApuracoesValorLiquidoAjustes(List<ApuracaoValorLiquidoAjuste> apuracoesValorLiquidoAjustes) {
        this.apuracoesValorLiquidoAjustes = apuracoesValorLiquidoAjustes;
    }

    public SituacaoBaixaPatrimonial getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoBaixaPatrimonial situacao) {
        this.situacao = situacao;
    }

    public List<LogErroEfetivacaoBaixaBem> getLogErrosEfetivacao() {
        return logErrosEfetivacao;
    }

    public void setLogErrosEfetivacao(List<LogErroEfetivacaoBaixaBem> logErrosEfetivacao) {
        this.logErrosEfetivacao = logErrosEfetivacao;
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

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public List<ItemEfetivacaoBaixaPatrimonial> getListaItemEfetivacao() {
        return listaItemEfetivacao;
    }

    public void setListaItemEfetivacao(List<ItemEfetivacaoBaixaPatrimonial> listaItemEfetivacao) {
        this.listaItemEfetivacao = listaItemEfetivacao;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public ParecerBaixaPatrimonial getParecerBaixaPatrimonial() {
        return parecerBaixaPatrimonial;
    }

    public void setParecerBaixaPatrimonial(ParecerBaixaPatrimonial parecerBaixaPatrimonial) {
        this.parecerBaixaPatrimonial = parecerBaixaPatrimonial;
    }

    public List<EfetivacaoBaixaLote> getListaLoteAlienacao() {
        return listaLoteAlienacao;
    }

    public void setListaLoteAlienacao(List<EfetivacaoBaixaLote> listaLoteAlienacao) {
        this.listaLoteAlienacao = listaLoteAlienacao;
    }

    public boolean isSolicitacaoBaixaPorAlienacao() {
        return getParecerBaixaPatrimonial() != null
            && getParecerBaixaPatrimonial().getSolicitacaoBaixa() != null
            && getParecerBaixaPatrimonial().getSolicitacaoBaixa().isTipoBaixaAlienacao();
    }

    public boolean isAguardandoEfetivacao() {
        return SituacaoBaixaPatrimonial.AGUARDANDO_EFETIVACAO.equals(this.situacao);
    }

    public boolean isFinalizada() {
        return SituacaoBaixaPatrimonial.FINALIZADA.equals(this.situacao);
    }

    public String getHistoricoRazaoItemBaixa() {
        try {
            return "Efetivação da Baixa de Bem Móvel por Desincorporação nº " + getCodigo() + " - " + getHistorico() + ".";
        } catch (NullPointerException ex) {
            return "Efetivação da Baixa Móvel por Desincorporação";
        }
    }

    public String getHistoricoRazaoPerda(String numeroEfetivacaoBaixa) {
        return "Registro de Perda da Efetivação da Baixa de Bens Móveis nº " + numeroEfetivacaoBaixa + ".";
    }

    public String getHistoricoRazaoGanho(String numeroEfetivacaoBaixa) {
        return "Registro de Ganho da Efetivação da Baixa de Bens Móveis nº " + numeroEfetivacaoBaixa + ".";
    }

    public boolean isEfetivacaoPorAlienacao() {
        return getParecerBaixaPatrimonial() != null
            && getParecerBaixaPatrimonial().getSolicitacaoBaixa() != null
            && getParecerBaixaPatrimonial().getSolicitacaoBaixa().isTipoBaixaAlienacao();
    }

}
