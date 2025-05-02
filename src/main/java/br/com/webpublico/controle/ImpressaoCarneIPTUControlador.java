/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HistoricoImpressaoDAM;
import br.com.webpublico.entidadesauxiliares.ImpressaoCarneIPTU;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.tributario.TipoPerfil;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.ImpressaoCarneIPTUFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.services.ServiceDAM;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.IOException;
import java.util.List;

/**
 * @author fabio
 */


@ManagedBean(name = "impressaoCarneIPTUControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoImpressaoCarneIPTU", pattern = "/carneiptu/", viewId = "/faces/tributario/cadastromunicipal/relatorio/carneiptu.xhtml"),
})
public class ImpressaoCarneIPTUControlador extends AbstractReport {

    @EJB
    private ImpressaoCarneIPTUFacade impressaoCarneIPTUFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @Limpavel(Limpavel.NULO)
    private Exercicio exercicio;
    private ConverterExercicio converterExercicio;
    @Limpavel(Limpavel.NULO)
    private String cadastroInicial, cadastroFinal;


    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(exercicioFacade);
        }
        return converterExercicio;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public boolean validarDebitosIptu() {
        ConfiguracaoTributario config = configuracaoTributarioFacade.retornaUltimo();

        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.BCI_INSCRICAO, ConsultaParcela.Operador.MAIOR_IGUAL, cadastroInicial);
        consulta.addParameter(ConsultaParcela.Campo.BCI_INSCRICAO, ConsultaParcela.Operador.MENOR_IGUAL, cadastroFinal);
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, config.getDividaIPTU().getId());
        consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, exercicio.getAno());
        consulta.addParameter(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Operador.IGUAL, 1);
        consulta.executaConsulta();

        return consulta.getResultados().isEmpty();
    }

    public void imprimirCarne() throws IOException, JRException {
        try {
            if (validarCampos()) {
                ServiceDAM serviceDAM = (ServiceDAM) Util.getSpringBeanPeloNome("serviceDAM");
                List<ImpressaoCarneIPTU> carnes = impressaoCarneIPTUFacade.buscarCarnesIPTU(exercicio.getAno(), cadastroInicial, cadastroFinal);
                if (!carnes.isEmpty()) {
                    impressaoCarneIPTUFacade.verificarEGerarDamCarnesIptu(carnes, HistoricoImpressaoDAM.TipoImpressao.SISTEMA);
                    RelatorioDTO dto = new RelatorioDTO();
                    dto.setNomeRelatorio("Carnê de IPTU");
                    dto.adicionarParametro("MODULO", "Tributário");
                    dto.adicionarParametro("EXERCICIO", exercicio.getAno());
                    dto.adicionarParametro("CADASTRO_INICIAL", cadastroInicial);
                    dto.adicionarParametro("CADASTRO_FINAL", cadastroFinal);
                    dto.setNomeParametroBrasao("BRASAO");
                    dto.adicionarParametro("TIPO_PERFIL", TipoPerfil.getDescricaoHomologacao());
                    dto.adicionarParametro("HOMOLOGACAO", serviceDAM.isAmbienteHomologacao());
                    dto.adicionarParametro("MSG_PIX", "Pagamento Via QrCode PIX");
                    dto.setApi("tributario/carne-iptu/");
                    ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
                    FacesUtil.addMensagemRelatorioSegundoPlano();
                } else {
                    FacesUtil.addError("Impossível continuar!", "Não foi localização nenhum carnê para impressão com o filtros utilizados!");
                }
            }
        } catch (
            WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (
            ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (exercicio == null || exercicio.getId() == null) {
            FacesUtil.addError("Impossível continuar!", "Exercício não informado!");
            return false;
        }
        return true;
    }


    @URLAction(mappingId = "novoImpressaoCarneIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        Util.limparCampos(this);
    }

    public void copiaCadastroInicialParaCadastroFinal() {
        this.cadastroFinal = this.cadastroInicial;
    }


}
