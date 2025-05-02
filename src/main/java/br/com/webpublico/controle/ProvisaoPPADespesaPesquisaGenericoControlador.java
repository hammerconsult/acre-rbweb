package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 01/08/13
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ProvisaoPPADespesaPesquisaGenericoControlador extends ComponentePesquisaGenerico implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("(Select orc.codigo || ' - ' || orc.descricao " +
            "                                  from HierarquiaOrganizacional orc " +
            "                                where '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between orc.inicioVigencia " +
            "                                  and coalesce(orc.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "') " +
            "                                  and orc.subordinada.id = obj.unidadeOrganizacional.id " +
            "                                  and orc.tipoHierarquiaOrganizacional = 'ORCAMENTARIA' " +
            "                                  and rownum = 1 ) ", "Unidade Orçamentária", String.class, false, true));
    }

    @Override
    public String getComplementoQuery() {
        ProvisaoPPAWizardControlador provisaoPPAWizardControlador = (ProvisaoPPAWizardControlador) Util.getControladorPeloNome("provisaoPPAWizardControlador");
        Long id = provisaoPPAWizardControlador.getId();

        return " where obj.subAcaoPPA.id = '" + id + "' " +
                "and " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public String getHqlConsulta() {
        return " select new ProvisaoPPADespesa(obj, " +
            "    (select ho from HierarquiaOrganizacional ho " +
            "               where ho.tipoHierarquiaOrganizacional = 'ORCAMENTARIA' " +
            "               and '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between ho.inicioVigencia and " +
            "               coalesce(ho.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "') " +
            "               and ho.subordinada = obj.unidadeOrganizacional)) " +
            "               from " + classe.getSimpleName() + " obj ";
    }
}
