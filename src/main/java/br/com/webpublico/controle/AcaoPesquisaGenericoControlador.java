package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.AndamentoAcao;
import br.com.webpublico.enums.HorizonteTemporal;
import br.com.webpublico.enums.TipoProgramaPPA;
import br.com.webpublico.negocios.PPAFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 30/07/13
 * Time: 10:10
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class AcaoPesquisaGenericoControlador extends ComponentePesquisaGenerico {

    private static final Logger logger = LoggerFactory.getLogger(AcaoPesquisaGenericoControlador.class);
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    @Override
    public void getCampos() {


        ItemPesquisaGenerica ppa = new ItemPesquisaGenerica("obj.programa.ppa", "PPA", PPA.class, true, false);
        ItemPesquisaGenerica codigoppa = new ItemPesquisaGenerica("to_number(obj.codigo) ", "Código", ProgramaPPA.class, false, true);
        ItemPesquisaGenerica programaCodigo = new ItemPesquisaGenerica("to_number(obj.programa.codigo) ", "Programa.Código", Long.class, false, true);
        ItemPesquisaGenerica tipoDeAcaoCodigo = new ItemPesquisaGenerica("to_number(obj.tipoAcaoPPA.codigo)", "Tipo de Ação.Código", Integer.class, false, true);

        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.descricao", "Descrição", String.class, false, false));
        itens.add(ppa);
        itens.add(new ItemPesquisaGenerica("programa", "Possui Programa ?", Boolean.class, false, false, "Não", "Sim"));
        itens.add(codigoppa);
        itens.add(new ItemPesquisaGenerica("programa.ppa.descricao", "Programa.PPA", String.class, false, false));
        itens.add(new ItemPesquisaGenerica("programa.denominacao", "Programa.Demonimação", String.class, false, false));
        itens.add(programaCodigo);
        itens.add(new ItemPesquisaGenerica("obj.responsavel.descricao", "Programa.Unidade Responsável", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("programa.itemPlanejamentoEstrategico.nome", "Programa.Objetivo.Nome do Eixo", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.programa.tipoPrograma", "Programa.Tipo de programa", TipoProgramaPPA.class, true, true));
        itens.add(new ItemPesquisaGenerica("obj.programa.horizonteTemporal", "Programa.Horizonte Temporal", HorizonteTemporal.class, true, false));
        itens.add(new ItemPesquisaGenerica("programa.inicio", "Programa.Data de Início", Date.class, false, false));
        itens.add(new ItemPesquisaGenerica("programa.fim", "Programa.Data de Fim", Date.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.programa.multisetorial", "Programa.Multisetorial", Boolean.class, false, false, "Não", "Sim"));
        itens.add(new ItemPesquisaGenerica("indicador.indicador.descricao", "Programa.Indicadores", String.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.descricao", "Programa.Descrição", String.class, false, false));
        itens.add(tipoDeAcaoCodigo);
        itens.add(new ItemPesquisaGenerica("obj.tipoAcaoPPA.descricao", "Tipo de ação.Descrição", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.tipoAcaoPPA.nomenclaturaTCE", "Tipo de Ação.Nomeclatura TCE", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.periodicidadeMedicao.descricao", "Periodicidade de Medição.Descrição", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.periodicidadeMedicao.dias", "Periodicidade de Medição.Dias", Integer.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.descricaoProduto", "Descrição do Produto", String.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.unidadeMedidaProduto.descricao", "Unidade de Medida de Produto.Descrição", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.unidadeMedidaProduto.sigla", "Unidade de Medida de Produto.Sigla", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("to_number(obj.funcao.codigo)", "Função.Código", Funcao.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.funcao.descricao", "Função.Descrição", Funcao.class, false, true));
        itens.add(new ItemPesquisaGenerica("to_number(obj.subFuncao.codigo)", "SubFunção.Código", SubFuncao.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.subFuncao.descricao", "SubFunção.Descrição", SubFuncao.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.subFuncao.funcao", "SubFunção.Função", SubFuncao.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.totalFisico", "Total.Físico", BigDecimal.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.totalFinanceiro", "Total.Financeiro", BigDecimal.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.andamento", "Andamento", AndamentoAcao.class, true, true));

        DataTablePesquisaGenerico item = new DataTablePesquisaGenerico();
        item.setItemPesquisaGenerica(ppa);
        camposPesquisa.add(item);

        ItemPesquisaGenerica itemProgramaExercicio = new ItemPesquisaGenerica("obj.exercicio.ano", "Exercício", Integer.class);
        DataTablePesquisaGenerico dataTablePesquisaG = new DataTablePesquisaGenerico();
        dataTablePesquisaG.setItemPesquisaGenerica(itemProgramaExercicio);
        dataTablePesquisaG.setPodeRemover(false);
        Exercicio exercicio = sistemaControlador.getExercicioCorrente();
        if (exercicio != null) {
            dataTablePesquisaG.setValuePesquisa(exercicio.getAno().toString());
        }
        itens.add(itemProgramaExercicio);
        camposPesquisa.add(dataTablePesquisaG);

        camposOrdenacao.add(codigoppa);
        camposOrdenacao.add(programaCodigo);
        camposOrdenacao.add(tipoDeAcaoCodigo);

        itensOrdenacao.addAll(itens);
    }

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();

        if (condicao.contains("programa = true")) {
            condicao = condicao.replace("programa = true", " obj.programa is not null ");
        }
        if (condicao.contains("programa = false")) {
            condicao = condicao.replace("programa = false", " obj.programa is null ");
        }
        return condicao;
    }

    @Override
    public String getComplementoQuery() {
        if (!isGestorOrcamento()) {
            return " left join obj.programa.indicadores indicador " +
                " where obj.responsavel.id in ("
                + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + ") " +
                "    and " + montaCondicao() + montaOrdenacao();
        }
        return super.getComplementoQuery();
    }

    public Boolean isGestorOrcamento() {
        UnidadeOrganizacional orc = super.getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente();
        UsuarioSistema usuario = super.getSistemaControlador().getUsuarioCorrente();
        Date data = super.getSistemaControlador().getDataOperacao();
        return usuarioSistemaFacade.isGestorOrcamento(usuario, orc, data);
    }

    @Override
    protected String ordenacaoPadrao() {
        return " order by to_number(obj.codigo) asc ";

    }

    private void ordenarPPA(List<PPA> ppaS) {
        Collections.sort(ppaS, new Comparator<PPA>() {
            @Override
            public int compare(PPA o1, PPA o2) {
                Date i1 = o1.getDataRegistro();
                Date i2 = o2.getDataRegistro();
                return i1.compareTo(i2);
            }
        });
    }

    @Override
    public List<SelectItem> recuperaValuesEnum(ItemPesquisaGenerica item) {
        String nomeDaClasse = item.getTipo().toString();
        nomeDaClasse = nomeDaClasse.replace("class ", "");
        Class<?> classe = null;
        try {
            classe = Class.forName(nomeDaClasse);
        } catch (ClassNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Erro !", "Classe não encontrada : " + nomeDaClasse));
        }
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        if (classe.equals(PPA.class)) {
            List<PPA> ppas = ppaFacade.lista();
            ordenarPPA(ppas);
            for (PPA ppa : ppas) {
                retorno.add(new SelectItem(ppa.getId(), ppa.getDescricao() + " - " + ppa.getVersao() +
                    (ppa.getAtoLegal() != null ? " - " + ppa.getAtoLegal().getTipoNumeroAnoPPA() : " - PLC Nº XX/XXXX")));
            }
            return retorno;
        } else {
            return super.recuperaValuesEnum(item);
        }
    }
}
