/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@ManagedBean(name = "relatorioServidoresPorOrgaoAgrupadoPorRecursoFP")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioServidoresPorOrgaoERecurso", pattern = "/relatorio/servidores-por-orgao-recursofp/novo/", viewId = "/faces/rh/relatorios/relatorioservidorespororgaoagrupadoporrecursofp.xhtml")
})
public class RelatorioServidoresPorOrgaoAgrupadoPorRecursoFP extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Integer mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;

    public RelatorioServidoresPorOrgaoAgrupadoPorRecursoFP() {
        geraNoDialog = Boolean.TRUE;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            DateTime dateTime = new DateTime(new Date());
            dateTime = dateTime.withMonthOfYear(mes);
            dateTime = dateTime.withYear(ano);
            dateTime = dateTime.withDayOfMonth(dateTime.dayOfMonth().getMaximumValue());
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("NOMERELATORIO", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("ORGAO", getCodigo());
            dto.adicionarParametro("DATA", DataUtil.getDataFormatada(dateTime.toDate()));
            dto.adicionarParametro("ANO", ano);
            dto.adicionarParametro("MES", mes - 1);
            dto.adicionarParametro("TIPOFOLHA", tipoFolhaDePagamento.name());
            dto.setNomeRelatorio("RELATORIO-DE-SERVIDORES-POR-ORGAO-AGRUPADOS-POR-RECURSOFP");
            dto.setApi("rh/servidores-por-orgao-recursofp/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getCodigo() {
        String retorno = " ";
        if (Integer.parseInt(hierarquiaOrganizacionalSelecionada.getCodigoNo()) == 1 && hierarquiaOrganizacionalSelecionada.getIndiceDoNo() == 1) {
            return retorno;
        }
        return " and rec.codigoorgao = cast('" + hierarquiaOrganizacionalSelecionada.getCodigoNo() + "' as integer) ";
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (ano == null || ano == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (mes != null && (mes < 1 || mes > 12)) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        ve.lancarException();
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    @URLAction(mappingId = "novoRelatorioServidoresPorOrgaoERecurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        mes = null;
        ano = null;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }
}
