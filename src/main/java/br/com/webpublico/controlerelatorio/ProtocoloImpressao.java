package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Processo;
import br.com.webpublico.entidades.Tramite;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Fabio
 */
public class ProtocoloImpressao extends AbstractReport {

    private static Logger logger = LoggerFactory.getLogger(ProtocoloImpressao.class);
    private Date dataOperacao;

    public ProtocoloImpressao() {
        try {
            SistemaService sistemaService = (SistemaService) Util.getSpringBeanPeloNome("sistemaService");
            dataOperacao = sistemaService.getDataOperacao();
        } catch (Exception e) {
            logger.error("Não voi possivel pega a data de operação para oa impressão do termo de recebimento", e);
        }
        geraNoDialog = true;
    }

    public void geraGuiaRecebimentoProtocolo(Processo p) throws JRException, IOException {
        HashMap parameters = new HashMap();
        String arquivoJasper = "guiaRecebimentoProtocolo.jasper";
        if (p.getEncaminhamentoMultiplo()) {
            arquivoJasper = "guiaRecebimentoProtocolosMultiplos.jasper";
        }
        parameters.put("SUBREPORT_DIR", getCaminho());
        if (p.getProcessoSuperior() == null) {
            parameters.put("PROCESSO", new BigDecimal(p.getId()));
        } else {
            parameters.put("PROCESSO", new BigDecimal(p.getProcessoSuperior().getId()));
        }
        parameters.put("IMAGEM", getCaminhoImagem());
        gerarRelatorio(arquivoJasper, parameters);
    }

    public void gerarGuiaRecebimentoProcesso(Processo p) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("PROCESSO", p.getId());
        dto.adicionarParametro("TRAMITE", p.getTramites().get(p.getTramites().size() - 1).getId());
        dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(p.getDataRegistro()));
        dto.setNomeRelatorio("GUIA DE RECEBIMENTO DO PROCESSO");
        dto.setApi("administrativo/guia-recebimento-processo/");
        ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
        FacesUtil.addMensagemRelatorioSegundoPlano();
    }

    public void gerarGuiaRecebimentoTramite(Tramite tra, boolean multiplos) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("PROCESSO", tra.getProcesso().getId());
        dto.adicionarParametro("TRAMITE", tra.getId());
        dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(dataOperacao));
        dto.adicionarParametro("multiplos", multiplos);
        dto.setNomeRelatorio("GUIA DE RECEBIMENTO DE DOCUMENTO");
        dto.setApi("administrativo/protocolo/");
        ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
        FacesUtil.addMensagemRelatorioSegundoPlano();
    }

    public void imprimirVariosTramitesEmUnicoArquivo(List<Tramite> tramitesSelecionados) {
        RelatorioDTO dto = new RelatorioDTO();
        List<RelatorioDTO> dtos = Lists.newArrayList();
        for (Tramite tramite : tramitesSelecionados) {
            RelatorioDTO relatorio = new RelatorioDTO();
            relatorio.setNomeParametroBrasao("IMAGEM");
            relatorio.adicionarParametro("PROCESSO", tramite.getProcesso().getId());
            relatorio.adicionarParametro("TRAMITE", tramite.getId());
            relatorio.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(dataOperacao));
            relatorio.setNomeRelatorio("GUIA DE RECEBIMENTO DE DOCUMENTO");
            dtos.add(relatorio);
        }
        dto.adicionarParametro("relatoriosDtos", dtos);
        dto.setNomeRelatorio("GUIA DE RECEBIMENTO DE DOCUMENTO");
        dto.setApi("administrativo/protocolo/multiplos-mesmo-arquivo/");
        ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
        FacesUtil.addMensagemRelatorioSegundoPlano();
    }

    public void geraListaParecerProtocoloProcesso(Processo processo) throws JRException, IOException {
        String arquivoJasper = "listaPareceresProcesso.jasper";
        if (processo.getProtocolo()) {
            arquivoJasper = "listaPareceresProtocolo.jasper";
        }
        HashMap parameters = new HashMap();
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("PROCESSO", new BigDecimal(processo.getId()));
        parameters.put("IMAGEM", getCaminhoImagem());
        gerarRelatorio(arquivoJasper, parameters);
    }

    public void gerarGuiaRecebimentoMultiplo(List<Tramite> tramites, String destino) throws JRException, IOException {
        String clausula = "";
        for (Tramite tramite : tramites) {
            if ("".equals(clausula)) {
                clausula = tramite.getId().toString();
            } else {
                clausula = clausula + "," + tramite.getId();
            }
        }
        String arquivoJasper = "guiaRecebimentoMultiplo.jasper";
        HashMap parameters = new HashMap();
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("TRAMITES", clausula);
        parameters.put("DESTINO", destino);
        parameters.put("IMAGEM", getCaminhoImagem());
        gerarRelatorio(arquivoJasper, parameters);
    }
}
