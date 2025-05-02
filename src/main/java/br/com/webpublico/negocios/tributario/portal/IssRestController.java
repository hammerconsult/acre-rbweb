package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.ProcessoCalculoISS;
import br.com.webpublico.nfse.domain.dtos.IssqnFmLancamentoNfseDTO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
@RequestMapping("/integracao/tributario")
public class IssRestController extends PortalRestController {

    @RequestMapping(value = "/lancar-iss-fora-municipio",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> lancarIssForaMunicipio(@RequestBody IssqnFmLancamentoNfseDTO lancamento) {
        try {
            ProcessoCalculoISS processoCalculoISS = getPortalContribunteFacade().getCalculaISSFacade().calcularIssForaMunicipio(lancamento);
            getPortalContribunteFacade().getCalculaISSFacade().lancarDebitoIssForaMunicipio(processoCalculoISS);
            List<ResultadoParcela> parcelas = getPortalContribunteFacade().getConsultaDebitoFacade().buscarParcelasCalculo(processoCalculoISS.getCalculos().get(0));
            List<DAM> dams = getPortalContribunteFacade().gerarDAMPortalWeb(parcelas);
            ByteArrayOutputStream dam = getPortalContribunteFacade().getConsultaDebitoFacade().gerarImpressaoDAMPortal(Lists.newArrayList(dams));
            AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
            assistente.setDescricaoProcesso("Enviando dam de issqn fora do município por e-mail.");
            getPortalContribunteFacade().getCalculaISSFacade().enviarEmailDamIssForaMunicipio(assistente, lancamento, dam);
            return ResponseEntity.ok(dam.toByteArray());
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
