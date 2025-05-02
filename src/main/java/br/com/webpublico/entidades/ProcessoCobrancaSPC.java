package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class ProcessoCobrancaSPC extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Exercicio exercicio;
    private Long codigo;
    @Temporal(TemporalType.DATE)
    private Date dataLancamento;
    private String numeroProtocolo;
    @ManyToOne
    private UsuarioSistema usuarioLancamento;
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    private String observacao;
    @ManyToOne
    private AtoLegal atoLegal;
    @ManyToOne
    private Pessoa contribuinte;
    @OneToMany(mappedBy = "processoCobrancaSPC", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemProcessoCobrancaSPC> itens;
    private BigDecimal valorImposto;
    private BigDecimal valorTaxa;
    private BigDecimal valorDesconto;
    private BigDecimal valorJuros;
    private BigDecimal valorMulta;
    private BigDecimal valorCorrecao;
    private BigDecimal valorHonorarios;
    private BigDecimal valorTotal;
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public ProcessoCobrancaSPC() {
        super();
        this.itens = Lists.newArrayList();
        this.valorImposto = BigDecimal.ZERO;
        this.valorTaxa = BigDecimal.ZERO;
        this.valorDesconto = BigDecimal.ZERO;
        this.valorJuros = BigDecimal.ZERO;
        this.valorMulta = BigDecimal.ZERO;
        this.valorCorrecao = BigDecimal.ZERO;
        this.valorHonorarios = BigDecimal.ZERO;
        this.valorTotal = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public UsuarioSistema getUsuarioLancamento() {
        return usuarioLancamento;
    }

    public void setUsuarioLancamento(UsuarioSistema usuarioLancamento) {
        this.usuarioLancamento = usuarioLancamento;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public List<ItemProcessoCobrancaSPC> getItens() {
        return itens;
    }

    public void setItens(List<ItemProcessoCobrancaSPC> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa;
    }

    public void setValorTaxa(BigDecimal valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorCorrecao() {
        return valorCorrecao;
    }

    public void setValorCorrecao(BigDecimal valorCorrecao) {
        this.valorCorrecao = valorCorrecao;
    }

    public BigDecimal getValorHonorarios() {
        return valorHonorarios;
    }

    public void setValorHonorarios(BigDecimal valorHonorarios) {
        this.valorHonorarios = valorHonorarios;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public void addItem(ItemProcessoCobrancaSPC itemProcessoCobrancaSPC) {
        if (itens == null) {
            itens = Lists.newArrayList();
        }
        valorImposto = valorImposto.add(itemProcessoCobrancaSPC.getValorImposto());
        valorTaxa = valorTaxa.add(itemProcessoCobrancaSPC.getValorTaxa());
        valorDesconto = valorDesconto.add(itemProcessoCobrancaSPC.getValorDesconto());
        valorJuros = valorJuros.add(itemProcessoCobrancaSPC.getValorJuros());
        valorMulta = valorMulta.add(itemProcessoCobrancaSPC.getValorMulta());
        valorCorrecao = valorCorrecao.add(itemProcessoCobrancaSPC.getValorCorrecao());
        valorHonorarios = valorHonorarios.add(itemProcessoCobrancaSPC.getValorHonorarios());
        valorTotal = valorTotal.add(itemProcessoCobrancaSPC.getValorTotal());
        itens.add(itemProcessoCobrancaSPC);
    }

    public void excluirItem(ItemProcessoCobrancaSPC itemProcessoCobrancaSPC) {
        valorImposto = valorImposto.subtract(itemProcessoCobrancaSPC.getValorImposto());
        valorTaxa = valorTaxa.subtract(itemProcessoCobrancaSPC.getValorTaxa());
        valorDesconto = valorDesconto.subtract(itemProcessoCobrancaSPC.getValorDesconto());
        valorJuros = valorJuros.subtract(itemProcessoCobrancaSPC.getValorJuros());
        valorMulta = valorMulta.subtract(itemProcessoCobrancaSPC.getValorMulta());
        valorCorrecao = valorCorrecao.subtract(itemProcessoCobrancaSPC.getValorCorrecao());
        valorHonorarios = valorHonorarios.subtract(itemProcessoCobrancaSPC.getValorHonorarios());
        valorTotal = valorTotal.subtract(itemProcessoCobrancaSPC.getValorTotal());
        itens.remove(itemProcessoCobrancaSPC);
    }

    public enum Situacao implements EnumComDescricao {
        ABERTO("Aberto"),
        PROCESSADO_INCONSISTENCIA("Processado com inconsistência"),
        PROCESSADO("Processado"),
        ESTORNADO("Estornado"),
        ESTORNADO_INCONSISTENCIA("Estornado com inconsistência");

        private final String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (atoLegal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal deve ser informado.");
        }
        if (Strings.isNullOrEmpty(observacao)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo ou Fundamentação Legal deve ser informado.");
        }
        if (contribuinte == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contribuinte deve ser informado.");
        }
        if (itens == null || itens.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Nenhum débito adicionado ao processo.");
        }
        ve.lancarException();
    }
}
