/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoProgramaPPA;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioPPAFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.contabil.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Usuario
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-ppa", pattern = "/relatorio/ppa/", viewId = "/faces/financeiro/relatorio/relatorioppa.xhtml")
})
public class RelatorioPPAControle extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioPPAFacade relatorioPPAFacade;
    private ConverterAutoComplete converterMacroObjetivoEstrategico;
    private MacroObjetivoEstrategico macroObjetivoEstrategico;
    private PPA ppa;
    private Exercicio exercicio;
    private LDO ldo;
    private ProgramaPPA[] programas;
    private Boolean mostrarTotalizador;
    private Boolean mostrarRodape;
    private Boolean mostrarRodapeRelatorioAcao;
    private Boolean mostrarUnidadeRelatorioAcao;
    @Enumerated(EnumType.STRING)
    private TipoRelatorio tipoRelatorioAcoes;
    @Enumerated(EnumType.STRING)
    private TipoRelatorio tipoRelatorioPrograma;
    @Enumerated(EnumType.STRING)
    private TipoRelatorio tipoRelatorioLDO;

    @URLAction(mappingId = "relatorio-ppa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        exercicio = null;
        ppa = null;
        macroObjetivoEstrategico = null;
        mostrarTotalizador = true;
        mostrarRodape = true;
        mostrarRodapeRelatorioAcao = true;
        mostrarUnidadeRelatorioAcao = true;
        tipoRelatorioAcoes = TipoRelatorio.SEM_TOTALIZADOR;
        tipoRelatorioPrograma = TipoRelatorio.SEM_TOTALIZADOR;
        tipoRelatorioLDO = TipoRelatorio.SEM_TOTALIZADOR;
    }

    public List<SelectItem> getTiposDePrograma() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (TipoProgramaPPA obj : TipoProgramaPPA.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<ProgramaPPA> getProgramasPPA() {
        List<ProgramaPPA> programas = relatorioPPAFacade.getProgramaPPAFacade().recuperaProgramasPPA(ppa);
        if (!programas.isEmpty()) {
            Collections.sort(programas, new Comparator<ProgramaPPA>() {
                @Override
                public int compare(ProgramaPPA o1, ProgramaPPA o2) {
                    String i1 = o1.getCodigo();
                    String i2 = o2.getCodigo();
                    return i1.compareTo(i2);
                }
            });
        }
        return programas;
    }

    public List<MacroObjetivoEstrategico> completarMacrosObjetivoEstrategicos(String parte) {
        return relatorioPPAFacade.getMacroObjetivoEstrategicoFacade().listaFiltrando(parte, "descricao");
    }

    private String concatenarIDSProgramas() {
        String ids = "";
        if (programas.length > 0) {
            for (ProgramaPPA programaPPA : this.getProgramas()) {
                ids += programaPPA.getId() + ",";
            }
            ids += ids.substring(0, ids.length() - 1);
        }
        return ids;
    }

    public List<SelectItem> buscarPPAs() {
        List<SelectItem> toreturn = Lists.newArrayList();
        toreturn.add(new SelectItem(null, ""));
        if (exercicio != null) {
            for (PPA object : relatorioPPAFacade.getpPAFacade().listaTodosPpaExericicioCombo(exercicio)) {
                toreturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toreturn;
    }

    public List<SelectItem> buscarLDOs() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        List<LDO> ldos = null;
        if (ppa == null) {
            ldos = relatorioPPAFacade.getlDOFacade().lista();
        } else {
            ldos = relatorioPPAFacade.getlDOFacade().listaLdoPorPpa(ppa);
        }
        for (LDO ldo : ldos) {
            if (ldo.getAtoLegal() != null) {
                toReturn.add(new SelectItem(ldo, ldo.getExercicio() + " - " + ldo.getAtoLegal().getNome()));
            } else {
                toReturn.add(new SelectItem(ldo, ldo.getExercicio().toString()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposDeRelatorioProgramas() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoRelatorio tipoRelatorio : TipoRelatorio.values()) {
            if (!TipoRelatorio.TOTALIZADOR_INVESTIMENTOS.equals(tipoRelatorio)) {
                toReturn.add(new SelectItem(tipoRelatorio, tipoRelatorio.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposDeRelatorioLDO() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoRelatorio tipoRelatorio : TipoRelatorio.values()) {
            if (!TipoRelatorio.TOTALIZADOR_INVESTIMENTOS.equals(tipoRelatorio)) {
                toReturn.add(new SelectItem(tipoRelatorio, tipoRelatorio.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposDeRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoRelatorio tipoRelatorio : TipoRelatorio.values()) {
            if (!TipoRelatorio.TOTALIZADOR_METAS_FISICAS.equals(tipoRelatorio)) {
                toReturn.add(new SelectItem(tipoRelatorio, tipoRelatorio.getDescricao()));
            }
        }
        return toReturn;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (ppa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo PPA deve ser informado.");
        }
        if (concatenarIDSProgramas().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Para gerar o relatório é obrigatório selecionar um programa.");
        }
        ve.lancarException();
    }

    public void gerarRelatorioAcaoPPA() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("MUNICIPIO", "Prefeitura Municipal de Rio Branco - AC");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("ID_PROGRAMA", concatenarIDSProgramas());
            dto.adicionarParametro("TIPORELATORIO", tipoRelatorioAcoes.name());
            dto.adicionarParametro("MOSTRA_UNIDADE", mostrarUnidadeRelatorioAcao);
            dto.adicionarParametro("MOSTRARRODAPE", mostrarRodapeRelatorioAcao);
            dto.setNomeRelatorio("Relatório de Ação");
            dto.setApi("contabil/ppa/acao-ppa/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioPPAPrograma() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("PPA", ppa.getId());
            dto.adicionarParametro("MOSTRATOTALIZADOR", mostrarTotalizador);
            dto.adicionarParametro("MOSTRARODAPE", mostrarRodape);
            dto.adicionarParametro("TIPORELATORIO", tipoRelatorioPrograma.name());
            dto.adicionarParametro("ANO", getSistemaFacade().getExercicioCorrente().getAno());
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("PROGRAMA_PPA", concatenarIDSProgramas());
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco - AC");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            if (macroObjetivoEstrategico != null) {
                dto.adicionarParametro("condicao", " AND PROGRAMAPPA.MACROOBJETIVOESTRATEGICO_ID = " + macroObjetivoEstrategico.getId());
            }
            dto.setNomeRelatorio("Relatório de Programa");
            dto.setApi("contabil/ppa/programa-ppa/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        }  catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void relatorioLDO() {
        if (exercicio != null && ppa != null) {
            buscarProgramasPorLdo();
        } else {
            gerarRelatorioPorLdo();
        }
    }

    private void validarCamposLdo() {
        ValidacaoException ve = new ValidacaoException();
        if (ldo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe uma LDO ou selecione um exercício e um PPA para gerar o relatório a partir de um programa selecionado.");
        }
        ve.lancarException();
    }

    private void gerarRelatorioPorLdo() {
        try {
            validarCamposLdo();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco - AC");
            dto.adicionarParametro("TIPORELATORIO", tipoRelatorioLDO.name());
            dto.adicionarParametro("CONDICAO_EXERC", " and exerc.id = " + ldo.getExercicio().getId());
            dto.adicionarParametro("LDO_ID", +ldo.getId());
            dto.setNomeRelatorio("Relatório de Programas de Diretrizes Orçamentárias");
            dto.setApi("contabil/ppa/ldo/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void buscarProgramasPorLdo() {
        try {
            List<ProgramaPPA> programas = Lists.newArrayList();
            if (ldo != null) {
                ldo = relatorioPPAFacade.getlDOFacade().recuperar(ldo.getId());
                List<ProvisaoPPALDO> provisoesPpaLdo = ldo.getProvisaoPPALDOs();
                if (!provisoesPpaLdo.isEmpty()) {
                    for (ProvisaoPPALDO prov : provisoesPpaLdo) {
                        ProgramaPPA programa = prov.getProdutoPPA().getAcaoPrincipal().getPrograma();
                        if (programa != null) {
                            programas.add(programa);
                            ppa = programa.getPpa();
                            break;
                        }
                    }
                    this.programas = programas.toArray(this.programas);
                }
            }
            gerarRelatorioPPAPrograma();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao recuperar valores para a LDO. " + e.getMessage());
        }
    }

    public ConverterAutoComplete getConverterMacroObjetivoEstrategico() {
        if (converterMacroObjetivoEstrategico == null) {
            converterMacroObjetivoEstrategico = new ConverterAutoComplete(MacroObjetivoEstrategico.class, relatorioPPAFacade.getMacroObjetivoEstrategicoFacade());
        }
        return converterMacroObjetivoEstrategico;
    }

    public MacroObjetivoEstrategico getMacroObjetivoEstrategico() {
        return macroObjetivoEstrategico;
    }

    public void setMacroObjetivoEstrategico(MacroObjetivoEstrategico macroObjetivoEstrategico) {
        this.macroObjetivoEstrategico = macroObjetivoEstrategico;
    }

    public Boolean getMostrarTotalizador() {
        return mostrarTotalizador;
    }

    public void setMostrarTotalizador(Boolean mostrarTotalizador) {
        this.mostrarTotalizador = mostrarTotalizador;
    }

    public TipoRelatorio getTipoRelatorioPrograma() {
        return tipoRelatorioPrograma;
    }

    public void setTipoRelatorioPrograma(TipoRelatorio tipoRelatorioPrograma) {
        this.tipoRelatorioPrograma = tipoRelatorioPrograma;
    }

    public Boolean getMostrarRodape() {
        return mostrarRodape;
    }

    public void setMostrarRodape(Boolean mostrarRodape) {
        this.mostrarRodape = mostrarRodape;
    }

    public Boolean getMostrarRodapeRelatorioAcao() {
        return mostrarRodapeRelatorioAcao;
    }

    public void setMostrarRodapeRelatorioAcao(Boolean mostrarRodapeRelatorioAcao) {
        this.mostrarRodapeRelatorioAcao = mostrarRodapeRelatorioAcao;
    }

    public Boolean getMostrarUnidadeRelatorioAcao() {
        return mostrarUnidadeRelatorioAcao;
    }

    public void setMostrarUnidadeRelatorioAcao(Boolean mostrarUnidadeRelatorioAcao) {
        this.mostrarUnidadeRelatorioAcao = mostrarUnidadeRelatorioAcao;
    }

    public TipoRelatorio getTipoRelatorioLDO() {
        return tipoRelatorioLDO;
    }

    public void setTipoRelatorioLDO(TipoRelatorio tipoRelatorioLDO) {
        this.tipoRelatorioLDO = tipoRelatorioLDO;
    }

    public TipoRelatorio getTipoRelatorioAcoes() {
        return tipoRelatorioAcoes;
    }

    public void setTipoRelatorioAcoes(TipoRelatorio tipoRelatorioAcoes) {
        this.tipoRelatorioAcoes = tipoRelatorioAcoes;
    }

    public ProgramaPPA[] getProgramas() {
        return programas;
    }

    public void setProgramas(ProgramaPPA[] programas) {
        this.programas = programas;
    }
    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }


    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }


    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    private enum TipoRelatorio {
        SEM_TOTALIZADOR("Relatório sem Totalizador", TipoRelatorioDTO.SEM_TOTALIZADOR),
        TOTALIZADOR_SEM_ANO("Relatório com Totalizador sem Ano", TipoRelatorioDTO.TOTALIZADOR_SEM_ANO),
        TOTALIZADOR_SEM_VALOR("Relatório com Totalizador sem Valores", TipoRelatorioDTO.TOTALIZADOR_SEM_VALOR),
        TOTALIZADOR_COM_VALOR("Relatório com Totalizador e Valores", TipoRelatorioDTO.TOTALIZADOR_COM_VALOR),
        TOTALIZADOR_METAS_FISICAS("Relatório das Metas Físicas", TipoRelatorioDTO.TOTALIZADOR_METAS_FISICAS),
        TOTALIZADOR_INVESTIMENTOS("Relatório dos Investimentos", TipoRelatorioDTO.TOTALIZADOR_INVESTIMENTOS);
        private String descricao;
        private TipoRelatorioDTO toDto;

        TipoRelatorio(String descricao, TipoRelatorioDTO toDto) {
            this.descricao = descricao;
            this.toDto = toDto;
        }

        public String getDescricao() {
            return descricao;
        }

        public TipoRelatorioDTO getToDto() {
            return toDto;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

}
