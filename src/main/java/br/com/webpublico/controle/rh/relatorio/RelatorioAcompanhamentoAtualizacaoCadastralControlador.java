package br.com.webpublico.controle.rh.relatorio;


import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.RelatorioAcompanhamentoAtualizacaoCadastral;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.rh.MostrarServidorDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by William on 26/07/2019.
 */

@ManagedBean(name = "relatorioAcompanhamentoAtualizacaoCadastralControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioAcompanhamentoAtualizacaoCadastral", pattern = "/relatorio/acompanhamento-atualizacao-cadastral/", viewId = "/faces/rh/relatorios/relatorioacompanhamentoatualizacaocadastral.xhtml")
})
public class RelatorioAcompanhamentoAtualizacaoCadastralControlador extends AbstractReport {

    private RelatorioAcompanhamentoAtualizacaoCadastral relatorio;
    private UnidadeOrganizacional orgao;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private ConverterAutoComplete converterHierarquia;
    private MostrarServidor mostrarServidor;
    private boolean estatisticaMensais;
    private ModalidadeContratoFP modalidadeContratoFP;

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

    public RelatorioAcompanhamentoAtualizacaoCadastral getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(RelatorioAcompanhamentoAtualizacaoCadastral relatorio) {
        this.relatorio = relatorio;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public MostrarServidor getMostrarServidor() {
        return mostrarServidor;
    }

    public void setMostrarServidor(MostrarServidor mostrarServidor) {
        this.mostrarServidor = mostrarServidor;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public boolean isEstatisticaMensais() {
        return estatisticaMensais;
    }

    public void setEstatisticaMensais(boolean estatisticaMensais) {
        this.estatisticaMensais = estatisticaMensais;
    }

    public RelatorioAcompanhamentoAtualizacaoCadastralControlador() {
        limparCampos();
    }

    @URLAction(mappingId = "novoRelatorioAcompanhamentoAtualizacaoCadastral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos(){
        geraNoDialog = Boolean.TRUE;
        relatorio = new RelatorioAcompanhamentoAtualizacaoCadastral();
        estatisticaMensais = false;
        hierarquiaOrganizacionalSelecionada = null;
        modalidadeContratoFP = null;
        mostrarServidor = null;
    }

    public List<SelectItem> getMostrarServidorSelect() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (MostrarServidor value : MostrarServidor.values()) {
            toReturn.add(new SelectItem(value, value.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getModalidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ModalidadeContratoFP object : modalidadeContratoFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (relatorio.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório");
        }
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão é obrigatório");
        } else {
            relatorio.setOrgao(hierarquiaOrganizacionalSelecionada.toString());
            relatorio.setHierarquiaOrganizacional(hierarquiaOrganizacionalSelecionada);
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-acompanhamento-atualizacao-cadastral/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("Conferência de Rendimentos Pagos");
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
        dto.adicionarParametro("DEPARTAMENTO", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", "Relatório de Acompanhamento de Atualização Cadastral");
        dto.adicionarParametro("ANO", getSistemaFacade().getExercicioCorrente().getAno());
        dto.adicionarParametro("MOSTAR-SERVIDOR", mostrarServidor.getToDto());
        dto.adicionarParametro("ESTATISTICAS-MENSAIS", estatisticaMensais);
        dto.adicionarParametro("MODALIDADE-CONTRATOFP", modalidadeContratoFP == null ? null : modalidadeContratoFP.getId());
        dto.adicionarParametro("ORGAO", relatorio.getOrgao());
        dto.adicionarParametro("ANO-ATUALIZACAO",relatorio.getAno());
        dto.adicionarParametro("ID-HIERARQUIA-SUPERIOR", ( relatorio.getHierarquiaOrganizacional() != null && relatorio.getHierarquiaOrganizacional().getSuperior() != null) ? relatorio.getHierarquiaOrganizacional().getSuperior().getId() : null);
        return dto;

    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }


    public ConverterAutoComplete getConverterHierarquia() {
        if (hierarquiaOrganizacionalSelecionada == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    private boolean validaCampos() {
        return true;
    }

    public UnidadeOrganizacional getOrgao() {
        return orgao;
    }

    public void setOrgao(UnidadeOrganizacional orgao) {
        this.orgao = orgao;
    }


    public enum MostrarServidor {
        NAO_EXIBIR("Não exibir lista de servidores", MostrarServidorDTO.NAO_EXIBIR),
        EXIBIR_ORDENADOS("Ordenados por mês",MostrarServidorDTO.EXIBIR_ORDENADOS);

        private String descricao;
        private MostrarServidorDTO toDto;

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public MostrarServidorDTO getToDto() {
            return toDto;
        }

        public void setToDto(MostrarServidorDTO toDto) {
            this.toDto = toDto;
        }

        MostrarServidor(String descricao, MostrarServidorDTO mostrarServidorDTO) {
            this.descricao = descricao;
            this.toDto = mostrarServidorDTO;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
