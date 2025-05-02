package br.com.webpublico.nfse.rest;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.negocios.DAMFacade;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.List;

@RequestMapping("/nfse")
@Controller
public class DeclaracaoMensalServicoResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(DeclaracaoMensalServicoResource.class);
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;
    private DAMFacade damFacade;
    private ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            declaracaoMensalServicoFacade = (DeclaracaoMensalServicoFacade) new InitialContext().lookup("java:module/DeclaracaoMensalServicoFacade");
            damFacade = (DAMFacade) new InitialContext().lookup("java:module/DAMFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/gerar-dam",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> gerarDam(@RequestParam(value = "id") Long
                                               id, @RequestParam(value = "prestadorId") Long prestadorId) {
        byte[] bytes = new byte[0];
        List<ResultadoParcela> parcelas = declaracaoMensalServicoFacade.recuperarDebitosDaDecalaracao(id, prestadorId);
        try {
            List<DAM> dams = damFacade.gerarDAMPortalWeb(parcelas);
            bytes = damFacade.gerarImpressaoDAMNfse(dams);
        } catch (Exception e) {
            logger.error("Erro ao gerar o dam de dms", e);
        }
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }
}
