/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-demonstrativo-plano-plurianual", pattern = "/relatorio/demonstrativo-plano-plurianual/", viewId = "/faces/financeiro/relatorio/relatorioacompplanoplurianual.xhtml")
})
@ManagedBean
public class RelatorioDemonstPlanoPlurianualControlador extends AbstractReport implements Serializable {

    @EJB
    private PPAFacade pPAFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private ProjetoAtividadeFacade acaoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private PPA ppa;
    private ProgramaPPA programaPPA;
    private AcaoPPA acaoPPA;
    private ContaDespesa conta;
    private FonteDeRecursos fonteDeRecursos;
    private List<HierarquiaOrganizacional> unidades;

    public RelatorioDemonstPlanoPlurianualControlador() {
    }

    public List<PPA> completarPPAs(String parte) {
        return pPAFacade.listaTodosPpaExercicio(getSistemaFacade().getExercicioCorrente(), parte);
    }

    public List<ProgramaPPA> completarProgramas(String parte) {
        if (ppa != null) {
            return programaPPAFacade.listaFiltrandoProgramasPorPPAeExercicio(parte.trim(), ppa, getSistemaFacade().getExercicioCorrente());
        }
        return Lists.newArrayList();
    }

    public List<AcaoPPA> completarAcoes(String parte) {
        if (programaPPA != null) {
            return acaoFacade.listaAcaoPPAPorPPAEPrograma(ppa, programaPPA, parte.trim());
        }
        return acaoFacade.listaAcaoPPAPorPPA(ppa, parte.trim());
    }

    public List<Conta> completarContasDeDespesa(String parte) {
        return contaFacade.listaFiltrandoDespesaCriteria(parte.trim(), getSistemaFacade().getExercicioCorrente());
    }

    public List<FonteDeRecursos> completarFontes(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte.trim(), getSistemaFacade().getExercicioCorrente());
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("EXERCICIO_ID", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("condicao", montarCondicao());
            dto.setNomeRelatorio("Relatório Demonstrativo do Acompanhamento do Plano Plurianual");
            dto.setApi("contabil/demonstrativo-plano-plurianual/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    @URLAction(mappingId = "relatorio-demonstrativo-plano-plurianual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        acaoPPA = null;
        ppa = null;
        programaPPA = null;
        fonteDeRecursos = null;
        conta = null;
        unidades = Lists.newArrayList();
    }

    public String montarCondicao() {
        StringBuilder stb = new StringBuilder();
        if (ppa != null) {
            stb.append(" AND PPA.ID = ").append(ppa.getId());
        }
        if (acaoPPA != null) {
            stb.append(" AND AC.ID = ").append(acaoPPA.getId());
        }
        if (programaPPA != null) {
            stb.append(" AND PROG.ID = ").append(programaPPA.getId());
        }
        if (fonteDeRecursos != null) {
            stb.append(" AND FONTE.ID = ").append(fonteDeRecursos.getId());
        }
        if (conta != null) {
            stb.append(" AND C.ID = ").append(conta.getCodigo());
        }
        if (!unidades.isEmpty()) {
            StringBuilder idUnidades = new StringBuilder();
            String concatena = "";
            for (HierarquiaOrganizacional unidade : unidades) {
                idUnidades.append(concatena).append(unidade.getSubordinada().getId());
                concatena = ",";
            }
            stb.append(" and VW.SUBORDINADA_ID IN (").append(idUnidades).append(")");
        } else {
            List<HierarquiaOrganizacional> hierarquias = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getExercicioCorrente(), getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            StringBuilder idUnidades = new StringBuilder();
            String concatena = "";
            for (HierarquiaOrganizacional ho : hierarquias) {
                idUnidades.append(concatena).append(ho.getSubordinada().getId());
                concatena = ", ";
            }
            stb.append(" and VW.SUBORDINADA_ID IN (").append(idUnidades).append(")");
        }
        return stb.toString();
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public ContaDespesa getConta() {
        return conta;
    }

    public void setConta(ContaDespesa conta) {
        this.conta = conta;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }
}
