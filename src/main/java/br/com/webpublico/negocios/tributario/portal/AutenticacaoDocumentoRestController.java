package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AutenticaAlvara;
import br.com.webpublico.entidadesauxiliares.AutenticaCertidao;
import br.com.webpublico.entidadesauxiliares.AutenticaITBI;
import br.com.webpublico.entidadesauxiliares.AutenticaNFSAvulsa;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.util.Util;
import br.com.webpublico.ws.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

/**
 * Created by Fabio on 14/11/2016.
 */
@Controller
@RequestMapping("/integracao/tributario/autenticacao")
public class AutenticacaoDocumentoRestController extends PortalRestController {

    @RequestMapping(value = "/autenticar-nfsavulsa",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSNFSAvulsa> autenticarNfsAvulsa(@RequestBody WsAutenticaNFSAvulsa autentica) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.AUTENTICIDADE_DOCUMENTOS, autentica.getCpfCnpjUsuario());
            sistemaService.setUsuarioAlternativo(autentica.getCpfCnpjUsuario());
            WSNFSAvulsa wsnfsAvulsa = getPortalContribunteFacade().getConsultaAutenticidadeNFSAvulsaFacade()
                .notaAutenticada(new AutenticaNFSAvulsa(autentica));
            return new ResponseEntity<>(wsnfsAvulsa, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/autenticar-alvara",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSAlvara> autenticarAlvara(@RequestBody WsAutenticaAlvara autentica) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.AUTENTICIDADE_DOCUMENTOS, autentica.getCpfCnpj());
            sistemaService.setUsuarioAlternativo(autentica.getCpfCnpj());
            WSAlvara wsAlvara = getPortalContribunteFacade().getCalculoAlvaraFacade()
                .buscarAlvaraAutenticado(new AutenticaAlvara(autentica));
            return new ResponseEntity<>(wsAlvara, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/autenticar-dispensa-licenciamento",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSAlvara> autenticarDispensaLicenciamento(@RequestBody WsAutenticaAlvara autentica) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.AUTENTICIDADE_DOCUMENTOS, autentica.getCpfCnpj());
            sistemaService.setUsuarioAlternativo(autentica.getCpfCnpj());
            WSAlvara wsAlvara = getPortalContribunteFacade().getCalculoAlvaraFacade()
                .buscarAlvaraAutenticado(new AutenticaAlvara(autentica));
            return new ResponseEntity<>(wsAlvara, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/autenticar-certidao",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSSolicitacaoDocumentoOficial> autenticarCertidao(@RequestBody WsAutenticaCertidao autentica) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.AUTENTICIDADE_DOCUMENTOS, autentica.getCpfCnpj());
            sistemaService.setUsuarioAlternativo(autentica.getCpfCnpj());
            WSSolicitacaoDocumentoOficial wsCertidao = getPortalContribunteFacade().getDocumentoOficialFacade()
                .buscarSocilicitacaoComAutenticidade(new AutenticaCertidao(autentica));
            return new ResponseEntity<>(wsCertidao, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/autenticar-itbi",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSItbi> autenticarItbi(@RequestBody WsAutenticaITBI autentica) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.AUTENTICIDADE_DOCUMENTOS, autentica.getCpfCnpj());
            sistemaService.setUsuarioAlternativo(autentica.getCpfCnpj());
            Exercicio exercicio = getPortalContribunteFacade().getExercicioFacade().getExercicioPorAno(autentica.getAno());

            WSItbi wsItbi = getPortalContribunteFacade().getcalculoITBIFacade()
                .montarWsItbi(exercicio, new AutenticaITBI(autentica));

            return new ResponseEntity<>(wsItbi, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/autenticidade-de-documentos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSSolicitacaoDocumentoOficial> buscarInformacoesDocumento(@PathVariable("id") Long id) {
        try {
            if (getPortalContribunteFacade().getDocumentoOficialFacade().buscarDocumentoPorId(id) != null) {
                WSSolicitacaoDocumentoOficial ws = getPortalContribunteFacade().getDocumentoOficialFacade().buscarDocumentoPorId(id);
                if (ws != null) {
                    return new ResponseEntity<>(ws, HttpStatus.OK);
                }
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/validar-documento-oficial/{chave}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> buscarInformacoesDocumento(@PathVariable("chave") String chave) {
        try {
            if (getPortalContribunteFacade().getDocumentoOficialFacade().buscarDocumentoPorChave(chave) != null) {
                DocumentoOficial doc = getPortalContribunteFacade().getDocumentoOficialFacade().buscarDocumentoPorChave(chave);
                if (doc != null) {
                    return new ResponseEntity<>(doc.getConteudo().getBytes(StandardCharsets.UTF_8), HttpStatus.OK);
                }
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/autenticidade-de-documentos/alvara/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSAlvara> buscarAlvaras(@PathVariable("id") Long id) {
        try {
            WSAlvara wsAlvara = getPortalContribunteFacade().getAlvaraFacade().buscarAndMontarWsAlvaraPorId(id);
            if(wsAlvara != null) {
                return new ResponseEntity<>(wsAlvara, HttpStatus.OK);
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/autenticidade-de-documentos/itbi/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSItbi> buscarItbiPorId(@PathVariable("id") Long id) {
        try {
            WSItbi wsItbi = getPortalContribunteFacade().getcalculoITBIFacade().montarWsItbiPorId(id);
            if(wsItbi != null) {
                return new ResponseEntity<>(wsItbi, HttpStatus.OK);
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/autenticidade-de-certidao/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WsAutenticaCertidao> buscarInformacoesCertidao(@PathVariable("id") Long id) {
        try {
            if (getPortalContribunteFacade().getDocumentoOficialFacade().buscarDocumentoPorId(id) != null) {
                WsAutenticaCertidao ws = getPortalContribunteFacade().getDocumentoOficialFacade().buscarCertidaoPorIdDaSolicitacao(id);
                if (ws != null) {
                    return new ResponseEntity<>(ws, HttpStatus.OK);
                }
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/gerar-alvara-autenticado",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getConteudoAlvara(@RequestParam(value = "id") Long id,
                                                    @RequestParam(value = "tipo") TipoAlvara tipo) {
        String separator = System.getProperty("file.separator");
        String subReport = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
        String img = Util.getApplicationPath("/img/") + separator;

        byte[] bytes = new byte[0];
        try {
            bytes = getPortalContribunteFacade().getAlvaraFacade().imprimirAlvaraPortal(id, TipoAlvara.tipoAlvaraPorDescricao(tipo.getDescricao()), subReport, img).toByteArray();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    @RequestMapping(value = "/imprimir-itbi",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> imprimirLaudoItbi(@RequestParam("id") Long id) {
        byte[] bytes = new byte[0];
        try {
            bytes = getPortalContribunteFacade().imprimirLaudoItbi(id).toByteArray();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    @RequestMapping(value = "/autenticidade-de-documentos/contra-cheque/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSFichaFinanceira> buscarContraCheque(@PathVariable("id") Long idFicha) {
        try {
            WSFichaFinanceira wsFicha = getPortalContribunteFacade().getContraChequeFacade().buscarContraChequePorFichaId(idFicha);
            if(wsFicha != null) {
                return new ResponseEntity<>(wsFicha, HttpStatus.OK);
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/autenticidade-de-documentos/cadastro-imobiliario-historico/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSHistoricoBCI> buscarHistoricoCadastroImobiliario(@PathVariable("id") Long idHistBCI) {
        try {
            WSHistoricoBCI wsHistoricoBCI = getPortalContribunteFacade().getCadastroImobiliarioImpressaoHistFacade().buscarHistoricoBCIPorID(idHistBCI);
            if(wsHistoricoBCI != null) {
                return new ResponseEntity<>(wsHistoricoBCI, HttpStatus.OK);
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/autenticar-cadastro-imobiliario-historico",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSHistoricoBCI> buscarRelatorioCadastroImobiliario(@RequestBody WSHistoricoBCI wsHistoricoBCI) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.AUTENTICIDADE_DOCUMENTOS, wsHistoricoBCI.getNomeUsuario());
            sistemaService.setUsuarioAlternativo(wsHistoricoBCI.getNomeUsuario());

            WSHistoricoBCI historicoBCI = getPortalContribunteFacade().getCadastroImobiliarioImpressaoHistFacade().buscarHistoricoBCIPorAutenticidade(wsHistoricoBCI.getAutenticidade());
            return new ResponseEntity<>(historicoBCI, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/autenticidade-de-documentos/cadastro-economico-historico/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSHistoricoBCM> buscarHistoricoCadastroEconomico(@PathVariable("id") Long idHistBCM) {
        try {
            WSHistoricoBCM wsHistoricoBCM = getPortalContribunteFacade().getCadastroEconomicoImpressaoHistFacade().buscarHistoricoBCMPorID(idHistBCM);
            if(wsHistoricoBCM != null) {
                return new ResponseEntity<>(wsHistoricoBCM, HttpStatus.OK);
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/autenticar-cadastro-economico-historico",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSHistoricoBCM> buscarRelatorioCadastroEconomico(@RequestBody WSHistoricoBCM wsHistoricoBCM) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.AUTENTICIDADE_DOCUMENTOS, wsHistoricoBCM.getNomeUsuario());
            sistemaService.setUsuarioAlternativo(wsHistoricoBCM.getNomeUsuario());

            WSHistoricoBCM historicoBCM = getPortalContribunteFacade().getCadastroEconomicoImpressaoHistFacade().buscarHistoricoBCMPorAutenticidade(wsHistoricoBCM.getAutenticidade());
            return new ResponseEntity<>(historicoBCM, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/autenticar-contra-cheque",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSFichaFinanceira> autenticarContraCheque(@RequestBody WSFichaFinanceira wsFicha) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.AUTENTICIDADE_DOCUMENTOS, wsFicha.getNomePessoa());
            sistemaService.setUsuarioAlternativo(wsFicha.getNomePessoa());

            WSFichaFinanceira wsFichaFinanceira = getPortalContribunteFacade().getContraChequeFacade().buscarContraChequePorAutenticidade(wsFicha.getAutenticidade());
            return new ResponseEntity<>(wsFichaFinanceira, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
