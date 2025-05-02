/*
 * Codigo gerado automaticamente em Sun Nov 27 17:13:06 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotas;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.VeiculoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "veiculoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "veiculoNovo", pattern = "/frota/veiculo/novo/", viewId = "/faces/administrativo/frota/veiculos/edita.xhtml"),
    @URLMapping(id = "veiculoListar", pattern = "/frota/veiculo/listar/", viewId = "/faces/administrativo/frota/veiculos/lista.xhtml"),
    @URLMapping(id = "veiculoEditar", pattern = "/frota/veiculo/editar/#{veiculoControlador.id}/", viewId = "/faces/administrativo/frota/veiculos/edita.xhtml"),
    @URLMapping(id = "veiculoVer", pattern = "/frota/veiculo/ver/#{veiculoControlador.id}/", viewId = "/faces/administrativo/frota/veiculos/visualizar.xhtml"),
})
public class VeiculoControlador extends PrettyControlador<Veiculo> implements Serializable, CRUD {

    @EJB
    private VeiculoFacade facade;
    private ProgramaRevisaoVeiculo programaRevisaoVeiculo;
    private UnidadeObjetoFrota unidadeObjetoFrota;
    private ConverterAutoComplete converterVeiculo;
    private ConverterAutoComplete converterProgramaRevisao;

    public VeiculoControlador() {
        super(Veiculo.class);
    }

    public VeiculoFacade getFacade() {
        return facade;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/veiculo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "veiculoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarEntidade();
    }

    @URLAction(mappingId = "veiculoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        Util.ordenarListas(selecionado.getKmPercorridos());
        recuperarHierarquiaResponsavel();
    }

    @URLAction(mappingId = "veiculoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        Util.ordenarListas(selecionado.getKmPercorridos());
        recuperarHierarquiaResponsavel();
    }

    public ConverterAutoComplete getConverterVeiculo() {
        if (converterVeiculo == null) {
            converterVeiculo = new ConverterAutoComplete(Veiculo.class, facade);
        }
        return converterVeiculo;
    }

    public ConverterAutoComplete getConverterProgramaRevisao() {
        if (converterProgramaRevisao == null) {
            converterProgramaRevisao = new ConverterAutoComplete(ProgramaRevisaoVeiculo.class, facade.getProgramaRevisaoVeiculoFacade());
        }
        return converterProgramaRevisao;
    }

    public void validatorVeiculo(Veiculo veiculo) {
        veiculo = facade.recuperar(veiculo.getId());
        if (veiculo != null) {
            verificarVencimentoDasRevisoes(veiculo);
            verificarTaxasDoVeiculo(veiculo);
        }
    }

    private void verificarTaxasDoVeiculo(Veiculo veiculo) throws ValidatorException {
        Date dataAtual = facade.getSistemaFacade().getDataOperacao();
        List<LancamentoTaxaVeiculo> taxas = facade.getLancamentoTaxaVeiculoFacade().taxasDoVeiculo(veiculo);
        long diferencaEmDias = 0;
        if (taxas != null) {
            for (LancamentoTaxaVeiculo taxa : taxas) {
                for (ItemLancamentoTaxaVeiculo item : taxa.getItensLancamentoTaxaVeiculo()) {
                    diferencaEmDias = Util.diferencaDeDiasEntreData(dataAtual, item.getDataVencimento());
                    if (!item.getFoiPaga() && diferencaEmDias <= 15) {
                        FacesUtil.addAtencao("O veículo " + veiculo + " possui taxas vencidas ou a vencer.");
                    }
                }
            }
        }
    }

    private void verificarVencimentoDasRevisoes(Veiculo veiculo) throws ValidatorException {
        ParametroFrotas parametroFrotas = facade.getParametroFrotasFacade().buscarParametroFrotas();
        if (parametroFrotas == null) {
            return;
        }
        long diasProximoAVencer = parametroFrotas.getDiasDaRevisaoAVencer();
        BigDecimal kmProximoAVencer = parametroFrotas.getQuilometrosDaRevisaoAVencer();
        Date dataAquisicao = veiculo.getDataAquisicao();
        Date dataAtual = facade.getSistemaFacade().getDataOperacao();
        long diasDeUso = Util.diferencaDeDiasEntreData(dataAquisicao, dataAtual);

        if (veiculo.getProgramaRevisao() != null && !veiculo.getProgramaRevisao().isEmpty()) {
            for (ProgramaRevisaoVeiculo programa : veiculo.getProgramaRevisao()) {
                if (!programa.getRevisaoRealizada()) {
                    boolean isParaNotificarRevisaoVencendo = false;
                    long diferencaPrazoDias = (programa.getPrazo().longValue() * 30) - diasDeUso;
                    if ((programa.getPrazo().longValue() * 30) > diasDeUso && diferencaPrazoDias <= diasProximoAVencer) {
                        isParaNotificarRevisaoVencendo = true;
                    }
                    BigDecimal diferencaPrazoKm = veiculo.getKmAquisicao().add(programa.getKm()).subtract(veiculo.getKmPercorrido().getKmAtual());
                    if (diferencaPrazoKm.compareTo(BigDecimal.ZERO) > 0 && diferencaPrazoKm.compareTo(kmProximoAVencer) <= 0) {
                        isParaNotificarRevisaoVencendo = true;
                    }
                    if (isParaNotificarRevisaoVencendo) {
                        FacesUtil.addAtencao("O veículo " + programa.getVeiculo() + " possui revisão(ões) próxima(s) do vencimento.");
                    }
                }
            }
        }
    }

    public List<Veiculo> completaVeiculo(String parte) {
        return facade.buscarVeiculosPorPlacaOrDescricaoOrIdentificacaoOrNumeroProcessoContrato(parte.trim());
    }

    public List<TaxaVeiculo> completarTaxa(String parte) {
        return facade.getTaxaVeiculoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<Bem> completarBem(String parte) {
        List<Bem> list = Lists.newArrayList();
        if (selecionado.getHierarquiaOrganizacional() != null) {
            list = facade.getBemFacade().buscarBensUnidadeAdministrativaPorGrupoPatrimonialVigenteNoParametroFrota(parte.trim(), selecionado.getHierarquiaOrganizacional(), selecionado.getTipoObjetoFrota());
            if (list.isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("Nenhum bem encontrado para a Órgão/Entidade/Fundo: " + selecionado.getHierarquiaOrganizacional() + ".");
                return list;
            }
        }
        return list;
    }

    private void verificarSeExisteParametroFrotasComGrupoPatrimonial() {
        ValidacaoException ve = new ValidacaoException();
        if (!facade.getParametroFrotasFacade().verificarSeExisteConfiguracaoVigenteParaGrupoPatrimonial(TipoObjetoFrota.VEICULO)) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Não foi encontrada/não está vigente, nenhuma Configuração de Grupo Patrimonial do Tipo Veículo no Parâmetro do Frotas.");
        }
        ve.lancarException();
    }

    private void inicializarEntidade() {
        selecionado.setKmAquisicao(BigDecimal.ZERO);
        selecionado.setKmAtual(BigDecimal.ZERO);
        selecionado.setKmFimGarantia(BigDecimal.ZERO);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            atribuirDataDeAquisicaoAoVeiculo();
            atualizarUnidadeResponsavel();
            facade.salvarNovo(selecionado);
            FacesUtil.addOperacaoRealizada("Registro Salvo com Sucesso");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void atualizarUnidadeResponsavel() {
        if (selecionado.getHierarquiaOrganizacionalResponsavel() != null) {
            selecionado.setUnidadeOrganizacionalResp(selecionado.getHierarquiaOrganizacionalResponsavel().getSubordinada());
        }
    }

    private void recuperarHierarquiaResponsavel() {
        if (selecionado.getUnidadeOrganizacionalResp() != null) {
            selecionado.setHierarquiaOrganizacionalResponsavel(
                facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(
                    selecionado.getDataAquisicao() != null ? selecionado.getDataAquisicao() : facade.getSistemaFacade().getDataOperacao(),
                    selecionado.getUnidadeOrganizacionalResp(),
                    TipoHierarquiaOrganizacional.ADMINISTRATIVA
                    )
            );
        }
    }

    private void atribuirDataDeAquisicaoAoVeiculo() {
        if (isTipoAquisicaoObjetoFrotaProprio()) {
            if (selecionado.getBem() != null && selecionado.getBem().getDataAquisicao() != null) {
                selecionado.setDataAquisicao(selecionado.getBem().getDataAquisicao());
            }
        }
    }

    private void validarCategoriaCaminhao() {
        ValidacaoException ve = new ValidacaoException();

        if (CategoriaVeiculoObjetoFrota.CAMINHAO.equals(getSelecionado().getCategoria())
            && getSelecionado().getTipoCaminhao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" Por favor informar o tipo de caminhão.");
        }
        ve.lancarException();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão/Entidade/Fundo deve ser informado.");
        }

        if (Strings.isNullOrEmpty(selecionado.getIdentificacao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Identificação deve ser informado.");
        }

        if (selecionado.getCategoria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Categoria deve ser informado.");
        }

        if (!selecionado.isCedido() && isTipoAquisicaoObjetoFrotaProprio() && selecionado.getBem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Bem Patrimonial deve ser informado.");
        }

        if (!selecionado.isCedido() && isTipoAquisicaoObjetoFrotaAlugado() && selecionado.getContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contrato deve ser informado.");
        }

        if ((selecionado.isCedido() || isTipoAquisicaoObjetoFrotaAlugado()) && Strings.isNullOrEmpty(selecionado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }

        if (selecionado.isCedido() && selecionado.getDataAquisicao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Aquisição deve ser informado.");
        }

        if ((!selecionado.isCedido()) && selecionado.getTipoAquisicaoObjetoFrota() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Frota deve ser informado.");
        }

        if (Strings.isNullOrEmpty(selecionado.getPlaca())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Placa deve ser informado.");
        }

        if (selecionado.getAnoFabricacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano de Fabricação deve ser informado.");
        }

        if (selecionado.getCor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cor deve ser informado.");
        }

        if (selecionado.getKmAquisicao() == null || selecionado.getKmAquisicao().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Km Aquisição/Contratação deve ser informado.");
        }

        if (selecionado.getKmAtual() == null || selecionado.getKmAtual().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Km Atual deve ser informado.");
        }
        if (selecionado.getUnidades().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O veículo não possui unidade adicionada.");
        }
        ve.lancarException();
        validarCategoriaCaminhao();
        validarRegrasEspecificas();
        ve.lancarException();
    }

    public void limparDadosDoBem() {
        selecionado.setBem(null);
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado != null) {
            if (selecionado.getKmAquisicao() != null && selecionado.getKmAquisicao().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quilometragem de Aquisição/Contratação deve ser informada com um valor maior ou igual a 0(zero).");
            }
            if (selecionado.getKmAtual() != null && selecionado.getKmAtual().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quilometragem Atual deve ser informada com um valor maior ou igual a 0(zero).");
            }
            ve.lancarException();
            if (selecionado.getKmAquisicao().compareTo(selecionado.getKmAtual()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quilometragem de Aquisição/Contratação não pode ser superior à atual");
            }
            if (selecionado.getHierarquiaOrganizacional() != null) {
                selecionado.setUnidadeOrganizacional(selecionado.getHierarquiaOrganizacional().getSubordinada());
                if (facade.identificacaoRegistrada(selecionado)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um veículo cadastrado com a identificação: " + selecionado.getIdentificacao() + ".");
                }
            }
            ve.lancarException();
            convertePlacaMaiuscula();
        }
    }

    public boolean isTipoAquisicaoObjetoFrotaProprio() {
        return TipoAquisicaoObjetoFrota.PROPRIO.equals(selecionado.getTipoAquisicaoObjetoFrota());
    }

    public Boolean isTipoAquisicaoObjetoFrotaAlugado() {
        return TipoAquisicaoObjetoFrota.ALUGADO.equals(selecionado.getTipoAquisicaoObjetoFrota());
    }

    private void convertePlacaMaiuscula() {
        if (selecionado != null) {
            if (selecionado.getPlaca() != null) {
                selecionado.setPlaca(selecionado.getPlaca().toUpperCase());
            }
        }
    }

    public ProgramaRevisaoVeiculo getProgramaRevisaoVeiculo() {
        return programaRevisaoVeiculo;
    }

    public void setProgramaRevisaoVeiculo(ProgramaRevisaoVeiculo programaRevisaoVeiculo) {
        this.programaRevisaoVeiculo = programaRevisaoVeiculo;
    }

    private boolean validaInformacoesProgramacaoRevisaoVeiculo(ProgramaRevisaoVeiculo programaRevisaoVeiculo) {
        boolean retorno = true;
        if (programaRevisaoVeiculo.getKm().compareTo(BigDecimal.ZERO) <= 0) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("A quilometragem deve ser informada com um valor maior que 0(zero).");
        }
        if (programaRevisaoVeiculo.getPrazo() <= 0) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("O prazo deve ser informado com um valor maior que 0(zero).");
        }
        return retorno;
    }

    public void novaUnidade() {
        unidadeObjetoFrota = new UnidadeObjetoFrota();
        unidadeObjetoFrota.setObjetoFrota(selecionado);
    }

    public void cancelarUnidade() {
        unidadeObjetoFrota = null;
    }

    public void adicionarUnidade() {
        try {
            Util.validarCampos(unidadeObjetoFrota);
            validarVigencia();
            selecionado.setHierarquiaOrganizacional(unidadeObjetoFrota.getHierarquiaOrganizacional());
            selecionado.setUnidadeOrganizacionalResp(null);
            selecionado.setHierarquiaOrganizacionalResponsavel(null);
            Util.adicionarObjetoEmLista(selecionado.getUnidades(), unidadeObjetoFrota);
            if (isTipoAquisicaoObjetoFrotaProprio()) {
                limparDadosDoBem();
            }
            cancelarUnidade();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarVigencia() {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeObjetoFrota.getFimVigencia() != null && unidadeObjetoFrota.getFimVigencia().before(unidadeObjetoFrota.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O fim de vigência deve ser posterior ao início de vigência.");
        }
        ve.lancarException();
        for (UnidadeObjetoFrota unidadeAdicionada : selecionado.getUnidades()) {
            if (!unidadeAdicionada.equals(unidadeObjetoFrota)) {
                unidadeAdicionada.validarVigencia(unidadeObjetoFrota);
            }
        }
    }

    public void editarUnidade(UnidadeObjetoFrota obj) {
        this.unidadeObjetoFrota = obj;
    }

    public void removerUnidade(UnidadeObjetoFrota obj) {
        for (UnidadeObjetoFrota unidade : selecionado.getUnidades()) {
            if (!unidade.equals(obj)) {
                if (unidade.getFimVigencia() == null || Util.getDataHoraMinutoSegundoZerado(unidade.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(facade.getSistemaFacade().getDataOperacao())) >= 0) {
                    selecionado.setHierarquiaOrganizacional(unidade.getHierarquiaOrganizacional());
                }
            } else {
                selecionado.setHierarquiaOrganizacional(null);
                selecionado.setUnidadeOrganizacional(null);
            }
        }
        limparDadosDoBem();
        selecionado.setUnidadeOrganizacionalResp(null);
        selecionado.setHierarquiaOrganizacionalResponsavel(null);
        selecionado.getUnidades().remove(obj);
    }

    public void novoProgramaRevisao() {
        programaRevisaoVeiculo = new ProgramaRevisaoVeiculo();
        programaRevisaoVeiculo.setSequencia(0);
        programaRevisaoVeiculo.setPrazo(0);
        programaRevisaoVeiculo.setDataLancamento(new Date());
        programaRevisaoVeiculo.setRevisaoRealizada(Boolean.FALSE);
        programaRevisaoVeiculo.setKm(BigDecimal.ZERO);
    }

    public void cancelarProgramaRevisao() {
        programaRevisaoVeiculo = null;
    }

    public void adicionarProgramaRevisaoVeiculo() {
        if (validaInformacoesProgramacaoRevisaoVeiculo(programaRevisaoVeiculo)) {
            if (selecionado.getProgramaRevisao() == null) {
                selecionado.setProgramaRevisao(new ArrayList());
            }
            programaRevisaoVeiculo.setVeiculo(selecionado);
            selecionado.getProgramaRevisao().add(programaRevisaoVeiculo);
            programaRevisaoVeiculo.organizaSequenciaProgramaRevisao(selecionado);
            cancelarProgramaRevisao();
        }
    }

    public void removeProgramaRevisaoVeiculo(ProgramaRevisaoVeiculo programaRevisaoVeiculo) {
        selecionado.getProgramaRevisao().remove(programaRevisaoVeiculo);
        programaRevisaoVeiculo.organizaSequenciaProgramaRevisao(selecionado);
    }

    public List<SelectItem> tipos() {
        return Util.getListSelectItem(Arrays.asList(TipoAquisicaoObjetoFrota.values()));
    }

    public void processaMudancaDeTipo() {
        try {
            selecionado.setBem(null);
            selecionado.setContrato(null);
            selecionado.setDataAquisicao(null);
            selecionado.setDescricao(null);
            if (isTipoAquisicaoObjetoFrotaProprio()) {
                verificarSeExisteParametroFrotasComGrupoPatrimonial();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void processaSelecaoContrato() {
        if (selecionado.getContrato() != null) {
            selecionado.setDataAquisicao(selecionado.getContrato().getInicioVigencia());
        } else {
            selecionado.setDataAquisicao(null);
        }

    }

    public List<SelectItem> categorias() {
        return Util.getListSelectItem(Arrays.asList(CategoriaVeiculoObjetoFrota.values()));
    }

    public List<SelectItem> getTiposCaminhao() {
        return Util.getListSelectItem(TipoCaminhao.values(), true);
    }

    public void processarSelecaoCedido() {
        selecionado.setCedidoPor(null);
        selecionado.setTipoAquisicaoObjetoFrota(null);
        selecionado.setContrato(null);
        selecionado.setBem(null);
        selecionado.setDescricao(null);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalFrotasFilhas(String parte) {
        if (selecionado.getHierarquiaOrganizacional() != null) {
            return facade.getHierarquiaOrganizacionalFacade().listaHierarquiasFilhasDeUmaHierarquiaOrganizacionalAdministrativa(
                selecionado.getHierarquiaOrganizacional(),
                facade.getSistemaFacade().getUsuarioCorrente(),
                facade.getSistemaFacade().getDataOperacao(),
                parte.trim());
        }
        return new ArrayList();
    }

    public UnidadeObjetoFrota getUnidadeObjetoFrota() {
        return unidadeObjetoFrota;
    }

    public void setUnidadeObjetoFrota(UnidadeObjetoFrota unidadeObjetoFrota) {
        this.unidadeObjetoFrota = unidadeObjetoFrota;
    }
}
