package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCalculo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Lançamento de Taxas Diversas")
public class CalculoTaxasDiversas extends Calculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Etiqueta("Exercício")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private Exercicio exercicio;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private Long numero;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Data do Lançamento")
    @Temporal(TemporalType.DATE)
    private Date dataHoraLancamento;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "calculoTaxasDiversas")
    private List<ItemCalculoTaxasDiversas> itens;
    @Tabelavel
    @Etiqueta(value = "Data de Vencimento")
    @Temporal(TemporalType.DATE)
    private Date vencimento;
    @ManyToOne
    @Etiqueta(value = "Contribuinte")
    private Pessoa contribuinte;
    private String observacao;
    @ManyToOne
    @Invisivel
    private ProcessoCalcTaxasDiversas processoCalcTaxasDiversas;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Situação da Taxa Diversa")
    private SituacaoCalculo situacao;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta(value = "Tipo de Cadastro")
    @Pesquisavel
    private TipoCadastroTributario tipoCadastroTributario;
    @Etiqueta(value = "Usuário do Lançamento")
    @ManyToOne
    private UsuarioSistema usuarioLancamento;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private CancelamentoTaxasDiversas cancelamentoTaxasDiversas;
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
    @Etiqueta("Contribuinte")
    private String nomeTabelavel;

    public CalculoTaxasDiversas() {
        vencimento = new Date();
        setDataCalculo(new Date());
        itens = new ArrayList<>();
        super.setTipoCalculo(TipoCalculo.TAXAS_DIVERSAS);
    }

    public CalculoTaxasDiversas(Long id, Exercicio exercicio, Long numero, Date dataLancamento, BigDecimal valorTotal, Date dataVencimento, SituacaoCalculo situacao, TipoCadastroTributario tipoCadastro, CalculoTaxasDiversas calculoTaxasDiversas, Pessoa contribuinte, String nomeTabelavel) {
        super.setId(id);
        this.exercicio = exercicio;
        this.numero = numero;
        this.dataHoraLancamento = dataLancamento;
        this.valorTotalTabelavel = valorTotal;
        this.vencimento = dataVencimento;
        this.situacao = situacao;
        this.tipoCadastroTributario = tipoCadastro;
        this.setCadastro(calculoTaxasDiversas.getCadastro());
        this.contribuinte = contribuinte;
        if (getCadastro() != null) {
            this.cadastroTabelavel = getCadastro().getNumeroCadastro();
        } else {
            this.cadastroTabelavel = contribuinte != null ? contribuinte.getCpf_Cnpj() : "";
        }
        this.nomeTabelavel = nomeTabelavel;
    }

    public String getNumeroFormatado() {
        return String.format("%06d", getNumero());
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public CancelamentoTaxasDiversas getCancelamentoTaxasDiversas() {
        return cancelamentoTaxasDiversas;
    }

    public void setCancelamentoTaxasDiversas(CancelamentoTaxasDiversas cancelamentoTaxasDiversas) {
        this.cancelamentoTaxasDiversas = cancelamentoTaxasDiversas;
    }

    public UsuarioSistema getUsuarioLancamento() {
        return usuarioLancamento;
    }

    public void setUsuarioLancamento(UsuarioSistema usuarioLancamento) {
        this.usuarioLancamento = usuarioLancamento;
    }

    public Date getDataHoraLancamento() {
        return dataHoraLancamento;
    }

    public void setDataHoraLancamento(Date dataHoraLancamento) {
        this.dataHoraLancamento = dataHoraLancamento;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public List<ItemCalculoTaxasDiversas> getItens() {
        return itens;
    }

    public void setItens(List<ItemCalculoTaxasDiversas> itens) {
        this.itens = itens;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public SituacaoCalculo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCalculo situacaoCalculo) {
        this.situacao = situacaoCalculo;
    }

    @Override
    public String toString() {
        return " R$-" + getValorReal() + " Vencimento: " + new SimpleDateFormat("dd/MM/yyyy").format(vencimento);
    }

    public ProcessoCalcTaxasDiversas getProcessoCalcTaxasDiversas() {
        return processoCalcTaxasDiversas;
    }

    public void setProcessoCalcTaxasDiversas(ProcessoCalcTaxasDiversas processoCalcTaxasDiversas) {
        super.setProcessoCalculo(processoCalcTaxasDiversas);
        this.processoCalcTaxasDiversas = processoCalcTaxasDiversas;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
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

    public String getNomeTabelavel() {
        return nomeTabelavel;
    }

    public void setNomeTabelavel(String nomeTabelavel) {
        this.nomeTabelavel = nomeTabelavel;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalcTaxasDiversas;
    }

}
