package br.com.webpublico.controle.administrativo.patrimonio;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoRelatorioApresentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioCessaoBensMoveis",
        pattern = "/relatorio-cessao-bens-moveis/novo/",
        viewId = "/faces/administrativo/patrimonio/relatorios/relatoriocessaobensmoveis.xhtml")})
public class RelatorioCessaoBensMoveisControlador extends RelatorioPatrimonioControlador {

    private HierarquiaOrganizacional unidadeDestino;
    private HierarquiaOrganizacional unidadeOrigem;
    private Date dataInicial;
    private Date dataFinal;
    private String filtros;
    private TipoCessao tipoCessao;
    private TipoRelatorioApresentacao tipoRelatorio;

    @URLAction(mappingId = "novoRelatorioCessaoBensMoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioCessaoBensMoveis() {
        limparCampos();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosRelatorioBensRecebidosPorCessao();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE CESSÃO DE BENS MÓVEIS");
            dto.adicionarParametro("CONDICAO", montaCondicao());
            dto.adicionarParametro("FILTROS", filtros + ".");
            dto.adicionarParametro("ISRESUMIDO", isRelatorioResumido());
            dto.setNomeRelatorio("RELATÓRIO-DE-CESSÃO-DE-BENS-MÓVEIS");
            dto.setApi("administrativo/cessao-bens-moveis/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }


    private String montaCondicao() {
        setFiltros("");
        String condicao = "";
        if (Util.isNotNull(unidadeOrigem)) {
            condicao = " AND UND_ORIGEM.id = " + this.unidadeOrigem.getSubordinada().getId();
            filtros = "unidade origem: " + unidadeOrigem.getDescricao() + ", ";
        }
        if (Util.isNotNull(unidadeDestino)) {
            condicao += " AND UND_DESTINO.id = " + this.unidadeDestino.getSubordinada().getId();
            filtros += "unidade destino: " + unidadeDestino.getDescricao() + ", ";
        }
        if (Util.isNotNull(dataInicial) && Util.isNotNull(dataFinal)) {
            condicao += " and trunc(lotecessao.datahoracriacao) between to_date('" + DataUtil.getDataFormatada(dataInicial)
                + "','dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "','dd/MM/yyyy') ";
            filtros += "data inicial: " + DataUtil.getDataFormatada(dataInicial) + ", data final: " + DataUtil.getDataFormatada(dataFinal) + ", ";
        } else if (Util.isNotNull(dataInicial)) {
            condicao += " and trunc(lotecessao.datahoracriacao) >= to_date('" + DataUtil.getDataFormatada(dataInicial) + "','dd/MM/yyyy')";
            filtros += "data inicial: " + DataUtil.getDataFormatada(dataInicial) + ", ";
        } else if (Util.isNotNull(dataFinal)) {
            condicao += " and trunc(lotecessao.datahoracriacao) <= to_date('" + DataUtil.getDataFormatada(dataFinal) + "','dd/MM/yyyy')";
            filtros += "data final: " + DataUtil.getDataFormatada(dataFinal) + ", ";
        }
        if (Util.isNotNull(tipoCessao)) {
            if (TipoCessao.CESSAO.equals(tipoCessao)) {
                condicao += " and dev.id is null and pc.id is null";
                filtros += "tipo Cessão: Em Cessão, ";
            } else if (TipoCessao.DEVOLUCAO.equals(tipoCessao)) {
                condicao += " and dev.id is not null";
                filtros += "tipo Cessão: Devolvidos, ";
            } else if (TipoCessao.PRORROGACAO.equals(tipoCessao)) {
                condicao += " and pc.id is not null";
                filtros += "tipo Cessão: Prorrogados, ";
            }
        }
        setFiltros(filtros.substring(0, filtros.length() - 2));
        return condicao;
    }

    private void validarFiltrosRelatorioBensRecebidosPorCessao() {
        ValidacaoException ve = new ValidacaoException();
        if (Util.isNull(unidadeOrigem) && Util.isNull(unidadeDestino)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Origem ou Destino deve ser informado.");
        }
        ve.lancarException();
    }


    public List<SelectItem> getTipo() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Todos"));
        toReturn.add(new SelectItem(TipoCessao.CESSAO, TipoCessao.CESSAO.getDescricao()));
        toReturn.add(new SelectItem(TipoCessao.PRORROGACAO, TipoCessao.PRORROGACAO.getDescricao()));
        toReturn.add(new SelectItem(TipoCessao.DEVOLUCAO, TipoCessao.DEVOLUCAO.getDescricao()));
        return toReturn;
    }

    public enum TipoCessao implements EnumComDescricao {

        CESSAO("Em Cessão"),
        PRORROGACAO("Prorrogados"),
        DEVOLUCAO("Devolvidos");

        private String descricao;

        TipoCessao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public HierarquiaOrganizacional getUnidadeDestino() {
        return unidadeDestino;
    }

    public void setUnidadeDestino(HierarquiaOrganizacional unidadeDestino) {
        this.unidadeDestino = unidadeDestino;
    }

    public HierarquiaOrganizacional getUnidadeOrigem() {
        return unidadeOrigem;
    }

    public void setUnidadeOrigem(HierarquiaOrganizacional unidadeOrigem) {
        this.unidadeOrigem = unidadeOrigem;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public TipoCessao getTipoCessao() {
        return tipoCessao;
    }

    public void setTipoCessao(TipoCessao tipoCessao) {
        this.tipoCessao = tipoCessao;
    }

    public TipoRelatorioApresentacao getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorioApresentacao tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public List<SelectItem> getTiposRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(TipoRelatorioApresentacao.RESUMIDO, "Sintético"));
        toReturn.add(new SelectItem(TipoRelatorioApresentacao.DETALHADO, "Analítico"));
        return toReturn;
    }

    public Boolean isRelatorioResumido() {
        return tipoRelatorio.equals(TipoRelatorioApresentacao.RESUMIDO);
    }
}
