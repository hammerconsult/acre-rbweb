package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.NaturezaDividaSubvencao;
import br.com.webpublico.enums.TipoOrdenacaoSubvencao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 17/12/13
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "subvencaoParametroControlador")
@ViewScoped

@URLMappings(mappings = {
    @URLMapping(id = "novoParametroSubvencao", pattern = "/subvencao-parametro/novo/", viewId = "/faces/tributario/dividaativa/subvencao/parametrosubvencao/edita.xhtml"),
    @URLMapping(id = "editarParametroSubvencao", pattern = "/subvencao-parametro/editar/#{subvencaoParametroControlador.id}/", viewId = "/faces/tributario/dividaativa/subvencao/parametrosubvencao/edita.xhtml"),
    @URLMapping(id = "listarParametroSubvencao", pattern = "/subvencao-parametro/listar/", viewId = "/faces/tributario/dividaativa/subvencao/parametrosubvencao/lista.xhtml"),
    @URLMapping(id = "verParametroSubvencao", pattern = "/subvencao-parametro/ver/#{subvencaoParametroControlador.id}/", viewId = "/faces/tributario/dividaativa/subvencao/parametrosubvencao/visualizar.xhtml")
})
public class SubvencaoParametroControlador extends PrettyControlador<SubvencaoParametro> implements CRUD, Serializable {

    @EJB
    private SubvencaoParametroFacade subvencaoParametroFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    private ConverterAutoComplete converterCMC;
    private ConverterAutoComplete converterExercicio;
    private ConverterAutoComplete converterDivida;
    private CadastroEconomicoSubvencao cadastroEconomicoSubvencao;
    private CadastroEconomicoSubvencao empresaCredora;
    private DividaSubvencao dividaSubvencao;
    private EmpresaDevedoraSubvencao empresaDevedoraSubvencao;
    private OrdenacaoParcelaSubvencao ordenacaoParcelaSubvencao;

    public SubvencaoParametroControlador() {
        super(SubvencaoParametro.class);
        cadastroEconomicoSubvencao = new CadastroEconomicoSubvencao();
        empresaCredora = new CadastroEconomicoSubvencao();
        dividaSubvencao = new DividaSubvencao();
        empresaDevedoraSubvencao = new EmpresaDevedoraSubvencao();
        ordenacaoParcelaSubvencao = new OrdenacaoParcelaSubvencao();
    }


    @URLAction(mappingId = "novoParametroSubvencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        if (!temMaisRegistros()) {
            FacesUtil.addError("Já existe Parâmetro cadastrado", "");
            redireciona();
            return;
        }
        super.novo();
    }

    @URLAction(mappingId = "verParametroSubvencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametroSubvencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado = subvencaoParametroFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            getFacede().salvar(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void adicionarCadastroEconomicoSubvencao() {
        if (!validaCadastroEconomicoSubvencao()) {
            cadastroEconomicoSubvencao.setSubvencaoParametro(selecionado);
            selecionado.getCadastroEconomicoSubvencao().add(cadastroEconomicoSubvencao);
            cadastroEconomicoSubvencao = new CadastroEconomicoSubvencao();
        }
    }

    public EmpresaDevedoraSubvencao getEmpresaDevedoraSubvencao() {
        return empresaDevedoraSubvencao;
    }

    public void setEmpresaDevedoraSubvencao(EmpresaDevedoraSubvencao empresaDevedoraSubvencao) {
        this.empresaDevedoraSubvencao = empresaDevedoraSubvencao;
    }

    public CadastroEconomicoSubvencao getEmpresaCredora() {
        return empresaCredora;
    }

    public void setEmpresaCredora(CadastroEconomicoSubvencao empresaCredora) {
        this.empresaCredora = empresaCredora;
    }

    public void excluirCadastroEconomicoSubvencao(CadastroEconomicoSubvencao cad) {
        selecionado.getCadastroEconomicoSubvencao().remove(cad);
    }

    public OrdenacaoParcelaSubvencao getOrdenacaoParcelaSubvencao() {
        return ordenacaoParcelaSubvencao;
    }

    public void setOrdenacaoParcelaSubvencao(OrdenacaoParcelaSubvencao ordenacaoParcelaSubvencao) {
        this.ordenacaoParcelaSubvencao = ordenacaoParcelaSubvencao;
    }

    public void excluirDividaSubvencao(DividaSubvencao dividaSubvencao) {
        selecionado.getListaDividaSubvencao().remove(dividaSubvencao);
    }

    public void alterarCadastroEconomicoSubvencao(CadastroEconomicoSubvencao cad) {
        this.cadastroEconomicoSubvencao = cad;
        excluirCadastroEconomicoSubvencao(cad);
    }

    public void alterarDividaSubvencao(DividaSubvencao dividaSubvencao) {
        this.dividaSubvencao = dividaSubvencao;
        excluirDividaSubvencao(dividaSubvencao);
    }

    private boolean validaCadastroEconomicoSubvencao() {
        boolean retorno = false;
        for (CadastroEconomicoSubvencao cad : selecionado.getCadastroEconomicoSubvencao()) {
            if (cad.getCadastroEconomico().equals(cadastroEconomicoSubvencao.getCadastroEconomico())) {
                FacesUtil.addError("Não foi possível continuar!", "Empresa Credora já adicionada.");
                retorno = true;
            }
        }
        if (cadastroEconomicoSubvencao.getCadastroEconomico() == null) {
            FacesUtil.addError("Campo Obrigatório!", "A Empresa Credora deve ser informada.");
            retorno = true;

        }
        if (cadastroEconomicoSubvencao.getVigenciaInicial() == null) {
            FacesUtil.addError("Campo Obrigatório!", "Data Inicial da Vigência deve ser preenchida.");
            retorno = true;
        }

        if (cadastroEconomicoSubvencao.getVigenciaInicial() != null && cadastroEconomicoSubvencao.getVigenciaFinal() != null) {
            if (cadastroEconomicoSubvencao.getVigenciaInicial().after(cadastroEconomicoSubvencao.getVigenciaFinal())) {
                FacesUtil.addError("Não foi possível continuar!", "A Data Inicial da Vigência não pode ser maior que a Data Final da Vigência.");
                retorno = true;

            }
        }

        return retorno;
    }

    public void adicionarDivida() {
        try {
            validarDivida();
            dividaSubvencao.setSubvencaoParametro(selecionado);
            selecionado.getListaDividaSubvencao().add(dividaSubvencao);
            dividaSubvencao = new DividaSubvencao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public Boolean temMaisRegistros() {
        return subvencaoParametroFacade.temMaisDeUmRegistroCadastrado();
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean retorno = true;
        if (selecionado.getCadastroEconomicoSubvencao().isEmpty()) {
            FacesUtil.addError("Não foi possível continuar!", "Deve ser informado um C.M.C");
            retorno = false;
        }
        if (selecionado.getListaDividaSubvencao().isEmpty()) {
            FacesUtil.addError("Não foi possível continuar!", "Deve ser informado uma Dívida");
            retorno = false;
        }

        for (CadastroEconomicoSubvencao economicoSubvencao : selecionado.getCadastroEconomicoSubvencao()) {
            if (economicoSubvencao.getEmpresaDevedoraSubvencao().isEmpty()) {
                FacesUtil.addError("Não foi possível continuar!", "A empresa " + economicoSubvencao.getCadastroEconomico().getCmcNomeCpfCnpj() + " não possui empresa devedora cadastrada.");
                retorno = false;
            }
        }
        return retorno;
    }


    private void validarDivida() {
        ValidacaoException ve = new ValidacaoException();
        for (DividaSubvencao divida : selecionado.getListaDividaSubvencao()) {
            if (divida.getDivida().equals(dividaSubvencao.getDivida())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Dívida já adicionada.");
            }
        }
        if (dividaSubvencao.getDivida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Dívida deve ser preenchida.");
        }
        if (dividaSubvencao.getExercicioInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício Inicial deve ser preenchido.");
        }

        if (dividaSubvencao.getExercicioFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício Final deve ser preenchido.");
        }

        if (dividaSubvencao.getDataInicialVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Data Inicial da Vigência deve ser preenchida.");
        }

        if (dividaSubvencao.getExercicioInicial() != null && dividaSubvencao.getExercicioFinal() != null) {
            if (dividaSubvencao.getExercicioInicial().getAno() > dividaSubvencao.getExercicioFinal().getAno()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício Inicial não pode ser maior que o Exercício Final");
            }
        }

        if (dividaSubvencao.getDataInicialVigencia() != null && dividaSubvencao.getDataFinalVigencia() != null) {
            if (dividaSubvencao.getDataInicialVigencia().after(dividaSubvencao.getDataFinalVigencia())) {
                ve.adicionarMensagemDeCampoObrigatorio("A Data Inicial da Vigência não pode ser maior que a Data Final da Vigência.");
            }
        }

        if (dividaSubvencao.getQtdeMinimaParcela() != null && dividaSubvencao.getQtdeMaximaParcela() != null) {
            if (dividaSubvencao.getQtdeMinimaParcela() > dividaSubvencao.getQtdeMaximaParcela()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade mínima de parcelas não pode ser maior que a quantidade máxima de parcelas");
            }
        }
        if ((dividaSubvencao.getQtdeMinimaParcela() != null && dividaSubvencao.getQtdeMaximaParcela() == null) || (dividaSubvencao.getQtdeMinimaParcela() == null && dividaSubvencao.getQtdeMaximaParcela() != null)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe a quantidade máxima e quantidade mínima de parcelas.");
        }
        if (dividaSubvencao.getNaturezaDividaSubvencao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Natureza deve ser preenchida.");
        }
        ve.lancarException();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/subvencao-parametro/";
    }

    public ConverterAutoComplete getConverterCMC() {
        if (converterCMC == null) {
            converterCMC = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return converterCMC;
    }

    public Converter getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, dividaFacade);
        }
        return converterDivida;
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public CadastroEconomicoSubvencao getCadastroEconomicoSubvencao() {
        return cadastroEconomicoSubvencao;
    }

    public void setCadastroEconomicoSubvencao(CadastroEconomicoSubvencao cadastroEconomicoSubvencao) {
        this.cadastroEconomicoSubvencao = cadastroEconomicoSubvencao;
    }

    public DividaSubvencao getDividaSubvencao() {
        return dividaSubvencao;
    }

    public void setDividaSubvencao(DividaSubvencao dividaSubvencao) {
        this.dividaSubvencao = dividaSubvencao;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return subvencaoParametroFacade;
    }

    public List<CadastroEconomico> completarCMC(String parte) {
        return cadastroEconomicoFacade.listaCadastroEconomicoPessoaJuridica(parte.trim().toLowerCase());
    }

    public List<Divida> completaDivida(String parte) {
        return dividaFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        cadastroEconomicoSubvencao.setCadastroEconomico((CadastroEconomico) obj);
    }

    public void atribuirEmpresaCredora(CadastroEconomicoSubvencao cmcSubvencao) {
        empresaCredora = cmcSubvencao;
    }

    public void adicionarEmpresaDevedora() {
        try {
            validarAdicionarEmpresaDevedora();
            empresaDevedoraSubvencao.setEmpresaCredora(empresaCredora);
            empresaCredora.getEmpresaDevedoraSubvencao().add(empresaDevedoraSubvencao);
            empresaDevedoraSubvencao = new EmpresaDevedoraSubvencao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerEmpresaDevedora(EmpresaDevedoraSubvencao empresa) {
        empresaCredora.getEmpresaDevedoraSubvencao().remove(empresa);
    }

    public void editarEmpresaDevedora(EmpresaDevedoraSubvencao empresa) {
        empresaDevedoraSubvencao = (EmpresaDevedoraSubvencao) Util.clonarObjeto(empresa);
        empresaCredora.getEmpresaDevedoraSubvencao().remove(empresa);
    }


    public void validarAdicionarEmpresaDevedora() {
        ValidacaoException ve = new ValidacaoException();
        if (empresaDevedoraSubvencao.getCadastroEconomico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empresa Devedora deve ser informado.");
        }
        if (empresaDevedoraSubvencao.getOrdem() == null || empresaDevedoraSubvencao.getOrdem() < 1) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ordem deve ser informado e deve ser maior que zero.");
        }

        if (empresaDevedoraSubvencao.getCadastroEconomico() != null && empresaDevedoraSubvencao.getOrdem() != null) {
            for (EmpresaDevedoraSubvencao devedoraSubvencao : empresaCredora.getEmpresaDevedoraSubvencao()) {
                if (devedoraSubvencao.getCadastroEconomico().getId().equals(empresaDevedoraSubvencao.getCadastroEconomico().getId())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Essa empresa já foi adicionada.");
                    break;
                }
                if (devedoraSubvencao.getOrdem().equals(empresaDevedoraSubvencao.getOrdem())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A ordem de número " + devedoraSubvencao.getOrdem() + " já foi informada");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void limparEmpresaDevedora() {
        empresaDevedoraSubvencao = new EmpresaDevedoraSubvencao();
    }

    public List<SelectItem> getNaturezaDividaSubvencao() {
        return Util.getListSelectItem(NaturezaDividaSubvencao.values());
    }

    public List<SelectItem> getTipoOrdenacaoSubvencao() {
        return Util.getListSelectItem(TipoOrdenacaoSubvencao.values());
    }

    public void adicionarOrdenacaoParcela() {
        try {
            validarOrdenacaoParcela();
            ordenacaoParcelaSubvencao.setSubvencaoParametro(selecionado);
            selecionado.getItemOrdanacaoParcelaSubvencao().add(ordenacaoParcelaSubvencao);
            ordenacaoParcelaSubvencao = new OrdenacaoParcelaSubvencao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarOrdenacaoParcela(OrdenacaoParcelaSubvencao ordenacao) {
        selecionado.getItemOrdanacaoParcelaSubvencao().remove(ordenacao);
        ordenacaoParcelaSubvencao = ordenacao;
    }

    public void removerOrdenacaoParcela(OrdenacaoParcelaSubvencao ordenacao) {
        selecionado.getItemOrdanacaoParcelaSubvencao().remove(ordenacao);
    }

    private void validarOrdenacaoParcela() {
        ValidacaoException ve = new ValidacaoException();
        if (ordenacaoParcelaSubvencao.getOrdem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Ordem deve ser informada.");
        }
        if (ordenacaoParcelaSubvencao.getTipoOrdenacaoSubvencao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Ordenação deve ser informado.");
        }
        for (OrdenacaoParcelaSubvencao ordenacao : selecionado.getItemOrdanacaoParcelaSubvencao()) {
            if (ordenacaoParcelaSubvencao.getOrdem() != null && (Objects.equals(ordenacaoParcelaSubvencao.getOrdem(), ordenacao.getOrdem()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma Ordenação de número " + ordenacao.getOrdem());
            }
            if (Objects.equals(ordenacaoParcelaSubvencao.getTipoOrdenacaoSubvencao(), ordenacao.getTipoOrdenacaoSubvencao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O TIpo de Ordenação " + ordenacao.getTipoOrdenacaoSubvencao().getDescricao() + " já foi adicionado.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTipoDoctoOficialTermo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDoctoOficial object : tipoDoctoOficialFacade.tipoDoctoPorModulo("", ModuloTipoDoctoOficial.SUBVENCAO)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }
}
