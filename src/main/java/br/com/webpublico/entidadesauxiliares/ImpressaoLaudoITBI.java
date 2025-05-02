package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ImpressaoLaudoITBI implements Serializable {
    private Long idProcesso;
    private Long idCadastroImobiliario;
    private Long idCadastroRural;
    private Long sequencia;
    private Integer exercicio;
    private Integer linha;
    private BigDecimal valorTotal;
    private BigDecimal baseCalculo;
    private BigDecimal valorVenal;
    private String tipoITBI;
    private String processo;
    private String observacao;
    private String codigoVerificacao;
    private String sequenciaZero;
    private String responsavelComissao;
    private String diretor;
    private String funcaoResponsavel;
    private String funcaoDiretor;
    private Date vencimentoLaudo;
    private Date lancamento;
    private Date dataImpressao;
    private Date dataImpressao2Via;
    private Boolean laudoImpresso;
    private Boolean segundaVia;
    private InputStream qrCode;
    private List<TransmissoesImpressaoLaudoITBI> transmissoes;
    private List<DadosImovelLaudoITBI> dadosImovel;
    private List<DadosImovelLaudoITBI> dadosImovelRural;
    private List<DadosValoresLaudoITBI> valoresLaudo;
    private List<DadosDAMsLaudoITBI> dans;
    private List<DadosRetificacoesLaudoITBI> retificacoes;
    private Boolean temIsencao;

    public ImpressaoLaudoITBI() {
        this.valorTotal = BigDecimal.ZERO;
        this.baseCalculo = BigDecimal.ZERO;
        this.valorVenal = BigDecimal.ZERO;
        this.laudoImpresso = Boolean.FALSE;
        this.segundaVia = Boolean.FALSE;
        this.temIsencao = Boolean.FALSE;
        this.dadosImovel = Lists.newArrayList();
        this.dadosImovelRural = Lists.newArrayList();
        this.valoresLaudo = Lists.newArrayList();
        this.dans = Lists.newArrayList();
        this.retificacoes = Lists.newArrayList();
        this.transmissoes = Lists.newArrayList();
    }

    public Long getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public Long getIdCadastroImobiliario() {
        return idCadastroImobiliario;
    }

    public void setIdCadastroImobiliario(Long idCadastroImobiliario) {
        this.idCadastroImobiliario = idCadastroImobiliario;
    }

    public Long getIdCadastroRural() {
        return idCadastroRural;
    }

    public void setIdCadastroRural(Long idCadastroRural) {
        this.idCadastroRural = idCadastroRural;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getLinha() {
        return linha;
    }

    public void setLinha(Integer linha) {
        this.linha = linha;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getValorVenal() {
        return valorVenal;
    }

    public void setValorVenal(BigDecimal valorVenal) {
        this.valorVenal = valorVenal;
    }

    public String getTipoITBI() {
        return tipoITBI;
    }

    public void setTipoITBI(String tipoITBI) {
        this.tipoITBI = tipoITBI;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public String getSequenciaZero() {
        return sequenciaZero;
    }

    public void setSequenciaZero(String sequenciaZero) {
        this.sequenciaZero = sequenciaZero;
    }

    public String getResponsavelComissao() {
        return responsavelComissao;
    }

    public void setResponsavelComissao(String responsavelComissao) {
        this.responsavelComissao = responsavelComissao;
    }

    public String getDiretor() {
        return diretor;
    }

    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }

    public String getFuncaoResponsavel() {
        return funcaoResponsavel;
    }

    public void setFuncaoResponsavel(String funcaoResponsavel) {
        this.funcaoResponsavel = funcaoResponsavel;
    }

    public String getFuncaoDiretor() {
        return funcaoDiretor;
    }

    public void setFuncaoDiretor(String funcaoDiretor) {
        this.funcaoDiretor = funcaoDiretor;
    }

    public Date getVencimentoLaudo() {
        return vencimentoLaudo;
    }

    public void setVencimentoLaudo(Date vencimentoLaudo) {
        this.vencimentoLaudo = vencimentoLaudo;
    }

    public Date getLancamento() {
        return lancamento;
    }

    public void setLancamento(Date lancamento) {
        this.lancamento = lancamento;
    }

    public Date getDataImpressao() {
        return dataImpressao;
    }

    public void setDataImpressao(Date dataImpressao) {
        this.dataImpressao = dataImpressao;
    }

    public Date getDataImpressao2Via() {
        return dataImpressao2Via;
    }

    public void setDataImpressao2Via(Date dataImpressao2Via) {
        this.dataImpressao2Via = dataImpressao2Via;
    }

    public Boolean getLaudoImpresso() {
        return laudoImpresso;
    }

    public void setLaudoImpresso(Boolean laudoImpresso) {
        this.laudoImpresso = laudoImpresso;
    }

    public Boolean getSegundaVia() {
        return segundaVia;
    }

    public void setSegundaVia(Boolean segundaVia) {
        this.segundaVia = segundaVia;
    }

    public InputStream getQrCode() {
        return qrCode;
    }

    public void setQrCode(InputStream qrCode) {
        this.qrCode = qrCode;
    }

    public List<DadosImovelLaudoITBI> getDadosImovel() {
        return dadosImovel;
    }

    public void setDadosImovel(List<DadosImovelLaudoITBI> dadosImovel) {
        this.dadosImovel = dadosImovel;
    }

    public List<DadosImovelLaudoITBI> getDadosImovelRural() {
        return dadosImovelRural;
    }

    public void setDadosImovelRural(List<DadosImovelLaudoITBI> dadosImovelRural) {
        this.dadosImovelRural = dadosImovelRural;
    }

    public List<DadosValoresLaudoITBI> getValoresLaudo() {
        return valoresLaudo;
    }

    public void setValoresLaudo(List<DadosValoresLaudoITBI> valoresLaudo) {
        this.valoresLaudo = valoresLaudo;
    }

    public List<DadosDAMsLaudoITBI> getDans() {
        return dans;
    }

    public void setDans(List<DadosDAMsLaudoITBI> dans) {
        this.dans = dans;
    }

    public List<DadosRetificacoesLaudoITBI> getRetificacoes() {
        return retificacoes;
    }

    public void setRetificacoes(List<DadosRetificacoesLaudoITBI> retificacoes) {
        this.retificacoes = retificacoes;
    }

    public void ordenarRetificacoes() {
        if (retificacoes != null) {
            Collections.sort(retificacoes, new Comparator<DadosRetificacoesLaudoITBI>() {
                @Override
                public int compare(DadosRetificacoesLaudoITBI o1, DadosRetificacoesLaudoITBI o2) {
                    return ComparisonChain
                        .start()
                        .compare(o1.getNumeroRetificacao(), o2.getNumeroRetificacao())
                        .result();
                }
            });
        }
    }

    public List<TransmissoesImpressaoLaudoITBI> getTransmissoes() {
        return transmissoes;
    }

    public void setTransmissoes(List<TransmissoesImpressaoLaudoITBI> transmissoes) {
        this.transmissoes = transmissoes;
    }

    public Boolean getTemIsencao() {
        return temIsencao;
    }

    public void setTemIsencao(Boolean temIsencao) {
        this.temIsencao = temIsencao;
    }

    public void temIsencao() {
        temIsencao = false;
        for (TransmissoesImpressaoLaudoITBI transmissao : transmissoes) {
            if(transmissao.getIsento()) {
                temIsencao = true;
                break;
            }
        }
    }
}
