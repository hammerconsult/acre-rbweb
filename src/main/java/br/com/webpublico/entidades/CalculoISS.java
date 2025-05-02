/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoCalculoISS;
import br.com.webpublico.enums.TipoSituacaoCalculoISS;
import br.com.webpublico.nfse.domain.dtos.enums.IssqnFmTipoLancamentoNfseDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import br.com.webpublico.util.anotacoes.UFM;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author daniel
 */
@Entity
@Etiqueta("Cálculos de ISS")
@Audited
public class CalculoISS extends Calculo {

    private static final long serialVersionUID = 1L;
    @ManyToOne
    private ProcessoCalculoISS processoCalculoISS;
    @Pesquisavel
    @Etiqueta("C.M.C.")
    @Tabelavel(ordemApresentacao = 1)
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Transient
    @Tabelavel(ordemApresentacao = 2)
    @Etiqueta(value = "Nome/Razão Social")
    private Pessoa pessoaParaLista;
    @Transient
    @Tabelavel(ordemApresentacao = 3)
    @Etiqueta(value = "CPF/CNPJ")
    private String cpfCnpjLista;
    @Transient
    @Tabelavel(ordemApresentacao = 7, ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Etiqueta(value = "Referência")
    private String exerciciodaLista;
    @Transient
    private Mes mesdeReferencia;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 8)
    @Etiqueta("Sequência")
    private Long sequenciaLancamento;
    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA, ordemApresentacao = 6)
    @UFM
    @Etiqueta("Valor do Faturamento (R$)")
    private BigDecimal faturamento;
    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA, ordemApresentacao = 8)
    @UFM
    @Etiqueta("Base de Cálculo (R$)")
    private BigDecimal baseCalculo;
    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA, ordemApresentacao = 9)
    @UFM
    @Etiqueta("Valor do ISS (R$)")
    private BigDecimal valorCalculado;
    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA, ordemApresentacao = 10)
    @UFM
    @Etiqueta("TSA (R$)")
    private BigDecimal taxaSobreIss;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Tipo do Cálculo")
    private TipoCalculoISS tipoCalculoISS;
    private BigDecimal aliquota;
    @OneToMany(mappedBy = "calculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCalculoIss> itemCalculoIsss;
    private Boolean ausenciaMovimento;
    @Transient
    private Map<Tributo, BigDecimal> itemValor;
    @Etiqueta(value = "Situação do Lançamento")
    @Tabelavel(ordemApresentacao = 5)
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoSituacaoCalculoISS tipoSituacaoCalculoISS;
    @ManyToOne
    private UsuarioSistema usuarioLancamento;
    @Transient
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 6, ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Etiqueta(value = "Data do Lançamento")
    @Temporal(TemporalType.DATE)
    private Date dataLacamento;
    @Etiqueta(value = "Exercício")
    @Transient
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 8, ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    private Exercicio exercicioProcesso;
    @OneToOne(mappedBy = "calculoISS")
    private ProcessoCalculoMultaAcessoria processoMultaAcessoria;
    @Etiqueta(value = "Multa Acessória (R$)")
    @Transient
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 8, ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @UFM
    private BigDecimal valorMultaAcessoria;
    private String motivoLancamentoValorMenor;
    private Integer quantidadeProfissionais;
    private BigDecimal qtdeUFMProfissionalSocio;
    private Boolean notaEletronica;
    @ManyToOne
    private CalculoISSEstorno calculoISSEstorno;
    @Enumerated(EnumType.STRING)
    private IssqnFmTipoLancamentoNfseDTO issqnFmTipoLancamentoNfse;
    @Transient
    @Tabelavel(ordemApresentacao = 4)
    @Etiqueta("Situação do Débito")
    private String situacaoDebito;

    public CalculoISS() {
        super();
        sequenciaLancamento = 1l;
        itemCalculoIsss = new ArrayList<>();
        aliquota = BigDecimal.ZERO;
        baseCalculo = BigDecimal.ZERO;
        faturamento = BigDecimal.ZERO;
        valorCalculado = BigDecimal.ZERO;
        taxaSobreIss = BigDecimal.ZERO;
        ausenciaMovimento = false;
        super.setTipoCalculo(TipoCalculo.ISS);
        exercicioProcesso = new Exercicio();

    }

    public CalculoISS(CalculoISS calculo, ParcelaValorDivida parcelaValorDivida) {
        this.setId(calculo.getId());
        this.processoMultaAcessoria = calculo.getProcessoMultaAcessoria();
        this.setCadastroEconomico(calculo.getCadastroEconomico());
        this.pessoaParaLista = calculo.getPessoas().get(0).getPessoa();
        Mes mes = Mes.getMesToInt(calculo.getProcessoCalculoISS().getMesReferencia());
        this.exerciciodaLista = (mes != null ? mes + "/" : "") + calculo.getProcessoCalculoISS().getExercicio();
        this.exercicioProcesso = calculo.getProcessoCalculoISS().getExercicio();
        if (this.pessoaParaLista != null) {
            this.cpfCnpjLista = this.pessoaParaLista.getCpf_Cnpj();
        }
        this.processoCalculoISS = calculo.getProcessoCalculoISS();
        this.mesdeReferencia = Mes.getMesToInt(calculo.getProcessoCalculoISS().getMesReferencia());
        this.baseCalculo = calculo.getBaseCalculo();
        this.faturamento = calculo.getFaturamento();
        this.valorCalculado = calculo.getValorCalculado();
        this.taxaSobreIss = calculo.getTaxaSobreIss();
        this.sequenciaLancamento = calculo.getSequenciaLancamento();
        itemCalculoIsss = new ArrayList<>();
        aliquota = BigDecimal.ZERO;
        ausenciaMovimento = false;
        super.setId(calculo.getId());
        super.setDataCalculo(calculo.getProcessoCalculo().getDataLancamento());
        dataLacamento = calculo.getProcessoCalculo().getDataLancamento();
        this.tipoSituacaoCalculoISS = calculo.getTipoSituacaoCalculoISS();
        super.setTipoCalculo(TipoCalculo.ISS);
        try {
            this.valorMultaAcessoria = this.processoMultaAcessoria.getCalculos().get(0).getValorReal();
        } catch (java.lang.NullPointerException ex) {
            this.valorMultaAcessoria = BigDecimal.ZERO;
        }
        this.setCalculoISSEstorno(calculo.getCalculoISSEstorno());
        if (parcelaValorDivida != null) {
            this.setSituacaoDebito(parcelaValorDivida.getSituacaoAtual().getSituacaoParcela().getDescricao());
        }
    }

    public CalculoISS(Long id, CadastroEconomico cmc, ProcessoCalculoISS processo, BigDecimal baseCalculo, BigDecimal faturamento, BigDecimal valorCalculado, BigDecimal taxaSobreIss, TipoSituacaoCalculoISS situacaoCalculoISS, Long sequenciaLancamento, CalculoISSEstorno calculoISSEstorno) {
        this.setCadastroEconomico(cmc);
        this.pessoaParaLista = cmc.getPessoa();
        Mes mes = Mes.getMesToInt(processo.getMesReferencia());
        this.exerciciodaLista = (mes != null ? mes + "/" : "") + processo.getExercicio();
        this.exercicioProcesso = processo.getExercicio();
        if (cmc.getPessoa() != null) {
            this.cpfCnpjLista = cmc.getPessoa().getCpf_Cnpj();
        }
        this.processoCalculoISS = processo;
        this.mesdeReferencia = Mes.getMesToInt(processo.getMesReferencia());
        this.baseCalculo = baseCalculo;
        this.faturamento = faturamento;
        this.valorCalculado = valorCalculado;
        this.taxaSobreIss = taxaSobreIss;
        this.sequenciaLancamento = sequenciaLancamento;
        itemCalculoIsss = new ArrayList<>();
        aliquota = BigDecimal.ZERO;
        ausenciaMovimento = false;
        this.setId(id);
        this.setDataCalculo(processo.getDataLancamento());
        dataLacamento = processo.getDataLancamento();
        this.tipoSituacaoCalculoISS = situacaoCalculoISS;
        super.setTipoCalculo(TipoCalculo.ISS);
        this.calculoISSEstorno = calculoISSEstorno;
    }

    public CalculoISS(Long id, CadastroEconomico cmc, ProcessoCalculoISS processo, BigDecimal baseCalculo, BigDecimal faturamento, BigDecimal valorCalculado, BigDecimal taxaSobreIss, TipoSituacaoCalculoISS situacaoCalculoISS, Long sequenciaLancamento) {
        this.setCadastroEconomico(cmc);
        this.pessoaParaLista = cmc.getPessoa();
        Mes mes = Mes.getMesToInt(processo.getMesReferencia());
        this.exerciciodaLista = (mes != null ? mes + "/" : "") + processo.getExercicio();
        this.exercicioProcesso = processo.getExercicio();
        if (cmc.getPessoa() != null) {
            this.cpfCnpjLista = cmc.getPessoa().getCpf_Cnpj();
        }
        this.processoCalculoISS = processo;
        this.mesdeReferencia = Mes.getMesToInt(processo.getMesReferencia());
        this.baseCalculo = baseCalculo;
        this.faturamento = faturamento;
        this.valorCalculado = valorCalculado;
        this.taxaSobreIss = taxaSobreIss;
        this.sequenciaLancamento = sequenciaLancamento;
        itemCalculoIsss = new ArrayList<>();
        aliquota = BigDecimal.ZERO;
        ausenciaMovimento = false;
        this.setId(id);
        this.setDataCalculo(processo.getDataLancamento());
        dataLacamento = processo.getDataLancamento();
        this.tipoSituacaoCalculoISS = situacaoCalculoISS;
        super.setTipoCalculo(TipoCalculo.ISS);
    }

    public Map<Tributo, BigDecimal> getItemValor() {
        itemValor = new HashMap<>();
        for (ItemCalculoIss item : itemCalculoIsss) {
            if (itemValor.containsKey(item.getTributo())) {
                BigDecimal valorAtual = itemValor.get(item.getTributo());
                itemValor.put(item.getTributo(), valorAtual.add(item.getValorCalculado()));
            } else {
                itemValor.put(item.getTributo(), item.getValorCalculado());
            }
        }
        return itemValor;
    }

    public Integer getQuantidadeProfissionais() {
        return quantidadeProfissionais;
    }

    public void setQuantidadeProfissionais(Integer quantidadeProfissionais) {
        this.quantidadeProfissionais = quantidadeProfissionais;
    }

    public BigDecimal getQtdeUFMProfissionalSocio() {
        return qtdeUFMProfissionalSocio;
    }

    public void setQtdeUFMProfissionalSocio(BigDecimal qtdeUFMProfissionalSocio) {
        this.qtdeUFMProfissionalSocio = qtdeUFMProfissionalSocio;
    }

    public Exercicio getExercicioProcesso() {
        return exercicioProcesso;
    }

    public void setExercicioProcesso(Exercicio exercicioProcesso) {
        this.exercicioProcesso = exercicioProcesso;
    }

    public String getExerciciodaLista() {
        return exerciciodaLista;
    }

    public void setExerciciodaLista(String exerciciodaLista) {
        this.exerciciodaLista = exerciciodaLista;
    }

    public Mes getMesdeReferencia() {
        return mesdeReferencia;
    }

    public void setMesdeReferencia(Mes mesdeReferencia) {
        this.mesdeReferencia = mesdeReferencia;
    }

    public String getCpfCnpjLista() {
        return cpfCnpjLista;
    }

    public void setCpfCnpjLista(String cpfCnpjLista) {
        this.cpfCnpjLista = cpfCnpjLista;
    }

    public Pessoa getPessoaParaLista() {
        return pessoaParaLista;
    }

    public void setPessoaParaLista(Pessoa pessoaParaLista) {
        this.pessoaParaLista = pessoaParaLista;
    }

    public ProcessoCalculoISS getProcessoCalculoISS() {
        return processoCalculoISS;
    }

    public void setProcessoCalculoISS(ProcessoCalculoISS processoCalculoISS) {
        super.setProcessoCalculo(processoCalculoISS);
        this.processoCalculoISS = processoCalculoISS;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
        setCadastro(cadastroEconomico);
    }

    public TipoCalculoISS getTipoCalculoISS() {
        return tipoCalculoISS;
    }

    public void setTipoCalculoISS(TipoCalculoISS tipoCalculoISS) {
        this.tipoCalculoISS = tipoCalculoISS;
    }

    public BigDecimal getValorCalculado() {
        return valorCalculado;
    }

    public void setValorCalculado(BigDecimal valorCalculado) {
        this.valorCalculado = valorCalculado;
    }

    public BigDecimal getFaturamento() {
        return faturamento;
    }

    public void setFaturamento(BigDecimal faturamento) {
        this.faturamento = faturamento;
    }

    public Long getSequenciaLancamento() {
        return sequenciaLancamento;
    }

    public void setSequenciaLancamento(Long sequenciaLancamento) {
        this.sequenciaLancamento = sequenciaLancamento;
    }

    public List<ItemCalculoIss> getItemCalculoIsss() {
        return itemCalculoIsss;
    }

    public void setItemCalculoIsss(List<ItemCalculoIss> itemCalculoIsss) {
        this.itemCalculoIsss = itemCalculoIsss;
    }

    public BigDecimal getTaxaSobreIss() {
        return taxaSobreIss;
    }

    public void setTaxaSobreIss(BigDecimal taxaSobreIss) {
        this.taxaSobreIss = taxaSobreIss;
    }

    public Boolean getAusenciaMovimento() {
        return ausenciaMovimento != null ? ausenciaMovimento : false;
    }

    public void setAusenciaMovimento(Boolean ausenciaMovimento) {
        this.ausenciaMovimento = ausenciaMovimento;
    }

    public TipoSituacaoCalculoISS getTipoSituacaoCalculoISS() {
        return tipoSituacaoCalculoISS;
    }

    public void setTipoSituacaoCalculoISS(TipoSituacaoCalculoISS tipoSituacaoCalculoISS) {
        this.tipoSituacaoCalculoISS = tipoSituacaoCalculoISS;
    }

    public UsuarioSistema getUsuarioLancamento() {
        return usuarioLancamento;
    }

    public void setUsuarioLancamento(UsuarioSistema usuarioLancamento) {
        this.usuarioLancamento = usuarioLancamento;
    }

    public Date getDataLacamento() {
        return dataLacamento;
    }

    public void setDataLacamento(Date dataLacamento) {
        this.dataLacamento = dataLacamento;
    }

    public String getMotivoLancamentoValorMenor() {
        return motivoLancamentoValorMenor;
    }

    public void setMotivoLancamentoValorMenor(String motivoLancamentoValorMenor) {
        this.motivoLancamentoValorMenor = motivoLancamentoValorMenor;
    }

    public ProcessoCalculoMultaAcessoria getProcessoMultaAcessoria() {
        return processoMultaAcessoria;
    }

    public void setProcessoMultaAcessoria(ProcessoCalculoMultaAcessoria processoMultaAcessoria) {
        this.processoMultaAcessoria = processoMultaAcessoria;
    }

    public BigDecimal getValorMultaAcessoria() {
        return valorMultaAcessoria;
    }

    public void setValorMultaAcessoria(BigDecimal valorMultaAcessoria) {
        this.valorMultaAcessoria = valorMultaAcessoria;
    }

    public Boolean getNotaEletronica() {
        return notaEletronica != null ? notaEletronica : false;
    }

    public void setNotaEletronica(Boolean notaEletronica) {
        this.notaEletronica = notaEletronica;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoISS;
    }

    public CalculoISSEstorno getCalculoISSEstorno() {
        return calculoISSEstorno;
    }

    public void setCalculoISSEstorno(CalculoISSEstorno calculoISSEstorno) {
        this.calculoISSEstorno = calculoISSEstorno;
    }

    public IssqnFmTipoLancamentoNfseDTO getIssqnFmTipoLancamentoNfse() {
        return issqnFmTipoLancamentoNfse;
    }

    public void setIssqnFmTipoLancamentoNfse(IssqnFmTipoLancamentoNfseDTO issqnFmTipoLancamentoNfse) {
        this.issqnFmTipoLancamentoNfse = issqnFmTipoLancamentoNfse;
    }

    public String getSituacaoDebito() {
        return situacaoDebito;
    }

    public void setSituacaoDebito(String situacaoDebito) {
        this.situacaoDebito = situacaoDebito;
    }
}
