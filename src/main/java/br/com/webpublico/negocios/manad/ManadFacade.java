package br.com.webpublico.negocios.manad;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.manad.Manad;
import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ManadUtil;
import br.com.webpublico.util.StringUtil;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 09/06/14
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ManadFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private ManadContabilFacade manadContabilFacade;
    @EJB
    private ManadRHFacade manadRHFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    @TransactionTimeout(unit = TimeUnit.DAYS, value = 1)
    public Manad gerarArquivo(Manad manad) {
        //RECUPERAR
        criarRegistrosGeral(manad);
        //System.out.println("recuperou geral");
        manadRHFacade.recuperarInformacoesArquivos(manad);
        //System.out.println("recuperou RH");
        manadContabilFacade.recuperarInformacoesArquivos(manad);
        //System.out.println("recuperou contabil");
        //CRIA ARQUIVO
        criarConteudoArquivo(manad);
        //System.out.println("crio conteudo geral");
        manadRHFacade.criarConteudoArquivo(manad);
        //System.out.println("crio conteudo RH");
        manadContabilFacade.criarConteudoArquivo(manad);
        //System.out.println("crio conteudo Contabil");
        criarConteudoArquivoBloco9(manad);
        //System.out.println("crio conteudo geral final");
        return manad;
    }

    private void criarConteudoArquivoBloco9(Manad manad) {
        StringBuilder conteudo = new StringBuilder();

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.G990.name(), conteudo);
        ManadUtil.criaLinha(2, manad.getQuantidadeLinhaBlocoL() + "", conteudo);
        ManadUtil.quebraLinha(conteudo);

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.G9001.name(), conteudo);
        ManadUtil.criaLinha(2, "0", conteudo);
        ManadUtil.quebraLinha(conteudo);

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.G9900.name(), conteudo);
        ManadUtil.criaLinha(2, "0", conteudo);
        ManadUtil.quebraLinha(conteudo);

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.G9990.name(), conteudo);
        ManadUtil.criaLinha(2, "8", conteudo);
        ManadUtil.quebraLinha(conteudo);

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.G9999.name(), conteudo);
        ManadUtil.criaLinha(2, manad.getRegistros().size() + "", conteudo);
        ManadUtil.quebraLinha(conteudo);

        manad.setConteudo(manad.getConteudo() + conteudo.toString());
    }

    private void criarRegistrosGeral(Manad manad) {
        manad.getRegistros().clear();
        if (manad.getPrefeitura() == null) {
            throw new ExcecaoNegocioGenerica("Informe a Prefeitura");
        }
        manad.getRegistros().add(new ManadRegistro(manad.getPrefeitura(), ManadRegistro.ManadRegistroTipo.G0, ManadRegistro.ManadModulo.GERAL, manad));

        if (manad.getExercicioInicial() == null) {
            throw new ExcecaoNegocioGenerica("Informe o Exercício Inicial");
        }

        if (manad.getExercicioFinal() == null) {
            throw new ExcecaoNegocioGenerica("Informe o Exercício Final");
        }

        if (manad.getContadores().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Informe pelo menos um(a) Contador(a).");
        }
        for (Pessoa pessoa : manad.getContadores()) {
            manad.getRegistros().add(new ManadRegistro(pessoa, ManadRegistro.ManadRegistroTipo.G50, ManadRegistro.ManadModulo.GERAL, manad));
        }


        if (manad.getDesenvolvedores().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Informe pelo menos um(a) Responsável pela Empresa(a).");
        }
        for (Pessoa pessoa : manad.getDesenvolvedores()) {
            manad.getRegistros().add(new ManadRegistro(pessoa, ManadRegistro.ManadRegistroTipo.G100, ManadRegistro.ManadModulo.GERAL, manad));
        }
    }

    public Manad criarConteudoArquivo(Manad manad) {
        StringBuilder conteudo = new StringBuilder();

        conteudo.append(criarLinhaPrefeitura(manad));
        conteudo.append(criarLinhaBlocoInicial());
        conteudo.append(criarLinhasContadoresDesenvolvedores(manad));
        conteudo.append(criarLinhaBlocoFinal(manad));
        conteudo.append(criarLinhaBlocoLancamentosContabeis(manad));


        manad.setConteudo(conteudo.toString());
        return manad;
    }

    private String criarLinhaBlocoLancamentosContabeis(Manad manad) {
        StringBuilder conteudo = new StringBuilder();
        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.I001.name(), conteudo);
        String codigo = "0";
        for (ManadRegistro registro : manad.getRegistros()) {
            if (registro.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.L050)) {
                codigo = "1";
                break;
            }
        }
        ManadUtil.criaLinhaSemPipe(2, codigo, conteudo);
        ManadUtil.quebraLinha(conteudo);

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.I990.name(), conteudo);
        ManadUtil.criaLinhaSemPipe(2, "00000000002", conteudo);
        ManadUtil.quebraLinha(conteudo);
        return conteudo.toString();
    }

    private String criarLinhaBlocoFinal(Manad manad) {
        StringBuilder conteudo = new StringBuilder();
        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.I990.name(), conteudo);
        Integer quantidade = manad.getContadores().size() + manad.getDesenvolvedores().size();
        quantidade += 3;
        ManadUtil.criaLinhaSemPipe(2, quantidade + "", conteudo);
        ManadUtil.quebraLinha(conteudo);
        return conteudo.toString();
    }

    private String criarLinhaBlocoInicial() {
        StringBuilder conteudo = new StringBuilder();
        ManadUtil.criaLinha(1, "0001", conteudo);
        ManadUtil.criaLinhaSemPipe(2, "0", conteudo);
        ManadUtil.quebraLinha(conteudo);
        return conteudo.toString();
    }

    private String criarLinhasContadoresDesenvolvedores(Manad manad) {
        StringBuilder retorno = new StringBuilder();
        for (ManadRegistro registroDaVez : manad.getRegistros()) {
            if (registroDaVez.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.G50)) {
                retorno.append(criarLinhaContador(registroDaVez));
            }
            if (registroDaVez.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.G100)) {
                retorno.append(criarLinhaDesenvolvedor(registroDaVez));
            }
        }
        return retorno.toString();
    }

    private String criarLinhaDesenvolvedor(ManadRegistro registroDaVez) {
        StringBuilder retorno = new StringBuilder();
        Pessoa pessoa = (Pessoa) registroDaVez.getObjeto();
        pessoa = pessoaFacade.recuperar(pessoa.getId());
        EnderecoCorreio enderecoPessoa = ManadUtil.getEnderecoPessoa(pessoa);
        Telefone telefonePessoa = ManadUtil.getTelefonePessoa(pessoa);
        Telefone faxPessoa = ManadUtil.getFaxPessoa(pessoa);

        ManadUtil.criaLinha(1, "0100", retorno);
        ManadUtil.criaLinha(2, pessoa.getNome(), retorno);
        ManadUtil.criaLinha(3, "ANALISTA DE DESENVOLVIMENTO", retorno);
        ManadUtil.criaLinha(4, "01/01/" + registroDaVez.getManad().getExercicioInicial().getAno(), retorno);
        ManadUtil.criaLinha(5, "31/12/" + registroDaVez.getManad().getExercicioFinal().getAno(), retorno);
        ManadUtil.criaLinha(6, "", retorno);
        ManadUtil.criaLinha(7, StringUtil.removeCaracteresEspeciaisSemEspaco(pessoa.getCpf_Cnpj()), retorno);
        ManadUtil.criaLinha(8, telefonePessoa.getTelefone(), retorno);
        ManadUtil.criaLinha(9, faxPessoa != null ? faxPessoa.getTelefone() : "", retorno);
        ManadUtil.criaLinhaSemPipe(10, pessoa.getEmail() != null ? pessoa.getEmail() : "", retorno);
        ManadUtil.quebraLinha(retorno);
        return retorno.toString();
    }

    private String criarLinhaContador(ManadRegistro registroDaVez) {
        StringBuilder retorno = new StringBuilder();
        Pessoa pessoa = (Pessoa) registroDaVez.getObjeto();
        pessoa = pessoaFacade.recuperar(pessoa.getId());
        EnderecoCorreio enderecoPessoa = ManadUtil.getEnderecoPessoa(pessoa);
        Telefone telefonePessoa = ManadUtil.getTelefonePessoa(pessoa);
        Telefone faxPessoa = ManadUtil.getFaxPessoa(pessoa);

        ManadUtil.criaLinha(1, "0050", retorno);
        ManadUtil.criaLinha(2, pessoa.getNome(), retorno);
        ManadUtil.criaLinha(3, "", retorno);
        ManadUtil.criaLinha(4, StringUtil.removeCaracteresEspeciaisSemEspaco(pessoa.getCpf_Cnpj()), retorno);
        ManadUtil.criaLinha(5, "", retorno);
        ManadUtil.criaLinha(6, "", retorno);
        ManadUtil.criaLinha(7, "", retorno);
        ManadUtil.criaLinha(8, enderecoPessoa.getLogradouro(), retorno);
        ManadUtil.criaLinha(9, enderecoPessoa.getNumero(), retorno);
        ManadUtil.criaLinha(10, enderecoPessoa.getComplemento(), retorno);
        ManadUtil.criaLinha(11, enderecoPessoa.getBairro(), retorno);
        ManadUtil.criaLinha(12, enderecoPessoa.getCep(), retorno);
        ManadUtil.criaLinha(13, enderecoPessoa.getUf(), retorno);
        ManadUtil.criaLinha(14, "", retorno);
        ManadUtil.criaLinha(15, "", retorno);
        ManadUtil.criaLinha(16, telefonePessoa.getTelefone(), retorno);
        ManadUtil.criaLinha(17, faxPessoa != null ? faxPessoa.getTelefone() : "", retorno);
        ManadUtil.criaLinhaSemPipe(18, pessoa.getEmail() != null ? pessoa.getEmail() : "", retorno);
        ManadUtil.quebraLinha(retorno);
        return retorno.toString();
    }


    private String criarLinhaPrefeitura(Manad manad) {
        StringBuilder retorno = new StringBuilder();
        ManadRegistro registro = null;

        for (ManadRegistro registroDaVez : manad.getRegistros()) {
            if (registroDaVez.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.G0)) {
                registro = registroDaVez;
                break;
            }
        }
        if (registro != null) {
            Entidade entidade = (Entidade) registro.getObjeto();

            ManadUtil.criaLinha(1, "0000", retorno);
            ManadUtil.criaLinha(2, entidade.getPessoaJuridica().getNome(), retorno);
            ManadUtil.criaLinha(3, entidade.getPessoaJuridica().getCnpjSemFormatacao(), retorno);
            ManadUtil.criaLinha(4, "", retorno);
            ManadUtil.criaLinha(5, "", retorno);
            ManadUtil.criaLinha(6, "", retorno);
            ManadUtil.criaLinha(7, "", retorno);
            ManadUtil.criaLinha(8, "", retorno);
            ManadUtil.criaLinha(9, "", retorno);
            ManadUtil.criaLinha(10, "", retorno);
            ManadUtil.criaLinha(11, "", retorno);
            ManadUtil.criaLinha(12, "", retorno);
            ManadUtil.criaLinha(13, "01/01/" + registro.getManad().getExercicioInicial().getAno(), retorno);
            ManadUtil.criaLinha(14, "31/12/" + registro.getManad().getExercicioFinal().getAno(), retorno);
            ManadUtil.criaLinha(15, "003", retorno);
            ManadUtil.criaLinha(16, "61", retorno);
            ManadUtil.criaLinhaSemPipe(17, "2", retorno);
        }
        ManadUtil.quebraLinha(retorno);
        return retorno.toString();

    }


    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ManadContabilFacade getManadContabilFacade() {
        return manadContabilFacade;
    }

    public ManadRHFacade getManadRHFacade() {
        return manadRHFacade;
    }


    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }
}
