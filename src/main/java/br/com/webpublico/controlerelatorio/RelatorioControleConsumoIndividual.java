package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.AbastecimentoObjetoFrotaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 21/10/14
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "relatorioControleConsumoIndividual")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioControleConsumoIndividual", pattern = "/relatorio-controle-consumo-individual/", viewId = "/faces/administrativo/frota/relatorios/relatorioControleConsumoIndividual.xhtml")
})
public class RelatorioControleConsumoIndividual {

    @EJB
    private SistemaFacade sistemaFacade;
    private Filtros filtros;
    private StringBuilder criteriosUtilizados;

    @URLAction(mappingId = "relatorioControleConsumoIndividual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparFiltros() {
        filtros = new Filtros();
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Todos"));
        for (Mes mes : Mes.values()) {
            toReturn.add(new SelectItem(mes, mes.getNumeroMes().toString()));
        }
        return toReturn;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            String nomeRelatorio = "RELATÓRIO DE CONTROLE DE CONSUMO INDIVIDUAL";
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("MODULO", "FROTAS");
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("condicao", montarFiltrosECriteriosUtilizados());
            dto.adicionarParametro("FILTROS", criteriosUtilizados.toString());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/controle-consumo-individual/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarFiltrosECriteriosUtilizados() {
        String juncao = " WHERE ";
        criteriosUtilizados = new StringBuilder();
        StringBuilder where = new StringBuilder();
        if (filtros != null) {
            if (filtros.getTipoObjetoFrota() != null) {
                where = where.append(juncao);
                where = where.append(" obj.tipoobjetofrota = ");
                where = where.append("'");
                where = where.append(filtros.getTipoObjetoFrota().name());
                where = where.append("'");
                juncao = " and ";
                criteriosUtilizados = criteriosUtilizados.append(" Tipo: ");
                criteriosUtilizados = criteriosUtilizados.append(filtros.getTipoObjetoFrota().getDescricao());
                if (filtros.getObjetoFrota() != null) {
                    where = where.append(juncao);
                    where = where.append(" obj.id = ");
                    where = where.append(filtros.getObjetoFrota().getId());
                    juncao = " and ";
                    if (filtros.getTipoObjetoFrota().equals(TipoObjetoFrota.VEICULO)) {
                        criteriosUtilizados = criteriosUtilizados.append(" Veículo: ");
                        criteriosUtilizados = criteriosUtilizados.append(((Veiculo) filtros.getObjetoFrota()).toString());
                    } else {
                        criteriosUtilizados = criteriosUtilizados.append(" Equipamento/Máquina: ");
                        criteriosUtilizados = criteriosUtilizados.append(((Equipamento) filtros.getObjetoFrota()).toString());
                    }
                }
                if (filtros.getMotorista() != null) {
                    where = where.append(juncao);
                    where = where.append(" mot.id = ");
                    where = where.append(filtros.getMotorista().getId());
                    juncao = " and ";
                    criteriosUtilizados = criteriosUtilizados.append(" Motorista: ");
                    criteriosUtilizados = criteriosUtilizados.append(filtros.getMotorista());
                }
                if (filtros.getPessoaFisica() != null) {
                    where = where.append(juncao);
                    where = where.append(" pf.id = ");
                    where = where.append(filtros.getPessoaFisica().getId());
                    juncao = " and ";
                    criteriosUtilizados = criteriosUtilizados.append(" Operador: ");
                    criteriosUtilizados = criteriosUtilizados.append(filtros.getPessoaFisica().getId());
                }
            }
            if (filtros.getMes() != null) {
                where.append(juncao);
                where.append(" extract(month from abast.dataabastecimento) = ");
                where.append(filtros.getMes().getNumeroMes());
                criteriosUtilizados = criteriosUtilizados.append(" Mês: ");
                criteriosUtilizados = criteriosUtilizados.append(filtros.getMes().getNumeroMes());
            }
        }
        return where.toString();
    }

    public void processaSelecaoOrgaoEntidadeFundo() {
        filtros.setObjetoFrota(null);
    }

    public Filtros getFiltros() {
        return filtros;
    }

    public void setFiltros(Filtros filtros) {
        this.filtros = filtros;
    }

    public class Filtros {

        private HierarquiaOrganizacional hierarquiaOrganizacional;
        private TipoObjetoFrota tipoObjetoFrota;
        private ObjetoFrota objetoFrota;
        private Motorista motorista;
        private PessoaFisica pessoaFisica;
        private Mes mes;

        public Filtros() {
        }

        public HierarquiaOrganizacional getHierarquiaOrganizacional() {
            return hierarquiaOrganizacional;
        }

        public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
            this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        }

        public TipoObjetoFrota getTipoObjetoFrota() {
            return tipoObjetoFrota;
        }

        public void setTipoObjetoFrota(TipoObjetoFrota tipoObjetoFrota) {
            this.tipoObjetoFrota = tipoObjetoFrota;
        }

        public ObjetoFrota getObjetoFrota() {
            return objetoFrota;
        }

        public void setObjetoFrota(ObjetoFrota objetoFrota) {
            this.objetoFrota = objetoFrota;
        }

        public Motorista getMotorista() {
            return motorista;
        }

        public void setMotorista(Motorista motorista) {
            this.motorista = motorista;
        }

        public PessoaFisica getPessoaFisica() {
            return pessoaFisica;
        }

        public void setPessoaFisica(PessoaFisica pessoaFisica) {
            this.pessoaFisica = pessoaFisica;
        }

        public Mes getMes() {
            return mes;
        }

        public void setMes(Mes mes) {
            this.mes = mes;
        }
    }

    public void processaMudancaTipoObjetoFrota() {
        filtros.setObjetoFrota(null);
        filtros.setMotorista(null);
        filtros.setPessoaFisica(null);
    }

}
