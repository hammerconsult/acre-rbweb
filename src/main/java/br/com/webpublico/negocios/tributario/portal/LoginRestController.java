package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.entidades.MetricaSistema;
import br.com.webpublico.entidades.comum.TermoUso;
import br.com.webpublico.entidadesauxiliares.AceiteTermoUsoDTO;
import br.com.webpublico.entidadesauxiliares.comum.UsuarioPortalWebDTO;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.ws.model.ConsultaCPF;
import br.com.webpublico.ws.model.WSPessoa;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/integracao/tributario/usuario")
public class LoginRestController extends PortalRestController {

    private static final String LOGIN_REST_CONTROLLER = "Login de Usuários";


    @RequestMapping(value = "/login",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam(value = "cpf") String cpf,
                                   @RequestParam(value = "senha") String senha) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), LOGIN_REST_CONTROLLER, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        try {
            WSPessoa pessoa = getPortalContribunteFacade().fazerLoginPortalWeb(senha, cpf);
            singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), LOGIN_REST_CONTROLLER, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.LOGIN, cpf);
            return ResponseEntity.ok().body(pessoa);
        } catch (Exception e) {
            getLogger().error("Exception {}", e.getMessage() + " => " + cpf);
            singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), LOGIN_REST_CONTROLLER, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/login-app",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UsuarioPortalWebDTO> realizarLoginApp(@RequestParam(value = "cpf", required = true) String cpf,
                                                                @RequestParam(value = "senha", required = false) String senha) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), LOGIN_REST_CONTROLLER, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        try {
            UsuarioPortalWebDTO usuarioWeb = getPortalContribunteFacade().fazerLoginPortalWebApp(senha, cpf);
            singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), LOGIN_REST_CONTROLLER, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.LOGIN, cpf);
            return new ResponseEntity<>(usuarioWeb, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception {}", e.getMessage() + " => " + cpf);
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), LOGIN_REST_CONTROLLER, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/cpf-disponivel",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> verificaDisponibilidadeCPF(@RequestParam(value = "cpf") String cpf) {
        try {
            ConsultaCPF consultaCPF = getPortalContribunteFacade().verificarDisponibilidadeCPF(cpf);
            return ResponseEntity.ok().body(consultaCPF);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/cpf-disponivel-para-primeiro-acesso",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ConsultaCPF> verificaDisponibilidadeCPFParaPrimeiroAcesso(@RequestParam(value = "cpf", required = true) String cpf) {
        try {
            ConsultaCPF consultaCPF = getPortalContribunteFacade().verificarDisponibilidadeCPFParaPrimeiroAcesso(cpf);
            return new ResponseEntity<>(consultaCPF, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/buscar-consulta-cpf-valido-credor",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ConsultaCPF> buscarConsultaCPFValidoCredor(@RequestParam(value = "cpf", required = true) String cpf, @RequestParam(value = "email", required = true) String email) {
        try {
            ConsultaCPF consultaCPF = getPortalContribunteFacade().buscarConsultaCPFValidoCredor(cpf, email);
            return new ResponseEntity<>(consultaCPF, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/esqueceu-senha",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> esqueciSenha(@RequestParam(value = "cpf") String cpf) {
        try {
            return ResponseEntity.ok().body(getPortalContribunteFacade().alterarLoginPortalWeb(cpf));
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body("Não foi possivel concluir a solicitação de uma nova senha.");
        }
    }

    @RequestMapping(value = "/alterar-senha-app",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> alterarSenhaApp(@RequestParam(value = "cpf") String cpf, @RequestParam(value = "novaSenha") String novaSenha) {
        try {
            getPortalContribunteFacade().alterarSenhaPortalWeb(cpf, novaSenha);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/criar-usuario",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSPessoa> criarLoginPortalWeb(@RequestParam(value = "cpf") String cpf,
                                                        @RequestParam(value = "email") String email) {
        try {
            WSPessoa wsPessoa = getPortalContribunteFacade().getUsuarioWebFacade().criarLoginPortalWeb(email, cpf);
            return new ResponseEntity<>(wsPessoa, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/desabilitar-usuario",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity desabilitarLoginPortalWeb(@RequestParam(value = "cpf") String cpf) {
        try {
            getPortalContribunteFacade().desabilitarLoginPortalWeb(cpf);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/criar-usuario",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> criarLoginPortalWeb(@RequestBody UsuarioPortalWebDTO usuarioPortalWebDTO) {
        try {
            getPortalContribunteFacade().getUsuarioWebFacade().criarLoginPortalWeb(usuarioPortalWebDTO);
            return ResponseEntity.ok().body("Foi enviado para o seu email a senha temporária para acesso.");
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @RequestMapping(value = "/get-quantidade-acessos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getAnoEmissaoIPTU() {
        try {
            Long quantidadeAcessosPortal = singletonMetricas.getQuantidadeAcessosPortal();
            return new ResponseEntity<>(quantidadeAcessosPortal, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(0L, HttpStatus.OK);
    }

    @RequestMapping(value = "/termo-uso/vigente",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TermoUso> buscarTermoUsoVigente(@RequestParam String tipo) {
        try {
            TermoUso dto = getTermoUsoFacade().buscarTermoUsoVigente(tipo);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/termo-uso/para-aceite",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> hasTermoUsoParaAceite(@RequestBody UsuarioPortalWebDTO usuario) {
        try {
            Boolean jaAceito = getTermoUsoFacade().hasTermoParaAceite(usuario);
            return new ResponseEntity<>(jaAceito, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }

    @RequestMapping(value = "/termo-uso/aceitar",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> aceitarTermoUso(@RequestBody AceiteTermoUsoDTO aceite) {
        try {
            getTermoUsoFacade().aceitarTermoUso(aceite.getTermoUsoId(), aceite.getUsuario());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
