/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Construcao;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.ConstrucaoFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.ProcessoCalculoIPTUFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author Heinz
 */


@ManagedBean(name = "relatorioDeCalculoIptuIndividual")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novorelatorioDeCalculoIptuIndividual", pattern = "/tributario/cadastromunicipal/relatorio/relatoriograficodecalculosimobiliarios/", viewId = "/faces/tributario/cadastromunicipal/relatorio/relatoriodecalculosimobiliarioindividual.xhtml"),
})
public class RelatorioDeCalculoIptuIndividual extends AbstractReport implements Serializable {

    @Limpavel(Limpavel.NULO)
    private Pessoa contribuinte;
    @EJB
    private PessoaFacade pessoaFacade;
    private Converter converterContribuinte;
    @Limpavel(Limpavel.NULO)
    private CadastroImobiliario cadastroImobiliario;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    private Converter converterCadastroImobiliario;
    @Limpavel(Limpavel.NULO)
    private Construcao construcao;
    @EJB
    private ConstrucaoFacade construcaoFacade;
    private Converter converterConstrucao;
    @Limpavel(Limpavel.NULO)
    private ProcessoCalculoIPTU processoCalculo;
    @EJB
    private ProcessoCalculoIPTUFacade processoCalculoIPTUFacade;
    private Converter converterProcessoCalculoIPTU;

    public Construcao getConstrucao() {
        return construcao;
    }

    public void setConstrucao(Construcao construcao) {
        this.construcao = construcao;
    }

    public ProcessoCalculoIPTU getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalculoIPTU processoCalculo) {
        this.processoCalculo = processoCalculo;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Converter getConverterConstrucao() {
        if (converterConstrucao == null) {
            converterConstrucao = new ConverterAutoComplete(Construcao.class, construcaoFacade);
        }
        return converterConstrucao;
    }

    public Converter getConverterProcessoCalculoIPTU() {
        if (converterProcessoCalculoIPTU == null) {
            converterProcessoCalculoIPTU = new ConverterAutoComplete(ProcessoCalculoIPTU.class, processoCalculoIPTUFacade);
        }
        return converterProcessoCalculoIPTU;
    }

    public Converter getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public Converter getConverterContribuinte() {
        if (converterContribuinte == null) {
            converterContribuinte = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterContribuinte;
    }

    public List<Pessoa> completaContribuinte(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public List<CadastroImobiliario> completaCadstroImobiliario(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<Construcao> completaConstrucao(String parte) {
        return construcaoFacade.listarPorCadastroImobiliario(cadastroImobiliario, parte.trim());
    }

    public List<ProcessoCalculoIPTU> completaProcessoDeCalculo(String parte) {
        return processoCalculoIPTUFacade.listaProcessosPorDescricao(parte.trim());
    }

    public String montaCondicao() {
        String condicao = "";

        if (contribuinte != null) {
            condicao += " and prop.pessoa_id = " + contribuinte.getId();
        }

        if (cadastroImobiliario != null) {
            condicao += " and ci.id = " + cadastroImobiliario.getId();
        }

        if (construcao != null) {
            condicao += " and const.id = " + construcao.getId();
        }

        if (processoCalculo != null) {
            condicao += " and processo.id = " + processoCalculo.getId();
        }

        return condicao;
    }

    public boolean validaDados() {
        boolean retorno = true;
        if (processoCalculo == null || processoCalculo.getId() == null) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage("Formulario:processo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar", " Selecione o Processo de Cálculo"));
        }
        return retorno;
    }

    public void gerarRelatorioImobiliario() throws JRException, IOException {
        if (!validaDados()) {
            return;
        }
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";

        String arquivoJasper = "RelatorioDeCalculos.jasper";

        HashMap parameters = new HashMap();

        parameters.put("CONDICAO", montaCondicao());
        parameters.put("SUB", subReport);
        parameters.put("BRASAO", getCaminhoImagem());
        parameters.put("USUARIO", usuarioLogado().getPessoaFisica().getNome());

        gerarRelatorio(arquivoJasper, parameters);
    }

    @URLAction(mappingId = "novorelatorioDeCalculoIptuIndividual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        Util.limparCampos(this);
    }
}
