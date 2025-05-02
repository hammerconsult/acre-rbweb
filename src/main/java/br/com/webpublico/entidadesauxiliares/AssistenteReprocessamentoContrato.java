package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AlteracaoContratual;
import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class AssistenteReprocessamentoContrato {

    private static final int QUANTIDADE_REGISTRO = 50;
    private Date dataInicial;
    private Date dataFinal;
    private Date dataOperacao;
    private Contrato contrato;
    private Integer quantidadeConsulta;
    private Boolean separaExecucoesCorretas;
    private Boolean execucaoReprocessada;
    private AlteracaoContratual alteracaoContratual;
    private List<ContratoCorrecaoVO> contratosCorrecoes;
    private AssistenteBarraProgresso assistenteBarraProgresso;

    public AssistenteReprocessamentoContrato() {
        contratosCorrecoes = Lists.newArrayList();
        quantidadeConsulta = QUANTIDADE_REGISTRO;
        separaExecucoesCorretas = true;
        execucaoReprocessada = false;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public AlteracaoContratual getAlteracaoContratual() {
        return alteracaoContratual;
    }

    public void setAlteracaoContratual(AlteracaoContratual alteracaoContratual) {
        this.alteracaoContratual = alteracaoContratual;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public List<ContratoCorrecaoVO> getContratosCorrecoes() {
        return contratosCorrecoes;
    }

    public void setContratosCorrecoes(List<ContratoCorrecaoVO> contratosCorrecoes) {
        this.contratosCorrecoes = contratosCorrecoes;
    }

    public Integer getQuantidadeConsulta() {
        if (quantidadeConsulta > QUANTIDADE_REGISTRO) {
            return QUANTIDADE_REGISTRO;
        }
        return quantidadeConsulta;
    }

    public void setQuantidadeConsulta(Integer quantidadeConsulta) {
        this.quantidadeConsulta = quantidadeConsulta;
    }

    public Boolean getSeparaExecucoesCorretas() {
        return separaExecucoesCorretas;
    }

    public void setSeparaExecucoesCorretas(Boolean separaExecucoesCorretas) {
        this.separaExecucoesCorretas = separaExecucoesCorretas;
    }

    public Boolean getExecucaoReprocessada() {
        return execucaoReprocessada;
    }

    public void setExecucaoReprocessada(Boolean execucaoReprocessada) {
        this.execucaoReprocessada = execucaoReprocessada;
    }

    public void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final deve ser informado.");
        }
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data final deve ser superior ou igual a data inicial.");
        }
        ve.lancarException();
    }
}
