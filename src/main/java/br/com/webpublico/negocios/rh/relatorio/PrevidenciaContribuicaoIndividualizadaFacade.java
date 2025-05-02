package br.com.webpublico.negocios.rh.relatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.rh.PrevidenciaContribuicaoIndividualizada;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.AssistentePrevidenciaContribuicao;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.beust.jcommander.internal.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by peixe on 20/08/2015.
 */
@Stateless
public class PrevidenciaContribuicaoIndividualizadaFacade implements Serializable {

    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ModuloExportacaoFacade moduloExportacaoFacade;
    @EJB
    private GrupoExportacaoFacade grupoExportacaoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;

    public RelatorioDTO montarRelatorioDTO(AssistentePrevidenciaContribuicao assistente) {
        PrevidenciaContribuicaoIndividualizada prev = new PrevidenciaContribuicaoIndividualizada();
        assistente.setContribuicaoIndividualizada(definirCabecalho(prev, assistente));
        ModuloExportacao modulo = buscarModuloExportacao(ModuloExportacao.MODULO_OUTROS);

        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("USUARIO", sistemaFacade.getLogin(), false);
        dto.setNomeRelatorio("Registro Individualizado de Contribuição");
        dto.adicionarParametro("PREFEITURA", "PREFEITURA MUNICIPAL DE RIO BRANCO");
        dto.adicionarParametro("SECRETARIA", assistente.getContribuicaoIndividualizada().getOrgao());
        dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.adicionarParametro("NOMERELATORIO", "Registro Individualizado de Contribuição");
        dto.adicionarParametro("INICIO", Util.parseMonthYear(assistente.getInicio()));
        dto.adicionarParametro("TERMINO", Util.parseMonthYear(assistente.getTermino() != null
            ? assistente.getTermino() : sistemaFacade.getDataOperacao()));
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("NUMERO_VINCULO", assistente.getContrato());
        dto.adicionarParametro("MATRICULA_VINCULO", assistente.getMatriculaFP().getMatricula());
        dto.adicionarParametro("PESSOA_CPF_VINCULO", assistente.getMatriculaFP().getPessoa().getCpf());
        dto.adicionarParametro("PESSOA_NOME_MATRICULA", assistente.getMatriculaFP().getPessoa().getNome());
        dto.adicionarParametro("DATA_INICIO", assistente.getInicio().getTime());
        dto.adicionarParametro("DATA_TERMINO", assistente.getTermino() != null ? assistente.getTermino().getTime() : sistemaFacade.getDataOperacao().getTime());
        dto.adicionarParametro("MODULO_OUTROS", modulo.getId());
        dto.adicionarParametro("OUTROS_BASE_PREVIDENCIA", buscarGrupoExportacaoPorCodigoEModuloExportacao(
            GrupoExportacao.OUTROS_BASE_PREVIDENCIA, modulo).getId());
        dto.adicionarParametro("OUTROS_VALOR_BRUTO", buscarGrupoExportacaoPorCodigoEModuloExportacao(
            GrupoExportacao.OUTROS_VALOR_BRUTO, modulo).getId());
        return dto;
    }

    private PrevidenciaContribuicaoIndividualizada definirCabecalho(PrevidenciaContribuicaoIndividualizada prev, AssistentePrevidenciaContribuicao assistente) {
        prev.setMatricula(assistente.getMatriculaFP().getMatricula());
        prev.setCpf(assistente.getMatriculaFP().getPessoa().getCpf());
        prev.setNome(assistente.getMatriculaFP().getPessoa().getNome());
        prev.setOrgao(buscarOrgaosVinculos(assistente));
        return prev;
    }

    private String buscarOrgaosVinculos(AssistentePrevidenciaContribuicao assistente) {
        List<VinculoFP> vinculos = Lists.newArrayList();
        Map<HierarquiaOrganizacional, String> hierarquias = new HashMap<>();
        StringBuilder retorno = new StringBuilder("");
        if (assistente.getContrato() == null || "multiplosVinculos".equals(assistente.getContrato())) {
            vinculos.addAll(vinculoFPFacade.buscarVinculosParaMatricula(assistente.getMatriculaFP().getMatricula()));
        } else {
            vinculos.add(vinculoFPFacade.recuperarVinculoPorMatriculaETipoOrNumero(assistente.getMatriculaFP().getMatricula(), assistente.getContrato()));
        }
        for (VinculoFP vinculo : vinculos) {
            HierarquiaOrganizacional hierarquia = getOrgao(vinculo);
            if (hierarquia != null) {
                if (!hierarquias.containsKey(hierarquia)) {
                    hierarquias.put(hierarquia, hierarquia.toString());
                }
            }
        }
        for (String value : hierarquias.values()) {
            retorno.append(value).append(", ");
        }
        return retorno.length() > 2 ? (retorno.substring(0, retorno.length() - 2) + ".") : "";
    }

    private HierarquiaOrganizacional getOrgao(VinculoFP vinculoFP) {
        try {
            LotacaoFuncional lotacao = lotacaoFuncionalFacade.buscarUltimaLotacaoVigentePorVinculoFP(vinculoFP);
            HierarquiaOrganizacional ho = null;
            HierarquiaOrganizacional filha = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(vinculoFP.getFinalVigencia() != null ? vinculoFP.getFinalVigencia() : sistemaFacade.getDataOperacao(), lotacao.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            if (filha != null) {
                ho = hierarquiaOrganizacionalFacade.getHierarquiaAdministrativaPorOrgaoAndTipoAdministrativa(filha.getCodigoDo2NivelDeHierarquia());
            }
            return ho;
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica("Não foi possível determinar a Hierarquia do Lotação Funcional. Erro:", e);
        }
    }

    private ModuloExportacao buscarModuloExportacao(Long codigoModulo) {
        return moduloExportacaoFacade.recuperaModuloExportacaoPorCodigo(codigoModulo);
    }

    private GrupoExportacao buscarGrupoExportacaoPorCodigoEModuloExportacao(Long codigoGrupo, ModuloExportacao modulo) {
        return grupoExportacaoFacade.recuperaGrupoExportacaoPorCodigoEModuloExportacao(codigoGrupo, modulo);
    }
}
