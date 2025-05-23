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

    public VoSimulacaoTransferenciaMovimentoPessoa() {
        propriedadesCadastrosImobiliarios = Lists.newArrayList();
        cadastrosEconomicos = Lists.newArrayList();
        propriedadesCadastrosRurais = Lists.newArrayList();
        movimentosContabeis = Lists.newArrayList();
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
}
