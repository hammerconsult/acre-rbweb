/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.GrupoRecursoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author boy
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "resumo-empenho-por-orgao", pattern = "/relatorio/resumo-empenho-por-orgao/", viewId = "/faces/rh/relatorios/relatorioresumodeempenhopororgao.xhtml"),
    @URLMapping(id = "eventos-calculados-por-orgao", pattern = "/relatorio/eventos-calculados-por-orgao/", viewId = "/faces/rh/relatorios/relatorioeventoscalculadospororgao.xhtml")
})
public class RelatorioResumoDeEmpenhoPorOrgaoControlador extends AbstractReport implements Serializable {

    public static final Logger logger = LoggerFactory.getLogger(RelatorioResumoDeEmpenhoPorOrgaoControlador.class);

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Mes mes;
    private Integer ano;
    private Integer versao;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private Converter converterHierarquia;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private TipoFolhaDePagamento tipoFolha;
    private List<GrupoRecursoFP> grupoRecursoFPs;
    private GrupoRecursoFP[] gruposSelecionados;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;


    public RelatorioResumoDeEmpenhoPorOrgaoControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItemSemCampoVazio(Mes.values(), false);
    }

    public List<GrupoRecursoFP> getGrupoRecursoFPs() {
        return grupoRecursoFPs;
    }

    public void setGrupoRecursoFPs(List<GrupoRecursoFP> grupoRecursoFPs) {
        this.grupoRecursoFPs = grupoRecursoFPs;
    }

    public GrupoRecursoFP[] getGruposSelecionados() {
        return gruposSelecionados;
    }

    public void setGruposSelecionados(GrupoRecursoFP[] gruposSelecionados) {
        this.gruposSelecionados = gruposSelecionados;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public Converter getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getDataVigencia());
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

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public void validarCampos(ValidacaoException ve) {
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é obrigatório");
        }
        if (ano == null || ano == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório");
        }
        if (tipoFolha == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor, preencha o campo Tipo de Folha!");
        }
        ve.lancarException();
    }

    private void validarCamposResumoEmpenhoPorOrgao() {
        ValidacaoException ve = new ValidacaoException();
        if (gruposSelecionados.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor, selecione um recurso folha de pagamento!");
        }
        validarCampos(ve);
    }

    private void validarCamposEventosCalcualdos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão é obrigatório");
        }
        validarCampos(ve);
    }

    @URLAction(mappingId = "resumo-empenho-por-orgao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCampos();
        grupoRecursoFPs = buscarGrupos();
    }

    public List<GrupoRecursoFP> buscarGrupos() {
        gruposSelecionados = new GrupoRecursoFP[]{};
        grupoRecursoFPs = grupoRecursoFPFacade.buscarGruposRecursoFPPorDataOperacao(getDataVigencia());
        return grupoRecursoFPs;
    }

    private Date getDataVigencia() {
        if (getMes() != null && getAno() != null) {
            return DataUtil.criarDataComMesEAno(mes.getNumeroMes(), ano).toDate();
        } else {
            return UtilRH.getDataOperacao();
        }
    }

    public void limparCampos() {
        mes = Mes.JANEIRO;
        ano = null;
        hierarquiaOrganizacionalSelecionada = null;
        tipoFolha = null;
        gruposSelecionados = new GrupoRecursoFP[]{};
        versao = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCamposResumoEmpenhoPorOrgao();
            DateTime dateTime = new DateTime(new Date());
            DateTime dateMes = new DateTime(getSistemaFacade().getDataOperacao());
            dateTime = dateTime.withMonthOfYear(mes.getNumeroMes());
            dateTime = dateTime.withYear(ano);
            if (dateMes.getMonthOfYear() == dateTime.getMonthOfYear()) {
                dateTime.withDayOfMonth(dateMes.getDayOfMonth());
            } else {
                dateTime = dateTime.withDayOfMonth(dateTime.dayOfMonth().getMaximumValue());
            }
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", sistemaControlador.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("MES", mes.getNumeroMesIniciandoEmZero());
            dto.adicionarParametro("ANO", ano);
            dto.adicionarParametro("SUBFOLHA", "");
            dto.adicionarParametro("DATA", DataUtil.getDataFormatada(dateTime.toDate()));
            dto.adicionarParametro("CODIGOORGAO", getMontarGrupo());
            dto.adicionarParametro("TIPOFOLHA", tipoFolha.name());
            dto.adicionarParametro("VERSAO", montarCampoVersao());
            dto.adicionarParametro("VERSAOFOLHA", versao);
            dto.setNomeRelatorio("RESUMO-DE-EMPENHO-POR-ORGAO");
            dto.setApi("rh/resumo-empenho-por-orgao/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório "));
        }
    }

    private String montarCampoVersao() {
        if (versao != null) {
            return " and folha.versao = " + versao + " ";
        }
        return " ";
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mes != null && ano != null && tipoFolha != null) {
            retorno.add(new SelectItem(null, ""));
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(mes, ano, tipoFolha)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    private String getMontarGrupo() {
        String retorno = " and grupo.nome in ( ";
        for (GrupoRecursoFP grupo : gruposSelecionados) {
            retorno += "'" + grupo.getNome() + "',";
        }
        retorno = retorno.substring(0, retorno.length() - 1);
        retorno += ") ";
        return retorno;
    }

    public List<SelectItem> getTiposFolhas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento obj : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public void gerarRelatorioEventosCalculados() {
        try {
            validarCamposEventosCalcualdos();
            String arquivoJasper = "RelatorioEventosCalculadosNoMesPorOrgao.jasper";
            String imagem = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/img");
            imagem += "/Brasao_de_Rio_Branco.gif";
            HashMap parameters = new HashMap();
            parameters.put("IMAGEM", imagem);
            parameters.put("MES", mes.getNumeroMesIniciandoEmZero());
            parameters.put("ANO", ano);
            parameters.put("UNIDADE_ID", hierarquiaOrganizacionalSelecionada.getSubordinada().getId());
            parameters.put("ORGAO", hierarquiaOrganizacionalSelecionada.getSubordinada().getDescricao());
            parameters.put("USER", getSistemaFacade().getUsuarioCorrente().getLogin());
            gerarRelatorio(arquivoJasper, parameters);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public TipoFolhaDePagamento getTipoFolha() {
        return tipoFolha;
    }

    public void setTipoFolha(TipoFolhaDePagamento tipoFolha) {
        this.tipoFolha = tipoFolha;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
