package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtaRegistroPreco;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.*;
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
    @URLMapping(id = "novaConsultaAta", pattern = "/consulta-ata-registro-preco/", viewId = "/faces/administrativo/relatorios/consulta-ata-registro-preco.xhtml")
})
public class ConsultaAtaRegistroPrecoControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ConsultaAtaRegistroPrecoControlador.class);
    @EJB
    private RelatorioAtaRegistroPrecoFacade facade;
    private AtaRegistroPreco ataRegistroPreco;
    private RelatorioAtaRegistroPreco relatorioAta;

    @URLAction(mappingId = "novaConsultaAta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConsulta() {
        limparDadosAta();
    }

    public List<AtaRegistroPreco> completarAtaRegistroPreco(String parte) {
        return facade.getAtaRegistroPrecoFacade().buscarAtaRegistroPreco(parte.trim());
    }

    public void atribuirUnidadeLicitacao() {
        HierarquiaOrganizacional hierarquia = facade.getLicitacaoFacade().recuperarUnidadeVigenteLicitacao(ataRegistroPreco.getLicitacao());
        ataRegistroPreco.getLicitacao().setUnidadeAdministrativa(hierarquia);
    }

    public void limparDadosAta() {
        setAtaRegistroPreco(null);
        setRelatorioAta(null);
    }

    public void voltar() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesUtil.getRequestContextPath() + "/home");
        } catch (IOException e) {
            logger.error("Erro ao voltar home{}", e);
        }
    }

    public void consultarDados() {
        try {
            validarConsulta();
            relatorioAta = facade.buscarDadosAtaRegistroPreco(ataRegistroPreco);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public boolean hasAtaContratada() {
        return relatorioAta != null && relatorioAta.getContratos() != null && !relatorioAta.getContratos().isEmpty();
    }

    public boolean hasAtaInformada() {
        return relatorioAta != null;
    }

    public BigDecimal getValorTotalItensContrato(AtaRegistroPrecoContrato contrato) {
        BigDecimal total = BigDecimal.ZERO;
        for (AtaRegistroPrecoItensContrato item : contrato.getItensContratoValor()) {
            total = total.add(item.getValorTotal());
        }
        for (AtaRegistroPrecoItensContrato item : contrato.getItensContratoQuantidade()) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalAta() {
        BigDecimal total = BigDecimal.ZERO;
        for (AtaRegistroPrecoFornecedor forn : relatorioAta.getFornecedores()) {
            for (AtaRegistroPrecoItens item : forn.getItensPorQuantidade()) {
                total = total.add(item.getValorTotal());
            }
            for (AtaRegistroPrecoItens item : forn.getItensPorValor()) {
                total = total.add(item.getValorTotal());
            }
        }
        return total;
    }

    private void validarConsulta() {
        ValidacaoException ve = new ValidacaoException();
        if (ataRegistroPreco == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ata Registro de Preço deve ser informado.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarConsulta();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + facade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("atas", Lists.newArrayList(RelatorioAtaRegistroPreco.ataToDto(relatorioAta)));
            dto.adicionarParametro("CONTRATOS", relatorioAta.getContratos());
            dto.adicionarParametro("UNIDADE_GERENCIADORA", getAtaRegistroPreco().getLicitacao().getUnidadeAdministrativa().toString());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE ATA REGISTRO DE PREÇO");
            dto.setNomeRelatorio("RELATÓRIO DE ATA REGISTRO DE PREÇO");
            dto.setApi("administrativo/ata-registro-preco/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    public RelatorioAtaRegistroPreco getRelatorioAta() {
        return relatorioAta;
    }

    public void setRelatorioAta(RelatorioAtaRegistroPreco relatorioAta) {
        this.relatorioAta = relatorioAta;
    }
}
