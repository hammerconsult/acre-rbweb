package br.com.webpublico.negocios;

import br.com.webpublico.BarCode;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteRelatorioBCE;
import br.com.webpublico.enums.TipoNaturezaJuridica;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Strings;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Stateless
public class RelatorioCadastroEconomicoFacade {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroEconomicoImpressaoHistFacade cadastroEconomicoImpressaoHistFacade;


    public RelatorioCadastroEconomicoFacade() {
    }

    public List<Logradouro> buscarLogradouros(String parte, String... atributos) {
        return logradouroFacade.listaFiltrando(parte.trim(), atributos);
    }

    public List<CNAE> buscarCnaesAtivosPorGrauDeRisco(String parte, GrauDeRiscoDTO grauDeRisco) {
        return cnaeFacade.buscarCnaesAtivosPorGrauDeRisco(parte, grauDeRisco);
    }

    public List<NaturezaJuridica> buscarNaturezaJuridica(TipoNaturezaJuridica tipo) {
        return naturezaJuridicaFacade.listaNaturezaJuridicaPorTipo(tipo);
    }

    public List<UsuarioSistema> buscarUsuariosSistema(String parte, String... atributos) {
        return usuarioSistemaFacade.listaFiltrando(parte, atributos);
    }

    public List<TipoAutonomo> buscarTiposAutonomo() {
        return tipoAutonomoFacade.lista();
    }

    public UsuarioSistema buscarUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public Date buscarDataCorrente() {
        return sistemaFacade.getDataOperacao();
    }

    public String gerarChaveAutenticidade(CadastroEconomicoImpressaoHist cadastroEconomicoImpressaoHist) {
        return cadastroEconomicoImpressaoHistFacade.gerarChaveAutenticidade(cadastroEconomicoImpressaoHist);
    }

    public void salvarHistoricoCadastro(CadastroEconomicoImpressaoHist cadastroEconomicoImpressaoHist) {
        cadastroEconomicoImpressaoHistFacade.salvar(cadastroEconomicoImpressaoHist);
    }

    public CadastroEconomico inicializarHistImpressao(Long idCadastro) {
        return cadastroEconomicoFacade.inicializarHistoricosImpressao(idCadastro);
    }

    public String atribuirUrlPortal() {
        return cadastroEconomicoFacade.atribuirUrlPortal();
    }

    public CadastroEconomico recuperarCnaesCadastro(Long idCmc) {
        return cadastroEconomicoFacade.recuperarEconomicoCnaeCadastro(idCmc);
    }

    public void montarCondicao(AssistenteRelatorioBCE assistente) {
        if (Strings.isNullOrEmpty(assistente.getInscricaoCadastralInicial())) {
            assistente.setInscricaoCadastralInicial("");
        }
        if (Strings.isNullOrEmpty(assistente.getInscricaoCadastralFinal())) {
            assistente.setInscricaoCadastralFinal("9999999999");
        }

        String juncao = " and ";
        assistente.setOrdemSql("");
        assistente.setCriterios(new StringBuilder());

        if (!Strings.isNullOrEmpty(assistente.getInscricaoCadastralInicial())) {
            assistente.getWhere().append(juncao).append(" to_number(ce.inscricaocadastral) >= ").append(assistente.getInscricaoCadastralInicial());
            juncao = " and ";
            assistente.getCriterios().append(" CMC Inicial: ").append(assistente.getInscricaoCadastralInicial()).append("; ");
        }
        if (!Strings.isNullOrEmpty(assistente.getInscricaoCadastralFinal())) {
            assistente.getWhere().append(juncao).append(" to_number(ce.inscricaocadastral) <= ").append(assistente.getInscricaoCadastralFinal());
            juncao = " and ";
            assistente.getCriterios().append(" CMC Final: ").append(assistente.getInscricaoCadastralFinal()).append("; ");
        }

        if (assistente.getDataCadastroEmpresa() != null) {
            assistente.getWhere().append(juncao).append(" ce.abertura = ").append("to_date('").append(DataUtil.getDataFormatada(assistente.
                getDataCadastroEmpresa())).append("', 'dd/MM/yyyy')");
            juncao = " and ";
            assistente.getCriterios().append(" Data de Cadastro: ").append(DataUtil.getDataFormatada(assistente.getDataCadastroEmpresa())).append("; ");
        }

        if (assistente.getCadastroEconomico() != null) {
            assistente.getWhere().append(juncao).append(" ce.id = ").append(assistente.getCadastroEconomico().getId());
            juncao = " and ";
            assistente.getCriterios().append(" Razão Social / C.M.C.: ").append(assistente.getCadastroEconomico().getPessoa());
        }

        if (assistente.getSituacao() != null) {
            assistente.getWhere().append(juncao).append("situacao.situacaocadastral = '").append(assistente.getSituacao().name()).append("' ");
            juncao = " and ";
            assistente.getCriterios().append(" Situação: ").append(assistente.getSituacao().getDescricao());
        }

        if (assistente.getNaturezaJuridica() != null) {
            assistente.getWhere().append(juncao).append("ce.naturezajuridica_id = ").append(assistente.getNaturezaJuridica().getId());
            juncao = " and ";
            assistente.getCriterios().append(" Natureza Jurídica: ").append(assistente.getNaturezaJuridica().getDescricao());
        }

        if (assistente.getTipoAutonomo() != null) {
            assistente.getWhere().append(juncao).append("ce.tipoautonomo_id = ").append(assistente.getTipoAutonomo().getId());
            juncao = " and ";
            assistente.getCriterios().append(" Tipo de Autônomo: ").append(assistente.getTipoAutonomo().getDescricao());
        }

        if (assistente.getPorte() != null) {
            assistente.getWhere().append(juncao).append("enq.porte = '").append(assistente.getPorte().name()).append("'");
            juncao = " and ";
            assistente.getCriterios().append(" Porte: ").append(assistente.getPorte().getDescricao());
        }

        if (assistente.getRegimeTributario() != null) {
            assistente.getWhere().append(juncao).append("enq.regimetributario = '").append(assistente.getRegimeTributario().name()).append("'");
            juncao = " and ";
            assistente.getCriterios().append(" Regime Tributário: ").append(assistente.getRegimeTributario().getDescricao());
        }

        if (assistente.getUsuarioSistema() != null) {
            assistente.getWhere().append(juncao).append("ce.usuariodocadastro_id = ").append(assistente.getUsuarioSistema().getId());
            juncao = " and ";
            assistente.getCriterios().append(" Usuário do Cadastro: ").append(assistente.getUsuarioSistema().getLogin());
        }

        if (assistente.getCpfCnpj() != null && !assistente.getCpfCnpj().trim().equals("")) {
            assistente.getWhere().append(juncao).append("replace(replace(replace(pf.cpf,'.',''),'-',''),'/','')  = '").append(StringUtil.removeCaracteresEspeciaisSemEspaco(assistente.getCpfCnpj())).append("' ");
            assistente.getWhere().append(" or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','')  = '").append(StringUtil.removeCaracteresEspeciaisSemEspaco(assistente.getCpfCnpj())).append("' ");
            juncao = " and ";
            assistente.getCriterios().append(" CPF/CNPJ: ").append(assistente.getCpfCnpj());
        }

        if (assistente.getLogradouro() != null && assistente.getLogradouro().getId() != null) {
            assistente.getWhere().append(juncao).append(" l.id = ").append(assistente.getLogradouro().getId());
            juncao = " and ";
            assistente.getCriterios().append(" Logradouro: ").append(assistente.getLogradouro().getNome()).append("; ");
        }

        if (assistente.getTipoIss() != null) {
            assistente.getWhere().append(juncao).append(" enq.tipoIssqn = '").append(assistente.getTipoIss()).append("'");
            juncao = " and ";
            assistente.getCriterios().append(" Tipo de ISSQN: ").append(assistente.getTipoIss().getDescricao()).append("; ");
        }

        if (assistente.getOcupacao() != null && !assistente.getOcupacao().isEmpty()) {
            assistente.getWhere().append(juncao).append(" ce.areautilizacao >= ").append(assistente.getOcupacao());
            juncao = " and ";
            assistente.getCriterios().append(" Ocupação Acima de (Em Metros): ").append(assistente.getOcupacao()).append("; ");
        }

        if (assistente.getClassifAtividade() != null && !assistente.getClassifAtividade().isEmpty()) {
            assistente.getWhere().append(juncao).append(" ce.classificacaoatividade = '").append(assistente.getClassifAtividade()).append("'");
            juncao = " and ";
            assistente.getCriterios().append(" Classificação da Atividade: ").append(assistente.getClassifAtividade()).append("; ");
        }

        if (assistente.getSubstitutoTributario() != null) {
            assistente.getWhere().append(juncao).append(" coalesce(enq.substitutotributario, 0) = ")
                .append(assistente.getSubstitutoTributario() ? 1 : 0);
            juncao = " and ";
            assistente.getCriterios().append(" Substituto Tributário: ").append(assistente.getSubstitutoTributario() ? "Sim" : "Não").append("; ");
        }

        if (assistente.getCnae() != null && assistente.getCnae().getId() != null) {
            assistente.getWhere().append(juncao).append(" exists (select 1 " +
                "                           from economicocnae e_cnae " +
                "                        where e_cnae.cadastroeconomico_id = ce.id " +
                "                          and (e_cnae.fim is null or e_cnae.fim > sysdate) " +
                "                          and e_cnae.cnae_id = ").append(assistente.getCnae().getId()).append(") ");
            assistente.getCriterios().append(" CNAE: ").append(assistente.getCnae().getToStringAutoComplete()).append("; ");
        }

        if (assistente.getGrauDeRisco() != null) {
            assistente.getWhere().append(juncao).append(" exists (select 1 " +
                "                           from economicocnae e_cnae " +
                "                           inner join cnae cnae on cnae.id = e_cnae.cnae_id " +
                "                        where e_cnae.cadastroeconomico_id = ce.id " +
                "                          and (e_cnae.fim is null or e_cnae.fim > sysdate) " +
                "                          and cnae.grauDeRisco = '").append(assistente.getGrauDeRisco().name()).append("') ");
            assistente.getCriterios().append(" Grau de Risco: ").append(assistente.getGrauDeRisco().getDescricao()).append("; ");
        }

        if (assistente.getMei() != null) {
            assistente.getWhere().append(juncao).append(" coalesce(ce.mei,0) = ").append(assistente.getMei() ? "1" : "0");
            assistente.getCriterios().append(" MEI: ").append(assistente.getMei() ? "Sim" : "Não").append("; ");
        }

        switch (assistente.getOrdenacao()) {
            case "C":
                assistente.setOrdemSql(" pf.nome,pj.razaosocial");
                assistente.getCriterios().append(" Ordenado por: Contribuinte").append("; ");
                break;
            case "N":
                assistente.setOrdemSql(" cast(ce.inscricaocadastral as number) ");
                assistente.getCriterios().append(" Ordenação : Numérica");
                break;
            case "A":
                assistente.setOrdemSql(" ce.classificacaoatividade , pf.nome ");
                assistente.getCriterios().append(" Odernado por: Classificação Atividade e Nome Contribuinte").append("; ");
                break;
            default:
                break;
        }

        if (!Strings.isNullOrEmpty(assistente.getOrdemSql())) {
            assistente.setOrdemSql(" order by " + assistente.getOrdemSql());
        } else {
            assistente.setOrdemSql(" order by ce.inscricaocadastral ");
        }
    }
}
