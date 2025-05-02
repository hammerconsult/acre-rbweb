package br.com.webpublico.seguranca.resources;

import br.com.webpublico.entidades.UsuarioNodeServer;
import br.com.webpublico.negocios.MonitoramentoNodeServerFacade;
import br.com.webpublico.negocios.RemessaProtestoFacade;
import br.com.webpublico.seguranca.AplicacaoDTO;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/aplicacao")
public class AplicacaoResource {

    private MonitoramentoNodeServerFacade monitoramentoNodeServerService;
    @Autowired
    private SistemaService sistemaService;

    @PostConstruct
    public void init(){
        monitoramentoNodeServerService = (MonitoramentoNodeServerFacade) Util.getFacadeViaLookup("java:module/MonitoramentoNodeServerFacade");
    }




    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AplicacaoDTO getStatusAplicacao() {
        AplicacaoDTO dto = new AplicacaoDTO();
        if(monitoramentoNodeServerService != null){
            List<UsuarioNodeServer> usuarioNodeServers = monitoramentoNodeServerService.buscarUltimosLoginsDeHoje(new Date());
            dto.setUsuariosAtivos(usuarioNodeServers != null ? usuarioNodeServers.size() : 0);
        }
        String appVersion = sistemaService.getAppVersion();
        dto.setAtivo(Boolean.TRUE);
        dto.setVersao(appVersion);
        dto.setNome("RBWEB - Webpublico");
        return dto;
    }


}
