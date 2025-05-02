/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoContabil;
import br.com.webpublico.entidades.Endereco;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.VinculoFP;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import org.junit.*;

/**
 *
 * @author Munif
 */
public class ConsultasIT {

    private static EJBContainer ejbC;
    private static Context ctx;
    private static PessoaFacade pessoaFacade;
    private static UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private static HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    private static EstoqueFacade estoqueFacade;
    private static ConfiguracaoContabil configuracaoContabil;
    private static EnderecoFacade enderecoFacade;
    private static Endereco endereco;
    private static Entidade entidade;
    private static EntidadeFacade entidadeFacade;
    private static ConfiguracaoContabilFacade configuracaoContabilFacade;
    private static BancoFacade bancoFacade;
    private static FuncoesFolhaFacade funcao;
    private static VinculoFPFacade vinculoFPFacade;

    @BeforeClass
    public static void setUpClass() throws Exception {
        try {
            String path = System.getProperty("java.class.path");
            int primeiroPonto = System.getProperty("os.name").matches(".*Linux.*")
                    ? path.indexOf(':') : path.indexOf(';');
            String caminho = path.substring(0, primeiroPonto).replace("test/classes", "web/WEB-INF/classes");
            //System.out.println("####path " + caminho);
            Map properties = new HashMap();
            properties.put(EJBContainer.MODULES, new File(caminho));
            ejbC = EJBContainer.createEJBContainer(properties);
            ctx = ejbC.getContext();
            pessoaFacade = (PessoaFacade) ctx.lookup("java:global/classes/PessoaFacade");

            unidadeOrganizacionalFacade = findReference(UnidadeOrganizacionalFacade.class);

            hierarquiaOrganizacionalFacade = (HierarquiaOrganizacionalFacadeOLD) ctx.lookup("java:global/classes/HierarquiaOrganizacionalFacade");
            entidadeFacade = (EntidadeFacade) ctx.lookup("java:global/classes/EntidadeFacade");
            enderecoFacade = (EnderecoFacade) ctx.lookup("java:global/classes/EnderecoFacade");
            configuracaoContabilFacade = (ConfiguracaoContabilFacade) ctx.lookup("java:global/classes/ConfiguracaoContabilFacade");
            bancoFacade = (BancoFacade) ctx.lookup("java:global/classes/BancoFacade");
            funcao = (FuncoesFolhaFacade) ctx.lookup("java:global/classes/FuncoesFolhaFacade");
            vinculoFPFacade = (VinculoFPFacade) ctx.lookup("java:global/classes/VinculoFPFacade");


            estoqueFacade = (EstoqueFacade) ctx.lookup("java:global/classes/EstoqueFacade");
        } catch (NamingException ex) {
//            Logger.getLogger(T02TributarioTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //public <T> Resultado<T> invocar(String nomeFuncao, Class<T> clazz, Object... args) {
    public static  <E> E findReference(Class<E> clazz) throws NamingException{
        return clazz.cast(ctx.lookup("java:global/classes/" + clazz.getSimpleName()));
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
//    @Test
//    public void meuTeste() {
//        ConfiguracaoContabil cc=unidadeOrganizacionalFacade.configuracaoContabilVigente();
//        //System.out.println (cc);
//        for (Estoque e : estoqueFacade.lista()) {
//            e.setBloqueado(null);
//            estoqueFacade.salvar(e);
//        }
    //   }
//
//    @Test
//    public void testCriarConfiguracaoContabil() throws Exception {
//        //System.out.println("Criando ConfiguracaoContabil");
//        configuracaoContabil = new ConfiguracaoContabil(new Date(), "9999.999.999.999");
//        configuracaoContabilFacade.salvar(configuracaoContabil);
//    }
//
//    @Test
//    public void testCriarEntidade() throws Exception {
//        //System.out.println("Criando Entidade");
//        long total = entidadeFacade.count();
//        entidade = new Entidade(null, "Web Público Entidade 0", "patroni@gamil.com", "www.webpublico.com.br", true, null, null, null, "Web Publico Sistemas de gestão publica LTDA", "web publico", "Web POublico", "37.261.438.0001-45", "8569888888888", "Web Público Entidade", null, null, TipoEntidade.OUTROS, 577, 21, null, true);
//        entidadeFacade.criaAssociacoesAleatorias(entidade);
//        entidadeFacade.salvarNovo(entidade);
//    }
//
//    @Test
//    public void testCriarUnidadeOrganizacional() throws Exception {
//        //System.out.println("Criando UnidadeOrganizacional");
//        endereco = enderecoFacade.umEndereco();
//        String mascara = configuracaoContabil.getMascaraUnidadeOrganizacional();
//        //cria a unidade e entidade Principal da arvore
//        Entidade ent = new Entidade(null, "Prefeitura Municipal de Rio Branco", "label", "label", true, null, null, null, "Prefeitura municipal de rio branco", "Prefeitura", "prefeitura", "34.261.438.0001-45", "8694555555", "label", null, null, TipoEntidade.OUTROS, 574, 21, null, true);
//        UnidadeOrganizacional und = new UnidadeOrganizacional("Prefeitura Municipal de Rio Branco", EsferaDoPoder.EXECUTIVO, endereco, ent);
//        HierarquiaOrganizacional hie = new HierarquiaOrganizacional(mascara.replace('9', '0'), 0, new Date(), null, true, und);
//        unidadeOrganizacionalFacade.salvarNovo(und);
//        hierarquiaOrganizacionalFacade.salvar(hie);
//    }
//    @Test
//    public void testImportaBancos() throws Exception {
//        //System.out.println("Importando Bancos");
//        FileInputStream stream = new FileInputStream("bancoscripts\\bancos_digito.csv");
//        InputStreamReader streamReader = new InputStreamReader(stream);
//        BufferedReader reader = new BufferedReader(streamReader);
//        String line = null;
//        int i = 0;
//        while ((line = reader.readLine()) != null) {
//            String partes[] = line.split(";");
//            if (partes.length > 1) {
//                String codigo = partes[0];
//                String digito = partes[1];
//                String nome = partes[2];
////                String site = "";
////                if (partes.length > 2) {
////                    site = partes[3];
////                }
//                Banco b = new Banco(nome, codigo, digito, null);
//                bancoFacade.salvarNovo(b);
//  if (b.getNumeroBanco().equals("341")) {
    //                  banco = b;
//}
//            }
//        }
//        assert (true);
//    }

    @Test
    public void testFuncoa() {
        FuncoesFolhaFacade ff =new FuncoesFolhaFacade();
        VinculoFP vinculo = (VinculoFP) vinculoFPFacade.getEntityManager().createQuery("from VinculoFP").setMaxResults(1).getSingleResult();
        //System.out.println("Obter Hora Mensal:: :: " + ff.obterHoraMensal(vinculo));
    }
}
