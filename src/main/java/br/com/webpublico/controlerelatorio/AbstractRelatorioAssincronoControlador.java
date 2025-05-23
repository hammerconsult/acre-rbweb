package br.com.webpublico.controlerelatorio;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.AbstractRelatorioAssincronoFacade;
import br.com.webpublico.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.concurrent.Future;

/**
 * Created by mateus on 19/03/18.
 */
public abstract class AbstractRelatorioAssincronoControlador extends AbstractReport {

    protected Future<ByteArrayOutputStream> futureBytesRelatorio;
    private byte[] bytes;

    public void verificarRelatorio() {
        if (futureBytesRelatorio != null && futureBytesRelatorio.isDone()) {
            FacesUtil.executaJavaScript("terminarRelatorio()");
            FacesUtil.executaJavaScript("clearInterval(timer)");
            try {
                bytes = futureBytesRelatorio.get().toByteArray();
                futureBytesRelatorio = null;
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            HashMap parametros = getParameters();
            setFutureBytesRelatorio(getFacade().gerarRelatorio(parametros, clausulasConsulta(), getCaminho(), arquivoJasper()));
            FacesUtil.executaJavaScript("iniciarRelatorio()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.executaJavaScript("dlgImprimir.hide()");
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    protected abstract AbstractRelatorioAssincronoFacade getFacade();

    protected abstract Object clausulasConsulta();

    protected abstract String arquivoJasper();

    protected abstract void validarCampos();

    protected abstract HashMap getParameters();

    protected void imprimir(String nomeRelatorio) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.responseComplete();
        try {
            if (bytes != null && bytes.length > 0) {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                response.setContentType("application/pdf");
                response.setHeader("Content-disposition", "inline; filename=\"" + nomeRelatorio + ".pdf\"");
                response.setContentLength(bytes.length);
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes, 0, bytes.length);
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public Future<ByteArrayOutputStream> getFutureBytesRelatorio() {
        return futureBytesRelatorio;
    }

    public void setFutureBytesRelatorio(Future<ByteArrayOutputStream> futureBytesRelatorio) {
        this.futureBytesRelatorio = futureBytesRelatorio;
    }
}
