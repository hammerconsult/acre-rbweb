package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.ProgramacaoCobranca;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by tharlyson on 30/01/20.
 */
public class AssistenteProgramacaoCobranca {

    private ProgramacaoCobranca selecionado;
    private List<ResultadoParcela> resultadosParcela;
    private List<ProgramacaoCobrancaVO> cobrancaVOS;
    private List<ProgramacaoCobrancaVO> cobrancaAuxiliaresVOS;
    private List<Pessoa> pessoas;
    private List<Pessoa> pessoasAuxiliares;
    private boolean finalizarProcesso;
    private Date dataOperacao;

    public AssistenteProgramacaoCobranca(ProgramacaoCobranca selecionado) {
        this.selecionado = selecionado;
        this.resultadosParcela = Lists.newArrayList();
        this.cobrancaVOS = Lists.newArrayList();
        this.cobrancaAuxiliaresVOS = Lists.newArrayList();
        this.pessoas = Lists.newArrayList();
        this.pessoasAuxiliares = Lists.newArrayList();
    }

    public ProgramacaoCobranca getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ProgramacaoCobranca selecionado) {
        this.selecionado = selecionado;
    }

    public List<ResultadoParcela> getResultadosParcela() {
        return resultadosParcela;
    }

    public void setResultadosParcela(List<ResultadoParcela> resultadosParcela) {
        this.resultadosParcela = resultadosParcela;
    }

    public List<ProgramacaoCobrancaVO> getCobrancaVOS() {
        return cobrancaVOS;
    }

    public void setCobrancaVOS(List<ProgramacaoCobrancaVO> cobrancaVOS) {
        this.cobrancaVOS = cobrancaVOS;
    }

    public List<ProgramacaoCobrancaVO> getCobrancaAuxiliaresVOS() {
        return cobrancaAuxiliaresVOS;
    }

    public void setCobrancaAuxiliaresVOS(List<ProgramacaoCobrancaVO> cobrancaAuxiliaresVOS) {
        this.cobrancaAuxiliaresVOS = cobrancaAuxiliaresVOS;
    }

    public boolean isFinalizarProcesso() {
        return finalizarProcesso;
    }

    public void setFinalizarProcesso(boolean finalizarProcesso) {
        this.finalizarProcesso = finalizarProcesso;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }

    public List<Pessoa> getPessoasAuxiliares() {
        return pessoasAuxiliares;
    }

    public void setPessoasAuxiliares(List<Pessoa> pessoasAuxiliares) {
        this.pessoasAuxiliares = pessoasAuxiliares;
    }
}
