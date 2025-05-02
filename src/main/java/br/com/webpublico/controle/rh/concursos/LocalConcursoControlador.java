package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.concursos.LocalConcurso;
import br.com.webpublico.enums.TipoLocalConcurso;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.concursos.LocalConcursoFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by venom on 01/12/14.
 */
@ManagedBean(name = "localConcursoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoLocalConcurso", pattern = "/local-concurso/novo/", viewId = "/faces/rh/concursos/local/edita.xhtml")
})
public class LocalConcursoControlador extends PrettyControlador<LocalConcurso> implements Serializable, CRUD {

    @EJB
    private LocalConcursoFacade localConcursoFacade;
    private LocalConcurso localInscricao;
    private LocalConcurso localPagamento;
    private LocalConcurso localProva;
    private ConverterGenerico converterLocalAgrupador;

    public LocalConcursoControlador() {
        super(LocalConcurso.class);
    }

    @URLAction(mappingId = "novoLocalConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
//        novoInscricao();
        novoPagamento();
        novoProva();
    }

    public void adicionarLocalInscricao() {
        if (Util.validaCampos(this.localInscricao)) {
            localConcursoFacade.salvarNovo(this.localInscricao);
            novoInscricao();
            cancelarLocalInscricao();

        } else {
            System.out.println("Não validou!");
        }
    }

    public void carregarFilhosDoSuperior() {
        try {
//            selecionado.setSuperior(localConcursoFacade.recuperar(selecionado.getSuperior().getId()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cancelarLocalInscricao() {
        this.localInscricao = null;
    }

    public void novoInscricao() {
        this.localInscricao = new LocalConcurso();
        this.localInscricao.setTipoLocalConcurso(TipoLocalConcurso.INSCRICAO);
    }

    public List<LocalConcurso> getLocaisInscricao() {
        return localConcursoFacade.listaPorTipo(TipoLocalConcurso.INSCRICAO);
    }

    public List<SelectItem> getAgrupadoresInscricao() {
        List<SelectItem> itens = new ArrayList<>();
        itens.add(new SelectItem(null, ""));
        for (LocalConcurso lc : getLocaisInscricao()) {
            itens.add(new SelectItem(lc, lc.toString()));
        }
        return itens;
    }

    public void adicionarLocalPagamento() {

    }

    public void cancelarLocalPagamento() {

    }

    private void novoPagamento() {
        this.localPagamento = new LocalConcurso();
        this.localPagamento.setTipoLocalConcurso(TipoLocalConcurso.PAGAMENTO);
    }

    public List<LocalConcurso> getLocaisPagamento() {
        return localConcursoFacade.listaPorTipo(TipoLocalConcurso.PAGAMENTO);
    }

    public List<SelectItem> getAgrupadoresPagamento() {
        List<SelectItem> itens = new ArrayList<>();
        itens.add(new SelectItem(null, ""));
        for (LocalConcurso lc : getLocaisPagamento()) {
            itens.add(new SelectItem(lc, lc.toString()));
        }
        return itens;
    }

    public void adicionarLocalProva() {

    }

    public void cancelarLocalProva() {

    }

    private void novoProva() {
        this.localProva = new LocalConcurso();
    }

    public List<LocalConcurso> getLocaisProva() {
        return null;
    }

    public List<SelectItem> getAgrupadoresProva() {
        List<SelectItem> itens = new ArrayList<>();
        itens.add(new SelectItem(null, ""));
        for (LocalConcurso lc : getLocaisProva()) {
            itens.add(new SelectItem(lc, lc.toString()));
        }
        return itens;
    }

    public ConverterGenerico getConverterLocalAgrupador() {
        if (converterLocalAgrupador == null) {
            converterLocalAgrupador = new ConverterGenerico(LocalConcurso.class, localConcursoFacade);
        }
        return converterLocalAgrupador;
    }

    @Override
    public AbstractFacade getFacede() {
        return localConcursoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/localconcurso/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public LocalConcurso getLocalInscricao() {
        return localInscricao;
    }

    public void setLocalInscricao(LocalConcurso localInscricao) {
        this.localInscricao = localInscricao;
    }

    public LocalConcurso getLocalPagamento() {
        return localPagamento;
    }

    public void setLocalPagamento(LocalConcurso localPagamento) {
        this.localPagamento = localPagamento;
    }

    public LocalConcurso getLocalProva() {
        return localProva;
    }

    public void setLocalProva(LocalConcurso localProva) {
        this.localProva = localProva;
    }
}
