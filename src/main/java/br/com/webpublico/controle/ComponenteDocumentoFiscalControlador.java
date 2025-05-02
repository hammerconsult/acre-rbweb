package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.ConfigContaDespesaTipoDocumentoFacade;
import br.com.webpublico.negocios.DoctoFiscalLiquidacaoFacade;
import br.com.webpublico.negocios.EmpenhoFacade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean
public class ComponenteDocumentoFiscalControlador implements Serializable {

    @EJB
    private DoctoFiscalLiquidacaoFacade facade;
    @EJB
    private ConfigContaDespesaTipoDocumentoFacade configContaDespesaTipoDocumentoFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    private DoctoFiscalLiquidacao doctoFiscalLiquidacao;
    private RequisicaoDeCompra requisicaoCompra;

    public void novo(DoctoFiscalLiquidacao documento, RequisicaoDeCompra requisicaoCompra) {
        if (documento != null) {
            this.doctoFiscalLiquidacao = documento;
        }
        if (requisicaoCompra != null) {
            this.requisicaoCompra = requisicaoCompra;
        }
    }

    public DoctoFiscalLiquidacaoFacade getFacade() {
        return facade;
    }

    public DoctoFiscalLiquidacao getDoctoFiscalLiquidacao() {
        return doctoFiscalLiquidacao;
    }

    public void setDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        this.doctoFiscalLiquidacao = doctoFiscalLiquidacao;
    }

    public List<TipoDocumentoFiscal> completarTiposDocumentos(String parte) {
        if (requisicaoCompra != null) {
            List<Empenho> empenhos = empenhoFacade.buscarEmpenhosPorRequisicaoCompra(requisicaoCompra.getId());
            List<Long> idsContaDespesa = Lists.newArrayList();
            List<String> tiposContasDespesa = Lists.newArrayList();
            if (!Util.isListNullOrEmpty(empenhos)) {
                for (Empenho empenho : empenhos) {
                    if (!idsContaDespesa.contains(empenho.getContaDespesa().getId())) {
                        idsContaDespesa.add(empenho.getContaDespesa().getId());
                    }
                    if (!tiposContasDespesa.contains(empenho.getTipoContaDespesa().name())) {
                        tiposContasDespesa.add(empenho.getTipoContaDespesa().name());
                    }
                }
            }
            List<TipoDocumentoFiscal> documentos = configContaDespesaTipoDocumentoFacade.buscarTiposDeDocumentosPorContaDeDespesa(idsContaDespesa, tiposContasDespesa, parte);
            if (!documentos.isEmpty()) {
                return documentos;
            }
        }
        return facade.getTipoDocumentoFiscalFacade().buscarTiposDeDocumentosAtivos(parte.trim());
    }

    public List<SelectItem> getUfs() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (UF obj : facade.getUfFacade().lista()) {
            toReturn.add(new SelectItem(obj, obj.getNome()));
        }
        return toReturn;
    }

    public boolean isTipoDocumentoFiscalObrigaChaveAcesso() {
        return doctoFiscalLiquidacao != null
            && doctoFiscalLiquidacao.getTipoDocumentoFiscal() != null
            && doctoFiscalLiquidacao.getTipoDocumentoFiscal().getObrigarChaveDeAcesso();
    }
}
