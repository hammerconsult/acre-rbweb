package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.LoteFacade;
import br.com.webpublico.negocios.QuadraFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoIptuDAO;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "atualizadorDeValoresBCIControlador")
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaAtualizacao",
        pattern = "/cadastro-imobiliario/atualizar-valores/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/atualizavalores.xhtml")
})
public class AtualizadorDeValoresBCIControlador implements Serializable {
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private QuadraFacade quadraFacade;
    @EJB
    private LoteFacade loteFacade;
    private CadastroImobiliarioFacade.AtualizadorBCI atualizadorBCI;
    private String tipoConsulta;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private Setor setorInicial;
    private Setor setorFinal;
    private Quadra quadraInicial;
    private Quadra quadraFinal;
    private Lote loteInicial;
    private Lote loteFinal;

    public CadastroImobiliarioFacade.AtualizadorBCI getAtualizadorBCI() {
        return atualizadorBCI;
    }

    public void setAtualizadorBCI(CadastroImobiliarioFacade.AtualizadorBCI atualizadorBCI) {
        this.atualizadorBCI = atualizadorBCI;
    }

    public String getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public String getInscricaoInicial() {
        if (inscricaoInicial == null) inscricaoInicial = "";
        return inscricaoInicial.trim();
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        if (inscricaoFinal == null) inscricaoFinal = "";
        return inscricaoFinal.trim();
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public Setor getSetorInicial() {
        return setorInicial;
    }

    public void setSetorInicial(Setor setorInicial) {
        this.setorInicial = setorInicial;
    }

    public Setor getSetorFinal() {
        return setorFinal;
    }

    public void setSetorFinal(Setor setorFinal) {
        this.setorFinal = setorFinal;
    }

    public Quadra getQuadraInicial() {
        return quadraInicial;
    }

    public void setQuadraInicial(Quadra quadraInicial) {
        this.quadraInicial = quadraInicial;
    }

    public Quadra getQuadraFinal() {
        return quadraFinal;
    }

    public void setQuadraFinal(Quadra quadraFinal) {
        this.quadraFinal = quadraFinal;
    }

    public Lote getLoteInicial() {
        return loteInicial;
    }

    public void setLoteInicial(Lote loteInicial) {
        this.loteInicial = loteInicial;
    }

    public Lote getLoteFinal() {
        return loteFinal;
    }

    public void setLoteFinal(Lote loteFinal) {
        this.loteFinal = loteFinal;
    }

    @URLAction(mappingId = "novaAtualizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        novaAtualizacao();
    }

    public void novaAtualizacao() {
        setTipoConsulta("1");
        setInscricaoInicial("100000000000000");
        setInscricaoFinal("999999999999999");
        setSetorInicial(null);
        setSetorFinal(null);
        setQuadraInicial(null);
        setQuadraFinal(null);
        setLoteInicial(null);
        setLoteFinal(null);
        setAtualizadorBCI(null);
    }

    public void atualizaBCI() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        JdbcCalculoIptuDAO calculoDAO = (JdbcCalculoIptuDAO) ap.getBean("jdbcCalculoIptuDAO");
        List<CadastroImobiliario> cadastros = calculoDAO.buscarCadastrosPorIncricaoSetorQuadraLote(getInscricaoInicial(),
            getInscricaoFinal(), getSetorInicial(), getSetorFinal(), getQuadraInicial(), getQuadraFinal(), getLoteInicial(), getLoteFinal());
        if (cadastros.isEmpty()) {
            FacesUtil.addWarn("Operação não realizada!", "Não foram encontrados cadastros com os filtros informados.");
            return;
        }
        ConfiguracaoTributario conf = cadastroImobiliarioFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        atualizadorBCI = new CadastroImobiliarioFacade.AtualizadorBCI(conf, cadastros, cadastroImobiliarioFacade.getSistemaFacade().getExercicioCorrente());
        atualizadorBCI.inicializa(cadastros.size());
        atualizadorBCI.setAtualizando(true);
        cadastroImobiliarioFacade.atualizaBCI(atualizadorBCI);
    }

    public boolean finalizouAtualizacao() {
        return atualizadorBCI == null || !atualizadorBCI.isAtualizando();
    }

    public void verificaAtualizacao() {
        if (finalizouAtualizacao()) {
            FacesUtil.executaJavaScript("terminaTimer()");
        }
        FacesUtil.atualizarComponente("Formulario");
    }

    public void alterouSetorInicialOuFinal() {
        setQuadraInicial(null);
        setQuadraFinal(null);
        setLoteInicial(null);
        setLoteFinal(null);
    }

    public Boolean desabilitarQuadra() {
        return setorInicial == null || setorFinal == null;
    }

    public List<Quadra> completarQuadra(String parte) {
        if (parte == null) return Lists.newArrayList();
        return quadraFacade.buscarQuadraPorSetorInicialAndSetorFinal(setorInicial, setorFinal, parte);
    }

    public void alterouQuadraInicialOuFinal() {
        setLoteInicial(null);
        setLoteFinal(null);
    }

    public Boolean desabilitarLote() {
        return quadraInicial == null || quadraFinal == null;
    }

    public List<Lote> completarLote(String parte) {
        if (parte == null) return Lists.newArrayList();
        return loteFacade.buscarLotePorQuadraInicialAndQuadraFinal(quadraInicial, quadraFinal, parte);
    }

    public void alterouTipoConsulta() {
        if (tipoConsulta.equals("1")) {
            setSetorInicial(null);
            setSetorFinal(null);
            setQuadraInicial(null);
            setQuadraFinal(null);
            setLoteInicial(null);
            setLoteFinal(null);
            setAtualizadorBCI(null);
        } else {
            setInscricaoInicial("100000000000000");
            setInscricaoFinal("999999999999999");
        }
    }
}
