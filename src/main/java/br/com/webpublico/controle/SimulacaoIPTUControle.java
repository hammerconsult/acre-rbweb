package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.negocios.AtributoFacade;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.CalculoIptuBean;
import br.com.webpublico.negocios.LoteFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author daniel
 */

@ManagedBean(name = "simulacaoIPTUControle")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoSimulacaoCalculoIPTU", pattern = "/tributario/cadastro-imobiliario/calculo-iptu/sumulacao", viewId = "/faces/tributario/iptu/simulacaoiptu/simula.xhtml"),
})

public class SimulacaoIPTUControle implements Serializable {

    private CadastroImobiliario modelo;
    private Construcao construcaoModelo;
    private Construcao construcaoSelecionada;
    private List<Construcao> construcoes;
    private Lote loteModelo;
    private List<Testada> testadas;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private AtributoFacade atributoFacade;
    @EJB
    private LoteFacade loteFacade;

    @EJB
    private CalculoIptuBean calculoIptuBean;

    private ConverterAutoComplete converterModelo, converterLote, converterConfiguracaoIPTU;
    private BigDecimal valorTotal;
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
    private Map<String, String> erros = new HashMap<String, String>();

    public CadastroImobiliario getModelo() {
        return modelo;
    }

    public void setModelo(CadastroImobiliario modelo) {
        this.modelo = modelo;
    }

    public List<Testada> getTestadas() {
        return testadas;
    }

    public void setTestadas(List<Testada> testadas) {
        this.testadas = testadas;
    }

    public Construcao getConstrucaoModelo() {
        return construcaoModelo;
    }

    public void setConstrucaoModelo(Construcao construcaoModelo) {
        this.construcaoModelo = construcaoModelo;
    }

    public Lote getLoteModelo() {
        return loteModelo;
    }

    public void setLoteModelo(Lote loteModelo) {
        this.loteModelo = loteModelo;
    }

    public Construcao getConstrucaoSelecionada() {
        return construcaoSelecionada;
    }

    public void setConstrucaoSelecionada(Construcao construcaoSelecionada) {
        atributoFacade.completaAtributos(construcaoSelecionada.getAtributos(), ClasseDoAtributo.CONSTRUCAO);
        this.construcaoSelecionada = construcaoSelecionada;
    }

    public List<Construcao> getConstrucoes() {
        return construcoes;
    }

    public void setConstrucoes(List<Construcao> construcoes) {
        this.construcoes = construcoes;
    }



    public Converter getConverterModelo() {
        if (converterModelo == null) {
            converterModelo = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterModelo;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public Converter getConverterLote() {
        if (converterLote == null) {
            converterLote = new ConverterAutoComplete(Lote.class, loteFacade);
        }
        return converterLote;
    }



    public List<CadastroImobiliario> completaModelo(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "codigo");
    }

    public List<Lote> completaLote(String parte) {
        return loteFacade.listaFiltrando(parte.trim(), "codigoLote");
    }

    public void setaModelo(SelectEvent e) {
        CadastroImobiliario ciTemp = (CadastroImobiliario) e.getObject();
        modelo = cadastroImobiliarioFacade.recuperar(ciTemp.getId());
        recuperaLote(modelo.getLote());
        construcoes = modelo.getConstrucoesAtivas();
        loteModelo = modelo.getLote();
        //imprime();
    }

    public void recuperaLote(Lote lote) {
        loteModelo = loteFacade.recuperar(lote.getId());
        testadas = loteModelo.getTestadas();
        modelo.setLote(loteModelo);
    }

    public void setaLote(SelectEvent e) {
        recuperaLote((Lote) e.getObject());

    }
    @URLAction(mappingId = "novoSimulacaoCalculoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        construcoes = new ArrayList<>();
        testadas = new ArrayList<>();

        modelo = new CadastroImobiliario();
        modelo.setConstrucoes(new ArrayList<Construcao>());
        modelo.setAtributos(atributoFacade.novoMapaAtributoValorAtributo(ClasseDoAtributo.CADASTRO_IMOBILIARIO));
        construcaoModelo = new Construcao();
        construcaoModelo.setAtributos(atributoFacade.novoMapaAtributoValorAtributo(ClasseDoAtributo.CONSTRUCAO));
        construcaoSelecionada = new Construcao();
        construcaoSelecionada.setAtributos(atributoFacade.novoMapaAtributoValorAtributo(ClasseDoAtributo.CONSTRUCAO));
        loteModelo = new Lote();
        loteModelo.setAtributos(atributoFacade.novoMapaAtributoValorAtributo(ClasseDoAtributo.LOTE));

        this.valorTotal = BigDecimal.ZERO;
    }

    public void adicionaConstrucao() {
        construcaoModelo.setImovel(modelo);
        construcaoModelo.setDescricao("Construção Simulada " + (construcoes.size() + 1));
        construcoes.add(construcaoModelo);
        construcaoModelo = new Construcao();
        construcaoModelo.setAtributos(atributoFacade.novoMapaAtributoValorAtributo(ClasseDoAtributo.CONSTRUCAO));
    }

    public BigDecimal getSomaAreaConstruida() {
        BigDecimal total = BigDecimal.ZERO;
        for (Construcao c : construcoes) {
            if (c.getAreaConstruida() != null) {
                total = total.add(new BigDecimal(c.getAreaConstruida().toString()));
            }
        }
        return total;
    }

    public void selecionaConstrucao(ActionEvent e) {
        construcaoSelecionada = ((Construcao) e.getComponent().getAttributes().get("objeto"));
    }


    public void simular() {
//        SimuladorIPTU.simulaFormulas(calculoIptuBean,
//                modelo, engine, valorTotal,
//                configuracaoIPTU,
//                itensConfiguracao,
//                construcaoSelecionada, atributoFacade);
    }

}
