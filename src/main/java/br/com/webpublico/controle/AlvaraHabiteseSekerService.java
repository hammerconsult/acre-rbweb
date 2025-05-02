package br.com.webpublico.controle;


import br.com.webpublico.negocios.AlvaraHabiteseSekerFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.executores.AlvaraHabiteseSekerExecutor;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;

@Service
public class AlvaraHabiteseSekerService {

    private AlvaraHabiteseSekerFacade alvaraHabiteseSekerFacade;
    private SistemaFacade sistemaFacade;
    private AssistenteBarraProgresso assistente;

    @PostConstruct
    private void init() {
        try {
            sistemaFacade = (SistemaFacade) new InitialContext().lookup("java:module/SistemaFacade");
            alvaraHabiteseSekerFacade = (AlvaraHabiteseSekerFacade) new InitialContext().lookup("java:module/AlvaraHabiteseSekerFacade");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public AssistenteBarraProgresso migrarAlvarasSeker(AssistenteBarraProgresso assistente) {
        //FacesUtil.addInfo("Migração iniciada com sucesso", "Para verificar o andamento da migração acesse a tela de processos assíncronos");
        alvaraHabiteseSekerFacade.migrarAlvaras(assistente);
        return assistente;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        AssistenteBarraProgresso assistenteBarraProgresso = new AssistenteBarraProgresso();
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDataAtual(new Date());
        assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        return assistenteBarraProgresso;
    }

    public void migrar() {
        assistente = getAssistenteBarraProgresso();
        AlvaraHabiteseSekerExecutor.build().executeFuture(assistente);
    }
}
