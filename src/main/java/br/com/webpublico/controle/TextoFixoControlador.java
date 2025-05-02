/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TextoFixo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TextoFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.ResultadoValidacao;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Renato
 */
@ManagedBean
@SessionScoped
public class TextoFixoControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private TextoFacade textoFacade;

    @Override
    public AbstractFacade getFacede() {
        return textoFacade;
    }

    public TextoFixoControlador() {
        metadata = new EntidadeMetaData(TextoFixo.class);
    }

    public List<SelectItem> getTipoCadastroTributario() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario object : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public TextoFixo esteSelecionado() {
        return ((TextoFixo) selecionado);
    }

    @Override
    public void novo() {
        super.novo();
        esteSelecionado().setCodigo(textoFacade.recuperaProximoCodigo());
        esteSelecionado().setTag("$TEXTO_FIXO" + esteSelecionado().getCodigo());
    }

    @Override
    public String salvar() {
        ResultadoValidacao resultado;
        resultado = textoFacade.salva(esteSelecionado());
        if (resultado.isValidado()) {
            lista = null;
            FacesUtil.printAllMessages(resultado.getMensagens());
            return caminho();
        } else {
            FacesUtil.printAllMessages(resultado.getMensagens());
            return "edita.xhtml";
        }
    }
}
