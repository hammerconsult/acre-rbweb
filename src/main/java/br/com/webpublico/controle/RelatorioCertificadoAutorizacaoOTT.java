package br.com.webpublico.controle;

import br.com.webpublico.entidades.OperadoraTecnologiaTransporte;
import br.com.webpublico.entidadesauxiliares.FiltroCertificadoAutorizacaoOTT;
import br.com.webpublico.enums.SituacaoCondutorOTT;
import br.com.webpublico.enums.SituacaoOTT;
import br.com.webpublico.enums.TipoRelatorioCertificado;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.OperadoraTecnologiaTransporteFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "relatorioCertificadoOTT")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-certificado-autorizacao-ott", pattern = "/relatorio/certificado-autorizacao-ott/", viewId = "/faces/tributario/rbtrans/relatorio/relatorio-certificado-ott.xhtml")
})
public class RelatorioCertificadoAutorizacaoOTT implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private OperadoraTecnologiaTransporteFacade operadoraFacade;
    private FiltroCertificadoAutorizacaoOTT filtroOtt;
    private OperadoraTecnologiaTransporte operadora;

    @URLAction(mappingId = "relatorio-certificado-autorizacao-ott", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void relatorioCertificadoOtt() {
        limparCampos();
        filtroOtt.setTipoRelatorio(TipoRelatorioCertificado.RELATORIO_OPERADORA_OTT);
    }

    public RelatorioCertificadoAutorizacaoOTT() {
    }

    public String montarNomeDoMunicipio() {
        return "PREFEITURA MUNICIPAL DE " + sistemaFacade.getMunicipio().toUpperCase();
    }

    public void limparCampos() {
        filtroOtt = new FiltroCertificadoAutorizacaoOTT();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("Relatório de Certificados de Autorização de OTT");
            dto.adicionarParametro("FILTROS", montarFiltros());
            dto.adicionarParametro("CRITERIOS", montarCriteriosUtilizados());
            montarParametros(dto);
            dto.setApi("tributario/relatorio-certificado-autorizacao/ott/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void montarParametros(RelatorioDTO dto) {
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
        dto.adicionarParametro("MODULO", "RBTRANS");
        dto.adicionarParametro("SECRETARIA", "Superintendência Municipal de Transportes E Trânsito – RBTRANS");
        dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
        dto.adicionarParametro("NOMERELATORIO", nomeRelatorio());
    }

    public String montarFiltros() {
        StringBuilder where = new StringBuilder();
        String clausula = " where ";
        if (filtroOtt.getOperadora() != null) {
            where.append(clausula).append(" condutor.operadoratectransporte_id  = ").append(filtroOtt.getOperadora().getId());
            clausula = " and ";
        }
        if (filtroOtt.getDataInicial() != null) {
            where.append(clausula).append(" trunc(certificado.dataemissao) >= to_date('").append(DataUtil.getDataFormatada(filtroOtt.getDataInicial())).append("','dd/MM/yyyy') ");
            clausula = " and ";
        }
        if (filtroOtt.getDataFinal() != null) {
            where.append(clausula).append(" trunc(certificado.dataemissao) <= to_date('").append(DataUtil.getDataFormatada(filtroOtt.getDataFinal())).append("','dd/MM/yyyy') ");
            clausula = " and ";
        }
        if (filtroOtt.isCondutoresRegulares()) {
            where.append(clausula).append(" coalesce(trunc(certificado.vencimento), doc.validade) > to_date('").append(DataUtil.getDataFormatada(sistemaFacade.getDataOperacao())).append("','dd/MM/yyyy') ");
            clausula = " and ";
        }
        if (filtroOtt.isCondutoresIrregulares()) {
            where.append(clausula).append(" coalesce(trunc(certificado.vencimento), doc.validade) < to_date('").append(DataUtil.getDataFormatada(sistemaFacade.getDataOperacao())).append("','dd/MM/yyyy') ");
            clausula = " and ";
        }
        if (filtroOtt.isAguardandoAprovacao() && TipoRelatorioCertificado.RELATORIO_CONDUTOR_OTT.equals(filtroOtt.getTipoRelatorio())) {
            where.append(clausula).append(" condutor.situacaocondutorott = '").append(SituacaoCondutorOTT.CADASTRADO).append("'");
            clausula = " and ";
        }
        if (filtroOtt.isAguardandoAprovacao() && TipoRelatorioCertificado.RELATORIO_OPERADORA_OTT.equals(filtroOtt.getTipoRelatorio())) {
            where.append(clausula).append(" operadora.situacao = '").append(SituacaoOTT.CADASTRADO).append("'");
            clausula = " and ";
        }
        return where.toString();
    }

    public String montarCriteriosUtilizados() {
        StringBuilder filtro = new StringBuilder();
        filtro.append("Critérios Utilizados: ");
        if (filtroOtt.getOperadora() != null) {
            filtro.append(" Operadora: ").append(filtroOtt.getOperadora().toString().trim()).append("; ");
        }
        filtro.append(" Filtrar ").append(isRelatorioOperadora() ? "Operadoras" : "Condutores").append(" Regulares: ").append(filtroOtt.isCondutoresRegulares() ? "Sim;" : "Não;");
        filtro.append(" Filtrar ").append(isRelatorioOperadora() ? "Operadoras" : "Condutores").append(" Irregulares: ").append(filtroOtt.isCondutoresIrregulares() ? "Sim;" : "Não;");
        filtro.append(" Filtrar Aguardando Aprovação: ").append(filtroOtt.isAguardandoAprovacao() ? "Sim;" : "Não;");
        if (filtroOtt.getDataInicial() != null) {
            filtro.append(" Período: De: ").append(Util.dateToString(filtroOtt.getDataInicial()));
        }
        if (filtroOtt.getDataFinal() != null) {
            filtro.append(" Até: ").append(Util.dateToString(filtroOtt.getDataFinal()));
        }
        return filtro.toString();
    }

    private String nomeRelatorio() {
        return TipoRelatorioCertificado.RELATORIO_CONDUTOR_OTT.equals(filtroOtt.getTipoRelatorio()) ?
            "Relatório de Certificados de Autorização de Condutores" : "Relatório de Certificado de Autorização de Operadoras de Tecnologia de Transporte - OTT";
    }

    public List<OperadoraTecnologiaTransporte> completarOperadora(String parte) {
        return operadoraFacade.listarOperadoras(parte);
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroOtt.getDataInicial() != null && filtroOtt.getDataFinal() != null) {
            if (filtroOtt.getDataInicial().after(filtroOtt.getDataFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser anterior ou igual a Data Final!");
            }
        }
        ve.lancarException();
    }

    public FiltroCertificadoAutorizacaoOTT getFiltroOtt() {
        return filtroOtt;
    }

    public void setFiltroOtt(FiltroCertificadoAutorizacaoOTT filtroOtt) {
        this.filtroOtt = filtroOtt;
    }

    public OperadoraTecnologiaTransporte getOperadora() {
        return operadora;
    }

    public void setOperadora(OperadoraTecnologiaTransporte operadora) {
        this.operadora = operadora;
    }

    private boolean isRelatorioOperadora() {
        return TipoRelatorioCertificado.RELATORIO_OPERADORA_OTT.equals(filtroOtt.getTipoRelatorio());
    }
}
