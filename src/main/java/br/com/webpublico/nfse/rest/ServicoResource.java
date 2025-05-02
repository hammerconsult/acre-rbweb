package br.com.webpublico.nfse.rest;


import br.com.webpublico.entidades.Servico;
import br.com.webpublico.negocios.ServicoFacade;
import br.com.webpublico.nfse.domain.dtos.ServicoNfseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

@RequestMapping("/nfse")
@Controller
public class ServicoResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(ServicoResource.class);
    private ServicoFacade servicoFacade;

    @PostConstruct
    public void init() {
        try {
            servicoFacade = (ServicoFacade) new InitialContext().lookup("java:module/ServicoFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/servico-por-codigo/{codigo}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServicoNfseDTO> recuperar(@PathVariable("codigo") String codigo) {
        Servico servico = servicoFacade.buscarPorCodigo(codigo, Boolean.TRUE);
        return new ResponseEntity<>(servico != null ? servico.toNfseDto() : null, HttpStatus.OK);
    }


    @RequestMapping(value = "/servicos-por-cnae-prestador", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServicoNfseDTO>> recuperar(@RequestParam(value = "cnaeId") Long cnaeId,
                                                          @RequestParam(value = "prestadorId") Long prestadorId) {
//        List<Servico> servicos = servicoFacade.buscarServicoPorCnaeAndCadastroEconomico(prestadorId, cnaeId);
        return new ResponseEntity(HttpStatus.OK);
    }


    @RequestMapping(value = "/servico-por-cnae/{filtro}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServicoNfseDTO>> buscarServicoPorCnae(Pageable pageable, @PathVariable("filtro") String filtro) throws URISyntaxException {
//        Page<ServicoNfseDTO> retorno = servicoFacade.buscarServicoPorCnae(pageable, filtro);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(HttpStatus.OK);
    }
}
