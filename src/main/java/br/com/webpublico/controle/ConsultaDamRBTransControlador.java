package br.com.webpublico.controle;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDAM;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidadesauxiliares.ConsultaDAMRBTrans;
import br.com.webpublico.enums.TipoDamRbtrans;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConsultaDamRBTransFacade;
import br.com.webpublico.negocios.ConsultaDebitoFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

import static br.com.webpublico.util.UtilRH.getDataOperacao;

/**
 * Created by Filipe
 * On 10, Maio, 2019,
 * At 14:19
 */

@ViewScoped
@ManagedBean(name = "consultaDamRBTransControlador")
@URLMappings(
    mappings = {@URLMapping(id = "novaConsultaDamRBTrans", pattern = "/consulta-dam-rbtrans/", viewId = "/faces/tributario/rbtrans/consultadamrbtrans/novo.xhtml")
    })
public class ConsultaDamRBTransControlador implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(ConsultaDamRBTransControlador.class);
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private ConsultaDamRBTransFacade consultaDamRBTransFacade;
    private ImprimeDAM imprimeDAM;
    private Exercicio anoDebito;
    private Date vencimentoDam;
    private List<ConsultaDAMRBTrans> consultas;
    private List<ResultadoParcela> selecionados;
    private TipoPermissaoRBTrans tipoPermissaoRBTrans;
    private Integer digitoInicial;
    private Integer digitoFinal;
    private TipoDamRbtrans tipoDamRbtrans;

    @URLAction(mappingId = "novaConsultaDamRBTrans", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        consultas = new ArrayList<>();
        anoDebito = new Exercicio();
        vencimentoDam = getDataOperacao();
    }

    public LinkedList<DAM> gerarDamIndividual() {
        LinkedList<DAM> dams = Lists.newLinkedList();
        LinkedList<ParcelaValorDivida> parcelas = consultaDamRBTransFacade.buscarDamsRBTrans(anoDebito, tipoDamRbtrans, tipoPermissaoRBTrans, digitoInicial, digitoFinal);
        try {
            Calendar c = DataUtil.ultimoDiaMes(getDataOperacao());
            for (ParcelaValorDivida parcela : parcelas) {
                ParcelaValorDivida p = new ParcelaValorDivida();
                p.setId(parcela.getId());
                DAM dam = consultaDebitoFacade.getDamFacade().recuperaDAMPeloIdParcela(parcela.getId());
                if (dam == null || parcela.isVencido(getDataOperacao())) {
                    dam = consultaDebitoFacade.getDamFacade().geraDAM(p, c.getTime());
                    for (ItemDAM iten : dam.getItens()) {
                        Hibernate.initialize(iten.getParcela());
                    }
                }
                dams.add(dam);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return dams;

    }

    public void imprimirDamIndividual() {
        try {
            validarCampo();
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            LinkedList<DAM> dams = gerarDamIndividual();
            imprimeDAM.imprimirDamUnicoViaApi(dams);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi encontrada nenhuma parcela em aberto.");
        }
    }

    public List<SelectItem> getTiposPermissao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPermissaoRBTrans tipo : TipoPermissaoRBTrans.values()) {
            if (!TipoPermissaoRBTrans.NAO_ESPECIFICADO.equals(tipo)) {
                toReturn.add(new SelectItem(tipo.name(), tipo.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposDam() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (TipoDamRbtrans tipo : TipoDamRbtrans.values()) {
            toReturn.add(new SelectItem(tipo.name(), tipo.getDescricao()));
        }
        return toReturn;
    }


    public void validarCampo() {
        ValidacaoException ve = new ValidacaoException();
        if (digitoInicial != null && digitoFinal != null && digitoInicial.compareTo(digitoFinal) > 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O valor final do número da permissão, não pode ser menor que o valor inicial.");
        }
        if (tipoPermissaoRBTrans == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Permissão deve ser informado.");
        }
        if (anoDebito == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano de Débito deve ser informado.");
        }
        ve.lancarException();
    }

    //Gettes and Setters//
    public ImprimeDAM getImprimeDAM() {
        return imprimeDAM;
    }

    public void setImprimeDAM(ImprimeDAM imprimeDAM) {
        this.imprimeDAM = imprimeDAM;
    }

    public Exercicio getAnoDebito() {
        return anoDebito;
    }

    public void setAnoDebito(Exercicio anoDebito) {
        this.anoDebito = anoDebito;
    }

    public Date getVencimentoDam() {
        return vencimentoDam;
    }

    public void setVencimentoDam(Date vencimentoDam) {
        this.vencimentoDam = vencimentoDam;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public void setConsultaDebitoFacade(ConsultaDebitoFacade consultaDebitoFacade) {
        this.consultaDebitoFacade = consultaDebitoFacade;
    }

    public String getCaminhoPadrao() {
        return "/consulta-dam-rbtrans/";
    }

    public List<ConsultaDAMRBTrans> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<ConsultaDAMRBTrans> consultas) {
        this.consultas = consultas;
    }

    public List<ResultadoParcela> getSelecionados() {
        return selecionados;
    }

    public void setSelecionados(List<ResultadoParcela> selecionados) {
        this.selecionados = selecionados;
    }

    public TipoPermissaoRBTrans getTipoPermissaoRBTrans() {
        return tipoPermissaoRBTrans;
    }

    public void setTipoPermissaoRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
    }

    public Integer getDigitoInicial() {
        return digitoInicial;
    }

    public void setDigitoInicial(Integer digitoInicial) {
        this.digitoInicial = digitoInicial;
    }

    public Integer getDigitoFinal() {
        return digitoFinal;
    }

    public void setDigitoFinal(Integer digitoFinal) {
        this.digitoFinal = digitoFinal;
    }

    public TipoDamRbtrans getTipoDamRbtrans() {
        return tipoDamRbtrans;
    }

    public void setTipoDamRbtrans(TipoDamRbtrans tipoDamRbtrans) {
        this.tipoDamRbtrans = tipoDamRbtrans;
    }
}
