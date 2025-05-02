/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCalculoDividaDiversa;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
@Entity

@Audited
@Etiqueta("Lançamento de Dívidas Diversas")
@GrupoDiagrama(nome = "Divida Diversa")
public class CalculoDividaDiversa extends Calculo implements Serializable, Comparable<CalculoDividaDiversa> {

    private static final long serialVersionUID = 1L;
    @Tabelavel
    @Etiqueta(value = "Exercício")
    @ManyToOne
    @Pesquisavel
    private Exercicio exercicio;
    @Tabelavel
    @Etiqueta(value = "Número")
    @Pesquisavel
    private Integer numeroLancamento;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Data Lancamento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLancamento;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta(value = "Tipo de Cadastro")
    @Tabelavel
    private TipoCadastroTributario tipoCadastroTributario;
    @ManyToOne
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @Tabelavel
    @Etiqueta(value = "Tipo Dívida Diversa")
    @ManyToOne
    private TipoDividaDiversa tipoDividaDiversa;
    @Invisivel
    private String observacao;
    @Transient
    @Etiqueta("Cadastro")
    private String cadastroPessoa;
    @Invisivel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta(value = "Vencimento")
    private Date dataVencimento;
    @Invisivel
    @Etiqueta(value = "Número do Processo")
    @Pesquisavel
    private Integer numeroProcessoProtocolo;
    @Invisivel
    private Integer anoProcessoProtocolo;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoCalculoDividaDiversa situacao;
    @Invisivel
    @ManyToOne
    private UsuarioSistema usuarioLancamento;
    @Invisivel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEfetivacao;
    @Invisivel
    @ManyToOne
    private UsuarioSistema usuarioEfetivacao;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "calculoDividaDiversa")
    private List<ItemCalculoDivDiversa> itens;
    @ManyToOne
    private ProcessoCalcDivDiversas processoCalcDivDiversas;
    private Integer numeroParcelas;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private CancelamentoDividaDiversa cancelamentoDividaDiversa;
    @Invisivel
    @OneToMany(mappedBy = "calculoDividaDiversa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoCalcDividaDiversa> arquivoCalcDividaDiversas;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Transient
    private String numeroFormatado;
    @Tabelavel
    @Monetario
    @Transient
    @Etiqueta("Valor")
    private BigDecimal valorTotalTabelavel;
    @Tabelavel
    @Transient
    @Etiqueta("Cadastro")
    private String cadastroTabelavel;
    @Tabelavel
    @Transient
    @Etiqueta("Nome/Razão Social e CPF/CNPJ")
    private List<String> pessoasTabelavel;
    @Etiqueta(value = "Exercício do Débito")
    @ManyToOne
    private Exercicio exercicioDoDebito;

    public CalculoDividaDiversa(Long id, Date dataLancamento, Date dataVencimento, Pessoa pessoa, Cadastro cadastro, Exercicio exercicio, Integer numero, TipoDividaDiversa tipo, SituacaoCalculoDividaDiversa situacao) {
        super.setId(id);
        this.dataLancamento = dataLancamento;
        this.dataVencimento = dataVencimento;
        this.cadastroPessoa = cadastro != null ? cadastro.getNumeroCadastro() : pessoa.getNomeCpfCnpj() + " " + pessoa.getCpf_Cnpj() != null ? pessoa.getCpf_Cnpj() : "";
        this.exercicio = exercicio;
        this.numeroLancamento = numero;
        this.tipoDividaDiversa = tipo;
        this.situacao = situacao;
        this.criadoEm = System.nanoTime();
        super.setTipoCalculo(TipoCalculo.DIVIDA_DIVERSA);
    }

    public CalculoDividaDiversa() {
        this.criadoEm = System.nanoTime();
        this.setTipoCalculo(TipoCalculo.DIVIDA_DIVERSA);
        this.arquivoCalcDividaDiversas = new ArrayList<>();
        this.itens = Lists.newArrayList();
        this.setPessoas(Lists.<CalculoPessoa>newArrayList());
        pessoasTabelavel = new ArrayList<String>();
    }

    public String getNumeroFormatado() {
        return String.format("%06d", getNumeroLancamento());
    }

    public void setNumeroFormatado(String numeroFormatado) {
        this.numeroFormatado = numeroFormatado;
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getNumeroLancamento() {
        return numeroLancamento;
    }

    public void setNumeroLancamento(Integer numeroLancamento) {
        this.numeroLancamento = numeroLancamento;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public TipoDividaDiversa getTipoDividaDiversa() {
        return tipoDividaDiversa;
    }

    public void setTipoDividaDiversa(TipoDividaDiversa tipoDividaDiversa) {
        this.tipoDividaDiversa = tipoDividaDiversa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Integer getNumeroProcessoProtocolo() {
        return numeroProcessoProtocolo;
    }

    public void setNumeroProcessoProtocolo(Integer numeroProcessoProtocolo) {
        this.numeroProcessoProtocolo = numeroProcessoProtocolo;
    }

    public Integer getAnoProcessoProtocolo() {
        return anoProcessoProtocolo;
    }

    public void setAnoProcessoProtocolo(Integer anoProcessoProtocolo) {
        this.anoProcessoProtocolo = anoProcessoProtocolo;
    }

    public SituacaoCalculoDividaDiversa getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCalculoDividaDiversa situacao) {
        this.situacao = situacao;
    }

    public UsuarioSistema getUsuarioLancamento() {
        return usuarioLancamento;
    }

    public void setUsuarioLancamento(UsuarioSistema usuarioLancamento) {
        this.usuarioLancamento = usuarioLancamento;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public UsuarioSistema getUsuarioEfetivacao() {
        return usuarioEfetivacao;
    }

    public void setUsuarioEfetivacao(UsuarioSistema usuarioEfetivacao) {
        this.usuarioEfetivacao = usuarioEfetivacao;
    }

    public List<ItemCalculoDivDiversa> getItens() {
        return itens;
    }

    public void setItens(List<ItemCalculoDivDiversa> itens) {
        this.itens = itens;
    }

    public ProcessoCalcDivDiversas getProcessoCalcDivDiversas() {
        return processoCalcDivDiversas;
    }

    public void setProcessoCalcDivDiversas(ProcessoCalcDivDiversas processoCalcDivDiversas) {
        super.setProcessoCalculo(processoCalcDivDiversas);
        this.processoCalcDivDiversas = processoCalcDivDiversas;
    }

    public CancelamentoDividaDiversa getCancelamentoDividaDiversa() {
        return cancelamentoDividaDiversa;
    }

    public void setCancelamentoDividaDiversa(CancelamentoDividaDiversa cancelamentoDividaDiversa) {
        this.cancelamentoDividaDiversa = cancelamentoDividaDiversa;
    }

    public String getCadastroPessoa() {
        if (cadastroPessoa == null) {
            cadastroPessoa = getCadastro() != null ? getCadastro().getNumeroCadastro() : pessoa.getNomeCpfCnpj() + " " + pessoa.getCpf_Cnpj();
        }
        return cadastroPessoa;
    }

    public void setCadastroPessoa(String CasdastroPessoa) {
        this.cadastroPessoa = CasdastroPessoa;
    }

    public List<ArquivoCalcDividaDiversa> getArquivoCalcDividaDiversas() {
        return arquivoCalcDividaDiversas;
    }

    public void setArquivoCalcDividaDiversas(List<ArquivoCalcDividaDiversa> arquivoCalcDividaDiversas) {
        this.arquivoCalcDividaDiversas = arquivoCalcDividaDiversas;
    }

    public BigDecimal getValorTotalTabelavel() {
        return valorTotalTabelavel;
    }

    public void setValorTotalTabelavel(BigDecimal valorTotalTabelavel) {
        this.valorTotalTabelavel = valorTotalTabelavel;
    }

    public String getCadastroTabelavel() {
        return cadastroTabelavel;
    }

    public void setCadastroTabelavel(String cadastroTabelavel) {
        this.cadastroTabelavel = cadastroTabelavel;
    }

    public List<String> getPessoasTabelavel() {
        return pessoasTabelavel;
    }

    public void setPessoasTabelavel(List<String> pessoasTabelavel) {
        this.pessoasTabelavel = pessoasTabelavel;
    }

    public Exercicio getExercicioDoDebito() {
        return exercicioDoDebito;
    }

    public void setExercicioDoDebito(Exercicio exercicioDoDebito) {
        this.exercicioDoDebito = exercicioDoDebito;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public String toString() {
        if (exercicio != null) {
            return "Exercício: " + exercicio.getAno() + " - " + numeroLancamento;
        }
        return "Número: " + numeroLancamento;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalcDivDiversas;
    }

    @Override
    public int compareTo(CalculoDividaDiversa o) {
        try {
            int retorno = o.getExercicio().getAno().compareTo(this.exercicio.getAno());
            if (retorno == 0) {
                retorno = o.getNumeroLancamento().compareTo(this.numeroLancamento);
            }
            return retorno;
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

}
