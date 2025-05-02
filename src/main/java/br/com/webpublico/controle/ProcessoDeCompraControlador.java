/*
 * Codigo gerado automaticamente em Wed Nov 16 15:51:50 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.GrupoContaDespesa;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProcessoDeCompraFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "processoDeCompraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-processo-compra", pattern = "/processo-compra/novo/", viewId = "/faces/administrativo/licitacao/processodecompra/edita.xhtml"),
    @URLMapping(id = "editar-processo-compra", pattern = "/processo-compra/editar/#{processoDeCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/processodecompra/edita.xhtml"),
    @URLMapping(id = "ver-processo-compra", pattern = "/processo-compra/ver/#{processoDeCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/processodecompra/visualizar.xhtml"),
    @URLMapping(id = "listar-processo-compra", pattern = "/processo-compra/listar/", viewId = "/faces/administrativo/licitacao/processodecompra/lista.xhtml")
})
public class ProcessoDeCompraControlador extends PrettyControlador<ProcessoDeCompra> implements Serializable, CRUD {

    @EJB
    private ProcessoDeCompraFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private TipoStatusSolicitacao statusAtualSolicitacao;
    private Boolean processoUtilizado;
    private FiltroHistoricoProcessoLicitatorio filtroAjusteProcesso;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    public ProcessoDeCompraControlador() {
        super(ProcessoDeCompra.class);
    }

    public ProcessoDeCompraFacade getFacade() {
        return facade;
    }

    @URLAction(mappingId = "novo-processo-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataProcesso(facade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @URLAction(mappingId = "ver-processo-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-processo-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        atribuirGrupoContaDespesaItemProcesso();
        setStatusAtualSolicitacao(facade.getSolicitacaoMaterialFacade().getStatusAtualDaSolicitacao(selecionado.getSolicitacaoMaterial()));
        atribuirHierarquiaDaUnidade();
        setProcessoUtilizado(verificarProcessoUtilizadoLicitacaoOrDispensa());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/processo-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarDataProcessoCompra();
            if (isOperacaoNovo()) {
                selecionado = facade.salvarRetornando(selecionado);
                FacesUtil.executaJavaScript("dlgRedirecionarLicOrDisp.show()");
            } else {
                selecionado = facade.salvarRetornando(selecionado);
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                redirecionarParaVerOrEditar(selecionado.getId(), "ver");
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        try {
            validarExclusaoProcesso();
            super.excluir();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public TipoMovimentoProcessoLicitatorio getTipoProcesso() {
        return TipoMovimentoProcessoLicitatorio.PROCESSO_COMPRA;
    }

    public List<SelectItem> itensTipoProcessoDeCompra() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        if (isVerificaUnidadeAdministrativaVinculada()) {
            retorno.add(new SelectItem(TipoProcessoDeCompra.LICITACAO));
        }
        retorno.add(new SelectItem(TipoProcessoDeCompra.CREDENCIAMENTO));
        retorno.add(new SelectItem(TipoProcessoDeCompra.DISPENSA_LICITACAO_INEXIGIBILIDADE));
        return retorno;
    }

    public void atribuirGrupoContaDespesa(ItemProcessoDeCompra item) {
        if (item != null && item.getObjetoCompra() != null) {
            GrupoContaDespesa grupoContaDespesa = facade.getObjetoCompraFacade().criarGrupoContaDespesa(item.getObjetoCompra().getTipoObjetoCompra(), item.getObjetoCompra().getGrupoObjetoCompra());
            item.getObjetoCompra().setGrupoContaDespesa(grupoContaDespesa);
        }
    }

    public List<UsuarioSistema> completaUsuariosSistemas(String parte) {
        return facade.getUsuarioSistemaFacade().completarUsuarioSistemaPeloNomePessoaFisica(parte.trim());
    }

    public List<HierarquiaOrganizacional> completarUnidadeResponsavel(String parte) {
        List<HierarquiaOrganizacional> retorno = new ArrayList<>();
        for (HierarquiaOrganizacional hierarquiaOrganizacional : facade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaOrganizacionalAdministrativa(parte.trim())) {
            if (hierarquiaOrganizacional.getSuperior() != null) {
                Util.adicionarObjetoEmLista(retorno, hierarquiaOrganizacional);
            }
        }
        return retorno;
    }

    private void validarExclusaoProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoProcessoDeCompra().isLicitacao()) {
            Licitacao licitacao = facade.getLicitacaoFacade().recuperarLicitacaoDoProcessoCompra(selecionado);
            if (licitacao != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Somente é permitido excluir processo de compra que não esteja vinculado a uma licitação. " +
                    "Este processo de compra está vinculado a licitação Nº: " + licitacao.getNumeroLicitacao() + ".");
            }
        } else {
            DispensaDeLicitacao dispensaDeLicitacao = facade.getDispensaDeLicitacaoFacade().recuperarDispensaLicitacaoPorProcessoCompra(selecionado);
            if (dispensaDeLicitacao != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Somente é permitido excluir processo de compra que não esteja vinculado a uma dispensa de licitação. " +
                    "Este processo de compra está vinculado a dispensa de licitação Nº: " + dispensaDeLicitacao.getNumeroDaDispensa() + ".");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getListaTipoSolicitacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoSolicitacao object : TipoSolicitacao.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void validarDataProcessoCompra() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataProcesso() != null) {
            if (selecionado.getDataProcesso().compareTo(facade.getSistemaFacade().getDataOperacao()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data do processo deve ser menor ou igual a data logada.");
            }
            DotacaoSolicitacaoMaterial dotacao = facade.getDotacaoSolicitacaoMaterialFacade().recuperarDotacaoPorSolicitacaoCompra(selecionado.getSolicitacaoMaterial());
            if (dotacao != null) {
                Integer anoDotacao = DataUtil.getAno(dotacao.getRealizadaEm());
                Integer anoProcesso = DataUtil.getAno(selecionado.getDataProcesso());
                if (!anoProcesso.equals(anoDotacao)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A data do processo de compra deve estar no mesmo exercício da data da reserva de dotação");
                }
            }
        }
        ve.lancarException();
    }

    public void listenerSolicitacao() {
        try {
            validarSolicitacao();
            validarDataProcessoCompra();
            selecionado.setSolicitacaoMaterial(facade.getSolicitacaoMaterialFacade().recuperar(selecionado.getSolicitacaoMaterial().getId()));
            selecionado.setUnidadeOrganizacional(selecionado.getSolicitacaoMaterial().getUnidadeOrganizacional());
            selecionado.setDescricao(selecionado.getSolicitacaoMaterial().getDescricao());
            atribuirHierarquiaDaUnidade();
            criarLoteAndItensProcessoCompra();
        } catch (ValidacaoException ve) {
            selecionado.setSolicitacaoMaterial(null);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void atribuirHierarquiaDaUnidade() {
        setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getUnidadeOrganizacional(), facade.getSistemaFacade().getDataOperacao()));
    }

    public void criarLoteAndItensProcessoCompra() {
        selecionado.setLotesProcessoDeCompra(new ArrayList<LoteProcessoDeCompra>());
        for (LoteSolicitacaoMaterial loteSolicitacaoMaterial : selecionado.getSolicitacaoMaterial().getLoteSolicitacaoMaterials()) {
            LoteProcessoDeCompra loteProcesso = new LoteProcessoDeCompra();
            loteProcesso.setProcessoDeCompra(selecionado);
            loteProcesso.setLoteSolicitacaoMaterial(loteSolicitacaoMaterial);
            loteProcesso.setDescricao(loteSolicitacaoMaterial.getDescricao());
            loteProcesso.setNumero(loteSolicitacaoMaterial.getNumero());
            loteProcesso.setValor(loteSolicitacaoMaterial.getValor());

            for (ItemSolicitacao itemSolicitacao : loteSolicitacaoMaterial.getItensSolicitacao()) {
                ItemProcessoDeCompra itemProcesso = new ItemProcessoDeCompra();
                itemProcesso.setLoteProcessoDeCompra(loteProcesso);
                itemProcesso.setItemSolicitacaoMaterial(itemSolicitacao);
                itemProcesso.setNumero(itemSolicitacao.getNumero());
                itemProcesso.setQuantidade(itemSolicitacao.getQuantidade());
                itemProcesso.setSituacaoItemProcessoDeCompra(SituacaoItemProcessoDeCompra.AGUARDANDO);
                atribuirGrupoContaDespesa(itemProcesso);

                Util.adicionarObjetoEmLista(loteProcesso.getItensProcessoDeCompra(), itemProcesso);
            }
            Util.adicionarObjetoEmLista(selecionado.getLotesProcessoDeCompra(), loteProcesso);
        }
    }

    private boolean isTipoSolicitacaoPermitido() {
        return ModalidadeLicitacao.getModalidadesParaTipoDoProcesso(selecionado.getTipoProcessoDeCompra()).contains(selecionado.getSolicitacaoMaterial().getModalidadeLicitacao());
    }

    private void validarSolicitacao() {
        ValidacaoException ve = new ValidacaoException();
        ConfiguracaoReservaDotacao config = facade.getConfiguracaoLicitacaoFacade().buscarConfiguracaoReservaDotacao(selecionado.getSolicitacaoMaterial().getModalidadeLicitacao(), selecionado.getSolicitacaoMaterial().getTipoNaturezaDoProcedimento());
        if (config != null && config.getTipoReservaDotacao().isReservaSolicitacaoCompra()) {
            DotacaoSolicitacaoMaterial dotacao = facade.getDotacaoSolicitacaoMaterialFacade().recuperarDotacaoPorSolicitacaoCompra(selecionado.getSolicitacaoMaterial());
            if (dotacao == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para o(a) " + config.getModalidadeLicitacao().getDescricao() + "/" + config.getNaturezaProcedimento() +
                    ", é necessário realizar a reserva de dotação da solicitação de compra.");
            }
        }
        if (selecionado.getTipoProcessoDeCompra() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o tipo do processo de compra antes de informar a solicitação de compra.");
        }
        setStatusAtualSolicitacao(facade.getSolicitacaoMaterialFacade().getStatusAtualDaSolicitacao(selecionado.getSolicitacaoMaterial()));
        if (statusAtualSolicitacao != null && !TipoStatusSolicitacao.APROVADA.equals(statusAtualSolicitacao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Solicitação selecionada deve estar APROVADA. Status atual da Solicitação: " + statusAtualSolicitacao.getDescricao());
        }
        if (!isTipoSolicitacaoPermitido()) {
            String mensagem = "O Tipo do processo de compra informado (" + selecionado.getTipoProcessoDeCompra().getDescricao() + ") só pode ser utilizado com solicitações das seguintes modalidades: <br/><br/>";
            for (ModalidadeLicitacao modalidade : ModalidadeLicitacao.getModalidadesParaTipoDoProcesso(selecionado.getTipoProcessoDeCompra())) {
                mensagem += "► " + modalidade.getDescricao() + "<br/>";
            }
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
        }
        ve.lancarException();
    }

    private Boolean verificarProcessoUtilizadoLicitacaoOrDispensa() {
        if (selecionado.getTipoProcessoDeCompra().isLicitacao()) {
            return facade.verificarVinculoComLicitacao(selecionado);
        }
        return facade.verificarVinculoComDispensaLicitacao(selecionado);
    }

    public boolean isUtilizadoEmLicitacao() {
        return isOperacaoEditar() && selecionado.getTipoProcessoDeCompra().isLicitacao() && processoUtilizado;
    }

    public boolean isUtilizadoEmDispensa() {
        return isOperacaoEditar() && selecionado.getTipoProcessoDeCompra().isDispensa() && processoUtilizado;
    }

    public boolean isVerificaUnidadeAdministrativaVinculada() {
        UnidadeOrganizacional unidade = facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente();
        ConfiguracaoProcessoCompra configuracao = facade.getConfiguracaoLicitacaoFacade().buscarConfiguracaoProcessoCompraVigente(UtilRH.getDataOperacaoFormatada(), unidade);
        if (facade.getUsuarioSistemaFacade().isGestorLicitacao(facade.getSistemaFacade().getUsuarioCorrente(), unidade)) {
            return configuracao != null;
        }
        return false;
    }

    public List<SolicitacaoMaterial> completarSolicitacao(String parte) {
        return facade.getSolicitacaoMaterialFacade().buscarSolicitacaoAprovadaPeloTipoAndTipoProcessoCompraAndDescricaoOrNumeroOndeUsuarioGestorLicitacao(parte,
            selecionado.getTipoSolicitacao(), selecionado.getTipoProcessoDeCompra(), facade.getSistemaFacade().getUsuarioCorrente());


    }

    public List<ProcessoDeCompra> buscarProcessoDeCompraPorAnoOrNumeroOrDescricaoAndUsuarioGestorLicitacaoParaDispensa(String anoNumeroDescricao) {
        return facade.buscarProcessoCompraSemProcessoLicitario(anoNumeroDescricao,
            TipoProcessoDeCompra.DISPENSA_LICITACAO_INEXIGIBILIDADE, facade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<ProcessoDeCompra> buscarProcessoDeCompraPorAnoOrNumeroOrDescricaoAndUsuarioGestorLicitacaoParaLicitacao(String anoNumeroDescricao) {
        return facade.buscarProcessoCompraSemProcessoLicitario(anoNumeroDescricao,
            TipoProcessoDeCompra.LICITACAO, facade.getSistemaFacade().getUsuarioCorrente());
    }

    public void redirecionarParaLicitacaoOrDispensa() {
        Web.poeNaSessao("PROCESSO_DE_COMPRA", selecionado);
        Web.setCaminhoOrigem("/processo-compra/listar/");
        if (selecionado.getTipoProcessoDeCompra().isLicitacao()) {
            FacesUtil.redirecionamentoInterno("/licitacao/novo/");
        } else if (selecionado.getTipoProcessoDeCompra().isDispensa()) {
            FacesUtil.redirecionamentoInterno("/dispensa-licitacao/novo/");
        } else {
            FacesUtil.redirecionamentoInterno("/credenciamento/novo/");
        }
    }

    private void atribuirGrupoContaDespesaItemProcesso() {
        for (LoteProcessoDeCompra lote : selecionado.getLotesProcessoDeCompra()) {
            for (ItemProcessoDeCompra item : lote.getItensProcessoDeCompra()) {
                atribuirGrupoContaDespesa(item);
            }
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public TipoStatusSolicitacao getStatusAtualSolicitacao() {
        return statusAtualSolicitacao;
    }

    public void setStatusAtualSolicitacao(TipoStatusSolicitacao statusAtualSolicitacao) {
        this.statusAtualSolicitacao = statusAtualSolicitacao;
    }

    public Boolean getProcessoUtilizado() {
        return processoUtilizado;
    }

    public void setProcessoUtilizado(Boolean processoUtilizado) {
        this.processoUtilizado = processoUtilizado;
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.PROCESSO_COMPRA);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
        if ("tab-ajuste".equals(tab)) {
            Long id = facade.recuperarIdLicitacaoOrDispensa(selecionado);
            if (id != null) {
                TipoMovimentoProcessoLicitatorio tipoProcesso = selecionado.getTipoProcessoDeCompra().isDispensa()
                    ? TipoMovimentoProcessoLicitatorio.DISPENSA_LICITACAO_INEXIGIBILIDADE
                    : TipoMovimentoProcessoLicitatorio.LICITACAO;
                filtroAjusteProcesso = new FiltroHistoricoProcessoLicitatorio(id, tipoProcesso);
            }
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroAjusteProcesso() {
        return filtroAjusteProcesso;
    }

    public void setFiltroAjusteProcesso(FiltroHistoricoProcessoLicitatorio filtroAjusteProcesso) {
        this.filtroAjusteProcesso = filtroAjusteProcesso;
    }
}
