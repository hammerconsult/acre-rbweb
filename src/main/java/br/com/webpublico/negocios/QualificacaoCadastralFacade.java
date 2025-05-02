package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.esocial.QualificacaoCadastral;
import br.com.webpublico.entidades.rh.esocial.QualificacaoCadastralPessoa;
import br.com.webpublico.entidades.rh.esocial.RetornoQualificacaoCadastral;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.esocial.InconsistenciaRetornoQualificacaoCadastral;
import br.com.webpublico.enums.rh.esocial.SituacaoQualificacaoCadastral;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.model.UploadedFile;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by mateus on 17/07/17.
 */
@Stateless
public class QualificacaoCadastralFacade extends AbstractFacade<QualificacaoCadastral> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;

    public QualificacaoCadastralFacade() {
        super(QualificacaoCadastral.class);
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    private String popularArquivo(QualificacaoCadastral selecionado, AssistenteBarraProgresso assistenteBarraProgresso, Date dataOperacao) {
        if (!selecionado.getPessoas().isEmpty()) {
            selecionado.getPessoas().removeAll(selecionado.getPessoas());
        }
        StringBuilder linhasArquivo = new StringBuilder();
        List<Object[]> funcionariosRecuperados = buscarFuncionarios(dataOperacao, selecionado.getHierarquiaOrganizacional());
        List<Object[]> funcionariosArquivo = Lists.newArrayList();
        String separador = ";";
        SimpleDateFormat sf = new SimpleDateFormat("ddMMyyyy");

        for (Object[] funcionario : funcionariosRecuperados) {
            assistenteBarraProgresso.setDescricaoProcesso("Recuperando ultima situação do servidor...");
            assistenteBarraProgresso.setTotal(funcionariosRecuperados.size());
            Object[] ultimaQualificacaoRecuperada = buscarUltimaQualificacaoCadastral(StringUtil.removeCaracteresEspeciaisSemEspaco(((String) funcionario[0])));
            if (ultimaQualificacaoRecuperada == null) {
                funcionariosArquivo.add(funcionario);
            } else if (ultimaQualificacaoRecuperada[2] == null || !SituacaoQualificacaoCadastral.QUALIFICADO.name().equals(ultimaQualificacaoRecuperada[2].toString().trim())) {
                funcionariosArquivo.add(funcionario);
            }
            assistenteBarraProgresso.conta();
        }

        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDescricaoProcesso("Exportando Qualificações");
        assistenteBarraProgresso.setTotal(funcionariosArquivo.size());
        popularQualificacoesOrFornecedores(selecionado, assistenteBarraProgresso, linhasArquivo, separador, sf, funcionariosArquivo);

        List<Object[]> fornecedores = buscarFornecedores(dataOperacao, selecionado.getHierarquiaOrganizacional());
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDescricaoProcesso("Exportando Fornecedores");
        assistenteBarraProgresso.setTotal(fornecedores.size());
        popularQualificacoesOrFornecedores(selecionado, assistenteBarraProgresso, linhasArquivo, separador, sf, fornecedores);

        return linhasArquivo.toString();
    }

    private void popularQualificacoesOrFornecedores(QualificacaoCadastral selecionado, AssistenteBarraProgresso assistenteBarraProgresso, StringBuilder linhasArquivo, String separador, SimpleDateFormat sf, List<Object[]> fornecedores) {
        for (Object[] obj : fornecedores) {
            QualificacaoCadastralPessoa qualificacaoCadastralPessoa = new QualificacaoCadastralPessoa();
            qualificacaoCadastralPessoa.setPessoa(new PessoaFisica(Long.parseLong(obj[4].toString()), (String) obj[2]));
            qualificacaoCadastralPessoa.setCpf(StringUtil.removeCaracteresEspeciaisSemEspaco(((String) obj[0])));
            qualificacaoCadastralPessoa.setNis(obj[1] != null ? StringUtil.removeCaracteresEspeciaisSemEspaco(((String) obj[1])) : "");
            qualificacaoCadastralPessoa.setNomeFuncionario(StringUtil.removeCaracteresEspeciais(((String) obj[2])).trim());
            qualificacaoCadastralPessoa.setQualificacaoCadastral(selecionado);
            qualificacaoCadastralPessoa.setDataNascimento((Date) obj[3]);
            Util.adicionarObjetoEmLista(selecionado.getPessoas(), qualificacaoCadastralPessoa);
            String linha = qualificacaoCadastralPessoa.getCpf() + separador +
                qualificacaoCadastralPessoa.getNis() + separador +
                qualificacaoCadastralPessoa.getNomeFuncionario() + separador +
                (qualificacaoCadastralPessoa.getDataNascimento() != null ? sf.format(qualificacaoCadastralPessoa.getDataNascimento()) : "") + "\r\n";
            linhasArquivo.append(linha);
            assistenteBarraProgresso.conta();
        }
    }

    private String buscarNomeArquivo(QualificacaoCadastral selecionado) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "D.CNS.CPF.001." + sf.format(selecionado.getDataEnvio()) + "." + StringUtil.removeCaracteresEspeciaisSemEspaco(selecionado.getCnpjMunicipio()) + "." + StringUtil.removeCaracteresEspeciaisSemEspaco(selecionado.getCpfTransmissor()) + ".txt";
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<QualificacaoCadastral> buscarInformacoesParaExportar(QualificacaoCadastral selecionado, AssistenteBarraProgresso assistenteBarraProgresso, Date dataOperacao) {
        AsyncResult<QualificacaoCadastral> result = new AsyncResult<>(selecionado);
        InputStream inputStream = new ByteArrayInputStream(popularArquivo(selecionado, assistenteBarraProgresso, dataOperacao).getBytes());
        if (selecionado.getDetentorArquivoComposicao() == null) {
            selecionado.setDetentorArquivoComposicao(new DetentorArquivoComposicao());
        }
        selecionado.getDetentorArquivoComposicao().setArquivosComposicao(Lists.<ArquivoComposicao>newArrayList());
        adicionarArquivo(selecionado, inputStream, buscarNomeArquivo(selecionado));
        return result;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public Future<QualificacaoCadastral> importarArquivo(QualificacaoCadastral selecionado, AssistenteBarraProgresso assistenteBarraProgresso, UploadedFile file, SituacaoQualificacaoCadastral processadoRejeitado) throws IOException {
        AsyncResult<QualificacaoCadastral> result = new AsyncResult<>(selecionado);
        InputStream inputStream = file.getInputstream();
        InputStream inputStreamCriarArquivo = file.getInputstream();
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        assistenteBarraProgresso.setDescricaoProcesso("Importando Qualificações");
        lerArquivo(selecionado, assistenteBarraProgresso, reader, processadoRejeitado);
        assistenteBarraProgresso.setDescricaoProcesso("Salvando registro");
        adicionarArquivo(selecionado, inputStreamCriarArquivo, file.getFileName());
        return result;
    }

    private void adicionarArquivo(QualificacaoCadastral selecionado, InputStream inputStream, String descricaoArquivo) {
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao(descricaoArquivo);
        arquivo.setNome(arquivo.getDescricao());
        arquivo.setMimeType("text/plain");
        arquivo.setInputStream(inputStream);

        try {
            arquivo.setTamanho(Long.valueOf(inputStream.available()));
        } catch (IOException e) {
            logger.error("Não foi possível definir o tamanho do arquivo");
        }
        try {
            novoArquivoMemoria(arquivo);
            arquivo = em.merge(arquivo);
        } catch (Exception ex) {
            logger.error("Erro:", ex.getMessage());
        }
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivo);
        arquivoComposicao.setDataUpload(new Date());
        arquivoComposicao.setDetentorArquivoComposicao(selecionado.getDetentorArquivoComposicao());
        selecionado.getDetentorArquivoComposicao().getArquivosComposicao().add(arquivoComposicao);
        salvarRetornando(selecionado);
    }

    private void lerArquivo(QualificacaoCadastral selecionado, AssistenteBarraProgresso assistenteBarraProgresso, BufferedReader reader, SituacaoQualificacaoCadastral processadoRejeitado) {
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    assistenteBarraProgresso.conta();
                    continue;
                }
                String[] partes = line.split(";");
                percorrerQualificacoesCadastraisPessoa(selecionado, processadoRejeitado, partes);
                assistenteBarraProgresso.conta();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void limparRetornos(QualificacaoCadastral selecionado, SituacaoQualificacaoCadastral processadoRejeitado) {
        for (QualificacaoCadastralPessoa qualificacaoCadastralPessoa : selecionado.getPessoas()) {
            if (processadoRejeitado.equals(qualificacaoCadastralPessoa.getProcessadoRejeitado())) {
                qualificacaoCadastralPessoa = em.find(QualificacaoCadastralPessoa.class, qualificacaoCadastralPessoa.getId());
                for (RetornoQualificacaoCadastral retornoQualificacaoCadastral : qualificacaoCadastralPessoa.getRetornos()) {
                    em.remove(retornoQualificacaoCadastral);
                }
                qualificacaoCadastralPessoa.getRetornos().removeAll(qualificacaoCadastralPessoa.getRetornos());
            }
        }
    }

    private void percorrerQualificacoesCadastraisPessoa(QualificacaoCadastral selecionado, SituacaoQualificacaoCadastral processadoRejeitado, String[] partes) {
        for (QualificacaoCadastralPessoa qualificacaoCadastralPessoa : selecionado.getPessoas()) {
            if (qualificacaoCadastralPessoa.getCpf().equals(partes[0])) {
                qualificacaoCadastralPessoa.setProcessadoRejeitado(processadoRejeitado);
                qualificacaoCadastralPessoa.getRetornos().removeAll(qualificacaoCadastralPessoa.getRetornos());
                setarSituacaoNaPessoa(qualificacaoCadastralPessoa, partes);
                if (SituacaoQualificacaoCadastral.QUALIFICADO.equals(processadoRejeitado)) {
                    validarArquivoProcessado(qualificacaoCadastralPessoa, partes);
                } else {
                    validarArquivoRejeitado(qualificacaoCadastralPessoa, partes);
                }
            }
        }
    }

    private void setarSituacaoNaPessoa(QualificacaoCadastralPessoa qualificacaoCadastralPessoa, String[] partes) {
        Long idPessoa;
        if (partes == null) {
            idPessoa = pessoaFacade.recuperarIdPessoaFisicaPorCPF(qualificacaoCadastralPessoa.getCpf());
        } else {
            idPessoa = pessoaFacade.recuperarIdPessoaFisicaPorCPF(partes[0]);
        }
        if (idPessoa != null) {
            em.createNativeQuery("update pessoafisica set SITUACAOQUALIFICACAOCADASTRAL = '" + qualificacaoCadastralPessoa.getProcessadoRejeitado().name()
                + "' where id = " + idPessoa).executeUpdate();
        }
    }

    private void validarArquivoProcessado(QualificacaoCadastralPessoa qualificacaoCadastralPessoa, String[] partes) {
        if (!"0".equals(partes[4])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_NIS_INV, "");
        }
        if (!"0".equals(partes[5])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CPF_INV, "");
        }
        if (!"0".equals(partes[6])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_NOME_INV, "");
        }
        if (!"0".equals(partes[7])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_DN_INV, "");
        }
        if (!"0".equals(partes[8])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CNIS_NIS, "");
        }
        if (!"0".equals(partes[9])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CNIS_DN, "");
        }
        if (!"0".equals(partes[10])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CNIS_OBITO, "");
        }
        if (!"0".equals(partes[11])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CNIS_CPF, "");
        }
        if (!"0".equals(partes[12])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CNIS_CPF_NAO_INF, "");
        }
        if (!"0".equals(partes[13])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CPF_NAO_CONSTA, "");
        }
        if (!"0".equals(partes[14])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CPF_NULO, "");
        }
        if (!"0".equals(partes[15])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CPF_CANCELADO, "");
        }
        if (!"0".equals(partes[16])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CPF_SUSPENSO, "");
        }
        if (!"0".equals(partes[17])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CPF_DN, "");
        }
        if (!"0".equals(partes[18])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CPF_NOME, partes[18].substring(3, partes[18].length()));
        }
        if (!"0".equals(partes[19])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_ORIENTACAO_CPF, "");
        }
        if (!"0".equals(partes[20])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.valueOf("COD_ORIENTACAO_NIS_" + partes[20]), "");
        }
    }

    private void validarArquivoRejeitado(QualificacaoCadastralPessoa qualificacaoCadastralPessoa, String[] partes) {
        if (!"0".equals(partes[4])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_CPF_INV, "");
        }
        if (!"0".equals(partes[5])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_NIS_INV, "");
        }
        if (!"0".equals(partes[6])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_NOME_INV, "");
        }
        if (!"0".equals(partes[7])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.COD_DN_INV, "");
        }
        if (!"0".equals(partes[8])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.SEPARADOR, "");
        }
        if (!"0".equals(partes[9])) {
            adicionarRetornoQualificacaoCadastral(qualificacaoCadastralPessoa, InconsistenciaRetornoQualificacaoCadastral.REG_DESFORMATADO, "");
        }
    }

    private void adicionarRetornoQualificacaoCadastral(QualificacaoCadastralPessoa qualificacaoCadastralPessoa,
                                                       InconsistenciaRetornoQualificacaoCadastral inconsistenciaRetorno,
                                                       String observacao) {
        RetornoQualificacaoCadastral retornoQualificacaoCadastral = new RetornoQualificacaoCadastral();
        retornoQualificacaoCadastral.setQualificacaoCadPessoa(qualificacaoCadastralPessoa);
        retornoQualificacaoCadastral.setInconsistenciaRetorno(inconsistenciaRetorno);
        retornoQualificacaoCadastral.setObservacao(observacao);
        qualificacaoCadastralPessoa.setProcessadoRejeitado(SituacaoQualificacaoCadastral.PENDENTE);
        setarSituacaoNaPessoa(qualificacaoCadastralPessoa, null);
        Util.adicionarObjetoEmLista(qualificacaoCadastralPessoa.getRetornos(), retornoQualificacaoCadastral);
    }

    public void novoArquivoMemoria(Arquivo arquivo) throws Exception {
        int bytesLidos = 0;
        while (true) {
            int restante = arquivo.getInputStream().available();
            byte[] buffer = new byte[restante > ArquivoParte.TAMANHO_MAXIMO ? ArquivoParte.TAMANHO_MAXIMO : restante];
            bytesLidos = arquivo.getInputStream().read(buffer);
            if (bytesLidos <= 0) {
                break;
            }
            ArquivoParte arquivoParte = new ArquivoParte();
            arquivoParte.setDados(buffer);
            arquivoParte.setArquivo(arquivo);
            arquivo.getPartes().add(arquivoParte);
        }
    }

    public void novoArquivo(Arquivo arquivo, InputStream is) throws Exception {
        int bytesLidos = 0;
        while (true) {
            int restante = is.available();
            byte[] buffer = new byte[restante > ArquivoParte.TAMANHO_MAXIMO ? ArquivoParte.TAMANHO_MAXIMO : restante];
            bytesLidos = is.read(buffer);
            if (bytesLidos <= 0) {
                break;
            }
            ArquivoParte arquivoParte = new ArquivoParte();
            arquivoParte.setDados(buffer);
            arquivoParte.setArquivo(arquivo);
            em.persist(arquivoParte);
        }
        em.persist(arquivo);
    }

    @Override
    public QualificacaoCadastral recuperar(Object id) {
        QualificacaoCadastral qualificacaoCadastral = em.find(QualificacaoCadastral.class, id);
        qualificacaoCadastral.getPessoas().size();
        if (qualificacaoCadastral.getDetentorArquivoComposicao() != null) {
            qualificacaoCadastral.getDetentorArquivoComposicao().getArquivosComposicao().size();
            for (ArquivoComposicao arquivoComposicao : qualificacaoCadastral.getDetentorArquivoComposicao().getArquivosComposicao()) {
                arquivoComposicao.getArquivo().getPartes().size();
            }
        }
        for (QualificacaoCadastralPessoa qualificacaoCadastralPessoa : qualificacaoCadastral.getPessoas()) {
            qualificacaoCadastralPessoa.getRetornos().size();
        }
        return qualificacaoCadastral;
    }

    public List<Object[]> buscarFuncionarios(Date dataOperacao, HierarquiaOrganizacional ho) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT DISTINCT pf.cpf AS cpf, ")
            .append("  (SELECT trab.PISPASEP ")
            .append("  FROM DOCUMENTOPESSOAL doc ")
            .append("  INNER JOIN CARTEIRATRABALHO trab ")
            .append("  ON trab.id                = doc.id ")
            .append("  WHERE doc.PESSOAFISICA_ID = pf.id ")
            .append("  AND rownum = 1 ")
            .append("  ) AS pisPasep, ")
            .append("  pf.nome AS nome, ")
            .append("  pf.dataNascimento AS dataNascimento, ")
            .append("  pf.id ")
            .append(" FROM vinculofp v ")
            .append(" INNER JOIN matriculafp mat ON mat.id = v.MATRICULAFP_ID ")
            .append(" inner join LOTACAOFUNCIONAL lotacao on lotacao.VINCULOFP_ID = v.id ")
            .append(" inner join HierarquiaOrganizacional ho on ho.SUBORDINADA_ID = lotacao.UNIDADEORGANIZACIONAL_ID ")
            .append(" INNER JOIN contratofp c ON c.id = v.id ")
            .append(" INNER JOIN pessoafisica pf ON pf.id = mat.PESSOA_ID ")
            .append(" WHERE to_date(:dataOperacao, 'dd/mm/yyyy') BETWEEN v.inicioVigencia AND COALESCE(v.finalVigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) ")
            .append(" and ho.SUBORDINADA_ID = lotacao.UNIDADEORGANIZACIONAL_ID ")
            .append(" and to_date(:vigencia, 'dd/MM/yyyy') between v.inicioVigencia and coalesce(v.finalVigencia,to_date(:vigencia, 'dd/MM/yyyy')) ")
            .append(" AND to_date(:vigencia, 'dd/MM/yyyy') >= lotacao.inicioVigencia AND to_date(:vigencia, 'dd/mm/yyyy') <= coalesce(lotacao.finalVigencia,to_date(:vigencia, 'dd/MM/yyyy')) ")
            .append(" AND to_date(:vigencia, 'dd/MM/yyyy') >= ho.inicioVigencia AND to_date(:vigencia, 'dd/MM/yyyy') <= coalesce(ho.fimVigencia,to_date(:vigencia, 'dd/MM/yyyy')) and ho.tipoHierarquiaOrganizacional = :tipoHierarquia ")
            .append(" and ho.codigo like :unidade ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("unidade", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("vigencia", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        return (List<Object[]>) q.getResultList();
    }

    private List<Object[]> buscarFornecedores(Date dataOperacao, HierarquiaOrganizacional ho) {
        StringBuilder sql = new StringBuilder();
        List<Long> idsClasseCredor = Lists.newArrayList();
        for (ClasseCredor classes : buscarClassesCredoresDaConfiguracao()) {
            idsClasseCredor.add(classes.getId());
        }
        sql.append(" SELECT DISTINCT pf.cpf AS cpf, ")
            .append("  (SELECT trab.PISPASEP ")
            .append("  FROM DOCUMENTOPESSOAL doc ")
            .append("  INNER JOIN CARTEIRATRABALHO trab ")
            .append("  ON trab.id                = doc.id ")
            .append("  WHERE doc.PESSOAFISICA_ID = pf.id ")
            .append("  AND rownum = 1 ")
            .append("  ) AS pisPasep, ")
            .append("  pf.nome AS nome, ")
            .append("  pf.dataNascimento AS dataNascimento, ")
            .append("  pf.id ")
            .append(" FROM empenho emp ")
            .append(" inner join liquidacao liq on emp.id = liq.empenho_id ")
            .append(" inner join pagamento pag on liq.id = pag.liquidacao_id ")
            .append(" inner join vwhierarquiaorcamentaria vworc on vworc.SUBORDINADA_ID = emp.unidadeorganizacional_ID ")
            .append(" INNER JOIN HIERARQUIAORGCORRE COR ON vworc.id = cor.hierarquiaorgorc_id ")
            .append(" INNER JOIN vwhierarquiaadministrativa vwadm ON vwadm.id = cor.hierarquiaorgadm_id ")
            .append(" INNER JOIN pessoafisica pf ON pf.id = emp.fornecedor_id ")
            .append(" INNER JOIN CLASSECREDOR CC ON emp.CLASSECREDOR_ID = CC.ID ")
            .append(" WHERE to_date(:dataOperacao, 'dd/mm/yyyy') BETWEEN vworc.inicioVigencia AND COALESCE(vworc.fimVigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) ")
            .append("   and to_date(:dataOperacao, 'dd/mm/yyyy') BETWEEN vwadm.inicioVigencia AND COALESCE(vwadm.fimVigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) ")
            .append("   and to_date(:dataOperacao, 'dd/mm/yyyy') BETWEEN cor.datainicio AND COALESCE(cor.datafim, to_date(:dataOperacao, 'dd/mm/yyyy')) ")
            .append("   and vwadm.codigo like :unidade ")
            .append("   and cc.TIPOCLASSECREDOR = :prestador ");
        if (!idsClasseCredor.isEmpty()) {
            sql.append(" and cc.id in :idsClasseCredor ");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("unidade", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("prestador", TipoClasseCredor.PRESTADOR_SERVICO.name());
        if (!idsClasseCredor.isEmpty()) {
            q.setParameter("idsClasseCredor", idsClasseCredor);
        }
        return (List<Object[]>) q.getResultList();
    }

    public List<ClasseCredor> buscarClassesCredoresDaConfiguracao() {
        return configuracaoRHFacade.buscarClassesCredoresDaConfiguracao();
    }

    public List<ClasseCredor> buscarClassesCredorPrestadoresDeServico() {
        return classeCredorFacade.listaFiltrandoPorPessoaPorTipoClasse("", null, TipoClasseCredor.PRESTADOR_SERVICO);
    }

    private Object[] buscarUltimaQualificacaoCadastral(String cpf) {
        String sql = " select distinct qualificacaopessoa.CPF, qualificacao.DATAENVIO, qualificacaopessoa.PROCESSADOREJEITADO  " +
            "       from QualificacaoCadastral qualificacao " +
            "       inner join QUALIFICACAOCADPESSOA qualificacaopessoa on qualificacaopessoa.QUALIFICACAOCADASTRAL_ID = qualificacao.id " +
            "       and replace(replace(replace(:cpf,'.',''),'-',''),'/','') = replace(replace(replace(qualificacaopessoa.cpf,'.',''),'-',''),'/','')" +
            "       order by qualificacao.DATAENVIO desc";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        q.setParameter("cpf", cpf);
        if (!q.getResultList().isEmpty()) {
            return (Object[]) q.getResultList().get(0);
        }
        return null;
    }

    public QualificacaoCadastral salvarRetornando(QualificacaoCadastral entity) {
        return em.merge(entity);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

}
