package br.com.webpublico.nfse.rest;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.nfse.domain.dtos.NfseArquivo;
import br.com.webpublico.nfse.util.PaginationUtil;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

@RequestMapping("/nfse")
@Controller
public class ArquivoResource implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ArquivoFacade arquivoFacade;

    @PostConstruct
    public void init() {
        try {
            arquivoFacade = (ArquivoFacade) Util.getFacadeViaLookup("java:module/ArquivoFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/arquivo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<NfseArquivo> uploadArquivo(@RequestBody NfseArquivo arquivoUploaded) {
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao(arquivoUploaded.getDescricao());
        arquivo.setMimeType(arquivoUploaded.getMimeType());
        arquivo.setNome(arquivoUploaded.getNome());
        arquivo.setTamanho(arquivoUploaded.getTamanho());

        try {
            arquivoUploaded.processarInpuStream();
            arquivoFacade.novoArquivo(arquivo, arquivoUploaded.getInputStream());
            return new ResponseEntity<>(new NfseArquivo(arquivo), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/arquivo/{id}")
    public HttpEntity<byte[]> downloadArquivo(@PathVariable Long id) throws IOException {
        try {
            Arquivo arquivo = arquivoFacade.recupera(id);
            byte[] byteFile = arquivoFacade.montarArquivoParaDownload(arquivo);
            String fileName = arquivo.getNome() != null && arquivo.getNome().contains(".") ?
                arquivo.getNome() : arquivo.getNome()
                + arquivoFacade.getExtension(arquivo.getMimeType());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            return new HttpEntity<>(byteFile, httpHeaders);
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpEntity(HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/arquivo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Arquivo>> recuperarArquivos(Pageable pageable, @RequestParam(value = "table") String table, @RequestParam(value = "searchFields", required = false) String searchFields, @RequestParam(value = "query", required = false) String query) throws URISyntaxException, IOException {
        Page<Arquivo> retorno = arquivoFacade.recuperarArquivosPaginado(pageable, query);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());

        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }
}
