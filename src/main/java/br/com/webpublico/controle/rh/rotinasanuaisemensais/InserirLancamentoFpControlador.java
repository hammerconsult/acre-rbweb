package br.com.webpublico.controle.rh.rotinasanuaisemensais;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.LancamentoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ComparadorDeFollhasFacade;
import br.com.webpublico.negocios.LancamentoFPFacade;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author peixe on 20/07/2016  18:16.
 */
@ManagedBean(name = "inserirLancamentoFpControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-migracao-lancamento", pattern = "/lancamentofp/migracao/", viewId = "/faces/rh/administracaodepagamento/lancamentofp/migracao.xhtml")
})
public class InserirLancamentoFpControlador extends PrettyControlador<LancamentoFP> implements Serializable, CRUD {

    protected static final Logger logger = LoggerFactory.getLogger(InserirLancamentoFpControlador.class);
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private ComparadorDeFollhasFacade comparadorDeFollhasFacade;
    private List<LancamentoFP> lancamentos;
    private List<LancamentoFP> rejeitados;
    private Date dataRegistro;

    @Override
    public String getCaminhoPadrao() {
        return "/lancamentofp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return lancamentoFPFacade;
    }

    @URLAction(mappingId = "nova-migracao-lancamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        dataRegistro = new Date();
        lancamentos = Lists.newLinkedList();
        rejeitados = Lists.newLinkedList();
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public List<LancamentoFP> getLancamentos() {
        return lancamentos;
    }

    public void setLancamentos(List<LancamentoFP> lancamentos) {
        this.lancamentos = lancamentos;
    }

    public List<LancamentoFP> getRejeitados() {
        return rejeitados;
    }

    public void setRejeitados(List<LancamentoFP> rejeitados) {
        this.rejeitados = rejeitados;
    }

    public void migrarLancamentos() {
        try {
            validaRegrasEspecificas();
            Map<Integer, List<LancamentoFP>> mapLancamentos = comparadorDeFollhasFacade.buscarLancamentosParaMigracao(dataRegistro);
            if (mapLancamentos != null) {
                lancamentos = mapLancamentos.get(1);
                rejeitados = mapLancamentos.get(2);
            }
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao buscar lançamentos. ", e);
            FacesUtil.addAtencao(e.getMessage());
        }
    }

    @Override
    public boolean validaRegrasEspecificas() {
        ValidacaoException val = new ValidacaoException();
        if (dataRegistro == null) {
            val.adicionarMensagemDeOperacaoNaoPermitida("O campo data é obrigatório.");
        }
        if (val.temMensagens()) {
            throw val;
        }
        return true;
    }

    public void persistirLancamentos() {
        try {
            lancamentoFPFacade.salvarLancamentos(lancamentos);
        } catch (Exception e) {
            logger.debug("Erro: ", e);
        }
    }

}
