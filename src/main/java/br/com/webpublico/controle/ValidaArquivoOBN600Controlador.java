/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.RegistroArquivoRemessa;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoRegistroArquivoRemessa;
import br.com.webpublico.enums.TipoRegistroOBN600Tipo2;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ArquivoOBN600;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author reidocrime
 */
@ManagedBean(name = "validaArquivoOBN600Controlador")
@ViewScoped
@URLMapping(id = "valida-obn600",
    pattern = "/valida-arquivo-obn600/edita/",
    viewId = "/faces/financeiro/orcamentario/arquivoremessa/validador/edita.xhtml")
public class ValidaArquivoOBN600Controlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ValidaArquivoOBN600Controlador.class);

    @EJB
    private ArquivoOBN600 arquivoOBN600;
    private UploadedFile uploadFile;
    private List<String> linhas;
    private List<RegistroArquivoRemessa> headerArquivoRemessas;
    private List<RegistroArquivoRemessa> registroDoisArquivoRemessas;
    private List<RegistroArquivoRemessa> registroUmArquivoRemessas;
    private List<RegistroArquivoRemessa> trailerArquivoRemessas;
    private StringBuilder tabelas;
    private StringBuilder borderosDoArquivo;
    private List<UploadedFile> arquivos;

    @URLAction(mappingId = "valida-obn600", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        uploadFile = new DefaultUploadedFile();
        registroDoisArquivoRemessas = arquivoOBN600.gerarPosicaoRegistroDoisObn600(null);
        registroUmArquivoRemessas = arquivoOBN600.gerarPosicaoRegistroUmObn600(null);
        headerArquivoRemessas = arquivoOBN600.gerarPosicaoHeaderObn600(null);
        trailerArquivoRemessas = arquivoOBN600.gerarPosicaoTrailerObn600(null);
        tabelas = new StringBuilder();
        borderosDoArquivo = new StringBuilder();
        List<UploadedFile> arquivos = new ArrayList<>();
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage msg;
        try {
            uploadFile = event.getFile();
            linhas = arquivoOBN600.convertInputStreamArquivoParaListString(uploadFile.getInputstream());
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, " O arquivo" + event.getFile().getFileName() + " foi adicionado com sucesso!", "");
        } catch (Exception ex) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), " O arquivo gerou um erro de leitura !");
            logger.error("Erro: ", ex);
        }
    }

    public void handleFilesUploads(FileUploadEvent event) {
        FacesMessage msg;
        try {
            uploadFile = event.getFile();
            linhas = arquivoOBN600.convertInputStreamArquivoParaListString(uploadFile.getInputstream());
        } catch (Exception ex) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), " O arquivo gerou um erro de leitura !");
            logger.error("Erro: ", ex);
        }

        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, " O arquivo" + event.getFile().getFileName() + " foi adicionado com sucesso!", "");
    }

    public void iniciaProcessosValidacao() {
        buscaValoresLayout();
    }

    private String removerAcentos(String str) {
        StringUtil.removeCaracteresEspeciais(str);
        return str.toUpperCase();
    }

    private void buscaValoresLayout() {
        StringBuilder tabela = new StringBuilder();
        boolean geraNovaTabela = false;
        List<RegistroArquivoRemessa> registro;

        if (uploadFile == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Não foi Localizado nenhum Arquivo.");
            return;
        }
        if (linhas == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Não foi encontrada nenhuma linha preenchida no arquivo!");
            return;
        }
        try {
            for (String s : linhas) {
                String header = "";
                String line = "";
                if (s.startsWith("2")) {
                    registro = registroDoisArquivoRemessas;
                    geraNovaTabela = false;
                } else if (s.startsWith("1")) {
                    registro = registroUmArquivoRemessas;
                    geraNovaTabela = true;
                } else if (s.startsWith("0")) {
                    registro = headerArquivoRemessas;
                    geraNovaTabela = true;
                } else if (s.startsWith("9")) {
                    registro = trailerArquivoRemessas;
                    geraNovaTabela = true;
                } else {
                    throw new ExcecaoNegocioGenerica("A linha contem um tipo de registro não tratado " + s.replace(" ", ";").replaceAll(";+", ";"));
                }

                for (RegistroArquivoRemessa r : registro) {
                    header += tagHtmlTh(r.getDescricaoTipoUtilizado());
                    String recorte = "";
                    try {

                        recorte = s.substring(r.getPosicaoInicial() - 1, r.getPosicaoFinal());
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ExcecaoNegocioGenerica("Erro ao validar arquivo. A linha acabou antes de apresentar o valor da posição " + (r.getPosicaoInicial() - 1) + " até " + r.getPosicaoFinal() + ". " + r.getDescricaoTipoUtilizado());
                    }

                    line += tagHtmlTd(recorte);
                    if (r.getTipoRegistroArquivoRemessa().equals(TipoRegistroArquivoRemessa.TIPO_2)) {//
                        if (r.getTipoRegistroOBN600Tipo2().equals(TipoRegistroOBN600Tipo2.CODIGO_RELACAO_OB)) {
                            if (!borderosDoArquivo.toString().contains(recorte)) {
                                adicionaCodigoDosBorderos(recorte);
                            }
                        }
                    }
                }
                tabela.append(tagHtmlTr(header));
                tabela.append(tagHtmlTr(line));

                if (geraNovaTabela) {
                    tabelas.append(tagHtmlTable(tabela.toString()));
                    tabela = new StringBuilder();
                }
            }
        } catch (Exception exe) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), exe.getMessage());
        }

    }


    public UploadedFile getFile() {
        return uploadFile;
    }

    public void setFile(UploadedFile file) {
        this.uploadFile = file;
    }

    private String tagHtmlTr(String valor) {
        return "<tr>" + valor + "</tr> ";
    }

    private String tagHtmlTh(String valor) {
        return "<th>" + valor + "</th> ";
    }

    private String tagHtmlTd(String valor) {
        return "<td>" + valor + "</td> ";
    }

    private String tagHtmlTable(String valor) {
        return "<table border=1 cellspacing=0 cellpadding=2 > " + valor + " </table>";
    }

    public String criaCorpoHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>");
        html.append(" <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        html.append(" <html>");
        html.append(" <head>");
        html.append(" <title>");
        html.append(" Sistema WebPúblico Vizualisador Layout do OBN600");
        html.append(" </title>");
        html.append(" </head>");
        html.append(" <body>");
        html.append(" <center><h3> Sistema WebPúblico Visualizador de Arquivo Layout OBN600</h3></center>");
        html.append(tabelas.toString());
        html.append(" <br/><br/>");
        html.append("<center><h3> Ordem(s) Bancária(s) que fazem parte do arquivo </h3></center>");
        html.append("<br/>");
        html.append(borderosDoArquivo.toString());
        return html.toString();
    }

    private void adicionaCodigoDosBorderos(String valor) {
        borderosDoArquivo.append("<b>Ordem(s) Bancária(s): </b>");
        borderosDoArquivo.append(valor);
        borderosDoArquivo.append(";  ");
    }

    public List<String> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<String> linhas) {
        this.linhas = linhas;
    }
}
