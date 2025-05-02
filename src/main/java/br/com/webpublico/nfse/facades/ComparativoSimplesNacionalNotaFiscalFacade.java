package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.enums.RegimeTributario;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.nfse.domain.ComparativoSimplesNacionalNotaFiscal;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.domain.dtos.LinhaCompSimplesNacionalNfseDTO;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ComparativoSimplesNacionalNotaFiscalFacade extends AbstractFacade<ComparativoSimplesNacionalNotaFiscal> {

    private static final DecimalFormat FORMATACAO_VALOR = new DecimalFormat("0.00");

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ConfiguracaoNfseFacade configuracaoNfseFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;

    public ComparativoSimplesNacionalNotaFiscalFacade() {
        super(ComparativoSimplesNacionalNotaFiscal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<ComparativoSimplesNacionalNotaFiscal> gerarComparativo(AssistenteBarraProgresso assistente,
                                                                         ComparativoSimplesNacionalNotaFiscal comparativo) throws Exception {

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Buscando informações para geração do arquivo.");
        List<LinhaCompSimplesNacionalNfseDTO> dados = buscarDados(comparativo);

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Gerando o arquivo.");
        comparativo.setArquivo(gerarArquivo(comparativo, dados));

        comparativo = salvarRetornando(comparativo);
        return new AsyncResult<>(comparativo);
    }

    private List<LinhaCompSimplesNacionalNfseDTO> buscarDados(ComparativoSimplesNacionalNotaFiscal comparativo) {
        Date competenciaInicial = DataUtil.getDia(1, comparativo.getMesInicial().getNumeroMes(), comparativo.getAnoInicial());
        Date competenciaFinal = DataUtil.getDia(1, comparativo.getMesFinal().getNumeroMes(), comparativo.getAnoFinal());
        competenciaFinal = DataUtil.ultimoDiaMes(competenciaFinal).getTime();

        Query q = em.createNativeQuery(" with valores_nfse as (select " +
                "                         ce.id as id_cadastro, " +
                "                         to_date(to_char(dec.competencia, 'yyyy-MM'), 'yyyy-MM') as competencia, " +
                "                         dec.basecalculo as base_calculo " +
                "                         from notafiscal nf " +
                "                        inner join declaracaoprestacaoservico dec on dec.id = nf.declaracaoprestacaoservico_id " +
                "                        inner join cadastroeconomico ce on ce.id = nf.prestador_id " +
                "                      where dec.competencia between :competencia_inicial and :competencia_final " +
                "                        and dec.situacao != :situacao) " +
                " select pj.cnpj, coalesce(vn.competencia, :competencia_inicial), coalesce(sum(vn.base_calculo), 0) " +
                "    from cadastroeconomico ce " +
                "   inner join pessoajuridica pj on pj.id = ce.pessoa_id " +
                "   inner join enquadramentofiscal eq on eq.cadastroeconomico_id = ce.id and eq.fimvigencia is null " +
                "   left join valores_nfse vn on vn.id_cadastro = ce.id " +
                " where eq.regimetributario = :regime " +
                "  and valida_cpf_cnpj(pj.cnpj) = 'S' " +
                (comparativo.getCadastroEconomico() != null ? " and ce.id = :cadastro_economico " : " ") +
                " group by pj.cnpj, vn.competencia ");
        
        q.setParameter("competencia_inicial", competenciaInicial);
        q.setParameter("competencia_final", competenciaFinal);
        q.setParameter("situacao", SituacaoNota.CANCELADA.name());
        q.setParameter("regime", RegimeTributario.SIMPLES_NACIONAL.name());
        if (comparativo.getCadastroEconomico() != null) {
            q.setParameter("cadastro_economico", comparativo.getCadastroEconomico().getId());
        }
        List resultList = q.getResultList();
        List<LinhaCompSimplesNacionalNfseDTO> linhas = Lists.newArrayList();
        if (!resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                linhas.add(new LinhaCompSimplesNacionalNfseDTO((String) obj[0], (Date) obj[1], (BigDecimal) obj[2]));
            }
        }
        preencherCompetenciasSemMovimento(competenciaInicial, competenciaFinal, linhas);
        Collections.sort(linhas);
        return linhas;
    }

    private void preencherCompetenciasSemMovimento(Date competenciaInicial, Date competenciaFinal, List<LinhaCompSimplesNacionalNfseDTO> linhas) {
        HashSet<String> cnpjs = new HashSet<>();
        for (LinhaCompSimplesNacionalNfseDTO linha : linhas) {
            cnpjs.add(linha.getCnpj());
        }
        for (String cnpj : cnpjs) {
            Date competenciaAtual = competenciaInicial;
            while (competenciaAtual.before(competenciaFinal)) {
                if (!hasCompetencia(cnpj, competenciaAtual, linhas)) {
                    linhas.add(new LinhaCompSimplesNacionalNfseDTO(cnpj, competenciaAtual, BigDecimal.ZERO));
                }
                competenciaAtual = DataUtil.adicionarMeses(competenciaAtual, 1);
            }
        }
    }

    public boolean hasCompetencia(String cnpj, Date competencia, List<LinhaCompSimplesNacionalNfseDTO> linhas) {
        if (linhas == null || linhas.isEmpty()) {
            return false;
        }
        for (LinhaCompSimplesNacionalNfseDTO linha : linhas) {
            if (linha.getCnpj().equals(cnpj) &&
                DataUtil.dataSemHorario(linha.getCompetencia()).equals(DataUtil.dataSemHorario(competencia))) {
                return true;
            }
        }
        return false;
    }

    private Arquivo gerarArquivo(ComparativoSimplesNacionalNotaFiscal comparativo, List<LinhaCompSimplesNacionalNfseDTO> dados) {
        try {
            ConfiguracaoNfse configuracaoNfse = configuracaoNfseFacade.recuperarConfiguracao();
            String codigoSiafi = configuracaoNfse.getCidade() != null ?
                configuracaoNfse.getCidade().getCodigoSiafi() : "";
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            if (dados != null) {
                for (LinhaCompSimplesNacionalNfseDTO dado : dados) {
                    buffer.write(StringUtil.retornaApenasNumeros(dado.getCnpj()).getBytes());
                    buffer.write(";".getBytes());
                    buffer.write((DataUtil.getDataFormatada(dado.getCompetencia(), "yyyy-MM")).getBytes());
                    buffer.write(";".getBytes());
                    buffer.write(formatarValor(dado.getBaseCalculo()).getBytes());
                    buffer.write(System.lineSeparator().getBytes());
                }
            }
            Arquivo arquivo = new Arquivo();
            arquivo.setNome(codigoSiafi + "_servicos_" + comparativo.getAnoInicial() + ".txt");
            arquivo.setMimeType("text/plain");
            arquivo.setDescricao("Comparativo do Simples Nacional x NFS-e");
            return arquivoFacade.retornaArquivoSalvo(arquivo, new ByteArrayInputStream(buffer.toByteArray()));
        } catch (Exception e) {
            logger.error("Erro ao gerar arquivo comparativo do simples nacional x nfs-e", e);
            return null;
        }
    }

    public String formatarValor(Number valor) {
        return FORMATACAO_VALOR.format(valor);
    }
}
