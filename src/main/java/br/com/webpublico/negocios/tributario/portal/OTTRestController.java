package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.entidades.CondutorOperadoraTecnologiaTransporte;
import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.entidades.RenovacaoOperadoraOTT;
import br.com.webpublico.enums.TipoMarca;
import br.com.webpublico.exception.ValidacaoExceptionOtt;
import br.com.webpublico.ott.*;
import br.com.webpublico.ott.enums.TipoPermissaoRBTransDTO;
import br.com.webpublico.ott.enums.TipoRespostaOttDTO;
import com.google.common.collect.Lists;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/integracao/tributario")
public class OTTRestController extends PortalRestController {

    @RequestMapping(value = "/documentos-credenciamento-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DocumentoCredenciamentoOttDTO>> getDocumentosCredenciamentoOtt() {
        List<DocumentoCredenciamentoOttDTO> documentos = getPortalContribunteFacade()
            .getDocumentosCredenciamentoOtt();
        return ResponseEntity.ok(documentos);
    }

    @RequestMapping(value = "/documentos-veiculo-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DocumentoVeiculoOttDTO>> getDocumentosVeiculoOtt() {
        List<DocumentoVeiculoOttDTO> documentos = getPortalContribunteFacade()
            .getDocumentosVeiculoOtt();
        return ResponseEntity.ok(documentos);
    }

    @RequestMapping(value = "/documentos-condutor-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DocumentoCondutorOttDTO>> getDocumentosCondutorOtt() {
        List<DocumentoCondutorOttDTO> documentos = getPortalContribunteFacade()
            .getDocumentosCondutorOtt();
        return ResponseEntity.ok(documentos);
    }

    @RequestMapping(value = "/solicitar-cadastro-ott",

        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OperadoraTecnologiaTransporteDTO> solicitarCadastro(@RequestBody OperadoraTecnologiaTransporteDTO operadora) {
        try {
            OperadoraTecnologiaTransporteDTO operadoraRetorno = getPortalContribunteFacade().salvarOperadoraTecnologiaTransporte(operadora);
            return new ResponseEntity<>(operadoraRetorno, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/get-veiculos-por-cnpj-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<VeiculoOttDTO>> buscarVeiculosPorCnpjDaOTT(@RequestParam(value = "cpf", required = true) String cpf,
                                                                          @RequestParam(value = "first", required = false) Integer first,
                                                                          @RequestParam(value = "max", required = false) Integer max) {
        sistemaService.setUsuarioAlternativo(cpf);
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.OTT, cpf);
            List<VeiculoOttDTO> veiculos = getPortalContribunteFacade().buscarVeiculosOTT(cpf, first, max);
            return new ResponseEntity<>(veiculos, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/count-veiculos-por-cnpj-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> contarVeiculosPorCnpjDaOTT(@RequestParam(value = "cpf", required = true) String cpf) {
        sistemaService.setUsuarioAlternativo(cpf);
        try {
            Integer counter = getPortalContribunteFacade().contarVeiculosOTT(cpf);
            return new ResponseEntity<>(counter, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/get-marcas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<MarcaDTO>> buscarMarcas(@RequestParam(value = "cpf", required = true) String cpf) {
        List<MarcaDTO> marcas = Lists.newArrayList();
        try {
            sistemaService.setUsuarioAlternativo(cpf);
            marcas = getPortalContribunteFacade().buscarMarcasDTOPorTipoMarcaPortalWeb(TipoMarca.CARRO);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(marcas, HttpStatus.OK);
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/get-cores",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CorDTO>> buscarCores(@RequestParam(value = "cpf", required = true) String cpf) {
        List<CorDTO> cores = Lists.newArrayList();
        try {
            sistemaService.setUsuarioAlternativo(cpf);
            cores = getPortalContribunteFacade().buscarCoresPortalWeb();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(cores, HttpStatus.OK);
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/veiculo-por-placa-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<VeiculoOttDTO> buscarVeiculoPorPlacaCNPJ(@RequestParam(value = "cnpj", required = true) String cnpj,
                                                                   @RequestParam(value = "placa", required = true) String placa) {
        try {
            sistemaService.setUsuarioAlternativo(cnpj);
            VeiculoOttDTO veiculo = getPortalContribunteFacade().getVeiculoOperadoraTecnologiaTransporteFacade().buscarVeiculoOttDTOPorCNPJPlaca(cnpj, placa);
            return new ResponseEntity<>(veiculo, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/confirmar-cadastro-veiculo-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<VeiculoOttDTO> confirmarVeiculo(@RequestBody VeiculoOttDTO veiculoOttDTO) {
        try {
            sistemaService.setUsuarioAlternativo(veiculoOttDTO.getOperadoraTransporte().getCnpj());
            VeiculoOttDTO veiculoRetorno = getPortalContribunteFacade().salvarVeiculoOTT(veiculoOttDTO);
            if (veiculoRetorno != null) {
                return new ResponseEntity<>(veiculoRetorno, HttpStatus.OK);
            }
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/adicionar-veiculo-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RespostaOttDTO retornoCadastroVeiculo(@RequestBody VeiculoRestOttDTO veiculoRestOttDTO) {
        try {
            getPortalContribunteFacade().validarCamposVeiculoRestOTT(veiculoRestOttDTO);
            getPortalContribunteFacade().fazerLoginPortalRestOTT(veiculoRestOttDTO.getSenha(), veiculoRestOttDTO.getCnpj());
            veiculoRestOttDTO = getPortalContribunteFacade().salvarVeiculoRestOttDTO(veiculoRestOttDTO);
            if (veiculoRestOttDTO != null) {
                return getExceptionVeiculoOttDTO(veiculoRestOttDTO, TipoRespostaOttDTO.SUCESSO);
            }
        } catch (ValidacaoExceptionOtt validacaoExceptionOtt) {
            return getExceptionVeiculoOttDTO(veiculoRestOttDTO, validacaoExceptionOtt.getMensagem());
        }
        return getExceptionOttDTO();
    }

    private RespostaOttDTO getExceptionVeiculoOttDTO(@RequestBody VeiculoRestOttDTO veiculoRestOttDTO, TipoRespostaOttDTO tipo) {
        RespostaOttDTO respostaOttDTO = new RespostaOttDTO();
        respostaOttDTO.setCnpj(veiculoRestOttDTO.getCnpj());
        respostaOttDTO.setCodigo(tipo.getCodigo());
        respostaOttDTO.setDescricao(tipo.getDescricao());
        return respostaOttDTO;
    }

    @RequestMapping(value = "/adicionar-condutor-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RespostaOttDTO retornoCadastroCondutor(@RequestBody CondutorRestOttDTO condutorRestOttDTO) {
        try {
            getPortalContribunteFacade().validarCamposCondutorRestOTT(condutorRestOttDTO);
            getPortalContribunteFacade().fazerLoginPortalRestOTT(condutorRestOttDTO.getSenha(), condutorRestOttDTO.getCnpj());
            condutorRestOttDTO = getPortalContribunteFacade().salvarCondutorRestOttDTO(condutorRestOttDTO);
            if (condutorRestOttDTO != null) {
                return getExceptionCondutorOttDTO(condutorRestOttDTO, TipoRespostaOttDTO.SUCESSO);
            }
        } catch (ValidacaoExceptionOtt validacaoExceptionOtt) {
            return getExceptionCondutorOttDTO(condutorRestOttDTO, validacaoExceptionOtt.getMensagem());
        }
        return getExceptionOttDTO();
    }

    private RespostaOttDTO getExceptionCondutorOttDTO(@RequestBody CondutorRestOttDTO condutorRestOttDTO, TipoRespostaOttDTO tipo) {
        RespostaOttDTO respostaOttDTO = new RespostaOttDTO();
        respostaOttDTO.setCnpj(condutorRestOttDTO.getCnpj());
        respostaOttDTO.setCodigo(tipo.getCodigo());
        respostaOttDTO.setDescricao(tipo.getDescricao());
        return respostaOttDTO;
    }

    @RequestMapping(value = "/situacao-veiculo-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RespostaOttDTO retornarSituacaoVeiculoOtt(@RequestBody SituacaoVeiculoRestDTO situacao) {
        try {
            getPortalContribunteFacade().validarCamposSituacaoVeiculoOtt(situacao);
            getPortalContribunteFacade().fazerLoginPortalRestOTT(situacao.getSenha(), situacao.getCnpj());
            String sit = getPortalContribunteFacade().recuperarSituacaoVeiculoOTT(situacao);
            if (sit != null) {
                return getExceptionSituacaoVeiculoOtt(situacao, sit);
            }
        } catch (ValidacaoExceptionOtt vott) {
            return getExceptionSituacaoVeiculoOtt(situacao, vott.getMensagem().getDescricao());
        }
        return getExceptionOttDTO();
    }

    @RequestMapping(value = "/situacao-condutor-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RespostaOttDTO retornarSituacaoCondutorOtt(@RequestBody SituacaoCondutorRestDTO situacao) {
        try {
            getPortalContribunteFacade().validarCamposSituacaoCondutorOtt(situacao);
            getPortalContribunteFacade().fazerLoginPortalRestOTT(situacao.getSenha(), situacao.getCnpj());
            String sit = getPortalContribunteFacade().recuperarSituacaoCondutorOTT(situacao);
            if (sit != null) {
                return getExceptionSituacaoCondutorOtt(situacao, sit);
            }
        } catch (ValidacaoExceptionOtt vott) {
            return getExceptionSituacaoCondutorOtt(situacao, vott.getMensagem().getDescricao());
        }

        return getExceptionOttDTO();
    }

    private RespostaOttDTO getExceptionSituacaoCondutorOtt(@RequestBody SituacaoCondutorRestDTO situacao, String retornoSituacao) {
        RespostaOttDTO respostaOttDTO = new RespostaOttDTO();
        respostaOttDTO.setCnpj(situacao.getCnpj());
        return getRespostaOttDTO(retornoSituacao, respostaOttDTO);
    }

    private RespostaOttDTO getExceptionSituacaoVeiculoOtt(@RequestBody SituacaoVeiculoRestDTO situacao, String retornoSituacao) {
        RespostaOttDTO respostaOttDTO = new RespostaOttDTO();
        respostaOttDTO.setCnpj(situacao.getCnpj());
        return getRespostaOttDTO(retornoSituacao, respostaOttDTO);
    }

    private RespostaOttDTO getRespostaOttDTO(String retornoSituacao, RespostaOttDTO respostaOttDTO) {
        respostaOttDTO.setCodigo(TipoRespostaOttDTO.SUCESSO.getCodigo());
        respostaOttDTO.setDescricao(retornoSituacao);
        return respostaOttDTO;
    }

    @RequestMapping(value = "/adicionar-viagem-condutor-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RespostaOttDTO retornoCadastroViagemCondutorOtt(@RequestBody ViagemCondutorOttDTO viagemCondutorOttDTO) {
        try {
            getPortalContribunteFacade().validarCamposViagemCondutorOtt(viagemCondutorOttDTO);
            getPortalContribunteFacade().fazerLoginPortalRestOTT(viagemCondutorOttDTO.getSenhaUsuario(), viagemCondutorOttDTO.getCnpjOtt());
            ViagemCondutorOttDTO viagem = getPortalContribunteFacade().salvarViagemCondutor(viagemCondutorOttDTO);

            if (viagem != null) {
                return getExceptionViagemCondutorOttDTO(viagemCondutorOttDTO, TipoRespostaOttDTO.SUCESSO);
            }
        } catch (ValidacaoExceptionOtt veott) {
            return getExceptionViagemCondutorOttDTO(viagemCondutorOttDTO, veott.getMensagem());
        }
        return getExceptionOttDTO();
    }

    private RespostaOttDTO getExceptionViagemCondutorOttDTO(@RequestBody ViagemCondutorOttDTO viagem, TipoRespostaOttDTO tipo) {
        RespostaOttDTO respostaOttDTO = new RespostaOttDTO();
        respostaOttDTO.setCnpj(viagem.getCnpjOtt());
        respostaOttDTO.setCodigo(tipo.getCodigo());
        respostaOttDTO.setDescricao(tipo.getDescricao());
        return respostaOttDTO;
    }


    private RespostaOttDTO getExceptionOttDTO() {
        RespostaOttDTO respostaOttDTO = new RespostaOttDTO();
        respostaOttDTO.setCodigo(TipoRespostaOttDTO.ERRO.getCodigo());
        respostaOttDTO.setDescricao(TipoRespostaOttDTO.ERRO.getDescricao());
        return respostaOttDTO;
    }

    @RequestMapping(value = "/get-condutores-por-cnpj-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CondutorOttDTO>> buscarCondutoresPorCnpjDaOTT(@RequestParam(value = "cpf", required = true) String cpf,
                                                                             @RequestParam(value = "first", required = false) Integer first,
                                                                             @RequestParam(value = "max", required = false) Integer max) {
        sistemaService.setUsuarioAlternativo(cpf);
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.OTT, cpf);
            List<CondutorOttDTO> condutores = getPortalContribunteFacade().buscarCondutoresOTT(cpf, first, max);
            return new ResponseEntity<>(condutores, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/count-condutores-por-cnpj-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> contarCondutoresPorCnpjDaOTT(@RequestParam(value = "cpf", required = true) String cpf) {
        sistemaService.setUsuarioAlternativo(cpf);
        try {
            Integer counter = getPortalContribunteFacade().contarCondutoresOTT(cpf);
            return new ResponseEntity<>(counter, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/condutor-por-cpf-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CondutorOttDTO> buscarCondutorPorCPFCNPJ(@RequestParam(value = "cnpj", required = true) String cnpj,
                                                                   @RequestParam(value = "cpf", required = true) String cpf) {
        try {
            CondutorOttDTO condutor = getPortalContribunteFacade().getCondutorOperadoraTecnologiaTransporteFacade().buscarCondutorOttDTOPorCPF(cnpj, cpf);
            return new ResponseEntity<>(condutor, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/adicionar-veiculo-condutor-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CondutorOttDTO> adicionarVeiculoCondutor(@RequestParam(value = "cnpj", required = true) String cnpj,
                                                                   @RequestParam(value = "cpf", required = true) String cpf,
                                                                   @RequestParam(value = "placa", required = true) String placa) {
        try {
            CondutorOttDTO condutor = getPortalContribunteFacade().getCondutorOperadoraTecnologiaTransporteFacade().adicionarVeiculoCondutor(cnpj, cpf, placa);
            return new ResponseEntity<>(condutor, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/excluir-veiculo-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<VeiculoOttDTO> excluirVeiculo(@RequestBody VeiculoOttDTO veiculoOttDTO) {
        try {
            VeiculoOttDTO veiculo = getPortalContribunteFacade().excluirVeiculoOtt(veiculoOttDTO);
            if (veiculo != null) {
                return new ResponseEntity<>(veiculo, HttpStatus.OK);
            }
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/desativar-veiculo-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<VeiculoOttDTO> desativarVeiculo(@RequestBody VeiculoOttDTO veiculoOttDTO) {

        try {
            VeiculoOttDTO veiculoRetorno = getPortalContribunteFacade().desativarVeiculoOTT(veiculoOttDTO);
            if (veiculoRetorno != null) {
                return new ResponseEntity<>(veiculoRetorno, HttpStatus.OK);
            }
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/desativar-condutor-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CondutorOttDTO> desativarCondutor(@RequestBody CondutorOttDTO condutorOttDTO) {

        try {
            CondutorOttDTO condutorRetorno = getPortalContribunteFacade().desativarCondutorOTT(condutorOttDTO);
            if (condutorRetorno != null) {
                return new ResponseEntity<>(condutorRetorno, HttpStatus.OK);
            }
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/excluir-condutor-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CondutorOttDTO> excluirCondutor(@RequestBody CondutorOttDTO condutorOttDTO) {
        try {
            CondutorOttDTO condutor = getPortalContribunteFacade().excluirCondutorOtt(condutorOttDTO);
            if (condutor != null) {
                return new ResponseEntity<>(condutor, HttpStatus.OK);
            }
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/confirmar-cadastro-condutor-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CondutorOttDTO> confirmarCondutor(@RequestBody CondutorOttDTO condutorOttDTO) {
        try {
            sistemaService.setUsuarioAlternativo(condutorOttDTO.getOperadoraTransporte().getCnpj());
            CondutorOttDTO condutorRetorno = getPortalContribunteFacade().salvarCondutorOTT(condutorOttDTO);
            if (condutorRetorno != null) {
                return new ResponseEntity<>(condutorRetorno, HttpStatus.OK);
            }
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/editar-cadastro-condutor-ott-arquivos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ArquivoDTO>> recuperarCondutorArquivos(@PathVariable("id") Long id) {
        List<ArquivoDTO> condutorArquivos = Lists.newArrayList();
        CondutorOttDTO condutorOttDTO = getPortalContribunteFacade().getCondutorOperadoraTecnologiaTransporteFacade().recuperar(id).toDTO();
        try {
            condutorArquivos = getPortalContribunteFacade().recuperarArquivosCondutor(condutorOttDTO);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(condutorArquivos, HttpStatus.OK);
    }

    @RequestMapping(value = "/editar-cadastro-veiculo-ott-arquivos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ArquivoDTO>> recuperarVeiculoArquivos(@PathVariable("id") Long id) {
        List<ArquivoDTO> veiculoArquivos = Lists.newArrayList();
        VeiculoOttDTO veiculoOttDTO = getPortalContribunteFacade().getVeiculoOperadoraTecnologiaTransporteFacade().recuperar(id).toDTO();
        try {
            veiculoArquivos = getPortalContribunteFacade().recuperarArquivosVeiculo(veiculoOttDTO);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(veiculoArquivos, HttpStatus.OK);
    }

    @RequestMapping(value = "/autenticidade-documento/ott/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OperadoraTecnologiaTransporteDTO> buscarInformacoesOTT(@PathVariable("id") Long id) {
        try {
            if (getPortalContribunteFacade().getOttFacade().buscarOTTPorId(id) != null) {
                OperadoraTecnologiaTransporteDTO ottDTO = getPortalContribunteFacade().getOttFacade().buscarOTTPorId(id).toDTO();
                if (ottDTO != null) {
                    return new ResponseEntity<>(ottDTO, HttpStatus.OK);
                }
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/autenticidade-documento/condutor-ott/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CondutorOttDTO> buscarInformacoesCondutorOTT(@PathVariable("id") Long id) {
        try {
            CondutorOperadoraTecnologiaTransporte condutorOperadoraTecnologiaTransporte = getPortalContribunteFacade()
                .getCondutorOperadoraTecnologiaTransporteFacade().recuperar(id);
            if (condutorOperadoraTecnologiaTransporte != null) {
                CondutorOttDTO condutorOttDTO = condutorOperadoraTecnologiaTransporte.toDTO();
                if (condutorOttDTO != null) {
                    return new ResponseEntity<>(condutorOttDTO, HttpStatus.OK);
                }
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/buscar-renovacoes-por-condutor/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<RenovacaoCondutorOttDTO>> buscarRenovacoesPorCondutorOtt(@PathVariable("id") Long id) {
        List<RenovacaoCondutorOttDTO> renovacoes = Lists.newArrayList();
        try {
            renovacoes = getPortalContribunteFacade().getCondutorOperadoraTecnologiaTransporteFacade().buscarRenovacoesPorCondutorOtt(id);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(renovacoes, HttpStatus.OK);
    }

    @RequestMapping(value = "/salvar-renovacao-de-autorizacao-condutor-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RenovacaoCondutorOttDTO> novaRenovacao(@RequestBody RenovacaoCondutorOttDTO renovacaoCondutorOttDTO) {
        try {
            renovacaoCondutorOttDTO = getPortalContribunteFacade().salvarRenovacaoCondutorOtt(renovacaoCondutorOttDTO);
            return new ResponseEntity<>(renovacaoCondutorOttDTO, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/buscar-renovacao-condutor-ott-arquivos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ArquivoDTO>> buscarRenovacaoCondutorArquivos(@PathVariable("id") Long id) {
        List<ArquivoDTO> condutorArquivos = Lists.newArrayList();

        Long idRenovacaoCondutor = getPortalContribunteFacade().getCondutorOperadoraTecnologiaTransporteFacade().recuperarRenovacao(id).getId();

        try {
            condutorArquivos = getPortalContribunteFacade().recuperarRenovacaoArquivosCondutor(idRenovacaoCondutor);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(condutorArquivos, HttpStatus.OK);
    }

    @RequestMapping(value = "/buscar-renovacoes-por-veiculo/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<RenovacaoVeiculoOttDTO>> buscarRenovacoesPorVeiculoOtt(@PathVariable("id") Long id) {
        List<RenovacaoVeiculoOttDTO> renovacoes = Lists.newArrayList();
        try {
            renovacoes = getPortalContribunteFacade().getVeiculoOperadoraTecnologiaTransporteFacade().buscarRenovacoesPorVeiculoOtt(id);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(renovacoes, HttpStatus.OK);
    }

    @RequestMapping(value = "/renovacao-de-autorizacao-veiculo-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RenovacaoVeiculoOttDTO> salvarRenovacaoVeiculo(@RequestBody RenovacaoVeiculoOttDTO renovacaoVeiculoOttDTO) {
        try {
            RenovacaoVeiculoOttDTO retorno = getPortalContribunteFacade().salvarRenovacaoVeiculoOtt(renovacaoVeiculoOttDTO);
            return new ResponseEntity<>(retorno, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/buscar-renovacao-veiculo-ott-arquivos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ArquivoDTO>> buscarRenovacaoVeiculoArquivos(@PathVariable("id") Long id) {

        List<ArquivoDTO> condutorArquivos = Lists.newArrayList();

        Long idRenovacaoVeiculo = getPortalContribunteFacade().getVeiculoOperadoraTecnologiaTransporteFacade().recuperarRenovacao(id).getId();

        try {
            condutorArquivos = getPortalContribunteFacade().recuperarRenovacaoArquivosVeiculo(idRenovacaoVeiculo);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(condutorArquivos, HttpStatus.OK);
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/condutor-por-cpf-nome-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CondutorOttDTO>> buscarCondutorPorCpfNome(@RequestParam(value = "filtro", required = true) String filtro,
                                                                         @RequestParam(value = "idOperadora", required = true) Long idOperadora) {
        List<CondutorOttDTO> condutores = Lists.newArrayList();
        try {
            condutores = getPortalContribunteFacade().getCondutorOperadoraTecnologiaTransporteFacade().buscarCondutoresPorCpfNome(filtro, idOperadora);
            return new ResponseEntity<>(condutores, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/veiculo-por-placa-modelo-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<VeiculoOttDTO>> buscarVeiculoPorPlacaModelo(@RequestParam(value = "filtro", required = true) String filtro,
                                                                           @RequestParam(value = "idOperadora", required = true) Long idOperadora) {
        List<VeiculoOttDTO> veiculos = Lists.newArrayList();
        try {
            veiculos = getPortalContribunteFacade().getVeiculoOperadoraTecnologiaTransporteFacade().buscarVeiculosPorPlacaModelo(filtro, idOperadora);
            return new ResponseEntity<>(veiculos, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/operadora-ott-por-cnpj",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OperadoraTecnologiaTransporteDTO> buscarCondutorPorCPFCNPJ(@RequestParam(value = "cnpj", required = true) String cnpj) {
        try {
            OperadoraTecnologiaTransporteDTO operadora = getPortalContribunteFacade().getOttFacade().buscarOperadoraOttPorCNPJ(cnpj).toDTO();
            return new ResponseEntity<>(operadora, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/editar-cadastro-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OperadoraTecnologiaTransporteDTO> editarCadastro(@RequestBody OperadoraTecnologiaTransporteDTO operadora) {
        try {
            OperadoraTecnologiaTransporteDTO operadoraRetorno = getPortalContribunteFacade().salvarOperadoraOttPortal(operadora);
            return new ResponseEntity<>(operadoraRetorno, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/renovacao-de-autorizacao-operadora-ott",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RenovacaoOperadoraOttDTO> salvarRenovacaoOperadora(@RequestBody RenovacaoOperadoraOttDTO renovacaoOperadora) {
        try {
            renovacaoOperadora = getPortalContribunteFacade().salvarRenovacaoOperadora(renovacaoOperadora);
            return new ResponseEntity<>(renovacaoOperadora, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/buscar-renovacao-operadora-ott-arquivos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ArquivoDTO>> buscarRenovacaoOperadoraArquivos(@PathVariable("id") Long id) {
        List<ArquivoDTO> operadoraArquivos = Lists.newArrayList();

        Long idRenovacaoOperadora = getPortalContribunteFacade().getOttFacade().recuperarRenovacao(id).getId();

        try {
            operadoraArquivos = getPortalContribunteFacade().recuperarRenovacaoArquivosOperadora(idRenovacaoOperadora);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(operadoraArquivos, HttpStatus.OK);
    }

    @RequestMapping(value = "/buscar-renovacoes-por-operadora/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<RenovacaoOperadoraOttDTO>> buscarRenovacoesPorOperadoraOtt(@PathVariable("id") Long id) {
        List<RenovacaoOperadoraOttDTO> renovacoes = Lists.newArrayList();
        try {
            renovacoes = getPortalContribunteFacade().getOttFacade().buscarRenovacoesPorOperadoraOtt(id);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(renovacoes, HttpStatus.OK);
    }

    @RequestMapping(value = "/verificar-certificado-operadora/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> verificarCertificadoValido(@PathVariable("id") Long id) {
        try {
            boolean certificadoVencido = getPortalContribunteFacade().getOttFacade().verificarCertificadoValido(id);
            return new ResponseEntity<>(certificadoVencido, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/anexos-credenciamento/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AnexoCredenciamentoOttDTO>> getAnexosCredenciamento(@PathVariable("id") Long id) {
        List<AnexoCredenciamentoOttDTO> anexos = getPortalContribunteFacade().getAnexosCredenciamento(id);
        return new ResponseEntity<>(anexos, HttpStatus.OK);
    }

    @RequestMapping(value = "/reenviar-anexos-credenciamento",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> reenviarAnexosCredenciamento(@RequestBody OperadoraTecnologiaTransporteDTO operadoraDTO) throws Exception {
        getPortalContribunteFacade().reenviarAnexosCredenciamento(operadoraDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/recuperar-renovacao-operadora-ott/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RenovacaoOperadoraOttDTO> recuperarRenocacaoOperadoraOtt(@PathVariable Long id) throws Exception {
        RenovacaoOperadoraOTT renovacaoOperadoraOTT = getPortalContribunteFacade()
            .getOperadoraTecnologiaTransporteFacade().recuperarRenovacaoOperadoraOTT(id);
        return new ResponseEntity<>(renovacaoOperadoraOTT.toDTO(), HttpStatus.OK);
    }

    @RequestMapping(value = "/anexos-renovacao/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AnexoCredenciamentoOttDTO>> getAnexosRenovacao(@PathVariable("id") Long id) {
        List<AnexoCredenciamentoOttDTO> anexos = getPortalContribunteFacade().getAnexosRenovacao(id);
        return new ResponseEntity<>(anexos, HttpStatus.OK);
    }

    @RequestMapping(value = "/reenviar-anexos-renovacao",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> reenviarAnexosRenovacao(@RequestBody RenovacaoOperadoraOttDTO renovacaoDTO) throws Exception {
        getPortalContribunteFacade().reenviarAnexosRenovacao(renovacaoDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/recuperar-veiculo-ott/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<VeiculoOttDTO> recuperarVeiculoOtt(@PathVariable("id") Long id) {
        VeiculoOttDTO veiculo = getPortalContribunteFacade().recuperarVeiculoOtt(id);
        return new ResponseEntity<>(veiculo, HttpStatus.OK);
    }

    @RequestMapping(value = "/anexos-veiculo/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AnexoVeiculoOttDTO>> getAnexosVeiculo(@PathVariable("id") Long id) {
        List<AnexoVeiculoOttDTO> anexos = getPortalContribunteFacade().getAnexosVeiculo(id);
        return new ResponseEntity<>(anexos, HttpStatus.OK);
    }

    @RequestMapping(value = "/reenviar-anexos-veiculo",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> reenviarAnexosVeiculo(@RequestBody VeiculoOttDTO veiculoOttDTO) throws Exception {
        getPortalContribunteFacade().reenviarAnexosVeiculo(veiculoOttDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/recuperar-renovacao-veiculo-ott/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RenovacaoVeiculoOttDTO> recuperarRenovacaoVeiculoOtt(@PathVariable("id") Long id) {
        RenovacaoVeiculoOttDTO renovacao = getPortalContribunteFacade().recuperarRenovacaoVeiculoOtt(id);
        return new ResponseEntity<>(renovacao, HttpStatus.OK);
    }

    @RequestMapping(value = "/anexos-renovacao-veiculo/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AnexoVeiculoOttDTO>> getAnexosRenovacaoVeiculo(@PathVariable("id") Long id) {
        List<AnexoVeiculoOttDTO> anexos = getPortalContribunteFacade().getAnexosRenovacaoVeiculo(id);
        return new ResponseEntity<>(anexos, HttpStatus.OK);
    }

    @RequestMapping(value = "/reenviar-anexos-renovacao-veiculo",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> reenviarAnexosRenovacaoVeiculo(@RequestBody RenovacaoVeiculoOttDTO renovacaoVeiculoOttDTO) throws Exception {
        getPortalContribunteFacade().reenviarAnexosRenovacaoVeiculo(renovacaoVeiculoOttDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/recuperar-condutor-ott/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CondutorOttDTO> recuperarCondutorOtt(@PathVariable("id") Long id) {
        CondutorOttDTO condutor = getPortalContribunteFacade().recuperarCondutorOtt(id);
        return new ResponseEntity<>(condutor, HttpStatus.OK);
    }

    @RequestMapping(value = "/anexos-condutor/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AnexoCondutorOttDTO>> getAnexosCondutor(@PathVariable("id") Long id) {
        List<AnexoCondutorOttDTO> anexos = getPortalContribunteFacade().getAnexosCondutor(id);
        return new ResponseEntity<>(anexos, HttpStatus.OK);
    }

    @RequestMapping(value = "/reenviar-anexos-condutor",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> reenviarAnexosCondutor(@RequestBody CondutorOttDTO condutorOttDTO) throws Exception {
        getPortalContribunteFacade().reenviarAnexosCondutor(condutorOttDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/recuperar-renovacao-condutor-ott/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RenovacaoCondutorOttDTO> recuperarRenovacaoCondutorOtt(@PathVariable("id") Long id) {
        RenovacaoCondutorOttDTO renovacao = getPortalContribunteFacade().recuperarRenovacaoCondutorOtt(id);
        return new ResponseEntity<>(renovacao, HttpStatus.OK);
    }

    @RequestMapping(value = "/anexos-renovacao-condutor/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AnexoCondutorOttDTO>> getAnexosRenovacaoCondutor(@PathVariable("id") Long id) {
        List<AnexoCondutorOttDTO> anexos = getPortalContribunteFacade().getAnexosRenovacaoCondutor(id);
        return new ResponseEntity<>(anexos, HttpStatus.OK);
    }

    @RequestMapping(value = "/reenviar-anexos-renovacao-condutor",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> reenviarAnexosRenovacaoCondutor(@RequestBody RenovacaoCondutorOttDTO renovacaoCondutorOttDTO) throws Exception {
        getPortalContribunteFacade().reenviarAnexosRenovacaoCondutor(renovacaoCondutorOttDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/recuperar-parametro-transito-transporte/{tipo}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ParametrosTransitoTransporteDTO> recuperarParametroTransitoTransporte(@PathVariable("tipo") String tipo) {
        ParametrosTransitoTransporteDTO parametroTransitoTransporte = getPortalContribunteFacade()
            .recuperarParametroTransitoTransporte(TipoPermissaoRBTransDTO.valueOf(tipo));
        return new ResponseEntity<>(parametroTransitoTransporte, HttpStatus.OK);
    }

    @RequestMapping(value = "/modelo-adesivo-ott",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ArquivoDTO> getModeloAdesivoOtt() {
        ArquivoDTO arquivoDTO = getPortalContribunteFacade().getModeloAdesivoOtt();
        return new ResponseEntity<>(arquivoDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/cnpj-permite-cadastro-ott/{cnpj}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> cnpjPermiteCadastroOTT(@PathVariable String cnpj) {
        Boolean permite = getPortalContribunteFacade().cnpjPermiteCadastroOTT(cnpj);
        return new ResponseEntity<>(permite, HttpStatus.OK);
    }
}
