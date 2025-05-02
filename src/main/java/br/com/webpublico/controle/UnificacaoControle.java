package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.PropriedadeFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "unificacaoControle")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "unificar", pattern = "/tributario/cadastroimobiliario/unificar/",
                viewId = "/faces/tributario/cadastromunicipal/unificacao/edita.xhtml")
})
public class UnificacaoControle implements Serializable {

    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private PropriedadeFacade propriedadeFacade;
    private Setor setor;
    private Quadra quadra;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterGenerico converterLote;
    private ConverterAutoComplete converterSetor;
    private ConverterAutoComplete converterQuadra;
    private transient DualListModel originarios;
    private List<CadastroImobiliario> source;
    private List<CadastroImobiliario> target;
    private String novaInscricaoCadastral;
    private List<Propriedade> propriedades;
    private Historico historico;
    private int tipoQuadra;
    private List<Lote> lotes;
    private List<Construcao> construcoes;
    private List<Testada> testadas;
    private Lote lote;
    private BigDecimal areaTotalTerritorial;
    private BigDecimal areaTotalConstruida;
    private CadastroImobiliario cadastro;

    public UnificacaoControle() {
    }

    public Historico getHistorico() {
        return historico;
    }

    public void setHistorico(Historico historico) {
        this.historico = historico;
    }

    public List<Lote> getLotes() {
        return lotes;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public List<Construcao> getConstrucoes() {
        return construcoes;
    }

    public BigDecimal getAreaTotalTerritorial() {
        return areaTotalTerritorial;
    }

    public BigDecimal getAreaTotalConstruida() {
        return areaTotalConstruida;
    }

    public List<Testada> getTestadas() {
        return testadas;
    }

    @URLAction(mappingId = "unificar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        source = Lists.newLinkedList();
        target = Lists.newLinkedList();
        lotes = Lists.newLinkedList();
        testadas = Lists.newLinkedList();
        propriedades = Lists.newLinkedList();
        construcoes = Lists.newLinkedList();
        originarios = new DualListModel(source, target);
        historico = new Historico(1L);
        historico.setDigitador(cadastroImobiliarioFacade.getSistemaFacade().getUsuarioCorrente());
        historico.setDataRegistro(new Date());
        setor = new Setor();
        quadra = new Quadra();
        novaInscricaoCadastral = "";
        tipoQuadra = 0;
        areaTotalConstruida = BigDecimal.ZERO;
        areaTotalTerritorial = BigDecimal.ZERO;
    }

    public String getNovaInscricaoCadastral() {
        return novaInscricaoCadastral;
    }

    public void setNovaInscricaoCadastral(String novaInscricaoCadastral) {
        this.novaInscricaoCadastral = novaInscricaoCadastral;
    }

    public List<Propriedade> getPropriedades() {
        return propriedades;
    }

    public void setPropriedades(List<Propriedade> propriedades) {
        this.propriedades = propriedades;
    }

    public int getTipoQuadra() {
        return tipoQuadra;
    }

    public void setTipoQuadra(int tipoQuadra) {
        this.tipoQuadra = tipoQuadra;
    }

    public Converter getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public Converter getConverterLote() {
        if (converterLote == null) {
            converterLote = new ConverterGenerico(Lote.class, cadastroImobiliarioFacade.getLoteFacade());
        }
        return converterLote;
    }

    public void defineInscricao() {
        if (lote != null) {
            for (CadastroImobiliario cadastro : (List<CadastroImobiliario>) originarios.getTarget()) {
                if (cadastro.getLote().equals(lote)) {
                    novaInscricaoCadastral = cadastro.getInscricaoCadastral();
                }
            }
        }
    }

    public Converter getConverterSetor() {
        if (converterSetor == null) {
            converterSetor = new ConverterAutoComplete(Setor.class, cadastroImobiliarioFacade.getSetorFacade());
        }
        return converterSetor;
    }

    public Converter getConverterQuadra() {
        if (converterQuadra == null) {
            converterQuadra = new ConverterAutoComplete(Quadra.class, cadastroImobiliarioFacade.getQuadraFacade());
        }
        return converterQuadra;
    }

    public void salvar() {
        if (valida()) {
            RequestContext.getCurrentInstance().execute("dlgHistorico.show()");
        }
    }

    public void salvarHistorico() {
        if (validaHistorico()) {
            try {
                unifica();
                RequestContext.getCurrentInstance().execute("dlgHistorico.hide()");
                RequestContext.getCurrentInstance().execute("dlgHistoricoSucesso.show()");
            } catch (Exception e) {
                e.printStackTrace();
                RequestContext.getCurrentInstance().execute("dlgHistoricoFracasso.show()");
                FacesUtil.addFatal("Impossível continuar:", "Erro : " + e.getMessage());
            }
        }
    }

    public boolean validaHistorico() {
        boolean retorno = true;
        if (historico.getDataRegistro() != null && historico.getDataSolicitacao() != null
                && (historico.getDataSolicitacao().compareTo(historico.getDataRegistro()) == 1)) {
            FacesUtil.addError("Impossível continuar!", "A data de solicitação é maior que a data de registro.");
            retorno = false;
        }

        return retorno;
    }

    public boolean valida() {
        boolean valida = true;
        if (getTotalProporcao().compareTo(new BigDecimal(100)) != 0) {
            valida = false;
            FacesUtil.addError("Impossível continuar!", "A soma da proporção dos proprietários não corresponde a 100%, a diferença é de " + getTotalProporcao().subtract(new BigDecimal(100)));
        }
        if (novaInscricaoCadastral == null || "".equals(novaInscricaoCadastral.trim())) {
            valida = false;
            FacesUtil.addError("Impossível continuar!", "Informe a nova inscrição cadastral");
        }
        if (originarios.getTarget().size() <= 1) {
            valida = false;
            FacesUtil.addError("Impossível continuar!", "Selecione no mínimo dois imóveis para a unificação");
        }
        for (CadastroImobiliario c : (List<CadastroImobiliario>) originarios.getTarget()) {
            if (cadastroImobiliarioFacade.imovelComPenhora(c)) {
                String mensagem = "O imóvel " + c.getInscricaoCadastral() + " encontra-se penhorado ";
                Penhora p = cadastroImobiliarioFacade.retornaPenhoraDoImovel(c);
                if (p != null && p.getId() != null) {
                    mensagem += "Conforme, Processo nº. " + p.getNumeroProcesso() + ", em data de " + new SimpleDateFormat("dd/MM/yyyy").format(p.getDataPenhora()) + ", pelo Autor " + p.getAutor().getNome();
                }
                FacesUtil.addError("Impossível continuar!", mensagem);
                valida = false;
            }
        }
        for (Propriedade propriedade : propriedades) {
            if (propriedade.getProporcao().compareTo(0.0) <= 0) {
                FacesUtil.addError("Impossível continuar!", "A proporção do proprietario " + propriedade.getPessoa() + " deve ser maior que ZERO");
                valida = false;
            }
        }
        int testadasPrincipais = 0;
        for (Testada testada : testadas) {
            if (testada.getPrincipal()) {
                testadasPrincipais++;
            }
        }
        if (testadasPrincipais > 1) {
            FacesUtil.addError("Impossível continuar!", "Apenas uma testada deve estar marcada como principal");
            valida = false;
        }
        return valida;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        if (quadra == null || quadra.getId() == null) {
            FacesUtil.addError("Impossível continuar!", "A Quadra deve ser Informada");
            return new ArrayList<CadastroImobiliario>();
        }
        return cadastroImobiliarioFacade.listaFiltrandoPorQuadra(parte.trim(), quadra);
    }

    public List<Setor> completaSetor(String parte) {
        return cadastroImobiliarioFacade.getSetorFacade().listaFiltrando(parte.trim(), "codigo", "nome");
    }

    public List<Quadra> completaQuadra(String parte) {
        if (setor != null && tipoQuadra > 0) {
            return cadastroImobiliarioFacade.getQuadraFacade().listaFiltrandoPorSetor(parte.trim(),
                    this.setor,
                    this.tipoQuadra);
        }
        return null;
    }

    public DualListModel getOriginarios() {
        return originarios;
    }

    public void setOriginarios(DualListModel originarios) {
        this.originarios = originarios;
    }

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public void carregaOriginarios(SelectEvent e) {
        quadra = (Quadra) e.getObject();
        source = cadastroImobiliarioFacade.listaPorSetorQuadra(this.setor, this.quadra);
        originarios = new DualListModel(source, target);
    }

    public void carregaPropriedades() {
        propriedades = new ArrayList<>();
        if (originarios.getTarget().isEmpty()) {
            FacesUtil.addError("Impossível continuar!", "Nenhum imóvel selecionado para a unificação");
        }

        List<Propriedade> propriedadesTemp = propriedadeFacade.listaPropriedadesVigentesPorImovel(originarios.getTarget());
        if (propriedadesTemp != null) {
            for (Propriedade propriedade : propriedadesTemp) {
                boolean pessoaNova = true;
                for (Propriedade p : propriedades) {
                    if (p.getPessoa().equals(propriedade.getPessoa())) {
                        pessoaNova = false;
                        break;
                    }
                }
                if (pessoaNova) {
                    propriedade.setInicioVigencia(new Date());
                    propriedades.add(propriedade);
                }
            }
        }

        defineProporcaoInicial(propriedades);
        lotes.clear();
        construcoes.clear();
        testadas.clear();
        areaTotalConstruida = BigDecimal.ZERO;
        areaTotalTerritorial = BigDecimal.ZERO;
        for (CadastroImobiliario c : (List<CadastroImobiliario>) originarios.getTarget()) {
            c = cadastroImobiliarioFacade.recuperar(c.getId());
            novaInscricaoCadastral = c.getInscricaoCadastral();
            if (c.getLote() != null) {
                lote = cadastroImobiliarioFacade.getLoteFacade().recuperar(c.getLote().getId());
                lotes.add(lote);
                verificaAdicionaTestadas();
                if (lote.getAreaLote() != null) {
                    areaTotalTerritorial = areaTotalTerritorial.add(new BigDecimal(lote.getAreaLote()));
                }
            }

            for (Construcao construcao : c.getConstrucoes()) {
                construcoes.add(construcao);
                areaTotalConstruida = areaTotalConstruida.add(new BigDecimal(construcao.getAreaConstruida()));
            }

            if (cadastroImobiliarioFacade.imovelComPenhora(c)) {
                Penhora p = cadastroImobiliarioFacade.retornaPenhoraDoImovel(c);
                String mensagem = "O imóvel " + c.getInscricaoCadastral() + " Encontra-se Penhorado, ";
                if (p != null && p.getId() != null) {
                    mensagem += "Conforme, Processo nº. " + p.getNumeroProcesso() + ", em data de " + new SimpleDateFormat("dd/MM/yyyy").format(p.getDataPenhora()) + ", pelo Autor " + p.getAutor().getNome();
                }
                FacesUtil.addError("Impossível continuar!", mensagem);
            }
        }
    }

    private boolean defineProporcaoInicial(List<Propriedade> propriedades) {
        if (propriedades.isEmpty() || propriedades == null) {
            return false;
        } else {
            BigDecimal cem = new BigDecimal("100");
            BigDecimal proporcao = cem.divide(new BigDecimal(propriedades.size()), 2, RoundingMode.HALF_EVEN);
            proporcao.setScale(2, RoundingMode.CEILING);
            BigDecimal soma = BigDecimal.ZERO;
            for (Propriedade p : propriedades) {
                p.setProporcao(proporcao.doubleValue());
                soma = soma.add(proporcao);
            }
            int dif = soma.compareTo(cem);
            if (dif != 0) {
                BigDecimal diferenca = cem.subtract(soma);
                propriedades.get(propriedades.size() - 1).setProporcao(proporcao.add(diferenca).doubleValue());
            }
            return true;
        }

    }

    public BigDecimal getTotalProporcao() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        total.setScale(15, RoundingMode.CEILING);
        for (Propriedade p : propriedades) {
            total = total.add(new BigDecimal(p.getProporcao()).setScale(15, RoundingMode.HALF_EVEN));
        }
        return total;
    }

    public List<SelectItem> getTiposDeQuadra() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(0, " "));
        toReturn.add(new SelectItem(1, "Quadra Loteamento"));
        toReturn.add(new SelectItem(2, "Quadra Município"));
        toReturn.add(new SelectItem(3, "Quadra Loteamento/Município"));
        return toReturn;

    }

    public void setarSetor(SelectEvent event) {
        Setor set = (Setor) event.getObject();
        if (set != null) {
            this.setor = set;
        }
    }

    public boolean mantenInscricao(String inscricao) {
        if (inscricao == null || inscricao.isEmpty()) {
            return false;
        }
        for (CadastroImobiliario c : (List<CadastroImobiliario>) originarios.getTarget()) {
            if (c.getInscricaoCadastral().equals(inscricao)) {
                return true;
            }
        }
        return false;
    }

    private CadastroImobiliario getCadastroDoLoteSelecionado() {
        for (CadastroImobiliario c : (List<CadastroImobiliario>) originarios.getTarget()) {
            if (c.getLote().equals(lote)) {
                return c;
            }
        }
        return null;
    }

    private CadastroImobiliario getCadastroUnificado() {
        for (CadastroImobiliario c : (List<CadastroImobiliario>) originarios.getTarget()) {
            if (c.getInscricaoCadastral().equals(novaInscricaoCadastral)) {
                return cadastroImobiliarioFacade.recuperar(c.getId());
            }
        }
        CadastroImobiliario cadastro = new CadastroImobiliario();
        CadastroImobiliario copiarDe = getCadastroDoLoteSelecionado();

        cadastro.setInscricaoCadastral(novaInscricaoCadastral);
        cadastro.setCodigo(novaInscricaoCadastral);
        cadastro.setAreaTotalConstruida(areaTotalConstruida);
        for (Atributo at : copiarDe.getAtributos().keySet()) {
            ValorAtributo novoValorAtributo = new ValorAtributo();
            ValorAtributo valorAtributo = copiarDe.getAtributos().get(at);
            novoValorAtributo.setAtributo(at);
            novoValorAtributo.setValorDate(valorAtributo.getValorDate());
            novoValorAtributo.setValorDecimal(valorAtributo.getValorDecimal());
            novoValorAtributo.setValorDiscreto(valorAtributo.getValorDiscreto());
            novoValorAtributo.setValorInteiro(valorAtributo.getValorInteiro());
            novoValorAtributo.setValorString(valorAtributo.getValorString());
            cadastro.getAtributos().put(at, novoValorAtributo);
        }
        return cadastro;
    }

    public void vaiParaCadastro() {
        FacesUtil.redirecionamentoInterno(((CadastroImobiliarioControlador) Util.getControladorPeloNome("cadastroImobiliarioControlador")).getCaminhoPadrao() + "ver/" + cadastro.getId() + "/");
    }

    private void unifica() {
        try {
            cadastro = getCadastroUnificado();
            if (cadastro == null) {
                FacesUtil.addError("Impossível Continuar", "Informe uma inscrição válida");
                return;
            }
            lote = cadastroImobiliarioFacade.getLoteFacade().recuperar(lote.getId());
            lote.getTestadas().clear();
            lote.setAreaLote(areaTotalTerritorial.doubleValue());

            for (Propriedade p : propriedades) {
                if (!cadastro.getPropriedade().contains(p)) {
                    Propriedade prop = new Propriedade();
                    prop.setAtual(p.getAtual());
                    prop.setDataRegistro(p.getDataRegistro());
                    prop.setFinalVigencia(p.getFinalVigencia());
                    prop.setInicioVigencia(p.getInicioVigencia());
                    prop.setPessoa(p.getPessoa());
                    prop.setProporcao(p.getProporcao());
                    prop.setTipoProprietario(p.getTipoProprietario());
                    prop.setImovel(cadastro);
                    cadastro.getPropriedade().add(prop);
                } else {
                    cadastro.getPropriedade().set(cadastro.getPropriedade().indexOf(p), p);
                }
            }
            for (Construcao c : construcoes) {
                if (!cadastro.getConstrucoes().contains(c)) {
                    geraConstrucao(cadastro, c);
                } else {
                    cadastro.getConstrucoes().set(cadastro.getConstrucoes().indexOf(c), c);
                }
            }
            for (Testada t : testadas) {
                Testada testada = new Testada();
                testada.setLote(lote);
                testada.setTamanho(t.getTamanho());
                testada.setFace(t.getFace());
                testada.setPrincipal(t.getPrincipal());
                lote.getTestadas().add(testada);
            }
            for (CadastroImobiliario c : (List<CadastroImobiliario>) originarios.getTarget()) {
                if (!c.getInscricaoCadastral().equals(novaInscricaoCadastral)) {
                    c.setAtivo(Boolean.FALSE);
                    cadastroImobiliarioFacade.merge(c);
                }
            }
            cadastro.setLote(lote);
            cadastroImobiliarioFacade.atualizaAreaTotalConstruida(cadastro);
            cadastroImobiliarioFacade.mergeCadastroImobiliario(cadastro);
            lote = (Lote) cadastroImobiliarioFacade.merge(lote);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verificaAdicionaTestadas() {
        List<Testada> inDisponivel = Lists.newLinkedList();
        for (Testada testada : lote.getTestadas()) {
            for (Testada testada2 : testadas) {
                if (testada.getFace().equals(testada2.getFace())) {
                    testada2.setTamanho(testada.getTamanho().add(testada2.getTamanho()));
                    inDisponivel.add(testada);
                }
            }
        }
        for (Testada t : lote.getTestadas()) {
            if (!inDisponivel.contains(t)) {
                Testada testada = new Testada();
                testada.setTamanho(t.getTamanho());
                testada.setFace(t.getFace());
                testada.setPrincipal(t.getPrincipal());
                testadas.add(testada);
            }
        }
    }

    private void geraConstrucao(CadastroImobiliario cadastro, Construcao c) {
        Construcao construcao = new Construcao();
        construcao.setImovel(cadastro);
        construcao.setAnoConstrucao(c.getAnoConstrucao());
        construcao.setAreaConstruida(c.getAreaConstruida());
        construcao.setCodigo(construcoes.indexOf(c) + 1);
        construcao.setDataAlvara(c.getDataAlvara());
        construcao.setDataHabitese(c.getDataHabitese());
        construcao.setDataProtocolo(c.getDataProtocolo());
        construcao.setDataRegistro(c.getDataRegistro());
        construcao.setDescricao(c.getDescricao());
        construcao.setEnglobado(c.getEnglobado());
        construcao.setHabitese(c.getHabitese());
        construcao.setNumeroAlvara(c.getNumeroAlvara());
        construcao.setNumeroProtocolo(c.getNumeroProtocolo());
        construcao.setQuantidadePavimentos(c.getQuantidadePavimentos());
        for (Atributo at : c.getAtributos().keySet()) {
            ValorAtributo novoValorAtributo = new ValorAtributo();
            ValorAtributo valorAtributo = c.getAtributos().get(at);
            novoValorAtributo.setAtributo(at);
            novoValorAtributo.setValorDate(valorAtributo.getValorDate());
            novoValorAtributo.setValorDecimal(valorAtributo.getValorDecimal());
            novoValorAtributo.setValorDiscreto(valorAtributo.getValorDiscreto());
            novoValorAtributo.setValorInteiro(valorAtributo.getValorInteiro());
            novoValorAtributo.setValorString(valorAtributo.getValorString());
            construcao.getAtributos().put(at, novoValorAtributo);
        }
        cadastro.getConstrucoes().add(construcao);
    }
}
