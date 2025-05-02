/*
 * Codigo gerado automaticamente em Wed Apr 04 18:03:10 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.configuracao.HoraExtraUnidade;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "horaExtraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoHoraExtra", pattern = "/hora-extra/novo/", viewId = "/faces/rh/administracaodepagamento/horaextra/edita.xhtml"),
    @URLMapping(id = "editarHoraExtra", pattern = "/hora-extra/editar/#{horaExtraControlador.id}/", viewId = "/faces/rh/administracaodepagamento/horaextra/edita.xhtml"),
    @URLMapping(id = "listarHoraExtra", pattern = "/hora-extra/listar/", viewId = "/faces/rh/administracaodepagamento/horaextra/lista.xhtml"),
    @URLMapping(id = "verHoraExtra", pattern = "/hora-extra/ver/#{horaExtraControlador.id}/", viewId = "/faces/rh/administracaodepagamento/horaextra/visualizar.xhtml")
})
public class HoraExtraControlador extends PrettyControlador<HoraExtra> implements Serializable, CRUD {

    @EJB
    private HoraExtraFacade horaExtraFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    private List<HoraExtra> horasExtras;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    private BigDecimal totalHorasPermitidas;
    @EJB
    private FuncaoGratificadaFacade funcao;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private CargoConfiancaFacade cargoConfianca;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private CalendarioFPFacade calendarioFPFacade;
    private ConfiguracaoRH configuracaoRH;
    private ItemCalendarioFP itemCalendarioFP;
    private CompetenciaFP competenciaFP;
    private Date dataOperacao;

    public HoraExtraControlador() {
        super(HoraExtra.class);
    }

    public HoraExtraFacade getFacade() {
        return horaExtraFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return horaExtraFacade;
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

    public List<SelectItem> getTiposHoraExtra() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoHoraExtra object : TipoHoraExtra.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<HoraExtra> getHorasExtras() {
        return horasExtras;
    }

    public HoraExtra getHoraExtra() {
        return (HoraExtra) selecionado;
    }

    public BigDecimal getTotalHorasPermitidas() {
        return totalHorasPermitidas;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getInicio() != null && selecionado.getFim() != null) {

            if (selecionado.getInicio().after(selecionado.getFim())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial não pode ser posterior à Data Final.");
            }

            if (DataUtil.getMes(selecionado.getInicio()) != DataUtil.getMes(selecionado.getFim())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial e a Data Final devem pertencer ao mesmo mês.");
            }

            if (selecionado.getInicio().before(selecionado.getContratoFP().getInicioVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Mês/Ano da Competência não pode ser anterior " +
                    "ao Início de Vigência do Contrato FP - " + DataUtil.getDataFormatada(selecionado.getContratoFP().getInicioVigencia()));
            } else {
                BigDecimal totalHoras = horaExtraFacade.totalHorasExtrasNoMesDoAno(selecionado.getContratoFP(), DataUtil.getAno(selecionado.getInicio()), DataUtil.getMes(selecionado.getInicio()));

                if (operacao == Operacoes.NOVO && horasExtras.isEmpty()) {
                    totalHoras = totalHoras.add(new BigDecimal(selecionado.getTotalHoras()));
                }

                if (operacao == Operacoes.EDITAR) {
                    HoraExtra h = horaExtraFacade.recuperar(selecionado.getId());
                    totalHoras = totalHoras.subtract(new BigDecimal(h.getTotalHoras()));
                    totalHoras = totalHoras.add(new BigDecimal(selecionado.getTotalHoras()));
                }

                for (HoraExtra horasExtra : horasExtras) {
                    if (horasExtra.getContratoFP().equals(selecionado.getContratoFP())) {
                        totalHoras = totalHoras.add(new BigDecimal(horasExtra.getTotalHoras()));
                    }
                }

                if (horasExtras != null && !horasExtras.isEmpty()) {
                    totalHoras = totalHoras.add(new BigDecimal(selecionado.getTotalHoras()));
                }

                totalHorasPermitidas = configuracaoRH.getHoraExtraMaximaPadrao();

                selecionado.setContratoFP(contratoFPFacade.recuperarContratoComHierarquia(selecionado.getContratoFP().getId()));
                if (configuracaoRH != null && !configuracaoRH.getHoraExtraUnidade().isEmpty()) {
                    for (HoraExtraUnidade horaExtraUnidade : configuracaoRH.getHoraExtraUnidade()) {
                        if (selecionado.getContratoFP().getUnidadeOrganizacional().getHierarquiasOrganizacionais().contains(horaExtraUnidade.getHierarquiaOrganizacional())) {
                            if (horaExtraUnidade.getFimVigencia() == null || (horaExtraUnidade.getInicioVigencia().before(sistemaFacade.getDataOperacao()) &&
                                horaExtraUnidade.getFimVigencia().after(sistemaFacade.getDataOperacao()))) {
                                totalHorasPermitidas = horaExtraUnidade.getHoraExtra();
                                break;
                            }
                        }
                    }
                }

                if (totalHorasPermitidas == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrada a quantidade de Hora Extra Máxima configurado na Configuração Geral do RH");
                }

                if (totalHorasPermitidas != null && totalHoras.compareTo(totalHorasPermitidas) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade de Horas extras maior do que a permitida. Restam " + totalHorasPermitidas.subtract(totalHoras.subtract(BigDecimal.valueOf(selecionado.getTotalHoras()))) + " horas");
                }
            }
        }

        List<Afastamento> afastamento = afastamentoFacade.listaFiltrandoServidorPorCompetencia(selecionado.getContratoFP(), selecionado.getInicio(), selecionado.getFim());
        if (afastamento != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existem afastamentos para esse servidor na competência informada, afastamento com ínicio " +
                DataUtil.getDataFormatada(afastamento.get(0).getInicio()) + " à " + DataUtil.getDataFormatada(afastamento.get(0).getTermino()));
        }

        List<ConcessaoFeriasLicenca> concessao = concessaoFeriasLicencaFacade.listaFiltrandoServidorPorCompetencia(selecionado.getContratoFP(), selecionado.getInicio(), selecionado.getFim());
        if (concessao != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existem Férias ou Licenças para esse servidor na competência informada com início " +
                DataUtil.getDataFormatada(concessao.get(0).getDataInicial()) + " à " + DataUtil.getDataFormatada(concessao.get(0).getDataFinal()));
        }

        if (selecionado.getTotalHoras() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Total de Horas lançadas deve ser maior que Zero");
        }

        ve.lancarException();
    }

    @URLAction(mappingId = "novoHoraExtra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        horasExtras = new ArrayList<>();
        itemCalendarioFP = null;
        dataOperacao = null;
        selecionado.setDataCadastro(UtilRH.getDataOperacao());
        configuracaoRH = configuracaoRHFacade.recuperar(configuracaoRHFacade.recuperarConfiguracaoRHVigente().getId());

        if (selecionado.getInicio() == null) {
            DateTime dateTime = competenciaFPFacade.recuperarDataUltimaCompetencia();
            if (dateTime != null) {
                selecionado.setInicio(dateTime.toDate());
            }
        }
    }

    @URLAction(mappingId = "verHoraExtra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver(); //To change body of generated methods, choose Tools | Templates.
    }

    @URLAction(mappingId = "editarHoraExtra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        horasExtras = new ArrayList<>();
        configuracaoRH = configuracaoRHFacade.recuperar(configuracaoRHFacade.recuperarConfiguracaoRHVigente().getId());
    }


    @Override
    public void salvar() {
        if (horasExtras.isEmpty()) {
            try {
                if (Util.validaCampos(selecionado)) {
                    validarCampos();
                    selecionado.setMes(Integer.parseInt(Util.getMesDaData(selecionado.getInicio())));
                    selecionado.setAno(Integer.parseInt(Util.getAnoDaData(selecionado.getInicio())));
                    salvarTodos(selecionado);
                    FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar());
                    redireciona();
                }

            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);
            }
        } else {
            for (HoraExtra he : horasExtras) {
                salvarTodos(he);
            }
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar());
            redireciona();
        }

    }

    public void salvarTodos(HoraExtra he) {
        if (operacao == Operacoes.NOVO) {
            getFacede().salvarNovo(he);
        } else {
            getFacede().salvar(he);
        }
    }

    public void adicionarHoraExtra() {
        try {
            if (Util.validaCampos(selecionado)) {
                validarCampos();
                selecionado.setMes(Integer.parseInt(Util.getMesDaData(selecionado.getInicio())));
                selecionado.setAno(Integer.parseInt(Util.getAnoDaData(selecionado.getInicio())));
                horasExtras.add(selecionado);
                selecionado = null;
                super.novo();
                selecionado.setDataCadastro(UtilRH.getDataOperacao());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarContrato() {
        if (selecionado.getContratoFP() != null) {
            if (selecionado.getContratoFP().getModalidadeContratoFP().getCodigo() != 1 && selecionado.getContratoFP().getModalidadeContratoFP().getCodigo() != 4) {
                FacesUtil.addWarn("Atenção ", "O contrato não pode ser " + selecionado.getContratoFP().getModalidadeContratoFP().getDescricao());
                limparContrato();
                return;
            }
            if (funcao.recuperaFuncaoGratificadaVigente(selecionado.getContratoFP(), sistemaFacade.getDataOperacao()).getId() != null) {
                FacesUtil.addWarn("Atenção ", " O contrato não pode ser adicionado pois possui acesso a função gratificada.");
                limparContrato();
                return;
            }
            if (cargoConfianca.recuperaCargoConfiancaVigente(selecionado.getContratoFP(), sistemaFacade.getDataOperacao()).getId() != null) {
                FacesUtil.addWarn("Atenção ", " O contrato não pode ser adicionado pois possui acesso a cargo de confiança.");
                limparContrato();
                return;
            }
        }
    }

    private void limparContrato() {
        selecionado.setContratoFP(null);
    }

    public void removerHoraExtra(HoraExtra he) {
        horasExtras.remove(he);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/hora-extra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ItemCalendarioFP getItemCalendarioFP() {
        if(selecionado != null && selecionado.getDataCadastro() != null){
            if (itemCalendarioFP == null) {
                DateTime now = selecionado.getDataCadastro() == null ? DateTime.now(): new DateTime(selecionado.getDataCadastro());
                competenciaFP = competenciaFPFacade.recuperarCompetenciaPorTipoMesAno(Mes.getMesToInt(now.getMonthOfYear()), now.getYear(), TipoFolhaDePagamento.NORMAL);
                itemCalendarioFP = calendarioFPFacade.buscarDataCalculoCalendarioFPPorCompetenciaFP(competenciaFP, true);
            }
        }
        return itemCalendarioFP;
    }

    public void setItemCalendarioFP(ItemCalendarioFP itemCalendarioFP) {
        this.itemCalendarioFP = itemCalendarioFP;
    }

    public Date getDataOperacao() {
        if(isOperacaoEditar()){
            return selecionado.getDataCadastro();
        }
        if (dataOperacao == null) {
            dataOperacao = sistemaFacade.getDataOperacao();
        }
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }
}
