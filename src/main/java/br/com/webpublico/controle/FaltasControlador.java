/*
 * Codigo gerado automaticamente em Wed Apr 04 18:03:10 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoFalta;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "faltasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoFaltas", pattern = "/faltas/novo/", viewId = "/faces/rh/administracaodepagamento/faltas/edita.xhtml"),
    @URLMapping(id = "editarFaltas", pattern = "/faltas/editar/#{faltasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/faltas/edita.xhtml"),
    @URLMapping(id = "listarFaltas", pattern = "/faltas/listar/", viewId = "/faces/rh/administracaodepagamento/faltas/lista.xhtml"),
    @URLMapping(id = "verFaltas", pattern = "/faltas/ver/#{faltasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/faltas/visualizar.xhtml")
})
public class FaltasControlador extends PrettyControlador<Faltas> implements Serializable, CRUD {

    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private CIDFacade cidFacade;
    private ConverterAutoComplete converterCid;
    @EJB
    private MedicoFacade medicoFacade;
    @EJB
    private JustificativaFaltasFacade justificativaFaltasFacade;
    private ConverterAutoComplete converterMedico;
    private List<Faltas> faltasList;
    private Integer diasJustificados;
    private String intervalosJustificados;

    public FaltasControlador() {
        super(Faltas.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return faltasFacade;
    }

    public List<ContratoFP> completaContrato(String s) {
        return contratoFPFacade.buscaContratoVigenteFiltrandoAtributosMatriculaFP(s.trim());
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public List<CID> completaCids(String parte) {
        return cidFacade.listaFiltrando(parte.trim(), "descricao", "codigoDaCid");
    }

    public List<SelectItem> getTiposFaltas() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoFalta object : TipoFalta.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterCid() {
        if (converterCid == null) {
            converterCid = new ConverterAutoComplete(CID.class, cidFacade);
        }
        return converterCid;
    }

    public List<Medico> completaMedico(String parte) {
        return medicoFacade.listaFiltrandoMedico(parte);
    }

    public Converter getConverterMedico() {
        if (converterMedico == null) {
            converterMedico = new ConverterAutoComplete(Medico.class, medicoFacade);
        }
        return converterMedico;
    }

    public Faltas getFaltas() {
        return (Faltas) selecionado;
    }

    public Integer getDiasJustificados() {
        return diasJustificados;
    }

    public void setDiasJustificados(Integer diasJustificados) {
        this.diasJustificados = diasJustificados;
    }

    public String getIntervalosJustificados() {
        return intervalosJustificados;
    }

    public void setIntervalosJustificados(String intervalosJustificados) {
        this.intervalosJustificados = intervalosJustificados;
    }

    @Override
    public void salvar() {
        if (faltasList.isEmpty()) {
            try {
                validarSePossuiAfastamento(selecionado.getInicio(), selecionado.getFim());
                if (!validaMesmoPeriodo()) {
                    return;
                }
                if (validaCampos()) {
                    super.salvar();
                }
            } catch (ValidacaoException v) {
                FacesUtil.printAllFacesMessages(v.getMensagens());
            }
        } else {
            for (Faltas f : faltasList) {
                try {
                    validarSePossuiAfastamento(f.getInicio(), f.getFim());
                    if (operacao.equals(Operacoes.NOVO)) {
                        getFacede().salvarNovo(f);
                    } else {
                        getFacede().salvar(f);
                    }
                    FacesUtil.addOperacaoRealizada("Servidor " + f.getContratoFP() + " " + getMensagemSucessoAoSalvar());
                } catch (ValidacaoException ex) {
                    FacesUtil.printAllFacesMessages(ex.getMensagens());
                    return;
                } catch (Exception e) {
                    descobrirETratarException(e);
                }
            }
            redireciona();
        }

    }

    private void validarSePossuiAfastamento(Date inicio, Date fim) {
        ValidacaoException ve = new ValidacaoException();
        Afastamento afastamento = faltasFacade.getAfastamentoFacade().servidorPossuiAfastamentoNoPeriodo(selecionado.getContratoFP(), inicio, fim);
        if (afastamento != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível realizar o lançamento de faltas, o servidor possui afastamento entre os dias "
                + DataUtil.getDataFormatada(afastamento.getInicio()) + " e " + DataUtil.getDataFormatada(afastamento.getTermino()));
        }
        ve.lancarException();
    }

    public boolean validaDatas(Date inicio, Date fim) {
        DateTime dateInicio = new DateTime(inicio);
        DateTime dateFim = new DateTime(fim);
        DateTime dataValidarInicio = new DateTime(selecionado.getInicio());
        DateTime dataValidarFim = new DateTime(selecionado.getFim());

        if (new Interval(dateInicio, dateFim).contains(dataValidarInicio)) {
            FacesUtil.addError("Atenção", "Já existe um registro lançado que se enquadra no mesmo período. a data " + Util.dateToString(dataValidarInicio.toDate()) + " está entre " + Util.dateToString(dateInicio.toDate()) + " e " + Util.dateToString(dateFim.toDate()));
            return false;
        }
        if (new Interval(dateInicio, dateFim).contains(dataValidarFim)) {
            FacesUtil.addError("Atenção", "Já existe um registro lançado que se enquadra no mesmo período. a data " + Util.dateToString(dataValidarFim.toDate()) + " está entre " + Util.dateToString(dateInicio.toDate()) + " e " + Util.dateToString(dateFim.toDate()));
            return false;
        }
        if (dateInicio.equals(dataValidarInicio) || dateInicio.equals(dataValidarFim) || dateFim.equals(dataValidarInicio) || dateFim.equals(dataValidarFim)) {
            FacesUtil.addError("Atenção", "Existe um registro lançado que se enquadra na mesma data");
            return false;
        }
        if (dataValidarInicio.isBefore(dateInicio) && dataValidarFim.isAfter(dateFim)) {
            FacesUtil.addError("Atenção", "Já existe um período lançado dentro do mesmo período.");
            return false;
        }
        return true;
    }

    private boolean validaMesmoPeriodo() {

        for (Faltas f : faltasList) {
            if (selecionado != null && f.getContratoFP().equals(selecionado.getContratoFP())) {
                if (validaDatas(f.getInicio(), f.getFim())) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        Faltas f = faltasFacade.servidorPossuiFaltasNoPeriodo(selecionado);
        if (f != null) {
            validaDatas(f.getInicio(), f.getFim());
            return false;
        }

        return true;
    }

    public Boolean validaCampos() {
        boolean retorno = Util.validaCampos(selecionado);
        if (!retorno)
            return retorno;

        if (getFaltas().getTipoFalta() == TipoFalta.FALTA_JUSTIFICADA && (getFaltas().getCid() == null || getFaltas().getMedico() == null)) {
            FacesUtil.addWarn("Atenção", "Quando o tipo de é justificada é obrigatório preenchimento do CID e do Médico");
            retorno = false;
        }

        if (getFaltas().getInicio().after(getFaltas().getFim())) {
            FacesUtil.addWarn("Atenção", "A data de início não pode ser posterior a data de fim");
            retorno = false;
        }
        return retorno;
    }

    @URLAction(mappingId = "novoFaltas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataCadastro(UtilRH.getDataOperacao());
        faltasList = new ArrayList<>();
    }

    @URLAction(mappingId = "verFaltas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        calcularDiasJustificados();
    }

    @URLAction(mappingId = "editarFaltas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        novo();
        super.editar();
        calcularDiasJustificados();
        faltasList = new ArrayList<>();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/faltas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<Faltas> getFaltasList() {
        return faltasList;
    }

    public void setFaltasList(List<Faltas> faltasList) {
        this.faltasList = faltasList;
    }

    public void adicionarFalta() {
        if (!validaCampos()) {
            return;
        }
        if (!validaMesmoPeriodo()) {
            return;
        }

        ContratoFP c = selecionado.getContratoFP();
        selecionado.setDataCadastro(UtilRH.getDataOperacao());
        faltasList = Util.adicionarObjetoEmLista(faltasList, selecionado);
        Collections.sort(faltasList, new Comparator<Faltas>() {
            @Override
            public int compare(Faltas o1, Faltas o2) {
                return o1.getInicio().compareTo(o2.getInicio());
            }
        });
        selecionado = null;
        super.novo();
        selecionado.setContratoFP(c);
    }

    public void edita(Faltas f) {
        selecionado = f;
        remove(f);
    }

    public void remove(Faltas f) {
        faltasList.remove(f);
    }

    public void limparCampos() {
        super.novo();
        selecionado.setDataCadastro(UtilRH.getDataOperacao());
    }

    private void validarExclusao() {
        ValidacaoException validacaoException = new ValidacaoException();
        if (faltasFacade.hasFolhaPeriodo(selecionado.getContratoFP(), selecionado)) {
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Desconto da falta já efetivado, necessário justificar a falta.");
        }
        validacaoException.lancarException();
    }

    public void calcularDiasJustificados() {
        int totalDiasJustificados = 0;
        StringBuilder intervalos = new StringBuilder();

        if (selecionado.getInicio() != null && selecionado.getFim() != null) {
            List<JustificativaFaltas> faltasjustificadas = justificativaFaltasFacade.buscarJustificativasPorIdFalta(selecionado.getContratoFP(), selecionado.getId());
            for (JustificativaFaltas justFalta : faltasjustificadas) {
                int dias = DataUtil.diasEntreDate(justFalta.getInicioVigencia(), justFalta.getFinalVigencia());
                totalDiasJustificados += dias;
                intervalos.append(DataUtil.getDataFormatada(justFalta.getInicioVigencia()))
                    .append(" a ")
                    .append(DataUtil.getDataFormatada(justFalta.getFinalVigencia()))
                    .append(" (").append(dias).append(" dias)")
                    .append("<br>");
            }

            setDiasJustificados(totalDiasJustificados);
            if (faltasjustificadas.isEmpty()) {
                setIntervalosJustificados("0");
            } else {
                setIntervalosJustificados(intervalos.length() > 0 ? intervalos.toString() : "0");
            }
        } else {
            setIntervalosJustificados("0");
        }
    }

    @Override
    public void excluir() {
        try {
            validarExclusao();
            super.excluir();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public boolean isFaltaInjustificada() {
        return TipoFalta.FALTA_INJUSTIFICADA.equals(selecionado.getTipoFalta());
    }

}
