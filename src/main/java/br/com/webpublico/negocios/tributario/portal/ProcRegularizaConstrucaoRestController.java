package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.AlvaraConstrucao;
import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.entidades.Habitese;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.webreportdto.dto.comum.HtmlPdfDTO;
import br.com.webpublico.ws.model.WsHabitese;
import br.com.webpublico.ws.model.WsProcRegularizaConstrucao;
import com.google.common.base.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/integracao/tributario/processo-regularizacao-construcao")
public class ProcRegularizaConstrucaoRestController extends PortalRestController {

    @RequestMapping(value = "/imprimirHabitese",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> imprimirHabitese(@RequestParam(value = "id") String id) {
        try {
            Habitese habitese = getPortalContribunteFacade().getHabiteseConstrucaoFacade().recuperar(Long.parseLong(id));
            getPortalContribunteFacade().getHabiteseConstrucaoFacade().atualizarDocumentoOficial(habitese);
            return new ResponseEntity<>(habitese.getDocumentoOficial().getConteudo().getBytes(StandardCharsets.UTF_8), HttpStatus.OK);
        } catch (ValidacaoException ve) {
            return new ResponseEntity<>(ve.getMensagens().get(0).getDetail().getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/imprimirAlvara",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> imprimirAlvara(@RequestParam(value = "id") String id) {
        try {
            AlvaraConstrucao alvara = getPortalContribunteFacade().getAlvaraConstrucaoFacade().recuperar(Long.parseLong(id));
            getPortalContribunteFacade().getAlvaraConstrucaoFacade().atualizarDocumentoOficial(alvara);
            return new ResponseEntity<>(alvara.getDocumentoOficial().getConteudo().getBytes(StandardCharsets.UTF_8), HttpStatus.OK);
        } catch (ValidacaoException ve) {
            return new ResponseEntity<>(ve.getMensagens().get(0).getDetail().getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/consultar",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WsProcRegularizaConstrucao>> consultar(@RequestParam(value = "cpfCnpj", required = true) String cpfCnpj,
                                                                      @RequestParam(value = "anoProtocolo", required = false) String anoProtocolo,
                                                                      @RequestParam(value = "numeroProtocolo", required = false) String numeroProtocolo,
                                                                      @RequestParam(value = "inscricaoCadastral", required = true) String inscricaoCadastral,
                                                                      @RequestParam(value = "codigo", required = true) Integer codigo,
                                                                      @RequestParam(value = "ano", required = true) Integer ano,
                                                                      @RequestParam(value = "dataCriacaoProcesso", required = true) String dataCriacaoProcesso) {
        sistemaService.setUsuarioAlternativo(cpfCnpj);
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.CONSULTA_PROCESSO_REGULARIZACAO_CONSTRUCAO, cpfCnpj);
            List<WsProcRegularizaConstrucao> processos = getPortalContribunteFacade().buscarProcessosRegularizacaoConstrucao(cpfCnpj, anoProtocolo, numeroProtocolo, inscricaoCadastral, codigo, ano, new SimpleDateFormat("dd/MM/yyyy").parse(dataCriacaoProcesso));
            for (WsProcRegularizaConstrucao processo : processos) {
                if(processo.getAlvara()!=null) {
                    processo.getAlvara().setProcRegularizaConstrucao(null);
                    for (WsHabitese habitese : processo.getAlvara().getHabiteses()) {
                        habitese.setAlvaraConstrucao(null);
                    }
                }
            }
            return new ResponseEntity<>(processos, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
