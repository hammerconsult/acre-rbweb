package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.UF;
import br.com.webpublico.nfse.domain.dtos.MunicipioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.UFNfseDTO;
import com.google.common.collect.Lists;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/integracao/tributario")
public class CidadeRestController extends PortalRestController {


    @RequestMapping(value = "/ufs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UFNfseDTO>> recuperarUfsIss() {
        List<UF> ufs = getPortalContribunteFacade().getUfFacade().listaUFNaoNula();
        List<UFNfseDTO> toReturn = Lists.newArrayList();
        for (UF uf : ufs) {
            toReturn.add(uf.toNfseDto());
        }
        return new ResponseEntity(toReturn, HttpStatus.OK);
    }


    @RequestMapping(value = "/cidades-by-uf",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MunicipioNfseDTO>> recuperarCidadesPorUfIss(@RequestParam(value = "uf") String uf) {
        List<Cidade> cidades = getPortalContribunteFacade().getCidadeFacade().listaCidadesPorUF(uf);
        List<MunicipioNfseDTO> toReturn = Lists.newArrayList();
        for (Cidade cidade : cidades) {
            toReturn.add(cidade.toNfseDto());
        }
        return new ResponseEntity(toReturn, HttpStatus.OK);
    }

    @RequestMapping(value = "/uf-por-id",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UFNfseDTO> recuperarUf(@RequestParam Long id) {
        UF uf = getPortalContribunteFacade().getUfFacade().recuperar(id);
        return new ResponseEntity(uf.toNfseDto(), HttpStatus.OK);
    }

    @RequestMapping(value = "/cidade-por-id",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MunicipioNfseDTO> recuperarCidade(@RequestParam Long id) {
        Cidade cidade = getPortalContribunteFacade().getCidadeFacade().recuperar(id);
        return new ResponseEntity(cidade.toNfseDto(), HttpStatus.OK);
    }

    @RequestMapping(value = "/cidade-por-codigo-ibge",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MunicipioNfseDTO> recuperarCidadePorCodigoIbge(@RequestParam String codigoIbge) {
        Cidade cidade = getPortalContribunteFacade().getCidadeFacade().buscarPorCodigoIbge(codigoIbge);
        return new ResponseEntity(cidade.toNfseDto(), HttpStatus.OK);
    }

    @RequestMapping(value = "/uf-por-uf",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UFNfseDTO> recuperarUfPorUf(@RequestParam(value = "uf") String uf) {
        UF ufEntity = getPortalContribunteFacade().getUfFacade().recuperaUfPorUf(uf);
        return new ResponseEntity(ufEntity.toNfseDto(), HttpStatus.OK);
    }
}
