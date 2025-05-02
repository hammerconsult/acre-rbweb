package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.negocios.PortalContribunteFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.negocios.comum.TermoUsoFacade;
import br.com.webpublico.seguranca.SingletonMetricas;
import br.com.webpublico.seguranca.service.SistemaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by clovis on 29/08/2016.
 */
public class PortalRestController implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PortalRestController.class);

    @Autowired
    protected SistemaService sistemaService;
    private PortalContribunteFacade portalContribunteFacade;
    private TermoUsoFacade termoUsoFacade;
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @Autowired
    public SingletonMetricas singletonMetricas;


    public PortalContribunteFacade getPortalContribunteFacade() {
        if (portalContribunteFacade == null) {
            try {
                portalContribunteFacade = (PortalContribunteFacade) new InitialContext().lookup("java:module/PortalContribunteFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return portalContribunteFacade;
    }

    public ConfiguracaoDeRelatorioFacade getConfiguracaoDeRelatorioFacade() {
        if (configuracaoDeRelatorioFacade == null) {
            try {
                configuracaoDeRelatorioFacade = (ConfiguracaoDeRelatorioFacade) new InitialContext().lookup("java:module/ConfiguracaoDeRelatorioFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return configuracaoDeRelatorioFacade;
    }

    public TermoUsoFacade getTermoUsoFacade() {
        if (termoUsoFacade == null) {
            try {
                termoUsoFacade = (TermoUsoFacade) new InitialContext().lookup("java:module/TermoUsoFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return termoUsoFacade;
    }

    public static Logger getLogger() {
        return logger;
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
        arquivo.setDescricao(((String) lhm.get("descricao")));
        arquivo.setMimeType(((String) lhm.get("mineType")));
        arquivo.setTamanho(lhm.get("tamanho") != null ? ((Integer) lhm.get("tamanho")).longValue() : null);
        return arquivo;
    }
}
