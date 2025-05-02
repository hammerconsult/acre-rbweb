/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.TipoDependente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.TipoDependenteFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "Relatorio-Dependentes-Por-Dependencia", pattern = "/relatorio/dependente-por-dependencia/", viewId = "/faces/rh/relatorios/relatoriodependentespordependencia.xhtml")
})
public class RelatorioDependentesPorDependenciaControlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private TipoDependenteFacade tipoDependenteFacade;
    private String filtro;
    private String filtroMatriculas;
    private List<TipoDependente> grupoTipoDependente;
    private TipoDependente[] tipoDependetesSelecionados;
    private Date dtInicio;
    private Date dtFinal;
    private String matriculaInicial;
    private String matriculaFinal;

    public RelatorioDependentesPorDependenciaControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public List<TipoDependente> getGrupoTipoDependente() {
        return grupoTipoDependente;
    }

    public void setGrupoTipoDependente(List<TipoDependente> grupoTipoDependente) {
        this.grupoTipoDependente = grupoTipoDependente;
    }

    public TipoDependente[] getTipoDependetesSelecionados() {
        return tipoDependetesSelecionados;
    }

    public void setTipoDependetesSelecionados(TipoDependente[] tipoDependetesSelecionados) {
        this.tipoDependetesSelecionados = tipoDependetesSelecionados;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Date getDtInicio() {
        return dtInicio;
    }

    public void setDtInicio(Date dtInicio) {
        this.dtInicio = dtInicio;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public String getMatriculaInicial() {
        return matriculaInicial;
    }

    public void setMatriculaInicial(String matriculaInicial) {
        this.matriculaInicial = matriculaInicial;
    }

    public String getMatriculaFinal() {
        return matriculaFinal;
    }

    public void setMatriculaFinal(String matriculaFinal) {
        this.matriculaFinal = matriculaFinal;
    }

    public void gerarRelatorio() {
        try {
            validarImpressao();
            RelatorioDTO dto = montarRelatorioDTO();
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Ocorreu um erro ao gerar o relatorio: " + e);
        }
    }

    public void gerarRelatorio(String tipoRelatorioDependentesPorDependencia) {
        try {
            validarImpressao();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioDependentesPorDependencia));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Ocorreu um erro ao gerar o relatorio: " + e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        String sql = montarSQL();
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DEPENDENTES POR DEPENDÊNCIA");
        dto.adicionarParametro("SQL", sql);
        dto.adicionarParametro("MATRICULAS", montaMatriculas());
        dto.adicionarParametro("DATAOPERACAO", UtilRH.getDataOperacao());
        dto.adicionarParametro("FILTRO", filtro);
        dto.adicionarParametro("GRUPO", getMontaGrupoTipoDependencia());
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DEPENDENTES POR DEPENDÊNCIA");
        dto.adicionarParametro("MATRICULASFILTRO", filtroMatriculas);
        dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getNome(), false);
        dto.setApi("rh/relatorio-de-dependentes-por-dependencia/");
        return dto;
    }


    private String getMontaGrupoTipoDependencia() {
        String retorno = " and depvin.tipodependente_id in ( ";
        for (TipoDependente grupo : tipoDependetesSelecionados) {
            retorno += "'" + grupo.getId() + "',";
        }
        retorno = retorno.substring(0, retorno.length() - 1);
        retorno += ") ";
        return retorno;
    }

    private String montaMatriculas() {
        StringBuilder stb = new StringBuilder();
        String juncao = " AND ";
        if (this.matriculaInicial != null && !this.matriculaInicial.equals("") && this.matriculaFinal != null && !this.matriculaFinal.equals("")) {
            stb.append(juncao).append(" MATRICULA.MATRICULA BETWEEN ").append(this.matriculaInicial).append(juncao).append(this.matriculaFinal);
            filtroMatriculas = " Matrícula de: " + matriculaInicial + " à " + matriculaFinal;
        }

        return stb.toString();
    }

    private String montarSQL() {
        String retorno = "";
        if (dtInicio != null && dtFinal != null) {
            retorno = "and (trunc(depvin.iniciovigencia) >= to_date('" + DataUtil.getDataFormatada(dtInicio) + "', 'dd/mm/yyyy') " +
                " and   coalesce(trunc(depvin.FINALVIGENCIA), current_date) <= to_date('" + DataUtil.getDataFormatada(dtFinal) + "', 'dd/mm/yyyy') \n" +
                " or trunc(depvin.FINALVIGENCIA) >= to_date('" + DataUtil.getDataFormatada(dtInicio) + "', 'dd/mm/yyyy') " +
                " and   coalesce(trunc(depvin.iniciovigencia), current_date) <= to_date('" + DataUtil.getDataFormatada(dtFinal) + "', 'dd/mm/yyyy'))";
            filtro = " Vigência de: " + DataUtil.getDataFormatada(dtInicio) + " até " + DataUtil.getDataFormatada(dtFinal);
        }
        return retorno;
    }

    public void validarImpressao() {
        ValidacaoException ve = new ValidacaoException();
        if (dtInicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial é obrigatório.");
        }

        if (dtFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final é obrigatório.");
        }

        if (this.matriculaInicial != null && this.matriculaFinal != null) {
            if (this.matriculaFinal.compareTo(this.getMatriculaInicial()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Matrícula Inicial não pode ser maior que a Matrícula Final");
            }
        }

        if (this.getDtInicio() != null && this.getDtFinal() != null) {
            if (this.getDtInicio().after(this.getDtFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser antes que a Data Final");
            }
        }

        if (tipoDependetesSelecionados.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o(s) tipo(s) de dependência.");
        }
        ve.lancarException();

    }

    @URLAction(mappingId = "Relatorio-Dependentes-Por-Dependencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        grupoTipoDependente = new ArrayList<>();
        dtInicio = null;
        dtFinal = null;
        matriculaInicial = null;
        matriculaFinal = null;
        tipoDependetesSelecionados = new TipoDependente[]{};
        carregaListaTipoDependencia();
    }

    public void carregaListaTipoDependencia() {
        grupoTipoDependente = tipoDependenteFacade.completaTipoDependente("");
    }
}
