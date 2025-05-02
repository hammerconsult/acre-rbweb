package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Alex
 */
@ManagedBean
@ViewScoped
public class PesquisaPastaGavetaContratoFP extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void novo(ComponentSystemEvent evento) {
        super.novo(evento);
        super.prepararAtributos();
        montarItensPesquisa();
        montarItensOrdenacao();
    }

    private void montarItensPesquisa() {
        super.getItens().add(criarItemMatricula());
        super.getItens().add(criarItemNomeTratamento());
        super.getItens().add(criarItemServidor());
        super.getItens().add(criarItemNumeroServidor());
        super.getItens().add(criarItemFicharioCodigo());
        super.getItens().add(criarItemFicharioDescricao());
        super.getItens().add(criarItemGavetaCodigo());
        super.getItens().add(criarItemGavetaDescricao());
        super.getItens().add(criarItemPasta());
        super.getItens().add(criarItemInicioVigencia());
        super.getItens().add(criarItemFinalVigencia());
    }

    private void montarItensOrdenacao() {
        super.getItensOrdenacao().add(criarItemMatricula());
        super.getItensOrdenacao().add(criarItemNomeTratamento());
        super.getItensOrdenacao().add(criarItemFicharioCodigo());
        super.getItensOrdenacao().add(criarItemFicharioDescricao());
        super.getItensOrdenacao().add(criarItemGavetaCodigo());
        super.getItensOrdenacao().add(criarItemGavetaDescricao());
        super.getItensOrdenacao().add(criarItemPasta());
        super.getItensOrdenacao().add(criarItemInicioVigencia());
        super.getItensOrdenacao().add(criarItemFinalVigencia());
    }

    private ItemPesquisaGenerica criarItemMatricula() {
        ItemPesquisaGenerica matricula = new ItemPesquisaGenerica();
        matricula.setLabel("Matrícula");
        matricula.setCondicao("obj.contratoFP.matriculaFP.matricula");
        matricula.setTipo(String.class);
        matricula.seteEnum(false);
        matricula.setPertenceOutraClasse(true);
        return matricula;
    }

    private ItemPesquisaGenerica criarItemNomeTratamento() {
        ItemPesquisaGenerica nomeTratamento = new ItemPesquisaGenerica();
        nomeTratamento.setLabel("Nome Tratamento/Social do Servidor");
        nomeTratamento.setCondicao("obj.contratoFP.matriculaFP.pessoa.nomeTratamento");
        nomeTratamento.setTipo(String.class);
        nomeTratamento.seteEnum(false);
        nomeTratamento.setPertenceOutraClasse(true);
        return nomeTratamento;
    }

    private ItemPesquisaGenerica criarItemServidor() {
        ItemPesquisaGenerica matricula = new ItemPesquisaGenerica();
        matricula.setLabel("Servidor");
        matricula.setCondicao("obj.contratoFP.matriculaFP.pessoa.nome");
        matricula.setTipo(String.class);
        matricula.seteEnum(false);
        matricula.setPertenceOutraClasse(true);
        return matricula;
    }

    private ItemPesquisaGenerica criarItemNumeroServidor() {
        ItemPesquisaGenerica matricula = new ItemPesquisaGenerica();
        matricula.setLabel("Numero");
        matricula.setCondicao("obj.contratoFP.numero");
        matricula.setTipo(String.class);
        matricula.seteEnum(false);
        matricula.setPertenceOutraClasse(true);
        return matricula;
    }

    private ItemPesquisaGenerica criarItemFicharioCodigo() {
        ItemPesquisaGenerica ficharioCodigo = new ItemPesquisaGenerica();
        ficharioCodigo.setLabel("Fichário Código");
        ficharioCodigo.setCondicao("obj.pastaGaveta.gavetaFichario.fichario.codigo");
        ficharioCodigo.setTipo(Integer.class);
        ficharioCodigo.seteEnum(false);
        ficharioCodigo.setPertenceOutraClasse(true);
        return ficharioCodigo;
    }

    private ItemPesquisaGenerica criarItemFicharioDescricao() {
        ItemPesquisaGenerica ficharioCodigo = new ItemPesquisaGenerica();
        ficharioCodigo.setLabel("Fichário Descrição");
        ficharioCodigo.setCondicao("obj.pastaGaveta.gavetaFichario.fichario.descricao");
        ficharioCodigo.setTipo(String.class);
        ficharioCodigo.seteEnum(false);
        ficharioCodigo.setPertenceOutraClasse(true);
        return ficharioCodigo;
    }

    private ItemPesquisaGenerica criarItemGavetaCodigo() {
        ItemPesquisaGenerica ficharioCodigo = new ItemPesquisaGenerica();
        ficharioCodigo.setLabel("Gaveta Código");
        ficharioCodigo.setCondicao("obj.pastaGaveta.gavetaFichario.codigo");
        ficharioCodigo.setTipo(Integer.class);
        ficharioCodigo.seteEnum(false);
        ficharioCodigo.setPertenceOutraClasse(true);
        return ficharioCodigo;
    }

    private ItemPesquisaGenerica criarItemGavetaDescricao() {
        ItemPesquisaGenerica ficharioCodigo = new ItemPesquisaGenerica();
        ficharioCodigo.setLabel("Gaveta Descrição");
        ficharioCodigo.setCondicao("obj.pastaGaveta.gavetaFichario.descricao");
        ficharioCodigo.setTipo(String.class);
        ficharioCodigo.seteEnum(false);
        ficharioCodigo.setPertenceOutraClasse(true);
        return ficharioCodigo;
    }

    private ItemPesquisaGenerica criarItemPasta() {
        ItemPesquisaGenerica ficharioCodigo = new ItemPesquisaGenerica();
        ficharioCodigo.setLabel("Pasta");
        ficharioCodigo.setCondicao("obj.pastaGaveta.numero");
        ficharioCodigo.setTipo(Integer.class);
        ficharioCodigo.seteEnum(false);
        ficharioCodigo.setPertenceOutraClasse(true);
        return ficharioCodigo;
    }

    private ItemPesquisaGenerica criarItemInicioVigencia() {
        ItemPesquisaGenerica ficharioCodigo = new ItemPesquisaGenerica();
        ficharioCodigo.setLabel("Inicio de Vigência");
        ficharioCodigo.setCondicao("obj.inicioVigencia");
        ficharioCodigo.setTipo(Date.class);
        ficharioCodigo.seteEnum(false);
        ficharioCodigo.setPertenceOutraClasse(false);
        return ficharioCodigo;
    }

    private ItemPesquisaGenerica criarItemFinalVigencia() {
        ItemPesquisaGenerica ficharioCodigo = new ItemPesquisaGenerica();
        ficharioCodigo.setLabel("Final de Vigência");
        ficharioCodigo.setCondicao("obj.finalVigencia");
        ficharioCodigo.setTipo(Date.class);
        ficharioCodigo.seteEnum(false);
        ficharioCodigo.setPertenceOutraClasse(false);
        return ficharioCodigo;
    }

    @Override
    public String getHqlConsulta() {
        return "select new PastaGavetaContratoFP(obj) from " + classe.getSimpleName() + " obj";
    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id) from PastaGavetaContratoFP obj";
    }
}
