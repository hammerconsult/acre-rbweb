package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.RelatorioPesquisaGenerico;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ComponentePesquisaGenericoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.RelatorioPesquisaGenericoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Tabelavel;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 17/07/13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "relatorioPesquisaGenericoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-configuracao-relatorio-pesquisa-generico", pattern = "/configuracao-relatorio/novo/", viewId = "/faces/admin/configuracao-relatorio-pesquisa-generico/edita.xhtml"),
        @URLMapping(id = "editar-configuracao-relatorio-pesquisa-generico", pattern = "/configuracao-relatorio/editar/#{relatorioPesquisaGenericoControlador.id}/", viewId = "/faces/admin/configuracao-relatorio-pesquisa-generico/edita.xhtml"),
        @URLMapping(id = "ver-configuracao-relatorio-pesquisa-generico", pattern = "/configuracao-relatorio/ver/#{relatorioPesquisaGenericoControlador.id}/", viewId = "/faces/admin/configuracao-relatorio-pesquisa-generico/visualizar.xhtml"),
        @URLMapping(id = "listar-configuracao-relatorio-pesquisa-generico", pattern = "/configuracao-relatorio/listar/", viewId = "/faces/admin/configuracao-relatorio-pesquisa-generico/lista.xhtml")
})
public class RelatorioPesquisaGenericoControlador extends PrettyControlador<RelatorioPesquisaGenerico> implements Serializable, CRUD {
    @EJB
    private RelatorioPesquisaGenericoFacade relatorioPesquisaGenericoFacade;
    @EJB
    private ComponentePesquisaGenericoFacade componentePesquisaGenericoFacade;
    @ManagedProperty(name = "relatorioGenericoControlador", value = "#{relatorioGenericoControlador}")
    private RelatorioGenericoControlador relatorioGenericoControlador;


    public RelatorioPesquisaGenericoControlador() {
        super(RelatorioPesquisaGenerico.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-relatorio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return relatorioPesquisaGenericoFacade;
    }

    @Override
    @URLAction(mappingId = "novo-configuracao-relatorio-pesquisa-generico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-configuracao-relatorio-pesquisa-generico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        selecionado = relatorioPesquisaGenericoFacade.recuperar(super.getId());
    }

    @Override
    @URLAction(mappingId = "editar-configuracao-relatorio-pesquisa-generico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        selecionado = relatorioPesquisaGenericoFacade.recuperar(super.getId());
        selecionado.recuperarEntidadeMetaData();
    }

    @Override
    public void salvar() {
        if (selecionado.getClasse() == null) {
            FacesUtil.addError("Erro ao salvar!", "Selecione uma entidade para configurar o Relatório.");
            return;
        }
        if (Util.validaCampos(selecionado)) {
            try {
                if (selecionado.getTarget() == null) {
                    throw new ExcecaoNegocioGenerica("É obrigatório que adicione ao menos 1 (UM) campo na Lista de Campos Selecionados.");
                } else {
                    if (selecionado.getTarget().isEmpty()) {
                        throw new ExcecaoNegocioGenerica("É obrigatório que adicione ao menos 1 (UM) campo na Lista de Campos Selecionados.");
                    }
                }

                selecionado.atribuiAtributoDoRelatorioProRelatorio();
                List<String> mensagens = selecionado.validarTamanhoCondicaoLabel();
                if (mensagens != null && !mensagens.isEmpty()) {
                    for (String mensagem : mensagens) {
                        FacesUtil.addError(RelatorioPesquisaGenerico.SUMMARY, mensagem);
                    }
                }

                RelatorioPesquisaGenerico relatorioPesquisaGenerico = relatorioPesquisaGenericoFacade.validaRelatorioPadrao(selecionado);

                if (relatorioPesquisaGenerico == null || !selecionado.getPadrao()) {
                    if (operacao == Operacoes.NOVO) {
                        selecionado.setCodigo(relatorioPesquisaGenericoFacade.retornaUltimoCodigoPorClasse(selecionado.getClasse()));
                        getFacede().salvarNovo(selecionado);
                        FacesUtil.addInfo("Salvo com sucesso.", "A configuação " + selecionado.getNomeRelatorio() + " - " + selecionado.getTipoRelatorio() + " foi salvo com sucesso!");
                    } else {
                        getFacede().salvar(selecionado);
                        FacesUtil.addInfo("Alerado com sucesso.", "A configuação " + selecionado.getNomeRelatorio() + " - " + selecionado.getTipoRelatorio() + " foi salvo com sucesso!");
                    }
                    redireciona();
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Já existe uma Configuração de Relátorio <b>" + relatorioPesquisaGenerico.getCodigo() + " - " + relatorioPesquisaGenerico.getNomeRelatorio() + "</b>, para a Entidade <b> " + relatorioPesquisaGenerico.getClasse() + "</b>, Tipo de Relatório <b> " + relatorioPesquisaGenerico.getTipoRelatorio() + "</b> definido como <b> PADRÃO </b>.");
                }


            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar!", e.getMessage()));
            }
        }
    }


    public RelatorioGenericoControlador getRelatorioGenericoControlador() {
        return relatorioGenericoControlador;
    }

    public void setRelatorioGenericoControlador(RelatorioGenericoControlador relatorioGenericoControlador) {
        this.relatorioGenericoControlador = relatorioGenericoControlador;
    }

    public List<SelectItem> getEntidadesDoProjeto() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (Class classe : relatorioPesquisaGenericoFacade.getAllClass()) {
            retorno.add(new SelectItem(classe, Persistencia.getNomeDaClasse(classe)));
        }
        Collections.sort(retorno, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                SelectItem s1 = (SelectItem) o1;
                SelectItem s2 = (SelectItem) o2;

                return s1.getLabel().compareTo(s2.getLabel());
            }
        });
        return retorno;
    }

    public void setaSelecionado(RelatorioPesquisaGenerico rpg) {
        this.selecionado = rpg;
    }

    public void imprimirRelatorio() {


        try {
            Class classe = selecionado.getClasse(selecionado.getClasse());
            Object objeto = (Object) classe.newInstance();

            String sql = "select obj  from " + classe.getSimpleName() + " obj order by obj." + Persistencia.getFieldId(classe).getName() + " desc";

            int quantidade = 9;
            if (selecionado.getTipoRelatorio().equals(TipoRelatorio.UNICO_REGISTRO)) {
                quantidade = 1;
            }

            List lista = componentePesquisaGenericoFacade.filtar(sql, objeto, 0, quantidade);
            if (selecionado.getAgrupador()) {
                relatorioGenericoControlador.imprimirRelatorioAgrupador(lista, selecionado);
            } else {
                if (selecionado.getTipoRelatorio().equals(TipoRelatorio.UNICO_REGISTRO)) {
                    selecionado.setObjetoSelecionadoRelatorio(lista.get(0));
                }
                relatorioGenericoControlador.imprimirRelatorio(lista, selecionado, false);
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar Pesquisar!", ex.getMessage()));
        }
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoFolha tipoFolha : TipoFolha.values()) {
            retorno.add(new SelectItem(tipoFolha, tipoFolha.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getListaTipoRelatorio() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoRelatorio obj : TipoRelatorio.values()) {
            retorno.add(new SelectItem(obj, obj.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getListaTipoFonteRelatorio() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoFonteRelatorio obj : TipoFonteRelatorio.values()) {
            retorno.add(new SelectItem(obj, obj.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getListaTipoAlinhamento() {
        List<SelectItem> retorno = new ArrayList<>();
        for (Tabelavel.ALINHAMENTO alinhamento : Tabelavel.ALINHAMENTO.values()) {
            retorno.add(new SelectItem(alinhamento, alinhamento.name()));
        }
        return retorno;
    }

    public List<SelectItem> getListaTipoFuncaoAgrupador() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoFuncaoAgrupador alinhamento : TipoFuncaoAgrupador.values()) {
            retorno.add(new SelectItem(alinhamento, alinhamento.name()));
        }
        return retorno;
    }

}
