package br.com.webpublico.controle;


import br.com.webpublico.entidadesauxiliares.comum.FiltroUnificacaoPessoaLote;
import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.negocios.UnificacaoPessoaLoteFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.viewobjects.PessoaDuplicadaDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "unificacaoPessoaLote",
        pattern = "/unificacao-pessoa-lote/",
        viewId = "/faces/comum/unificacao-pessoa-lote/edita.xhtml")
})
public class UnificacaoPessoaLoteControlador implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(UnificacaoPessoaLoteControlador.class);

    @EJB
    private UnificacaoPessoaLoteFacade facade;

    private FiltroUnificacaoPessoaLote filtro;
    private Future<List<PessoaDuplicadaDTO>> futurePesquisa;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<PessoaDuplicadaDTO> pessoasDuplicadas;
    private Future futureUnificacao;

    public FiltroUnificacaoPessoaLote getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroUnificacaoPessoaLote filtro) {
        this.filtro = filtro;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<PessoaDuplicadaDTO> getPessoasDuplicadas() {
        return pessoasDuplicadas;
    }

    public void setPessoasDuplicadas(List<PessoaDuplicadaDTO> pessoasDuplicadas) {
        this.pessoasDuplicadas = pessoasDuplicadas;
    }

    public List<SelectItem> getTiposPessoa() {
        return Util.getListSelectItem(TipoPessoa.values());
    }

    @URLAction(mappingId = "unificacaoPessoaLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void init() {
        filtro = new FiltroUnificacaoPessoaLote();
        pessoasDuplicadas = null;
    }

    public void desmarcarOutrasPessoas(PessoaDuplicadaDTO pessoa) {
        for (PessoaDuplicadaDTO item : pessoasDuplicadas) {
            if (!item.getId().equals(pessoa.getId()) &&
                item.getCpfCnpj().equals(pessoa.getCpfCnpj())) {
                item.setCorreta(Boolean.FALSE);
            }
        }
    }

    public void iniciarPesquisa() {
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        futurePesquisa = facade.buscarPessoasDuplicadas(assistenteBarraProgresso, filtro);
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
            pessoasDuplicadas = futurePesquisa.get();
            if (pessoasDuplicadas == null || pessoasDuplicadas.isEmpty()) {
                FacesUtil.addAtencao("Nenhuma pessoa duplicada encontrada para os filtros selecionados.");
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro inesperado ao consultar pessoas duplicadas. Erro " + e.getMessage());
        } finally {
            FacesUtil.executaJavaScript("closeDialog(dlgAcompanhamento)");
        }
    }

    public void iniciarUnificacao() {
        if (pessoasDuplicadas == null || pessoasDuplicadas.isEmpty()) {
            FacesUtil.addAtencao("Nenhuma pessoa duplicada encontrada para os filtros selecionados.");
            return;
        }
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        futureUnificacao = facade.unificarPessoasDuplicadas(assistenteBarraProgresso, pessoasDuplicadas);
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
        FacesUtil.addOperacaoRealizada("Unificação de pessoas concluída.");
        FacesUtil.executaJavaScript("closeDialog(dlgAcompanhamento)");
    }
}
