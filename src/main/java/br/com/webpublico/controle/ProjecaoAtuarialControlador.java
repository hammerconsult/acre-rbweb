/*
 * Codigo gerado automaticamente em Thu Jul 05 09:35:03 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemProjecaoAtuarial;
import br.com.webpublico.entidades.ProjecaoAtuarial;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.ProjecaoAtuarialFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-projecao-atuarial", pattern = "/projecao-atuarial/novo/", viewId = "/faces/financeiro/projecaoatuarial/edita.xhtml"),
    @URLMapping(id = "editar-projecao-atuarial", pattern = "/projecao-atuarial/editar/#{projecaoAtuarialControlador.id}/", viewId = "/faces/financeiro/projecaoatuarial/edita.xhtml"),
    @URLMapping(id = "ver-projecao-atuarial", pattern = "/projecao-atuarial/ver/#{projecaoAtuarialControlador.id}/", viewId = "/faces/financeiro/projecaoatuarial/visualizar.xhtml"),
    @URLMapping(id = "listar-projecao-atuarial", pattern = "/projecao-atuarial/listar/", viewId = "/faces/financeiro/projecaoatuarial/lista.xhtml")
})
public class ProjecaoAtuarialControlador extends PrettyControlador<ProjecaoAtuarial> implements Serializable, CRUD {

    @EJB
    private ProjecaoAtuarialFacade projecaoAtuarialFacade;
    private ItemProjecaoAtuarial itemSelecionado;
    private Integer exercicio;
    private Exercicio exercicioProjecao;
    private Boolean exercicioValido;
    @EJB
    private ExercicioFacade exercicioFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    @Override
    public AbstractFacade getFacede() {
        return projecaoAtuarialFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/projecao-atuarial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return ((ProjecaoAtuarial) selecionado).getId();
    }

    @Override
    @URLAction(mappingId = "ver-projecao-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        exercicioValido = true;
    }

    @Override
    @URLAction(mappingId = "editar-projecao-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        itemSelecionado = new ItemProjecaoAtuarial();
        exercicio = null;
    }

    @Override
    @URLAction(mappingId = "novo-projecao-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        itemSelecionado = new ItemProjecaoAtuarial();
        selecionado.setItemProjecaoAtuarials(new ArrayList<ItemProjecaoAtuarial>());
        selecionado.setSaldoAnterior(BigDecimal.ZERO);
        exercicio = null;
    }

    public void salvar() {
        ProjecaoAtuarial pa = ((ProjecaoAtuarial) selecionado);
        if (Util.validaCampos(selecionado)) {
            if (operacao.equals(Operacoes.NOVO)) {
                try {
                    projecaoAtuarialFacade.salvarNovo(pa);
                    FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
                    redireciona();
                } catch (Exception ex) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
                }
            } else {
                try {
                    projecaoAtuarialFacade.salvar(pa);
                    FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro alterado com sucesso.");
                    redireciona();
                } catch (Exception ex) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
                }
            }
        }
    }

    private void validarProjecao() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Exercício deve ser informado.");
        } else {
            for (ItemProjecaoAtuarial itemProjecaoAtuarial : selecionado.getItemProjecaoAtuarials()) {
                if (exercicio.equals(itemProjecaoAtuarial.getExercicio()) && !itemSelecionado.equals(itemProjecaoAtuarial)) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada(" Já existe uma projeção para o exercício de " + itemProjecaoAtuarial.getExercicio());
                }
            }
        }
        if (itemSelecionado.getReceitasPrevidenciarias().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Receita Previdenciária deve ser maior que zero (0).");
        }
        if (itemSelecionado.getDespesasPrevidenciarias().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Despesa Previdenciária deve ser maior que zero (0).");
        }
        ve.lancarException();
    }

    public void adicionarProjecao() {
        try {
            validarProjecao();
            itemSelecionado.setProjecaoAtuarial(selecionado);
            itemSelecionado.setResultadoPrevidenciario(itemSelecionado.getReceitasPrevidenciarias().subtract(itemSelecionado.getDespesasPrevidenciarias()));
            itemSelecionado.setExercicio(exercicio);
            Util.adicionarObjetoEmLista(selecionado.getItemProjecaoAtuarials(), itemSelecionado);
            calcularSaldoAnterior();
            itemSelecionado = new ItemProjecaoAtuarial();
            exercicio = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void alterarProjecao(ItemProjecaoAtuarial item) {
        itemSelecionado = item;
        exercicio = item.getExercicio();
    }

    public void removerProjecao(ItemProjecaoAtuarial item) {
        selecionado.getItemProjecaoAtuarials().remove(item);
        calcularSaldoAnterior();
    }

    public void calcularSaldoAnterior() {
        Collections.sort(selecionado.getItemProjecaoAtuarials());
        BigDecimal saldoAnterior = selecionado.getSaldoAnterior().compareTo(BigDecimal.ZERO) < 0 ? selecionado.getSaldoAnterior().multiply(new BigDecimal("-1")) : selecionado.getSaldoAnterior();
        for (ItemProjecaoAtuarial itemProjecaoAtuarial : selecionado.getItemProjecaoAtuarials()) {
            itemProjecaoAtuarial.setSaldoPrevidenciario(itemProjecaoAtuarial.getResultadoPrevidenciario().add(saldoAnterior));
            saldoAnterior = itemProjecaoAtuarial.getSaldoPrevidenciario();
        }
    }

    public BigDecimal getTotalReceitasPrev() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ItemProjecaoAtuarial item : selecionado.getItemProjecaoAtuarials()) {
            total = total.add(item.getReceitasPrevidenciarias());
        }
        return total;
    }

    public BigDecimal getTotalDespesaPrev() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ItemProjecaoAtuarial item : selecionado.getItemProjecaoAtuarials()) {
            total = total.add(item.getDespesasPrevidenciarias());
        }
        return total;
    }

    public BigDecimal getTotalResultadoPrev() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ItemProjecaoAtuarial item : selecionado.getItemProjecaoAtuarials()) {
            total = total.add(item.getResultadoPrevidenciario());
        }
        return total;
    }

    public BigDecimal getTotalSaldoPrev() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ItemProjecaoAtuarial item : selecionado.getItemProjecaoAtuarials()) {
            total = total.add(item.getSaldoPrevidenciario());
        }
        return total;
    }

    public void validaExercicio(FacesContext context, UIComponent component, Object value) {
        if (value.toString().trim().isEmpty()) {
            return;
        }
        Integer ano = Integer.parseInt(value.toString());
        FacesMessage message = new FacesMessage();
        List<Exercicio> exer = projecaoAtuarialFacade.getExercicioFacade().lista();
        Exercicio ex = null;
        for (Exercicio e : exer) {
            if (ano.compareTo(e.getAno()) == 0) {
                ex = e;
                ((ProjecaoAtuarial) selecionado).setExercicio(ex);
            }
        }
        if (ex == null) {
            ((ProjecaoAtuarial) selecionado).setExercicio(null);
            message.setDetail("Exercício Inválido!");
            message.setSummary("Operação não Permitida!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void duplicarProjecao() {

        ProjecaoAtuarial novaProjecao = clonarProjecao();
        List<ItemProjecaoAtuarial> itensNovaProjecao = Lists.newArrayList();

        novaProjecao.setExercicio(exercicioProjecao);
        novaProjecao.setId(null);

        for (ItemProjecaoAtuarial item : selecionado.getItemProjecaoAtuarials()) {
            ItemProjecaoAtuarial itemClonado = clonarItemProjecao(item);
            atribuirNovaProjecaoAtuarial(novaProjecao, itensNovaProjecao, itemClonado);
        }

        novaProjecao.setItemProjecaoAtuarials(itensNovaProjecao);

        try {
            projecaoAtuarialFacade.salvarNovo(novaProjecao);
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
            redireciona();
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
            redireciona();
        }

    }

    public void definirExercicioValido() {
        Matcher matcher = definirPadraoExercicio();

        if (!matcher.find()) {
            exercicioValido = false;
            FacesUtil.addOperacaoNaoPermitida("Por favor informe um exercício válido! ");
        } else
            exercicioValido = true;
    }

    private Matcher definirPadraoExercicio() {
        Pattern pattern = Pattern.compile("\\d{4,6}");
        return pattern.matcher(exercicioProjecao.getAno().toString());
    }


    private void atribuirNovaProjecaoAtuarial(ProjecaoAtuarial novaProjecao, List<ItemProjecaoAtuarial> itens, ItemProjecaoAtuarial novoItem) {
        novoItem.setId(null);
        novoItem.setProjecaoAtuarial(novaProjecao);
        itens.add(novoItem);
    }

    private ItemProjecaoAtuarial clonarItemProjecao(ItemProjecaoAtuarial item) {
        return (ItemProjecaoAtuarial) Util.clonarObjeto(item);
    }


    private ProjecaoAtuarial clonarProjecao() {
        return (ProjecaoAtuarial) Util.clonarObjeto(selecionado);
    }

    public List<SelectItem> exercicios() {
        List<SelectItem> retorno = Lists.newArrayList();

        for (Exercicio obj : exercicioFacade.lista()) {
            retorno.add(new SelectItem(obj, obj.getAno().toString()));
        }

        return retorno;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ProjecaoAtuarialControlador() {
        metadata = new EntidadeMetaData(ProjecaoAtuarial.class);
    }

    public ProjecaoAtuarialFacade getFacade() {
        return projecaoAtuarialFacade;
    }

    public ItemProjecaoAtuarial getItemSelecionado() {
        return itemSelecionado;
    }

    public void setItemSelecionado(ItemProjecaoAtuarial itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public Exercicio getExercicioProjecao() {
        return exercicioProjecao;
    }

    public void setExercicioProjecao(Exercicio exercicioProjecao) {
        this.exercicioProjecao = exercicioProjecao;
    }

    public Boolean getExercicioValido() {
        return exercicioValido;
    }

    public void setExercicioValido(Boolean exercicioValido) {
        this.exercicioValido = exercicioValido;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }
}
