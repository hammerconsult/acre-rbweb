package br.com.webpublico.pncp.service;

import br.com.webpublico.negocios.*;
import br.com.webpublico.pncp.dto.*;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ContratacaoService extends PncpService {

    private final Logger logger = LoggerFactory.getLogger(ContratacaoService.class);
    private final String URL_CONTRATACOES = "/api/v1/contratacoes/";
    private final String URL_DOCUMENTOS = "/api/v1/documentos/";
    private final String URL_ITENS = "/api/v1/itens/";
    private LicitacaoFacade licitacaoFacade;
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    private ProcessoDeCompraFacade processoDeCompraFacade;

    @PostConstruct
    public void init() {
        super.init();
        try {
            licitacaoFacade = (LicitacaoFacade) Util.getFacadeViaLookup("java:module/LicitacaoFacade");
            processoDeCompraFacade = (ProcessoDeCompraFacade) Util.getFacadeViaLookup("java:module/ProcessoDeCompraFacade");
            ataRegistroPrecoFacade = (AtaRegistroPrecoFacade) Util.getFacadeViaLookup("java:module/AtaRegistroPrecoFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

//    public List<AtaRegistroDTO> buscarAtas(FiltroAtaRegistroPrecoDto filtroAtaRegistroPrecoDto) {
//       return ataRegistroPrecoFacade.buscarAtasPncpPorData(filtroAtaRegistroPrecoDto);
//    }
//
//    public List<ContratacaoDTO> buscarContratacoes(FiltroContratacaoDTO filtroContratacaoDTO) {
//        return licitacaoFacade.buscarLicitacoesPncpPorLeiEData(filtroContratacaoDTO);
//    }
//
//    public List<ResultadoItemDTO> obterResultadosItensContratacao(ContratacaoDTO contratacao) {
//        return processoDeCompraFacade.buscarResultadosItensContratacao(contratacao);
//    }
}
