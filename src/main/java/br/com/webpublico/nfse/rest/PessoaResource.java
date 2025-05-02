package br.com.webpublico.nfse.rest;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.nfse.domain.dtos.ImagemUsuarioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.PessoaNfseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.Serializable;

/**
 * Created by daniel on 26/04/17.
 */
@Controller
@RequestMapping("/nfse")
public class PessoaResource implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PessoaResource.class);
    private UsuarioWebFacade usuarioWebFacade;
    private PessoaFacade pessoaFacade;

    @PostConstruct
    public void init() {
        try {
            usuarioWebFacade = (UsuarioWebFacade) new InitialContext().lookup("java:module/UsuarioWebFacade");
            pessoaFacade = (PessoaFacade) new InitialContext().lookup("java:module/PessoaFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/pessoa_do_usuario",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PessoaNfseDTO> getPorUsuarioLogado(@RequestParam(value = "login") String login) {
        logger.debug("REST request to get Pessoa Do Usuário ");
        try {
            Pessoa pessoa = usuarioWebFacade.buscarPessoaPorLoginUsuario(login);
            return new ResponseEntity<>((PessoaNfseDTO) pessoa.toNfseDto(), HttpStatus.OK);
        } catch (Exception e) {
            logger.debug("Não foi encontrada nenhuma pessoa, Exception {} ", e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/pessoa_por_cpfCnpj",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PessoaNfseDTO> getPorCpfCnpj(@RequestParam(value = "cpfCnpj") String cpfCnpj) {
        logger.debug("REST request to get Pessoa Do Usuário ");
        try {
            Pessoa pessoa = pessoaFacade.buscarPessoaComDepedenciasPorCpfOrCnpj(cpfCnpj);
            return new ResponseEntity<>(pessoa.toNfseDto(), HttpStatus.OK);
        } catch (Exception e) {
            logger.debug("Não foi encontrada nenhuma pessoa, Exception {} ", e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PessoaNfseDTO> getPorId(@RequestParam(value = "id") Long id) {
        logger.debug("REST request to get Pessoa Do Usuário ");
        try {
            Pessoa pessoa = pessoaFacade.recuperar(id);
            return new ResponseEntity<>((PessoaNfseDTO) pessoa.toNfseDto(), HttpStatus.OK);
        } catch (Exception e) {
            logger.debug("Não foi encontrada nenhuma pessoa, Exception {} ", e.getMessage());
        }
        return null;
    }


    @RequestMapping(value = "/pessoa",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PessoaNfseDTO> save(@RequestBody PessoaNfseDTO pessoaNfseDTO) {
        logger.debug("REST request to get Pessoa Do Usuário ");
        try {
            PessoaNfseDTO retorno = pessoaFacade.atualizarDadosPessoa(pessoaNfseDTO);
            return new ResponseEntity<>(retorno, HttpStatus.OK);
        } catch (Exception e) {
            logger.debug("Não foi encontrada nenhuma pessoa, Exception {} ", e.getMessage());
        }
        return null;
    }


    @RequestMapping(value = "/pessoa/imagem",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImagemUsuarioNfseDTO> getImagemUsuario(@RequestParam("id") Long id) {
        logger.debug("REST request to get Imagem Do Usuário ");
        try {
            return new ResponseEntity<>(pessoaFacade.buscarImagemDaPessoa(id), HttpStatus.OK);
        } catch (Exception e) {
            logger.debug("Não foi encontrada nenhuma pessoa, Exception {} ", e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/pessoa/imagem",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveImagemUsuario(@RequestBody ImagemUsuarioNfseDTO imagemDto) {
        logger.debug("REST request to POST Pessoa Do Usuário ");
        try {
            pessoaFacade.atualizarImagemPessoa(imagemDto);
        } catch (Exception e) {
            logger.debug("Não foi encontrada nenhuma pessoa, Exception {} ", e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
