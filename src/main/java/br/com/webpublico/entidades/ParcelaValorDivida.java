/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoDebito;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.enums.TipoDeDebitoDTO;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author munif
 */
@GrupoDiagrama(nome = "Tributario")
@Entity
@Audited
public class ParcelaValorDivida extends SuperEntidade implements Serializable, IParcela {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valor;
    @ManyToOne
    private ValorDivida valorDivida;
    @ManyToOne
    private OpcaoPagamento opcaoPagamento;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    private BigDecimal percentualValorTotal;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRegistro;
    private String sequenciaParcela;
    private Long quantidadeParcela;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parcelaValorDivida")
    private List<ItemParcelaValorDivida> itensParcelaValorDivida;
    private Boolean dividaAtiva;
    private Boolean dividaAtivaAjuizada;
    private Boolean debitoProtestado;
    private Date dataProtesto;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parcela", orphanRemoval = true)
    private List<SituacaoParcelaValorDivida> situacoes;
    @Transient
    private BigDecimal valorAcrescimo;
    @Transient
    private BigDecimal valorMulta;
    @Transient
    private BigDecimal valorJuros;
    @Transient
    private BigDecimal valorTotal;
    @Transient
    private BigDecimal valorSaldo;
    @Transient
    private BigDecimal valorHonorarios;
    @Transient
    private Long dividaId;
    @Transient
    private ResultadoParcela resultadoParcela;
    @ManyToOne
    private SituacaoParcelaValorDivida situacaoAtual;
    @Temporal(TemporalType.DATE)
    private Date dataPrescricao;

    public ParcelaValorDivida() {
        dataRegistro = new Date();
        situacoes = Lists.newArrayList();
        itensParcelaValorDivida = Lists.newArrayList();
        valorJuros = BigDecimal.ZERO;
        valorMulta = BigDecimal.ZERO;
        valorHonorarios = BigDecimal.ZERO;
    }

    public ParcelaValorDivida(Long id, BigDecimal valor, Date vencimento, Long dividaId) {
        this.id = id;
        this.valor = valor;
        this.vencimento = vencimento;
        this.dividaId = dividaId;
    }

    public ParcelaValorDivida(ParcelaValorDivida pvd, BigDecimal valor) {
        this.id = pvd.getId();
        this.valor = valor;
        this.valorDivida = pvd.getValorDivida();
        this.opcaoPagamento = pvd.getOpcaoPagamento();
        this.vencimento = pvd.getVencimento();
        this.percentualValorTotal = pvd.getPercentualValorTotal();
        this.dataRegistro = pvd.getDataRegistro();
        this.sequenciaParcela = pvd.getSequenciaParcela();
        this.dividaAtiva = pvd.getDividaAtiva();
        this.dividaAtivaAjuizada = pvd.getDividaAtivaAjuizada();
        this.debitoProtestado = pvd.isDebitoProtestado();
        this.dataProtesto = pvd.getDataProtesto();
        this.criadoEm = System.nanoTime();
    }

    public ParcelaValorDivida(Long idParcela) {
        this.id = idParcela;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDividaId() {
        return dividaId;
    }

    public void setDividaId(Long dividaId) {
        this.dividaId = dividaId;
    }

    public OpcaoPagamento getOpcaoPagamento() {
        return opcaoPagamento;
    }

    public void setOpcaoPagamento(OpcaoPagamento opcaoPagamento) {
        this.opcaoPagamento = opcaoPagamento;
    }

    public ValorDivida getValorDivida() {
        return valorDivida;
    }

    public void setValorDivida(ValorDivida valorDivida) {
        this.valorDivida = valorDivida;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getPercentualValorTotal() {
        return percentualValorTotal;
    }

    public void setPercentualValorTotal(BigDecimal percentualValorTotal) {
        this.percentualValorTotal = percentualValorTotal;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getSequenciaParcela() {
        if (opcaoPagamento != null && opcaoPagamento.getPromocional() && sequenciaParcela != null && !sequenciaParcela.contains("/UN")) {
            sequenciaParcela = sequenciaParcela;
        }
        return sequenciaParcela;
    }

    public void setSequenciaParcela(String numeroParcela) {
        this.sequenciaParcela = numeroParcela;
    }

    public List<ItemParcelaValorDivida> getItensParcelaValorDivida() {
        return itensParcelaValorDivida;
    }

    public void setItensParcelaValorDivida(List<ItemParcelaValorDivida> itensParcelaValorDivida) {
        this.itensParcelaValorDivida = itensParcelaValorDivida;
    }

    public BigDecimal getValorAcrescimo() {
        return valorAcrescimo;
    }

    public void setValorAcrescimo(BigDecimal valorAcrescimo) {
        this.valorAcrescimo = valorAcrescimo;
    }

    public Boolean getDividaAtivaAjuizada() {
        return dividaAtivaAjuizada != null ? dividaAtivaAjuizada : false;
    }

    public void setDividaAtivaAjuizada(Boolean dividaAtivaAjuizada) {
        this.dividaAtivaAjuizada = dividaAtivaAjuizada;
    }

    public Long getQuantidadeParcela() {
        return quantidadeParcela;
    }

    public void setQuantidadeParcela(Long quantidadeParcela) {
        this.quantidadeParcela = quantidadeParcela;
    }

    public BigDecimal getValorSaldo() {
        return valorSaldo;
    }

    public void setValorSaldo(BigDecimal valorSaldo) {
        this.valorSaldo = valorSaldo;
    }

    public Boolean isDividaAtiva() {
        if (this.dividaAtiva == null) {
            return Boolean.FALSE;
        } else {
            return this.dividaAtiva;
        }
    }

    public Boolean getDividaAtiva() {
        if (this.dividaAtiva == null) {
            return Boolean.FALSE;
        } else {
            return this.dividaAtiva;
        }
    }

    public void setDividaAtiva(Boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public Boolean isDividaAtivaAjuizada() {
        if (this.dividaAtivaAjuizada == null) {
            return Boolean.FALSE;
        } else {
            return this.dividaAtivaAjuizada;
        }
    }

    public Boolean isDebitoProtestado() {
        return debitoProtestado != null ? debitoProtestado : Boolean.FALSE;
    }

    public void setDebitoProtestado(Boolean debitoProtestado) {
        this.debitoProtestado = debitoProtestado;
    }

    public Date getDataProtesto() {
        return dataProtesto;
    }

    public void setDataProtesto(Date dataProtesto) {
        this.dataProtesto = dataProtesto;
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

    public List<SituacaoParcelaValorDivida> getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(List<SituacaoParcelaValorDivida> situacoes) {
        this.situacoes = situacoes;
    }

    public SituacaoParcelaValorDivida getUltimaSituacao() {
        return getSituacaoAtual();
    }

    public SituacaoParcelaValorDivida getSituacaoAtual() {
        return situacaoAtual;
    }

    public void setSituacaoAtual(SituacaoParcelaValorDivida situacaoAtual) {
        this.situacaoAtual = situacaoAtual;
    }

    public Date getDataPrescricao() {
        return dataPrescricao;
    }

    public void setDataPrescricao(Date dataPrescricao) {
        this.dataPrescricao = dataPrescricao;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public TipoDeDebitoDTO getTipoDeDebito() {
        if (isDividaAtiva() && isDividaAtivaAjuizada() && isDebitoProtestado()) {
            return TipoDeDebitoDTO.AJZP;
        } else if (isDividaAtiva() && isDividaAtivaAjuizada()) {
            return TipoDeDebitoDTO.AJZ;
        } else if (isDividaAtiva() && isDebitoProtestado()) {
            return TipoDeDebitoDTO.DAP;
        } else if (isDividaAtiva()) {
            return TipoDeDebitoDTO.DA;
        } else if (!isDividaAtivaAjuizada() && isDebitoProtestado()) {
            return TipoDeDebitoDTO.EXP;
        } else if (!isDividaAtivaAjuizada()) {
            return TipoDeDebitoDTO.EX;
        }
        return null;
    }

    public boolean isVencido(Date dataOperacao) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dataVencimento = null;

        try {
            dataOperacao = sdf.parse(sdf.format(dataOperacao));
            dataVencimento = sdf.parse(sdf.format(vencimento));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dataVencimento.before(dataOperacao);
    }

    public boolean isCotaUnica() {
        try {
            return !opcaoPagamento.getPermiteAtraso();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ValorDivida" + valorDivida + "Parcela: R$" + valor + " / Vencto: " + vencimento;
    }

    public SituacaoDebito getSituacaoDebito() {
        if (dividaAtiva != null && dividaAtiva) {
            return SituacaoDebito.DIVIDA_ATIVA;
        }
        if (dividaAtivaAjuizada != null && dividaAtivaAjuizada) {
            return SituacaoDebito.AJUIZADA;
        }
        return SituacaoDebito.EXERCICIO;
    }

//    public String getCodigoDeBarrasSemDigitoVerificador() {
//        String campo1 = this.codigoBarras.substring(00, 11); //11 caracteres
//        String campo2 = this.codigoBarras.substring(14, 25);
//        String campo3 = this.codigoBarras.substring(28, 39);
//        String campo4 = this.codigoBarras.substring(42, 53);
//        return campo1 + campo2 + campo3 + campo4;
//    }

    public void zeraValores() {
        valorAcrescimo = BigDecimal.ZERO;
        valorJuros = BigDecimal.ZERO;
        valorMulta = BigDecimal.ZERO;
        valorSaldo = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
    }

    public boolean isSituacaoPago() {
        return getUltimaSituacao().isSituacaoPago();
    }
//    public void addSituacao(SituacaoParcela situacao) {
//        if (this.situacoes == null) {
//            this.situacoes = new ArrayList<SituacaoParcelaValorDivida>();
//        }
//        SituacaoParcelaValorDivida toAdd = new SituacaoParcelaValorDivida();
//        toAdd.setDataLancamento(new Date());
//        toAdd.setParcela(this);
//        toAdd.setSituacaoParcela(situacao);
//        situacoes.add(toAdd);
//    }
}
