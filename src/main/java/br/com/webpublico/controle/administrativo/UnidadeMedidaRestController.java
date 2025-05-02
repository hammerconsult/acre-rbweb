package br.com.webpublico.controle.administrativo;

import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.negocios.UnidadeMedidaFacade;
import br.com.webpublico.util.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;

@Controller
@RequestMapping("/administrativo/unidade-medida")
public class UnidadeMedidaRestController {


    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashMap> recuperarUnidadeMedida(@PathVariable Long id) {
        UnidadeMedidaFacade unidadeMedidaFacade = (UnidadeMedidaFacade) Util.getFacadeViaLookup("java:module/UnidadeMedidaFacade");
        UnidadeMedida unidade = unidadeMedidaFacade.recuperar(id);

        LinkedHashMap retorno = new LinkedHashMap();
        retorno.put("id", unidade.getId());
        retorno.put("descricao", unidade.getDescricao());
        retorno.put("sigla", unidade.getSigla() != null ? unidade.getSigla() : "");
        retorno.put("mascaraQuantidadeExemplo", unidade.getMascaraQuantidade() != null ? unidade.getMascaraQuantidade().getExemplo() : "");
        retorno.put("mascaraQuantidadeDescricao", unidade.getMascaraQuantidade() != null ? unidade.getMascaraQuantidade().getDescricaoLonga() : "");
        retorno.put("mascaraValorExemplo", unidade.getMascaraValorUnitario() != null ? unidade.getMascaraValorUnitario().getExemplo() : "");
        retorno.put("mascaraValorDescricao", unidade.getMascaraValorUnitario() != null ? unidade.getMascaraValorUnitario().getDescricaoLonga() : "");

        return new ResponseEntity(retorno, HttpStatus.OK);
    }
}
