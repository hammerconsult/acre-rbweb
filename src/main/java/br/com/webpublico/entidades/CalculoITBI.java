/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoITBI;
import br.com.webpublico.enums.TipoBaseCalculo;
import br.com.webpublico.enums.TipoITBI;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
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

/**
 * @author daniel
 */
@Entity
@Audited
@Etiqueta("I.T.B.I.")
public class CalculoITBI extends Calculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManyToOne
    private ProcessoCalculoITBI processoCalculoITBI;
    private Integer sequencia;
    @ManyToOne
    private Exercicio exercicio;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLancamento;
    @Enumerated(EnumType.STRING)
    private TipoITBI tipoItbi;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @ManyToOne
    private CadastroRural cadastroRural;
    @Transient
    private String imovelLista;
    private String observacao;
    @Enumerated(EnumType.STRING)
    private TipoBaseCalculo tipoBaseCalculoITBI;
    private BigDecimal valorReajuste;
    private BigDecimal valorVenal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoITBI", orphanRemoval = true)
    private List<ItemCalculoITBI> itensCalculo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoITBI", orphanRemoval = true)
    private List<AdquirentesCalculoITBI> adquirentesCalculo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoITBI", orphanRemoval = true)
    private List<TransmitentesCalculoITBI> transmitentesCalculo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoITBI", orphanRemoval = true)
    private List<PropriedadeSimulacaoITBI> proprietariosSimulacao;
    @Enumerated(EnumType.STRING)
    private SituacaoITBI situacao;
    @OneToMany(mappedBy = "calculoITBI", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoITBI> arquivos;
    private BigDecimal baseCalculo;
    @Invisivel
    private Integer numeroParcelas;
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    private Processo processo;
    @OneToMany(mappedBy = "calculoITBI", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoImpressaoLaudo> historicosLaudo;
    @ManyToOne
    private TipoIsencaoITBI tipoIsencaoITBI;
    @Transient
    private Boolean isentoSub;
    private String codigoVerificacao;
    private BigDecimal totalBaseCalculo;
    private BigDecimal percentual;

    @ManyToOne
    private UsuarioSistema usuarioSistemaRetificacao;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRetificacao;
    private String motivoRetificacao;

    @ManyToOne
    private UsuarioSistema usuarioSistemaCancelamento;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCancelamento;
    private String motivoCancelamento;
    private Integer ordemCalculo;

    public CalculoITBI() {
        setTipoCalculo(TipoCalculo.ITBI);
        this.valorReajuste = BigDecimal.ZERO;
        this.baseCalculo = BigDecimal.ZERO;
        this.totalBaseCalculo = BigDecimal.ZERO;
        this.situacao = SituacaoITBI.ABERTO;
        this.itensCalculo = Lists.newArrayList();
        this.adquirentesCalculo = Lists.newArrayList();
        this.transmitentesCalculo = Lists.newArrayList();
        this.historicosLaudo = Lists.newArrayList();
        this.arquivos = Lists.newArrayList();
        this.proprietariosSimulacao = Lists.newArrayList();
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<AdquirentesCalculoITBI> getAdquirentesCalculo() {
        return adquirentesCalculo;
    }

    public void setAdquirentesCalculo(List<AdquirentesCalculoITBI> adquirentesCalculo) {
        this.adquirentesCalculo = adquirentesCalculo;
    }

    public List<ItemCalculoITBI> getItensCalculo() {
        return itensCalculo;
    }

    public void setItensCalculo(List<ItemCalculoITBI> itensCalculo) {
        this.itensCalculo = itensCalculo;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo != null ? baseCalculo.setScale(15, RoundingMode.DOWN) : BigDecimal.ZERO;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public ProcessoCalculoITBI getProcessoCalculoITBI() {
        return processoCalculoITBI;
    }

    public void setProcessoCalculoITBI(ProcessoCalculoITBI processoCalculoITBI) {
        super.setProcessoCalculo(processoCalculoITBI);
        this.processoCalculoITBI = processoCalculoITBI;
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

    public CadastroRural getCadastroRural() {
        return cadastroRural;
    }

    public void setCadastroRural(CadastroRural cadastroRural) {
        this.cadastroRural = cadastroRural;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public TipoITBI getTipoItbi() {
        return tipoItbi;
    }

    public void setTipoItbi(TipoITBI tipoItbi) {
        this.tipoItbi = tipoItbi;
    }

    public String getImovel() {
        return cadastroImobiliario != null ? cadastroImobiliario.getCodigo() : cadastroRural.getNumeroIncra();
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

    public List<TransmitentesCalculoITBI> getTransmitentesCalculo() {
        return transmitentesCalculo;
    }

    public void setTransmitentesCalculo(List<TransmitentesCalculoITBI> transmitentesCalculo) {
        this.transmitentesCalculo = transmitentesCalculo;
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public List<ArquivoITBI> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ArquivoITBI> arquivos) {
        this.arquivos = arquivos;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoITBI;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public List<HistoricoImpressaoLaudo> getHistoricosLaudo() {
        return historicosLaudo;
    }

    public void setHistoricosLaudo(List<HistoricoImpressaoLaudo> historicosLaudo) {
        this.historicosLaudo = historicosLaudo;
    }

    @Override
    public Cadastro getCadastro() {
        return cadastroImobiliario != null ? cadastroImobiliario : cadastroRural;
    }

    public List<Pessoa> getProprietariosQueTransmitiram() {
        List<Pessoa> toReturn = new ArrayList<>();

        for (TransmitentesCalculoITBI t : transmitentesCalculo) {
            toReturn.add(t.getPessoa());
        }

        return toReturn;
    }

    public TipoIsencaoITBI getTipoIsencaoITBI() {
        return tipoIsencaoITBI;
    }

    public void setTipoIsencaoITBI(TipoIsencaoITBI tipoIsencaoITBI) {
        this.tipoIsencaoITBI = tipoIsencaoITBI;
    }

    public SituacaoITBI getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoITBI situacao) {
        this.situacao = situacao;
    }

    public void defineReferencia() {
        StringBuilder sb = new StringBuilder();
        sb.append(processoCalculoITBI.getCodigo())
            .append("/")
            .append(processoCalculoITBI.getExercicio().getAno());
        super.setReferencia(sb.toString());
    }

    public UsuarioSistema getUsuarioSistemaRetificacao() {
        return usuarioSistemaRetificacao;
    }

    public void setUsuarioSistemaRetificacao(UsuarioSistema usuarioSistemaRetificacao) {
        this.usuarioSistemaRetificacao = usuarioSistemaRetificacao;
    }

    public Date getDataRetificacao() {
        return dataRetificacao;
    }

    public void setDataRetificacao(Date dataRetificacao) {
        this.dataRetificacao = dataRetificacao;
    }

    public String getMotivoRetificacao() {
        return motivoRetificacao;
    }

    public void setMotivoRetificacao(String motivoRetificacao) {
        this.motivoRetificacao = motivoRetificacao;
    }

    public UsuarioSistema getUsuarioSistemaCancelamento() {
        return usuarioSistemaCancelamento;
    }

    public void setUsuarioSistemaCancelamento(UsuarioSistema usuarioSistemaCancelamento) {
        this.usuarioSistemaCancelamento = usuarioSistemaCancelamento;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public boolean isEmitido() {
        return SituacaoITBI.EMITIDO.equals(this.getSituacao());
    }

    public boolean isAssinado() {
        return SituacaoITBI.ASSINADO.equals(this.getSituacao());
    }

    public boolean isRetificado() {
        return SituacaoITBI.RETIFICADO.equals(this.getSituacao());
    }

    public boolean isCancelado() {
        return SituacaoITBI.CANCELADO.equals(this.getSituacao());
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

    public BigDecimal getTotalBaseCalculo() {
        if (getPercentual().compareTo(BigDecimal.ZERO) > 0) {
            return getBaseCalculo().multiply(getPercentual().divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
        }
        return getBaseCalculo();
    }

    public Pessoa getAdquirente() {
        if (adquirentesCalculo != null && !adquirentesCalculo.isEmpty()) {
            ordernarAdquirentes();
            return adquirentesCalculo.get(0).getAdquirente();
        }
        return null;
    }

    public List<PropriedadeSimulacaoITBI> getProprietariosSimulacao() {
        return proprietariosSimulacao;
    }

    public void setProprietariosSimulacao(List<PropriedadeSimulacaoITBI> proprietariosSimulacao) {
        this.proprietariosSimulacao = proprietariosSimulacao;
    }

    public void ordernarAdquirentes() {

        Collections.sort(adquirentesCalculo, new Comparator<AdquirentesCalculoITBI>() {
            @Override
            public int compare(AdquirentesCalculoITBI o1, AdquirentesCalculoITBI o2) {
                String nome1 = o1.getAdquirente() != null ? o1.getAdquirente().getNome() : "";
                String nome2 = o2.getAdquirente() != null ? o2.getAdquirente().getNome() : "";
                return ComparisonChain.start().compare(nome2, nome1, Ordering.<String>natural().reverse()).result();
            }
        });

        Collections.sort(adquirentesCalculo, new Comparator<AdquirentesCalculoITBI>() {
            @Override
            public int compare(AdquirentesCalculoITBI o1, AdquirentesCalculoITBI o2) {
                BigDecimal percentual1 = o1.getPercentual() != null ? o1.getPercentual() : BigDecimal.ZERO;
                BigDecimal percentual2 = o2.getPercentual() != null ? o2.getPercentual() : BigDecimal.ZERO;
                return ComparisonChain.start().compare(percentual1, percentual2, Ordering.<BigDecimal>natural().reverse()).result();
            }
        });
    }

    public BigDecimal getTotalPercentualAdquirentes() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (AdquirentesCalculoITBI ac : getAdquirentesCalculo()) {
            total = total.add(ac.getPercentual());
        }
        return total;
    }
}
