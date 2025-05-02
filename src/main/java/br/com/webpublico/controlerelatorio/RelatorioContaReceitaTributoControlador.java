/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author Edi
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-conta-receita-tributo", pattern = "/relatorio/conta-receita-X-tributo/", viewId = "/faces/financeiro/relatorio/relatorio-conta-receita-tributo.xhtml")
})
public class RelatorioContaReceitaTributoControlador extends RelatorioContabilSuperControlador implements Serializable {

    @URLAction(mappingId = "relatorio-conta-receita-tributo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataReferencia = getSistemaControlador().getDataOperacao();
        exercicio = getSistemaControlador().getExercicioCorrente();
        filtros = "";
        conta = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("clausula", gerarSql());
            dto.adicionarParametro("dataReferencia", getSistemaControlador().getDataOperacao());
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("MODULO", "Contábil");
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/conta-receita-tributo/");
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

    public String gerarSql() {
        StringBuilder sb = new StringBuilder();
        filtros = "";
        if (exercicio != null) {
            sb.append(" and enq.exerciciovigente_id = ").append(exercicio.getId());
            filtros += "Exercício: " + exercicio.getAno() + " -";
        }
        if (conta != null) {
            sb.append(" and c.codigo = '").append(conta.getCodigo()).append("'");
            filtros += " Conta de Receita: " + conta.getCodigo();
        }
        atualizaFiltrosGerais();
        return sb.toString();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        ve.lancarException();
    }

    public void atribuirNullContaReceita() {
        conta = null;
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-RECEITA-X-TRIBUTO";
    }

    public List<Exercicio> completaExercicio(String parte) {
        return relatorioContabilSuperFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    public List<Conta> completaContaReceita(String parte) {
        if (exercicio != null) {
            return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoContaReceitaPorExercicio(parte.trim(), exercicio);
        } else {
            FacesUtil.addAtencao("Nenhuma conta de receita encontrada para o exercício informado.");
            return Lists.newArrayList();
        }
    }
}
