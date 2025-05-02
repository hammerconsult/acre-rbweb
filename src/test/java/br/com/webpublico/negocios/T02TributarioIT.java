/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author terminal3
 */
public class T02TributarioIT {

    private static final Logger logger = LoggerFactory.getLogger(T02TributarioIT.class);

    private static Context ctx;
    private static EJBContainer ejbC;
    private static EntidadeFacade entidadeFacade;
    private static Entidade entidade;
    private static UF parana;
    private static Cidade cidade;
    private static Nacionalidade nacionalidade;
    private static EscritorioContabil escritorioContabil;
    private static BairroFacade bairroFacade;
    private static Bairro bairro;
    private static Endereco endereco;
    private static TipoLogradouroFacade tipoLogradouroFacade;
    private static TipoLogradouro tipoLogradouro;
    private static LogradouroFacade logradouroFacade;
    private static Logradouro logradouro;
    private static CNAEFacade cnaeFacade;
    private static CNAE cnae;
    private static Telefone telefone;
    private static CBO cbo;
    private static Pessoa pessoa;
    private static BancoFacade bancoFacade;
    private static Banco banco;
    private static AgenciaFacade agenciaFacade;
    private static Agencia agencia;
    private static AssuntoFacade assuntoFacade;
    private static Assunto assunto;
    private static AtividadeEconomicaFacade atividadeEconomicaFacade;
    private static AtividadeEconomica atividadeEconomica;
    private static ValorPossivelFacade valorPossivelFacade;
    private static AtributoFacade atributoFacade;
    private static ExercicioFacade exercicioFacade;
    private static Exercicio exercicio;
    private static CargoFacade cargoFacade;
    private static Cargo cargo;
    private static CartorioFacade cartorioFacade;
    private static Cartorio cartorio;
    private static CategoriaServicoFacade categoriaServicoFacade;
    private static CategoriaServico categoriaServico;
    private static ContaCorrenteBancariaFacade contaBancariaFacade;
    private static ContaCorrenteBancaria contaBancaria;
    private static IndiceEconomicoFacade indiceEconomicoFacade;
    private static IndiceEconomico indiceEconomico;
    private static MoedaFacade moedaFacade;
    private static Moeda moeda;
    private static CotacaoMoedaFacade cotacaoMoedaFacade;
    private static CotacaoMoeda cotacaoMoeda;
    private static DocumentoFacade documentoFacade;
    private static Documento documento;
    private static EsferaGovernoFacade esferaGovernoFacade;
    private static EsferaGoverno esferaGoverno;
    private static EnderecoFacade enderecoFacade;
    private static FaceFacade faceFacade;
    private static Face face;
    private static FeriadoFacade feriadoFacade;
    private static Feriado feriado;
    private static AtoLegalFacade atoLegalFacade;
    private static AtoLegal atoLegal;
    private static LoteamentoFacade loteamentoFacade;
    private static Loteamento loteamento;
    private static ConfiguracaoContabilFacade configuracaoContabilFacade;
    private static ConfiguracaoContabil configuracaoContabil;
    private static HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    private static HierarquiaOrganizacional hirarquiaOrganizacional;
    private static UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private static UnidadeOrganizacional unidadeOrganizacional;
    //private static TipoProcessoFacade tipoProcessoFacade;
    //private static TipoProcesso tipoProcesso;
    private static SubAssuntoFacade subAssuntoFacade;
    private static SubAssunto subAssunto;
    private static ProcessoFacade processoFacade;
    private static Processo processo;
    private static QuadraFacade quadraFacade;
    private static Quadra quadra;
    private static ServicoFacade servicoFacade;
    private static Servico servico;
    private static SetorFacade setorFacade;
    //    private static Setor setor;
    private static TermoFacade termoFacade;
    private static Termo termo;
    private static CidadeFacade cidadeFacade;

    public T02TributarioIT() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        try {
            SistemaFacade.logar = false;
            Map properties = new HashMap();
            properties.put(EJBContainer.MODULES, new File("D:\\webpublico\\projetos\\webpublico\\build\\web\\WEB-INF\\classes"));
            ejbC = EJBContainer.createEJBContainer(properties);
            ctx = ejbC.getContext();
            bairroFacade = (BairroFacade) ctx.lookup("java:global/classes/BairroFacade");
            configuracaoContabilFacade = (ConfiguracaoContabilFacade) ctx.lookup("java:global/classes/ConfiguracaoContabilFacade");
            tipoLogradouroFacade = (TipoLogradouroFacade) ctx.lookup("java:global/classes/TipoLogradouroFacade");
            logradouroFacade = (LogradouroFacade) ctx.lookup("java:global/classes/LogradouroFacade");
            entidadeFacade = (EntidadeFacade) ctx.lookup("java:global/classes/EntidadeFacade");
            cnaeFacade = (CNAEFacade) ctx.lookup("java:global/classes/CNAEFacade");
            bancoFacade = (BancoFacade) ctx.lookup("java:global/classes/BancoFacade");
            agenciaFacade = (AgenciaFacade) ctx.lookup("java:global/classes/AgenciaFacade");
            assuntoFacade = (AssuntoFacade) ctx.lookup("java:global/classes/AssuntoFacade");
            atividadeEconomicaFacade = (AtividadeEconomicaFacade) ctx.lookup("java:global/classes/AtividadeEconomicaFacade");
            cargoFacade = (CargoFacade) ctx.lookup("java:global/classes/CargoFacade");
            cartorioFacade = (CartorioFacade) ctx.lookup("java:global/classes/CartorioFacade");
            valorPossivelFacade = (ValorPossivelFacade) ctx.lookup("java:global/classes/ValorPossivelFacade");
            atributoFacade = (AtributoFacade) ctx.lookup("java:global/classes/AtributoFacade");
            exercicioFacade = (ExercicioFacade) ctx.lookup("java:global/classes/ExercicioFacade");
            categoriaServicoFacade = (CategoriaServicoFacade) ctx.lookup("java:global/classes/CategoriaServicoFacade");
            contaBancariaFacade = (ContaCorrenteBancariaFacade) ctx.lookup("java:global/classes/ContaCorrenteBancariaFacade");
            indiceEconomicoFacade = (IndiceEconomicoFacade) ctx.lookup("java:global/classes/IndiceEconomicoFacade");
            moedaFacade = (MoedaFacade) ctx.lookup("java:global/classes/MoedaFacade");
            cotacaoMoedaFacade = (CotacaoMoedaFacade) ctx.lookup("java:global/classes/CotacaoMoedaFacade");
            documentoFacade = (DocumentoFacade) ctx.lookup("java:global/classes/DocumentoFacade");
            esferaGovernoFacade = (EsferaGovernoFacade) ctx.lookup("java:global/classes/EsferaGovernoFacade");
            faceFacade = (FaceFacade) ctx.lookup("java:global/classes/FaceFacade");
            feriadoFacade = (FeriadoFacade) ctx.lookup("java:global/classes/FeriadoFacade");
            atoLegalFacade = (AtoLegalFacade) ctx.lookup("java:global/classes/AtoLegalFacade");
            loteamentoFacade = (LoteamentoFacade) ctx.lookup("java:global/classes/LoteamentoFacade");
            //tipoProcessoFacade = (TipoProcessoFacade) ctx.lookup("java:global/classes/TipoProcessoFacade");
            subAssuntoFacade = (SubAssuntoFacade) ctx.lookup("java:global/classes/SubAssuntoFacade");
            processoFacade = (ProcessoFacade) ctx.lookup("java:global/classes/ProcessoFacade");
            quadraFacade = (QuadraFacade) ctx.lookup("java:global/classes/QuadraFacade");
            servicoFacade = (ServicoFacade) ctx.lookup("java:global/classes/ServicoFacade");
            setorFacade = (SetorFacade) ctx.lookup("java:global/classes/SetorFacade");
            termoFacade = (TermoFacade) ctx.lookup("java:global/classes/TermoFacade");
            enderecoFacade = (EnderecoFacade) ctx.lookup("java:global/classes/EnderecoFacade");
            unidadeOrganizacionalFacade = (UnidadeOrganizacionalFacade) ctx.lookup("java:global/classes/UnidadeOrganizacionalFacade");
            hierarquiaOrganizacionalFacade = (HierarquiaOrganizacionalFacadeOLD) ctx.lookup("java:global/classes/HierarquiaOrganizacionalFacade");

            cidadeFacade = (CidadeFacade) ctx.lookup("java:global/classes/CidadeFacade");
            cidade = cidadeFacade.lista().get(1);
        } catch (NamingException ex) {
            logger.error("Erro:", ex);
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        ctx.close();
        ejbC.close();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCriarBairro() throws Exception {
        //System.out.println("Criando Bairros");
        long total = bairroFacade.count();
        Bairro bairros[] = {
            new Bairro("Zona 3"),
            new Bairro("Zona 4"),
            new Bairro("Zona 5"),
            new Bairro("Zona 6"),
            new Bairro("Zona 7"),
            new Bairro("Zona 8")
        };

        for (Bairro bai : bairros) {
            bairroFacade.criaAssociacoesAleatorias(bai);
            bairroFacade.salvarNovo(bai);
        }

        bairro = new Bairro("Zona 2");
        bairroFacade.criaAssociacoesAleatorias(bairro);
        bairroFacade.salvarNovo(bairro);

        assert (total + bairros.length + 1 == bairroFacade.count());
    }

    @Test
    public void testCriarTipoLogradouro() throws Exception {
        //System.out.println("Criando Tipos de Logradouro");
        long total = tipoLogradouroFacade.count();
        TipoLogradouro tipoLogradouros[] = {
            new TipoLogradouro("Avenida", "AV"),
            new TipoLogradouro("Aeroporto", "AE"),
            new TipoLogradouro("Alameda", "AL"),
            new TipoLogradouro("Apartamento", "AP"),
            new TipoLogradouro("Beco", "BC"),
            new TipoLogradouro("Bloco", "BL"),
            new TipoLogradouro("Caminho", "CAM"),
            new TipoLogradouro("Escadinha", "ESCD"),
            new TipoLogradouro("Estação", "EST"),
            new TipoLogradouro("Estrada", "ETR"),
            new TipoLogradouro("Fazenda", "FAZ"),
            new TipoLogradouro("Fortaleza", "FORT"),
            new TipoLogradouro("Galeria", "GL"),
            new TipoLogradouro("Ladeira", "LD"),
            new TipoLogradouro("Largo", "LGO"),
            new TipoLogradouro("Praça", "PÇA"),
            new TipoLogradouro("Parque", "PRQ"),
            new TipoLogradouro("Praia", "PR"),
            new TipoLogradouro("Quadra", "QD"),
            new TipoLogradouro("Quilômetro", "KM"),
            new TipoLogradouro("Quinta", "QTA"),
            new TipoLogradouro("Rodovia", "ROD"),
            new TipoLogradouro("Super Quadra", "SQD"),
            new TipoLogradouro("Travessa", "TRV"),
            new TipoLogradouro("Viaduto", "VD"),
            new TipoLogradouro("Vila", "VL")
        };

        for (TipoLogradouro tipoL : tipoLogradouros) {
            tipoLogradouroFacade.salvarNovo(tipoL);
        }

        tipoLogradouro = new TipoLogradouro("Rua", "R");
        tipoLogradouroFacade.salvarNovo(tipoLogradouro);

        assert (total + tipoLogradouros.length + 1 == tipoLogradouroFacade.count());
    }

    @Test
    public void testCriarLogradouro() throws Exception {
        //System.out.println("Criando Logradouros");
//        logradouro = new Logradouro(tipoLogradouro, "Marcelino Champagnat", bairro, new ArrayList<CEP>());
//        logradouro.getCep().add(new CEP("87010430", 0, 2000));
//        logradouro.getCep().add(new CEP("87010435", 2001, 99999));
//        logradouroFacade.salvarNovo(logradouro);
//
//        logradouro = new Logradouro(tipoLogradouro, "Neo Alves Martins", bairro, new ArrayList<CEP>());
//        logradouro.getCep().add(new CEP("87010000", 0, 99999));
//        logradouroFacade.salvarNovo(logradouro);
//
//        logradouro = new Logradouro(tipoLogradouro, "Cerqueira Cesar", bairro, new ArrayList<CEP>());
//        logradouro.getCep().add(new CEP("85410000", 0, 99999));
//        logradouroFacade.salvarNovo(logradouro);
//
//        logradouro = new Logradouro(tipoLogradouro, "Vaz Caminha", bairro, new ArrayList<CEP>());
//        logradouro.getCep().add(new CEP("85410000", 0, 99999));
//        logradouroFacade.salvarNovo(logradouro);
//
//        logradouro = new Logradouro(tipoLogradouro, "Martin Afonso", bairro, new ArrayList<CEP>());
//        logradouro.getCep().add(new CEP("85410000", 0, 99999));
//        logradouroFacade.salvarNovo(logradouro);

        assert (true);
    }

    @Test
    public void testImportaCnaes() throws Exception {
        //System.out.println("Importando CNAE");
        FileInputStream stream = new FileInputStream("bancoscripts\\CNAE.csv");
        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(streamReader);
        String line = null;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            String partes[] = line.split(";");
            if (partes.length > 1) {
                String codigo = partes[0];
                String nome = partes[1];
                String site = "";
                if (partes.length > 2) {
                    site = partes[2];
                }
                CNAE b = new CNAE(codigo, nome);
                cnaeFacade.salvarNovo(b);

            }
        }
        assert (true);
    }

    @Test
    public void testImportaBancos() throws Exception {
        //System.out.println("Importando Bancos");
        FileInputStream stream = new FileInputStream("bancoscripts\\bancos_digito.csv");
        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(streamReader);
        String line = null;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            String partes[] = line.split(";");
            if (partes.length > 1) {
                String codigo = partes[0];
                String digito = partes[1];
                String nome = partes[2];
                String site = "";
                if (partes.length > 3) {
                    site = partes[3];
                }
                Banco b = new Banco(nome, codigo, digito, site);
                bancoFacade.salvarNovo(b);
                if (b.getNumeroBanco().equals("341")) {
                    banco = b;
                }
            }
        }
        assert (true);
    }

    @Test
    public void testCriarAgencias() throws Exception {
        //System.out.println("Criando Agencias");
        long total = agenciaFacade.count();

        Agencia agencias[] = {
            new Agencia(banco, "1424", "9", "Agência Maringá", new EnderecoCorreio()),
            new Agencia(banco, "1425", "2", "Agência Diamante", new EnderecoCorreio()),
            new Agencia(banco, "1426", "8", "Agência Paranavai II", new EnderecoCorreio()),
            new Agencia(banco, "2891", "5", "Agência Londrina", new EnderecoCorreio()),
            new Agencia(banco, "4532", "3", "Agência Banco do Corrupto", new EnderecoCorreio()),
            new Agencia(banco, "2987", "7", "Agência Mandaguari", new EnderecoCorreio()),
            new Agencia(banco, "5490", "6", "Agência Paranaguá", new EnderecoCorreio())
        };

        for (Agencia log : agencias) {
            agencia = log;
            agenciaFacade.salvarNovo(log);
        }
        assert (total + agencias.length == agenciaFacade.count());
    }

    @Test
    public void testCriaAssunto() throws Exception {
        //System.out.println("Criando Assunto");
        long total = assuntoFacade.count();
        Assunto assuntos[] = {
            new Assunto("Meio Ambiente"),
            new Assunto("Contrução Civil"),
            new Assunto("Segurança")
        };
        for (Assunto as : assuntos) {
            assuntoFacade.salvarNovo(as);
        }
        assunto = new Assunto("Licitação");
        assuntoFacade.salvarNovo(assunto);
        assert (total + assuntos.length + 1 == assuntoFacade.count());
    }

    @Test
    public void testAtividadeEconomica() throws Exception {
        //System.out.println("Criando Atividade Economica");
        long total = atividadeEconomicaFacade.count();
        AtividadeEconomica atividades[] = {
            new AtividadeEconomica("Cultivo de fumo", "   ", new BigDecimal(12.3256), new BigDecimal(2.3211), new BigDecimal(0.3256)),
            new AtividadeEconomica("Cultivo de Algodão", " ", new BigDecimal(12.3256), new BigDecimal(2.3211), new BigDecimal(0.3256)),
            new AtividadeEconomica("Horticultura", " ", new BigDecimal(12.3256), new BigDecimal(2.3211), new BigDecimal(0.3256)),
            new AtividadeEconomica("Cultivo de flores e plantas ornamentais", " ", new BigDecimal(12.3256), new BigDecimal(2.3211), new BigDecimal(0.3256)),
            new AtividadeEconomica("Atividade Agropecuária", " ", new BigDecimal(12.3256), new BigDecimal(2.3211), new BigDecimal(0.3256))
        };

        for (AtividadeEconomica ativ : atividades) {
            atividadeEconomicaFacade.salvarNovo(ativ);
        }

        atividadeEconomica = new AtividadeEconomica("Cultivo de Mandioca", " ", new BigDecimal(8.3256), new BigDecimal(2.3211), new BigDecimal(0.4321));
        atividadeEconomicaFacade.salvarNovo(atividadeEconomica);

        assert (total + atividades.length + 1 == atividadeEconomicaFacade.count());
    }

    @Test
    public void testCriaExercicios() throws Exception {
        //System.out.println("Criando Exercicios");
        for (int ano = 2007; ano < 2011; ano++) {
            exercicio = new Exercicio(ano);
            exercicioFacade.salvarNovo(exercicio);
        }
        exercicio = new Exercicio(2011);
        exercicioFacade.salvarNovo(exercicio);
        assert (true);
    }

    @Test
    public void testAtributo() throws Exception {
        //System.out.println("Criando Atributos");
        Atributo atributo = null;
        atributoFacade.salvarNovo(atributo = new Atributo(0, "Situação", ClasseDoAtributo.LOTE, TipoAtributo.DISCRETO, TipoComponenteVisual.COMBO, true, new Date()));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Uma Frente", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Mais de Uma Frente", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Vila", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Encravado", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Gleba", atributo));

        atributoFacade.salvarNovo(atributo = new Atributo(0, "Perfil", ClasseDoAtributo.LOTE, TipoAtributo.DISCRETO, TipoComponenteVisual.COMBO, true, new Date()));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Plano", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Aclive", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Declive", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Irregular", atributo));

        atributoFacade.salvarNovo(atributo = new Atributo(0, "Solo", ClasseDoAtributo.LOTE, TipoAtributo.DISCRETO, TipoComponenteVisual.COMBO, true, new Date()));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Inundável", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Firme", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Alagado", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Combnação Demais", atributo));

        atributoFacade.salvarNovo(atributo = new Atributo(0, "Ocupação", ClasseDoAtributo.CADASTRO_IMOBILIARIO, TipoAtributo.DISCRETO, TipoComponenteVisual.COMBO, true, new Date()));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Não edificado", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Ruinas", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Em demolição", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Construção paralizada", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Construção em andamento", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Construido", atributo));

        atributoFacade.salvarNovo(atributo = new Atributo(0, "Patrimônio", ClasseDoAtributo.CADASTRO_IMOBILIARIO, TipoAtributo.DISCRETO, TipoComponenteVisual.COMBO, true, new Date()));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Público", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Privado", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Religioso", atributo));

        atributoFacade.salvarNovo(atributo = new Atributo(0, "Limitação", ClasseDoAtributo.CADASTRO_IMOBILIARIO, TipoAtributo.DISCRETO, TipoComponenteVisual.COMBO, true, new Date()));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Sim", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Não", atributo));

        atributoFacade.salvarNovo(atributo = new Atributo(0, "Localização", ClasseDoAtributo.CONSTRUCAO, TipoAtributo.DISCRETO, TipoComponenteVisual.COMBO, true, new Date()));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Frente", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Fundos", atributo));

        atributoFacade.salvarNovo(atributo = new Atributo(0, "Estrutura", ClasseDoAtributo.CONSTRUCAO, TipoAtributo.DISCRETO, TipoComponenteVisual.COMBO, true, new Date()));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Alvenaria", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Madeira", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Metálica", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Concreto", atributo));

        atributoFacade.salvarNovo(atributo = new Atributo(0, "Instalação Elétrica", ClasseDoAtributo.CONSTRUCAO, TipoAtributo.DISCRETO, TipoComponenteVisual.COMBO, true, new Date()));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Inexistente", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Aparente", atributo));
        valorPossivelFacade.salvarNovo(new ValorPossivel("Embutido", atributo));
    }

    @Test
    public void testCriarCargo() throws Exception {
        //System.out.println("Criando Cargos");

        long total = cargoFacade.count();
        Cargo cargos[] = {
            new Cargo("Administrador de Banco de Dados"),
            new Cargo("Gerente de TI"),
            new Cargo("CEO"),
            new Cargo("CIO"),
            new Cargo("Programador"),
            new Cargo("Conselheiro"),
            new Cargo("Gerente de Produção"),
            new Cargo("Serviços Gerais"),
            new Cargo("Secretária"),
            new Cargo("Engenheiro Civil"),
            new Cargo("Arquiteto")
        };
        for (Cargo crg : cargos) {
            cargoFacade.salvarNovo(crg);
        }
        cargo = new Cargo("Analista de Sistemas");
        cargoFacade.salvarNovo(cargo);
        assert (total + cargos.length + 1 == cargoFacade.count());
    }

//    @Test
//    public void testCriarCartorios() throws Exception {
//        //System.out.println("Criando Cartórios");
//        long total = cartorioFacade.count();
//        Cartorio cartorios[] = {
////            new Cartorio("Tomazzoni", new EnderecoCorreio()),
////            new Cartorio("Monteiro", new EnderecoCorreio()),
////            new Cartorio("Registro Geral", new EnderecoCorreio()),
////            new Cartorio("Gebara", new EnderecoCorreio()),
////            new Cartorio("Olegari", new EnderecoCorreio()),
////            new Cartorio("Salvatore", new EnderecoCorreio())
//        };
//
//        for (Cartorio cart : cartorios) {
//            cartorio = cart;
//            cartorioFacade.salvarNovo(cart);
//        }
//        assert (total + cartorios.length == cartorioFacade.count());
//    }

    @Test
    public void testCategoriaServico() throws Exception {
        //System.out.println("Criando Categoria Serviço");
        long total = categoriaServicoFacade.count();
        CategoriaServico categorias[] = {
            new CategoriaServico("1234", "Serviços de Academias", new BigDecimal(12), new BigDecimal(15), new BigDecimal(34)),
            new CategoriaServico("4321", "Serviços de Construção", new BigDecimal(45), new BigDecimal(18), new BigDecimal(11)),
            new CategoriaServico("1567", "Serviços de Limpeza", new BigDecimal(15), new BigDecimal(17), new BigDecimal(9)),
            new CategoriaServico("1579", "Serviços de Galvanoplastia ", new BigDecimal(4), new BigDecimal(7), new BigDecimal(1)),
            new CategoriaServico("9841", "Serviços de Empreiteiros", new BigDecimal(6), new BigDecimal(2), new BigDecimal(8))
        };

        for (CategoriaServico cat : categorias) {
            categoriaServicoFacade.salvarNovo(cat);
        }

        categoriaServico = new CategoriaServico("7777", "Serviços Médicos", new BigDecimal(12), new BigDecimal(15), new BigDecimal(34));
        categoriaServicoFacade.salvarNovo(categoriaServico);

        assert (total + categorias.length + 1 == categoriaServicoFacade.count());
    }

    @Test
    public void testCriarContaCorrente() throws Exception {
        //System.out.println("Criando Conta Corrente");
        long total = contaBancariaFacade.count();

        ContaCorrenteBancaria contas[] = {
            new ContaCorrenteBancaria("1987", "8", agencia, SituacaoConta.ATIVO, null),
            new ContaCorrenteBancaria("5938", "1", agencia, SituacaoConta.BLOQUEADA, (List<Pessoa>) null),
            new ContaCorrenteBancaria("0249", "2", agencia, SituacaoConta.INATIVO, (List<Pessoa>) null),
            new ContaCorrenteBancaria("2373", "7", agencia, SituacaoConta.ATIVO, (List<Pessoa>) null),
            new ContaCorrenteBancaria("6423", "3", agencia, SituacaoConta.ATIVO, (List<Pessoa>) null),
            new ContaCorrenteBancaria("9123", "1", agencia, SituacaoConta.ATIVO, (List<Pessoa>) null),
            new ContaCorrenteBancaria("6583", "9", agencia, SituacaoConta.ATIVO, (List<Pessoa>) null),
            new ContaCorrenteBancaria("1235", "2", agencia, SituacaoConta.ATIVO, (List<Pessoa>) null),
            new ContaCorrenteBancaria("6930", "3", agencia, SituacaoConta.ATIVO, (List<Pessoa>) null),
            new ContaCorrenteBancaria("2137", "7", agencia, SituacaoConta.ATIVO, (List<Pessoa>) null),
            new ContaCorrenteBancaria("5712", "5", agencia, SituacaoConta.ATIVO, (List<Pessoa>) null)};

        for (ContaCorrenteBancaria log : contas) {
            contaBancariaFacade.criaAssociacoesAleatorias(log);
            contaBancariaFacade.salvarNovo(log);
        }

        assert (total + contas.length == contaBancariaFacade.count());
    }

    @Test
    public void testCriarIndicesEconomicos() throws Exception {
        //System.out.println("Criando Indices Economicos");
        long total = indiceEconomicoFacade.count();
        IndiceEconomico ind[] = {
            new IndiceEconomico("IGPM"),
            new IndiceEconomico("UFMRB")
        };
        for (IndiceEconomico i : ind) {
            indiceEconomicoFacade.salvarNovo(i);
            indiceEconomico = i;
        }
        indiceEconomico = new IndiceEconomico("ICMS");
        indiceEconomicoFacade.salvarNovo(indiceEconomico);

        assert (total + ind.length + 1 == indiceEconomicoFacade.count());
    }

    @Test
    public void testCriarMoedas() throws Exception {
        //System.out.println("Criando Moedas");
        long total = moedaFacade.count();
        Moeda moedas[] = {
            new Moeda("Real", indiceEconomico, 2011, 01, new BigDecimal(53.00000)),
            new Moeda("Rúpia", indiceEconomico, 2011, 02, new BigDecimal(54.54631)),
            new Moeda("Guarani", indiceEconomico, 2011, 03, new BigDecimal(55.78322)),
            new Moeda("Euro", indiceEconomico, 2011, 04, new BigDecimal(56.00123)),
            new Moeda("Yen", indiceEconomico, 2011, 05, new BigDecimal(1.08431)),
            new Moeda("Libra", indiceEconomico, 2011, 06, new BigDecimal(1.08782)),
            new Moeda("Francos", indiceEconomico, 2011, 07, new BigDecimal(1.15373)),};

        for (Moeda mda : moedas) {
            moeda = mda;
            moedaFacade.salvarNovo(mda);
        }

        moeda = new Moeda("Dólar", indiceEconomico, 2011, 12, new BigDecimal(12.65235));
        moedaFacade.salvarNovo(moeda);

        assert (total + moedas.length + 1 == moedaFacade.count());
    }

    @Test
    public void testCriarCotacaoMoeda() throws Exception {
        //System.out.println("Criando Cotação de Moeda");
        long total = cotacaoMoedaFacade.count();

        CotacaoMoeda cotaMoeda[] = {
            new CotacaoMoeda(2000, 1, moeda, new BigDecimal(2.00)),
            new CotacaoMoeda(2001, 2, moeda, new BigDecimal(2.01)),
            new CotacaoMoeda(2002, 3, moeda, new BigDecimal(2.02)),
            new CotacaoMoeda(2003, 4, moeda, new BigDecimal(2.30)),
            new CotacaoMoeda(2004, 5, moeda, new BigDecimal(2.99)),
            new CotacaoMoeda(2005, 6, moeda, new BigDecimal(3.75)),
            new CotacaoMoeda(2006, 7, moeda, new BigDecimal(8.23)),
            new CotacaoMoeda(2007, 8, moeda, new BigDecimal(9.72)),
            new CotacaoMoeda(2008, 9, moeda, new BigDecimal(11.55)),
            new CotacaoMoeda(2009, 10, moeda, new BigDecimal(23.84)),
            new CotacaoMoeda(2010, 11, moeda, new BigDecimal(2000.28))
        };

        for (CotacaoMoeda cotacaoMoeda : cotaMoeda) {
            cotacaoMoedaFacade.salvarNovo(cotacaoMoeda);
        }

        assert (total + cotaMoeda.length == cotacaoMoedaFacade.count());
    }

    @Test
    public void testCriarDocumento() throws Exception {
        //System.out.println("Criando Documento");
        long total = documentoFacade.count();
        Documento documentos[] = {
            new Documento("Ata"),
            new Documento("Registro"),
            new Documento("Certidão"),
            new Documento("Alvará"),
            new Documento("Citação"),
            new Documento("Contestação")
        };

        for (Documento doc : documentos) {
            documento = doc;
            documentoFacade.salvarNovo(doc);
        }

        documento = new Documento("Edital");
        documentoFacade.salvarNovo(documento);

        assert (total + documentos.length + 1 == documentoFacade.count());
    }

    @Test
    public void testCriarEsferasDeGoverno() throws Exception {
        //System.out.println("Criando EsferasDeGoverno");
        long total = esferaGovernoFacade.count();
        EsferaGoverno egs[] = {
            new EsferaGoverno("MUNICIPAL"),
            new EsferaGoverno("ESTADUAL"),
            new EsferaGoverno("FEDERAL")
        };
        for (EsferaGoverno eg : egs) {
            esferaGovernoFacade.salvarNovo(eg);
        }

        esferaGoverno = new EsferaGoverno("Outros");
        esferaGovernoFacade.salvarNovo(esferaGoverno);
        assert (total + egs.length + 1 == esferaGovernoFacade.count());
    }

    @Test
    public void testeFace() {
        //System.out.println("Criando Faces");
        long total = faceFacade.count();
        Face faces[] = {
//            new Face(20d, logradouro, null, "12345E", new BigDecimal(12)),
//            new Face(18d, logradouro, null, "34521X", new BigDecimal(15)),
//            new Face(24d, logradouro, null, "12345L", new BigDecimal(18)),
//            new Face(28d, logradouro, null, "88730E", new BigDecimal(25)),
//            new Face(15d, logradouro, null, "23498F", new BigDecimal(60))
        };
        for (Face facess : faces) {
            faceFacade.salvarNovo(facess);
        }
//        face = new Face(30d, logradouro, null, "45316F", new BigDecimal(80));
        faceFacade.salvarNovo(face);
        assert (total + faces.length + 1 == faceFacade.count());
    }

    @Test
    public void testCriarFeriado() throws Exception {
        //System.out.println("Criando Feriado");
        long total = feriadoFacade.count();
        Feriado feriados[] = {
            new Feriado(new Date("01/01/2011"), "Feriado Nacional"),
            new Feriado(new Date("08/03/2011"), "Carnaval"),
            new Feriado(new Date("21/04/2011"), "Tiradentes"),
            new Feriado(new Date("07/09/2011"), "Indepêndencia do Brasil"),};

        for (Feriado fe : feriados) {
            feriado = fe;
            feriadoFacade.salvarNovo(fe);
        }

        assert (total + feriados.length == feriadoFacade.count());
    }

    @Test
    public void testCriarLei() throws Exception {
        //System.out.println("Criando Lei");
        long total = atoLegalFacade.count();
        AtoLegal leis[] = {
            new AtoLegal(esferaGoverno, "1601", new Date("21/01/2010"), new Date("21/01/2010"), exercicio, "Feriado Estadual e Feriado Municipal -  Lei Municipal n.º  1.601/2006", null, PropositoAtoLegal.OUTROS, TipoAtoLegal.LEI_ORDINARIA),
            new AtoLegal(esferaGoverno, "2247", new Date("01/11/2010"), new Date("01/11/2010"), exercicio, "Ponto Facultativo. Comemoração do dia 28/10 adiado para esta data, nos termos da Lei n.º 2.247/2009.", null, PropositoAtoLegal.OUTROS, TipoAtoLegal.LEI_ORDINARIA),
            new AtoLegal(esferaGoverno, "1609", new Date("28/12/2010"), new Date("28/12/2010"), exercicio, "Feriado Municipal - Lei Municipal n.º  1.609/2006", null, PropositoAtoLegal.OUTROS, TipoAtoLegal.LEI_ORDINARIA)
        };

        for (AtoLegal l : leis) {
            atoLegalFacade.salvarNovo(l);
        }
        atoLegal = new AtoLegal(esferaGoverno, "2247", new Date("19/11/2010"), new Date("19/11/2010"), exercicio, "Feriado Estadual. Comemoração do dia 17 adiado para esta data, nos termos da Lei n.º 2.247/2009.", null, PropositoAtoLegal.OUTROS, TipoAtoLegal.LEI_ORDINARIA);
        atoLegalFacade.salvar(atoLegal);
        assert (total + leis.length + 1 == atoLegalFacade.count());
    }

    @Test
    public void testCriarLoteamento() throws Exception {
        //System.out.println("Criando Loteamento");
        long total = loteamentoFacade.count();
        Loteamento loteamentos[] = {
            new Loteamento("Jardim America"),
            new Loteamento("Jardim Alvorada I"),
            new Loteamento("Jardim Alvorada II"),
            new Loteamento("Requião I"),
            new Loteamento("Requião II"),
            new Loteamento("Requião III")
        };

        for (Loteamento log : loteamentos) {
            loteamentoFacade.criaAssociacoesAleatorias(log);
            loteamentoFacade.salvarNovo(log);
        }

        loteamento = new Loteamento("Jardim Oasis");
        loteamentoFacade.criaAssociacoesAleatorias(loteamento);
        loteamentoFacade.salvarNovo(loteamento);
        assert (total + loteamentos.length + 1 == loteamentoFacade.count());
    }

//    @Test
//    public void testCriarTipoProcesso() throws Exception {
//        //System.out.println("Criando Tipos de Processos");
//        long total = tipoProcessoFacade.count();
//        TipoProcesso tprocess[] = {
//            new TipoProcesso("Nome2", "Processo2"),
//            new TipoProcesso("Nome3", "Processo3"),
//            new TipoProcesso("Nome4", "Processo4"),
//            new TipoProcesso("Nome5", "Processo5"),
//            new TipoProcesso("Nome6", "Processo6"),
//            new TipoProcesso("Nome7", "Processo7")
//        };
//        for (TipoProcesso tpc : tprocess) {
//            tipoProcessoFacade.salvarNovo(tpc);
//        }
//        tipoProcesso = new TipoProcesso("Nome1", "Processo1");
//        tipoProcessoFacade.salvarNovo(tipoProcesso);
//        assert (total + tprocess.length + 1 == tipoProcessoFacade.count());
//    }

    @Test
    public void testCriarSubAssunto() throws Exception {
        //System.out.println("Criando Subassunto");
        long total = subAssuntoFacade.count();
        List<Documento> documentosCopulados = new ArrayList<Documento>();
        documentosCopulados.add(documento);
        List<ItemRota> itemRotas = new ArrayList<ItemRota>();
        SubAssunto subAssuntos[] = {
            new SubAssunto("Corta de arvore", "Podar arvores para copel", assunto, itemRotas),
            new SubAssunto("Licitação Materiais de Contrução", "Materia para contruir prédio", assunto, itemRotas),
            new SubAssunto("Licitação Materiais de Manutenção Mecanica", null, assunto, itemRotas),
            new SubAssunto("Licitação Desenvolvimento de Sistema", null, assunto, itemRotas),
            new SubAssunto("Licitação Manutenção de Hardware", null, assunto, itemRotas)
        };
        for (SubAssunto subAssu : subAssuntos) {
            subAssuntoFacade.salvarNovo(subAssu);
        }
        subAssunto = new SubAssunto("Licitação Materiais de Informatica", null, assunto, itemRotas);
        subAssuntoFacade.salvarNovo(subAssunto);
        assert (total + subAssuntos.length + 1 == subAssuntoFacade.count());
    }

    @Test
    public void testCriarQuadra() throws Exception {
        //System.out.println("Criando Quadra");
        long total = quadraFacade.count();
        Quadra quadras[] = {
            new Quadra(loteamento, "Quadra1"),
            new Quadra(loteamento, "Quadra2"),
            new Quadra(loteamento, "Quadra3"),
            new Quadra(loteamento, "Quadra4"),
            new Quadra(loteamento, "Quadra5")
        };

        for (Quadra qua : quadras) {
            quadraFacade.salvarNovo(qua);
        }

        quadra = new Quadra(loteamento, "Quadra6");
        quadraFacade.salvarNovo(quadra);
        assert (total + quadras.length + 1 == quadraFacade.count());
    }

    @Test
    public void testServico() throws Exception {
        //System.out.println("Criando Serviço");
        long total = servicoFacade.count();
        Servico servicos[] = {
            new Servico("Cultivo de fumo", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023)),
            new Servico("Cultivo de Soja", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023)),
            new Servico("Cultivo de Uva", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023)),
            new Servico("Cultivo de Laranja", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023)),
            new Servico("Fabricacão de Cafe", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023)),
            new Servico("Fabricacao de Açucar bruto", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023)),
            new Servico("Fabricacao de Açucar Refinado", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023)),
            new Servico("Tecelagem de fios de Algodão", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023)),
            new Servico("Tecelagem de fios de Ceda", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023)),
            new Servico("Impressão de Material de Segurança", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023)),
            new Servico("Impressão de Materiais para outros usos", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023))
        };
        for (Servico ser : servicos) {
            servicoFacade.salvarNovo(ser);
        }
        servico = new Servico("Categoria 0", categoriaServico, new BigDecimal(1.012), new BigDecimal(1.023));
        servicoFacade.salvarNovo(servico);
        assert (total + servicos.length + 1 == servicoFacade.count());
    }

    @Test
    public void testeSetor() {
        //System.out.println("Criando Setores");
        long total = setorFacade.count();
//        Setor setores[] = {
//            new Setor("Norte", new BigDecimal(12.1), null),
//            new Setor("Sul", new BigDecimal(12.2), null),
//            new Setor("Leste", new BigDecimal(12.3), null),
//            new Setor("Oeste", new BigDecimal(12.4), null),
//            new Setor("Residencial", new BigDecimal(80), null),
//            new Setor("Nobre", new BigDecimal(28), null),
//            new Setor("Industrial", new BigDecimal(55), null),
//            new Setor("Comercial", new BigDecimal(18), null)
//        };
//        for (Setor sets : setores) {
//            setorFacade.criaAssociacoesAleatorias(sets);
//            setorFacade.salvarNovo(sets);
//        }
//        setor = new Setor("Ribeirinho", new BigDecimal(22.5), cidade);
//        setorFacade.criaAssociacoesAleatorias(setor);
//        setorFacade.salvarNovo(setor);
//        assert (total + setores.length + 1 == setorFacade.count());
        assert (true);
    }

    @Test
    public void testeTermo() {
        //System.out.println("Criando Termos");
        long total = termoFacade.count();
        Termo termos[] = {
            new Termo("Termo 1", null)
        };
        for (Termo t : termos) {
            termoFacade.salvarNovo(t);
        }
        termo = new Termo("Confirmar Termo", new Texto("Texto"));
        termoFacade.salvarNovo(termo);
        assert (total + termos.length + 1 == termoFacade.count());
    }

    @Test
    public void testCriarConfiguracaoContabil() throws Exception {
        //System.out.println("Criando ConfiguracaoContabil");
//        configuracaoContabil = new ConfiguracaoContabil(new Date(), "9999.999.999.999"," "," ");
        configuracaoContabilFacade.salvar(configuracaoContabil);
    }
//    @Test
//    public void testCriarEntidade() throws Exception {
//        //System.out.println("Criando Entidade");
//        long total = entidadeFacade.count();
//        entidade = new Entidade(null, "Web Público Entidade 0", "patroni@gamil.com", "www.webpublico.com.br", true, null, (List<Telefone>) telefone, null, "Web Publico Sistemas de gestão publica LTDA", "web publico", "Web POublico", "37.261.438.0001-45", "8569888888888", "Web Público Entidade", null, null, TipoEntidade.OUTROS, 577, 21, cnae, true);
//        entidadeFacade.criaAssociacoesAleatorias(entidade);
//        entidadeFacade.salvarNovo(entidade);
//    }
//    @Test
//    public void testCriarUnidadeOrganizacional() throws Exception {
//        //System.out.println("Criando UnidadeOrganizacional");
//        endereco = enderecoFacade.umEndereco();
//        String mascara = configuracaoContabil.getMascaraUnidadeOrganizacional();
//        //cria a unidade e entidade Principal da arvore
//        Entidade ent = new Entidade(null, "Prefeitura Municipal de Rio Branco", "label", "label", true, null, (List<Telefone>) telefone, null, "Prefeitura municipal de rio branco", "Prefeitura", "prefeitura", "34.261.438.0001-45", "8694555555", "label", null, null, TipoEntidade.OUTROS, 574, 21, cnae, true);
//        UnidadeOrganizacional und = new UnidadeOrganizacional("Prefeitura Municipal de Rio Branco", EsferaDoPoder.EXECUTIVO, endereco, ent);
//        HierarquiaOrganizacional hie = new HierarquiaOrganizacional(mascara.replace('9', '0'), 0,  null, und, new Date(), new Date(),TipoHierarquiaOrganizacional.ADMINISTRATIVA);
//        unidadeOrganizacionalFacade.salvarNovo(und);
//        hierarquiaOrganizacionalFacade.salvar(hie);
//
////        int contInd = 1;
////        UnidadeOrganizacional[] unidadesOrganizacionais = {
////            new UnidadeOrganizacional("Secretaria Municipal de Desenvolvimento", EsferaDoPoder.EXECUTIVO, endereco, null),
////            new UnidadeOrganizacional("Secretaria Municipal de Saude", EsferaDoPoder.EXECUTIVO, endereco, null),
////            new UnidadeOrganizacional("Secretaria Municipal de Educação", EsferaDoPoder.EXECUTIVO, endereco, null),
////            new UnidadeOrganizacional("Secreterai Municipal de Esportes", EsferaDoPoder.EXECUTIVO, endereco, null),
////            new UnidadeOrganizacional("Secretaria Municipal da Mulher", EsferaDoPoder.EXECUTIVO, endereco, null),
////            new UnidadeOrganizacional("Secretaria Municipal de Assitencia Social", EsferaDoPoder.EXECUTIVO, endereco, null),
////            new UnidadeOrganizacional("Secreterai Municipal do Menor e Adolecente", EsferaDoPoder.EXECUTIVO, endereco, null),
////            new UnidadeOrganizacional("Posto de Saúde 1", EsferaDoPoder.EXECUTIVO, endereco, null),
////            new UnidadeOrganizacional("Posto de Saúde 2", EsferaDoPoder.EXECUTIVO, endereco, null),
////            new UnidadeOrganizacional("Posto de Saúde 3", EsferaDoPoder.EXECUTIVO, endereco, null)
////        };
////
////        UnidadeOrganizacional tempUm = null;
////        UnidadeOrganizacional tempDois = null;
////        UnidadeOrganizacional sup=null;
////        for (UnidadeOrganizacional u : unidadesOrganizacionais) {
////            if (contInd == 1) {
////                ent = new Entidade(null, "Centro de Ensino Munif", "munif@gmail.com", "www.munif.com.br", true, null, (List<Telefone>) telefone, null, "Click Sistemas LTDA", "Click Sistemas", "Click", "34.261.438.0001-45", "8694555555", "Centro de Ensino Munif", null, null, TipoEntidade.OUTROS, 574, 21, cnae, true);
////                u.setEntidade(ent);
////                tempUm = u;
////            } else if (contInd == 2) {
////                ent = new Entidade(null, "Sysmar Software Público", "jaime@gmail.com", "www.symar.com.br", true, null, (List<Telefone>) telefone, null, "Sysmar Sistemas SA", "Sysmar Informatica", "Sysmar", "36.261.438.0001-45", "9258645455", "Sysmar Software Público ", null, null, TipoEntidade.OUTROS, 577, 21, cnae, true);
////                u.setEntidade(ent);
////                tempDois = u;
////            }
////
////            if (contInd <= 2) {
////                sup=und;
////            } else if (contInd <= 4) {
////                sup = tempDois;
////            }else{
////                sup = tempUm;
////            }
////            contInd++;
////
////            unidadeOrganizacionalFacade.salvarNovo(u);
////            hirarquiaOrganizacional = new HierarquiaOrganizacional(contInd + "", contInd, new Date(), sup, Boolean.TRUE, u);
////            unidadeOrganizacionalFacade.atribuiMascara(hirarquiaOrganizacional,contInd);
////            hierarquiaOrganizacionalFacade.salvarNovo(hirarquiaOrganizacional);
////        }
//    }
}
