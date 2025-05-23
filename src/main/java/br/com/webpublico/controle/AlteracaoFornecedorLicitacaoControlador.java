package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusLancePregao;
import br.com.webpublico.enums.TipoAlteracaoFornecedorLicitacao;
import br.com.webpublico.enums.TipoApuracaoLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlteracaoFornecedorLicitacaoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-alteracao-forn-lic", pattern = "/alteracao-fornecedor-licitacao/novo/", viewId = "/faces/administrativo/licitacao/alteracao-fornecedor-licitacao/edita.xhtml"),
    @URLMapping(id = "listar-alteracao-forn-lic", pattern = "/alteracao-fornecedor-licitacao/listar/", viewId = "/faces/administrativo/licitacao/alteracao-fornecedor-licitacao/lista.xhtml"),
    @URLMapping(id = "ver-alteracao-forn-lic", pattern = "/alteracao-fornecedor-licitacao/ver/#{alteracaoFornecedorLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/alteracao-fornecedor-licitacao/visualizar.xhtml")
})
public class AlteracaoFornecedorLicitacaoControlador extends PrettyControlador<AlteracaoFornecedorLicitacao> implements Serializable, CRUD {

    @EJB
    private AlteracaoFornecedorLicitacaoFacade facade;
    private Licitacao licitacao;
    private LicitacaoFornecedor participanteLicitacao;
    private Pessoa participanteSubstituicao;
    private List<LicitacaoFornecedor> participantes;
    private List<PropostaFornecedor> propostas;
    private PropostaFornecedor propostaFornecedor;
    private RepresentanteLicitacao representanteLicitacao;
    private List<ItemPregao> itensPregao;
    private ItemPregao itemPregao;
    private RodadaPregao rodadaPregao;
    private LotePropostaFornecedor lotePropostaFornecedor;
    private ItemPropostaFornecedor itemPropostaFornecedor;
    private List<LotePropostaFornecedor> lotesProposta;
    private DispensaDeLicitacao dispensaLicitacao;
    private List<Contrato> contratosDispensa;
    private ConverterAutoComplete converterFornecedorDispensaLicitacao;


    public AlteracaoFornecedorLicitacaoControlador() {
        super(AlteracaoFornecedorLicitacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/alteracao-fornecedor-licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "nova-alteracao-forn-lic", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @URLAction(mappingId = "ver-alteracao-forn-lic", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        if (isSubstituirFornecedorDispensa()) {
            dispensaLicitacao = selecionado.getFornecedorDispensaLicitacao().getDispensaDeLicitacao();
        } else {
            licitacao = selecionado.getLicitacaoFornecedor().getLicitacao();
        }
        if (isTipoAlteracaoProposta()) {
            itensPregao = facade.getItemPregaoFacade().buscarItemPregaoPorItemProcessoCompra(licitacao, getIdsItemProcessoCompra(), null);
        }
    }

    public void salvar() {
        try {
            validarCamposObrigatorio();
            atribuirPropostaFornecedor();
            criarItensAlteracao();
            selecionado = facade.salvarRetornando(selecionado, participanteSubstituicao);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao salvar alteração do fornecedor da licitação ", e);
            descobrirETratarException(e);
        }
    }

    public void salvarLance() {
        try {
            validarSalvarLance();
            facade.salvarLance(itemPregao);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao salvar lance pregão", e);
            descobrirETratarException(e);
        }
    }

    public List<Long> getIdsItemProcessoCompra() {
        List<Long> ids = Lists.newArrayList();
        for (AlteracaoFornecedorLicitacaoItem item : selecionado.getItens()) {
            ids.add(item.getItemPropostaFornecedor().getItemProcessoDeCompra().getId());
        }
        return ids;
    }


    private void criarItensAlteracao() {
        if (isTipoAlteracaoProposta()) {
            for (LotePropostaFornecedor lote : lotesProposta) {
                for (ItemPropostaFornecedor itemProp : lote.getItens()) {
                    if (itemProp.getSelecionado()) {
                        AlteracaoFornecedorLicitacaoItem itemAlt = new AlteracaoFornecedorLicitacaoItem();
                        itemAlt.setAlteracaoFornecedorLicit(selecionado);
                        itemAlt.setItemPropostaFornecedor(itemProp);
                        selecionado.getItens().add(itemAlt);
                    }
                }
            }
        }
    }

    private void validarSalvarLance() {
        ValidacaoException ve = new ValidacaoException();
        boolean lanceGerado = false;
        for (LancePregao lancePregao : rodadaPregao.getListaDeLancePregao()) {
            lanceGerado = lancePregao.getId() == null;
        }
        if (!lanceGerado) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Antes de salvar, é necessário gerar o(s) lance(s) no pregão para a proposta do fornecedor.");
        }
        ve.lancarException();
    }

    private void atribuirPropostaFornecedor() {
        if (!isSubstituirFornecedorDispensa()) {
            selecionado.setLicitacaoFornecedor(participanteLicitacao);
            if (isTipoAlteracaoProposta()) {
                propostaFornecedor.setLotes(lotesProposta);
                selecionado.setPropostaFornecedor(propostaFornecedor);
            } else {
                participanteLicitacao.setRepresentante(representanteLicitacao);
            }
        }
    }

    public List<SelectItem> getTiposAlteracao() {
        return Util.getListSelectItem(TipoAlteracaoFornecedorLicitacao.values(), false);
    }

    public List<PropostaFornecedor> completarPropostaFornecedor(String parte) {
        return facade.getPropostaFornecedorFacade().buscarPorLicitacao(licitacao.getId(), parte.trim());
    }

    public List<Licitacao> completarLicitacao(String parte) {
        return facade.getLicitacaoFacade().buscarLicitacoesParaAlteracaoFonrecedor(parte.trim());
    }

    public List<LicitacaoFornecedor> completarParticipantesLicitacao(String parte) {
        return facade.getParticipanteLicitacaoFacade().buscarPorLicitacao(licitacao.getId(), parte.trim());
    }

    public List<DispensaDeLicitacao> completarDispensaLicitacao(String parte) {
        return facade.getDispensaLicitacaoFacade().buscarDispensaDeLicitacaoPorNumeroOrExercicioOrResumoAndUsuarioAndGestor(parte.trim(), facade.getSistemaFacade().getUsuarioCorrente(), Boolean.TRUE);
    }

    public List<FornecedorDispensaDeLicitacao> completarFornecedoresDispensa(String parte) {
        return facade.getFornecedorDispensaDeLicitacaoFacade().buscarFornecedoresDispensaLicitacao(dispensaLicitacao, parte.trim());
    }

    public List<Pessoa> completarPessoasFornecedorasDispensa(String parte) {
        return facade.getDispensaLicitacaoFacade().getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<Contrato> buscarContratosDispensa() {
        contratosDispensa = facade.getContratoFacade().buscarContratoDispensa(dispensaLicitacao);
        return contratosDispensa;
    }

    public void limparCampos() {
        propostaFornecedor = null;
        participanteLicitacao = null;
        licitacao = null;
        selecionado.setTipoAlteracao(null);
    }

    public void listenerNovaParticipanteLicitacao() {
        representanteLicitacao = null;
        if (isParticipantePessoaJuridica()) {
            representanteLicitacao = new RepresentanteLicitacao();
        }
    }

    public void listenerParticipanteLicitacao() {
        if (hasParticipante()) {
            representanteLicitacao = participanteLicitacao.getRepresentante();
            if (representanteLicitacao == null){
                listenerNovaParticipanteLicitacao();
            }
        }
    }

    public void listenerNovaPropostaFornecedor() {
        try {
            validarNovaProposta();
            if (participanteLicitacao != null) {
                propostaFornecedor.setRepresentante(participanteLicitacao.getRepresentante());
                propostaFornecedor.setInstrumentoRepresentacao(participanteLicitacao.getInstrumentoRepresentacao());
                propostaFornecedor.setFornecedor(participanteLicitacao.getEmpresa());
                selecionado.setLicitacaoFornecedor(participanteLicitacao);
                facade.getPropostaFornecedorFacade().criarLotesItensProposta(propostaFornecedor);
                lotesProposta = propostaFornecedor.getLotes();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarNovaProposta() {
        ValidacaoException ve = new ValidacaoException();
        if (facade.getPropostaFornecedorFacade().fornecedorJaFezPropostaParaEstaLicitacao(participanteLicitacao.getEmpresa(), licitacao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O fornecedor selecionado já possui proposta para a licitação: " + licitacao + ".");
        }
        LicitacaoFornecedor participante = facade.getParticipanteLicitacaoFacade().buscarLicitacaoFornecedor(licitacao, participanteLicitacao.getEmpresa());
        if (participante == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A licitação não possui fornecedor com a pessoa " + propostaFornecedor.getFornecedor());
        }
        ve.lancarException();
    }

    public void listenerPropostaFornecedor() {
        propostaFornecedor = facade.getPropostaFornecedorFacade().recuperarComDependenciasItens(propostaFornecedor.getId());
        lotesProposta = propostaFornecedor.getLotes();
        participanteLicitacao = facade.getParticipanteLicitacaoFacade().buscarLicitacaoFornecedor(licitacao, propostaFornecedor.getFornecedor());
        for (LotePropostaFornecedor lote : lotesProposta) {
            for (ItemPropostaFornecedor item : lote.getItens()) {
                List<ItemPregao> itensPregao = facade.getItemPregaoFacade().buscarItemPregaoPorItemProcessoCompra(licitacao, Lists.newArrayList(item.getItemProcessoDeCompra().getId()), propostaFornecedor);
                if (itensPregao != null && itensPregao.size() == 1) {
                    item.setItemPregao(itensPregao.get(0));
                }
            }
        }
    }

    public void listenerLicitacaoOuDispensa() {
        setParticipanteLicitacao(null);
        setPropostaFornecedor(null);
        selecionado.setFornecedorDispensaLicitacao(null);
        if (selecionado.getTipoAlteracao() != null && (dispensaLicitacao != null || licitacao != null)) {
            switch (selecionado.getTipoAlteracao()) {
                case NOVA_PROPOSTA:
                    novaPropostaFornecedor();
                    break;
                case ALTERAR_PROPOSTA:
                    buscarPropostasFornecedor();
                    break;
                case NOVO_PARTICIPANTE:
                    novoParticipanteLicitacao();
                    break;
                case ALTERAR_PARTICIPANTE:
                    buscarParticipantesLicitacao();
                    break;
                case SUBSTITUIR_FORNECEDOR_DISPENSA_LICITACAO:
                    buscarContratosDispensa();
                    break;
                default:
                    break;
            }
        }
    }

    public void limparCamposAoTrocarTipoAlteracao() {
        setLicitacao(null);
        setDispensaLicitacao(null);
        selecionado.setFornecedorDispensaLicitacao(null);
        contratosDispensa = null;
    }

    private void buscarParticipantesLicitacao() {
        participantes = facade.getParticipanteLicitacaoFacade().buscarPorLicitacao(licitacao.getId(), "");
    }

    private void buscarPropostasFornecedor() {
        propostas = facade.getPropostaFornecedorFacade().buscarPorLicitacao(licitacao.getId(), "");
    }

    public void selecionarProposta(PropostaFornecedor proposta) {
        propostaFornecedor = proposta;
    }

    public void selecionarParticipante(LicitacaoFornecedor participante) {
        participanteLicitacao = participante;
    }

    public void novoParticipanteLicitacao() {
        try {
            participanteLicitacao = new LicitacaoFornecedor();
            buscarParticipantesLicitacao();
            participanteLicitacao.setCodigo(facade.getParticipanteLicitacaoFacade().getMaiorCodigoLicitacaoFornecedor(participantes));
            participanteLicitacao.setLicitacao(licitacao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCamposObrigatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoAlteracao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de alteração deve ser informado.");
        }

        if (isSubstituirFornecedorDispensa()) {
            if (dispensaLicitacao == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Dispensa de Licitação/Inexigibilidade deve ser informado.");
            }
            if (selecionado.getFornecedorDispensaLicitacao() == null){
                ve.adicionarMensagemDeCampoObrigatorio("O campo fornecedor 'de' deve ser informado.");
            }
            if (participanteSubstituicao == null){
                ve.adicionarMensagemDeCampoObrigatorio("O campo fornecedor 'para' deve ser informado.");
            }
        } else {
            if (licitacao == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo licitação deve ser informado.");
            }
        }

        if (isNovoParticipante() || isAlterarParticipante()) {
            if (!hasParticipante()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum alteração referente ao participante foi realizada.");
            } else if (participanteLicitacao.getEmpresa() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo fornecedor deve ser informado. ");
            } else {
                participantes.stream()
                    .filter(part -> !part.equals(participanteLicitacao))
                    .filter(part -> part.getEmpresa().equals(participanteLicitacao.getEmpresa()))
                    .map(part -> "Fornecedor já cadastrado para esta licitação.")
                    .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
            }
            ve.lancarException();

            if (isParticipantePessoaJuridica()) {
                Util.validarCampos(representanteLicitacao);
                if (Strings.isNullOrEmpty(participanteLicitacao.getInstrumentoRepresentacao())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo instrumento de representação deve ser informado. ");
                }
            }
        }
        if (isNovaProposta() || isAlterarProposta()) {
            if (!hasParticipante()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum alteração referente a proposta foi realizada.");
            }
        }
        if (isSubstituirParticipante()){
            if (participanteLicitacao == null){
                ve.adicionarMensagemDeCampoObrigatorio("O campo participante 'de' deve ser informado.");
            }
            if (participanteSubstituicao == null){
                ve.adicionarMensagemDeCampoObrigatorio("O campo participante 'para' deve ser informado.");
            }

        }
        ve.lancarException();
    }

    public void novaPropostaFornecedor() {
        try {
            propostaFornecedor = new PropostaFornecedor();
            propostaFornecedor.setLicitacao(licitacao);
            propostaFornecedor.setDataProposta(licitacao.getAbertaEm());
            propostaFornecedor.setLicitacaoFornecedor(participanteLicitacao);
            buscarPropostasFornecedor();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarAndVerificarCpfRepresentante() {
        try {
            validarCpf();
            verificarExistenciaRepresentante();
            participanteLicitacao.setRepresentante(representanteLicitacao);
        } catch (ValidacaoException ve) {
            representanteLicitacao.setNome(null);
            representanteLicitacao.setCpf(null);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCpf() {
        ValidacaoException ve = new ValidacaoException();
        if (StringUtils.isEmpty(representanteLicitacao.getCpf())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo CPF deve ser informado.");
        } else if (!Util.validarCpf(representanteLicitacao.getCpf())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF digitado é inválido.");
        }
        ve.lancarException();
    }

    private void verificarExistenciaRepresentante() {
        String cpf = representanteLicitacao.getCpf();
        RepresentanteLicitacao representante = facade.getLicitacaoFacade().getRepresentanteLicitacaoFacade().buscarRepresentanteLicitacaoPorCpf(cpf);
        if (representante.getId() != null) {
            setRepresentanteLicitacao(representante);
        } else {
            setRepresentanteLicitacao(representante);
            representanteLicitacao.setCpf(cpf);
        }
    }

    public void selecionarItemPregao(ItemPregao item) {
        itemPregao = facade.getPregaoFacade().buscarRodadasAndLancesPregao(item);
        Collections.sort(itemPregao.getListaDeRodadaPregao());
        rodadaPregao = itemPregao.getListaDeRodadaPregao().get(item.getListaDeRodadaPregao().size() - 1);
        if (rodadaPregao != null) {
            for (RodadaPregao rodada : itemPregao.getListaDeRodadaPregao()) {
                itemPregao.getPregao().atribuirValorLanceRodadaAnterior(itemPregao, rodada);

                for (LancePregao lancePregao : rodada.getListaDeLancePregao()) {
                    List<ItemPropostaFornecedor> itensProposta = facade.getPropostaFornecedorFacade().recuperarItemPropostaFornecedorPorItemProcessoCompra(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra());
                    facade.getPregaoFacade().atribuirValorPrimeiraPropostaNoLance(lancePregao, itensProposta);
                }
            }
            FacesUtil.executaJavaScript("dlgLancePregao.show()");
        }
    }

    public void gerarLanceParaNovaProposta() {
        try {
            for (RodadaPregao rod : itemPregao.getListaDeRodadaPregao()) {
                validarGeracaoLance(rod);
                LancePregao lance = new LancePregao();
                lance.setRodadaPregao(rod);
                lance.setStatusLancePregao(StatusLancePregao.DECLINADO);
                lance.setPropostaFornecedor(selecionado.getPropostaFornecedor());
                lance.setValor(BigDecimal.ZERO);
                lance.setPercentualDesconto(BigDecimal.ZERO);
                lance.setValorPropostaInicial(getValorPropostaItemAlteracao());
                Util.adicionarObjetoEmLista(rod.getListaDeLancePregao(), lance);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarGeracaoLance(RodadaPregao rodadaPregao) {
        ValidacaoException ve = new ValidacaoException();
        for (LancePregao lance : rodadaPregao.getListaDeLancePregao()) {
            if (lance.getPropostaFornecedor().equals(selecionado.getPropostaFornecedor())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A rodada já possui um lance para o proposta " + selecionado.getPropostaFornecedor());
                break;
            }
        }
        ve.lancarException();
    }

    private BigDecimal getValorPropostaItemAlteracao() {
        for (AlteracaoFornecedorLicitacaoItem item : selecionado.getItens()) {
            if (item.getItemPropostaFornecedor().getItemProcessoDeCompra().equals(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra())) {
                return selecionado.getLicitacaoFornecedor().getLicitacao().getTipoAvaliacao().isMaiorDesconto()
                    ?  item.getItemPropostaFornecedor().getPercentualDesconto()
                    :  item.getItemPropostaFornecedor().getPreco();
            }
        }
        return BigDecimal.ZERO;
    }

    public void carregarInformacaoProduto(ItemPropostaFornecedor item) {
        itemPropostaFornecedor = item;
        if (itemPropostaFornecedor.getItemProcessoDeCompra() != null && item.getItemProcessoDeCompra().getItemSolicitacaoMaterial() != null) {
            itemPropostaFornecedor.setDescricaoProduto(itemPropostaFornecedor.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao());
        }
    }

    public void selecionarItem(ItemPropostaFornecedor item) {
        boolean selecionado = !Strings.isNullOrEmpty(item.getMarca()) && (item.hasPreco() || item.hasPercentualDesconto());
        item.setSelecionado(selecionado);
    }

    public void selecionarLote(LotePropostaFornecedor loteProposta) {
        try {
            validarFornecedor();
            lotePropostaFornecedor = loteProposta;
            FacesUtil.executaJavaScript("dlgProposta.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarFornecedor() {
        ValidacaoException ve = new ValidacaoException();
        if (propostaFornecedor.getFornecedor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo fornecedor deve ser informado antes de realizar os lançamentos.");
        }
        ve.lancarException();
    }

    public void confirmaInformacaoProduto() {
        ValidacaoException exception = new ValidacaoException();
        try {
            Util.validarQuantidadeCaracter(itemPropostaFornecedor, exception);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
        if (!exception.temMensagens())
            FacesUtil.executaJavaScript("dlgInfoProduto.hide()");
    }

    public void cancelarInformacaoProduto() {
        itemPropostaFornecedor.setModelo("");
        itemPropostaFornecedor.setDescricaoProduto("");
    }

    public void cancelarProposta() {
        for (LotePropostaFornecedor lote : lotesProposta) {
            if (lotePropostaFornecedor.equals(lote)) {
                for (ItemPropostaFornecedor item : lote.getItens()) {
                    if (item.getItemPregao() == null) {
                        item.setMarca(null);
                        item.setModelo(null);
                        item.setDescricaoProduto(null);
                        item.setPreco(BigDecimal.ZERO);
                    }
                }
                atualizaValorLote();
            }
        }
        FacesUtil.executaJavaScript("dlgProposta.hide()");
    }

    public void confirmarProposta() {
        try {
            facade.getPropostaFornecedorFacade().validarPropostas(licitacao, lotePropostaFornecedor);
            atualizaValorLote();
            atualizarItensProposta();
            FacesUtil.executaJavaScript("dlgProposta.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void atualizarItensProposta() {
        limparItensProposta();
        for (LotePropostaFornecedor lote : lotesProposta) {
            for (ItemPropostaFornecedor item : lote.getItens()) {
                propostaFornecedor.setListaDeItensPropostaFornecedor(Util.adicionarObjetoEmLista(propostaFornecedor.getListaDeItensPropostaFornecedor(), item));
            }
        }
        Util.adicionarObjetoEmLista(propostaFornecedor.getLotes(), lotePropostaFornecedor);
    }

    private void limparItensProposta() {
        propostaFornecedor.setListaDeItensPropostaFornecedor(null);
    }

    public void atualizaValorLote() {
        BigDecimal valor = BigDecimal.ZERO;
        for (ItemPropostaFornecedor ipf : lotePropostaFornecedor.getItens()) {
            if (ipf.hasPreco()) {
                if (ipf.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle().isTipoControlePorQuantidade()) {
                    valor = valor.add(ipf.getPrecoTotal());
                } else {
                    valor = valor.add(ipf.getPreco());
                }
            }
        }
        lotePropostaFornecedor.setValor(valor);
    }

    public boolean isMaiorDesconto() {
        return licitacao != null && licitacao.getTipoAvaliacao().isMaiorDesconto();
    }

    public boolean isApuracaoPorItem() {
        return licitacao != null && licitacao.getTipoApuracao().isPorItem();
    }

    public boolean hasLotes() {
        return lotesProposta != null && !lotesProposta.isEmpty();
    }

    public boolean hasItensPregao() {
        return itensPregao != null && !itensPregao.isEmpty();
    }

    public boolean hasParticipantes() {
        return participantes != null && !participantes.isEmpty();
    }

    public boolean hasPropostas() {
        return propostas != null && !propostas.isEmpty();
    }

    public boolean hasContratos() {
        return contratosDispensa != null && !contratosDispensa.isEmpty();
    }

    public LotePropostaFornecedor getLotePropostaFornecedor() {
        return lotePropostaFornecedor;
    }

    public void setLotePropostaFornecedor(LotePropostaFornecedor lotePropostaFornecedor) {
        this.lotePropostaFornecedor = lotePropostaFornecedor;
    }

    public ItemPropostaFornecedor getItemPropostaFornecedor() {
        return itemPropostaFornecedor;
    }

    public void setItemPropostaFornecedor(ItemPropostaFornecedor itemPropostaFornecedor) {
        this.itemPropostaFornecedor = itemPropostaFornecedor;
    }

    public void cancelarLanceParaNovaProposta() {
        itemPregao = null;
    }

    public boolean hasParticipante() {
        return participanteLicitacao != null;
    }

    public boolean hasPropostaFornecedor() {
        return propostaFornecedor != null;
    }

    public boolean isParticipantePessoaJuridica() {
        return hasParticipante() && participanteLicitacao.getEmpresa() != null && participanteLicitacao.getEmpresa().isPessoaJuridica();
    }

    public boolean isPropostaFornecedorPessoaFisica() {
        return hasPropostaFornecedor() && propostaFornecedor.getFornecedor() != null && propostaFornecedor.getFornecedor().isPessoaFisica();
    }

    public boolean isNovoParticipante() {
        return selecionado.getTipoAlteracao() != null && selecionado.getTipoAlteracao().isNovoParticipante();
    }

    public boolean isAlterarParticipante() {
        return selecionado.getTipoAlteracao() != null && selecionado.getTipoAlteracao().isAlterarParticipante();
    }

    public boolean isNovaProposta() {
        return selecionado.getTipoAlteracao() != null && selecionado.getTipoAlteracao().isNovaProposta();
    }

    public boolean isAlterarProposta() {
        return selecionado.getTipoAlteracao() != null && selecionado.getTipoAlteracao().isAlterarProposta();
    }

    public boolean isSubstituirParticipante() {
        return selecionado.getTipoAlteracao() != null && selecionado.getTipoAlteracao().isSubstituirParticipante();
    }

    public boolean isSubstituirFornecedorDispensa() {
        return selecionado.getTipoAlteracao() != null && selecionado.getTipoAlteracao().isSubstituirFornecedorDispensa();
    }

    public boolean isTipoAlteracaoProposta() {
        return licitacao != null && (isNovaProposta() || isAlterarProposta());
    }

    public boolean isTipoAlteracaoParticipante() {
        return licitacao != null && (isNovoParticipante() || isAlterarParticipante());
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public LicitacaoFornecedor getParticipanteLicitacao() {
        return participanteLicitacao;
    }

    public void setParticipanteLicitacao(LicitacaoFornecedor participanteLicitacao) {
        this.participanteLicitacao = participanteLicitacao;
    }

    public PropostaFornecedor getPropostaFornecedor() {
        return propostaFornecedor;
    }

    public void setPropostaFornecedor(PropostaFornecedor propostaFornecedor) {
        this.propostaFornecedor = propostaFornecedor;
    }

    public RepresentanteLicitacao getRepresentanteLicitacao() {
        return representanteLicitacao;
    }

    public void setRepresentanteLicitacao(RepresentanteLicitacao representanteLicitacao) {
        this.representanteLicitacao = representanteLicitacao;
    }

    public List<LicitacaoFornecedor> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<LicitacaoFornecedor> participantes) {
        this.participantes = participantes;
    }

    public List<ItemPregao> getItensPregao() {
        return itensPregao;
    }

    public void setItensPregao(List<ItemPregao> itensPregao) {
        this.itensPregao = itensPregao;
    }

    public ItemPregao getItemPregao() {
        return itemPregao;
    }

    public void setItemPregao(ItemPregao itemPregao) {
        this.itemPregao = itemPregao;
    }

    public RodadaPregao getRodadaPregao() {
        return rodadaPregao;
    }

    public void setRodadaPregao(RodadaPregao rodadaPregao) {
        this.rodadaPregao = rodadaPregao;
    }

    public List<LotePropostaFornecedor> getLotesProposta() {
        return lotesProposta;
    }

    public void setLotesProposta(List<LotePropostaFornecedor> lotesProposta) {
        this.lotesProposta = lotesProposta;
    }

    public List<PropostaFornecedor> getPropostas() {
        return propostas;
    }

    public void setPropostas(List<PropostaFornecedor> propostas) {
        this.propostas = propostas;
    }

    public Pessoa getParticipanteSubstituicao() {
        return participanteSubstituicao;
    }

    public void setParticipanteSubstituicao(Pessoa participanteSubstituicao) {
        this.participanteSubstituicao = participanteSubstituicao;
    }

    public DispensaDeLicitacao getDispensaLicitacao() {
        return dispensaLicitacao;
    }

    public void setDispensaLicitacao(DispensaDeLicitacao dispensaLicitacao) {
        this.dispensaLicitacao = dispensaLicitacao;
    }

    public List<Contrato> getContratosDispensa() {
        return contratosDispensa;
    }

    public void setContratosDispensa(List<Contrato> contratosDispensa) {
        this.contratosDispensa = contratosDispensa;
    }

    public ConverterAutoComplete getConverterFornecedorDispensaLicitacao() {
        if (converterFornecedorDispensaLicitacao == null) {
            converterFornecedorDispensaLicitacao = new ConverterAutoComplete(FornecedorDispensaDeLicitacao.class, facade.getFornecedorDispensaDeLicitacaoFacade());
        }
        return converterFornecedorDispensaLicitacao;
    }

    public void redirecionarContrato(Contrato contrato) {
        FacesUtil.redirecionamentoInterno("/contrato-adm/ver/" + contrato.getId() + "/");
    }
}
