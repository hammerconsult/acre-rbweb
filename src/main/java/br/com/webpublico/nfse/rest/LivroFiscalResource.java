package br.com.webpublico.nfse.rest;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.nfse.domain.dtos.ConsultaLivroFiscalNfseDTO;
import br.com.webpublico.nfse.domain.dtos.LivroFiscalCompetenciaNfseDTO;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import com.beust.jcommander.internal.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

@RequestMapping("/nfse")
@Controller
public class LivroFiscalResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(LivroFiscalResource.class);

    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;

    @PostConstruct
    public void init() {
        try {
            InitialContext initialContext = new InitialContext();
            declaracaoMensalServicoFacade = (DeclaracaoMensalServicoFacade) initialContext.lookup("java:module/DeclaracaoMensalServicoFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/competencias-livro-fiscal", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LivroFiscalCompetenciaNfseDTO> buscarCompetenciasLivroFiscal(@RequestBody ConsultaLivroFiscalNfseDTO consulta) {
        try {
            List<LivroFiscalCompetenciaNfseDTO> competencias =
                declaracaoMensalServicoFacade.buscarCompetenciasLivroFiscal(consulta.getPrestadorId(),
                    consulta.getExercicioInicial(), consulta.getExercicioFinal(),
                    consulta.getMesInicial(), consulta.getMesFinal(), TipoMovimentoMensal.valueOf(consulta.getTipoMovimento().name()));
            return new ResponseEntity(competencias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/gerar-relatorio-livro-fiscal",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> gerarRelatorioLivroFiscal(@RequestBody LivroFiscalCompetenciaNfseDTO dto) {
        byte[] bytes = new byte[0];
        try {
            CadastroEconomico cadastroEconomico = declaracaoMensalServicoFacade
                .getCadastroEconomicoFacade().recuperar(dto.getPrestadorId());

            String jasper = declaracaoMensalServicoFacade.getJasperLivroFiscal(dto);

            bytes = declaracaoMensalServicoFacade.gerarImprimeRelatorioLivroFiscal(dto, cadastroEconomico).
                gerarRelatorio(jasper, Lists.newArrayList(dto), false);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }
}
