/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCalculoDividaDiversa;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CalculoDividaDiversaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "cancelamentoDividaDiversaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "listarCancelamentoDividaDiversa", pattern = "/cancelamento-de-divida-diversa/listar/", viewId = "/faces/tributario/taxasdividasdiversas/cancelamentodividasdiversas/lista.xhtml"),
        @URLMapping(id = "verCancelamentoDividaDiversa", pattern = "/cancelamento-de-divida-diversa/ver/#{cancelamentoDividaDiversaControlador.id}/", viewId = "/faces/tributario/taxasdividasdiversas/cancelamentodividasdiversas/visualizar.xhtml")
})
public class CancelamentoDividaDiversaControlador extends PrettyControlador<CalculoDividaDiversa> implements Serializable, CRUD {

    @EJB
    private CalculoDividaDiversaFacade calculoDividaDiversasFacade;
    private CancelamentoDividaDiversa cancelamentoDividaDiversa;
    private boolean selecionouSim;
    List<ValorDivida> listaValorDivida;
    private List<CalculoDividaDiversa> lista;
    private PesquisaDividas pesquisaDivida;

    public CancelamentoDividaDiversaControlador() {
        super(CalculoDividaDiversa.class);
    }

    public CancelamentoDividaDiversa getCancelamentoDividaDiversa() {
        return cancelamentoDividaDiversa;
    }

    public void setCancelamentoDividaDiversa(CancelamentoDividaDiversa cancelamentoDividaDiversa) {
        this.cancelamentoDividaDiversa = cancelamentoDividaDiversa;
    }

    public List<CalculoDividaDiversa> getLista() {
        return lista;
    }

    public void setLista(List<CalculoDividaDiversa> lista) {
        this.lista = lista;
    }

    public PesquisaDividas getPesquisaDivida() {
        return pesquisaDivida;
    }

    public void setPesquisaDivida(PesquisaDividas pesquisaDivida) {
        this.pesquisaDivida = pesquisaDivida;
    }

    @Override
    public AbstractFacade getFacede() {
        return calculoDividaDiversasFacade;
    }

    public List<ValorDivida> getListaValorDivida() {
        return listaValorDivida;
    }

    public boolean isSelecionouSim() {
        return selecionouSim;
    }

    public void setSelecionouSim(boolean selecionouSim) {
        this.selecionouSim = selecionouSim;
    }

    @URLAction(mappingId = "listarCancelamentoDividaDiversa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listaCancelamento() {
        super.novo();
        limpaConsulta();
    }

    @URLAction(mappingId = "verCancelamentoDividaDiversa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionouSim = false;
        listaValorDivida = calculoDividaDiversasFacade.getValorDividaPorCalculo(selecionado);
    }

    private boolean validaCamposPreenchidos() {
        if (cancelamentoDividaDiversa.getMotivo() == null || cancelamentoDividaDiversa.getMotivo().trim().length() <= 0) {
            FacesUtil.addError("Erro ao tentar cancelar a Dívida Diversa!", "O campo motivo é obrigatório.");
            return false;
        }
        return true;
    }

    public void cancelarDividaDiversa() {
        if (validaCamposPreenchidos()) {
            if (cancelamentoDividaDiversa.getMotivo().trim().length() > 1000) {
                cancelamentoDividaDiversa.setMotivo(cancelamentoDividaDiversa.getMotivo().trim().substring(0, 999));
            }
            selecionado.setSituacao(SituacaoCalculoDividaDiversa.CANCELADO);
            selecionado.setCancelamentoDividaDiversa(cancelamentoDividaDiversa);
            cancelaParcelaDaListaDeValorDivida();
            calculoDividaDiversasFacade.salva(selecionado);
            FacesUtil.addInfo("Sucesso! ", "Dívida Diversa cancelada.");
            redireciona();
        }
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public List<CalculoDividaDiversa> getListaCalculoDividasDiversas() {
        return calculoDividaDiversasFacade.listaCalculosDisponiveisParaCancelamento();
    }

    public String montaDescricaoCadastro() {
        return calculoDividaDiversasFacade.montaDescricaoCadastro(selecionado);
    }

    public void preparaCancelamento() {
        this.cancelamentoDividaDiversa = new CancelamentoDividaDiversa();
        this.cancelamentoDividaDiversa.setDataCancelamento(new Date());
        this.cancelamentoDividaDiversa.setUsuarioCancelamento(getSistemaControlador().getUsuarioCorrente());
        selecionouSim = true;
    }

    private boolean validaCamposCancelamento() {
        boolean retorno = true;
        if (cancelamentoDividaDiversa.getMotivo() == null || cancelamentoDividaDiversa.getMotivo().trim().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o motivo do cancelamento.");
        } else if (cancelamentoDividaDiversa.getMotivo().length() > 3000) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Descrição do motivo muito longa.");
        }
        return retorno;
    }

    private void cancelaParcelaDaListaDeValorDivida() {
        for (ValorDivida vd : listaValorDivida) {
            calculoDividaDiversasFacade.cancelaParcelasValorDivida(vd);
        }
    }

    public void verificarPermissaoCancelamento() {
        boolean permitido = false;
        if(!listaValorDivida.isEmpty()) {
            for (ValorDivida vd : listaValorDivida) {
                permitido = calculoDividaDiversasFacade.permitirCancelamento(vd);
            }
        }else{
            permitido = true;
        }
        if (permitido) {
            RequestContext.getCurrentInstance().execute("widgetDialogCancelamento.show()");
        } else {
            FacesUtil.addError("Não foi possível continuar!", "O sistema identificou que a Dívida Diversa possui parcela(s) com situação igual a PAGA ou INSCRITA EM DÍVIDA ATIVA.");
        }
    }

    public BigDecimal getTotalUFM() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoDivDiversa item : selecionado.getItens()) {
            if (item.getValorUFM() != null) {
                total = total.add(item.getValorUFM());
            }
        }
        return total;
    }

    public BigDecimal getTotalReais() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoDivDiversa item : selecionado.getItens()) {
            if (item.getValorReal() != null) {
                total = total.add(item.getValorReal());
            }
        }
        return total;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cancelamento-de-divida-diversa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> tiposCadastrosTributarios() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, "TODOS"));
        for (TipoCadastroTributario tpcadtrib : TipoCadastroTributario.values()) {
            lista.add(new SelectItem(tpcadtrib, tpcadtrib.getDescricao()));
        }
        return lista;
    }

    public void filtrarEmitido() {
        //pesquisaDivida.setSituacaoCalculoDividaDiversa(SituacaoCalculoDividaDiversa.EFETIVADO);
        lista = calculoDividaDiversasFacade.buscarListaDeCalculoDividaDiversaParaCancelamentoPorPesquisaDivida(pesquisaDivida);
    }

    public void limpaConsulta() {
        novaPesquisa();
        lista = new ArrayList<>();
        listaValorDivida = new ArrayList<>();
    }

    public void novaPesquisa() {
        pesquisaDivida = new PesquisaDividas();
    }

}
