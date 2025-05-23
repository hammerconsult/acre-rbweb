package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.negocios.MoedaFacade;
import br.com.webpublico.negocios.TransferenciaParametrosTributarioFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ManagedBean(name = "transferenciaParametrosTributarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaTransferenciaParametros", pattern = "/tributario/transferencia-de-parametros-de-exercicio/",
        viewId = "/faces/tributario/configuracao/transferencia/edita.xhtml")
})
public class TransferenciaParametrosTributarioControlador implements Serializable {

    public static final Logger logger = LoggerFactory.getLogger(TransferenciaParametrosTributarioControlador.class.getName());
    @EJB
    private TransferenciaParametrosTributarioFacade transferenciaParametrosTributarioFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    private Exercicio origem;
    private Exercicio destino;
    private BigDecimal ufmDestino;
    private List<TransferenciaParametroTributario> transferencias;
    private TransferenciaParametroTributario[] transferenciasSelecionadas;
    private CompletableFuture<AssistenteBarraProgresso> future;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private int transfCorrente;

    public TransferenciaParametrosTributarioControlador() {
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    @URLAction(mappingId = "novaTransferenciaParametros", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        origem = null;
        destino = null;
        ufmDestino = BigDecimal.ZERO;
        transfCorrente = 0;
        carregarTransferencias();
    }

    public Exercicio getOrigem() {
        return origem;
    }

    public void setOrigem(Exercicio origem) {
        this.origem = origem;
    }

    public int getTransfCorrente() {
        return transfCorrente;
    }

    public void setTransfCorrente(int transfCorrente) {
        this.transfCorrente = transfCorrente;
    }

    public int getQuantidadeProcesso() {
        return transferenciasSelecionadas.length;
    }

    public Exercicio getDestino() {
        return destino;
    }

    public void setDestino(Exercicio destino) {
        this.destino = destino;
    }

    public BigDecimal getUfmDestino() {
        return ufmDestino;
    }


    public void setUfmDestino(BigDecimal ufmDestino) {
        this.ufmDestino = ufmDestino;
    }

    public void buscarUfmDestino() {
        if (destino != null) {
            ufmDestino = moedaFacade.recuperaValorUFMPorExercicio(destino.getAno());
        }
    }


    public void setTransferencias(List<TransferenciaParametroTributario> transferencias) {
        this.transferencias = transferencias;
    }

    public List<TransferenciaParametroTributario> getTransferencias() {
        return transferencias;
    }

    public void carregarTransferencias() {
        transferencias = Lists.newArrayList();
        for (TransferenciaParametroTributario trans : TransferenciaParametroTributario.values()) {
            if (trans.isTransferir()) {
                transferencias.add(trans);
            }
        }
    }

    public TransferenciaParametroTributario[] getTransferenciasSelecionadas() {
        return transferenciasSelecionadas;
    }

    public void setTransferenciasSelecionadas(TransferenciaParametroTributario[] transferenciasSelecionadas) {
        this.transferenciasSelecionadas = transferenciasSelecionadas;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (origem == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Exercício de Origem!");
        }
        if (destino == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Exercício de Destino!");
        }
        if (ufmDestino == null || ufmDestino.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("A UFM do Exercício de Destino deve ser informada e deve ser maior que zero!");
        }

        if (transferenciasSelecionadas.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Parâmetro para ser transferido!");
        }
        ve.lancarException();
    }


    public void verificarSeTerminou() {
        if (future != null && future.isDone()) {
            transfCorrente++;
            future = null;
            try {
                TransferenciaParametroTributario transferenciaDaVez = transferenciasSelecionadas[transfCorrente];
                transferirExercicio(origem, destino, transferenciaDaVez);
            } catch (IndexOutOfBoundsException ex) {
                FacesUtil.executaJavaScript("termina()");
                FacesUtil.atualizarComponente("progressoTransferencia");

            }
        }
    }

    public boolean isTodosMarcados() {
        return transferenciasSelecionadas != null && transferenciasSelecionadas.length == TransferenciaParametroTributario.values().length;
    }


    public void marcarTodosProcessos() {
        transferenciasSelecionadas = new TransferenciaParametroTributario[TransferenciaParametroTributario.values().length];
        int posicao = 0;
        for (TransferenciaParametroTributario transferenciaParametroTributario : TransferenciaParametroTributario.values()) {
            transferenciasSelecionadas[posicao] = transferenciaParametroTributario;
            posicao++;
        }
    }

    public void desmarcarTodosProcessos() {
        transferenciasSelecionadas = new TransferenciaParametroTributario[0];
    }


    public void processarTransferencia() {
        try {
            validarCampos();
            FacesUtil.executaJavaScript("acompanhaTransferencia()");
            ConfiguracaoTributario configTrib = configuracaoTributarioFacade.retornaUltimo();
            transferenciaParametrosTributarioFacade.cadastrarUfm(destino, ufmDestino);
            logger.debug("Transferir de " + origem.getAno() + " para " + destino.getAno());

            TransferenciaParametroTributario transferenciaDaVez = transferenciasSelecionadas[transfCorrente];
            transferirExercicio(origem, destino, transferenciaDaVez);
            configTrib.setExercicioPortal(destino);
            configuracaoTributarioFacade.salvar(configTrib);

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public BigDecimal getPercentualGeralProcesso() {
        int percentual = (transfCorrente) * 100;
        return BigDecimal.valueOf(percentual / transferenciasSelecionadas.length);
    }


    public void transferirExercicio(Exercicio origem, Exercicio destino, TransferenciaParametroTributario transferencia) {

        assistenteBarraProgresso = new AssistenteBarraProgresso();
        assistenteBarraProgresso.setUsuarioSistema(transferenciaParametrosTributarioFacade.getSistemaFacade().getUsuarioCorrente());
        if (TransferenciaParametroTributario.ENQUADRAMENTOS_E_CONTAS_DOS_TRIBUTOS.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirEnquadramentosAndContas(origem, destino, assistenteBarraProgresso));
        }
        if (TransferenciaParametroTributario.PONTUACOES_DE_IPTU.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirPontuacoesIptu(origem, destino, assistenteBarraProgresso));
        }
        if (TransferenciaParametroTributario.VALORES_DAS_FACES_DE_QUADRA.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao() + "- Buscando Registros...");
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirValoresFaceDeQuadra(origem, destino, assistenteBarraProgresso, transferencia));
        }
        if (TransferenciaParametroTributario.SERVICOS_DAS_FACES_DE_QUADRA.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao() + " - Buscando Registros...");
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirServicosFaceDeQuadra(origem, destino, assistenteBarraProgresso, transferencia));
        }
        if (TransferenciaParametroTributario.FAIXAS_DE_ALVARA.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirFaixasDeAlvara(origem, destino, assistenteBarraProgresso));
        }
        if (TransferenciaParametroTributario.PARAMETROS_DE_TIPO_DE_DIVIDA_DIVERSA.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametrosTipoDividaDiversa(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.PARAMETROS_DE_FISCALIZACAO_DE_ISS.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametrosFiscalizacaoIss(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.PARAMETROS_DE_ITBI.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametrosItbi(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.PARAMETROS_DE_DIVIDA_ATIVA.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametrosDividaAtiva(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.PARAMETROS_DE_COBRANCAS_ADMINISTRATIVAS.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametrosCobrancasAdmininstrativas(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.PARAMETROS_DE_RENDAS_PATRIMONIAIS_E_CEASA.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametrosRendasCeasa(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.OPCAO_PAGAMENTO_FIXO.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametrosOpcaoPagamento(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.PARAMETROS_DE_SIMPLES_NACIONAL.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametrosSimplesNacional(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.PARAMETRO_REGULARIZACAO.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametroRegularizacao(origem, destino, assistenteBarraProgresso));
        }
        if (TransferenciaParametroTributario.HABITESE_FAIXA_DE_VALORES.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirHabiteseFaixaDeValores(origem, destino, assistenteBarraProgresso));
        }
        if (TransferenciaParametroTributario.PARAMETRO_ALVARA_IMEDIATO.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametroAlvaraImediato(origem, destino, assistenteBarraProgresso));
        }
        if (TransferenciaParametroTributario.PARAMETRO_MALA_DIRETA_IPTU.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametroMalaDiretaIptu(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.PARAMETRO_LICENCIAMENTO_AMBIENTAL.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametroLicenciamentoAmbiental(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.CATEGORIA_ASSUNTO_LICENCIAMENTO_AMBIENTAL.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirCategoriaAssuntoLicenciamentoAmbiental(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.PARAMETRO_DE_MARCA_A_FOGO.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametroMarcaFogo(origem, destino, assistenteBarraProgresso));
        }

        if (TransferenciaParametroTributario.PARAMETRO_INFORMACOES_RBTRANS.equals(transferencia)) {
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> transferenciaParametrosTributarioFacade.transferirParametroInformacoesRBTrans(origem, destino, assistenteBarraProgresso));
        }
    }

    public enum TransferenciaParametroTributario {
        ENQUADRAMENTOS_E_CONTAS_DOS_TRIBUTOS("Enquadramentos e Contas dos Tributos", true),
        PONTUACOES_DE_IPTU("Pontuações de IPTU", true),
        VALORES_DAS_FACES_DE_QUADRA("Valores das Faces de Quadra", true),
        SERVICOS_DAS_FACES_DE_QUADRA("Serviços das Faces de Quadra", true),
        FAIXAS_DE_ALVARA("Faixas de Alvará", true),
        OPCAO_PAGAMENTO_FIXO("Opção de Pagamento de Vencimento Fixo", true),
        PARAMETROS_DE_TIPO_DE_DIVIDA_DIVERSA("Parâmetros de Tipo de Dívida Diversa", true),
        PARAMETROS_DE_FISCALIZACAO_DE_ISS("Parâmetros de Fiscalização de ISS", true),
        PARAMETROS_DE_ITBI("Parâmetros de ITBI", true),
        PARAMETROS_DE_DIVIDA_ATIVA("Parâmetros de Dívida Ativa", true),
        PARAMETROS_DE_COBRANCAS_ADMINISTRATIVAS("Parâmetros de Cobranças Administrativas", true),
        PARAMETROS_DE_RENDAS_PATRIMONIAIS_E_CEASA("Parâmetros de Rendas Patrimoniais e Ceasa", true),
        PARAMETROS_DE_SIMPLES_NACIONAL("Parâmetros de Simples Nacional", true),
        PARAMETRO_REGULARIZACAO("Parâmetro de Regularização de Construção", true),
        HABITESE_FAIXA_DE_VALORES("Faixa de Valores de Construções Habite-se", true),
        PARAMETRO_ALVARA_IMEDIATO("Parâmetro do Alvará Imediato", true),
        PARAMETRO_MALA_DIRETA_IPTU("Parâmetro da Mala Direta de IPTU", true),
        PARAMETRO_LICENCIAMENTO_AMBIENTAL("Parâmetro do Licenciamento Ambiental", true),
        CATEGORIA_ASSUNTO_LICENCIAMENTO_AMBIENTAL("Categorias dos Assuntos do Licenciamento Ambiental", true),
        PARAMETRO_DE_MARCA_A_FOGO("Parâmetro de Marca a Fogo", true),
        PARAMETRO_INFORMACOES_RBTRANS("Parâmetro de informações do RB Trans", true);
        private String descricao;
        private boolean transferir;

        TransferenciaParametroTributario(String descricao, boolean transferir) {
            this.descricao = descricao;
            this.transferir = transferir;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isTransferir() {
            return transferir;
        }
    }
}
