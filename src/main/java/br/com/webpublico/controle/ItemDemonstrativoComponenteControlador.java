package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.negocios.contabil.execucao.NotaExplicativaFacade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 30/01/14
 * Time: 10:53
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "itemDemonstrativoComponenteControlador")
@ViewScoped
public class ItemDemonstrativoComponenteControlador implements Serializable {
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private NotaExplicativaFacade notaExplicativaFacade;
    private List<ItemDemonstrativoComponente> itens;
    private List<ItemDemonstrativoComponente> itensSelecionados;
    private String nomeRelatorio;
    private TipoRelatorioItemDemonstrativo tipoRelatorioItemDemonstrativo;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private Exercicio exercicio;

    public ItemDemonstrativoComponenteControlador() {
    }

    public String getNomeRelatorio() {
        return nomeRelatorio;
    }

    public void setNomeRelatorio(String nomeRelatorio) {
        this.nomeRelatorio = nomeRelatorio;
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TipoRelatorioItemDemonstrativo getTipoRelatorioItemDemonstrativo() {
        return tipoRelatorioItemDemonstrativo;
    }

    public void setTipoRelatorioItemDemonstrativo(TipoRelatorioItemDemonstrativo tipoRelatorioItemDemonstrativo) {
        this.tipoRelatorioItemDemonstrativo = tipoRelatorioItemDemonstrativo;
    }

    public void novo(String nomeRelatorio, TipoRelatorioItemDemonstrativo tipoRelatorioItemDemonstrativo, List<ItemDemonstrativoComponente> itensSelecionados, Exercicio exercicio) {
        this.nomeRelatorio = nomeRelatorio;
        this.tipoRelatorioItemDemonstrativo = tipoRelatorioItemDemonstrativo;
        this.itensSelecionados = itensSelecionados;

        if (exercicio == null) {
            this.exercicio = sistemaControlador.getExercicioCorrente();
        } else {
            this.exercicio = exercicio;
        }

        List<ItemDemonstrativo> itensDemonstrativos = itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(this.exercicio, "", this.nomeRelatorio, this.tipoRelatorioItemDemonstrativo);

        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            ItemDemonstrativoComponente itemDemonstrativoComponente = new ItemDemonstrativoComponente(itemDemonstrativo);
            itemDemonstrativoComponente.setNumeroNota(notaExplicativaFacade.recuperarNumeroNota(itemDemonstrativo));
            Util.adicionarObjetoEmLista(itens, itemDemonstrativoComponente);
            itensSelecionados = Util.adicionarObjetoEmLista(itensSelecionados, itemDemonstrativoComponente);
        }
    }

    public void adicionarTodosItens() {
        itensSelecionados.addAll(itens);
    }

    public void removerTodosItens() {
        itensSelecionados.removeAll(itens);
    }

    public void adicionarItem(ItemDemonstrativoComponente item) {
        itensSelecionados.add(item);
    }

    public void removerItem(ItemDemonstrativoComponente item) {
        itensSelecionados.remove(item);
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }
}
