package br.com.webpublico.controle;

import br.com.webpublico.entidades.DoctoFiscalEntradaCompra;
import br.com.webpublico.entidadesauxiliares.EmpenhoDocumentoFiscal;
import br.com.webpublico.entidadesauxiliares.EmpenhoDocumentoFiscalItem;
import br.com.webpublico.entidadesauxiliares.FiltroEmpenhoDocumentoFiscal;
import br.com.webpublico.negocios.RequisicaoDeCompraFacade;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class ComponenteEmpenhoDocumentoFiscalControlador implements Serializable {

    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    private List<EmpenhoDocumentoFiscal> empenhosDocumentoFiscal;
    private EmpenhoDocumentoFiscal empenhoSelecionado;

    public void buscarEmpenhosDocumentoFiscal(FiltroEmpenhoDocumentoFiscal filtro) {
        if (filtro !=null) {
            empenhosDocumentoFiscal = requisicaoDeCompraFacade.buscarEmpenhosDocumentoFiscal(filtro);
        }
    }

    public void selecionarEmpenho(EmpenhoDocumentoFiscal emp) {
        this.empenhoSelecionado = emp;
        FacesUtil.executaJavaScript("atualizaComponente(); dialogVisualizarItensEmpenho.show()");
    }

    public List<EmpenhoDocumentoFiscalItem> getItens() {
        return empenhoSelecionado != null ? empenhoSelecionado.getItens() : new ArrayList<>();
    }

    public BigDecimal getTotalizadorItensEmpenho(EmpenhoDocumentoFiscal empDoc) {
        BigDecimal total = BigDecimal.ZERO;
        if (empDoc != null) {
            for (EmpenhoDocumentoFiscalItem item : empDoc.getItens()) {
                total = total.add(item.getValorTotal());
            }
        }
        return total;
    }

    public List<EmpenhoDocumentoFiscal> getEmpenhosDocumentoFiscal() {
        return empenhosDocumentoFiscal;
    }

    public void setEmpenhosDocumentoFiscal(List<EmpenhoDocumentoFiscal> empenhosDocumentoFiscal) {
        this.empenhosDocumentoFiscal = empenhosDocumentoFiscal;
    }

    public EmpenhoDocumentoFiscal getEmpenhoSelecionado() {
        return empenhoSelecionado;
    }

    public void setEmpenhoSelecionado(EmpenhoDocumentoFiscal empenhoSelecionado) {
        this.empenhoSelecionado = empenhoSelecionado;
    }
}
