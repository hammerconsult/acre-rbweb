/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.interfaces.GeradorRelatorioAssincrono;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * @author Renato
 */
public class LivroDividaAtivaRelatorio extends AbstractReport implements GeradorRelatorioAssincrono {
    private Long id;
    private boolean gerando;
    private Boolean gerado;
    private ByteArrayOutputStream dadosByte;
    private String caminhoImagem;
    private String report;

    public LivroDividaAtivaRelatorio(Long id) {
        this.id = id;
        caminhoImagem = getCaminhoImagem();
        report = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/WEB-INF/report/LivroDividaAtiva.jasper");

    }

    @Override
    public HashMap getParametros() {
        HashMap parameters = new HashMap();
        parameters.put("LIVRO_ID", id);
        parameters.put("BRASAO", caminhoImagem);
        return parameters;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean getGerando() {
        return gerando;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setGerando(Boolean gerando) {
        this.gerando = gerando; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getReport() {
        return report;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteArrayOutputStream getDadosByte() {
        return dadosByte;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setDadosByte(ByteArrayOutputStream dadosByte) {
        this.dadosByte = dadosByte;//To change body of implemented methods use File | Settings | File Templates.
    }

    public Boolean getGerado() {
        return gerado;
    }

    public void setGerado(Boolean gerado) {
        this.gerado = gerado;
    }
}
