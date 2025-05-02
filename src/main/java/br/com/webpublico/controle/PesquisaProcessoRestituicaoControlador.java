package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Wellington on 07/12/2015.
 */
@ManagedBean
@ViewScoped
public class PesquisaProcessoRestituicaoControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.codigo", "Código", Long.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.data", "Data de lançamento", Date.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.exercicio.ano", "Exercício", Integer.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.protocolo.numero", "Número de Protocolo", String.class, false, false));
        itens.add(new ItemPesquisaGenerica("cad.bci", "Inscrição Imobiliária", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("cad.cmc", "C.M.C.", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pessoa.razaoSocial", "Razão Social do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pessoa.nome", "Nome do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pessoa.cpf", "CPF do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pessoa.cnpj", "CNPJ do Contribuinte", String.class, false, true));

        itensOrdenacao.addAll(itens);

    }

    @Override
    public String getHqlConsulta() {
        return "select distinct obj  from ProcessoRestituicao obj ";
    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id)  from ProcessoRestituicao obj ";
    }

    @Override
    public String getComplementoQuery() {
        StringBuilder sql = new StringBuilder();

        sql.append("join obj.pagamentos pr_pag ");
        sql.append("join pr_pag.pagamentoJudicial pg_judic ");
        sql.append("left join pg_judic.cadastro cad ");
        sql.append("left join pg_judic.pessoa pessoa ");
        sql.append(" where ");

        sql.append(montaCondicao());
        sql.append(montaOrdenacao());

        return sql.toString();
    }

    @Override
    public String montaCondicao() {
        String valor;
        String condicao = super.montaCondicao();
        for (DataTablePesquisaGenerico obj : camposPesquisa) {
            if (obj.getItemPesquisaGenerica() != null) {
                if (obj.getItemPesquisaGenerica().getCondicao().equals("cad.bci")) {
                    valor = obj.getValuePesquisa().toLowerCase();
                    if (valor != null && valor != "") {
                        condicao = condicao.replace("lower(to_char(cad.bci)) LIKE '%" + valor + "%' and ", condicaoInscricaoImobiliaria(valor));
                    } else {
                        condicao = condicao.replace("cad.cmc is null and", condicaoInscricaoImobiliaria(valor));
                    }
                    break;
                }
                if (obj.getItemPesquisaGenerica().getCondicao().equals("cad.cmc")) {
                    valor = obj.getValuePesquisa().toLowerCase();
                    if (valor != null && valor != "") {
                        condicao = condicao.replace("lower(to_char(cad.cmc)) LIKE '%" + valor + "%' and ", condicaoInscricaoCadastral(valor));
                    } else {
                        condicao = condicao.replace("cad.cmc is null and", condicaoInscricaoCadastral(valor));
                    }
                    break;
                }
            }
        }
        return condicao;
    }

    public String condicaoInscricaoImobiliaria(String valor) {
        StringBuilder sb = new StringBuilder();
        sb.append(" cad.id in (");
        sb.append("select id from CadastroImobiliario where inscricaoCadastral = '" + valor + "'");
        sb.append(") and ");
        return sb.toString();
    }

    public String condicaoInscricaoCadastral(String valor) {
        StringBuilder sb = new StringBuilder();
        sb.append(" cad.id in (");
        sb.append("select id from CadastroEconomico where inscricaoCadastral = '" + valor + "'");
        sb.append(") and ");
        return sb.toString();
    }

}
