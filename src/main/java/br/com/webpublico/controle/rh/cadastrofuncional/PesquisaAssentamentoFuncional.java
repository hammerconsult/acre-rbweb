package br.com.webpublico.controle.rh.cadastrofuncional;

import br.com.webpublico.controle.PesquisaGenericaRHControlador;
import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

@ManagedBean
@ViewScoped
public class PesquisaAssentamentoFuncional extends PesquisaGenericaRHControlador implements Serializable {

    public PesquisaAssentamentoFuncional() {
        setNomeVinculo("obj");
    }

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        adicionaItemPesquisaGenerica("Matrícula", montaSetCondicao("contratoFP.matriculaFP.matricula"), MatriculaFP.class, Boolean.FALSE);
        adicionaItemPesquisaGenerica("Nome do Servidor", montaSetCondicao("contratoFP.matriculaFP.pessoa.nome"), PessoaFisica.class, Boolean.FALSE);
        adicionaItemPesquisaGenerica("Data de Cadastro", montaSetCondicao("dataCadastro"), Date.class, Boolean.FALSE);
    }
}
