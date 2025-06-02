package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Propriedade;
import br.com.webpublico.entidades.PropriedadeRural;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Wellington on 02/12/2015.
 */
public class VoSimulacaoTransferenciaMovimentoPessoa implements Serializable {

    private List<Propriedade> propriedadesCadastrosImobiliarios;
    private List<CadastroEconomico> cadastrosEconomicos;
    private List<PropriedadeRural> propriedadesCadastrosRurais;
    private List<ResultadoParcela> resultadoParcelas;
    private List<TransferenciaMovPessoaContabil> movimentosContabeis;
    private List<DocumentosPessoaisVO> documentosPessoaisVOs;
    private List<EnderecoVO> enderecoVOs;
    private List<TelefoneVO> telefoneVOs;
    private List<InformacaoBancariaVO> informacaoBancariaVOs;
    private List<DependenteVO> dependenteVOs;
    private List<DependenteVO> responsaveisDependenteVOs;
    private List<PensaoAlimenticiaVO> pensaoAlimenticiaVOs;
    private List<PensaoAlimenticiaVO> responsavelPensaoAlimenticiaVOs;
    private List<CadastroRHVO> matriculasVOs;
    private List<CadastroRHVO> prestadoresVOs;

    public VoSimulacaoTransferenciaMovimentoPessoa() {
        propriedadesCadastrosImobiliarios = Lists.newArrayList();
        cadastrosEconomicos = Lists.newArrayList();
        propriedadesCadastrosRurais = Lists.newArrayList();
        movimentosContabeis = Lists.newArrayList();
        documentosPessoaisVOs = Lists.newArrayList();
        enderecoVOs = Lists.newArrayList();
        telefoneVOs = Lists.newArrayList();
        informacaoBancariaVOs = Lists.newArrayList();
        dependenteVOs = Lists.newArrayList();
        pensaoAlimenticiaVOs = Lists.newArrayList();
        responsavelPensaoAlimenticiaVOs = Lists.newArrayList();
        responsaveisDependenteVOs = Lists.newArrayList();
        matriculasVOs = Lists.newArrayList();
        prestadoresVOs = Lists.newArrayList();
    }

    public List<PropriedadeRural> getPropriedadesCadastrosRurais() {
        return propriedadesCadastrosRurais;
    }

    public void setPropriedadesCadastrosRurais(List<PropriedadeRural> propriedadesCadastrosRurais) {
        this.propriedadesCadastrosRurais = propriedadesCadastrosRurais;
    }

    public List<Propriedade> getPropriedadesCadastrosImobiliarios() {
        return propriedadesCadastrosImobiliarios;
    }

    public void setPropriedadesCadastrosImobiliarios(List<Propriedade> propriedadesCadastrosImobiliarios) {
        this.propriedadesCadastrosImobiliarios = propriedadesCadastrosImobiliarios;
    }

    public List<CadastroEconomico> getCadastrosEconomicos() {
        return cadastrosEconomicos;
    }

    public void setCadastrosEconomicos(List<CadastroEconomico> cadastrosEconomicos) {
        this.cadastrosEconomicos = cadastrosEconomicos;
    }

    public List<ResultadoParcela> getResultadoParcelas() {
        return resultadoParcelas;
    }

    public void setResultadoParcelas(List<ResultadoParcela> resultadoParcelas) {
        this.resultadoParcelas = resultadoParcelas;
    }

    public List<TransferenciaMovPessoaContabil> getMovimentosContabeis() {
        return movimentosContabeis;
    }

    public void setMovimentosContabeis(List<TransferenciaMovPessoaContabil> movimentosContabeis) {
        this.movimentosContabeis = movimentosContabeis;
    }

    public List<DocumentosPessoaisVO> getDocumentosPessoaisVOs() {
        return documentosPessoaisVOs;
    }

    public void setDocumentosPessoaisVOs(List<DocumentosPessoaisVO> documentosPessoaisVOs) {
        this.documentosPessoaisVOs = documentosPessoaisVOs;
    }

    public List<EnderecoVO> getEnderecoVOs() {
        return enderecoVOs;
    }

    public void setEnderecoVOs(List<EnderecoVO> enderecoVOs) {
        this.enderecoVOs = enderecoVOs;
    }

    public List<TelefoneVO> getTelefoneVOs() {
        return telefoneVOs;
    }

    public void setTelefoneVOs(List<TelefoneVO> telefoneVOs) {
        this.telefoneVOs = telefoneVOs;
    }

    public List<InformacaoBancariaVO> getInformacaoBancariaVOs() {
        return informacaoBancariaVOs;
    }

    public void setInformacaoBancariaVOs(List<InformacaoBancariaVO> informacaoBancariaVOs) {
        this.informacaoBancariaVOs = informacaoBancariaVOs;
    }

    public List<DependenteVO> getDependenteVOs() {
        return dependenteVOs;
    }

    public void setDependenteVOs(List<DependenteVO> dependenteVOs) {
        this.dependenteVOs = dependenteVOs;
    }

    public List<PensaoAlimenticiaVO> getPensaoAlimenticiaVOs() {
        return pensaoAlimenticiaVOs;
    }

    public void setPensaoAlimenticiaVOs(List<PensaoAlimenticiaVO> pensaoAlimenticiaVOs) {
        this.pensaoAlimenticiaVOs = pensaoAlimenticiaVOs;
    }

    public List<PensaoAlimenticiaVO> getResponsavelPensaoAlimenticiaVOs() {
        return responsavelPensaoAlimenticiaVOs;
    }

    public void setResponsavelPensaoAlimenticiaVOs(List<PensaoAlimenticiaVO> responsavelPensaoAlimenticiaVOs) {
        this.responsavelPensaoAlimenticiaVOs = responsavelPensaoAlimenticiaVOs;
    }

    public List<DependenteVO> getResponsaveisDependenteVOs() {
        return responsaveisDependenteVOs;
    }

    public void setResponsaveisDependenteVOs(List<DependenteVO> responsaveisDependenteVOs) {
        this.responsaveisDependenteVOs = responsaveisDependenteVOs;
    }

    public List<CadastroRHVO> getMatriculasVOs() {
        return matriculasVOs;
    }

    public void setMatriculasVOs(List<CadastroRHVO> matriculasVOs) {
        this.matriculasVOs = matriculasVOs;
    }

    public List<CadastroRHVO> getPrestadoresVOs() {
        return prestadoresVOs;
    }

    public void setPrestadoresVOs(List<CadastroRHVO> prestadoresVOs) {
        this.prestadoresVOs = prestadoresVOs;
    }
}
