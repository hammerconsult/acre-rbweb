package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 16/10/14
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
public class EstadoBemSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_ESTADO_BEM = "INSERT INTO ESTADOBEM (ID, VALORORIGINAL, IDENTIFICACAO, GRUPOBEM_ID, DETENTORAADMINISTRATIVA_ID, ESTADOBEM, DETENTORAORCAMENTARIA_ID, TIPOGRUPO, SITUACAOCONSERVACAOBEM, VALORACUMULADODEAJUSTE, VALORACUMULADODAAMORTIZACAO, VALORACUMULADODADEPRECIACAO, VALORACUMULADODAEXAUSTAO, GRUPOOBJETOCOMPRA_ID, TIPOAQUISICAOBEM, LOCALIZACAO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final List<EstadoBem> estados;
    private final SingletonGeradorId geradorDeIds;

    public EstadoBemSetter(List<EstadoBem> estados, SingletonGeradorId geradorDeIds) {
        this.estados = estados;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        EstadoBem estadoBem = estados.get(i);
        estadoBem.setId(geradorDeIds.getProximoId());

        ps.setLong(1, estadoBem.getId());
        ps.setBigDecimal(2, estadoBem.getValorOriginal());
        ps.setString(3, estadoBem.getIdentificacao());
        ps.setLong(4, estadoBem.getGrupoBem().getId());
        ps.setLong(5, estadoBem.getDetentoraAdministrativa().getId());
        ps.setString(6, estadoBem.getEstadoBem().name().toUpperCase());
        ps.setLong(7, estadoBem.getDetentoraOrcamentaria().getId());
        ps.setString(8, estadoBem.getTipoGrupo().name().toUpperCase());
        ps.setString(9, estadoBem.getSituacaoConservacaoBem().name().toUpperCase());
        ps.setBigDecimal(10, estadoBem.getValorAcumuladoDeAjuste());
        ps.setBigDecimal(11, estadoBem.getValorAcumuladoDaAmortizacao());
        ps.setBigDecimal(12, estadoBem.getValorAcumuladoDaDepreciacao());
        ps.setBigDecimal(13, estadoBem.getValorAcumuladoDaExaustao());
        if(estadoBem.getGrupoObjetoCompra() != null){
            ps.setLong(14, estadoBem.getGrupoObjetoCompra().getId());
        }else {
            ps.setNull(14, Types.NULL );
        }
        ps.setString(15, estadoBem.getTipoAquisicaoBem().name().toUpperCase());
        ps.setString(16, estadoBem.getLocalizacao());
    }

    @Override
    public int getBatchSize() {
        return estados.size();
    }
}
