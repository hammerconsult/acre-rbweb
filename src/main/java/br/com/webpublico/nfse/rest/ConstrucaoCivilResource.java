package br.com.webpublico.nfse.rest;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.nfse.domain.ConstrucaoCivil;
import br.com.webpublico.nfse.domain.dtos.ConstrucaoCivilNfseDTO;
import br.com.webpublico.nfse.facades.ConstrucaoCivilFacade;
import br.com.webpublico.nfse.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

@RequestMapping("/nfse/construcao-civil")
@Controller
public class ConstrucaoCivilResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(ConstrucaoCivilResource.class);

    private ConstrucaoCivilFacade construcaoCivilFacade;
    private CadastroEconomicoFacade cadastroEconomicoFacade;

    @PostConstruct
    public void init() {
        try {
            construcaoCivilFacade = (ConstrucaoCivilFacade) new InitialContext().lookup("java:module/ConstrucaoCivilFacade");
            cadastroEconomicoFacade = (CadastroEconomicoFacade) new InitialContext().lookup("java:module/CadastroEconomicoFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConstrucaoCivilNfseDTO> getById(@PathVariable Long id) {
        ConstrucaoCivil construcaoCivil = this.construcaoCivilFacade.recuperar(id);
        return new ResponseEntity(construcaoCivil.toNfseDto(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteById(@PathVariable Long id) {
        try {
            construcaoCivilFacade.remover(construcaoCivilFacade.recuperar(id));
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/from-prestador", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ConstrucaoCivilNfseDTO>> buscarConstrucoesCivil(Pageable pageable,
                                                                               @RequestParam(value = "filtro") String filtro,
                                                                               @RequestParam(value = "prestadorId") Long prestadorId) throws Exception {
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperar(prestadorId);
        Page<ConstrucaoCivilNfseDTO> obras = construcaoCivilFacade.buscarConstrucoesCivil(pageable, cadastroEconomico, filtro);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(obras, "/from-prestador", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(obras.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConstrucaoCivilNfseDTO> salvar(@RequestBody ConstrucaoCivilNfseDTO construcaoCivilNfseDTO) {
        try {
            construcaoCivilNfseDTO = construcaoCivilFacade.salvarByDTO(construcaoCivilNfseDTO);
            return new ResponseEntity(construcaoCivilNfseDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/by-art", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConstrucaoCivilNfseDTO> getByArt(@RequestParam String art) throws Exception {
        ConstrucaoCivil construcaoCivil = this.construcaoCivilFacade.buscarConstrucaoCivilPorArt(art);
        return new ResponseEntity(construcaoCivil != null ? construcaoCivil.toNfseDto() : null, HttpStatus.OK);
    }

}
