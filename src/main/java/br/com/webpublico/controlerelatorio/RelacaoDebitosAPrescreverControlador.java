package br.com.webpublico.controlerelatorio;


import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoPrazoPrescricional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.SituacaoCadastralPessoaDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author octavio
 * Date : 26/04/2019
 */

@ManagedBean
@ViewScoped
@URLMappings(
    mappings = {
        @URLMapping(id = "novaRelacaoDebitosAPrescrever", pattern = "/tributario/relacao-debitos-a-prescrever/",
            viewId = "/faces/tributario/debitosaprescrever/relatorio/relacaodebitosaprescrever.xhtml")
    }
)
public class RelacaoDebitosAPrescreverControlador implements Serializable {

    private FiltroRelacaoDebitosAPrescreverDTO filtroRelacaoDebitosAPrescrever;
    @EJB
    private RelacaoDebitosAPrescreverFacade relacaoDebitosAPrescreverFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private DividaFacade dividaFacade;
    private List<SelectItem> classificacoesAtividades;
    private List<SelectItem> tiposAutonomos;
    private List<SelectItem> naturezasJuridicas;
    private List<SelectItem> tiposImovel;
    private List<SelectItem> dividas;
    private Object objeto;
    private Map<TipoObjetoLista, List<Object>> mapObjetosSelecionados;
    private SituacaoCadastralCadastroEconomico situacaoCadastralEconomico;
    private TipoPrazoPrescricional tipoPrazoPrescricional;

    public FiltroRelacaoDebitosAPrescreverDTO getFiltroRelacaoDebitosAPrescrever() {
        return filtroRelacaoDebitosAPrescrever;
    }

    public void setFiltroRelacaoDebitosAPrescrever(FiltroRelacaoDebitosAPrescreverDTO filtroRelacaoDebitosAPrescrever) {
        this.filtroRelacaoDebitosAPrescrever = filtroRelacaoDebitosAPrescrever;
    }

    public Object getObjeto() {
        return objeto;
    }

    public void setObjeto(Object objeto) {
        this.objeto = objeto;
    }

    public Map<TipoObjetoLista, List<Object>> getMapObjetosSelecionados() {
        if (mapObjetosSelecionados == null) {
            mapObjetosSelecionados = Maps.newHashMap();
        }
        return mapObjetosSelecionados;
    }

    public List<Object> objetosSelecionados(String tipoObjeto) {
        return getMapObjetosSelecionados().get(TipoObjetoLista.valueOf(tipoObjeto.toUpperCase()));
    }

    public SituacaoCadastralCadastroEconomico getSituacaoCadastralEconomico() {
        return situacaoCadastralEconomico;
    }

    public void setSituacaoCadastralEconomico(SituacaoCadastralCadastroEconomico situacaoCadastralEconomico) {
        this.situacaoCadastralEconomico = situacaoCadastralEconomico;
    }

    public TipoPrazoPrescricional getTipoPrazoPrescricional() {
        return tipoPrazoPrescricional;
    }

    public void setTipoPrazoPrescricional(TipoPrazoPrescricional tipoPrazoPrescricional) {
        this.tipoPrazoPrescricional = tipoPrazoPrescricional;
    }

    @URLAction(mappingId = "novaRelacaoDebitosAPrescrever", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void instanciarFiltroLancamento() {
        filtroRelacaoDebitosAPrescrever = new FiltroRelacaoDebitosAPrescreverDTO();
        Exercicio exercicioVigente = relacaoDebitosAPrescreverFacade.getExercicioFacade().getExercicioCorrente();
        filtroRelacaoDebitosAPrescrever.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoDebitosAPrescrever.setExercicioDividaFinal(exercicioVigente.getAno());
        if (TipoCadastroTributarioDTO.IMOBILIARIO.equals(filtroRelacaoDebitosAPrescrever.getTipoCadastroTributario())) {
            filtroRelacaoDebitosAPrescrever.setInscricaoInicial("100000000000000");
            filtroRelacaoDebitosAPrescrever.setInscricaoFinal("999999999999999");
        } else if (TipoCadastroTributarioDTO.ECONOMICO.equals(filtroRelacaoDebitosAPrescrever.getTipoCadastroTributario())) {
            filtroRelacaoDebitosAPrescrever.setInscricaoInicial("1");
            filtroRelacaoDebitosAPrescrever.setInscricaoFinal("9999999999");
        }
    }

    public void limparCampos() {
        FacesUtil.redirecionamentoInterno("/tributario/relacao-debitos-a-prescrever/");
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosUtilizados();
            popularFiltroDTO();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeParametroBrasao("BRASAO");
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("NOMERELATORIO", "RELAÇÃO DE DÉBITOS A PRESCREVER");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
            dto.adicionarParametro("FILTRO_DTO", filtroRelacaoDebitosAPrescrever);
            dto.setNomeRelatorio("Relação de lançamento de débitos a prescrever");
            dto.setApi("tributario/relacao-debitos-a-prescrever/" +
                (TipoRelatorioDTO.XLS.equals(dto.getTipoRelatorio()) ? "excel/" : ""));
            ReportService.getInstance().gerarRelatorio(relacaoDebitosAPrescreverFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void popularFiltroDTO() {
        for (TipoObjetoLista value : TipoObjetoLista.values()) {
            if (getMapObjetosSelecionados().containsKey(value)) {
                StringBuilder idsObjeto = new StringBuilder();
                StringBuilder descricaoObjeto = new StringBuilder();

                if (value.equals(TipoObjetoLista.BAIRRO)) {
                    for (Object o : getMapObjetosSelecionados().get(value)) {
                        idsObjeto.append(((Bairro) o).getId());
                        descricaoObjeto.append(((Bairro) o).getDescricao());
                        if (!o.equals(getMapObjetosSelecionados().get(value).get(getMapObjetosSelecionados().get(value).size() - 1))) {
                            idsObjeto.append(", ");
                            descricaoObjeto.append(", ");
                        }
                    }
                    getFiltroRelacaoDebitosAPrescrever().setIdsBairros(idsObjeto.toString());
                    getFiltroRelacaoDebitosAPrescrever().setDescricaoBairro(descricaoObjeto.toString());
                } else if (value.equals(TipoObjetoLista.LOGRADOURO)) {
                    for (Object o : getMapObjetosSelecionados().get(value)) {
                        idsObjeto.append(((Logradouro) o).getId());
                        descricaoObjeto.append(((Logradouro) o).getNome());
                        if (!o.equals(getMapObjetosSelecionados().get(value).get(getMapObjetosSelecionados().get(value).size() - 1))) {
                            idsObjeto.append(", ");
                            descricaoObjeto.append(", ");
                        }
                    }
                    getFiltroRelacaoDebitosAPrescrever().setIdsLogradouro(idsObjeto.toString());
                    getFiltroRelacaoDebitosAPrescrever().setDescricaoLogradouros(descricaoObjeto.toString());
                } else if (value.equals(TipoObjetoLista.DIVIDA)) {
                    for (Object o : getMapObjetosSelecionados().get(value)) {
                        idsObjeto.append(((Divida) o).getId());
                        descricaoObjeto.append(((Divida) o).getDescricao());
                        if (!o.equals(getMapObjetosSelecionados().get(value).get(getMapObjetosSelecionados().get(value).size() - 1))) {
                            idsObjeto.append(", ");
                            descricaoObjeto.append(", ");
                        }
                    }
                    getFiltroRelacaoDebitosAPrescrever().setIdsDivida(idsObjeto.toString());
                    getFiltroRelacaoDebitosAPrescrever().setDividas(descricaoObjeto.toString());
                } else if (value.equals(TipoObjetoLista.TIPO_AUTONOMO)) {
                    for (Object o : getMapObjetosSelecionados().get(value)) {
                        idsObjeto.append(((TipoAutonomo) o).getId());
                        descricaoObjeto.append(((TipoAutonomo) o).getDescricao());
                        if (!o.equals(getMapObjetosSelecionados().get(value).get(getMapObjetosSelecionados().get(value).size() - 1))) {
                            idsObjeto.append(", ");
                            descricaoObjeto.append(", ");
                        }
                    }
                    getFiltroRelacaoDebitosAPrescrever().setIdsTipoAutonomo(idsObjeto.toString());
                    getFiltroRelacaoDebitosAPrescrever().setDescricaoTipoAutonomo(descricaoObjeto.toString());
                } else if (value.equals(TipoObjetoLista.CONTRIBUINTE)) {
                    List<Long> idsContribuintes = Lists.newArrayList();
                    for (Object o : getMapObjetosSelecionados().get(value)) {
                        idsContribuintes.add(((Pessoa) o).getId());
                        descricaoObjeto.append(((Pessoa) o).getNome()).append(" (").append(((Pessoa) o).getCpf_Cnpj()).append(")");
                        if (!o.equals(getMapObjetosSelecionados().get(value).get(getMapObjetosSelecionados().get(value).size() - 1))) {
                            descricaoObjeto.append(", ");
                        }
                    }
                    getFiltroRelacaoDebitosAPrescrever().setIdsContribuintes(idsContribuintes);
                    getFiltroRelacaoDebitosAPrescrever().setContribuintes(descricaoObjeto.toString());
                } else if (value.equals(TipoObjetoLista.PROPRIETARIO)) {
                    for (Object o : getMapObjetosSelecionados().get(value)) {
                        idsObjeto.append(((Pessoa) o).getId());
                        descricaoObjeto.append(((Pessoa) o).getNome()).append(" (").append(((Pessoa) o).getCpf_Cnpj()).append(")");
                        if (!o.equals(getMapObjetosSelecionados().get(value).get(getMapObjetosSelecionados().get(value).size() - 1))) {
                            idsObjeto.append(", ");
                            descricaoObjeto.append(", ");
                        }
                    }
                    getFiltroRelacaoDebitosAPrescrever().setIdsProprietarios(idsObjeto.toString());
                    getFiltroRelacaoDebitosAPrescrever().setNomesProprietarios(descricaoObjeto.toString());
                } else if (value.equals(TipoObjetoLista.COMPROMISSARIO)) {
                    for (Object o : getMapObjetosSelecionados().get(value)) {
                        idsObjeto.append(((Pessoa) o).getId());
                        descricaoObjeto.append(((Pessoa) o).getNome()).append(" (").append(((Pessoa) o).getCpf_Cnpj()).append(")");
                        if (!o.equals(getMapObjetosSelecionados().get(value).get(getMapObjetosSelecionados().get(value).size() - 1))) {
                            idsObjeto.append(", ");
                            descricaoObjeto.append(", ");
                        }
                    }
                    getFiltroRelacaoDebitosAPrescrever().setIdsCompromissario(idsObjeto.toString());
                    getFiltroRelacaoDebitosAPrescrever().setNomesCompromissarios(descricaoObjeto.toString());
                } else if (value.equals(TipoObjetoLista.CLASSIFICACAO_ATIVIDADE)) {
                    for (Object o : getMapObjetosSelecionados().get(value)) {
                        idsObjeto.append("'").append(((ClassificacaoAtividade) o).name()).append("'");
                        descricaoObjeto.append(((ClassificacaoAtividade) o).getDescricao());
                        if (!o.equals(getMapObjetosSelecionados().get(value).get(getMapObjetosSelecionados().get(value).size() - 1))) {
                            idsObjeto.append(", ");
                            descricaoObjeto.append(", ");
                        }
                    }
                    getFiltroRelacaoDebitosAPrescrever().setNameClassificacaoAtividade(idsObjeto.toString());
                    getFiltroRelacaoDebitosAPrescrever().setDescricaoClassificacaoAtividade(descricaoObjeto.toString());
                } else if (value.equals(TipoObjetoLista.CNAE)) {
                    for (Object o : getMapObjetosSelecionados().get(value)) {
                        idsObjeto.append(((CNAE) o).getId());
                        descricaoObjeto.append(((CNAE) o).getCodigoCnae());
                        if (!o.equals(getMapObjetosSelecionados().get(value).get(getMapObjetosSelecionados().get(value).size() - 1))) {
                            idsObjeto.append(", ");
                            descricaoObjeto.append(", ");
                        }
                    }
                    getFiltroRelacaoDebitosAPrescrever().setIdsCnaes(idsObjeto.toString());
                    getFiltroRelacaoDebitosAPrescrever().setCodigosCnaes(descricaoObjeto.toString());
                } else if (value.equals(TipoObjetoLista.NATUREZA_JURIDICA)) {
                    for (Object o : getMapObjetosSelecionados().get(value)) {
                        idsObjeto.append(((NaturezaJuridica) o).getId());
                        descricaoObjeto.append(((NaturezaJuridica) o).getDescricao());
                        if (!o.equals(getMapObjetosSelecionados().get(value).get(getMapObjetosSelecionados().get(value).size() - 1))) {
                            idsObjeto.append(", ");
                            descricaoObjeto.append(", ");
                        }
                    }
                    getFiltroRelacaoDebitosAPrescrever().setNamesNaturezaJuridica(idsObjeto.toString());
                    getFiltroRelacaoDebitosAPrescrever().setDescricaoNaturezasJuridicas(descricaoObjeto.toString());
                }
            }
        }

        if (getSituacaoCadastralEconomico() != null) {
            getFiltroRelacaoDebitosAPrescrever().setSituacaoCadastroEconomico(getSituacaoCadastralEconomico().name());
        }
        if (getTipoPrazoPrescricional() != null) {
            getFiltroRelacaoDebitosAPrescrever().setAnoPrazoPrescricional(getTipoPrazoPrescricional().getAno());
            getFiltroRelacaoDebitosAPrescrever().setDescricaoPrazoPrescricional(getTipoPrazoPrescricional().getDescricao());
        }
    }

    public List<SituacaoParcelaWebreportDTO> getTodasAsSituacoes() {
        return Lists.newArrayList(SituacaoParcelaWebreportDTO.values());
    }

    public List<SelectItem> getClassificacoesAtividades() {
        if (classificacoesAtividades == null) {
            classificacoesAtividades = Lists.newArrayList();
            classificacoesAtividades.add(new SelectItem(null, "     "));
            for (ClassificacaoAtividade classificacaoAtividade : ClassificacaoAtividade.values()) {
                classificacoesAtividades.add(new SelectItem(classificacaoAtividade, classificacaoAtividade.getDescricao()));
            }
        }
        return classificacoesAtividades;
    }

    public List<SelectItem> getTiposAutonomos() {
        if (tiposAutonomos == null) {
            tiposAutonomos = Lists.newArrayList();
            tiposAutonomos.add(new SelectItem(null, "     "));
            for (TipoAutonomo tipoAutonomo : tipoAutonomoFacade.lista()) {
                tiposAutonomos.add(new SelectItem(tipoAutonomo, tipoAutonomo.getDescricao()));
            }
        }
        return tiposAutonomos;
    }

    public List<SelectItem> getNaturezasJuridicas() {
        if (naturezasJuridicas == null) {
            naturezasJuridicas = Lists.newArrayList();
            naturezasJuridicas.add(new SelectItem(null, "     "));
            for (NaturezaJuridica naturezaJuridica : naturezaJuridicaFacade.lista()) {
                naturezasJuridicas.add(new SelectItem(naturezaJuridica, naturezaJuridica.getDescricao()));
            }
        }
        return naturezasJuridicas;
    }

    public List<SelectItem> getTiposImovel() {
        if (tiposImovel == null) {
            tiposImovel = Lists.newArrayList();
            tiposImovel.add(new SelectItem(null, "     "));
            for (TipoImovelDTO tipo : TipoImovelDTO.values()) {
                tiposImovel.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return tiposImovel;
    }

    public List<SelectItem> tiposRelatorio() {
        List<SelectItem> itens = Lists.newArrayList();
        for (br.com.webpublico.webreportdto.dto.tributario.TipoRelatorioDTO tipo : br.com.webpublico.webreportdto.dto.tributario.TipoRelatorioDTO.values()) {
            itens.add(new SelectItem(tipo, tipo.toString()));
        }
        return itens;
    }

    private void validarFiltrosUtilizados() {
        ValidacaoException ve = new ValidacaoException();
        FiltroRelacaoDebitosAPrescreverDTO filtroLancamento = filtroRelacaoDebitosAPrescrever;
        if (filtroLancamento.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o Exercício Inicial!");
        }
        if (filtroLancamento.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o Exercício Final!");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataLancamentoInicial(),
            filtroLancamento.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataVencimentoInicial(),
            filtroLancamento.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataMovimentoInicial(),
            filtroLancamento.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataPagamentoInicial(),
            filtroLancamento.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        if (filtroLancamento.getExercicioInicial() != null && filtroLancamento.getExercicioFinal() != null) {
            if (filtroLancamento.getExercicioInicial() > filtroLancamento.getExercicioFinal()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Ano do Processo de Débito Inícial deve ser menor ou igual ao Ano do Processo de Débito Final.");
            }
        }
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (configuracaoTributario.getTributoHabitese() == null) {
            if (filtroLancamento.isEmitirHabitese()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado tributo de habite-se configurado para o IPTU nas configurações do tributário.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "     "));
        for (TipoCadastroTributarioDTO tipo : TipoCadastroTributarioDTO.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getPrazosPrescricionais() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "     "));
        for (TipoPrazoPrescricional tipo : TipoPrazoPrescricional.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getDividas() {
        if (dividas == null) {
            dividas = Lists.newArrayList();
            dividas.add(new SelectItem(null, ""));
            for (Divida divida : dividaFacade.listaDividasOrdenadoPorDescricao()) {
                dividas.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividas;
    }

    public List<SelectItem> getSituacoesCadastroImobiliario() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Todas"));
        retorno.add(new SelectItem(true, "Ativo"));
        retorno.add(new SelectItem(false, "Inativo"));
        return retorno;
    }

    public List<SelectItem> getSituacoesCadastroEconomico() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Todas"));
        for (SituacaoCadastralCadastroEconomico sit : SituacaoCadastralCadastroEconomico.values()) {
            retorno.add(new SelectItem(sit, sit.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getSituacoesPessoa() {
        List<SelectItem> retorno = new ArrayList<>();
        for (SituacaoCadastralPessoaDTO sit : SituacaoCadastralPessoaDTO.values()) {
            retorno.add(new SelectItem(sit, sit.getDescricao()));
        }
        return retorno;
    }

    private void limparSituacaoCadastros() {
        filtroRelacaoDebitosAPrescrever.setSituacaoCadastroImobiliario(null);
        filtroRelacaoDebitosAPrescrever.setSituacaoCadastroEconomico("");
        situacaoCadastralEconomico = null;
        filtroRelacaoDebitosAPrescrever.setSituacaoCadastroRural(Boolean.TRUE);
        filtroRelacaoDebitosAPrescrever.setSituacaoCadastroPessoa("ATIVO");
        filtroRelacaoDebitosAPrescrever.setSituacoes(new SituacaoParcelaWebreportDTO[]{SituacaoParcelaWebreportDTO.EM_ABERTO});
    }

    public void limparCadastroSituacao() {
        limparSituacaoCadastros();
    }

    public List<SelectItem> getTiposDeIssqn() {
        List<SelectItem> tipos = Lists.newArrayList();
        tipos.add(new SelectItem(null, "     "));
        for (TipoIssqnNfseDTO tipo : TipoIssqnNfseDTO.values()) {
            tipos.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return tipos;
    }

    public List<SelectItem> getTiposPorte() {
        List<SelectItem> tipos = Lists.newArrayList();
        tipos.add(new SelectItem(null, "     "));
        for (br.com.webpublico.pessoa.enumeration.TipoPorte porte : br.com.webpublico.pessoa.enumeration.TipoPorte.values()) {
            tipos.add(new SelectItem(porte, porte.getDescricao()));
        }
        return tipos;
    }

    public List<Logradouro> completaLogradouro(String parte) {
        if (getMapObjetosSelecionados().containsKey(TipoObjetoLista.BAIRRO) && !getMapObjetosSelecionados().get(TipoObjetoLista.BAIRRO).isEmpty()) {
            return relacaoDebitosAPrescreverFacade.getLogradouroFacade().completaLogradourosPorBairros(parte, (List) getMapObjetosSelecionados().get(TipoObjetoLista.BAIRRO));
        } else {
            return relacaoDebitosAPrescreverFacade.getLogradouroFacade().listaLogradourosAtivos(parte);
        }
    }

    public void validarEAdicionaObjetos(String tipoObjeto) {

        /** Se o getObjeto() for do tipo PESSOA ou ENUM, deve informar qual a finalidade da Pessoa ou o tipo do ENUM através do param tipoObjeto **/
        try {
            ValidacaoException ve = new ValidacaoException();

            if (getObjeto().getClass() == Bairro.class) {
                if (getMapObjetosSelecionados().containsKey(TipoObjetoLista.BAIRRO)) {
                    if (getMapObjetosSelecionados().get(TipoObjetoLista.BAIRRO).contains(getObjeto())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Esse bairro já foi adicionado.");
                    }
                } else {
                    getMapObjetosSelecionados().put(TipoObjetoLista.BAIRRO, Lists.newArrayList());
                }
                ve.lancarException();
                getMapObjetosSelecionados().get(getEnumTipoObjetoPelaClasse(getObjeto().getClass())).add(getObjeto());
            } else if (getObjeto().getClass() == Logradouro.class) {
                if (getMapObjetosSelecionados().containsKey(TipoObjetoLista.LOGRADOURO)) {
                    if (getMapObjetosSelecionados().get(TipoObjetoLista.LOGRADOURO).contains(getObjeto())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Esse logradouro já foi adicionado.");
                    }
                } else {
                    getMapObjetosSelecionados().put(TipoObjetoLista.LOGRADOURO, Lists.newArrayList());
                }
                ve.lancarException();
                getMapObjetosSelecionados().get(getEnumTipoObjetoPelaClasse(getObjeto().getClass())).add(getObjeto());
            } else if (getObjeto().getClass() == NaturezaJuridica.class) {
                if (getMapObjetosSelecionados().containsKey(TipoObjetoLista.NATUREZA_JURIDICA)) {
                    if (getMapObjetosSelecionados().get(TipoObjetoLista.NATUREZA_JURIDICA).contains(getObjeto())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Essa natureza jurídica já foi adicionada.");
                    }
                } else {
                    getMapObjetosSelecionados().put(TipoObjetoLista.NATUREZA_JURIDICA, Lists.newArrayList());
                }
                ve.lancarException();
                getMapObjetosSelecionados().get(getEnumTipoObjetoPelaClasse(getObjeto().getClass())).add(getObjeto());
            } else if (getObjeto().getClass() == TipoAutonomo.class) {
                if (getMapObjetosSelecionados().containsKey(TipoObjetoLista.TIPO_AUTONOMO)) {
                    if (getMapObjetosSelecionados().get(TipoObjetoLista.TIPO_AUTONOMO).contains(getObjeto())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Esse Tipo de Autônomo já foi adicionado.");
                    }
                } else {
                    getMapObjetosSelecionados().put(TipoObjetoLista.TIPO_AUTONOMO, Lists.newArrayList());
                }
                ve.lancarException();
                getMapObjetosSelecionados().get(getEnumTipoObjetoPelaClasse(getObjeto().getClass())).add(getObjeto());
            } else if ((getObjeto().getClass() == PessoaJuridica.class || getObjeto().getClass() == PessoaFisica.class) && TipoObjetoLista.CONTRIBUINTE.name().equals(tipoObjeto.toUpperCase())) {
                if (getMapObjetosSelecionados().containsKey(TipoObjetoLista.CONTRIBUINTE)) {
                    if (getMapObjetosSelecionados().get(TipoObjetoLista.CONTRIBUINTE).contains(getObjeto())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Esse contribuínte já foi adicionado.");
                    }
                } else {
                    getMapObjetosSelecionados().put(TipoObjetoLista.CONTRIBUINTE, Lists.newArrayList());
                }
                ve.lancarException();
                getMapObjetosSelecionados().get(TipoObjetoLista.valueOf(tipoObjeto.toUpperCase())).add(getObjeto());
            } else if ((getObjeto().getClass() == PessoaJuridica.class || getObjeto().getClass() == PessoaFisica.class) && TipoObjetoLista.PROPRIETARIO.name().equals(tipoObjeto.toUpperCase())) {
                if (getMapObjetosSelecionados().containsKey(TipoObjetoLista.PROPRIETARIO)) {
                    if (getMapObjetosSelecionados().get(TipoObjetoLista.PROPRIETARIO).contains(getObjeto())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Esse proprietário já foi adicionado.");
                    }
                } else {
                    getMapObjetosSelecionados().put(TipoObjetoLista.PROPRIETARIO, Lists.newArrayList());
                }
                ve.lancarException();
                getMapObjetosSelecionados().get(TipoObjetoLista.valueOf(tipoObjeto.toUpperCase())).add(getObjeto());
            } else if ((getObjeto().getClass() == PessoaJuridica.class || getObjeto().getClass() == PessoaFisica.class) && TipoObjetoLista.COMPROMISSARIO.name().equals(tipoObjeto.toUpperCase())) {
                if (getMapObjetosSelecionados().containsKey(TipoObjetoLista.COMPROMISSARIO)) {
                    if (getMapObjetosSelecionados().get(TipoObjetoLista.COMPROMISSARIO).contains(getObjeto())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Esse compromissário já foi adicionado.");
                    }
                } else {
                    getMapObjetosSelecionados().put(TipoObjetoLista.COMPROMISSARIO, Lists.newArrayList());
                }
                ve.lancarException();
                getMapObjetosSelecionados().get(TipoObjetoLista.valueOf(tipoObjeto.toUpperCase())).add(getObjeto());
            } else if (getObjeto().getClass() == Divida.class) {
                if (getMapObjetosSelecionados().containsKey(TipoObjetoLista.DIVIDA)) {
                    if (getMapObjetosSelecionados().get(TipoObjetoLista.DIVIDA).contains(getObjeto())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Essa dívida já foi adicionada.");
                    }
                } else {
                    getMapObjetosSelecionados().put(TipoObjetoLista.DIVIDA, Lists.newArrayList());
                }
                ve.lancarException();
                getMapObjetosSelecionados().get(getEnumTipoObjetoPelaClasse(getObjeto().getClass())).add(getObjeto());
            } else if (tipoObjeto.toUpperCase().equals(TipoObjetoLista.CLASSIFICACAO_ATIVIDADE.name())) {
                if (getMapObjetosSelecionados().containsKey(TipoObjetoLista.CLASSIFICACAO_ATIVIDADE)) {
                    if (getMapObjetosSelecionados().get(TipoObjetoLista.CLASSIFICACAO_ATIVIDADE).contains(ClassificacaoAtividade.valueOf(getObjeto().toString()))) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Essa classificação de atividade já foi adicionada.");
                    }
                } else {
                    getMapObjetosSelecionados().put(TipoObjetoLista.CLASSIFICACAO_ATIVIDADE, Lists.newArrayList());
                }
                ve.lancarException();
                getMapObjetosSelecionados().get(TipoObjetoLista.CLASSIFICACAO_ATIVIDADE).add(ClassificacaoAtividade.valueOf(getObjeto().toString()));
            } else if (getObjeto().getClass() == CNAE.class) {
                if (getMapObjetosSelecionados().containsKey(TipoObjetoLista.CNAE)) {
                    if (getMapObjetosSelecionados().get(TipoObjetoLista.CNAE).contains(getObjeto())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Esse cnae já foi adicionado.");
                    }
                } else {
                    getMapObjetosSelecionados().put(TipoObjetoLista.CNAE, Lists.newArrayList());
                }
                ve.lancarException();
                getMapObjetosSelecionados().get(getEnumTipoObjetoPelaClasse(getObjeto().getClass())).add(getObjeto());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            setObjeto(null);
        }
    }

    public void removerObjetos(Object obj, String tipoObjeto) {
        if (!tipoObjeto.isEmpty()) {
            /** Se o getObjeto() for do tipo PESSOA ou ENUM, deve informar qual a finalidade da Pessoa ou o tipo do ENUM através do param tipoObjeto **/
            getMapObjetosSelecionados().get(TipoObjetoLista.valueOf(tipoObjeto)).remove(obj);
        } else {
            getMapObjetosSelecionados().get(getEnumTipoObjetoPelaClasse(obj.getClass())).remove(obj);
        }
    }

    TipoObjetoLista getEnumTipoObjetoPelaClasse(Class classe) {
        for (TipoObjetoLista value : TipoObjetoLista.values()) {
            if (value.getClasse().equals(classe)) {
                return value;
            }
        }
        return null;
    }

    private enum TipoObjetoLista {

        BAIRRO(Bairro.class),
        LOGRADOURO(Logradouro.class),
        TIPO_AUTONOMO(TipoAutonomo.class),
        DIVIDA(Divida.class),
        CONTRIBUINTE(Pessoa.class),
        PROPRIETARIO(Pessoa.class),
        COMPROMISSARIO(Pessoa.class),
        CLASSIFICACAO_ATIVIDADE(ClassificacaoAtividade.class),
        CNAE(CNAE.class),
        NATUREZA_JURIDICA(NaturezaJuridica.class);

        TipoObjetoLista(Class classe) {
            this.classe = classe;
        }

        private Class classe;

        public Class getClasse() {
            return classe;
        }

        public void setClasse(Class classe) {
            this.classe = classe;
        }
    }
}
