package br.com.webpublico.nfse.facades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.nfse.ParecerFiscalNfse;
import br.com.webpublico.nfse.domain.DeclaracaoMensalServico;
import br.com.webpublico.nfse.domain.ItemLivroFiscal;
import br.com.webpublico.nfse.domain.LivroFiscal;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.nfse.exceptions.NfseOperacaoNaoPermitidaException;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.util.PesquisaGenericaNfseUtil;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class LivroFiscalFacade extends AbstractFacade<LivroFiscal> {

    private static final String SQL_BASE = "select livro.id, ex.ano, livro.numero, livro.tipoMovimento, livro.mesInicial, livro.mesFinal, livro.abertura, livro.encerramento " +
        " from LivroFiscal livro " +
        " inner join exercicio ex on ex.id = livro.exercicio_id " +
        " where livro.prestador_id = :empresaId ";
    private static final String SQL_RELATORIO_LIVRO_NOTAS = "SELECT " +
        "  distinct " +
        "  coalesce(nfse.id,SD.id)                      AS idNota, " +
        "  coalesce(nfse.NUMERO,SD.NUMERO)                  AS numeroNota, " +
        "  dps.TIPODOCUMENTO as tipoNota, " +
        "  dps.OPTANTESIMPLESNACIONAL as simplesNacionalNota, " +
        "  0 as meiNota, " +
        "  dps.NATUREZAOPERACAO as exigibilidadeNota, " +
        "  dps.SITUACAO as situacaoNota, " +
        "  dps.ISSRETIDO as issRetidoNota, " +
        "  dps.BASECALCULO as baseCalculoNota, " +
        "  dps.ISSCALCULADO as issNota, " +
        "  dadosTomador.NOMERAZAOSOCIAL as nomeTomador, " +
        "  dadosTomador.CPFCNPJ as cpfCnpjTomador, " +
        "  dadosTomador.MUNICIPIO as municipioTomador, " +
        "  dadostomador.UF as ufTomadpr " +
        "FROM " +
        "  DECLARACAOMENSALSERVICO dms " +
        "  INNER JOIN itemlivrofiscal itemlivro ON itemlivro.DECLARACAOMENSALSERVICO_ID = dms.id " +
        "  INNER JOIN NOTADECLARADA ON NOTADECLARADA.DECLARACAOMENSALSERVICO_ID = dms.id " +
        "  INNER JOIN DECLARACAOPRESTACAOSERVICO dps " +
        "    ON dps.ID = NOTADECLARADA.DECLARACAOPRESTACAOSERVICO_ID " +
        "  left JOIN NOTAFISCAL nfse ON nfse.DECLARACAOPRESTACAOSERVICO_ID = dps.ID " +
        "  left JOIN SERVICODECLARADO SD ON SD.DECLARACAOPRESTACAOSERVICO_ID = dps.ID " +
        "  left join DADOSPESSOAISNFSE dadosTomador on dadosTomador.id = dps.DADOSPESSOAISTOMADOR_ID";
    private static final String SQL_RELATORIO_LIVRO_SERVICOS = "SELECT " +
        "  SERVICO.CODIGO, " +
        "  ids.QUANTIDADE, " +
        "  ids.VALORSERVICO, " +
        "  ids.DEDUCOES, " +
        "  ids.DESCONTOSINCONDICIONADOS, " +
        "  ids.DESCONTOSCONDICIONADOS, " +
        "  ids.BASECALCULO, " +
        "  ids.ALIQUOTASERVICO, " +
        "  ids.ISS," +
        "  dps.ISSRETIDO " +
        "FROM " +
        "  ITEMDECLARACAOSERVICO ids " +
        "  inner join SERVICO on SERVICO.ID = ids.SERVICO_ID " +
        "  INNER JOIN DECLARACAOPRESTACAOSERVICO dps " +
        "    ON dps.ID = ids.DECLARACAOPRESTACAOSERVICO_ID " +
        "  INNER JOIN NOTADECLARADA ON NOTADECLARADA.DECLARACAOPRESTACAOSERVICO_ID = dps.id " +
        "  INNER JOIN DECLARACAOMENSALSERVICO dms ON dms.id = NOTADECLARADA.DECLARACAOMENSALSERVICO_ID " +
        "  left JOIN NOTAFISCAL NFSE ON NFSE.DECLARACAOPRESTACAOSERVICO_ID = DPS.ID " +
        "  left JOIN SERVICODECLARADO SD ON SD.DECLARACAOPRESTACAOSERVICO_ID = dps.ID ";
    private static final String SQL_RELATORIO_LIVRO = "SELECT" +
        "  distinct " +
        "  livro.id as idLivro," +
        "  livro.numero as numeroLivro," +
        "  livro.mesinicial as mesInicialLivro," +
        "  livro.mesfinal as mesFInalLivro," +
        "  cmc.id                               cmc_id," +
        "  cmc.inscricaocadastral            AS cmc," +
        "  coalesce(pf.cpf, pj.cnpj)         AS cpf_cnpj," +
        "  coalesce(pf.nome, pj.RAZAOSOCIAL) AS nome," +
        "  endereco.LOGRADOURO               AS logradouroEndereco," +
        "  endereco.NUMERO                   AS numeroEndereco," +
        "  endereco.COMPLEMENTO              AS complementeoEndereco," +
        "  endereco.BAIRRO                   AS bairroEndereco," +
        "  endereco.cep                      AS cepEndereco," +
        "  endereco.LOCALIDADE               AS cidadeEndereco," +
        "  endereco.UF                       AS ufEndereco," +
        "  pjEscritorio.NOMEFANTASIA         AS nomeEscritorio," +
        "  pjEscritorio.CNPJ                 AS cnpjEcritorio," +
        "  contador.NOME                     AS contadorEscritorio," +
        "  escritorio.CRCESCRITORIO          as crcEscritorio," +
        "  livro.tipoMovimento as tipoMovimento " +
        " FROM" +
        "  Livrofiscal livro" +
        "  INNER JOIN EXERCICIO ex ON ex.id = livro.EXERCICIO_ID" +
        "  INNER JOIN CADASTROECONOMICO cmc ON cmc.id = livro.PRESTADOR_ID" +
        "  LEFT JOIN pessoafisica pf ON pf.id = cmc.PESSOA_ID" +
        "  LEFT JOIN PESSOAJURIDICA pj ON pj.id = cmc.PESSOA_ID" +
        "  LEFT JOIN PESSOA_ENDERECOCORREIO p_end ON p_end.PESSOA_ID = cmc.PESSOA_ID" +
        "  LEFT JOIN ENDERECOCORREIO endereco ON endereco.id = p_end.ENDERECOSCORREIO_ID AND endereco.PRINCIPAL = 1 " +
        "  LEFT JOIN ESCRITORIOCONTABIL escritorio ON escritorio.id = cmc.ESCRITORIOCONTABIL_ID" +
        "  LEFT JOIN PESSOAJURIDICA pjEscritorio ON pjEscritorio.id = escritorio.PESSOA_ID" +
        "  LEFT JOIN PESSOAFISICA contador ON escritorio.RESPONSAVEL_ID = contador.ID";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;

    public LivroFiscalFacade() {
        super(LivroFiscal.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LivroFiscal recupera(Long id, Long prestadorId) {
        LivroFiscal recuperar = recuperar(id);
        Hibernate.initialize(recuperar.getItens());
        if (recuperar.getPrestador().getId().equals(prestadorId)) {
            return recuperar;
        }
        return null;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void criarLivroParaDeclaracaoMensal(DeclaracaoMensalServico declaracao) {
        LivroFiscalNfseDTO livro = new LivroFiscalNfseDTO();
        livro.setAbertura(declaracao.getAbertura());
        livro.setEncerramento(declaracao.getEncerramento());
        livro.setExercicio(declaracao.getExercicio().getAno());
        livro.setMesInicial(declaracao.getMes().getNumeroMes());
        livro.setMesFinal(declaracao.getMes().getNumeroMes());
        livro.setTipoMovimento(declaracao.getTipoMovimento().toDto());
        livro.setPrestador(new PrestadorServicoNfseDTO(declaracao.getPrestador().getId(), null, null));
        salvar(livro);
    }

    public void salvar(LivroFiscalNfseDTO dto) {
        if (dto.getPrestador() == null) {
            throw new NfseOperacaoNaoPermitidaException("Para gerar um novo livro é necessário informar o prestador de serviços");
        }
        if (dto.getAbertura() == null) {
            throw new NfseOperacaoNaoPermitidaException("Para gerar um novo livro é necessário informar a data de abertura");
        }
        if (dto.getEncerramento() == null) {
            throw new NfseOperacaoNaoPermitidaException("Para gerar um novo livro é necessário informar a data de encerramento");
        }

        for (String nomeDoMes : Mes.getTodosMesesComoStringNoIntevalo(Mes.getMesToInt(dto.getMesInicial()), Mes.getMesToInt(dto.getMesFinal()))) {
            Mes mes = Mes.valueOf(nomeDoMes);
            gerarLivroParaMes(TipoMovimentoMensal.NORMAL, mes, dto.getExercicio(), dto.getPrestador().getId());
            gerarLivroParaMes(TipoMovimentoMensal.RETENCAO, mes, dto.getExercicio(), dto.getPrestador().getId());
        }
    }

    private void gerarLivroParaMes(TipoMovimentoMensal tipoMovimentoMensal, Mes mes, Integer exercicio, Long prestadorId) {
        LivroFiscal livro = buscarPorMesesAndExercicio(mes, mes, exercicio, prestadorId, tipoMovimentoMensal);
        if (livro == null) {
            livro = new LivroFiscal();
            livro.setAbertura(new Date());
            livro.setEncerramento(livro.getAbertura());
            livro.setExercicio(exercicioFacade.recuperarExercicioPeloAno(exercicio));
            livro.setMesInicial(mes);
            livro.setMesFinal(mes);
            livro.setTipoMovimento(tipoMovimentoMensal);
            livro.setPrestador(cadastroEconomicoFacade.recuperar(prestadorId));
            livro.setNumero(getUltimoNumeroDoLivroDoPrestador(prestadorId, exercicio) + 1);
        }
        List<DeclaracaoMensalServicoNfseDTO> declaracoes = declaracaoMensalServicoFacade.buscarDeclaracoesNoPeriodoSemLivro(mes, mes, exercicio, prestadorId, tipoMovimentoMensal);
        for (DeclaracaoMensalServicoNfseDTO declaracaoDTO : declaracoes) {
            ItemLivroFiscal item = new ItemLivroFiscal();
            item.setDeclaracaoMensalServico(new DeclaracaoMensalServico(declaracaoDTO.getId()));
            item.setLivroFiscal(livro);
            livro.setEncerramento(new Date());
            livro.getItens().add(item);
        }
        em.merge(livro);
    }

    public void delete(Long id) {
        remover(recuperar(id));
    }

    public LivroFiscal buscarPorMesesAndExercicio(Mes mesInicial, Mes mesFinal, Integer ano, Long prestadorId, TipoMovimentoMensal tipoMovimentoMensal) {

        String hql = "Select livro from LivroFiscal livro "
            + " where livro.mesInicial = :mesInicial "
            + " and livro.mesFinal = :mesFinal "
            + " and livro.exercicio.ano = :ano "
            + " and livro.prestador.id = :empresaId"
            + " and livro.tipoMovimento = :tipoMovimentoMensal";

        Query q = em.createQuery(hql);
        q.setParameter("empresaId", prestadorId);
        q.setParameter("mesInicial", mesInicial);
        q.setParameter("mesFinal", mesFinal);
        q.setParameter("ano", ano);
        q.setParameter("tipoMovimentoMensal", tipoMovimentoMensal);
        if (!q.getResultList().isEmpty()) {
            return (LivroFiscal) q.getResultList().get(0);
        }
        return null;
    }

    public Page<LivroFiscalNfseDTO> recuperarPorEmpresa(Pageable pageable, Long prestadorId, Integer mesInicial, Integer mesFinal, Integer ano) {

        String hql = SQL_BASE
            + "and livro.mesInicial in (:meses) "
            + "and livro.mesFinal in (:meses) "
            + "and ex.ano = :ano "
            + "order by livro.NUMERO, ex.ano";
        Query q = em.createNativeQuery(hql);
        q.setParameter("empresaId", prestadorId);
        q.setParameter("meses", Mes.getTodosMesesComoStringNoIntevalo(Mes.getMesToInt(mesInicial), Mes.getMesToInt(mesFinal)));
        q.setParameter("ano", ano);
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);
        int resultCount = q.getResultList().size();
        List<Object[]> resultado = q.getResultList();
        List<LivroFiscalNfseDTO> retorno = Lists.newArrayList();
        for (Object[] obj : resultado) {
            LivroFiscalNfseDTO toAdd = popularDtoPeloSqlBase(obj);
            retorno.add(toAdd);
        }
        return new PageImpl<>(retorno, pageable, resultCount);
    }

    private LivroFiscalNfseDTO popularDtoPeloSqlBase(Object[] obj) {
        LivroFiscalNfseDTO toAdd = new LivroFiscalNfseDTO();
        toAdd.setId(((BigDecimal) obj[0]).longValue());
        toAdd.setExercicio(((BigDecimal) obj[1]).intValue());
        toAdd.setNumero(((BigDecimal) obj[2]).intValue());
        toAdd.setTipoMovimento(TipoMovimentoMensal.valueOf((String) obj[3]).toDto());
        toAdd.setMesInicial(Mes.valueOf((String) obj[4]).getNumeroMes());
        toAdd.setMesFinal(Mes.valueOf((String) obj[5]).getNumeroMes());
        toAdd.setAbertura((Date) obj[6]);
        toAdd.setEncerramento((Date) obj[7]);
        return toAdd;
    }

    private Integer getUltimoNumeroDoLivroDoPrestador(Long prestadorId, Integer exercicio) {
        Query q = em.createNativeQuery("select coalesce(max(a.numero),0) " +
            "  from LivroFiscal a inner join exercicio ex on ex.id = a.exercicio_id where a.prestador_id =:empresaId and ex.ano = :exercicio");
        q.setParameter("empresaId", prestadorId);
        q.setParameter("exercicio", exercicio);
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getSingleResult()).intValue();
        }
        return 0;
    }

    public List<RelatorioLivroFiscalDTO> montarRelatorioLivroFiscal(Long idLivro) {
        String sql = SQL_RELATORIO_LIVRO;
        if (idLivro != null) {
            sql += " where livro.id = :idLivro";
        } else {
            sql += " order by cmc.id, livro.numero ";
        }

        Query q = em.createNativeQuery(sql);
        if (idLivro != null) {
            q.setParameter("idLivro", idLivro);
        }
        List<RelatorioLivroFiscalDTO> livros = Lists.newArrayList();
        List<Object[]> resultListLivros = q.getResultList();
        for (Object[] obj : resultListLivros) {
            RelatorioLivroFiscalDTO livroDto = new RelatorioLivroFiscalDTO();
            Mes mesInicial = Mes.valueOf((String) obj[2]);
            Mes mesFinal = Mes.valueOf((String) obj[3]);

            popularLivroParaRelatorio(obj, livroDto, mesInicial, mesFinal);

            livroDto.setPeriodos(percorrerPeriodosParaRelatorio(livroDto, mesInicial, mesFinal));

            livroDto.setResumo(new ArrayList<RelatorioLivroFiscalResumoDTO>());
            for (RelatorioLivroFiscalPeriodoDTO periodoDTO : livroDto.getPeriodos()) {
                for (RelatorioLivroFiscalNotaDTO relatorioLivroFiscalNotaDTO : periodoDTO.getNotas()) {
                    for (RelatorioLivroFiscalServicoDTO servico : relatorioLivroFiscalNotaDTO.getServicos()) {
                        RelatorioLivroFiscalResumoDTO resumo = new RelatorioLivroFiscalResumoDTO(servico.getServico(), servico.getAliquota(), servico.getRetido());
                        if (livroDto.getResumo().contains(resumo)) {
                            livroDto.getResumo().get(livroDto.getResumo().indexOf(resumo)).calcularServico(servico);
                        } else {
                            resumo.calcularServico(servico);
                            livroDto.getResumo().add(resumo);
                        }
                    }
                }
            }
            Collections.sort(livroDto.getPeriodos());
            Collections.sort(livroDto.getResumo());
            livros.add(livroDto);
        }
        Collections.sort(livros);
        return livros;
    }

    private List<RelatorioLivroFiscalPeriodoDTO> percorrerPeriodosParaRelatorio(RelatorioLivroFiscalDTO livroDto, Mes mesInicial, Mes mesFinal) {
        List<RelatorioLivroFiscalPeriodoDTO> periodoDTOS = Lists.newArrayList();
        for (String mes : Mes.getTodosMesesComoStringNoIntevalo(mesInicial, mesFinal)) {
            if (TipoMovimentoMensal.NORMAL.name().equals(livroDto.getTipoMovimento()))
                periodoDTOS.add(criarPeriodoParaMes(livroDto, mes, false));
            periodoDTOS.add(criarPeriodoParaMes(livroDto, mes, true));
        }
        return periodoDTOS;
    }

    private RelatorioLivroFiscalPeriodoDTO criarPeriodoParaMes(RelatorioLivroFiscalDTO livroDto, String mes, Boolean issRetido) {
        RelatorioLivroFiscalPeriodoDTO periodoDTO = new RelatorioLivroFiscalPeriodoDTO();
        periodoDTO.setPeriodoInicial(DataUtil.primeiroDiaMes(Mes.valueOf(mes)));
        periodoDTO.setPeriodoFinal(DataUtil.ultimoDiaDoMes(Mes.valueOf(mes)));
        periodoDTO.setRetencao(issRetido);

        Query qNota = em.createNativeQuery(SQL_RELATORIO_LIVRO_NOTAS + " where dms.mes = :mes" +
            "  and itemlivro.livrofiscal_id = :idLivro and coalesce(dps.ISSRETIDO,0) = :issRetido");
        qNota.setParameter("idLivro", livroDto.getIdLivro());
        qNota.setParameter("mes", mes);
        qNota.setParameter("issRetido", issRetido);
        List<RelatorioLivroFiscalNotaDTO> notas = Lists.newArrayList();
        List<Object[]> resultListNotas = qNota.getResultList();
        Map<String, RelatorioLivroFiscalResumoDTO> mapaResumo = Maps.newHashMap();
        for (Object[] objNota : resultListNotas) {
            RelatorioLivroFiscalNotaDTO nota = new RelatorioLivroFiscalNotaDTO();
            popularNotaParaRelatorio(objNota, nota);
            notas.add(nota);

            Query qServico = em.createNativeQuery(SQL_RELATORIO_LIVRO_SERVICOS + " where coalesce(nfse.id,sd.id) = :idNota");
            qServico.setParameter("idNota", nota.getIdNota());
            List<RelatorioLivroFiscalServicoDTO> servicos = Lists.newArrayList();
            List<Object[]> resultListServicos = qServico.getResultList();
            for (Object[] objServico : resultListServicos) {
                servicos.add(popularServicoParaRelatorio(objServico));
            }
            nota.setServicos(servicos);
            Collections.sort(nota.getServicos());
        }
        periodoDTO.setNotas(notas);
        Collections.sort(periodoDTO.getNotas());
        return periodoDTO;
    }

    private void popularLivroParaRelatorio(Object[] obj, RelatorioLivroFiscalDTO livroDto, Mes mesInicial, Mes mesFinal) {
        livroDto.setIdLivro(((Number) obj[0]).longValue());
        livroDto.setNumeroLivro(((Number) obj[1]).intValue());

        livroDto.setPeriodoInicial(DataUtil.primeiroDiaMes(mesInicial));
        livroDto.setPeriodoFinal(DataUtil.ultimoDiaDoMes(mesFinal));
        if (obj[4] != null) livroDto.setIdEmpresa(((Number) obj[4]).longValue());
        if (obj[5] != null) livroDto.setInscricaoMunicipalEmpresa(((String) obj[5]));
        if (obj[6] != null) livroDto.setCpfCnpjEmpresa(((String) obj[6]));
        if (obj[7] != null) livroDto.setNomeRazaoSocialEmpresa(((String) obj[7]));
        if (obj[8] != null) livroDto.setEnderecoEmpresa(((String) obj[8]));
        if (obj[9] != null) livroDto.setNumeroEmpresa(((String) obj[9]));
        if (obj[10] != null) livroDto.setComplementoEmpresa(((String) obj[10]));
        if (obj[11] != null) livroDto.setBairroEmpresa(((String) obj[11]));
        if (obj[12] != null) livroDto.setCepEmpresa(((String) obj[12]));
        if (obj[13] != null) livroDto.setCidadeEmpresa(((String) obj[13]));
        if (obj[14] != null) livroDto.setUfEmpresa(((String) obj[14]));
        if (obj[15] != null) livroDto.setNomeRazaoSocialEscritorio(((String) obj[15]));
        if (obj[16] != null) livroDto.setCpfCnpjEscritorio(((String) obj[16]));
        if (obj[17] != null) livroDto.setNomeContador(((String) obj[17]));
        if (obj[18] != null) livroDto.setCrcEscritorio(((String) obj[18]));
        if (obj[19] != null) livroDto.setTipoMovimento((String) obj[19]);
    }

    private void popularNotaParaRelatorio(Object[] objNota, RelatorioLivroFiscalNotaDTO nota) {
        if (objNota[0] != null) nota.setIdNota(((Number) objNota[0]).longValue());
        if (objNota[1] != null) nota.setNumeroNota(((Number) objNota[1]).intValue());
        if (objNota[2] != null) nota.setTipoNota(((String) objNota[2]));
        if (objNota[3] != null) nota.setSimplesNacional(((BigDecimal) objNota[3]).intValue() == 1);
        if (objNota[4] != null) nota.setMei(((BigDecimal) objNota[4]).intValue() == 1);
        if (objNota[5] != null) nota.setExigibilidadeIss(((String) objNota[5]));
        if (objNota[6] != null) nota.setSituacaoNota(((String) objNota[6]));
        if (objNota[7] != null) nota.setIssRetido((((BigDecimal) objNota[7]).intValue() == 1));
        if (objNota[8] != null) nota.setTotalBaseCalculo(((BigDecimal) objNota[8]));
        if (objNota[9] != null) nota.setTotalISS(((BigDecimal) objNota[9]));
        if (objNota[10] != null) nota.setNomeTtomador(((String) objNota[10]));
        if (objNota[11] != null) nota.setCpfCnpjTtomador(((String) objNota[11]));
        if (objNota[12] != null) nota.setMunicipio(((String) objNota[12]));
        if (objNota[13] != null) nota.setUf(((String) objNota[13]));

    }

    private RelatorioLivroFiscalServicoDTO popularServicoParaRelatorio(Object[] objServico) {
        RelatorioLivroFiscalServicoDTO servicoDTO = new RelatorioLivroFiscalServicoDTO();
        if (objServico[0] != null) servicoDTO.setServico((String) objServico[0]);
        if (objServico[1] != null) servicoDTO.setQuantidade(((Number) objServico[1]).intValue());
        if (objServico[2] != null) servicoDTO.setValorServico((BigDecimal) objServico[2]);
        if (objServico[3] != null) servicoDTO.setDeducoes((BigDecimal) objServico[3]);
        if (objServico[4] != null) servicoDTO.setDescontosIncondicionaos((BigDecimal) objServico[4]);
        if (objServico[5] != null) servicoDTO.setDescontosCondicionados((BigDecimal) objServico[5]);
        if (objServico[6] != null) servicoDTO.setBaseCalculo((BigDecimal) objServico[6]);
        if (objServico[7] != null) servicoDTO.setAliquota((BigDecimal) objServico[7]);
        if (objServico[8] != null) servicoDTO.setIss((BigDecimal) objServico[8]);
        if (objServico[9] != null) servicoDTO.setRetido(((BigDecimal) objServico[9]).intValue() == 1);

        return servicoDTO;
    }


    public List<ParecerFiscalNfse> buscarPareceresPorCompetencia(Mes mes, Integer ano, Long idCadastro) {
        Query query = em.createQuery("select p from ParecerFiscalNfse  p where p.ano = :ano and p.mes = :mes and p.cadastroEconomico.id = :idCadastro");
        query.setParameter("ano", ano);
        query.setParameter("mes", mes);
        query.setParameter("idCadastro", idCadastro);
        return query.getResultList();
    }


    public ParecerFiscalNfse salvar(ParecerFiscalNfse parecer) {
        return em.merge(parecer);
    }
}
