package br.com.webpublico.controle.rh.esocial;


import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.esocial.RegistroS1200Facade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "relatorioS1200Controlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-s1200-esocial", pattern = "/rh/e-social/relatorio-s1200-esocial/", viewId = "/faces/rh/esocial/relatorio/inconsistencia-s1200.xhtml"),
})
public class RelatorioS1200Controlador implements Serializable {

    @EJB
    private RegistroS1200Facade registroS1200Facade;
    private Exercicio exercicio;
    private Mes mes;
    @EJB
    private SistemaFacade sistemaFacade;
    private SituacaoESocial situacaoESocial;

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }


    public SituacaoESocial getSituacaoESocial() {
        return situacaoESocial;
    }

    public void setSituacaoESocial(SituacaoESocial situacaoESocial) {
        this.situacaoESocial = situacaoESocial;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getEntidade() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ConfiguracaoEmpregadorESocial configuracao : registroS1200Facade.getConfiguracaoEmpregadorESocialFacade().lista()) {
            toReturn.add(new SelectItem(configuracao.getEntidade(), configuracao.getEntidade().getPessoaJuridica().getCnpj() + " - " + configuracao.getEntidade().getNome()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "relatorio-s1200-esocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        situacaoESocial = SituacaoESocial.PROCESSADO_COM_ERRO;
    }

    public List<SelectItem> getTiposFolha() {
        return Util.getListSelectItem(TipoFolhaDePagamento.getFolhasPorPrioridadeDeUso(), false);
    }

    private void validarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        ve.lancarException();
    }


    public void gerarRelatorio() {
        try {
            validarRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin());
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("Relatório de inconsitência S1200");
            Date dataInicial = DataUtil.montaData(1, mes.getNumeroMesIniciandoEmZero(), exercicio.getAno()).getTime();
            Date dataFinal = DataUtil.ultimoDiaMes(dataInicial).getTime();
            dto.adicionarParametro("dataInicial", DataUtil.getDataFormatada(dataInicial));
            dto.adicionarParametro("dataFinal", DataUtil.getDataFormatada(dataFinal));
            dto.adicionarParametro("situacao", situacaoESocial != null ? situacaoESocial.name() : "");
            dto.setApi("rh/relatorio-inconsistencia-s1200/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getSituacaoes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (SituacaoESocial eSocial : SituacaoESocial.getSituacoesRelatorioInconsistencia()) {
            retorno.add(new SelectItem(eSocial, eSocial.getDescricao()));
        }

        return retorno;
    }

}
