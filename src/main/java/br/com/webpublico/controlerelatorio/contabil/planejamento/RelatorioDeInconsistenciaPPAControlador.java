package br.com.webpublico.controlerelatorio.contabil.planejamento;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PPA;
import br.com.webpublico.entidades.ProgramaPPA;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoProgramaPPA;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.PPAFacade;
import br.com.webpublico.negocios.ProgramaPPAFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by renato on 28/07/17.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-inconsistencia-ppa", pattern = "/relatorio/inconsistencia-ppa/", viewId = "/faces/financeiro/relatorio/relatorio-inconsistencia-ppa.xhtml")
})
public class RelatorioDeInconsistenciaPPAControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PPAFacade pPAFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    private PPA ppa;
    private Exercicio exercicio;
    private ProgramaPPA[] programasSelecionados;
    private Boolean mostrarTotalGeral;
    private Boolean mostrarRodape;

    @URLAction(mappingId = "relatorio-inconsistencia-ppa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposPPaPrograma() {
        exercicio = sistemaFacade.getExercicioCorrente();
        programasSelecionados = null;
        ppa = null;
        mostrarRodape = Boolean.TRUE;
        mostrarTotalGeral = Boolean.TRUE;
    }

    public void gerarRelatorio() {
        try {
            validarRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("NOME", "Relatório de inconsistência do " + ppa.getDescricao());
            dto.adicionarParametro("TOTALGERAL", mostrarTotalGeral);
            dto.adicionarParametro("RODAPE", mostrarRodape);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/inconsistencia-ppa/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício.");
        }
        if (ppa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o PPA.");
        }
        if (programasSelecionados == null || programasSelecionados.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("É necessário pelo menos ter 1 (UM) programa selecionado.");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        if (ppa != null) {
            parametros.add(new ParametrosRelatorios(" PPA.id ", ":ppaId", null, OperacaoRelatorio.IGUAL, ppa.getId(), null, 1, false));
        }
        if (programasSelecionados != null && programasSelecionados.length > 0) {
            List<Long> ids = new ArrayList<>();
            for (ProgramaPPA prog : programasSelecionados) {
                ids.add(prog.getId());
            }
            parametros.add(new ParametrosRelatorios(" PROGRAMAPPA.id ", ":progId", null, OperacaoRelatorio.IN, ids, null, 1, false));
        }
        return parametros;
    }

    public String getNomeRelatorio() {
        return "RELATORIO-INCONSISTENCIA-PPA";
    }

    public List<SelectItem> getPPAs() {
        List<SelectItem> toreturn = new ArrayList<SelectItem>();
        toreturn.add(new SelectItem(null, ""));
        if (exercicio != null) {
            for (PPA object : pPAFacade.listaTodosPpaExericicioCombo(exercicio)) {
                toreturn.add(new SelectItem(object, object.toString()));
            }
        } else {
            toreturn = null;
        }
        return toreturn;
    }

    public List<ProgramaPPA> getProgramasPPA() {
        List<ProgramaPPA> listaDados = programaPPAFacade.recuperaProgramasPPA(ppa);
        if (!listaDados.isEmpty()) {
            Collections.sort(listaDados, new Comparator<ProgramaPPA>() {
                @Override
                public int compare(ProgramaPPA o1, ProgramaPPA o2) {
                    String i1 = o1.getCodigo();
                    String i2 = o2.getCodigo();
                    return i1.compareTo(i2);
                }
            });
        }
        return listaDados;
    }

    public List<SelectItem> getTipoDePrograma() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (TipoProgramaPPA obj : TipoProgramaPPA.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public ProgramaPPA[] getProgramasSelecionados() {
        return programasSelecionados;
    }

    public void setProgramasSelecionados(ProgramaPPA[] programasSelecionados) {
        this.programasSelecionados = programasSelecionados;
    }

    public Boolean getMostrarTotalGeral() {
        return mostrarTotalGeral;
    }

    public void setMostrarTotalGeral(Boolean mostrarTotalGeral) {
        this.mostrarTotalGeral = mostrarTotalGeral;
    }

    public Boolean getMostrarRodape() {
        return mostrarRodape;
    }

    public void setMostrarRodape(Boolean mostrarRodape) {
        this.mostrarRodape = mostrarRodape;
    }
}
