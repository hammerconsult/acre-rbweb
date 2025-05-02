package br.com.webpublico.controle;

import br.com.webpublico.entidades.CalculoRBTrans;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.util.EntidadeMetaData;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ManagedBean
@ViewScoped
public class PesquisaDebitosRbTrans extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("cad.inscricaoCadastral", "C.M.C.", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pes.nome", "Nome do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pes.razaoSocial", "Razão Social do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pes.cpf", "CPF do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pes.cnpj", "CNPJ do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.numeroLancamento", "Número do Lançamento", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.processoCalculo.exercicio.ano", "Ano do Lançamento", String.class, false, true));
    }

    @Override
    public String getHqlContador() {
        return "select count(obj.id) " +
            " from CalculoRBTrans obj " +
            " left join obj.cadastro cad " +
            " left join obj.pessoas cp " +
            " left join cp.pessoa pes ";
    }

    @Override
    public String getHqlConsulta() {
        return "select distinct obj " +
            " from CalculoRBTrans obj " +
            " left join obj.cadastro cad " +
            " left join obj.pessoas cp " +
            " left join cp.pessoa pes ";
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
        lista = new ArrayList((new HashSet<CalculoRBTrans>(lista)));
        if (lista.size() > 0) {
            metadata = new EntidadeMetaData(lista.get(0).getClass());
            for (CalculoRBTrans calculo : (List<CalculoRBTrans>) lista) {
                calculo.preencherColunasPesquisaGenerica();
            }
        }
    }

}


