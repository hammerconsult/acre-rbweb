package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.SolicitacaoCadastroPessoa;
import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.enums.TipoSolicitacaoCadastroPessoa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.pessoa.dto.*;
import br.com.webpublico.ws.model.WSNivelEscolaridade;
import br.com.webpublico.ws.model.WSProfissao;
import br.com.webpublico.ws.model.WSUF;
import br.com.webpublico.ws.model.WsSolicitacaoCadastro;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.faces.application.FacesMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Buzatto on 02/03/2017.
 */
@Controller
@RequestMapping("/integracao/tributario")
public class SolicitacaoCadastroRestController extends PortalRestController {

    @RequestMapping(value = "/solicitar-cadastro",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WsSolicitacaoCadastro> solicitarCadastro(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro) {
        try {
            getPortalContribunteFacade().salvarSolicitacaoCadastroViaPortal(solicitacaoCadastro);
        } catch (ValidacaoException e) {
            List<String> mensagens = Lists.newArrayList();
            for (FacesMessage facesMessage : e.getMensagens()) {
                mensagens.add(facesMessage.getDetail());
            }
            solicitacaoCadastro.setMensagem(StringUtils.join(mensagens, "</br>"));
        } catch (Exception e) {
            solicitacaoCadastro.setMensagem("Erro inesperado ao efetuar a solicitação de cadastro.");
        }
        return new ResponseEntity<>(solicitacaoCadastro, HttpStatus.OK);
    }

    @RequestMapping(value = "/salvar-atualizacao-cadastral",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PessoaFisicaDTO> salvarAtualizacaoCadastral(@RequestBody PessoaFisicaDTO pessoaFisicaDTO) {
        try {
            getPortalContribunteFacade().getPessoaFacade().salvarAtualizacaoCadastral(pessoaFisicaDTO);
            return new ResponseEntity<>(pessoaFisicaDTO, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
            return new ResponseEntity<>(pessoaFisicaDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/get-contratos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ContratoFPDTO>> getContratos(@RequestBody PessoaFisicaDTO pessoaFisicaDTO) {
        try {
            List<ContratoFPDTO> contratos = getPortalContribunteFacade().getContratoFPFacade().buscarContratosVigentesPorPessoaFisica(pessoaFisicaDTO.getId());
            return new ResponseEntity<>(contratos, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<List<ContratoFPDTO>>(new ArrayList<ContratoFPDTO>(), HttpStatus.OK);

    }

    @RequestMapping(value = "/get-all-ufs", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<WSUF>> getAllUF() {
        try {
            List<WSUF> ufs = new ArrayList<>();
            for (UF uf : getPortalContribunteFacade().getUfFacade().lista()) {
                ufs.add(new WSUF(uf.getId(), uf.getUf(), uf.getNome()));
            }
            return new ResponseEntity<>(ufs, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<WSUF>>(new ArrayList<WSUF>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-estados", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<UFDTO>> getAllUFs() {
        try {
            return new ResponseEntity<>(UF.toUFsDTO(getPortalContribunteFacade().getUfFacade().lista()), HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<UFDTO>>(new ArrayList<UFDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-conselhos", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ConselhoClasseDTO>> buscarTodosConselhos() {
        try {
            return new ResponseEntity<>(ConselhoClasseOrdem.toConselhos(getPortalContribunteFacade().getConselhoClasseOrdemFacade().lista()), HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<ConselhoClasseDTO>>(new ArrayList<ConselhoClasseDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-formacoes", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<FormacaoDTO>> buscarTodasFormacoes() {
        try {
            return new ResponseEntity<>(Formacao.toFormacoesDTO(getPortalContribunteFacade().getFormacaoFacade().lista()), HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<FormacaoDTO>>(new ArrayList<FormacaoDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-area-formacoes", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<AreaFormacaoDTO>> buscarTodasAreasFormacoes() {
        try {
            return new ResponseEntity<>(AreaFormacao.toAreasFormacoesDTO(getPortalContribunteFacade().getAreaFormacaoFacade().lista()), HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<AreaFormacaoDTO>>(new ArrayList<AreaFormacaoDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-habilidades", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<HabilidadeDTO>> buscarTodasHabilidades() {
        try {
            return new ResponseEntity<>(Habilidade.toHabilidadesDTO(getPortalContribunteFacade().getHabilidadeFacade().lista()), HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<HabilidadeDTO>>(new ArrayList<HabilidadeDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-bancos", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<BancoDTO>> getBancos() {
        try {
            return new ResponseEntity<>(Banco.toBancosDTO(getPortalContribunteFacade().getBancoFacade().buscarTodosOrdenandoPorParametro("to_number(regexp_replace(numeroBanco, '[^0-9]', ''))")), HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<BancoDTO>>(new ArrayList<BancoDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-nacionalidades", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<NacionalidadeDTO>> getNacionalidades() {
        try {
            return new ResponseEntity<>(Nacionalidade.toNacionalidadesDTO(getPortalContribunteFacade().getNacionalidadeFacade().lista()), HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<NacionalidadeDTO>>(new ArrayList<NacionalidadeDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-nivel-escolaridade", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<WSNivelEscolaridade>> getAllNivelEscolaridade() {
        try {
            List<WSNivelEscolaridade> niveisEscolaridade = new ArrayList<>();
            for (NivelEscolaridade nivelEscolaridade : getPortalContribunteFacade().getNivelEscolaridadeFacade().lista()) {
                niveisEscolaridade.add(new WSNivelEscolaridade(nivelEscolaridade.getDescricao(), nivelEscolaridade.getId()));
            }
            return new ResponseEntity<>(niveisEscolaridade, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<WSNivelEscolaridade>>(new ArrayList<WSNivelEscolaridade>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-cnaes", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<CnaeDTO>> getAllCnaes() {
        try {
            List<CnaeDTO> cnaes = new ArrayList<>();
            for (CNAE cnae : getPortalContribunteFacade().getCnaeFacade().listaCnaesAtivos()) {
                cnaes.add(new CnaeDTO(cnae.getId(), cnae.getCodigoCnae(), cnae.getDescricaoDetalhada()));
            }
            return new ResponseEntity<>(cnaes, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<CnaeDTO>>(new ArrayList<CnaeDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-nivel-escolaridade-dto", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<NivelEscolaridadeDTO>> getAllNivelEscolaridadeDTO() {
        try {
            List<NivelEscolaridade> lista = getPortalContribunteFacade().getNivelEscolaridadeFacade().lista();
            return new ResponseEntity<>(NivelEscolaridade.toNiveisEscolaridadesDTOs(lista), HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<NivelEscolaridadeDTO>>(new ArrayList<NivelEscolaridadeDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-cid-by-codigo/{codigoDaCid}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> getCidDTOs(@PathVariable("codigoDaCid") String codigoDaCid) {
        try {
            Boolean codigo = getPortalContribunteFacade().getCidFacade().isCodigoCidValido(codigoDaCid);
            return new ResponseEntity<>(codigo, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-profissoes", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<WSProfissao>> getAllProfissoes() {
        try {
            List<WSProfissao> profissoes = new ArrayList<>();
            for (Profissao profissao : getPortalContribunteFacade().getProfissaoFacade().lista()) {
                profissoes.add(new WSProfissao(profissao.getId(), profissao.getDescricao()));
            }
            return new ResponseEntity<>(profissoes, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<WSProfissao>>(new ArrayList<WSProfissao>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-tipos-dependente", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<TipoDependenteDTO>> getAllTiposDependente() {
        try {
            List<TipoDependente> tiposRecuperados = getPortalContribunteFacade().getTipoDependenteFacade().lista();
            List<TipoDependente> tipos = Lists.newArrayList();
            for (TipoDependente tiposRecuperado : tiposRecuperados) {
                if (tiposRecuperado.getMostrarNoPortal()) {
                    tipos.add(tiposRecuperado);
                }
            }

            return new ResponseEntity<>(TipoDependente.toTiposDependentesDTO(tipos), HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<TipoDependenteDTO>>(new ArrayList<TipoDependenteDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-all-graus-de-parentesco", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<GrauDeParentescoDTO>> getAllGrausDeParentesco() {
        try {
            List<GrauDeParentescoDTO> grausDeParentesco = GrauDeParentesco.toGrauDeParentescoDTO(getPortalContribunteFacade().getGrauDeParentescoFacade().buscarGrauDeParentescoAtivo());
            return new ResponseEntity<>(grausDeParentesco, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<GrauDeParentescoDTO>>(new ArrayList<GrauDeParentescoDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-obrigatorios-cadastro",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DocumentoObrigatorioDTO>> buscarObrigatoriosCadastro() {
        try {
            List<DocumentoObrigatorioDTO> documentos = getPortalContribunteFacade().buscarDocumentosObrigatorioCadastro(null, TipoSolicitacaoCadastroPessoa.TRIBUTARIO);
            return new ResponseEntity<>(documentos, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-obrigatorios-cadastro-pf",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DocumentoObrigatorioDTO>> buscarObrigatoriosCadastroPF() {
        try {
            List<DocumentoObrigatorioDTO> documentos = getPortalContribunteFacade().buscarDocumentosObrigatorioCadastro(TipoPessoa.FISICA, TipoSolicitacaoCadastroPessoa.TRIBUTARIO);
            return new ResponseEntity<>(documentos, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-cids",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CidDTO>> getContratos(@RequestBody String parte) {
        try {
            List<CidDTO> cids = getPortalContribunteFacade().getCidFacade().buscarTodosCidDTO(parte);
            return new ResponseEntity<>(cids, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Erro: ", ex);
        }
        return new ResponseEntity<List<CidDTO>>(new ArrayList<CidDTO>(), HttpStatus.OK);

    }

    @RequestMapping(value = "/registrar-solicitacao-cadastro-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> registrarSolicitacaoCadastroPessoa(@RequestParam String cpf,
                                                                   @RequestParam String email) {
        try {
            getPortalContribunteFacade().registrarSolicitacaoCadastroPessoa(cpf, email);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-solicitacao-cadastro-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SolicitacaoCadastroPessoa> getSolicitacaoCadastroPessoa(@RequestParam String key) {
        SolicitacaoCadastroPessoa solicitacaoCadastroPessoa = getPortalContribunteFacade().getSolicitacaoCadastroPessoa(key);
        solicitacaoCadastroPessoa.setUsuarioRejeicao(null);
        return new ResponseEntity<>(solicitacaoCadastroPessoa, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-arquivos-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ArquivoDTO>> buscarArquivosDaPessoa(@RequestParam String cpfCnpj) {
        List<ArquivoDTO> arquivos = Lists.newArrayList();
        List<Pessoa> pessoas = getPortalContribunteFacade().getPessoaFacade().buscarPessoasPorCPFCNPJ(cpfCnpj, false);
        try {
            if (!pessoas.isEmpty()) {
                arquivos = getPortalContribunteFacade().buscarArquivosDaPessoa(pessoas.get(0).getId());
            }
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(arquivos, HttpStatus.OK);
    }
}
