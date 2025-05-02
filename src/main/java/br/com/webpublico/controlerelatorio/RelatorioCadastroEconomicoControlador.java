/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteRelatorioBCE;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.negocios.RelatorioCadastroEconomicoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
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
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioCMC", pattern = "/tributario/cmc/relatorio-cmc/",
        viewId = "/faces/tributario/cadastromunicipal/relatorio/relatoriocadastroeconomico.xhtml"),
})

public class RelatorioCadastroEconomicoControlador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioCadastroEconomicoControlador.class);

    @EJB
    private RelatorioCadastroEconomicoFacade relatorioFacade;

    private AssistenteRelatorioBCE assistente;

    public RelatorioCadastroEconomicoControlador() {
        assistente = new AssistenteRelatorioBCE();
    }

    public AssistenteRelatorioBCE getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteRelatorioBCE assistente) {
        this.assistente = assistente;
    }

    public List<Logradouro> buscarLogradouros(String parte) {
        return relatorioFacade.buscarLogradouros(parte, "nome");
    }

    public List<CNAE> buscarCnaes(String parte) {
        return relatorioFacade.buscarCnaesAtivosPorGrauDeRisco(parte.trim(), assistente.getGrauDeRisco());
    }

    public List<SelectItem> buscarNaturezasJuridicas() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        List<NaturezaJuridica> listaNaturezaJuridica = relatorioFacade.buscarNaturezaJuridica(null);

        for (NaturezaJuridica natureza : listaNaturezaJuridica) {
            retorno.add(new SelectItem(natureza, natureza.getCodigo() + " - " + natureza.getDescricao()));
        }
        return retorno;
    }

    public List<UsuarioSistema> buscarUsuariosSistema(String parte) {
        return relatorioFacade.buscarUsuariosSistema(parte.trim(), "login");
    }

    public String getMascara() {
        if (("CPF").equals(assistente.getCpfCnpj())) {
            return "999.999.999-99";
        } else {
            return "99.999.999/9999-99";
        }
    }

    public List<SelectItem> getSituacaoCadastro() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (SituacaoCadastralCadastroEconomico sit : SituacaoCadastralCadastroEconomico.values()) {
            retorno.add(new SelectItem(sit, sit.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> listaTiposAutonomos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAutonomo tipo : relatorioFacade.buscarTiposAutonomo()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "novoRelatorioCMC", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        assistente = new AssistenteRelatorioBCE();
        assistente.setOrdenacao("C");
    }

    private RelatorioDTO montarParametrosComuns() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeRelatorio("Boletim De Cadastro Mobiliário - BCM");
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("USUARIO", relatorioFacade.buscarUsuarioCorrente().getNome());
        dto.adicionarParametro("LOGIN", relatorioFacade.buscarUsuarioCorrente().getLogin());
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
        dto.adicionarParametro("NOMERELATORIO", "BOLETIM DE CADASTRO MOBILIÁRIO - BCM");
        dto.adicionarParametro("URL_PORTAL", relatorioFacade.atribuirUrlPortal());
        dto.adicionarParametro("TIPO_RELATORIO", assistente.getTipoRelatorio().equals(0));
        dto.adicionarParametro("WHERE", assistente.getWhere());
        dto.adicionarParametro("ORDERBY", assistente.getOrdemSql());
        dto.setApi("tributario/boletim-cadastro-mobiliario/");

        return dto;
    }

    public void gerarRelatorioEconomico(CadastroEconomico cadastroEconomico) {
        try {
            assistente.setTipoRelatorio(1);
            assistente.setWhere(new StringBuilder());
            if (cadastroEconomico != null) {
                assistente.getWhere().append(" and ce.id = ").append(cadastroEconomico.getId());
                assistente.setOrdemSql(" order by ce.inscricaocadastral ");
            }
            RelatorioDTO dto = montarParametrosComuns();
            dto.adicionarParametro("FILTROS", "Inscrição Cadastral : " + cadastroEconomico.getInscricaoCadastral());
            ReportService.getInstance().gerarRelatorio(relatorioFacade.buscarUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (Exception e) {
            logger.error("Erro ao gerar relatŕoio por Cadastro. ", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void gerarRelatorio(String tiporelatorioExtensao) {
        try {
            assistente.setWhere(new StringBuilder());
            relatorioFacade.montarCondicao(assistente);
            RelatorioDTO dto = montarParametrosComuns();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tiporelatorioExtensao));
            dto.adicionarParametro("FILTROS", "Inscrição Cadastral : " + assistente.getCriterios().toString());
            ReportService.getInstance().gerarRelatorio(relatorioFacade.buscarUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (Exception e) {
            logger.error("Erro ao gerar relatório. ", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public List<SelectItem> getClassificacaoAtividade() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ClassificacaoAtividade object : ClassificacaoAtividade.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposIss() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoIssqn object : TipoIssqn.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getGrausDeRisco() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "TODOS"));
        for (GrauDeRiscoDTO object : GrauDeRiscoDTO.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getBooleanValues() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "TODOS"));
        toReturn.add(new SelectItem(Boolean.TRUE, "Sim"));
        toReturn.add(new SelectItem(Boolean.FALSE, "Não"));
        return toReturn;
    }
}
