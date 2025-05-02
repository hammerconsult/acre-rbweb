package br.com.webpublico.controle;


import br.com.webpublico.entidadesauxiliares.CadastroEconomicoDuplicadoDTO;
import br.com.webpublico.entidadesauxiliares.FiltroUnificacaoCadastroEconomicoLote;
import br.com.webpublico.negocios.UnificacaoCadastroEconomicoLoteFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "unificacaoCadastroEconomicoLote",
        pattern = "/tributario/unificacao-cadastro-economico-lote/",
        viewId = "/faces/tributario/cadastromunicipal/unificacaocadastroeconomicolote/edita.xhtml")
})
public class UnificacaoCadastroEconomicoLoteControlador implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(UnificacaoCadastroEconomicoLoteControlador.class);

    @EJB
    private UnificacaoCadastroEconomicoLoteFacade facade;

    private FiltroUnificacaoCadastroEconomicoLote filtro;
    private Future<List<CadastroEconomicoDuplicadoDTO>> futurePesquisa;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<CadastroEconomicoDuplicadoDTO> cadastrosDuplicados;
    private Future futureUnificacao;

    public FiltroUnificacaoCadastroEconomicoLote getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroUnificacaoCadastroEconomicoLote filtro) {
        this.filtro = filtro;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<CadastroEconomicoDuplicadoDTO> getCadastrosDuplicados() {
        return cadastrosDuplicados;
    }

    public void setCadastrosDuplicados(List<CadastroEconomicoDuplicadoDTO> cadastrosDuplicados) {
        this.cadastrosDuplicados = cadastrosDuplicados;
    }

    @URLAction(mappingId = "unificacaoCadastroEconomicoLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void init() {
        filtro = new FiltroUnificacaoCadastroEconomicoLote();
        cadastrosDuplicados = null;
    }

    public void desmarcarOutrosCadastros(CadastroEconomicoDuplicadoDTO cadastro) {
        for (CadastroEconomicoDuplicadoDTO item : cadastrosDuplicados) {
            if (!item.getId().equals(cadastro.getId()) &&
                item.getCpfCnpj().equals(cadastro.getCpfCnpj())) {
                item.setCorreto(Boolean.FALSE);
            }
        }
    }

    public void iniciarPesquisa() {
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        futurePesquisa = facade.buscarCadastrosDuplicados(assistenteBarraProgresso, filtro);
        FacesUtil.executaJavaScript("acompanharPesquisa()");
    }

    public void acompanharPesquisa() {
        if (futurePesquisa != null) {
            if (futurePesquisa.isCancelled()) {
                FacesUtil.executaJavaScript("cancelarPesquisa()");
            } else if (futurePesquisa.isDone()) {
                FacesUtil.executaJavaScript("finalizarPesquisa()");
            }
        }
    }

    public void finalizarPesquisa() {
        try {
            cadastrosDuplicados = futurePesquisa.get();
            if (cadastrosDuplicados == null || cadastrosDuplicados.isEmpty()) {
                FacesUtil.addAtencao("Nenhum cadastro econômico duplicado encontrado para os filtros selecionados.");
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro inesperado ao consultar cadastros econômicos duplicados. Erro " + e.getMessage());
        } finally {
            FacesUtil.executaJavaScript("closeDialog(dlgAcompanhamento)");
        }
    }

    public void iniciarUnificacao() {
        if (cadastrosDuplicados == null || cadastrosDuplicados.isEmpty()) {
            FacesUtil.addAtencao("Nenhum cadastro econômico duplicado encontrado para os filtros selecionados.");
            return;
        }
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        futureUnificacao = facade.unificarCadastrosEconomicosDuplicados(assistenteBarraProgresso, cadastrosDuplicados);
        FacesUtil.executaJavaScript("acompanharUnificacao()");
    }

    public void acompanharUnificacao() {
        if (futureUnificacao != null) {
            if (futureUnificacao.isCancelled()) {
                FacesUtil.executaJavaScript("cancelarUnificacao()");
            } else if (futureUnificacao.isDone()) {
                FacesUtil.executaJavaScript("finalizarUnificacao()");
            }
        }
    }

    public void finalizarUnificacao() {
        init();
    }
}
