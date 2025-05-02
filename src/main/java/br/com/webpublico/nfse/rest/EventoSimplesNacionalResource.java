package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.domain.LinhaEventoSimplesNacional;
import br.com.webpublico.nfse.domain.dtos.LinhaEventoSimplesNacionalNfseDTO;
import br.com.webpublico.nfse.facades.LinhaEventoSimplesNacionalFacade;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;

@RequestMapping("/nfse")
@Controller
public class EventoSimplesNacionalResource implements Serializable {

    private LinhaEventoSimplesNacionalFacade linhaEventoSimplesNacionalFacade;
    private final Logger logger = LoggerFactory.getLogger(EventoSimplesNacionalResource.class);

    @PostConstruct
    public void init() {
        try {
            linhaEventoSimplesNacionalFacade = (LinhaEventoSimplesNacionalFacade) new InitialContext().lookup("java:module/LinhaEventoSimplesNacionalFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/eventos-por-empresa", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LinhaEventoSimplesNacionalNfseDTO>> buscarEventosPorEmpresa(@RequestParam(value = "prestadorId") Long prestadorId,
                                                                                           @RequestParam(value = "filtro") String filtro) throws Exception {

        List<LinhaEventoSimplesNacionalNfseDTO> toReturn = Lists.newArrayList();
        List<LinhaEventoSimplesNacional> linhasEventoSimpolesNacional = linhaEventoSimplesNacionalFacade.buscarLinhasEventoSimplesNacionalPorEmpresaAndFiltro(prestadorId, filtro);
        if (linhasEventoSimpolesNacional != null) {
            for (LinhaEventoSimplesNacional linhaEventoSimplesNacional : linhasEventoSimpolesNacional) {
                toReturn.add(linhaEventoSimplesNacional.toNfseDto());
            }
        }
        return new ResponseEntity(toReturn, HttpStatus.OK);
    }
}
