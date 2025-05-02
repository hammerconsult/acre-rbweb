/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.negocios.PixFacade;
import net.sf.jasperreports.engine.JRException;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author gustavo
 */
public class EmiteCarneGenerico extends AbstractReport {

    private PixFacade pixFacade;

    @PostConstruct
    public void init() {
        try {
            pixFacade = (PixFacade) Util.getFacadeViaLookup("java:module/PixFacade");
        } catch (Exception e) {
            logger.error("Erro ao recuperar facade pix. ", e);
        }
    }

    public void emiteCarne(Long valorDividaId, String titulo, String endereco, String cnpjCpf,
                           String contribuinte, String cadastro,
                           String exercicio, boolean isHomologacao) throws JRException, IOException {
        pixFacade.gerarQrCodePIXPeloIdValorDivida(valorDividaId);

        HashMap parameters = new HashMap();
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF/report") + "/";
        parameters.put("TITULO", titulo);
        //System.out.println("valorDividaId " + valorDividaId);
        parameters.put("VALORDIVIDA_ID", valorDividaId);
        parameters.put("ENDERECO", endereco);
        parameters.put("CNPJ_CPF", cnpjCpf);
        parameters.put("CONTRIBUINTE", contribuinte);
        parameters.put("CADASTRO", cadastro);
        parameters.put("EXERCICIO", exercicio);
        parameters.put("BRASAO", getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("HOMOLOGACAO", isHomologacao);
        parameters.put("MSG_PIX", "Pagamento Via QrCode PIX");
        gerarRelatorio("CarneGenerico.jasper", parameters);
    }

    public void emiteCarne(Long valorDividaId, String titulo, String endereco, String cnpjCpf,
                           String contribuinte, String cadastro,
                           String exercicio,
                           String whereAdicional, boolean isHomologacao) throws JRException, IOException {
        pixFacade.gerarQrCodePIXPeloIdValorDivida(valorDividaId);

        HashMap parameters = new HashMap();
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF/report") + "/";
        parameters.put("TITULO", titulo);
        //System.out.println("valorDividaId " + valorDividaId);
        parameters.put("VALORDIVIDA_ID", valorDividaId);
        parameters.put("ENDERECO", endereco);
        parameters.put("CNPJ_CPF", cnpjCpf);
        parameters.put("CONTRIBUINTE", contribuinte);
        parameters.put("CADASTRO", cadastro);
        parameters.put("EXERCICIO", exercicio);
        parameters.put("CONTINUACAO_WHERE", whereAdicional);
        //System.out.println("continuacao_where = " + whereAdicional);
        parameters.put("BRASAO", getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("HOMOLOGACAO", isHomologacao);
        parameters.put("MSG_PIX", "Pagamento Via QrCode PIX");
        gerarRelatorio("CarneGenerico.jasper", parameters);
    }

    public void emiteDam(Long valorDividaId, String descricaoLonga, String descricaoCurta, String cnpjCpf, String contribuinte, String cadastro, String tipoCadastro, String referencia, String tributo, boolean isHomologacao) throws JRException, IOException {
        pixFacade.gerarQrCodePIXPeloIdValorDivida(valorDividaId);

        HashMap parameters = new HashMap();
        parameters.put("VALORDIVIDA_ID", valorDividaId);
        parameters.put("DESCRICAO_LONGA", descricaoLonga);
        parameters.put("DESCRICAO_CURTA", descricaoCurta);
        parameters.put("NOME_CONTRIBUINTE", contribuinte);
        parameters.put("CPF_CONTRIBUINTE", cnpjCpf);
        parameters.put("INSCRICAO_CONTRIBUINTE", cadastro);
        parameters.put("TITULO_INSCRICAO", tipoCadastro);
        parameters.put("DESCRICAO_TRIBUTO", tributo);
        parameters.put("REFERENCIA", referencia);
        parameters.put("BRASAO", getCaminhoImagem());
        parameters.put("HOMOLOGACAO", isHomologacao);
        parameters.put("MSG_PIX", "Pagamento Via QrCode PIX");
        gerarRelatorio("CarneDamGenerico.jasper", parameters);
    }
}
