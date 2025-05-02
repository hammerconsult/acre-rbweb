package br.com.webpublico.negocios.contabil.reprocessamento.rowmapper;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.EsferaOrcamentaria;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProvisaoPPAFonteDespesaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        //SELECT ID, SOMENTELEITURA, VALOR, DESTINACAODERECURSOS_ID, ORIGEM_ID, PROVISAOPPADESPESA_ID, ESFERAORCAMENTARIA, HISTORICORAZAO,HISTORICONOTA,  EVENTOCONTABIL_ID, EXTENSAOFONTERECURSO_ID FROM PROVISAOPPAFONTE
        ProvisaoPPAFonte provisaoPPAFonte = new ProvisaoPPAFonte();
        provisaoPPAFonte.setId(resultSet.getLong("ID"));
        provisaoPPAFonte.setSomenteLeitura(resultSet.getBoolean("SOMENTELEITURA"));
        provisaoPPAFonte.setValor(resultSet.getBigDecimal("VALOR"));

        ContaDeDestinacao cd = new ContaDeDestinacao();
        cd.setId(resultSet.getLong("DESTINACAODERECURSOS_ID"));
        provisaoPPAFonte.setDestinacaoDeRecursos(cd);

        Long idOrigem = resultSet.getLong("ORIGEM_ID");
        if (idOrigem != null) {
            ProvisaoPPAFonte origem = new ProvisaoPPAFonte();
            origem.setId(idOrigem);
            provisaoPPAFonte.setOrigem(origem);
        }

        ProvisaoPPADespesa provisaoPPADespesa = new ProvisaoPPADespesa();
        provisaoPPADespesa.setId(resultSet.getLong("PROVISAOPPADESPESA_ID"));
        provisaoPPAFonte.setProvisaoPPADespesa(provisaoPPADespesa);
        provisaoPPAFonte.setEsferaOrcamentaria(EsferaOrcamentaria.valueOf(resultSet.getString("ESFERAORCAMENTARIA")));
        provisaoPPAFonte.setHistoricoRazao(resultSet.getString("HISTORICORAZAO"));
        provisaoPPAFonte.setHistoricoNota(resultSet.getString("HISTORICONOTA"));

        Long idEventoContabil = resultSet.getLong("EVENTOCONTABIL_ID");
        if (idEventoContabil != null) {
            EventoContabil eventoContabil = new EventoContabil();
            eventoContabil.setId(idEventoContabil);
            provisaoPPAFonte.setEventoContabil(eventoContabil);
        }

        Long idExtensaoFonte = resultSet.getLong("EXTENSAOFONTERECURSO_ID");
        if (idExtensaoFonte != null) {
            ExtensaoFonteRecurso extensaoFonteRecurso = new ExtensaoFonteRecurso();
            extensaoFonteRecurso.setId(idExtensaoFonte);
            provisaoPPAFonte.setExtensaoFonteRecurso(extensaoFonteRecurso);
        }
        return provisaoPPAFonte;
    }
}
