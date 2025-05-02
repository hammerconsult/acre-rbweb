/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ProgramaPPA;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacadeOLD;
import br.com.webpublico.negocios.ProgramaPPAFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author reidocrime
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-diretrizes-orcamentarias", pattern = "/relatorio/diretrizesorcamentarias/", viewId = "/faces/financeiro/relatorio/relatoriodiretrizesorcmetaspriori.xhtml")})
public class RelatorioDiretrizesOrcamentariasMetaPriori extends AbstractReport implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioDiretrizesOrcamentariasMetaPriori.class);

    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ProgramaPPA programa;
    private Date dtInicial;
    private Date dtFinal;
    private HierarquiaOrganizacional orgaoSelecionadoInicio;
    private HierarquiaOrganizacional orgaoSelecionadoFim;
    private HierarquiaOrganizacional orgaoSelecionado;
    private List<HierarquiaOrganizacional> listaUnidadesdoOrgao;
    private ConverterAutoComplete converterOrgao;
    private ConverterAutoComplete converterProgramaPPA;

    public ConverterAutoComplete getConverterOrgao() {
        if (converterOrgao == null) {
            converterOrgao = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterOrgao;
    }

    public ConverterAutoComplete getConverterProgramaPPA() {
        if (converterProgramaPPA == null) {
            converterProgramaPPA = new ConverterAutoComplete(ProgramaPPA.class, programaPPAFacade);
        }
        return converterProgramaPPA;
    }

    public List<ProgramaPPA> completaProgramasPPA(String parte) {
        return programaPPAFacade.listaFiltrandoProgramasPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<HierarquiaOrganizacional> completaOrgao(String parte) {
        List<HierarquiaOrganizacional> lista = hierarquiaOrganizacionalFacade.filtraPorNivel(parte, "2", TipoHierarquiaOrganizacional.ORCAMENTARIA, sistemaControlador.getExercicioCorrente());
        return lista;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getOrgaoSelecionado() {
        return orgaoSelecionado;
    }

    public void setOrgaoSelecionado(HierarquiaOrganizacional orgaoSelecionado) {
        this.orgaoSelecionado = orgaoSelecionado;
    }

    public HierarquiaOrganizacional getOrgaoSelecionadoFim() {
        return orgaoSelecionadoFim;
    }

    public void setOrgaoSelecionadoFim(HierarquiaOrganizacional orgaoSelecionadoFim) {
        this.orgaoSelecionadoFim = orgaoSelecionadoFim;
    }

    public HierarquiaOrganizacional getOrgaoSelecionadoInicio() {
        return orgaoSelecionadoInicio;
    }

    public void setOrgaoSelecionadoInicio(HierarquiaOrganizacional orgaoSelecionadoInicio) {
        this.orgaoSelecionadoInicio = orgaoSelecionadoInicio;
    }

    public List<HierarquiaOrganizacional> getListaUnidadesdoOrgao() {
        return listaUnidadesdoOrgao;
    }

    public void setListaUnidadesdoOrgao(List<HierarquiaOrganizacional> listaUnidadesdoOrgao) {
        this.listaUnidadesdoOrgao = listaUnidadesdoOrgao;
    }

    public ProgramaPPA getPrograma() {
        return programa;
    }

    public void setPrograma(ProgramaPPA programa) {
        this.programa = programa;
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

    @URLAction(mappingId = "relatorio-diretrizes-orcamentarias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        orgaoSelecionadoInicio = null;
        orgaoSelecionadoFim = null;
        orgaoSelecionado = null;
        listaUnidadesdoOrgao = null;
        dtInicial = null;
        dtFinal = null;
    }

    private String concatenaId(List<HierarquiaOrganizacional> lista) {
        StringBuilder stb = new StringBuilder();
        for (HierarquiaOrganizacional h : lista) {
            if (stb.length() > 1) {
                stb.append(",");
            }
            stb.append(h.getId());
        }
        return stb.toString();
    }

    public void relatorioPorUnidades() throws JRException, IOException {
        if (listaUnidadesdoOrgao != null || orgaoSelecionado != null) {
            String arquivoJasper = "RelDiretrizesORcamentariaAxexoMetasPriori.jasper";
            HashMap parameters = new HashMap();
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

            parameters.put("DATAINICIAL", formato.format(sistemaControlador.getDataOperacao()));
            parameters.put("DATAFINAL", formato.format(sistemaControlador.getDataOperacao()));
            parameters.put("EXERCICIO_ID", sistemaControlador.getExercicioCorrente().getId());
            parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno().toString());
            parameters.put("MUNICIPIO", sistemaControlador.getMunicipio());
            parameters.put("IMAGEM", getCaminhoImagem());
            parameters.put("SUBREPORT_DIR", getCaminho());
//            parameters.put("LISTA_ORGAO", listaUnidadesdoOrgao); Verificar dentro do relatório onde é utilizar. Retirado apenas para a geração do mesmo
            parameters.put("CONDICAO_UND", " AND ho_SUB.SUBORDINADA_ID IN(" + concatenaId(listaUnidadesdoOrgao) + ") ");
            if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                parameters.put("USER", sistemaControlador.getUsuarioCorrente().getNome());
            } else {
                parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
            }
            gerarRelatorio(arquivoJasper, parameters);
        } else {
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valor Nulo", "Não foi selecionada nenhuma unidade"));
        }

    }

    public void relatorioPorFaixa() {
        List<HierarquiaOrganizacional> listaHi;
        String arquivoJasper = "RelDiretrizesORcamentariaAxexoMetasPriori.jasper";
        HashMap parameters = new HashMap();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        parameters.put("DATAINICIAL", formato.format(sistemaControlador.getDataOperacao()));
        parameters.put("DATAFINAL", formato.format(sistemaControlador.getDataOperacao()));
        parameters.put("EXERCICIO_ID", sistemaControlador.getExercicioCorrente().getId());
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno().toString());
        parameters.put("MUNICIPIO", "Município de Rio Branco - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", getCaminho());
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        }
        //System.out.println("parametros: "+formato.format(sistemaControlador.getDataOperacao()) + " - " + sistemaControlador.getExercicioCorrente().getId() + " - "+ sistemaControlador.getExercicioCorrente().getAno().toString() + " - " + sistemaControlador.getMunicipio() + " - "+ getCaminho());
        try {
            if (orgaoSelecionadoInicio != null || orgaoSelecionadoFim != null) {
                if (validaOrgao()) {
                    listaHi = hierarquiaOrganizacionalFacade.listaIntervalo(orgaoSelecionadoInicio.getCodigo(), orgaoSelecionadoFim.getCodigo(), "2", sistemaControlador.getExercicioCorrente());
                    if (!listaHi.isEmpty()) {
                        parameters.put("SQL", " and vworg.id in (" + concatenaId(listaHi) + ")");
                    }
                    gerarRelatorio(arquivoJasper, parameters);
                }
            } else {
                gerarRelatorio(arquivoJasper, parameters);
            }

//            novo();
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void listaUnidades() {
        if (orgaoSelecionado != null) {
            if (orgaoSelecionado.getId() != null) {
                listaUnidadesdoOrgao = hierarquiaOrganizacionalFacade.getHIerarquiaFilhosDe(orgaoSelecionado.getSubordinada(), TipoHierarquiaOrganizacional.ORCAMENTARIA, sistemaControlador.getExercicioCorrente());
                if (listaUnidadesdoOrgao.isEmpty()) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valor Nulo", "Orgão não possui unidades");
                    FacesContext.getCurrentInstance().addMessage("msgs", msg);
                }
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Adicionar", "Selecione um Orgão ");
                FacesContext.getCurrentInstance().addMessage("msgs", msg);
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Adicionar", "Selecione um Orgão ");
            FacesContext.getCurrentInstance().addMessage("msgs", msg);
        }
    }

    public void removerUnidade(HierarquiaOrganizacional hie) {
        RequestContext context = RequestContext.getCurrentInstance();
        listaUnidadesdoOrgao.remove(hie);
        context.update(":Formulario:tabelaUnd");
    }

    private Boolean validaOrgao() {
        if (orgaoSelecionadoInicio == null && orgaoSelecionadoFim != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "Selecione o Órgão Inicial"));
            return false;
        }
        if (orgaoSelecionadoInicio != null && orgaoSelecionadoFim == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "Selecione o Órgão Final"));
            return false;
        }
        if (orgaoSelecionadoInicio.getCodigo().compareTo(orgaoSelecionadoFim.getCodigo()) > 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "O orgão inicial não pode ser maior que o orgão final"));
            return false;
        }
        return true;
    }
}
