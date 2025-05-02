package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.domain.SolicitacaoRPS;
import br.com.webpublico.nfse.domain.dtos.SolicitacaoRPSNfseDTO;
import br.com.webpublico.nfse.facades.SolicitacaoRPSFacade;
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

@RequestMapping("/nfse")
@Controller
public class SolicitacaoRPSResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(SolicitacaoRPSResource.class);
    private SolicitacaoRPSFacade solicitacaoRPSFacade;

    @PostConstruct
    public void init() {
        try {
            solicitacaoRPSFacade = (SolicitacaoRPSFacade) new InitialContext().lookup("java:module/SolicitacaoRPSFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/solicitacao-rps", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<SolicitacaoRPSNfseDTO> getSolicitacaoRPS(@RequestParam(value = "id") Long id, @RequestParam(value = "prestadorId") Long prestadorId) {
        try {
            SolicitacaoRPS recuperar = solicitacaoRPSFacade.recuperar(id, prestadorId);
            if (recuperar != null) {
                return new ResponseEntity<>((SolicitacaoRPSNfseDTO) recuperar.toNfseDto(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/solicitacao-rps", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<SolicitacaoRPSNfseDTO> createSolicitacaoRPS(@RequestBody SolicitacaoRPSNfseDTO dto) {
        try {
            /*if (solicitacaoRPSFacade.hasSolicitacaoSolicitacaoRPSAguardando(dto.getPrestador().getId())) {
                return new ResponseEntity<>(null, setMessageError("Este Cadastro Mobiliário já possui uma solicitação de RPS aguardando deferimento. Não é possível solicitar nova."), HttpStatus.OK);
            }*/
            logger.debug("createSolicitacaoRPS of method POST {}", dto);
            solicitacaoRPSFacade.createAndSave(dto);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/solicitacao-rps", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<SolicitacaoRPSNfseDTO> updateSolicitacaoRPS(@RequestBody SolicitacaoRPSNfseDTO aidfe) {
        try {
            solicitacaoRPSFacade.atualizar(aidfe);
            return new ResponseEntity<>(aidfe, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/solicitacao-rps-por-empresa", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SolicitacaoRPSNfseDTO>> recuperarSolicitacaoRPSPorEmpresa(Pageable pageable,
                                                                                         @RequestParam(value = "filtro") String filtro,
                                                                                         @RequestParam(value = "prestadorId") Long prestadorId)
        throws URISyntaxException {
        Page<SolicitacaoRPSNfseDTO> retorno = solicitacaoRPSFacade.buscarSolicitacaoRPSPorEmpresa(pageable, prestadorId, filtro);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/solicitacao-rps-quantidade-por-empresa", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> buscarTotalQuantidadeSolicitadaEmpresa(@RequestParam(value = "prestadorId") Long prestadorId) throws URISyntaxException {
        Integer retorno = solicitacaoRPSFacade.buscarTotalQuantidadeSolicitadaEmpresa(prestadorId);
        return new ResponseEntity(retorno, HttpStatus.OK);
    }


    @RequestMapping(value = "/recuperar-solicitacao-rps-por-empresa-e-situacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SolicitacaoRPSNfseDTO>> recuperarSolicitacaoRPSPorEmpresaESituacao(Pageable pageable,
                                                                                                  @RequestParam(value = "prestadorId") Long prestadorId,
                                                                                                  @RequestParam(value = "situacao") String situacao)
        throws URISyntaxException {
        /*Page<Aidfe> retorno = aidfeService.recuperarAidfePorEmpresaESituacao(pageable, prestadorId, situacao);
        List<AidfeNfseDTO> dtos = Lists.newArrayList();
        for (Aidfe aidfe : retorno.getContent()) {
            dtos.add((AidfeNfseDTO) aidfe.toNfseDto());
        }*/
        List<SolicitacaoRPSNfseDTO> dtos = Lists.newArrayList();
        Page<SolicitacaoRPSNfseDTO> retorno = null;
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(dtos, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/recuperar-solicitacao-rps-por-situacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SolicitacaoRPSNfseDTO>> recuperarSolicitacaoRPSPorSituacao(Pageable pageable, @RequestParam(value = "situacao") String situacao) throws URISyntaxException {
        /*Page<Aidfe> retorno = aidfeService.recuperarAidfePorSituacao(pageable, situacao);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        List<AidfeNfseDTO> dtos = Lists.newArrayList();
        for (Aidfe aidfe : retorno.getContent()) {
            dtos.add((AidfeNfseDTO) aidfe.toNfseDto());
        }*/
        List<SolicitacaoRPSNfseDTO> dtos = Lists.newArrayList();
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(null, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(dtos, headers, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/solicitacao-rps/{id}")
    public ResponseEntity<SolicitacaoRPSNfseDTO> deleteAssunto(@PathVariable("id") Long id) {
        try {
            SolicitacaoRPS recuperar = solicitacaoRPSFacade.recuperar(id);
            solicitacaoRPSFacade.remover(recuperar);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }
}
