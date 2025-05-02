package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 09/06/14
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class SolicitacaoCancelamentoReservaDotacaoPesquisaGenericoControlador extends ComponentePesquisaGenerico implements Serializable {


    @Override
    public String getComplementoQuery() {
        return " where obj.unidadeOrganizacional.id in (" + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + ") "
                + " and " + montaCondicao() + montaOrdenacao();
    }
}
