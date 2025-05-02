package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoProcessoDebito;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@GrupoDiagrama(nome = "Tributario")
@Audited
@Etiqueta("Processo de Débito em Lote")
public class ProcessoDebitoEmLote implements Serializable, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private Long criadoEm;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Enumerated(EnumType.STRING)
    private TipoProcessoDebito tipo;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Lançamento")
    private Date lancamento;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    private String motivo;
    @ManyToOne
    private UsuarioSistema usuarioIncluiu;
    @OneToMany(mappedBy = "processoDebitoEmLote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemProcessoDebitoEmLote> itens;
    @OneToMany(mappedBy = "processoDebitoEmLote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParcelaProcessoDebitoEmLote> parcelasProcessoEmLote;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoProcessoDebito situacao;
    @Temporal(TemporalType.DATE)
    private Date dataEstorno;
    private String motivoEstorno;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Temporal(TemporalType.DATE)
    private Date dataPagamento;
    private BigDecimal valorPago;
    @Temporal(TemporalType.DATE)
    private Date validade;
    @ManyToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @Enumerated(EnumType.STRING)
    private TipoCadastroTributario tipoCadastro;

    public ProcessoDebitoEmLote() {
        itens = Lists.newArrayList();
        parcelasProcessoEmLote = Lists.newArrayList();
        criadoEm = System.nanoTime();
        motivo = "";
        motivoEstorno = "";
    }

    public SituacaoProcessoDebito getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcessoDebito situacao) {
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return this.criadoEm;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public List<ItemProcessoDebitoEmLote> getItens() {
        return itens;
    }

    public void setItens(List<ItemProcessoDebitoEmLote> itens) {
        this.itens = itens;
    }

    public UsuarioSistema getUsuarioIncluiu() {
        return usuarioIncluiu;
    }

    public void setUsuarioIncluiu(UsuarioSistema usuarioIncluiu) {
        this.usuarioIncluiu = usuarioIncluiu;
    }

    public Date getLancamento() {
        return lancamento;
    }

    public void setLancamento(Date lancamento) {
        this.lancamento = lancamento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public TipoProcessoDebito getTipo() {
        return tipo;
    }

    public void setTipo(TipoProcessoDebito tipo) {
        this.tipo = tipo;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public List<ParcelaProcessoDebitoEmLote> getParcelasProcessoEmLote() {
        return parcelasProcessoEmLote;
    }

    public void setParcelasProcessoEmLote(List<ParcelaProcessoDebitoEmLote> parcelasProcessoEmLote) {
        this.parcelasProcessoEmLote = parcelasProcessoEmLote;
    }

    public Boolean getValido() {

        return this.validade != null
            && SituacaoProcessoDebito.FINALIZADO.equals(this.getSituacao()) ?
            this.validade.compareTo(Util.getDataHoraMinutoSegundoZerado(new Date())) >= 0
            : true;
    }

    public String processosIndividuaisGerados() {
        StringBuilder retorno = new StringBuilder();
        if (!itens.isEmpty()) {
            for (ItemProcessoDebitoEmLote item : itens) {
                retorno.append(item.getProcessoDebito().getCodigo()).append("/").append(item.getProcessoDebito().getExercicio().getAno());
                if (item.equals(itens.get(itens.size() - 1))) {
                    retorno.append(".");
                } else {
                    retorno.append(", ");
                }
            }
        }
        return retorno.toString();
    }

    public boolean isAberto() {
        return SituacaoProcessoDebito.EM_ABERTO.equals(this.getSituacao());
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ProcessoDebitoEmLote[ id=" + id + " ]";
    }

    public SituacaoParcela getSituacaoParcela() {
        if (this.tipo == null) {
            return null;
        }
        return this.tipo.getSituacaoParcela();
    }
}
