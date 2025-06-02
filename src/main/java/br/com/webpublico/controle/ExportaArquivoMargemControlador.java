/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ExportaArquivoMargem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.ArquivoMargemVinculo;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@ManagedBean
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "inicializa-exportar-margem", pattern = "/exportar-arquivomargem/novo/", viewId = "/faces/rh/administracaodepagamento/exportaarquivomargem/edita.xhtml"),
    @URLMapping(id = "listar-exportar-margem", pattern = "/exportar-arquivomargem/listar/", viewId = "/faces/rh/administracaodepagamento/exportaarquivomargem/lista.xhtml"),
    @URLMapping(id = "editar-exportar-margem", pattern = "/exportar-arquivomargem/editar/#{exportaArquivoMargemControlador.id}/", viewId = "/faces/rh/administracaodepagamento/exportaarquivomargem/edita.xhtml"),
    @URLMapping(id = "ver-exportar-margem", pattern = "/exportar-arquivomargem/ver/#{exportaArquivoMargemControlador.id}/", viewId = "/faces/rh/administracaodepagamento/exportaarquivomargem/visualizar.xhtml"),
    @URLMapping(id = "exportar-aquivo-margem-log", pattern = "/exportar-arquivomargem/log/", viewId = "/faces/rh/administracaodepagamento/exportaarquivomargem/log.xhtml")
})
public class ExportaArquivoMargemControlador extends PrettyControlador<ExportaArquivoMargem> implements Serializable, CRUD {

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private UtilImportaExportaFacade utilImportaExportaFacade;
    private Integer anoSelecionado;
    private Integer mesSelecionado;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionado;
    private StreamedContent fileDownload;
    private File arquivo;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    private VinculoFP vinculoFP;
    private ArquivoMargemVinculo arquivoMargemVinculo;

    //    private ExportaArquivoMargem exportaArquivoMargem = new ExportaArquivoMargem();

    public ExportaArquivoMargemControlador() {
        super(ExportaArquivoMargem.class);
        arquivo = null;
        arquivoMargemVinculo = new ArquivoMargemVinculo();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exportar-arquivomargem/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getPercentual() {
        return (selecionado.getContador() * 100) / (selecionado.getContadorTotal() == 0 ? 0.001 : selecionado.getContadorTotal());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @URLAction(mappingId = "inicializa-exportar-margem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado = new ExportaArquivoMargem();
        selecionado.setLiberarPaineis(false);
        selecionado.setLiberado(false);
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    @URLAction(mappingId = "ver-exportar-margem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editar-exportar-margem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        //Por causa da tela de processamento.
    }

    @Override
    public AbstractFacade getFacede() {
        return utilImportaExportaFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionado() {
        return hierarquiaOrganizacionalSelecionado;
    }

    public void setHierarquiaOrganizacionalSelecionado(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionado) {
        this.hierarquiaOrganizacionalSelecionado = hierarquiaOrganizacionalSelecionado;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public Integer getAnoSelecionado() {
        return anoSelecionado;
    }

    public void setAnoSelecionado(Integer anoSelecionado) {
        this.anoSelecionado = anoSelecionado;
    }

    public Integer getMesSelecionado() {
        return mesSelecionado;
    }

    public void setMesSelecionado(Integer mesSelecionado) {
        this.mesSelecionado = mesSelecionado;
    }

    public void exportarArquivoMargem() {
        try {
            selecionado.setLiberado(false);
            arquivo = null;
            validaCampos();
            definirMesAnoFinanceiro();
            ConfiguracaoFP configuracaoFP = configuracaoRHFacade.buscarConfiguracaoFPVigente(sistemaControlador.getDataOperacao());
            List<VinculoFP> listaContratoFP = contratoFPFacade.listaContratosExportacao(selecionado.getMesFinanceiro(), selecionado.getAnoFinanceiro(), hierarquiaOrganizacionalSelecionado, selecionado.getItemArquivoMargem());
            if (listaContratoFP.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção ! Não há dados para os filtros selecionados !", ""));
                return;
            }
            selecionado.setLiberarPaineis(true);
            selecionado.setContadorTotal(listaContratoFP.size());

            FacesUtil.redirecionamentoInterno("/exportar-arquivomargem/log/");

            utilImportaExportaFacade.montarArquivoMargem(listaContratoFP, sistemaControlador.getExercicioCorrente(), selecionado.getMes(), selecionado.getAno(), selecionado, configuracaoFP);


//            FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_INFO, "Arquivo gerado com sucesso !", "Clique no botão 'Download do Arquivo' para baixar o aquivo em seu computador."));
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        } catch (Exception ee) {
            FacesUtil.addOperacaoRealizada(ee.getMessage());
            logger.error("Erro: ", ee);
        }
    }

    private void definirMesAnoFinanceiro() {
        if (selecionado.getMesFinanceiro() == null || selecionado.getAnoFinanceiro() == null) {
            selecionado.setMesFinanceiro(selecionado.getMes());
            selecionado.setAnoFinanceiro(selecionado.getAno());
        }
    }

    public void limpaCampos() {
        mesSelecionado = null;
        anoSelecionado = null;

    }

    public void validaCampos() {
        ValidacaoException val = new ValidacaoException();
        if (selecionado.getMes() != null) {
            if ((selecionado.getMes() == 0) && (selecionado.getMes() > 12)) {
                val.adicionarMensagemDeOperacaoNaoPermitida("Informe um mês válido entre 01 e 12!");

            }
        } else {
            val.adicionarMensagemDeCampoObrigatorio("Informe o mês");
        }

        if (selecionado.getAno() == null) {
            val.adicionarMensagemDeCampoObrigatorio("Informe o Ano !");
        }
        try {
            ConfiguracaoFP configuracaoFP = configuracaoRHFacade.buscarConfiguracaoFPVigente(configuracaoRHFacade.getSistemaFacade().getDataOperacao());
            if (configuracaoFP.getPercentualMargemDisponivel() == null || configuracaoFP.getPercentualMargemDisponivel().compareTo(BigDecimal.ZERO) == 0) {
                val.adicionarMensagemDeCampoObrigatorio("Preencha corretamente o percentual da 'Margem Disponível' nas Configurações do RH");
            }
            if (configuracaoFP.getPercentualMargemEmprestimo() == null || configuracaoFP.getPercentualMargemEmprestimo().compareTo(BigDecimal.ZERO) == 0) {
                val.adicionarMensagemDeCampoObrigatorio("Preencha corretamente o percentual da 'Margem de Empréstimo' nas Configurações do RH");
            }
            if (configuracaoFP.getQtdeMinimaDiasEuConsigMais() == null) {
                val.adicionarMensagemDeCampoObrigatorio("Informe a quantidade mínima para os servidores terem direito ao 'Eu Consigo+'. Vá em Configurações do RH");
            }
            if (configuracaoFP.getBaseMargemEuConsigoMais() == null) {
                val.adicionarMensagemDeCampoObrigatorio("Informe a base de calculo do Eu Consigo+. Vá em Configurações do RH");
            }
        } catch (Exception e) {
            val.adicionarMensagemDeOperacaoNaoPermitida(e.getMessage());
        }
        val.lancarException();
    }

    public StreamedContent getFileDownload() throws IOException {
        SimpleDateFormat sf = new SimpleDateFormat("MMyyyy");
        arquivo = File.createTempFile("eConsig-Margens.txt", "txt");
        FileOutputStream fos = new FileOutputStream(arquivo);
        InputStream stream = new FileInputStream(arquivo);
        //System.out.println("Arquivo: " + arquivo);
        fos.write(selecionado.getConteudo().getBytes());
        fos.close();
        Date dataGeracaoArquivo = DataUtil.montaData(1, selecionado.getMes() - 1, selecionado.getAno()).getTime();
        fileDownload = new DefaultStreamedContent(stream, "text/plain", "Margem_" + sf.format(dataGeracaoArquivo) + ".txt");
        return fileDownload;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void adicionarVinculo() {
        arquivoMargemVinculo.setVinculoFP(vinculoFP);
        arquivoMargemVinculo.setExportaArquivoMargem(selecionado);
        selecionado.getItemArquivoMargem().add(arquivoMargemVinculo);
        arquivoMargemVinculo = new ArquivoMargemVinculo();
        vinculoFP = null;
        FacesUtil.atualizarComponente("Formulario");
    }

    public void removerServidor(ArquivoMargemVinculo arquivoMargemVinculo) {
        selecionado.getItemArquivoMargem().remove(arquivoMargemVinculo);
    }
}
