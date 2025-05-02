package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.TipoReintegracao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "reintegracaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoReintegracao", pattern = "/reintegracao/novo/", viewId = "/faces/rh/administracaodepagamento/reintegracao/edita.xhtml"),
    @URLMapping(id = "editarReintegracao", pattern = "/reintegracao/editar/#{reintegracaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/reintegracao/edita.xhtml"),
    @URLMapping(id = "listarReintegracao", pattern = "/reintegracao/listar/", viewId = "/faces/rh/administracaodepagamento/reintegracao/lista.xhtml"),
    @URLMapping(id = "verReintegracao", pattern = "/reintegracao/ver/#{reintegracaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/reintegracao/edita.xhtml"),
})
public class ReintegracaoControlador extends PrettyControlador<Reintegracao> implements Serializable, CRUD {

    @EJB
    private ReintegracaoFacade reintegracaoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HorarioContratoFPFacade horarioContratoFPFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private AssociacaoFacade associacaoFacade;
    @EJB
    private SindicatoFacade sindicatoFacade;
    @EJB
    private HorarioDeTrabalhoFacade horarioDeTrabalhoFacade;
    @EJB
    private TipoPrevidenciaFPFacade tipoPrevidenciaFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private SituacaoContratoFPFacade situacaoContratoFPFacade;

    // Listas
    private List<EnquadramentoFuncional> enquadramentos;
    private List<PrevidenciaVinculoFP> previdencias;
    private List<OpcaoValeTransporteFP> opcoesValeTransporte;
    private List<HorarioContratoFP> horarios;
    private List<SindicatoVinculoFP> sindicatos;
    private List<AssociacaoVinculoFP> associacoes;
    private List<SituacaoContratoFP> situacoes;

    // Representações dos selecionados
    private PrevidenciaVinculoFP previdenciaVinculoFP;
    private OpcaoValeTransporteFP opcaoValeTransporteFP;
    private HorarioContratoFP horarioContratoFP;
    private HierarquiaOrganizacional hierarquiaOrganizacionalLotacaoFuncional;
    private LotacaoFuncional lotacaoFuncional;
    private SindicatoVinculoFP sindicatoVinculoFP;
    private AssociacaoVinculoFP associacaoVinculoFP;
    private SituacaoContratoFP situacaoContratoFP;
    private ExoneracaoRescisao exoneracao;
//    private ContratoFP contratoFPSelecionado;

    public ReintegracaoControlador() {
        super(Reintegracao.class);
    }

    public ReintegracaoFacade getReintegracaoFacade() {
        return getReintegracaoFacade();
    }

    @Override
    public AbstractFacade getFacede() {
        return reintegracaoFacade;
    }

    @Override
    public void salvar() {
        try {
            validarCamposObrigatoriosReintegracao();
            permitirManipularRegistro();
            if (Operacoes.NOVO.equals(operacao)) {
                criarProvimentoParaContratoComReintegracao();
                reintegracaoFacade.salvarNovo(selecionado);
                situacaoContratoFPFacade.salvar(situacaoContratoFP);
            } else {
                selecionado.getProvimentoFP().setAtoLegal(selecionado.getAtoLegal());
                reintegracaoFacade.salvar(selecionado);
            }
            FacesUtil.addInfo("", "Salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Erro!", "Problema ao Salvar. " + e);
        }
    }

    @URLAction(mappingId = "novoReintegracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataReintegracao(UtilRH.getDataOperacao());
        situacaoContratoFP = new SituacaoContratoFP();
    }

    @URLAction(mappingId = "editarReintegracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        carregarObjetosParaVerOuEditar();
    }

    @URLAction(mappingId = "verReintegracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        carregarObjetosParaVerOuEditar();
    }

    private void carregarObjetosParaVerOuEditar() {
        selecionado.setContratoFP(contratoFPFacade.recuperarParaReintegracao(selecionado.getContratoFP().getId()));
        atribuirListasDoContratoParaListasControlador();
        ordenarListasContratoSelecionado();
    }

    private void recuperarHorariosDoContrato() {
        horarios = new ArrayList<>();
        for (LotacaoFuncional lotacaoFuncional : selecionado.getContratoFP().getLotacaoFuncionals()) {
            if (!horarios.contains(lotacaoFuncional.getHorarioContratoFP())) {
                horarios.add(horarioContratoFPFacade.recuperar(lotacaoFuncional.getHorarioContratoFP().getId()));
            }
        }
    }

    public List<SelectItem> getTipoPrevidenciaFp() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPrevidenciaFP object : tipoPrevidenciaFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getHorarioDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (HorarioDeTrabalho object : horarioDeTrabalhoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getSindicato() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Sindicato object : sindicatoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getAssociacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Associacao object : associacaoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getExoneracaoRescisao() {
        if (selecionado.getContratoFP() != null) {
            return Util.getListSelectItem(exoneracaoRescisaoFacade.buscarExoneracaoRescisaoContrato(selecionado.getContratoFP().getId()), false);
        }
        return Lists.newArrayList();
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return reintegracaoFacade.listaFiltrandoContratosRescindidosExonerados(parte.trim(),
            ModalidadeContratoFP.CONCURSADOS,
            ModalidadeContratoFP.CARGO_COMISSAO,
            ModalidadeContratoFP.CONTRATO_TEMPORARIO);
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte, PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    public HierarquiaOrganizacional hierarquiaPai(UnidadeOrganizacional u) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(u, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getDataReintegracao());
    }

    public List<SituacaoFuncional> completaSituacoesFuncionais(String parte) {
        return situacaoFuncionalFacade.listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    private boolean jaExisteRegistroAdicionadoPelaReintegracao(Object obj, String alias) {
        if (obj != null) {
            FacesUtil.addOperacaoNaoPermitida("Já existe um registro de " + alias + " adicionado a partir desta reintegração. Verifique as informações e tente novamente.");
            return true;
        }
        return false;
    }

    private void permitirManipularRegistro() {
        ValidacaoException ve = new ValidacaoException();
        if (!reintegracaoFacade.podeExcluir(selecionado.getContratoFP(), selecionado.getDataReintegracao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido manipular as informações desta reintegração, já foi processada folha de pagamento após a realização desta reintegração.");
        }
        ve.lancarException();
    }

    private boolean validarVigenciaNovoObjetoAdicionadoEmLista(ValidadorVigencia vv, List<? extends ValidadorVigencia> objs) {
        if (objs == null || objs.isEmpty()) {
            return true;
        }
        ValidadorVigencia ultimo = objs.get(objs.size() - 1);
        if (ultimo == null) {
            return true;
        }

        if (ultimo.getFimVigencia() != null) {
            if (vv.getInicioVigencia().compareTo(ultimo.getFimVigencia()) <= 0) {
                String dataNova = Util.formatterDiaMesAno.format(vv.getInicioVigencia());
                String dataAntiga = Util.formatterDiaMesAno.format(ultimo.getFimVigencia());
                FacesUtil.addOperacaoNaoPermitida("A data de inicio de vigência (" + dataNova + ") deste registro deve ser posterior a data de final de vigência (" + dataAntiga + ") do último registro já existente.");
                return false;
            }
            return true;
        }

        if (ultimo.getFimVigencia() == null && ultimo.getInicioVigencia() != null) {
            if (vv.getInicioVigencia().compareTo(ultimo.getInicioVigencia()) <= 0) {
                String dataNova = Util.formatterDiaMesAno.format(vv.getInicioVigencia());
                String dataAntiga = Util.formatterDiaMesAno.format(ultimo.getInicioVigencia());
                FacesUtil.addOperacaoNaoPermitida("A data de inicio de vigência (" + dataNova + ") deste registro deve ser posterior a data de inicio de vigência (" + dataAntiga + ") do último registro já existente.");
                return false;
            }
            return true;
        }


        return true;
    }

    private <T extends ValidadorVigencia> void alterarVigenciaUltimoRegistroLista(T vv, List<T> lista) {
        try {
            ordenarListaDescendentemente(lista);
            ValidadorVigencia antigo = lista.get(0); // Devem vir ordenadas decrescentemente
            if (antigo.getFimVigencia() == null) {
                antigo.setFimVigencia(DataUtil.getDataDiaAnterior(vv.getInicioVigencia()));
                lista = Util.adicionarObjetoEmLista(lista, (T) antigo);
            }
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    private boolean existeServidorSelecionado() {
        if (selecionado.getContratoFP() == null) {
            FacesUtil.addOperacaoNaoPermitida("Para adicionar informações a esta reintegração é necessário selecionar um servidor.");
            FacesUtil.addOperacaoNaoPermitida("Por favor, selecione um servidor para poder continuar.");
            return false;
        }
        return true;
    }

    // ************************
    // PREVIDÊNCIA
    // ************************
    public void novaPrevidencia() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            permitirManipularRegistro();
            if (jaExisteRegistroAdicionadoPelaReintegracao(selecionado.getPrevidenciaVinculoFP(), "previdência")) {
                return;
            }
            previdenciaVinculoFP = new PrevidenciaVinculoFP();
            previdenciaVinculoFP.setContratoFP(selecionado.getContratoFP());
            previdenciaVinculoFP.setInicioVigencia(selecionado.getDataReintegracao());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarPrevidencia() {
        previdenciaVinculoFP = null;
    }

    public void confirmarPrevidencia() {
        if (!podeConfirmarPrevidencia()) {
            return;
        }
        alterarVigenciaUltimoRegistroLista(previdenciaVinculoFP, previdencias);
        selecionado.setPrevidenciaAnterior(previdencias == null || previdencias.isEmpty() ? null : previdencias.get(0));
        selecionado.setPrevidenciaVinculoFP(previdenciaVinculoFP);

        previdencias = Util.adicionarObjetoEmLista(previdencias, previdenciaVinculoFP);
        cancelarPrevidencia();
        ordenarListaDescendentemente(previdencias);
    }

    private boolean podeConfirmarPrevidencia() {
        if (!validarConfirmacao(previdenciaVinculoFP)) {
            return false;
        }

        if (!validarVigenciaNovoObjetoAdicionadoEmLista(previdenciaVinculoFP, previdencias)) {
            return false;
        }

        return true;
    }

    public void removerPrevidencia(PrevidenciaVinculoFP prev) {
        try {
            permitirManipularRegistro();
            previdencias.remove(prev);
            selecionado.setPrevidenciaVinculoFP(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    // ************************
    // VALE TRANSPORTE
    // ************************
    public void novoOpcaoValeTransporteFP() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            permitirManipularRegistro();
            if (jaExisteRegistroAdicionadoPelaReintegracao(selecionado.getOpcaoValeTransporteFP(), "vale transporte")) {
                return;
            }
            opcaoValeTransporteFP = new OpcaoValeTransporteFP();
            opcaoValeTransporteFP.setContratoFP(selecionado.getContratoFP());
            opcaoValeTransporteFP.setInicioVigencia(selecionado.getDataReintegracao());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarOpcaoValeTransporteFP() {
        if (!podeConfirmarOpcaoValeTransporte()) {
            return;
        }
        alterarVigenciaUltimoRegistroLista(opcaoValeTransporteFP, opcoesValeTransporte);
        selecionado.setOpcaoValeTransporteAnterior(opcoesValeTransporte == null || opcoesValeTransporte.isEmpty() ? null : opcoesValeTransporte.get(0));

        opcoesValeTransporte = Util.adicionarObjetoEmLista(opcoesValeTransporte, opcaoValeTransporteFP);
        selecionado.setOpcaoValeTransporteFP(opcaoValeTransporteFP);
        cancelarOpcaoValeTransporteFP();
        ordenarListaDescendentemente(opcoesValeTransporte);
    }

    public boolean podeConfirmarOpcaoValeTransporte() {
        if (!validarConfirmacao(opcaoValeTransporteFP)) {
            return false;
        }

        if (!validarVigenciaNovoObjetoAdicionadoEmLista(opcaoValeTransporteFP, opcoesValeTransporte)) {
            return false;
        }

        return true;
    }

    public void cancelarOpcaoValeTransporteFP() {
        opcaoValeTransporteFP = null;
    }

    public void removerOpcaoValeTransporteFP(OpcaoValeTransporteFP valeTransporteFP) {
        try {
            permitirManipularRegistro();
            opcoesValeTransporte.remove(valeTransporteFP);
            selecionado.setOpcaoValeTransporteFP(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    // ************************
    // HORÁRIO DE TRABALHO
    // ************************
    public void novoHorarioTrabalhoServidor() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            permitirManipularRegistro();
            if (jaExisteRegistroAdicionadoPelaReintegracao(selecionado.getHorarioContratoFP(), "horário")) {
                return;
            }
            horarioContratoFP = new HorarioContratoFP();
            horarioContratoFP.setInicioVigencia(selecionado.getDataReintegracao());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarHorarioContratoFP() {
        horarioContratoFP = null;
    }

    public void confirmarHorarioContratoFP() {
        if (!podeConfirmarHorarioContratoFP()) {
            return;
        }
        alterarVigenciaUltimoRegistroLista(horarioContratoFP, horarios);
        selecionado.setHorarioAnterior(horarios == null || horarios.isEmpty() ? null : horarios.get(0));


        selecionado.setHorarioContratoFP(horarioContratoFP);

        horarios = Util.adicionarObjetoEmLista(horarios, horarioContratoFP);
        cancelarHorarioContratoFP();
        ordenarListaDescendentemente(horarios);
    }

    public boolean podeConfirmarHorarioContratoFP() {
        if (!validarConfirmacao(horarioContratoFP)) {
            return false;
        }

        if (!validarVigenciaNovoObjetoAdicionadoEmLista(horarioContratoFP, horarios)) {
            return false;
        }

        return true;
    }

    public void removeHorarioCotratoFP(HorarioContratoFP h) {
        try {
            permitirManipularRegistro();
            horarios.remove(h);
            selecionado.setHorarioContratoFP(null);
            selecionado.setLotacaoFuncional(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void informarHorarioTrabalhoServidor(HorarioContratoFP horarioContrato) {
        horarioContratoFP = horarioContrato;
    }

    public void selecionarHorarioTrabalhoServidor(HorarioContratoFP horarioContrato) {
        horarioContratoFP = horarioContrato;
    }

    public void cancelarLotacaoFuncional() {
        hierarquiaOrganizacionalLotacaoFuncional = null;
        lotacaoFuncional = null;
    }

    public void novoLotacaoFuncional() {
        try {
            permitirManipularRegistro();
            if (jaExisteRegistroAdicionadoPelaReintegracao(selecionado.getLotacaoFuncional(), "lotação funcional")) {
                return;
            }
            lotacaoFuncional = new LotacaoFuncional();
            lotacaoFuncional.setInicioVigencia(selecionado.getDataReintegracao());
            lotacaoFuncional.setHorarioContratoFP(horarioContratoFP);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private boolean validarCamposObrigatoriosLotacaoFuncional() {
        boolean validou = true;
        if (hierarquiaOrganizacionalLotacaoFuncional == null) {
            FacesUtil.addCampoObrigatorio("É obrigatório informar uma unidade organizacional.");
            validou = false;
        }

        if (lotacaoFuncional.getInicioVigencia() == null) {
            FacesUtil.addCampoObrigatorio("É obrigatório informar o início de vigência.");
            validou = false;
        }
        return validou;
    }

    private void alterarUltimaLotacaoFuncional() {
        try {
            ordenarListaDescendentemente(horarios);
            HorarioContratoFP horarioAnterior = horarios.get(1); // Pegar o segundo horário, pois o primeiro é o que foi recentemente adicionado
            ordenarListaDescendentemente(horarioAnterior.getLocalTrabalho());
            LotacaoFuncional lfAnterior = horarioAnterior.getLocalTrabalho().get(0);
            selecionado.setLotacaoFuncionalAnterior(lfAnterior);
            lfAnterior.setFinalVigencia(DataUtil.getDataDiaAnterior(selecionado.getDataReintegracao()));
            horarioAnterior.setLocalTrabalho(Util.adicionarObjetoEmLista(horarioAnterior.getLocalTrabalho(), lfAnterior));
            selecionado.getContratoFP().setHorarioContratoFPs(Util.adicionarObjetoEmLista(horarios, horarioAnterior));
        } catch (IndexOutOfBoundsException ex) {
            return;
        }
    }

    public void confirmarLotacaoFuncional() {
        if (!validarCamposObrigatoriosLotacaoFuncional()) {
            return;
        }

        alterarUltimaLotacaoFuncional();
        lotacaoFuncional.setUnidadeOrganizacional(hierarquiaOrganizacionalLotacaoFuncional.getSubordinada());
        lotacaoFuncional.setVinculoFP(selecionado.getContratoFP());
        horarioContratoFP.setLocalTrabalho(Util.adicionarObjetoEmLista(horarioContratoFP.getLocalTrabalho(), lotacaoFuncional));
        selecionado.setLotacaoFuncional(lotacaoFuncional);
        cancelarLotacaoFuncional();
    }

    // ************************
    // SINDICATO
    // ************************
    public void novoSindicato() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            permitirManipularRegistro();
            if (jaExisteRegistroAdicionadoPelaReintegracao(selecionado.getSindicatoVinculoFP(), "sindicato")) {
                return;
            }
            sindicatoVinculoFP = new SindicatoVinculoFP();
            sindicatoVinculoFP.setVinculoFP(selecionado.getContratoFP());
            sindicatoVinculoFP.setInicioVigencia(selecionado.getDataReintegracao());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarSindicatoVinculoFP() {
        if (!podeConfirmarSindicatoVinculoFP()) {
            return;
        }

        alterarVigenciaUltimoRegistroLista(sindicatoVinculoFP, sindicatos);
        selecionado.setSindicatoAnterior(sindicatos == null || sindicatos.isEmpty() ? null : sindicatos.get(0));

        selecionado.setSindicatoVinculoFP(sindicatoVinculoFP);
        sindicatos = Util.adicionarObjetoEmLista(sindicatos, sindicatoVinculoFP);
        cancelarSindicatoVinculoFP();
    }

    public boolean podeConfirmarSindicatoVinculoFP() {
        if (!validarConfirmacao(sindicatoVinculoFP)) {
            return false;
        }
        if (!validarVigenciaNovoObjetoAdicionadoEmLista(sindicatoVinculoFP, sindicatos)) {
            return false;
        }
        return true;
    }

    public void cancelarSindicatoVinculoFP() {
        sindicatoVinculoFP = null;
    }

    public void removerSindicatoVinculoFP(SindicatoVinculoFP e) {
        try {
            permitirManipularRegistro();
            sindicatos.remove(e);
            selecionado.setSindicatoVinculoFP(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    // ************************
    // ASSOCIAÇÃO
    // ************************
    public void novaAssociacaoVinculoFP() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            permitirManipularRegistro();
            if (jaExisteRegistroAdicionadoPelaReintegracao(selecionado.getAssociacaoVinculoFP(), "associação")) {
                return;
            }
            associacaoVinculoFP = new AssociacaoVinculoFP();
            associacaoVinculoFP.setVinculoFP(selecionado.getContratoFP());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void confirmarAssociacaoVinculoFP() {
        if (!podeConfirmarAssociacaoVinculoFP()) {
            return;
        }
        alterarVigenciaUltimoRegistroLista(associacaoVinculoFP, associacoes);
        selecionado.setAssociacaoAnterior(associacoes == null || associacoes.isEmpty() ? null : associacoes.get(0));

        associacoes = Util.adicionarObjetoEmLista(associacoes, associacaoVinculoFP);
        cancelarAssociacaoVinculoFP();
        ordenarListaDescendentemente(associacoes);
    }

    public boolean podeConfirmarAssociacaoVinculoFP() {
        if (!validarConfirmacao(associacaoVinculoFP)) {
            return false;
        }
        if (!validarVigenciaNovoObjetoAdicionadoEmLista(associacaoVinculoFP, associacoes)) {
            return false;
        }
        return true;
    }

    public void cancelarAssociacaoVinculoFP() {
        associacaoVinculoFP = null;
    }

    public void selecionarAssociacaoVinculoFP(AssociacaoVinculoFP ass) {
        try {
            permitirManipularRegistro();
            associacaoVinculoFP = ass;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerAssociacaoVinculoFP(AssociacaoVinculoFP assVFP) {
        try {
            permitirManipularRegistro();
            associacoes.remove(assVFP);
            selecionado.setAssociacaoVinculoFP(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    // ************************
    // SITUAÇÃO FUNCIONAL
    // ************************
    private void validarCamposObrigatoriosReintegracao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContratoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor é obrigatório.");
        }
        if (selecionado.getTipoReintegracao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Reintegração é obrigatório.");
        }
        if (selecionado.getTipoReintegracao() != null && selecionado.getTipoReintegracao().equals(TipoReintegracao.REINTEGRACAO_DECISAO_JUDICIAL)
            && Strings.isNullOrEmpty(selecionado.getNumeroProcessoJudicial())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número do Processo Judicial é obrigatório para <b>" + selecionado.getTipoReintegracao().getDescricao());
        }
        if (selecionado.getTipoReintegracao() != null && selecionado.getTipoReintegracao().equals(TipoReintegracao.REINTEGRACAO_ANISTIA_LEGAL)
            && Strings.isNullOrEmpty(selecionado.getNumeroLeiAnistia())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número da Lei de Anistia é obrigatório para <b>" + selecionado.getTipoReintegracao().getDescricao());
        }
        if (selecionado.getInicioEfeitosFinanceiros() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início Efeitos Financeiros é obrigatório.");
        }
        if (selecionado.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal é obrigatório.");
        }
        if (selecionado.getDataReintegracao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data da Reintegração é obrigatório.");
        }
        if (selecionado.getPrevidenciaVinculoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar uma nova opção de previdência para o servidor.");
        }
        if (selecionado.getHorarioContratoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar um novo horário de trabalho para o servidor.");
        }
        if (selecionado.getLotacaoFuncional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar um local de trabalho (lotação funcional) para o novo horário de trabalho do servidor.");
        }
        ve.lancarException();
    }

    public boolean validarConfirmacao(ValidadorEntidade obj) {
        if (!Util.validaCampos(obj)) {
            return false;
        }
        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return false;
        }
        return true;
    }

    public Boolean getVerificaEdicao() {
        if ((operacao.equals(Operacoes.EDITAR))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isVisualizar() {
        return Operacoes.VER.equals(operacao);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reintegracao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public PrevidenciaVinculoFP getPrevidenciaVinculoFP() {
        return previdenciaVinculoFP;
    }

    public void setPrevidenciaVinculoFP(PrevidenciaVinculoFP previdenciaVinculoFP) {
        this.previdenciaVinculoFP = previdenciaVinculoFP;
    }

    public OpcaoValeTransporteFP getOpcaoValeTransporteFP() {
        return opcaoValeTransporteFP;
    }

    public void setOpcaoValeTransporteFP(OpcaoValeTransporteFP opcaoValeTransporteFP) {
        this.opcaoValeTransporteFP = opcaoValeTransporteFP;
    }

    public HorarioContratoFP getHorarioContratoFP() {
        return horarioContratoFP;
    }

    public void setHorarioContratoFP(HorarioContratoFP horarioContratoFP) {
        this.horarioContratoFP = horarioContratoFP;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalLotacaoFuncional() {
        return hierarquiaOrganizacionalLotacaoFuncional;
    }

    public void setHierarquiaOrganizacionalLotacaoFuncional(HierarquiaOrganizacional hierarquiaOrganizacionalLotacaoFuncional) {
        this.hierarquiaOrganizacionalLotacaoFuncional = hierarquiaOrganizacionalLotacaoFuncional;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public SindicatoVinculoFP getSindicatoVinculoFP() {
        return sindicatoVinculoFP;
    }

    public void setSindicatoVinculoFP(SindicatoVinculoFP sindicatoVinculoFP) {
        this.sindicatoVinculoFP = sindicatoVinculoFP;
    }

    public AssociacaoVinculoFP getAssociacaoVinculoFP() {
        return associacaoVinculoFP;
    }

    public void setAssociacaoVinculoFP(AssociacaoVinculoFP associacaoVinculoFP) {
        this.associacaoVinculoFP = associacaoVinculoFP;
    }

    public List<PrevidenciaVinculoFP> getPrevidencias() {
        return previdencias;
    }

    public void setPrevidencias(List<PrevidenciaVinculoFP> previdencias) {
        this.previdencias = previdencias;
    }

    public List<OpcaoValeTransporteFP> getOpcoesValeTransporte() {
        return opcoesValeTransporte;
    }

    public void setOpcoesValeTransporte(List<OpcaoValeTransporteFP> opcoesValeTransporte) {
        this.opcoesValeTransporte = opcoesValeTransporte;
    }

    public List<HorarioContratoFP> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioContratoFP> horarios) {
        this.horarios = horarios;
    }

    public List<SindicatoVinculoFP> getSindicatos() {
        return sindicatos;
    }

    public void setSindicatos(List<SindicatoVinculoFP> sindicatos) {
        this.sindicatos = sindicatos;
    }

    public List<AssociacaoVinculoFP> getAssociacoes() {
        return associacoes;
    }

    public void setAssociacoes(List<AssociacaoVinculoFP> associacoes) {
        this.associacoes = associacoes;
    }

    public List<SituacaoContratoFP> getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(List<SituacaoContratoFP> situacoes) {
        this.situacoes = situacoes;
    }

    public List<EnquadramentoFuncional> getEnquadramentos() {
        return enquadramentos;
    }

    public void setEnquadramentos(List<EnquadramentoFuncional> enquadramentos) {
        this.enquadramentos = enquadramentos;
    }

    public ConverterServidor getConverterContratoFP() {
        return new ConverterServidor();
    }

    public void atualizarDatas() {
        if (selecionado.getContratoFP() != null) {
            try {
                validarDataReintegracao();
                selecionado.getEnquadramentoFuncional().setInicioVigencia(selecionado.getDataReintegracao());
                exoneracao = recuperarExoneracaoRescisao();
                selecionado.getEnquadramentoAnterior().setFinalVigencia(exoneracao != null ? exoneracao.getDataRescisao() : selecionado.getDataReintegracao());
                situacaoContratoFP.setInicioVigencia(selecionado.getInicioEfeitosFinanceiros());
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    public void validarDataReintegracao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataReintegracao().compareTo(selecionado.getEnquadramentoAnterior().getInicioVigencia()) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Reintegração deve ser superior ao Início de Vigência (" +
                DataUtil.getDataFormatada(selecionado.getEnquadramentoAnterior().getInicioVigencia()) + ") do Enquadramento Funcional anterior.");
            limpar();
        }

        if (ve.temMensagens()) {
            throw ve;
        }

    }

    public void limpar() {
        selecionado.setContratoFP(null);
        setEnquadramentos(new ArrayList<EnquadramentoFuncional>());
        setPrevidencias(new ArrayList<PrevidenciaVinculoFP>());
        setOpcoesValeTransporte(new ArrayList<OpcaoValeTransporteFP>());
        setHorarios(new ArrayList<HorarioContratoFP>());
        setSindicatos(new ArrayList<SindicatoVinculoFP>());
        setAssociacoes(new ArrayList<AssociacaoVinculoFP>());
        setSituacoes(new ArrayList<SituacaoContratoFP>());
    }

    private void atribuirListasDoContratoParaListasControlador() {
        enquadramentos = selecionado.getContratoFP().getEnquadramentos();
        if(enquadramentos.isEmpty()){
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoRealizada("O Servidor não possui um Enquadramento Funcional em vigência");
            ve.lancarException();
        }
        previdencias = selecionado.getContratoFP().getPrevidenciaVinculoFPs();
        opcoesValeTransporte = selecionado.getContratoFP().getOpcaoValeTransporteFPs();
        recuperarHorariosDoContrato();
        sindicatos = selecionado.getContratoFP().getSindicatosVinculosFPs();
        associacoes = selecionado.getContratoFP().getAssociacaosVinculosFPs();
        situacoes = selecionado.getContratoFP().getSituacaoFuncionals();
    }

    public Boolean isContratoTemporario() {
        try {
            return selecionado.getContratoFP().getModalidadeContratoFP().getCodigo().compareTo(ModalidadeContratoFP.CONTRATO_TEMPORARIO) == 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean isCargoEmComissao() {
        try {
            return selecionado.getContratoFP().getModalidadeContratoFP().getCodigo().compareTo(ModalidadeContratoFP.CARGO_COMISSAO) == 0;
        } catch (Exception ex) {
            return false;
        }
    }


    private boolean isRegimeEstatutario() {
        try {
            return selecionado.getContratoFP().getModalidadeContratoFP().getCodigo().compareTo(ModalidadeContratoFP.CONCURSADOS) == 0;
        } catch (Exception ex) {
            return false;
        }
    }


    public void validarServidorSelecionado(ActionEvent ev) {
        try {
            if (selecionado.getContratoFP() == null) {
                return;
            }
            if (!isRegimeEstatutario() && !isContratoTemporario() && !isCargoEmComissao()) {
                FacesUtil.addOperacaoNaoPermitida("O servidor selecionado deve ser Estatutário ou Contrato Temporário ou Cargo em Comissão, para realizar a reintegração. Por favor Verifique as informações e tente novamente.");
                FacesUtil.executaJavaScript("limparCamposDoAutoComplete('" + ev.getComponent().getClientId() + "')");
                selecionado.setContratoFP(null);
                return;
            }

            if (!vinculoFPFacade.isVinculoFPVivo(selecionado.getContratoFP().getId())) {
                FacesUtil.addOperacaoNaoPermitida("O servidor selecionado possui registro de óbito. Por favor, selecione um servidor vivo.");
                FacesUtil.executaJavaScript("limparCamposDoAutoComplete('" + ev.getComponent().getClientId() + "')");
                selecionado.setContratoFP(null);
                return;
            }

            if ((!situacaoFuncionalFacade.isUltimaSituacaoFuncionalContrato(selecionado.getContratoFP().getId(), SituacaoFuncional.EXONERADO_RESCISO) && !situacaoFuncionalFacade.isUltimaSituacaoFuncionalContrato(selecionado.getContratoFP().getId(), SituacaoFuncional.APOSENTADO)) &&
                (!provimentoFPFacade.isUltimoProvimentoVinculoDoTipo(selecionado.getContratoFP().getId(), TipoProvimento.EXONERACAORESCISAO_CARREIRA) && !provimentoFPFacade.isUltimoProvimentoVinculoDoTipo(selecionado.getContratoFP().getId(), TipoProvimento.EXONERACAORESCISAO_COMISSAO))) {
                FacesUtil.addOperacaoNaoPermitida("O servidor selecionado não está exonerado nem aposentado.");
                FacesUtil.addOperacaoNaoPermitida("Para reintegrar um servidor é necessário que ele esteja exonerado por provimentos ou pela situação funcional.");
                FacesUtil.addOperacaoNaoPermitida("Verifique as informações e tente novamente.");
                FacesUtil.executaJavaScript("limparCamposDoAutoComplete('" + ev.getComponent().getClientId() + "')");
                selecionado.setContratoFP(null);
                return;
            }

            atribuirListasDoContratoParaListasControlador();
            ordenarListasContratoSelecionado();
            atribuirFinalVigenciaAnteriorParaReintegracao();
            criarNovoEnquadramentoFuncionalComBaseNoUltimoDoContratoSelecionado();
            criarNovaSituacaoComBaseNaUltimaDoContratoSelecionado();
            atribuirExoneracaoRescisao();

            validarDataReintegracao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void criarProvimentoParaContratoComReintegracao() {
        ProvimentoFP provimentoFP = new ProvimentoFP();
        provimentoFP.setVinculoFP(selecionado.getContratoFP());
        provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.REINTEGRACAO));
        provimentoFP.setDataProvimento(selecionado.getDataReintegracao());
        provimentoFP.setAtoLegal(selecionado.getAtoLegal());
        provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(selecionado.getContratoFP()));
        selecionado.setProvimentoFP(provimentoFP);
    }

    private void atribuirFinalVigenciaAnteriorParaReintegracao() {
        selecionado.setFimVigenciaAnterior(selecionado.getContratoFP().getFinalVigencia());
    }

    private void criarNovaSituacaoComBaseNaUltimaDoContratoSelecionado() {
        SituacaoContratoFP situacaoAntiga = situacoes.get(0); // Última Situação (lista ordenada decrescentemente)
        situacaoAntiga.setFinalVigencia(DataUtil.getDataDiaAnterior(selecionado.getDataReintegracao()));

        situacaoContratoFP.setInicioVigencia(selecionado.getDataReintegracao());
        situacaoContratoFP.setContratoFP(selecionado.getContratoFP());
        situacaoContratoFP.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.ATIVO_PARA_FOLHA));

        situacoes = Util.adicionarObjetoEmLista(situacoes, situacaoAntiga);
        situacoes = Util.adicionarObjetoEmLista(situacoes, situacaoContratoFP);

        selecionado.setSituacaoAnterior(situacaoAntiga);

        ordenarListaDescendentemente(situacoes);
    }

    private void criarNovoEnquadramentoFuncionalComBaseNoUltimoDoContratoSelecionado() {
        EnquadramentoFuncional enquadramentoAntigo = enquadramentos.get(0); // Último Enquadramento (lista ordenada decrescentemente)
        exoneracao = recuperarExoneracaoRescisao();
        enquadramentoAntigo.setFinalVigencia(exoneracao != null ? exoneracao.getDataRescisao() : selecionado.getDataReintegracao());
        EnquadramentoFuncional enquadramentoNovo = new EnquadramentoFuncional();
        enquadramentoNovo.setInicioVigencia(selecionado.getDataReintegracao());
        enquadramentoNovo.setFinalVigencia(null);
        enquadramentoNovo.setCategoriaPCS(enquadramentoAntigo.getCategoriaPCS());
        enquadramentoNovo.setContratoServidor(selecionado.getContratoFP());
        enquadramentoNovo.setProgressaoPCS(enquadramentoAntigo.getProgressaoPCS());
        enquadramentoNovo.setDataCadastro(selecionado.getDataReintegracao());

        enquadramentos = Util.adicionarObjetoEmLista(enquadramentos, enquadramentoAntigo);
        enquadramentos = Util.adicionarObjetoEmLista(enquadramentos, enquadramentoNovo);

        selecionado.setEnquadramentoFuncional(enquadramentoNovo);
        selecionado.setEnquadramentoAnterior(enquadramentoAntigo);
        ordenarListaDescendentemente(enquadramentos);
    }

    public void atribuirExoneracaoRescisao(){
        selecionado.setExoneracaoRescisao(recuperarExoneracaoRescisao());
    }

    private ExoneracaoRescisao recuperarExoneracaoRescisao() {
        if (selecionado.getContratoFP() != null) {
            return exoneracaoRescisaoFacade.buscarExoneracaoRescisaoContratoNotReintegracao(selecionado.getContratoFP().getId());
        }
        return null;
    }

    public void ordenarListaDescendentemente(List lista) {
        Collections.sort(lista, Collections.reverseOrder());
    }

    public void ordenarListasContratoSelecionado() {
        ordenarListaDescendentemente(enquadramentos);
        ordenarListaDescendentemente(previdencias);
        ordenarListaDescendentemente(opcoesValeTransporte);
        ordenarListaDescendentemente(horarios);
        ordenarListaDescendentemente(sindicatos);
        ordenarListaDescendentemente(associacoes);
        ordenarListaDescendentemente(situacoes);
    }

    public BigDecimal recuperarValorEnquadramento(EnquadramentoFuncional ef) {
        return enquadramentoFuncionalFacade.getValorEnquadramentoFuncional(ef.getCategoriaPCS().getId(), ef.getProgressaoPCS().getId(), ef.getFinalVigencia() != null ? ef.getFinalVigencia() : selecionado.getDataReintegracao());
    }

    public String getMensagemObservacaoDoRegistro(ValidadorVigencia objDaReintegracao, ValidadorVigencia objDaVez, List<? extends ValidadorVigencia> lista) {
        try {
            if (objDaReintegracao.equals(objDaVez)) {
                return "Registro adicionado por esta reintegração";
            }
        } catch (NullPointerException npe) {
        }


        try {
            int idx = lista.indexOf(objDaReintegracao);
            ValidadorVigencia objAnterior = lista.get(idx + 1);
            if (!objAnterior.equals(objDaVez)) {
                return "";
            }
            if (DataUtil.getDataDiaAnterior(objDaReintegracao.getInicioVigencia()).compareTo(objAnterior.getFimVigencia()) == 0) {
                return "Registro alterado automaticamente por esta reintegração";
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
        }

        return "";
    }

    public ValidadorVigencia alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReintegracao(ValidadorVigencia objDaReintegracao, List<? extends ValidadorVigencia> lista, Date valorParaSetar) {
        try {
            int idx = lista.indexOf(objDaReintegracao);
            ValidadorVigencia objAnterior = lista.get(idx + 1);
            if (DataUtil.getDataDiaAnterior(objDaReintegracao.getInicioVigencia()).compareTo(objAnterior.getFimVigencia()) == 0) {
                objAnterior.setFimVigencia(valorParaSetar);
                return objAnterior;
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
        }
        return null;
    }

    private void atribuirFimDeVigenciaDoContratoNaLotacaoFuncionalAnterior() {
        try {
            ordenarListaDescendentemente(horarios);
            HorarioContratoFP horarioAnterior = horarios.get(1); // Pegar o segundo horário, pois o primeiro é o que foi adicionado pela reintegração
            ordenarListaDescendentemente(horarioAnterior.getLocalTrabalho());
            LotacaoFuncional lfAnterior = horarioAnterior.getLocalTrabalho().get(0);
            selecionado.setLotacaoFuncionalAnterior(lfAnterior);
            lfAnterior.setFinalVigencia(selecionado.getFimVigenciaAnterior());
            horarioAnterior.setLocalTrabalho(Util.adicionarObjetoEmLista(horarioAnterior.getLocalTrabalho(), lfAnterior));
            selecionado.getContratoFP().setHorarioContratoFPs(Util.adicionarObjetoEmLista(horarios, horarioAnterior));
        } catch (IndexOutOfBoundsException ex) {
            return;
        }
    }

    @Override
    public void excluir() {
        try {
            permitirManipularRegistro();
            atribuirListasDoContratoParaListasControlador();
            ordenarListasContratoSelecionado();
            atribuirFimDeVigenciaDoContratoNaLotacaoFuncionalAnterior();

            selecionado.setEnquadramentoAnterior((EnquadramentoFuncional) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReintegracao(selecionado.getEnquadramentoFuncional(), enquadramentos, selecionado.getFimVigenciaAnterior()));
            selecionado.setPrevidenciaAnterior((PrevidenciaVinculoFP) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReintegracao(selecionado.getPrevidenciaVinculoFP(), previdencias, selecionado.getFimVigenciaAnterior()));
            selecionado.setOpcaoValeTransporteAnterior((OpcaoValeTransporteFP) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReintegracao(selecionado.getOpcaoValeTransporteFP(), opcoesValeTransporte, selecionado.getFimVigenciaAnterior()));
            selecionado.setHorarioAnterior((HorarioContratoFP) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReintegracao(selecionado.getHorarioContratoFP(), horarios, selecionado.getFimVigenciaAnterior()));
            selecionado.setSindicatoAnterior((SindicatoVinculoFP) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReintegracao(selecionado.getSindicatoVinculoFP(), sindicatos, selecionado.getFimVigenciaAnterior()));
            selecionado.setAssociacaoAnterior((AssociacaoVinculoFP) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReintegracao(selecionado.getAssociacaoVinculoFP(), associacoes, selecionado.getFimVigenciaAnterior()));

            try {
                reintegracaoFacade.remover(selecionado);
                FacesUtil.addOperacaoRealizada("Registro excluído com sucesso.");
                redireciona();
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getTipoReintegracao() {
        return Util.getListSelectItem(TipoReintegracao.values());
    }

    public boolean isProcessoJudicialObrigatorio() {
        try {
            return TipoReintegracao.REINTEGRACAO_DECISAO_JUDICIAL.equals(selecionado.getTipoReintegracao());
        } catch (NullPointerException ne) {
            return false;
        }
    }

    public boolean isLeiAnistiaObrigatorio() {
        try {
            return TipoReintegracao.REINTEGRACAO_ANISTIA_LEGAL.equals(selecionado.getTipoReintegracao());
        } catch (NullPointerException ne) {
            return false;
        }
    }

    public class ConverterServidor implements Serializable, Converter {
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
            try {
                ContratoFP c = contratoFPFacade.recuperarParaReintegracao(Long.parseLong(s));
                return c;
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
            return "" + o;
        }
    }

    public ExoneracaoRescisao getExoneracao() {
        return exoneracao;
    }

    public void setExoneracao(ExoneracaoRescisao exoneracao) {
        this.exoneracao = exoneracao;
    }
}
