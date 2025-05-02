package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.BemFacade;
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

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-bens-moveis-em-bloqueio",
        pattern = "/relatorio-bens-moveis-em-bloqueio/",
        viewId = "/faces/administrativo/relatorios/relatorio-bens-moveis-em-bloqueio.xhtml")}
)
public class RelatorioBensMoveisEmBloqueioControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private BemFacade bemFacade;
    public static final String NOME_RELATORIO = "RELATORIO-BENS-MOVEIS-EM-BLOQUEIO-MOVIMENTACAO";
    private FiltroPesquisaBem filtroRelatorio;

    @URLAction(mappingId = "relatorio-bens-moveis-em-bloqueio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtroRelatorio = new FiltroPesquisaBem();
        filtroRelatorio.setDataOperacao(sistemaFacade.getDataOperacao());
        filtroRelatorio.setDataReferencia(sistemaFacade.getDataOperacao());
    }

    public List<Bem> completarBensMoveis(String parte) {
        return bemFacade.completarBem(parte.trim(), TipoBem.MOVEIS, filtroRelatorio.getHierarquiaAdministrativa(), filtroRelatorio.getHierarquiaOrcamentaria());
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalAdministrativaOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaDoUsuario(parte, null,
            sistemaFacade.getDataOperacao(), sistemaFacade.getUsuarioCorrente(), filtroRelatorio.getHierarquiaAdministrativa(), Boolean.TRUE);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalOrcamentariaOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(parte, null,
            sistemaFacade.getDataOperacao(), sistemaFacade.getUsuarioCorrente(), filtroRelatorio.getHierarquiaAdministrativa());
    }

    protected void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getHierarquiaOrcamentaria() == null && filtroRelatorio.getHierarquiaAdministrativa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar a Unidade Administrativa ou Orçamentária para gerar o relatório.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            if (filtroRelatorio.getDataReferencia() == null) {
                filtroRelatorio.setDataReferencia(sistemaFacade.getDataOperacao());
            }
            UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();

            dto.setNomeRelatorio("RELATÓRIO DE BENS MÓVEIS EM BLOQUEIO DE MOVIMENTAÇÃO/ALTERAÇÃO");
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + sistemaFacade.getMunicipio().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(filtroRelatorio.getDataOperacao()));
            dto.adicionarParametro("CONDICAO", filtroRelatorio.getCondicaoConsultaRelatorioBloqueioBens());
            dto.adicionarParametro("FILTROS", getDescricaoFiltros());
            dto.adicionarParametro("USUARIO", usuarioCorrente.getNome(), false);
            dto.setApi("administrativo/bens-moveis-em-bloqueio/");
            ReportService.getInstance().gerarRelatorio(usuarioCorrente, dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public String getDescricaoFiltros() {
        String descricao = "";
        if (filtroRelatorio.getHierarquiaAdministrativa() != null) {
            descricao += " Unidade Administrativa: " + filtroRelatorio.getHierarquiaAdministrativa().getCodigo() + " -";
        }
        if (filtroRelatorio.getHierarquiaOrcamentaria() != null) {
            descricao += " Unidade Orçamentária: " + filtroRelatorio.getHierarquiaOrcamentaria().getCodigo() + " -";
        }
        if (filtroRelatorio.getBem() != null) {
            descricao += " Bem: " + filtroRelatorio.getBem().getIdentificacao() + " -";
        }
        return descricao.substring(0, descricao.length() - 1);
    }

    public FiltroPesquisaBem getFiltrosRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltros(FiltroPesquisaBem filtros) {
        this.filtroRelatorio = filtros;
    }
}
