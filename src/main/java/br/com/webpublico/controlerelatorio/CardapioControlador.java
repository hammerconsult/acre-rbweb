package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ComposicaoNutricionalVO;
import br.com.webpublico.entidadesauxiliares.ContratoCardapioMaterialVO;
import br.com.webpublico.entidadesauxiliares.ContratoCardapioVO;
import br.com.webpublico.enums.SituacaoCardapio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CardapioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-cardapio", pattern = "/cardapio/novo/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio/edita.xhtml"),
    @URLMapping(id = "editar-cardapio", pattern = "/cardapio/editar/#{cardapioControlador.id}/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio/edita.xhtml"),
    @URLMapping(id = "listar-cardapio", pattern = "/cardapio/listar/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio/lista.xhtml"),
    @URLMapping(id = "ver-cardapio", pattern = "/cardapio/ver/#{cardapioControlador.id}/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio/visualizar.xhtml"),
})
public class CardapioControlador extends PrettyControlador<Cardapio> implements Serializable, CRUD {

    @EJB
    private CardapioFacade facade;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private CardapioAgenda diaAgenda;
    private CardapioAgendaRefeicao refeicaoSelecionada;
    private Material material;
    private String CRN;
    private List<ContratoCardapioVO> contratos;
    private Map<Date, Feriado> mapDiaFeriado;
    private Map<Integer, ComposicaoNutricionalVO> mapComposicaoNutricionalPorSemana;
    private boolean isFeriadoIncluso = false;
    private boolean isSabadoIncluso = false;

    public CardapioControlador() {
        super(Cardapio.class);
    }

    @Override
    @URLAction(mappingId = "novo-cardapio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataCadastro(facade.getSistemaFacade().getDataOperacao());
        diasPorSemana = Maps.newHashMap();
    }

    @Override
    @URLAction(mappingId = "ver-cardapio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        recuperarCRN();
        recuperarRequisicoesCompra();
    }

    @Override
    @URLAction(mappingId = "editar-cardapio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        atribuirHierarquiaAdministrativa();
        atribuirHierarquiaOrcamentaria();
        diasPorSemana = Maps.newHashMap();
        mapDiaFeriado = Maps.newHashMap();
        popularMapaPorSemana();
        novaComposicaoNutricionalVO();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cardapio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        validarCampos();
        return super.validaRegrasEspecificas();
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (SituacaoCardapio object : SituacaoCardapio.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<ProgramaAlimentacao> completarProgramaAlimentacao(String parte) {
        return facade.getProgramaAlimentacaoFacade().buscarPorNome(parte.trim());
    }

    public List<PessoaFisica> completarNutricionista(String parte) {
        return facade.getPessoaFisicaFacade().buscarNutricionistas(parte.trim());
    }

    public List<HierarquiaOrganizacional> completarHierarquiasAdministrativas(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaFiltrandoTipoAdministrativaAndHierarquiaSemRaiz(parte.trim());
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrcamentarias(String parte) {
        if (hierarquiaAdministrativa != null) {
            return facade.getHierarquiaOrganizacionalFacade().buscarFiltrandoHierarquiaOrcamentariaPorUnidadeAdministrativa(parte.trim(), getHierarquiaAdministrativa().getSubordinada(), facade.getSistemaFacade().getDataOperacao());
        }
        return Lists.newArrayList();
    }

    public List<Refeicao> completarRefeicoes(String parte) {
        if (selecionado.getProgramaAlimentacao().getPublicoAlvo() == null) {
            return facade.getRefeicaoFacade().buscarRefeicao(parte.trim());
        }
        return facade.getRefeicaoFacade().buscarRefeicaoPorPublicoAlvo(parte.trim(), selecionado.getProgramaAlimentacao().getPublicoAlvo());
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        if (hierarquiaAdministrativa != null) {
            selecionado.setUnidadeAdministrativa(hierarquiaAdministrativa.getSubordinada());
        }
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        if (hierarquiaAdministrativa != null && hierarquiaOrcamentaria != null) {
            selecionado.setUnidadeOrcamentaria(hierarquiaOrcamentaria.getSubordinada());
        }
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    private void atribuirHierarquiaAdministrativa() {
        hierarquiaAdministrativa = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataCadastro(), selecionado.getUnidadeAdministrativa(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    private void atribuirHierarquiaOrcamentaria() {
        hierarquiaOrcamentaria = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataCadastro(), selecionado.getUnidadeOrcamentaria(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataInicial() != null && selecionado.getDataFinal() != null) {
            if (selecionado.getDataFinal().before(selecionado.getDataInicial())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data final deve ser posterior a inicial.");
            }
        }
        if (selecionado.getDiasSemana() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor, preencha os campos da Agenda.");
        }

        if (selecionado.getDiasSemana() != null) {
            for (CardapioAgenda agenda : selecionado.getDiasSemana()) {
                if (agenda.getDiaSemana() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Data deve ser preenchido em todos os dias da Agenda.");
                }
                for (CardapioAgendaRefeicao ref : agenda.getRefeicoes()) {
                    if (ref.getRefeicao() == null) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar uma refeição vazia.");
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void atribuirTextoFormatadoDescricao() {
        String textoDescricao = "";
        if (selecionado != null) {
            if (selecionado.getProgramaAlimentacao() != null) {
                textoDescricao = selecionado.getProgramaAlimentacao().getNome() + " - ";
                selecionado.setDescricao(textoDescricao);
            }
            if (hierarquiaAdministrativa != null) {
                textoDescricao = textoDescricao + hierarquiaAdministrativa + " - ";
                selecionado.setDescricao(textoDescricao);
            }
            if (hierarquiaOrcamentaria != null) {
                textoDescricao = textoDescricao + hierarquiaOrcamentaria + " - ";
                selecionado.setDescricao(textoDescricao);
            }
            if (selecionado.getNutricionista() != null) {
                textoDescricao = textoDescricao + "NUTRICIONISTA: " + selecionado.getNutricionista().getNome() + " - " + getCRN() + " - ";
                selecionado.setDescricao(textoDescricao);
            }
            if (selecionado.getDataInicial() != null) {
                textoDescricao = textoDescricao + DataUtil.getDataFormatada(selecionado.getDataInicial()) + " a ";
                selecionado.setDescricao(textoDescricao);
            }
            if (selecionado.getDataFinal() != null) {
                textoDescricao = textoDescricao + DataUtil.getDataFormatada(selecionado.getDataFinal());
                selecionado.setDescricao(textoDescricao);
            }
        }
    }

    public void listenerNutricionista() {
        recuperarCRN();
        atribuirTextoFormatadoDescricao();
    }

    public String getCRN() {
        return CRN;
    }

    public void setCRN(String CRN) {
        this.CRN = CRN;
    }

    public void recuperarCRN() {
        CRN = facade.getPessoaFisicaFacade().recuperarCRN(selecionado.getNutricionista().getId());
    }

    public CardapioAgenda getDiaAgenda() {
        return diaAgenda;
    }

    public void setDiaAgenda(CardapioAgenda diaAgenda) {
        this.diaAgenda = diaAgenda;
    }

    Map<Integer, List<CardapioAgenda>> diasPorSemana;

    public Map<Integer, List<CardapioAgenda>> getDiasPorSemana() {
        return diasPorSemana;
    }

    public List<Integer> getSemanas() {
        return Lists.newArrayList(diasPorSemana.keySet());
    }

    private void recuperarRequisicoesCompra() {
        for (CardapioRequisicaoCompra req : selecionado.getRequisicoesCompra()) {
            req.setRequisicaoCompra(facade.getRequisicaoDeCompraFacade().recuperarComDependenciasItens(req.getRequisicaoCompra().getId()));
        }
    }

    public void popularMapaPorSemana() {
        for (CardapioAgenda cardapioAgenda : selecionado.getDiasSemana()) {
            int numSemana = new DateTime(cardapioAgenda.getDiaSemana()).weekOfWeekyear().get();
            if (!diasPorSemana.containsKey(numSemana)) {
                diasPorSemana.put(numSemana, Lists.newArrayList());
            }
            diasPorSemana.get(numSemana).add(cardapioAgenda);
            preencherMapDiaFeriado(cardapioAgenda);
        }
    }

    public void criarAgenda() {
        try {
            mapDiaFeriado = new HashMap<>();
            selecionado.validarCamposObrigatorios();
            DateTime inicio = new DateTime(selecionado.getDataInicial());

            while (inicio.isBefore(new DateTime(selecionado.getDataFinal()))
                || inicio.isEqual(new DateTime(selecionado.getDataFinal()))) {

                if (!isSabadoIncluso) {
                    if (!DataUtil.isSabadoDomingo(inicio.toDate())) {
                        diaAgenda = new CardapioAgenda();
                        diaAgenda.setCardapio(selecionado);
                        diaAgenda.setDiaSemana(inicio.toDate());

                        if (!isFeriadoIncluso) {
                            preencherMapDiaFeriado(diaAgenda);
                        }
                        diaAgenda.setRefeicoes(Lists.newArrayList());
                        addDiaAgenda();
                        int key = inicio.weekOfWeekyear().get();
                        if (!diasPorSemana.containsKey(key)) {
                            diasPorSemana.put(key, Lists.newArrayList());
                        }
                        diasPorSemana.get(key).add(diaAgenda);
                    }
                } else {
                    if (!DataUtil.isDomingo(inicio.toDate())) {
                        diaAgenda = new CardapioAgenda();
                        diaAgenda.setCardapio(selecionado);
                        diaAgenda.setDiaSemana(inicio.toDate());

                        if (!isFeriadoIncluso) {
                            preencherMapDiaFeriado(diaAgenda);
                        }
                        diaAgenda.setRefeicoes(Lists.newArrayList());
                        addDiaAgenda();
                        int key = inicio.weekOfWeekyear().get();
                        if (!diasPorSemana.containsKey(key)) {
                            diasPorSemana.put(key, Lists.newArrayList());
                        }
                        diasPorSemana.get(key).add(diaAgenda);
                    }
                }
                inicio = inicio.plusDays(1);
            }
            novaComposicaoNutricionalVO();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void novaComposicaoNutricionalVO() {
        mapComposicaoNutricionalPorSemana = Maps.newHashMap();
        for (Integer semana : getSemanas()) {
            ComposicaoNutricionalVO cn = new ComposicaoNutricionalVO();
            if (selecionado.getProgramaAlimentacao().getComposicaoNutricional() != null) {
                cn.setEnergiaKCALPrograma(selecionado.getProgramaAlimentacao().getComposicaoNutricional().getEnergiaKCAL());
                cn.setCHOgPrograma(selecionado.getProgramaAlimentacao().getComposicaoNutricional().getCHOg());
                cn.setPTNgPrograma(selecionado.getProgramaAlimentacao().getComposicaoNutricional().getPTNg());
                cn.setLPDgPrograma(selecionado.getProgramaAlimentacao().getComposicaoNutricional().getLPDg());
                cn.setFIBRASgPrograma(selecionado.getProgramaAlimentacao().getComposicaoNutricional().getFIBRASg());
                cn.setVITAmcgPrograma(selecionado.getProgramaAlimentacao().getComposicaoNutricional().getVITAmcg());
                cn.setVITCmcgPrograma(selecionado.getProgramaAlimentacao().getComposicaoNutricional().getVITCmcg());
                cn.setCAmgPrograma(selecionado.getProgramaAlimentacao().getComposicaoNutricional().getCAmg());
                cn.setFEmgPrograma(selecionado.getProgramaAlimentacao().getComposicaoNutricional().getFEmg());
                cn.setZNmgPrograma(selecionado.getProgramaAlimentacao().getComposicaoNutricional().getZNmg());
                cn.setNAmgPrograma(selecionado.getProgramaAlimentacao().getComposicaoNutricional().getNAmg());
            }
            mapComposicaoNutricionalPorSemana.put(semana, cn);
        }
        atualizarComposicaoNutricional();
    }

    private Feriado preencherMapDiaFeriado(CardapioAgenda cardapioAgenda) {
        Feriado feriado = facade.getFeriadoFacade().buscarFeriadoPorAno(
            facade.getSistemaFacade().getExercicioCorrente().getAno(),
            cardapioAgenda.getDiaSemana());

        mapDiaFeriado.put(cardapioAgenda.getDiaSemana(), feriado);
        return feriado;
    }

    public void addDiaAgenda() {
        if (selecionado.getDiasSemana() == null) {
            selecionado.setDiasSemana(Lists.newArrayList());
        }
        selecionado.getDiasSemana().add(diaAgenda);
    }

    public void addRefeicao(CardapioAgenda dia) {
        try {
            if (dia.getRefeicoes() == null) {
                dia.setRefeicoes(Lists.newArrayList());
            }
            validarRefeicaoNullNoCardapio(dia);
            CardapioAgendaRefeicao refeicao = new CardapioAgendaRefeicao();
            refeicao.setCardapioAgenda(dia);
            dia.getRefeicoes().add(refeicao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void atualizarComposicaoNutricional() {
        mapComposicaoNutricionalPorSemana.forEach((semana, cn) -> {
            cn.inicializarValoresNutricionaisRefeicao();
        });

        diasPorSemana.forEach((semana, dias) -> {
            ComposicaoNutricionalVO cn = mapComposicaoNutricionalPorSemana.get(semana);

            dias.forEach(dia -> {
                dia.getRefeicoes().stream()
                    .filter(ref -> ref.getRefeicao() != null)
                    .forEach(ref -> {
                        cn.setEnergiaKCALRefeicao(cn.getEnergiaKCALRefeicao().add(ref.getRefeicao().getComposicaoNutricional().getEnergiaKCAL()));
                        cn.setCHOgRefeicao(cn.getCHOgRefeicao().add(ref.getRefeicao().getComposicaoNutricional().getCHOg()));
                        cn.setPTNgRefeicao(cn.getPTNgRefeicao().add(ref.getRefeicao().getComposicaoNutricional().getPTNg()));
                        cn.setLPDgRefeicao(cn.getLPDgRefeicao().add(ref.getRefeicao().getComposicaoNutricional().getLPDg()));
                        cn.setFIBRASgRefeicao(cn.getFIBRASgRefeicao().add(ref.getRefeicao().getComposicaoNutricional().getFIBRASg()));
                        cn.setVITAmcgRefeicao(cn.getVITAmcgRefeicao().add(ref.getRefeicao().getComposicaoNutricional().getVITAmcg()));
                        cn.setVITCmcgRefeicao(cn.getVITCmcgRefeicao().add(ref.getRefeicao().getComposicaoNutricional().getVITCmcg()));
                        cn.setCAmgRefeicao(cn.getCAmgRefeicao().add(ref.getRefeicao().getComposicaoNutricional().getCAmg()));
                        cn.setFEmgRefeicao(cn.getFEmgRefeicao().add(ref.getRefeicao().getComposicaoNutricional().getFEmg()));
                        cn.setZNmgRefeicao(cn.getZNmgRefeicao().add(ref.getRefeicao().getComposicaoNutricional().getZNmg()));
                        cn.setNAmgRefeicao(cn.getNAmgRefeicao().add(ref.getRefeicao().getComposicaoNutricional().getNAmg()));
                    });
            });
        });
    }

    private void validarRefeicaoNullNoCardapio(CardapioAgenda dia) {
        ValidacaoException ve = new ValidacaoException();
        if (!dia.getRefeicoes().isEmpty()) {
            CardapioAgendaRefeicao ultimaRefeicao = getUltimaRefeicaoAdicionaDoDia(dia);
            if (ultimaRefeicao != null && ultimaRefeicao.getRefeicao() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar uma nova refeição sem antes informar a anterior.");
            }
        }
        ve.lancarException();
    }

    private CardapioAgendaRefeicao getUltimaRefeicaoAdicionaDoDia(CardapioAgenda dia) {
        try {
            return dia.getRefeicoes().get(dia.getRefeicoes().size() - 1);
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }

    public void removerRefeicao(CardapioAgenda diaAgenda, CardapioAgendaRefeicao refeicao) {
        diaAgenda.getRefeicoes().remove(refeicao);
        atualizarComposicaoNutricional();
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void removerMaterial(CardapioAgendaRefeicaoMaterial material) {
        refeicaoSelecionada.getMateriais().remove(material);
    }

    public void adicionarNovoMaterial() {
        try {
            validarMaterialJaAdicionado();
            if (refeicaoSelecionada.getRefeicao() == null) {
                FacesUtil.addOperacaoNaoPermitida("Selecione uma refeição para adiocionar um material.");
            } else if (material != null) {
                CardapioAgendaRefeicaoMaterial cardapioAgendaRefeicaoMaterial = new CardapioAgendaRefeicaoMaterial();
                cardapioAgendaRefeicaoMaterial.setCardapioAgendaRefeicao(refeicaoSelecionada);
                cardapioAgendaRefeicaoMaterial.setMaterial(material);
                refeicaoSelecionada.getMateriais().add(cardapioAgendaRefeicaoMaterial);
            }
            setMaterial(null);
            FacesUtil.atualizarComponente("formDlgMateriais");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void copiarMateriais(CardapioAgendaRefeicao cardapioAgendaRefeicao) {
        if (cardapioAgendaRefeicao.getRefeicao() != null) {
            Refeicao recuperada = facade.getRefeicaoFacade().recuperar(cardapioAgendaRefeicao.getRefeicao().getId());
            cardapioAgendaRefeicao.setMateriais(new ArrayList<>());
            for (RefeicaoMaterial material : recuperada.getMateriais()) {
                CardapioAgendaRefeicaoMaterial cardapioAgendaRefeicaoMaterial = new CardapioAgendaRefeicaoMaterial();
                cardapioAgendaRefeicaoMaterial.setCardapioAgendaRefeicao(cardapioAgendaRefeicao);
                cardapioAgendaRefeicaoMaterial.setMaterial(material.getMaterial());
                cardapioAgendaRefeicao.getMateriais().add(cardapioAgendaRefeicaoMaterial);
            }
        }
        atualizarComposicaoNutricional();
    }

    public void validarMaterialJaAdicionado() {
        ValidacaoException ve = new ValidacaoException();
        if (material == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo material deve ser informado.");
        }
        ve.lancarException();
        if (refeicaoSelecionada.getRefeicao() != null) {
            for (CardapioAgendaRefeicaoMaterial matNaLista : refeicaoSelecionada.getMateriais()) {
                if (material.equals(matNaLista.getMaterial())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Material já adicionado na lista.");
                }
            }
        }
        ve.lancarException();
    }

    public List<ContratoCardapioVO> getContratos() {
        return contratos;
    }

    public void setContratos(List<ContratoCardapioVO> contratos) {
        this.contratos = contratos;
    }

    public CardapioAgendaRefeicao getRefeicaoSelecionada() {
        return refeicaoSelecionada;
    }

    public void limparDadosHierarquiaAdm() {
        setHierarquiaOrcamentaria(null);
        selecionado.setUnidadeOrcamentaria(null);
    }


    public void recuperarMateriaisRefeicaoDialog(CardapioAgendaRefeicao refeicao) {
        try {
            refeicaoSelecionada = refeicao;
            FacesUtil.atualizarComponente("formDlgMateriais");
            FacesUtil.executaJavaScript("dlgMateriais.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void recuperarValorNutricionalRefeicaoDialog(CardapioAgendaRefeicao refeicao) {
        try {
            this.refeicaoSelecionada = refeicao;
            FacesUtil.atualizarComponente("formDlgValorNutri");
            FacesUtil.executaJavaScript("dlgValorNutri.show()");
        } catch (Exception ex) {
            FacesUtil.addOperacaoRealizada(ex.getMessage());
        }
    }

    public void verificarMateriailPertencenteRefeicao(ContratoCardapioMaterialVO materialVO) {
        for (CardapioAgenda cardapioAgenda : selecionado.getDiasSemana()) {
            for (CardapioAgendaRefeicao refeicao : cardapioAgenda.getRefeicoes()) {
                for (CardapioAgendaRefeicaoMaterial material : refeicao.getMateriais()) {
                    if (materialVO.getCodigoMaterial().equals(material.getMaterial().getCodigo())) {
                        materialVO.setPertenceRefeicao(true);
                    }
                }
            }
        }
    }

    public void fecharDialog() {
        FacesUtil.executaJavaScript("dlgMateriais.hide();");
        FacesUtil.executaJavaScript("dlgValorNutri.hide();");
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-contratos".equals(tab) && (contratos == null || contratos.isEmpty())) {
            contratos = facade.buscarContratos(selecionado, hierarquiaAdministrativa);
            for (ContratoCardapioVO contrato : contratos) {
                for (ContratoCardapioMaterialVO material : contrato.getMateriais()) {
                    verificarMateriailPertencenteRefeicao(material);
                }
            }
        }
    }

    public void redirecionarContrato(ContratoCardapioVO contrato) {
        FacesUtil.redirecionamentoInterno("/contrato-adm/ver/" + contrato.getId() + "/");
    }

    @Override
    public void redireciona() {
        if (selecionado.getId() != null && isOperacaoNovo()) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } else {
            FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
        }
    }

    public boolean isDiaSemanaFeriado(CardapioAgenda cardapioAgenda) {
        return cardapioAgenda != null && mapDiaFeriado != null && mapDiaFeriado.get(cardapioAgenda.getDiaSemana()) != null
            && Util.isListNullOrEmpty(cardapioAgenda.getRefeicoes());
    }

    public void removerDiaSemana(Integer semana, CardapioAgenda diaSemana) {
        selecionado.getDiasSemana().remove(diaSemana);
        diasPorSemana.get(semana).remove(diaSemana);
        atualizarComposicaoNutricional();
        FacesUtil.atualizarComponente("Formulario:tab-geral:pn-agenda");
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            String nomeRelatorio = "Relatório do Cardápio";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE " + facade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("SECRETARIA", selecionado.getUnidadeAdministrativa().getDescricao().toUpperCase());
            dto.adicionarParametro("DIVISAO", selecionado.getUnidadeOrcamentaria().getDescricao().toUpperCase());
            dto.adicionarParametro("PROGRAMA", selecionado.getProgramaAlimentacao().getNome().toUpperCase().concat(" - ").concat(selecionado.getProgramaAlimentacao().getDescricao().toUpperCase()));
            dto.adicionarParametro("NUTRICIONISTA", selecionado.getNutricionista().getNome().toUpperCase());
            dto.adicionarParametro("CRN", getCRN());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("idCardapio", selecionado.getId());
            dto.adicionarParametro("ano_exercicio", facade.getSistemaFacade().getExercicioCorrente().getAno());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/cardapio/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public Map<Date, Feriado> getMapDiaFeriado() {
        return mapDiaFeriado;
    }

    public void setMapDiaFeriado(Map<Date, Feriado> mapDiaFeriado) {
        this.mapDiaFeriado = mapDiaFeriado;
    }

    public Map<Integer, ComposicaoNutricionalVO> getMapComposicaoNutricionalPorSemana() {
        return mapComposicaoNutricionalPorSemana;
    }

    public void setMapComposicaoNutricionalPorSemana(Map<Integer, ComposicaoNutricionalVO> mapComposicaoNutricionalPorSemana) {
        this.mapComposicaoNutricionalPorSemana = mapComposicaoNutricionalPorSemana;
    }

    public boolean isFeriadoIncluso() {
        return isFeriadoIncluso;
    }

    public void setFeriadoIncluso(boolean feriadoIncluso) {
        isFeriadoIncluso = feriadoIncluso;
    }

    public boolean isSabadoIncluso() {
        return isSabadoIncluso;
    }

    public void setSabadoIncluso(boolean sabadoIncluso) {
        isSabadoIncluso = sabadoIncluso;
    }
}
