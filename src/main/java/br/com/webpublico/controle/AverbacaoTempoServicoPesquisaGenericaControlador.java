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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;

@ManagedBean
@ViewScoped
public class AverbacaoTempoServicoPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(AverbacaoTempoServicoPesquisaGenericaControlador.class);

    @Override
    public void getCampos() {
        super.getCampos();

        ItemPesquisaGenerica pessoa = new ItemPesquisaGenerica();
        pessoa.setCondicao("obj.contratoFP.matriculaFP.pessoa");
        pessoa.setLabel("Servidor");
        pessoa.setTipo(Pessoa.class);
        pessoa.setPertenceOutraClasse(true);
        super.getItens().add(pessoa);

        ItemPesquisaGenerica matricula = new ItemPesquisaGenerica();
        matricula.setCondicao("obj.contratoFP.matriculaFP.matricula");
        matricula.setLabel("Matrícula");
        matricula.setTipo(MatriculaFP.class);
        matricula.setPertenceOutraClasse(true);
        super.getItens().add(matricula);

        ItemPesquisaGenerica contrato = new ItemPesquisaGenerica();
        contrato.setCondicao("obj.contratoFP.numero");
        contrato.setLabel("Contrato do Servidor");
        contrato.setTipo(ContratoFP.class);
        contrato.setPertenceOutraClasse(true);
        super.getItens().add(contrato);

    }

    @Override
    public void novo(ComponentSystemEvent evento) {
        try {
            super.novo(evento);
            List<ItemPesquisaGenerica> itens = new ArrayList<>();
            List<ItemPesquisaGenerica> itensOrdenacao = new ArrayList<>();
            if (super.getItens() != null) {
                for (ItemPesquisaGenerica itemPesquisaGenerica : super.getItens()) {
                    if (itemPesquisaGenerica.getLabel().startsWith("Tipo de promoção") || (itemPesquisaGenerica.getLabel().startsWith("Data de Cadastro"))) {
                        itens.add(itemPesquisaGenerica);
                    }
                }
            }
            for (ItemPesquisaGenerica itemPesquisaGenerica : itens) {
                super.getItens().remove(itemPesquisaGenerica);
            }

            if (super.getItensOrdenacao() != null) {
                for (ItemPesquisaGenerica itemPesquisaGenerica : super.getItensOrdenacao()) {
                    if (itemPesquisaGenerica.getLabel().startsWith("Tipo de promoção") || (itemPesquisaGenerica.getLabel().startsWith("Data de Cadastro"))) {
                        itensOrdenacao.add(itemPesquisaGenerica);
                    }
                }
            }

            for (ItemPesquisaGenerica itemPesquisaGenerica : itensOrdenacao) {
                super.getItensOrdenacao().remove(itemPesquisaGenerica);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());

        }
    }
}
