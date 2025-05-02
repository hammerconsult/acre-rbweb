package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.StatusLancamentoOutorga;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 17/12/13
 * Time: 09:49
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PesquisaLancamentoOutorga extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("exercicio.ano", "Exercício", Integer.class));
        itens.add(new ItemPesquisaGenerica("mes", "Mês", String.class));
        itens.add(new ItemPesquisaGenerica("cmc.inscricaoCadastral", "C.M.C", String.class));
        itens.add(new ItemPesquisaGenerica("cmc.pessoa", "Nome/Razão Social/CPF/CNPJ", Pessoa.class));
        itens.add(new ItemPesquisaGenerica("dataLancamento", "Data do Lançamento", Date.class));
        itens.add(new ItemPesquisaGenerica("usuarioLancamento", "Usuário do Lançamento", UsuarioSistema.class));
        itens.add(new ItemPesquisaGenerica("statusLancamentoOutorga", "Situação do Lançamento", StatusLancamentoOutorga.class, true));


        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("exercicio", "Exercício", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("mes", "Mês", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("cmc.inscricaoCadastral", "C.M.C", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("cmc.pessoa", "Nome/Razão Social/CPF/CNPJ", Pessoa.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("dataLancamento", "Data do Lançamento", Date.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("usuarioLancamento", "Usuário do Lançamento", UsuarioSistema.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("statusLancamentoOutorga", "Situação do Lançamento", StatusLancamentoOutorga.class));


    }


    @Override
    public String getHqlConsulta() {
        return "select new LancamentoOutorga(obj.id, exercicio, cmc, usuario, obj.mes, obj.passageiroTranspEquiv, obj.valorDaTarifa, " +
                "obj.percentualDaOutorga, obj.dataLancamento, obj.valorOutorga, obj.valorISSCorrespondente, obj.statusLancamentoOutorga) " +
                " from LancamentoOutorga obj " +
                " left join obj.usuarioLancamento usuario " +
                " inner join obj.cmc cmc" +
                " inner join obj.exercicio exercicio ";
    }


}

