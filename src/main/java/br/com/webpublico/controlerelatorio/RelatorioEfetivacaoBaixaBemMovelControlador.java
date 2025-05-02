package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.EfetivacaoBaixaPatrimonial;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioEfetivacaoBaixaBemMovel;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoBaixa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
    @URLMapping(id = "relatorioEfetivacaoBaixaBemMovel", pattern = "/relatorio-efetivacao-baixa-bem-movel/",
        viewId = "/faces/administrativo/relatorios/relatorio-efetivacao-baixa-bem-movel.xhtml")}
)
public class RelatorioEfetivacaoBaixaBemMovelControlador {

    public static final String NOME_RELATORIO = "RELATORIO-DE-EFETIVAÇÃO-BEM-MÓVEL";
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private FiltroRelatorioEfetivacaoBaixaBemMovel filtros;

    public void limparCampos() {
        filtros = new FiltroRelatorioEfetivacaoBaixaBemMovel();
        filtros.setVerificarGestor(true);
        filtros.setDataOperacao(sistemaFacade.getDataOperacao());
        filtros.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
    }

    @URLAction(mappingId = "relatorioEfetivacaoBaixaBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCampos();
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

    protected void validarCampos() {
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

    public void gerarRelatorioEfetivacao(EfetivacaoBaixaPatrimonial efetivacao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();
            List<ParametrosRelatorios> parametros = Lists.newArrayList();
            parametros.add(new ParametrosRelatorios(" efet.id ", ":idEfetivacao", null, OperacaoRelatorio.IGUAL, efetivacao.getId(), null, 1, false));
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(parametros));
            criarParametroDTO(dto, usuarioCorrente);
            ReportService.getInstance().gerarRelatorio(usuarioCorrente, dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            criarParametroDTO(dto, usuarioCorrente);
            ReportService.getInstance().gerarRelatorio(usuarioCorrente, dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void criarParametroDTO(RelatorioDTO dto, UsuarioSistema usuarioCorrente) {
        dto.adicionarParametro("USUARIO", usuarioCorrente.getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("MODULO", "Patrimônio");
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE EFETIVAÇÃO DE BAIXA DE BEM MÓVEL");
        dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        dto.setNomeRelatorio(NOME_RELATORIO);
        dto.setApi("administrativo/efetivacao-baixa-bem-movel/");
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(" trunc(efet.dataEfetivacao) ", ":dataInicial", ":dataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(filtros.getDataInicial()), DataUtil.getDataFormatada(filtros.getDataFinal()), 1, true));
        parametros.add(new ParametrosRelatorios(" usuo.usuariosistema_id ", ":idUsuario", null, OperacaoRelatorio.IGUAL, filtros.getUsuarioSistema().getId(), null, 1, false));
        parametros.add(new ParametrosRelatorios(" usuo.gestorpatrimonio ", ":gestor", null, OperacaoRelatorio.IGUAL, 1, null, 1, false));
        if (filtros.getHierarquiaAdministrativa() != null) {
            parametros.add(new ParametrosRelatorios(" vwadm.codigo ", ":codigoAdm", null, OperacaoRelatorio.LIKE, filtros.getHierarquiaAdministrativa().getCodigoSemZerosFinais() + "%", null, 1, false));
        }
        if (filtros.getHierarquialOrcamentaria() != null) {
            parametros.add(new ParametrosRelatorios(" vworc.codigo ", ":codigoOrc", null, OperacaoRelatorio.LIKE, filtros.getHierarquialOrcamentaria().getCodigoSemZerosFinais() + "%", null, 1, false));
        }
        if (!Strings.isNullOrEmpty(filtros.getNumeroSolicitacao())) {
            parametros.add(new ParametrosRelatorios(" sol.codigo ", ":numeroSolicitacao", null, OperacaoRelatorio.IGUAL, filtros.getNumeroSolicitacao().trim(), null, 1, false));
        }
        if (!Strings.isNullOrEmpty(filtros.getNumeroEfetivacao())) {
            parametros.add(new ParametrosRelatorios(" efet.codigo ", ":numeroEfetivacao", null, OperacaoRelatorio.IGUAL, filtros.getNumeroEfetivacao().trim(), null, 1, false));
        }
        if (filtros.getTipoBaixa() != null) {
            parametros.add(new ParametrosRelatorios(" sol.tipoBaixa ", ":tipoBaixa", null, OperacaoRelatorio.IGUAL, filtros.getTipoBaixa().name(), null, 1, false));
        }
        return parametros;
    }

    public FiltroRelatorioEfetivacaoBaixaBemMovel getFiltros() {
        return filtros;
    }

    public void setFiltros(FiltroRelatorioEfetivacaoBaixaBemMovel filtros) {
        this.filtros = filtros;
    }
}
