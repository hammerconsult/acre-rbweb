/*
 * Codigo gerado automaticamente em Wed Feb 15 08:57:52 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.dirf.GrauParentescoDirf;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.PensaoAlimenticiaFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "pensaoAlimenticiaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPensaoAlimenticia", pattern = "/pensao-alimenticia/novo/", viewId = "/faces/rh/administracaodepagamento/pensaoalimenticia/edita.xhtml"),
    @URLMapping(id = "editarPensaoAlimenticia", pattern = "/pensao-alimenticia/editar/#{pensaoAlimenticiaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pensaoalimenticia/edita.xhtml"),
    @URLMapping(id = "listarPensaoAlimenticia", pattern = "/pensao-alimenticia/listar/", viewId = "/faces/rh/administracaodepagamento/pensaoalimenticia/lista.xhtml"),
    @URLMapping(id = "verPensaoAlimenticia", pattern = "/pensao-alimenticia/ver/#{pensaoAlimenticiaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pensaoalimenticia/visualizar.xhtml")
})
public class PensaoAlimenticiaControlador extends PrettyControlador<PensaoAlimenticia> implements Serializable, CRUD {

    @EJB
    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private ConverterAutoComplete converterVinculoFP;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterEventoFP;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterContaCorrenteBancaria;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private BeneficioPensaoAlimenticia beneficioPensaoAlimenticia;
    private ValorPensaoAlimenticia valorPensaoAlimenticia;
    private List<ValorPensaoAlimenticia> valoresPensaoAlimenticia;
    private BeneficioPensaoAlimenticia beneficio;
    private MoneyConverter moneyConverter;
    private ModoSelecao modoSelecao;
    private ItemBaseFP itemBaseFP;
    private BaseFP baseFP;
    private BaseFP baseAux;
    private ConverterAutoComplete converterBaseFP;
    private List<String> beneficiariosEncontrados;
    private Boolean clonando = false;
    private Boolean edicaoValores;

    public PensaoAlimenticiaControlador() {
        super(PensaoAlimenticia.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return pensaoAlimenticiaFacade;
    }

    public BeneficioPensaoAlimenticia getBeneficioPensaoAlimenticia() {
        return beneficioPensaoAlimenticia;
    }

    public void setBeneficioPensaoAlimenticia(BeneficioPensaoAlimenticia beneficioPensaoAlimenticia) {
        this.beneficioPensaoAlimenticia = beneficioPensaoAlimenticia;
    }

    public BaseFP getBaseFP() {
        return baseFP;
    }

    public void setBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
    }

    public ValorPensaoAlimenticia getValorPensaoAlimenticia() {
        return valorPensaoAlimenticia;
    }

    public void setValorPensaoAlimenticia(ValorPensaoAlimenticia valorPensaoAlimenticia) {
        this.valorPensaoAlimenticia = valorPensaoAlimenticia;
    }

    public Boolean getClonando() {
        return clonando;
    }

    public List<SelectItem> getContasCorrentesBancarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (getBeneficioPensaoAlimenticia().getPessoaFisicaBeneficiario() != null) {
            for (ContaCorrenteBancaria object : pensaoAlimenticiaFacade.getContaCorrenteBancPessoaFacade().listaContasPorPessoaFisica(getBeneficioPensaoAlimenticia().getPessoaFisicaBeneficiario())) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterContaCorrenteBancaria == null) {
            converterContaCorrenteBancaria = new ConverterAutoComplete(ContaCorrenteBancaria.class, pensaoAlimenticiaFacade.getContaCorrenteBancPessoaFacade());
        }
        return converterContaCorrenteBancaria;
    }

    public List<ValorPensaoAlimenticia> getValoresPensaoAlimenticia() {
        List<ValorPensaoAlimenticia> toReturn = new ArrayList<>();

        if (valoresPensaoAlimenticia == null || valoresPensaoAlimenticia.isEmpty()) {
            valoresPensaoAlimenticia = new ArrayList<>();

        }
        for (ValorPensaoAlimenticia v : valoresPensaoAlimenticia) {
            if (v.getBeneficioPensaoAlimenticia().equals(beneficio)) {
                toReturn.add(v);
            }
        }
        return toReturn;
    }

    public void setValoresPensaoAlimenticia(List<ValorPensaoAlimenticia> valoresPensaoAlimenticia) {
        this.valoresPensaoAlimenticia = valoresPensaoAlimenticia;
    }

    public List<SelectItem> getTipoValorPensaoAlimenticia() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoValorPensaoAlimenticia tipo : TipoValorPensaoAlimenticia.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void removerItemBaseFP(ItemBaseFP e) {
        if (valorPensaoAlimenticia != null && valorPensaoAlimenticia.getBaseFP() != null && valorPensaoAlimenticia.getBaseFP().getItensBasesFPs().contains(e)) {
            valorPensaoAlimenticia.getBaseFP().getItensBasesFPs().remove(e);
        }
    }

    public Converter getConverterContratoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, pensaoAlimenticiaFacade.getVinculoFPFacade());
        }
        return converterVinculoFP;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, pensaoAlimenticiaFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public Converter getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, pensaoAlimenticiaFacade.getEventoFPFacade());
        }
        return converterEventoFP;
    }

    public ConverterAutoComplete getConverterBaseFP() {
        if (converterBaseFP == null) {
            converterBaseFP = new ConverterAutoComplete(BaseFP.class, pensaoAlimenticiaFacade.getBaseFPFacade());
        }
        return converterBaseFP;
    }

    public List<BaseFP> completarBasesFP(String filtro) {
        return pensaoAlimenticiaFacade.getBaseFPFacade().listaFiltrando(filtro.trim(), "codigo", "descricaoReduzida");
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(PessoaFisica.class, pensaoAlimenticiaFacade.getPessoaFisicaFacade());
        }
        return converterPessoa;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public List<VinculoFP> completarInstituidor(String parte) {
        return pensaoAlimenticiaFacade.getVinculoFPFacade().buscaVinculoVigenteFiltrandoAtributosMatriculaFP(parte.trim());
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return pensaoAlimenticiaFacade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaControlador.getDataOperacao());
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return pensaoAlimenticiaFacade.getAtoLegalFacade().listaFiltrandoAtoLegal(parte.trim());
    }

    public List<EventoFP> completarEventoFP(String parte) {
        return pensaoAlimenticiaFacade.getEventoFPFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<PessoaFisica> completarPessoaFisica(String parte) {
        return pensaoAlimenticiaFacade.getPessoaFisicaFacade().listaFiltrandoPorTipoPerfil(parte.trim(), PerfilEnum.PERFIL_PENSIONISTA);
    }

    public List<PessoaFisica> completarBeneficiariosPensaoAlimenticia(String parte) {
        return pensaoAlimenticiaFacade.getPessoaFisicaFacade().buscarPessoasPorTipoPerfilAndAtiva(parte.trim(), PerfilEnum.PERFIL_PENSIONISTA);
    }

    public ModoSelecao getModoSelecao() {
        return modoSelecao;
    }

    public void setModoSelecao(ModoSelecao modoSelecao) {
        this.modoSelecao = modoSelecao;
    }

    public ItemBaseFP getItemBaseFP() {
        return itemBaseFP;
    }


    @URLAction(mappingId = "novoPensaoAlimenticia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionar();
        hierarquiaOrganizacionalSelecionada = new HierarquiaOrganizacional();
        getSelecionado().setBeneficiosPensaoAlimenticia(new ArrayList<BeneficioPensaoAlimenticia>());
        valoresPensaoAlimenticia = new ArrayList<>();
        definirSessao();
        PensaoAlimenticia fromSession = (PensaoAlimenticia) Web.pegaDaSessao(PensaoAlimenticia.class);
        if (isSessao() && fromSession != null) {
            selecionado = fromSession;
        }
    }

    @URLAction(mappingId = "verPensaoAlimenticia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarPensaoAlimenticia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionar();
    }

    public void selecionar() {
        valoresPensaoAlimenticia = new ArrayList<>();
        beneficiariosEncontrados = Lists.newArrayList();
        edicaoValores = Boolean.FALSE;
    }

    @Override
    public void salvar() {
        try {
            validarCampo();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarCampo() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();

        if (getSelecionado().getBeneficiosPensaoAlimenticia().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível cadastrar uma pensão sem Beneficiários.");
        }

        if (!getSelecionado().getBeneficiosPensaoAlimenticia().isEmpty()) {
            for (BeneficioPensaoAlimenticia ben : getSelecionado().getBeneficiosPensaoAlimenticia()) {
                if (ben.getValoresPensaoAlimenticia().isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Beneficiário " + ben.getPessoaFisicaBeneficiario().getNome() + " não pode ser salvo sem valor válido!");
                }
            }
        }
        ve.lancarException();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void adicionarBeneficiario() {
        try {
            validarItemBeneficiario();
            getBeneficioPensaoAlimenticia().setPensaoAlimenticia(getSelecionado());
            Util.adicionarObjetoEmLista(getSelecionado().getBeneficiosPensaoAlimenticia(), getBeneficioPensaoAlimenticia());

            if (isTipoBase(getBeneficioPensaoAlimenticia())) {
                modoSelecao = ModoSelecao.NOVO;
                baseFP = new BaseFP();
                itemBaseFP = new ItemBaseFP();
                if (valorPensaoAlimenticia != null) {
                    valorPensaoAlimenticia.setBaseFP(baseFP);
                }
            }

            cancelarBeneficiario();
            FacesUtil.atualizarComponente("Formulario:painelBeneficiario");
        } catch (ValidacaoException ve) {
            logger.error("Erro: ", ve);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro: ", e);
        }
    }

    public void validarNovoBeneficiario() {
        ValidacaoException ve = new ValidacaoException();
        if (getSelecionado().getVinculoFP() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O campo Instituidor deve ser informado.");
        }
        ve.lancarException();
    }

    public void novoBeneficiario() {
        try {
            validarNovoBeneficiario();
            setBeneficioPensaoAlimenticia(new BeneficioPensaoAlimenticia());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void criarNovaBase() {

        procurarEDefinirNovoCodigoBaseFP(baseAux);
        logger.debug(baseAux.getCodigo());

        baseAux.setDescricao("Base Sobre Bruto - " + selecionado.getVinculoFP());
        definirItensBase(baseAux);

        for (ItemBaseFP fp : baseAux.getItensBasesFPs()) {
            logger.debug(fp.getEventoFP() + "");
        }

    }

    private void definirItensBase(BaseFP baseFP) {
        for (EventoFP eventoFP : eventoFPFacade.buscarEventosAtivosPorTipoEvento(TipoEventoFP.VANTAGEM)) {
            ItemBaseFP item = criarItemBaseFP(eventoFP, baseFP);
            baseFP.getItensBasesFPs().add(item);
        }
        for (EventoFP eventoFP : eventoFPFacade.findEventosPorIdentificacao(IdentificacaoEventoFP.IMPOSTO_RENDA)) {
            ItemBaseFP item = criarItemBaseFP(eventoFP, baseFP);
            baseFP.getItensBasesFPs().add(item);
        }
        for (EventoFP eventoFP : eventoFPFacade.findEventosPorIdentificacao(IdentificacaoEventoFP.INSS)) {
            ItemBaseFP item = criarItemBaseFP(eventoFP, baseFP);
            baseFP.getItensBasesFPs().add(item);
        }
        for (EventoFP eventoFP : eventoFPFacade.findEventosPorIdentificacao(IdentificacaoEventoFP.RPPS)) {
            ItemBaseFP item = criarItemBaseFP(eventoFP, baseFP);
            baseFP.getItensBasesFPs().add(item);
        }
    }

    private ItemBaseFP criarItemBaseFP(EventoFP eventoFP, BaseFP baseFP) {
        ItemBaseFP item = new ItemBaseFP();
        item.setEventoFP(eventoFP);
        item.setBaseFP(baseFP);
        item.setOperacaoFormula(TipoEventoFP.VANTAGEM.equals(eventoFP.getTipoEventoFP()) ? OperacaoFormula.ADICAO : OperacaoFormula.SUBTRACAO);
        item.setSomaValorRetroativo(false);
        item.setTipoValor(TipoValor.NORMAL);
        item.setDataRegistro(new Date());
        return item;
    }

    public void cancelarBeneficiario() {
        setBeneficioPensaoAlimenticia(null);
    }

    public void removerBeneficiario(ActionEvent evt) {
        BeneficioPensaoAlimenticia item = (BeneficioPensaoAlimenticia) evt.getComponent().getAttributes().get("item");
        getSelecionado().getBeneficiosPensaoAlimenticia().remove(item);
    }

    public void removerItemValorBeneficio(ValorPensaoAlimenticia itemValor) {
        valoresPensaoAlimenticia.remove(itemValor);
    }

    public void editarValorPensaoAlimenticia(ValorPensaoAlimenticia itemValor) {
        valorPensaoAlimenticia = (ValorPensaoAlimenticia) Util.clonarObjeto(itemValor);
        modoSelecao = valorPensaoAlimenticia.getId() != null ? ModoSelecao.EXISTENTE : ModoSelecao.NOVO;
        edicaoValores = Boolean.TRUE;
    }

    public void editarBeneficiario(BeneficioPensaoAlimenticia beneficio) {
        this.setBeneficioPensaoAlimenticia(beneficio);

    }


    public void associarValorBeneficio(BeneficioPensaoAlimenticia bene) {
        baseAux = new BaseFP();
        valoresPensaoAlimenticia = new ArrayList<>();
        valoresPensaoAlimenticia = bene.getValoresPensaoAlimenticia();
        valorPensaoAlimenticia = new ValorPensaoAlimenticia();
        valorPensaoAlimenticia.setBeneficioPensaoAlimenticia(bene);
        valorPensaoAlimenticia.setInicioVigencia(bene.getInicioVigencia());
        beneficio = bene;
        if (isTipoBase(beneficio)) {
            itemBaseFP = new ItemBaseFP();
        }
        if (ehSalarioMinimoIntegral()) {
            valorPensaoAlimenticia.setValor(BigDecimal.ONE);
        }
        clonando = false;

        if (bene.getId() == null && TipoValorPensaoAlimenticia.BASE_BRUTO.equals(bene.getTipoValorPensaoAlimenticia())) {
            criarNovaBase();
        }
    }

    public void adicionarItemValorBeneficio() {
        try {
            if (!baseAux.getItensBasesFPs().isEmpty() && !clonando) {
                valorPensaoAlimenticia.setBaseFP(baseAux);
                baseAux = new BaseFP();
            }
            validarValorBeneficio();
            Util.adicionarObjetoEmLista(valoresPensaoAlimenticia, valorPensaoAlimenticia);
            for (BeneficioPensaoAlimenticia ben : getSelecionado().getBeneficiosPensaoAlimenticia()) {
                if (ben == valorPensaoAlimenticia.getBeneficioPensaoAlimenticia()) {
                    ben.setValoresPensaoAlimenticia(valoresPensaoAlimenticia);
                }
            }
            cancelarValorPensaoAlimenticia();
            clonando = false;
            edicaoValores = Boolean.FALSE;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarValorPensaoAlimenticia() {
        valorPensaoAlimenticia = new ValorPensaoAlimenticia();
        valorPensaoAlimenticia.setBeneficioPensaoAlimenticia(beneficio);
        modoSelecao = null;
    }

    public String recuperarValorVigenteBeneficio(BeneficioPensaoAlimenticia b) {
        String retorno = "Adicionar Valores";
        logger.debug("beneficiario > " + b);
        valoresPensaoAlimenticia = new ArrayList<>();
        valoresPensaoAlimenticia = b.getValoresPensaoAlimenticia();
        if (!valoresPensaoAlimenticia.isEmpty()) {
            DecimalFormat df = new DecimalFormat("#,###,##0.00");
            for (ValorPensaoAlimenticia valorBen : valoresPensaoAlimenticia) {
                if (valorBen.getValor() != null) {
                    retorno = "" + df.format(valorBen.getValor());
                    break;
                }
            }
        }
        return retorno;
    }

    public String recuperarBaseVigenteBeneficio(BeneficioPensaoAlimenticia b) {
        String retorno = "Adicionar Bases";
        valoresPensaoAlimenticia = new ArrayList<>();
        valoresPensaoAlimenticia = b.getValoresPensaoAlimenticia();
        if (!valoresPensaoAlimenticia.isEmpty()) {
            for (ValorPensaoAlimenticia valorBen : valoresPensaoAlimenticia) {
                if (Util.validaVigencia(valorBen.getInicioVigencia(), valorBen.getFinalVigencia())) {
                    retorno = "" + valorBen.getBaseFP();
                    break;
                }
            }
        }
        return retorno;
    }

    public Integer recuperaQuantidadeDeBeneficiarios(List<BeneficioPensaoAlimenticia> lista) {
        Integer total = 0;
        List<PessoaFisica> listaPessoa = new ArrayList<>();
        for (BeneficioPensaoAlimenticia obj : lista) {
            if (!listaPessoa.contains(obj.getPessoaFisicaBeneficiario())) {
                listaPessoa.add(obj.getPessoaFisicaBeneficiario());
                total++;
            }
        }
        return total;
    }

    public boolean ehPercentualSalarioMinimo() {
        return beneficio != null && beneficio.isPensaoPercentualSobreSalarioMinimo();
    }

    public boolean isValorFixoSalarioMinimo() {
        return beneficio != null && beneficio.isPensaoValorFixo();
    }

    public boolean ehSalarioMinimoIntegral() {
        return beneficio != null && beneficio.isPensaoSalarioMinimoIntegral();
    }

    public boolean mostrarBase(BeneficioPensaoAlimenticia beneficio) {
        return beneficio.isPensaoValorBase();
    }

    public boolean mostrarBase() {
        if (valorPensaoAlimenticia != null) {
            return valorPensaoAlimenticia.getBeneficioPensaoAlimenticia() != null && isTipoBase(valorPensaoAlimenticia.getBeneficioPensaoAlimenticia());
        }
        return false;
    }

    public boolean mostrarBaseItem() {
        if (beneficio != null) {
            return isTipoBase(beneficio);
        }

        return false;
    }

    boolean isTipoBase(BeneficioPensaoAlimenticia bpa) {
        return TipoValorPensaoAlimenticia.BASE.equals(bpa.getTipoValorPensaoAlimenticia())
            || TipoValorPensaoAlimenticia.BASE_BRUTO.equals(bpa.getTipoValorPensaoAlimenticia())
            || TipoValorPensaoAlimenticia.BASE_SOBRE_LIQUIDO.equals(bpa.getTipoValorPensaoAlimenticia());
    }

    public void validarValorBeneficio() {
        ValidacaoException ve = new ValidacaoException();
        if (valorPensaoAlimenticia.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início da Vigência deve ser informado.");
        }
        if (valorPensaoAlimenticia.getValor() == null) {
            if (beneficio.isPensaoValorFixo()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Valor deve ser informado.");
            }
            if (beneficio.isPensaoPercentualSobreSalarioMinimo() || beneficio.isPensaoValorBase()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Percentual deve ser obrigatório.");
            }
            if (beneficio.isPensaoValorBase()) {
                FacesUtil.addCampoObrigatorio("O campo Quantidade deve ser informado.");
            }
            if (TipoValorPensaoAlimenticia.BASE_BRUTO.equals(beneficio.getTipoValorPensaoAlimenticia())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Percentual (%) deve ser informado.");
            }
        }
        if (valorPensaoAlimenticia.getBeneficioPensaoAlimenticia().isPensaoValorBase()) {
            if (valorPensaoAlimenticia.getBaseFP() == null
                || (valorPensaoAlimenticia.getBaseFP().getId() == null
                && valorPensaoAlimenticia.getBaseFP().getItensBasesFPs().isEmpty())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo BaseFP é obrigatório");
            }
            if (ModoSelecao.EXISTENTE.equals(modoSelecao) && !edicaoValores && !clonando) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Clonar a BaseFP é obrigatório.");
            }
        }
        ve.lancarException();
        validarVigencia(valorPensaoAlimenticia);
        validarValor(valorPensaoAlimenticia);
    }

    public void validarValor(ValorPensaoAlimenticia valorPensaoAlimenticia) {
        ValidacaoException ve = new ValidacaoException();
        if (valorPensaoAlimenticia.getValor() != null) {
            if (valorPensaoAlimenticia.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                if (beneficio.getTipoValorPensaoAlimenticia().equals(TipoValorPensaoAlimenticia.VALOR_FIXO)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor não pode ser menor ou igual a zero.");
                }
                if (beneficio.getTipoValorPensaoAlimenticia().equals(TipoValorPensaoAlimenticia.PERCENTUAL_SOBRE_SALARIO_MINIMO)
                    || beneficio.getTipoValorPensaoAlimenticia().equals(TipoValorPensaoAlimenticia.BASE)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Percentual não pode ser menor ou igual a zero.");
                }
                if (beneficio.getTipoValorPensaoAlimenticia().equals(TipoValorPensaoAlimenticia.SALARIO_MINIMO_INTEGRAL)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Quantidade não pode ser menor ou igual a zero.");
                }
            }
        }
        ve.lancarException();
    }

    public void validarVigencia(ValorPensaoAlimenticia valorPensaoAlimenticia) {
        ValidacaoException ve = new ValidacaoException();
        if (!this.valorPensaoAlimenticia.equals(valorPensaoAlimenticia) && valorPensaoAlimenticia.getInicioVigencia().before(beneficio.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O dia " + DataUtil.getDataFormatada(valorPensaoAlimenticia.getInicioVigencia()) +
                " colocado como Início da Vigência do valor é inferior ao dia " + DataUtil.getDataFormatada(beneficio.getInicioVigencia()) +
                " colocado como Início da Vigência da pensão.");
        }
        if (beneficio.getFinalVigencia() != null && valorPensaoAlimenticia.getFimVigencia() != null
            && valorPensaoAlimenticia.getFimVigencia().before(beneficio.getFinalVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O dia " + DataUtil.getDataFormatada(valorPensaoAlimenticia.getFinalVigencia()) +
                " colocado como Final da Vigência do valor é inferior ao dia " + DataUtil.getDataFormatada(beneficio.getInicioVigencia()) +
                " colocado como Início da Vigência da pensão.");
        }
        if (beneficio.getFinalVigencia() != null && valorPensaoAlimenticia.getInicioVigencia().after(beneficio.getFinalVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O dia " + DataUtil.getDataFormatada(valorPensaoAlimenticia.getInicioVigencia()) +
                " colocado como Início da Vigência do valor é superior ao dia " + DataUtil.getDataFormatada(beneficio.getFinalVigencia()) +
                " colocado como Final da Vigência da pensão.");
        }
        if (beneficio.getFinalVigencia() != null && valorPensaoAlimenticia.getFinalVigencia() != null
            && valorPensaoAlimenticia.getFimVigencia().after(beneficio.getFinalVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O dia " + DataUtil.getDataFormatada(valorPensaoAlimenticia.getFinalVigencia()) +
                " colocado como Final da Vigência do valor é superior ao dia " + DataUtil.getDataFormatada(beneficio.getFinalVigencia()) +
                " colocado como Final da Vigência da pensão.");
        }
        ve.lancarException();
        validarVigenciaValores(valorPensaoAlimenticia);
    }

    public void validarVigenciaValores(ValorPensaoAlimenticia valorPensao) {
        ValidacaoException ve = new ValidacaoException();
        if (!beneficio.getValoresPensaoAlimenticia().isEmpty()) {
            for (ValorPensaoAlimenticia pensaoAlimenticia : beneficio.getValoresPensaoAlimenticia()) {
                if (!valorPensao.equals(pensaoAlimenticia)) {
                    if (pensaoAlimenticia.getInicioVigencia() != null && pensaoAlimenticia.getFinalVigencia() != null) {
                        if (valorPensao.getInicioVigencia().compareTo(pensaoAlimenticia.getInicioVigencia()) == 0) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um valor cadastrado com Início da Vigência no dia "
                                + DataUtil.getDataFormatada(pensaoAlimenticia.getInicioVigencia()));
                        }
                        if (valorPensao.getInicioVigencia().compareTo(pensaoAlimenticia.getFinalVigencia()) == 0) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um valor cadastrado com Final da Vigência no dia "
                                + DataUtil.getDataFormatada(pensaoAlimenticia.getInicioVigencia()));
                        }
                        if (valorPensao.getInicioVigencia().compareTo(pensaoAlimenticia.getInicioVigencia()) == 1
                            && valorPensao.getInicioVigencia().compareTo(pensaoAlimenticia.getFinalVigencia()) == -1) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("A data " + DataUtil.getDataFormatada(valorPensao.getInicioVigencia()) +
                                " selecionada para Início da Vigência encontra-se no intervalo de um valor já cadastrado");
                        }
                    }
                    if (pensaoAlimenticia.getFinalVigencia() == null) {
                        if (valorPensao.getInicioVigencia().compareTo(pensaoAlimenticia.getInicioVigencia()) == 0) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um valor cadastrado com Início da Vigência no dia "
                                + DataUtil.getDataFormatada(pensaoAlimenticia.getInicioVigencia()));
                        }
                        if (valorPensao.getInicioVigencia().compareTo(pensaoAlimenticia.getInicioVigencia()) == 1) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Foi encontrado um valor vigente, para adicionar um novo valor o anterior deve ser encerrado.");
                        }
                        if (valorPensao.getFinalVigencia() != null && valorPensao.getFinalVigencia().compareTo(pensaoAlimenticia.getInicioVigencia()) == 0) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um valor cadastrado com Início da Vigência no dia "
                                + DataUtil.getDataFormatada(pensaoAlimenticia.getInicioVigencia()));
                        }
                    }
                    if ((valorPensao.getInicioVigencia().compareTo(pensaoAlimenticia.getInicioVigencia()) == -1)
                        && (valorPensao.getFinalVigencia() == null
                        || valorPensao.getFinalVigencia().compareTo(pensaoAlimenticia.getInicioVigencia()) == 1)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Foi encontrado um valor com vigência superior ao Início de Vigência desejado, " +
                            "com isso é necessário informar um Final de Vigência inferior a data " + DataUtil.getDataFormatada(pensaoAlimenticia.getInicioVigencia()));
                    }
                }
            }
        }
        if (valorPensao.getFinalVigencia() != null && valorPensao.getFinalVigencia().before(valorPensao.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de Final de Vigência deve ser igual ou superior a data de Início de Vigência.");
        }
        ve.lancarException();
    }

    public void validarFimVigencia(ValorPensaoAlimenticia valorPensao) {
        if (valorPensao.getInicioVigencia() != null
            && valorPensao.getFimVigencia() != null
            && valorPensao.getFimVigencia().before(valorPensao.getInicioVigencia())) {
            valorPensao.setFimVigencia(null);
            FacesUtil.addOperacaoNaoPermitida("A data fim de vigência deve ser superior a data de inicío de vigência.");
        }
    }


    public Boolean validaItemValorBeneficio() {
        Boolean retorno = true;

        if (valorPensaoAlimenticia.getValor() != null && valorPensaoAlimenticia.getBaseFP() == null) {
            if (valorPensaoAlimenticia.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                FacesContext.getCurrentInstance().addMessage("FormularioDialog:val", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Não é possível criar um benefício com valor menor ou igual a 0"));
                retorno = false;
            }
        } else if (valorPensaoAlimenticia.getValor() == null) {
            FacesContext.getCurrentInstance().addMessage("FormularioDialog:val", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "O campo Valor é obrigatório!"));
            retorno = false;
        }
        if (valorPensaoAlimenticia.getInicioVigencia() == null) {
            FacesContext.getCurrentInstance().addMessage("FormularioDialog:inicioVigenciaValor", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "O Início de Vigência é obrigatório!"));
            retorno = false;
        } else if (valorPensaoAlimenticia.getInicioVigencia().before(beneficio.getInicioVigencia())) {
            FacesUtil.addMessageWarn("FormularioDialog:msgsDialog", "Atenção", "A data de início da vigência do valor não pode ser inferior a data de início da pensão !");
            retorno = false;
        }
        if (valorPensaoAlimenticia.getFinalVigencia() != null && valorPensaoAlimenticia.getInicioVigencia() != null) {
            if (valorPensaoAlimenticia.getInicioVigencia().after(valorPensaoAlimenticia.getFinalVigencia())) {
                FacesContext.getCurrentInstance().addMessage("FormularioDialog:msgsDialog", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A data de início de vigência não pode ser maior que a data final de vigência!"));
                retorno = false;
            }
        }
        if (beneficio != null) {
            for (ValorPensaoAlimenticia item : beneficio.getValoresPensaoAlimenticia()) {
                if (item.getFinalVigencia() == null && (beneficio.getInicioVigencia().after(item.getInicioVigencia()))) {
                    FacesContext.getCurrentInstance().addMessage("FormularioDialog:msgsDialog", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "O Pensionista " + beneficio.getPessoaFisicaBeneficiario().getNome() + " possui uma vigência aberta com início anterior à data informada!"));
                    retorno = false;

                } else if (Util.validaVigencia(valorPensaoAlimenticia.getInicioVigencia(), valorPensaoAlimenticia.getFinalVigencia())) {
                    if (Util.validaVigencia(item.getInicioVigencia(), item.getFinalVigencia())) {
                        FacesContext.getCurrentInstance().addMessage("FormularioDialog:msgsDialog", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "O Pensionista " + beneficio.getPessoaFisicaBeneficiario().getNome() + " já possui um valor vigente!"));
                        retorno = false;
                    }
                }
            }
        }
        return retorno;
    }

    public void validarItemBeneficiario() {
        ValidacaoException ve = new ValidacaoException();
        validarNovoBeneficiario();
        if (getBeneficioPensaoAlimenticia().getPessoaFisicaBeneficiario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Beneficiário da Pensão Alimentícia deve ser informado.");
        }

        if (getBeneficioPensaoAlimenticia().getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência deve ser informado.");
        }

        if (getBeneficioPensaoAlimenticia().getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal deve ser informado.");
        }

        if (getBeneficioPensaoAlimenticia().getEventoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Verba de Desconto deve ser informado.");
        }

        if (getBeneficioPensaoAlimenticia().getTipoValorPensaoAlimenticia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Valor deve ser informado.");
        }

        if (getBeneficioPensaoAlimenticia().getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Hierarquia Organizacional deve ser informado.");
        }

        if (getBeneficioPensaoAlimenticia().getPessoaFisicaBeneficiario() != null) {
            for (BeneficioPensaoAlimenticia ben : getSelecionado().getBeneficiosPensaoAlimenticia()) {
                if (isBeneficiarioIdentico(ben)
                    && isTipoValorPensaoAlimenticiaIdentico(ben)
                    && isFinalVigenciaDefinidaBeneficiario(ben)
                    && isDiferencaDiasMenorIgualZero(ben)
                    && !ben.equals(getBeneficioPensaoAlimenticia())
                ) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Este Beneficiário já foi inserido na lista.");
                }
            }
        }

        if (getBeneficioPensaoAlimenticia().getInicioVigencia() != null && getBeneficioPensaoAlimenticia().getFinalVigencia() != null) {
            if (getBeneficioPensaoAlimenticia().getInicioVigencia().after(getBeneficioPensaoAlimenticia().getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de Início da Vigência não pode ser superior a data Final de Vigência.");
            }
        }

        if (getBeneficioPensaoAlimenticia().getPessoaFisicaBeneficiario() != null && getBeneficioPensaoAlimenticia().getPessoaFisicaResponsavel() != null) {
            if (getBeneficioPensaoAlimenticia().getPessoaFisicaBeneficiario().equals(getBeneficioPensaoAlimenticia().getPessoaFisicaResponsavel())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Responsável não pode ser o Beneficiário.");
            }
        }
        ve.lancarException();
    }

    public boolean isDiferencaDiasMenorIgualZero(BeneficioPensaoAlimenticia ben) {
        return DataUtil.diferencaDiasInteira(ben.getFinalVigencia(), getBeneficioPensaoAlimenticia().getInicioVigencia()) <= 0;
    }

    public boolean isFinalVigenciaDefinidaBeneficiario(BeneficioPensaoAlimenticia ben) {
        return ben.getFinalVigencia() != null;
    }

    public boolean isTipoValorPensaoAlimenticiaIdentico(BeneficioPensaoAlimenticia ben) {
        return ben.getTipoValorPensaoAlimenticia().equals(getBeneficioPensaoAlimenticia().getTipoValorPensaoAlimenticia());
    }

    public boolean isBeneficiarioIdentico(BeneficioPensaoAlimenticia ben) {
        return ben.getPessoaFisicaBeneficiario().equals(getBeneficioPensaoAlimenticia().getPessoaFisicaBeneficiario());
    }

    //TODO
    private void verificarInstituidores() {
        beneficiariosEncontrados = Lists.newArrayList();
        if (getBeneficioPensaoAlimenticia().getPessoaFisicaBeneficiario() != null) {
            List<PensaoAlimenticia> pensoes = pensaoAlimenticiaFacade.buscarPensoesAlimenticiasPorBeneficiario(getBeneficioPensaoAlimenticia().getPessoaFisicaBeneficiario(), sistemaControlador.getDataOperacao());
            if (!pensoes.isEmpty()) {
                for (PensaoAlimenticia pensao : pensoes) {
                    beneficiariosEncontrados.add("O Beneficiário selecionado já é instituído por " + pensao.getVinculoFP());
                }
            }
        }
    }

    public void verificarInstituidorBeneficiario() {
        verificarInstituidores();
        if (!beneficiariosEncontrados.isEmpty()) {
            FacesUtil.executaJavaScript("mensagens.show()");
        }
    }

    public void limparBeneficiario() {
        getBeneficioPensaoAlimenticia().setPessoaFisicaBeneficiario(null);
    }

    public List<SelectItem> getEventoFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (EventoFP object : pensaoAlimenticiaFacade.getEventoFPFacade().listaEventosAtivosFolha()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getModos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Selecione um modo"));
        for (ModoSelecao object : ModoSelecao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public BigDecimal recuperaValorSalarioMinimo() {
        return pensaoAlimenticiaFacade.getReferenciaFPFacade().recuperaSalarioMinimoVigente();
    }

    public void calcularPercentualSobreSalarioMinimo() {
        if (valorPensaoAlimenticia.getValor() != null && valorPensaoAlimenticia.getValor().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addWarn("Atenção!", "O percentual deve ser maior que zero.");
            valorPensaoAlimenticia.setValor(new BigDecimal("100"));
        }
    }

    public BigDecimal recuperaValorCalculadoSobrePercentualSalarioMinimo(BigDecimal percentual) {
        if (percentual != null) {
            return recuperaValorSalarioMinimo().multiply(percentual.divide(new BigDecimal("100")));
        }
        return null;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/pensao-alimenticia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void buscarBaseFP() {
        BaseFP base = valorPensaoAlimenticia.getBaseFP();
        if (base != null && !base.getCodigo().equals("")) {
            BaseFP b = pensaoAlimenticiaFacade.getBaseFPFacade().getBaseByCodigoEFiltro(base.getCodigo().trim(), FiltroBaseFP.PENSAO_ALIMENTICIA);
            if (b != null) {
                setBaseFP(b);
            } else {
                if (base != null && !operacao.equals(Operacoes.EDITAR))
                    if (pensaoAlimenticiaFacade.getBaseFPFacade().existeCodigo(getBaseFP().getCodigo())) {
                        FacesUtil.addWarn("Atenção", "Este código não pode ser utilizado");
                        getBaseFP().setCodigo("");
                    }

            }
        }
    }

    public void validarAndFecharDialog() {
        if (valorPensaoAlimenticia != null
            && valorPensaoAlimenticia.getBaseFP() != null
            && valorPensaoAlimenticia.getBaseFP().getItensBasesFPs() != null
            && !valorPensaoAlimenticia.getBaseFP().getItensBasesFPs().isEmpty()) {
            FacesUtil.executaJavaScript("dialogbasefp.hide()");
        } else {
            FacesUtil.addError("Atenção", "É obrigatório pelo menos um evento a base.");
        }
    }

    public void associarBase() {

        if (valorPensaoAlimenticia != null && isTipoBase(valorPensaoAlimenticia.getBeneficioPensaoAlimenticia())) {
            BaseFP baseFP = new BaseFP();
            baseFP.setDescricao(valorPensaoAlimenticia.getBeneficioPensaoAlimenticia().getTipoValorPensaoAlimenticia().getDescricao() + " " + selecionado.getVinculoFP());
            valorPensaoAlimenticia.setBaseFP(baseFP);
            procurarEDefinirNovoCodigoBaseFP(valorPensaoAlimenticia);

        }
    }

    public void definirBase() {
        if (!isOperacaoEditar()) {
            if (baseAux != null && TipoValorPensaoAlimenticia.BASE_BRUTO.equals(beneficio.getTipoValorPensaoAlimenticia())) {
                valorPensaoAlimenticia.setBaseFP(baseAux);
            }
        }
    }

    public void definirBase(ValorPensaoAlimenticia val) {
        if (isTipoBase(val.getBeneficioPensaoAlimenticia())) {
            valorPensaoAlimenticia = val;
        }
    }

    public void clonarBase() {
        try {
            validarValorPensaoParaClonar();
            clonarBaseFP(valorPensaoAlimenticia);
            if (valorPensaoAlimenticia != null && valorPensaoAlimenticia.getBaseFP() != null && valorPensaoAlimenticia.getBaseFP().getId() == null) {
                procurarEDefinirNovoCodigoBaseFP(valorPensaoAlimenticia);
            }
            FacesUtil.executaJavaScript("dialogbasefp.show()");
            clonando = true;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarValorPensaoParaClonar() {
        ValidacaoException ve = new ValidacaoException();
        if (valorPensaoAlimenticia.getBaseFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor, informe a BaseFP para clonar!");
        }
        ve.lancarException();
    }

    private void clonarBaseFP(ValorPensaoAlimenticia valorPensaoAlimenticia) {
        BaseFP outra = pensaoAlimenticiaFacade.getBaseFPFacade().recuperar(valorPensaoAlimenticia.getBaseFP().getId());
        BaseFP base = new BaseFP();
        base.setFiltroBaseFP(FiltroBaseFP.PENSAO_ALIMENTICIA);
        base.setDescricao(outra.getDescricao());
        base.setCodigo(outra.getCodigo());
        base.setDescricaoReduzida(outra.getDescricaoReduzida());
        base.setItensBasesFPs(clonarIntesBase(outra.getItensBasesFPs(), base));

        valorPensaoAlimenticia.setBaseFP(base);

    }

    private List<ItemBaseFP> clonarIntesBase(List<ItemBaseFP> itensBasesFPs, BaseFP base) {
        List<ItemBaseFP> novos = Lists.newLinkedList();

        for (ItemBaseFP itensBasesFP : itensBasesFPs) {
            ItemBaseFP item = new ItemBaseFP();
            item.setBaseFP(base);
            item.setEventoFP(itensBasesFP.getEventoFP());
            item.setDataRegistro(new Date());
            item.setOperacaoFormula(itensBasesFP.getOperacaoFormula());
            item.setTipoValor(itensBasesFP.getTipoValor());
            item.setSomaValorRetroativo(itensBasesFP.getSomaValorRetroativo());
            novos.add(item);

        }
        return novos;

    }

    private void procurarEDefinirNovoCodigoBaseFP(ValorPensaoAlimenticia valorPensaoAlimenticia) {
        if (valorPensaoAlimenticia != null && valorPensaoAlimenticia.getBaseFP() != null) {
            String b = pensaoAlimenticiaFacade.getSingletonGeradorCodigoRH().getProximoCodigoBaseFPPensaoAlimenticia();
            if (b != null) {
                valorPensaoAlimenticia.getBaseFP().setCodigo(b);
            }
        }
    }

    private void procurarEDefinirNovoCodigoBaseFP(BaseFP base) {
        if (base != null) {
            String b = pensaoAlimenticiaFacade.getSingletonGeradorCodigoRH().getProximoCodigoBaseFPPensaoAlimenticia();
            if (b != null) {
                base.setCodigo(b);
            }
        }
    }


    public void instanciarBaseConformeTipo() {
        clonando = false;
        if (ModoSelecao.NOVO.equals(modoSelecao)) {
            itemBaseFP = new ItemBaseFP();
            if (baseAux != null && baseAux.getItensBasesFPs().isEmpty()) {
                associarBase();
            }
        } else {
            valorPensaoAlimenticia.setBaseFP(null);
        }
    }

    public void adicionarItemBaseFP() {
        try {
            validarBaseFP();
            baseAux = valorPensaoAlimenticia.getBaseFP();
            itemBaseFP.setBaseFP(baseAux);
            baseAux.getItensBasesFPs().add(itemBaseFP);
            Collections.sort(baseAux.getItensBasesFPs());
            itemBaseFP = new ItemBaseFP();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarBaseFP() {
        ValidacaoException ve = new ValidacaoException();
        if (valorPensaoAlimenticia.getBaseFP() != null) {
            if (valorPensaoAlimenticia.getBaseFP().getDescricao().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
            }
            for (ItemBaseFP item : valorPensaoAlimenticia.getBaseFP().getItensBasesFPs()) {
                if ((item.getEventoFP().equals(itemBaseFP.getEventoFP()))
                    && (item.getOperacaoFormula().equals(itemBaseFP.getOperacaoFormula()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Essa verba já existe na base atual.");
                }
            }
        }
        ve.lancarException();
    }

    public BaseFP getBaseAux() {
        return baseAux;
    }

    public void setBaseAux(BaseFP baseAux) {
        this.baseAux = baseAux;
    }

    public List<SelectItem> getGrauDeParentesco() {
        return Util.getListSelectItem(GrauParentescoDirf.values());
    }

    public List<String> getBeneficiariosEncontrados() {
        return beneficiariosEncontrados;
    }

    public void setBeneficiariosEncontrados(List<String> beneficiariosEncontrados) {
        this.beneficiariosEncontrados = beneficiariosEncontrados;
    }

    public enum ModoSelecao {
        NOVO("Novo"),
        EXISTENTE("Existente");
        String descricao;

        ModoSelecao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

}
