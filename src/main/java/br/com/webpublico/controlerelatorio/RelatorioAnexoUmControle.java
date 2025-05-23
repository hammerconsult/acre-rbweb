/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.EsferaOrcamentaria;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoRelatorioQDD;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FonteDeRecursosFacade;
import br.com.webpublico.negocios.FuncaoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.relatoriofacade.RelatorioDeAnexosFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.contabil.AnexoSeis4320TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Enumerated;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author reidocrime
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo1-planejamento", pattern = "/relatorio/planejamento/lei4320/anexo1/", viewId = "/faces/financeiro/relatorio/relatorioanexo1loa.xhtml"),
    @URLMapping(id = "relatorio-lei4320-anexo2-geral-planejamento", pattern = "/relatorio/planejamento/lei4320/anexo2geral/", viewId = "/faces/financeiro/relatorio/relatorioanexo2loareceitageral.xhtml"),
    @URLMapping(id = "relatorio-lei4320-anexo2-receita-planejamento", pattern = "/relatorio/planejamento/lei4320/anexo2receita/", viewId = "/faces/financeiro/relatorio/relatorioanexo2loareceitaorgaos.xhtml"),
    @URLMapping(id = "relatorio-lei4320-anexo2-despesa-planejamento", pattern = "/relatorio/planejamento/lei4320/anexo2despesa/", viewId = "/faces/financeiro/relatorio/relatorioanexo2despesa.xhtml"),
    @URLMapping(id = "relatorio-lei4320-anexo6-planejamento", pattern = "/relatorio/planejamento/lei4320/anexo6/", viewId = "/faces/financeiro/relatorio/relatorioanexo6loa.xhtml"),
    @URLMapping(id = "relatorio-lei4320-anexo7-planejamento", pattern = "/relatorio/planejamento/lei4320/anexo7/", viewId = "/faces/financeiro/relatorio/relatorioanexo7loa.xhtml"),
    @URLMapping(id = "relatorio-lei4320-anexo8-planejamento", pattern = "/relatorio/planejamento/lei4320/anexo8/", viewId = "/faces/financeiro/relatorio/relatorioanexo8loa.xhtml"),
    @URLMapping(id = "relatorio-lei4320-anexo9-planejamento", pattern = "/relatorio/planejamento/lei4320/anexo9/", viewId = "/faces/financeiro/relatorio/relatorioanexo9loa.xhtml"),
    @URLMapping(id = "relatorio-lei4320-qdd-planejamento", pattern = "/relatorio/planejamento/lei4320/qdd/", viewId = "/faces/financeiro/relatorio/relatorioqddloa.xhtml"),
    @URLMapping(id = "relatorio-lei4320-qdd-planejamento-unidade", pattern = "/relatorio/planejamento/lei4320/qdd-unidade/", viewId = "/faces/financeiro/relatorio/relatorioqddloaunidade.xhtml")
})
@ManagedBean
public class RelatorioAnexoUmControle extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioDeAnexosFacade relatorioDeAnexosFacade;
    private HierarquiaOrganizacional orgaoInicial;
    private Conta contaDespesa;
    private HierarquiaOrganizacional orgaoFinal;
    private HierarquiaOrganizacional unidadeInicial;
    private HierarquiaOrganizacional unidadeFinal;
    private ConverterAutoComplete converterOrgaoInicial;
    private ConverterAutoComplete converterOrgaoFinal;
    private ConverterAutoComplete converterContaDespesa;
    private ConverterExercicio converterExercicio;
    private Exercicio exercicio;
    @Enumerated
    private TipoRelatorio tipoRelatorioAnexoDoisDespesa = TipoRelatorio.CONSOLIDADO;
    @Enumerated
    private AnexoSeis4320TipoRelatorioDTO tipoRelatorioAnexoSeis;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private EsferaOrcamentaria esferaOrcamentaria = EsferaOrcamentaria.ORCAMENTOGERAL;
    private Boolean intra = Boolean.FALSE;
    private BigDecimal reservaContingencia;
    private MoneyConverter moneyConverter;
    private List<HierarquiaOrganizacional> listaUnidades;
    private TipoRelatorioQDD tiporelatorioQDD;
    private FonteDeRecursos fonteDeRecursosQDD;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    private ConverterAutoComplete converterFonteDeRecursos;
    @EJB
    private FuncaoFacade funcaoFacade;
    private ConverterAutoComplete converterFuncao;
    private Funcao funcao;
    private Boolean mostraUsuario = Boolean.FALSE;

    public RelatorioAnexoUmControle() {
        listaUnidades = new ArrayList<>();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(getExercicioFacade());
        }
        return converterExercicio;
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo1-planejamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposAnexoUm() {
        reservaContingencia = BigDecimal.ZERO;
        mostraUsuario = false;
        intra = false;
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo2-geral-planejamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposAnexoDoisGeral() {
        mostraUsuario = false;
        orgaoInicial = null;
        orgaoFinal = null;
        unidadeInicial = null;
        unidadeFinal = null;
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo2-receita-planejamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCamposAnexoDoisReceita() {
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo2-despesa-planejamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCamposAnexoDoisDespesa() {
        this.unidadeFinal = null;
        this.unidadeInicial = null;
        this.orgaoFinal = null;
        this.orgaoInicial = null;
        this.tipoRelatorioAnexoDoisDespesa = null;
        this.listaUnidades = new ArrayList<>();
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo6-planejamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCamposAnexoSeis() {
        this.listaUnidades = new ArrayList<>();
        tipoRelatorioAnexoSeis = AnexoSeis4320TipoRelatorioDTO.CONSOLIDADO;
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo7-planejamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCamposAnexoSete() {
        mostraUsuario = false;
        exercicio = sistemaControlador.getExercicioCorrente();
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo8-planejamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCamposAnexoOito() {
        mostraUsuario = false;
        exercicio = sistemaControlador.getExercicioCorrente();
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo9-planejamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCamposAnexoNove() {
        mostraUsuario = false;
        exercicio = sistemaControlador.getExercicioCorrente();
        orgaoInicial = null;
        orgaoFinal = null;
        funcao = null;
    }

    @URLAction(mappingId = "relatorio-lei4320-qdd-planejamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCamposQDD() {
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public ConverterAutoComplete getConverterOrgaoInicial() {
        if (converterOrgaoInicial == null) {
            converterOrgaoInicial = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterOrgaoInicial;
    }

    public ConverterAutoComplete getConverterOrgaoFinal() {
        if (converterOrgaoInicial == null) {
            converterOrgaoInicial = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterOrgaoInicial;
    }

    public ConverterAutoComplete getConverterUnidadeFinal() {
        if (converterOrgaoFinal == null) {
            converterOrgaoFinal = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterOrgaoFinal;
    }

    public ConverterAutoComplete getConverterUnidadeInicial() {
        if (converterOrgaoFinal == null) {
            converterOrgaoFinal = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterOrgaoFinal;
    }

    public ConverterAutoComplete getConverterContaDespesa() {
        if (converterContaDespesa == null) {
            converterContaDespesa = new ConverterAutoComplete(Conta.class, relatorioDeAnexosFacade.getContaFacade());
        }
        return converterContaDespesa;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalUnidade(String parte) {
        return hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel(parte, sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
    }

    public List<EsferaOrcamentaria> listaEsferaOrcamentaria() {
        List<EsferaOrcamentaria> lista = null;
        return lista;
    }

    public List<Conta> buscarContas(String parte) {
        try {
            List<Conta> contas = relatorioDeAnexosFacade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), getSistemaControlador().getExercicioCorrente());
            if (contas.isEmpty()) {
                return new ArrayList<>();
            } else {
                return contas;
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.listaHierarquiaOrgaoUsuarioCorrentePorNivel(parte, sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 2);
    }

    public List<SelectItem> getListaEsferaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (EsferaOrcamentaria eo : EsferaOrcamentaria.values()) {
            toReturn.add(new SelectItem(eo, eo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoRelatorio tipoRel : TipoRelatorio.values()) {
            toReturn.add(new SelectItem(tipoRel, tipoRel.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoRelatorioAnexoSeis() {
        return Util.getListSelectItemSemCampoVazio(AnexoSeis4320TipoRelatorioDTO.values());
    }

    public void gerarRelatorioAnexoUm() {
        try {
            Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
            LOA loa = sistemaControlador.getLOADoExercicio(exercicioCorrente);
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("ANO_EXERCICIO", exercicioCorrente.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", exercicioCorrente.getId());
            dto.adicionarParametro("LOA_ID", loa.getId());
            dto.adicionarParametro("INTRA", intra);
            dto.adicionarParametro("DATA", sistemaControlador.getDataOperacao().getTime());
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("ESFERA_ORCAMENTARIA", esferaOrcamentaria.getDescricao().substring(4, esferaOrcamentaria.getDescricao().length()).trim());
            dto.adicionarParametro("ESFERA", esferaOrcamentaria.getToDto());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio("ANEXO I");
            dto.adicionarParametro("USER", mostraUsuario ? "Emitido por: " + getSistemaFacade().getUsuarioCorrente().getNome() : "", false);
            dto.setApi("contabil/anexo-1-planejamento/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioAnexoDoisrR() throws JRException, IOException {
        try {
            validarOrgaoAndUnidade();
            Exercicio exercicio = sistemaControlador.getExercicioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", exercicio.getId());
            dto.adicionarParametro("DATA", sistemaControlador.getDataOperacao().getTime());
            dto.adicionarParametro("SQL", clausulaAnexoDois());
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio("ANEXO II R - GERAL");
            dto.adicionarParametro("USER", mostraUsuario ? "Emitido por: " + getSistemaFacade().getUsuarioCorrente().getNome() : "", false);
            dto.setApi("contabil/anexo-2-receita-geral-planejamento/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void gerarRodapeRelatorio(HashMap parameters) {
        if (mostraUsuario) {
            if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                parameters.put("USER", "Emitido por: " + sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            } else {
                parameters.put("USER", "Emitido por: " + sistemaControlador.getUsuarioCorrente().getUsername());
            }
        } else {
            parameters.put("USER", "");
        }
    }

    private String clausulaAnexoDois() {
        String retorno = "";
        if (orgaoInicial != null && orgaoFinal != null) {
            retorno += " and vworg.codigo between '" + orgaoInicial.getCodigo() + "' and '" + orgaoFinal.getCodigo() + "'";
        }
        if (unidadeInicial != null && unidadeFinal != null) {
            retorno += " and vw.codigo between '" + unidadeInicial.getCodigo() + "' and '" + unidadeFinal.getCodigo() + "'";
        }
        return retorno;
    }

    private String gerarSqlAnexoDoisGeral() {
        StringBuilder stb = new StringBuilder();
        stb.append(" where 1=1 ");
        if (this.orgaoInicial != null) {
            stb.append(" and ho_org.CODIGO BETWEEN \'");
            stb.append(orgaoInicial.getCodigo());
            stb.append("\'");
        }
        if (this.orgaoFinal != null) {
            stb.append(" AND \'");
            stb.append(orgaoFinal.getCodigo());
            stb.append("\' AND ");
        } else if (this.orgaoInicial != null) {
            stb.append(" AND \'");
            stb.append(orgaoInicial.getCodigo());
            stb.append("\' AND ");
        }

        if (this.unidadeInicial != null) {
            stb.append(" and ho_und.CODIGO BETWEEN  \'");
            stb.append(unidadeInicial.getCodigo());
            stb.append("\' ");
        }
        if (this.unidadeFinal != null) {
            stb.append(" AND \'");
            stb.append(unidadeFinal.getCodigo());
            stb.append("\' AND ");
        } else if (this.unidadeInicial != null) {
            stb.append(" AND \'");
            stb.append(unidadeInicial.getCodigo());
            stb.append("\' AND ");
        }
        return recortaAndFinal(stb.toString()).replace("and  and", " and ");
    }

    public void gerarRelatorioAnexoDoisrROrgaoUnidade() {
        try {
            validarOrgaoAndUnidade();
            Exercicio e = sistemaControlador.getExercicioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setApi("contabil/anexo2-orgao-unidade/");
            dto.setNomeRelatorio("Anexo 02 - Receita Segundo as Categorias Econômicas");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("ANO_EXERCICIO", e.getAno().toString());
            dto.adicionarParametro("exercicioId", e.getId());
            dto.adicionarParametro("dataOperacao", sistemaControlador.getDataOperacao().getTime());
            dto.adicionarParametro("sql", gerarSqlAnexoDoisGeral());
            dto.adicionarParametro("USER", mostraUsuario ? "Emitido por: " + getSistemaFacade().getUsuarioCorrente().getNome() : "", false);

            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
        }
    }

    public String geraVigenciaSQL() {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder stb = new StringBuilder();
        String whereAnd = " ";
        stb.append(whereAnd).append("to_date('").append(formato.format(sistemaControlador.getDataOperacao())).append("', 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date('").append(formato.format(sistemaControlador.getDataOperacao())).append("', 'dd/mm/yyyy'))");
        whereAnd = " AND ";
        stb.append(whereAnd).append("to_date('").append(formato.format(sistemaControlador.getDataOperacao())).append("', 'dd/mm/yyyy') between vworg.iniciovigencia and coalesce(vworg.fimvigencia, to_date('").append(formato.format(sistemaControlador.getDataOperacao())).append("', 'dd/mm/yyyy'))");
        return stb.toString();
    }

    private String recortaAndFinal(String sql) {
        String comandoSQL = sql.toLowerCase().trim();
        boolean andFinal = comandoSQL.endsWith("and");
        if (andFinal) {
            int tamanho = comandoSQL.length();
            comandoSQL = comandoSQL.substring(0, tamanho - 3);
        }
        return comandoSQL;
    }

    private String sqlUnidades() {
        StringBuilder sql = new StringBuilder();
        if (!listaUnidades.isEmpty()) {
            StringBuilder idUnidades = new StringBuilder();
            String concat = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concat).append(lista.getSubordinada().getId());
                concat = ",";
            }
            sql.append(" WHERE ").append(" HO_UND.SUBORDINADA_ID IN (").append(idUnidades).append(")");
        }
        return sql.toString();
    }

    public void gerarRelatorioSeis() {
        try {
            validarCampos();
            Exercicio e = sistemaControlador.getExercicioCorrente();

            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("ANEXO-SEIS-LEI-4320");
            dto.adicionarParametro("USER", usuarioLogado().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("ANO_EXERCICIO", e.getAno().toString());
            dto.adicionarParametro("exercicioId", e.getId());
            dto.adicionarParametro("dataOperacao", sistemaControlador.getDataOperacao().getTime());
            dto.adicionarParametro("codicao", sqlUnidades());
            dto.adicionarParametro("tipoRelatorio", tipoRelatorioAnexoSeis);
            dto.setApi("contabil/anexo-seis-4320/");

            ReportService.getInstance().gerarRelatorio(usuarioLogado(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void validarCampos() {
        ValidacaoException ex = new ValidacaoException();
        if (this.orgaoInicial == null && this.orgaoFinal != null) {
            ex.adicionarMensagemDeCampoObrigatorio(" Informe o Órgão Inicial!");
        }

        if (this.unidadeInicial == null && this.unidadeFinal != null) {
            ex.adicionarMensagemDeCampoObrigatorio(" Informe a Unidade Inicial!");
        }

        if (this.orgaoInicial != null
            && this.orgaoInicial.getCodigo().compareTo(this.orgaoFinal.getCodigo()) > 0) {
            ex.adicionarMensagemDeOperacaoNaoPermitida(" Órgão Inicial maior que o Órgão Final!");
        }

        if (this.unidadeInicial != null
            && this.unidadeInicial.getCodigo().compareTo(this.unidadeFinal.getCodigo()) > 0) {
            ex.adicionarMensagemDeOperacaoNaoPermitida(" Unidade Inicial maior que a Unidade Final!");
        }
        ex.lancarException();
    }


    public void gerarRelatorioAnexoNove() {
        try {
            validarOrgao();

//            String arquivoJasper = "RelAnexoNoveLOA.jasper";

            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("ANEXO-IX");
            dto.adicionarParametro("USER", usuarioLogado().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("EXERCICIO_ID", exercicio.getId());
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
            dto.adicionarParametro("DATA", sistemaControlador.getDataOperacao().getTime());
            dto.adicionarParametro("complemento", montarClausulaAnexoNove());
            dto.setApi("contabil/anexo-9-planejamento/");
            ReportService.getInstance().gerarRelatorio(usuarioLogado(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarClausulaAnexoNove() {
        String retorno = "";
        if (orgaoInicial != null && orgaoFinal != null) {
            retorno += " and ho_orgao.CODIGO BETWEEN '" + orgaoInicial.getCodigo() + "' and '" + orgaoFinal.getCodigo() + "' ";
        }
        if (this.funcao != null) {
            retorno += " and f.id = " + funcao.getId();
        }
        return retorno;
    }

    public void gerarRelatorioAnexoOito() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", exercicio.getId());
            dto.adicionarParametro("DATA", sistemaControlador.getDataOperacao().getTime());
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio("ANEXO VIII");
            dto.adicionarParametro("USER", mostraUsuario ? "Emitido por: " + getSistemaFacade().getUsuarioCorrente().getNome() : "", false);
            dto.setApi("contabil/anexo-8-planejamento/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioAnexoSete() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", exercicio.getId());
            dto.adicionarParametro("DATA", sistemaControlador.getDataOperacao().getTime());
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio("ANEXO VII");
            dto.adicionarParametro("USER", mostraUsuario ? "Emitido por: " + getSistemaFacade().getUsuarioCorrente().getNome() : "", false);
            dto.setApi("contabil/anexo-7-planejamento/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioAnexoDoisDespesa() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaControlador.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", sistemaControlador.getExercicioCorrente().getId());
            dto.adicionarParametro("tipoRelatorio", tipoRelatorioAnexoDoisDespesa.name());
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(sistemaControlador.getDataOperacao()));
            dto.adicionarParametro("condicao", montarCondicaoAnexoDoisDespesa());
            dto.setNomeRelatorio("Anexo II D");
            dto.setApi("contabil/anexo-dois-despesa/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicaoAnexoDoisDespesa() {
        StringBuilder stb = new StringBuilder();
        if (this.listaUnidades.size() > 0) {
            StringBuilder idUnidades = new StringBuilder();
            String concat = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concat).append(lista.getId());
                concat = ",";
            }
            stb.append(" AND VW.ID IN (").append(idUnidades).append(")");
        }
        return stb.toString();
    }

    private void validarOrgaoAndUnidade() {
        validarOrgao();
        ValidacaoException ve = new ValidacaoException();
        if (unidadeInicial == null && unidadeFinal != null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Inicial deve ser informado.");
        }
        if (unidadeInicial != null && unidadeFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Final deve ser informado.");
        }
        ve.lancarException();
        if (unidadeInicial != null && unidadeFinal != null && unidadeInicial.getCodigo().compareTo(unidadeFinal.getCodigo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código da Unidade Inicial deve ser menor que o código da Unidade Final.");
        }
        ve.lancarException();
    }

    private void validarOrgao() {
        ValidacaoException ve = new ValidacaoException();
        if (orgaoInicial == null && orgaoFinal != null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão Inicial deve ser informado.");
        }
        if (orgaoInicial != null && orgaoFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão Final deve ser informado.");
        }
        ve.lancarException();
        if (orgaoInicial != null && orgaoFinal != null && orgaoInicial.getCodigo().compareTo(orgaoFinal.getCodigo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código do Órgão Inicial deve ser menor que o código do Órgão Final.");
        }
        ve.lancarException();
    }

    @Deprecated
    public String valida() {
        StringBuilder stb = new StringBuilder();
        if (this.orgaoInicial == null && this.orgaoFinal != null) {
            stb.append(" Informe o Orgão Inicial! \n");
        }

        if (this.unidadeInicial == null && this.unidadeFinal != null) {
            stb.append(" Informe a Unidade Inicial! \n");
        }

        if (this.orgaoInicial != null) {
            if (this.orgaoInicial.getCodigo().compareTo(this.orgaoFinal.getCodigo()) > 0) {
                stb.append(" Orgão Inicial maior que o Orgão Final! \n");
            }
        }

        if (this.unidadeInicial != null) {
            if (this.unidadeInicial.getCodigo().compareTo(this.unidadeFinal.getCodigo()) > 0) {
                stb.append(" Unidade Inicial maior que a Unidade Final! \n");
            }
        }
        return stb.toString();
    }

    public void gerarRelatorioQuadroDeDetalhamentoDeDespesaOrcamentariaUnidadeLogada() {
        try {
            Exercicio exercicio = sistemaControlador.getExercicioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("EXERCICIO", "Exercicio: " + exercicio.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", exercicio.getId());
            dto.adicionarParametro("TIPORELATORIO", TipoRelatorioQDD.CONTAS_FONTES_RECURSOS.name());
            dto.adicionarParametro("DATA", sistemaControlador.getDataOperacao().getTime());
            dto.adicionarParametro("complementoQuery", " and VW.subordinada_id = " + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId());
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio("QUADRO DE DETALHAMENTO DE DESPESA ORÇAMENTÁRIA");
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setApi("contabil/qdd-orcamentaria/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioQuadroDeDetalhamentoDeDespesaOrcamentaria() {
        try {
            Exercicio exercicio = sistemaControlador.getExercicioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("EXERCICIO", "Exercicio: " + exercicio.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", exercicio.getId());
            dto.adicionarParametro("TIPORELATORIO", tiporelatorioQDD.name());
            dto.adicionarParametro("DATA", sistemaControlador.getDataOperacao().getTime());
            dto.adicionarParametro("complementoQuery", buscarComplementoQdd());
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio("QUADRO DE DETALHAMENTO DE DESPESA ORÇAMENTÁRIA");
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setApi("contabil/qdd-orcamentaria/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String buscarComplementoQdd() {
        String retorno = " ";
        StringBuilder idsUnidades = new StringBuilder();
        if (!listaUnidades.isEmpty()) {
            String concatena = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : listaUnidades) {
                idsUnidades.append(concatena).append(hierarquiaOrganizacional.getSubordinada().getId());
                concatena = ",";
            }
            retorno += " and VW.SUBORDINADA_ID IN (" + idsUnidades + ") ";
        } else {
            List<HierarquiaOrganizacional> unidadesDoUsuario = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            String concatena = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.append(concatena).append(hierarquiaOrganizacional.getSubordinada().getId());
                concatena = ", ";
            }
            retorno += " and VW.SUBORDINADA_ID IN (" + idsUnidades + ") ";
        }
        if (fonteDeRecursosQDD != null) {
            retorno += " and fonte.id = " + fonteDeRecursosQDD.getId();
        }
        if (contaDespesa != null) {
            retorno += " and b.codigo like '" + contaDespesa.getCodigoSemZerosAoFinal() + "%' ";
        }
        return retorno;
    }

    public HierarquiaOrganizacional getOrgaoFinal() {
        return orgaoFinal;
    }

    public void setOrgaoFinal(HierarquiaOrganizacional orgaoFinal) {
        this.orgaoFinal = orgaoFinal;
    }

    public HierarquiaOrganizacional getOrgaoInicial() {
        return orgaoInicial;
    }

    public void setOrgaoInicial(HierarquiaOrganizacional orgaoInicial) {
        this.orgaoInicial = orgaoInicial;
    }

    public HierarquiaOrganizacional getUnidadeFinal() {
        return unidadeFinal;
    }

    public void setUnidadeFinal(HierarquiaOrganizacional unidadeFinal) {
        this.unidadeFinal = unidadeFinal;
    }

    public HierarquiaOrganizacional getUnidadeInicial() {
        return unidadeInicial;
    }

    public void setUnidadeInicial(HierarquiaOrganizacional unidadeInicial) {
        this.unidadeInicial = unidadeInicial;
    }

    public EsferaOrcamentaria getEsferaOrcamentaria() {
        return esferaOrcamentaria;
    }

    public void setEsferaOrcamentaria(EsferaOrcamentaria esferaOrcamentaria) {
        this.esferaOrcamentaria = esferaOrcamentaria;
    }

    public Boolean getIntra() {
        return intra;
    }

    public void setIntra(Boolean intra) {
        this.intra = intra;
    }

    public BigDecimal getReservaContingencia() {
        return reservaContingencia;
    }

    public void setReservaContingencia(BigDecimal reservaContingencia) {
        this.reservaContingencia = reservaContingencia;
    }

    public TipoRelatorio getTipoRelatorioAnexoDoisDespesa() {
        return tipoRelatorioAnexoDoisDespesa;
    }

    public void setTipoRelatorioAnexoDoisDespesa(TipoRelatorio tipoRelatorioAnexoDoisDespesa) {
        this.tipoRelatorioAnexoDoisDespesa = tipoRelatorioAnexoDoisDespesa;
    }

    public AnexoSeis4320TipoRelatorioDTO getTipoRelatorioAnexoSeis() {
        return tipoRelatorioAnexoSeis;
    }

    public void setTipoRelatorioAnexoSeis(AnexoSeis4320TipoRelatorioDTO tipoRelatorioAnexoSeis) {
        this.tipoRelatorioAnexoSeis = tipoRelatorioAnexoSeis;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public TipoRelatorioQDD getTiporelatorioQDD() {
        return tiporelatorioQDD;
    }

    public void setTiporelatorioQDD(TipoRelatorioQDD tiporelatorioQDD) {
        this.tiporelatorioQDD = tiporelatorioQDD;
    }

    public List<SelectItem> getTiposRelatoriosQDD() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorioQDD.values());
    }

    public FonteDeRecursos getFonteDeRecursosQDD() {
        return fonteDeRecursosQDD;
    }

    public void setFonteDeRecursosQDD(FonteDeRecursos fonteDeRecursosQDD) {
        this.fonteDeRecursosQDD = fonteDeRecursosQDD;
    }

    public ConverterAutoComplete getConverterFonteDeRecursos() {
        if (converterFonteDeRecursos == null) {
            converterFonteDeRecursos = new ConverterAutoComplete(FonteDeRecursos.class, fonteDeRecursosFacade);
        }
        return converterFonteDeRecursos;
    }

    public ConverterAutoComplete getConverterFuncao() {
        if (converterFuncao == null) {
            converterFuncao = new ConverterAutoComplete(Funcao.class, funcaoFacade);
        }
        return converterFuncao;
    }

    public List<Funcao> completaFuncao(String parte) {
        return funcaoFacade.listaFiltrandoFuncao(parte.trim());
    }

    public List<FonteDeRecursos> completaFonteDeRecursos(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Boolean getMostraUsuario() {
        return mostraUsuario;
    }

    public void setMostraUsuario(Boolean mostraUsuario) {
        this.mostraUsuario = mostraUsuario;
    }

    private enum TipoRelatorio {

        CONSOLIDADO("Consolidado"),
        ORGAO_UNIDADE("Órgão e Unidade"),
        ORGAO("Órgão");
        private String descricao;

        private TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }
}
