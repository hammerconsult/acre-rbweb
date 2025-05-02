/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author juggernaut
 */
@Entity
public class VoReceitaRealizadaAteBimestreReceita implements Serializable {
    @Id
    private BigDecimal id;
    private BigDecimal impostos;
    private BigDecimal taxas;
    private BigDecimal contribuicaoMelhoria;
    private BigDecimal deducaoReceitaTributaria;
    private BigDecimal contribuicoesSociais;
    private BigDecimal contribuicaoIntervencaoDominioEconomico;
    private BigDecimal contribuicaoIluminacaoPublica;
    private BigDecimal deducoesReceitaContribuicoes;
    private BigDecimal receitasImobiliarias;
    private BigDecimal receitasValoresMobiliarios;
    private BigDecimal receitasConcessoesPermissoes;
    private BigDecimal compensacoesFinanceiras;
    private BigDecimal outrasReceitasPatrimoniais;
    private BigDecimal deducoesReceitaPatrimonial;
    private BigDecimal receitaProducaoVegetal;
    private BigDecimal receitaProducaoAnimalDerivados;
    private BigDecimal outrasReceitasAgropecuarias;
    private BigDecimal deducoesReceitaAgropecuaria;
    private BigDecimal receitaIndustriaExtrativaMineral;
    private BigDecimal receitaIndustriaTransformacao;
    private BigDecimal receitaIndustriaConstrucao;
    private BigDecimal outrasReceitasIndustriais;
    private BigDecimal deducoesReceitaIndustrial;
    private BigDecimal receitaServicos;
    private BigDecimal deducoesReceitaServicos;
    private BigDecimal transferenciasIntergovernamentais;
    private BigDecimal transferenciasInstituicoesPrivadas;
    private BigDecimal transferenciasExterior;
    private BigDecimal transferenciasPessoas;
    private BigDecimal transferenciasConvenios;
    private BigDecimal transferenciasCombateFome;
    private BigDecimal multaJurosMora;
    private BigDecimal indenizacaoRestituicoes;
    private BigDecimal receitaDividaAtiva;
    private BigDecimal receitasCorrentesDiversas;
    private BigDecimal deducoesOutrasReceitasCorrentes;
    private BigDecimal operacoesCreditoInternas;
    private BigDecimal operacoesCreditoExternas;
    private BigDecimal alienacaoBensMoveis;
    private BigDecimal alienacaoBensImoveis;
    private BigDecimal amortizacoesEmprestimos;
    private BigDecimal transferenciasIntergovernamentaisCapital;
    private BigDecimal transferenciasInstituicoesPrivadasCapital;
    private BigDecimal transferenciasExteriorCapital;
    private BigDecimal transferenciasPessoasCapital;
    private BigDecimal transferenciasOutrasInstituicoesPublicasCapital;
    private BigDecimal transferenciasConveniosCapital;
    private BigDecimal transferenciaCombateFomeCapital;
    private BigDecimal integralizacaoCapitalSolcial;
    private BigDecimal divAtivProvAmortizEmpFinanc;
    private BigDecimal receitasCapitalDiversas;
    private BigDecimal receitasIntraOrcamentarias;
    private BigDecimal mobiliariaCreditoInternas;
    private BigDecimal contratualCreditoInternas;
    private BigDecimal mobiliariaCreditoExternas;
    private BigDecimal contratualCreditoExternas;

    public BigDecimal getAlienacaoBensImoveis() {
        return alienacaoBensImoveis;
    }

    public void setAlienacaoBensImoveis(BigDecimal alienacaoBensImoveis) {
        this.alienacaoBensImoveis = alienacaoBensImoveis;
    }

    public BigDecimal getAlienacaoBensMoveis() {
        return alienacaoBensMoveis;
    }

    public void setAlienacaoBensMoveis(BigDecimal alienacaoBensMoveis) {
        this.alienacaoBensMoveis = alienacaoBensMoveis;
    }

    public BigDecimal getCompensacoesFinanceiras() {
        return compensacoesFinanceiras;
    }

    public void setCompensacoesFinanceiras(BigDecimal compensacoesFinanceiras) {
        this.compensacoesFinanceiras = compensacoesFinanceiras;
    }

    public BigDecimal getContratualCreditoExternas() {
        return contratualCreditoExternas;
    }

    public void setContratualCreditoExternas(BigDecimal contratualCreditoExternas) {
        this.contratualCreditoExternas = contratualCreditoExternas;
    }

    public BigDecimal getContratualCreditoInternas() {
        return contratualCreditoInternas;
    }

    public void setContratualCreditoInternas(BigDecimal contratualCreditoInternas) {
        this.contratualCreditoInternas = contratualCreditoInternas;
    }

    public BigDecimal getContribuicaoIluminacaoPublica() {
        return contribuicaoIluminacaoPublica;
    }

    public void setContribuicaoIluminacaoPublica(BigDecimal contribuicaoIluminacaoPublica) {
        this.contribuicaoIluminacaoPublica = contribuicaoIluminacaoPublica;
    }

    public BigDecimal getContribuicaoIntervencaoDominioEconomico() {
        return contribuicaoIntervencaoDominioEconomico;
    }

    public void setContribuicaoIntervencaoDominioEconomico(BigDecimal contribuicaoIntervencaoDominioEconomico) {
        this.contribuicaoIntervencaoDominioEconomico = contribuicaoIntervencaoDominioEconomico;
    }

    public BigDecimal getContribuicaoMelhoria() {
        return contribuicaoMelhoria;
    }

    public void setContribuicaoMelhoria(BigDecimal contribuicaoMelhoria) {
        this.contribuicaoMelhoria = contribuicaoMelhoria;
    }

    public BigDecimal getContribuicoesSociais() {
        return contribuicoesSociais;
    }

    public void setContribuicoesSociais(BigDecimal contribuicoesSociais) {
        this.contribuicoesSociais = contribuicoesSociais;
    }

    public BigDecimal getDeducaoReceitaTributaria() {
        return deducaoReceitaTributaria;
    }

    public void setDeducaoReceitaTributaria(BigDecimal deducaoReceitaTributaria) {
        this.deducaoReceitaTributaria = deducaoReceitaTributaria;
    }

    public BigDecimal getDeducoesOutrasReceitasCorrentes() {
        return deducoesOutrasReceitasCorrentes;
    }

    public void setDeducoesOutrasReceitasCorrentes(BigDecimal deducoesOutrasReceitasCorrentes) {
        this.deducoesOutrasReceitasCorrentes = deducoesOutrasReceitasCorrentes;
    }

    public BigDecimal getDeducoesReceitaAgropecuaria() {
        return deducoesReceitaAgropecuaria;
    }

    public void setDeducoesReceitaAgropecuaria(BigDecimal deducoesReceitaAgropecuaria) {
        this.deducoesReceitaAgropecuaria = deducoesReceitaAgropecuaria;
    }

    public BigDecimal getDeducoesReceitaIndustrial() {
        return deducoesReceitaIndustrial;
    }

    public void setDeducoesReceitaIndustrial(BigDecimal deducoesReceitaIndustrial) {
        this.deducoesReceitaIndustrial = deducoesReceitaIndustrial;
    }

    public BigDecimal getDeducoesReceitaServicos() {
        return deducoesReceitaServicos;
    }

    public void setDeducoesReceitaServicos(BigDecimal deducoesReceitaServicos) {
        this.deducoesReceitaServicos = deducoesReceitaServicos;
    }

    public BigDecimal getDivAtivProvAmortizEmpFinanc() {
        return divAtivProvAmortizEmpFinanc;
    }

    public void setDivAtivProvAmortizEmpFinanc(BigDecimal divAtivProvAmortizEmpFinanc) {
        this.divAtivProvAmortizEmpFinanc = divAtivProvAmortizEmpFinanc;
    }

    public BigDecimal getImpostos() {
        return impostos;
    }

    public void setImpostos(BigDecimal impostos) {
        this.impostos = impostos;
    }

    public BigDecimal getIndenizacaoRestituicoes() {
        return indenizacaoRestituicoes;
    }

    public void setIndenizacaoRestituicoes(BigDecimal indenizacaoRestituicoes) {
        this.indenizacaoRestituicoes = indenizacaoRestituicoes;
    }

    public BigDecimal getIntegralizacaoCapitalSolcial() {
        return integralizacaoCapitalSolcial;
    }

    public void setIntegralizacaoCapitalSolcial(BigDecimal integralizacaoCapitalSolcial) {
        this.integralizacaoCapitalSolcial = integralizacaoCapitalSolcial;
    }

    public BigDecimal getMobiliariaCreditoExternas() {
        return mobiliariaCreditoExternas;
    }

    public void setMobiliariaCreditoExternas(BigDecimal mobiliariaCreditoExternas) {
        this.mobiliariaCreditoExternas = mobiliariaCreditoExternas;
    }

    public BigDecimal getMobiliariaCreditoInternas() {
        return mobiliariaCreditoInternas;
    }

    public void setMobiliariaCreditoInternas(BigDecimal mobiliariaCreditoInternas) {
        this.mobiliariaCreditoInternas = mobiliariaCreditoInternas;
    }

    public BigDecimal getMultaJurosMora() {
        return multaJurosMora;
    }

    public void setMultaJurosMora(BigDecimal multaJurosMora) {
        this.multaJurosMora = multaJurosMora;
    }

    public BigDecimal getOperacoesCreditoExternas() {
        return operacoesCreditoExternas;
    }

    public void setOperacoesCreditoExternas(BigDecimal operacoesCreditoExternas) {
        this.operacoesCreditoExternas = operacoesCreditoExternas;
    }

    public BigDecimal getOperacoesCreditoInternas() {
        return operacoesCreditoInternas;
    }

    public void setOperacoesCreditoInternas(BigDecimal operacoesCreditoInternas) {
        this.operacoesCreditoInternas = operacoesCreditoInternas;
    }

    public BigDecimal getOutrasReceitasAgropecuarias() {
        return outrasReceitasAgropecuarias;
    }

    public void setOutrasReceitasAgropecuarias(BigDecimal outrasReceitasAgropecuarias) {
        this.outrasReceitasAgropecuarias = outrasReceitasAgropecuarias;
    }

    public BigDecimal getOutrasReceitasIndustriais() {
        return outrasReceitasIndustriais;
    }

    public void setOutrasReceitasIndustriais(BigDecimal outrasReceitasIndustriais) {
        this.outrasReceitasIndustriais = outrasReceitasIndustriais;
    }

    public BigDecimal getOutrasReceitasPatrimoniais() {
        return outrasReceitasPatrimoniais;
    }

    public void setOutrasReceitasPatrimoniais(BigDecimal outrasReceitasPatrimoniais) {
        this.outrasReceitasPatrimoniais = outrasReceitasPatrimoniais;
    }

    public BigDecimal getReceitaDividaAtiva() {
        return receitaDividaAtiva;
    }

    public void setReceitaDividaAtiva(BigDecimal receitaDividaAtiva) {
        this.receitaDividaAtiva = receitaDividaAtiva;
    }

    public BigDecimal getReceitaIndustriaConstrucao() {
        return receitaIndustriaConstrucao;
    }

    public void setReceitaIndustriaConstrucao(BigDecimal receitaIndustriaConstrucao) {
        this.receitaIndustriaConstrucao = receitaIndustriaConstrucao;
    }

    public BigDecimal getReceitaIndustriaTransformacao() {
        return receitaIndustriaTransformacao;
    }

    public void setReceitaIndustriaTransformacao(BigDecimal receitaIndustriaTransformacao) {
        this.receitaIndustriaTransformacao = receitaIndustriaTransformacao;
    }

    public BigDecimal getReceitaProducaoAnimalDerivados() {
        return receitaProducaoAnimalDerivados;
    }

    public void setReceitaProducaoAnimalDerivados(BigDecimal receitaProducaoAnimalDerivados) {
        this.receitaProducaoAnimalDerivados = receitaProducaoAnimalDerivados;
    }

    public BigDecimal getReceitaProducaoVegetal() {
        return receitaProducaoVegetal;
    }

    public void setReceitaProducaoVegetal(BigDecimal receitaProducaoVegetal) {
        this.receitaProducaoVegetal = receitaProducaoVegetal;
    }

    public BigDecimal getReceitasCapitalDiversas() {
        return receitasCapitalDiversas;
    }

    public void setReceitasCapitalDiversas(BigDecimal receitasCapitalDiversas) {
        this.receitasCapitalDiversas = receitasCapitalDiversas;
    }

    public BigDecimal getReceitasConcessoesPermissoes() {
        return receitasConcessoesPermissoes;
    }

    public void setReceitasConcessoesPermissoes(BigDecimal receitasConcessoesPermissoes) {
        this.receitasConcessoesPermissoes = receitasConcessoesPermissoes;
    }

    public BigDecimal getReceitasCorrentesDiversas() {
        return receitasCorrentesDiversas;
    }

    public void setReceitasCorrentesDiversas(BigDecimal receitasCorrentesDiversas) {
        this.receitasCorrentesDiversas = receitasCorrentesDiversas;
    }

    public BigDecimal getReceitasImobiliarias() {
        return receitasImobiliarias;
    }

    public void setReceitasImobiliarias(BigDecimal receitasImobiliarias) {
        this.receitasImobiliarias = receitasImobiliarias;
    }

    public BigDecimal getReceitasIntraOrcamentarias() {
        return receitasIntraOrcamentarias;
    }

    public void setReceitasIntraOrcamentarias(BigDecimal receitasIntraOrcamentarias) {
        this.receitasIntraOrcamentarias = receitasIntraOrcamentarias;
    }

    public BigDecimal getReceitasValoresMobiliarios() {
        return receitasValoresMobiliarios;
    }

    public void setReceitasValoresMobiliarios(BigDecimal receitasValoresMobiliarios) {
        this.receitasValoresMobiliarios = receitasValoresMobiliarios;
    }

    public BigDecimal getTaxas() {
        return taxas;
    }

    public void setTaxas(BigDecimal taxas) {
        this.taxas = taxas;
    }

    public BigDecimal getTransferenciaCombateFomeCapital() {
        return transferenciaCombateFomeCapital;
    }

    public void setTransferenciaCombateFomeCapital(BigDecimal transferenciaCombateFomeCapital) {
        this.transferenciaCombateFomeCapital = transferenciaCombateFomeCapital;
    }

    public BigDecimal getTransferenciasCombateFome() {
        return transferenciasCombateFome;
    }

    public void setTransferenciasCombateFome(BigDecimal transferenciasCombateFome) {
        this.transferenciasCombateFome = transferenciasCombateFome;
    }

    public BigDecimal getTransferenciasConvenios() {
        return transferenciasConvenios;
    }

    public void setTransferenciasConvenios(BigDecimal transferenciasConvenios) {
        this.transferenciasConvenios = transferenciasConvenios;
    }

    public BigDecimal getTransferenciasConveniosCapital() {
        return transferenciasConveniosCapital;
    }

    public void setTransferenciasConveniosCapital(BigDecimal transferenciasConveniosCapital) {
        this.transferenciasConveniosCapital = transferenciasConveniosCapital;
    }

    public BigDecimal getTransferenciasExterior() {
        return transferenciasExterior;
    }

    public void setTransferenciasExterior(BigDecimal transferenciasExterior) {
        this.transferenciasExterior = transferenciasExterior;
    }

    public BigDecimal getTransferenciasExteriorCapital() {
        return transferenciasExteriorCapital;
    }

    public void setTransferenciasExteriorCapital(BigDecimal transferenciasExteriorCapital) {
        this.transferenciasExteriorCapital = transferenciasExteriorCapital;
    }

    public BigDecimal getTransferenciasInstituicoesPrivadas() {
        return transferenciasInstituicoesPrivadas;
    }

    public void setTransferenciasInstituicoesPrivadas(BigDecimal transferenciasInstituicoesPrivadas) {
        this.transferenciasInstituicoesPrivadas = transferenciasInstituicoesPrivadas;
    }

    public BigDecimal getTransferenciasInstituicoesPrivadasCapital() {
        return transferenciasInstituicoesPrivadasCapital;
    }

    public void setTransferenciasInstituicoesPrivadasCapital(BigDecimal transferenciasInstituicoesPrivadasCapital) {
        this.transferenciasInstituicoesPrivadasCapital = transferenciasInstituicoesPrivadasCapital;
    }

    public BigDecimal getTransferenciasIntergovernamentais() {
        return transferenciasIntergovernamentais;
    }

    public void setTransferenciasIntergovernamentais(BigDecimal transferenciasIntergovernamentais) {
        this.transferenciasIntergovernamentais = transferenciasIntergovernamentais;
    }

    public BigDecimal getTransferenciasIntergovernamentaisCapital() {
        return transferenciasIntergovernamentaisCapital;
    }

    public void setTransferenciasIntergovernamentaisCapital(BigDecimal transferenciasIntergovernamentaisCapital) {
        this.transferenciasIntergovernamentaisCapital = transferenciasIntergovernamentaisCapital;
    }

    public BigDecimal getTransferenciasOutrasInstituicoesPublicasCapital() {
        return transferenciasOutrasInstituicoesPublicasCapital;
    }

    public void setTransferenciasOutrasInstituicoesPublicasCapital(BigDecimal transferenciasOutrasInstituicoesPublicasCapital) {
        this.transferenciasOutrasInstituicoesPublicasCapital = transferenciasOutrasInstituicoesPublicasCapital;
    }

    public BigDecimal getTransferenciasPessoas() {
        return transferenciasPessoas;
    }

    public void setTransferenciasPessoas(BigDecimal transferenciasPessoas) {
        this.transferenciasPessoas = transferenciasPessoas;
    }

    public BigDecimal getTransferenciasPessoasCapital() {
        return transferenciasPessoasCapital;
    }

    public void setTransferenciasPessoasCapital(BigDecimal transferenciasPessoasCapital) {
        this.transferenciasPessoasCapital = transferenciasPessoasCapital;
    }

    public BigDecimal getAmortizacoesEmprestimos() {
        return amortizacoesEmprestimos;
    }

    public void setAmortizacoesEmprestimos(BigDecimal amortizacoesEmprestimos) {
        this.amortizacoesEmprestimos = amortizacoesEmprestimos;
    }

    public BigDecimal getDeducoesReceitaContribuicoes() {
        return deducoesReceitaContribuicoes;
    }

    public void setDeducoesReceitaContribuicoes(BigDecimal deducoesReceitaContribuicoes) {
        this.deducoesReceitaContribuicoes = deducoesReceitaContribuicoes;
    }

    public BigDecimal getDeducoesReceitaPatrimonial() {
        return deducoesReceitaPatrimonial;
    }

    public void setDeducoesReceitaPatrimonial(BigDecimal deducoesReceitaPatrimonial) {
        this.deducoesReceitaPatrimonial = deducoesReceitaPatrimonial;
    }

    public BigDecimal getReceitaIndustriaExtrativaMineral() {
        return receitaIndustriaExtrativaMineral;
    }

    public void setReceitaIndustriaExtrativaMineral(BigDecimal receitaIndustriaExtrativaMineral) {
        this.receitaIndustriaExtrativaMineral = receitaIndustriaExtrativaMineral;
    }

    public BigDecimal getReceitaServicos() {
        return receitaServicos;
    }

    public void setReceitaServicos(BigDecimal receitaServicos) {
        this.receitaServicos = receitaServicos;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VoReceitaRealizadaAteBimestreReceita other = (VoReceitaRealizadaAteBimestreReceita) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.id);
        return hash;
    }

}
