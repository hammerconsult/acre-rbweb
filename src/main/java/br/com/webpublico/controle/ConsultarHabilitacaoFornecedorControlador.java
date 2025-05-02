/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ItemProcessoDeCompra;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.enums.SituacaoItemCertame;
import br.com.webpublico.enums.StatusLancePregao;
import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consultar-habilitacao-fornecedor-licitacao", pattern = "/habilitacao-fornecedor/licitacao/#{consultarHabilitacaoFornecedorControlador.id}/fornecedor/#{consultarHabilitacaoFornecedorControlador.fornecedorId}/", viewId = "/faces/administrativo/licitacao/habilitacaofornecedor/consultar-habilitacao.xhtml")
})
public class ConsultarHabilitacaoFornecedorControlador extends PrettyControlador<Licitacao> implements Serializable, CRUD {


    private Long fornecedorId;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    private Pessoa fornecedor;
    private List<ItemProcessoDeCompra> itensDoFornecedor;

    public ConsultarHabilitacaoFornecedorControlador() {
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    @Override
    public String getCaminhoPadrao() {
        return "";
    }

    @Override
    public Object getUrlKeyValue() {
        return null;
    }

    @Override
    public AbstractFacade getFacede() {
        return licitacaoFacade;
    }

    public Long getFornecedorId() {
        return fornecedorId;
    }

    public void setFornecedorId(Long fornecedorId) {
        this.fornecedorId = fornecedorId;
    }

    @URLAction(mappingId = "consultar-habilitacao-fornecedor-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        selecionado = licitacaoFacade.recuperar(getId());
        fornecedor = pessoaFacade.recuperar(fornecedorId);
        itensDoFornecedor = processoDeCompraFacade.getItensDoFornecedorNaLicitacao(selecionado, fornecedor);
    }

    public List<ItemProcessoDeCompra> getItensDoFornecedor() {
        return itensDoFornecedor;
    }

    public void setItensDoFornecedor(List<ItemProcessoDeCompra> itensDoFornecedor) {
        this.itensDoFornecedor = itensDoFornecedor;
    }

    public boolean isPregao() {
        try {
            return selecionado.isPregao() || selecionado.isRDC();
        } catch (Exception e) {
            return false;
        }
    }

    public String getStatusDoItemProcessoDeCompraNoCertame(ItemProcessoDeCompra ipc) {
        ipc.setSituacaoItemCertame(processoDeCompraFacade.getStatusDoItemProcessoDeCompraNoCertame(selecionado, fornecedor, ipc));
        try {
            return ipc.getSituacaoItemCertame().getDescricao();
        } catch (NullPointerException npe) {
            return "Não Venceu";
        }
    }

    public String getStatusDoItemProcessoDeCompraNoPregao(ItemProcessoDeCompra ipc) {
        ipc.setStatusLancePregao(processoDeCompraFacade.getStatusDoItemProcessoDeCompraNoPregao(selecionado, fornecedor, ipc));
        if (ipc.getStatusLancePregao().equals(StatusLancePregao.ATIVO)) {
            return "Não Venceu";
        }
        return ipc.getStatusLancePregao().getDescricao();
    }

    public String getSituacaoDoFornecedorDoItemNoCertame(ItemProcessoDeCompra ipc) {
        if (ipc.getSituacaoItemCertame() == null) {
            return TipoClassificacaoFornecedor.INABILITADO.getDescricao();
        }

        if (!ipc.getSituacaoItemCertame().equals(SituacaoItemCertame.VENCEDOR)) {
            return TipoClassificacaoFornecedor.INABILITADO.getDescricao();
        } else {
            try {
                return processoDeCompraFacade.getSituacaoDoFornecedorDoItemNoCertame(selecionado, fornecedor, ipc).getDescricao();
            } catch (NullPointerException npe) {
            }
        }

        return TipoClassificacaoFornecedor.INABILITADO.getDescricao();
    }

    public String getSituacaoDoFornecedorDoItemNoPregao(ItemProcessoDeCompra ipc) {
        if (!ipc.getStatusLancePregao().equals(StatusLancePregao.VENCEDOR)) {
            return TipoClassificacaoFornecedor.INABILITADO.getDescricao();
        } else {
            try {
                return processoDeCompraFacade.getSituacaoDoFornecedorDoItemNoPregao(selecionado, fornecedor, ipc).getDescricao();
            } catch (NullPointerException npe) {
            }
        }

        return TipoClassificacaoFornecedor.INABILITADO.getDescricao();
    }
}
