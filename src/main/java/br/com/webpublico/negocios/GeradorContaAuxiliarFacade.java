package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SomaSubtraiSaldoContaContabil;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.enums.TipoLancamentoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Stateless
public class GeradorContaAuxiliarFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TipoContaAuxiliarFacade tipoContaAuxiliarFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private SaldoContaContabilFacade saldoContaContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private ExtensaoFonteRecursoFacade extensaoFonteRecursoFacade;

    public TipoContaAuxiliarFacade getTipoContaAuxiliarFacade() {
        return tipoContaAuxiliarFacade;
    }

    private HashMap<String, Object> montarHashObjetos(TipoContaAuxiliar tipoContaAuxiliar, Object[] obj) {
        HashMap<String, Object> retorno = new HashMap<>();
        retorno.put("CONTACONTABIL", (ContaContabil) contaFacade.recuperar(((BigDecimal) obj[0]).longValue()));
        retorno.put("UNIDADE", unidadeOrganizacionalFacade.recuperar(((BigDecimal) obj[1]).longValue()));
        retorno.put("VALOR", (BigDecimal) obj[2]);
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
            case "92":
            case "93":
                break;
            case "94":
                retorno.put("DESTINACAO", (ContaDeDestinacao) contaFacade.recuperar(((BigDecimal) obj[3]).longValue()));
                break;
            case "95":
                retorno.put("DESTINACAO", (ContaDeDestinacao) contaFacade.recuperar(((BigDecimal) obj[3]).longValue()));
                break;
            case "96":
                retorno.put("DESTINACAO", (ContaDeDestinacao) contaFacade.recuperar(((BigDecimal) obj[3]).longValue()));
                break;
            case "97":

                break;
            case "98":
                retorno.put("DESTINACAO", (ContaDeDestinacao) contaFacade.recuperar(((BigDecimal) obj[3]).longValue()));
                break;
            case "99":
                retorno.put("DESTINACAO", (ContaDeDestinacao) contaFacade.recuperar(((BigDecimal) obj[3]).longValue()));
                retorno.put("FUNCAO", funcaoFacade.recuperar(((BigDecimal) obj[4]).longValue()));
                retorno.put("SUBFUNCAO", subFuncaoFacade.recuperar(((BigDecimal) obj[5]).longValue()));
                retorno.put("CONTADESPESA", (ContaDespesa) contaFacade.recuperar(((BigDecimal) obj[6]).longValue()));
                retorno.put("EXTENSAO", extensaoFonteRecursoFacade.recuperar(((BigDecimal) obj[7]).longValue()));
                retorno.put("EXERCICIO", obj[8]);
                break;
        }
        return retorno;
    }

    public void gerarContasAuxiliares(TipoContaAuxiliar tipoContaAuxiliar, String sql) {
        Query q = em.createNativeQuery(sql);
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                HashMap<String, Object> objetos = montarHashObjetos(tipoContaAuxiliar, obj);
                ContaAuxiliar contaAuxiliar = gerarContaAuxiliarPeloTipo(tipoContaAuxiliar, objetos);
                BigDecimal valor = (BigDecimal) objetos.get("VALOR");
                if (contaAuxiliar != null) {
                    saldoContaContabilFacade.geraSaldoContaContabil(DataUtil.getDateParse("01/01/2019"),
                        contaAuxiliar,
                        (UnidadeOrganizacional) objetos.get("UNIDADE"),
                        valor.compareTo(BigDecimal.ZERO) < 0 ? TipoLancamentoContabil.DEBITO : TipoLancamentoContabil.CREDITO,
                        valor.compareTo(BigDecimal.ZERO) < 0 ? valor.multiply(new BigDecimal("-1")) : valor,
                        TipoBalancete.TRANSPORTE,
                        SomaSubtraiSaldoContaContabil.SOMA);
                }
            }
        }
    }

    private ContaAuxiliar gerarContaAuxiliarPeloTipo(TipoContaAuxiliar tipoContaAuxiliar, HashMap<String, Object> objetos) {
        ContaContabil contaContabil = (ContaContabil) objetos.get("CONTACONTABIL");
        UnidadeOrganizacional unidadeOrganizacional = (UnidadeOrganizacional) objetos.get("UNIDADE");
        ContaAuxiliar contaAuxiliar = null;
        ContaDeDestinacao contaDeDestinacao;
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                contaAuxiliar = gerarContaAuxiliar(tipoContaAuxiliar, contaContabil, UtilGeradorContaAuxiliar.gerarContaAuxiliar1(unidadeOrganizacional));
                break;
            case "92":
                contaAuxiliar = gerarContaAuxiliar(tipoContaAuxiliar, contaContabil, UtilGeradorContaAuxiliar.gerarContaAuxiliar2(unidadeOrganizacional, contaContabil.getSubSistema()));
                break;
            case "93":

                break;
            case "94":
                contaDeDestinacao = (ContaDeDestinacao) objetos.get("DESTINACAO");
                contaAuxiliar = gerarContaAuxiliar(tipoContaAuxiliar, contaContabil, UtilGeradorContaAuxiliar.gerarContaAuxiliar4(unidadeOrganizacional, contaContabil.getSubSistema(), contaDeDestinacao));
                break;
            case "95":
                contaDeDestinacao = (ContaDeDestinacao) objetos.get("DESTINACAO");
                contaAuxiliar = gerarContaAuxiliar(tipoContaAuxiliar, contaContabil, UtilGeradorContaAuxiliar.gerarContaAuxiliar5(unidadeOrganizacional, contaDeDestinacao));
                break;
            case "96":

                break;
            case "97":

                break;
            case "98":
                contaDeDestinacao = (ContaDeDestinacao) objetos.get("DESTINACAO");
                contaAuxiliar = gerarContaAuxiliar(tipoContaAuxiliar, contaContabil, UtilGeradorContaAuxiliar.gerarContaAuxiliar8(unidadeOrganizacional, contaContabil.getSubSistema(), 0, contaDeDestinacao));
                break;
            case "99":
                Funcao funcao = (Funcao) objetos.get("FUNCAO");
                SubFuncao subfuncao = (SubFuncao) objetos.get("SUBFUNCAO");
                contaDeDestinacao = (ContaDeDestinacao) objetos.get("DESTINACAO");
                ContaDespesa contaDespesa = (ContaDespesa) objetos.get("CONTADESPESA");
                ExtensaoFonteRecurso extensaoFonteRecurso = (ExtensaoFonteRecurso) objetos.get("EXTENSAO");
                Integer exercicio = ((BigDecimal) objetos.get("EXERCICIO")).intValue();
                contaAuxiliar = gerarContaAuxiliar(tipoContaAuxiliar, contaContabil, UtilGeradorContaAuxiliar.gerarContaAuxiliar9(unidadeOrganizacional,
                    funcao.getCodigo() + subfuncao.getCodigo(),
                    contaDeDestinacao,
                    contaDespesa,
                    (extensaoFonteRecurso.getCodigo().toString().startsWith("4") ? 2 :
                        extensaoFonteRecurso.getCodigo().toString().startsWith("1") ||
                            extensaoFonteRecurso.getCodigo().toString().startsWith("2") ||
                            extensaoFonteRecurso.getCodigo().toString().startsWith("3") ? 1 : 0),
                    exercicio,
                    sistemaFacade.getExercicioCorrente()));
                break;
        }
        return contaAuxiliar;
    }

    private ContaAuxiliar gerarContaAuxiliar(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil, TreeMap mapContaAuxiliar) {
        return tipoContaAuxiliarFacade.gerarMapContaAuxiliar(tipoContaAuxiliar, contaContabil, mapContaAuxiliar, sistemaFacade.getExercicioCorrente());
    }
}
