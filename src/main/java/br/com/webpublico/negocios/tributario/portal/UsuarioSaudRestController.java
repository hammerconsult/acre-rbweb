package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.ws.model.WsParametroSaudDocumento;
import br.com.webpublico.ws.model.WsUsuarioSaud;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.faces.application.FacesMessage;
import java.util.List;

/**
 * Created by Buzatto on 02/03/2017.
 */
@Controller
@RequestMapping("/integracao/tributario/saud")
public class UsuarioSaudRestController extends PortalRestController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioSaudRestController.class);

    @RequestMapping(value = "/documentos-obrigatorios",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WsParametroSaudDocumento>> getDocumentosObrigatorios() {
        List<WsParametroSaudDocumento> documentos = getPortalContribunteFacade().getWsParametroSaudDocumentos();
        return new ResponseEntity<>(documentos, HttpStatus.OK);
    }

    @RequestMapping(value = "/cadastrar-usuario",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WsUsuarioSaud> cadastrarUsuario(@RequestBody WsUsuarioSaud wsUsuarioSaud) {
        try {
            getPortalContribunteFacade().cadastrarUsuarioSaud(wsUsuarioSaud);
        } catch (ValidacaoException e) {
            List<String> mensagens = Lists.newArrayList();
            for (FacesMessage facesMessage : e.getMensagens()) {
                mensagens.add(facesMessage.getDetail());
            }
            wsUsuarioSaud.setMensagem(StringUtils.join(mensagens, "</br>"));
        } catch (Exception e) {
            wsUsuarioSaud.setMensagem("Erro inesperado ao efetuar a solicitação de cadastro.");
        }
        return new ResponseEntity<>(wsUsuarioSaud, HttpStatus.OK);
    }

    @RequestMapping(value = "/usuario/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WsUsuarioSaud> getWsUsuarioSaud(@PathVariable Long id) {
        WsUsuarioSaud wsUsuarioSaud = getPortalContribunteFacade().getWsUsuarioSaud(id);
        return new ResponseEntity<>(wsUsuarioSaud, HttpStatus.OK);
    }

    @RequestMapping(value = "/retorno-documentos-rejeitados",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void salvarRetornoDocumentosRejeitados(@RequestBody WsUsuarioSaud wsUsuarioSaud) {
        try {
            getPortalContribunteFacade().salvarRetornoDocumentosRejeitados(wsUsuarioSaud);
        } catch (Exception e) {
            logger.error("Erro no retorno de documentos rejeitados do SAUD. " + e.getMessage());
            logger.debug("StackTrace. ", e);
        }
    }
}
