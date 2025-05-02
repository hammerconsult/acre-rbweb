/*
 * Codigo gerado automaticamente em Fri Jul 13 10:46:12 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoOperacaoAtuarial;
import br.com.webpublico.enums.TipoPlano;
import br.com.webpublico.enums.TipoProvisao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TipoPassivoAtuarialFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "tipoPassivoAtuarialControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-tipo-passivo-atuarial", pattern = "/tipo-passivo-atuarial/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/tipopassivoatuarial/edita.xhtml"),
        @URLMapping(id = "editar-tipo-passivo-atuarial", pattern = "/tipo-passivo-atuarial/editar/#{tipoPassivoAtuarialControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/tipopassivoatuarial/edita.xhtml"),
        @URLMapping(id = "ver-tipo-passivo-atuarial", pattern = "/tipo-passivo-atuarial/ver/#{tipoPassivoAtuarialControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/tipopassivoatuarial/visualizar.xhtml"),
        @URLMapping(id = "listar-tipo-passivo-atuarial", pattern = "/tipo-passivo-atuarial/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/tipopassivoatuarial/lista.xhtml")
})
public class TipoPassivoAtuarialControlador extends PrettyControlador<TipoPassivoAtuarial> implements Serializable, CRUD {

    @EJB
    private TipoPassivoAtuarialFacade tipoPassivoAtuarialFacade;
    private TipoOperacaoAtuarial tipoOperacaoAtuarial;
    private TipoPlano tipoPlano;
    private TipoProvisao tipoProvisao;

    public TipoPassivoAtuarialControlador() {
        super(TipoPassivoAtuarial.class);
    }

    public TipoPassivoAtuarialFacade getFacade() {
        return tipoPassivoAtuarialFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoPassivoAtuarialFacade;
    }

    public TipoOperacaoAtuarial getTipoOperacaoAtuarial() {
        return tipoOperacaoAtuarial;
    }

    public void setTipoOperacaoAtuarial(TipoOperacaoAtuarial tipoOperacaoAtuarial) {
        this.tipoOperacaoAtuarial = tipoOperacaoAtuarial;
    }

    public TipoPlano getTipoPlano() {
        return tipoPlano;
    }

    public void setTipoPlano(TipoPlano tipoPlano) {
        this.tipoPlano = tipoPlano;
    }

    public TipoProvisao getTipoProvisao() {
        return tipoProvisao;
    }

    public void setTipoProvisao(TipoProvisao tipoProvisao) {
        this.tipoProvisao = tipoProvisao;
    }

    public List<TipoOperacaoAtuarial> getTiposOperacoesAtuariais() {
        List<TipoOperacaoAtuarial> lista = new ArrayList<TipoOperacaoAtuarial>();
        lista.addAll(Arrays.asList(TipoOperacaoAtuarial.values()));
        return lista;
    }

    public List<TipoPlano> getTiposPlanos() {
        List<TipoPlano> lista = new ArrayList<TipoPlano>();
        lista.addAll(Arrays.asList(TipoPlano.values()));
        return lista;
    }

    public List<TipoProvisao> getTiposProvisoes() {
        List<TipoProvisao> lista = new ArrayList<TipoProvisao>();
        lista.addAll(Arrays.asList(TipoProvisao.values()));
        return lista;
    }

    public void selecionar(ActionEvent evento) {
        TipoPassivoAtuarial tpa = (TipoPassivoAtuarial) evento.getComponent().getAttributes().get("objeto");
        selecionado = tipoPassivoAtuarialFacade.recuperar(tpa.getId());
    }

    public void addTipoOperacaoAtuarial() {
        if (tipoOperacaoJaExistente()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " O tipo de operação atuarial: " +selecionado.getTipoOperacaoAtuarial()+ " já foi adicionado na lista.");
        } else {
            ((TipoPassivoAtuarial) selecionado).getTipoOperacaoAtuarial().add(this.tipoOperacaoAtuarial);
        }
    }

    public void salvar(){
        try {
            selecionado.setCodigo(tipoPassivoAtuarialFacade.getUltimoNumero());
             super.salvar();
        }   catch (ExcecaoNegocioGenerica ex){
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public void removeTipoOperacaoAtuarial(TipoOperacaoAtuarial tipoOperacaoAtuarial) {
        ((TipoPassivoAtuarial) selecionado).getTipoOperacaoAtuarial().remove(tipoOperacaoAtuarial);
    }

    public void addTipoPlano() {
        if (tipoPlanoJaExistente()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " O tipo de plano: " + selecionado.getTipoPlano() + " já foi adicionado na lista.");
        } else {
            ((TipoPassivoAtuarial) selecionado).getTipoPlano().add(tipoPlano);
        }
    }

    public void removeTipoPlano(TipoPlano tipoPlano) {
        ((TipoPassivoAtuarial) selecionado).getTipoPlano().remove(tipoPlano);
    }

    public void addTipoProvisao() {
        if (tipoProvisaoJaExistente()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " O tipo de provisão: " +selecionado.getTipoProvisao()+ " já foi adicionado na lista.");
        } else {
            ((TipoPassivoAtuarial) selecionado).getTipoProvisao().add(tipoProvisao);
        }
    }

    public void removeTipoProvisao(TipoProvisao tipoProvisao) {
        ((TipoPassivoAtuarial) selecionado).getTipoProvisao().remove(tipoProvisao);
    }

    private boolean tipoOperacaoJaExistente() {
        for (TipoOperacaoAtuarial tipo : ((TipoPassivoAtuarial) selecionado).getTipoOperacaoAtuarial()) {
            if (tipoOperacaoAtuarial.equals(tipo)) {
                return true;
            }
        }
        return false;
    }

    private boolean tipoPlanoJaExistente() {
        for (TipoPlano tipo : ((TipoPassivoAtuarial) selecionado).getTipoPlano()) {
            if (tipoPlano.equals(tipo)) {
                return true;
            }
        }
        return false;
    }

    private boolean tipoProvisaoJaExistente() {
        for (TipoProvisao tipo : ((TipoPassivoAtuarial) selecionado).getTipoProvisao()) {
            if (tipoProvisao.equals(tipo)) {
                return true;
            }
        }
        return false;
    }


    @URLAction(mappingId = "novo-tipo-passivo-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-tipo-passivo-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-tipo-passivo-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-passivo-atuarial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

}
