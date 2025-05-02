package br.com.webpublico.entidades;

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
 * User: BRAINIAC
 * Date: 26/12/13
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Table(name = "LOTEEFETTRANSFBEM")
@Etiqueta(value = "Efetivação de Transferência de Bem", genero = "M")
public class LoteEfetivacaoTransferenciaBem extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;

    @Pesquisavel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema usuarioSistema;

    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Solicitação de Transferência")
    private LoteTransferenciaBem solicitacaoTransferencia;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Efetivação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEfetivacao;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoTransferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaBemConcedida> efetivacoesConcedidas;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoTransferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaDepreciacaoConcedida> depreciacoesConcedidas;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoTransferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaAmortizacaoConcedida> amortizacoesConcedidas;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoTransferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaExaustaoConcedida> exaustoesConcedidas;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoTransferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaReducaoConcedida> ajustesConcedidos;

    @Invisivel
    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EfetivacaoTransferenciaBem> efetivacoesRecebidas;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoTransferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaDepreciacaoRecebida> depreciacoesRecebidas;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoTransferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaAmortizacaoRecebida> amortizacoesRecebidas;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoTransferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaExaustaoRecebida> exaustoesRecebidas;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoTransferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaReducaoRecebida> ajustesRecebidos;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Bem")
    private TipoBem tipoBem;

    @Transient
    @Tabelavel
    @Etiqueta("Usuário da Efetivação")
    private String nomeResponsavel;

    @Transient
    @Tabelavel
    @Etiqueta("Unidade Administrativa de Destino")
    private String descricaoUnidadeDestino;

    @Transient
    private HierarquiaOrganizacional unidadeDestino;

    public LoteEfetivacaoTransferenciaBem() {
        super();
        efetivacoesRecebidas = new ArrayList<>();
    }

    public LoteTransferenciaBem getSolicitacaoTransferencia() {
        return solicitacaoTransferencia;
    }

    public void setSolicitacaoTransferencia(LoteTransferenciaBem solicitacaoTransferencia) {
        this.solicitacaoTransferencia = solicitacaoTransferencia;
    }

    public List<EfetivacaoTransferenciaBem> getEfetivacoesRecebidas() {
        return efetivacoesRecebidas;
    }

    public void setEfetivacoesRecebidas(List<EfetivacaoTransferenciaBem> efetivacoesRecebidas) {
        this.efetivacoesRecebidas = efetivacoesRecebidas;
    }

    public List<TransferenciaBemConcedida> getEfetivacoesConcedidas() {
        return efetivacoesConcedidas;
    }

    public void setEfetivacoesConcedidas(List<TransferenciaBemConcedida> efetivacoesConcedidas) {
        this.efetivacoesConcedidas = efetivacoesConcedidas;
    }

    public List<TransferenciaDepreciacaoConcedida> getDepreciacoesConcedidas() {
        return depreciacoesConcedidas;
    }

    public void setDepreciacoesConcedidas(List<TransferenciaDepreciacaoConcedida> depreciacoesConcedidas) {
        this.depreciacoesConcedidas = depreciacoesConcedidas;
    }

    public List<TransferenciaAmortizacaoConcedida> getAmortizacoesConcedidas() {
        return amortizacoesConcedidas;
    }

    public void setAmortizacoesConcedidas(List<TransferenciaAmortizacaoConcedida> amortizacoesConcedidas) {
        this.amortizacoesConcedidas = amortizacoesConcedidas;
    }

    public List<TransferenciaExaustaoConcedida> getExaustoesConcedidas() {
        return exaustoesConcedidas;
    }

    public void setExaustoesConcedidas(List<TransferenciaExaustaoConcedida> exaustoesConcedidas) {
        this.exaustoesConcedidas = exaustoesConcedidas;
    }

    public List<TransferenciaReducaoConcedida> getAjustesConcedidos() {
        return ajustesConcedidos;
    }

    public void setAjustesConcedidos(List<TransferenciaReducaoConcedida> ajustesConcedidos) {
        this.ajustesConcedidos = ajustesConcedidos;
    }

    public List<TransferenciaDepreciacaoRecebida> getDepreciacoesRecebidas() {
        return depreciacoesRecebidas;
    }

    public void setDepreciacoesRecebidas(List<TransferenciaDepreciacaoRecebida> depreciacoesRecebidas) {
        this.depreciacoesRecebidas = depreciacoesRecebidas;
    }

    public List<TransferenciaAmortizacaoRecebida> getAmortizacoesRecebidas() {
        return amortizacoesRecebidas;
    }

    public void setAmortizacoesRecebidas(List<TransferenciaAmortizacaoRecebida> amortizacoesRecebidas) {
        this.amortizacoesRecebidas = amortizacoesRecebidas;
    }

    public List<TransferenciaExaustaoRecebida> getExaustoesRecebidas() {
        return exaustoesRecebidas;
    }

    public void setExaustoesRecebidas(List<TransferenciaExaustaoRecebida> exaustoesRecebidas) {
        this.exaustoesRecebidas = exaustoesRecebidas;
    }

    public List<TransferenciaReducaoRecebida> getAjustesRecebidos() {
        return ajustesRecebidos;
    }

    public void setAjustesRecebidos(List<TransferenciaReducaoRecebida> ajustesRecebidos) {
        this.ajustesRecebidos = ajustesRecebidos;
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

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
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

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + DataUtil.getDataFormatada(dataEfetivacao) + " - " + usuarioSistema.getPessoaFisica().getNome();
        } catch (NullPointerException ex) {
            return "";
        }
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setDescricaoUnidadeDestino(String descricaoUnidadeDestino) {
        this.descricaoUnidadeDestino = descricaoUnidadeDestino;
    }

    public String getDescricaoUnidadeDestino() {
        return descricaoUnidadeDestino;
    }

    public HierarquiaOrganizacional getUnidadeDestino() {
        return unidadeDestino;
    }

    public void setUnidadeDestino(HierarquiaOrganizacional unidadeDestino) {
        if (unidadeDestino != null) {
            setUnidadeOrganizacional(unidadeDestino.getSubordinada());
        }
        this.unidadeDestino = unidadeDestino;
    }

    public String getHistoricoRazao() {
        return "Efetivação de Transferência de Bem Móvel nº " + getCodigo();
    }
}
