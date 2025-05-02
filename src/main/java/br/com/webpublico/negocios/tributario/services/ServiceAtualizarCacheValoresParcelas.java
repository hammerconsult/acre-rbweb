package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.FiltroMaioresDevedores;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class ServiceAtualizarCacheValoresParcelas {

    public static final Logger logger = LoggerFactory.getLogger(ServiceAtualizarCacheValoresParcelas.class);
    @PersistenceContext
    protected transient EntityManager em;
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;

    @PostConstruct
    private void init() {
        try {
            configuracaoDeRelatorioFacade = (ConfiguracaoDeRelatorioFacade) new InitialContext().lookup("java:module/ConfiguracaoDeRelatorioFacade");
        } catch (NamingException e) {
            logger.error(e.getExplanation());
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 12)
    public void atualizarViaMaioresDevedores() {
        try {
            logger.debug("............ Iniciando a rotina de atualização dos valores das parcelas");
            logger.debug("............ Consultando maiores Devedores ");
            FiltroMaioresDevedores filtro = new FiltroMaioresDevedores(null);
            filtro.setDataRelatorio(new Date());
            filtro.setVencimentoInicial(DataUtil.getDateParse("01/01/1990"));
            filtro.setVencimentoFinal(DataUtil.getDateParse("01/01/2020"));
            filtro.setQtdeDevedores(999);
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("vencimentoInicial", DataUtil.getDataFormatada(filtro.getVencimentoInicial()));
            dto.adicionarParametro("vencimentoFinal", DataUtil.getDataFormatada(filtro.getVencimentoFinal()));
            dto.adicionarParametro("qtdeDevedores", filtro.getQtdeDevedores());
            dto.setApi("tributario/maiores-devedores/atualizar-valores/");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
            ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
            ResponseEntity<Number> responseEntity =  new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, Number.class);
            Number qtdeAtualizada = responseEntity.getBody();
            logger.info("............ retornou --->  " + qtdeAtualizada);
        } catch (Exception e) {
            logger.error("............ naõ foi possível atualizar o cache de valores da parcela via maiores Devedores ");
        }
    }

}
