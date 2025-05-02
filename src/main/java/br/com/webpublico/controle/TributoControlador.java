/*
 * Codigo gerado automaticamente em Mon Dec 05 14:26:26 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TributoFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "tributoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "criarTributo",
        pattern = "/tributo/novo/",
        viewId = "/faces/tributario/tributo/edita.xhtml"),
    @URLMapping(id = "editarTributo",
        pattern = "/tributo/editar/#{tributoControlador.id}/",
        viewId = "/faces/tributario/tributo/edita.xhtml"),
    @URLMapping(id = "listarTributo",
        pattern = "/tributo/listar/",
        viewId = "/faces/tributario/tributo/lista.xhtml"),
    @URLMapping(id = "verTributo",
        pattern = "/tributo/ver/#{tributoControlador.id}/",
        viewId = "/faces/tributario/tributo/visualizar.xhtml"),
}
)
public class TributoControlador extends PrettyControlador<Tributo> implements Serializable, CRUD {

    @EJB
    private TributoFacade tributoFacade;
    private ConverterAutoComplete converterEntidade;
    private Entidade entidade;
    private Exercicio exercicio;
    private ConverterExercicio converterExercicio;
    private ConverterGenerico converterEnquadramento;
    private ConverterAutoComplete converterTributo;
    private ConverterAutoComplete converterReceitaLOA;
    private ContaTributoReceita conta;
    private Map<KeyMapaFonteRecursos, List<FonteDeRecursos>> mapFontesDeRecurso = Maps.newHashMap();

    public TributoControlador() {
        super(Tributo.class);
    }

    public ContaTributoReceita getConta() {
        return conta;
    }

    public void setConta(ContaTributoReceita conta) {
        this.conta = conta;
    }

    public Converter getConverterEnquadramento() {
        if (converterEnquadramento == null) {
            converterEnquadramento = new ConverterGenerico(EnquadramentoTributoExerc.class, tributoFacade.getEnquadramentoFacade());
        }
        return converterEnquadramento;
    }

    public Converter getConverterReceitaLOA() {
        if (converterReceitaLOA == null) {
            converterReceitaLOA = new ConverterAutoComplete(Conta.class, tributoFacade.getContaFacade());
        }
        return converterReceitaLOA;
    }

    public Converter getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, tributoFacade);
        }
        return converterTributo;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(tributoFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<SelectItem> getExercicios() {
        List<Exercicio> exercicios = tributoFacade.getExercicioFacade().lista();
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (Exercicio exerc : exercicios) {
            retorno.add(new SelectItem(exerc, exerc.getAno().toString()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoTributo() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (Tributo.TipoTributo tipo : Tributo.TipoTributo.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getOperacoesReceitas() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (OperacaoReceita or : OperacaoReceita.retornarOperacoesReceitaNormal()) {
            retorno.add(new SelectItem(or, or.getDescricao()));
        }
        return retorno;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public TributoFacade getFacade() {
        return tributoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tributoFacade;
    }

    public List<SelectItem> getEnquadramentosDoExercicio() {
        List<EnquadramentoTributoExerc> listaEnquadramentos = tributoFacade.getEnquadramentoFacade().recuperaPorExercicioVigente(exercicio);
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (EnquadramentoTributoExerc e : listaEnquadramentos) {
            retorno.add(new SelectItem(e, e.getCodigo() + " - " + e.getDescricao()));
        }
        return retorno;
    }

    public void atualizaEnquadramentos() {

        if (exercicio != null) {
            this.exercicio = tributoFacade.getExercicioFacade().recuperar(this.exercicio.getId());
        }
    }

    public List<Conta> completaReceitaLOA(String parte) {
        if (exercicio != null && exercicio.getId() != null) {
            return tributoFacade.getContaFacade().listaFiltrandoReceitaAnalitica(parte.trim(), exercicio);
        } else {
            FacesUtil.addWarn("Não foi possível continuar!", "Informe o exercício.");
            FacesUtil.atualizarComponente("Formulario");
        }
        return new ArrayList<>();
    }

    @Override
    public void salvar() {
        super.salvar(Redirecionar.VER);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "criarTributo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        conta = new ContaTributoReceita();
        selecionado.setCodigo(tributoFacade.sugereCodigoTributo());

    }

    @Override
    @URLAction(mappingId = "editarTributo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        Collections.sort(selecionado.getContaTributoReceitas());
        conta = new ContaTributoReceita();
    }

    @Override
    @URLAction(mappingId = "verTributo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    private boolean validaCodigo() {
        boolean resultado = true;
        if (selecionado.getCodigo() == null || selecionado.getCodigo().intValue() <= 0) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe um código maior que zero.");
        } else if (tributoFacade.codigoJaExiste(selecionado)) {
            selecionado.setCodigo(tributoFacade.sugereCodigoTributo());
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "O código informado já existe em outro Tributo. O sistema gerou novo código.");
        } else if (selecionado.getCodigo() > 9999) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "O código informado não pode conter mais que 4 dígitos.");
        }
        return resultado;
    }

    public List<Entidade> completeEntidade(String parte) {
        return tributoFacade.getEntidadeFacade().listaEntidades(parte.trim());
    }

    public List<Tributo> completeTributo(String parte) {
        return tributoFacade.listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public ConverterAutoComplete getConverterEntidade() {
        if (converterEntidade == null) {
            converterEntidade = new ConverterAutoComplete(Entidade.class, tributoFacade.getEntidadeFacade());
        }
        return converterEntidade;
    }


    private boolean validaDescricao() {
        return this.tributoFacade.validaDescricao(selecionado);
    }

    public void validarAdicionarConta() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício deve ser informados.");
        }
        if (conta.getEnquadramento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Enquadramento deve ser informados.");
        }
        if (conta.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Início de Vigência deve ser informados.");
        }
        ve.lancarException();

        if (!DataUtil.getAno(conta.getInicioVigencia()).equals(exercicio.getAno())) {
            ve.adicionarMensagemDeCampoObrigatorio("O Início de Vigência deve estar dentro do exercício informado.");
        }

        if (conta.getFimVigencia() != null && !DataUtil.getAno(conta.getFimVigencia()).equals(exercicio.getAno())) {
            ve.adicionarMensagemDeCampoObrigatorio("O Final de Vigência deve estar dentro do exercício informado.");
        }
        ve.lancarException();

        for (ContaTributoReceita contaTributoReceita : selecionado.getContaTributoReceitas()) {
            if (exercicio.getId().equals(contaTributoReceita.getEnquadramento().getExercicioVigente().getId())) {
                boolean adicionouFinal = false;
                if (contaTributoReceita.getFimVigencia() == null) {
                    contaTributoReceita.setFimVigencia(DataUtil.adicionaDias(conta.getInicioVigencia(), -1));
                    adicionouFinal = true;
                }
                if (!DataUtil.isVigenciaValida(conta, selecionado.getContaTributoReceitas())) {
                    if (adicionouFinal) {
                        contaTributoReceita.setFimVigencia(null);
                    }
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Enquadramento com o exercício de " + exercicio.getAno() + " já foi adicionado na vigência!");
                    break;
                }
            }
        }

        ve.lancarException();
    }

    public void adicionarConta() {
        try {
            validarAdicionarConta();
            conta.setTributo(selecionado);
            selecionado.getContaTributoReceitas().add(conta);
            this.exercicio = null;
            conta = new ContaTributoReceita();
            Collections.sort(selecionado.getContaTributoReceitas());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerConta(ContaTributoReceita aRemover) {
        conta = aRemover;
        selecionado.setContasParaRemover(Util.adicionarObjetoEmLista(selecionado.getContasParaRemover(), conta));
        selecionado.getContaTributoReceitas().remove(conta);
        conta = new ContaTributoReceita();
    }

    public void alterarConta(ContaTributoReceita aAlterar) {
        conta = aAlterar;
        this.exercicio = conta.getEnquadramento().getExercicioVigente();
        selecionado.getContaTributoReceitas().remove(conta);

    }


    public String formataNumero(String numero) {
        if (numero.length() == 7) {
            return "0" + numero;
        } else if (numero.length() == 6) {
            return "00" + numero;
        } else if (numero.length() == 5) {
            return "000" + numero;
        } else if (numero.length() == 4) {
            return "0000" + numero;
        } else if (numero.length() == 3) {
            return "00000" + numero;
        } else if (numero.length() == 2) {
            return "000000" + numero;
        } else if (numero.length() == 1) {
            return "0000000" + numero;
        } else if (numero.length() == 0) {
            return "0";
        }
        return numero;
    }

    @Override
    protected boolean validaRegrasParaSalvar() {
        boolean validou = super.validaRegrasParaSalvar();
        if (validou) {
            if (!validaCodigo()) {
                FacesUtil.addError("Operação não permitida!", "O código informado já existe em outro Tributo.");
                validou = false;
            }
            if (!validaDescricao()) {
                FacesUtil.addError("Operação não permitida!", "A descrição informada já existe para outro Tributo.");
                validou = false;
            }
        }
        return validou;
    }

    public void selecionouContaReceitaArrecadacaoExercicio() {
        try {
            avisoContaReceita(conta.getContaExercicio());
            conta.setOperacaoArrecadacaoExercicio(null);
            conta.setFonteDeRecursosExercicio(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void selecionouOperacaoReceitaArrecadacaoExercicio() {
        conta.setFonteDeRecursosExercicio(null);
    }

    public void selecionouContaReceitaArrecadacaoDividaAtiva() {
        try {
            avisoContaReceita(conta.getContaDividaAtiva());
            conta.setOperacaoArrecadacaoDivAtiva(null);
            conta.setFonteDeRecursosDividaAtiva(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void selecionouOperacaoReceitaArrecadacaoDividaAtiva() {
        conta.setFonteDeRecursosDividaAtiva(null);
    }

    public void selecionouContaReceitaRenunciaExercicio() {
        try {
            avisoContaReceita(conta.getContaRenunciaExercicio());
            conta.setOperacaoRenunciaExercicio(null);
            conta.setFonteDeRecursosRenunciaExercicio(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void selecionouContaReceitaRenunciaDividaAtiva() {
        try {
            avisoContaReceita(conta.getContaRenunciaDividaAtiva());
            conta.setOperacaoRenunciaDivAtiva(null);
            conta.setFonteDeRecursosRenunciaDividaAtiva(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void selecionouContaReceitaRestituicaoExercicio() {
        try {
            avisoContaReceita(conta.getContaRestituicaoExercicio());
            conta.setOperacaoRestituicaoExercicio(null);
            conta.setFonteDeRecursosRestituicaoExercicio(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void selecionouOperacaoReceitaRestituicaoExercicio() {
        conta.setFonteDeRecursosRestituicaoExercicio(null);
    }

    public void selecionouContaReceitaRestituicaoDividaAtiva() {
        try {
            avisoContaReceita(conta.getContaRestituicaoDividaAtiva());
            conta.setOperacaoRestituicaoDivAtiva(null);
            conta.setFonteDeRecursosRestituicaoDividaAtiva(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void selecionouOperacaoReceitaRestituicaoDividaAtiva() {
        conta.setFonteDeRecursosRestituicaoDividaAtiva(null);
    }

    public void selecionouContaReceitaDescontoExercicio() {
        try {
            avisoContaReceita(conta.getContaDescontoExercicio());
            conta.setOperacaoDescConcedidoExercicio(null);
            conta.setFonteDeRecursosDescontoExercicio(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void selecionouOperacaoReceitaDescontoExercicio() {
        conta.setFonteDeRecursosDescontoExercicio(null);
    }

    public void selecionouContaReceitaDescontoDividaAtiva() {
        try {
            avisoContaReceita(conta.getContaDescontoDividaAtiva());
            conta.setOperacaoDescConcedidoDivAtiva(null);
            conta.setFonteDeRecursosDescontoDividaAtiva(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void selecionouOperacaoReceitaDescontoDividaAtiva() {
        conta.setFonteDeRecursosDescontoDividaAtiva(null);
    }

    public void selecionouContaReceitaDeducoesExercicio() {
        try {
            avisoContaReceita(conta.getContaDeducoesExercicio());
            conta.setOperacaoOutraDeducaoExercicio(null);
            conta.setFonteDeRecursosDeducoesExercicio(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void selecionouOperacaoReceitaDeducaoExercicio() {
        conta.setFonteDeRecursosDeducoesExercicio(null);
    }

    public void selecionouContaReceitaDeducoesDividaAtiva() {
        try {
            avisoContaReceita(conta.getContaDeducoesDividaAtiva());
            conta.setOperacaoOutraDeducaoDivAtiva(null);
            conta.setFonteDeRecursosDeducoesDividaAtiva(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void selecionouOperacaoReceitaDeducaoDividaAtiva() {
        conta.setFonteDeRecursosDeducoesDividaAtiva(null);
    }

    public void avisoContaReceita(ContaReceita contaReceita) {
        if (contaReceita != null && !tributoFacade.getContaFacade().existeEssaContaNaReceitaLoa(contaReceita)) {
            FacesUtil.addAtencao("A conta de receita " + contaReceita + " não foi inclusa em uma Receita LOA!");
        }
    }

    public void selecionaConta(SelectEvent e) {
        ContaReceita c = (ContaReceita) e.getObject();
        if (!tributoFacade.getContaFacade().existeEssaContaNaReceitaLoa(c)) {
            FacesUtil.addWarn("Atenção!", "A conta de receita " + c + " não foi inclusa em uma Receita LOA!");
        }
    }

    public List<Tributo> completarTributos(String parte) {
        return tributoFacade.buscarTributosPorDescricao(parte);
    }

    public List<Tributo> completarTributosTaxa(String parte) {
        return tributoFacade.buscarTributoPorTipo(parte, Tributo.TipoTributo.TAXA);
    }

    public List<SelectItem> getFontesDeReceitaPorConta(ContaReceita contaReceita, OperacaoReceita operacaoReceita) {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        if (selecionado.getEntidade() != null && exercicio != null && contaReceita != null && operacaoReceita != null) {
            KeyMapaFonteRecursos keyMapaFonteRecursos = new KeyMapaFonteRecursos(selecionado.getEntidade().getUnidadeOrganizacionalOrc(),
                exercicio, contaReceita, operacaoReceita);
            List<FonteDeRecursos> fontesDeRecursos = mapFontesDeRecurso.get(keyMapaFonteRecursos);
            if (fontesDeRecursos == null) {
                fontesDeRecursos = tributoFacade.getFonteDeRecursosFacade()
                    .buscarFontesDeRecursoPorContaReceitaAndExercicio(selecionado.getEntidade().getUnidadeOrganizacionalOrc(),
                        exercicio, contaReceita, operacaoReceita);
                mapFontesDeRecurso.put(keyMapaFonteRecursos, fontesDeRecursos);
            }
            for (FonteDeRecursos fr : fontesDeRecursos) {
                retorno.add(new SelectItem(fr, fr.toString()));
            }
        }
        return retorno;
    }

    public boolean habilitarContaReceita() {
        return selecionado.getEntidade() != null
            && exercicio != null;
    }

    public boolean habilitarOperacaoReceita(ContaReceita contaReceita) {
        return contaReceita != null;
    }

    public boolean habilitarFonteDeRecursos(OperacaoReceita operacaoReceita) {
        return operacaoReceita != null;
    }

    public class KeyMapaFonteRecursos {
        private UnidadeOrganizacional unidadeOrganizacional;
        private Exercicio exercicio;
        private ContaReceita contaReceita;
        private OperacaoReceita operacaoReceita;

        public KeyMapaFonteRecursos() {
        }

        public KeyMapaFonteRecursos(UnidadeOrganizacional unidadeOrganizacional,
                                    Exercicio exercicio,
                                    ContaReceita contaReceita,
                                    OperacaoReceita operacaoReceita) {
            this.unidadeOrganizacional = unidadeOrganizacional;
            this.exercicio = exercicio;
            this.contaReceita = contaReceita;
            this.operacaoReceita = operacaoReceita;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KeyMapaFonteRecursos that = (KeyMapaFonteRecursos) o;
            return Objects.equals(unidadeOrganizacional, that.unidadeOrganizacional) && Objects.equals(exercicio, that.exercicio) && Objects.equals(contaReceita, that.contaReceita) && operacaoReceita == that.operacaoReceita;
        }

        @Override
        public int hashCode() {
            return Objects.hash(unidadeOrganizacional, exercicio, contaReceita, operacaoReceita);
        }
    }
}
