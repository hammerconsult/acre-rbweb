/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.SextaParteFacade;
import br.com.webpublico.relatoriofacade.rh.RelatorioTempoDeServicoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Joao
 */
@ManagedBean(name = "relatorioPreviaSextaParteController")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "gerarRelatorioPreviaSextaParte", pattern = "/relatorio/previa-sexta-parte/listar/", viewId = "/faces/rh/relatorios/relatorioPreviaSextaParte.xhtml")
})
public class RelatorioPreviaSextaParteController extends AbstractReport implements Serializable {

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private SextaParteFacade sextaParteFacade;
    @EJB
    private RelatorioTempoDeServicoFacade relatorioTempoDeServicoFacade;
    private ContratoFP contratoFP;
    private TipoSextaParte tipoSextaParte;
    private PessoaFisica pessoaFisica;
    private Cargo cargo;
    private LotacaoFuncional lotacaoFuncional;
    private MatriculaFP matricula;
    private Date inicioVigencia;
    private Date finalVigencia;
    private boolean exibirSomenteAfastamentoDescontaTempoServicoEfetivo = false;
    private boolean exibirSomenteAverbacoesSextaParte = false;


    public List<ContratoFP> completarContratosFP(String filtro) {
        return contratoFPFacade.buscaContratoFPFiltrandoAtributosVinculoMatriculaFP(filtro.trim());
    }

    @URLAction(mappingId = "gerarRelatorioPreviaSextaParte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        contratoFP = null;
        tipoSextaParte = null;
    }



    public void gerarRelatorio(String tipoRelatorio) {
        try {
            //  validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DA PREVIA SEXTA PARTE");
            dto.setNomeRelatorio("RELATÓRIO-DA-PREVIA-SEXTA-PARTE");
            dto.adicionarParametro("CONTRATOFP", contratoFP.getId());
            dto.adicionarParametro("TIPOSEXTAPARTE", tipoSextaParte.toString());
            dto.adicionarParametro("IDTIPOSEXTAPARTE", tipoSextaParte.getId());
            dto.adicionarParametro("DATAREFERENCIA", getSistemaFacade().getDataOperacao());
            dto.adicionarParametro("EXIBIR_SOMENTE_AFASTAMENTO_DESCONTA_TEMPO_SERVICO_EFETIVO", exibirSomenteAfastamentoDescontaTempoServicoEfetivo);
            dto.adicionarParametro("EXIBIR_SOMENTE_AVERBACOES_SEXTA_PARTE", exibirSomenteAverbacoesSextaParte);
            dto.setApi("rh/previa-sexta-parte/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error("Erro ao gerar relatório de Prévia da Sexta Parte: {} " + ex);
        }
    }

    public List<TipoSextaParte> listarTiposSextaParte(String parte) {
        return sextaParteFacade.getTipoSextaParteFacade().listaFiltrando(parte,"descricao");
    }

    public ContratoFP getContratoFP() {
         return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public TipoSextaParte getTipoSextaParte() {
        return tipoSextaParte;
    }

    public void setTipoSextaParte(TipoSextaParte tipoSextaParte) {
        this.tipoSextaParte = tipoSextaParte;
    }


    public PessoaFisica getPessoaFisica() {return pessoaFisica;}


    public void setPessoaFisica(PessoaFisica pessoaFisica) {this.pessoaFisica = pessoaFisica;}


    public Cargo getCargo() {return cargo;}


    public void setCargo(Cargo cargo) {this.cargo = cargo;}

    public LotacaoFuncional getLotacaoFuncional() {return lotacaoFuncional;}

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {this.lotacaoFuncional = lotacaoFuncional;}

    public MatriculaFP getMatricula() {
        return matricula;
    }

    public void setMatricula(MatriculaFP matricula) {
        this.matricula = matricula;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public boolean isExibirSomenteAfastamentoDescontaTempoServicoEfetivo() {
        return exibirSomenteAfastamentoDescontaTempoServicoEfetivo;
    }

    public void setExibirSomenteAfastamentoDescontaTempoServicoEfetivo(boolean exibirSomenteAfastamentoDescontaTempoServicoEfetivo) {
        this.exibirSomenteAfastamentoDescontaTempoServicoEfetivo = exibirSomenteAfastamentoDescontaTempoServicoEfetivo;
    }

    public boolean isExibirSomenteAverbacoesSextaParte() {
        return exibirSomenteAverbacoesSextaParte;
    }

    public void setExibirSomenteAverbacoesSextaParte(boolean exibirSomenteAverbacoesSextaParte) {
        this.exibirSomenteAverbacoesSextaParte = exibirSomenteAverbacoesSextaParte;
    }
}

