package br.com.webpublico.controlerelatorio.contabil.emenda;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.Vereador;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.StatusEmenda;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.VereadorFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.beust.jcommander.internal.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-emenda-entidade", pattern = "/relatorio/emenda-entidade/", viewId = "/faces/financeiro/relatorio/relatorio-emenda-entidade.xhtml")
})
public class RelatorioEmendaEntidadeControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private VereadorFacade vereadorFacade;
    private PessoaJuridica entidade;
    private Exercicio exercicio;
    private Vereador vereador;
    private StatusEmenda statusCamara;
    private StatusEmenda statusPrefeitura;
    private String filtros;

    @URLAction(mappingId = "relatorio-emenda-entidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        entidade = null;
        exercicio = sistemaFacade.getExercicioCorrente();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Planejamento - SEPLAN");
            dto.adicionarParametro("condicao", montarCondicao());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/emenda-entidade/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        ve.lancarException();
    }

    private String montarCondicao() {
        String condicao = " where ex.id = " + exercicio.getId();
        filtros = " Exercício: " + exercicio.getAno();
        if (entidade != null) {
            condicao += " and pjEntidade.ID = " + entidade.getId();
            filtros += " Entidade: " + entidade.toString();
        }
        if (vereador != null) {
            condicao += " and em.vereador_id = " + vereador.getId();
            filtros += " Vereador: " + vereador.toString();
        }
        if (statusCamara != null) {
            condicao += " and em.statusCamara = '" + statusCamara.name() + "'";
            filtros += " Status da Câmara: " + statusCamara.getDescricao();
        }
        if (statusPrefeitura != null) {
            condicao += " and em.statusPrefeitura = '" + statusPrefeitura.name() + "'";
            filtros += " Status da Prefeitura: " + statusPrefeitura.getDescricao();
        }
        return condicao;
    }

    public String getNomeRelatorio() {
        return "RELATORIO-EMENDA-POR-ENTIDADE";
    }

    public List<Pessoa> completarPessoasJuridicas(String filtro) {
        return pessoaFacade.listaPessoasJuridicas(filtro, SituacaoCadastralPessoa.ATIVO);
    }

    public List<Vereador> completarVereadores(String filtro) {
        if (exercicio != null) {
            return vereadorFacade.listaVereadorPorExercicio(filtro, exercicio);
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getStatusEmendas() {
        return Util.getListSelectItem(StatusEmenda.values());
    }

    public void limparVereador() {
        vereador = null;
    }

    public PessoaJuridica getEntidade() {
        return entidade;
    }

    public void setEntidade(PessoaJuridica entidade) {
        this.entidade = entidade;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Vereador getVereador() {
        return vereador;
    }

    public void setVereador(Vereador vereador) {
        this.vereador = vereador;
    }

    public StatusEmenda getStatusCamara() {
        return statusCamara;
    }

    public void setStatusCamara(StatusEmenda statusCamara) {
        this.statusCamara = statusCamara;
    }

    public StatusEmenda getStatusPrefeitura() {
        return statusPrefeitura;
    }

    public void setStatusPrefeitura(StatusEmenda statusPrefeitura) {
        this.statusPrefeitura = statusPrefeitura;
    }
}
