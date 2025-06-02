/*
 * Codigo gerado automaticamente em Mon May 30 11:39:08 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoConvenioArquivoMensal;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import org.hibernate.Hibernate;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ConfiguracaoContabilFacade extends AbstractFacade<ConfiguracaoContabil> {

    @EJB
    HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoContabilFacade() {
        super(ConfiguracaoContabil.class);
    }

    @Override
    public ConfiguracaoContabil recuperar(Object id) {
        ConfiguracaoContabil configuracaoContabil = em.find(ConfiguracaoContabil.class, id);
        Hibernate.initialize(configuracaoContabil.getContasDespesa());
        Hibernate.initialize(configuracaoContabil.getContasReceita());
        Hibernate.initialize(configuracaoContabil.getContasContabeisTransferenciaResultado());
        Hibernate.initialize(configuracaoContabil.getContasContabeisTransferenciaAjustes());
        Hibernate.initialize(configuracaoContabil.getUsuariosGestores());
        Hibernate.initialize(configuracaoContabil.getContasReinf());
        Hibernate.initialize(configuracaoContabil.getUnidadesDocumentosBloqueados());
        Hibernate.initialize(configuracaoContabil.getFontesTesouro());
        Hibernate.initialize(configuracaoContabil.getTiposContasDespesasReinf());
        Hibernate.initialize(configuracaoContabil.getArquivosLayouts());
        return configuracaoContabil;
    }

    public ConfiguracaoContabil configuracaoContabilVigente() {
        String hql = "from ConfiguracaoContabil c where c.desde = (select max(a.desde) from ConfiguracaoContabil a)";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        return recuperar(((ConfiguracaoContabil) q.getSingleResult()).getId());
    }

    public ConfiguracaoContabil coniguracaoContabilAnterior() {
        String hql = "from ConfiguracaoContabil conf order by conf.id desc";
        Query q = getEntityManager().createQuery(hql);
        q.setMaxResults(2);
        if (q.getResultList().size() > 1) {
            ConfiguracaoContabil conf = (ConfiguracaoContabil) q.getResultList().get(1);
            return conf;
        } else {
            return null;
        }
    }

    public List<String> localizaMaioresNiveis() {
        List<String> niveisString = new ArrayList<String>();
        List<HierarquiaOrganizacional> listaH = hierarquiaOrganizacionalFacade.lista();

        for (HierarquiaOrganizacional h : listaH) {
            //System.out.println("mascara antes de qubrar " + h.getCodigo());
            String[] mascs = h.getCodigo().replace("0", "").split("\\.");

            int n = 0;

            for (String s : mascs) {
                int temp1 = Integer.parseInt(s);
                int temp2 = niveisString.indexOf(n);
                if (temp1 > temp2) {
                    try {
                        niveisString.set(n, mascs[n]);
                        n++;
                    } catch (IndexOutOfBoundsException ex) {
                        niveisString.add(n, mascs[n]);
                        n++;
                    }
                }
            }
        }

        return niveisString;
    }

    public boolean validaNiveis(ConfiguracaoContabil conf) {

        String[] partesMascaraNova = conf.getMascaraUnidadeOrganizacional().split("\\.");
        //System.out.println(partesMascaraNova);
        List<String> l = localizaMaioresNiveis();
        //System.out.println("if  " + partesMascaraNova + "  parametros :partesMascaraNova.length < l.size()" + partesMascaraNova.length + " " + l.size());
        if (partesMascaraNova.length < l.size()) {
            return false;
        }

        int x = l.size() - 1;
        //System.out.println("  " + partesMascaraNova + "  " + l);
        if ((partesMascaraNova.length >= l.size())) {
            for (int i = 0; i <= x; i++) {
                boolean b = partesMascaraNova[i].length() >= (l.get(i).length());
                //System.out.println("");
                //System.out.println("teste boolean do for e " + i + "  iagual " + b + "  " + partesMascaraNova[i] + "   -   " + l.get(i));
                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }

    public void alterarCodigosHierarquiaOrcamentaria(ConfiguracaoContabil conf) {

        List<HierarquiaOrganizacional> listaH = hierarquiaOrganizacionalFacade.retornaTodasHierarquiasOrcamentarias();

        String mascaraNova = conf.getMascaraUnidadeOrganizacional();
        for (HierarquiaOrganizacional hi : listaH) {
            String mascaraRecalculada;
            mascaraRecalculada = igualaCaracteres(mascaraNova, hi.getCodigo());
            mascaraNova.replace("#", "0");
            if (mascaraRecalculada != null) {
                mascaraRecalculada = mascaraRecalculada.replace("#", "0");
                hi.setCodigo(mascaraRecalculada);
                getEntityManager().merge(hi);
            }
        }

    }

    private String igualaCaracteres(String novaMascara, String atual) {
        StringBuilder strAtual = new StringBuilder();
        if (atual != null) {
            String[] partes = atual.split("\\.");
            int tamanhoAtual = partes.length;
            String[] partesNova = novaMascara.split("\\.");
            int tamanhoNova = partesNova.length;

            for (int i = 0; i < tamanhoNova; i++) {
                String string = "";
                int tam = partesNova[i].length();
                try {
                    if (partes[i].length() > tam) {
                        int param = partes[i].length() - tam;
                        string = calculaRecorte(partes[i], param, false);
                        string = string + ".";
                        strAtual.append(string);
                    } else if (partes[i].length() < tam) {
                        int param = tam - partes[i].length();
                        string = calculaRecorte(partes[i], param, true);
                        string = string + ".";
                        strAtual.append(string);
                    } else {
                        string = partes[i] + ".";
                        strAtual.append(string);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    string = partesNova[i] + ".";
                    strAtual.append(string);
                }
            }
            return strAtual.toString().substring(0, strAtual.toString().length() - 1);
        }
        return atual;
    }

    private String calculaRecorte(String recorte, int tamanho, boolean maior) {
        String temp = "";
        if (maior) {
            for (int x = 0; x < tamanho; x++) {
                temp = temp + "0";
            }
            return temp = temp + recorte;
        } else {
            return recorte.substring(tamanho, recorte.length());
        }

    }

    private boolean validaRecorte(String parte, int recorte) {
        String p = parte.replaceAll("[1-9]", "");
        p = p.trim();
        boolean aprovado = p.length() >= recorte;
        return aprovado;
    }

    public ConfiguracaoContabil listaConfiguracaoContabilFiltrandoVigencia(Date desde) {
        //busca a maior vigencia, sendo menor ou igual ao filtro da tela
        String hql = "select conf.*, confmod.DESDE, confmod.DTYPE from ConfiguracaoContabil conf"
            + " INNER JOIN CONFIGURACAOMODULO CONFMOD ON CONF.ID = CONFMOD.ID "
            + " WHERE CONF.ID = (SELECT MAX(CONF2.ID) FROM CONFIGURACAOMODULO CONF2 "
            + " where conf2.desde <=  to_date(:parametro, 'dd/MM/yyyy') and conf2.id in "
            + " (SELECT C.ID FROM CONFIGURACAOCONTABIL C))"
            + " order by CONF.id desc ";
        Query q = em.createNativeQuery(hql, ConfiguracaoContabil.class);
        q.setParameter("parametro", new SimpleDateFormat("dd/MM/yyyy").format(desde));
        q.setMaxResults(1);
        List<ConfiguracaoContabil> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return new ConfiguracaoContabil();
        }
        return lista.get(0);
    }

    public boolean isUnidadeOrcamentariaBloqueada(UnidadeOrganizacional uo) {
        String sql = " select ccUniDocBloq.* "
            + " from CONFIGCUNIDADEDOCBLOQ ccUniDocBloq "
            + " where ccUniDocBloq.bloqueado = 1 "
            + "   and ccUniDocBloq.unidadeorganizacional_id = :idUnidade ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", uo.getId());
        List<Object[]> retorno = q.getResultList();
        return retorno != null && !retorno.isEmpty();
    }

    public BigDecimal buscarRetencaoMaximaReinfPorContaExtraETipoArquivo(Conta contaExtraorcamentaria, TipoArquivoReinf tipoArquivoReinf) {
        String sql = " select coalesce(conf.retencaomaxima, 0) " +
            " from CONFIGCONTCONTAREINF conf " +
            "   inner join ConfiguracaoModulo cm on conf.configuracaoContabil_id = cm.id " +
            "   inner join conta c on conf.contaextra_id = c.id " +
            " where cm.desde = (select max(cModulo.desde) from ConfiguracaoContabil cCont inner join ConfiguracaoModulo cModulo on cCont.id = cModulo.id) " +
            "  and trim(c.codigo) = :codigoConta " +
            (tipoArquivoReinf != null ? " and conf.tipoArquivoReinf = :tipoArquivo " : "");
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigoConta", contaExtraorcamentaria.getCodigo().trim());
        if (tipoArquivoReinf != null) {
            q.setParameter("tipoArquivo", tipoArquivoReinf.name());
        }
        List<BigDecimal> rentencaoMaxima = q.getResultList();
        if (rentencaoMaxima.isEmpty()) {
            return null;
        }
        ;
        return rentencaoMaxima.get(0);
    }

    public List<ConfiguracaoContabilContaReinf> buscarContasReinfVigentesPorTipoArquivo(TipoArquivoReinf tipoArquivoReinf) {
        String sql = " select ccContaReinf.* " +
            " from CONFIGCONTCONTAREINF ccContaReinf " +
            "    inner join ConfiguracaoContabil cc on cc.id = ccContaReinf.configuracaoContabil_id " +
            "    inner join ConfiguracaoModulo cm on cc.id = cm.id " +
            " where cm.desde = (select max(cModulo.desde) from ConfiguracaoContabil cCont inner join ConfiguracaoModulo cModulo on cCont.id = cModulo.id ) " +
            "   and ccContaReinf.tipoArquivoReinf = :tipoArquivo ";
        Query q = em.createNativeQuery(sql, ConfiguracaoContabilContaReinf.class);
        q.setParameter("tipoArquivo", tipoArquivoReinf.name());
        return q.getResultList();
    }

    public boolean isGerarSaldoUtilizandoMicroService(String nomeColuna) {
        String sql = " select cc.id, cc." + nomeColuna +
            " from ConfiguracaoContabil cc " +
            "   inner join ConfiguracaoModulo cm on cc.id = cm.id " +
            " where cm.desde = (select max(cModulo.desde) from ConfiguracaoContabil cCont inner join ConfiguracaoModulo cModulo on cCont.id = cModulo.id ) " +
            "   and cc." + nomeColuna + " = 1 ";
        Query q = em.createNativeQuery(sql);
        return !q.getResultList().isEmpty();
    }

    public ConfiguracaoContabilArquivoLayout buscarArquivoLayouPorTipo(TipoConvenioArquivoMensal tipo) {
        String sql = " select carqlayout.* " +
            " from CONFIGCARQUIVOLAYOUT carqlayout " +
            "   inner join ConfiguracaoModulo cm on cm.id = carqlayout.configuracaoContabil_id " +
            " where cm.desde = (select max(cModulo.desde) from ConfiguracaoContabil cCont inner join ConfiguracaoModulo cModulo on cCont.id = cModulo.id) " +
            "  and carqlayout.tipo = :tipoArquivo ";
        Query q = em.createNativeQuery(sql, ConfiguracaoContabilArquivoLayout.class);
        q.setParameter("tipoArquivo", tipo.name());
        List<ConfiguracaoContabilArquivoLayout> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            ConfiguracaoContabilArquivoLayout retorno = resultado.get(0);
            if (retorno.getArquivo() != null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (ArquivoParte a : retorno.getArquivo().getPartes()) {
                    try {
                        buffer.write(a.getDados());
                    } catch (IOException ex) {
                        logger.error("Erro ao escrever arquivos partes. ", ex);
                    }
                }
                InputStream is = new ByteArrayInputStream(buffer.toByteArray());
                retorno.getArquivo().setInputStream(is);
            }
            return retorno;
        }
        return null;
    }

    public StreamedContent downloadArquivo(Arquivo arquivo) {
        if (arquivo != null) {
            return arquivoFacade.montarArquivoParaDownloadPorArquivo(arquivo);
        }
        return null;
    }

    public ContaExtraorcamentaria buscarContaPadraoInssPorExercicio(Exercicio exercicio) {
        return buscarContaExtraOrcamentariaPadraoPorExercicio("contaextrainsspadraodocliq_id", exercicio);
    }

    public ContaExtraorcamentaria buscarContaPadraoIrrfPorExercicio(Exercicio exercicio) {
        return buscarContaExtraOrcamentariaPadraoPorExercicio("contaextrairrfpadraodocliq_id", exercicio);
    }

    private ContaExtraorcamentaria buscarContaExtraOrcamentariaPadraoPorExercicio(String campoContaTabelaConfiguracaoContabil, Exercicio exercicio) {
        String sql = " select c.*,  " +
            "       ce.consignacao, " +
            "       ce.contacontabil_id, " +
            "       ce.contaextraorcamentaria_id, " +
            "       ce.tipocontaextra_id, " +
            "       ce.tipocontaextraorcamentaria, " +
            "       ce.tiporetencao_id " +
            "    from contaextraorcamentaria ce  " +
            "    inner join conta c on c.id = ce.id  " +
            "    inner join planodecontas pl on c.planodecontas_id = pl.id  " +
            "    inner join planodecontasexercicio ple on pl.id = ple.planoextraorcamentario_id " +
            "    where ple.exercicio_id = :idExercicio" +
            "      and c.codigo = (select cpadrao.codigo " +
            "                      from configuracaocontabil cc " +
            "                         inner join conta cpadrao on cpadrao.id = cc." + campoContaTabelaConfiguracaoContabil +
            "                      where rownum = 1) ";
        Query q = getEntityManager().createNativeQuery(sql, ContaExtraorcamentaria.class);
        q.setParameter("idExercicio", exercicio.getId());
        List<ContaExtraorcamentaria> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }
}
