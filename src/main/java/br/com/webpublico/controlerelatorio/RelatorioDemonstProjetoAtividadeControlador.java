/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FonteDeRecursosFacade;
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
import java.io.Serializable;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-demonstrativo-projeto-atividade", pattern = "/relatorio/demonstrativo-projeto-atividade-segundo-fonte-de-recursos/", viewId = "/faces/financeiro/relatorio/relatoriodemonstprojetoatividade.xhtml")
})

public class RelatorioDemonstProjetoAtividadeControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    private FonteDeRecursos fonteDeRecursosInicial;
    private FonteDeRecursos fonteDeRecursosFinal;
    private Boolean mostraUsuario;

    public RelatorioDemonstProjetoAtividadeControlador() {
    }

    @URLAction(mappingId = "relatorio-demonstrativo-projeto-atividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        fonteDeRecursosFinal = null;
        fonteDeRecursosInicial = null;
        mostraUsuario = Boolean.FALSE;
    }

    public List<FonteDeRecursos> completarFontes(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte, sistemaFacade.getExercicioCorrente());
    }

    public void geraRelatorio() {
        try {
            Exercicio e = sistemaFacade.getExercicioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", mostraUsuario ? "Emitido por: " + sistemaFacade.getUsuarioCorrente().getNome() : "", false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("EXERCICIO_ANO", e.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", e.getId());
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("condicao", montarCondicao());
            dto.setNomeRelatorio("Demonstrativos dos Projetos/Atividades Segundo as Fontes de Recursos");
            dto.setApi("contabil/demonstrativo-projeto-atividade/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicao() {
        String condicao = "";
        if (fonteDeRecursosInicial != null && fonteDeRecursosFinal == null) {
            fonteDeRecursosFinal = fonteDeRecursosInicial;
        } else if (fonteDeRecursosFinal != null && fonteDeRecursosInicial == null) {
            fonteDeRecursosInicial = fonteDeRecursosFinal;
        }
        if (fonteDeRecursosInicial != null) {
            condicao += " and FO.codigo between '" + fonteDeRecursosInicial.getCodigo() + "' and '" + fonteDeRecursosFinal.getCodigo() + "' ";
        }
        return condicao;
    }

    public FonteDeRecursos getFonteDeRecursosFinal() {
        return fonteDeRecursosFinal;
    }

    public void setFonteDeRecursosFinal(FonteDeRecursos fonteDeRecursosFinal) {
        this.fonteDeRecursosFinal = fonteDeRecursosFinal;
    }

    public FonteDeRecursos getFonteDeRecursosInicial() {
        return fonteDeRecursosInicial;
    }

    public void setFonteDeRecursosInicial(FonteDeRecursos fonteDeRecursosInicial) {
        this.fonteDeRecursosInicial = fonteDeRecursosInicial;
    }

    public Boolean getMostraUsuario() {
        return mostraUsuario;
    }

    public void setMostraUsuario(Boolean mostraUsuario) {
        this.mostraUsuario = mostraUsuario;
    }
}
