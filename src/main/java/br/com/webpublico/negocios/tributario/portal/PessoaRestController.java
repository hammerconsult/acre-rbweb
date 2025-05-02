package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.nfse.domain.dtos.MunicipioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.PessoaNfseDTO;
import br.com.webpublico.pessoa.dto.PessoaFisicaDTO;
import br.com.webpublico.pessoa.dto.PessoaJuridicaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequestMapping("/integracao/tributario")
public class PessoaRestController extends PortalRestController {

    private static final Logger logger = LoggerFactory.getLogger(PessoaRestController.class);

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/get-pessoa-fisica",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PessoaFisicaDTO> buscarPessoaFisicaPorCpf(@RequestParam(value = "cpfCnpj", required = true) String cpfCnpj) {
        try {
            if (getPortalContribunteFacade().getAlteracaoCadastralPessoaFacade().verificarAlteracaoEmAbertoParaPessoa(cpfCnpj)) {
                throw new Exception("Já existe uma atualização cadastral em aberto para essa pessoa!");
            }
            PessoaFisicaDTO pessoaFisicaDTO = getPortalContribunteFacade().buscarPessoaFisicaPorCpf(cpfCnpj);
            if (pessoaFisicaDTO == null) {
                throw new Exception("Pessoa Física com CPF " + cpfCnpj + " não encontrada!");
            } else {
                return new ResponseEntity<>(pessoaFisicaDTO, HttpStatus.OK);
            }
        } catch (Exception e) {
            return getResponsePessoaFisica(e.getMessage());
        }
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/get-pessoa-fisica-credor",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PessoaFisicaDTO> buscarPessoaFisicaCredorPorCpf(@RequestParam(value = "cpfCnpj", required = true) String cpfCnpj) {
        try {
            if (getPortalContribunteFacade().getAlteracaoCadastralPessoaFacade().verificarAlteracaoEmAbertoParaPessoa(cpfCnpj)) {
                throw new Exception("Já existe uma atualização cadastral em aberto para essa pessoa!");
            }
            return new ResponseEntity<>(getPortalContribunteFacade().buscarPessoaFisicaPorCpf(cpfCnpj), HttpStatus.OK);
        } catch (Exception e) {
            return getResponsePessoaFisica(e.getMessage());
        }
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/get-pessoa-juridica",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PessoaJuridicaDTO> buscarPessoaJuridicaPorCnpj(@RequestParam(value = "cpfCnpj", required = true) String cpfCnpj) {
        try {
            if (getPortalContribunteFacade().getAlteracaoCadastralPessoaFacade().verificarAlteracaoEmAbertoParaPessoa(cpfCnpj)) {
                throw new Exception("Já existe uma atualização cadastral em aberto para essa pessoa!");
            }
            PessoaJuridicaDTO pessoaJuridicaDTO = getPortalContribunteFacade().buscarPessoaJuridicaPorCnpj(cpfCnpj);
            if (pessoaJuridicaDTO == null) {
                throw new Exception("Pessoa Jurídica com CNPJ " + cpfCnpj + " não encontrada!");
            } else {
                return new ResponseEntity<>(pessoaJuridicaDTO, HttpStatus.OK);
            }
        } catch (Exception e) {
            return getResponsePessoaJuridica(e.getMessage());
        }
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/get-pessoa-juridica-credor",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PessoaJuridicaDTO> buscarPessoaJuridicaCredorPorCnpj(@RequestParam(value = "cpfCnpj", required = true) String cpfCnpj) {
        try {
            if (getPortalContribunteFacade().getAlteracaoCadastralPessoaFacade().verificarAlteracaoEmAbertoParaPessoa(cpfCnpj)) {
                throw new Exception("Já existe uma atualização cadastral em aberto para essa pessoa!");
            }
            return new ResponseEntity<>(getPortalContribunteFacade().buscarPessoaJuridicaPorCnpj(cpfCnpj), HttpStatus.OK);
        } catch (Exception e) {
            return getResponsePessoaJuridica(e.getMessage());
        }
    }

    private ResponseEntity<PessoaFisicaDTO> getResponsePessoaFisica(String msg) {
        HttpHeaders header = new HttpHeaders();
        header.add("message-error", msg);
        return new ResponseEntity<PessoaFisicaDTO>(null, header, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<PessoaJuridicaDTO> getResponsePessoaJuridica(String msg) {
        HttpHeaders header = new HttpHeaders();
        header.add("message-error", msg);
        return new ResponseEntity<PessoaJuridicaDTO>(null, header, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Long> getResponseId(String msg) {
        HttpHeaders header = new HttpHeaders();
        header.add("message-error", msg);
        return new ResponseEntity<Long>(null, header, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/confirmar-cadastro-alteracao-pessoa-fisica",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PessoaFisicaDTO> confirmarCadastroAlteracaoPessoaFisica(@RequestBody PessoaFisicaDTO pessoaFisicaDTO) {
        try {
            getPortalContribunteFacade().salvarAlteracaoPessoaFisica(pessoaFisicaDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/confirmar-cadastro-alteracao-pessoa-juridica",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PessoaJuridicaDTO> confirmarCadastroAlteracaoPessoaFisica(@RequestBody PessoaJuridicaDTO pessoaJuridicaDTO) {
        try {
            getPortalContribunteFacade().salvarAlteracaoPessoaJuridica(pessoaJuridicaDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping(value = "/confirmar-cadastro-pessoa-arquivos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ArquivoDTO> confirmarCondutorArquivos(@RequestBody List<LinkedHashMap> param) {
        try {
            List<ArquivoDTO> arquivos = getLinkedHashMapToArquivoDTO(param);
            getPortalContribunteFacade().salvarArquivosAlteracaoPessoa(arquivos);
            return new ResponseEntity<>(new ArquivoDTO(), HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    public List<ArquivoDTO> getLinkedHashMapToArquivoDTO(List<LinkedHashMap> param) {
        List<ArquivoDTO> arquivos = Lists.newArrayList();
        for (LinkedHashMap lhm : param) {
            arquivos.add(traduzirHasMapParaArquivoDTO(lhm));
        }
        return arquivos;
    }

    private ArquivoDTO traduzirHasMapParaArquivoDTO(LinkedHashMap lhm) {
        ObjectMapper mapper = new ObjectMapper();
        ArquivoDTO arquivo = mapper.convertValue(lhm, ArquivoDTO.class);
        arquivo.setId(lhm.get("id") != null ? (Long) lhm.get("id") : null);
        arquivo.setNome(((String) lhm.get("nome")));
        arquivo.setConteudo(((String) lhm.get("conteudo")));
        arquivo.setMimeType(((String) lhm.get("mineType")));
        arquivo.setTamanho(lhm.get("tamanho") != null ? ((Integer) lhm.get("tamanho")).longValue() : null);
        return arquivo;
    }

    @RequestMapping(value = "/pessoa-por-cpf-cnpj",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPorCpfCnpj(@RequestParam(value = "cpfCnpj") String cpfCnpj) {
        try {
            Pessoa pessoa = getPortalContribunteFacade().getPessoaFacade().buscarPessoaComDepedenciasPorCpfOrCnpj(cpfCnpj);
            if (pessoa != null) {
                PessoaNfseDTO pessoaNfseDTO = pessoa.toNfseDto();
                MunicipioNfseDTO municipio = pessoaNfseDTO.getDadosPessoais().getMunicipio();
                if (municipio != null && municipio.getId() == null) {
                    Cidade cidade = getPortalContribunteFacade().getPessoaFacade().getCidadeFacade().recuperaCidadePorNomeEEstado(municipio.getNome(),
                        municipio.getEstado());
                    if (cidade != null) {
                        pessoaNfseDTO.getDadosPessoais().setMunicipio(cidade.toNfseDto());
                    }

                }
                return ResponseEntity.ok(pessoaNfseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nenhuma pessoa encontrada.");
            }
        } catch (Exception e) {
            logger.debug("Não foi encontrada nenhuma pessoa, Exception {} ", e.getMessage());
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
