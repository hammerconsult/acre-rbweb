package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AgrupadorGrupoLevantamentoEstoque;
import br.com.webpublico.enums.NaturezaTipoGrupoMaterial;
import br.com.webpublico.enums.SituacaoEntradaMaterial;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.AgrupadorLevantamentoEstoque;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EfetivacaoLevantamentoEstoqueFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Desenvolvimento on 31/01/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEfetivacaoLevantamentoEstoque", pattern = "/efetivacao-levantamento-estoque/novo/", viewId = "/faces/administrativo/materiais/efetivacao-levantamento-estoque/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoLevantamentoEstoque", pattern = "/efetivacao-levantamento-estoque/editar/#{efetivacaoLevantamentoEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/efetivacao-levantamento-estoque/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoLevantamentoEstoque", pattern = "/efetivacao-levantamento-estoque/listar/", viewId = "/faces/administrativo/materiais/efetivacao-levantamento-estoque/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoLevantamentoEstoque", pattern = "/efetivacao-levantamento-estoque/ver/#{efetivacaoLevantamentoEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/efetivacao-levantamento-estoque/visualizar.xhtml")
})
public class EfetivacaoLevantamentoEstoqueControlador extends PrettyControlador<EfetivacaoLevantamentoEstoque> implements Serializable, CRUD {

    @EJB
    private EfetivacaoLevantamentoEstoqueFacade facade;
    private List<LevantamentoEstoque> levantamentoEstoquesRecuperados;
    private List<AgrupadorGrupoLevantamentoEstoque> agrupamentos;
    private List<BensEstoque> bensEstoque;

    public EfetivacaoLevantamentoEstoqueControlador() {
        super(EfetivacaoLevantamentoEstoque.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoEfetivacaoLevantamentoEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributos();
    }

    private void inicializarAtributos() {
        selecionado.setDataEntrada(facade.getSistemaFacade().getDataOperacao());
        selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica());
        levantamentoEstoquesRecuperados = new ArrayList<>();
        agrupamentos = new ArrayList<>();
        bensEstoque = new ArrayList<>();
    }

    @URLAction(mappingId = "verEfetivacaoLevantamentoEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarEfetivacaoLevantamentoEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-levantamento-estoque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarRegrasSalvar();
            selecionado.criarItemEfetivacaoLevantamento(levantamentoEstoquesRecuperados, agrupamentos);
            selecionado = facade.salvarEfetivacao(selecionado, bensEstoque);
            redirecionarParaVerOrEditar(selecionado.getId(), "ver");
            FacesUtil.addOperacaoRealizada("Efetivação realizada co sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica | OperacaoEstoqueException ex) {
            logger.error("Erro ao salvar a efetivação do levantamento estoque {}", ex.getMessage());
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao salvar a efetivação do levantamento estoque {}", e.getMessage());
            logger.debug("Erro ao salvar a efetivação do levantamento estoque", e);
            descobrirETratarException(e);
        }
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorMaterial(parte, facade.getSistemaFacade().getDataOperacao(), facade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<LevantamentoEstoque> getLevantamentoEstoquesRecuperados() {
        Collections.sort(levantamentoEstoquesRecuperados);
        return levantamentoEstoquesRecuperados;
    }

    public void setLevantamentoEstoquesRecuperados(List<LevantamentoEstoque> levantamentoEstoquesRecuperados) {
        this.levantamentoEstoquesRecuperados = levantamentoEstoquesRecuperados;
    }

    public List<AgrupadorGrupoLevantamentoEstoque> getAgrupamentos() {
        Collections.sort(agrupamentos);
        return agrupamentos;
    }

    public void setAgrupamentos(List<AgrupadorGrupoLevantamentoEstoque> agrupamentos) {
        this.agrupamentos = agrupamentos;
    }

    public void buscarLevantamentos() {
        try {
            validarUnidade();
            levantamentoEstoquesRecuperados = facade.getLevantamentoEstoqueFacade().buscarLevantamentoPorUnidadeOrcamentaria(selecionado.getHierarquiaOrcamentaria());
            if (levantamentoEstoquesRecuperados == null || levantamentoEstoquesRecuperados.isEmpty()) {
                FacesUtil.addOperacaoNaoRealizada("A unidade " + selecionado.getHierarquiaOrcamentaria() + " não possui levantamento de estoque com a situação <b>Finalizado</b> para ser efetivado.");
            } else {
                List<AgrupadorLevantamentoEstoque> itensLevantamentos = retornarTodosOsAgrupadorLevantamentoEstoque(levantamentoEstoquesRecuperados);
                itensLevantamentos.addAll(adicionarItensDeGruposExternos());
                agrupamentos = agruparPorGrupo(itensLevantamentos);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void gerarBensEstoque() {
        try {
            validarUnidade();
            facade.gerarBensEstoqueContabil(agrupamentos, selecionado);
            preencherBensEstoqueContabil();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void preencherBensEstoqueContabil() {
        bensEstoque = Lists.newArrayList();
        for (AgrupadorGrupoLevantamentoEstoque agrup : agrupamentos) {
            if (agrup.hasBensEstoqueContabil()) {
                bensEstoque.add(agrup.getBensEstoque());
            }
        }
    }

    private void validarUnidade() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getHierarquiaOrcamentaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade orçamentária deve ser informado.");
        }
        ve.lancarException();
    }

    public List<AgrupadorLevantamentoEstoque> adicionarItensDeGruposExternos() {
        List<AgrupadorLevantamentoEstoque> listaDeGruposNaoExistemNosItensLevantamento = new ArrayList<>();
        for (final GrupoMaterial grupoMaterial : facade.getGrupoMaterialFacade().lista()) {
            boolean contem = false;
            for (AgrupadorLevantamentoEstoque item : retornarTodosOsAgrupadorLevantamentoEstoque(levantamentoEstoquesRecuperados)) {
                if (item.getGrupoMaterial().equals(grupoMaterial)) {
                    contem = true;
                    break;
                }
            }
            if (!contem) {
                AgrupadorLevantamentoEstoque novoGrupo = new AgrupadorLevantamentoEstoque() {
                    @Override
                    public GrupoMaterial getGrupoMaterial() {
                        return grupoMaterial;
                    }

                    @Override
                    public TipoEstoque getTipoEstoque() {
                        return TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO;
                    }

                    @Override
                    public BigDecimal getQuantidade() {
                        return BigDecimal.ZERO;
                    }

                    @Override
                    public BigDecimal getValorTotal() {
                        return BigDecimal.ZERO;
                    }

                    @Override
                    public Boolean getGrupoExterno() {
                        return Boolean.TRUE;
                    }
                };
                listaDeGruposNaoExistemNosItensLevantamento.add(novoGrupo);
            }
        }
        return listaDeGruposNaoExistemNosItensLevantamento;
    }

    public List<AgrupadorLevantamentoEstoque> retornarTodosOsAgrupadorLevantamentoEstoque(List<LevantamentoEstoque> levantamentos) {
        List<AgrupadorLevantamentoEstoque> itensLevantamentos = new ArrayList<>();
        for (LevantamentoEstoque levantamento : levantamentos) {
            itensLevantamentos.addAll(levantamento.getItensLevantamentoEstoque());
        }
        return itensLevantamentos;
    }

    public BigDecimal getValorTotalLevantamento() {
        BigDecimal total = BigDecimal.ZERO;
        for (AgrupadorGrupoLevantamentoEstoque agrupamento : agrupamentos) {
            total = total.add(agrupamento.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalQuantidade() {
        BigDecimal total = BigDecimal.ZERO;
        for (AgrupadorGrupoLevantamentoEstoque agrupamento : agrupamentos) {
            total = total.add(agrupamento.getQuantidade());
        }
        return total;
    }

    public BigDecimal getValorTotalSaldoGrupoMaterial() {
        BigDecimal total = BigDecimal.ZERO;
        for (AgrupadorGrupoLevantamentoEstoque agrupamento : agrupamentos) {
            total = total.add(agrupamento.getSaldoGrupoMaterial());
        }
        return total;
    }

    public BigDecimal getValorTotalBensEstoqueContabil() {
        BigDecimal total = BigDecimal.ZERO;
        for (AgrupadorGrupoLevantamentoEstoque agrupamento : agrupamentos) {
            total = total.add(agrupamento.getValorBensEstoqueContabil());
        }
        return total;
    }

    public BigDecimal getValorTotalBensEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        if (bensEstoque != null && !bensEstoque.isEmpty()) {
            for (BensEstoque bem : bensEstoque) {
                total = total.add(bem.getValor());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalEstoqueAtual() {
        BigDecimal total = BigDecimal.ZERO;
        for (AgrupadorGrupoLevantamentoEstoque agrupamento : agrupamentos) {
            total = total.add(agrupamento.getEstoqueAtual());
        }
        return total;
    }

    public BigDecimal getValorTotalDiferenca() {
        return getValorTotalSaldoGrupoMaterial().subtract(getValorTotalEstoqueAtual()).subtract(getValorTotalLevantamento());
    }

    public String getQuantidadeTotalFormatada() {
        return Util.formataQuandoDecimal(getValorTotalQuantidade(), TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
    }

    private List<AgrupadorGrupoLevantamentoEstoque> agruparPorGrupo(List<? extends AgrupadorLevantamentoEstoque> itensLevantamentos) {
        List<AgrupadorGrupoLevantamentoEstoque> retorno = new ArrayList<>();
        for (AgrupadorLevantamentoEstoque item : itensLevantamentos) {
            if (retorno.isEmpty()) {
                novoAgrupador(retorno, item);
            } else {
                boolean encontrou = false;
                for (AgrupadorGrupoLevantamentoEstoque agrupador : retorno) {
                    if (agrupador.equals(item)) {
                        agrupador.adicionarValor(item);
                        encontrou = true;
                        break;
                    }
                }
                if (!encontrou) {
                    novoAgrupador(retorno, item);
                }
            }
        }

        for (AgrupadorGrupoLevantamentoEstoque agrupador : retorno) {
            agrupador.setSaldoGrupoMaterial(getSaldoGrupoMaterialPorTipoEstoqueAndOrcamentaria(agrupador));
            agrupador.setEstoqueAtual(getSaldoEstoquePorGrupo(agrupador));
            agrupador.setContabilMaiorMateriais(agrupador.getDiferenca().compareTo(BigDecimal.ZERO) > 0);
        }
        return retorno;
    }

    private void novoAgrupador(List<AgrupadorGrupoLevantamentoEstoque> retorno, AgrupadorLevantamentoEstoque item) {
        AgrupadorGrupoLevantamentoEstoque novo = new AgrupadorGrupoLevantamentoEstoque(item);
        BigDecimal saldoGrupo = getSaldoGrupoMaterialPorTipoEstoqueAndOrcamentaria(novo);
        novo.setSaldoGrupoMaterial(saldoGrupo);
        BigDecimal saldoEstoque = getSaldoEstoquePorGrupo(novo);
        novo.setEstoqueAtual(saldoEstoque);
        if (novo.isValorLevantametnoMaiorQueZero() || novo.isQuantidadeMaiorQueZero() || novo.isSaldoGrupoMaterialMaiorQueZero() || novo.isValorEstoqueMaiorQueZero()) {
            retorno.add(novo);
        }
    }

    public BigDecimal getSaldoEstoquePorGrupo(AgrupadorGrupoLevantamentoEstoque agrupador) {
        try {
            return facade.getEstoqueFacade().buscarUltimoSaldoEstoquePorData(
                agrupador.getGrupoMaterial(),
                selecionado.getHierarquiaOrcamentaria().getSubordinada(),
                agrupador.getTipoEstoque(),
                facade.getSistemaFacade().getDataOperacao());
        } catch (NullPointerException ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSaldoGrupoMaterialPorTipoEstoqueAndOrcamentaria(AgrupadorGrupoLevantamentoEstoque agrupador) {
        MovimentoGrupoMaterial filtros = new MovimentoGrupoMaterial();
        filtros.setGrupoMaterial(agrupador.getGrupoMaterial());
        filtros.setNaturezaTipoGrupoMaterial(NaturezaTipoGrupoMaterial.ORIGINAL);
        filtros.setUnidadeOrganizacional(selecionado.getHierarquiaOrcamentaria().getSubordinada());
        filtros.setTipoEstoque(agrupador.getTipoEstoque());
        filtros.setDataMovimento(facade.getSistemaFacade().getDataOperacao());
        try {
            return facade.getSaldoGrupoMaterialFacade().buscarUltimoSaldoPorData(filtros).getValorSaldo();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
        return BigDecimal.ZERO;
    }

    public void validarRegrasSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getHierarquiaOrcamentaria() == null){
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade orçamentária deve ser informado.");
        }
        if (levantamentoEstoquesRecuperados == null || levantamentoEstoquesRecuperados.isEmpty()){
            ve.adicionarMensagemDeOperacaoNaoPermitida("A efetivação não possui levantamentos para ser efetivado.");
        }
        ve.lancarException();
        for (LevantamentoEstoque levantamento : levantamentoEstoquesRecuperados) {
            if (!levantamento.getHierarquiaOrcamentaria().equals(selecionado.getHierarquiaOrcamentaria())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade orçamentária não pode ser diferente da unidade orçamentária dos levantamento <b>" + levantamento.getHierarquiaOrcamentaria() + "<b/>");
            }
        }
        for (AgrupadorGrupoLevantamentoEstoque agrupamento : agrupamentos) {
            if (agrupamento.hasDiferenca()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Saldo incorreto para o grupo <b>" + agrupamento.getGrupoMaterial().toString().toUpperCase() + "</b> e tipo de estoque <b>" + agrupamento.getTipoEstoque().getDescricao().toUpperCase() + "</b> diferença: <b>" + Util.formataValor(agrupamento.getDiferenca()) + "</b>");
            }
        }
        ve.lancarException();
    }

    public List<BensEstoque> getBensEstoque() {
        return bensEstoque;
    }

    public void setBensEstoque(List<BensEstoque> bensEstoque) {
        this.bensEstoque = bensEstoque;
    }
}

