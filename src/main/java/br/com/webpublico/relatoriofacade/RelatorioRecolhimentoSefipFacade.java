package br.com.webpublico.relatoriofacade;

import br.com.webpublico.controlerelatorio.RelatorioRecolhimentoSEFIPControlador;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Sefip;
import br.com.webpublico.entidadesauxiliares.DetalhesRelatorioRecolhimentoSefip;
import br.com.webpublico.entidadesauxiliares.ReferenciaFPFuncao;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.SefipNomeReduzido;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.FuncoesFolhaFacade;
import br.com.webpublico.negocios.ReferenciaFPFacade;
import br.com.webpublico.negocios.SefipFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Desenvolvimento on 06/09/2016.
 */
@Stateless
public class RelatorioRecolhimentoSefipFacade implements Serializable {

    private static final String EVENTO = "900";
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SefipFacade sefipFacade;
    @EJB
    private FuncoesFolhaFacade funcoesFolhaFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    private BigDecimal porcentagemPatronal;

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteBarraProgresso> processarContratos(RelatorioRecolhimentoSEFIPControlador.Filtros filtros, List<DetalhesRelatorioRecolhimentoSefip> retorno, AssistenteBarraProgresso assistenteBarraProgresso, List<ContratoFP> contratos) {
        if (!contratos.isEmpty()) {
            for (ContratoFP contrato : contratos) {
                contrato.setMes(filtros.getSefip().getMes());
                contrato.setAno(filtros.getSefip().getAno());

                DetalhesRelatorioRecolhimentoSefip item = new DetalhesRelatorioRecolhimentoSefip();
                item.setOrgao(contrato.getHierarquiaOrganizacional().toString());
                item.setMatricula(contrato.getMatriculaFP().getMatricula());
                item.setContrato(contrato.getContratoFP().getNumero());
                item.setNome(contrato.getMatriculaFP().getPessoa().getNome());
                item.setBase(getBase(filtros.getSefip(), contrato));

                item.setPorcentagemSegurado(getPorcentagemSegurado(filtros.getSefip(), contrato));
                item.setValorSegurado(getValorSegurado(filtros.getSefip(), contrato));

                item.setPorcentagemPatronal(getPorcentagemPatronal(contrato));
                item.setValorPatronal(getValorPatronal(filtros.getSefip(), contrato));

                if (filtros.getSalarioMaternidade()) {
                    item.setSalarioMaternidade(getSalarioMaternidade(filtros.getSefip(), contrato));
                }
                if (filtros.getSalarioFamilia()) {
                    item.setSalarioFamilia(getSalarioFamilia(filtros.getSefip(), contrato));
                }

                retorno.add(item);
                assistenteBarraProgresso.conta();
            }
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }

    private BigDecimal getSalarioFamilia(Sefip sefip, ContratoFP contrato) {
        return sefipFacade.getTodosValoresDaUnidadePorEventoAndContrato(sefip, contrato, contrato.getHierarquiaOrganizacional(), SefipNomeReduzido.SALARIO_FAMILIA);
    }

    private BigDecimal getSalarioMaternidade(Sefip sefip, ContratoFP contrato) {
        return sefipFacade.getTodosValoresDaUnidadePorEventoAndContrato(sefip, contrato, contrato.getHierarquiaOrganizacional(), SefipNomeReduzido.SALARIO_MATERNIDADE);
    }

    private BigDecimal getPorcentagemPatronal(ContratoFP contrato) {
        if (porcentagemPatronal == null) {
            ReferenciaFPFuncao referenciaFPFuncao = referenciaFPFacade.obterReferenciaValorFP(contrato, "23", new Date());
            porcentagemPatronal = BigDecimal.valueOf(referenciaFPFuncao.getValor());
        }
        return porcentagemPatronal;
    }

    private BigDecimal getValorPatronal(Sefip sefip, ContratoFP contrato) {
        ReferenciaFPFuncao referenciaFPFuncao = referenciaFPFacade.obterReferenciaValorFP(contrato, "23", new Date());
        BigDecimal baseDoEvento = sefipFacade.getBaseDoEvento(sefip, contrato, EVENTO);

        return baseDoEvento.multiply(new BigDecimal(referenciaFPFuncao.getValor() / 100));
    }

    private BigDecimal getPorcentagemSegurado(Sefip sefip, ContratoFP contrato) {
        return sefipFacade.getSomaDosEventosReferencia(sefip, contrato, SefipNomeReduzido.VALOR_DESCONTADO_DO_SEGURADO);
    }

    private BigDecimal getValorSegurado(Sefip sefip, ContratoFP contrato) {
        BigDecimal valorDescontadoSegurado = BigDecimal.ZERO;
        valorDescontadoSegurado = sefipFacade.getSomaDosEventos(sefip, contrato, SefipNomeReduzido.VALOR_DESCONTADO_DO_SEGURADO);
        if (sefip.getMes() == 13) {
            if (sefipFacade.doisOuMaisContratosAtivosNaPrefeitura(contrato)) {
                contrato.setOcorrenciaSEFIP(sefipFacade.getOcorrenciaSefipDoTipoMultiplosVinculos());
                valorDescontadoSegurado = sefipFacade.getSomaDosEventos(sefip, contrato, SefipNomeReduzido.VALOR_DESCONTADO_DO_SEGURADO);
            }
        }
        return valorDescontadoSegurado;
    }

    private BigDecimal getBase(Sefip sefip, ContratoFP c) {
        return sefipFacade.getBaseDoEvento(sefip, c, EVENTO);
    }

    public List<HierarquiaOrganizacional> buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado(RelatorioRecolhimentoSEFIPControlador.Filtros filtros) {
        if (filtros != null) {
            return entidadeFacade.buscarHierarquiasDaEntidade(filtros.getSefip().getEntidade(), CategoriaDeclaracaoPrestacaoContas.SEFIP, filtros.getPrimeiroDiaDoMes(), filtros.getUltimoDiaDoMes());
        }
        return new ArrayList<>();
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public SefipFacade getSefipFacade() {
        return sefipFacade;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<Boolean> buscarContratos(List<ContratoFP> contratosGeral, List<HierarquiaOrganizacional> hierarquias, RelatorioRecolhimentoSEFIPControlador.Filtros filtros, AssistenteBarraProgresso assistenteBarraProgresso) {
        for (HierarquiaOrganizacional hierarquia : hierarquias) {
            List<ContratoFP> contratos = Lists.newArrayList();
            contratos.addAll(getSefipFacade().getContratosComInss(filtros.getSefip(), hierarquia));
            contratos.addAll(getSefipFacade().getContratosSemInss(filtros.getSefip(), hierarquia));
            contratos = new ArrayList(new HashSet(contratos));
            for (ContratoFP contrato : contratos) {
                contrato.setHierarquiaOrganizacional(hierarquia);
            }
            contratosGeral.addAll(contratos);
            assistenteBarraProgresso.setTotal(assistenteBarraProgresso.getTotal() + contratos.size());
        }
        return new AsyncResult<>(Boolean.TRUE);
    }
}
