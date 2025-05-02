/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.MacroObjetivoEstrategicoFacade;
import br.com.webpublico.negocios.PPAFacade;
import br.com.webpublico.negocios.ProgramaPPAFacade;
import br.com.webpublico.negocios.ProjetoAtividadeFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Preconditions;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author wiplash
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-demonstrativo-eixo-programa", pattern = "/relatorio/demonstrativo-eixo-programa/", viewId = "/faces/financeiro/relatorio/relatoriodemonsteixoprograma.xhtml")
})
public class RelatorioDemonstrativoEixoProgramaControlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private MacroObjetivoEstrategicoFacade macroObjetivoEstrategicoFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private ProjetoAtividadeFacade acaoFacade;
    @EJB
    private PPAFacade pPAFacade;
    private PPA ppa;
    private AcaoPPA acaoPPA;
    private ProgramaPPA programaPPA;
    private MacroObjetivoEstrategico macroObjetivoEstrategico;
    private ConverterAutoComplete converterMacroObjetivoEstrategico;
    private ConverterAutoComplete converterProgramaPPA;
    private ConverterAutoComplete converterAcaoPPA;
    private ConverterAutoComplete converterPPA;
    @Enumerated(EnumType.STRING)
    private TipoRelatorio tipoRelatorio;
    private Exercicio exInicial;
    private Exercicio exFinal;

    private enum TipoRelatorio {

        CONSOLIDADO("Consolidado"),
        ORGAO("Orgão");
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

    public RelatorioDemonstrativoEixoProgramaControlador() {
    }

    public List<MacroObjetivoEstrategico> listaMacroObjetivoEstrategicos(String parte) {
        return macroObjetivoEstrategicoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<ProgramaPPA> listaProgramaPPAs(String parte) {
        return programaPPAFacade.listaFiltrandoProgramasPorMacroObjetivo(parte, macroObjetivoEstrategico);
    }

    public List<AcaoPPA> listaAcoesPPA(String parte) {
        return acaoFacade.buscarProjetoAtividadePorPrograma(programaPPA, parte);
    }

    public List<PPA> listaPPA(String parte) {
        return pPAFacade.listaFiltrandoPPA(parte);
    }

    public List<SelectItem> getListaTipoRelatorios() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoRelatorio tp : TipoRelatorio.values()) {
            toReturn.add(new SelectItem(tp, tp.descricao));
        }
        return toReturn;
    }

    @URLAction(mappingId = "relatorio-demonstrativo-eixo-programa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        macroObjetivoEstrategico = null;
    }

    public void recuperaEx() {
        try {
            Preconditions.checkNotNull(ppa, "Problema ao utilizar o PPA selecionado");
            List<Exercicio> lista = pPAFacade.exercicioPorPPA(ppa);
            int tamanho = lista.size();
            Preconditions.checkArgument(tamanho > 0, "Não foram encontrados exercícios para o PPA selecionado");
            exInicial = lista.get(0);
            exFinal = lista.get(tamanho - 1);
        } catch (Exception ex) {
            FacesUtil.addError("Erro", ex.getMessage());
        }

    }

    private Boolean validaCampos() {
        Boolean controle = true;
        if (ppa == null && tipoRelatorio.equals(TipoRelatorio.ORGAO)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo Obrigatório", "O campo PPA é Obrigatório!"));
            controle = false;
        }
        return controle;
    }

    public void gerarRelatorio() {
        if (validaCampos()) {
            try {
                String nomeRelatorio;
                HashMap parameters = new HashMap();
                parameters.put("SUBREPORT_DIR", getCaminho());
                parameters.put("IMAGEM", getCaminhoImagem());
                String sql = gerarSql();
                if (!sql.trim().equals("")) {
                    parameters.put("SQL", sql);
                } else {
                    parameters.put("SQL", " 1 = 1 ");
                }
                if (tipoRelatorio.equals(TipoRelatorio.CONSOLIDADO)) {
                    nomeRelatorio = "RelatorioDemonstrativoEixoProgramaPlanoPlurianual.jasper";
                } else {
                    nomeRelatorio = "RelatorioDemonstrativoEixoProgramaPlanoPlurianualOrgao.jasper";
                }
                if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                    parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
                } else {
                    parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
                }
                gerarRelatorio(nomeRelatorio, parameters);
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao gerar o relatório", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
            }
        }
    }

    private String gerarSql() {
        StringBuilder stb = new StringBuilder();
        String concat = "";
        if (macroObjetivoEstrategico != null) {
            stb.append(concat).append(" MACRO.ID = ").append(macroObjetivoEstrategico.getId());
            concat = " AND ";
        }
        if (programaPPA != null) {
            stb.append(concat).append(" PROG.ID = ").append(programaPPA.getId());
            concat = " AND ";
        }
        if (acaoPPA != null) {
            stb.append(concat).append(" ACAO.ID = ").append(acaoPPA.getId());
            concat = " AND ";
        }
        if (tipoRelatorio.equals(TipoRelatorio.ORGAO)) {
            recuperaEx();
            stb.append(concat).append(" TO_DATE(\'").append("01/01/").append(exInicial.getAno()).append("\', 'DD/MM/YYYY') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(\'").append("31/12/").append(exFinal.getAno()).append("\', 'DD/MM/YYYY')) ");
        }
        return stb.toString();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public MacroObjetivoEstrategico getMacroObjetivoEstrategico() {
        return macroObjetivoEstrategico;
    }

    public void setMacroObjetivoEstrategico(MacroObjetivoEstrategico macroObjetivoEstrategico) {
        this.macroObjetivoEstrategico = macroObjetivoEstrategico;
    }

    public MacroObjetivoEstrategicoFacade getMacroObjetivoEstrategicoFacade() {
        return macroObjetivoEstrategicoFacade;
    }

    public void setMacroObjetivoEstrategicoFacade(MacroObjetivoEstrategicoFacade macroObjetivoEstrategicoFacade) {
        this.macroObjetivoEstrategicoFacade = macroObjetivoEstrategicoFacade;
    }

    public ProgramaPPAFacade getProgramaPPAFacade() {
        return programaPPAFacade;
    }

    public void setProgramaPPAFacade(ProgramaPPAFacade programaPPAFacade) {
        this.programaPPAFacade = programaPPAFacade;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public ConverterAutoComplete getConverterMacroObjetivoEstrategico() {
        if (converterMacroObjetivoEstrategico == null) {
            converterMacroObjetivoEstrategico = new ConverterAutoComplete(MacroObjetivoEstrategico.class, macroObjetivoEstrategicoFacade);
        }
        return converterMacroObjetivoEstrategico;
    }

    public ConverterAutoComplete getConverterProgramaPPA() {
        if (converterProgramaPPA == null) {
            converterProgramaPPA = new ConverterAutoComplete(ProgramaPPA.class, programaPPAFacade);
        }
        return converterProgramaPPA;
    }

    public ConverterAutoComplete getConverterAcaoPPA() {
        if (converterAcaoPPA == null) {
            converterAcaoPPA = new ConverterAutoComplete(AcaoPPA.class, acaoFacade);
        }
        return converterAcaoPPA;
    }

    public void setaMacroObjetivoEstrategico(SelectEvent evt) {
        macroObjetivoEstrategico = (MacroObjetivoEstrategico) evt.getObject();
        programaPPA = null;
        acaoPPA = null;
    }

    public void setaProgramaPPA(SelectEvent evt) {
        programaPPA = (ProgramaPPA) evt.getObject();
        acaoPPA = null;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public PPAFacade getpPAFacade() {
        return pPAFacade;
    }

    public void setpPAFacade(PPAFacade pPAFacade) {
        this.pPAFacade = pPAFacade;
    }

    public ConverterAutoComplete getConverterPPA() {
        if (converterPPA == null) {
            converterPPA = new ConverterAutoComplete(PPA.class, pPAFacade);
        }
        return converterPPA;
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public Exercicio getExInicial() {
        return exInicial;
    }

    public void setExInicial(Exercicio exInicial) {
        this.exInicial = exInicial;
    }

    public Exercicio getExFinal() {
        return exFinal;
    }

    public void setExFinal(Exercicio exFinal) {
        this.exFinal = exFinal;
    }
}
