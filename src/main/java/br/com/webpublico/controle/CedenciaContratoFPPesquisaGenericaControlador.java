/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.SituacaoFuncional;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.FinalidadeCedenciaContratoFP;
import br.com.webpublico.enums.TipoCedenciaContratoFP;
import br.com.webpublico.negocios.SituacaoFuncionalFacade;
import br.com.webpublico.util.UtilRH;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CedenciaContratoFPPesquisaGenericaControlador extends PesquisaGenericaRHControlador implements Serializable {
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;

    public CedenciaContratoFPPesquisaGenericaControlador() {
        setNomeVinculo("obj.contratoFP");
    }

    @Override
    public void getCampos() {
        super.getCampos();
        adicionaItemPesquisaGenerica("Tipo de Contrato de Cedência", "tipoContratoCedenciaFP", TipoCedenciaContratoFP.class, Boolean.TRUE, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Finalidade da Cedência", "finalidadeCedenciaContratoFP", FinalidadeCedenciaContratoFP.class, Boolean.TRUE, Boolean.TRUE);
        itens.add(new ItemPesquisaGenerica("(select sit.descricao from SituacaoContratoFP sitFp join sitFp.situacaoFuncional sit where sitFp.contratoFP.id = obj.contratoFP.id and '"+ UtilRH.getDataOperacaoFormatada() + "'   between sitFp.inicioVigencia and coalesce(sitFp.finalVigencia, '" + UtilRH.getDataOperacaoFormatada() + "') and rownum = 1) ", "Situação Funcional", String.class, true, true, "Não", "Sim"));
        adicionaItemPesquisaGenerica("Unidade Externa", "obj.cessionario.pessoaJuridica.razaoSocial", PessoaJuridica.class, Boolean.TRUE);
    }

    @Override
    public List<SelectItem> recuperaValuesEnum(ItemPesquisaGenerica item) {
        List<SelectItem> toReturn = new ArrayList<>();
        if (item.getLabel().equals("Situação Funcional")) {
            for (SituacaoFuncional sf : situacaoFuncionalFacade.lista()) {
                toReturn.add(new SelectItem(sf.getDescricao(), sf.getDescricao()));
            }
        } else {
            toReturn = super.recuperaValuesEnum(item);
        }
        return toReturn;
    }

}
