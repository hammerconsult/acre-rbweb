package br.com.webpublico.controle;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioExtratoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.administrativo.patrimonio.ExtratoBemMovelFacade;
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
import java.io.Serializable;
import java.util.List;

/**
 * Created by Desenvolvimento on 20/01/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "extratoBemMovel", pattern = "/extrato-bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/relatorios/extratobemmovel.xhtml")
})
public class ExtratoBemControlador implements Serializable {

    public static final String NOME_RELATORIO = "EXTRATO-DE-BEM-MÓVEL";
    @EJB
    private ExtratoBemMovelFacade extratoBemMovelFacade;
    private FiltroRelatorioExtratoBem filtroRelatorio;

    @URLAction(mappingId = "extratoBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioExtratoBem() {
        novoRelatorio();
    }

    public void novoRelatorio() {
        filtroRelatorio = new FiltroRelatorioExtratoBem();
        filtroRelatorio.setTipoBem(TipoBem.MOVEIS);
        filtroRelatorio.setUsuarioSistema(extratoBemMovelFacade.getSistemaFacade().getUsuarioCorrente());
    }

    private String montarNomeSecretaria() {
        return extratoBemMovelFacade.getBemFacade().montarNomeSecretaria(filtroRelatorio.getHierarquiaAdministrativa(), filtroRelatorio.getHierarquiaOrcamentaria());
    }

    private String montarNomeArquivo() {
        String retorno = NOME_RELATORIO;
        if (filtroRelatorio.getHierarquiaAdministrativa() != null) {
            retorno += "-" + filtroRelatorio.getHierarquiaAdministrativa().getCodigo().replace(".", "-");
        }
        if (filtroRelatorio.getHierarquiaOrcamentaria() != null) {
            retorno += "-" + filtroRelatorio.getHierarquiaOrcamentaria().getCodigo().replace(".", "-");
        }
        if (filtroRelatorio.getBem() != null) {
            retorno += "-NRP-" + filtroRelatorio.getBem().getIdentificacao();
        }
        retorno += "-" + DataUtil.getDataFormatada(filtroRelatorio.getDataInicial(), "yyyy-MM-dd") + "-A-" + DataUtil.getDataFormatada(filtroRelatorio.getDataFinal(), "yyyy-MM-dd");
        return retorno;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalAdministrativaOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return extratoBemMovelFacade.getHierarquiaOrganizacionalFacade().buscarHierarquiaOrganizacionalAdministrativaDoUsuario(parte, null,
            extratoBemMovelFacade.getSistemaFacade().getDataOperacao(), extratoBemMovelFacade.getSistemaFacade().getUsuarioCorrente(), filtroRelatorio.getHierarquiaOrcamentaria(), Boolean.TRUE);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalOrcamentariaOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return extratoBemMovelFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(parte, null,
            extratoBemMovelFacade.getSistemaFacade().getDataOperacao(), extratoBemMovelFacade.getSistemaFacade().getUsuarioCorrente(), filtroRelatorio.getHierarquiaAdministrativa());
    }

    public List<Bem> completarBemMovel(String parte) {
        return extratoBemMovelFacade.getBemFacade().completarBem(parte.trim(), TipoBem.MOVEIS, filtroRelatorio.getHierarquiaAdministrativa(), filtroRelatorio.getHierarquiaOrcamentaria(), false);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", filtroRelatorio.getUsuarioSistema().getNome(), false);
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("SECRETARIA", montarNomeSecretaria());
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("FILTROS", filtroRelatorio.getCriteriosUtilizados());
            dto.adicionarParametro("usuarioSistemaId", filtroRelatorio.getUsuarioSistema().getId());
            dto.adicionarParametro("bemId", filtroRelatorio.getBem() != null ? filtroRelatorio.getBem().getId() : null);
            dto.adicionarParametro("dataInicial", filtroRelatorio.getDataInicial());
            dto.adicionarParametro("dataFinal", filtroRelatorio.getDataFinal());
            dto.adicionarParametro("tipoBem", filtroRelatorio.getTipoBem().getToDto());
            dto.adicionarParametro("filtro", filtroRelatorio.getFiltro());
            dto.setNomeRelatorio(montarNomeArquivo());
            dto.adicionarParametro("NOMERELATORIO", "EXTRATO DE BEM MÓVEL");
            dto.setApi("administrativo/extrato-bem-movel/");
            ReportService.getInstance().gerarRelatorio(extratoBemMovelFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (WebReportRelatorioExistenteException ex) {
            ReportService.getInstance().abrirDialogConfirmar(ex);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public String montarNomeDoMunicipio() {
        return "MUNICÍPIO DE " + extratoBemMovelFacade.getSistemaFacade().getMunicipio().toUpperCase();
    }

    protected void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getHierarquiaAdministrativa() == null && filtroRelatorio.getHierarquiaOrcamentaria() == null && filtroRelatorio.getBem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A unidade administrativa, unidade orçamentária ou bem deve ser informado.");
        }
        if (filtroRelatorio.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data inicial para gerar o relatório.");
        }
        if (filtroRelatorio.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data final para gerar o relatório.");
        }
        ve.lancarException();
        if (filtroRelatorio.getDataFinal() != null && filtroRelatorio.getDataFinal().before(filtroRelatorio.getDataInicial())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Final deve ser maior que a Data Inicial.");
        }
        ve.lancarException();
    }

    public void atribuirDataAquisicao() {
        if (filtroRelatorio.getBem() != null && filtroRelatorio.getBem().getDataAquisicao() != null) {
            filtroRelatorio.setDataInicial(filtroRelatorio.getBem().getDataAquisicao());
            filtroRelatorio.setDataFinal(extratoBemMovelFacade.getSistemaFacade().getDataOperacao());
        }
    }

    public FiltroRelatorioExtratoBem getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroRelatorioExtratoBem filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }
}
