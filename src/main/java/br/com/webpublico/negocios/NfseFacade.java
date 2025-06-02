/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoNfseDao;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.nfse.enums.TipoDeclaracaoMensalServico;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.facades.ConfiguracaoNfseFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.ws.model.WSDadosArrecadacaoEntrada;
import br.com.webpublico.ws.model.WSDadosIssSemMovimentoEntrada;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

@Stateless
public class NfseFacade extends AbstractFacade<CalculoNfse> {

    private static final int SSL_PORT = 443;

    @EJB
    public DAMFacade damFacade;
    @EJB
    protected ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    protected ConfiguracaoNfseFacade configuracaoNfseFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    private JdbcCalculoNfseDao nfseDAO;
    private JdbcDividaAtivaDAO dividaAtivaDAO;
    @EJB
    private CalculaISSFacade issFacade;

    public NfseFacade() {
        super(CalculoNfse.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        nfseDAO = (JdbcCalculoNfseDao) ap.getBean("calculoNfseDao");
        dividaAtivaDAO = (JdbcDividaAtivaDAO) ap.getBean("dividaAtivaDAO");
    }

    @Override
    public CalculoNfse recuperar(Object id) {
        CalculoNfse recuperar = super.recuperar(id);
        recuperar.getLogs().size();
        return recuperar;
    }

    private void criarLogInsercaoComSucesso(CalculoNfse calculoNfse) {
        LogNFSE log = new LogNFSE();
        log.setConteudoEnviado(toXml(calculoNfse.toDadosArrecadacaoEntrada()));
        log.setDataEnvio(new Date());
        log.setCalculoNfse(calculoNfse);
        log.setTipo(LogNFSE.Tipo.ENTRADA);
        log.setConteudoRetorno("Guia INSERIDA no Webpublico com sucesso!");
        salvaLog(log);
    }

    private boolean hasCalculoJaInserido(CalculoNfse existente) {
        List<ResultadoParcela> resultados = new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, existente.getId())
            .executaConsulta().getResultados();

        if (!resultados.isEmpty()) {
            List<DAM> dams = damFacade.recuperaDAMsPeloCalculo(existente, null);
            if (!dams.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void criarLogAlteracaoComSucesso(CalculoNfse calculoNfse) {
        LogNFSE log = new LogNFSE();
        log.setConteudoEnviado(toXml(calculoNfse.toDadosArrecadacaoEntrada()));
        log.setDataEnvio(new Date());
        log.setCalculoNfse(calculoNfse);
        log.setTipo(LogNFSE.Tipo.ENTRADA);
        log.setConteudoRetorno("Guia ALTERADA no Webpublico com sucesso!");
        salvaLog(log);
    }

    private void realizarAlteracao(WSDadosArrecadacaoEntrada dadosEntrada, CalculoNfse existente) {
        existente.recuperarDados(dadosEntrada);
        existente = em.merge(existente);
        ValorDivida valorDivida = damFacade.buscarUltimoValorDividaDoCalculo(existente.getId());
        gerarDAM(existente, valorDivida);
    }

    private void criarItemCalculo(WSDadosArrecadacaoEntrada dadosEntrada, CalculoNfse calculoNfse) {
        ItemCalculoNfse itemCalculoNfse = new ItemCalculoNfse();
        itemCalculoNfse.setCalculoNfse(calculoNfse);
        itemCalculoNfse.setTributo(recuperarTributo(dadosEntrada.getGRCTRIBUTO()));
        calculoNfse.getItensCalculo().add(itemCalculoNfse);
    }

    private void criarItemCalculo(WSDadosIssSemMovimentoEntrada dadosEntrada, CalculoNfse calculoNfse) {
        ItemCalculoNfse itemCalculoNfse = new ItemCalculoNfse();
        itemCalculoNfse.setCalculoNfse(calculoNfse);
        itemCalculoNfse.setTributo(recuperarTributo(dadosEntrada.getDMSTRIB()));
        calculoNfse.getItensCalculo().add(itemCalculoNfse);
    }

    private CalculoNfse criarCalculo(WSDadosIssSemMovimentoEntrada dadosEntrada, ProcessoCalculoNfse processoCalculoNfse) {
        CalculoNfse calculoNfse = new CalculoNfse();
        calculoNfse.setProcessoCalculo(processoCalculoNfse);
        calculoNfse.recuperarDados(dadosEntrada);
        calculoNfse.setDataCalculo(dadosEntrada.getDMSDTMOV());
        calculoNfse.setSimulacao(false);
        if (dadosEntrada.getDMSCTCID() != null) {
            calculoNfse.setCadastro(consultaDebitoFacade.getCadastroEconomicoFacade().recuperaPorInscricao(dadosEntrada.getDMSCTCID().toString()));
            calculoNfse.setSubDivida(buscarNumeroDeNotasJaInseridasNoMes(calculoNfse.getCadastro(), calculoNfse.getMesDeReferencia(), calculoNfse.getAnoDeReferencia()));
        }
        criarCalculoPessoa(calculoNfse);
        calculoNfse.setTipoCalculo(Calculo.TipoCalculo.NFSE);
        criarItemCalculo(dadosEntrada, calculoNfse);
        processoCalculoNfse.getCalculosNfse().add(calculoNfse);
        return calculoNfse;
    }

    private CalculoNfse criarCalculo(WSDadosArrecadacaoEntrada dadosEntrada, ProcessoCalculoNfse processoCalculoNfse) {
        CalculoNfse calculoNfse = new CalculoNfse();
        calculoNfse.setProcessoCalculo(processoCalculoNfse);
        calculoNfse.recuperarDados(dadosEntrada);
        calculoNfse.setDataCalculo(dadosEntrada.getGRCDTMOVIMENTO());
        calculoNfse.setSimulacao(false);
        if (dadosEntrada.getGRCCTCID() != null) {
            calculoNfse.setCadastro(consultaDebitoFacade.getCadastroEconomicoFacade().recuperaPorInscricao(dadosEntrada.getGRCCTCID().toString()));
            calculoNfse.setSubDivida(buscarNumeroDeNotasJaInseridasNoMes(calculoNfse.getCadastro(), calculoNfse.getMesDeReferencia(), calculoNfse.getAnoDeReferencia()));
        }
        criarCalculoPessoa(calculoNfse);
        calculoNfse.setTipoCalculo(Calculo.TipoCalculo.NFSE);
        criarItemCalculo(dadosEntrada, calculoNfse);
        processoCalculoNfse.getCalculosNfse().add(calculoNfse);
        return calculoNfse;
    }

    private void criarCalculoPessoa(CalculoNfse calculoNfse) {
        if (calculoNfse.getCadastro() != null) {
            CalculoPessoa cp = new CalculoPessoa();
            cp.setPessoa(calculoNfse.getCadastro().getPessoa());
            cp.setCalculo(calculoNfse);
            calculoNfse.getPessoas().add(cp);
        }
    }

    private ProcessoCalculoNfse criarProcessoCalculo(WSDadosArrecadacaoEntrada dadosEntrada) {
        ProcessoCalculoNfse processoCalculoNfse = new ProcessoCalculoNfse();
        if (dadosEntrada.getGRCTRIBUTO() == null || dadosEntrada.getGRCTRIBUTO().equals(1)) {
            processoCalculoNfse.setDivida(configuracaoNfseFacade.buscarDividaNfse(TipoMovimentoMensal.NORMAL, TipoDeclaracaoMensalServico.PRINCIPAL));
        } else if (dadosEntrada.getGRCTRIBUTO() != null && dadosEntrada.getGRCTRIBUTO().equals(2)) {
            processoCalculoNfse.setDivida(configuracaoNfseFacade.buscarDividaNfse(TipoMovimentoMensal.SUBSTITUICAO, TipoDeclaracaoMensalServico.PRINCIPAL));
        } else if (dadosEntrada.getGRCTRIBUTO() != null && dadosEntrada.getGRCTRIBUTO().equals(3)) {
            processoCalculoNfse.setDivida(configuracaoNfseFacade.buscarDividaNfse(TipoMovimentoMensal.RETENCAO, TipoDeclaracaoMensalServico.PRINCIPAL));
        }
        processoCalculoNfse.setExercicio(consultaDebitoFacade.getExercicioFacade().getExercicioPorAno(dadosEntrada.getGRCANOREF()));
        processoCalculoNfse.setDataLancamento(dadosEntrada.getGRCDTMOVIMENTO());
        criarCalculo(dadosEntrada, processoCalculoNfse);
        nfseDAO.salvar(processoCalculoNfse);
        return processoCalculoNfse;
    }

    private ProcessoCalculoNfse criarProcessoCalculo(WSDadosIssSemMovimentoEntrada dadosEntrada) {
        ProcessoCalculoNfse processoCalculoNfse = new ProcessoCalculoNfse();
        processoCalculoNfse.setDivida(configuracaoNfseFacade.buscarDividaNfse(TipoMovimentoMensal.NORMAL, TipoDeclaracaoMensalServico.PRINCIPAL));
        processoCalculoNfse.setExercicio(consultaDebitoFacade.getExercicioFacade().getExercicioPorAno(dadosEntrada.getDMSANOREF()));
        processoCalculoNfse.setDataLancamento(dadosEntrada.getDMSDTMOV());
        criarCalculo(dadosEntrada, processoCalculoNfse);
        nfseDAO.salvar(processoCalculoNfse);
        return processoCalculoNfse;
    }

    private Long buscarNumeroDeNotasJaInseridasNoMes(CadastroEconomico cadastro, Integer mesDeReferencia, Integer anoDeReferencia) {
        String sql = "SELECT count(nfse.id)+1 FROM calculonfse nfse " +
            " INNER JOIN calculo ON calculo.id = nfse.id " +
            " WHERE nfse.anodereferencia = :ano AND nfse.mesdereferencia = :mes " +
            " AND calculo.cadastro_id = :cadastro";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", anoDeReferencia);
        q.setParameter("mes", mesDeReferencia);
        q.setParameter("cadastro", cadastro.getId());
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getResultList().get(0)).longValue();
        }
        return 1L;
    }

    private Tributo recuperarTributo(Integer codigoTributo) {
        switch (codigoTributo) {
            case 1:
                return configuracaoNfseFacade.buscarTributoNfse(TipoMovimentoMensal.NORMAL, TipoDeclaracaoMensalServico.PRINCIPAL);
            case 2:
                return configuracaoNfseFacade.buscarTributoNfse(TipoMovimentoMensal.NORMAL, TipoDeclaracaoMensalServico.COMPLEMENTAR);
            case 3:
                return configuracaoNfseFacade.buscarTributoNfse(TipoMovimentoMensal.RETENCAO, TipoDeclaracaoMensalServico.PRINCIPAL);
            case 8:
                return configuracaoNfseFacade.buscarTributoNfse(TipoMovimentoMensal.NORMAL, TipoDeclaracaoMensalServico.PRINCIPAL);
            default:
                throw new ExcecaoNegocioGenerica("Nenhum tributo encontrado nas configurações para o código " + codigoTributo + ".");
        }
    }

    public CalculoNfse recuperarPorIdentificacao(Integer grcctcid, Integer ano) {
        String sql = "select c from CalculoNfse c " +
            " where c.identificacaoDaGuia = :identificacao " +
            " and c.anoDeReferencia = :ano";
        Query q = em.createQuery(sql)
            .setParameter("identificacao", grcctcid)
            .setParameter("ano", ano);
        if (!q.getResultList().isEmpty()) {
            CalculoNfse calculoNfse = (CalculoNfse) q.getResultList().get(0);
            return calculoNfse;
        }
        return null;
    }

    public void gerarDebito(CalculoNfse calculo, boolean semMovimento) {
        ValorDivida valorDivida = issFacade.criarValorDivida(calculo, calculo.getDataVencimentoDebito(), calculo.getItensCalculo().get(0).getTributo());
        dividaAtivaDAO.persisteValorDivida(valorDivida);
        if (!semMovimento) {
            gerarDAM(calculo, valorDivida);
        }
    }

    public DAM gerarDAM(CalculoNfse calculo, ValorDivida valorDivida) {

        ParcelaValorDivida parcela = valorDivida.getParcelaValorDividas().get(0);
        List<ResultadoParcela> resultados = new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcela.getId())
                .executaConsulta().getResultados();
        return damFacade.gerarDAMUnicoViaApi(null, resultados.get(0));
    }

    public String toXml(Object entrada) {
        try {
            JAXBContext jc = JAXBContext.newInstance(entrada.getClass());
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(entrada, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public void salvaLog(LogNFSE log) {
        em.persist(log);
    }
}
