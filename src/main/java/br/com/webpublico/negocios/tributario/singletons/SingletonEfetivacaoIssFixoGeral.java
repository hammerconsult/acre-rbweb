package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.entidades.CalculoISS;
import br.com.webpublico.entidades.EfetivacaoProcessoIssFixoGeral;
import br.com.webpublico.entidades.OcorrenciaEfetivacaoIssFixoGeral;
import br.com.webpublico.util.AssistenteBarraProgresso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 02/08/13
 * Time: 08:36
 * To change this template use File | Settings | File Templates.
 */
public class SingletonEfetivacaoIssFixoGeral {
    private static SingletonEfetivacaoIssFixoGeral INSTANCE;
    private List<OcorrenciaEfetivacaoIssFixoGeral> ocorrencias;
    private List<BigDecimal> listacalculosParaEfetivar;
    private boolean concluiuEfetivacao;
    private AssistenteBarraProgresso assistenteBarraProgresso;

    private SingletonEfetivacaoIssFixoGeral() {
        testaEInicializaOcorrencias();
    }

    public static SingletonEfetivacaoIssFixoGeral getInstance() {
        if (INSTANCE == null) {
            novaInstancia();
        }
        return INSTANCE;
    }

    private static void novaInstancia() {
        INSTANCE = new SingletonEfetivacaoIssFixoGeral();
    }

    public synchronized void adicionarFalha(String message, CalculoISS calculoISS, EfetivacaoProcessoIssFixoGeral efetivacao) {
        ocorrencias.add(new OcorrenciaEfetivacaoIssFixoGeral(message, calculoISS, efetivacao));
    }

    private void testaEInicializaOcorrencias() {
        if (ocorrencias == null) {
            ocorrencias = new ArrayList<>();
        }
    }

    public void novaListaDeOcorrencias() {
        ocorrencias = new ArrayList<>();
    }

    public List<OcorrenciaEfetivacaoIssFixoGeral> getOcorrencias() {
        return ocorrencias;
    }

    public void setConcluiuEfetivacao(boolean concluiuEfetivacao) {
        this.concluiuEfetivacao = concluiuEfetivacao;
    }

    public List<BigDecimal> getListacalculosParaEfetivar() {
        return listacalculosParaEfetivar;
    }

    public void setListacalculosParaEfetivar(List<BigDecimal> listacalculosParaEfetivar) {
        this.listacalculosParaEfetivar = listacalculosParaEfetivar;
    }

    public void limparDependencias() {
        novaInstancia();
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public synchronized void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
