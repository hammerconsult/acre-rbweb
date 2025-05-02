/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Previsão Inicial da Receita")
public class ReceitaLOA extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LOA loa;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Conta de Receita")
    private Conta contaDeReceita;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor(R$)")
    private BigDecimal valor;
    @OneToMany(mappedBy = "receitaLOA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaLOAFonte> receitaLoaFontes;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Unidade Organizacional")
    private UnidadeOrganizacional entidade;
    @OneToMany(mappedBy = "receitaLOA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrevisaoReceitaOrc> previsaoReceitaOrc;
    @Transient
    private Long criadoEm;
    @Etiqueta("Código Reduzido")
    private String codigoReduzido;
    private BigDecimal saldo;
    @Transient
    private Long conjuntoFonte;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Operação da Receita")
    private OperacaoReceita operacaoReceita;

    public ReceitaLOA() {
        dataRegistro = new Date();
        receitaLoaFontes = new ArrayList<ReceitaLOAFonte>();
        valor = new BigDecimal(BigInteger.ZERO);
        previsaoReceitaOrc = new ArrayList<PrevisaoReceitaOrc>();
        saldo = BigDecimal.ZERO;
    }

    public String getCodigoReduzido() {
        return codigoReduzido;
    }

    public void setCodigoReduzido(String codigoReduzido) {
        this.codigoReduzido = codigoReduzido;
    }

    public List<ReceitaLOAFonte> getReceitaLoaFontes() {
        return receitaLoaFontes;
    }

    public void setReceitaLoaFontes(List<ReceitaLOAFonte> receitaLoaFontes) {
        this.receitaLoaFontes = receitaLoaFontes;
    }

    public Conta getContaDeReceita() {
        return contaDeReceita;
    }

    public void setContaDeReceita(Conta contaDeReceita) {
        this.contaDeReceita = contaDeReceita;
    }

    public LOA getLoa() {
        return loa;
    }

    public void setLoa(LOA loa) {
        this.loa = loa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public UnidadeOrganizacional getEntidade() {
        return entidade;
    }

    public void setEntidade(UnidadeOrganizacional entidade) {
        this.entidade = entidade;
    }

    public List<PrevisaoReceitaOrc> getPrevisaoReceitaOrc() {
        return previsaoReceitaOrc;
    }

    public void setPrevisaoReceitaOrc(List<PrevisaoReceitaOrc> previsaoReceitaOrc) {
        this.previsaoReceitaOrc = previsaoReceitaOrc;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getConjuntoFonte() {
        return conjuntoFonte;
    }

    public void setConjuntoFonte(Long conjuntoFonte) {
        this.conjuntoFonte = conjuntoFonte;
    }

    public OperacaoReceita getOperacaoReceita() {
        return operacaoReceita;
    }

    public void setOperacaoReceita(OperacaoReceita operacaoReceita) {
        this.operacaoReceita = operacaoReceita;
    }

    @Override
    public String toString() {
        return "Lei Orçamentária: " + loa + " Conta: " + contaDeReceita + " Valor: " + Util.formataValor(valor);
    }

    public String getComplementoContabil() {
        String retorno = "";
        if (this.contaDeReceita.getCodigo() != null && this.contaDeReceita.getDescricao() != null) {
            return "Conta de Receita: " + this.contaDeReceita.getCodigo() + " " + this.contaDeReceita.getDescricao();
        }
        if (!"".equals(retorno)) {
            return retorno;
        }
        return "";
    }

    public List<Long> getConjuntos() {
        List<Long> retorno = new ArrayList<Long>();
        for (ReceitaLOAFonte receitaLOAFonte : this.getReceitaLoaFontes()) {
            Util.adicionarObjetoEmLista(retorno, receitaLOAFonte.getItem());
        }
        return retorno;
    }

    public List<ReceitaLOAFonte> getReceitaLoaFontePorConjunto(Long conjunto) {
        List<ReceitaLOAFonte> retorno = new ArrayList<ReceitaLOAFonte>();
        for (ReceitaLOAFonte receitaLOAFonte : this.getReceitaLoaFontes()) {
            if (receitaLOAFonte.getItem().equals(conjunto)) {
                retorno.add(receitaLOAFonte);
            }
        }
        return retorno;

    }
}
