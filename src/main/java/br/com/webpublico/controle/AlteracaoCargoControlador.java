package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.DateSelectEvent;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 13/07/15
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class AlteracaoCargoControlador extends PrettyControlador<AlteracaoCargo> implements Serializable, CRUD {

    // modalidade contrato
    public static final Long CONTRATO_TEMPORARIO = 4l;
    public static final Long CONCURSADOS = 1l;
    public TipoPCS tipoPCS;
    @EJB
    private AlteracaoCargoFacade alteracaoCargoFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private TipoRegimeFacade tipoRegimeFacade;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    private ContratoFP contratoFPSelecionado;
    private ContratoFPCargo contratoFPCargoSelecionado;
    private LotacaoFuncional lotacaoFuncionalSelecionado;
    private EnquadramentoFuncional enquadramentoFuncionalSelecionado;
    private List<ContratoFPCargo> cargos;
    private List<LotacaoFuncional> lotacoes;
    private List<EnquadramentoFuncional> enquadramentos;
    private PlanoCargosSalarios planoCargosSalariosSelecionado;
    private List<PlanoCargosSalarios> planos;
    private CategoriaPCS categoriaPCSfilha;
    private ProgressaoPCS progressaoPCSPai;
    private EnquadramentoPCS enquadramentoPCS;
    private ConverterAutoComplete converterModalidade;
    private ConverterAutoComplete converterRegime;
    private AlteracaoCargo alteracaoCargo;

    public AlteracaoCargoControlador() {
        super(AlteracaoCargo.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/alteracao-cargo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return alteracaoCargoFacade;
    }

    @Override
    public void novo() {
        super.novo();
        inicializarDados();
        alteracaoCargo = new AlteracaoCargo();
    }

    public void inicializarDados() {
        selecionado.setData(alteracaoCargoFacade.getSistemaFacade().getDataOperacao());
        selecionado.getProvimentoFP().setDataProvimento(alteracaoCargoFacade.getSistemaFacade().getDataOperacao());
        selecionado.getProvimentoFP().setSequencia(alteracaoCargoFacade.getProvimentoFPFacade().geraSequenciaPorVinculoFP(selecionado.getProvimentoFP().getVinculoFP()));

        instanciarPlanos();
        enquadramentoPCS = new EnquadramentoPCS();
        tipoPCS = TipoPCS.QUADRO_EFETIVO;
    }

    @Override
    public void editar() {
        super.editar();

        inicializarDadosAoEditarOrVer();
        ordenarListas();
        alteracaoCargo = new AlteracaoCargo();
    }

    public void inicializarDadosAoEditarOrVer() {
        contratoFPSelecionado = selecionado.getProvimentoFP().getVinculoFP().getContratoFP();
        cargos = selecionado.getProvimentoFP().getVinculoFP().getContratoFP().getCargos();
        lotacoes = selecionado.getProvimentoFP().getVinculoFP().getLotacaoFuncionals();
        instanciarPlanos();
        enquadramentos = selecionado.getProvimentoFP().getVinculoFP().getContratoFP().getEnquadramentos();
    }

    public void instanciarPlanos() {
        planos = new ArrayList<>();
    }

    private void ordenarListas() {
        ordenarCargosPorInicioVigenciaAsc();
        ordenarLotacoesPorInicioVigenciaAsc();
        ordenarEnquadramentosFuncionaisAsc();
    }

    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (podeSalvar()) {
            try {
                ContratoFP contrato = selecionado.getProvimentoFP().getVinculoFP().getContratoFP();
                selecionado.setData(selecionado.getProvimentoFP().getDataProvimento());
                removerCargoAdicionadoDaLista();
                removerLotacaoFuncionalAdicionadoDaLista();
                removerEnquadramentoFuncionalAdicionadoDaLista();

                alteracaoCargoFacade.salvar(selecionado, contrato);
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);
            }
            redireciona();
        }
    }

    @Override
    public void excluir() {
        try {
            if (podeExcluir()) {
                ContratoFP contrato = selecionado.getProvimentoFP().getVinculoFP().getContratoFP();
                removerCargoAdicionadoDaLista();
                removerLotacaoFuncionalAdicionadoDaLista();
                removerEnquadramentoFuncionalAdicionadoDaLista();

                cancelarFinalVigenciaDoUltimoCargoVigente(contrato);
                cancelarFinalVigenciaDaUltimaLotacaoFuncionalVigente(contrato);
                cancelarFinalVigenciaDoUltimoEnquadramentoFuncionalVigente(contrato);

                alteracaoCargoFacade.excluir(selecionado, contrato);
                redireciona();
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            }
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void cancelarFinalVigenciaDoUltimoEnquadramentoFuncionalVigente(ContratoFP contrato) {
        EnquadramentoFuncional ultimoEnquadramentoFuncionalVigente = contrato.getUltimoEnquadramentoFuncionalEmRelacaoAoInicioDeVigencia();
        if (ultimoEnquadramentoFuncionalVigente != null) {
            ultimoEnquadramentoFuncionalVigente.setFinalVigencia(null);
        }
    }

    private void cancelarFinalVigenciaDaUltimaLotacaoFuncionalVigente(ContratoFP contrato) {
        LotacaoFuncional ultimaLotacaoFuncionalVigente = contrato.getUltimaLotacaoFuncionalEmRelacaoAoInicioDeVigencia();
        if (ultimaLotacaoFuncionalVigente != null) {
            ultimaLotacaoFuncionalVigente.setFinalVigencia(null);
        }
    }

    private void cancelarFinalVigenciaDoUltimoCargoVigente(ContratoFP contrato) {
        ContratoFPCargo ultimoCargoVigente = contrato.getUltimoCargoEmRelacaoAoInicioDeVigencia();
        contrato.setCargo(ultimoCargoVigente.getCargo());
        if (ultimoCargoVigente != null) {
            ultimoCargoVigente.setFimVigencia(null);
        }
    }

    private boolean podeExcluir() {
        Integer ano = DataUtil.getAno(selecionado.getData());
        Integer mes = DataUtil.getMes(selecionado.getData());

        FichaFinanceiraFP fichaFinanceiraFP = alteracaoCargoFacade.getFichaFinanceiraFPFacade().recuperaFichaFinanceiraFPPorVinculoFPMesAno(selecionado.getProvimentoFP().getVinculoFP(), ano, mes);
        if (fichaFinanceiraFP != null) {
            FacesUtil.addOperacaoNaoPermitida("Foi encontrado cálculo de folha efetivada com a data de cadastro do enquadramento. É impossível excluir este registro!");
            return false;
        }

        return true;
    }

    public void removerEnquadramentoFuncionalAdicionadoDaLista() {
        if (selecionado.temEnquadramentoFuncional()) {
            if (enquadramentos.contains(selecionado.getEnquadramentoFuncional())) {
                enquadramentos.remove(selecionado.getEnquadramentoFuncional());
            }
        }
    }

    private void removerLotacaoFuncionalAdicionadoDaLista() {
        if (selecionado.getLotacaoFuncional() != null) {
            if (lotacoes.contains(selecionado.getLotacaoFuncional())) {
                lotacoes.remove(selecionado.getLotacaoFuncional());
            }
        }
    }

    private void removerCargoAdicionadoDaLista() {
        if (selecionado.getContratoFPCargo() != null) {
            if (cargos.contains(selecionado.getContratoFPCargo())) {
                cargos.remove(selecionado.getContratoFPCargo());
            }
        }
    }

    private boolean podeSalvar() {
        if (!validarConfirmacao(selecionado.getProvimentoFP())) {
            return false;
        }
        if (!temCargoAdicionado() && !temLotacaoFuncionalAdicionada()) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório adicionar novo cargo ou nova lotação funcional.");
            return false;
        }
        if (selecionado.temCargo() && !selecionado.temEnquadramentoFuncional()) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório adicionar novo enquadramento funcional quando adicionar novo cargo.");
            return false;
        }
        return true;
    }

    private boolean temLotacaoFuncionalAdicionada() {
        return selecionado.getLotacaoFuncional() != null;
    }

    private boolean temCargoAdicionado() {
        return selecionado.getContratoFPCargo() != null;
    }

    private boolean validarConfirmacao(ValidadorEntidade obj) {
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

    public List<ContratoFP> getContratosFPConcursadosAndContratoTemporario(String parte) {
        List<Long> codigos = getCodigoModalidadeConcursadosAndContratoTemporario();
        return alteracaoCargoFacade.getContratoFPFacade().recuperaContratoVigentePorModalidades(parte.trim(), codigos);
    }

    public List<Long> getCodigoModalidadeConcursadosAndContratoTemporario() {
        return Arrays.asList(CONCURSADOS, CONTRATO_TEMPORARIO);
    }

    public List<SelectItem> getTipoRegimes() {
        return Util.getListSelectItemSemCampoVazio(tipoRegimeFacade.lista().toArray());
    }

    public List<SelectItem> getModalidadesContratoFP() {
        return Util.getListSelectItemSemCampoVazio(modalidadeContratoFPFacade.buscarTodasAsModalidades().toArray());
    }

    public List<ContratoFP> getContratosFPCargoEmComissao(String parte) {
        List<Long> codigos = new ArrayList<>();
        codigos.add(Long.valueOf(2));
        return alteracaoCargoFacade.getContratoFPFacade().recuperaContratoVigentePorModalidades(parte.trim(), codigos);
    }

    public List<AtoLegal> getAtosLegais(String parte) {
        return alteracaoCargoFacade.getAtoLegalFacade().listaFiltrandoAtoLegalPorPropositoAtoLegal(parte.trim(), PropositoAtoLegal.ATO_DE_PESSOAL);
    }

    public ConverterServidor getConverterContratoFP() {
        return new ConverterServidor();
    }

    public ConverterAutoComplete getConverterCbo() {
        return new ConverterAutoComplete(CBO.class, alteracaoCargoFacade.getCboFacade());
    }

    public ConverterAutoComplete getConverterPlanoCargosSalarios() {
        return new ConverterAutoComplete(PlanoCargosSalarios.class, alteracaoCargoFacade.getPlanoCargosSalariosFacade());
    }

    public void carregaServidor() {
        anularSelecionados();
        selecionado.getProvimentoFP().setVinculoFP(alteracaoCargoFacade.getContratoFPFacade().recuperarParaTelaAlteracaoDeCargo(contratoFPSelecionado.getId()));
        if (isServidorValido()) {
            inicializarListasDoControlador();
            cargos = selecionado.getProvimentoFP().getVinculoFP().getContratoFP().getCargos();
            lotacoes = selecionado.getProvimentoFP().getVinculoFP().getLotacaoFuncionals();
            enquadramentos = selecionado.getProvimentoFP().getVinculoFP().getContratoFP().getEnquadramentos();
            ordenarListas();
        }
    }

    private void anularSelecionados() {
        contratoFPCargoSelecionado = null;
        lotacaoFuncionalSelecionado = null;
        enquadramentoFuncionalSelecionado = null;

        selecionado.setContratoFPCargo(null);
        selecionado.setLotacaoFuncional(null);
        selecionado.setEnquadramentoFuncional(null);
    }

    private boolean isServidorValido() {
        SituacaoContratoFP situacaoContratoFPVigente = getSituacaoContratoFPVigente();
        if (situacaoContratoFPVigente.isEmFerias()) {
            FacesUtil.addWarn("Atenção!", "O servidor está em período de férias.");
            return true;
        }
        if (!situacaoContratoFPVigente.isEmExercicio()) {
            FacesUtil.addOperacaoNaoPermitida(getMensagem());
            cancelarContratoFPSelecionado();
            inicializarListasDoControlador();
            return false;
        }
        return true;
    }

    private void cancelarContratoFPSelecionado() {
        contratoFPSelecionado = null;
        selecionado.getProvimentoFP().setVinculoFP(null);
    }

    public String getMensagem() {
        SituacaoContratoFP situacaoContratoFPVigente = getSituacaoContratoFPVigente();
        return "Este servidor está com situação funcional "
            + situacaoContratoFPVigente.getSituacaoFuncional().getCodigo()
            + " - " + situacaoContratoFPVigente.getSituacaoFuncional().getDescricao()
            + ". É impossível realizar o enquadramento!";
    }

    private SituacaoContratoFP getSituacaoContratoFPVigente() {
        return alteracaoCargoFacade.getContratoFPFacade().recuperaSituacaoVigenteContratoFP(selecionado.getProvimentoFP().getVinculoFP().getContratoFP(), selecionado.getData());
    }

    private void inicializarListasDoControlador() {
        cargos = new ArrayList<>();
        lotacoes = new ArrayList<>();
        enquadramentos = new ArrayList<>();
    }

    public void novoCargo() {
        contratoFPCargoSelecionado = new ContratoFPCargo();
        contratoFPCargoSelecionado.setContratoFP(selecionado.getProvimentoFP().getVinculoFP().getContratoFP());
        selecionado.setContratoFPCargo(contratoFPCargoSelecionado);
    }

    public void confirmarCargo() {
        if (podeConfirmarCargo()) {
            adicionarCargoNaLista();
            cancelarCargo();
            validarEnquadramentoFuncional();
        }
    }

    private void validarEnquadramentoFuncional() {
        if (selecionado.temEnquadramentoFuncional()) {
            if (!enquadramentoFuncionalAdicionadoTemVinculoComCargo(selecionado.getContratoFPCargo().getCargo())) {
                FacesUtil.addAtencao("Devido o enquadramento funcional já adicionado não estar relacionado ao cargo <b>" + selecionado.getContratoFPCargo().getCargo().toString() + "</b>.</br>O enquadramento funcional <b>" + selecionado.getEnquadramentoFuncional().toString() + "</b> será removido.");
                removerEnquadramentoFuncional(selecionado.getEnquadramentoFuncional());
            }
        }
    }

    private boolean enquadramentoFuncionalAdicionadoTemVinculoComCargo(Cargo cargo) {
        EnquadramentoFuncional enquadramentoFuncionalAdicionado = selecionado.getEnquadramentoFuncional();
        enquadramentoFuncionalAdicionado.setCategoriaPCS(categoriaPCSFacade.recuperar(enquadramentoFuncionalAdicionado.getCategoriaPCS().getId()));
        for (CargoCategoriaPCS cargoCategoriaPCS : enquadramentoFuncionalAdicionado.getCategoriaPCS().getCargosCategoriaPCS()) {
            if (cargoCategoriaPCS.getCargo().equals(cargo)) {
                return true;
            }
        }
        return false;
    }

    public void cancelarCargo() {
        if (contratoFPCargoSelecionado.isEditando()) {
            if (!podeConfirmarCargo()) {
                return;
            }
            contratoFPCargoSelecionado.setOperacao(null);
            adicionarCargoNaLista();
        }
        if (!selecionado.getProvimentoFP().getVinculoFP().getContratoFP().temContratoFPCargoAdicionado(selecionado.getContratoFPCargo())) {
            selecionado.setContratoFPCargo(null);
        }
        contratoFPCargoSelecionado = null;
    }

    public void selecionarCargo(ContratoFPCargo contratoFPCargo) {
        contratoFPCargoSelecionado = contratoFPCargo;
        contratoFPCargoSelecionado.setOperacao(Operacoes.EDITAR);

        cargos.remove(contratoFPCargo);
    }

    public void removerCargo(ContratoFPCargo contratoFPCargo) {
        cargos.remove(contratoFPCargo);
        selecionado.setContratoFPCargo(null);
        if (!CollectionUtils.isEmpty(cargos)) {
            cargos.get(0).setFimVigencia(null);
        }
        removerEnquadramentoFuncional();
    }

    private void removerEnquadramentoFuncional() {
        if (selecionado.temEnquadramentoFuncional()) {
            removerEnquadramentoFuncional(selecionado.getEnquadramentoFuncional());
        }
    }

    private boolean podeConfirmarCargo() {
        if (!isCargoInformado()) {
            FacesUtil.addCampoObrigatorio("O campo cargo é obrigatório.");
            return false;
        }
        if (!isCboInformado()) {
            FacesUtil.addCampoObrigatorio("O campo cbo é obrigatório.");
            return false;
        }
        if (!isInicioVigenciaInformada()) {
            FacesUtil.addCampoObrigatorio("O campo início de vigência é obrigatório.");
            return false;
        }
        if (!DataUtil.isVigenciaValida(contratoFPCargoSelecionado, cargos)) {
            return false;
        }
        return true;
    }

    private boolean isCargoInformado() {
        return contratoFPCargoSelecionado.getCargo() != null;
    }

    public boolean isCboInformado() {
        return contratoFPCargoSelecionado.getCbo() != null;
    }

    public boolean isInicioVigenciaInformada() {
        return contratoFPCargoSelecionado.getInicioVigencia() != null;
    }

    public boolean desabilitarBotaoNovoCargo() {
        if (!isServidorInformado()) {
            return true;
        }
        if (temCargoSelecionado()) {
            return true;
        }
        if (selecionado.getProvimentoFP().getVinculoFP().getContratoFP().temContratoFPCargoAdicionado(selecionado.getContratoFPCargo())) {
            return true;
        }
        return false;
    }

    public boolean desabilitarBotaoNovaLotacaoFuncional() {
        if (!isServidorInformado()) {
            return true;
        }
        if (temLotacaoFuncionalSelecionado()) {
            return true;
        }
        if (selecionado.getProvimentoFP().getVinculoFP().getContratoFP().temLotacaoFuncionalAdicionada(alteracaoCargo.getLotacaoFuncional())) {
            return true;
        }
        return false;
    }

    public void adicionarCargoNaLista() {
        cargos = Util.adicionarObjetoEmLista(cargos, contratoFPCargoSelecionado);
        ordenarCargosPorInicioVigenciaAsc();
    }

    private void ordenarCargosPorInicioVigenciaAsc() {
        Collections.sort(cargos, new Comparator<ContratoFPCargo>() {
            @Override
            public int compare(ContratoFPCargo o1, ContratoFPCargo o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    public void novaLotacaoFuncional() {
        lotacaoFuncionalSelecionado = new LotacaoFuncional();
        lotacaoFuncionalSelecionado.setVinculoFP(selecionado.getProvimentoFP().getVinculoFP());
        lotacaoFuncionalSelecionado.setDataRegistro(selecionado.getProvimentoFP().getDataProvimento());
        lotacaoFuncionalSelecionado.setHorarioContratoFP(new HorarioContratoFP());
        if (!lotacoes.isEmpty()) {
            lotacaoFuncionalSelecionado.setUnidadeOrganizacional(lotacoes.get(lotacoes.size() - 1).getUnidadeOrganizacional());
        }
        alteracaoCargo.setLotacaoFuncional(lotacaoFuncionalSelecionado);
    }

    public void confirmarLotacaoFuncional() {
        if (podeConfirmarLotacaoFuncional()) {
            setaDatasHorarioContratoFP();
            adicionarLotacaoFuncionalNaLista();
            selecionado.setLotacaoFuncional(alteracaoCargo.getLotacaoFuncional());
            cancelarLotacaoFuncional();
        }
    }

    public void cancelarLotacaoFuncional() {
        if (lotacaoFuncionalSelecionado.isEditando()) {
            if (!podeConfirmarLotacaoFuncional()) {
                return;
            }
            lotacaoFuncionalSelecionado.setOperacao(null);
            adicionarLotacaoFuncionalNaLista();
        }
        if (!selecionado.getProvimentoFP().getVinculoFP().getContratoFP().temLotacaoFuncionalAdicionada(selecionado.getLotacaoFuncional())) {
            selecionado.setLotacaoFuncional(null);
            alteracaoCargo.setLotacaoFuncional(null);
        }
        lotacaoFuncionalSelecionado = null;
    }

    public void selecionarLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        lotacaoFuncionalSelecionado = lotacaoFuncional;
        lotacaoFuncionalSelecionado.setOperacao(Operacoes.EDITAR);

        lotacoes.remove(lotacaoFuncional);
    }

    public void removerLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        lotacoes.remove(lotacaoFuncional);
        selecionado.setLotacaoFuncional(null);
        if (!CollectionUtils.isEmpty(lotacoes)) {
            lotacoes.get(0).setFinalVigencia(null);
        }
    }

    public String getDescricaoRecursoDoVinculoFPVigente() {
        RecursoDoVinculoFP recursoDoVinculoFPVigente = selecionado.getProvimentoFP().getVinculoFP().getContratoFP().getRecursoDoVinculoFPVigente(selecionado.getProvimentoFP().getDataProvimento());
        if (recursoDoVinculoFPVigente != null) {
            return recursoDoVinculoFPVigente.getDescricao();
        }
        return "";
    }

    private void setaDatasHorarioContratoFP() {
        lotacaoFuncionalSelecionado.getHorarioContratoFP().setInicioVigencia(lotacaoFuncionalSelecionado.getInicioVigencia());
        if (lotacaoFuncionalSelecionado.getFinalVigencia() != null) {
            lotacaoFuncionalSelecionado.getHorarioContratoFP().setFinalVigencia(lotacaoFuncionalSelecionado.getFinalVigencia());
        }
    }

    private void adicionarLotacaoFuncionalNaLista() {
        lotacoes = Util.adicionarObjetoEmLista(lotacoes, lotacaoFuncionalSelecionado);
        ordenarLotacoesPorInicioVigenciaAsc();
    }

    private void ordenarLotacoesPorInicioVigenciaAsc() {
        Collections.sort(lotacoes, new Comparator<LotacaoFuncional>() {
            @Override
            public int compare(LotacaoFuncional o1, LotacaoFuncional o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    private boolean podeConfirmarLotacaoFuncional() {
        if (lotacaoFuncionalSelecionado.getInicioVigencia() == null) {
            FacesUtil.addCampoObrigatorio("O campo início de vigência é obrigatório.");
            return false;
        }
        if (lotacaoFuncionalSelecionado.getHorarioContratoFP().getHorarioDeTrabalho() == null) {
            FacesUtil.addCampoObrigatorio("O campo horário de trabalho é obrigatório.");
            return false;
        }
        if (!DataUtil.isVigenciaValida(lotacaoFuncionalSelecionado, lotacoes)) {
            return false;
        }
        return true;
    }

    public boolean isServidorInformado() {
        return contratoFPSelecionado != null;
    }

    public List<Cargo> completarCargoPorTipoPCS(String parte) {
        return alteracaoCargoFacade.getCargoFacade().buscarCargosVigentesPorUnidadesUsuarioAndUnidadeOrganizacional(parte.trim(), this.contratoFPSelecionado.getUnidadeOrganizacional().getId(), tipoPCS);
    }

    public void cancelarCBO() {
        contratoFPCargoSelecionado.setCbo(null);
        List<CBO> cbos = getCbos("");
        if (cbos.size() == 1) {
            contratoFPCargoSelecionado.setCbo(cbos.get(0));
        }
    }

    public List<CBO> getCbos(String parte) {
        if (contratoFPCargoSelecionado.getCargo() == null) {
            FacesUtil.addOperacaoNaoPermitida("É necessário informar um cargo no campo anterior para poder listar os CBOs.");
            return new ArrayList<>();
        }
        return alteracaoCargoFacade.getCboFacade().listarFiltrandoPorCargo(parte.trim(), contratoFPCargoSelecionado.getCargo());
    }

    public void novoEnquadramentoFuncional() {
        enquadramentoFuncionalSelecionado = new EnquadramentoFuncional();
        enquadramentoFuncionalSelecionado.setContratoServidor(selecionado.getProvimentoFP().getVinculoFP().getContratoFP());
        enquadramentoFuncionalSelecionado.setDataCadastro(selecionado.getProvimentoFP().getDataProvimento());
        enquadramentoFuncionalSelecionado.setProvimentoFP(selecionado.getProvimentoFP());
        selecionado.setEnquadramentoFuncional(enquadramentoFuncionalSelecionado);
    }

    public boolean desabilitarBotaoNovoEnquadramentoFuncional() {
        if (!isServidorInformado()) {
            return true;
        }
        if (!selecionado.temCargo()) {
            return true;
        }
        if (temEnquadramentoFuncionalSelecionado()) {
            return true;
        }
        if (selecionado.getProvimentoFP().getVinculoFP().getContratoFP().temEnquadramentoFuncionalAdicionado(selecionado.getEnquadramentoFuncional())) {
            return true;
        }
        return false;
    }

    public boolean desabilitarBotaoEditarCargo(ContratoFPCargo cargo) {
        if (temCargoSelecionado() || alteracaoTemCargoAndCargoNaoEstaRelacionadaAEstaAlteracao(cargo)) {
            return true;
        }
        return false;
    }

    private boolean temCargoSelecionado() {
        return contratoFPCargoSelecionado != null;
    }

    private boolean alteracaoTemCargoAndCargoNaoEstaRelacionadaAEstaAlteracao(ContratoFPCargo cargo) {
        return selecionado.temCargo() && !cargo.isOrigemAlteracaoCargo(selecionado);
    }

    public boolean desabilitarBotaoEditarLotacaoFuncional(LotacaoFuncional lotacao) {
        if (temLotacaoFuncionalSelecionado() || alteracaoTemLotacaoFuncionalAndLotacaoNaoEstaRelacionadaAEstaAlteracao(lotacao)) {
            return true;
        }
        return false;
    }

    private boolean alteracaoTemLotacaoFuncionalAndLotacaoNaoEstaRelacionadaAEstaAlteracao(LotacaoFuncional lotacao) {
        return selecionado.temLotacaoFuncional() && !lotacao.isOrigemAlteracaoCargo(selecionado);
    }

    private boolean temLotacaoFuncionalSelecionado() {
        return lotacaoFuncionalSelecionado != null;
    }

    public boolean desabilitarBotaoEditarEnquadramentoFuncional(EnquadramentoFuncional ef) {
        if (!selecionado.temCargo() || temEnquadramentoFuncionalSelecionado() || alteracaoTemEnquadramentoFuncionalAndEnquadramentoNaoEstaRelacionadoEstaAlteracao(ef)) {
            return true;
        }
        return false;
    }

    private boolean alteracaoTemEnquadramentoFuncionalAndEnquadramentoNaoEstaRelacionadoEstaAlteracao(EnquadramentoFuncional ef) {
        return selecionado.temEnquadramentoFuncional() && !ef.isOriginadoDaAlteracaoCargo(selecionado);
    }

    public void carregarPlanosDeCargosEsalariosParaDateSelect(DateSelectEvent event) {
        enquadramentoFuncionalSelecionado.setInicioVigencia(event.getDate());
        limparPlanos();
        carregarPlanos();
        cancelarPlanoCargosSalariosAndCategoriaAndProgressaoAndReferenciaAndValor();
    }

    public void carregarPlanosDeCargosEsalariosParaValueChange(AjaxBehaviorEvent event) {
        enquadramentoFuncionalSelecionado.setInicioVigencia((Date) event.getComponent().getAttributes().get("value"));
        limparPlanos();
        carregarPlanos();
        cancelarPlanoCargosSalariosAndCategoriaAndProgressaoAndReferenciaAndValor();
    }

    private void limparPlanos() {
        planos.clear();
    }

    public void carregarPlanos() {
        if (selecionado.temCargo()) {
            List<CategoriaPCS> categorias = alteracaoCargoFacade.getCategoriaPCSFacade().buscarCategoriasNoEnquadramentoFuncionalPorCargoAndDataVigencia(selecionado.getContratoFPCargo().getCargo(), enquadramentoFuncionalSelecionado.getInicioVigencia());
            for (CategoriaPCS categoriaPCS : categorias) {
                planos.add(categoriaPCS.getPlanoCargosSalarios());
            }
        }
    }

    public List<SelectItem> getPlanos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (PlanoCargosSalarios plano : planos) {
            toReturn.add(new SelectItem(plano, plano.toString()));
        }
        return toReturn;
    }

    public void setPlanos(List<PlanoCargosSalarios> planos) {
        this.planos = planos;
    }

    public List<SelectItem> getCategoriasPCS() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (enquadramentoFuncionalSelecionado.temInicioDeVigencia() && this.temPlanoCargosSalariosSelecionado()) {
            if (planoCargosSalariosSelecionado.getId() != null) {
                for (CategoriaPCS object : alteracaoCargoFacade.getCategoriaPCSFacade().recuperaCategoriasNoEnquadramentoFuncionalVigente(planoCargosSalariosSelecionado, enquadramentoFuncionalSelecionado.getInicioVigencia())) {

                    CategoriaPCS cat = new CategoriaPCS();
                    String nome = "";
                    cat = alteracaoCargoFacade.getCategoriaPCSFacade().recuperar(object.getId());
                    nome = cat.getDescricao();
                    if (cat.getFilhos().isEmpty()) {
                        while ((cat.getSuperior() != null) && (!cat.equals(cat.getSuperior()))) {
                            cat = cat.getSuperior();
                            nome = cat.getDescricao() + "/" + nome;
                        }
                        toReturn.add(new SelectItem(object, nome));
                    }
                }
            }
        }

        return Util.ordenaSelectItem(toReturn);
    }

    public boolean temPlanoCargosSalariosSelecionado() {
        return planoCargosSalariosSelecionado != null;
    }

    public List<SelectItem> getProgrecoesPCS() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (enquadramentoFuncionalSelecionado.temInicioDeVigencia() && this.temPlanoCargosSalariosSelecionado() && this.temCategoriaPCSFilhaInformada()) {
            if (planoCargosSalariosSelecionado.getId() != null) {
                List<ProgressaoPCS> listaProgressao = alteracaoCargoFacade.getProgressaoPCSFacade().getRaizPorPlanoECategoriaVigenteNoEnquadramento(planoCargosSalariosSelecionado, categoriaPCSfilha, enquadramentoFuncionalSelecionado.getInicioVigencia());
                if (listaProgressao != null && !listaProgressao.isEmpty()) {
                    UtilRH.ordenarProgressoes(listaProgressao);
                    for (ProgressaoPCS object : listaProgressao) {
                        ProgressaoPCS prog = new ProgressaoPCS();
                        String nome = "";
                        prog = alteracaoCargoFacade.getProgressaoPCSFacade().recuperar(object.getId());
                        nome = prog.getDescricao();
                        toReturn.add(new SelectItem(object, nome));
                    }
                }
            }
        }
        return toReturn;
    }

    public boolean temCategoriaPCSFilhaInformada() {
        return categoriaPCSfilha != null;
    }

    public List<SelectItem> getProgrecoesPCSApenasFilhos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (this.temProgressaoPCSPaiInformado() && this.temPlanoCargosSalariosSelecionado()) {
            if (progressaoPCSPai.getId() != null && planoCargosSalariosSelecionado.getId() != null) {
                List<ProgressaoPCS> listaProgressao = alteracaoCargoFacade.getProgressaoPCSFacade().getFilhosDe(progressaoPCSPai, planoCargosSalariosSelecionado);
                UtilRH.ordenarProgressoes(listaProgressao);
                for (ProgressaoPCS object : listaProgressao) {
                    String nome = "";
                    ProgressaoPCS prog = alteracaoCargoFacade.getProgressaoPCSFacade().recuperar(object.getId());
                    nome = prog.getDescricao();
                    toReturn.add(new SelectItem(object, nome));
                }
            }
        }
        return toReturn;
    }

    public boolean temProgressaoPCSPaiInformado() {
        return progressaoPCSPai != null;
    }

    public EnquadramentoPCS getEnquadramento() {
        if (temEnquadramentoFuncionalSelecionado()) {
            if (temCategoriaPCSFilhaInformada() && enquadramentoFuncionalSelecionado.getProgressaoPCS() != null) {
                Date dataParametro = enquadramentoFuncionalSelecionado.getFinalVigencia() == null ? UtilRH.getDataOperacao() : enquadramentoFuncionalSelecionado.getFinalVigencia();
                enquadramentoPCS = alteracaoCargoFacade.getEnquadramentoPCSFacade().recuperaValor(categoriaPCSfilha, enquadramentoFuncionalSelecionado.getProgressaoPCS(), dataParametro);
                if (temEnquadramentoPCS() && enquadramentoPCS.getId() != null) {
                    enquadramentoFuncionalSelecionado.setVencimentoBase(enquadramentoPCS.getVencimentoBase());
                }
            }
        }
        if (enquadramentoFuncionalSelecionado.getProgressaoPCS() == null) {
            enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
        }
        if (enquadramentoPCS == null || enquadramentoPCS.getId() == null) {
            enquadramentoPCS = new EnquadramentoPCS();
            enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
        }
        return enquadramentoPCS;
    }

    public void cancelarPlanoCargosSalariosAndCategoriaAndProgressaoAndReferenciaAndValor() {
        planoCargosSalariosSelecionado = null;
        cancelarCategoriaAndProgressaoAndReferenciaAndValor();
    }

    public void cancelarCategoriaAndProgressaoAndReferenciaAndValor() {
        categoriaPCSfilha = null;
        cancelarProgressaoAndReferenciaAndValor();
    }

    public void cancelarProgressaoAndReferenciaAndValor() {
        progressaoPCSPai = null;
        cancelarReferenciaAndValor();
    }

    public void cancelarReferenciaAndValor() {
        if (temEnquadramentoFuncionalSelecionado()) {
            enquadramentoFuncionalSelecionado.setProgressaoPCS(null);
        }
        enquadramentoPCS = new EnquadramentoPCS();
    }

    public void confirmarEnquadramentoFuncional() {
        if (temEnquadramentoPCS()) {
            enquadramentoFuncionalSelecionado.setProgressaoPCS(enquadramentoPCS.getProgressaoPCS());
            enquadramentoFuncionalSelecionado.setCategoriaPCS(enquadramentoPCS.getCategoriaPCS());
        }
        if (enquadramentoFuncionalSelecionado.temFinalDeVigencia() && !enquadramentoFuncionalSelecionado.temDataCadastroFinalDeVigencia()) {
            enquadramentoFuncionalSelecionado.setDataCadastroFinalVigencia(UtilRH.getDataOperacao());
        }
        if (podeConfirmarEnquadramentoFuncional()) {
            adicionarEnquadramentoFuncionalNaLista();
            ordenarEnquadramentosFuncionaisAsc();
            limparDadosPanelEnquadramento();
        }
    }

    private void ordenarEnquadramentosFuncionaisAsc() {
        Collections.sort(enquadramentos, new Comparator<EnquadramentoFuncional>() {
            @Override
            public int compare(EnquadramentoFuncional o1, EnquadramentoFuncional o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    public boolean temEnquadramentoPCS() {
        return enquadramentoPCS != null;
    }

    private void adicionarEnquadramentoFuncionalNaLista() {
        enquadramentos = Util.adicionarObjetoEmLista(enquadramentos, enquadramentoFuncionalSelecionado);
    }

    private boolean podeConfirmarEnquadramentoFuncional() {
        if (!enquadramentoFuncionalSelecionado.temInicioDeVigencia()) {
            FacesUtil.addCampoObrigatorio("O campo início de vigência é obrigatório!");
            return false;
        }
        if (planoCargosSalariosSelecionado == null) {
            FacesUtil.addCampoObrigatorio("O campo plano de cargos e salários é obrigatório!");
            return false;
        }
        if (categoriaPCSfilha == null) {
            FacesUtil.addCampoObrigatorio("O campo categoria de plano de cargos e salários é obrigatório!");
            return false;
        }
        if (progressaoPCSPai == null) {
            FacesUtil.addCampoObrigatorio("O campo progressão de plano de cargos e salários é obrigatório!");
            return false;
        }
        if (!enquadramentoFuncionalSelecionado.temProgressaoPCS()) {
            FacesUtil.addCampoObrigatorio("O campo referência é obrigatório!");
            return false;
        }
        if (!DataUtil.isVigenciaValida(enquadramentoFuncionalSelecionado, enquadramentos)) {
            return false;
        }
        return true;
    }

    public void cancelarEnquadramentoFuncional() {
        if (enquadramentoFuncionalSelecionado.estaEditando()) {
            if (!podeConfirmarEnquadramentoFuncional()) {
                return;
            }
            enquadramentos = Util.adicionarObjetoEmLista(enquadramentos, enquadramentoFuncionalSelecionado);
            ordenarEnquadramentosFuncionaisAsc();
        }
        limparDadosPanelEnquadramento();
        if (!selecionado.getProvimentoFP().getVinculoFP().getContratoFP().temEnquadramentoFuncionalAdicionado(selecionado.getEnquadramentoFuncional())) {
            selecionado.setEnquadramentoFuncional(null);
        }
    }

    public void limparDadosPanelEnquadramento() {
        enquadramentoFuncionalSelecionado = null;
        planoCargosSalariosSelecionado = null;
        cancelarCategoriaAndProgressaoAndReferenciaAndValor();
        instanciarPlanos();
    }

    public void selecionarEnquadramentoFuncional(EnquadramentoFuncional ef) {
        enquadramentoFuncionalSelecionado = ef;
        enquadramentoFuncionalSelecionado.setOperacao(Operacoes.EDITAR);

        planoCargosSalariosSelecionado = enquadramentoFuncionalSelecionado.getCategoriaPCS().getPlanoCargosSalarios();
        progressaoPCSPai = enquadramentoFuncionalSelecionado.getProgressaoPCS().getSuperior();
        categoriaPCSfilha = enquadramentoFuncionalSelecionado.getCategoriaPCS();

        carregarPlanos();

        enquadramentos.remove(ef);
    }

    public void removerEnquadramentoFuncional(EnquadramentoFuncional ef) {
        enquadramentos.remove(ef);
        selecionado.setEnquadramentoFuncional(null);
        if (!CollectionUtils.isEmpty(enquadramentos)) {
            enquadramentos.get(0).setFinalVigencia(null);
        }
    }

    public boolean desabilitarBotaoSalvar() {
        return isOperacaoEditar() && !getSituacaoContratoFPVigente().isEmExercicio();
    }

    public AlteracaoCargoFacade getAlteracaoCargoFacade() {
        return alteracaoCargoFacade;
    }

    public void setAlteracaoCargoFacade(AlteracaoCargoFacade alteracaoCargoFacade) {
        this.alteracaoCargoFacade = alteracaoCargoFacade;
    }

    public ContratoFP getContratoFPSelecionado() {
        return contratoFPSelecionado;
    }

    public void setContratoFPSelecionado(ContratoFP contratoFPSelecionado) {
        this.contratoFPSelecionado = contratoFPSelecionado;
    }

    public ContratoFPCargo getContratoFPCargoSelecionado() {
        return contratoFPCargoSelecionado;
    }

    public void setContratoFPCargoSelecionado(ContratoFPCargo contratoFPCargoSelecionado) {
        this.contratoFPCargoSelecionado = contratoFPCargoSelecionado;
    }

    public LotacaoFuncional getLotacaoFuncionalSelecionado() {
        return lotacaoFuncionalSelecionado;
    }

    public void setLotacaoFuncionalSelecionado(LotacaoFuncional lotacaoFuncionalSelecionado) {
        this.lotacaoFuncionalSelecionado = lotacaoFuncionalSelecionado;
    }

    public EnquadramentoFuncional getEnquadramentoFuncionalSelecionado() {
        return enquadramentoFuncionalSelecionado;
    }

    public void setEnquadramentoFuncionalSelecionado(EnquadramentoFuncional enquadramentoFuncionalSelecionado) {
        this.enquadramentoFuncionalSelecionado = enquadramentoFuncionalSelecionado;
    }

    public List<ContratoFPCargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<ContratoFPCargo> cargos) {
        this.cargos = cargos;
    }

    public List<LotacaoFuncional> getLotacoes() {
        return lotacoes;
    }

    public void setLotacoes(List<LotacaoFuncional> lotacoes) {
        this.lotacoes = lotacoes;
    }

    public List<EnquadramentoFuncional> getEnquadramentos() {
        return enquadramentos;
    }

    public void setEnquadramentos(List<EnquadramentoFuncional> enquadramentos) {
        this.enquadramentos = enquadramentos;
    }

    public PlanoCargosSalarios getPlanoCargosSalariosSelecionado() {
        return planoCargosSalariosSelecionado;
    }

    public void setPlanoCargosSalariosSelecionado(PlanoCargosSalarios planoCargosSalariosSelecionado) {
        this.planoCargosSalariosSelecionado = planoCargosSalariosSelecionado;
    }

    public CategoriaPCS getCategoriaPCSfilha() {
        return categoriaPCSfilha;
    }

    public void setCategoriaPCSfilha(CategoriaPCS categoriaPCSfilha) {
        this.categoriaPCSfilha = categoriaPCSfilha;
    }

    public ProgressaoPCS getProgressaoPCSPai() {
        return progressaoPCSPai;
    }

    public void setProgressaoPCSPai(ProgressaoPCS progressaoPCSPai) {
        this.progressaoPCSPai = progressaoPCSPai;
    }

    public TipoPCS getTipoPCS() {
        return tipoPCS;
    }

    public void setTipoPCS(TipoPCS tipoPCS) {
        this.tipoPCS = tipoPCS;
    }

    public ConverterAutoComplete getConverterModalidade() {
        if (converterModalidade == null) {
            converterModalidade = new ConverterAutoComplete(ModalidadeContratoFP.class, modalidadeContratoFPFacade);
        }
        return converterModalidade;
    }

    public void setConverterModalidade(ConverterAutoComplete converterModalidade) {
        this.converterModalidade = converterModalidade;
    }

    public ConverterAutoComplete getConverterRegime() {
        if (converterRegime == null) {
            converterRegime = new ConverterAutoComplete(TipoRegime.class, tipoRegimeFacade);
        }
        return converterRegime;
    }

    public void setConverterRegime(ConverterAutoComplete converterRegime) {
        this.converterRegime = converterRegime;
    }


    public EnquadramentoPCS getEnquadramentoPCS() {
        try {
            Date dataParametro = enquadramentoFuncionalSelecionado.getFinalVigencia() == null ? new Date() : enquadramentoFuncionalSelecionado.getFinalVigencia();
            enquadramentoPCS = alteracaoCargoFacade.getEnquadramentoPCSFacade().recuperaValor(categoriaPCSfilha, enquadramentoFuncionalSelecionado.getProgressaoPCS(), dataParametro);
            enquadramentoFuncionalSelecionado.setVencimentoBase(enquadramentoPCS.getVencimentoBase());
        } catch (Exception ex) {
            enquadramentoPCS = new EnquadramentoPCS();
            enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
        }
        return enquadramentoPCS;
    }

    public void setEnquadramentoPCS(EnquadramentoPCS enquadramentoPCS) {
        this.enquadramentoPCS = enquadramentoPCS;
    }

    public boolean temEnquadramentoFuncionalSelecionado() {
        return enquadramentoFuncionalSelecionado != null;
    }

    public AlteracaoCargo getAlteracaoCargo() {
        return alteracaoCargo;
    }

    public void setAlteracaoCargo(AlteracaoCargo alteracaoCargo) {
        this.alteracaoCargo = alteracaoCargo;
    }

    public class ConverterServidor implements Serializable, Converter {
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
            try {
                ContratoFP c = alteracaoCargoFacade.getContratoFPFacade().recuperarParaTelaAlteracaoDeCargo(Long.parseLong(s));
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
}
