package br.com.webpublico.controle;

import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.entidadesauxiliares.GrupoContaDespesa;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.enums.TipoSolicitacao;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ObjetoCompraFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
public class AutoCompleteObjetoCompraControlador implements Serializable {

    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    private TipoSolicitacao tipoSolicitacao;
    public TipoObjetoCompra[] tiposObjetoCompraSelecionado;

    public void novo(TipoSolicitacao tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
    }

    public List<ObjetoCompra> completarObjetoCompra(String parte) {
        if (tiposObjetoCompraSelecionado != null && tiposObjetoCompraSelecionado.length > 0) {
            return objetoCompraFacade.buscarObjetoCompraPorDescricaoOrcodigoAndListTipoObjetoCompra(parte.trim(), Arrays.asList(tiposObjetoCompraSelecionado));
        }
        return objetoCompraFacade.completaObjetoCompra(parte.trim());
    }

    public List<TipoObjetoCompra> getTiposObjetoCompra() {
        if (tipoSolicitacao != null) {
            if (tipoSolicitacao.isObraServicoEngenharia()) {
                tiposObjetoCompraSelecionado = new TipoObjetoCompra[]{TipoObjetoCompra.PERMANENTE_IMOVEL};
                return Lists.newArrayList(TipoObjetoCompra.PERMANENTE_IMOVEL);
            }
            if (tipoSolicitacao.isCompraServico()) {
                return Lists.newArrayList(TipoObjetoCompra.getTiposObjetoCompraOrdemCompraAndServico());
            }
        }
        return Arrays.asList(TipoObjetoCompra.values());
    }

    public void listenerObjetoCompra(ObjetoCompra objetoCompra) {
        try {
            if (objetoCompra != null) {
                TabelaEspecificacaoObjetoCompraControlador controlador = (TabelaEspecificacaoObjetoCompraControlador) Util.getControladorPeloNome("tabelaEspecificacaoObjetoCompraControlador");
                controlador.recuperarObjetoCompra(objetoCompra);
                GrupoContaDespesa grupoContaDespesa = objetoCompraFacade.criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(), objetoCompra.getGrupoObjetoCompra());
                objetoCompra.setGrupoContaDespesa(grupoContaDespesa);
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void itemSelect() {
    }

    public TipoObjetoCompra[] getTiposObjetoCompraSelecionado() {
        return tiposObjetoCompraSelecionado;
    }

    public void setTiposObjetoCompraSelecionado(TipoObjetoCompra[] tiposObjetoCompraSelecionado) {
        this.tiposObjetoCompraSelecionado = tiposObjetoCompraSelecionado;
    }
}
