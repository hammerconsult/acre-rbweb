package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 27/08/15
 * Time: 09:14
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PesquisaLicitacaoControlador extends AbstractPesquisaLicitacaoCompra {

    @Override
    public String getNivelHierarquia() {
        return null;
    }

    @Override
    protected String[] getAtributoUnidadeOrganizacional() {
        return new String[]{"pc.unidadeOrganizacional.id"};
    }

    @Override
    public String getComplementoQuery() {
        return " inner join obj.processoDeCompra pc " + super.getComplementoQuery();
    }
}
