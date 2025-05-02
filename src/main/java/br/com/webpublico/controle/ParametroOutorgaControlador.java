package br.com.webpublico.controle;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 26/08/14
 * Time: 08:43
 * To change this template use File | Settings | File Templates.
 */


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ValidadorDatasEMesesOutorga;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "parametroOutorgaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroOutorga", pattern = "/rbtrans/parametrotransito/outorga/novo/", viewId = "/faces/tributario/rbtrans/parametros/outorga/edita.xhtml"),
    @URLMapping(id = "editarParametroOutorga", pattern = "/rbtrans/parametrotransito/outorga/editar/#{parametroOutorgaControlador.id}/", viewId = "/faces/tributario/rbtrans/parametros/outorga/edita.xhtml"),
    @URLMapping(id = "listarParametroOutorga", pattern = "/rbtrans/parametrotransito/outorga/listar/", viewId = "/faces/tributario/rbtrans/parametros/outorga/lista.xhtml"),
    @URLMapping(id = "verParametroOutorga", pattern = "/rbtrans/parametrotransito/outorga/ver/#{parametroOutorgaControlador.id}/", viewId = "/faces/tributario/rbtrans/parametros/outorga/visualizar.xhtml")
})
public class ParametroOutorgaControlador extends PrettyControlador<ParametrosOutorgaRBTrans> implements Serializable, CRUD {

    @EJB
    private ParametroOutorgaFacade parametroOutorgaFacade;
    @EJB
    private SubvencaoProcessoFacade subvencaoProcessoFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    private ConverterAutoComplete converterDivida;
    private ConverterAutoComplete converterTributo;
    private ConverterAutoComplete converterIndice;
    private ConverterAutoComplete converterTipoDocumento;
    private ParametroOutorgaSubvencao parametroOutorgaSubvencao;

    public ParametroOutorgaControlador() {
        super(ParametrosOutorgaRBTrans.class);
    }

    public ParametroOutorgaSubvencao getParametroOutorgaSubvencao() {
        return parametroOutorgaSubvencao;
    }

    public void setParametroOutorgaSubvencao(ParametroOutorgaSubvencao parametroOutorgaSubvencao) {
        this.parametroOutorgaSubvencao = parametroOutorgaSubvencao;
    }

    public List<Divida> completaDivida(String parte) {
        return parametroOutorgaFacade.getDividaFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<Tributo> completarTributos(String parte) {
        return parametroOutorgaFacade.getTributoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<TipoDoctoOficial> completaTipoDocumento(String parte) {
        return tipoDoctoOficialFacade.recuperaTiposDoctoOficialPorModulo(ModuloTipoDoctoOficial.BLOQUEIO_OUTORGA, parte);
    }

    public ConverterAutoComplete getConverterTipoDocumento() {
        if (converterTipoDocumento == null) {
            converterTipoDocumento = new ConverterAutoComplete(TipoDoctoOficial.class, tipoDoctoOficialFacade);
        }
        return converterTipoDocumento;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, parametroOutorgaFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public Converter getConverterIndice() {
        if (converterIndice == null) {
            converterIndice = new ConverterAutoComplete(IndiceEconomico.class, parametroOutorgaFacade.getIndiceFacade());
        }
        return converterIndice;
    }

    public ConverterAutoComplete getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, parametroOutorgaFacade.getTributoFacade());
        }
        return converterTributo;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    @URLAction(mappingId = "novoParametroOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setUsuarioCadastrou(getSistemaControlador().getUsuarioCorrente());
        selecionado.setCadastradoEm(getSistemaControlador().getDataOperacao());
        parametroOutorgaSubvencao = new ParametroOutorgaSubvencao();
    }

    @Override
    @URLAction(mappingId = "editarParametroOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        parametroOutorgaSubvencao = new ParametroOutorgaSubvencao();
        selecionado.setUsuarioAlterou(getSistemaControlador().getUsuarioCorrente());
        selecionado.setAtualizadoEm(new Date());
        Collections.sort(selecionado.getParametroOutorgaSubvencao());
    }

    @Override
    @URLAction(mappingId = "verParametroOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        Collections.sort(selecionado.getParametroOutorgaSubvencao());
    }

    public List<SelectItem> getIndices() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (IndiceEconomico object : parametroOutorgaFacade.getIndiceFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public void excluir() {
        boolean podeExcluirParametroOutorga = true;
        for (ParametroOutorgaSubvencao parametro : selecionado.getParametroOutorgaSubvencao()) {
            if (hasSubvencaoLancada(parametro)) {
                FacesUtil.addOperacaoNaoPermitida("Não é possível remover o Parâmetro de Outorga do RBTRANS, existe Processo de Subvenção lançado com dados do parâmetro.");
                podeExcluirParametroOutorga = false;
                break;
            }
        }
        if (podeExcluirParametroOutorga) {
            super.excluir();
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroOutorgaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rbtrans/parametrotransito/outorga/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDivida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dívida é obrigatório.");
        }
        if (selecionado.getTributo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tributo é obrigatório.");
        }

        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício de Refêrencia é obrigatório.");
        }
        if (selecionado.getDiaVencimentoPrimeiraParcela() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Primeira Parcela é obrigatório.");
        }
        if (selecionado.getTipoMesVencimento() == null){
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo Mês do Vencimento é obrigatório.");
        }
        if (selecionado.getExercicio() != null && selecionado.getId() == null) {
            if (parametroOutorgaFacade.hasParametroOutorgaNoExercicio(selecionado.getExercicio())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe Parâmetro de Outorga cadastrado para o exercício de <b>" + selecionado.getExercicio().getAno() + "</b>");

            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getMes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Mes mes : Mes.values()) {
            retorno.add(new SelectItem(mes, mes.getDescricao()));
        }
        return retorno;
    }

    public void adicionarParametroSubvencao() {
        try {
            validaParametroSubvencao();
            Date dataAtual = new Date();
            if (parametroOutorgaSubvencao.getUsuarioCadastro() == null) {
                parametroOutorgaSubvencao.setUsuarioCadastro(getSistemaControlador().getUsuarioCorrente());
                parametroOutorgaSubvencao.setDataCadastro(dataAtual);
            }
            if (parametroOutorgaSubvencao.getId() != null) {
                parametroOutorgaSubvencao.setUsuarioAtualizacao(getSistemaControlador().getUsuarioCorrente());
                parametroOutorgaSubvencao.setDataAtualizacao(dataAtual);
            }
            parametroOutorgaSubvencao.setParametroOutorga(selecionado);
            selecionado.getParametroOutorgaSubvencao().add(parametroOutorgaSubvencao);
            Collections.sort(selecionado.getParametroOutorgaSubvencao());
            parametroOutorgaSubvencao = new ParametroOutorgaSubvencao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void excluirParametroSubvencao(ParametroOutorgaSubvencao parametro) {
        selecionado.getParametroOutorgaSubvencao().remove(parametro);
    }

    public void alterarParametroSubvencao(ParametroOutorgaSubvencao parametro) {
        selecionado.getParametroOutorgaSubvencao().remove(parametro);
        this.parametroOutorgaSubvencao = parametro;
    }

    private void validaParametroSubvencao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExercicio() != null && selecionado.getId() == null) {
            if (parametroOutorgaFacade.hasParametroOutorgaNoExercicio(selecionado.getExercicio())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe Parâmetro de Outorga cadastrado para o exercício de <b>" + selecionado.getExercicio().getAno() + "</b>");
            }
        }
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Para continuar é necessário informar o exercício do Parâmetro de Outorga.");
        }
        if (parametroOutorgaSubvencao.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial é Obrigatório");
        }
        if (parametroOutorgaSubvencao.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final é Obrigatório");
        }
        if (parametroOutorgaSubvencao.getTipoPassageiro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Passageiro é Obrigatório.");
        }
        if (parametroOutorgaSubvencao.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês de Referência é Obrigatório.");
        }
        if (parametroOutorgaSubvencao.getQtdeAlunosTransportados() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Quantidade Total de Alunos Transportados é Obrigatório.");
        }
        if (parametroOutorgaSubvencao.getPercentualSubvencao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Percentual de Subvenção é Obrigatório.");
        }
        if (parametroOutorgaSubvencao.getValorPassagem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Valor da Passagem por Aluno (R$) é Obrigatório.");
        }

        ValidadorDatasEMesesOutorga validador = new ValidadorDatasEMesesOutorga(parametroOutorgaSubvencao.getDataInicial(), parametroOutorgaSubvencao.getDataFinal(), parametroOutorgaSubvencao.getMes());
        validador.validarDatasAndMes();

        if (parametroOutorgaSubvencao.getTipoPassageiro() != null && parametroOutorgaSubvencao.getMes() != null) {
            for (ParametroOutorgaSubvencao parametro : selecionado.getParametroOutorgaSubvencao()) {
                if (parametroOutorgaSubvencao.getTipoPassageiro().equals(parametro.getTipoPassageiro()) && parametroOutorgaSubvencao.getMes().equals(parametro.getMes())) {
                    if (parametro.getDataInicial() != null && parametro.getDataFinal() != null) {
                        validador = new ValidadorDatasEMesesOutorga(parametroOutorgaSubvencao.getDataInicial(), parametroOutorgaSubvencao.getDataFinal(), parametro.getDataInicial(), parametro.getDataFinal());
                        validador.validarPeriodosVigentes();
                    }
                }
            }
        }
        ve.lancarException();
    }

    public boolean hasSubvencaoLancada(ParametroOutorgaSubvencao subvencao) {
        return subvencaoProcessoFacade.hasLancamentoValidoParaACompetencia(subvencao.getMes(), selecionado.getExercicio().getId(), subvencao.getTipoPassageiro(), subvencao.getDataInicial(), subvencao.getDataFinal());
    }

    public List<SelectItem> getTipoPassageiro() {
        return Util.getListSelectItem(Lists.newArrayList(TipoPassageiro.values()));
    }

    public List<SelectItem> getTipoMesVencimento(){
        return  Util.getListSelectItem(Lists.newArrayList(TipoMesVencimento.values()));
    }

    public boolean isDesabilitarCampoEdicaoRegistro() {
        return Operacoes.EDITAR.equals(operacao);
    }

}
