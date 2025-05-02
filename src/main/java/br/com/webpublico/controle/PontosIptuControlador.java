/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.enums.TipoPontoIPTU;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtributoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PontosFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
@ManagedBean(name = "pontosIptuControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPontosIptu", pattern = "/pontos-iptu/novo/", viewId = "/faces/tributario/iptu/tabeladepontos/novo.xhtml"),
    @URLMapping(id = "listarPontosIptu", pattern = "/pontos-iptu/listar/", viewId = "/faces/tributario/iptu/tabeladepontos/principal.xhtml"),
    @URLMapping(id = "verPontosIptu", pattern = "/pontos-iptu/editar/#{pontosIptuControlador.id}/", viewId = "/faces/tributario/iptu/tabeladepontos/detalhes.xhtml"),
    @URLMapping(id = "duplicarPontosIptu", pattern = "/pontos-iptu/duplicar/#{pontosIptuControlador.id}/", viewId = "/faces/tributario/iptu/tabeladepontos/detalhes.xhtml")
})
public class PontosIptuControlador extends PrettyControlador<Pontuacao> implements Serializable, CRUD {

    @EJB
    private AtributoFacade atributoFacade;
    @EJB
    private PontosFacade pontosFacade;
    private List<String> atributosSelecionados;
    private List<String> atributosLote;
    private List<String> atributosConstrucao;
    private List<String> atributosImobiliario;
    private ItemPontuacao itemPontuacao;
    private Exercicio exercicio;
    private String identificacao;
    private List<Atributo> atributos;
    private boolean duplicarPontuacao ;
    private Exercicio exercicioOrigem;
    private Exercicio exercicioDestino;

    public PontosIptuControlador() {
        super(Pontuacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return pontosFacade;
    }

    public ClasseDoAtributo getCADASTRO_IMOBILIARIO() {
        return ClasseDoAtributo.CADASTRO_IMOBILIARIO;
    }

    public ClasseDoAtributo getCONSTRUCAO() {
        return ClasseDoAtributo.CONSTRUCAO;
    }

    public ClasseDoAtributo getLOTE() {
        return ClasseDoAtributo.LOTE;
    }

    public boolean podeDuplicar(){
        return duplicarPontuacao;
    }

    public Exercicio getExercicioDestino() {
        return exercicioDestino;
    }

    public void setExercicioDestino(Exercicio exercicioDestino) {
        this.exercicioDestino = exercicioDestino;
    }

    public Exercicio getExercicioOrigem() {
        return exercicioOrigem;
    }

    public void setExercicioOrigem(Exercicio exercicioOrigem) {
        this.exercicioOrigem = exercicioOrigem;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/pontos-iptu/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoPontosIptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        duplicarPontuacao = false;
        atributosLote = new ArrayList<String>();
        atributosConstrucao = new ArrayList<String>();
        atributosImobiliario = new ArrayList<String>();
        atributosSelecionados = new ArrayList<String>();
        exercicio = new Exercicio();
        identificacao = "";
    }

    @Override
    @URLAction(mappingId = "verPontosIptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.editar();
        duplicarPontuacao = false;
        List<Atributo> atributos = pontosFacade.quantidadeAtributos(selecionado);
        selecionado.setAtributos(atributos);
        itemPontuacao = new ItemPontuacao();
        identificacao = selecionado.getIdentificacao();
        exercicio = selecionado.getExercicio();
    }

    @URLAction(mappingId = "listarPontosIptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        exercicioOrigem = null;
        exercicioDestino = null;
    }

    public void novaPontuacao() throws ExcecaoNegocioGenerica {
        try {
            Pontuacao novaPontuacao = new Pontuacao();
            novaPontuacao.setIdentificacao(identificacao);
            novaPontuacao.setExercicio(exercicio);
            novaPontuacao.setTipoPontoIPTU(selecionado.getTipoPontoIPTU());
            atributosSelecionados.addAll(atributosLote);
            atributosSelecionados.addAll(atributosConstrucao);
            atributosSelecionados.addAll(atributosImobiliario);
            if (valida(novaPontuacao)) {
                novaPontuacao.setAtributos(new ArrayList<Atributo>());
                for (String idAtributoComoString : atributosSelecionados) {
                    novaPontuacao.getAtributos().add(atributoFacade.recuperar(Long.parseLong(idAtributoComoString)));
                }
                pontosFacade.novaPontuacao(novaPontuacao);
                FacesUtil.addInfo("Salvo com sucesso", "O Registro foi salvo com sucesso");
                atributosSelecionados = new ArrayList<>();
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao()+"listar/");
            }
            //FacesUtil.redirecionamentoInterno(getCaminhoPadrao()+"listar/");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addError("Impossível continuar", e.getMessage());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao()+"listar/");
        }
    }

    public boolean valida(Pontuacao novaPontuacao) {
        boolean valida = true;
        if (!pontosFacade.consultaIdentificacao(novaPontuacao)) {
            FacesUtil.addError("Atenção!", " O valor do campo identificação já existe, insira outro");
            valida = false;
        }
        if (atributosSelecionados.size() > 3) {
            valida = false;
            FacesUtil.addError("Atenção!", " Selecione no maximo três atributos");
        }
        return valida;
    }


    @URLAction(mappingId = "duplicarPontosIptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void duplicar() {
        super.editar();
        duplicarPontuacao = true;
        atributos = pontosFacade.quantidadeAtributos(selecionado);
        selecionado.setAtributos(atributos);
        itemPontuacao = new ItemPontuacao();
        identificacao = "Copia_" + selecionado.getIdentificacao();
        exercicio = selecionado.getExercicio();
    }

    public void duplicaPonto() {
        pontosFacade.duplicar(selecionado, identificacao, exercicio);
        FacesUtil.redirecionamentoInterno("/pontos-iptu/listar/");
        FacesUtil.addInfo("Salvo com sucesso", "O registro salvo com sucesso");
    }

    public int getQuantidadeAtributos() {
        if (selecionado != null && selecionado.getId() != null) {
            List<Atributo> atributos = pontosFacade.quantidadeAtributos(selecionado);
            selecionado.setAtributos(atributos);
            return atributos.size();
        }
        return 0;
    }

    public List<ItemPontuacao> getItensPontuacao() {
        return pontosFacade.listaItensPontuacao(selecionado);
    }

    public ItemPontuacao recuperarItemUm(ValorPossivel vp) {
        for (ItemPontuacao ip : selecionado.getItens()) {
            List<ValorPossivel> listVP = pontosFacade.listaValoresPossiveisPorItem(ip);
            if (listVP.contains(vp)) {
                return ip;
            }
        }
        ItemPontuacao item = new ItemPontuacao();
        item.setPontuacao(selecionado);
        item.getValores().add(vp);
        selecionado.getItens().add(item);
        return item;
    }

    public ItemPontuacao recuperarItemDois(ValorPossivel vp1, ValorPossivel vp2) {
        for (ItemPontuacao ip : selecionado.getItens()) {
            List<ValorPossivel> listVP = pontosFacade.listaValoresPossiveisPorItem(ip);
            if (listVP.contains(vp1)) {
                if (listVP.contains(vp2)) {
                    return ip;
                }
            }
        }
        return new ItemPontuacao();
    }

    public ItemPontuacao recuperarItemTres(ValorPossivel vp1, ValorPossivel vp2, ValorPossivel vp3) {
        for (ItemPontuacao ip : selecionado.getItens()) {
            List<ValorPossivel> listVP = pontosFacade.listaValoresPossiveisPorItem(ip);
            if (listVP.contains(vp1)) {
                if (listVP.contains(vp2)) {
                    if (listVP.contains(vp3)) {
                        return ip;
                    }
                }
            }
        }
        return new ItemPontuacao();
    }

    public void setaItensPontucacao(ItemPontuacao item) {
        int x = selecionado.getItens().indexOf(item);
        if (x >= 0) {
            selecionado.getItens().set(x, item);
        } else {
            selecionado.getItens().add(item);
        }
    }

    public void alterar() {
        if (pontosFacade.consultaIdentificacao(selecionado)) {
            selecionado.setIdentificacao(identificacao);
            selecionado.setExercicio(exercicio);
            pontosFacade.salvar(selecionado);
            FacesUtil.addInfo("Salvo com sucesso", "O registro salvo com sucesso");
            FacesUtil.redirecionamentoInterno("/pontos-iptu/listar/");
        } else {
            FacesUtil.addWarn("Impossível continuar", "Identificação já existente");
        }
    }

    public ItemPontuacao getItemPontuacao() {
        return itemPontuacao;
    }

    public void setItemPontuacao(ItemPontuacao itemPontuacao) {
        this.itemPontuacao = itemPontuacao;
    }

    public void remover(Pontuacao pontuacao) {
        pontosFacade.remover(pontuacao);
        FacesUtil.atualizarComponente("tabelaPontos");
        FacesUtil.addInfo("Removido com Sucesso","");
    }

    public List<String> getAtributosConstrucao() {
        return atributosConstrucao;
    }

    public void setAtributosConstrucao(List<String> atributosConstrucao) {
        this.atributosConstrucao = atributosConstrucao;
    }

    public List<String> getAtributosImobiliario() {
        return atributosImobiliario;
    }

    public void setAtributosImobiliario(List<String> atributosImobiliario) {
        this.atributosImobiliario = atributosImobiliario;
    }

    public List<String> getAtributosLote() {
        return atributosLote;
    }

    public void setAtributosLote(List<String> atributosLote) {
        this.atributosLote = atributosLote;
    }

    public List<SelectItem> getTipoPontos() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, " "));
        for (TipoPontoIPTU e : TipoPontoIPTU.values()) {
            lista.add(new SelectItem(e, e.getDescricao()));
        }
        return lista;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public void validaIdentificador(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        String identificador = value.toString();
        if (identificador.length() > 0) {
            if (!Character.isLetter(identificador.charAt(0))) {
                message.setDetail("A palavra deve ser iniciada com uma letra");
                message.setSummary("A palavra deve ser iniciada com uma letra");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
            if (identificador.contains(" ")) {
                message.setDetail("A palavra não pode conter espaços em branco");
                message.setSummary("A palavra não pode conter espaços em branco");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public boolean validaExericiosDuplicacao() {
        boolean retorno = true;
        if (exercicioOrigem == null) {
            FacesUtil.addError("Atenção!", "Informe o Exercício de Origem!");
            retorno = false;
        }
        if (exercicioDestino == null) {
            FacesUtil.addError("Atenção!", "Informe o Exercício de Destino!");
            retorno = false;
        }
        return retorno;
    }

    public void duplicarDados() {
        if (validaExericiosDuplicacao()) {
            List<Pontuacao> listaPontuacaoOrigem = pontosFacade.listaPontuacoesPorExercicio(exercicioOrigem);
            List<Pontuacao> listaPontuacaoDestino = pontosFacade.listaPontuacoesPorExercicio(exercicioDestino);

            if (!listaPontuacaoOrigem.isEmpty()) {
                if (listaPontuacaoDestino.isEmpty()) {
                    for (Pontuacao pontuacao : listaPontuacaoOrigem) {
                        pontosFacade.duplicar(pontuacao, pontuacao.getIdentificacao(), exercicioDestino);
                    }
                    FacesUtil.addInfo("Sucesso!", "Pontuações do exercício " + exercicioDestino.getAno() + " gerados com sucesso!");
                    FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
                } else {
                    FacesUtil.addError("Pontuações já encontradas para o exercício de destino", exercicioDestino.getAno() + "");
                }
            } else {
                FacesUtil.addError("Pontuações não encontradas para o exercício de origem", exercicioOrigem.getAno() + "");
            }
        }
    }
}
