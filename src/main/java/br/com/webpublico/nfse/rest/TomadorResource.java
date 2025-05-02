package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.domain.TomadorServicoNfse;
import br.com.webpublico.nfse.domain.dtos.TomadorServicoDTO;
import br.com.webpublico.nfse.facades.TomadorServicoNfseFacade;
import br.com.webpublico.nfse.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by daniel on 26/04/17.
 */
@Controller
@RequestMapping("/nfse")
public class TomadorResource implements Serializable {


    private static final Logger log = LoggerFactory.getLogger(TomadorResource.class);
    private TomadorServicoNfseFacade tomadorServicoNfseFacade;

    @PostConstruct
    public void init() {
        try {
            tomadorServicoNfseFacade = (TomadorServicoNfseFacade) new InitialContext().lookup("java:module/TomadorServicoNfseFacade");
        } catch (Exception ex) {
            log.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/tomador/buscar-por-cpfCnpj-and-prestadorId",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TomadorServicoDTO> getPorCpfCnpj(@RequestParam(value = "cpfCnpj") String cpfCnpj,
                                                           @RequestParam(value = "idPrestador") Long idPrestador) {
        log.debug("REST request to get Pessoa Do Usuário ");
        try {
            TomadorServicoNfse retorno = tomadorServicoNfseFacade.buscarCpfCnpjPrestadorId(cpfCnpj, idPrestador);
            log.debug("Retornando o cara ", retorno);
            return new ResponseEntity<>((TomadorServicoDTO) retorno.toNfseDto(), HttpStatus.OK);
        } catch (Exception e) {
            log.debug("Não foi encontrada nenhuma pessoa, Exception {} ", e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/tomador/listar",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TomadorServicoDTO>> pesquisaGenerica(Pageable pageable,
                                                                    @RequestParam(value = "query", required = false) String query,
                                                                    @RequestParam(value = "prestadorId", required = false) Long prestadorId)
        throws URISyntaxException, IOException {
        if (query == null) {
            query = "";
        }
        List<TomadorServicoDTO> retorno = tomadorServicoNfseFacade.listaTomadorServicoNfsePorCMCNomeRazaoSocialCPFCNPJ(query, prestadorId, pageable);
        Integer totalCadastros = tomadorServicoNfseFacade.contarTomadorServicoNfsePorCMCNomeRazaoSocialCPFCNPJ(query, prestadorId);

        Page returnPage = new PageImpl(retorno, pageable, totalCadastros);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(returnPage, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(returnPage.getContent(), headers, HttpStatus.OK);
    }


}
