/*
 * Codigo gerado automaticamente em Wed Apr 13 15:02:46 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Indicador;
import br.com.webpublico.entidades.ValorIndicador;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.IndicadorFacade;
import br.com.webpublico.negocios.ValorIndicadorFacade;
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
public class ValorIndicadorControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ValorIndicadorFacade valorIndicadorFacade;
    @EJB
    private IndicadorFacade indicadorFacade;
    protected ConverterGenerico converterIndicador;

    public ValorIndicadorControlador() {
        metadata = new EntidadeMetaData(ValorIndicador.class);
    }

    public ValorIndicadorFacade getFacade() {
        return valorIndicadorFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return valorIndicadorFacade;
    }

    public List<SelectItem> getIndicador() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Indicador object : indicadorFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterIndicador() {
        if (converterIndicador == null) {
            converterIndicador = new ConverterGenerico(Indicador.class, indicadorFacade);
        }
        return converterIndicador;
    }
}
