/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.controle.ProcessoParcelamentoControlador;
import br.com.webpublico.enums.SituacaoParcelamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * @author renato
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Processo de Parcelamento")
public class ProcessoParcelamento extends Calculo implements Serializable, PossuidorArquivo {

    @Pesquisavel
    @Etiqueta(value = "Número")
    private Long numero;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Exercício")
    private Exercicio exercicio;
    @Transient
    @Tabelavel
    @Etiqueta(value = "Número")
    private String numeroComposto;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private EstornoParcelamento estornoParcelamento;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "processoParcelamento")
    private CancelamentoParcelamento cancelamentoParcelamento;
    @ManyToOne
    private Pessoa pessoa;
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Situação")
    @Pesquisavel
    @Tabelavel
    private SituacaoParcelamento situacaoParcelamento;
    private Integer numeroParcelas;
    private BigDecimal valorParcela = BigDecimal.ZERO;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta(value = "Data do Parcelamento")
    @Pesquisavel
    private Date dataProcessoParcelamento;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "processoParcelamento")
    private List<ItemProcessoParcelamento> itensProcessoParcelamento;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Parâmetro")
    private ParamParcelamento paramParcelamento;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta(value = "Vencimento 1ª Parcela")
    private Date vencimentoPrimeiraParcela;
    private BigDecimal valorTotalMulta = BigDecimal.ZERO;
    private BigDecimal valorTotalJuros = BigDecimal.ZERO;
    private BigDecimal valorTotalCorrecao = BigDecimal.ZERO;
    private BigDecimal valorTotalHonorarios = BigDecimal.ZERO;
    private BigDecimal valorTotalHonorariosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorTotalImposto = BigDecimal.ZERO;
    private BigDecimal valorTotalTaxa = BigDecimal.ZERO;

    private BigDecimal valorDescontoMulta = BigDecimal.ZERO;
    private BigDecimal valorDescontoJuros = BigDecimal.ZERO;
    private BigDecimal valorDescontoCorrecao = BigDecimal.ZERO;
    private BigDecimal valorDescontoHonorarios = BigDecimal.ZERO;
    private BigDecimal valorDescontoImposto = BigDecimal.ZERO;
    private BigDecimal valorDescontoTaxa = BigDecimal.ZERO;

    private BigDecimal valorTotalDesconto = BigDecimal.ZERO;
    private BigDecimal valorEntrada = BigDecimal.ZERO;
    private BigDecimal valorUltimaParcela = BigDecimal.ZERO;
    private BigDecimal percentualEntrada = BigDecimal.ZERO;
    private BigDecimal valorPrimeiraParcela = BigDecimal.ZERO;
    @ManyToOne
    private Pessoa fiador;
    @ManyToOne
    private Pessoa procurador;
    @Pesquisavel
    @Etiqueta(value = "Reparcelamento")
    private Integer numeroReparcelamento;
    @ManyToOne(cascade = CascadeType.ALL)
    private ProcessoCalcParcelamento processoCalculo;
    @ManyToOne(cascade = CascadeType.ALL)
    private DocumentoOficial termo;
    @ManyToOne
    private ParamParcelamentoTributo faixaDesconto;
    @Transient
    @Tabelavel
    @Etiqueta(value = "Cadastro")
    private String cadastroPessoaParcelamento;
    @Transient
    private ProcessoParcelamentoControlador.Valores valoresOriginais;
    @ManyToOne
    private UsuarioSistema usuarioResponsavel;
    @Etiqueta(value = "Permitir Cancelar os Parcelamentos")
    private Boolean permitirCancelarParcelamento;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "processoParcelamento")
    private List<ParcelaProcessoParcelamento> parcelasProcessoParcelamento;
    @Transient
    private Integer quantidadeMaximaParcelas;
    @ManyToOne
    private UsuarioSistema usuarioReativacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataReativacao;
    private String motivoReativacao;
    private String enderecoReativacao;
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    private String contato;

    public ProcessoParcelamento(Long idParcelamento) {
        this();
        this.setId(idParcelamento);
    }

    public ProcessoParcelamento() {
        super.setTipoCalculo(TipoCalculo.PARCELAMENTO);
        definirNumeroProcesso();
        definirCadastroPessoa();
        permitirCancelarParcelamento = false;
        parcelasProcessoParcelamento = Lists.newArrayList();
    }

    public Date getDataProcessoParcelamento() {
        return dataProcessoParcelamento;
    }

    public void setDataProcessoParcelamento(Date dataProcessoParcelamento) {
        this.dataProcessoParcelamento = dataProcessoParcelamento;
    }

    public EstornoParcelamento getEstornoParcelamento() {
        return estornoParcelamento;
    }

    public void setEstornoParcelamento(EstornoParcelamento estornoParcelamento) {
        this.estornoParcelamento = estornoParcelamento;
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas != null ? numeroParcelas : 0;
    }

    public void setNumeroParcelas(Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public Integer getNumeroParcelasComEntrada() {
        if (valorEntrada != null && valorEntrada.compareTo(BigDecimal.ZERO) > 0) {
            return numeroParcelas != null ? numeroParcelas + 1 : 0;
        }
        return numeroParcelas != null ? numeroParcelas : 0;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public SituacaoParcelamento getSituacaoParcelamento() {
        return situacaoParcelamento;
    }

    public void setSituacaoParcelamento(SituacaoParcelamento situacaoParcelamento) {
        this.situacaoParcelamento = situacaoParcelamento;
    }

    public BigDecimal getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(BigDecimal valorParcela) {
        this.valorParcela = valorParcela;
    }

    public List<ItemProcessoParcelamento> getItensProcessoParcelamento() {
        return itensProcessoParcelamento;
    }

    public void setItensProcessoParcelamento(List<ItemProcessoParcelamento> itensProcessoParcelamento) {
        this.itensProcessoParcelamento = itensProcessoParcelamento;
    }

    public List<ParcelaProcessoParcelamento> getParcelasProcessoParcelamento() {
        return parcelasProcessoParcelamento;
    }

    public void setParcelasProcessoParcelamento(List<ParcelaProcessoParcelamento> parcelasProcessoParcelamento) {
        this.parcelasProcessoParcelamento = parcelasProcessoParcelamento;
    }

    public ParamParcelamento getParamParcelamento() {
        return paramParcelamento;
    }

    public void setParamParcelamento(ParamParcelamento paramParcelamento) {
        this.paramParcelamento = paramParcelamento;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    @Override
    public void setCadastro(Cadastro cadastro) {
        super.setCadastro(cadastro);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getVencimentoPrimeiraParcela() {
        return vencimentoPrimeiraParcela;
    }

    public void setVencimentoPrimeiraParcela(Date vencimentoPrimeiraParcela) {
        this.vencimentoPrimeiraParcela = vencimentoPrimeiraParcela;
    }

    public BigDecimal getValorTotalCorrecao() {
        if (valorTotalCorrecao == null) {
            valorTotalCorrecao = BigDecimal.ZERO;
        }
        return valorTotalCorrecao.setScale(2, RoundingMode.HALF_UP);
    }

    public void setValorTotalCorrecao(BigDecimal valorTotalCorrecao) {
        this.valorTotalCorrecao = valorTotalCorrecao;
    }

    public BigDecimal getValorTotalHonorarios() {
        if (valorTotalHonorarios == null) {
            valorTotalHonorarios = BigDecimal.ZERO;
        }
        return valorTotalHonorarios.setScale(2, RoundingMode.HALF_UP);
    }

    public void setValorTotalHonorarios(BigDecimal valorTotalHonorarios) {
        this.valorTotalHonorarios = valorTotalHonorarios;
    }

    public BigDecimal getValorTotalJuros() {
        if (valorTotalJuros == null) {
            valorTotalJuros = BigDecimal.ZERO;
        }
        return valorTotalJuros.setScale(2, RoundingMode.HALF_UP);
    }

    public void setValorTotalJuros(BigDecimal valorTotalJuros) {
        this.valorTotalJuros = valorTotalJuros;
    }

    public BigDecimal getValorTotalMulta() {
        if (valorTotalMulta == null) {
            valorTotalMulta = BigDecimal.ZERO;
        }
        return valorTotalMulta.setScale(2, RoundingMode.HALF_UP);
    }

    public void setValorTotalMulta(BigDecimal valorTotalMulta) {
        this.valorTotalMulta = valorTotalMulta;
    }

    public BigDecimal getValorTotalImposto() {
        if (valorTotalImposto == null) {
            valorTotalImposto = BigDecimal.ZERO;
        }
        return valorTotalImposto.setScale(2, RoundingMode.HALF_UP);
    }

    public void setValorTotalImposto(BigDecimal valorTotalImposto) {
        this.valorTotalImposto = valorTotalImposto;
    }

    public BigDecimal getValorTotalTaxa() {
        if (valorTotalTaxa == null) {
            valorTotalTaxa = BigDecimal.ZERO;
        }
        return valorTotalTaxa.setScale(2, RoundingMode.HALF_UP);
    }

    public void setValorTotalTaxa(BigDecimal valorTotalTaxa) {
        this.valorTotalTaxa = valorTotalTaxa;
    }

    public BigDecimal getValorTotalDesconto() {
        if (valorTotalDesconto == null) {
            valorTotalDesconto = BigDecimal.ZERO;
        }
        return valorTotalDesconto.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void setValorTotalDesconto(BigDecimal valorTotalDesconto) {
        this.valorTotalDesconto = valorTotalDesconto;
    }

    public BigDecimal getValorEntrada() {
        return valorEntrada != null ? this.valorEntrada.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorEntrada(BigDecimal valorEntrada) {
        if (valorEntrada == null || valorEntrada.compareTo(BigDecimal.ZERO) < 0) {
            valorEntrada = BigDecimal.ZERO;
        }
        this.valorEntrada = valorEntrada;
    }

    public BigDecimal getValorPrimeiraParcela() {
        return valorPrimeiraParcela;
    }

    public void setValorPrimeiraParcela(BigDecimal valorPrimeiraParcela) {
        this.valorPrimeiraParcela = valorPrimeiraParcela;
    }

    public DocumentoOficial getTermo() {
        return termo;
    }

    public void setTermo(DocumentoOficial termo) {
        this.termo = termo;
    }

    public BigDecimal getValorTotalHonorariosAtualizado() {
        if (valorTotalHonorariosAtualizado == null) {
            valorTotalHonorariosAtualizado = BigDecimal.ZERO;
        }
        return valorTotalHonorariosAtualizado.setScale(2, RoundingMode.HALF_UP);
    }

    public void setValorTotalHonorariosAtualizado(BigDecimal valorTotalHonorariosAtualizado) {
        this.valorTotalHonorariosAtualizado = valorTotalHonorariosAtualizado;
    }

    public BigDecimal getTotalOriginal() {
        return getValorTotalImposto().add(getValorTotalTaxa());
    }

    public BigDecimal getTotalGeral() {
        BigDecimal retorno = getValorTotalImposto().add(getValorTotalTaxa());
        if (valorTotalCorrecao != null) {
            retorno = retorno.add(getValorTotalCorrecao());
        }
        if (valorTotalJuros != null) {
            retorno = retorno.add(getValorTotalJuros());
        }
        if (valorTotalMulta != null) {
            retorno = retorno.add(getValorTotalMulta());
        }
        if (valorTotalHonorariosAtualizado != null) {
            retorno = retorno.add(getValorTotalHonorariosAtualizado());
        }
        return retorno.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalGeralOriginal() {
        BigDecimal retorno = getValorTotalImposto().add(getValorTotalTaxa());
        if (valorTotalCorrecao != null) {
            retorno = retorno.add(getValorTotalCorrecao());
        }
        if (valorTotalJuros != null) {
            retorno = retorno.add(getValorTotalJuros());
        }
        if (valorTotalMulta != null) {
            retorno = retorno.add(getValorTotalMulta());
        }
        if (valorTotalHonorarios != null) {
            retorno = retorno.add(getValorTotalHonorarios());
        }
        return retorno.setScale(2, RoundingMode.HALF_UP);
    }

    public CancelamentoParcelamento getCancelamentoParcelamento() {
        return cancelamentoParcelamento;
    }

    public void setCancelamentoParcelamento(CancelamentoParcelamento cancelamentoParcelamento) {
        this.cancelamentoParcelamento = cancelamentoParcelamento;
    }

    public BigDecimal getPercentualEntrada() {
        if (percentualEntrada == null) {
            return BigDecimal.ZERO;
        }
        return percentualEntrada;
    }

    public void setPercentualEntrada(BigDecimal percentualEntrada) {
        this.percentualEntrada = percentualEntrada;
    }

    public Pessoa getFiador() {
        return fiador;
    }

    public void setFiador(Pessoa fiador) {
        this.fiador = fiador;
    }

    public Integer getNumeroReparcelamento() {
        return numeroReparcelamento;
    }

    public void setNumeroReparcelamento(Integer numeroReparcelamento) {
        this.numeroReparcelamento = numeroReparcelamento;
    }

    public BigDecimal getValorUltimaParcela() {
        return valorUltimaParcela != null ? valorUltimaParcela : BigDecimal.ZERO;
    }

    public void setValorUltimaParcela(BigDecimal valorUltimaParcela) {
        this.valorUltimaParcela = valorUltimaParcela;
    }

    public ParamParcelamentoTributo getFaixaDesconto() {
        return faixaDesconto;
    }

    public void setFaixaDesconto(ParamParcelamentoTributo faixaDesconto) {
        this.faixaDesconto = faixaDesconto;
    }

    public UsuarioSistema getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(UsuarioSistema usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    @Override
    public String toString() {
        return numero + "/" + exercicio + " - " + getCadastro();
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalcParcelamento processoCalculo) {
        super.setProcessoCalculo(processoCalculo);
        this.processoCalculo = processoCalculo;
    }

    public String getNumeroComposto() {
        return numero != null ? numero + (numeroReparcelamento != null && numeroReparcelamento > 0 ? "-" + numeroReparcelamento : "") : "";
    }

    public void setNumeroComposto(String numeroComposto) {
        this.numeroComposto = numeroComposto;
    }

    public String getNumeroCompostoComAno() {
        return getNumeroComposto() +
            "/" +
            exercicio.getAno();
    }

    public String getCadastroPessoaParcelamento() {
        return cadastroPessoaParcelamento;
    }

    public void setCadastroPessoaParcelamento(String cadastroPessoaParcelamento) {
        this.cadastroPessoaParcelamento = cadastroPessoaParcelamento;
    }

    public Pessoa getProcurador() {
        return procurador;
    }

    public void setProcurador(Pessoa procurador) {
        this.procurador = procurador;
    }

    public void calcularValores(ProcessoParcelamentoControlador.Valores valores) {
        setValorTotalCorrecao(valores.correcao);
        setValorTotalMulta(valores.multa);
        setValorTotalJuros(valores.juros);
        setValorTotalImposto(valores.imposto);
        setValorTotalTaxa(valores.taxa);
        setValorTotalHonorarios(valores.honorarios);
        setValorTotalHonorariosAtualizado(valores.honorarios);

        setValorDescontoImposto(valores.descontoImposto);
        setValorDescontoTaxa(valores.descontoTaxa);
        setValorDescontoJuros(valores.descontoJuros);
        setValorDescontoMulta(valores.descontoMulta);
        setValorDescontoCorrecao(valores.descontoCorrecao);
        setValorDescontoHonorarios(valores.descontoHonorarios);
        setValorTotalDesconto(getValorDescontoImposto().add(getValorDescontoTaxa()).add(getValorDescontoJuros()).add(getValorDescontoMulta()).add(getValorDescontoCorrecao()).add(getValorDescontoHonorarios()));

        setValorReal(valores.total);
        setValorEfetivo(valores.total);
        this.valoresOriginais = valores;
    }

    public void setValoresOriginais(ProcessoParcelamentoControlador.Valores valoresOriginais) {
        this.valoresOriginais = valoresOriginais;
    }

    public Boolean getPermitirCancelarParcelamento() {
        return permitirCancelarParcelamento != null ? permitirCancelarParcelamento : false;
    }

    public void setPermitirCancelarParcelamento(Boolean permitirCancelarParcelamento) {
        this.permitirCancelarParcelamento = permitirCancelarParcelamento;
    }

    public BigDecimal getValorDesconto() {
        return getValorTotalDesconto();
    }

    public BigDecimal getValorDescontoMulta() {
        return valorDescontoMulta != null ? valorDescontoMulta : BigDecimal.ZERO;
    }

    public void setValorDescontoMulta(BigDecimal valorDescontoMulta) {
        this.valorDescontoMulta = valorDescontoMulta;
    }

    public BigDecimal getValorDescontoJuros() {
        return valorDescontoJuros != null ? valorDescontoJuros : BigDecimal.ZERO;
    }

    public void setValorDescontoJuros(BigDecimal valorDescontoJuros) {
        this.valorDescontoJuros = valorDescontoJuros;
    }

    public BigDecimal getValorDescontoCorrecao() {
        return valorDescontoCorrecao != null ? valorDescontoCorrecao : BigDecimal.ZERO;
    }

    public void setValorDescontoCorrecao(BigDecimal valorDescontoCorrecao) {
        this.valorDescontoCorrecao = valorDescontoCorrecao;
    }

    public BigDecimal getValorDescontoHonorarios() {
        return valorDescontoHonorarios != null ? valorDescontoHonorarios : BigDecimal.ZERO;
    }

    public void setValorDescontoHonorarios(BigDecimal valorDescontoHonorarios) {
        this.valorDescontoHonorarios = valorDescontoHonorarios;
    }

    public BigDecimal getValorDescontoImposto() {
        return valorDescontoImposto != null ? valorDescontoImposto : BigDecimal.ZERO;
    }

    public void setValorDescontoImposto(BigDecimal valorDescontoImposto) {
        this.valorDescontoImposto = valorDescontoImposto;
    }

    public BigDecimal getValorDescontoTaxa() {
        return valorDescontoTaxa != null ? valorDescontoTaxa : BigDecimal.ZERO;
    }

    public void setValorDescontoTaxa(BigDecimal valorDescontoTaxa) {
        this.valorDescontoTaxa = valorDescontoTaxa;
    }

    public void definirCadastroPessoa() {
        if (pessoa != null) {
            cadastroPessoaParcelamento = pessoa.getNomeCpfCnpj();
        } else if (getCadastro() != null) {
            cadastroPessoaParcelamento = getCadastro().getNumeroCadastro() + " (" + getCadastro().getTipoDeCadastro().getDescricao() + ")";
        }
    }

    public void definirNumeroProcesso() {
        numeroComposto = numero + (numeroReparcelamento != null && numeroReparcelamento > 0 ? " - " + numeroReparcelamento : "");
    }

    public boolean isCancelado() {
        return this.situacaoParcelamento.equals(SituacaoParcelamento.CANCELADO);
    }

    public BigDecimal getTotalImpostoOriginado() {
        BigDecimal totalImposto = BigDecimal.ZERO;
        if (parcelasProcessoParcelamento != null) {
            for (ParcelaProcessoParcelamento parcelaProcessoParcelamento : parcelasProcessoParcelamento) {
                totalImposto = totalImposto.add(parcelaProcessoParcelamento.getImposto());
            }
        }
        return totalImposto;
    }

    public BigDecimal getTotalTaxaOriginado() {
        BigDecimal totalTaxa = BigDecimal.ZERO;
        if (parcelasProcessoParcelamento != null) {
            for (ParcelaProcessoParcelamento parcelaProcessoParcelamento : parcelasProcessoParcelamento) {
                totalTaxa = totalTaxa.add(parcelaProcessoParcelamento.getTaxa());
            }
        }
        return totalTaxa;
    }

    public BigDecimal getTotalJurosOriginado() {
        BigDecimal totalJuros = BigDecimal.ZERO;
        if (parcelasProcessoParcelamento != null) {
            for (ParcelaProcessoParcelamento parcelaProcessoParcelamento : parcelasProcessoParcelamento) {
                totalJuros = totalJuros.add(parcelaProcessoParcelamento.getJuros());
            }
        }
        return totalJuros;
    }

    public BigDecimal getTotalMultaOriginado() {
        BigDecimal totalMulta = BigDecimal.ZERO;
        if (parcelasProcessoParcelamento != null) {
            for (ParcelaProcessoParcelamento parcelaProcessoParcelamento : parcelasProcessoParcelamento) {
                totalMulta = totalMulta.add(parcelaProcessoParcelamento.getMulta());
            }
        }
        return totalMulta;
    }

    public BigDecimal getTotalCorrecaoOriginado() {
        BigDecimal totalCorrecao = BigDecimal.ZERO;
        if (parcelasProcessoParcelamento != null) {
            for (ParcelaProcessoParcelamento parcelaProcessoParcelamento : parcelasProcessoParcelamento) {
                totalCorrecao = totalCorrecao.add(parcelaProcessoParcelamento.getCorrecao());
            }
        }
        return totalCorrecao;
    }

    public BigDecimal getTotalHonorariosOriginado() {
        BigDecimal totalHonorarios = BigDecimal.ZERO;
        if (parcelasProcessoParcelamento != null) {
            for (ParcelaProcessoParcelamento parcelaProcessoParcelamento : parcelasProcessoParcelamento) {
                totalHonorarios = totalHonorarios.add(parcelaProcessoParcelamento.getHonorarios());
            }
        }
        return totalHonorarios;
    }

    public BigDecimal getTotalSemDescontoOriginado() {
        BigDecimal total = BigDecimal.ZERO;
        if (parcelasProcessoParcelamento != null) {
            for (ParcelaProcessoParcelamento parcelaProcessoParcelamento : parcelasProcessoParcelamento) {
                total = total.add(parcelaProcessoParcelamento.getTotalSemDesconto());
            }
        }
        return total;
    }

    public BigDecimal getTotalComDescontoOriginado() {
        BigDecimal total = BigDecimal.ZERO;
        if (parcelasProcessoParcelamento != null) {
            for (ParcelaProcessoParcelamento parcelaProcessoParcelamento : parcelasProcessoParcelamento) {
                total = total.add(parcelaProcessoParcelamento.getTotalComDesconto());
            }
        }
        return total;
    }

    public BigDecimal getTotalDescontoOriginado() {
        BigDecimal total = BigDecimal.ZERO;
        if (parcelasProcessoParcelamento != null) {
            for (ParcelaProcessoParcelamento parcelaProcessoParcelamento : parcelasProcessoParcelamento) {
                total = total.add(parcelaProcessoParcelamento.getDesconto());
            }
        }
        return total;
    }

    public Integer getQuantidadeMaximaParcelas() {
        return quantidadeMaximaParcelas;
    }

    public void setQuantidadeMaximaParcelas(Integer quantidadeMaximaParcelas) {
        this.quantidadeMaximaParcelas = quantidadeMaximaParcelas;
    }

    public boolean isDescontoImposto() {
        return getValorDescontoImposto().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isDescontoTaxa() {
        return getValorDescontoTaxa().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isDescontoJuros() {
        return getValorDescontoJuros().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isDescontoMulta() {
        return getValorDescontoMulta().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isDescontoCorrecao() {
        return getValorDescontoCorrecao().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isDescontoHonorarios() {
        return getValorDescontoHonorarios().compareTo(BigDecimal.ZERO) > 0;
    }

    public UsuarioSistema getUsuarioReativacao() {
        return usuarioReativacao;
    }

    public void setUsuarioReativacao(UsuarioSistema usuarioReativacao) {
        this.usuarioReativacao = usuarioReativacao;
    }

    public Date getDataReativacao() {
        return dataReativacao;
    }

    public void setDataReativacao(Date dataReativacao) {
        this.dataReativacao = dataReativacao;
    }

    public String getMotivoReativacao() {
        return motivoReativacao;
    }

    public void setMotivoReativacao(String motivoReativacao) {
        this.motivoReativacao = motivoReativacao;
    }

    public String getEnderecoReativacao() {
        return enderecoReativacao;
    }

    public void setEnderecoReativacao(String enderecoReativacao) {
        this.enderecoReativacao = enderecoReativacao;
    }

    public boolean hasEntradaInformada() {
        return getValorEntrada().compareTo(BigDecimal.ZERO) > 0;
    }
}
