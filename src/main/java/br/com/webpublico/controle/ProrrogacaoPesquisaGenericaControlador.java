/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

@ManagedBean
@ViewScoped
public class ProrrogacaoPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ProrrogacaoPesquisaGenericaControlador.class);

    @Override
    public void getCampos() {
        ItemPesquisaGenerica pessoa = new ItemPesquisaGenerica();
        pessoa.setCondicao("obj.provimentoFP.vinculoFP.matriculaFP.pessoa");
        pessoa.setLabel("Servidor");
        pessoa.setTipo(Pessoa.class);
        pessoa.setPertenceOutraClasse(true);
        super.getItens().add(pessoa);

        ItemPesquisaGenerica matricula = new ItemPesquisaGenerica();
        matricula.setCondicao("obj.provimentoFP.vinculoFP.matriculaFP.matricula");
        matricula.setLabel("Matrícula");
        matricula.setTipo(MatriculaFP.class);
        matricula.setPertenceOutraClasse(true);
        super.getItens().add(matricula);

        ItemPesquisaGenerica contrato = new ItemPesquisaGenerica();
        contrato.setCondicao("obj.provimentoFP.vinculoFP.numero");
        contrato.setLabel("Contrato do Servidor");
        contrato.setTipo(ContratoFP.class);
        contrato.setPertenceOutraClasse(true);
        super.getItens().add(contrato);
        super.getCampos();
        Collections.sort(super.getItens(), new Comparator<ItemPesquisaGenerica>() {
            @Override
            public int compare(ItemPesquisaGenerica o1, ItemPesquisaGenerica o2) {
                if (o1.getLabel().isEmpty()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    @Override
    public String getHqlConsulta() {
        return "select new Prorrogacao(obj, contrato) from Prorrogacao obj ";
    }

    @Override
    public String getComplementoQuery() {
        return ", ContratoFP contrato" +
            " join obj.provimentoFP provimentoFP " +
            " join provimentoFP.vinculoFP vinculoFP " +
            super.getComplementoQuery();
    }

    @Override
    protected String ordenacaoPadrao() {
        return "order by obj.provimentoFP.vinculoFP.matriculaFP.pessoa.nome asc, " +
            "obj.provimentoFP.vinculoFP.matriculaFP.matricula asc, " +
            "obj.provimentoFP.vinculoFP.numero asc, " +
            "obj.prorrogadoAte asc," +
            "obj.provimentoFP.dataProvimento asc";
    }

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        condicao += " and contrato.id = vinculoFP.id ";
        return condicao;
    }
}
