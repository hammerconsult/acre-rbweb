package br.com.webpublico.negocios.rh;

import br.com.webpublico.entidades.rh.AtualizacaoDadosRBPonto;
import br.com.webpublico.entidades.rh.webservices.ConfiguracaoWebServiceRH;
import br.com.webpublico.entidadesauxiliares.rh.integracaoponto.EditarFeriasDTO;
import br.com.webpublico.entidadesauxiliares.rh.integracaoponto.ExcluirFeriasDTO;
import br.com.webpublico.enums.rh.StatusIntegracaoRBPonto;
import br.com.webpublico.enums.rh.TipoInformacaoEnvioRBPonto;
import br.com.webpublico.enums.rh.TipoOperacaoIntegracaoRBPonto;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoWSRHFacade;
import br.com.webpublico.negocios.rh.integracaoponto.AtualizacaoDadosRBPontoFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.List;


@Service
public class AtualizacaoDadosRBPontoService {

    private static final Logger logger = LoggerFactory.getLogger(br.com.webpublico.negocios.tributario.services.IntegracaoRedeSimService.class);

    private AtualizacaoDadosRBPontoFacade atualizacaoDadosRBPontoFacade;
    private ConfiguracaoWSRHFacade configuracaoWSRHFacade;

    @PostConstruct
    private void init() {
        try {
            atualizacaoDadosRBPontoFacade = (AtualizacaoDadosRBPontoFacade) new InitialContext().lookup("java:module/AtualizacaoDadosRBPontoFacade");
            configuracaoWSRHFacade = (ConfiguracaoWSRHFacade) new InitialContext().lookup("java:module/ConfiguracaoWSRHFacade");
        } catch (NamingException e) {
            logger.error("Injecao de facades falhou {}", e);
        }
    }

    public void reprocessarIntegradosComErro() {
        List<AtualizacaoDadosRBPonto> integradosComErro = atualizacaoDadosRBPontoFacade.buscarPorStatus(StatusIntegracaoRBPonto.INTEGRADO_COM_ERRO);
        ConfiguracaoWebServiceRH configuracaoWebServiceRH = configuracaoWSRHFacade.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.PONTO);
        integradosComErro.forEach(integradoComErro -> {
            if (TipoInformacaoEnvioRBPonto.FERIAS.equals(integradoComErro.getTipoInformacao())) {
                if (TipoOperacaoIntegracaoRBPonto.EDICAO.equals(integradoComErro.getTipoOperacao())) {
                    EditarFeriasDTO editarFeriasDTO;
                    try {
                        editarFeriasDTO = Util.objectFromJsonString(integradoComErro.getRequisicao(), EditarFeriasDTO.class);
                    } catch (IOException ioException) {
                        integradoComErro.setLog(ioException.getMessage());
                        atualizacaoDadosRBPontoFacade.salvar(integradoComErro);
                        return;
                    }
                    atualizacaoDadosRBPontoFacade.atualizarFerias(editarFeriasDTO, configuracaoWebServiceRH, integradoComErro);
                }
                if (TipoOperacaoIntegracaoRBPonto.EXCLUSAO.equals(integradoComErro.getTipoOperacao())) {
                    ExcluirFeriasDTO excluirFeriasDTO;
                    try {
                        excluirFeriasDTO = Util.objectFromJsonString(integradoComErro.getRequisicao(), ExcluirFeriasDTO.class);
                    } catch (IOException ioException) {
                        integradoComErro.setLog(ioException.getMessage());
                        atualizacaoDadosRBPontoFacade.salvar(integradoComErro);
                        return;
                    }
                    atualizacaoDadosRBPontoFacade.excluirFerias(excluirFeriasDTO, configuracaoWebServiceRH, integradoComErro);
                }
            }
        });
    }
}
