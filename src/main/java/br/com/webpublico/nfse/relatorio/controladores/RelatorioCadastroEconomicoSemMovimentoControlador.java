package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoNotaFiscalServico;
import br.com.webpublico.enums.TipoPorte;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.nfse.domain.dtos.FiltroRelatorioCadastroEconomicoSemMovimento;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-cadastro-economico-sem-movimento",
        pattern = "/nfse/relatorio/cadastro-economico-sem-movimento/",
        viewId = "/faces/tributario/nfse/relatorio/cadastro-economico-sem-movimento.xhtml")
})
public class RelatorioCadastroEconomicoSemMovimentoControlador extends AbstractReport {

    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    private FiltroRelatorioCadastroEconomicoSemMovimento filtro;

    @URLAction(mappingId = "novo-relatorio-cadastro-economico-sem-movimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioCadastroEconomicoSemMovimento();
        filtro.setDataFinal(new Date());
        filtro.setDataInicial(DataUtil.getPrimeiroDiaMes(DataUtil.getAno(filtro.getDataFinal()), DataUtil.getMes(filtro.getDataFinal()) - 1));
    }

    public void gerarRelatorio() {
        try {
            filtro.validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Relatório de Contribuintes sem Movimentação");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco");
            dto.adicionarParametro("TITULO", "Secretaria Municipal de Finanças");
            dto.adicionarParametro("MODULO", "Tributário");
            dto.adicionarParametro("FILTROS", filtro.montarDescricaoFiltros());
            dto.adicionarParametro("CONDICAO", montaCondicao());
            dto.setApi("tributario/cadastro-economico-sem-movimento/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String montaCondicao() {
        StringBuilder condicao;
        condicao = new StringBuilder(" WHERE CAD.CLASSIFICACAOATIVIDADE = '" + ClassificacaoAtividade.PRESTACAO_DE_SERVICO.name() + "'" +
            " AND ((SIT.TIPONOTAFISCALSERVICO = '" + TipoNotaFiscalServico.ELETRONICA.name() + "'" +
            "     AND NOT EXISTS (SELECT 1 " +
            "                        FROM NOTAFISCAL NOTA " +
            "                      WHERE NOTA.PRESTADOR_ID = CAD.ID " +
            "                        AND TRUNC(NOTA.EMISSAO) BETWEEN TO_DATE('" + DataUtil.getDataFormatada(filtro.getDataInicial()) + "'" +
            " , 'dd/MM/yyyy') AND TO_DATE('" + DataUtil.getDataFormatada(filtro.getDataFinal()) + "'" +
            " , 'dd/MM/yyyy'))) " +
            "       OR SIT.TIPONOTAFISCALSERVICO != '" + TipoNotaFiscalServico.ELETRONICA.name() + "'" +
            " ) ");

        if (filtro.getCadastrosEconomicos() != null && !filtro.getCadastrosEconomicos().isEmpty()) {
            condicao.append(" AND CAD.ID IN (");
            for(Long idCadastro : filtro.getIdsCadastros()) {
                condicao.append(idCadastro);
                if(!filtro.getIdsCadastros().get(filtro.getIdsCadastros().size() - 1).equals(idCadastro)) {
                    condicao.append(" , ");
                }
            }
            condicao.append(") ");
        }
        if (!Strings.isNullOrEmpty(filtro.getCnpjInicial())) {
            condicao.append(" AND PJ.CNPJ >= ");
            condicao.append(StringUtil.retornaApenasNumeros(filtro.getCnpjInicial()));
        }
        if (!Strings.isNullOrEmpty(filtro.getCnpjFinal())) {
            condicao.append(" AND PJ.CNPJ <= ");
            condicao.append(StringUtil.retornaApenasNumeros(filtro.getCnpjFinal()));
        }
        if (filtro.getPortes() != null && !filtro.getPortes().isEmpty()) {
            condicao.append(" AND SIT.PORTE IN ( ");
            for(String name : filtro.getNamesPortes()) {
                condicao.append("'");
                condicao.append(name);
                condicao.append("'");
                if(!filtro.getNamesPortes().get(filtro.getNamesPortes().size() - 1).equals(name)) {
                    condicao.append(" , ");
                }
            }
            condicao.append(") ");
        }
        if (filtro.getSituacoesCadastrais() != null && !filtro.getSituacoesCadastrais().isEmpty()) {
            condicao.append(" AND SITCAD.SITUACAOCADASTRAL IN ( ");
            for(String name : filtro.getNamesSituacoesCadastrais()) {
                condicao.append("'");
                condicao.append(name);
                condicao.append("'");
                if(!filtro.getNamesSituacoesCadastrais().get(filtro.getNamesSituacoesCadastrais().size() - 1).equals(name)) {
                    condicao.append(" , ");
                }
            }
            condicao.append(") ");
        }
        if (filtro.getCnaes() != null && !filtro.getCnaes().isEmpty()) {
            condicao.append(" AND EXISTS(SELECT 1  " + "                  FROM ECONOMICOCNAE EC " + "               WHERE EC.CADASTROECONOMICO_ID = CAD.ID AND EC.CNAE_ID IN ( ");
            for(Long idCnae : filtro.getIdsCnaes()) {
                condicao.append(idCnae);
                if(!filtro.getIdsCnaes().get(filtro.getIdsCnaes().size() - 1).equals(idCnae)) {
                    condicao.append(" , ");
                }
            }
            condicao.append(" )) ");
        }
        if (filtro.getServicos() != null && !filtro.getServicos().isEmpty()) {
            condicao.append(" AND EXISTS(SELECT 1  " + "                  FROM CADASTROECONOMICO_SERVICO CE_S " + "               WHERE CE_S.CADASTROECONOMICO_ID = CAD.ID AND CE_S.SERVICO_ID IN ( ");
            for(Long idServico : filtro.getIdsServicos()) {
                condicao.append(idServico);
                if(!filtro.getIdsServicos().get(filtro.getIdsServicos().size() - 1).equals(idServico)) {
                    condicao.append(" , ");
                }
            }
            condicao.append(" )) ");
        }
        return condicao.toString();
    }

    public List<SelectItem> getPortes() {
        return Util.getListSelectItem(TipoPorte.values());
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoCadastralCadastroEconomico.values());
    }

    public FiltroRelatorioCadastroEconomicoSemMovimento getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioCadastroEconomicoSemMovimento filtro) {
        this.filtro = filtro;
    }
}
