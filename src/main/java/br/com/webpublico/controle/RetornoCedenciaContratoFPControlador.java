/*
 * Codigo gerado automaticamente em Tue Feb 21 10:56:59 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
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
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "retornoCedenciaContratoFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRetornoCedenciaContratoFP", pattern = "/retornocedencia/novo/", viewId = "/faces/rh/administracaodepagamento/retornocedenciacontratofp/edita.xhtml"),
    @URLMapping(id = "editarRetornoCedenciaContratoFP", pattern = "/retornocedencia/editar/#{retornoCedenciaContratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/retornocedenciacontratofp/edita.xhtml"),
    @URLMapping(id = "listarRetornoCedenciaContratoFP", pattern = "/retornocedencia/listar/", viewId = "/faces/rh/administracaodepagamento/retornocedenciacontratofp/lista.xhtml"),
    @URLMapping(id = "verRetornoCedenciaContratoFP", pattern = "/retornocedencia/ver/#{retornoCedenciaContratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/retornocedenciacontratofp/visualizar.xhtml")
})
public class RetornoCedenciaContratoFPControlador extends PrettyControlador<RetornoCedenciaContratoFP> implements Serializable, CRUD {

    @EJB
    private RetornoCedenciaContratoFPFacade retornoCedenciaContratoFPFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    private ConverterGenerico converterLotacaoFuncional;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;
    private CedenciaContratoFP cedenciaContratoFPRecuperada;
    private LotacaoFuncional lotacaoFuncionalSelecionado;
    private HierarquiaOrganizacional hierarquiaOrganizacionalLotacao;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    private ConverterGenerico converterHorarioContratoFP;
    @EJB
    private HorarioContratoFPFacade horarioContratoFPFacade;
    @EJB
    private HorarioDeTrabalhoFacade horarioDeTrabalhoFacade;
    private ConverterGenerico converterHorarioDeTrabalho;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Integer cedido;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private ContratoFP contratoFP;

    public RetornoCedenciaContratoFPControlador() {
        super(RetornoCedenciaContratoFP.class);
        cedido = new Integer(0);
    }

    @Override
    public AbstractFacade getFacede() {
        return retornoCedenciaContratoFPFacade;
    }

    public List<SelectItem> getAtoLegal() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (AtoLegal object : atoLegalFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public List<SelectItem> getContratoFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (ContratoFP object : contratoFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public List<SelectItem> getLotacaoFuncional() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (LotacaoFuncional object : lotacaoFuncionalFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterLotacaoFuncional() {
        if (converterLotacaoFuncional == null) {
            converterLotacaoFuncional = new ConverterGenerico(LotacaoFuncional.class, lotacaoFuncionalFacade);
        }
        return converterLotacaoFuncional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalLotacao() {
        return hierarquiaOrganizacionalLotacao;
    }

    public void setHierarquiaOrganizacionalLotacao(HierarquiaOrganizacional hierarquiaOrganizacionalLotacao) {
        this.hierarquiaOrganizacionalLotacao = hierarquiaOrganizacionalLotacao;
    }

    public LotacaoFuncional getLotacaoFuncionalSelecionado() {
        return lotacaoFuncionalSelecionado;
    }

    public void setLotacaoFuncionalSelecionado(LotacaoFuncional lotacaoFuncionalSelecionado) {
        this.lotacaoFuncionalSelecionado = lotacaoFuncionalSelecionado;
    }


    public Integer getCedido() {
        return cedido;
    }

    public void setCedido(Integer cedido) {
        this.cedido = cedido;
    }

    @URLAction(mappingId = "novoRetornoCedenciaContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        cedido = new Integer(0);

        RetornoCedenciaContratoFP r = (RetornoCedenciaContratoFP) selecionado;
        r.setLotacaoFuncional(new LotacaoFuncional());
        r.getLotacaoFuncional().setHorarioContratoFP(new HorarioContratoFP());

        cedenciaContratoFPRecuperada = new CedenciaContratoFP();
        hierarquiaOrganizacionalLotacao = new HierarquiaOrganizacional();
        hierarquiaOrganizacionalLotacao.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setOficioRetorno(Boolean.FALSE);
    }

    @URLAction(mappingId = "verRetornoCedenciaContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionar();
    }

    @URLAction(mappingId = "editarRetornoCedenciaContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionar();
    }

    public void selecionar() {

        RetornoCedenciaContratoFP r = (RetornoCedenciaContratoFP) selecionado;
        cedenciaContratoFPRecuperada = r.getCedenciaContratoFP();

        if (r.getLotacaoFuncional() != null) {
            cedido = new Integer(1);

            hierarquiaOrganizacionalLotacao = new HierarquiaOrganizacional();
            hierarquiaOrganizacionalLotacao.setExercicio(sistemaControlador.getExercicioCorrente());
            hierarquiaOrganizacionalLotacao.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            HierarquiaOrganizacional h = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(r.getLotacaoFuncional().getUnidadeOrganizacional(), hierarquiaOrganizacionalLotacao, hierarquiaOrganizacionalLotacao.getExercicio());
            hierarquiaOrganizacionalLotacao = h;
        } else {
            cedido = new Integer(2);
        }
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoDePessoal(parte.trim());
    }

    public List<CedenciaContratoFP> completarCedencias(String parte) {
          return cedenciaContratoFPFacade.buscarCedenciasSemRetornoDeCedencia(parte);
    }

    public void addLotacaoFuncional() {
        RetornoCedenciaContratoFP r = (RetornoCedenciaContratoFP) selecionado;
        r.getLotacaoFuncional().setDataRegistro(new Date());

        r.getLotacaoFuncional().setUnidadeOrganizacional(hierarquiaOrganizacionalLotacao.getSubordinada());
        r.getLotacaoFuncional().setVinculoFP(r.getContratoFP());
    }

    public ConverterGenerico getConverterHorarioContratoFP() {
        if (converterHorarioContratoFP == null) {
            converterHorarioContratoFP = new ConverterGenerico(HorarioContratoFP.class, horarioContratoFPFacade);
        }
        return converterHorarioContratoFP;
    }

    public ConverterGenerico getConverterHorarioDeTrabalho() {
        if (converterHorarioDeTrabalho == null) {
            converterHorarioDeTrabalho = new ConverterGenerico(HorarioDeTrabalho.class, horarioDeTrabalhoFacade);
        }
        return converterHorarioDeTrabalho;
    }

    public List<SelectItem> getHorarioDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (HorarioDeTrabalho object : horarioDeTrabalhoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void associa(ActionEvent e) {
        RetornoCedenciaContratoFP r = (RetornoCedenciaContratoFP) selecionado;
        r.getLotacaoFuncional().setHorarioContratoFP((HorarioContratoFP) e.getComponent().getAttributes().get("objeto"));
    }

    public CedenciaContratoFP getCedenciaContratoFPRecuperada() {
        return cedenciaContratoFPRecuperada;
    }

    public void setCedenciaContratoFPRecuperada(CedenciaContratoFP cedenciaContratoFPRecuperada) {
        this.cedenciaContratoFPRecuperada = cedenciaContratoFPRecuperada;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                if (cedido == 1) {
                    salvarRetornoServidorCedidoUnidadeExterna();
                } else if (cedido == 2) {
                    salvarRetornoServidorCedidoPrefeitura();
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
                redireciona();

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
            }
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Boolean validaCampos() {
        boolean valida = true;

        RetornoCedenciaContratoFP r = (RetornoCedenciaContratoFP) selecionado;
        if (Util.validaCampos(selecionado)) {
            if (r.getDataRetorno() != null) {
                if (r.getDataRetorno().before(r.getCedenciaContratoFP().getDataCessao())) {
                    FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção !", "O campo Data do Retorno deve ser superior a data da cessão !"));
                    return false;
                }
            }
        } else {
            return false;
        }

        if (cedido == 1) {
            LotacaoFuncional lotacao = r.getLotacaoFuncional();

            if (lotacao.getInicioVigencia() != null) {
                if (lotacao.getFinalVigencia() != null) {
                    if (lotacao.getFinalVigencia().before(lotacao.getInicioVigencia())) {
                        FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção !", "O Início da vigência na lotação funcional não pode ser maior que o final da vigência !"));
                        valida = false;
                    }
                }
            } else {
                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção !", "O campo Início da vigência na lotação funcional é obrigatório !"));
                valida = false;
            }

            if (hierarquiaOrganizacionalLotacao == null || hierarquiaOrganizacionalLotacao.getId() == null) {
                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção !", "O campo Hierarquia Organizacional é obrigatório !"));
                valida = false;
            }

            if (selecionado.getOficioRetorno() && (selecionado.getAtoLegal() == null || selecionado.getAtoLegal().getId() == null)) {
                FacesUtil.addError("Atenção!", "O Ato Legal deve ser informado quando o Ofício de Retorno estiver selecionado.");
                valida = false;
            }

//            HorarioContratoFP horario = lotacao.getHorarioContratoFP();
//
//            if (horario.getInicioVigencia() != null) {
//                if (horario.getFinalVigencia() != null) {
//                    if (horario.getFinalVigencia().before(horario.getInicioVigencia())) {
//                        FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção !", "O Início da vigência no horário contrato FP  não pode ser maior que o final da vigência !"));
//                        valida = false;
//                    }
//                }
//            } else {
//                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção !", "O campo Início da vigência no horário contrato FP é obrigatório !"));
//                valida = false;
//            }

        }
        return valida;
    }

    @Override
    public void excluir() {
        try {
            excluirSelecionado();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ve) {
            FacesUtil.addOperacaoNaoRealizada(ve.getMessage());
        }
    }


    public void excluirSelecionado() {
        ValidacaoException ve = new ValidacaoException();
            RetornoCedenciaContratoFP r = (RetornoCedenciaContratoFP) selecionado;

            if (folhaDePagamentoFacade.recuperaFolhaCalculadaPorData(r.getDataRetorno()) != null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenção !", "A Folha de Pagamento na data de retorno informada já foi calculada ! Não é possível excluir !"));
                return;
            }

            if (cedido == 1) {
                excluirRetornoServidorCedidoUnidadeExterna(r);
            } else if (cedido == 2) {
                excluirRetornoServidorCedidoPrefeitura(r);
            }
       ve.lancarException();
    }

    public boolean isDataRetornoIgualFinalVigenciaDoContrato(RetornoCedenciaContratoFP retorno) {
        return (retorno.getDataRetorno().compareTo(retorno.getContratoFP().getFinalVigencia()) == 0);
    }

    public boolean isDataRetornoIgualDataRetornoCedencia(RetornoCedenciaContratoFP retorno) {
        if(retorno.getCedenciaContratoFP().getDataRetorno()!= null){
            return (retorno.getDataRetorno().compareTo(retorno.getCedenciaContratoFP().getDataRetorno()) == 0);
        } else{
            return false;
        }
    }

    public void salvarRetornoServidorCedidoUnidadeExterna() {
        RetornoCedenciaContratoFP r = (RetornoCedenciaContratoFP) selecionado;
        r.getLotacaoFuncional().setUnidadeOrganizacional(hierarquiaOrganizacionalLotacao.getSubordinada());
        r.getLotacaoFuncional().setVinculoFP(r.getContratoFP());


        if (operacao == Operacoes.NOVO) {
            r.getLotacaoFuncional().getHorarioContratoFP().setInicioVigencia(r.getLotacaoFuncional().getInicioVigencia());
            r.getLotacaoFuncional().getHorarioContratoFP().setFinalVigencia(r.getLotacaoFuncional().getFinalVigencia());
            r.getCedenciaContratoFP().setDataRetorno(r.getDataRetorno());
            ((RetornoCedenciaContratoFPFacade) this.getFacede()).salvarNovoRetornoServidorCedidoUE(r);
        } else {
            if (isDataRetornoIgualDataRetornoCedencia(r)) {
                r.getCedenciaContratoFP().setDataRetorno(r.getDataRetorno());
            }
            ((RetornoCedenciaContratoFPFacade) this.getFacede()).salvarRetornoServidorCedidoUE(r);
        }
    }

    public void salvarRetornoServidorCedidoPrefeitura() {
        RetornoCedenciaContratoFP r = (RetornoCedenciaContratoFP) selecionado;

        //o Retorno de Servidores Cedidos para a Prefeitura não possui a lotação funcional
        r.setLotacaoFuncional(null);

        if (operacao == Operacoes.NOVO) {
            r.getContratoFP().setFinalVigencia(r.getDataRetorno());
            r.getCedenciaContratoFP().setDataRetorno(r.getDataRetorno());
            ((RetornoCedenciaContratoFPFacade) this.getFacede()).salvarNovoRetornoServidorCedidoPrefeitura(r);
        } else {
            if (isDataRetornoIgualFinalVigenciaDoContrato(r)) {
                r.getContratoFP().setFinalVigencia(r.getDataRetorno());
            }

            if (isDataRetornoIgualDataRetornoCedencia(r)) {
                r.getCedenciaContratoFP().setDataRetorno(r.getDataRetorno());
            }

            ((RetornoCedenciaContratoFPFacade) this.getFacede()).salvarRetornoServidorCedidoPrefeitura(r);
        }
    }

    public void excluirRetornoServidorCedidoPrefeitura(RetornoCedenciaContratoFP r) {
        if (isDataRetornoIgualFinalVigenciaDoContrato(r)) {
            r.getContratoFP().setFinalVigencia(null);
        }
        if (r.getLotacaoFuncional() != null) {
            r.getLotacaoFuncional().setRetornoCedenciaContratoFP(null);
        }

        if (isDataRetornoIgualDataRetornoCedencia(r)) {
            r.getCedenciaContratoFP().setDataRetorno(null);
        }

        ((RetornoCedenciaContratoFPFacade) this.getFacede()).removerRetornoServidorCedidoPrefeitura(r);
    }

    public void excluirRetornoServidorCedidoUnidadeExterna(RetornoCedenciaContratoFP r) {
        ValidacaoException ve = new ValidacaoException();
        CedenciaContratoFP cedenciaVigente = cedenciaContratoFPFacade.recuperaCedenciaUnidadeExternaVigentePorContratoFP(r.getContratoFP());
        CedenciaContratoFP ultimaCedencia = cedenciaContratoFPFacade.recuperaUltimaCedenciaUnidadeExternaPorContratoFP(r.getContratoFP());

        if ((cedenciaVigente != null) && (!cedenciaVigente.equals(r.getCedenciaContratoFP()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Atenção !", "A Lotação funcional informada neste retorno não é a lotação vigente no contrato ! Não é possível excluir !");
        } else if ((ultimaCedencia != null) && (!ultimaCedencia.equals(r.getCedenciaContratoFP()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Atenção !", "A Lotação funcional informada neste retorno não é a lotação vigente no contrato ! Não é possível excluir !");
        } else {
            if (isDataRetornoIgualDataRetornoCedencia(r)) {
                r.getCedenciaContratoFP().setDataRetorno(null);
            }

            ((RetornoCedenciaContratoFPFacade) this.getFacede()).removerRetornoServidorCedidoUE(r);
        }
        ve.lancarException();
    }

    public LotacaoFuncional getUltimaLotacaoFuncional() {
        return retornoCedenciaContratoFPFacade.getUltimaLotacaoFuncional(selecionado.getContratoFP());
    }

    public void sugereDataLotacaoFuncional() {
        Date inicioVigenciaHorarioFuncional = selecionado.getLotacaoFuncional().getHorarioContratoFP().getInicioVigencia();
        if(inicioVigenciaHorarioFuncional != null) {
        selecionado.getLotacaoFuncional().setInicioVigencia(inicioVigenciaHorarioFuncional);
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/retornocedencia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void recuperarCedenciaContratoFP() {
        if (selecionado.getCedenciaContratoFP() != null) {
            cedenciaContratoFPRecuperada = selecionado.getCedenciaContratoFP();
        }
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }
}
