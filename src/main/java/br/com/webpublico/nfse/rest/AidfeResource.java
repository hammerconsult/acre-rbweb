package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.domain.Aidfe;
import br.com.webpublico.nfse.domain.SituacaoAidfe;
import br.com.webpublico.nfse.domain.dtos.AidfeNfseDTO;
import br.com.webpublico.nfse.facades.AidfeFacade;
import br.com.webpublico.nfse.util.PaginationUtil;
import br.com.webpublico.util.Util;
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
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

@RequestMapping("/nfse")
@Controller
public class AidfeResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(AidfeResource.class);
    private AidfeFacade aidfeFacade;

    @PostConstruct
    public void init() {
        try {
            aidfeFacade = (AidfeFacade) Util.getFacadeViaLookup("java:module/AidfeFacade");
        } catch (Exception e) {
            logger.error("Erro ao iniciar AIDFEResource", e);
        }
    }

    @RequestMapping(value = "/aidfe", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AidfeNfseDTO> getAidfe(@RequestParam(value = "id") Long id, @RequestParam(value = "prestadorId") Long prestadorId) {
        try {
            Aidfe recuperar = aidfeFacade.recuperar(id, prestadorId);
            if (recuperar != null) {
                return new ResponseEntity<>((AidfeNfseDTO) recuperar.toNfseDto(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/aidfe", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AidfeNfseDTO> createAidfe(@RequestBody AidfeNfseDTO aidfe) {
        try {
            if (aidfeFacade.hasSolicitacaoAidfeAguardando(aidfe.getPrestadorServicos().getId())) {
                return new ResponseEntity<>(null, setMessageError("Este Cadastro Mobiliário já possui uma AIDF-e aguardando deferimento. Não é possível solicitar nova AIDF-e."), HttpStatus.OK);
            }
            aidfeFacade.createAndSave(aidfe);
            return new ResponseEntity<>(aidfe, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/aidfe", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<AidfeNfseDTO> updateAidfe(@RequestBody AidfeNfseDTO aidfe) {
        try {
            aidfeFacade.atualizar(aidfe);
            return new ResponseEntity<>(aidfe, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/aidfe-por-empresa", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AidfeNfseDTO>> recuperarAidfePorEmpresa(Pageable pageable,
                                                                       @RequestParam(value = "filtro") String filtro,
                                                                       @RequestParam(value = "prestadorId") Long prestadorId)
        throws URISyntaxException {
        Page<AidfeNfseDTO> retorno = aidfeFacade.buscarAidfePorEmpresa(pageable, prestadorId, filtro);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/recuperar-aidfe-por-empresa-e-situacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AidfeNfseDTO>> recuperarAidfePorEmpresaESituacao(Pageable pageable,
                                                                                @RequestParam(value = "prestadorId") Long prestadorId,
                                                                                @RequestParam(value = "situacao") String situacao)
        throws URISyntaxException {
        Page<Aidfe> retorno = aidfeFacade.recuperarAidfePorEmpresaESituacao(pageable, prestadorId, situacao);
        List<AidfeNfseDTO> dtos = Lists.newArrayList();
        for (Aidfe aidfe : retorno.getContent()) {
            dtos.add((AidfeNfseDTO) aidfe.toNfseDto());
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(dtos, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/recuperar-aidfe-por-situacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AidfeNfseDTO>> recuperarAidfePorSituacao(Pageable pageable, @RequestParam(value = "situacao") String situacao) throws URISyntaxException {
        Page<Aidfe> retorno = aidfeFacade.recuperarAidfePorSituacao(pageable, situacao);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        List<AidfeNfseDTO> dtos = Lists.newArrayList();
        for (Aidfe aidfe : retorno.getContent()) {
            dtos.add((AidfeNfseDTO) aidfe.toNfseDto());
        }
        return new ResponseEntity(dtos, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/aidfe-obter-proximo-numero-inicial-por-empresa", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> obterProximoNumeroInicialPorEmpresa(@RequestParam(value = "prestadorId") Long prestadorId) throws URISyntaxException {
        Long numero = aidfeFacade.obterProximoNumeroIncial(prestadorId);
        return new ResponseEntity(numero, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/aidfe/{id}")
    public ResponseEntity<Aidfe> deleteAssunto(@PathVariable("id") Long id) {
        try {
            Aidfe aidfe = aidfeFacade.removerAidfe(id);
            return new ResponseEntity<>(aidfe, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/aidfe-deferir", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<List<Aidfe>> deferirAidfe(@RequestBody Aidfe[] selecionadas) {
        try {
            List<Aidfe> aidfes = aidfeFacade.deferirIndeferir(Arrays.asList(selecionadas), SituacaoAidfe.DEFERIDA);
            return new ResponseEntity<>(aidfes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/aidfe-deferir-parcial", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Aidfe> deferirParcialAidfe(@RequestBody Aidfe aidfe) {
        try {
            aidfe = aidfeFacade.deferirParcialmente(aidfe);
            return new ResponseEntity<>(aidfe, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/aidfe-indeferir", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<List<Aidfe>> indeferirAidfe(@RequestBody Aidfe[] selecionadas) {
        try {
            List<Aidfe> aidfes = aidfeFacade.deferirIndeferir(Arrays.asList(selecionadas), SituacaoAidfe.INDEFERIDA);
            return new ResponseEntity<>(aidfes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }
}
