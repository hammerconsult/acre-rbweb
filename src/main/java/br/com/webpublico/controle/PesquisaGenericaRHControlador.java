/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Pesquisavel;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class PesquisaGenericaRHControlador extends ComponentePesquisaGenerico implements Serializable {

    private String nomeVinculo;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public void setNomeVinculo(String nomeVinculo) {
        this.nomeVinculo = nomeVinculo;
    }

    public boolean isVinculoFP() {
        boolean retorno = false;
        if (classe.equals(VinculoFP.class)) {
            retorno = true;
        } else if (classe.equals(ContratoFP.class)) {
            retorno = false;
        } else if (!classe.equals(Object.class) && classe.getSuperclass().equals(VinculoFP.class)) {
            retorno = true;
        }
        return retorno;
    }

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        adicionaItemPesquisaGenerica("Matrícula", montaSetCondicao("matriculaFP.matricula"), MatriculaFP.class, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Servidor", montaSetCondicao("matriculaFP.pessoa"), Pessoa.class, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Contrato", montaSetCondicao("numero"), ContratoFP.class, Boolean.TRUE);
        if (classe != null) {
            for (Field field : Persistencia.getAtributos(classe)) {
                if (field.isAnnotationPresent(Pesquisavel.class)) {
                    criarItemPesquisaGenerico(field);
                }
            }
        }
    }

    public String montaSetCondicao(String condicao) {
        if (isVinculoFP()) {
            return condicao;
        }
        return nomeVinculo + "." + condicao;
    }

    public void adicionaItemPesquisaGenerica(String label, String condicao, Object classe, Boolean pertenceOutraClasse) {
        ItemPesquisaGenerica item = new ItemPesquisaGenerica(condicao, label, classe, false, pertenceOutraClasse);
        super.getItens().add(item);
        super.getItensOrdenacao().add(item);
    }

    public void adicionaItemPesquisaGenerica(String label, String condicao, Object classe, Boolean eEnum, Boolean pertenceOutraClasse) {
        ItemPesquisaGenerica item = new ItemPesquisaGenerica(condicao, label, classe, eEnum, pertenceOutraClasse);
        super.getItens().add(item);
        super.getItensOrdenacao().add(item);
    }

    public String getComplementoQueryFiltrandoPelasUnidadesDoUsuario() {
        if (getSistemaControlador().getUsuarioCorrente().possuiAcessoTodosVinculosRH()) {
            return "";
        }

        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(getSistemaControlador().getDataOperacao(), getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        String complementoQuery = "";
        if (ho.getNivelNaEntidade() == 2) {
            complementoQuery = ho.getCodigo().substring(0, 6);
            complementoQuery = " inner join lotacao.unidadeOrganizacional uo" +
                " inner join uo.hierarquiasOrganizacionais ho" +
                " where substr(ho.codigo,1,6) = '" + complementoQuery + "'" +
                " and to_date('" + Util.dateToString(getSistemaControlador().getDataOperacao()) + "','dd/mm/yyyy') between ho.inicioVigencia and coalesce(ho.fimVigencia,to_date('" + Util.dateToString(getSistemaControlador().getDataOperacao()) + "','dd/mm/yyyy'))" +
                " and ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA'";
        } else {
            complementoQuery = " where lotacao.unidadeOrganizacional.id = " + getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente().getId();
        }

        return complementoQuery + " and to_date('" + Util.dateToString(getSistemaControlador().getDataOperacao()) + "','dd/mm/yyyy') between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia,to_date('" + Util.dateToString(getSistemaControlador().getDataOperacao()) + "','dd/mm/yyyy')) ";
    }
}
