package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.AssistenteSimulacaoParcelamento;
import br.com.webpublico.entidadesauxiliares.CDAResultadoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
public class ItemParcelamentoJudicial extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ParcelamentoJudicial parcelamentoJudicial;
    @Enumerated(EnumType.STRING)
    private TipoCadastroTributario tipoCadastroTributario;
    @ManyToOne
    private Cadastro cadastro;
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    private Divida divida;
    @OneToMany(mappedBy = "itemParcelamentoJudicial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DebitoParcelamentoJudicial> debitos;
    @Transient
    private List<ParamParcelamento> parametros;
    @OneToOne
    private ProcessoParcelamento processoParcelamento;
    @Transient
    private List<ResultadoParcela> parcelasParcelamento;
    @Transient
    private AssistenteSimulacaoParcelamento assistenteSimulacaoParcelamento;

    public ItemParcelamentoJudicial() {
        super();
        this.debitos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ParcelamentoJudicial getParcelamentoJudicial() {
        return parcelamentoJudicial;
    }

    public void setParcelamentoJudicial(ParcelamentoJudicial parcelamentoJudicial) {
        this.parcelamentoJudicial = parcelamentoJudicial;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public List<DebitoParcelamentoJudicial> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<DebitoParcelamentoJudicial> debitos) {
        this.debitos = debitos;
    }

//    public ParamParcelamento getParametro() {
//        return parametro;
//    }
//
//    public void setParametro(ParamParcelamento parametro) {
//        this.parametro = parametro;
//    }

    public List<ParamParcelamento> getParametros() {
        return parametros;
    }

    public void setParametros(List<ParamParcelamento> parametros) {
        this.parametros = parametros;
    }

//    public Integer getNumeroParcelas() {
//        return numeroParcelas;
//    }
//
//    public void setNumeroParcelas(Integer numeroParcelas) {
//        this.numeroParcelas = numeroParcelas;
//    }

    public ProcessoParcelamento getProcessoParcelamento() {
        return processoParcelamento;
    }

    public void setProcessoParcelamento(ProcessoParcelamento processoParcelamento) {
        this.processoParcelamento = processoParcelamento;
    }

    public List<ResultadoParcela> getParcelasParcelamento() {
        return parcelasParcelamento;
    }

    public void setParcelasParcelamento(List<ResultadoParcela> parcelasParcelamento) {
        this.parcelasParcelamento = parcelasParcelamento;
    }

    public AssistenteSimulacaoParcelamento getAssistenteSimulacaoParcelamento() {
        return assistenteSimulacaoParcelamento;
    }

    public void setAssistenteSimulacaoParcelamento(AssistenteSimulacaoParcelamento assistenteSimulacaoParcelamento) {
        this.assistenteSimulacaoParcelamento = assistenteSimulacaoParcelamento;
    }

    public BigDecimal getValorImposto() {
        return this.debitos.stream().map(DebitoParcelamentoJudicial::getResultadoParcela)
            .map(ResultadoParcela::getValorImposto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorImpostoParcelamento() {
        return this.parcelasParcelamento.stream()
            .map(ResultadoParcela::getValorImposto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorTaxa() {
        return this.debitos.stream().map(DebitoParcelamentoJudicial::getResultadoParcela)
            .map(ResultadoParcela::getValorTaxa)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorTaxaParcelamento() {
        return this.parcelasParcelamento.stream()
            .map(ResultadoParcela::getValorTaxa)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorDesconto() {
        return this.debitos.stream().map(DebitoParcelamentoJudicial::getResultadoParcela)
            .map(ResultadoParcela::getValorDesconto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorDescontoParcelamento() {
        return this.parcelasParcelamento.stream()
            .map(ResultadoParcela::getValorDesconto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorJuros() {
        return this.debitos.stream().map(DebitoParcelamentoJudicial::getResultadoParcela)
            .map(ResultadoParcela::getValorJuros)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorJurosParcelamento() {
        return this.parcelasParcelamento.stream()
            .map(ResultadoParcela::getValorJuros)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorMulta() {
        return this.debitos.stream().map(DebitoParcelamentoJudicial::getResultadoParcela)
            .map(ResultadoParcela::getValorMulta)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorMultaParcelamento() {
        return this.parcelasParcelamento.stream()
            .map(ResultadoParcela::getValorMulta)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorHonorarios() {
        return this.debitos.stream().map(DebitoParcelamentoJudicial::getResultadoParcela)
            .map(ResultadoParcela::getValorHonorarios)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorHonorariosParcelamento() {
        return this.parcelasParcelamento.stream()
            .map(ResultadoParcela::getValorHonorarios)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorCorrecao() {
        return this.debitos.stream().map(DebitoParcelamentoJudicial::getResultadoParcela)
            .map(ResultadoParcela::getValorCorrecao)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorCorrecaoParcelamento() {
        return this.parcelasParcelamento.stream()
            .map(ResultadoParcela::getValorCorrecao)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorTotal() {
        return this.debitos.stream().map(DebitoParcelamentoJudicial::getResultadoParcela)
            .map(ResultadoParcela::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorTotalParcelamento() {
        return this.parcelasParcelamento.stream()
            .map(ResultadoParcela::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getTituloAgrupamento() {
        StringBuilder titulo = new StringBuilder();
        titulo.append(tipoCadastroTributario.getDescricao()).append(" ");
        if (cadastro != null) {
            titulo.append(cadastro.getNumeroCadastro());
        } else {
            titulo.append(pessoa.getNomeCpfCnpj());
        }
        titulo.append(" ").append(divida.getDescricao());
        return titulo.toString();
    }
}
