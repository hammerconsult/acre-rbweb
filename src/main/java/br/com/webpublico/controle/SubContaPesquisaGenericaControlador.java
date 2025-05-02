package br.com.webpublico.controle;

import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidadesauxiliares.AtributoRelatorioGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 03/12/13
 * Time: 08:28
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class SubContaPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {


    @Override
    public void filtrar() {
        String hql = getHqlConsulta() + getComplementoQuery();
        String hqlCount = getHqlContador() + getComplementoQuery();
        executarConsulta(hql, hqlCount);
    }

    @Override
    public String getHqlConsulta() {
        return super.getHqlConsulta();
    }

    @Override
    public String getHqlContador() {
        return super.getHqlContador();
    }

    @Override
    public String getComplementoQuery() {
        return " where " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public void getCampos() {
        super.getCampos();
        for (ItemPesquisaGenerica itemPesquisaGenerica : super.getItens()) {
            if (itemPesquisaGenerica.getCondicao().equals("contaBancariaEntidade.numeroConta")) {
                itemPesquisaGenerica.setTipo(Integer.class);
            }
        }
    }

    @Override
    public String preencherCampo(Object objeto, AtributoRelatorioGenerico atributo) {
        if (atributo.getClasse().contains("ContaBancariaEntidade")) {
            SubConta subConta = (SubConta) objeto;
            if (subConta != null && subConta.getContaBancariaEntidade() != null) {
                return subConta.getContaBancariaEntidade().toStringBancoAgenciaContaDescricao();
            }
        }
        return super.preencherCampo(objeto, atributo);
    }
}
