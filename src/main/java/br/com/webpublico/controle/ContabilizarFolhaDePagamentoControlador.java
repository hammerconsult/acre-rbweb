/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.FolhaDePagamento;
import br.com.webpublico.entidades.RecursoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ContabilizarFolhaDePagamentoFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.RecursoFPFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author everton
 */
@ManagedBean
@SessionScoped
public class ContabilizarFolhaDePagamentoControlador implements Serializable {

    private FolhaDePagamento folhaDePagamento;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private ContabilizarFolhaDePagamentoFacade contabilizarFolhaDePagamentoFacade;
    private ConverterGenerico converterFolha;
    private Date dataContabilizacao = new Date();

    public ContabilizarFolhaDePagamentoControlador() {
    }

    public Date getDataContabilizacao() {
        return dataContabilizacao;
    }

    public void setDataContabilizacao(Date dataContabilizacao) {
        this.dataContabilizacao = dataContabilizacao;
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public List<RecursoFP> completaRecursoFP(String parte) {
        return recursoFPFacade.listaFiltrando(parte, "codigo", "descricao");
    }

    public List<SelectItem> getFolhas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (FolhaDePagamento folha : folhaDePagamentoFacade.recuperarFolhaAberta()) {
            toReturn.add(new SelectItem(folha, folha.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterFolha() {
        if (converterFolha == null) {
            converterFolha = new ConverterGenerico(FolhaDePagamento.class, folhaDePagamentoFacade);
        }
        return converterFolha;
    }

    public void novo() {
        folhaDePagamento = null;
        dataContabilizacao = new Date();
    }

    private void validarParametros() throws ValidacaoException {
        if (folhaDePagamento == null) {
            throw new ValidacaoException("Informe a folha de pagamento!");
        }

        if (getDataContabilizacao() == null) {
            throw new ValidacaoException("Informe a data de contabilização!");
        }
    }

    public void contabilizar() {
        try {
            validarParametros();
            contabilizarFolhaDePagamentoFacade.contabilizar(folhaDePagamento, dataContabilizacao);

            FacesUtil.addMessageInfo("Informação", "Contabilização da folha de pagamento concluída com sucesso!");
            novo();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageWarn("Atenção", "Contabilização da folha de pagamento não efetuada. " + e.getMessage());
        }
    }
}
