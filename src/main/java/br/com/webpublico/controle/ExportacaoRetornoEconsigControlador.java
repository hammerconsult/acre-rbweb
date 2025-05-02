/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ExportarArquivoRetorno;
import br.com.webpublico.entidades.LancamentoFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@ManagedBean
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "inicializa-arquivo-retorno", pattern = "/arquivo-retorno/novo/", viewId = "/faces/rh/administracaodepagamento/arquivoretorno/edita.xhtml"),
    @URLMapping(id = "listar-arquivo-retorno", pattern = "/arquivo-retorno/listar/", viewId = "/faces/rh/administracaodepagamento/arquivoretorno/lista.xhtml"),
    @URLMapping(id = "editar-arquivo-retorno", pattern = "/arquivo-retorno/editar/#{exportacaoRetornoEconsigControlador.id}/", viewId = "/faces/rh/exportaarquivomargem/arquivoretorno/edita.xhtml"),
    @URLMapping(id = "ver-arquivo-retorno", pattern = "/arquivo-retorno/ver/#{exportacaoRetornoEconsigControlador.id}/", viewId = "/faces/rh/administracaodepagamento/arquivoretorno/visualizar.xhtml"),
    @URLMapping(id = "aquivoretorno-log", pattern = "/arquivo-retorno/log/", viewId = "/faces/rh/administracaodepagamento/arquivoretorno/log.xhtml")
})
public class ExportacaoRetornoEconsigControlador extends PrettyControlador<ExportarArquivoRetorno> implements Serializable, CRUD {

    File arquivo;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNew;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ExportacaoRetornoEconsigFacade exportacaoRetornoEconsigFacade;
    private StreamedContent fileDownload;
    @EJB
    private ArquivoEconsigFacade arquivoEconsigFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    public ExportacaoRetornoEconsigControlador() {
        super(ExportarArquivoRetorno.class);
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }


    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));

        if (selecionado.getMes() != null && selecionado.getAno() != null && selecionado.getTipoFolhaDePagamento() != null) {
            for (Integer versaoFolha : folhaDePagamentoFacade.recuperarVersao(Mes.getMesToInt(selecionado.getMes()), selecionado.getAno(), selecionado.getTipoFolhaDePagamento())) {
                retorno.add(new SelectItem(versaoFolha, String.valueOf(versaoFolha)));
            }
        }
        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/arquivo-retorno/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getPercentual() {
        return (selecionado.getContador() * 100) / (selecionado.getContadorTotal() == 0 ? 0.001 : selecionado.getContadorTotal());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @URLAction(mappingId = "inicializa-arquivo-retorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado = new ExportarArquivoRetorno();
        selecionado.setLiberarPaineis(false);
        selecionado.setLiberado(false);
    }

    @URLAction(mappingId = "ver-arquivo-retorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editar-arquivo-retorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public AbstractFacade getFacede() {
        return exportacaoRetornoEconsigFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void exportarArquivoRetorno() {
        try {
            selecionado.setLiberado(false);
            arquivo = null;
            validarCampos();
            List<LancamentoFP> lista = buscarLancamentos(selecionado);
            if (lista.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção ! Não há dados para os filtros selecionados!", ""));
                return;
            }
            selecionado.setLiberarPaineis(true);
            selecionado.setContadorTotal(lista.size());

            FacesUtil.redirecionamentoInterno("/arquivo-retorno/log/");
            exportacaoRetornoEconsigFacade.gerarArquivo(selecionado, lista);

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ee) {
            FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_INFO, "Problemas na geração do arquivo", "Log do erro:" + ee));
            logger.error("Erro: ", ee);
        }
    }

    private List<LancamentoFP> buscarLancamentos(ExportarArquivoRetorno selecionado) {
        if (selecionado.getVersao() == null || (selecionado.getVersao() != null && selecionado.getVersao().equals(1))) {
            return lancamentoFPFacade.buscarLancamentosArquivoRetorno(selecionado);
        } else {
            return lancamentoFPFacade.buscarLancamentosPorVersao(selecionado);
        }

    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMes() != null) {
            if ((selecionado.getMes() == 0) && (selecionado.getMes() > 12)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Informe um mês válido entre 01 e 12.");
            }
        } else {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Mês.");
        }
        if (selecionado.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Ano.");
        }
        if (selecionado.getTipoFolhaDePagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Folha.");
        }
        if (selecionado.getMes() != null && selecionado.getAno() != null) {
            if (arquivoEconsigFacade.existeArquivoProcessado(Mes.getMesToInt(selecionado.getMes()), selecionado.getAno())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O arquivo de Movimento Financeiro do e-Consig deve ser importado.");
            }
            if (exportacaoRetornoEconsigFacade.jaExisteLancamentoParaMesEAnoEFolha(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("já existe um arquivo de margem gerado para o período");
            }
        }
        ve.lancarException();
    }

    public String getNomeArquivo() {
        return "Retorno_" + selecionado.getMes() + selecionado.getAno() + ".txt";
    }

    public String getDataFormatada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(new Date());
    }

    public StreamedContent getFileDownload() throws IOException {
        arquivo = File.createTempFile(getNomeArquivo(), ".txt");
        FileOutputStream fos = new FileOutputStream(arquivo);
        InputStream stream = new FileInputStream(arquivo);

        fos.write(selecionado.getConteudo().getBytes());
        fos.close();
        fileDownload = new DefaultStreamedContent(stream, "text/plain", getNomeArquivo());
        return fileDownload;
    }

}
