package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.DemonstrativoAuxilioFuneral;
import br.com.webpublico.negocios.FunerariaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static br.com.webpublico.util.DataUtil.getAnosAndDiasFormatadosPorPeriodo;

/**
 * Criado por Mateus
 * Data: 15/05/2017.
 */
@Stateless
public class DemonstrativoAuxilioFuneralFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FunerariaFacade funerariaFacade;

    public List<DemonstrativoAuxilioFuneral> buscarDados(String filtros) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select aux.nomefalecido, ")
            .append("        aux.cpffalecido, ")
            .append("        aux.rgfalecido, ")
            .append("        aux.datanascimentofalecido, ")
            .append("        aux.enderecofalecido, ")
            .append("        proc.descricao as procedencia, ")
            .append("        aux.datadoatendimento, ")
            .append("        aux.beneficio, ")
            .append("        aux.numeroguiasepultamento, ")
            .append("        aux.datafalecimento, ")
            .append("        (select sum(comp.renda) as renda from ComposicaoFamiliar comp where comp.auxiliofuneral_id = aux.id) as rendaBruta, ")
            .append("        (select sum(comp.renda) as renda from ComposicaoFamiliar comp where comp.auxiliofuneral_id = aux.id) ")
            .append("        / coalesce((select count(comp.id) as renda from ComposicaoFamiliar comp where comp.auxiliofuneral_id = aux.id), 1)  as rendaPerCapita, ")
            .append("        fun.descricao as funeraria, ")
            .append("        aux.cemiterio ")
            .append(" from auxilioFuneral aux ")
            .append(" inner join procedencia proc on aux.procedencia_id = proc.id ")
            .append(" inner join funeraria fun on aux.funeraria_id = fun.id ")
            .append(filtros)
            .append(" order by aux.nomefalecido ");
        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> resultado = q.getResultList();
        List<DemonstrativoAuxilioFuneral> toReturn = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                DemonstrativoAuxilioFuneral demonstrativoAuxilioFuneral = new DemonstrativoAuxilioFuneral();
                demonstrativoAuxilioFuneral.setNomeFalecido((String) obj[0]);
                demonstrativoAuxilioFuneral.setCpf((String) obj[1]);
                demonstrativoAuxilioFuneral.setRg((String) obj[2]);
                demonstrativoAuxilioFuneral.setDataNascimento((Date) obj[3]);
                demonstrativoAuxilioFuneral.setEndereco((String) obj[4]);
                demonstrativoAuxilioFuneral.setProcedencia((String) obj[5]);
                demonstrativoAuxilioFuneral.setDataAtendimento((Date) obj[6]);
                demonstrativoAuxilioFuneral.setBeneficios((String) obj[7]);
                demonstrativoAuxilioFuneral.setNumeroDeclaracaoObito((String) obj[8]);
                demonstrativoAuxilioFuneral.setDataFalecimento((Date) obj[9]);
                demonstrativoAuxilioFuneral.setRendaBruta((BigDecimal) obj[10]);
                demonstrativoAuxilioFuneral.setRendaPerCapita((BigDecimal) obj[11]);
                demonstrativoAuxilioFuneral.setFuneraria((String) obj[12]);
                demonstrativoAuxilioFuneral.setCemiterio((String) obj[13]);
                demonstrativoAuxilioFuneral.setIdade(buscarIdade(demonstrativoAuxilioFuneral.getDataNascimento(), demonstrativoAuxilioFuneral.getDataFalecimento()));
                toReturn.add(demonstrativoAuxilioFuneral);
            }
        }
        return toReturn;
    }


    private String buscarIdade(Date dataNascimento, Date dataFalecimento) {
        if (dataNascimento == null || dataFalecimento == null) {
            return "";
        }
        DateTime inicio = new DateTime(dataNascimento);
        DateTime fim = new DateTime(dataFalecimento);
        return getAnosAndDiasFormatadosPorPeriodo(inicio, fim, 0);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public FunerariaFacade getFunerariaFacade() {
        return funerariaFacade;
    }
}
