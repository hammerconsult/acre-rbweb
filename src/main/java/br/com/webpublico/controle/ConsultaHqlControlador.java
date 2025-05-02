/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.comum.consultasql.ColunaView;
import br.com.webpublico.entidades.comum.consultasql.HistoricoConsultarSql;
import br.com.webpublico.entidades.comum.consultasql.ObjetoView;
import br.com.webpublico.entidades.comum.consultasql.View;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConsultaHqlFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Html2Pdf;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jaime
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-consultar-sql", pattern = "/consultar-sql/novo/", viewId = "/faces/admin/consultahql/edita.xhtml"),
    @URLMapping(id = "ver-consultar-sql", pattern = "/consultar-sql/ver/#{consultaHqlControlador.id}/", viewId = "/faces/admin/consultahql/visualizar.xhtml"),
    @URLMapping(id = "listar-consultar-sql", pattern = "/consultar-sql/listar/", viewId = "/faces/admin/consultahql/lista.xhtml")
})
public class ConsultaHqlControlador implements Serializable {

    @EJB
    private ConsultaHqlFacade consultaHqlFacade;
    private View view;
    private Long id;
    private HistoricoConsultarSql historico;
    private static final Logger logger = LoggerFactory.getLogger(ConsultaHqlControlador.class);

    @URLAction(mappingId = "novo-consultar-sql", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        try {
            if (!consultaHqlFacade.getSistemaFacade().getUsuarioCorrente().hasRole("ROLE_ADMIN")) {
                FacesContext.getCurrentInstance().getExternalContext().redirect(FacesUtil.getRequestContextPath() + "/home");
            }
            view = new View();
            view.setUsuarioSistema(consultaHqlFacade.getSistemaFacade().getUsuarioCorrente());
            view.setHistoricos(consultaHqlFacade.buscarHistoricoUsuarioLogado());
        } catch (Exception ex) {
            logger.error("Erro ao acessar a tela de novo-consultar-sql", ex);
        }
    }

    @URLAction(mappingId = "ver-consultar-sql", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        try {
            if (!consultaHqlFacade.getSistemaFacade().getUsuarioCorrente().hasRole("ROLE_ADMIN")) {
                FacesContext.getCurrentInstance().getExternalContext().redirect(FacesUtil.getRequestContextPath() + "/home");
            }
            historico = consultaHqlFacade.recuperarHistorico(id);
        } catch (Exception ex) {
            logger.error("Erro ao acessar a tela de ver-consultar-sql", ex);
        }
    }

    @URLAction(mappingId = "listar-consultar-sql", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        try {
            if (!consultaHqlFacade.getSistemaFacade().getUsuarioCorrente().hasRole("ROLE_ADMIN")) {
                FacesContext.getCurrentInstance().getExternalContext().redirect(FacesUtil.getRequestContextPath() + "/home");
            }
        } catch (Exception ex) {
            logger.error("Erro ao acessar a tela de listar-consultar-sql", ex);
        }
    }

    public void filtrar() {
        try {
            validarQuery();
            if (view.isSqlPesquisar()) {
                view.setInicioExecucao(System.currentTimeMillis());
                view = consultaHqlFacade.recuperarObjetos(view);
                view.setFimExecucao(System.currentTimeMillis());
                view.setTempoExecucao(String.valueOf((view.getFimExecucao() - view.getInicioExecucao()) / 1000d));
            } else {
                FacesUtil.executaJavaScript("dialogMotivo.show()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void salvarHistorico() {
        try {
            validarQuery();
            if (view.isSqlPesquisar()) {
                consultaHqlFacade.gerarNovoHistorico(view);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void executarComando() {
        try {
            validarExecutarComando();
            view.setInicioExecucao(System.currentTimeMillis());
            view = consultaHqlFacade.executarUpdate(view);
            view.setFimExecucao(System.currentTimeMillis());
            view.setTempoExecucao(String.valueOf((view.getFimExecucao() - view.getInicioExecucao()) / 1000d));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void validarExecutarComando() {
        ValidacaoException ve = new ValidacaoException();
        if (view.getMotivo() == null || view.getMotivo().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarQuery() {
        ValidacaoException ve = new ValidacaoException();
        if (view.getSql().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O SQL deve ser informado.");
        }
        ve.lancarException();
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public List<SelectItem> getQuantidadeRegistro() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(1, "1"));
        toReturn.add(new SelectItem(10, "10"));
        toReturn.add(new SelectItem(25, "25"));
        toReturn.add(new SelectItem(50, "50"));
        toReturn.add(new SelectItem(100, "100"));
        toReturn.add(new SelectItem(0, "Todos"));
        return toReturn;
    }

    public StreamedContent exportarExcel() {
        try {
            validarExportar();
            List<String> titulos = new ArrayList<>();
            for (ColunaView colunaView : this.view.getColunas()) {
                titulos.add(colunaView.getNomeColuna());
            }
            List<Object[]> objetos = new ArrayList<>();

            for (ObjetoView objeto : view.getObjetos()) {
                Object[] obj = new Object[objeto.getColunas().size()];
                int posicao = 0;
                for (ColunaView colunaView : objeto.getColunas()) {
                    obj[posicao] = colunaView.getValorParaExportar();
                    posicao++;
                }
                objetos.add(obj);
            }
            ExcelUtil excel = new ExcelUtil();
            excel.gerarExcel(view.getTitulo(), view.getNomeArquivo(), titulos, objetos, null);
            return excel.fileDownload();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
        return null;
    }

    private void validarExportar() {
        ValidacaoException ve = new ValidacaoException();
        if (view.getTitulo().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Título é obrigatório");
        }
        if (view.getNomeArquivo().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Nome do arquivo é obrigatório");
        }
        ve.lancarException();
    }

    public void exportarPDF() {
        try {
            validarExportar();
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline;filename=" + view.getNomeArquivo() + ".pdf");
            response.setCharacterEncoding("UTF-8");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String html = montarHtml();

            Html2Pdf.convert(html, baos);
            byte[] bytes = baos.toByteArray();
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();

            facesContext.responseComplete();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }

    }

    private String montarHtml() {
        StringBuilder conteudo = montarConteudoTabela();
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <style type=\"text/css\"  media=\"all\">"
            + " @page{"
            + " size: A4 landscape; "
            + " margin-top: 1.0in;"
            + " margin-bottom: 1.0in;"
            + " @bottom-center {"
            + " content: element(footer);"
            + " }"
            + " @top-center {"
            + " content: element(header);"
            + " }"
            + "}"
            + "#page-header {"
            + " display: block;"
            + " position: running(header);"
            + " }"
            + " #page-footer {"
            + " display: block;"
            + " position: running(footer);"
            + " }"
            + " .page-number:before {"
            + "  content: counter(page) "
            + " }"
            + " .page-count:before {"
            + " content: counter(pages);  "
            + "}"
            + "</style>"
            + " <style type=\"text/css\">"
            + ".igualDataTable{"
            + "    border-collapse: collapse; /* CSS2 */"
            + "    width: 100%;"
            + "    ;"
            + "}"
            + ".igualDataTable th{"
            + "    border: 0px solid #aaaaaa; "
            + "    height: 20px;"
            + "    background: #ebebeb 50% 50% repeat-x;"
            + "}"
            + ".igualDataTable td{"
            + "    padding: 2px;"
            + "    border: 0px; "
            + "    height: 20px;"
            + "}"
            + ".igualDataTable thead td{"
            + "    border: 1px solid #aaaaaa; "
            + "    background: #6E95A6 repeat-x scroll 50% 50%; "
            + "    border: 0px; "
            + "    text-align: center; "
            + "    height: 20px;"
            + "}"
            + " .igualDataTable tr:nth-child(2n+1) {"
            + " background:lightgray;"
            + " }"
            + "body{"
            + "font-size: 8pt; font-family:\"Arial\", Helvetica, sans-serif"
            + "}"
            + ""
            + "</style>"
            + " <title>"
            + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">"
            + view.getNomeArquivo()
            + " </title>"
            + " </head>"
            + " <body>"
            + conteudo.toString()
            + " </body>"
            + " </html>";
    }

    private StringBuilder montarConteudoTabela() {
        StringBuilder conteudo = new StringBuilder();
        conteudo.append(adicionaCabecalho());
        conteudo.append("<br></br>");
        conteudo.append("<br></br>");
        conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
        conteudo.append("<tr>");
        for (ColunaView colunaView : this.view.getColunas()) {
            conteudo.append("<td>");
            conteudo.append(colunaView.getNomeColuna());
            conteudo.append("</td>");
        }
        conteudo.append("</tr>");

        for (ObjetoView objeto : view.getObjetos()) {
            conteudo.append("<tr>");
            for (ColunaView colunaView : objeto.getColunas()) {
                conteudo.append("<td>");
                conteudo.append(colunaView.getValorParaExportar());
                conteudo.append("</td>");
            }
            conteudo.append("</tr>");
        }
        conteudo.append("</table>");
        return conteudo;
    }

    public String adicionaCabecalho() {
        String caminhoDaImagem = FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";

        String conteudo =
            "<table>"
                + "<tbody>"
                + "<tr>"
                + "<td style=\"text-align: left;\" align=\"right\"><img src=\"" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" /></td>"
                + "<td style=\"line-height:100%; margin-left: 50px;\">"
                + "<h2> PREFEITURA MUNICIPAL DE RIO BRANCO</h2>"
                + " <h3> " + view.getTitulo() + " </h3>"
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>";
        return conteudo;
    }

    public String getCaminhoPadrao() {
        return "/consultar-sql/";
    }

    public void cancelar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoricoConsultarSql getHistorico() {
        return historico;
    }

    public void setHistorico(HistoricoConsultarSql historico) {
        this.historico = historico;
    }
}
