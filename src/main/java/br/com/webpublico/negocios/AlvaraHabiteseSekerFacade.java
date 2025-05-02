/*
 * Codigo gerado automaticamente em Fri Feb 11 13:51:59 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ResponsavelServico;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoConstrucao;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoEspecialidadeServico;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ResponsavelServicoFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
public class AlvaraHabiteseSekerFacade extends AbstractFacade<AlvaraHabiteseSeker> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private UsuarioAlvaraSekerFacade usuarioAlvaraSekerFacade;
    @EJB
    private DiretorSecretarioSekerFacade diretorSecretarioSekerFacade;
    @EJB
    private ConstrucaoExistenteSekerFacade construcaoExistenteSekerFacade;
    @EJB
    private EspecializacaoSekerFacade especializacaoSekerFacade;
    @EJB
    private ResponsavelTecnicoSekerFacade responsavelTecnicoSekerFacade;
    @EJB
    private TipoUsoConstrucaoSekerFacade tipoUsoConstrucaoSekerFacade;
    @EJB
    private ProcRegularizaConstrucaoFacade procRegularizaConstrucaoFacade;
    @EJB
    private HabiteseConstrucaoFacade habiteseConstrucaoFacade;
    @EJB
    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;
    @EJB
    private ResponsavelServicoFacade responsavelServicoFacade;

    private List<AlvaraHabiteseSeker> habitesesSeker = new ArrayList<>();
    private List<AlvaraHabiteseSeker> alvarasSeker = new ArrayList<>();

    public AlvaraHabiteseSekerFacade() {
        super(AlvaraHabiteseSeker.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public List<AlvaraHabiteseSeker> buscarAlvarasSeker(Boolean habitese) {
        String sql = " select * from ALVARAHABITESESEKER " +
            " where TB_SEK_TIPODOCUMENTO = :tipoDocumento " +
            " order by IDALVARAHABITESE ";
        //(habitese ? "" : " FETCH FIRST 500 ROWS ONLY");
        Query q = em.createNativeQuery(sql, AlvaraHabiteseSeker.class);
        q.setParameter("tipoDocumento", habitese ? 2 : 1);
        return q.getResultList();
    }

    public List<CadastroImobiliario> buscarCadastroImobiliarioSekerNaTabelaSeker() {
        String sql = "select cad.*, ci.* from CadastroImobiliario ci " + " inner join Cadastro cad on cad.id = ci.id" + " where ci.inscricaoCadastral in(select replace(loteCadastro,'.','') from ALVARAHABITESESEKER)";
        Query q = em.createNativeQuery(sql, CadastroImobiliario.class);
        return q.getResultList();
    }

    public Exercicio buscarExercicioPeloAno(Integer ano) {
        String sql = "select * from EXERCICIO " + " where ANO = :ano";
        Query q = em.createNativeQuery(sql, Exercicio.class);
        q.setParameter("ano", ano);
        List<Exercicio> resultado = q.getResultList();
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    public PessoaFisica buscarTecnicoSeker(String nome) {
        Query q = em.createNativeQuery(" select p.*, pf.* from pessoafisica pf inner join pessoa p on p.id = pf.id " +
            " where trim(lower(translate(pf.nome, 'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç', 'aaaaaaaaeeeeiioooooouuuucc'))) " +
            " LIKE trim(lower(translate(:nome, 'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç', 'aaaaaaaaeeeeiioooooouuuucc'))) " +
            " order by p.id desc " + " fetch first 1 rows only ", PessoaFisica.class);
        q.setParameter("nome", "%" + nome.trim() + "%");
        List<PessoaFisica> resultado = q.getResultList();
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    public List<Atributo> buscarAtributos() {
        Query q = em.createNativeQuery(" select a.* from atributo a where a.identificacao in (:identificacoes) ", Atributo.class);
        q.setParameter("identificacoes", Lists.newArrayList("utilizacao_imovel", "padrao_residencial"));
        return q.getResultList();
    }

    public ValorPossivel buscarValorPossivel(String valor) {
        Query q = em.createNativeQuery(" select vp.* from valorpossivel vp where trim(vp.valor) = :valor ", ValorPossivel.class);
        q.setParameter("valor", valor.trim());
        List<ValorPossivel> result = q.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Construcao> buscarConstrucoesCadastroImobiliario(List<CadastroImobiliario> cadastros) {
        List<Long> idCadastros = new ArrayList<>();
        List<Construcao> retorno = new ArrayList<>();
        Long count = 1000L;
        Long count2 = cadastros.size() / count;
        Integer count3 = 0;
        for (CadastroImobiliario ci : cadastros) {
            count3++;
            idCadastros.add(ci.getId());
            if (idCadastros.size() == count || count2 >= count3) {
                Query q = em.createNativeQuery(" select c.* from construcao c " + " where c.codigo = 1 and c.IMOVEL_ID in (:idCadastros) ", Construcao.class);
                q.setParameter("idCadastros", idCadastros);
                retorno.addAll(q.getResultList());
                idCadastros.clear();
            }
        }
        return retorno;
    }

    public void migrarAlvaras(AssistenteBarraProgresso assistente) {
        logger.info("===> START MIGRACAO SEKER <===");
        alvarasSeker = buscarAlvarasSeker(false);
        habitesesSeker = buscarAlvarasSeker(true);
        List<UsuarioAlvaraSeker> usuarios = usuarioAlvaraSekerFacade.buscarUsuariosSeker();
        Map<Integer, Exercicio> exercicios = new HashMap<>();
        Map<String, UsuarioSistema> cacheUsuarioWebpublico = new HashMap<>();
        Map<String, PessoaFisica> cacheResponsavelTeninco = new HashMap<>();
        Map<String, CadastroImobiliario> cacheCadastroImobiliario = new HashMap<>();
        Map<Long, Construcao> cacheConstrucaoCadastroImobiliario = new HashMap<>();
        List<String> responsaveisQueNaoTemNoWP = new ArrayList<>();
        List<CadastroImobiliario> cadastro = buscarCadastroImobiliarioSekerNaTabelaSeker();
        List<Construcao> construcoes = buscarConstrucoesCadastroImobiliario(cadastro);
        preencherCacheCadastro(cacheCadastroImobiliario, cadastro);
        preencherCacheConstrucoes(cacheConstrucaoCadastroImobiliario, construcoes);
        //List<DiretorSecretarioSeker> diretores = diretorSecretarioSekerFacade.buscarDiretorSecretarioSeker();
        //List<ConstrucaoExistenteSeker> construcaoExistente = construcaoExistenteSekerFacade.buscarConstrucaoExistenteSeker();
        List<EspecializacaoSeker> especializacao = especializacaoSekerFacade.buscarEspecializacaoSeker();
        List<ResponsavelTecnicoSeker> responsavelTecnico = responsavelTecnicoSekerFacade.buscarResponsavelSeker();
        List<TipoUsoConstrucaoSeker> tipoUsoConstrucao = tipoUsoConstrucaoSekerFacade.buscarTipoUsoConstrucaoSeker();
        List<Atributo> atributosJaNaBase = buscarAtributos();
        List<ValorPossivel> valoresPossiveisCadastradosPeloSeker = Lists.newArrayList();
        assistente.setDescricaoProcesso("Migração de alvaras do SEKER");
        assistente.setTotal(alvarasSeker.size());
        assistente.setExecutando(true);
        int count = 0;
        int countAlvarasSemCadastros = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        for (AlvaraHabiteseSeker alvara : alvarasSeker) {
            assistente.conta();
            count++;
            logger.info("Count alvará: " + count + " de " + alvarasSeker.size());
            ProcRegularizaConstrucao procRegularizaConstrucao = new ProcRegularizaConstrucao();
            AlvaraConstrucao alvaraConstrucao = new AlvaraConstrucao();
            try {
                migrarUsuario(cacheUsuarioWebpublico, usuarios, alvara, procRegularizaConstrucao, alvaraConstrucao);

                try {
                    if (alvara.getDataAtual() != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(alvara.getDataAtual());
                        Exercicio exercicio = getExercicio(exercicios, calendar);
                        procRegularizaConstrucao.setExercicio(exercicio);
                        alvaraConstrucao.setExercicio(exercicio);
                        if (alvara.getNumProtocolo() != null) {
                            if (alvara.getNumProtocolo().contains("/")) {
                                String numeroAno = alvara.getNumProtocolo();
                                String numero = numeroAno.substring(0, numeroAno.indexOf('/'));
                                String ano = numeroAno.substring(numeroAno.indexOf('/') + 1);
                                procRegularizaConstrucao.setNumeroProtocolo(numero.replace(".", ""));
                                procRegularizaConstrucao.setAnoProtocolo(ano);
                                alvaraConstrucao.setNumeroProtocolo(numero.replace(".", ""));
                                alvaraConstrucao.setAnoProtocolo(ano);

                            } else {
                                procRegularizaConstrucao.setNumeroProtocolo(alvara.getNumProtocolo().replace(".", ""));
                                procRegularizaConstrucao.setAnoProtocolo(exercicio.getAno().toString());
                                alvaraConstrucao.setNumeroProtocolo(alvara.getNumProtocolo().replace(".", ""));
                                alvaraConstrucao.setAnoProtocolo(exercicio.getAno().toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Erro ao setar exercício", e);
                }

                CadastroImobiliario cadastroImobiliario = getCadastroImobiliario(cacheCadastroImobiliario, alvara);

                if (cadastroImobiliario != null) {
                    procRegularizaConstrucao.setCadastroImobiliario(cadastroImobiliario);
                    procRegularizaConstrucao.setCodigo(alvara.getIdAlvaraHabitese());
                    procRegularizaConstrucao.setDataInicioObra(alvara.getDataAtual());
                    procRegularizaConstrucao.setMatriculaINSS(alvara.getNunMatInss());
                    procRegularizaConstrucao.setDataCriacao(alvara.getDataAtual());
                    try {
                        if (alvara.getaConstruir() != null) {
                            if (!alvara.getaConstruir().toLowerCase().trim().equals("x") && !alvara.getaConstruir().toLowerCase().contains("u") && !alvara.getaConstruir().toLowerCase().contains("+") && !alvara.getaConstruir().toLowerCase().contains("n")) {
                                procRegularizaConstrucao.setaConstruir(new BigDecimal(alvara.getaConstruir().replace(".", "").replace(",", ".")));
                            }
                        }
                        if (alvara.getaDemolir() != null) {
                            if (!alvara.getaDemolir().toLowerCase().trim().equals("x") && !alvara.getaDemolir().toLowerCase().contains("u") && !alvara.getaDemolir().toLowerCase().contains("+") && !alvara.getaDemolir().toLowerCase().contains("n")) {
                                procRegularizaConstrucao.setaDemolir(new BigDecimal(alvara.getaDemolir().replace(".", "").replace(",", ".")));
                            }
                        }
                    } catch (Exception e) {
                        logger.error("=====> Erro ao setar valor a demolir ou a construir do alvará -> " + alvara.getIdAlvaraHabitese(), e);
                    }
                    String requeridoPor = "";
                    if (alvara.getCpfRequerente() != null || alvara.getCnpjRequerente() != null) {
                        requeridoPor = " (Requerido por: " + (alvara.getCpfRequerente() != null ? alvara.getCpfRequerente() : alvara.getCnpjRequerente()) + ")";
                    }
                    procRegularizaConstrucao.setObservacao(alvara.getObservacao() != null ? (alvara.getObservacao() + requeridoPor) : null);
                    procRegularizaConstrucao.setSituacao(ProcRegularizaConstrucao.Situacao.FINALIZADO);

                    migrarResponsavelTecnico(cacheResponsavelTeninco, responsavelTecnico, alvara, procRegularizaConstrucao, alvaraConstrucao, especializacao, responsaveisQueNaoTemNoWP);

                    ConstrucaoAlvara construcaoAlvara = new ConstrucaoAlvara();
                    Construcao construcaoCadastro = getConstrucaoPrincipal(cacheConstrucaoCadastroImobiliario, cadastroImobiliario.getId());
                    if (construcaoCadastro != null) {
                        construcaoCadastro.setNumeroAlvara(alvara.getNumAlvara());
                        construcaoCadastro.setDataAlvara(alvara.getDataAtual());
                        construcaoCadastro.setHabitese(null);
                        construcaoCadastro.setDataHabitese(null);
                    }
                    try {
                        if (new BigDecimal(alvara.getAreaTerreno()).compareTo(BigDecimal.ZERO) > 0) {
                            construcaoAlvara.setAreaConstruida(new BigDecimal(alvara.getAreaTerreno()));
                        } else if (new BigDecimal(alvara.getConstruExistente()).compareTo(BigDecimal.ZERO) > 0) {
                            construcaoAlvara.setAreaConstruida(new BigDecimal(alvara.getConstruExistente()));
                        } else {
                            construcaoAlvara.setAreaConstruida(BigDecimal.ZERO);
                        }
                    } catch (NumberFormatException nfe) {
                        logger.error("Erro ao converter o valor: " + alvara.getAreaTerreno());
                    }
                    construcaoAlvara.setQuantidadePavimentos(Integer.parseInt(alvara.getNunPavimentos().toString()));
                    if (alvara.getCancelado().equals(0L)) {
                        alvaraConstrucao.setSituacao(AlvaraConstrucao.Situacao.FINALIZADO);
                    } else {
                        alvaraConstrucao.setSituacao(AlvaraConstrucao.Situacao.CANCELADO);
                    }
                    if (alvara.getValidade() != null && !alvara.getValidade().equals("00/00/0000")) {
                        alvaraConstrucao.setDataVencimentoCartaz(formatter.parse(alvara.getValidade()));
                    }
                    alvaraConstrucao.setDataExpedicao(alvara.getDataAtual());
                    if (alvara.getTbSekTipoUsoConstrucao() != null) {
                        popularCaracteristicaConstrucao(construcaoAlvara, alvara, atributosJaNaBase, valoresPossiveisCadastradosPeloSeker, tipoUsoConstrucao);
                    }
                    procRegularizaConstrucao = procRegularizaConstrucaoFacade.salvarRetornando(procRegularizaConstrucao);

                    alvaraConstrucao.setProcRegularizaConstrucao(procRegularizaConstrucao);
                    alvaraConstrucao.setCodigo(alvara.getIdAlvaraHabitese());
                    alvaraConstrucao.setConstrucaoAlvara(construcaoAlvara);
                    if (construcaoCadastro != null) {
                        construcaoAlvara.setConstrucao(construcaoCadastro);
                        em.merge(alvaraConstrucao.getConstrucaoAlvara().getConstrucao());
                    }

                    alvaraConstrucao.setCodigo(alvara.getIdAlvaraHabitese());

                    alvaraConstrucao = alvaraConstrucaoFacade.salvarRetornando(alvaraConstrucao);
                    adicionarHabitese(alvara, alvaraConstrucao);
                } else {
                    logger.info("=====> Não encontrou cadastro para o alvará " + alvara.getIdAlvaraHabitese());
                    countAlvarasSemCadastros++;
                }
                logger.info("Alvará " + alvara.getIdAlvaraHabitese() + " Deu bom");
            } catch (Exception e) {
                logger.error("=====> Alvará " + alvara.getIdAlvaraHabitese() + " Deu Ruim <=====", e);
                break;
            }

        }
        logger.info("===> Qtde de alvarás Seker sem cadastro imobiliário no WP: " + countAlvarasSemCadastros + " <===");
        logger.info("===> FINISH MIGRACAO SEKER <===");
        assistente.setExecutando(false);
    }

    private void adicionarHabitese(AlvaraHabiteseSeker alvara, AlvaraConstrucao alvaraConstrucao) {
        for (AlvaraHabiteseSeker habiteseSeker : habitesesSeker) {
            Habitese habitese = new Habitese();
            if (alvara.getNumero() != null && habiteseSeker.getNumAlvara() != null) {
                try {
                    if (alvara.getNumero().trim().equals(habiteseSeker.getNumAlvara().trim())) {
                        logger.info("Alvara " + alvara.getIdAlvaraHabitese() + " possui habitese. (Habitese: " + habiteseSeker.getIdAlvaraHabitese() + ")");
                        popularCaracteristicasHabitese(habiteseSeker, habitese);
                        habitese.setCodigo(habiteseSeker.getIdAlvaraHabitese());

                        if (habiteseSeker.getNumProcessoAdmin() != null) {
                            if (habiteseSeker.getNumProcessoAdmin().contains("/")) {

                                String numeroAno = habiteseSeker.getNumProcessoAdmin();
                                String numero = numeroAno.substring(0, numeroAno.indexOf('/'));
                                String ano = numeroAno.substring(numeroAno.indexOf('/') + 1);

                                habitese.setNumeroProtocolo(numero.replace(".", ""));
                                habitese.setAnoProtocolo(ano);

                            } else {
                                habitese.setNumeroProtocolo(habiteseSeker.getNumProcessoAdmin().replace(".", ""));
                                habitese.setAnoProtocolo(alvaraConstrucao.getExercicio().getAno().toString());
                            }
                        }
                        habitese.setUsuarioSistema(alvaraConstrucao.getUsuarioIncluiu());
                        habitese.setExercicio(alvaraConstrucao.getExercicio());
                        habitese.setDataLancamento(habiteseSeker.getDataAtual());
                        if (alvaraConstrucao.getConstrucaoAlvara() != null) {
                            if (alvaraConstrucao.getConstrucaoAlvara().getConstrucao() != null) {
                                alvaraConstrucao.getConstrucaoAlvara().getConstrucao().setHabitese(habitese.getCodigo() + "/" + habitese.getExercicio().getAno());
                                if (habitese.getDataLancamento() != null) {
                                    alvaraConstrucao.getConstrucaoAlvara().getConstrucao().setDataHabitese(habitese.getDataLancamento());
                                }
                                em.merge(alvaraConstrucao.getConstrucaoAlvara().getConstrucao());
                            }
                        }
                        habitese.setAlvaraConstrucao(alvaraConstrucao);
                        if (habiteseSeker.getCancelado() == 0) {
                            habitese.setSituacao(Habitese.Situacao.FINALIZADO);
                        } else {
                            habitese.setSituacao(Habitese.Situacao.CANCELADO);
                        }
                        habiteseConstrucaoFacade.salvar(habitese);
                    }
                } catch (Exception e) {
                    logger.error("Erro ao Adicionar habitese. habitese: " + habiteseSeker.getIdAlvaraHabitese() + " Alvará: " + alvara.getIdAlvaraHabitese(), e);
                    e.printStackTrace();
                }
            }
        }
    }

    private void popularCaracteristicasHabitese(AlvaraHabiteseSeker alvara, Habitese habitese) {
        CaracteristicaConstrucaoHabitese caracteristica = new CaracteristicaConstrucaoHabitese();
        habitese.setCaracteristica(caracteristica);
        habitese.setDataLancamento(alvara.getDataAtual());
        habitese.setDataVistoria(alvara.getDataVistoria());
        if (alvara.getaConstruir() != null) {
            caracteristica.setTipoConstrucao(TipoConstrucao.CONSTRUCAO);
            caracteristica.setTipoHabitese(CaracteristicaConstrucaoHabitese.TipoHabitese.TOTAL);
            caracteristica.setAreaConstruida((!"x".equals(alvara.getaConstruir().toLowerCase()) && !alvara.getaConstruir().toLowerCase().contains("u") && !alvara.getaConstruir().toLowerCase().contains("+") && !alvara.getaConstruir().toLowerCase().contains("n"))
                ? new BigDecimal(alvara.getaConstruir().replace(".", "").replace(",", "."))
                : BigDecimal.ZERO);
        } else if (alvara.getaDemolir() != null) {
            caracteristica.setTipoConstrucao(TipoConstrucao.DEMOLICAO);
            caracteristica.setTipoHabitese(CaracteristicaConstrucaoHabitese.TipoHabitese.TOTAL);
            caracteristica.setAreaConstruida((!"x".equals(alvara.getaDemolir().toLowerCase()) && !alvara.getaDemolir().toLowerCase().contains("u") && !alvara.getaDemolir().toLowerCase().contains("+") && !alvara.getaDemolir().toLowerCase().contains("n"))
                ? new BigDecimal(alvara.getaDemolir().replace(".", "").replace(",", "."))
                : BigDecimal.ZERO);
        } else {
            caracteristica.setTipoConstrucao(TipoConstrucao.REFORMA);
            caracteristica.setTipoHabitese(CaracteristicaConstrucaoHabitese.TipoHabitese.PARCIAL);
            caracteristica.setAreaConstruida(BigDecimal.ZERO);
        }

        caracteristica.setQuantidadeDePavimentos(alvara.getNunPavimentos().intValue());
        caracteristica.setHabitese(habitese);
    }

    private void popularCaracteristicaConstrucao(ConstrucaoAlvara construcaoAlvara, AlvaraHabiteseSeker alvaraSeker, List<Atributo> atributosJaNaBase, List<ValorPossivel> valoresPossiveisCadastradosPeloSeker, List<TipoUsoConstrucaoSeker> tipoUsoConstrucao) {
        try {
            List<CaracteristicasAlvaraConstrucao> caracteristicasAlvaraConstrucao = new ArrayList<>();
            for (Atributo atributo : atributosJaNaBase) {
                CaracteristicasAlvaraConstrucao caracteristica = new CaracteristicasAlvaraConstrucao();
                ValorAtributo valorAtributo = new ValorAtributo();
                ValorPossivel valorPossivel = new ValorPossivel();

                valorPossivel.setAtributo(atributo);
                for (TipoUsoConstrucaoSeker tipoUso : tipoUsoConstrucao) {
                    if (alvaraSeker.getTbSekTipoUsoConstrucao().equals(tipoUso.getId())) {
                        valorPossivel.setValor(tipoUso.getDescricao());
                        break;
                    }
                }

                valorAtributo.setAtributo(atributo);
                if (valorPossivel.getValor() != null) {
                    ValorPossivel vp = null;
                    for (ValorPossivel vPossivel : valoresPossiveisCadastradosPeloSeker) {
                        if (vPossivel.getValor().equals(valorPossivel.getValor())) {
                            vp = vPossivel;
                            valoresPossiveisCadastradosPeloSeker.add(vPossivel);
                            break;
                        }
                    }
                    if (vp == null) {
                        vp = buscarValorPossivel(valorPossivel.getValor());
                    }
                    if (vp != null) {
                        if (!valoresPossiveisCadastradosPeloSeker.contains(vp)) {
                            valoresPossiveisCadastradosPeloSeker.add(vp);
                        }
                        logger.info("Achou valor possivel na base --> " + vp.getValor());
                        valorAtributo.setValorDiscreto(vp);
                    } else {
                        logger.info("Salvando novo valor possivel --> " + valorPossivel.getValor());
                        vp = em.merge(valorPossivel);
                        valoresPossiveisCadastradosPeloSeker.add(vp);
                        valorAtributo.setValorDiscreto(vp);
                    }
                }

                caracteristica.setConstrucaoAlvara(construcaoAlvara);
                caracteristica.setAtributo(atributo);
                caracteristica.setValorAtributo(valorAtributo);

                caracteristicasAlvaraConstrucao.add(caracteristica);
            }
            construcaoAlvara.setCaracteristicas(caracteristicasAlvaraConstrucao);
        } catch (Exception e) {
            logger.error("Erro ao popular caracteristicas da construção", e);
        }
    }

    private void preencherCacheCadastro(Map<String, CadastroImobiliario> cacheCadastroImobilizario, List<CadastroImobiliario> cadastro) {
        if (!cadastro.isEmpty()) {
            for (CadastroImobiliario cadastroImobiliario : cadastro) {
                cacheCadastroImobilizario.put(cadastroImobiliario.getInscricaoCadastral(), cadastroImobiliario);
            }
        }
    }

    private void preencherCacheConstrucoes(Map<Long, Construcao> cacheConstrucao, List<Construcao> construcoes) {
        if (!construcoes.isEmpty()) {
            for (Construcao c : construcoes) {
                cacheConstrucao.put(c.getImovel().getId(), c);
            }
        }
    }

    private Construcao getConstrucaoPrincipal(Map<Long, Construcao> cacheConstrucao, Long idCadastro) {
        if (cacheConstrucao.containsKey(idCadastro)) {
            return cacheConstrucao.get(idCadastro);
        }
        return null;
    }

    private CadastroImobiliario getCadastroImobiliario(Map<String, CadastroImobiliario> cacheCadastroImobilizario, AlvaraHabiteseSeker alvara) {
        if (alvara.getLoteCadastro() != null) {
            String loteCadastro = alvara.getLoteCadastro().replaceAll("\\.", "");
            if (cacheCadastroImobilizario.containsKey(loteCadastro)) {
                return cacheCadastroImobilizario.get(loteCadastro);
            }
        }
        return null;
    }

    private Exercicio getExercicio(Map<Integer, Exercicio> exercicios, Calendar calendar) {
        Integer ano = calendar.get(Calendar.YEAR);
        if (exercicios.containsKey(ano)) {
            return exercicios.get(ano);
        }

        Exercicio exercicio = buscarExercicioPeloAno(ano);
        exercicios.put(ano, exercicio);
        return exercicio;
    }

    private void migrarResponsavelTecnico(Map<String, PessoaFisica> cacheResponsavelTeninco, List<ResponsavelTecnicoSeker> responsavelTecnico, AlvaraHabiteseSeker alvara, ProcRegularizaConstrucao procRegularizaConstrucao, AlvaraConstrucao alvaraConstrucao, List<EspecializacaoSeker> especializacoes, List<String> responsaveisQueNaoTemNoWP) {
        for (ResponsavelTecnicoSeker responsavelTecnicoSeker : responsavelTecnico) {
            if (responsavelTecnicoSeker.getId().equals(alvara.getTbSekResponsavelTecnico())) {
                TipoEspecialidadeServico tipoEspecialidadeServico = null;
                ResponsavelServico responsavelServico = new ResponsavelServico();
                responsavelServico.setCauCrea(responsavelTecnicoSeker.getCrea());
                for (TipoEspecialidadeServico especialidadeServico : TipoEspecialidadeServico.values()) {
                    for (EspecializacaoSeker especializacaoSeker : especializacoes) {
                        if (responsavelTecnicoSeker.getEspecializacaoId() != null && responsavelTecnicoSeker.getEspecializacaoId().equals(Integer.parseInt(especializacaoSeker.getId().toString()))) {
                            if (especialidadeServico.getDescricao().equals(especializacaoSeker.getField0())) {
                                tipoEspecialidadeServico = especialidadeServico;
                            } else if (especializacaoSeker.getField0().equals("Tecnologo") || especializacaoSeker.getField0().equals("Técnologo")) {
                                tipoEspecialidadeServico = TipoEspecialidadeServico.TECNOLOGO;
                            }
                        } else {
                            tipoEspecialidadeServico = TipoEspecialidadeServico.TECNICO_EM_EDIFICACOES;
                        }
                    }
                }
                if (tipoEspecialidadeServico != null) {
                    responsavelServico.setTipoEspecialidadeServico(tipoEspecialidadeServico);
                }
                if (responsavelTecnicoSeker.getNome() != null) {
                    PessoaFisica pessoaFisica = getPessoaFisica(cacheResponsavelTeninco, responsavelTecnicoSeker);
                    if (pessoaFisica == null) {
                        if (responsavelTecnicoSeker.getNome().equals("Sâmella Araujo Lopes de Melo")) {
                            pessoaFisica = buscarTecnicoSeker("SAMELA ARAUJO LOPES DE MELO");
                            cacheResponsavelTeninco.put("Sâmella Araujo Lopes de Melo", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Luis Guilherme G. O. Bacchi")) {
                            pessoaFisica = buscarTecnicoSeker("LUIS GUILHERME GUIMARAES OLIVEIRA BACCHI");
                            cacheResponsavelTeninco.put("Luis Guilherme G. O. Bacchi", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("José Cristiano Lima de Matos")) {
                            pessoaFisica = buscarTecnicoSeker("JOSÉ CRISTINO LIMA DE MATOS");
                            cacheResponsavelTeninco.put("José Cristiano Lima de Matos", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Charlei Jorge  de Oliveira Albuquerque")) {
                            pessoaFisica = buscarTecnicoSeker("CHARLEI JORGE DE OLIVEIRA ALBUQUERQUE");
                            cacheResponsavelTeninco.put("Charlei Jorge  de Oliveira Albuquerque", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Gabriel Assumpção Firmo Dantas")) {
                            pessoaFisica = buscarTecnicoSeker("GABRIEL ASSSUMPÇÃO FIRMO DANTAS");
                            cacheResponsavelTeninco.put("Gabriel Assumpção Firmo Dantas", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("João Roberto de Araújo Campos")) {
                            pessoaFisica = buscarTecnicoSeker("JOÃO ROBERTO ARAUJO CAMPOS");
                            cacheResponsavelTeninco.put("João Roberto de Araújo Campos", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Laís Veloso Ribeiro Buzolin")) {
                            pessoaFisica = buscarTecnicoSeker("LAIS VELOSO RIBEIRO");
                            cacheResponsavelTeninco.put("Laís Veloso Ribeiro Buzolin", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("N' Diogou Diene")) {
                            pessoaFisica = buscarTecnicoSeker("NDIOGOU DIENE");
                            cacheResponsavelTeninco.put("N' Diogou Diene", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Jean Carlos Moreira de Mesquita")) {
                            pessoaFisica = buscarTecnicoSeker("JEAN CARLOS MOREIRA COSTA");
                            cacheResponsavelTeninco.put("Jean Carlos Moreira de Mesquita", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Soraya Saraiva Andrade")) {
                            pessoaFisica = buscarTecnicoSeker("SORAYA SARAIVA DE ANDRADE");
                            cacheResponsavelTeninco.put("Soraya Saraiva Andrade", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Soraya Saraiva Andrade")) {
                            pessoaFisica = buscarTecnicoSeker("SORAYA SARAIVA DE ANDRADE");
                            cacheResponsavelTeninco.put("Soraya Saraiva Andrade", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Haley Márcio V. Boas da Costa")) {
                            pessoaFisica = buscarTecnicoSeker("HALEY MARCIO VILAS BOAS DA COSTA");
                            cacheResponsavelTeninco.put("Haley Márcio V. Boas da Costa", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Lidianna Sousa de Almeida Sasai")) {
                            pessoaFisica = buscarTecnicoSeker("LIDIANNA SOUSA DE ALMEIDA");
                            cacheResponsavelTeninco.put("Lidianna Sousa de Almeida Sasai", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Jurandyr Rodrigues da Silva Junior")) {
                            pessoaFisica = buscarTecnicoSeker("JURANDYR RODRIGUES DA SILVA JUNIOR");
                            cacheResponsavelTeninco.put("Jurandyr Rodrigues da Silva Junior", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Ione Maria Jalul Araújo Alexandria")) {
                            pessoaFisica = buscarTecnicoSeker("IONE MARIA JALUL ARAUJO DE ALEXANDRIA");
                            cacheResponsavelTeninco.put("Ione Maria Jalul Araújo Alexandria", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Andrea Soares Goes")) {
                            pessoaFisica = buscarTecnicoSeker("ANDREA SOARES LIMA DO NASCIMENTO");
                            cacheResponsavelTeninco.put("Andrea Soares Goes", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Luiz Alberto Círico")) {
                            pessoaFisica = buscarTecnicoSeker("LUIZ ALBERTO C BEZERRA");
                            cacheResponsavelTeninco.put("Luiz Alberto Círico", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Fabiana Raggi de Sá Sant'ana")) {
                            pessoaFisica = buscarTecnicoSeker("FABIANA RAGGI DE SA SANT ANA ");
                            cacheResponsavelTeninco.put("Fabiana Raggi de Sá Sant'ana", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Edilberto Ferreira Janssen Júnior")) {
                            pessoaFisica = buscarTecnicoSeker("EDILBERTO FERREIRA JANSEN JUNIOR");
                            cacheResponsavelTeninco.put("Edilberto Ferreira Janssen Júnior", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Francisco Ferreira da Silva Furtado Filho")) {
                            pessoaFisica = buscarTecnicoSeker("FRANCISCO FERREIRA DA SILVA FILHO");
                            cacheResponsavelTeninco.put("Francisco Ferreira da Silva Furtado Filho", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("José Alfredo Vaz de Asevedo")) {
                            pessoaFisica = buscarTecnicoSeker("JOSE ALFREDO VAZ DE AZEVEDO");
                            cacheResponsavelTeninco.put("José Alfredo Vaz de Asevedo", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Nélio Alzenir Afonso Alencar")) {
                            pessoaFisica = buscarTecnicoSeker("NELIO ALZENIR AFOSNSO ALENCAR");
                            cacheResponsavelTeninco.put("Nélio Alzenir Afonso Alencar", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Soad Farias da Franca")) {
                            pessoaFisica = buscarTecnicoSeker("SOAD FARIAS DA FRANCA");
                            cacheResponsavelTeninco.put("Soad Farias da Franca", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Francisco Everaldo de Souza Ferreira")) {
                            pessoaFisica = buscarTecnicoSeker("FRANCISCO EVERALDO DE SOUZA");
                            cacheResponsavelTeninco.put("Francisco Everaldo de Souza Ferreira", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Luiz Expedido Amaro de Freitas")) {
                            pessoaFisica = buscarTecnicoSeker("LUIZ EXPEDITO AMARO DE FREITAS");
                            cacheResponsavelTeninco.put("Luiz Expedido Amaro de Freitas", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Ricardo Emerson Jardim Rodrigues")) {
                            pessoaFisica = buscarTecnicoSeker("RICARDO EMERSON RODRIGUES");
                            cacheResponsavelTeninco.put("Ricardo Emerson Jardim Rodrigues", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Makllayne Neves Silva")) {
                            pessoaFisica = buscarTecnicoSeker("MAKLLAYNE NEVES DA SILVA");
                            cacheResponsavelTeninco.put("Makllayne Neves Silva", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Irenice S. Mourão Brandão")) {
                            pessoaFisica = buscarTecnicoSeker("IRENICE STILSON MOURAO BRANDAO");
                            cacheResponsavelTeninco.put("Irenice S. Mourão Brandão", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Haley Márcio V. Boas da Costa")) {
                            pessoaFisica = buscarTecnicoSeker("HALEY MARCIO VILAS BOAS DA COSTA");
                            cacheResponsavelTeninco.put("Haley Márcio V. Boas da Costa", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Isla Samara Silva Madeiros")) {
                            pessoaFisica = buscarTecnicoSeker("ISLA SAMARA SILVA MEDEIROS");
                            cacheResponsavelTeninco.put("Isla Samara Silva Madeiros", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Ana Carolina Camargo R. do Valle")) {
                            pessoaFisica = buscarTecnicoSeker("ANA CAROLINA CAMARGO RIBEIRO DO VALLE");
                            cacheResponsavelTeninco.put("Ana Carolina Camargo R. do Valle", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Dhefle Kaiã Sousa Macêdo")) {
                            pessoaFisica = buscarTecnicoSeker("DHEFLE KAIA SOUZA MACEDO");
                            cacheResponsavelTeninco.put("Dhefle Kaiã Sousa Macêdo", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Paulo César Barbosa de Toledo Lourenço")) {
                            pessoaFisica = buscarTecnicoSeker("PAULO CESAR BARBOSA");
                            cacheResponsavelTeninco.put("Paulo César Barbosa de Toledo Lourenço", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Kerolayne Lima Aguilar")) {
                            pessoaFisica = buscarTecnicoSeker("KAROLAYNE LIMA AGUILAR");
                            cacheResponsavelTeninco.put("Kerolayne Lima Aguilar", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Paulo Renato Noranha Dantas")) {
                            pessoaFisica = buscarTecnicoSeker("PAULO RENATO NORONHA DANTAS");
                            cacheResponsavelTeninco.put("Paulo Renato Noranha Dantas", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("WEBBER XAVIER D'AVILA LUCENA")) {
                            pessoaFisica = buscarTecnicoSeker("WEBBER XAVIER D AVILA LUCENA");
                            cacheResponsavelTeninco.put("WEBBER XAVIER D'AVILA LUCENA", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Alam Moacyr Lucena e Souza")) {
                            pessoaFisica = buscarTecnicoSeker("ALAN MOACYR LUCENA E SOUZA");
                            cacheResponsavelTeninco.put("Alam Moacyr Lucena e Souza", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Celio Monteiro  da silva Junior")) {
                            pessoaFisica = buscarTecnicoSeker("CELIO MONTEIRO DA SILVA JUNIOR");
                            cacheResponsavelTeninco.put("Celio Monteiro  da silva Junior", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Maria Alcineyde Melo de Lima Farias")) {
                            pessoaFisica = buscarTecnicoSeker("MARIA ALCINEIDE MELO DE LIMA FARIAS");
                            cacheResponsavelTeninco.put("Maria Alcineyde Melo de Lima Farias", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Reuel Barbosa Moraes da Costa")) {
                            pessoaFisica = buscarTecnicoSeker("REUEL BARBOSA MORAIS DA COSTA");
                            cacheResponsavelTeninco.put("Reuel Barbosa Moraes da Costa", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Solange Maria Malaquias Carvalho")) {
                            pessoaFisica = buscarTecnicoSeker("SOLANGE MARIA MALAQUIAS");
                            cacheResponsavelTeninco.put("Solange Maria Malaquias Carvalho", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Tariny Queiroz Valer")) {
                            pessoaFisica = buscarTecnicoSeker("THAYS QUEIROZ VALER");
                            cacheResponsavelTeninco.put("Tariny Queiroz Valer", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("João Batista dos Santos Júnior")) {
                            pessoaFisica = buscarTecnicoSeker("JOÃO BATISTA DOS SANTOS");
                            cacheResponsavelTeninco.put("João Batista dos Santos Júnior", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Gleilce Andrade Araujo de Lima")) {
                            pessoaFisica = buscarTecnicoSeker("GLEICE ANDRADE ARAUJO");
                            cacheResponsavelTeninco.put("Gleilce Andrade Araujo de Lima", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Ismael Costa Souza")) {
                            pessoaFisica = buscarTecnicoSeker("ISMAEL COSTA DE SOUZA");
                            cacheResponsavelTeninco.put("Ismael Costa Souza", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Dandara Cristtinny Brito Lima")) {
                            pessoaFisica = buscarTecnicoSeker("DANDARA CRISTINNY BRITO LIMA");
                            cacheResponsavelTeninco.put("Dandara Cristtinny Brito Lima", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Mirian Costa de Mattos Leite")) {
                            pessoaFisica = buscarTecnicoSeker("MIRIAN COSTA DE MATTOS");
                            cacheResponsavelTeninco.put("Mirian Costa de Mattos Leite", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Elenildo Correia da Costa")) {
                            pessoaFisica = buscarTecnicoSeker("ELENILDO CORREA DA COSTA");
                            cacheResponsavelTeninco.put("Elenildo Correia da Costa", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Leonardo Neder de Faro Freire")) {
                            pessoaFisica = buscarTecnicoSeker("LEONARDO NEDER DE FARO FEIRE");
                            cacheResponsavelTeninco.put("Leonardo Neder de Faro Freire", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Whendryckson Werbster de Lima Wanderley")) {
                            pessoaFisica = buscarTecnicoSeker("WHENDRYCKSON WEBSTER DE LIMA WANDERLEY");
                            cacheResponsavelTeninco.put("Whendryckson Werbster de Lima Wanderley", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Paulo Roberto Cavalcantiy")) {
                            pessoaFisica = buscarTecnicoSeker("PAULO ROBERTO CAVALCANTE");
                            cacheResponsavelTeninco.put("Paulo Roberto Cavalcanti", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("CRISTIANE SZILAGYU SALDANHA")) {
                            pessoaFisica = buscarTecnicoSeker("CRISTIANE SZILAGYI SALDANHA");
                            cacheResponsavelTeninco.put("CRISTIANE SZILAGYU SALDANHA", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Raida Machado da Silva Azevedo")) {
                            pessoaFisica = buscarTecnicoSeker("RAILDA MACHADO DA SILVA AZEVEDO");
                            cacheResponsavelTeninco.put("Raida Machado da Silva Azevedo", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Fernando Pinto de Brito Borba")) {
                            pessoaFisica = buscarTecnicoSeker("FERNANDO PINTO DE BRITO");
                            cacheResponsavelTeninco.put("Fernando Pinto de Brito Borba", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("José Augusto Leão Camilo")) {
                            pessoaFisica = buscarTecnicoSeker("JOSE AUGUSTO LEAL");
                            cacheResponsavelTeninco.put("José Augusto Leão Camilo", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Esdra Aquila Gama de Sousa")) {
                            pessoaFisica = buscarTecnicoSeker("ESDRAS AQUILA GAMA DE SOUSA");
                            cacheResponsavelTeninco.put("Esdra Aquila Gama de Sousa", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("João Paulo  Feitosa Couto")) {
                            pessoaFisica = buscarTecnicoSeker("JOAO PAULO FEITOSA COUTO");
                            cacheResponsavelTeninco.put("João Paulo  Feitosa Couto", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Josevaldo de Souza Meira")) {
                            pessoaFisica = buscarTecnicoSeker("JOSEVALDO DE SOUZA ROMERO");
                            cacheResponsavelTeninco.put("Josevaldo de Souza Meira", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Pietra de Oliveira Araújo Ribera")) {
                            pessoaFisica = buscarTecnicoSeker("PIETRA DE OLIVEIRA ARAUJO");
                            cacheResponsavelTeninco.put("Pietra de Oliveira Araújo Ribera", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Williyan Fernandes Dias")) {
                            pessoaFisica = buscarTecnicoSeker("WILLYAN FERNANDES DIAS");
                            cacheResponsavelTeninco.put("Williyan Fernandes Dias", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Geiza Gabriela de Araújo Negreiros Sasai")) {
                            pessoaFisica = buscarTecnicoSeker("GEIZA GABRIELA DE ARAUJO NEGREIROS");
                            cacheResponsavelTeninco.put("Geiza Gabriela de Araújo Negreiros Sasai", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Juliana Nunes de Gusmão Mendes")) {
                            pessoaFisica = buscarTecnicoSeker("JULIANA NUNES DE GUSMAO");
                            cacheResponsavelTeninco.put("Juliana Nunes de Gusmão Mendes", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Guilherme Valente Gondin")) {
                            pessoaFisica = buscarTecnicoSeker("GUILHERME VALENTE GONDIM");
                            cacheResponsavelTeninco.put("Guilherme Valente Gondin", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("RAFAEL DE SOUZA COSTA")) {
                            pessoaFisica = buscarTecnicoSeker("RAFAEL DE SOUSA COSTA");
                            cacheResponsavelTeninco.put("RAFAEL DE SOUZA COSTA", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Renê Sarkis Freire")) {
                            pessoaFisica = buscarTecnicoSeker("RENER SARKIS FREIRE");
                            cacheResponsavelTeninco.put("Renê Sarkis Freire", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Artur Souza da Silva - CREA: 75825724249")) {
                            pessoaFisica = buscarTecnicoSeker("ARTUR SOUZA DA SILVA");
                            cacheResponsavelTeninco.put("Artur Souza da Silva - CREA: 75825724249", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Luenna Ingredy Lessa da Silva")) {
                            pessoaFisica = buscarTecnicoSeker("LUENA INGREDY LESSA DA SILVA");
                            cacheResponsavelTeninco.put("Luenna Ingredy Lessa da Silva", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Caira Lorena Siqueira Rocha")) {
                            pessoaFisica = buscarTecnicoSeker("CAIRA L S ROCHA");
                            cacheResponsavelTeninco.put("Caira Lorena Siqueira Rocha", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Karoline Andrade Costa Castro")) {
                            pessoaFisica = buscarTecnicoSeker("KAROLINE ANDRADE COSTA");
                            cacheResponsavelTeninco.put("Karoline Andrade Costa Castro", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Domingos do Monte - CREA 16452992287")) {
                            pessoaFisica = buscarTecnicoSeker("DOMINGOS DO MONTE");
                            cacheResponsavelTeninco.put("Domingos do Monte - CREA 16452992287", pessoaFisica);
                        }
                        if (responsavelTecnicoSeker.getNome().equals("Jimena Rosana Aguilar Ameneiros")) {
                            pessoaFisica = buscarTecnicoSeker("JIMENA ROSANA AQUILAR AMINEIROS");
                            cacheResponsavelTeninco.put("Jimena Rosana Aguilar Ameneiros", pessoaFisica);
                        }
                        if (pessoaFisica == null && !responsaveisQueNaoTemNoWP.contains(responsavelTecnicoSeker.getNome())) {
                            responsaveisQueNaoTemNoWP.add(responsavelTecnicoSeker.getNome());
                        }
                    }
                    if (pessoaFisica != null) {
                        responsavelServico.setPessoa(pessoaFisica);
                        ResponsavelServico responsavel = responsavelServicoFacade.salvarRetornando(responsavelServico);
                        procRegularizaConstrucao.setResponsavelProjeto(responsavel);

                        procRegularizaConstrucao.setResponsavelExecucao(responsavel);
                        alvaraConstrucao.setResponsavelServico(responsavel);
                    }
                }
                break;
            }
        }
    }

    private PessoaFisica getPessoaFisica(Map<String, PessoaFisica> cacheResponsavelTeninco, ResponsavelTecnicoSeker responsavelTecnicoSeker) {
        String nome = responsavelTecnicoSeker.getNome();
        if (cacheResponsavelTeninco.containsKey(nome)) {
            return cacheResponsavelTeninco.get(nome);
        }
        PessoaFisica pessoaFisica = buscarTecnicoSeker(nome);
        cacheResponsavelTeninco.put(nome, pessoaFisica);
        return pessoaFisica;

    }

    private void migrarUsuario(Map<String, UsuarioSistema> cacheUsuarioWebpublico, List<UsuarioAlvaraSeker> usuarios, AlvaraHabiteseSeker alvara, ProcRegularizaConstrucao procRegularizaConstrucao, AlvaraConstrucao alvaraConstrucao) {
        UsuarioSistema usuarioSistemaWP = null;
        for (UsuarioAlvaraSeker usuarioAlvaraSeker : usuarios) {
            if (usuarioAlvaraSeker.getIdUsuario().equals(alvara.getTbUsuarioIdusuario())) {
                usuarioSistemaWP = getUsuarioSistemaWP(cacheUsuarioWebpublico, usuarioAlvaraSeker);
                if (usuarioSistemaWP != null) {
                    procRegularizaConstrucao.setUsuarioIncluiu(usuarioSistemaWP);
                    alvaraConstrucao.setUsuarioIncluiu(usuarioSistemaWP);
                }
            }
        }
    }

    private UsuarioSistema getUsuarioSistemaWP(Map<String, UsuarioSistema> cacheUsuarioWebpublico, UsuarioAlvaraSeker usuarioAlvaraSeker) {
        if (cacheUsuarioWebpublico.containsKey(usuarioAlvaraSeker.getCpfUsuario())) {
            return cacheUsuarioWebpublico.get(usuarioAlvaraSeker.getCpfUsuario());
        }
        UsuarioSistema usuarioSistema = usuarioAlvaraSekerFacade.buscarUsuariosWp(usuarioAlvaraSeker.getCpfUsuario());
        if (usuarioSistema != null) {
            cacheUsuarioWebpublico.put(usuarioAlvaraSeker.getCpfUsuario(), usuarioSistema);
            return usuarioSistema;
        }
        return null;
    }
}
