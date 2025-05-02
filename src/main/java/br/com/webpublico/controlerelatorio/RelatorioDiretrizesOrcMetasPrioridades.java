/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.LDO;
import br.com.webpublico.entidades.PPA;
import br.com.webpublico.entidades.ProgramaPPA;
import br.com.webpublico.enums.TipoProgramaPPA;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.LDOFacade;
import br.com.webpublico.negocios.PPAFacade;
import br.com.webpublico.negocios.ProgramaPPAFacade;
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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-diretrizes-orcamentaria-anexo-metas-prioridades", pattern = "/relatorio/diretrizes-orcamentaria-anexo-metas-prioridades/", viewId = "/faces/financeiro/relatorio/relatoriodiretrizesorcmetasprioridades.xhtml")
})
public class RelatorioDiretrizesOrcMetasPrioridades extends AbstractReport implements Serializable {

    @EJB
    private PPAFacade pPAFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private LDOFacade lDOFacade;
    private Exercicio exercicioSelecionado;
    private ProgramaPPA[] programasSelecionados;
    private PPA ppaSelecionado;
    private LDO ldoSelecionado;
    private Boolean exibirMetaFinanceira;
    private Boolean exibirMetasFiscaisDiferenteZero;
    private Boolean exibirRodape;

    @URLAction(mappingId = "relatorio-diretrizes-orcamentaria-anexo-metas-prioridades", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        exibirMetaFinanceira = false;
        exibirMetasFiscaisDiferenteZero = false;
        exibirRodape = true;
        exercicioSelecionado = null;
        ppaSelecionado = null;
        getProgramasPPA().clear();
    }

    public List<ProgramaPPA> getProgramasPPA() {
        List<ProgramaPPA> listaDados = programaPPAFacade.recuperaProgramasPPA(getPpaSelecionado());
        Collections.sort(listaDados, new Comparator<ProgramaPPA>() {
            @Override
            public int compare(ProgramaPPA o1, ProgramaPPA o2) {
                return o1.getCodigo().compareTo(o2.getCodigo());
            }
        });
        return listaDados;
    }

    public List<SelectItem> getPpas() {
        List<SelectItem> toreturn = new ArrayList<SelectItem>();
        toreturn.add(new SelectItem(null, ""));
        if (exercicioSelecionado != null) {
            for (PPA object : pPAFacade.listaTodosPpaExericicioCombo(exercicioSelecionado)) {
                toreturn.add(new SelectItem(object, object.getDescricao() + " --- " + object.getVersao() + " --- " + object.getAprovacao()));
            }
        } else {
            toreturn = null;
        }
        return toreturn;
    }

    public List<SelectItem> getTipoDePrograma() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (TipoProgramaPPA obj : TipoProgramaPPA.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getLdos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        List<LDO> ldos = null;
        if (ppaSelecionado == null) {
            ldos = lDOFacade.lista();
        } else {
            ldos = lDOFacade.listaLdoPorPpa(ppaSelecionado);
        }

        if (ldos != null) {
            for (LDO l : ldos) {
                if (l.getAtoLegal() != null) {
                    toReturn.add(new SelectItem(l, l.getExercicio() + " - " + l.getAtoLegal().getNome()));
                } else {
                    toReturn.add(new SelectItem(l, l.getExercicio().toString()));
                }
            }
        }
        return toReturn;
    }

    public void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicioSelecionado == null && ldoSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício ou a LDO deve ser informado.");
        }
        if (exercicioSelecionado != null && ppaSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo PPA deve ser informado.");
        }
        if (exercicioSelecionado != null && ppaSelecionado != null && programasSelecionados.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor, selecione um programa na lista.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarCamposObrigatorios();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Prefeitura Municipal de Rio Branco - AC");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("EXIBIRMETAFINANCEIRA", exibirMetaFinanceira);
            dto.adicionarParametro("PASSARPELALDO", ldoSelecionado != null);
            dto.adicionarParametro("EXIBIRRODAPE", exibirRodape);
            dto.adicionarParametro("condicao", montarCondicao());
            if (ldoSelecionado == null) {
                dto.adicionarParametro("EXIBIRMETAFISICADIFZERO", exibirMetasFiscaisDiferenteZero ? " and p.METAFISICA <> 0 " : "");
                dto.adicionarParametro("ANO", exercicioSelecionado.getAno());
                dto.adicionarParametro("CONDICAO_EXERC", " AND EXERC.ID = " + exercicioSelecionado.getId());
                dto.adicionarParametro("EXERCICIO_ID", exercicioSelecionado.getId());
            } else {
                dto.adicionarParametro("ANO", ldoSelecionado.getExercicio().getAno());
                dto.adicionarParametro("CONDICAO_EXERC", " AND EXERC.ID = " + ldoSelecionado.getExercicio().getId());
                dto.adicionarParametro("EXERCICIO_ID", ldoSelecionado.getExercicio().getId());
                dto.adicionarParametro("EXIBIRMETAFISICADIFZERO", exibirMetasFiscaisDiferenteZero ? " and provldo.totalfisico <> 0 and provldo.totalfisico is not null  " : "");
            }
            dto.setNomeRelatorio(nomeArquivo());
            dto.setApi("contabil/diretrizes-orcamentaria-metas-prioridades/");
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

    private String montarCondicao() {
        StringBuilder sb = new StringBuilder();
        String concat = " where ";
        if (ppaSelecionado != null) {
            sb.append(concat).append(" PROGRAMAPPA.PPA_ID = ").append(ppaSelecionado.getId());
            concat = " and ";
        }
        if (ldoSelecionado != null) {
            sb.append(concat).append(" ldo.id = ").append(ldoSelecionado.getId());
            concat = " and ";
        }
        if (programasSelecionados.length > 0) {
            sb.append(concat).append(" programappa.id in (").append(concatenaIDSProgramas()).append(")");
        }
        return sb.toString();
    }

    public String nomeArquivo() {
        if (ldoSelecionado != null) {
            return "ANEXOI-PRIORIDADES-METAS" + "-" + ldoSelecionado.getExercicio().toString();
        }
        return "ANEXOI-PRIORIDADES-METAS" + "-" + exercicioSelecionado.toString();
    }

    public String concatenaIDSProgramas() {
        String ids = "";
        if (programasSelecionados.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (ProgramaPPA programaPPA : this.getProgramasSelecionados()) {
                sb.append(programaPPA.getId()).append(",");
            }
            ids += sb.substring(0, sb.length() - 1);
        }
        return ids;
    }

    public Exercicio getExercicioSelecionado() {
        return exercicioSelecionado;
    }

    public void setExercicioSelecionado(Exercicio exercicioSelecionado) {
        this.exercicioSelecionado = exercicioSelecionado;
    }

    public PPA getPpaSelecionado() {
        return ppaSelecionado;
    }

    public void setPpaSelecionado(PPA ppaSelecionado) {
        this.ppaSelecionado = ppaSelecionado;
    }

    public ProgramaPPA[] getProgramasSelecionados() {

        return programasSelecionados;
    }

    public void setProgramasSelecionados(ProgramaPPA[] programasSelecionados) {
        this.programasSelecionados = programasSelecionados;
    }

    public Boolean getExibirMetaFinanceira() {
        return exibirMetaFinanceira;
    }

    public void setExibirMetaFinanceira(Boolean exibirMetaFinanceira) {
        this.exibirMetaFinanceira = exibirMetaFinanceira;
    }

    public Boolean getExibirMetasFiscaisDiferenteZero() {

        return exibirMetasFiscaisDiferenteZero;
    }

    public void setExibirMetasFiscaisDiferenteZero(Boolean exibirMetasFiscaisDiferenteZero) {
        this.exibirMetasFiscaisDiferenteZero = exibirMetasFiscaisDiferenteZero;
    }

    public LDO getLdoSelecionado() {
        return ldoSelecionado;
    }

    public void setLdoSelecionado(LDO ldoSelecionado) {
        this.ldoSelecionado = ldoSelecionado;
    }

    public Boolean getExibirRodape() {
        return exibirRodape;
    }

    public void setExibirRodape(Boolean exibirRodape) {
        this.exibirRodape = exibirRodape;
    }
}
