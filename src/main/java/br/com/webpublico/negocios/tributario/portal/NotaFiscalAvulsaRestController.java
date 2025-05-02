package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.tributario.EmissaoNotaAvulsaDTO;
import br.com.webpublico.tributario.GeraAliquotaNotaAvulsaDTO;
import br.com.webpublico.tributario.NovoTomadorDTO;
import br.com.webpublico.ws.model.WSNFSAvulsa;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/integracao/tributario/nfs-avulsas")
public class NotaFiscalAvulsaRestController extends PortalRestController {


    @RequestMapping(value = "/get-nota-fiscal-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSNFSAvulsa>> buscarNotasPorCpfCnpj(@RequestParam(value = "cpf", required = true) String cpf,
                                                                   @RequestParam(value = "first", required = false) Integer first,
                                                                   @RequestParam(value = "max", required = false) Integer max) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.NOTA_FISCAL_AVULSA, cpf);
            sistemaService.setUsuarioAlternativo(cpf);
            List<WSNFSAvulsa> notas = getPortalContribunteFacade().buscarNotasPortal(cpf, first, max);
            return new ResponseEntity<>(notas, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/get-nota-fiscal-por-id/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSNFSAvulsa> getNotaPorId(@PathVariable("id") Long id) {
        try {
            WSNFSAvulsa notas = getPortalContribunteFacade().buscarNotasPortal(id);
            return new ResponseEntity<>(notas, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/count-nota-fiscal-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> contarNotasPorCpfCnpj(@RequestParam(value = "cpf", required = true) String cpf) {
        try {
            sistemaService.setUsuarioAlternativo(cpf);
            Integer notas = getPortalContribunteFacade().contarNotasPortal(cpf);
            return new ResponseEntity<>(notas, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/gerar-nota",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public byte[] geraRelatorio(@RequestBody WSNFSAvulsa nota) {
        try {
            sistemaService.setUsuarioAlternativo(nota.getCpfCnpj());
            return getPortalContribunteFacade().gerarImpressaoNotaAvulsa(nota.getId());
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/emitir-nota",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSNFSAvulsa> emitirNotaAvulsa(@RequestBody EmissaoNotaAvulsaDTO dto) {
        try {
            sistemaService.setUsuarioAlternativo(dto.getCpfPrestador());
            WSNFSAvulsa wsInfo = getPortalContribunteFacade().gerarNotaFiscaoAvulsaPortal(dto);
            if (wsInfo != null) {
                return new ResponseEntity<>(wsInfo, HttpStatus.OK);
            }
        } catch (Exception e) {
            getLogger().error("Exception. ", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/buscar-informacoes-aliquota",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GeraAliquotaNotaAvulsaDTO> buscarInformacoesAliquota(@RequestParam(value = "cpf") String cpf) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.NOTA_FISCAL_AVULSA, cpf);
            sistemaService.setUsuarioAlternativo(cpf);
            GeraAliquotaNotaAvulsaDTO prestador = getPortalContribunteFacade().buscarInformacoesAliquota(cpf);
            if (prestador != null) {
                return new ResponseEntity<>(prestador, HttpStatus.OK);
            }
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/buscar-informacoes-tomador",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<NovoTomadorDTO> buscarNomePessoaPorCpfCnpj(@RequestParam(value = "cpfCnpj") String cpfCnpj) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.NOTA_FISCAL_AVULSA, cpfCnpj);
            sistemaService.setUsuarioAlternativo(cpfCnpj);
            NovoTomadorDTO tomador = getPortalContribunteFacade().buscarInformacoesTomador(cpfCnpj);
            if (tomador != null) {
                return new ResponseEntity<>(tomador, HttpStatus.OK);
            }
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
