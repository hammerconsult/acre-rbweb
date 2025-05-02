package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.Agencia;
import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.enums.TipoSolicitacaoCadastroPessoa;
import br.com.webpublico.pessoa.dto.AgenciaDTO;
import br.com.webpublico.pessoa.dto.DocumentoObrigatorioDTO;
import br.com.webpublico.pessoa.dto.HierarquiaOrganizacionalDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/integracao/tributario")
public class SolicitacaoCadastroCredorRestController extends PortalRestController {

    @RequestMapping(value = "/imprimir-dam-credor", method = RequestMethod.GET)
    public ResponseEntity<byte[]> imprimirDamCredor(@RequestParam String key) {
        try {
            Calculo calculo = getPortalContribunteFacade().gerarDebitoSolicitacaoCadastroCredor(key);
            return new ResponseEntity<>(getPortalContribunteFacade().getSolicitacaoCadastroPessoaFacade().gerarBytesDAM(calculo, key), HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Erro inesperado ao efetuar a solicitação de cadastro.", e);
            return null;
        }
    }

    @RequestMapping(value = "/buscar-bytes-dam-credor", method = RequestMethod.GET)
    public ResponseEntity<byte[]> buscarBytesDamCredor(@RequestParam String key) {
        try {
            return new ResponseEntity<>(getPortalContribunteFacade().getSolicitacaoCadastroPessoaFacade().gerarBytesDAM(null, key), HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Erro inesperado ao efetuar a solicitação de cadastro.", e);
            return null;
        }
    }

    @RequestMapping(value = "/verificar-pessoa-rejeitada", method = RequestMethod.GET)
    public ResponseEntity<Boolean> verificarPessoaRejeitada(@RequestParam String key) {
        try {
            return new ResponseEntity<>(getPortalContribunteFacade().verificarPessoaRejeitada(key), HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Erro inesperado ao efetuar a solicitação de cadastro.", e);
            return null;
        }
    }

    @RequestMapping(value = "/get-all-hierarquias-orcamentarias", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<HierarquiaOrganizacionalDTO>> getAllHierarquiasOrcamentariasVigentes() {
        try {
            List<HierarquiaOrganizacionalDTO> hierarquias = HierarquiaOrganizacional.toHierarquiaOrganizacionalDTOs(
                getPortalContribunteFacade().getHierarquiaOrganizacionalFacade().listaTodasPorNivel("", "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), null)
            );
            return new ResponseEntity<>(hierarquias, HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<HierarquiaOrganizacionalDTO>>(new ArrayList<HierarquiaOrganizacionalDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-agencias-por-banco", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<AgenciaDTO>> getAgenciasPorBanco(@RequestParam(value = "idBanco") Long idBanco) {
        try {
            return new ResponseEntity<>(Agencia.toAgenciaDTOs(getPortalContribunteFacade().getAgenciaFacade().buscarAgenciasPorIdBanco(idBanco)), HttpStatus.OK);
        } catch (Exception ex) {
            getLogger().error("Exception", ex);
        }
        return new ResponseEntity<List<AgenciaDTO>>(new ArrayList<AgenciaDTO>(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-obrigatorios-cadastro-pf-credor",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DocumentoObrigatorioDTO>> buscarObrigatoriosCadastroPFCredor() {
        try {
            List<DocumentoObrigatorioDTO> documentos = getPortalContribunteFacade().buscarDocumentosObrigatorioCadastro(TipoPessoa.FISICA, TipoSolicitacaoCadastroPessoa.CONTABIL);
            return new ResponseEntity<>(documentos, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-obrigatorios-cadastro-pj-credor",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DocumentoObrigatorioDTO>> buscarObrigatoriosCadastroPJCredor() {
        try {
            List<DocumentoObrigatorioDTO> documentos = getPortalContribunteFacade().buscarDocumentosObrigatorioCadastro(TipoPessoa.JURIDICA, TipoSolicitacaoCadastroPessoa.CONTABIL);
            return new ResponseEntity<>(documentos, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/registrar-solicitacao-cadastro-credor",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> registrarSolicitacaoCadastroCredor(@RequestParam String cpf,
                                                                   @RequestParam String email) {
        try {
            getPortalContribunteFacade().registrarSolicitacaoCadastroCredor(cpf, email);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
