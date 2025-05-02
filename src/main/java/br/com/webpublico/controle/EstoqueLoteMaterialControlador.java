/*
 * Codigo gerado automaticamente em Wed Jul 20 14:29:10 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Estoque;
import br.com.webpublico.entidades.EstoqueLoteMaterial;
import br.com.webpublico.entidades.LoteMaterial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EstoqueFacade;
import br.com.webpublico.negocios.EstoqueLoteMaterialFacade;
import br.com.webpublico.negocios.LoteMaterialFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class EstoqueLoteMaterialControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private EstoqueLoteMaterialFacade estoqueLoteMaterialFacade;
    @EJB
    private LoteMaterialFacade loteMaterialFacade;
    private ConverterGenerico converterLoteMaterial;
    @EJB
    private EstoqueFacade estoqueFacade;
    private ConverterGenerico converterEstoque;

    public EstoqueLoteMaterialControlador() {
        metadata = new EntidadeMetaData(EstoqueLoteMaterial.class);
    }

    public EstoqueLoteMaterialFacade getFacade() {
        return estoqueLoteMaterialFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return estoqueLoteMaterialFacade;
    }

    public List<SelectItem> getLoteMaterial() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (LoteMaterial object : loteMaterialFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getIdentificacao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterLoteMaterial() {
        if (converterLoteMaterial == null) {
            converterLoteMaterial = new ConverterGenerico(LoteMaterial.class, loteMaterialFacade);
        }
        return converterLoteMaterial;
    }

    public List<SelectItem> getEstoque() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Estoque object : estoqueFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDataEstoque().toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterEstoque() {
        if (converterEstoque == null) {
            converterEstoque = new ConverterGenerico(Estoque.class, estoqueFacade);
        }
        return converterEstoque;
    }
}
