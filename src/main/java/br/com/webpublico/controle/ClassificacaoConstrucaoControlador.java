package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabitesePadroesConstrucao;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoConstrucao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ClassificacaoConstrucaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaClassificacaoConstrucao",
        pattern = "/classificacao-construcao/novo/",
        viewId = "/faces/tributario/classificacaoconstrucao/edita.xhtml"),
    @URLMapping(id = "listarClassificacaoConstrucao",
        pattern = "/classificacao-construcao/listar/",
        viewId = "/faces/tributario/classificacaoconstrucao/lista.xhtml"),
    @URLMapping(id = "verClassificacaoConstrucao",
        pattern = "/classificacao-construcao/ver/#{classificacaoConstrucaoControlador.id}/",
        viewId = "/faces/tributario/classificacaoconstrucao/visualizar.xhtml"),
})
public class ClassificacaoConstrucaoControlador extends PrettyControlador<ClassificacaoConstrucao> implements CRUD {

    @EJB
    private ClassificacaoConstrucaoFacade classificacaoConstrucaoFacade;
    private ProcRegularizaConstrucao procRegularizaConstrucao;
    private ConverterAutoComplete converterPadraoConstrucao;
    private ConverterAutoComplete converterCadastroImobiliario;
    private CadastroImobiliario cadastroImobiliario;
    private List<Habitese> habiteses;
    private List<Atributo> atributos;

    public ProcRegularizaConstrucao getProcRegularizaConstrucao() {
        return procRegularizaConstrucao;
    }

    public void setProcRegularizaConstrucao(ProcRegularizaConstrucao procRegularizaConstrucao) {
        this.procRegularizaConstrucao = procRegularizaConstrucao;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public List<Habitese> getHabiteses() {
        if (habiteses == null) {
            habiteses = Lists.newArrayList();
        }
        return habiteses;
    }

    public void setHabiteses(List<Habitese> habiteses) {
        this.habiteses = habiteses;
    }

    public List<Atributo> getAtributos() {
        if (atributos == null) {
            atributos = Lists.newArrayList();
        }
        return atributos;
    }

    public void setAtributos(List<Atributo> atributos) {
        this.atributos = atributos;
    }

    public ClassificacaoConstrucaoControlador() {
        super(ClassificacaoConstrucao.class);
    }

    @URLAction(mappingId = "novaClassificacaoConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(classificacaoConstrucaoFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataClassificacao(classificacaoConstrucaoFacade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "verClassificacaoConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarProcRegularizacao();
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            classificacaoConstrucaoFacade.getHabiteseConstrucaoFacade().salvar(selecionado.getHabitese());
            classificacaoConstrucaoFacade.getAlvaraConstrucaoFacade().salvar(selecionado.getHabitese().getAlvaraConstrucao());
            if (TipoConstrucao.CONSTRUCAO.equals(selecionado.getHabitese().getCaracteristica().getTipoConstrucao())) {
                criarConstrucao();
            } else if (TipoConstrucao.REFORMA.equals(selecionado.getHabitese().getCaracteristica().getTipoConstrucao())) {
                editarConstrucao();
            } else if (TipoConstrucao.DEMOLICAO.equals(selecionado.getHabitese().getCaracteristica().getTipoConstrucao())) {
                cancelarConstrucao();
            }
            super.salvar(Redirecionar.VER);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return classificacaoConstrucaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/classificacao-construcao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void inicializarHabitese(Habitese habitese) {
        selecionado.setHabitese(classificacaoConstrucaoFacade.getHabiteseConstrucaoFacade().recuperar(habitese.getId()));
    }

    public void buscarHabitesesCadastro() {
        cadastroImobiliario = classificacaoConstrucaoFacade.getCadastroImobiliarioFacade().recuperar(cadastroImobiliario.getId());
        setHabiteses(classificacaoConstrucaoFacade.buscarHabiteseComCaracteristicaSemPadraoConstrucaoDoCadastro(cadastroImobiliario.getId()));
        if (habiteses.size() == 1) {
            inicializarHabitese(habiteses.get(0));
            recuperarProcRegularizacao();
            FacesUtil.atualizarComponente("Formulario");
        } else {
            FacesUtil.executaJavaScript("dlgEscolherHabitese.show()");
        }
    }

    public void setHabitese(Habitese h) {
        inicializarHabitese(h);
        FacesUtil.atualizarComponente("Formulario");
        FacesUtil.executaJavaScript("dlgEscolherHabitese.hide()");
    }

    public List<CadastroImobiliario> completarCadastros(String parte) {
        return classificacaoConstrucaoFacade.buscarCadastrosComHabiteseComCaracteristicaSemPadraoConstrucao(parte);
    }

    public List<HabitesePadroesConstrucao> completarPadrao() {
        return classificacaoConstrucaoFacade.buscarPadroesConstrucao();
    }

    public void recuperarProcRegularizacao() {
        try {
            validarSelecionarProcesso();
        } catch (ValidacaoException ve) {
            procRegularizaConstrucao = null;
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void criarConstrucao() {
        ConstrucaoAlvara construcaoAlvara = selecionado.getHabitese().getAlvaraConstrucao().getConstrucaoAlvara();
        AlvaraConstrucao alvaraConstrucao = selecionado.getHabitese().getAlvaraConstrucao();
        Construcao construcao = new Construcao();
        construcao.setImovel(procRegularizaConstrucao.getCadastroImobiliario());
        construcao.setCodigo(classificacaoConstrucaoFacade.buscarCodigoNovaContrucaoCadastro(procRegularizaConstrucao.getCadastroImobiliario().getId()).intValue());
        construcao.setAnoConstrucao(new Date());
        construcao.setDescricao(construcao.getCodigo().toString());
        construcao.setDataRegistro(new Date());
        construcao.setAreaConstruida(construcaoAlvara.getAreaConstruida().doubleValue());
        construcao.setQuantidadePavimentos(construcaoAlvara.getQuantidadePavimentos());
        construcao.setAreaTotalConstruida(construcaoAlvara.getAreaConstruida().doubleValue());
        construcao.setCancelada(false);
        construcao.setHabitese(selecionado.getHabitese().getCodigo() + "/" + selecionado.getHabitese().getExercicio().getAno());
        construcao.setDataAlvara(alvaraConstrucao.getDataExpedicao());
        construcao.setNumeroAlvara(alvaraConstrucao.getCodigo().toString());
        popularCaracteristicasConstrucaoCadastro(construcao);
        classificacaoConstrucaoFacade.getConstrucaoFacade().salvarNovo(construcao);
    }

    private void editarConstrucao() {
        Construcao construcao = selecionado.getHabitese().getAlvaraConstrucao().getConstrucaoAlvara().getConstrucao();
        List<CaracteristicasAlvaraConstrucao> caracteristicasAlvaraConstrucao = selecionado.getHabitese().getAlvaraConstrucao().getConstrucaoAlvara().getCaracteristicas();
        if (construcao != null) {
            construcao = classificacaoConstrucaoFacade.getConstrucaoFacade().recuperar(construcao.getId());
            for (CaracteristicasAlvaraConstrucao caracteristicaConstrucaoAlvara : caracteristicasAlvaraConstrucao) {
                boolean jaTemAtributo = false;
                for (CaracteristicasConstrucao caracteristicaConstrucaoCadastro : construcao.getCaracteristicasConstrucao()) {
                    if (caracteristicaConstrucaoAlvara.getAtributo().getId().equals(caracteristicaConstrucaoCadastro.getAtributo().getId())) {
                        jaTemAtributo = true;
                        caracteristicaConstrucaoCadastro.getValorAtributo().setValorDiscreto(caracteristicaConstrucaoAlvara.getValorAtributo().getValorDiscreto());
                        caracteristicaConstrucaoCadastro.setValorAtributo(caracteristicaConstrucaoAlvara.getValorAtributo());
                        break;
                    }
                }
                if (!jaTemAtributo) {
                    CaracteristicasConstrucao caracteristicaConstrucaoCadastro = new CaracteristicasConstrucao();
                    ValorAtributo va = new ValorAtributo();
                    va.setAtributo(caracteristicaConstrucaoAlvara.getAtributo());
                    va.setValorDiscreto(caracteristicaConstrucaoAlvara.getValorAtributo().getValorDiscreto());
                    caracteristicaConstrucaoCadastro.setConstrucao(construcao);
                    caracteristicaConstrucaoCadastro.setAtributo(caracteristicaConstrucaoAlvara.getAtributo());
                    caracteristicaConstrucaoCadastro.setValorAtributo(va);
                    construcao.getCaracteristicasConstrucao().add(caracteristicaConstrucaoCadastro);
                }
            }
            classificacaoConstrucaoFacade.getConstrucaoFacade().salvar(construcao);
        }
    }

    private void cancelarConstrucao() {
        Construcao construcao = selecionado.getHabitese().getAlvaraConstrucao().getConstrucaoAlvara().getConstrucao();
        if (construcao != null) {
            construcao = classificacaoConstrucaoFacade.getConstrucaoFacade().recuperar(construcao.getId());
            construcao.setCancelada(true);
            classificacaoConstrucaoFacade.getConstrucaoFacade().salvar(construcao);
        }
    }

    private void popularCaracteristicasConstrucaoCadastro(Construcao c) {
        List<CaracteristicasAlvaraConstrucao> caracteristicasAlvara = selecionado.getHabitese().getAlvaraConstrucao().getConstrucaoAlvara().getCaracteristicas();
        for (CaracteristicasAlvaraConstrucao caracteristicaAlvara : caracteristicasAlvara) {
            CaracteristicasConstrucao caracteristicasConstrucaoCadastro = new CaracteristicasConstrucao();
            ValorAtributo va = new ValorAtributo();
            va.setAtributo(caracteristicaAlvara.getAtributo());
            va.setValorDiscreto(caracteristicaAlvara.getValorAtributo().getValorDiscreto());
            caracteristicasConstrucaoCadastro.setConstrucao(c);
            caracteristicasConstrucaoCadastro.setAtributo(caracteristicaAlvara.getAtributo());
            caracteristicasConstrucaoCadastro.setValorAtributo(va);
            c.getCaracteristicasConstrucao().add(caracteristicasConstrucaoCadastro);
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getHabitese() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um habite-se");
        } else if (selecionado.getHabitese().getCaracteristica().getPadrao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um Padrão da Construção");
        }
        for (Map.Entry<Atributo, ValorAtributo> mapAtributo : selecionado.getHabitese().getAlvaraConstrucao().getConstrucaoAlvara().getAtributos().entrySet()) {
            if (atributos.contains(mapAtributo.getKey()) && (mapAtributo.getValue() == null || mapAtributo.getValue().getValorDiscreto() == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o atributo " + mapAtributo.getKey().getNome());
            }
        }
        ve.lancarException();
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, classificacaoConstrucaoFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public ConverterAutoComplete getConverterPadraoConstrucao() {
        if (converterPadraoConstrucao == null) {
            converterPadraoConstrucao = new ConverterAutoComplete(HabitesePadroesConstrucao.class, classificacaoConstrucaoFacade.getHabitesePadroesConstrucaoFacade());
        }
        return converterPadraoConstrucao;
    }

    public List<Atributo> verificarAtributos(List<Atributo> atributos) {
        if (selecionado.getHabitese() != null) {
            ConstrucaoAlvara c = selecionado.getHabitese().getAlvaraConstrucao().getConstrucaoAlvara();
            setAtributos(atributos);
            for (Atributo atributo : this.atributos) {
                if (!c.getAtributos().containsKey(atributo)) {
                    CaracteristicasAlvaraConstrucao cac = new CaracteristicasAlvaraConstrucao();
                    ValorAtributo va = new ValorAtributo();
                    va.setAtributo(atributo);
                    cac.setAtributo(atributo);
                    cac.setValorAtributo(va);
                    cac.setConstrucaoAlvara(c);
                    selecionado.getHabitese().getAlvaraConstrucao().getConstrucaoAlvara().getCaracteristicas().add(cac);
                    c.getAtributos().put(atributo, va);
                }
            }
        }
        return atributos;
    }

    private void validarSelecionarProcesso() {
        ValidacaoException ve = new ValidacaoException();
        procRegularizaConstrucao = selecionado.getHabitese().getAlvaraConstrucao().getProcRegularizaConstrucao();
        if (procRegularizaConstrucao == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível recuperar o dados do processo de regularização!");
        } else {
            procRegularizaConstrucao = classificacaoConstrucaoFacade.getAlvaraConstrucaoFacade().getProcRegularizaConstrucaoFacade().recarregar(procRegularizaConstrucao);
        }
        ve.lancarException();
    }
}
