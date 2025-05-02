/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PPA;
import br.com.webpublico.entidades.ProgramaPPA;
import br.com.webpublico.enums.TipoProgramaPPA;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
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

/**
 * @author Edi
 */
@ManagedBean(name = "relatorioAcompanhamentoPpaLdoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-acompanhamento-ppa-ldo", pattern = "/relatorio/acompanhamento/ppaldo/", viewId = "/faces/financeiro/relatorio/relatorioacompanhamentoppaldo.xhtml")
})
public class RelatorioAcompanhamentoPpaLdoControlador extends AbstractReport implements Serializable {

    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private PPAFacade pPAFacade;
    private ProgramaPPA[] programasSelecionados;
    private PPA ppa;

    @URLAction(mappingId = "relatorio-acompanhamento-ppa-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.ppa = null;
        this.programasSelecionados = null;
    }

    public List<ProgramaPPA> getProgramasPPA() {
        List<ProgramaPPA> retorno = programaPPAFacade.recuperaProgramasPPA(ppa);
        Collections.sort(retorno, new Comparator<ProgramaPPA>() {
            @Override
            public int compare(ProgramaPPA o1, ProgramaPPA o2) {
                String i1 = o1.getCodigo();
                String i2 = o2.getCodigo();
                return i1.compareTo(i2);
            }
        });
        return retorno;
    }

    public List<PPA> completarPPAs(String parte) {
        return pPAFacade.listaFiltrandoPPA(parte.trim());
    }


    private String concatenarIDSProgramas() {
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

    public List<SelectItem> getTiposDeProgramas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (TipoProgramaPPA obj : TipoProgramaPPA.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (ppa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo PPA deve ser informado.");
        }
        if (programasSelecionados.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Para gerar o relatório é obrigatório selecionar o programa ppa na lista.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            List<Exercicio> exercicios = pPAFacade.listaPpaEx(ppa);
            Collections.sort(exercicios, new Comparator<Exercicio>() {
                @Override
                public int compare(Exercicio o1, Exercicio o2) {
                    return o1.getAno().compareTo(o2.getAno());
                }
            });
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Prefeitura Municipal de Rio Branco - AC");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("condicao", montarCondicao());
            dto.adicionarParametro("PPA_ID", ppa.getId());
            dto.adicionarParametro("EXERC_1", exercicios.get(0).getAno());
            dto.adicionarParametro("EXERC_2", exercicios.get(1).getAno());
            dto.adicionarParametro("EXERC_3", exercicios.get(2).getAno());
            dto.adicionarParametro("EXERC_4", exercicios.get(3).getAno());
            dto.setNomeRelatorio("Relatório de Acompanhamento do PPA e LDO");
            dto.setApi("contabil/acompanhamento-ppa-ldo/");
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
        StringBuilder sql = new StringBuilder();
        sql.append(" and prog.ppa_id = ").append(ppa.getId());
        if (concatenarIDSProgramas().length() > 0) {
            sql.append(" and prog.id in (").append(concatenarIDSProgramas()).append(")");
        }
        return sql.toString();
    }

    public ProgramaPPA[] getProgramasSelecionados() {
        return programasSelecionados;
    }

    public void setProgramasSelecionados(ProgramaPPA[] programasSelecionados) {
        this.programasSelecionados = programasSelecionados;
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }
}
