/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.concursos;


import br.com.webpublico.controle.rh.concursos.ClassificacaoConcursoControlador;
import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.RG;
import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.enums.CriterioDesempate;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.enums.rh.concursos.MetodoAvaliacao;
import br.com.webpublico.enums.rh.concursos.StatusClassificacaoInscricao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ClassificacaoConcursoFacade extends AbstractFacade<ClassificacaoConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private InscricaoConcursoFacade inscricaoConcursoFacade;
    @EJB
    private ProvaConcursoFacade provaConcursoFacade;
    private CargoConcurso cargoConcurso;

    public ClassificacaoConcursoFacade() {
        super(ClassificacaoConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ClassificacaoConcurso recuperar(Object id) {
        ClassificacaoConcurso classificacaoConcurso = super.recuperar(id);
        classificacaoConcurso.getInscricoes().size();
        return classificacaoConcurso;
    }

    public ClassificacaoConcurso getUltimaClassificacao(Concurso c, CargoConcurso cargo) {
        String sql = "select c.* from classificacaoconcurso c                               " +
            "     inner join avaliacaoprovaconcurso a on a.id = c.avaliacaoprovaconcurso_id " +
            "     inner join concurso conc            on conc.id = a.concurso_id            " +
            "     inner join provaconcurso p          on a.prova_id = p.id                  " +
            "     inner join cargoconcurso cc         on cc.id = p.cargoconcurso_id         " +
            "          where cc.id = :cargo_id                                              " +
            "            and conc.id = :concurso_id                                         " +
            "       order by c.ordem desc                                                   ";
        Query q = em.createNativeQuery(sql, ClassificacaoConcurso.class);
        q.setParameter("concurso_id", c.getId());
        q.setParameter("cargo_id", cargo.getId());
        q.setMaxResults(1);
        try {
            return (ClassificacaoConcurso) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public ClassificacaoConcurso buscarClassificacaoComClassificados(Object id) {
        ClassificacaoConcurso cc = super.recuperar(id);
        cc.getInscricoes().size();
        return cc;
    }

    public CargoConcurso criarNovaClassificacaoParaOcargo(CargoConcurso cargoConcurso) {
        ClassificacaoConcurso novaClassificacao = new ClassificacaoConcurso();
        novaClassificacao.setCargo(cargoConcurso);
        cargoConcurso.setClassificacaoConcurso(novaClassificacao);
        return cargoConcurso;
    }

    private List<ClassificacaoInscricao> criarInscricoesLimpasDaClassificacao(CargoConcurso cargoConcurso) {
        List<ClassificacaoInscricao> classificacaoDosInscritos = new ArrayList<>();
        List<InscricaoConcurso> inscricoes = inscricaoConcursoFacade.getInscricoesDoCargo(cargoConcurso);

        for (InscricaoConcurso inscricaoConcurso : inscricoes) {
            ClassificacaoInscricao classificacaoInscrito = new ClassificacaoInscricao();
            classificacaoInscrito.setPontuacao(0);
            classificacaoInscrito.setMedia(BigDecimal.ZERO);
            classificacaoInscrito.setClassificacaoConcurso(cargoConcurso.getClassificacaoConcurso());
            classificacaoInscrito.setInscricaoConcurso(inscricaoConcurso);
            classificacaoInscrito.setStatus(StatusClassificacaoInscricao.FILA_DE_ESPERA);
            classificacaoDosInscritos = Util.adicionarObjetoEmLista(classificacaoDosInscritos, classificacaoInscrito);
        }

        return classificacaoDosInscritos;
    }

    private ClassificacaoInscricao criarClassificacaoInscricao(CargoConcurso cargoConcurso, Map<ClassificacaoConcursoControlador.CampoImportacao, Object> linha, String cpf, BarraProgressoAssistente assistente) {
        ClassificacaoInscricao classificacaoInscrito = new ClassificacaoInscricao();
        InscricaoConcurso inscricao = inscricaoConcursoFacade.buscarInscricaoDoCargoPorCpf(cargoConcurso, cpf);
        classificacaoInscrito.setInscricaoConcurso(inscricao != null ? inscricao : criarInscricao(cargoConcurso, linha));
        classificacaoInscrito.setMedia(BigDecimal.ZERO);
        classificacaoInscrito.setPontuacao(0);
        for (Map.Entry<ClassificacaoConcursoControlador.CampoImportacao, Object> campoImportacaoObjectEntry : linha.entrySet()) {
            switch (campoImportacaoObjectEntry.getKey()) {
                case POSICAO:
                    classificacaoInscrito.setPosicao(Integer.valueOf(StringUtil.retornaApenasNumeros((String) campoImportacaoObjectEntry.getValue())));
                    break;
                case STATUS:
                    classificacaoInscrito.setStatus(campoImportacaoObjectEntry.getKey() == null || "".equals(campoImportacaoObjectEntry.getKey()) ? StatusClassificacaoInscricao.CLASSIFICADO : StatusClassificacaoInscricao.getValue(campoImportacaoObjectEntry.getKey() + ""));
                    break;
                default:
                    break;
            }
        }
        return classificacaoInscrito;

    }


    private ClassificacaoInscricao criarClassificacaoInscricaoEmMemoria(Map<ClassificacaoConcursoControlador.CampoImportacao, Object> linha, String cpf, BarraProgressoAssistente assistente) {
        ClassificacaoInscricao classificacaoInscrito = new ClassificacaoInscricao();
        classificacaoInscrito.setInscricaoConcurso(criarInscricaoEmMemoria(linha));
        classificacaoInscrito.setMedia(BigDecimal.ZERO);
        classificacaoInscrito.setPontuacao(0);
        for (Map.Entry<ClassificacaoConcursoControlador.CampoImportacao, Object> campoImportacaoObjectEntry : linha.entrySet()) {
            switch (campoImportacaoObjectEntry.getKey()) {
                case POSICAO:
                    classificacaoInscrito.setPosicao(Integer.valueOf(StringUtil.retornaApenasNumeros((String) campoImportacaoObjectEntry.getValue())));
                    break;
                case STATUS:
                    classificacaoInscrito.setStatus(campoImportacaoObjectEntry.getKey() == null || "".equals(campoImportacaoObjectEntry.getKey()) ? StatusClassificacaoInscricao.CLASSIFICADO : StatusClassificacaoInscricao.getValue(campoImportacaoObjectEntry.getKey() + ""));
                    break;
                default:
                    break;
            }
        }
        return classificacaoInscrito;

    }

    private InscricaoConcurso criarInscricao(CargoConcurso cargoConcurso, Map<ClassificacaoConcursoControlador.CampoImportacao, Object> linha) {
        InscricaoConcurso inscricaoConcurso = new InscricaoConcurso();


        for (Map.Entry<ClassificacaoConcursoControlador.CampoImportacao, Object> campoValor : linha.entrySet()) {
            switch (campoValor.getKey()) {
                case INSCRICAO:
                    inscricaoConcurso.setNumero(Integer.valueOf(StringUtil.retornaApenasNumeros(campoValor.getValue() + "")));
                    break;
                case SEXO:
                    inscricaoConcurso.setSexo(Sexo.valueOf((campoValor.getValue() + "").toUpperCase()));
                    break;
                case NOME:
                    inscricaoConcurso.setNome(campoValor.getValue() + "");
                    break;
                case NOME_MAE:
                    inscricaoConcurso.setNomeMae(campoValor.getValue() + "");
                    break;
                case IDENTIDADE:
                    inscricaoConcurso.setRg(criarRg(campoValor.getValue() + ""));
                    break;
                case CPF:
                    inscricaoConcurso.setCpf(campoValor.getValue() + "");
                    break;
                case NASCIMENTO:
                    inscricaoConcurso.setDataNascimento((Date) campoValor.getValue());
                    break;
                case ENDERECO:
                    inscricaoConcurso.setEnderecoCorreio(criarOuRecuperarEndereco(campoValor.getValue() + ""));
                    break;
                case CEP:
                    //JÁ DEFINIDO NO ENDEREÇO
                    break;
                case TELEFONE:
                    inscricaoConcurso.setTelefone(campoValor.getValue() + "");
                    break;
                case CELULAR:
                    inscricaoConcurso.setCelular(campoValor.getValue() + "");
                    break;
                case DEFICIENTE_FISICO:
                    inscricaoConcurso.setDeficienteFisico(campoValor.getValue().equals("Sim"));
                    break;

                case RACA:
                    //determinar raça
                    break;
                case EMAIL:
                    inscricaoConcurso.setEmail(campoValor.getValue() + "");
                    break;
                default:
                    break;
            }
        }
        inscricaoConcurso.setCargoConcurso(cargoConcurso);
        inscricaoConcurso.setConcurso(cargoConcurso.getConcurso());
        adicionarInscricaoConcurso(cargoConcurso.getConcurso().getInscricoes(), inscricaoConcurso);
        inscricaoConcurso.setNumero(cargoConcurso.getConcurso().getInscricoes().size());
        return inscricaoConcurso;
    }

    private InscricaoConcurso criarInscricaoEmMemoria(Map<ClassificacaoConcursoControlador.CampoImportacao, Object> linha) {
        InscricaoConcurso inscricaoConcurso = new InscricaoConcurso();


        for (Map.Entry<ClassificacaoConcursoControlador.CampoImportacao, Object> campoValor : linha.entrySet()) {
            switch (campoValor.getKey()) {
                case INSCRICAO:
                    inscricaoConcurso.setNumero(Integer.valueOf(StringUtil.retornaApenasNumeros(campoValor.getValue() + "")));
                    break;
                case SEXO:
                    inscricaoConcurso.setSexo(Sexo.valueOf((campoValor.getValue() + "").toUpperCase()));
                    break;
                case NOME:
                    inscricaoConcurso.setNome(campoValor.getValue() + "");
                    break;
                case NOME_MAE:
                    inscricaoConcurso.setNomeMae(campoValor.getValue() + "");
                    break;
                case IDENTIDADE:
                    inscricaoConcurso.setRg(criarRg(campoValor.getValue() + ""));
                    break;
                case CPF:
                    inscricaoConcurso.setCpf(campoValor.getValue() + "");
                    break;
                case NASCIMENTO:
                    inscricaoConcurso.setDataNascimento((Date) campoValor.getValue());
                    break;
                case ENDERECO:
                    inscricaoConcurso.setEnderecoCorreio(criarOuRecuperarEndereco(campoValor.getValue() + ""));
                    break;
                case CEP:
                    //JÁ DEFINIDO NO ENDEREÇO
                    break;
                case TELEFONE:
                    inscricaoConcurso.setTelefone(campoValor.getValue() + "");
                    break;
                case CELULAR:
                    inscricaoConcurso.setCelular(campoValor.getValue() + "");
                    break;
                case DEFICIENTE_FISICO:
                    inscricaoConcurso.setDeficienteFisico(campoValor.getValue().equals("Sim"));
                    break;

                case RACA:
                    //determinar raça
                    break;
                case EMAIL:
                    inscricaoConcurso.setEmail(campoValor.getValue() + "");
                    break;
                default:
                    break;
            }
        }
        return inscricaoConcurso;
    }


    private EnderecoCorreio criarOuRecuperarEndereco(String s) {
        //TODO immplementar modo de tentar achar o número do endereço da pessoa.
        List<String> tokenizer = StringUtil.tokenizer(s, ",");
        return null;
    }

    private RG criarRg(String value) {
        //TODO quebrar a string do rg para criar o rg no sistema..
        return null;
    }

    private boolean hasInconsistenciaNaLinha(String[] linha, BarraProgressoAssistente assistente) {
        boolean hasInconsistencia = false;
        if (!Util.validarCpf((linha[1]))) {
            assistente.addMensagemErro("O CPF (" + linha[1] + ") da inscrição '" + linha[0] + "' é inválido!");
            hasInconsistencia = true;
        }
        if (!"M".equals(linha[3]) && !"F".equals(linha[3])) {
            assistente.addMensagemErro("O Sexo (" + linha[3] + ") da inscrição '" + linha[0] + "' é inválido!");
            hasInconsistencia = true;
        }
        try {
            StatusClassificacaoInscricao.valueOf(linha[8]);
        } catch (IllegalArgumentException ex) {
            assistente.addMensagemErro("O Status (" + linha[8] + ") da inscrição '" + linha[0] + "' é inválido!");
            hasInconsistencia = true;
        }
        return hasInconsistencia;
    }

    private void adicionarInscricaoConcurso(List<InscricaoConcurso> inscricoes, InscricaoConcurso inscricaoConcurso) {
        for (InscricaoConcurso ic : inscricoes) {
            if (ic.getCpf().equals(inscricaoConcurso.getCpf())) {
                return;
            }
        }
        Util.adicionarObjetoEmLista(inscricoes, inscricaoConcurso);
    }

    private List<ProvaConcurso> buscarProvasDoConcursoParaOcargo(CargoConcurso cc) {
        String sql = "     select pc.* from provaconcurso pc                                      " +
            " inner join cargoconcurso        cc on cc.id          = pc.cargoconcurso_id " +
            " inner join concurso              c on cc.concurso_id = c.id                " +
            "      where     c.id = :concurso_id                                         " +
            "        and    cc.id = :cargo_id                                            ";
        Query q = em.createNativeQuery(sql, ProvaConcurso.class);
        q.setParameter("concurso_id", cc.getConcurso().getId());
        q.setParameter("cargo_id", cc.getId());
        return q.getResultList();
    }

    private List<ProvaConcurso> buscarProvasAvaliadasDoConcursoParaOcargo(CargoConcurso cc, MetodoAvaliacao... metodos) {
        String sql = "     select pc.* from provaconcurso pc                                " +
            " inner join avaliacaoprovaconcurso apc on apc.prova_id = pc.id                 " +
            " inner join cargoconcurso        cc    on cc.id          = pc.cargoconcurso_id " +
            " inner join concurso              c    on cc.concurso_id = c.id                " +
            "      where     c.id = :concurso_id                                            " +
            "        and    cc.id = :cargo_id                                               ";
        for (MetodoAvaliacao metodo : metodos) {
            sql += " and pc.metodoavaliacao = :metodo_" + metodo;
        }


        Query q = em.createNativeQuery(sql, ProvaConcurso.class);
        q.setParameter("concurso_id", cc.getConcurso().getId());
        q.setParameter("cargo_id", cc.getId());
        for (MetodoAvaliacao metodo : metodos) {
            q.setParameter("metodo_" + metodo, metodo.name());
        }
        return q.getResultList();
    }

    public void validarProvasCadastradasAndAvaliadas(CargoConcurso cargoConcurso) throws ExcecaoNegocioGenerica {
        List<ProvaConcurso> provasCadastradas = buscarProvasDoConcursoParaOcargo(cargoConcurso);
        List<ProvaConcurso> provasAvaliadas = buscarProvasAvaliadasDoConcursoParaOcargo(cargoConcurso);

        if (provasCadastradas == null || provasCadastradas.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Não existem provas cadastradas para este cargo neste concurso. Devem haver provas cadastradas e avaliadas para poder continuar.");
        }

        if (provasAvaliadas == null || provasAvaliadas.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Não existem provas avaliadas para este cargo neste concurso. Todas as provas cadastradas devem estar avaliadas para poder continuar.");
        }

        if (provasCadastradas.size() != provasAvaliadas.size()) {
            throw new ExcecaoNegocioGenerica("Nem todas as provas deste concurso foram avaliadas. Todas as provas cadastradas devem estar avaliadas para poder continuar.");
        }
    }

    private BigDecimal getValorFormatadoComDuasCasasDecimais(float valor) {
        DecimalFormat formato = new DecimalFormat();
        formato.setMaximumFractionDigits(2);
        DecimalFormatSymbols padrao = new DecimalFormatSymbols();
        padrao.setDecimalSeparator('.');
        formato.setDecimalFormatSymbols(padrao);
        return new BigDecimal(formato.format(valor));
    }

    private CargoConcurso calcularMediaDasNotasDosCandidatosPorNota(CargoConcurso cargo) throws ExcecaoNegocioGenerica {
        List<ProvaConcurso> provasPorNotaAvaliadas = buscarProvasAvaliadasDoConcursoParaOcargo(cargo, MetodoAvaliacao.POR_NOTA);
        if (provasPorNotaAvaliadas == null || provasPorNotaAvaliadas.isEmpty()) {
            return cargo;
        }
        List<InscricaoConcurso> inscricoes = inscricaoConcursoFacade.getInscricoesDoCargo(cargo);

        for (InscricaoConcurso inscrito : inscricoes) {
            ClassificacaoInscricao classificado = cargo.getClassificacaoConcurso().getClassificado(inscrito);
            float somaDasNotas = 0f;
            for (ProvaConcurso prova : provasPorNotaAvaliadas) {
                BigDecimal notaAtual = provaConcursoFacade.buscarNotaDoCanditadoNaProva(inscrito, prova);

                somaDasNotas += Float.parseFloat("" + notaAtual);
            }

            float media = somaDasNotas / provasPorNotaAvaliadas.size();

            classificado.setMedia(getValorFormatadoComDuasCasasDecimais(media));

            cargo.getClassificacaoConcurso().setInscricoes(Util.adicionarObjetoEmLista(cargo.getClassificacaoConcurso().getInscricoes(), classificado));
        }
        return cargo;

    }

    private CargoConcurso criarPontuacaoDosCandidatosEmpatados(CargoConcurso cargoConcurso) {
        Concurso concurso = concursoFacade.recuperarConcursoComCriteriosDeDesempate(cargoConcurso.getConcurso().getId());
        List<DesempateConcurso> desempates = new ArrayList<>();
        if (concurso.existemDesempatesCadastradosPeloUsuario()) {
            desempates = concurso.getDesempatesOrdenados();
        } else {
            desempates.add(getDesempateDeIdade());
        }

        // PONTUAÇÃO INICIAL
        for (ClassificacaoInscricao ciDaVez : cargoConcurso.getClassificacaoConcurso().getInscricoes()) {
            ciDaVez.setPontuacao(ciDaVez.getMedia().multiply(BigDecimal.TEN).intValue());
        }

        for (DesempateConcurso desempate : desempates) {
            for (ClassificacaoInscricao ciDaVez : cargoConcurso.getClassificacaoConcurso().getInscricoes()) {
                ciDaVez.setPontuacaoDaRodada(ciDaVez.getPontuacao());
                if (ciDaVez.getClassificacaoConcurso().possuiOutroCandidatoEmpatado(ciDaVez)) {
                    ciDaVez.pontuarPorDesempate(desempate.getCriterioDesempate());
                    ciDaVez.addObservacoes(" >>> Classificação definida por critério de desempate: '" + desempate.getCriterioDesempate().getDescricao() + "'");
                }
            }

            for (ClassificacaoInscricao ciDaVez : cargoConcurso.getClassificacaoConcurso().getInscricoes()) {
                ciDaVez.setPontuacao(ciDaVez.getPontuacaoDaRodada());
            }
        }

        cargoConcurso.setConcurso(concurso);
        cargoConcurso = desempatarPelaIdadeCasoAindaExistamCandidatosEmpatados(cargoConcurso);

        return cargoConcurso;
    }

    private CargoConcurso atribuirPosicaoDosInscritosNaClassificacao(CargoConcurso cargoConcurso) {
        Collections.sort(cargoConcurso.getClassificacaoConcurso().getInscricoes(), cargoConcurso.getClassificacaoConcurso().getOrdenadorPorMediaAndPontuacao());

        int posicao = 1;
        for (ClassificacaoInscricao classificacaoInscricao : cargoConcurso.getClassificacaoConcurso().getInscricoes()) {
            classificacaoInscricao.setPosicao(posicao);
            classificacaoInscricao.setConvocadoEm(null);
            posicao++;
        }
        return cargoConcurso;
    }

    private CargoConcurso desclassificarCandidatosNaoAprovados(CargoConcurso cargoConcurso) {
        List<ProvaConcurso> provasAvaliadas = buscarProvasAvaliadasDoConcursoParaOcargo(cargoConcurso);
        if (provasAvaliadas == null || provasAvaliadas.isEmpty()) {
            return cargoConcurso;
        }

        List<InscricaoConcurso> inscricoes = inscricaoConcursoFacade.getInscricoesDoCargo(cargoConcurso);

        for (InscricaoConcurso inscrito : inscricoes) {
            ClassificacaoInscricao classificado = cargoConcurso.getClassificacaoConcurso().getClassificado(inscrito);
            for (ProvaConcurso prova : provasAvaliadas) {
                BigDecimal notaAtual = provaConcursoFacade.buscarNotaDoCanditadoNaProva(inscrito, prova);
                if (prova.getMetodoAvaliacao().isPorObjetivo() && notaAtual.compareTo(BigDecimal.ZERO) == 0) {
                    classificado.setStatus(StatusClassificacaoInscricao.DESCLASSIFICADO);
                    classificado.addObservacoes(" >>> Desclassificado por não ter sido aprovado na prova: '" + prova + "'");
                }

                if (prova.getMetodoAvaliacao().isPorNota() && notaAtual.compareTo(prova.getNotaMinima()) < 0) {
                    classificado.setStatus(StatusClassificacaoInscricao.DESCLASSIFICADO);
                    classificado.addObservacoes(" >>> Desclassificado por não atingir a nota mínima necessária na prova: '" + prova + "' - Nota Mínima: " + prova.getNotaMinima());
                }
            }
        }

        return cargoConcurso;
    }

    private CargoConcurso atribuirStatusNosClassificadosNormais(CargoConcurso cargoConcurso) {
        int vagasNormaisPreenchidas = 0;
        for (ClassificacaoInscricao classificacaoInscricao : cargoConcurso.getClassificacaoConcurso().getInscricoes()) {
            if (classificacaoInscricao.getStatus() != null && classificacaoInscricao.getStatus().equals(StatusClassificacaoInscricao.DESCLASSIFICADO)) {
                continue;
            }

            if (classificacaoInscricao.getInscricaoConcurso().getDeficienteFisico()) {
                continue;
            }

            if (vagasNormaisPreenchidas == cargoConcurso.getVagasDisponiveis()) {
                classificacaoInscricao.setStatus(StatusClassificacaoInscricao.FILA_DE_ESPERA);
                continue;
            }

            classificacaoInscricao.setStatus(StatusClassificacaoInscricao.CLASSIFICADO);
            classificacaoInscricao.addObservacoes(" >>> Ocupando VAGAS NORMAIS.");
            vagasNormaisPreenchidas++;
        }

        return cargoConcurso;
    }

    private CargoConcurso atribuirStatusNosClassificadosEspeciais(CargoConcurso cargoConcurso) {
        int vagasEspeciaisPreenchidas = 0;
        for (ClassificacaoInscricao classificacaoInscricao : cargoConcurso.getClassificacaoConcurso().getInscricoes()) {
            if (classificacaoInscricao.getStatus() != null && classificacaoInscricao.getStatus().equals(StatusClassificacaoInscricao.DESCLASSIFICADO)) {
                continue;
            }

            if (!classificacaoInscricao.getInscricaoConcurso().getDeficienteFisico()) {
                continue;
            }

            if (vagasEspeciaisPreenchidas == cargoConcurso.getVagasEspeciais()) {
                classificacaoInscricao.setStatus(StatusClassificacaoInscricao.FILA_DE_ESPERA);
                continue;
            }

            classificacaoInscricao.setStatus(StatusClassificacaoInscricao.CLASSIFICADO);
            classificacaoInscricao.addObservacoes(" >>> Ocupando VAGAS ESPECIAIS.");
            vagasEspeciaisPreenchidas++;
        }

        return cargoConcurso;
    }

    private CargoConcurso atribuirStatusNosClassificadosDeAcordoComNumeroDeVagas(CargoConcurso cargoConcurso) {
        cargoConcurso = atribuirStatusNosClassificadosNormais(cargoConcurso);
        cargoConcurso = atribuirStatusNosClassificadosEspeciais(cargoConcurso);
        return cargoConcurso;
    }

    public CargoConcurso gerarOrAtualizarClassificacaoDesteCargo(CargoConcurso cargoConcurso) throws ExcecaoNegocioGenerica {
        validarProvasCadastradasAndAvaliadas(cargoConcurso);

        if (cargoConcurso.getClassificacaoConcurso() == null) {
            cargoConcurso = criarNovaClassificacaoParaOcargo(cargoConcurso);
        }

        // Criar inscrições
        cargoConcurso.getClassificacaoConcurso().setInscricoes(criarInscricoesLimpasDaClassificacao(cargoConcurso));

        // Atribuir notas das provas
        cargoConcurso = calcularMediaDasNotasDosCandidatosPorNota(cargoConcurso);
        cargoConcurso = desclassificarCandidatosNaoAprovados(cargoConcurso);

        // Criar pontuação dos candidatos empatados
        cargoConcurso = criarPontuacaoDosCandidatosEmpatados(cargoConcurso);

        // Definir posição dos inscritos
        cargoConcurso = atribuirPosicaoDosInscritosNaClassificacao(cargoConcurso);
        Collections.sort(cargoConcurso.getClassificacaoConcurso().getInscricoes());

        cargoConcurso = atribuirStatusNosClassificadosDeAcordoComNumeroDeVagas(cargoConcurso);

        return cargoConcurso;
    }

    private DesempateConcurso getDesempateDeIdade() {
        DesempateConcurso d = new DesempateConcurso();
        d.setCriterioDesempate(CriterioDesempate.IDADE);
        return d;
    }

    private CargoConcurso desempatarPelaIdadeCasoAindaExistamCandidatosEmpatados(CargoConcurso cargoConcurso) {
        if (cargoConcurso.getConcurso().existeDesempateDoTipo(CriterioDesempate.IDADE)) {
            return cargoConcurso;
        }

        for (ClassificacaoInscricao inscricaoDaVez : cargoConcurso.getClassificacaoConcurso().getInscricoes()) {
            if (!inscricaoDaVez.getClassificacaoConcurso().possuiOutroCandidatoEmpatado(inscricaoDaVez)) {
                continue;
            }
            inscricaoDaVez.addObservacoes(" >>> Classificação definida pela IDADE (Padrão Nacional). O candidato mais velho deve ter prioridade em assumir o cargo.");
            inscricaoDaVez.pontuarPorDesempate(CriterioDesempate.IDADE);
        }

        for (ClassificacaoInscricao ciDaVez : cargoConcurso.getClassificacaoConcurso().getInscricoes()) {
            ciDaVez.setPontuacao(ciDaVez.getPontuacaoDaRodada());
        }


        return cargoConcurso;
    }

    public ClassificacaoInscricao buscarClassificacaoInscricao(Long id) {
        return em.find(ClassificacaoInscricao.class, id);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<CargoConcurso> importarClassificacoes(CargoConcurso selecionado, BarraProgressoAssistente assistente, Workbook workbook) throws InterruptedException {
        AsyncResult<CargoConcurso> result = new AsyncResult<>(selecionado);
        assistente.inicializa();

        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
        int rowsCount = sheet.getLastRowNum();

        Map<String, Map<ClassificacaoConcursoControlador.CampoImportacao, Object>> listaClassificados = Maps.newLinkedHashMap();

        classificarInscritos(sheet, rowsCount, listaClassificados);


        assistente.setTotal(rowsCount);
        assistente.addMensagem("Importando Classificações");
        selecionado.getClassificacaoConcurso().getInscricoes().removeAll(selecionado.getClassificacaoConcurso().getInscricoes());
        for (Map.Entry<String, Map<ClassificacaoConcursoControlador.CampoImportacao, Object>> linha : listaClassificados.entrySet()) {

            try {
                if (!hasCpfAdicionado(selecionado.getClassificacaoConcurso().getInscricoes(), linha.getKey())) {
                    ClassificacaoInscricao ci = criarClassificacaoInscricao(selecionado, linha.getValue(), linha.getKey(), assistente);
                    ci.setClassificacaoConcurso(selecionado.getClassificacaoConcurso());
                    adicionarClassificacaoInscricao(selecionado.getClassificacaoConcurso().getInscricoes(), ci);
                } else {
                    assistente.addMensagemErro("Já existe um CPF informado com o número " + linha.getKey());
                }
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
                assistente.addMensagemErro("Ocorreu uma inconsistencia na importação do arquivo na linha " + (assistente.getProcessados() + 1) + ", verifique os dados e tente novamente!");
            }
            assistente.setProcessados(assistente.getProcessados() + 1);
        }
        this.cargoConcurso = selecionado;
        return result;
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<ClassificacaoInscricao> importarClassificacoesSemConcurso(List<ClassificacaoInscricao> classificacoes, BarraProgressoAssistente assistente, Workbook workbook) throws InterruptedException {
        assistente.inicializa();

        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
        int rowsCount = sheet.getLastRowNum();

        Map<String, Map<ClassificacaoConcursoControlador.CampoImportacao, Object>> listaClassificados = Maps.newLinkedHashMap();

        classificarInscritos(sheet, rowsCount, listaClassificados);


        assistente.setTotal(rowsCount);
        assistente.addMensagem("Importando Classificações");
        for (Map.Entry<String, Map<ClassificacaoConcursoControlador.CampoImportacao, Object>> linha : listaClassificados.entrySet()) {

            try {
                ClassificacaoInscricao ci = criarClassificacaoInscricaoEmMemoria(linha.getValue(), linha.getKey(), assistente);
                adicionarIncricao(ci, classificacoes);
                logger.debug("Importando... {} ", ci.getInscricaoConcurso());
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
                assistente.addMensagemErro("Ocorreu uma inconsistencia na importação do arquivo na linha " + (assistente.getProcessados() + 1) + ", verifique os dados e tente novamente!");
            }
            assistente.setProcessados(assistente.getProcessados() + 1);
        }
        return classificacoes;
    }

    private void adicionarIncricao(ClassificacaoInscricao ci, List<ClassificacaoInscricao> classificacoes) {
        classificacoes.add(ci);
    }

    private void classificarInscritos(Sheet sheet, int rowsCount, Map<String, Map<ClassificacaoConcursoControlador.CampoImportacao, Object>> listaClassificados) {
        for (int i = 0; i <= rowsCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                break;
            }
            int colCounts = row.getLastCellNum();

            Object valorCelula = null;
            List<ClassificacaoConcursoControlador.CampoImportacao> campos = Lists.newArrayList(ClassificacaoConcursoControlador.CampoImportacao.values());
            List<Map<ClassificacaoConcursoControlador.CampoImportacao, Object>> camposClassificados = Lists.newLinkedList();
            Map mapa = Maps.newLinkedHashMap();
            String chave = "";
            for (ClassificacaoConcursoControlador.CampoImportacao campoImportacao : campos) {
                Cell cell = row.getCell(campos.indexOf(campoImportacao));
                valorCelula = getValorCell(cell);
                if (ClassificacaoConcursoControlador.CampoImportacao.CPF.equals(campoImportacao)) {
                    chave = valorCelula + "";
                }
                mapa.put(campoImportacao, valorCelula);
            }
            listaClassificados.put(chave, mapa);
        }
    }


    private Object getValorCell(Cell cell) {
        if (cell == null) {
            return "";
        }

        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            try {
                if (cell.getDateCellValue() != null) {
                    return cell.getDateCellValue();
                }
            } catch (NumberFormatException nf) {

            }
            Double valor = cell.getNumericCellValue();
            return valor.intValue() + "";
        } else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return cell.getStringCellValue();
        } else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
            return cell.getBooleanCellValue() ? "Sim " : "Não";
        } else if (Cell.CELL_TYPE_FORMULA == cell.getCellType()) {
            if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_STRING) {
                return cell.getStringCellValue();
            } else if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_NUMERIC) {
                return String.valueOf(cell.getNumericCellValue());
            } else {
                return "";
            }
        } else if (Cell.CELL_TYPE_ERROR == cell.getCellType()) {
            return "error";
        } else if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
            return "";
        } else {
            return "";

        }
    }

    private boolean hasCpfAdicionado(List<ClassificacaoInscricao> inscricoes, String cpf) {
        for (ClassificacaoInscricao ci : inscricoes) {
            if (ci.getInscricaoConcurso().getCpf().equals(cpf)) {
                return true;
            }
        }
        return false;
    }

    private void adicionarClassificacaoInscricao(List<ClassificacaoInscricao> classificacoes, ClassificacaoInscricao classificacaoInscricao) {
        if (classificacaoInscricao == null)
            return;
        for (ClassificacaoInscricao ci : classificacoes) {
            if (ci.getInscricaoConcurso().getCpf().equals(classificacaoInscricao.getInscricaoConcurso().getCpf())) {
                return;
            }
        }
        Util.adicionarObjetoEmLista(classificacoes, classificacaoInscricao);
    }

    public CargoConcurso getCargoConcurso() {
        return cargoConcurso;
    }
}
