package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoExportacaoDebitosIPTU;
import br.com.webpublico.negocios.tributario.dao.JdbcExportacaoDebitosIptuDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoExportacaoDebitosIPTU;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author octavio
 */
@Stateless
public class ExportacaoDebitosIPTUFacade extends AbstractFacade<ExportacaoDebitosIPTU> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConvenioListaDebitosFacade convenioListaDebitosFacade;
    @EJB
    private SingletonGeradorCodigoExportacaoDebitosIPTU singletonGeradorCodigoExportacaoDebitosIPTU;
    @EJB
    private ArquivoFacade arquivoFacade;

    public ExportacaoDebitosIPTUFacade() {
        super(ExportacaoDebitosIPTU.class);
    }

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ExportacaoDebitosIPTU recuperar(Object id) {
        return super.recuperar(id);
    }

    private List<ExportacaoDebitosIPTULinha> buscarLinhasDoProcesso(Long idExportacao) {
        String sql = "select * from ExportacaoDebitosIPTULinha where exportacaoDebitosIPTU_id = :id order by indice";
        return em.createNativeQuery(sql, ExportacaoDebitosIPTULinha.class).setParameter("id", idExportacao).getResultList();
    }

    private List<String> buscarInscricoesCadastroImobiliarios(ExportacaoDebitosIPTU selecionado) {
        String sql = "select ci.inscricaoCadastral from CadastroImobiliario ci " +
            "where ci.inscricaoCadastral between :inscricaoInicial and :inscricaoFinal" +
            "  and coalesce(ci.ativo,0) = 1";
        Query q = em.createNativeQuery(sql);
        q.setParameter("inscricaoInicial", selecionado.getInscricaoInicial());
        q.setParameter("inscricaoFinal", selecionado.getInscricaoFinal());
        List<String> lista = q.getResultList();
        List<String> inscricoes = Lists.newArrayList();
        for (String inscricao : lista) {
            inscricoes.add(inscricao);
        }
        return inscricoes;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 6)
    public StreamedContent montarArquivoParaDownload(ExportacaoDebitosIPTU exportacaoDebitosIPTU) {
        String nomeArquivo = exportacaoDebitosIPTU.getTipoArqExportacaoDebitosIPTU().getDescricao() + " - " +
            DataUtil.converterAnoMesDia(exportacaoDebitosIPTU.getDataGeracao()) + " - " + exportacaoDebitosIPTU.getNumeroRemessa() + ".txt";
        try {
            List<String> linhas = buscarLinhasDoArquivo(exportacaoDebitosIPTU.getId());
            StringBuilder sb = new StringBuilder();
            for (String linha : linhas) {
                sb.append(linha).append("\n");
            }
            byte[] bytes = StandardCharsets.ISO_8859_1.encode(sb.toString()).array();
            try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                return new DefaultStreamedContent(inputStream, "text/plain", nomeArquivo);
            }
        } catch (Exception ex) {
            logger.error("Erro ao gerar arquivo de texto. ", ex);
        }
        return null;
    }

    private List<String> buscarLinhasDoArquivo(Long idExportacao) {
        String sql = " select linhas.linha from exportacaodebitosiptulinha linhas " +
            " where linhas.exportacaodebitosiptu_id = :idExportacao " +
            " order by indice ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idExportacao", idExportacao);

        List<String> linhas = q.getResultList();
        return linhas != null ? linhas : Lists.<String>newArrayList();
    }

    public void excluirLinhas(ExportacaoDebitosIPTU exportacaoDebitosIPTU) {
        JdbcExportacaoDebitosIptuDAO exportacaoDebitosIptuDAO = Util.recuperarSpringBean(JdbcExportacaoDebitosIptuDAO.class);
        exportacaoDebitosIptuDAO.excluirLinhasExportacaoIPTU(exportacaoDebitosIPTU);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 6)
    public int verificarCadastros(ExportacaoDebitosIPTU exportacaoDebitosIPTU) {
        List<String> inscricoes = buscarInscricoesCadastroImobiliarios(exportacaoDebitosIPTU);
        List<ExportacaoDebitosIPTULinha> exportacaoDebitosIPTULinhas = buscarLinhasDoProcesso(exportacaoDebitosIPTU.getId());
        int erros = 0;
        for (String inscricao : inscricoes) {
            boolean temInscricao = false;
            for (ExportacaoDebitosIPTULinha linha : exportacaoDebitosIPTULinhas) {
                if (linha.getLinha().contains(inscricao)) {
                    temInscricao = true;
                    break;
                }
            }
            if (!temInscricao) {
                erros++;
            }
        }
        return erros;
    }

    public UsuarioSistema recuperarUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public Exercicio recuperarExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public List<ConvenioListaDebitos> buscarConvenios(String parte) {
        return convenioListaDebitosFacade.buscarConvenioListaDebitos(parte);
    }

    public Long recuperarNumeroRemessa() {
        if (isPerfilDev()) {
            return singletonGeradorCodigoExportacaoDebitosIPTU.getProximoCodigo(SistemaFacade.PerfilApp.DEV, sistemaFacade.getExercicioCorrente());
        }
        return singletonGeradorCodigoExportacaoDebitosIPTU.getProximoCodigo(SistemaFacade.PerfilApp.PROD, sistemaFacade.getExercicioCorrente());
    }

    public boolean isPerfilDev() {
        return sistemaFacade.isPerfilDev();
    }

    public StreamedContent montarArquivoParaDownloadPorArquivo(Arquivo arquivo) {
        return arquivoFacade.montarArquivoParaDownloadPorArquivo(arquivo);
    }

    public ExportacaoDebitosIPTU alterarSituacao(ExportacaoDebitosIPTU exportacao) {
        if (exportacao != null && exportacao.getArquivo() == null &&
            !SituacaoExportacaoDebitosIPTU.CONCLUIDO.equals(exportacao.getSituacaoExportacaoDebitosIPTU())) {
            String sql = " select ediptu.id from exportacaodebitosiptu ediptu " +
                " where ediptu.id = :idExportacao " +
                " and exists(select * from exportacaodebitosiptulinha linha where linha.exportacaodebitosiptu_id = ediptu.id) " +
                " and ediptu.arquivo_id is null" +
                " and ediptu.situacaoexportacaodebitosiptu <> :concluido ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idExportacao", exportacao.getId());
            q.setParameter("concluido", SituacaoExportacaoDebitosIPTU.CONCLUIDO.name());

            List<BigDecimal> ids = q.getResultList();

            if (ids != null) {
                if (ids.isEmpty()) {
                    exportacao.setSituacaoExportacaoDebitosIPTU(SituacaoExportacaoDebitosIPTU.ABERTO);
                } else {
                    exportacao.setSituacaoExportacaoDebitosIPTU(SituacaoExportacaoDebitosIPTU.CONCLUIDO);
                }
            }
            return em.merge(exportacao);
        }
        return exportacao;
    }
}
