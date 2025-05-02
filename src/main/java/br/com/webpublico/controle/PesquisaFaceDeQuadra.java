package br.com.webpublico.controle;

import br.com.webpublico.entidades.Bairro;
import br.com.webpublico.entidades.Logradouro;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.math.BigDecimal;

@ManagedBean
public class PesquisaFaceDeQuadra extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getHqlConsulta() {
        return " select new Face(obj.id, obj.largura, lb, obj.codigoFace, obj.lado) " +
                " from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getComplementoQuery() {
        return " left join obj.logradouroBairro lb " +
               " where " + montaCondicao() + montaOrdenacao();
    }


    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        for (DataTablePesquisaGenerico data : camposPesquisa) {
            if (data != null) {
                if (data.getItemPesquisaGenerica() != null) {
                    if (data.getItemPesquisaGenerica().getTipo() != null) {
                        if (data.getItemPesquisaGenerica().getTipo().toString().equalsIgnoreCase(Logradouro.class.toString())) {
                            condicao = condicao.replace("lower(to_char(logradouro)) LIKE '%"+data.getValuePesquisa().toLowerCase()+"%' and", "");
                            condicao += " and lower(to_char(obj.logradouroBairro.logradouro.tipoLogradouro.descricao)) || ' ' || lower(to_char(obj.logradouroBairro.logradouro.nome)) LIKE '%" + data.getValuePesquisa().toLowerCase() + "%'";
                        }
                        if (data.getItemPesquisaGenerica().getTipo().toString().equalsIgnoreCase(Bairro.class.toString())) {
                            condicao = condicao.replace("lower(to_char(bairro)) LIKE '%"+data.getValuePesquisa().toLowerCase()+"%' and", "");
                            condicao += " and lower(to_char(obj.logradouroBairro.bairro.descricao)) LIKE '%" + data.getValuePesquisa().toLowerCase() + "%'";
                        }
                    }
                }
            }
        }
        return condicao;
    }

    @Override
    public void getCampos() {
        adicionarItensNaOrdenacao(new ItemPesquisaGenerica("", "", null));
        adicionarItensNaOrdenacao(new ItemPesquisaGenerica("logradouro", "Logradouro", Logradouro.class));
        adicionarItensNaOrdenacao(new ItemPesquisaGenerica("bairro", "Bairro", Bairro.class));
        adicionarItensNaOrdenacao(new ItemPesquisaGenerica("largura", "Largura da Face", Double.class));
        adicionarItensNaOrdenacao(new ItemPesquisaGenerica("codigoFace", "Código da Face", String.class));
        adicionarItensNaOrdenacao(new ItemPesquisaGenerica("lado", "Lado", String.class));
    }

    public void adicionarItensNaOrdenacao(ItemPesquisaGenerica item) {
        itens.add(item);
        itensOrdenacao.add(item);
    }
}
