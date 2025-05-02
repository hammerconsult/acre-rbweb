/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFPFacade;
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
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-frequencias-por-periodo", pattern = "/relatorio/frequencias-por-periodo/", viewId = "/faces/rh/relatorios/relatoriofrequenciasporperiodo.xhtml")
})
public class RelatorioFrequenciaPorPeriodoControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private ContratoFP servidor;
    private Date dataInicial;
    private Date dataFinal;
    private String filtros;
    private String tipoFalta;
    private Boolean todosTipos;

    public RelatorioFrequenciaPorPeriodoControlador() {
        todosTipos = Boolean.TRUE;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getTipoFalta() {
        return tipoFalta;
    }

    public void setTipoFalta(String tipoFalta) {
        this.tipoFalta = tipoFalta;
    }

    public ContratoFP getServidor() {
        return servidor;
    }

    public void setServidor(ContratoFP servidor) {
        this.servidor = servidor;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public Boolean getTodosTipos() {
        if (todosTipos) {
            tipoFalta = null;
        }
        return todosTipos;
    }

    public void setTodosTipos(Boolean todosTipos) {
        this.todosTipos = todosTipos;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
            try {
                validarCampos();
                filtros = " ";
                String nomeRelatorio = "RELATÓRIO DE FREQUÊNCIA POR PERÍODO";
                RelatorioDTO dto = new RelatorioDTO();
                dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
                dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
                dto.setNomeParametroBrasao("BRASAO");
                dto.adicionarParametro("tiposDeFaltas", montarTipoDeFaltas());
                dto.adicionarParametro("idContrato", servidor.getId());
                dto.adicionarParametro("FILTROS", filtros);
                dto.adicionarParametro("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
                dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
                dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
                dto.adicionarParametro("MODULO", "Recursos Humanos");
                dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
                dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
                dto.setNomeRelatorio(nomeRelatorio);
                dto.setApi("rh/frequencia-por-periodo/");
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

    private String montarTipoDeFaltas() {
        if (!todosTipos && tipoFalta != null) {
            filtros += "Tipo de Faltas: " + tipoFalta;
            return " AND DADOS.TIPO_FALTA = '" + tipoFalta + "'";
        } else {
            filtros += "Tipo de Faltas: Todas";
            return " ";
        }
    }

    @URLAction(mappingId = "relatorio-frequencias-por-periodo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = null;
        dataFinal = null;
        servidor = null;
        tipoFalta = null;
        todosTipos = Boolean.TRUE;
        filtros = "";
    }

    public List<SelectItem> getTipoFaltas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem("Falta Justificada"));
        toReturn.add(new SelectItem("Falta Justificada Parcialmente"));
        toReturn.add(new SelectItem("Falta Injustificada"));
        return toReturn;
    }

    public List<ContratoFP> completarServidores(String parte) {
        return contratoFPFacade.recuperaContratoMatricula(parte.trim());
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial é obrigatório.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final é obrigatório.");
        }
        if (servidor == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor é obrigatório.");
        }
        if (!todosTipos && tipoFalta == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Falta é obrigatório.");
        }
        ve.lancarException();
        if ( dataInicial.compareTo(dataFinal) > 0) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data Inicial não pode ser posterior à Data Final.");
        }
        ve.lancarException();
    }
}
