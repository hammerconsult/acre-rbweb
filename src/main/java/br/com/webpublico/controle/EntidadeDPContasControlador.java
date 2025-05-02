/*
 * Codigo gerado automaticamente em Fri Sep 02 14:51:15 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-entidade-prestacao-contas", pattern = "/entidade-prestacao-contas/novo/", viewId = "/faces/rh/administracaodepagamento/entidadedpcontas/edita.xhtml"),
    @URLMapping(id = "novo-copia-entidade-prestacao-contas", pattern = "/entidade-prestacao-contas/novo/copiar/#{entidadeDPContasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/entidadedpcontas/edita.xhtml"),
    @URLMapping(id = "editar-entidade-prestacao-contas", pattern = "/entidade-prestacao-contas/editar/#{entidadeDPContasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/entidadedpcontas/edita.xhtml"),
    @URLMapping(id = "ver-entidade-prestacao-contas", pattern = "/entidade-prestacao-contas/ver/#{entidadeDPContasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/entidadedpcontas/visualizar.xhtml"),
    @URLMapping(id = "listar-entidade-prestacao-contas", pattern = "/entidade-prestacao-contas/listar/", viewId = "/faces/rh/administracaodepagamento/entidadedpcontas/lista.xhtml")
})
public class EntidadeDPContasControlador extends PrettyControlador<EntidadeDPContas> implements Serializable, CRUD {

    @EJB
    private EntidadeDPContasFacade entidadeDPContasFacade;
    @EJB
    private DeclaracaoPrestacaoContasFacade declaracaoPrestacaoContasFacade;
    private ItemEntidadeDPContas itemEntidadeDPContas;
    private ConverterAutoComplete converterDeclaracaoPrestacaoContas;
    private ConverterAutoComplete converterEntidade;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalFiltro;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais = Lists.newArrayList();
    private OpcaoFiltroHierarquiasOrganizacionais opcaoFiltroHierarquiasOrganizacionais;

    public EntidadeDPContasControlador() {
        super(EntidadeDPContas.class);
    }

    public EntidadeDPContasFacade getFacade() {
        return entidadeDPContasFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return entidadeDPContasFacade;
    }

    public ItemEntidadeDPContas getItemEntidadeDPContas() {
        return itemEntidadeDPContas;
    }

    public void setItemEntidadeDPContas(ItemEntidadeDPContas itemEntidadeDPContas) {
        this.itemEntidadeDPContas = itemEntidadeDPContas;
    }

    @URLAction(mappingId = "novo-entidade-prestacao-contas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(Util.criaDataPrimeiroDiaDoMesFP(00, Integer.parseInt(Util.getAnoDaData(UtilRH.getDataOperacao()))));
        selecionado.setFinalVigencia(Util.getUltimoDiaDoAno(Integer.parseInt(Util.getAnoDaData(UtilRH.getDataOperacao()))));
    }

    public void adicionarEntidade() {
        try {
            validarEntidade();
            itemEntidadeDPContas.setEntidadeDPContas(selecionado);
            itemEntidadeDPContas.limparItensEntidadeUnidadeOrganizacional();
            for (HierarquiaOrganizacional ho : getHierarquiasMarcadas()) {
                ItemEntidadeDPContasUnidadeOrganizacional ieuo = new ItemEntidadeDPContasUnidadeOrganizacional();
                ieuo.setItemEntidadeDPContas(itemEntidadeDPContas);
                ieuo.setHierarquiaOrganizacional(ho);
                itemEntidadeDPContas.setItemEntidadeUnidadeOrganizacional(Util.adicionarObjetoEmLista(itemEntidadeDPContas.getItemEntidadeUnidadeOrganizacional(), ieuo));
            }
            selecionado.setItensEntidaDPContas(Util.adicionarObjetoEmLista(selecionado.getItensEntidaDPContas(), itemEntidadeDPContas));
            Util.ordenarListas(itemEntidadeDPContas.getItemEntidadeUnidadeOrganizacional(), selecionado.getItensEntidaDPContas());
            cancelarEntidade();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<HierarquiaOrganizacional> getHierarquiasMarcadas() {
        List<HierarquiaOrganizacional> hierarquiasMarcadas = Lists.newArrayList();
        for (HierarquiaOrganizacional ho : hierarquiasOrganizacionais) {
            if (ho.isSelecionado()) {
                hierarquiasMarcadas.add(ho);
            }
        }
        return hierarquiasMarcadas;
    }

    public void cancelarEntidade() {
        itemEntidadeDPContas = null;
        hierarquiaOrganizacionalFiltro = null;
    }

    public void removerItem(ItemEntidadeDPContas itemEntidade) {
        selecionado.getItensEntidaDPContas().remove(itemEntidade);
    }

    public void alterarItem(ItemEntidadeDPContas itemEntidade) {
        itemEntidadeDPContas = itemEntidade;

        recuperarOpcaoFiltro();
        carregarHierarquiasOrganizacionais();
        marcarHierarquiasDaEntidade();

        FacesUtil.atualizarComponente("Formulario");
    }

    public void marcarHierarquiasDaEntidade() {
        for (HierarquiaOrganizacional ho : hierarquiasOrganizacionais) {
            for (ItemEntidadeDPContasUnidadeOrganizacional itemEntidadeUnidadeOrganizacional : itemEntidadeDPContas.getItemEntidadeUnidadeOrganizacional()) {
                if (ho.equals(itemEntidadeUnidadeOrganizacional.getHierarquiaOrganizacional())) {
                    ho.setSelecionado(true);
                }
            }
        }
    }

    public void recuperarOpcaoFiltro() {
        if (itemEntidadeDPContas.temHierarquiaFilhaAdicionada()) {
            opcaoFiltroHierarquiasOrganizacionais = OpcaoFiltroHierarquiasOrganizacionais.COMPLETA;
        } else {
            opcaoFiltroHierarquiasOrganizacionais = OpcaoFiltroHierarquiasOrganizacionais.SOMENTE_ORGAO;
        }
    }

    public ConverterAutoComplete getConverterEntidade() {
        if (converterEntidade == null) {
            converterEntidade = new ConverterAutoComplete(Entidade.class, entidadeFacade);
        }
        return converterEntidade;
    }

    public ConverterAutoComplete getConverterDeclaracaoPrestacaoContas() {
        if (converterDeclaracaoPrestacaoContas == null) {
            converterDeclaracaoPrestacaoContas = new ConverterAutoComplete(DeclaracaoPrestacaoContas.class, declaracaoPrestacaoContasFacade);
        }
        return converterDeclaracaoPrestacaoContas;
    }


    public List<DeclaracaoPrestacaoContas> buscarDeclaracaoPrestacaoContas(String parte) {
        return declaracaoPrestacaoContasFacade.buscarDeclaracaoPrestacaoDeContas(parte.trim());
    }

    public List<Entidade> completaEntidade(String parte) {
        return entidadeFacade.listaEntidades(parte.trim());
    }

    public void validarEntidade() {
        ValidacaoException ve = new ValidacaoException();

        if (itemEntidadeDPContas.getEntidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo entidade é obrigatório.");
            ve.lancarException();
        }
        if (todasHierarquiasDesmarcadas()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar (marcar) ao menos um orgão no painel abaixo.");
            ve.lancarException();
        }
        for (ItemEntidadeDPContas i : selecionado.getItensEntidaDPContas()) {
            if (i.getEntidade().equals(itemEntidadeDPContas.getEntidade()) && !i.equals(itemEntidadeDPContas)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A entidade <b>'" + i.getEntidade() + "'</b> já foi adicionada. Não é permitido adicionar a mesma entidade mais de uma vez.");
                ve.lancarException();
            }
        }
        for (HierarquiaOrganizacional hierarquiaMarcada : getHierarquiasMarcadas()) {
            for (ItemEntidadeDPContas itemEntidaDPConta : selecionado.getItensEntidaDPContas()) {
                for (ItemEntidadeDPContasUnidadeOrganizacional itemEntUn : itemEntidaDPConta.getItemEntidadeUnidadeOrganizacional()) {
                    if (hierarquiaMarcada.equals(itemEntUn.getHierarquiaOrganizacional()) && !itemEntidadeDPContas.getEntidade().equals(itemEntidaDPConta.getEntidade())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Hierarquia " + hierarquiaMarcada.toString() + " já associada à Entidade " + itemEntidaDPConta.getEntidade().getNome() + " da Declaração/Prestação de Contas.");
                        ve.lancarException();
                    }
                }
            }
        }
    }

    @URLAction(mappingId = "editar-entidade-prestacao-contas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        opcaoFiltroHierarquiasOrganizacionais = OpcaoFiltroHierarquiasOrganizacionais.SOMENTE_ORGAO;
    }

    @URLAction(mappingId = "ver-entidade-prestacao-contas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/entidade-prestacao-contas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarEntidadeDPContas();
            super.salvar();
        } catch (ValidacaoException ex) {
            if (ex.temMensagens()) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
            } else {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }

    private void validarEntidadeDPContas() {
        Util.validarCampos(selecionado);
        validarHierarquiaOrganizacionalMarcadaAndAdicionada();
        if (selecionado.getInicioVigencia().compareTo(selecionado.getFinalVigencia()) > 0) {
            throw new ValidacaoException("A data de início de vigência deve ser anterior a data de final de vigência.");
        }

        EntidadeDPContas ei = entidadeDPContasFacade.recuperarEntidadeDPContasVigenteParaCategoria(selecionado.getDeclaracaoPrestacaoContas().getCategoriaDeclaracaoPrestacaoContas(), selecionado.getInicioVigencia());
        if (ei != null && !ei.equals(selecionado)) {
            throw new ValidacaoException("Já existe uma configuração de declarações vigente cadastrada para o tipo <b>" + selecionado.getDeclaracaoPrestacaoContas().getCategoriaDeclaracaoPrestacaoContas().getDescricao() + "</b>.");
        }
    }

    private void validarHierarquiaOrganizacionalMarcadaAndAdicionada() {
        if (itemEntidadeDPContas != null && !todasHierarquiasDesmarcadas()) {
            throw new ValidacaoException("Clique no botão Adicionar para adicionar o(s) órgão(s) marcado(s) à entidade selecionada.");
        }
    }

    public void novoItemEntidadeDPContas() {
        if (selecionado.getInicioVigencia() == null || selecionado.getFinalVigencia() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Os campos início de vigência e final de vigência são obrigatórios.");
            return;
        }
        itemEntidadeDPContas = new ItemEntidadeDPContas();
        opcaoFiltroHierarquiasOrganizacionais = OpcaoFiltroHierarquiasOrganizacionais.SOMENTE_ORGAO;
        carregarHierarquiasOrganizacionais();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasVigentesNoPeriodoNoNivel() {
        return hierarquiaOrganizacionalFacade.getHierarquiasVigentesNoPeriodoNoNivel(selecionado.getInicioVigencia(), selecionado.getFinalVigencia(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, 2);
    }

    public void novaCopiaSelecionado() {
        FacesUtil.redirecionamentoInterno("/entidade-prestacao-contas/novo/copiar/" + selecionado.getId() + "/");
    }


    @URLAction(mappingId = "novo-copia-entidade-prestacao-contas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void copiarSelecionado() {
        EntidadeDPContas recuperado = (EntidadeDPContas) getFacede().recuperar(getId());
        novo();
        selecionado.setInicioVigencia(recuperado.getInicioVigencia());
        selecionado.setFinalVigencia(recuperado.getFinalVigencia());
        if (isSessao()) {
            return;
        }
        for (ItemEntidadeDPContas iecAntigo : recuperado.getItensEntidaDPContas()) {
            ItemEntidadeDPContas iecNovo = new ItemEntidadeDPContas();
            iecNovo.setEntidade(iecAntigo.getEntidade());
            iecNovo.setEntidadeDPContas(selecionado);
            iecNovo.setItemEntidadeUnidadeOrganizacional(new ArrayList<ItemEntidadeDPContasUnidadeOrganizacional>());
            for (ItemEntidadeDPContasUnidadeOrganizacional ieuoAntigo : iecAntigo.getItemEntidadeUnidadeOrganizacional()) {
                ItemEntidadeDPContasUnidadeOrganizacional ieuoNovo = new ItemEntidadeDPContasUnidadeOrganizacional();
                ieuoNovo.setItemEntidadeDPContas(iecNovo);
                ieuoNovo.setHierarquiaOrganizacional(ieuoAntigo.getHierarquiaOrganizacional());
                iecNovo.getItemEntidadeUnidadeOrganizacional().add(ieuoNovo);
            }
            selecionado.getItensEntidaDPContas().add(iecNovo);
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalFiltro() {
        return hierarquiaOrganizacionalFiltro;
    }

    public void setHierarquiaOrganizacionalFiltro(HierarquiaOrganizacional hierarquiaOrganizacionalFiltro) {
        this.hierarquiaOrganizacionalFiltro = hierarquiaOrganizacionalFiltro;
    }

    public OpcaoFiltroHierarquiasOrganizacionais getOpcaoFiltroHierarquiasOrganizacionais() {
        return opcaoFiltroHierarquiasOrganizacionais;
    }

    public void setOpcaoFiltroHierarquiasOrganizacionais(OpcaoFiltroHierarquiasOrganizacionais opcaoFiltroHierarquiasOrganizacionais) {
        this.opcaoFiltroHierarquiasOrganizacionais = opcaoFiltroHierarquiasOrganizacionais;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrganizacionais(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiasFiltrandoVigentesPorPeriodoAndTipo(parte.trim(), selecionado.getInicioVigencia(), selecionado.getFinalVigencia(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public ConverterAutoComplete getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public void filtrarHierarquias() {
        List<HierarquiaOrganizacional> backupHierarquiasMarcadas = getHierarquiasMarcadas();

        hierarquiasOrganizacionais.clear();
        hierarquiasOrganizacionais = buscarHierarquiasOrganizacionais(hierarquiaOrganizacionalFiltro.getCodigoSemZerosFinais());

        if (isEditandoEntidade()) {
            for (HierarquiaOrganizacional ho : backupHierarquiasMarcadas) {
                hierarquiasOrganizacionais = Util.adicionarObjetoEmLista(hierarquiasOrganizacionais, ho);
            }
        }
    }

    public void anularHierarquiaOrganizacionalFiltroAndCarregarHierarquiasNovamente() {
        setHierarquiaOrganizacionalFiltro(null);
        carregarHierarquiasOrganizacionais();
    }

    public void carregarHierarquiasOrganizacionais() {
        hierarquiasOrganizacionais.clear();
        if (isOpcaoSomenteOrgao()) {
            hierarquiasOrganizacionais = buscarHierarquiasVigentesNoPeriodoNoNivel();
        } else {
            hierarquiasOrganizacionais = buscarHierarquiasOrganizacionais("");
        }
    }

    public String getDescricaoHierarquia(HierarquiaOrganizacional ho) {
        try {
            String descricao = ho.toString();
            Integer indiceDoNo = ho.getIndiceDoNo();
            if (indiceDoNo > 2) {
                StringBuilder sb = new StringBuilder();
                String espaco = "   ";
                for (int i = 0; i < indiceDoNo - 2; i++) {
                    sb.append(espaco).append(espaco).append(espaco);
                }
                sb.append(descricao);
                return sb.toString();
            }
            return descricao;
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public boolean isEditandoEntidade() {
        return selecionado.getItensEntidaDPContas().contains(itemEntidadeDPContas);
    }

    public boolean isOpcaoSomenteOrgao() {
        return OpcaoFiltroHierarquiasOrganizacionais.SOMENTE_ORGAO.equals(opcaoFiltroHierarquiasOrganizacionais);
    }

    public boolean todasHierarquiasMarcadas() {
        for (HierarquiaOrganizacional ho : hierarquiasOrganizacionais) {
            if (!ho.isSelecionado()) {
                return false;
            }
        }
        return true;
    }

    public boolean todasHierarquiasDesmarcadas() {
        for (HierarquiaOrganizacional ho : hierarquiasOrganizacionais) {
            if (ho.isSelecionado()) {
                return false;
            }
        }
        return true;
    }

    public void marcarTodasHierarquias() {
        for (HierarquiaOrganizacional ho : hierarquiasOrganizacionais) {
            ho.setSelecionado(true);
        }
    }

    public void desmarcarTodasHierarquias() {
        for (HierarquiaOrganizacional ho : hierarquiasOrganizacionais) {
            ho.setSelecionado(false);
        }
    }

    public enum OpcaoFiltroHierarquiasOrganizacionais {
        SOMENTE_ORGAO,
        COMPLETA;

        OpcaoFiltroHierarquiasOrganizacionais() {
        }
    }
}
