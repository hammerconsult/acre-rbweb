package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SubSistema;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

@Entity
@Audited
public class ContaAuxiliarDetalhada extends Conta {
    @ManyToOne
    private Conta contaDeDestinacao;
    @ManyToOne
    private Conta conta;
    @ManyToOne
    private Exercicio exercicioAtual;
    @ManyToOne
    private Exercicio exercicioOrigem;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Enumerated(EnumType.STRING)
    private SubSistema subSistema;
    private Integer dividaConsolidada;
    private Integer es;
    private String classificacaoFuncional;
    private int hashContaAuxiliarDetalhada;
    @ManyToOne
    private ContaContabil contaContabil;
    @ManyToOne
    private TipoContaAuxiliar tipoContaAuxiliar;
    private String codigoCO;

    public ContaAuxiliarDetalhada() {
        super();
        setdType("ContaAuxiliarDetalhada");
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, SubSistema subSistema) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.subSistema = subSistema;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional, Conta contaDeDestinacao, Conta conta, Integer es, Exercicio exercicio, Exercicio exercicioOrigem) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.classificacaoFuncional = classificacaoFuncional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.conta = conta;
        this.es = es;
        this.exercicioAtual = exercicio;
        this.exercicioOrigem = exercicioOrigem;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional, Conta contaDeDestinacao, Conta conta, String codigoCO, Exercicio exercicio, Exercicio exercicioOrigem) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.classificacaoFuncional = classificacaoFuncional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.conta = conta;
        this.codigoCO = codigoCO;
        this.exercicioAtual = exercicio;
        this.exercicioOrigem = exercicioOrigem;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, Conta contaDeDestinacao, Conta conta, Exercicio exercicio) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.conta = conta;
        this.exercicioAtual = exercicio;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, Conta contaDeDestinacao, Conta conta, Exercicio exercicio, String codigoCO) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.conta = conta;
        this.exercicioAtual = exercicio;
        this.codigoCO = codigoCO;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, SubSistema subSistema, Integer dividaConsolidada) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.subSistema = subSistema;
        this.dividaConsolidada = dividaConsolidada;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.classificacaoFuncional = classificacaoFuncional;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional, Conta contaDeDestinacao) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.classificacaoFuncional = classificacaoFuncional;
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional, Conta contaDeDestinacao, Conta conta) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.classificacaoFuncional = classificacaoFuncional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.conta = conta;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional, Conta contaDeDestinacao, String codigoCO) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.classificacaoFuncional = classificacaoFuncional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.codigoCO = codigoCO;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional, Conta contaDeDestinacao, Conta conta, Integer es) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.classificacaoFuncional = classificacaoFuncional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.conta = conta;
        this.es = es;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional, Conta contaDeDestinacao, Conta conta, String codigoCO) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.classificacaoFuncional = classificacaoFuncional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.conta = conta;
        this.codigoCO = codigoCO;
    }

    public ContaAuxiliarDetalhada(UnidadeOrganizacional unidadeOrganizacional, SubSistema financeiroPermanente, Integer dividaConsolidada, Conta contaDeDestinacao) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.subSistema = financeiroPermanente;
        this.dividaConsolidada = dividaConsolidada;
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public ContaAuxiliarDetalhada(Conta contaDeDestinacao, SubSistema financeiroPermanente, Exercicio exercicioAtual, UnidadeOrganizacional unidadeOrganizacional) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.contaDeDestinacao = contaDeDestinacao;
        this.subSistema = financeiroPermanente;
        this.exercicioAtual = exercicioAtual;
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ContaAuxiliarDetalhada(Conta contaDeDestinacao, Exercicio exercicioAtual, UnidadeOrganizacional unidadeOrganizacional) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.contaDeDestinacao = contaDeDestinacao;
        this.exercicioAtual = exercicioAtual;
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ContaAuxiliarDetalhada(Conta contaDeDestinacao, Exercicio exercicioAtual, UnidadeOrganizacional unidadeOrganizacional, String codigoCO) {
        super();
        setdType("ContaAuxiliarDetalhada");
        this.contaDeDestinacao = contaDeDestinacao;
        this.exercicioAtual = exercicioAtual;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.codigoCO = codigoCO;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Exercicio getExercicioAtual() {
        return exercicioAtual;
    }

    public void setExercicioAtual(Exercicio exercicioAtual) {
        this.exercicioAtual = exercicioAtual;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public SubSistema getSubSistema() {
        return subSistema;
    }

    public void setSubSistema(SubSistema subSistema) {
        this.subSistema = subSistema;
    }

    public Integer getDividaConsolidada() {
        return dividaConsolidada;
    }

    public void setDividaConsolidada(Integer dividaConsolidada) {
        this.dividaConsolidada = dividaConsolidada;
    }

    public Integer getEs() {
        return es;
    }

    public void setEs(Integer es) {
        this.es = es;
    }

    public String getClassificacaoFuncional() {
        return classificacaoFuncional;
    }

    public void setClassificacaoFuncional(String classificacaoFuncional) {
        this.classificacaoFuncional = classificacaoFuncional;
    }

    public int getHashContaAuxiliarDetalhada() {
        return hashContaAuxiliarDetalhada;
    }

    public void setHashContaAuxiliarDetalhada(int hashContaAuxiliarDetalhada) {
        this.hashContaAuxiliarDetalhada = hashContaAuxiliarDetalhada;
    }

    public ContaContabil getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(ContaContabil contaContabil) {
        this.contaContabil = contaContabil;
    }

    public TipoContaAuxiliar getTipoContaAuxiliar() {
        return tipoContaAuxiliar;
    }

    public void setTipoContaAuxiliar(TipoContaAuxiliar tipoContaAuxiliar) {
        this.tipoContaAuxiliar = tipoContaAuxiliar;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public Exercicio getExercicioOrigem() {
        return exercicioOrigem;
    }

    public void setExercicioOrigem(Exercicio exercicioOrigem) {
        this.exercicioOrigem = exercicioOrigem;
    }

    public String getCodigoCO() {
        return codigoCO;
    }

    public void setCodigoCO(String codigoCO) {
        this.codigoCO = codigoCO;
    }
}
