package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.tributario.enumeration.MotivoDevolucaoArquivoCEDO;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.rh.TipoPessoaDTO;
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelatorioArquivoCEDODTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorioArquivoCedo", pattern = "/tributario/relatorio/arquivo-cedo/",
                viewId = "/faces/tributario/iptu/arquivocedo/relatorioarquivocedo.xhtml"),
})

public class RelatorioArquivoCedoControlador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioArquivoCedoControlador.class);

    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioArquivoCEDODTO filtro;
    private Pessoa contribuinte;

    @URLAction(mappingId = "relatorioArquivoCedo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void inicializaFiltro() {
        filtro = new FiltroRelatorioArquivoCEDODTO();
    }

    public FiltroRelatorioArquivoCEDODTO getFiltro() {
        return filtro;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        if (contribuinte != null) {
            filtro.setIdPessoaProprietario(contribuinte.getId());
        } else {
            filtro.setIdPessoaProprietario(null);
        }
        this.contribuinte = contribuinte;
    }

    public void limparCampos() {
        filtro.limparCampos();
        contribuinte = null;
    }

    public String getCaminhoPadrao() {
        return "/tributario/relatorio/arquivo-cedo/";
    }

    public List<SelectItem> getTiposPessoa() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPessoaDTO tipo : TipoPessoaDTO.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public static List<SelectItem> getTipoRelatorio() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(false, "Resumido"));
        toReturn.add(new SelectItem(true, "Detalhado"));
        return toReturn;
    }

    public static List<SelectItem> getMotivoRetorno() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (MotivoDevolucaoArquivoCEDO tipo : MotivoDevolucaoArquivoCEDO.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    private RelatorioDTO montarParametrosComuns() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeRelatorio("Relatório do arquivo CEDO dos correios " + (filtro.getRelatorioDetalhado() ? "detalhado" : "resumido"));
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
        dto.adicionarParametro("NOME_RELATORIO", "Relatório de Controle Eletrônico de Devolução Objetos dos Correios - CEDO");
        dto.adicionarParametro("FILTRO", filtro);
        return dto;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = montarParametrosComuns();
            TipoRelatorioDTO tipoRelatorio = TipoRelatorioDTO.valueOf(tipoRelatorioExtensao);
            dto.setTipoRelatorio(tipoRelatorio);
            dto.setApi("tributario/arquivo-cedo/" + (TipoRelatorioDTO.XLS.equals(tipoRelatorio) || TipoRelatorioDTO.CSV.equals(tipoRelatorio) ? "excel/" : ""));
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException ex) {
            logger.error("Erro ao gerar relatório. ", ex);
            FacesUtil.addWarn(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), ex.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao gerar relatório. ", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }
}
