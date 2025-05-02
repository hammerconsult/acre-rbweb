package br.com.webpublico.negocios.tributario.consultaparcela.rest;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.consultaparcela.DTO.AgrupadorParcelas;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.tributario.consultadebitos.ConsultarDebitos;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.dtos.DamDTO;
import br.com.webpublico.tributario.consultadebitos.dtos.PagamentoDTO;
import br.com.webpublico.tributario.consultadebitos.dtos.ParcelaDTO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;


@RequestMapping("/tributario")
@Controller
public class ConsultaDebitoResource {

    private static Logger logger = LoggerFactory.getLogger(ConsultaDebitoResource.class);
    private DAMFacade damFacade;
    private ConsultaDebitoFacade consultaDebitoFacade;
    private LoteBaixaFacade loteBaixaFacade;
    private NotaFiscalFacade notaFiscalFacade;
    private PagamentoInternetBankingFacade pagamentoInternetBankingFacade;


    @PostConstruct
    public void init() {
        try {
            damFacade = (DAMFacade) new InitialContext().lookup("java:module/DAMFacade");
            consultaDebitoFacade = (ConsultaDebitoFacade) new InitialContext().lookup("java:module/ConsultaDebitoFacade");
            loteBaixaFacade = (LoteBaixaFacade) new InitialContext().lookup("java:module/LoteBaixaFacade");
            notaFiscalFacade = (NotaFiscalFacade) new InitialContext().lookup("java:module/NotaFiscalFacade");
            pagamentoInternetBankingFacade = (PagamentoInternetBankingFacade) new InitialContext().lookup("java:module/PagamentoInternetBankingFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/consultar-debito", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ParcelaDTO>> consultar(@RequestBody LinkedHashMap param) {
        try {
            List<ParcelaDTO> retorno = Lists.newArrayList();
            ConsultarDebitos consultaDebito = new ObjectMapper().convertValue(param, ConsultarDebitos.class);
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.copiarConsultaDebitosToConsultaParcela(consultaDebito);
            if (!consultaDebito.getFiltros().isEmpty()) {
                consultaParcela.executaConsulta();
                List<ResultadoParcela> resultados = consultaParcela.getResultados();
                for (ResultadoParcela resultado : resultados) {
                    ParcelaDTO parcelaDTO = resultado.toParcelaDTO();
                    parcelaDTO.setEnderecoCadastro(consultaDebitoFacade.getEnderecoFacade().montarEnderecoCadastroImobiliarioIntegracaoBB(parcelaDTO.getIdCadastro()));
                    parcelaDTO.setDivida(resultado.getDivida());
                    retorno.add(parcelaDTO);
                }
            }
            return new ResponseEntity(retorno, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro {}", e);
            return new ResponseEntity(null, setMessageError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/consultar-debito-por-nota/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ParcelaDTO>> consultarPorNota(@PathVariable("id") Long id) {
        try {
            logger.debug("consultar-debito-por-nota " + id);
            List<ParcelaDTO> retorno = Lists.newArrayList();
            List<ResultadoParcela> resultados = notaFiscalFacade.buscarParcelasDaNota(id);
            for (ResultadoParcela resultado : resultados) {
                ParcelaDTO parcelaDTO = resultado.toParcelaDTO();
                DAM dam = damFacade.recuperaUltimoDamParcela(resultado.getIdParcela());
                parcelaDTO.setNumeroDam(dam != null ? dam.getNumeroDAM() : "");
                retorno.add(parcelaDTO);
            }
            return new ResponseEntity(retorno, HttpStatus.OK);
        } catch (Exception e) {
            logger.debug("Erro {}", e.getMessage());
            return new ResponseEntity(null, setMessageError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/consultar-dam", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<DamDTO> buscarDam(@RequestBody AgrupadorParcelas agrupador) {
        try {

            if (agrupador.getParcelas().size() == 1) {
                List<ResultadoParcela> resultados = new ConsultaParcela()
                    .addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.PARCELA_ID,
                        br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, agrupador.getParcelas().get(0).getIdParcela())
                    .executaConsulta().getResultados();
                DAM dam = damFacade.buscarOuGerarDam(resultados.get(0));
                return new ResponseEntity(dam.toDamDTO(), HttpStatus.OK);
            } else {
                Calendar c = DataUtil.ultimoDiaMes(new Date());
                Date vencimentoDam = c.getTime();

                List<ParcelaValorDivida> parcelas = Lists.newArrayList();
                for (ParcelaDTO parcela : agrupador.getParcelas()) {
                    parcelas.add(consultaDebitoFacade.recuperaParcela(parcela.getIdParcela()));
                }
                DAM dam = damFacade.geraDAM(parcelas, vencimentoDam);
                return new ResponseEntity(dam.toDamDTO(), HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Erro {}", e);
            return new ResponseEntity(null, setMessageError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/enviar-dam", method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> enviarDam(@RequestBody AgrupadorParcelas agrupadorParcelas, HttpServletRequest request, HttpServletResponse response) {
        try {

            FacesUtil.ciarFacesContext(request, response);

            DamDTO dam = buscarDam(agrupadorParcelas).getBody();
            String[] emails = agrupadorParcelas.getEmails().split(",");
            consultaDebitoFacade.enviarEmailsDAMs(Lists.newArrayList(dam), emails);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/gerar-dam",
        method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<byte[]> gerarDam(@RequestBody DamDTO damDTO) {
        byte[] bytes = new byte[0];
        try {
            DAM dam = damFacade.recuperarDamComCalculos(damDTO.getId());
            if (DAM.Tipo.UNICO.equals(dam.getTipo())) {
                bytes = consultaDebitoFacade.gerarImpressaoDAMPortal(Lists.newArrayList(dam)).toByteArray();
            } else {
                bytes = new ImprimeDAM().gerarByteImpressaoDamUnicoViaApi(Lists.newArrayList(dam));
            }
        } catch (Exception e) {
            logger.error("Erro ao emitir o DAM: ", e);
        }
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    @RequestMapping(value = "/pagar-dam",
        method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<PagamentoDTO> pagarDam(@RequestBody PagamentoDTO pagamentoDTO) {
        try {
            pagamentoInternetBankingFacade.registrarPagamento(pagamentoDTO);
            return new ResponseEntity<>(pagamentoDTO, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao pagar dam  ", e);
            HttpHeaders header = new HttpHeaders();
            header.add("error-message", e.getMessage());
            return new ResponseEntity<>(pagamentoDTO, header, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/estornar-pagamento-dam",
        method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<PagamentoDTO> estornarPagamentoDam(@RequestBody PagamentoDTO pagamentoDTO) {
        try {

            loteBaixaFacade.estornarPagamentoInternetBanking(pagamentoDTO);
            return new ResponseEntity<>(pagamentoDTO, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao pagar dam  ", e);
            return new ResponseEntity<>(pagamentoDTO, HttpStatus.BAD_REQUEST);

        }
    }
}
