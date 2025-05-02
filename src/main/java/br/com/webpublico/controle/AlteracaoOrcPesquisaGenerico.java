/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.negocios.AlteracaoORCFacade;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Romanini
 */
@ManagedBean
@ViewScoped
public class AlteracaoOrcPesquisaGenerico extends ComponentePesquisaGenerico implements Serializable {

    @EJB
    private AlteracaoORCFacade alteracaoORCFacade;

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("(Select orc.codigo || ' - ' || orc.descricao " +
                "                                  from HierarquiaOrganizacional orc " +
                "                                where obj.dataAlteracao between orc.inicioVigencia " +
                "                                  and coalesce(orc.fimVigencia, obj.dataAlteracao) " +
                "                                  and orc.subordinada.id = und" +
                "                                  and orc.tipoHierarquiaOrganizacional = 'ORCAMENTARIA' " +
                "                                  and rownum = 1 )", "Unidade Orçamentária", String.class, false, true));
    }

    @Override
    public String getHqlConsulta() {

        return " select new AlteracaoORC(obj, (select ho from HierarquiaOrganizacional ho " +
                " where ho.tipoHierarquiaOrganizacional = 'ORCAMENTARIA' " +
                " and obj.dataAlteracao between ho.inicioVigencia and coalesce(ho.fimVigencia, obj.dataAlteracao) " +
                " and ho.subordinada = und)) from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getComplementoQuery() {
        return "  left join obj.atoLegal ato " +
                " inner join obj.unidadeOrganizacionalOrc und " +
            super.getComplementoQuery();
    }

    @Override
    public String montaCondicao() {
        Boolean usuarioGestor = isGestor();
        String condicao = super.montaCondicao();
        condicao += " and to_char (obj.dataAlteracao, 'yyyy') = '" + super.getSistemaControlador().getExercicioCorrenteAsInteger().toString() + "'";
        if (!usuarioGestor) {
            condicao += " and obj.unidadeOrganizacionalOrc.id = " + super.getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId();
        }
        return condicao;
    }

    public Boolean isGestor() {
        try {
            return alteracaoORCFacade.isGestorOrcamento(super.getSistemaControlador().getUsuarioCorrente(), super.getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(), super.getSistemaControlador().getDataOperacao());
        } catch (Exception ex) {
            FacesUtil.addError("Erro: ", "Problema ao verificar se o usuario é gestor " + ex.getMessage());
            return false;
        }
    }
}
