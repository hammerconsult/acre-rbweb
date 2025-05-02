package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 12/12/13
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
public class PesquisaContribuinteDebitoOutorga extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getHqlConsulta() {
        return "select new ContribuinteDebitoOutorga(obj.id, obj.cadastroEconomico, obj.cadastradoEm, usuarioCadastrou, obj.atualizadoEm, usuarioAlterou) from ContribuinteDebitoOutorga obj ";
    }

    @Override
    public String getComplementoQuery() {
        return "left join obj.usuarioCadastrou usuarioCadastrou left join obj.usuarioAlterou usuarioAlterou where " + montaCondicao() + montaOrdenacao();
    }

    @Override
    protected String montaOrdenacao() {
        if (camposOrdenacao == null || camposOrdenacao.size() <= 1) {
            return " order by obj.exercicio desc";
        }
        return super.montaOrdenacao();
    }

    @Override
    public String montaCondicao() {
        String condicao = "";
        for (DataTablePesquisaGenerico dataTablePesquisaGenerico : camposPesquisa) {
            if (validaItem(dataTablePesquisaGenerico)) {
                try {
                    System.out.println("dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao() " + dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao());
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("obj.inscricaoCadastral")
                        && !"".equals(dataTablePesquisaGenerico.getValuePesquisa().trim())) {
                        condicao += " obj.cadastroEconomico.inscricaoCadastral = " + dataTablePesquisaGenerico.getValuePesquisa();
                        condicao += " and ";
                    }
                    if (!dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("obj.inscricaoCadastral")
                        && !"".equals(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().trim())) {
                        return super.montaCondicao();
                    }

                } catch (NullPointerException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar montar a condição de pesquisa!", ""));
                }

            }
        }
        condicao += " 1=1 ";
        return condicao;
    }
}
