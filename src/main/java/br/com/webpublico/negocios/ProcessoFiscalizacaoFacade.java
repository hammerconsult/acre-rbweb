/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonProcessoFiscalizacao;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Leonardo
 */
@Stateless
public class ProcessoFiscalizacaoFacade extends CalculoExecutorDepoisDePagar<ProcessoFiscalizacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SecretariaFiscalizacaoFacade secretariaFiscalizacaoFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private GeraValorDividaProcFiscal geraDebito;
    @EJB
    private QuadraFacade quadraFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ProcessoFacade processoFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private DenunciaFacade denunciaFacade;
    @EJB
    private SingletonProcessoFiscalizacao singletonProcessoFiscalizacao;

    public ProcessoFiscalizacaoFacade() {
        super(ProcessoFiscalizacao.class);
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public DenunciaFacade getDenunciaFacade() {
        return denunciaFacade;
    }

    public void setDenunciaFacade(DenunciaFacade denunciaFacade) {
        this.denunciaFacade = denunciaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<ProcessoFiscalizacao> listaProcessos(UnidadeOrganizacional unidade) {
        String hql = "from ProcessoFiscalizacao proc ";
        if (unidade != null && unidade.getId() != null) {
            hql += "where proc.secretariaFiscalizacao.unidadeOrganizacional = :unidade "
                + "order by proc.id desc";
        }
        Query q = this.em.createQuery(hql, ProcessoFiscalizacao.class);

        if (unidade != null && unidade.getId() != null) {
            q.setParameter("unidade", unidade);
        }

        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }

    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public ProcessoFacade getProcessoFacade() {
        return processoFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public QuadraFacade getQuadraFacade() {
        return quadraFacade;
    }

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    public List<Bairro> completaBairro(String parte) {
        return bairroFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<Lote> completaLote(String parte) {
        return loteFacade.listaFiltrando(parte.trim(), "codigoLote");
    }

    public List<Quadra> completaQuadra(String parte) {
        return quadraFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public List<Endereco> completaEndereco(String parte) {
        String sql = "select endereco from Endereco endereco"
            + " inner join endereco.logradouro logradouro"
            + " where lower(logradouro.nome) like :parte or endereco.numero like :parte";
        Query consulta = em.createQuery(sql);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<Endereco>();
        }
    }

    public EntityManager getEm() {
        return em;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public SecretariaFiscalizacaoFacade getSecretariaFiscalizacaoFacade() {
        return secretariaFiscalizacaoFacade;
    }

    public Long recuperaProximaCodigo(SecretariaFiscalizacao secretariaFiscalizacao) {
        Query consulta = em.createQuery("select max(s.codigo) from ProcessoFiscalizacao s where s.secretariaFiscalizacao = :secretaria");
        consulta.setParameter("secretaria", secretariaFiscalizacao);
        try {
            return (Long) consulta.getSingleResult() + 1l;
        } catch (Exception e) {
            return 1l;
        }
    }

    public String recuperaProximaCodigoTermo(SecretariaFiscalizacao secretariaFiscalizacao) {
        String ano = sistemaFacade.getExercicioCorrente().getAno().toString();
        Query consulta = em.createQuery("select max(termo.numero) from ProcessoFiscalizacao s "
            + " inner join s.termoAdvertencia termo "
            + " where s.secretariaFiscalizacao = :secretaria");
        consulta.setParameter("secretaria", secretariaFiscalizacao);

        String resultado;
        try {
            if (consulta.getSingleResult() == null) {
                resultado = "000001/" + ano;
            } else {
                resultado = consulta.getSingleResult().toString();
                String[] teste = resultado.split("/");
                String codigo = resultado.substring(0, teste[0].length());
                long codigoMaisUm = Long.parseLong(codigo) + 1;
                String numeroNovoCodigo = String.valueOf(codigoMaisUm);
                resultado = StringUtil.preencheString(numeroNovoCodigo, teste[0].length(), '0') + "/" + ano;
            }
        } catch (Exception e) {
            resultado = "000001/" + ano;
        }
        return resultado;
    }

    public String recuperaProximaCodigoAutoDeInfracao(SecretariaFiscalizacao secretariaFiscalizacao) {
        String ano = sistemaFacade.getExercicioCorrente().getAno().toString();
        Query consulta = em.createQuery("select max(auto.numero) from ProcessoFiscalizacao s "
            + " inner join s.autoInfracaoFiscalizacao auto "
            + " where s.secretariaFiscalizacao = :secretaria");
        consulta.setParameter("secretaria", secretariaFiscalizacao);

        String resultado;
        consulta.setMaxResults(1);
        if (consulta.getResultList().get(0) == null) {
            resultado = "000001/" + ano;
        } else {
            resultado = consulta.getResultList().get(0).toString();
            String[] teste = resultado.split("/");
            String codigo = resultado.substring(0, teste[0].length());
            long codigoMaisUm = Long.parseLong(codigo) + 1;
            String numeroNovoCodigo = String.valueOf(codigoMaisUm);
            resultado = StringUtil.preencheString(numeroNovoCodigo, teste[0].length(), '0') + "/" + ano;
        }

        return resultado;
    }

    public SituacaoProcessoFiscal recuperaUltimaSituacaoDoProcesso(ProcessoFiscalizacao processo) {
        Query consulta = em.createQuery("select s from SituacaoProcessoFiscal s"
            + " where s.processoFiscalizacao = :processo"
            + " and s.id = (select max(sit.id) from SituacaoProcessoFiscal sit where sit.processoFiscalizacao = :processo)");
        consulta.setParameter("processo", processo);
        try {
            return (SituacaoProcessoFiscal) consulta.getSingleResult();
        } catch (Exception e) {
            return new SituacaoProcessoFiscal();
        }
    }

    public List<SecretariaFiscalizacao> completaSecretaria(String parte) {
        Query consulta = em.createQuery("select s from SecretariaFiscalizacao s where lower(s.unidadeOrganizacional.descricao) like :parte");
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return consulta.getResultList();
    }

    @Override
    public ProcessoFiscalizacao recuperar(Object id) {
        ProcessoFiscalizacao processo = em.find(ProcessoFiscalizacao.class, id);
        if (processo.getCadastro() != null) {
            processo.setCadastro(initializeAndUnproxy(processo.getCadastro()));
        }
        if (processo.getPessoa() != null) {
            processo.setPessoa(initializeAndUnproxy(processo.getPessoa()));
        }
        processo.getSecretariaFiscalizacao().getId();
        processo.getSituacoesProcessoFiscalizacao().size();
        processo.getRecursoFiscalizacaos().size();
        processo.getTermoGeralFiscalizacao().size();
        processo.getItensProcessoFiscalizacao().size();
        if (processo.getDetentorArquivoComposicao() != null) {
            processo.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return processo;
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficial(String parte, TipoCadastroDoctoOficial tipo) {

        Query consulta = em.createQuery(" select tipo from TipoDoctoOficial tipo "
            + " where tipo.moduloTipoDoctoOficial = :modulo"
            + " and tipo.tipoCadastroDoctoOficial = :tipo"
            + " and (lower(tipo.descricao) like :parte or to_char(tipo.codigo) like :parte)");
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("modulo", ModuloTipoDoctoOficial.TERMOADVERTENCIA);
        consulta.setParameter("tipo", tipo);
        try {
            return (List<TipoDoctoOficial>) consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<TipoDoctoOficial>();
        }
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialAutoInfracao(String parte, TipoCadastroDoctoOficial tipo) {
        Query consulta = em.createQuery(" select tipo from TipoDoctoOficial tipo "
            + " where tipo.moduloTipoDoctoOficial = 'AUTOINFRACAO'"
            + " and tipo.tipoCadastroDoctoOficial = '" + tipo.name() + "'"
            + " and (lower(tipo.descricao) like :parte or to_char(tipo.codigo) like :parte)");
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        try {
            return (List<TipoDoctoOficial>) consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<TipoDoctoOficial>();
        }
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialParecerRecurso(String parte, TipoCadastroDoctoOficial tipo) {
        Query consulta = em.createQuery(" select tipo from TipoDoctoOficial tipo "
            + " where tipo.moduloTipoDoctoOficial = 'PARECER_FISCALIZACAO'"
            + " and tipo.tipoCadastroDoctoOficial = '" + tipo.name() + "'"
            + " and (lower(tipo.descricao) like :parte or to_char(tipo.codigo) like :parte)");
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");

        try {
            return (List<TipoDoctoOficial>) consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<TipoDoctoOficial>();
        }
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialTermoGerais(String parte, TipoCadastroDoctoOficial tipo) {
        Query consulta = em.createQuery(" select tipo from TipoDoctoOficial tipo "
            + " where tipo.moduloTipoDoctoOficial = 'TERMO_GERAIS'"
            + " and tipo.tipoCadastroDoctoOficial = '" + tipo.name() + "'"
            + " and (lower(tipo.descricao) like :parte or to_char(tipo.codigo) like :parte)");
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        try {
            return (List<TipoDoctoOficial>) consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<TipoDoctoOficial>();
        }
    }

    public DocumentoOficial geraDocumentoTermoAdvertencia(TipoDoctoOficial tipoDoctoOficial, ProcessoFiscalizacao processo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        return documentoOficialFacade.geraDocumentoTermoAdvertencia(processo, null, processo.getCadastro(), processo.getPessoa(), tipoDoctoOficial, sistemaControlador);
    }

    public void geraPDF(DocumentoOficial documentoOficial) {
        documentoOficialFacade.emiteDocumentoOficial(documentoOficial);
    }

    public DocumentoOficial geraDocumentoAutoInfracao(TipoDoctoOficial tipoDoctoOficial, ProcessoFiscalizacao processoFiscalizacao, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        return documentoOficialFacade.geraDocumentoAutoInfracaoFiscalizacao(processoFiscalizacao, null, processoFiscalizacao.getCadastro(), processoFiscalizacao.getPessoa(), tipoDoctoOficial, sistemaControlador);
    }

    public DocumentoOficial geraDocumentoParecer(TipoDoctoOficial tipoDoctoOficial, RecursoFiscalizacao recurso, Cadastro cadastro, Pessoa pessoa, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        return documentoOficialFacade.geraDocumentoParecerRecursoFiscalizacao(recurso, null, cadastro, pessoa, tipoDoctoOficial, sistemaControlador);
    }

    public Arquivo salvarArquivo(FileUploadEvent upload) throws Exception {
        UploadedFile arquivoRecebido = upload.getFile();
        Arquivo arquivo = null;
        if (upload != null) {
            arquivo = new Arquivo();
            arquivo.setNome(arquivoRecebido.getFileName());
            arquivo.setMimeType(arquivoFacade.getMimeType(arquivoRecebido.getFileName()));
            arquivo.setDescricao(upload.getFile().getFileName());
            arquivo.setTamanho(arquivoRecebido.getSize());
            arquivoRecebido.getInputstream().close();
            arquivo = arquivoFacade.retornaArquivoSalvo(arquivo, arquivoRecebido.getInputstream());
        }
        return arquivo;
    }

    public String codigoPorSecretaria() {
        return singletonProcessoFiscalizacao.getProximoNumero(sistemaFacade.getExercicioCorrente().getAno()) + "";
    }

    public ValorDivida retornaValorDividaDoCalculo(CalculoProcFiscalizacao calculo) {
        Query q = em.createQuery("from ValorDivida vd where vd.calculo.id = :calculo");
        q.setParameter("calculo", calculo.getId());
        List<ValorDivida> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return null;
        } else {
            resultado.get(0).getParcelaValorDividas().size();
            return resultado.get(0);
        }
    }

    public ValorDivida getValorDivida(ProcessoFiscalizacao processoFiscalizacao) throws ExcecaoNegocioGenerica, UFMException {
        CalculoProcFiscalizacao calc = recuperaCalculo(processoFiscalizacao);
        if (calc == null) {
            try {
                calc = criarCalculo(processoFiscalizacao);
                geraDebito.geraDebito(calc.getProcesso());
            } catch (Exception e) {
                FacesUtil.addError("Ocorreu um erro", e.getMessage());
            }
        }
        return recuperaValorDivida(calc);
    }


    public CalculoProcFiscalizacao recuperaCalculo(ProcessoFiscalizacao processoFiscalizacao) {
        Query q = em.createQuery("select calculo from CalculoProcFiscalizacao calculo where calculo.processoFiscalizacao = :processo "
            + " and calculo.id = (select max(c.id) from CalculoProcFiscalizacao c where c.processoFiscalizacao = :processo)");
        q.setParameter("processo", processoFiscalizacao);
        if (!q.getResultList().isEmpty()) {
            return (CalculoProcFiscalizacao) q.getResultList().get(0);
        }
        return null;
    }

    private ValorDivida recuperaValorDivida(CalculoProcFiscalizacao calc) {
        Query consulta = em.createQuery("select vd from ValorDivida vd where vd.calculo = :calculo");
        consulta.setParameter("calculo", calc);
        List<ValorDivida> resultado = consulta.getResultList();
        if (!resultado.isEmpty()) {
            resultado.get(0).getParcelaValorDividas().size();
            return resultado.get(0);
        }
        return null;
    }

    private CalculoProcFiscalizacao criarCalculo(ProcessoFiscalizacao processoFiscalizacao) throws ExcecaoNegocioGenerica, UFMException {
        Divida divida = recuperaDividaDoProcessso(processoFiscalizacao);
        if (divida == null) {
            throw new ExcecaoNegocioGenerica("Atenção! Confira as dívidas da secretaria " + processoFiscalizacao.getSecretariaFiscalizacao().getUnidadeOrganizacional().getDescricao() + ", pois não foram encontradas!");
        }

        CalculoProcFiscalizacao calculo = new CalculoProcFiscalizacao();
        calculo.setCadastro(processoFiscalizacao.getCadastro());
        calculo.setDataCalculo(processoFiscalizacao.getDataCadastro());
        calculo.setIsento(Boolean.FALSE);
        calculo.setProcessoFiscalizacao(processoFiscalizacao);
        if (processoFiscalizacao.getPessoa() != null) {
            calculo.getPessoas().add(criarCalculoPessoa(processoFiscalizacao.getPessoa(), calculo));
        } else {
            calculo.getPessoas().add(criarCalculoPessoa(recuperaPessoaDoCadastro(processoFiscalizacao.getCadastro()), calculo));
        }

        //Cria Processo de Calculo
        ProcessoCalculoProcFiscal processo = new ProcessoCalculoProcFiscal();
        processo.setCompleto(Boolean.TRUE);
        processo.setDataLancamento(processoFiscalizacao.getDataCadastro());
        processo.setDivida(divida);
        processo.setExercicio(sistemaFacade.getExercicioCorrente());
        calculo.setProcesso(processo);

        //Cria Item Processo De Calculo
        BigDecimal valorTotalCalculo = BigDecimal.ZERO;
        BigDecimal valorTotalItem = BigDecimal.ZERO;
        calculo.setItemCalcProcFiscalizacoes(new ArrayList());
        for (ItemProcessoFiscalizacao item : processoFiscalizacao.getItensProcessoFiscalizacao()) {
            if (item.getPenalidadeFiscalizacaoSecretaria().getTipoCobranca().equals(PenalidadeFiscalizacaoSecretaria.TipoCobranca.FIXO_UFM)) {
                BigDecimal valorUDMParaData = moedaFacade.recuperaValorUFMParaData(processoFiscalizacao.getDataCadastro());
                valorTotalItem = item.valorTotalUFMConvertido(valorUDMParaData);
                calculo.getItemCalcProcFiscalizacoes().add(criaItemCalcProcFiscalizaca(calculo, item, valorTotalItem));
                valorTotalCalculo = valorTotalCalculo.add(valorTotalItem);
            } else if (item.getPenalidadeFiscalizacaoSecretaria().getTipoCobranca().equals(PenalidadeFiscalizacaoSecretaria.TipoCobranca.FIXO_REAL)) {
                valorTotalItem = item.getValor().multiply(item.getQuantidade()).setScale(2, RoundingMode.HALF_EVEN);
                calculo.getItemCalcProcFiscalizacoes().add(criaItemCalcProcFiscalizaca(calculo, item, valorTotalItem));
                valorTotalCalculo = valorTotalCalculo.add(valorTotalItem);
            }
        }

        calculo.setValorReal(valorTotalCalculo);
        calculo.setValorEfetivo(valorTotalCalculo);

        //Termina Criacao do Calculo
        calculo.setTipoCalculo(Calculo.TipoCalculo.PROC_FISCALIZACAO);
        processo.getCalculos().add(calculo);
        processo.setDivida(divida);
        processo = em.merge(processo);
        return processo.getCalculos().get(0);
    }

    private ItemCalcProcFiscalizacao criaItemCalcProcFiscalizaca(CalculoProcFiscalizacao calculo, ItemProcessoFiscalizacao itemProcessoFiscalizacao, BigDecimal valor) {
        ItemCalcProcFiscalizacao item = new ItemCalcProcFiscalizacao();
        item.setCalculo(calculo);
        item.setItemProcessoFiscalizacao(itemProcessoFiscalizacao);
        item.setValorEfetivo(valor);
        item.setValorReal(valor);
        return item;
    }

    public Pessoa recuperaPessoaDoCadastro(Cadastro cadastro) {
        List<Pessoa> pessoas = pessoaFacade.recuperaPessoasDoCadastro(cadastro);
        try {
            return pessoas.get(0);
        } catch (Exception e) {

        }
        return null;
    }

    private CalculoPessoa criarCalculoPessoa(Pessoa pessoa, CalculoProcFiscalizacao calculo) {
        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setCalculo(calculo);
        calculoPessoa.setPessoa(pessoa);
        return calculoPessoa;

    }

    public SituacaoProcessoFiscal salvaSituacao(SituacaoProcessoFiscal situacao) {
        return em.merge(situacao);
    }

    public String nomeProprietario(CadastroImobiliario cadastro) {
        return cadastroImobiliarioFacade.recuperarProprietariosVigentes(cadastro).get(0).getPessoa().getNome();
    }

    public String cpfProprietario(CadastroImobiliario cadastro) {
        return cadastroImobiliarioFacade.recuperarProprietariosVigentes(cadastro).get(0).getPessoa().getCpf_Cnpj();
    }

    private Divida recuperaDividaDoProcessso(ProcessoFiscalizacao processoFiscalizacao) {
        if (processoFiscalizacao.getTipoCadastroFiscalizacao().equals(TipoCadastroTributario.ECONOMICO)) {
            return processoFiscalizacao.getSecretariaFiscalizacao().getDividaEconomico();
        }
        if (processoFiscalizacao.getTipoCadastroFiscalizacao().equals(TipoCadastroTributario.IMOBILIARIO)) {
            return processoFiscalizacao.getSecretariaFiscalizacao().getDividaImobiliario();
        }
        if (processoFiscalizacao.getTipoCadastroFiscalizacao().equals(TipoCadastroTributario.PESSOA)) {
            return processoFiscalizacao.getSecretariaFiscalizacao().getDividaPessoa();
        }
        if (processoFiscalizacao.getTipoCadastroFiscalizacao().equals(TipoCadastroTributario.RURAL)) {
            return processoFiscalizacao.getSecretariaFiscalizacao().getDividaRural();
        }
        return null;
    }

    public boolean existeNumeroRepetidoDoTermo(TermoAdvertencia termo) {
        Query consulta = em.createQuery("select t from TermoAdvertencia t where t.numero = :numero");
        consulta.setParameter("numero", termo.getNumero());
        try {
            TermoAdvertencia termoRecuperado = (TermoAdvertencia) consulta.getResultList().get(0);

            if (termoRecuperado.getNumero().equals(termo.getNumero())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public List<CadastroEconomico> pesquisaCadastroDeSocio(Pessoa pessoa) {
        String hql = "select c from CadastroEconomico c "
            + " inner join c.sociedadeCadastroEconomico sociedade"
            + " inner join sociedade.socio socio"
            + " where socio in ( select pf from PessoaFisica pf where pf = :pessoa)"
            + " or socio in ( select pj from PessoaJuridica pj where pj = :pessoa)";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        return q.getResultList();
    }

    public List<ProcessoFiscalizacao> recuperaReincidencias(ProcessoFiscalizacao selecionado) {
        String hql = " from ProcessoFiscalizacao proc "
            + " where (proc.cadastro = :cadastro or proc.pessoa = :pessoa) "
            + " and proc.dataCadastro < ((proc.secretariaFiscalizacao.prazoReincidenciaGenerica*365)+proc.dataCadastro)";
        if (selecionado.getId() != null) {
            hql += "   and proc <> :processo ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("cadastro", selecionado.getCadastro());
        q.setParameter("pessoa", selecionado.getPessoa());
        if (selecionado.getId() != null) {
            q.setParameter("processo", selecionado);
        }

        List<ProcessoFiscalizacao> retorno = q.getResultList();


        return retorno;
    }

    public void suspenderDebito(ProcessoFiscalizacao processo, Calculo calculo) {
        List<ParcelaValorDivida> parcelas = new ArrayList<>();
        if (calculo != null) {
            parcelas = this.recuperaParcelaValorDivida(calculo);
        }
        for (ParcelaValorDivida parcelaValorDivida : parcelas) {
            SituacaoParcelaValorDivida spvd = new SituacaoParcelaValorDivida();
            spvd.setDataLancamento(new Date());
            spvd.setSituacaoParcela(SituacaoParcela.SUSPENSAO);
            spvd.setSaldo(parcelaValorDivida.getUltimaSituacao().getSaldo());
            spvd.setParcela(parcelaValorDivida);
            parcelaValorDivida.getSituacoes().add(spvd);
            this.em.merge(parcelaValorDivida);
        }
    }

    private List<ParcelaValorDivida> recuperaParcelaValorDivida(Calculo calculo) {
        String hql = "select pvd from ParcelaValorDivida pvd join fetch pvd.situacoes where pvd.valorDivida.calculo = :calc";
        Query q = this.em.createQuery(hql);
        q.setParameter("calc", calculo);
        return q.getResultList();
    }

    public List<Lote> completaLotePorQuadra(String parte, Quadra quadra) {
        return loteFacade.listaPorQuadra(parte.trim(), quadra);
    }

    public ProcessoFiscalizacao salva(ProcessoFiscalizacao processo) {
        return em.merge(processo);
    }

    public void cancelaDebito(ProcessoFiscalizacao processoFiscalizacao) {
        CalculoProcFiscalizacao calculoProcFiscalizacao = recuperaCalculo(processoFiscalizacao);
        if (calculoProcFiscalizacao != null) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculoProcFiscalizacao.getId());
            List<ResultadoParcela> resultadoParcelas = consultaParcela.executaConsulta().getResultados();
            UsuarioSistema usuarioSistema = sistemaFacade.getUsuarioCorrente();
            for (ResultadoParcela resultadoParcela : resultadoParcelas) {
                ParcelaValorDivida parcela = (ParcelaValorDivida) recuperar(ParcelaValorDivida.class, resultadoParcela.getIdParcela());
                damFacade.cancelarDamsDaParcela(parcela, usuarioSistema);
                SituacaoParcelaValorDivida situacaoParcelaValorDivida = new SituacaoParcelaValorDivida();
                situacaoParcelaValorDivida.setDataLancamento(new Date());
                situacaoParcelaValorDivida.setParcela(parcela);
                situacaoParcelaValorDivida.setSituacaoParcela(SituacaoParcela.CANCELAMENTO);
                parcela.getSituacoes().add(situacaoParcelaValorDivida);
                em.merge(parcela);
            }
        }
    }

    public List<UsuarioSistema> completarFiscalDesignado(String filtro) {
        String sql = " select distinct us.* " +
            "   from AutoInfracaoFiscalizacao ai " +
            "  inner join usuariosistema us on us.id = ai.usuariosistema_id " +
            "  left join pessoafisica pf on us.pessoafisica_id = pf.id " +
            "where trim(lower(us.login)) like :parte or " +
            "      trim(lower(pf.cpf)) like :parte or " +
            "      trim(lower(pf.nome)) like :parte ";
        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("parte", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    @Override
    public void depoisDePagar(Calculo calculo) {
        ProcessoFiscalizacao pf = (em.find(CalculoProcFiscalizacao.class, calculo.getId())).getProcessoFiscalizacao();
        SituacaoProcessoFiscal spf = new SituacaoProcessoFiscal();
        spf.setData(pf.getDataCadastro());
        spf.setStatusProcessoFiscalizacao(StatusProcessoFiscalizacao.PAGO);
        spf.setProcessoFiscalizacao(pf);
        pf.getSituacoesProcessoFiscalizacao().add(spf);
        em.merge(pf);
    }

}
