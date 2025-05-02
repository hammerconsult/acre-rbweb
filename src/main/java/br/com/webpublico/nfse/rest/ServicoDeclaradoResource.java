package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.domain.LoteDocumentoRecebido;
import br.com.webpublico.nfse.domain.TipoDocumentoServicoDeclarado;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.nfse.facades.ServicoDeclaradoFacade;
import br.com.webpublico.nfse.util.PaginationUtil;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

@RequestMapping("/nfse/servico-declarado")
@Controller
public class ServicoDeclaradoResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(ServicoDeclaradoResource.class);
    private ServicoDeclaradoFacade servicoDeclaradoFacade;

    @PostConstruct
    public void init() {
        try {
            servicoDeclaradoFacade = (ServicoDeclaradoFacade) new InitialContext().lookup("java:module/ServicoDeclaradoFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity delete(@RequestParam(value = "id") Long id) {
        try {
            servicoDeclaradoFacade.delete(id);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(null, HttpStatus.OK);
    }


    @RequestMapping(value = "/por-competencia-empresa", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NotaFiscalSearchDTO>> recuperarNotaPorEmpresaCompetenciaSemDeclarar(@RequestParam(value = "empresaId") Long empresaId,
                                                                                                   @RequestParam(value = "mes") int mes,
                                                                                                   @RequestParam(value = "ano") int ano) {
        List<NotaFiscalSearchDTO> retorno = servicoDeclaradoFacade.buscarPorEmpresaCompetencia(empresaId, mes, ano);
        return new ResponseEntity(retorno, HttpStatus.OK);
    }


    @RequestMapping(value = "/importar-xml", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<LoteImportacaoDocumentoRecebidoNfseDTO> importar(@RequestBody LoteImportacaoDocumentoRecebidoNfseDTO lote) {
        try {
            LoteDocumentoRecebido loteDocs = servicoDeclaradoFacade.criarLote(lote);
            return new ResponseEntity(loteDocs.toNfseDto(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/lote-por-empresa", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LoteImportacaoDocumentoRecebidoNfseDTO>> getAllLotes(Pageable pageable,
                                                                                    @RequestParam(value = "filtro") String filtro,
                                                                                    @RequestParam(value = "empresaId") Long empresaId)
        throws URISyntaxException {
        Page<LoteImportacaoDocumentoRecebidoNfseDTO> retorno = servicoDeclaradoFacade.buscarLotesPorEmpresa(pageable, empresaId, filtro);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/lote/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoteImportacaoDocumentoRecebidoNfseDTO> getByLoteId(@PathVariable("id") Long id) {
        return new ResponseEntity(servicoDeclaradoFacade.recuperarLote(id).toNfseDto(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tipos-documentos-servico-declarado", method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TipoDocumentoServicoDeclaradoNfseDTO>> getAllTipoServicoDeclarado() {
        List<TipoDocumentoServicoDeclarado> tipos = servicoDeclaradoFacade.findAllTiposServicosDeclarados();
        List<TipoDocumentoServicoDeclaradoNfseDTO> dtos = Lists.newArrayList();
        for (TipoDocumentoServicoDeclarado tipo : tipos) {
            dtos.add(tipo.toNfseDto());
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/consultar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServicoDeclaradoSearchNfseDTO>> buscarServicosDeclarados(@RequestBody ConsultaServicoDeclaradoDTO consultaServicoDeclarado)
        throws Exception {
        Pageable pageable = PaginationUtil.generatePageRequest(consultaServicoDeclarado.getOffset(),
            consultaServicoDeclarado.getLimit());

        Page<ServicoDeclaradoSearchNfseDTO> retorno = servicoDeclaradoFacade.consultarServicosDeclarados(pageable,
            consultaServicoDeclarado.getParametrosQuery(), consultaServicoDeclarado.getOrderBy());

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());

        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/por-lote", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServicoDeclaradoSearchDTO>> buscarServicosDeclaradosPorLote(Pageable pageable,
                                                                                           @RequestParam(value = "filtro") String filtro,
                                                                                           @RequestParam(value = "empresaId") Long empresaId,
                                                                                           @RequestParam(value = "loteId") Long loteId) throws URISyntaxException {
        Page<ServicoDeclaradoSearchDTO> retorno = servicoDeclaradoFacade.buscarServicosDeclaradosPorLote(pageable, empresaId, filtro, loteId);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }
}
