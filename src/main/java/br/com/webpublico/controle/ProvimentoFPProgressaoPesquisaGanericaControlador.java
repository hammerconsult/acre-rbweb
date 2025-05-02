/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.TipoProvimento;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class ProvimentoFPProgressaoPesquisaGanericaControlador extends ComponentePesquisaGenerico implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ProvimentoFPProgressaoPesquisaGanericaControlador.class);

    @Override
    public String getComplementoQuery() {
        return " inner join obj.provimentoFP provimento " +
            "    inner join provimento.tipoProvimento tipo" +
            "    where to_char(obj.inicioVigencia, 'dd/mm/yyyy') <= " + "'" + super.getSistemaControlador().getDataOperacao() + "'"
            + "  and tipo.codigo = " + TipoProvimento.PROVIMENTO_PROGRESSAO + " and " + montaCondicao() + " order by obj.inicioVigencia desc ";
    }

    @Override
    public void getCampos() {

        super.getCampos();

        ItemPesquisaGenerica pessoa = new ItemPesquisaGenerica();
        pessoa.setCondicao("obj.contratoServidor.matriculaFP.pessoa");
        pessoa.setLabel("Servidor");
        pessoa.setTipo(Pessoa.class);
        pessoa.setPertenceOutraClasse(true);
        super.getItens().add(pessoa);

        ItemPesquisaGenerica matricula = new ItemPesquisaGenerica();
        matricula.setCondicao("obj.contratoServidor.matriculaFP.matricula");
        matricula.setLabel("Matrícula");
        matricula.setTipo(MatriculaFP.class);
        matricula.setPertenceOutraClasse(true);
        super.getItens().add(matricula);

        ItemPesquisaGenerica contrato = new ItemPesquisaGenerica();
        contrato.setCondicao("obj.contratoServidor.numero");
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
