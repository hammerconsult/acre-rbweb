package br.com.webpublico.negocios.contabil.reprocessamento;

import br.com.webpublico.entidades.ReprocessamentoHistorico;
import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;


@Stateless
public class ReprocessamentoHistoricoFacade extends AbstractFacade<ReprocessamentoHistorico> implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public ReprocessamentoHistoricoFacade() {
        super(ReprocessamentoHistorico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ReprocessamentoHistorico recuperar(Object id) {
        ReprocessamentoHistorico entity = super.recuperar(id);
        Hibernate.initialize(entity.getMensagens());
        return entity;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void geraPDFLog(ReprocessamentoHistorico historico, boolean isApenasErros, TipoReprocessamentoHistorico tipo) {
        String caminhoDaImagem = FacesUtil.geraUrlImagemDir() + "/img/Brasao_de_Rio_Branco.gif";
        String conteudo = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
            + " <title>"
            + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">"
            + " </title>"
            + " </head>"
            + " <body>"
            + "<center>"
            + "<table>"
            + "<tr>"
            + "<td><img src=\"" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNICIPIO DE RIO BRANCO\" /></td>"
            + "<td>Municipio de Rio Branco - AC</td>"
            + "</tr>"
            + "<tr>"
            + "<td colspan=\"2\"> <center> LOG REPROCESSAMENTO "+ tipo.getDescricao().toUpperCase() +" </center> </td>"
            + "</tr>"
            + "</table>"
            + "</center>"
            + "<div style=\"border : solid #92B8D3 1px; \""
            + "<p style='font-size : 8px!important;'>"
            + (isApenasErros ? getLogErros(historico) : getLogCompleto(historico))
            + "</p>"
            + "</div>"
            + "USUÁRIO RESPONSÁVEL:" + sistemaFacade.getUsuarioCorrente().toString() + "<br/>"
            + "               DATA:" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "<br/>"
            + " </body>"
            + " </html>";
        Util.downloadPDF("Log Reprocessamento " + tipo.getDescricao(), conteudo, FacesContext.getCurrentInstance());
    }

    public String getLogCompleto(ReprocessamentoHistorico historico) {
        if (historico != null && historico.getMensagens() != null && !historico.getMensagens().isEmpty()) {
            StringBuilder logFinal = new StringBuilder();
            historico.getMensagens().forEach(log -> logFinal.append(log.getLinhaLog()));
            return logFinal.toString();
        }
        return "... Log do Reprocessamento ...";
    }

    public String getLogErros(ReprocessamentoHistorico historico) {
        if (historico != null && historico.getMensagens() != null && !historico.getMensagens().isEmpty()) {
            StringBuilder logFinal = new StringBuilder();
            historico.getMensagens().forEach(log -> {
                if (log.getLogDeErro()) {
                    logFinal.append(log.getLinhaLog());
                }
            });
            return logFinal.toString();
        }
        return "... Log do Reprocessamento ...";
    }
}
