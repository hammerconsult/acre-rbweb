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
import br.com.webpublico.util.DataUtil;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "reajusteMediaAposentadoriaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reajuste-media-aposentadoria", pattern = "/reajuste-media-aposentadoria/novo/", viewId = "/faces/rh/configuracao/reajuste-media-aposentadoria/edita.xhtml"),
    @URLMapping(id = "editar-reajuste-media-aposentadoria", pattern = "/reajuste-media-aposentadoria/editar/#{reajusteMediaAposentadoriaControlador.id}/", viewId = "/faces/rh/configuracao/reajuste-media-aposentadoria/edita.xhtml"),
    @URLMapping(id = "listar-reajuste-media-aposentadoria", pattern = "/reajuste-media-aposentadoria/listar/", viewId = "/faces/rh/configuracao/reajuste-media-aposentadoria/lista.xhtml"),
    @URLMapping(id = "ver-reajuste-media-aposentadoria", pattern = "/reajuste-media-aposentadoria/ver/#{reajusteMediaAposentadoriaControlador.id}/", viewId = "/faces/rh/configuracao/reajuste-media-aposentadoria/visualizar.xhtml"),
})
public class ReajusteMediaAposentadoriaControlador extends PrettyControlador<ReajusteMediaAposentadoria> implements Serializable, CRUD {

    @EJB
    private ReajusteMediaAposentadoriaFacade reajusteMediaAposentadoriaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    List<Exercicio> exercicios = Lists.newArrayList();
    private Integer mesInicioVigencia;
    private Integer anoInicioVigencia;
    private Integer mesFinalVigencia;
    private Integer anoFinalVigencia;


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
        recuperarMesEAno();
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

    public Integer getMesInicioVigencia() {
        return mesInicioVigencia;
    }

    public void setMesInicioVigencia(Integer mesInicioVigencia) {
        this.mesInicioVigencia = mesInicioVigencia;
    }

    public Integer getAnoInicioVigencia() {
        return anoInicioVigencia;
    }

    public void setAnoInicioVigencia(Integer anoInicioVigencia) {
        this.anoInicioVigencia = anoInicioVigencia;
    }

    public Integer getMesFinalVigencia() {
        return mesFinalVigencia;
    }

    public void setMesFinalVigencia(Integer mesFinalVigencia) {
        this.mesFinalVigencia = mesFinalVigencia;
    }

    public Integer getAnoFinalVigencia() {
        return anoFinalVigencia;
    }

    public void setAnoFinalVigencia(Integer anoFinalVigencia) {
        this.anoFinalVigencia = anoFinalVigencia;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            atribuirVigencia();
            validarReajuste();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar Reajuste de Média de Aposentadoria {}", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void atribuirVigencia() {
        if (mesInicioVigencia != null && anoInicioVigencia != null) {
            selecionado.setInicioVigencia(DataUtil.primeiroDiaMes(mesInicioVigencia, anoInicioVigencia));
        }
        if (mesFinalVigencia != null && anoFinalVigencia != null) {
            selecionado.setFinalVigencia(DataUtil.getUltimoDiaMes(mesFinalVigencia, anoFinalVigencia));
        }
    }

    private void recuperarMesEAno() {
        if (selecionado.getInicioVigencia() != null) {
            LocalDate inicio = DataUtil.dateToLocalDate(selecionado.getInicioVigencia());
            mesInicioVigencia = inicio.getMonthValue();
            anoInicioVigencia = inicio.getYear();
        }
        if (selecionado.getFinalVigencia() != null) {
            LocalDate fim = DataUtil.dateToLocalDate(selecionado.getFinalVigencia());
            mesFinalVigencia = fim.getMonthValue();
            anoFinalVigencia = fim.getYear();
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
        if (mesInicioVigencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo mês do início de vigência deve ser informado.");
        } else if (mesInicioVigencia < 1 || mesInicioVigencia > 12) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo mês do início de vigência informado é inválido!");
        }
        if (anoInicioVigencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ano do início de vigência deve ser informado.");
        }
        if (mesFinalVigencia != null && (mesFinalVigencia < 1 || mesFinalVigencia > 12)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo mês do final de vigência informado é inválido!");
        }
        ve.lancarException();
    }

    public void validarReajuste() {
        ValidacaoException ve = new ValidacaoException();
        List<ReajusteMediaAposentadoria> reajustes = reajusteMediaAposentadoriaFacade.buscarReajustePorAnoReajusteAnoReferenciEMes(selecionado.getExercicio(), selecionado.getExercicioReferencia(), selecionado.getMes());
        if (reajustes != null && !reajustes.isEmpty()) {
            for (ReajusteMediaAposentadoria reajuste : reajustes) {
                if ((selecionado.getId() == null || !reajuste.getId().equals(selecionado.getId())) && isPeriodoConcomitante(reajuste)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe registro de percentual de reajuste para a vigência escolhida. Por favor, encerre a vigência atual para Ano de Reajuste, Ano de Referência e Mês selecionados.");
                    break;
                }
            }

        }
        ve.lancarException();
    }

    public boolean isPeriodoConcomitante(ReajusteMediaAposentadoria reajuste) {
        LocalDate inicioVigenciaAdicionado = DataUtil.dateToLocalDate(selecionado.getInicioVigencia());
        LocalDate finalVigenciaAdicionado = selecionado.getFinalVigencia() != null ? DataUtil.dateToLocalDate(selecionado.getFinalVigencia()) : null;

        LocalDate inicioVigencia = DataUtil.dateToLocalDate(reajuste.getInicioVigencia());
        LocalDate finalVigencia = reajuste.getFinalVigencia() != null ? DataUtil.dateToLocalDate(reajuste.getFinalVigencia()) : null;

        if ((inicioVigenciaAdicionado.compareTo(inicioVigencia) <= 0 && (finalVigenciaAdicionado == null || finalVigenciaAdicionado.compareTo(inicioVigencia) >= 0))
            || (inicioVigenciaAdicionado.isAfter(inicioVigencia) && (finalVigencia == null || inicioVigenciaAdicionado.compareTo(finalVigencia) <= 0))
            || (finalVigenciaAdicionado != null && finalVigenciaAdicionado.compareTo(inicioVigencia) >= 0 && (finalVigencia == null || (finalVigenciaAdicionado.isAfter(inicioVigencia) && finalVigenciaAdicionado.isBefore(finalVigencia))))) {
            return true;
        }
        return false;
    }
}
