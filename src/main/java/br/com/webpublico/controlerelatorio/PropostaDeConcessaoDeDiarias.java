/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.DiariaDeCampo;
import br.com.webpublico.entidades.PropostaConcessaoDiaria;
import br.com.webpublico.enums.TipoProposta;
import net.sf.jasperreports.engine.JRException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author major
 */
@ManagedBean
@SessionScoped
public class PropostaDeConcessaoDeDiarias extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    /**
     * Creates a new instance of RelatorioDeCotasOrcamentarias
     */
    public PropostaDeConcessaoDeDiarias() {
    }

    public void geraProposta(PropostaConcessaoDiaria proposta) throws JRException, IOException {
        String nomeArquivo = "";
        if (proposta.getTipoProposta().equals(TipoProposta.COLABORADOR_EVENTUAL)) {
            nomeArquivo = "PropostaDeConcessaoDeDiariasColaboradorEventual.jasper";
        } else {
            nomeArquivo = "PropostaDeConcessaoDeDiarias.jasper";
        }
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "Muncípio de Rio Branco - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("ID_PROPOSTA", proposta.getId());
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        }
        gerarRelatorio(nomeArquivo, parameters);
    }

    public void geraRelatorioViagem(PropostaConcessaoDiaria proposta) throws JRException, IOException {
        String nomeArquivo = "RelatorioViagem.jasper";
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "Prefeitura Municipal de Rio Branco - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("ID_PROPOSTA", proposta.getId());
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        }
        gerarRelatorio(nomeArquivo, parameters);
    }

    public void geraRelatorioDiariaCampo(DiariaDeCampo diaria) throws JRException, IOException {
        String nomeArquivo = "RelatorioDiariasCampo.jasper";
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "Prefeitura Municipal de Rio Branco - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("ID_DIARIA", diaria.getId());
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        }
        gerarRelatorio(nomeArquivo, parameters);
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
