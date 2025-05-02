package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoDespesaORC;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class FormulaItemDemonstrativoVO {
    private OperacaoFormula operacaoFormula;
    private List<ComponenteFormulaItem> componentesItemDemonstrativo;
    private List<ComponenteFormulaConta> componentesConta;
    private List<ComponenteFormulaPrograma> componentesPrograma;
    private List<ComponenteFormulaAcao> componentesAcao;
    private List<ComponenteFormulaSubacao> componentesSubacao;
    private List<ComponenteFormulaFuncao> componentesFuncao;
    private List<ComponenteFormulaSubFuncao> componentesSubFuncao;
    private List<ComponenteFormulaUnidadeOrganizacional> componentesUnidadeOrganizacional;
    private List<ComponenteFormulaFonteRecurso> componentesFonteRecurso;
    private List<ComponenteFormulaTipoDesp> componentesTipoDespesa;
    private List<ComponenteFormulaSubConta> componentesSubConta;
    private List<ComponenteFormulaValorPadrao> componentesValorPadrao;
    //Valores informados na tela para criar novo componente
    private ItemDemonstrativo itemDemonst;
    private ProgramaPPA programaPPA;
    private AcaoPPA acaoPPA;
    private SubAcaoPPA subAcaoPPA;
    private Funcao funcao;
    private SubFuncao subFuncao;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private FonteDeRecursos fonteDeRecursos;
    private String codigoInicial;
    private Conta conta;
    private TipoDespesaORC tipoDespesaORC;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private SubConta subConta;
    private BigDecimal valorPadrao;
    private Mes mes;
    private PlanoDeContas planoDeContas;

    public FormulaItemDemonstrativoVO() {
        componentesItemDemonstrativo = Lists.newArrayList();
        componentesConta = Lists.newArrayList();
        componentesPrograma = Lists.newArrayList();
        componentesAcao = Lists.newArrayList();
        componentesSubacao = Lists.newArrayList();
        componentesFuncao = Lists.newArrayList();
        componentesSubFuncao = Lists.newArrayList();
        componentesUnidadeOrganizacional = Lists.newArrayList();
        componentesFonteRecurso = Lists.newArrayList();
        componentesTipoDespesa = Lists.newArrayList();
        componentesSubConta = Lists.newArrayList();
        componentesValorPadrao = Lists.newArrayList();
    }

    public OperacaoFormula getOperacaoFormula() {
        return operacaoFormula;
    }

    public void setOperacaoFormula(OperacaoFormula operacaoFormula) {
        this.operacaoFormula = operacaoFormula;
    }

    public ItemDemonstrativo getItemDemonst() {
        return itemDemonst;
    }

    public void setItemDemonst(ItemDemonstrativo itemDemonst) {
        this.itemDemonst = itemDemonst;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public TipoDespesaORC getTipoDespesaORC() {
        return tipoDespesaORC;
    }

    public void setTipoDespesaORC(TipoDespesaORC tipoDespesaORC) {
        this.tipoDespesaORC = tipoDespesaORC;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public List<ComponenteFormulaItem> getComponentesItemDemonstrativo() {
        return componentesItemDemonstrativo;
    }

    public void setComponentesItemDemonstrativo(List<ComponenteFormulaItem> componentesItemDemonstrativo) {
        this.componentesItemDemonstrativo = componentesItemDemonstrativo;
    }

    public List<ComponenteFormulaConta> getComponentesConta() {
        return componentesConta;
    }

    public void setComponentesConta(List<ComponenteFormulaConta> componentesConta) {
        this.componentesConta = componentesConta;
    }

    public List<ComponenteFormulaPrograma> getComponentesPrograma() {
        return componentesPrograma;
    }

    public void setComponentesPrograma(List<ComponenteFormulaPrograma> componentesPrograma) {
        this.componentesPrograma = componentesPrograma;
    }

    public List<ComponenteFormulaAcao> getComponentesAcao() {
        return componentesAcao;
    }

    public void setComponentesAcao(List<ComponenteFormulaAcao> componentesAcao) {
        this.componentesAcao = componentesAcao;
    }

    public List<ComponenteFormulaSubacao> getComponentesSubacao() {
        return componentesSubacao;
    }

    public void setComponentesSubacao(List<ComponenteFormulaSubacao> componentesSubacao) {
        this.componentesSubacao = componentesSubacao;
    }

    public List<ComponenteFormulaFuncao> getComponentesFuncao() {
        return componentesFuncao;
    }

    public void setComponentesFuncao(List<ComponenteFormulaFuncao> componentesFuncao) {
        this.componentesFuncao = componentesFuncao;
    }

    public List<ComponenteFormulaSubFuncao> getComponentesSubFuncao() {
        return componentesSubFuncao;
    }

    public void setComponentesSubFuncao(List<ComponenteFormulaSubFuncao> componentesSubFuncao) {
        this.componentesSubFuncao = componentesSubFuncao;
    }

    public List<ComponenteFormulaUnidadeOrganizacional> getComponentesUnidadeOrganizacional() {
        return componentesUnidadeOrganizacional;
    }

    public void setComponentesUnidadeOrganizacional(List<ComponenteFormulaUnidadeOrganizacional> componentesUnidadeOrganizacional) {
        this.componentesUnidadeOrganizacional = componentesUnidadeOrganizacional;
    }

    public List<ComponenteFormulaFonteRecurso> getComponentesFonteRecurso() {
        return componentesFonteRecurso;
    }

    public void setComponentesFonteRecurso(List<ComponenteFormulaFonteRecurso> componentesFonteRecurso) {
        this.componentesFonteRecurso = componentesFonteRecurso;
    }

    public List<ComponenteFormulaTipoDesp> getComponentesTipoDespesa() {
        return componentesTipoDespesa;
    }

    public void setComponentesTipoDespesa(List<ComponenteFormulaTipoDesp> componentesTipoDespesa) {
        this.componentesTipoDespesa = componentesTipoDespesa;
    }

    public List<ComponenteFormulaSubConta> getComponentesSubConta() {
        return componentesSubConta;
    }

    public void setComponentesSubConta(List<ComponenteFormulaSubConta> componentesSubConta) {
        this.componentesSubConta = componentesSubConta;
    }

    public PlanoDeContas getPlanoDeContas() {
        return planoDeContas;
    }

    public void setPlanoDeContas(PlanoDeContas planoDeContas) {
        this.planoDeContas = planoDeContas;
    }

    public List<ComponenteFormulaValorPadrao> getComponentesValorPadrao() {
        return componentesValorPadrao;
    }

    public void setComponentesValorPadrao(List<ComponenteFormulaValorPadrao> componentesValorPadrao) {
        this.componentesValorPadrao = componentesValorPadrao;
    }

    public BigDecimal getValorPadrao() {
        return valorPadrao;
    }

    public void setValorPadrao(BigDecimal valorPadrao) {
        this.valorPadrao = valorPadrao;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }
}
