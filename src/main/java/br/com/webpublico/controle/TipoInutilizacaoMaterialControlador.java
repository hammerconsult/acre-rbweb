/*
 * Codigo gerado automaticamente em Wed Feb 22 20:21:33 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoInutilizacaoMaterial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoInutilizacaoMaterialFacade;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class TipoInutilizacaoMaterialControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private TipoInutilizacaoMaterialFacade tipoInutilizacaoMaterialFacade;

    public TipoInutilizacaoMaterialControlador() {
        metadata = new EntidadeMetaData(TipoInutilizacaoMaterial.class);
    }

    public TipoInutilizacaoMaterialFacade getFacade() {
        return tipoInutilizacaoMaterialFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoInutilizacaoMaterialFacade;
    }
}
