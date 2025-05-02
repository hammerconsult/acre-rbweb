package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioAdministrativo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-contrato-reserva-dotacao", pattern = "/relatorios/contrato-reserva-dotacao/", viewId = "/faces/administrativo/relatorios/relatoriocontratoreservadotacao.xhtml")
})
@ManagedBean
public class RelatorioContratoReservaControlador implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioAdministrativo filtroRelatorio;
    private String filtros;

    @URLAction(mappingId = "relatorio-contrato-reserva-dotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtroRelatorio = new FiltroRelatorioAdministrativo();
        filtroRelatorio.setDataInicial(new Date());
        filtroRelatorio.setDataFinal(new Date());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(getDataReferencia()));
            dto.adicionarParametro("condicao", montarClausulaWhere());
            dto.adicionarParametro("FILTROS", getFiltros());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/contrato-reserva-dotacao/");
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

    private String getNomeRelatorio() {
        return "RELATÓRIO DE CONTRATOS E RESERVAS DE DOTAÇÃO";
    }

    private Date getDataReferencia() {
        if (filtroRelatorio.getDataFinal() != null) {
            return montarDataFinal();
        }
        return sistemaFacade.getDataOperacao();
    }

    public void limparUnidadesEContrato() {
        filtroRelatorio.setHierarquiaAdministrativa(null);
        filtroRelatorio.setHierarquiaOrcamentaria(null);
        filtroRelatorio.setContrato(null);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (filtroRelatorio.getHierarquiaAdministrativa() != null) {
            return hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(filtroRelatorio.getHierarquiaAdministrativa().getSubordinada(), getDataReferencia());
        }
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), getDataReferencia());
    }

    public List<HierarquiaOrganizacional> completarHierarquiasAdministrativas(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaUsuarioPorTipoData(parte.trim(), sistemaFacade.getUsuarioCorrente(), getDataReferencia(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public List<Contrato> completarContratos(String filtro) {
        if (filtroRelatorio.getHierarquiaAdministrativa() != null) {
            return contratoFacade.listaFiltrandoContrato(filtro, filtroRelatorio.getHierarquiaAdministrativa().getSubordinada());
        }
        return Lists.newArrayList();
    }

    private String montarClausulaWhere() {
        setFiltro("");
        String retorno = " and uc.unidadeadministrativa_id = " + filtroRelatorio.getHierarquiaAdministrativa().getSubordinada().getId();
        if (filtroRelatorio.getHierarquiaOrcamentaria() != null) {
            retorno += " and cont.id in ( SELECT distinct EXEC.CONTRATO_ID " +
                "  FROM execucaoContrato EXEC " +
                " INNER JOIN execucaocontratoempenho execemp ON exec.id = execemp.EXECUCAOCONTRATO_ID " +
                " INNER JOIN EXECUCAOCONTRATOTIPO exectipo ON exectipo.EXECUCAOCONTRATO_ID = exec.id " +
                " INNER JOIN execucaocontratotipofonte execfont ON exectipo.id = execfont.EXECUCAOCONTRATOTIPO_ID " +
                " INNER JOIN fontedespesaorc fontDesp ON execfont.FONTEDESPESAORC_ID = fontdesp.id " +
                " INNER JOIN despesaorc desp ON fontDesp.DESPESAORC_ID = desp.id " +
                " INNER JOIN provisaoppadespesa provDesp ON desp.PROVISAOPPADESPESA_ID = provDesp.id" +
                " where provdesp.unidadeorganizacional_id = " + filtroRelatorio.getHierarquiaOrcamentaria().getSubordinada().getId() +
                ") ";
            filtros += " Unidade Orçamentária: " + filtroRelatorio.getHierarquiaOrcamentaria().getCodigo() + "; ";
        }
        if (filtroRelatorio.getContrato() != null) {
            retorno += " and cont.id = " + filtroRelatorio.getContrato().getId();
            filtros += "Contrato: " + filtroRelatorio.getContrato().getNumeroTermo() + "; ";
        }
        if (filtroRelatorio.getDataInicial() != null && filtroRelatorio.getDataFinal() != null) {
            Date dataFinal = montarDataFinal();
            retorno += " and cont.iniciovigencia between to_date('" + DataUtil.getDataFormatada(filtroRelatorio.getDataInicial(), "MM/yyyy") + "', 'MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal, "MM/yyyy") + "', 'MM/yyyy') ";
            filtros += "Período: " + DataUtil.getDataFormatada(filtroRelatorio.getDataInicial()) + " até " + DataUtil.getDataFormatada(dataFinal) + "; ";
        }
        return retorno;
    }

    private Date montarDataFinal() {
        String mesDaData = Util.getMesDaData(filtroRelatorio.getDataFinal());
        int ano = DataUtil.getAno(filtroRelatorio.getDataFinal());
        int mes = Integer.valueOf(mesDaData);
        int dia = DataUtil.ultimoDiaDoMes(mes);
        return Util.montarData(dia, mes - 1, ano);
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getHierarquiaAdministrativa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa deve ser informado!");
        }
        if (filtroRelatorio.getDataInicial() != null && filtroRelatorio.getDataFinal() != null) {
            if (filtroRelatorio.getDataInicial().after(filtroRelatorio.getDataFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial deve ser anterior ou igual a data final.");
            }
        }
        ve.lancarException();
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltro(String filtros) {
        this.filtros = filtros;
    }

    public FiltroRelatorioAdministrativo getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroRelatorioAdministrativo filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }
}
