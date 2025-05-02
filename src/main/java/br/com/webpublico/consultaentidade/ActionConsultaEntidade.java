package br.com.webpublico.consultaentidade;

import br.com.webpublico.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ActionConsultaEntidade {

    public String href;
    public String icone;
    public String descricao;
    public String title;
    public String style;
    public boolean isDownload;
    public TipoAlinhamento alinhamento;

    public ActionConsultaEntidade() {
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public TipoAlinhamento getAlinhamento() {
        return alinhamento == null ? TipoAlinhamento.ESQUERDA : alinhamento;
    }

    public void setAlinhamento(TipoAlinhamento alinhamento) {
        this.alinhamento = alinhamento;
    }

    public String getStyle() {
        return style == null ? "height: 25px; width: 25px;" : style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void action(Map<String, Object> registro) throws IOException {
        String url = href;
        if (registro != null) {
            for (String s : registro.keySet()) {
                url = url.replace("$" + s, registro.get(s) != null ? registro.get(s).toString() : "");
            }
        }
        FacesUtil.redirecionamentoInterno(url);
    }

    protected void escreverNoResponse(byte[] bytes, String nomeArquivo, String contentType) throws IOException {
        if (bytes != null && bytes.length > 0) {
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline; filename=" + nomeArquivo);
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
            FacesContext.getCurrentInstance().responseComplete();
        }
    }

    public boolean canRenderizar(Map<String, Object> registro) {
        return true;
    }
}
