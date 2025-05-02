package br.com.webpublico.controle.administrativo;

import br.com.webpublico.entidades.Adesao;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.AdesaoAtaRegistroPreco;
import br.com.webpublico.entidadesauxiliares.AtaRegistroPrecoContrato;
import br.com.webpublico.entidadesauxiliares.AtaRegistroPrecoItensContrato;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.RelatorioAtaRegistroPrecoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConsultaAdesao", pattern = "/consulta-adesao-ata-registro-preco/", viewId = "/faces/administrativo/relatorios/consulta-adesao-ata.xhtml")
})
public class ConsultaAdesaoAtaRegistroPrecoControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ConsultaAdesaoAtaRegistroPrecoControlador.class);
    @EJB
    private RelatorioAtaRegistroPrecoFacade fadade;
    private Adesao adesao;
    private List<AdesaoAtaRegistroPreco> itensAdesao;
    private List<AtaRegistroPrecoContrato> contratos;

    @URLAction(mappingId = "novaConsultaAdesao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConsulta() {
        limparDadosAdesao();
    }

    public List<Adesao> completarAdesao(String parte) {
        return fadade.getAdesaoFacade().buscarAdesaoPorTipoGestorLicitacao(parte.trim());
    }

    public void atribuirDadosSolicitacaoAta(){
        HierarquiaOrganizacional hierarquiaDaUnidade = fadade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), adesao.getSolicitacaoMaterialExterno().getUnidadeOrganizacional(), fadade.getSistemaFacade().getDataOperacao());
        adesao.getSolicitacaoMaterialExterno().setHierarquiaOrganizacional(hierarquiaDaUnidade);
    }

    public void limparDadosAdesao(){
        setAdesao(null);
        itensAdesao = Lists.newArrayList();
        contratos = Lists.newArrayList();
    }

    public void voltar(){
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesUtil.getRequestContextPath() + "/home");
        } catch (IOException e) {
            logger.error("Erro ao voltar home{}", e);
        }
    }

    public void consultarDados() {
        try {
            validarConsulta();
            itensAdesao = fadade.buscarDadosAdesaoAtaRegistroPreco(adesao);
            contratos = fadade.buscarDadosAdesaoContato(adesao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public boolean hasAdesaoContratadas(){
        return contratos !=null && !contratos.isEmpty() ;
    }

    public boolean hasItensPesquisados(){
        return itensAdesao !=null && !itensAdesao.isEmpty() ;
    }

    public BigDecimal getValorTotalItensContrato(AtaRegistroPrecoContrato contrato){
        BigDecimal total = BigDecimal.ZERO;
        for (AtaRegistroPrecoItensContrato item : contrato.getItensContratoQuantidade()) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalAdesao(){
        BigDecimal total = BigDecimal.ZERO;
        for (AdesaoAtaRegistroPreco item : itensAdesao) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    private void validarConsulta() {
        ValidacaoException ve = new ValidacaoException();
        if (adesao == null){
            ve.adicionarMensagemDeCampoObrigatorio("O campo Adesão deve ser informado.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarConsulta();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", fadade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + fadade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("CONTRATOS",  contratos);
            dto.adicionarParametro("UNIDADE_GERENCIADORA", getUnidadeGerenciadoraAta());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE ADESÃO A ATA REGISTRO DE PREÇO");
            dto.adicionarParametro("adesoes", AdesaoAtaRegistroPreco.adesoesAtasToDto(itensAdesao));
            dto.setNomeRelatorio("RELATÓRIO DE ADESÃO A ATA REGISTRO DE PREÇO");
            dto.setApi("administrativo/adesao-ata-registro-preco/");
            ReportService.getInstance().gerarRelatorio(fadade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String getUnidadeGerenciadoraAta() {
        HierarquiaOrganizacional hierarquia = fadade.getLicitacaoFacade().recuperarUnidadeVigenteLicitacao(adesao.getAtaDeRegistroDePreco().getLicitacao());
        if (hierarquia != null) {
            return hierarquia.getCodigo() + " - " + hierarquia.getDescricao();
        }
        return " ";
    }

    public Adesao getAdesao() {
        return adesao;
    }

    public void setAdesao(Adesao adesao) {
        this.adesao = adesao;
    }

    public List<AdesaoAtaRegistroPreco> getItensAdesao() {
        return itensAdesao;
    }

    public void setItensAdesao(List<AdesaoAtaRegistroPreco> itensAdesao) {
        this.itensAdesao = itensAdesao;
    }

    public List<AtaRegistroPrecoContrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<AtaRegistroPrecoContrato> contratos) {
        this.contratos = contratos;
    }
}
