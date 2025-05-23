package br.com.webpublico.controle.rh.configuracao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.rh.configuracao.ReajusteMediaAposentadoria;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.rh.configuracao.ReajusteMediaAposentadoriaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import br.com.webpublico.exception.ValidacaoException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "reajusteMediaAposentadoriaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reajuste-media-aposentadoria", pattern = "/reajuste-media-aposentadoria/novo/", viewId = "/faces/rh/configuracao/reajuste-media-aposentadoria/edita.xhtml"),
    @URLMapping(id = "editar-reajuste-media-aposentadoria", pattern = "/reajuste-media-aposentadoria/editar/#{reajusteMediaAposentadoriaControlador.id}/", viewId = "/faces/rh/configuracao/reajuste-media-aposentadoria/edita.xhtml"),
    @URLMapping(id = "listar-reajuste-media-aposentadoria", pattern = "/reajuste-media-aposentadoria/listar/", viewId = "/faces/rh/configuracao/reajuste-media-aposentadoria/lista.xhtml"),
    @URLMapping(id = "ver-reajuste-media-aposentadoria", pattern = "/reajuste-media-aposentadoria/ver/#{reajusteMediaAposentadoriaControlador.id}/", viewId = "/faces/rh/configuracao/reajuste-media-aposentadoria/edita.xhtml"),
})
public class ReajusteMediaAposentadoriaControlador extends PrettyControlador<ReajusteMediaAposentadoria> implements Serializable, CRUD {

    @EJB
    private ReajusteMediaAposentadoriaFacade reajusteMediaAposentadoriaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    List<Exercicio> exercicios = Lists.newArrayList();


    public ReajusteMediaAposentadoriaControlador() {
        super(ReajusteMediaAposentadoria.class);
    }


    @Override
    public AbstractFacade getFacede() {
        return reajusteMediaAposentadoriaFacade;
    }

    @URLAction(mappingId = "novo-reajuste-media-aposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        exercicios = Lists.newArrayList();
        DateTime agora = DateTime.now();
        selecionado.setExercicio(exercicioFacade.getExercicioPorAno(agora.getYear()));
        selecionado.setExercicioReferencia(exercicioFacade.getExercicioPorAno(agora.minusYears(1).getYear()));
        selecionado.setMes(Mes.getMesToInt(agora.getMonthOfYear()));
        carregarExercicios();
    }

    private void carregarExercicios() {
        exercicios = exercicioFacade.buscarExerciciosAnterioresAnoAtual(DateTime.now().plusYears(2).getYear());
    }

    @URLAction(mappingId = "editar-reajuste-media-aposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-reajuste-media-aposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }


    @Override
    public String getCaminhoPadrao() {
        return "/reajuste-media-aposentadoria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> recuperarAnos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (exercicios != null && exercicios.isEmpty()) {
            carregarExercicios();
        }
        for (Exercicio e : exercicios) {
            toReturn.add(new SelectItem(e, "" + e));
        }
        return toReturn;
    }

    public List<SelectItem> recuperarMeses() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Mes mes : Mes.values()) {
            toReturn.add(new SelectItem(mes, mes.getDescricao()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterExercicio() {
        return new ConverterAutoComplete(Exercicio.class, exercicioFacade);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarReajuste();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar Reajuste de Média de Aposentadoria {}", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano do Reajuste deve ser informado.");
        }
        if (selecionado.getExercicioReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano de Referência deve ser informado.");
        }
        if (selecionado.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (selecionado.getValorReajuste() == null || selecionado.getValorReajuste().equals(BigDecimal.ZERO)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Percentual deve ser informado.");
        }
        ve.lancarException();
    }

    public void validarReajuste() {
        ValidacaoException ve = new ValidacaoException();
        List<ReajusteMediaAposentadoria> reajustes = reajusteMediaAposentadoriaFacade.buscarReajustePorAnoReajusteAnoReferenciEMes(selecionado.getExercicio(), selecionado.getExercicioReferencia(), selecionado.getMes());
        if (reajustes != null && !reajustes.isEmpty() && (selecionado.getId() == null || (!reajustes.get(0).getId().equals(selecionado.getId())))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe Percentual registrado para esse Ano de Reajuste, Ano de Referência e Mês.");
        }
        ve.lancarException();
    }
}
