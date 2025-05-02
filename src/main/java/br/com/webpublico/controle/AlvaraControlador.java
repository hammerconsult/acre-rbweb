package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.negocios.AlvaraFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean(name = "alvaraControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoSolicitacaoAlvara", pattern = "/solicitacao-de-renovacao-de-alvara/", viewId = "/faces/tributario/alvara/solicitacaoderenovacao/edita.xhtml"),
})
public class AlvaraControlador extends AbstractReport implements Serializable {

    @EJB
    private AlvaraFacade alvaraFacade;
    private List<EnderecoCorreio> enderecos;
    private Alvara selecionado;
    private CadastroEconomico cadastroEconomico;
    private List<TipoVistoria> listaTipoVistoria;
    private ConverterAutoComplete converterCadastroEconomico;
    private String caminho;
    private String numeroProtocolo;
    private String vistoriaIds;
    private boolean habilitaBotaoCalculo = false;

    public AlvaraControlador() {
        selecionado = new Alvara();
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador)Util.getControladorPeloNome("SistemaControlador");
    }

    public Alvara getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Alvara selecionado) {
        this.selecionado = selecionado;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<TipoVistoria> getListaTipoVistoria() {
        return listaTipoVistoria;
    }

    public void setListaTipoVistoria(List<TipoVistoria> listaTipoVistoria) {
        this.listaTipoVistoria = listaTipoVistoria;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String caminho() {
        return this.caminho;
    }

    public boolean isHabilitaBotaoCalculo() {
        return habilitaBotaoCalculo;
    }

    public void setHabilitaBotaoCalculo(boolean habilitaBotaoCalculo) {
        this.habilitaBotaoCalculo = habilitaBotaoCalculo;
    }

    public List<EnderecoCorreio> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoCorreio> enderecos) {
        this.enderecos = enderecos;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getVistoriaIds() {
        return vistoriaIds;
    }

    public void setVistoriaIds(String vistoriaIds) {
        this.vistoriaIds = vistoriaIds;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return alvaraFacade.getCadastroEconomicoFacade().listaCadastroEconomicoPorPessoa(parte.trim());
    }

    public Converter getConverterCadastroEconomico() {
        if (this.converterCadastroEconomico == null) {
            this.converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, alvaraFacade.getCadastroEconomicoFacade());
        }
        return this.converterCadastroEconomico;
    }

    public void atualizaCadEconomico(SelectEvent event) {
        this.enderecos.removeAll(enderecos);
        this.cadastroEconomico = (CadastroEconomico) event.getObject();
        this.selecionado.setCadastroEconomico(alvaraFacade.getCadastroEconomicoFacade().recuperar(cadastroEconomico.getId()));
        this.enderecos.addAll(alvaraFacade.getEnderecoFacade().enderecoPorPessoa(this.selecionado.getCadastroEconomico().getPessoa()));
    }

    public void gerarVistoria() throws JRException, IOException {
        if (selecionado.getCadastroEconomico().getId() == null) {
            FacesUtil.addError("Não foi possível continuar ", "selecione um CMC.");
        } else if (this.alvaraFacade.verificaExistenciaAlvara(selecionado.getCadastroEconomico())) {
            if (numeroProtocolo == null) {
                numeroProtocolo = "";
            }
            if (selecionado.getCadastroEconomico() != null && selecionado.getCadastroEconomico().getId() != null) {
                alvaraFacade.getVistoriaFacade().gerarVistoria(selecionado, selecionado.getTipoAlvara(), numeroProtocolo);
                gerarRelatorio();
                FacesUtil.addInfo("Vistorias geradas com sucesso!", "");
                habilitaBotaoCalculo = false;
            } else {
                FacesUtil.addError("Impossível continuar", "Selecione o Cadastro Econômico");
                habilitaBotaoCalculo = false;
            }
        } else {
            FacesUtil.addError("Para efetuar a renovação, ", "selecione um CMC que já possui um alvará");
        }
    }

    public List<SelectItem> getListaTipoAlvara() {
        List<SelectItem> listaTipoVistoria = new ArrayList<SelectItem>();
        for (TipoAlvara ta : TipoAlvara.values()) {
            listaTipoVistoria.add(new SelectItem(ta, ta.getDescricao()));
        }
        return listaTipoVistoria;
    }

    @URLAction(mappingId = "novoSolicitacaoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        this.cadastroEconomico = new CadastroEconomico();
        this.enderecos = new ArrayList<EnderecoCorreio>();
        numeroProtocolo = "";
        habilitaBotaoCalculo = false;
    }

    public void gerarRelatorio() throws JRException, IOException {
        if (selecionado.getCadastroEconomico() != null || !vistoriaIds.isEmpty()) {
            String arquivoJasper = "RelatorioVistorias.jasper";
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF/report") + "/";
            HashMap parameters = new HashMap();
            parameters.put("VISTORIA_IDS", this.vistoriaIds);
            parameters.put("SUBREPORT_DIR", subReport);
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("CMC", selecionado.getCadastroEconomico().getInscricaoCadastral());
            gerarRelatorio(arquivoJasper, parameters);
        } else {
            FacesUtil.addWarn("Atenção ", "Não foi possível gerar o relatório de vistorias.");
        }
    }
}
