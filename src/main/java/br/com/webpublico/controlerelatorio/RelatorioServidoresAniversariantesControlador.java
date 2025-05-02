package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 14/05/14
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-servidores-aniversariantes", pattern = "/relatorio/servidores-aniversariantes/", viewId = "/faces/rh/relatorios/relatorioservidoresaniversariantes.xhtml")
})
public class RelatorioServidoresAniversariantesControlador implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoServidorFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private Date dtInicial;
    private Date dtFinal;
    private ModalidadeContratoFP modalidadeContratoFP;

    public RelatorioServidoresAniversariantesControlador() {

    }

    public void gerarRelatorio() throws JRException, IOException {
        try {
            validaCampos();
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            RelatorioDTO dto = montarRelatorioDto(abstractReport);
            ReportService.getInstance().gerarRelatorio(abstractReport.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorio) throws JRException, IOException {
        try {
            validaCampos();
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            RelatorioDTO dto = montarRelatorioDto(abstractReport);
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            ReportService.getInstance().gerarRelatorio(abstractReport.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDto(AbstractReport abstractReport) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", abstractReport.getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("DATAINICIAL", DataUtil.getDataFormatada(dtInicial));
        dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(dtFinal));
        dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
        dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        dto.adicionarParametro("condicao", getFiltros());
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.setApi("rh/servidores-aniversariantes/");
        return dto;
    }

    private String getNomeRelatorio() {
        return "RELATÓRIO SERVIDORES ANIVERSARIANTES";
    }

    private String getFiltros() {
        String retorno = " ";
        if (hierarquiaOrganizacionalSelecionada != null) {
            retorno += (Integer.parseInt(hierarquiaOrganizacionalSelecionada.getCodigoNo()) == 1 && hierarquiaOrganizacionalSelecionada.getIndiceDoNo() == 1)
                ? " "
                : "and rec.codigoorgao = cast('" + hierarquiaOrganizacionalSelecionada.getCodigoNo() + "' as integer)";
        }
        if (modalidadeContratoFP != null) {
            retorno += " and modfp.id = " + modalidadeContratoFP.getId();
        }
        return retorno;
    }

    public void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        if (dtInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dtFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (this.dtInicial.after(this.dtFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial não pode ser maior que a Data Final");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "relatorio-servidores-aniversariantes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.hierarquiaOrganizacionalSelecionada = null;
        this.dtInicial = null;
        this.dtFinal = null;
        this.modalidadeContratoFP = null;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<SelectItem> getModalidadeContratoServidor() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ModalidadeContratoFP object : modalidadeContratoServidorFacade.modalidadesAtivas()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public Date getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(Date dtInicial) {
        this.dtInicial = dtInicial;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

}
