package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Equipamento;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorioDetalhamentoEquipamento", pattern = "/detalhamento-equipamento/",
            viewId = "/faces/administrativo/frota/relatorios/relatorioDetalhamentoEquipamento.xhtml")
})
public class RelatorioDetalhamentoEquipamentoControlador {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioDetalhamentoEquipamentoControlador.class);
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private StringBuffer filtro;
    private Equipamento equipamento;

    @URLAction(mappingId = "relatorioDetalhamentoEquipamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparFiltros() {
        equipamento = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            String nomeRelatorio = "DETALHAMENTO DE EQUIPAMENTO";
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "FROTAS");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("condicao", montarCondicaoEFiltros());
            dto.adicionarParametro("FILTROS", filtro.toString());
            dto.adicionarParametro("usuarioSistema", sistemaFacade.getUsuarioCorrente().getId());
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/detalhamento-equipamento/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicaoEFiltros() {
        StringBuilder condicao = new StringBuilder();
        filtro = new StringBuffer();

        if (getEquipamento() != null && getEquipamento().getId() != null) {
            condicao.append(" and e.id = ").append(getEquipamento().getId());
            filtro.append("Equipamento: ").append(getEquipamento());
        }
        return condicao.toString();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public StringBuffer getFiltro() {
        return filtro;
    }

    public void setFiltro(StringBuffer filtro) {
        this.filtro = filtro;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    public void limparCampos() {
        equipamento = null;
    }
}
