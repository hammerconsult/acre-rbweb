/*
 * Codigo gerado automaticamente em Fri Jan 06 09:41:47 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CalendarioFP;
import br.com.webpublico.entidades.ItemCalendarioFP;
import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.enums.MesCalendarioPagamento;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CalendarioFPFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean(name = "calendarioFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCalendarioFP", pattern = "/calendariofp/novo/", viewId = "/faces/rh/administracaodepagamento/calendariofp/edita.xhtml"),
    @URLMapping(id = "editarCalendarioFP", pattern = "/calendariofp/editar/#{calendarioFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/calendariofp/edita.xhtml"),
    @URLMapping(id = "listarCalendarioFP", pattern = "/calendariofp/listar/", viewId = "/faces/rh/administracaodepagamento/calendariofp/lista.xhtml"),
    @URLMapping(id = "verCalendarioFP", pattern = "/calendariofp/ver/#{calendarioFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/calendariofp/visualizar.xhtml")
})
public class CalendarioFPControlador extends PrettyControlador<CalendarioFP> implements Serializable, CRUD {

    @EJB
    private CalendarioFPFacade calendarioFPFacade;
    private Date diaAtual;

    public CalendarioFPControlador() {
        super(CalendarioFP.class);

        diaAtual = new Date();
    }

    public CalendarioFPFacade getFacade() {
        return calendarioFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return calendarioFPFacade;
    }

    public Date getDiaAtual() {
        return diaAtual;
    }

    public void setDiaAtual(Date diaAtual) {
        this.diaAtual = diaAtual;
    }

    public List<SelectItem> getAnos() {
        Calendar c = new GregorianCalendar();
        Integer ano = c.get(Calendar.YEAR);

        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(ano, ano + ""));
        for (int i = 0; i < 4; i++) {
            ano += 1;
            toReturn.add(new SelectItem(ano, ano + ""));
        }
        return toReturn;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/calendariofp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoCalendarioFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verCalendarioFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editarCalendarioFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();

    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    public void selecionar() {
        if (selecionado.getAno() != null) {
            CalendarioFP c = calendarioFPFacade.recuperarByAno(selecionado.getAno());
            if (c == null) {
                selecionado.setItemCalendarioFPs(new ArrayList<ItemCalendarioFP>());
                ItemCalendarioFP item;
                for (MesCalendarioPagamento object : MesCalendarioPagamento.values()) {
                    item = new ItemCalendarioFP(object, null, null, null, null, selecionado);
                    selecionado.getItemCalendarioFPs().add(item);
                }
            } else {
                selecionado = c;
                FacesUtil.redirecionamentoInterno("/calendariofp/editar/" + selecionado.getId());

            }

        }
    }

    @Override
    public RevisaoAuditoria getUltimaRevisao() {
        if (ultimaRevisao == null) {
            ultimaRevisao = buscarUltimaAuditoria();
            if (selecionado.getItemCalendarioFPs() != null) {
                for (ItemCalendarioFP itemCalendarioFP : selecionado.getItemCalendarioFPs()) {
                    RevisaoAuditoria revisaoAuditoria = buscarUltimaAuditoria(ItemCalendarioFP.class, itemCalendarioFP.getId());
                    if (ultimaRevisao == null || (revisaoAuditoria != null && revisaoAuditoria.getDataHora().after(ultimaRevisao.getDataHora()))) {
                        ultimaRevisao = revisaoAuditoria;
                    }
                }
            }
        }
        return ultimaRevisao;
    }

    public Boolean validaCampos() {
        String msg = "";
        Integer mes;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date valida = new Date();
        Boolean validaData = true;

        for (ItemCalendarioFP item : selecionado.getItemCalendarioFPs()) {
            // a verificação faz com que no caso do décimo terceiro,
            // considerar que seja o mês 12 - dezembro
            if (item.getMesCalendarioPagamento() == MesCalendarioPagamento.DECIMO) {
                mes = item.getMesCalendarioPagamento().ordinal() - 1;
            } else {
                mes = item.getMesCalendarioPagamento().ordinal();
            }

            //Valida ultimo dia para a entrega de documentos
            msg = "";
            if (item.getDiaEntregaDocumentos() != null) {
                Calendar c = Calendar.getInstance();
                //System.out.println(item.getCalendarioFP());
                //System.out.println(mes);
                //System.out.println(item.getDataDiaEntregaDocumentos());
                c.set(selecionado.getAno(), mes, item.getDiaEntregaDocumentos());
                c.setLenient(false);

                if (c.before(diaAtual)) {
                    msg = " A data do último dia para entrega de documentos não pode ser inferior a data atual !";
                    validaData = false;
                }

                try {
                    valida = (df.parse(df.format(c.getTime())));
                } catch (Exception e) {
                    msg = " A data " + item.getDiaEntregaDocumentos() + " de " + item.getMesCalendarioPagamento() + " do último dia para entrega de documentos é inválida !";
                    validaData = false;
                }

                if (!msg.equals("")) {
                    FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", msg));
                }
            }

            //Valida Dia do corte das consignações
            msg = "";
            if (item.getDiaCorteConsignacoes() != null) {
                Calendar c = Calendar.getInstance();
                c.set(selecionado.getAno(), mes, item.getDiaCorteConsignacoes());
                c.setLenient(false);

                if (c.before(diaAtual)) {
                    msg = " A data do dia do corte das consignações não pode ser inferior a data atual !";
                    validaData = false;
                }

                try {
                    valida = (df.parse(df.format(c.getTime())));
                } catch (Exception e) {
                    msg = " A data " + item.getDiaCorteConsignacoes() + " de " + item.getMesCalendarioPagamento() + " do dia do corte das consignações é inválida !";
                    validaData = false;
                }

                if (!msg.equals("")) {
                    FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", msg));
                }
            }

            //Valida ultimo dia para processamento
            msg = "";
            if (item.getUltimoDiaProcessamento() != null) {
                Calendar c = Calendar.getInstance();
                c.set(selecionado.getAno(), mes, item.getUltimoDiaProcessamento());
                c.setLenient(false);

                if (c.before(diaAtual)) {
                    msg = " A data do último dia para processamento não pode ser inferior a data atual !";
                    validaData = false;
                }

                try {
                    valida = (df.parse(df.format(c.getTime())));
                } catch (Exception e) {
                    msg = " A data " + item.getUltimoDiaProcessamento() + " de " + item.getMesCalendarioPagamento() + " do último dia para processamento é inválida !";
                    validaData = false;
                }

                if (!msg.equals("")) {
                    FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", msg));
                }
            }

            //Valida dia do pagamento
            msg = "";
            if (item.getDiaPagamento() != null) {
                Calendar c = Calendar.getInstance();
                c.set(selecionado.getAno(), mes, item.getDiaPagamento());
                c.setLenient(false);

                if (c.before(diaAtual)) {
                    msg = " A data do dia do pagamento não pode ser inferior a data atual !";
                    validaData = false;
                }

                try {
                    valida = (df.parse(df.format(c.getTime())));
                } catch (Exception e) {
                    msg = " A data " + item.getDiaPagamento() + " de " + item.getMesCalendarioPagamento() + " do dia do pagamento é inválida !";
                    validaData = false;
                }

                if (!msg.equals("")) {
                    FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", msg));
                }
            }
        }
        return validaData;
    }
}
