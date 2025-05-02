/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.util.ResultadoValidacao;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cheles
 */
public class FiltrosConsultaCredenciaisRBTrans implements Serializable {

    private Integer numeroPermissaoInicial;
    private Integer numeroPermissaoFinal;
    private Integer numeroCmcInicial;
    private Integer numeroCmcFinal;
    private Date dataGeracaoInicial;
    private Date dataGeracaoFinal;
    private TipoPermissaoRBTrans tipoTransporte;
    private TipoRequerenteCredencialRBTrans tipoRequerente;
    private TipoCredencialRBTrans tipoCredencial;
    private TipoPagamentoCredencialRBTrans statusPagamento;
    private TipoValidadeCredencialRBTrans tipoValidade;
    private TipoStatusEmissaoCredencialRBTrans statusEmissao;
    private TipoFiltroCredencialRBTrans tipoFiltro;
    /*
     * Os atributos "nomePermissionario", "placaVeiculo" e
     * "quantidadeRegistros", são utilizados somente durante a pesquisa de
     * permissões de transporte, na página de listagem de permissões de
     * transporte.
     */
    private String nomePermissionario;
    private String placaVeiculo;
    private int quantidadeRegistros;

    public void FiltrosConsultaCredenciaisRBTrans() {
        numeroPermissaoInicial = null;
        numeroPermissaoFinal = null;
        numeroCmcInicial = null;
        numeroCmcFinal = null;
        dataGeracaoInicial = null;
        dataGeracaoFinal = null;
        tipoTransporte = null;
        tipoRequerente = null;
        tipoCredencial = null;
        statusPagamento = null;
        tipoValidade = null;
        statusEmissao = null;
        nomePermissionario = null;
        placaVeiculo = null;

    }

    public String obterNomeClassePermissao() {
        switch (getTipoTransporte()) {
            case TAXI:
                return "Permissão de Táxi";
            case MOTO_TAXI:
                return "Permissão de Moto-Táxi";
            case FRETE:
                return "Permissão de Frete";
            default:
                return null;
        }
    }

    public String obterNomeClasseCredencial() {
        switch (getTipoCredencial()) {
            case TRAFEGO:
                return CredencialTrafego.class.getSimpleName();
            case TRANSPORTE:
                return CredencialTransporte.class.getSimpleName();
            default:
                return null;
        }
    }

    public boolean isTrafego() {
        return getTipoCredencial() == null ? false : getTipoCredencial().equals(TipoCredencialRBTrans.TRAFEGO);
    }

    public boolean isTransporte() {
        return getTipoCredencial() == null ? false : getTipoCredencial().equals(TipoCredencialRBTrans.TRANSPORTE);
    }

    public boolean isFiltroPermissao() {
        return getTipoFiltro() == null ? false : getTipoFiltro().equals(TipoFiltroCredencialRBTrans.PERMISSAO);
    }

    public boolean isFiltroCmc() {
        return getTipoFiltro() == null ? false : getTipoFiltro().equals(TipoFiltroCredencialRBTrans.CMC);
    }

    public boolean isValidadeEmDia() {
        return getTipoValidade() == null ? false : getTipoValidade().equals(TipoValidadeCredencialRBTrans.EM_DIA);
    }

    public boolean isValidadeVencida() {
        return getTipoValidade() == null ? false : getTipoValidade().equals(TipoValidadeCredencialRBTrans.VENCIDA);
    }

    public boolean isValidadeTodas() {
        return getTipoValidade() == null ? false : getTipoValidade().equals(TipoValidadeCredencialRBTrans.TODAS);
    }

    public boolean isStatusEmissaoEmitida() {
        return getStatusEmissao() == null ? false : getStatusEmissao().equals(TipoStatusEmissaoCredencialRBTrans.EMITIDA);
    }

    public boolean isStatusEmissaoNaoEmitida() {
        return getStatusEmissao() == null ? false : getStatusEmissao().equals(TipoStatusEmissaoCredencialRBTrans.NAO_EMITIDA);
    }

    public boolean isStatusEmissaoTodas() {
        return getStatusEmissao() == null ? false : getStatusEmissao().equals(TipoStatusEmissaoCredencialRBTrans.TODAS);
    }

    public ResultadoValidacao validaFiltros() {

        ResultadoValidacao resultado = new ResultadoValidacao();
        String summary = "Impossível continuar!";

        if (tipoFiltro == null) {
            resultado.addErro(null, summary, "O campo Filtro deve ser informado.");
            return resultado;
        }

        if (isFiltroPermissao()) {
            validaFiltroPermissao(resultado, summary);
        } else {
            validaFiltroCmc(resultado, summary);
        }

        if (dataGeracaoInicial == null) {
            resultado.addErro(null, summary, "A Data de Geração Inicial deve ser informada.");
        }

        if (dataGeracaoFinal == null) {
            resultado.addErro(null, summary, "A Data de Geração Final deve ser informada.");
        }

        if (dataGeracaoInicial != null && dataGeracaoFinal != null) {
            if (dataGeracaoInicial.compareTo(dataGeracaoFinal) > 0) {
                resultado.addErro(null, summary, "A Data de Geração Inicial não pode ser posterior a Data de Geração Final.");
            }
        }

        if (tipoTransporte == null) {
            resultado.addErro(null, summary, "O campo Tipo de Transporte deve ser informado.");
        }

        if (tipoRequerente == null) {
            resultado.addErro(null, summary, "O campo Requerente deve ser informado.");
        }

        if (tipoCredencial == null) {
            resultado.addErro(null, summary, "O campo Tipo de Credencial deve ser informado.");
        }

        if (statusPagamento == null) {
            resultado.addErro(null, summary, "O campo Status do Pagamento deve ser informado.");
        }

        if (tipoValidade == null) {
            resultado.addErro(null, summary, "O campo Validade deve ser informado.");
        }

        if (statusEmissao == null) {
            resultado.addErro(null, summary, "O campo Status da Emissão deve ser informado.");
        }

        return resultado;
    }

    private void validaFiltroCmc(ResultadoValidacao resultado, String summary) {
        if (numeroCmcInicial == null || numeroCmcInicial <= 0) {
            resultado.addErro(null, summary, "O campo Número do CMC Inicial não foi informado.");
        }

        if (numeroCmcFinal == null || numeroCmcFinal <= 0) {
            resultado.addErro(null, summary, "O campo Número do CMC Final não foi informado.");
        }

        if (numeroCmcFinal != null && numeroCmcInicial != null) {
            if (numeroCmcInicial.compareTo(numeroCmcFinal) > 0) {
                resultado.addErro(null, summary, "O Número do CMC Inicial não pode ser superior ao Número do CMC Final.");
            }
        }
    }

    private void validaFiltroPermissao(ResultadoValidacao resultado, String summary) {
        validaNumeroPermissaoInicial(resultado, summary);
        validaNumeroPermissaoFinal(resultado, summary);
        validaNumeroPermissaoInicialMaiorQueFinal(resultado, summary);
    }

    private void validaNumeroPermissaoFinal(ResultadoValidacao resultado, String summary) {
        if (numeroPermissaoFinal == null || numeroPermissaoFinal <= 0) {
            resultado.addErro(null, summary, "O campo Número da Permissão Final deve ser informado.");
        }
    }

    private void validaNumeroPermissaoInicial(ResultadoValidacao resultado, String summary) {
        if (numeroPermissaoInicial == null || numeroPermissaoInicial <= 0) {
            resultado.addErro(null, summary, "O campo Número da Permissão Inícial deve ser informado.");
        }
    }

    private void validaNumeroPermissaoInicialMaiorQueFinal(ResultadoValidacao resultado, String summary) {
        if (numeroPermissaoInicial != null && numeroPermissaoFinal != null) {
            if (numeroPermissaoInicial.compareTo(numeroPermissaoFinal) > 0) {
                resultado.addErro(null, summary, "O Número da Permissão Inicial não pode ser superior ao Número da Permissão Final.");
            }
        }
    }

    private void validaNumeroCmcInicialMaiorQueFinal(ResultadoValidacao resultado, String summary) {
        if (numeroCmcInicial != null && numeroCmcFinal != null) {
            if (numeroCmcInicial.compareTo(numeroCmcFinal) > 0) {
                resultado.addErro(null, summary, "O Número do CMC Inicial não pode ser superior ao Número do CMC Final.");
            }
        }
    }

    private boolean isCamposFiltroPesquisaPermissoesVazios() {
        return (nomePermissionario == null || nomePermissionario.isEmpty())
                && (placaVeiculo == null || placaVeiculo.isEmpty())
                && numeroPermissaoInicial == null
                && numeroPermissaoFinal == null
                && numeroCmcInicial == null
                && numeroCmcFinal == null;
    }

    public ResultadoValidacao validarPesquisa() {
        ResultadoValidacao resultado = new ResultadoValidacao();
        String summary = "Impossível continuar!";

        validaNumeroPermissaoInicialMaiorQueFinal(resultado, summary);
        validaNumeroCmcInicialMaiorQueFinal(resultado, summary);

        if ((numeroPermissaoInicial != null && numeroPermissaoFinal == null) || (numeroPermissaoInicial == null && numeroPermissaoFinal != null)) {
            resultado.addErro(null, summary, "Para filtrar pelo número das permissões, informe os campos número inicial e final.");
        }
        if (tipoTransporte == null) {
            resultado.addErro(null, summary, "É necessário informar o tipo de permissão");
        }

        if ((numeroCmcInicial != null && numeroCmcFinal == null) || (numeroCmcInicial == null && numeroCmcFinal != null)) {
            resultado.addErro(null, summary, "Para filtrar pela inscrição cadastral (CMC), informe os campos cmc inicial e final.");
        }

        if (placaVeiculo != null && !placaVeiculo.isEmpty() && placaVeiculo.length() < 7) {
            resultado.addErro(null, summary, "Verifique se a placa do veículo foi informada corretamente.");
        }

        return resultado;
    }

    public boolean temNomePermissionario() {
        return nomePermissionario != null && !nomePermissionario.isEmpty();
    }

    public boolean temPlacaDeCarro() {
        return placaVeiculo != null && !placaVeiculo.isEmpty();
    }

    public boolean temCmc() {
        return numeroCmcInicial != null && numeroCmcInicial != 0 && numeroCmcFinal != null && numeroCmcFinal != 0;
    }

    public boolean temNumeroPermissao() {
        return numeroPermissaoInicial != null && numeroPermissaoInicial != 0 && numeroPermissaoFinal != null && numeroPermissaoFinal != 0;
    }

    public String obterQueryPesquisaPermissoes() {
        String hql = "  select pt "
                + "       from " + obterNomeClassePermissao() + " pt "
                + " inner join pt.cadastroEconomico ce ";

        String where = "where pt.finalVigencia is null";

        if (temPlacaDeCarro()) {
            hql += " , VeiculoPermissionario vp "
                    + "  left join vp.veiculoTransporte vt ";
            where = FiltrosConsultaCredenciaisRBTrans.adicionarClausulaWhere(where, "lower(vt.placa) = :placa");
        }

        if (temNomePermissionario()) {
            hql += "  left join ce.pessoa p ";
            where = FiltrosConsultaCredenciaisRBTrans.adicionarClausulaWhere(where, "lower(p.nome) like :nome");
        }

        if (temNumeroPermissao()) {
            where = FiltrosConsultaCredenciaisRBTrans.adicionarClausulaWhere(where, "(pt.numero >= :numeroInicial and pt.numero <= :numeroFinal)");
        }

        if (temCmc()) {
            where = FiltrosConsultaCredenciaisRBTrans.adicionarClausulaWhere(where, "(ce.inscricaoCadastral >= :cmcInicial and ce.inscricaoCadastral <= :cmcFinal)");
        }
        //System.out.println("where --- " + where);
        return hql + where;
        //isCamposFiltroPesquisaPermissoesVazios() ? hql : hql + where;
    }

    private static String adicionarClausulaWhere(String where, String clausula) {
        if (where.length() > 5) {
            return where + " and " + clausula;
        }

        return where + " " + clausula;
    }

    public Date getDataGeracaoFinal() {
        return dataGeracaoFinal;
    }

    public void setDataGeracaoFinal(Date dataGeracaoFinal) {
        this.dataGeracaoFinal = dataGeracaoFinal;
    }

    public Date getDataGeracaoInicial() {
        return dataGeracaoInicial;
    }

    public void setDataGeracaoInicial(Date dataGeracaoInicial) {
        this.dataGeracaoInicial = dataGeracaoInicial;
    }

    public Integer getNumeroCmcFinal() {
        return numeroCmcFinal;
    }

    public void setNumeroCmcFinal(Integer numeroCmcFinal) {
        this.numeroCmcFinal = numeroCmcFinal;
    }

    public Integer getNumeroCmcInicial() {
        return numeroCmcInicial;
    }

    public void setNumeroCmcInicial(Integer numeroCmcInicial) {
        this.numeroCmcInicial = numeroCmcInicial;
    }

    public Integer getNumeroPermissaoFinal() {
        return numeroPermissaoFinal;
    }

    public void setNumeroPermissaoFinal(Integer numeroPermissaoFinal) {
        this.numeroPermissaoFinal = numeroPermissaoFinal;
    }

    public Integer getNumeroPermissaoInicial() {
        return numeroPermissaoInicial;
    }

    public void setNumeroPermissaoInicial(Integer numeroPermissaoInicial) {
        this.numeroPermissaoInicial = numeroPermissaoInicial;
    }

    public TipoStatusEmissaoCredencialRBTrans getStatusEmissao() {
        return statusEmissao;
    }

    public void setStatusEmissao(TipoStatusEmissaoCredencialRBTrans statusEmissao) {
        this.statusEmissao = statusEmissao;
    }

    public TipoPagamentoCredencialRBTrans getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(TipoPagamentoCredencialRBTrans statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public TipoCredencialRBTrans getTipoCredencial() {
        return tipoCredencial;
    }

    public void setTipoCredencial(TipoCredencialRBTrans tipoCredencial) {
        this.tipoCredencial = tipoCredencial;
    }

    public TipoRequerenteCredencialRBTrans getTipoRequerente() {
        return tipoRequerente;
    }

    public void setTipoRequerente(TipoRequerenteCredencialRBTrans tipoRequerente) {
        this.tipoRequerente = tipoRequerente;
    }

    public TipoPermissaoRBTrans getTipoTransporte() {
        return tipoTransporte;
    }

    public void setTipoTransporte(TipoPermissaoRBTrans tipoTransporte) {
        this.tipoTransporte = tipoTransporte;
    }

    public TipoValidadeCredencialRBTrans getTipoValidade() {
        return tipoValidade;
    }

    public void setTipoValidade(TipoValidadeCredencialRBTrans tipoValidade) {
        this.tipoValidade = tipoValidade;
    }

    public TipoFiltroCredencialRBTrans getTipoFiltro() {
        return tipoFiltro;
    }

    public void setTipoFiltro(TipoFiltroCredencialRBTrans tipoFiltro) {
        this.tipoFiltro = tipoFiltro;
    }

    public String getNomePermissionario() {
        return nomePermissionario;
    }

    public void setNomePermissionario(String nomePermissionario) {
        this.nomePermissionario = nomePermissionario;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public int getQuantidadeRegistros() {
        return quantidadeRegistros;
    }

    public void setQuantidadeRegistros(int quantidadeRegistros) {
        this.quantidadeRegistros = quantidadeRegistros;
    }
}
