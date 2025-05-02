package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.NivelOcorrencia;
import br.com.webpublico.enums.TipoOcorrencia;
import br.com.webpublico.negocios.tributario.auxiliares.GeradorDeIds;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoGeralISSFixoDao;
import br.com.webpublico.interfaces.AssitenteDoGeradorDeIds;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.web.context.ContextLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 08/07/13
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class SingletonLancamentoGeralISSFixo implements AssitenteDoGeradorDeIds {

    private static SingletonLancamentoGeralISSFixo INSTANCE;
    public boolean persistindoCalculo;
    public boolean persistindoOcorrencia;
    public Integer totalCadastros;
    public UsuarioSistema usuarioSistema;
    public Long inicioLancamento;
    private List<OcorrenciaProcessoCalculoGeralIssFixo> ocorrencias;
    private Integer contadorCalculoGeralISSFixo;
    private boolean podeLancar;
    private List<CalculoISS> calculos;
    private int contadorSucesso;
    private int contadorFalha;
    private JdbcCalculoGeralISSFixoDao calculoGeralISSFixoDao;
    private GeradorDeIds geradorDeIds;
    private AssistenteBarraProgresso assistenteBarraProgresso;


    private SingletonLancamentoGeralISSFixo() {
        inicializarAtributos();
    }

    public static synchronized SingletonLancamentoGeralISSFixo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SingletonLancamentoGeralISSFixo();
        }

        return INSTANCE;
    }

    public static void limparInformacoes() {
        INSTANCE = null;
    }

    public synchronized JdbcCalculoGeralISSFixoDao getCalculoGeralISSFixoDao() {
        return calculoGeralISSFixoDao;
    }

    public synchronized void contarMaisUmSucesso() {
        contadorSucesso++;
    }

    public synchronized void contarMaisUmaFalha() {
        contadorFalha++;
    }

    public int getContadorSucesso() {
        return contadorSucesso;
    }

    public int getContadorFalha() {
        return contadorFalha;
    }

    public GeradorDeIds getGeradorDeIds() {
        return geradorDeIds;
    }

    private void inicializarAtributos() {
        contadorCalculoGeralISSFixo = 0;
        totalCadastros = 0;
        calculos = new ArrayList<>();
        ocorrencias = new ArrayList<>();
        calculoGeralISSFixoDao = (JdbcCalculoGeralISSFixoDao) ContextLoader.getCurrentWebApplicationContext().getBean("persisteCalculoGeralISSFixo");
        geradorDeIds = new GeradorDeIds(this);
    }

    public Double getPorcentagemDoCalculo() {
        if (contadorCalculoGeralISSFixo == null || totalCadastros == null) {
            return 0d;
        }

        return (contadorCalculoGeralISSFixo.doubleValue() * 100) / totalCadastros;
    }

    public synchronized void acrescentaMaisUmCalculo() {
        contadorCalculoGeralISSFixo++;
    }

    public synchronized void acrescentaMaisUmGerado(CalculoISS calculoISS) {
        calculos.add(calculoISS);
    }

    public List<CalculoISS> getCalculos() {
        return calculos;
    }

    public boolean podeLancar() {
        return podeLancar;
    }

    public void cancelarLancamento() {
        podeLancar = false;
    }

    public void iniciarLancamento() {
        podeLancar = true;
    }

    public boolean terminouLancamento() {
        return (contadorCalculoGeralISSFixo == totalCadastros) || !podeLancar;
    }

    public boolean todosCadastrosForamProcessados() {
        return contadorCalculoGeralISSFixo == totalCadastros;
    }

    public synchronized void novaListaDeCalculos() {
        calculos = new ArrayList<>();
    }

    public synchronized void novaListaDeOcorrenciasSucesso() {
        ocorrencias = new ArrayList<>();
    }

    public List<OcorrenciaProcessoCalculoGeralIssFixo> getOcorrencias() {
        testarEInicializarOcorrencias();
        return ocorrencias;
    }

    public void setOcorrencias(List<OcorrenciaProcessoCalculoGeralIssFixo> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public synchronized void adicionarOcorrenciasDeSucesso(List<CalculoISS> calculos) {
        for (CalculoISS c : calculos) {
            adicionarOcorrenciaDeSucesso(c);
        }
    }

    private synchronized void adicionarOcorrenciaDeSucesso(CalculoISS calculoISS) {
        SingletonLancamentoGeralISSFixo.getInstance().contarMaisUmSucesso();
        testarEInicializarOcorrencias();

        String msg = "Lançamento de ISS Fixo realizado com sucesso para o contribuinte " + calculoISS.getCadastroEconomico().getInscricaoCadastral();
        ocorrencias.add(getOcorrenciaDeSucessoInicializada(calculoISS, msg));
    }

    private void testarEInicializarOcorrencias() {
        if (ocorrencias == null) {
            ocorrencias = new ArrayList<OcorrenciaProcessoCalculoGeralIssFixo>();
        }
    }

    public synchronized void adicionarOcorrenciaDeFalha(CadastroEconomico ce, String msg) {
        SingletonLancamentoGeralISSFixo.getInstance().contarMaisUmaFalha();
        testarEInicializarOcorrencias();

        ocorrencias.add(getOcorrenciaDeFalhaInicializada(ce, msg));
    }

    private synchronized OcorrenciaProcessoCalculoGeralIssFixo getOcorrenciaDeSucessoInicializada(CalculoISS calculoISS, String msg) {
        OcorrenciaProcessoCalculoGeralIssFixo oco = new OcorrenciaProcessoCalculoGeralIssFixo(calculoISS,
            (ProcessoCalculoGeralIssFixo) assistenteBarraProgresso.getSelecionado(), msg);

        oco.getOcorrencia().setNivelOcorrencia(NivelOcorrencia.INFORMACAO);
        oco.getOcorrencia().setTipoOcorrencia(TipoOcorrencia.CALCULO);

        return oco;
    }

    private synchronized OcorrenciaProcessoCalculoGeralIssFixo getOcorrenciaDeFalhaInicializada(CadastroEconomico ce, String msg) {
        OcorrenciaProcessoCalculoGeralIssFixo oco = new OcorrenciaProcessoCalculoGeralIssFixo(ce,
            (ProcessoCalculoGeralIssFixo) assistenteBarraProgresso.getSelecionado(), msg);

        oco.getOcorrencia().setNivelOcorrencia(NivelOcorrencia.ERRO);
        oco.getOcorrencia().setTipoOcorrencia(TipoOcorrencia.CALCULO);

        return oco;
    }

    @Override
    public int getQuantidade() {
        return calculos.size() + ocorrencias.size();
    }

    @Override
    public JdbcDaoSupport getDao() {
        return calculoGeralISSFixoDao;
    }

    public Long getProximoId() {
        return geradorDeIds.getProximoId();
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public synchronized void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
