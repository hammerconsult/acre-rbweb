/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.ProcessoCalculoIPTUFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/*
 * @author leonardo
 */

@ManagedBean(name = "relatorioLancamentoIptuPorInscricao")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRelatorioLancamentoIptuPorInscricao", pattern = "/tributario/cadastromunicipal/relatorio/relatoriolancamentosporinscricao/", viewId = "/faces/tributario/cadastromunicipal/relatorio/relatoriolancamentosporinscricao.xhtml")
})
public class RelatorioLancamentoIptuPorInscricao extends AbstractReport {

    @Limpavel(Limpavel.NULO)
    private ProcessoCalculoIPTU processoCalculoIPTU;
    @EJB
    private ProcessoCalculoIPTUFacade processoCalculoIPTUFacade;
    private Converter converterProcessoCalculoIPTU;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroExercicio;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String inscricaoInicial;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String inscricaoFinal;
    private TipoCadastroTributario tipoCadastroTributario;

    public RelatorioLancamentoIptuPorInscricao() {
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String InscricaoFinal) {
        this.inscricaoFinal = InscricaoFinal;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String InscricaoInicial) {
        this.inscricaoInicial = InscricaoInicial;
    }

    public ProcessoCalculoIPTU getProcessoCalculoIPTU() {
        return processoCalculoIPTU;
    }

    public void setProcessoCalculoIPTU(ProcessoCalculoIPTU processoCalculoIPTU) {
        this.processoCalculoIPTU = processoCalculoIPTU;
    }

    public String getFiltroExercicio() {
        return filtroExercicio;
    }

    public void setFiltroExercicio(String filtroExercicio) {
        this.filtroExercicio = filtroExercicio;
    }

    public Converter getConverterProcessoCalculoIPTU() {
        if (converterProcessoCalculoIPTU == null) {
            converterProcessoCalculoIPTU = new ConverterAutoComplete(ProcessoCalculoIPTU.class, processoCalculoIPTUFacade);
        }
        return converterProcessoCalculoIPTU;
    }

    public List<ProcessoCalculoIPTU> completaProcessoCalculoIPTU(String parte) {
        List<ProcessoCalculoIPTU> lista = processoCalculoIPTUFacade.listaProcessosPorDescricao(parte);
        ProcessoCalculoIPTU erro = new ProcessoCalculoIPTU();
        if (lista.isEmpty() && parte.trim().equals("")) {
            erro.setDescricao("Não existem processos de cálculo de IPTU");
        } else if (lista.isEmpty()) {
            erro.setDescricao("Não foi encontrada o processo de cálculo de IPTU");
        } else {
            return lista;
        }
        lista.add(erro);
        return lista;
    }

    @URLAction(mappingId = "novoRelatorioLancamentoIptuPorInscricao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        Util.limparCampos(this);
        tipoCadastroTributario = null;
    }

    public void montaRelatorio() throws IOException, JRException {
        String arquivoJasper = "RelatorioSimulacaoIptuSintetico.jasper";
        HashMap parametros = new HashMap();


        String where = "";
        if ((this.inscricaoFinal != null && !this.inscricaoFinal.toString().equals("")) && (this.inscricaoInicial != null && !this.inscricaoInicial.toString().equals(""))) {
            where += " and inscricao between " + this.inscricaoInicial.toString() + " and " + this.inscricaoFinal.toString();
            parametros.put("INSCRICAOINICIAL", this.inscricaoInicial.toString());
            parametros.put("INSCRICAOFINAL", this.inscricaoFinal.toString());
        }
        if (this.filtroExercicio != null && !this.filtroExercicio.equals("")) {
            where += " and ano = " + this.filtroExercicio;
        }
        if (this.processoCalculoIPTU != null && this.processoCalculoIPTU.getDescricao().equals("")) {
            where += " and descprocesso = " + this.processoCalculoIPTU.getDescricao();
        }
        parametros.put("WHERE", where);
        parametros.put("BRASAO", getCaminhoImagem());
        gerarRelatorio(arquivoJasper, parametros);

    }
}
