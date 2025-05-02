package br.com.webpublico.nfse.rest;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.nfse.domain.dtos.CadastroImobiliarioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.CadastroImobiliarioSearchNfseDTO;
import br.com.webpublico.nfse.util.PaginationUtil;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

@RequestMapping("/nfse/cadastro-imobiliario")
@Controller
public class CadastroImobiliarioResource implements Serializable {
    private final Logger log = LoggerFactory.getLogger(CadastroImobiliarioResource.class);
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;

    @PostConstruct
    public void init() {
        try {
            cadastroImobiliarioFacade = (CadastroImobiliarioFacade) Util.getFacadeViaLookup("java:module/CadastroImobiliarioFacade");
        } catch (Exception ex) {
            log.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/{id}",
        method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CadastroImobiliarioNfseDTO> findById(@PathVariable Long id) {
        CadastroImobiliario cadastroImobiliario = cadastroImobiliarioFacade.recuperar(id);
        return new ResponseEntity(cadastroImobiliario != null ? cadastroImobiliario.toNfseDto() : null, HttpStatus.OK);
    }

    @RequestMapping(value = "/by-inscricao",
        method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CadastroImobiliarioNfseDTO> findByInscricao(@RequestParam String inscricao) {
        CadastroImobiliario cadastroImobiliario = cadastroImobiliarioFacade.recuperaPorInscricao(inscricao);
        return new ResponseEntity(cadastroImobiliario != null ? cadastroImobiliario.toNfseDto() : null, HttpStatus.OK);
    }

    @RequestMapping(value = "/search",
        method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CadastroImobiliarioSearchNfseDTO>> search(Pageable pageable,
                                                                         @RequestParam String search) throws Exception {
        Page<CadastroImobiliarioSearchNfseDTO> page = cadastroImobiliarioFacade.buscarCadastroImobiliarioSearch(pageable, search);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/from-prestador", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(page.getContent(), headers, HttpStatus.OK);
    }
}
