package br.com.webpublico.controle;

import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.tributario.AuditoriaBeanFacade;
import br.com.webpublico.negocios.tributario.AuditoriaFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.EmailService;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by wellington on 18/04/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "geracaoAuditoria",
        pattern = "/auditoria/geracao-auditoria/",
        viewId = "/faces/admin/auditoria/geracao.xhtml")
})
public class AuditoriaControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaFacade.class);
    @EJB
    private AuditoriaFacade auditoriaFacade;
    @EJB
    private AuditoriaBeanFacade auditoriaBeanFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<Future> futures;
    private boolean futuresConcluidas;
    private String tabelas;

    public boolean isFuturesConcluidas() {
        if (futures == null) {
            futuresConcluidas = false;
        } else {
            futuresConcluidas = true;
            for (Future future : futures) {
                if (!future.isDone()) {
                    futuresConcluidas = false;
                    break;
                }
            }
        }
        return futuresConcluidas;
    }

    public void setFuturesConcluidas(boolean futuresConcluidas) {
        this.futuresConcluidas = futuresConcluidas;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public void gerarAuditoria() {
        try {
            logger.debug("INICIOU PROCESSO DE GERAÇÃO DE AUDITORIA");
            futures = Lists.newArrayList();
            auditoriaBeanFacade.criarRevisaoInicial();
            List<String> listTabelas = Lists.newArrayList();
            if (tabelas != null && !tabelas.isEmpty()) {
                listTabelas = Lists.newArrayList(tabelas.toUpperCase().split(","));
            } else {
                listTabelas = auditoriaFacade.buscarTabelasAuditadas();
            }
            if (!listTabelas.isEmpty()) {
                assistenteBarraProgresso = new AssistenteBarraProgresso("Geração de auditória de inserção para registros migrados", listTabelas.size());
                for (List<String> parte : Lists.partition(listTabelas, listTabelas.size() >= 5 ? listTabelas.size() / 5 : 1)) {
                    futures.add(auditoriaFacade.inserirAuditoriaParaAsTabelas(assistenteBarraProgresso, parte));
                }
                logger.debug("acompanhando");
                FacesUtil.executaJavaScript("acompanharGeracao()");
                FacesUtil.executaJavaScript("dlgAcompanhamento.show()");
            } else {
                FacesUtil.addOperacaoNaoRealizada("Nenhuma tabela encontrada");
            }
        } finally {
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public void atualizarAuditoria() {
        futures = Lists.newArrayList();
        auditoriaBeanFacade.criarRevisaoInicial();
        List<String> tabelas = auditoriaFacade.buscarTabelasAuditadas();
        assistenteBarraProgresso = new AssistenteBarraProgresso("Update nas tabelas de auditoria atribuindo a revisão 1 onde está vinculado a revisão 0.", tabelas.size());
        for (List<String> parte : Lists.partition(tabelas, tabelas.size() >= 5 ? tabelas.size() / 5 : 1)) {
            futures.add(auditoriaFacade.updateRevisaoDasTabelasDeAuditoria(assistenteBarraProgresso, parte, 0l, 1l));
        }
        logger.debug("acompanhando");
        FacesUtil.executaJavaScript("acompanharGeracao()");
        FacesUtil.executaJavaScript("dlgAcompanhamento.show()");
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public void terminarProcesso() {
        FacesUtil.addOperacaoRealizada("Geração de auditoria concluida com sucesso!");
        logger.debug("TERMINOU PROCESSO DE GERAÇÃO DE AUDITORIA");
    }

    public void enviarEmail(String email, String corpoEmail, String assunto) {
        EmailService.getInstance().enviarEmail(email, assunto, corpoEmail);
    }

    private String criarMensagemLogGeradaoAuditoria() {
        StringBuilder toReturn = new StringBuilder();
        if (!assistenteBarraProgresso.getMensagens().isEmpty()) {
            toReturn.append("Rotina concluida, porem contem os seguintes erros: ");
            for (String mensagem : assistenteBarraProgresso.getMensagens()) {
                toReturn.append(mensagem).append("\n");
            }
        } else {
            toReturn.append("Rotina concluida com sucesso e sem erros!");
        }
        logger.debug("MENSAGEM [{}]", toReturn.toString());
        return toReturn.toString();
    }

    public String getTabelas() {
        return tabelas;
    }

    public void setTabelas(String tabelas) {
        this.tabelas = tabelas;
    }
}
