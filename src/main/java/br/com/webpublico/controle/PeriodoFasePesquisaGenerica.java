/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Fase;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author Edi
 */
@ManagedBean
@ViewScoped
public class PeriodoFasePesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void novo(ComponentSystemEvent evento) {
        super.novo(evento);
        super.prepararAtributos();

        ItemPesquisaGenerica codigo = new ItemPesquisaGenerica();
        codigo.setLabel("Código da Fase");
        codigo.setCondicao("to_number(f.codigo)");
        codigo.setTipo(Fase.class);
        codigo.setPertenceOutraClasse(true);

        ItemPesquisaGenerica nomeDaFase = new ItemPesquisaGenerica();
        nomeDaFase.setLabel("Nome da Fase");
        nomeDaFase.setCondicao("f.nome");
        nomeDaFase.setTipo(Fase.class);
        nomeDaFase.setPertenceOutraClasse(true);

        ItemPesquisaGenerica exercicio = new ItemPesquisaGenerica();
        Field fieldExercicio = getFieldDaClasse(super.getClasse(), Exercicio.class);
        exercicio.setLabel(super.recuperaNomeDoCampo(fieldExercicio));
        exercicio.setCondicao("obj.exercicio.ano");
        exercicio.setTipoOrdenacao("desc");
        exercicio.setPertenceOutraClasse(false);

        ItemPesquisaGenerica descricao = new ItemPesquisaGenerica();
        descricao.setLabel("Descrição");
        descricao.setCondicao("obj.descricao");
        descricao.setTipoOrdenacao("desc");

        super.getItens().add(codigo);
        super.getItens().add(nomeDaFase);

        super.getItensOrdenacao().add(codigo);
        super.getItensOrdenacao().add(nomeDaFase);

        super.getCamposOrdenacao().add(exercicio);
        super.getCamposOrdenacao().add(codigo);
        super.getCamposOrdenacao().add(descricao);
    }

    public Field getFieldDaClasse(Class classe, Class classeDoAtributo) {
        for (Field field : classe.getDeclaredFields()) {
            if (field.getType().equals(classeDoAtributo)) {
                return field;
            }
        }
        return null;
    }

    public Field getFieldDaClassePeloNome(Class classe, String nomeDoAtributo) {
        for (Field field : classe.getDeclaredFields()) {
            if (field.getName().equals(nomeDoAtributo)) {
                return field;
            }
        }
        return null;
    }

    @Override
    public String getHqlConsulta() {
        return super.getHqlConsulta();
    }

    @Override
    public String getComplementoQuery() {
        return " inner join obj.fase f where "
            + montaCondicao() + montaOrdenacao();
    }
}
