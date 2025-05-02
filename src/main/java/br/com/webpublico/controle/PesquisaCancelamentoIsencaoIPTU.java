/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * @author boy
 */
@ManagedBean
@ViewScoped
public class PesquisaCancelamentoIsencaoIPTU extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("isencao.cadastroImobiliario.inscricaoCadastral", "Cadastro Imobiliário", String.class));
        itens.add(new ItemPesquisaGenerica("dataOperacao", "Data de Operação", Date.class));
        itens.add(new ItemPesquisaGenerica("isencao.processoIsencaoIPTU.numero", "Número do Processo", Long.class));
        itens.add(new ItemPesquisaGenerica("isencao.processoIsencaoIPTU.exercicioReferencia.ano", "Exercício do Processo", Integer.class));
        itens.add(new ItemPesquisaGenerica("usuarioSistema.pessoaFisica.nome", "Usuário", String.class));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("isencao.cadastroImobiliario.inscricaoCadastral", "Cadastro Imobiliário", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("dataOperacao", "Data de Operação", Date.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("isencao.processoIsencaoIPTU.numero", "Número do Processo", Long.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("isencao.processoIsencaoIPTU.exercicioReferencia.ano", "Exercício do Processo", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("usuarioSistema.pessoaFisica.nome", "Usuário", String.class));


    }
}
