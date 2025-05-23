package br.com.webpublico.nfse.rest;


import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.negocios.CidadeFacade;
import br.com.webpublico.nfse.domain.dtos.MunicipioNfseDTO;
import br.com.webpublico.nfse.util.PaginationUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;

@RequestMapping("/nfse")
@Controller
public class CidadeResource implements Serializable {
    private CidadeFacade cidadeFacade;
    private final Logger logger = LoggerFactory.getLogger(CidadeResource.class);

    @PostConstruct
    public void init() {
        try {
            cidadeFacade = (CidadeFacade) new InitialContext().lookup("java:module/CidadeFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/cidades-by-uf",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public <T> ResponseEntity<List<T>> recuperarCidadesPorUf(@RequestParam(value = "uf") String uf) {
        List<Cidade> cidades = cidadeFacade.listaCidadesPorUF(uf);
        return new ResponseEntity(cidades, HttpStatus.OK);
    }

    @RequestMapping(value = "/cidades-by-filter",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MunicipioNfseDTO>> buscarCidadesComCodigoPorParametro(Pageable pageable,
                                                                                     @RequestParam(value = "param", required = false) String param) throws URISyntaxException, UnsupportedEncodingException {
        if (!Strings.isNullOrEmpty(param)) {
            param = URLDecoder.decode(param, "UTF-8");
            param = URLDecoder.decode(param, "UTF-8");
        }
        Page<Cidade> cidades = cidadeFacade.buscarListarApenasComCodigo(pageable, param);
        List<MunicipioNfseDTO> dtos = Lists.newArrayList();
        for (Cidade cidade : cidades) {
            dtos.add(cidade.toNfseDto());
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(cidades, "", pageable.getPageNumber(), pageable.getPageSize());

        return new ResponseEntity(dtos, headers, HttpStatus.OK);
    }
}
