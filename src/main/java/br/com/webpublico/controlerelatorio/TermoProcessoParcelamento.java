/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DemonstrativoCancelamentoParcelamento;
import br.com.webpublico.entidadesauxiliares.DemonstrativoParcelamento;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.Util;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Renato
 */
public class TermoProcessoParcelamento extends AbstractReport {

    HashMap parameters;

    public void emiteTermo(Pessoa contribuinte, EnderecoCorreio enderecoCorreio, String clausulaTerceira, String clausulaQuarta, String dataHoje, String usuarioLogado, String quadra, String lote, String inscricao, String dividas, String exercicios, Long id, Pessoa fiador, Telefone telefoneFiador, EnderecoCorreio enderecoFiador, String quadraFiador, String loteFiador) throws JRException, IOException {
        parameters = new HashMap();
        parameters.put("CAMINHOIMAGEM", getCaminhoImagem());
        parameters.put("CLAUSULATERCEIRA", clausulaTerceira);
        parameters.put("CLAUSULAQUARTA", clausulaQuarta);
        parameters.put("DATAHOJE", dataHoje);
        parameters.put("USUARIOLOGADO", usuarioLogado);
        parametroContribuinte(contribuinte, enderecoCorreio, quadra, lote);
        parametroFiador(fiador, telefoneFiador, enderecoFiador, quadraFiador, loteFiador);

        addParametro("INSCRICAO", inscricao);
        addParametro("DIVIDAS", dividas);
        addParametro("EXERCICIOS", exercicios);
        addParametro("PROCESSO_ID", id);
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        parameters.put("SUBREPORT_DIR", subReport);
        gerarRelatorio("termo_processo_parcelamento.jasper", parameters);
    }

    private void parametroContribuinte(Pessoa contribuinte, EnderecoCorreio enderecoCorreio, String quadra, String lote) {
        if (contribuinte instanceof PessoaJuridica) {
            parameters.put("NOMEFANTASIA", ((PessoaJuridica) contribuinte).getNomeFantasia());
            parameters.put("INSCRICAOESTADUAL", ((PessoaJuridica) contribuinte).getInscricaoEstadual());
            parameters.put("NOME", contribuinte.getNome());
        } else {
            parameters.put("NOMEFANTASIA", " ");
            parameters.put("INSCRICAOESTADUAL", " ");
            parameters.put("NOME", ((PessoaFisica) contribuinte).getNome());
        }
        parameters.put("CPFCNPJ", contribuinte.getCpf_Cnpj());

        if (contribuinte.getTelefones().isEmpty()) {
            parameters.put("TELEFONE", " ");
        } else {
            addParametro("TELEFONE", contribuinte.getTelefones().get(0).getTelefone());
        }

        if (enderecoCorreio != null) {
            addParametro("ENDERECO", enderecoCorreio.getLogradouro());
            addParametro("NUMERO", enderecoCorreio.getNumero());
            addParametro("COMPLEMENTO", enderecoCorreio.getComplemento());
            addParametro("BAIRRO", enderecoCorreio.getBairro());
            addParametro("CIDADE", enderecoCorreio.getLocalidade());
            addParametro("ESTADO", enderecoCorreio.getUf());
            addParametro("QUADRA", quadra);
            addParametro("LOTE", lote);
            addParametro("CEP", enderecoCorreio.getCep());
        }
    }


    private void parametroFiador(Pessoa fiador, Telefone telefoneFiador, EnderecoCorreio enderecoCorreio, String quadra, String lote) {
        if (fiador != null) {
            if (fiador instanceof PessoaJuridica) {
                parameters.put("FIADOR_INSCRICAOESTADUAL", ((PessoaJuridica) fiador).getInscricaoEstadual());
                parameters.put("FIADOR_NOME", fiador.getNome());
            } else {
                parameters.put("FIADOR_INSCRICAOESTADUAL", " ");
                parameters.put("FIADOR_NOME", ((PessoaFisica) fiador).getNome());
            }
            parameters.put("FIADOR_CPFCNPJ", fiador.getCpf_Cnpj());
        } else {
            parameters.put("FIADOR_INSCRICAOESTADUAL", "");
            parameters.put("FIADOR_NOME", "");
            parameters.put("FIADOR_CPFCNPJ", "");
        }

        if (telefoneFiador != null) {
            addParametro("FIADOR_TELEFONE", telefoneFiador.getTelefone());
        } else {
            addParametro("FIADOR_TELEFONE", "");
        }

        if (enderecoCorreio != null) {
            addParametro("FIADOR_ENDERECO", enderecoCorreio.getLogradouro());
            addParametro("FIADOR_END_NUMERO", enderecoCorreio.getNumero());
            addParametro("FIADOR_COMPLEMENTO", enderecoCorreio.getComplemento());
            addParametro("FIADOR_BAIRRO", enderecoCorreio.getBairro());
            addParametro("FIADOR_CIDADE", enderecoCorreio.getLocalidade());
            addParametro("FIADOR_ESTADO", enderecoCorreio.getUf());
            addParametro("FIADOR_QUADRA", quadra);
            addParametro("FIADOR_LOTE", lote);
            addParametro("FIADOR_CEP", enderecoCorreio.getCep());
        } else {
            addParametro("FIADOR_ENDERECO", "");
            addParametro("FIADOR_END_NUMERO", "");
            addParametro("FIADOR_COMPLEMENTO", "");
            addParametro("FIADOR_BAIRRO", "");
            addParametro("FIADOR_CIDADE", "");
            addParametro("FIADOR_ESTADO", "");
            addParametro("FIADOR_QUADRA", "");
            addParametro("FIADOR_LOTE", "");
            addParametro("FIADOR_CEP", "");
        }
    }

    public void addParametro(String parametro, Object valor) {
        if (valor != null) {
            parameters.put(parametro, valor);
        } else {
            parameters.put(parametro, " - ");
        }
    }

    public void emitirDemonstrativo(DemonstrativoParcelamento demonstrativoParcelamento) throws JRException, IOException {
        byte[] bytes = gerarBytesDemonstrativo(demonstrativoParcelamento);
        escreveNoResponse("DemonstrativoParcelamento.jasper", bytes);
    }

    public byte[] gerarBytesDemonstrativo(DemonstrativoParcelamento demonstrativoParcelamento) throws JRException, IOException {
        String arquivoJasper = "DemonstrativoParcelamento.jasper";
        HashMap parameters = new HashMap();
        setGeraNoDialog(true);
        parameters.put("BRASAO", getCaminhoImagem());
        parameters.put("USUARIO", Util.disfarcarLogin(SistemaFacade.obtemLogin()));
        parameters.put("MOSTRADESCONTO", demonstrativoParcelamento.getMostraDesconto());
        parameters.put("IP", "");
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        parameters.put("SUBREPORT_DIR", subReport);
        ArrayList<DemonstrativoParcelamento> lista = new ArrayList<>();
        lista.add(demonstrativoParcelamento);
        return gerarBytesRelatorio(arquivoJasper, parameters, new JRBeanCollectionDataSource(lista));
    }

    public void emitirDemonstrativoCancelamento(DemonstrativoCancelamentoParcelamento demonstrativoCancelamentoParcelamento) throws JRException, IOException {
        String arquivoJasper = "DemonstrativoCancelamentoParcelamento.jasper";
        HashMap parameters = new HashMap();
        setGeraNoDialog(true);
        parameters.put("BRASAO", getCaminhoImagem());
        parameters.put("USUARIO", SistemaFacade.obtemLogin());
        parameters.put("IP", SistemaFacade.obtemIp());
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        parameters.put("SUBREPORT_DIR", subReport);

        ArrayList<DemonstrativoCancelamentoParcelamento> lista = new ArrayList<>();
        lista.add(demonstrativoCancelamentoParcelamento);
        gerarRelatorio(arquivoJasper, parameters, new JRBeanCollectionDataSource(lista));
    }

    public void gerarRelatorio(String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException, IOException {
        byte[] bytes = gerarBytesRelatorio(arquivoJasper, parametros, jrbc);
        escreveNoResponse(arquivoJasper, bytes);
    }

    public byte[] gerarBytesRelatorio(String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
        JasperPrint jasperPrint = JasperFillManager.fillReport(scontext.getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, jrbc);
        return preparaExportaReport(jasperPrint).toByteArray();
    }
}
