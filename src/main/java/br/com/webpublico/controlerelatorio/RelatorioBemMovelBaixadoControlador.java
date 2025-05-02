package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioBemMovelBaixado;
import br.com.webpublico.enums.TipoBaixa;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoBemFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Arrays;
import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorioBemMovelBaixado",
        pattern = "/relatorio-bem-movel-baixado/",
        viewId = "/faces/administrativo/relatorios/relatorio-bem-movel-baixado.xhtml")}
)
public class RelatorioBemMovelBaixadoControlador {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    private FiltroRelatorioBemMovelBaixado filtros;

    @URLAction(mappingId = "relatorioBemMovelBaixado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtros = new FiltroRelatorioBemMovelBaixado();
        filtros.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE BEM MÓVEL BAIXADO");
            dto.adicionarParametro("condicao", filtros.getCondicao());
            dto.adicionarParametro("dataInicial", DataUtil.getDataFormatada(filtros.getDataInicial()));
            dto.adicionarParametro("dataFinal", DataUtil.getDataFormatada(filtros.getDataFinal()));
            dto.adicionarParametro("FILTROS", filtros.getFiltrosUtilizados());
            dto.setNomeRelatorio("RELATORIO-DE-BEM-MÓVEL-BAIXADO");
            dto.setApi("administrativo/bem-movel-baixado/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<GrupoBem> completarGrupoPatrimonial(String parte) {
        return grupoBemFacade.buscarGrupoBemPorTipoBem(parte.trim(), TipoBem.MOVEIS);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaAdministrativa(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaDoUsuario(parte, null,
            sistemaFacade.getDataOperacao(), sistemaFacade.getUsuarioCorrente(), null, Boolean.TRUE);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(parte, null,
            sistemaFacade.getDataOperacao(), sistemaFacade.getUsuarioCorrente(), filtros.getHierarquiaAdministrativa());
    }

    public List<SelectItem> getTiposDeBaixa() {
        return Util.getListSelectItem(Arrays.asList(TipoBaixa.values()));
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtros.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial deve ser informado.");
        }
        if (filtros.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final deve ser informado.");
        }
        ve.lancarException();
        if (filtros.getDataFinal().before(filtros.getDataInicial())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data final deve ser posterior a data inicial.");
        }
        ve.lancarException();
    }

    public FiltroRelatorioBemMovelBaixado getFiltros() {
        return filtros;
    }

    public void setFiltros(FiltroRelatorioBemMovelBaixado filtros) {
        this.filtros = filtros;
    }
}
