/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoBaseCalculo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

@Entity
@Audited
@Etiqueta("I.T.B.I. Online")
public class CalculoSolicitacaoItbiOnline extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SolicitacaoItbiOnline solicitacaoItbiOnline;
    private Integer sequencia;
    @ManyToOne
    private Exercicio exercicio;
    @Transient
    private String imovelLista;
    @Enumerated(EnumType.STRING)
    private TipoBaseCalculo tipoBaseCalculoITBI;
    private BigDecimal valorReajuste;
    private BigDecimal valorVenal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoSolicitacaoItbiOnline", orphanRemoval = true)
    private List<ItemCalculoITBIOnline> itensCalculo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoSolicitacaoItbiOnline", orphanRemoval = true)
    private List<AdquirentesSolicitacaoITBIOnline> adquirentesCalculo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoSolicitacaoItbiOnline", orphanRemoval = true)
    private List<TransmitentesSolicitacaoItbiOnline> transmitentesCalculo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoSolicitacaoItbiOnline", orphanRemoval = true)
    private List<PropriedadeSimulacaoITBIOnline> proprietariosSimulacao;
    private BigDecimal baseCalculo;
    @ManyToOne
    private TipoIsencaoITBI tipoIsencaoITBI;
    @Transient
    private Boolean isentoSub;
    private BigDecimal totalBaseCalculo;
    private BigDecimal percentual;
    private Integer ordemCalculo;
    private Integer quantidadeParcelas;
    private BigDecimal valorCalculado;
    private BigDecimal valorReal;

    public CalculoSolicitacaoItbiOnline() {
        this.valorReajuste = BigDecimal.ZERO;
        this.baseCalculo = BigDecimal.ZERO;
        this.totalBaseCalculo = BigDecimal.ZERO;
        this.valorReal = BigDecimal.ZERO;
        this.isentoSub = Boolean.FALSE;
        this.itensCalculo = Lists.newArrayList();
        this.adquirentesCalculo = Lists.newArrayList();
        this.transmitentesCalculo = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsentoSub() {
        return isentoSub;
    }

    public void setIsentoSub(Boolean isentoSub) {
        this.isentoSub = isentoSub;
    }

    public String getImovelLista() {
        return imovelLista;
    }

    public void setImovelLista(String imovelLista) {
        this.imovelLista = imovelLista;
    }

    public Integer getQuantidadeParcelas() {
        if(quantidadeParcelas == null) {
            quantidadeParcelas = 1;
        }
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(Integer quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public List<AdquirentesSolicitacaoITBIOnline> getAdquirentesCalculo() {
        return adquirentesCalculo;
    }

    public void setAdquirentesCalculo(List<AdquirentesSolicitacaoITBIOnline> adquirentesCalculo) {
        this.adquirentesCalculo = adquirentesCalculo;
    }

    public List<ItemCalculoITBIOnline> getItensCalculo() {
        return itensCalculo;
    }

    public void setItensCalculo(List<ItemCalculoITBIOnline> itensCalculo) {
        this.itensCalculo = itensCalculo;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo != null ? baseCalculo.setScale(2, RoundingMode.DOWN) : BigDecimal.ZERO;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public SolicitacaoItbiOnline getSolicitacaoItbiOnline() {
        return solicitacaoItbiOnline;
    }

    public void setSolicitacaoItbiOnline(SolicitacaoItbiOnline solicitacaoItbiOnline) {
        this.solicitacaoItbiOnline = solicitacaoItbiOnline;
    }

    public TipoBaseCalculo getTipoBaseCalculoITBI() {
        return tipoBaseCalculoITBI;
    }

    public void setTipoBaseCalculoITBI(TipoBaseCalculo tipoBaseCalculoITBI) {
        this.tipoBaseCalculoITBI = tipoBaseCalculoITBI;
    }

    public BigDecimal getValorReajuste() {
        return valorReajuste;
    }

    public void setValorReajuste(BigDecimal valorReajuste) {
        this.valorReajuste = valorReajuste;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public BigDecimal getValorVenal() {
        return valorVenal;
    }

    public void setValorVenal(BigDecimal valorVenal) {
        this.valorVenal = valorVenal;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<TransmitentesSolicitacaoItbiOnline> getTransmitentesCalculo() {
        return transmitentesCalculo;
    }

    public void setTransmitentesCalculo(List<TransmitentesSolicitacaoItbiOnline> transmitentesCalculo) {
        this.transmitentesCalculo = transmitentesCalculo;
    }

    public List<Pessoa> getProprietariosQueTransmitiram() {
        List<Pessoa> toReturn = new ArrayList<>();

        for (TransmitentesSolicitacaoItbiOnline t : transmitentesCalculo) {
            toReturn.add(t.getTransmitente());
        }

        return toReturn;
    }

    public TipoIsencaoITBI getTipoIsencaoITBI() {
        return tipoIsencaoITBI;
    }

    public void setTipoIsencaoITBI(TipoIsencaoITBI tipoIsencaoITBI) {
        this.tipoIsencaoITBI = tipoIsencaoITBI;
    }

    public BigDecimal getPercentual() {
        return percentual != null ? percentual : BigDecimal.ZERO;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public Integer getOrdemCalculo() {
        return ordemCalculo != null ? ordemCalculo : 1;
    }

    public void setOrdemCalculo(Integer ordemCalculo) {
        this.ordemCalculo = ordemCalculo;
    }

    public void setTotalBaseCalculo(BigDecimal totalBaseCalculo) {
        this.totalBaseCalculo = totalBaseCalculo;
    }

    public BigDecimal getValorReal() {
        return valorReal;
    }

    public void setValorReal(BigDecimal valorReal) {
        this.valorReal = valorReal;
    }

    public SolicitacaoItbiOnline getItbiOnline() {
        return solicitacaoItbiOnline;
    }

    public void setItbiOnline(SolicitacaoItbiOnline solicitacaoItbiOnline) {
        this.solicitacaoItbiOnline = solicitacaoItbiOnline;
    }

    public List<PropriedadeSimulacaoITBIOnline> getProprietariosSimulacao() {
        return proprietariosSimulacao;
    }

    public void setProprietariosSimulacao(List<PropriedadeSimulacaoITBIOnline> proprietariosSimulacao) {
        this.proprietariosSimulacao = proprietariosSimulacao;
    }

    public BigDecimal getTotalBaseCalculo() {
        if (getPercentual().compareTo(BigDecimal.ZERO) > 0) {
            return getBaseCalculo().multiply(getPercentual().divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
        }
        return getBaseCalculo();
    }

    public BigDecimal getValorCalculado() {
        return valorCalculado;
    }

    public void setValorCalculado(BigDecimal valorCalculado) {
        this.valorCalculado = valorCalculado;
    }

    public Pessoa getAdquirente() {
        if (adquirentesCalculo != null && !adquirentesCalculo.isEmpty()) {
            ordernarAdquirentes();
            return adquirentesCalculo.get(0).getAdquirente();
        }
        return null;
    }

    public void ordernarAdquirentes() {

        Collections.sort(adquirentesCalculo, new Comparator<AdquirentesSolicitacaoITBIOnline>() {
            @Override
            public int compare(AdquirentesSolicitacaoITBIOnline o1, AdquirentesSolicitacaoITBIOnline o2) {
                String nome1 = o1.getAdquirente() != null ? o1.getAdquirente().getNome() : "";
                String nome2 = o2.getAdquirente() != null ? o2.getAdquirente().getNome() : "";
                return ComparisonChain.start().compare(nome2, nome1, Ordering.<String>natural().reverse()).result();
            }
        });

        Collections.sort(adquirentesCalculo, new Comparator<AdquirentesSolicitacaoITBIOnline>() {
            @Override
            public int compare(AdquirentesSolicitacaoITBIOnline o1, AdquirentesSolicitacaoITBIOnline o2) {
                BigDecimal percentual1 = o1.getPercentual() != null ? o1.getPercentual() : BigDecimal.ZERO;
                BigDecimal percentual2 = o2.getPercentual() != null ? o2.getPercentual() : BigDecimal.ZERO;
                return ComparisonChain.start().compare(percentual1, percentual2, Ordering.<BigDecimal>natural().reverse()).result();
            }
        });
    }

    public BigDecimal getTotalPercentualAdquirentes() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (AdquirentesSolicitacaoITBIOnline ac : getAdquirentesCalculo()) {
            total = total.add(ac.getPercentual());
        }
        return total;
    }

}
