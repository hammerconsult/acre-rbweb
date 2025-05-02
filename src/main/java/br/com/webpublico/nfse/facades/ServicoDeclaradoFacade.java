package br.com.webpublico.nfse.facades;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.nfse.enums.*;
import br.com.webpublico.nfse.exceptions.NfseOperacaoNaoPermitidaException;
import br.com.webpublico.nfse.util.GeradorQuery;
import br.com.webpublico.nfse.util.PesquisaGenericaNfseUtil;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.*;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@Stateless
public class ServicoDeclaradoFacade extends AbstractFacade<ServicoDeclarado> {

    private final static Integer MAX_RESULT = 10;
    private final static String SELECT_IMPRESSAO_SERVICO_DECLARADO = " select ce.inscricaoCadastral as tomadorCmc, " +
        "        dpt.nomeRazaoSocial as tomadorRazaoSocial, " +
        "        dpt.cpfCnpj as tomadorCpfCnpj, " +
        "        dpp.inscricaoMunicipal as prestadorCmc, " +
        "        dpp.nomeRazaoSocial as prestadorRazaoSocial, " +
        "        dpp.cpfCnpj as prestadorCpfCnpj, " +
        "        sd.numero as numero, " +
        "        sd.emissao as emissao, " +
        "        tsd.descricao as tipoDocumento, " +
        "        c.nome as municipio, " +
        "        s.codigo as item, " +
        "        s.nome as servico, " +
        "        ids.valorServico as valorServico," +
        "        dec.deducoesLegais as deducao, " +
        "        dec.baseCalculo as baseCalculo, " +
        "        ids.aliquotaServico as aliquota, " +
        "        ids.iss as imposto," +
        "        dec.situacao as situacao ";
    private final static String SELECT_SERVICO_DECLARADO = " select sd.id as id, " +
        "        sd.numero as numero, " +
        "        sd.emissao as emissao, " +
        "        sd.tiposervicodeclarado as tipo_servico, " +
        "        tsd.codigo as tipo_documento_codigo, " +
        "        tsd.descricao as tipo_documento_descricao, " +
        "        dpt.cpfcnpj as tomador_cpf_cnpj," +
        "        dpt.nomerazaosocial as tomador_nome_razao, " +
        "        dpp.cpfcnpj as prestador_cpf_cnpj," +
        "        dpp.nomerazaosocial as prestador_nome_razao, " +
        "        dec.issretido as iss_retido, " +
        "        dec.totalservicos as total_servico, " +
        "        dec.basecalculo as base_calculo, " +
        "        dec.isscalculado as iss_calculado," +
        "        dec.situacao as situacao  ";
    private final static String FROM_SERVICO_DECLARADO = "     from declaracaoprestacaoservico dec " +
        "  inner join itemdeclaracaoservico ids on ids.declaracaoprestacaoservico_id = dec.id " +
        "  inner join servico s on s.id = ids.servico_id " +
        "  left join cidade c on c.id = ids.municipio_id " +
        "  left join uf uf on uf.id = c.uf_id " +
        "  inner join servicodeclarado sd on dec.id = sd.declaracaoprestacaoservico_id " +
        "  inner join cadastroeconomico ce on ce.id = sd.cadastroeconomico_id " +
        "  left join tipodocservicodeclarado tsd on tsd.id = sd.tipodocservicodeclarado_id " +
        "  left join dadospessoaisnfse dpt on dpt.id = dec.dadospessoaistomador_id " +
        "  left join dadospessoaisnfse dpp on dpp.id = dec.dadospessoaisprestador_id " +
        "  left join notadeclarada nd on nd.DECLARACAOPRESTACAOSERVICO_ID = dec.ID and dec.SITUACAO = 'PAGA' " +
        "  left join declaracaomensalservico dms on dms.id = nd.DECLARACAOMENSALSERVICO_ID and dec.SITUACAO = 'PAGA' " +
        "  left join processocalculo proc on proc.id = dms.PROCESSOCALCULO_ID and dec.SITUACAO = 'PAGA' " +
        "  left join calculo on calculo.PROCESSOCALCULO_ID = proc.id and dec.SITUACAO = 'PAGA' " +
        "  left join valordivida vd on vd.CALCULO_ID = calculo.id and dec.SITUACAO = 'PAGA' " +
        "  left join PARCELAVALORDIVIDA pvd on pvd.VALORDIVIDA_ID = vd.id and dec.SITUACAO = 'PAGA'";

    private final String CAMPOS_SQL_RELATORIO_SERVICOS_DECLARADOS = "select dec.id, " +
        " sd.numero, " +
        " sd.emissao, " +
        " dpp.nomeRazaoSocial prestadorNomeRazao, " +
        " dpp.cpfCnpj prestadorCnpj, " +
        " dpt.nomeRazaoSocial tomadorNomeRazao, " +
        " dpt.cpfCnpj tomadorCpfCnpj, " +
        " dec.situacao, " +
        " dec.modalidade, " +
        " dec.issRetido, " +
        " dec.totalServicos, " +
        " dec.deducoesLegais + dec.descontosIncondicionais + dec.descontosCondicionais + dec.retencoesFederais, " +
        " dec.baseCalculo, " +
        " dec.isscalculado," +
        " dec.naturezaoperacao," +
        " coalesce(dec.competencia, sd.emissao), " +
        " case when dec.SITUACAO = 'PAGA' then (select max(numerodam) " +
        "                                            from dam inner join itemdam id on id.DAM_ID = dam.id " +
        "                                            where id.PARCELA_ID = pvd.id and dam.SITUACAO = 'PAGO') " +
        " else '' end as  dam, " +
        " case when dec.SITUACAO = 'PAGA' then (select max(ilb.DATAPAGAMENTO) " +
        "                                            from dam inner join itemdam id on id.DAM_ID = dam.id " +
        "                                                    inner join ITEMLOTEBAIXA ilb on ilb.DAM_ID = dam.id " +
        "                                            where id.PARCELA_ID = pvd.id and dam.SITUACAO = 'PAGO')" +
        " else null end as pagamento ";


    @EJB
    private TipoDocumentoServicoDeclaradoFacade tipoDocumentoFiscalFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private PaisFacade paisFacade;
    @EJB
    private ServicoFacade servicoFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private UsuarioWebFacade usuarioNfseService;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public ServicoDeclaradoFacade() {
        super(ServicoDeclarado.class);
    }


    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    @Override
    public ServicoDeclarado recuperar(Object id) {
        ServicoDeclarado dec = super.recuperar(id);
        for (ItemDeclaracaoServico item : dec.getDeclaracaoPrestacaoServico().getItens()) {
            Hibernate.initialize(item.getDetalhes());
        }
        return dec;
    }

    public Long buscarUltimoNumero(Long prestadorId, Integer ano, TipoServicoDeclarado tipo) {
        String sql = "select max(numero) from" +
            "  SERVICODECLARADO where CADASTROECONOMICO_ID = :prestadorId " +
            "  and extract(year from EMISSAO) =:ano " +
            "  and TIPOSERVICODECLARADO = :tipo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("prestadorId", prestadorId);
        q.setParameter("ano", ano);
        q.setParameter("tipo", tipo.name());
        if (!q.getResultList().isEmpty()) {
            return ((Number) q.getResultList().get(0)).longValue();
        }
        return 0L;
    }


    public ServicoDeclarado recupera(Long id) {
        ServicoDeclarado recuperar = recuperar(id);
        Hibernate.initialize(recuperar.getDeclaracaoPrestacaoServico().getItens());
        cadastroEconomicoFacade.inicializarDependencias(recuperar.getCadastroEconomico());
        return recuperar;
    }

    public void delete(Long id) {
        ServicoDeclarado servicoDeclarado = recuperar(id);
        if (declaracaoMensalServicoFacade.hasDeclaracaoMensalServico(servicoDeclarado.getDeclaracaoPrestacaoServico().getId())) {
            throw new NfseOperacaoNaoPermitidaException("Já foi emitida a guia do serviço. Por esse motivo não pode ser excluido.");
        }
        em.remove(recuperar(id));
    }

    public void criarItemDeclaracao(DeclaracaoPrestacaoServico declaracao, ItemDeclaracaoServicoNfseDTO itemDTO) {
        ItemDeclaracaoServico item = new ItemDeclaracaoServico();
        item.setAliquotaServico(itemDTO.getAliquotaServico());
        item.setBaseCalculo(itemDTO.getBaseCalculo());
        item.setDescricao(itemDTO.getDescricao());
        item.setDeducoes(itemDTO.getDeducoes());
        item.setIss(itemDTO.getIss());
        if (itemDTO.getMunicipio() != null && itemDTO.getMunicipio().getId() != null) {
            item.setMunicipio(cidadeFacade.recuperar(itemDTO.getMunicipio().getId()));
        }

        item.setNomeServico(itemDTO.getNomeServico());
        item.setObservacoes(itemDTO.getObservacoes());
        if (itemDTO.getPais() != null && itemDTO.getPais().getId() != null) {
            item.setPais(paisFacade.recuperar(itemDTO.getPais().getId()));
        }
        item.setPrestadoNoPais(itemDTO.getPrestadoNoPais());

        if (itemDTO.getServico() != null && itemDTO.getServico().getId() != null) {
            item.setServico(servicoFacade.recuperar(itemDTO.getServico().getId()));
        }
        if (itemDTO.getCnae() != null && itemDTO.getCnae().getId() != null) {
            item.setCnae(cnaeFacade.recuperar(itemDTO.getCnae().getId()));
        }
        item.setValorServico(itemDTO.getValorServico());
        item.setDeclaracaoPrestacaoServico(declaracao);
        declaracao.getItens().add(item);
    }

    public List<NotaFiscalSearchDTO> buscarPorEmpresaCompetencia(Long empresaId, int mes, int ano) {

        String hql = "SELECT" +
            "  dec.id," +
            "  coalesce(nota.numero, rec.numero)," +
            "  coalesce(nota.emissao, rec.emissao)," +
            "  dadosPessoais.nomeRazaoSocial as nome_tomador," +
            "  dadosPessoais.cpfCnpj as cpfcnpj_tomador," +
            "  dadosPessoaisPrestador.nomeRazaoSocial as nome_prestador," +
            "  dadosPessoaisPrestador.cpfCnpj as cpfcnpj_prestador," +
            "  dec.situacao," +
            "  dec.totalServicos," +
            "  dec.issPagar," +
            "  dec.baseCalculo," +
            "  dec.issretido," +
            "  dec.tipodocumento, " +
            "  dec.issCalculado, " +
            "  ce.id as cadastro_id," +
            "  dec.naturezaoperacao " +
            " FROM DeclaracaoPrestacaoServico dec" +
            "  LEFT JOIN NotaFiscal nota ON dec.id = nota.declaracaoPrestacaoServico_id" +
            "  LEFT JOIN servicodeclarado sd ON dec.id = sd.declaracaoPrestacaoServico_id" +
            "  INNER JOIN DadosPessoaisNfse dadosPessoais ON dadosPessoais.id = dec.dadosPessoaisTomador_id" +
            "  INNER JOIN DadosPessoaisNfse dadosPessoaisPrestador ON dadosPessoaisPrestador.id = dec.dadosPessoaisPrestador_id" +
            "  INNER JOIN PESSOA ON pessoa.id = dadosPessoais.PESSOA_ID" +
            "  INNER JOIN CADASTROECONOMICO ce ON ce.PESSOA_ID = PESSOA.id" +
            " WHERE dec.ISSRETIDO = 1" +
            "      AND ce.id = :empresaId" +
            "      AND EXTRACT(MONTH FROM dec.COMPETENCIA) = :mes" +
            "      AND EXTRACT(YEAR FROM dec.COMPETENCIA) = :ano";

        Query q = em.createNativeQuery(hql);
        q.setParameter("empresaId", empresaId);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        List<Object[]> resultado = q.getResultList();
        List<NotaFiscalSearchDTO> retorno = Lists.newArrayList();
        NotaFiscalFacade.popularSearchDto(resultado, retorno);
        return retorno;
    }

    public ServicoDeclarado buscarPorNumero(String numero, Long cadastroId) {
        Query q = em.createNativeQuery("select sd.* " +
            " from servicodeclarado sd " +
            " where sd.numero = :numero and sd.cadastroeconomico_id = :cadastroId", ServicoDeclarado.class);
        q.setParameter("numero", Integer.valueOf(numero));
        q.setParameter("cadastroId", cadastroId);
        if (!q.getResultList().isEmpty()) {
            return (ServicoDeclarado) q.getResultList().get(0);
        }
        return null;
    }

    public List<ServicoDeclarado> buscarPorNumeroParcialAndSituacao(String numero, Long cadastroId, SituacaoNota... situacoes) {
        List<String> situacoesString = Lists.newArrayList();
        for (SituacaoNota situacao : situacoes) {
            situacoesString.add(situacao.name());
        }
        String sql = "select sd.* " +
            " from servicodeclarado sd " +
            " inner join declaracaoprestacaoservico dps on sd.declaracaoPrestacaoServico_id = dps.id" +
            " where sd.cadastroeconomico_id = :cadastroId" +
            " and dps.situacao in (:situacoes)";
        if (!Strings.isNullOrEmpty(numero)) {
            sql += " and to_char(sd.numero) = :numero ";
        }
        Query q = em.createNativeQuery(sql, ServicoDeclarado.class);
        if (!Strings.isNullOrEmpty(numero)) {
            q.setParameter("numero", numero);
        }
        q.setParameter("cadastroId", cadastroId);
        q.setParameter("situacoes", situacoesString);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public ServicoDeclarado buscarPorDeclaracaoPrestacaoServico(DeclaracaoPrestacaoServico declaracaoPrestacaoServico) {
        String sql = "select sd from ServicoDeclarado sd where sd.declaracaoPrestacaoServico.id = :id";
        Query q = em.createQuery(sql).setParameter("id", declaracaoPrestacaoServico.getId());
        if (!q.getResultList().isEmpty()) {
            return (ServicoDeclarado) q.getResultList().get(0);
        }
        return null;
    }


    public LoteDocumentoRecebido criarLote(LoteImportacaoDocumentoRecebidoNfseDTO dto) {
        LoteDocumentoRecebido lote = new LoteDocumentoRecebido();
        lote.setSituacao(SituacaoLoteDocumentoRecebido.AGUARDANDO);
        lote.setPrestador(cadastroEconomicoFacade.recuperar(dto.getPrestador().getId()));
        try {
            lote.setXml(base64ToXml(dto));
            Document doc = Util.inicializarDOM(lote.getXml());
            XPath xPath = XPathFactory.newInstance().newXPath();
            lote.setNumero(getEvaluate(xPath, doc, "ImportacaoNotasFiscais/LoteNotasFiscais/NumeroLote"));
            validarNumeroLote(lote.getNumero(), dto.getPrestador().getId());
        } catch (ValidacaoException e) {
            throw new NfseOperacaoNaoPermitidaException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            lote.setSituacao(SituacaoLoteDocumentoRecebido.INCONSISTENTE);
            lote.addLog("O Lote não pode ser processado " + e.getMessage());
        }
        LoteDocumentoRecebido salvar = em.merge(lote);
        return salvar;
    }

    public void validarNumeroLote(String numero, Long prestadorId) {
        String sql = "select id from LoteDocumentoRecebido where numero = :numero and prestador_id = :prestadorId";
        Query q = em.createNativeQuery(sql);
        q.setParameter("numero", numero);
        q.setParameter("prestadorId", prestadorId);
        if (!q.getResultList().isEmpty()) {
            throw new ValidacaoException("Já existe um lote na base com o número " + numero);
        }
    }

    private BigDecimal getValorDoXml(XPath xPath, Node docRecebido, String s) throws XPathExpressionException {
        try {
            return new BigDecimal(getEvaluate(xPath, docRecebido, s));
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }


    private String base64ToXml(LoteImportacaoDocumentoRecebidoNfseDTO lote) {
        String conteudo = lote.getFile();
        String data = conteudo.split("base64,")[1];
        Base64 decoder = new Base64();
        byte[] decode = decoder.decode(data);
        return new String(decode);
    }

    private String getEvaluate(XPath xPath, Node nodeDoc, String expression) throws XPathExpressionException {
        return xPath.compile(expression).evaluate(nodeDoc);
    }

    public Page<LoteImportacaoDocumentoRecebidoNfseDTO> buscarLotesPorEmpresa(Pageable pageable, Long empresaId, String filtro) {
        String select = "select a.id, a.numero, a.situacao ";
        String count = "select count(a.id) ";
        String from = "  from LoteDocumentoRecebido a" +
            " where a.prestador_id = :empresaId " +
            " and (lower(a.numero) like :filtro " +
            " OR lower(a.situacao) like :filtro) ";
        from += PesquisaGenericaNfseUtil.montarOrdem(pageable);
        Query q = em.createNativeQuery(select + from);
        Query qCount = em.createNativeQuery(count + from);
        q.setParameter("empresaId", empresaId);
        qCount.setParameter("empresaId", empresaId);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        qCount.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);
        int resultCount = ((Number) qCount.getSingleResult()).intValue();
        List<LoteImportacaoDocumentoRecebidoNfseDTO> dtos = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        for (Object[] obj : resultado) {
            LoteImportacaoDocumentoRecebidoNfseDTO dto = new LoteImportacaoDocumentoRecebidoNfseDTO();
            dto.setId(((Number) obj[0]).longValue());
            dto.setNumero((String) obj[1]);
            dto.setSituacao(LoteImportacaoDocumentoRecebidoNfseDTO.Situacao.valueOf((String) obj[2]));
            dtos.add(dto);
        }
        return new PageImpl<>(dtos, pageable, resultCount);
    }

    public NfseEntity recuperarLote(Long id) {
        return em.find(LoteDocumentoRecebido.class, id);
    }

    public Page<ServicoDeclaradoSearchDTO> buscarServicosDeclarados(Pageable pageable, Long idEmpresa, String filtro, Long loteId) {
        filtro = "%" + filtro.trim().toLowerCase() + "%";
        String select = " select " +
            " sd.id as identificador, " +
            " sd.numero as numero, " +
            " sd.emissao as emissao, " +
            " case " +
            "      when dec.tipodocumento = 'NFSE' then dadospessoaistomador.nomerazaosocial " +
            "      when dec.tipodocumento = 'SERVICO_DECLARADO' then dadospessoaisprestador.nomerazaosocial " +
            " end as cpfCnpj,  " +
            " case " +
            "      when dec.tipodocumento = 'NFSE' then dadospessoaistomador.cpfcnpj " +
            "      when dec.tipodocumento = 'SERVICO_DECLARADO' then dadospessoaisprestador.cpfcnpj " +
            "   end as nomeRazaoSocial, " +
            "   dec.situacao, " +
            "   dec.modalidade, " +
            "   dec.issretido, " +
            "   dec.totalservicos, " +
            "   dec.basecalculo, " +
            "   dec.isscalculado," +
            "   dec.competencia, " +
            "   dec.tipoDocumento as origem, " +
            "   sd.tiposervicodeclarado as tipo, " +
            "   tipodoc.descricao as tipoDocumento," +
            "   dec.id as idDeclaracao," +
            "   dms.id as idDms," +
            "   calculo.id as idCalculo ";
        String from = " from declaracaoprestacaoservico dec " +
            "  left join servicodeclarado sd on dec.id = sd.declaracaoprestacaoservico_id " +
            "  left join TIPODOCSERVICODECLARADO tipodoc on tipodoc.id = sd.tipodocservicodeclarado_id " +
            "  inner join dadospessoaisnfse dadospessoaisprestador on dadospessoaisprestador.id = dec.dadospessoaisprestador_id " +
            "  left join dadospessoaisnfse dadospessoaistomador on dadospessoaistomador.id = dec.dadospessoaistomador_id " +
            "  left join notadeclarada nd on nd.declaracaoprestacaoservico_id = dec.id " +
            "  left join declaracaomensalservico dms on dms.id = nd.declaracaomensalservico_id  " +
            "                                      and dms.situacao != :cancelado  " +
            " left join processocalculo proc on proc.id = dms.processocalculo_id   " +
            " left join calculo on calculo.processocalculo_id = proc.id   " +
            "where (sd.cadastroeconomico_id = :idEmpresa) " +
            "   and dec.situacao in (:situacao) " +
            "   and (to_char(sd.numero) = :numero " +
            "        or " +
            "        (dec.tipodocumento = 'NFSE' " +
            "         and (lower(dadospessoaisprestador.nomerazaosocial) like :filtro or " +
            "              dadospessoaisprestador.cpfcnpj like replace(replace(replace(:filtro,'.',''),'-',''),'/',''))) " +
            "        or " +
            "        (dec.tipodocumento = 'SERVICO_DECLARADO' " +
            "         and (lower(dadospessoaisprestador.nomerazaosocial) like :filtro or " +
            "              dadospessoaisprestador.cpfcnpj like replace(replace(replace(:filtro,'.',''),'-',''),'/','') or " +
            "              lower(dadospessoaistomador.nomerazaosocial) like :filtro or " +
            "              dadospessoaistomador.cpfcnpj like replace(replace(replace(:filtro,'.',''),'-',''),'/',''))) " +
            "       ) ";
        if (loteId != null) {
            from += " and sd.lote_id = :loteId";
        }

        String orderBy = PesquisaGenericaNfseUtil.montarOrdem(pageable);
        if (Strings.isNullOrEmpty(orderBy)) {
            orderBy = " order by sd.emissao desc";
        }
        Query q = em.createNativeQuery(select + from + orderBy);
        Query qCount = em.createNativeQuery("select count(dec.id) " + from);
        q.setParameter("idEmpresa", idEmpresa);
        qCount.setParameter("idEmpresa", idEmpresa);
        q.setParameter("filtro", filtro);
        q.setParameter("numero", StringUtil.retornaApenasNumeros(filtro));
        qCount.setParameter("filtro", filtro);
        qCount.setParameter("numero", StringUtil.retornaApenasNumeros(filtro));
        q.setParameter("situacao", Lists.newArrayList(SituacaoNota.EMITIDA.name(), SituacaoNota.CANCELADA.name()));
        qCount.setParameter("situacao", Lists.newArrayList(SituacaoNota.EMITIDA.name(), SituacaoNota.CANCELADA.name()));
        if (loteId != null) {
            q.setParameter("loteId", loteId);
            qCount.setParameter("loteId", loteId);
        }
        q.setParameter("cancelado", DeclaracaoMensalServico.Situacao.CANCELADO.name());
        qCount.setParameter("cancelado", DeclaracaoMensalServico.Situacao.CANCELADO.name());
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);
        int resultCount = ((Number) qCount.getSingleResult()).intValue();
        List<Object[]> resultado = q.getResultList();
        return pupularServicosDeclaradosDto(pageable, resultCount, resultado);
    }

    public Page<ServicoDeclaradoSearchDTO> pupularServicosDeclaradosDto(Pageable pageable, int resultCount, List<Object[]> resultado) {
        List<ServicoDeclaradoSearchDTO> retorno = Lists.newArrayList();
        for (Object[] obj : resultado) {
            ServicoDeclaradoSearchDTO dto = new ServicoDeclaradoSearchDTO();
            dto.setId(((BigDecimal) obj[0]).longValue());
            if (obj[1] != null) {
                dto.setNumero(((BigDecimal) obj[1]).intValue());
            }
            dto.setEmissao((Date) obj[2]);
            dto.setNomePrestador((String) obj[3]);
            dto.setCpfCnpjPrestador((String) obj[4]);
            dto.setSituacao((String) obj[5]);
            dto.setModalidade((String) obj[6]);
            dto.setIssRetido(((Number) obj[7]).intValue() == 1);
            dto.setTotalServicos((BigDecimal) obj[8]);
            dto.setBaseCalculo((BigDecimal) obj[9]);
            dto.setIssCalculado((BigDecimal) obj[10]);
            dto.setCompetencia((Date) obj[11]);
            dto.setOrigem((String) obj[12]);
            dto.setTipo((String) obj[13]);
            dto.setTipoDocumento((String) obj[14]);
            dto.setIdDeclaracao(((BigDecimal) obj[15]).longValue());
            dto.setIdDms(obj[16] != null ? ((BigDecimal) obj[16]).longValue() : null);
            if (obj[17] != null) {
                List<ResultadoParcela> cp = new ConsultaParcela()
                    .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, ((BigDecimal) obj[17]).longValue())
                    .executaConsulta().getResultados();
                for (ResultadoParcela parcela : cp) {
                    dto.setSituacaoDebito(parcela.getSituacao());
                }
            }
            retorno.add(dto);
        }
        return new PageImpl<>(retorno, pageable, resultCount);
    }

    public List<TipoDocumentoServicoDeclarado> findAllTiposServicosDeclarados() {
        String hql = "select tipo from TipoDocumentoServicoDeclarado tipo where tipo.ativo = 1 ";
        return em.createQuery(hql).getResultList();
    }

    public Page<ServicoDeclaradoSearchDTO> buscarServicosDeclarados(Pageable pageable, Long empresaId, String filtro) {
        return buscarServicosDeclarados(pageable, empresaId, filtro, null);
    }

    public Page<ServicoDeclaradoSearchDTO> buscarServicosDeclaradosPorLote(Pageable pageable, Long empresaId, String filtro, Long loteId) {
        return buscarServicosDeclarados(pageable, empresaId, filtro, loteId);
    }

    public DeclaracaoPrestacaoServico recuperarDeclaracaoPrestacaoServico(Object id) {
        DeclaracaoPrestacaoServico dec = em.find(DeclaracaoPrestacaoServico.class, id);
        Hibernate.initialize(dec.getItens());
        return dec;
    }

    public List<ServicoDeclaradoSearchNfseDTO> consultarServicosDeclarados(List<ParametroQuery> parametros,
                                                                           String orderBy,
                                                                           Integer firstResult, Integer maxResult) throws Exception {

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_SERVICO_DECLARADO)
            .append(FROM_SERVICO_DECLARADO);
        Map<String, Object> parameters = GeradorQuery.montarParametroString(sql, parametros);
        sql.append(orderBy);
        Query q = em.createNativeQuery(sql.toString());
        GeradorQuery.adicionarParametrosNaQuery(parameters, q);

        if (firstResult != null)
            q.setFirstResult(firstResult);

        if (maxResult != null)
            q.setMaxResults(maxResult);

        List resultList = q.getResultList();
        List<Object[]> objetos = resultList;

        if (resultList != null && !resultList.isEmpty()) {
            List<ServicoDeclaradoSearchNfseDTO> retorno = Lists.newArrayList();
            for (Object[] obj : objetos) {

                ServicoDeclaradoSearchNfseDTO item = new ServicoDeclaradoSearchNfseDTO();
                item.setId(((BigDecimal) obj[0]).longValue());
                item.setNumero(obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null);
                item.setEmissao((Date) obj[2]);
                item.setTipoServicoDeclarado((String) obj[3]);
                item.setTipoDocumentoCodigo(obj[4] != null ? ((BigDecimal) obj[4]).longValue() : null);
                item.setTipoDocumentoDescricao(obj[5] != null ? (String) obj[5] : "");
                item.setTomadorCpfCnpj((String) obj[6]);
                item.setTomadorNomeRazaoSocial((String) obj[7]);
                item.setPrestadorCpfCnpj((String) obj[8]);
                item.setPrestadorNomeRazaoSocial((String) obj[9]);
                item.setIssRetido(obj[8] != null && ((BigDecimal) obj[10]).intValue() == 1);
                item.setTotalServicos((BigDecimal) obj[11]);
                item.setBaseCalculo((BigDecimal) obj[12]);
                item.setIssCalculado((BigDecimal) obj[13]);
                item.setSituacao((String) obj[14]);
                retorno.add(item);
            }
            return retorno;
        }
        return Lists.newArrayList();
    }

    public Page<ServicoDeclaradoSearchNfseDTO> consultarServicosDeclarados(Pageable pageable,
                                                                           List<ParametroQuery> parametros,
                                                                           String orderBy) throws Exception {
        List<ServicoDeclaradoSearchNfseDTO> dtos = consultarServicosDeclarados(parametros, orderBy, pageable.getOffset(), pageable.getPageSize());

        StringBuilder sqlCount = new StringBuilder();
        sqlCount.append("select count(sd.id) ")
            .append(FROM_SERVICO_DECLARADO);
        Map<String, Object> parameters = GeradorQuery.montarParametroString(sqlCount, parametros);
        Query queryCount = em.createNativeQuery(sqlCount.toString());
        GeradorQuery.adicionarParametrosNaQuery(parameters, queryCount);
        Long count = 0L;
        List resultListCount = queryCount.getResultList();
        if (resultListCount != null && !resultListCount.isEmpty()) {
            count = ((BigDecimal) resultListCount.get(0)).longValue();
        }
        return new PageImpl<>(dtos, pageable, count);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<XSSFWorkbook> consultarServicos(FiltroRelatorioServicosDeclaradosDTO filtro) {
        List<RelatorioServicosDeclaradosDTO> resultados = buscarServicosDeclarados(filtro);
        ExcelUtil excelUtil = new ExcelUtil();
        XSSFWorkbook pastaDeTrabalho = new XSSFWorkbook();
        XSSFSheet sheet = excelUtil.criarSheet(pastaDeTrabalho, "RELATÓRIO DE SERVICOS DECLARADOS");

        int linhaInicial = 0;
        XSSFRow cabecalho = excelUtil.criaRow(sheet, linhaInicial);
        List<String> colunas = Lists.newArrayList(
            "Número", "Emissão", "Nome/Razão Social", "Situação", "Modalidade", "Operação","Retido", "Serviços(R$)",
            "Retenções(R$)", "Base de Cálculo(R$)", "ISS Calculado(R$)", "Número do DAM", "Data do Pagamento"
        );

        CellStyle cellStyle = pastaDeTrabalho.createCellStyle();
        XSSFFont font = pastaDeTrabalho.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Arial");
        font.setBold(true);
        font.setItalic(false);
        cellStyle.setFont(font);

        for (String atributo : colunas) {
            XSSFCell xssfCell = excelUtil.criaCell(cabecalho, colunas.indexOf(atributo));
            xssfCell.setCellValue(atributo);
            xssfCell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(colunas.indexOf(atributo));
        }

        linhaInicial++;
        for (RelatorioServicosDeclaradosDTO o : resultados) {
            XSSFRow linha = excelUtil.criaRow(sheet, linhaInicial);
            excelUtil.criaCell(linha,0).setCellValue(o.getNumero());
            excelUtil.criaCell(linha,1).setCellValue(DataUtil.getDataFormatada(o.getEmissao()));
            excelUtil.criaCell(linha,2).setCellValue(o.getNomeRazaoSocial());
            excelUtil.criaCell(linha,3).setCellValue(o.getSituacao());
            excelUtil.criaCell(linha,4).setCellValue(o.getModalidade());
            excelUtil.criaCell(linha,5).setCellValue(o.getNaturezaOperacao());
            excelUtil.criaCell(linha,6).setCellValue(o.getIssRetido());
            excelUtil.criaCell(linha,7).setCellValue(o.getTotalServicos().doubleValue());
            excelUtil.criaCell(linha,8).setCellValue(o.getTotalDeducoes().doubleValue());
            excelUtil.criaCell(linha,9).setCellValue(o.getBaseCalculo().doubleValue());
            excelUtil.criaCell(linha,10).setCellValue(o.getIssCalculo().doubleValue());
            if(o.getNumeroDam() != null){
                excelUtil.criaCell(linha,11).setCellValue(o.getNumeroDam());
            }else{
                excelUtil.criaCell(linha,11).setCellValue("");
            } if(o.getPagamento() != null){
                excelUtil.criaCell(linha,12).setCellValue(DataUtil.getDataFormatada(o.getPagamento()));
            }else{
                excelUtil.criaCell(linha,12).setCellValue("");
            }
            linhaInicial++;
        }
        linhaInicial = linhaInicial + 5;
        XSSFRow rowUsuario = excelUtil.criaRow(sheet, linhaInicial);
        excelUtil.criaCell(rowUsuario, 1).setCellValue(filtro.getUsuarioSistema().getNome());
        linhaInicial++;
        XSSFRow rodape = excelUtil.criaRow(sheet, linhaInicial);
        excelUtil.criaCell(rodape, 1).setCellValue("WebPúblico - Sistema Integrado de Gestão Pública");
        linhaInicial++;
        XSSFRow rowGeradoEm = excelUtil.criaRow(sheet, linhaInicial);
        excelUtil.criaCell(rowGeradoEm, 1).setCellValue("Gerado em - " + DataUtil.getDataFormatada(new Date()));
        return new AsyncResult<>(pastaDeTrabalho);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @Asynchronous
    public Future<JasperPrint> gerarRelatorioServicosDeclarados(HashMap parameters, FiltroRelatorioServicosDeclaradosDTO filtro, String caminho) {
        List<RelatorioServicosDeclaradosDTO> servicosDeclaradosDTOS = buscarServicosDeclarados(filtro);
        parameters.put("TOTALIZADOR", criarTotalizadorServicosDeclarados(servicosDeclaradosDTOS, filtro.getTipoAgrupamento()));
        parameters.put("TIPO_AGRUPAMENTO", filtro.getTipoAgrupamento().name());
        String arquivoJasper = "RelatorioDeServicosDeclarados.jasper";
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(servicosDeclaradosDTOS);
        try {
            JasperPrint jasperPrint = AbstractReport.getAbstractReport()
                .gerarBytesDeRelatorioComDadosEmCollectionView(caminho, arquivoJasper, parameters, ds);
            return new AsyncResult<>(jasperPrint);
        } catch (Exception e) {
            logger.error("Erro ao gerar relatório", e);
            return null;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<RelatorioServicosDeclaradosDTO> buscarServicosDeclarados(FiltroRelatorioServicosDeclaradosDTO filtro) {
        String sql = "";
        sql += CAMPOS_SQL_RELATORIO_SERVICOS_DECLARADOS;
        sql += FROM_SERVICO_DECLARADO;
        String whereOrAnd = " where ";
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            sql += whereOrAnd + " sd.numero = " + "cast(:numero as number)";
            whereOrAnd = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjInicialPrestador())) {
            sql += whereOrAnd + " dpp.cpfcnpj >= :cpfCnpjInicialPrestador ";
            whereOrAnd = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjFinalPrestador())) {
            sql += whereOrAnd + " dpp.cpfcnpj <= :cpfCnpjFinalPrestador ";
            whereOrAnd = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjInicialTomador())) {
            sql += whereOrAnd + " dpt.cpfcnpj >= :cpfCnpjInicialTomador ";
            whereOrAnd = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjFinalTomador())) {
            sql += whereOrAnd + " dpt.cpfcnpj <= :cpfCnpjFinalTomador ";
            whereOrAnd = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getTomador())) {
            sql += whereOrAnd + " dpt.nomerazaosocial = :tomador or dpt.nomerazaosocial = :tomador ";
            whereOrAnd = " and ";
        }
        if (filtro.getDataInicial() != null) {
            sql += whereOrAnd + " sd.emissao >= to_date(:dataInicial, 'dd/mm/yyyy') ";
            whereOrAnd = " and ";
        }
        if (filtro.getDataFinal() != null) {
            sql += whereOrAnd + " sd.emissao <= to_date(:dataFinal, 'dd/mm/yyyy') ";
            whereOrAnd = " and ";
        }
        if (filtro.getDataPagamentoInicial() != null) {
            sql += whereOrAnd + " exists (select id.id from dam inner join itemdam id on id.DAM_ID = dam.id " +
                " inner join ITEMLOTEBAIXA ilb on ilb.DAM_ID = dam.id " +
                " where id.PARCELA_ID = pvd.id and dam.SITUACAO = 'PAGO' and ilb.DATAPAGAMENTO  >= to_date(:dataPagamentoInicial, 'dd/mm/yyyy'))";
            whereOrAnd = " and ";
        }
        if (filtro.getDataPagamentoFinal() != null) {
            sql += whereOrAnd + " exists (select id.id from dam inner join itemdam id on id.DAM_ID = dam.id " +
                " inner join ITEMLOTEBAIXA ilb on ilb.DAM_ID = dam.id " +
                " where id.PARCELA_ID = pvd.id and dam.SITUACAO = 'PAGO' and ilb.DATAPAGAMENTO  <= to_date(:dataPagamentoFinal, 'dd/mm/yyyy'))";
            whereOrAnd = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getPrestador())) {
            sql += whereOrAnd + " dpp.nomerazaosocial like :prestador or dpp.cpfcnpj like :prestador";
        }
        if (filtro.getExigibilidade() != null) {
            sql += whereOrAnd + " dec.naturezaOperacao = :naturezaOperacao ";
        }
        if (filtro.getSituacoes() != null && !filtro.getSituacoes().isEmpty()) {
            sql += whereOrAnd + " dec.situacao in (:situacoes) ";
        }

        if (filtro.getServicos() != null && !filtro.getServicos().isEmpty()) {
            sql += whereOrAnd + " exists (select 1 from itemdeclaracaoservico ids where ids.declaracaoprestacaoservico_id = dec.id ";
            sql += " and ids.servico_id in (:servicos)) ";
            whereOrAnd = " and ";
        }

        sql += " order by sd.numero ";
        Query q = em.createNativeQuery(sql);
        if (filtro.getNumero() != null && !filtro.getNumero().isEmpty()) {
            q.setParameter("numero", filtro.getNumero());
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjInicialPrestador())) {
            q.setParameter("cpfCnpjInicialPrestador", StringUtil.retornaApenasNumeros(filtro.getCpfCnpjInicialPrestador()));
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjFinalPrestador())) {
            q.setParameter("cpfCnpjFinalPrestador", StringUtil.retornaApenasNumeros(filtro.getCpfCnpjFinalPrestador()));
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjInicialTomador())) {
            q.setParameter("cpfCnpjInicialTomador", StringUtil.retornaApenasNumeros(filtro.getCpfCnpjInicialTomador()));
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjFinalTomador())) {
            q.setParameter("cpfCnpjFinalTomador", StringUtil.retornaApenasNumeros(filtro.getCpfCnpjFinalTomador()));
        }
        if (!Strings.isNullOrEmpty(filtro.getTomador())) {
            q.setParameter("tomador", "%" + filtro.getTomador().trim().toUpperCase() + "%");
        }
        if (filtro.getDataInicial() != null) {
            q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        }
        if (filtro.getDataPagamentoInicial() != null) {
            q.setParameter("dataPagamentoInicial", DataUtil.getDataFormatada(filtro.getDataPagamentoInicial()));
        }
        if (filtro.getDataPagamentoFinal() != null) {
            q.setParameter("dataPagamentoFinal", DataUtil.getDataFormatada(filtro.getDataPagamentoFinal()));
        }
        if (filtro.getDataFinal() != null) {
            q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        }
        if (!Strings.isNullOrEmpty(filtro.getPrestador())) {
            q.setParameter("prestador", "%" + filtro.getPrestador().trim().toUpperCase() + "%");
        }
        if (filtro.getExigibilidade() != null) {
            q.setParameter("naturezaOperacao", filtro.getExigibilidade().name());
        }
        if (filtro.getSituacoes() != null && !filtro.getSituacoes().isEmpty()) {
            List<String> situacoes = Lists.newArrayList();
            for (SituacaoNota s : filtro.getSituacoes()) {
                situacoes.add(s.name());
            }
            q.setParameter("situacoes", situacoes);
        }

        if (filtro.getServicos() != null && !filtro.getServicos().isEmpty()) {
            q.setParameter("servicos", filtro.getIdsServicos());
        }

        List<RelatorioServicosDeclaradosDTO> registros = popularDTORelatorioServicosDeclarados(q);
        if (registros != null && !registros.isEmpty()) {
            for (RelatorioServicosDeclaradosDTO registro : registros) {
                registro.setItens(buscarItensServicosDeclarados(registro));
            }
        }
        return registros;
    }

    public List<RelatorioServicosDeclaradosDetailDTO> buscarItensServicosDeclarados(RelatorioServicosDeclaradosDTO relatorioServicosDeclaradosDTO) {
        String sql = "select " +
            "       s.codigo as codigoServico, " +
            "       ids.nomeservico as nomeServico, " +
            "       ids.descricao as descricao, " +
            "       ids.valorservico as valorServico, " +
            "       ids.deducoes as deducoes, " +
            "       ids.basecalculo as baseCalculo, " +
            "       ids.quantidade as quantidade, " +
            "       ids.aliquotaservico as aliquota," +
            "       ids.iss as iss " +
            "   from itemdeclaracaoservico ids " +
            "  inner join servico s on ids.servico_id = s.id " +
            "  inner join declaracaoprestacaoservico dps on dps.id = ids.declaracaoprestacaoservico_id  " +
            "where dps.id = :idDeclaracaoPrestacaoServico ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idDeclaracaoPrestacaoServico", relatorioServicosDeclaradosDTO.getId());
        return RelatorioServicosDeclaradosDetailDTO.popularDTO(q.getResultList());
    }

    public List<TotalizadorRelatorioServicosDeclaradosDTO> criarTotalizadorServicosDeclarados(List<RelatorioServicosDeclaradosDTO> servicosDeclaradosDTOS,
                                                                                              FiltroRelatorioServicosDeclaradosDTO.TipoAgrupamento tipoAgrupamento) {
        switch (tipoAgrupamento) {
            case NATUREZA_OPERACAO:
                return TotalizadorRelatorioServicosDeclaradosDTO.totalizarPorNatureza(servicosDeclaradosDTOS);
            case COMPETENCIA_NATUREZA_OPERACAO:
                return TotalizadorRelatorioServicosDeclaradosDTO.totalizarPorCompetenciaNatureza(servicosDeclaradosDTOS);
            case SERVICO_NATUREZA_OPERACAO:
                return TotalizadorRelatorioServicosDeclaradosDTO.totalizarPorServicoNatureza(servicosDeclaradosDTOS);
        }
        return Lists.newArrayList();
    }

    private List<RelatorioServicosDeclaradosDTO> popularDTORelatorioServicosDeclarados(Query q) {
        List<Object[]> resultadoConsuta = q.getResultList();
        List<RelatorioServicosDeclaradosDTO> retorno = Lists.newArrayList();
        for (Object[] obj : resultadoConsuta) {
            RelatorioServicosDeclaradosDTO dto = new RelatorioServicosDeclaradosDTO();
            popularServicosDeclarados(obj, dto);
            retorno.add(dto);
        }
        return retorno;
    }

    private void popularServicosDeclarados(Object[] obj, RelatorioServicosDeclaradosDTO dto) {
        dto.setId(((Number) obj[0]).longValue());
        dto.setNumero(obj[1] != null ? obj[1].toString() : "");
        dto.setEmissao((Date) obj[2]);
        dto.setNomeRazaoSocial((String) obj[3]);
        dto.setCpfCnpj((String) obj[4]);
        dto.setSituacao(obj[7] != null ? SituacaoNota.valueOf((String) obj[7]).getDescricao() : null);
        dto.setModalidade(obj[8] != null ? ModalidadeEmissao.valueOf((String) obj[8]).getDescricao() : null);
        dto.setIssRetido(((Number) obj[9]).intValue() > 0 ? "Sim" : "Não");
        dto.setTotalServicos((BigDecimal) obj[10]);
        dto.setTotalDeducoes((BigDecimal) obj[11]);
        dto.setBaseCalculo((BigDecimal) obj[12]);
        dto.setIssCalculo((BigDecimal) obj[13]);
        dto.setNaturezaOperacao(obj[14] != null ? Exigibilidade.valueOf((String) obj[14]).getDescricao() : null);
        dto.setCompetencia((Date) obj[15]);
        dto.setNumeroDam((String) obj[16]);
        dto.setPagamento((Date) obj[17]);
    }

}
