package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoGuiaPagamento;
import br.com.webpublico.enums.TipoIdentificacaoGuia;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuiaPagamentoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        GuiaPagamento guiaPagamento = new GuiaPagamento();
        guiaPagamento.setId(rs.getLong("ID"));
        Pagamento pagamento = new Pagamento();
        pagamento.setId(rs.getLong("PAGAMENTO_ID"));
        guiaPagamento.setPagamento(pagamento.getId() != 0 ? pagamento : null);
        GuiaFatura guiaFatura = new GuiaFatura();
        guiaFatura.setId(rs.getLong("GUIAFATURA_ID"));
        guiaPagamento.setGuiaFatura(guiaFatura.getId() != 0 ? guiaFatura : null);
        GuiaConvenio guiaConvenio = new GuiaConvenio();
        guiaConvenio.setId(rs.getLong("GUIACONVENIO_ID"));
        guiaPagamento.setGuiaConvenio(guiaConvenio.getId() != 0 ? guiaConvenio : null);
        GuiaGPS guiaGPS = new GuiaGPS();
        guiaGPS.setId(rs.getLong("GUIAGPS_ID"));
        guiaPagamento.setGuiaGPS(guiaGPS.getId() != 0 ? guiaGPS : null);
        GuiaDARF guiaDARF = new GuiaDARF();
        guiaDARF.setId(rs.getLong("GUIADARF_ID"));
        guiaPagamento.setGuiaDARF(guiaDARF.getId() != 0 ? guiaDARF : null);
        GuiaDARFSimples guiaDARFSimples = new GuiaDARFSimples();
        guiaDARFSimples.setId(rs.getLong("GUIADARFSIMPLES_ID"));
        guiaPagamento.setGuiaDARFSimples(guiaDARFSimples.getId() != 0 ? guiaDARFSimples : null);
        guiaPagamento.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.valueOf(rs.getString("SITUACAOGUIAPAGAMENTO")));
        guiaPagamento.setNumeroAutenticacao(rs.getString("NUMEROAUTENTICACAO"));
        Pessoa pessoa = createNewPessoa();
        pessoa.setId(rs.getLong("PESSOA_ID"));
        guiaPagamento.setPessoa(pessoa.getId() != 0 ? pessoa : null);
        guiaPagamento.setTipoIdentificacaoGuia(rs.getString("TIPOIDENTIFICACAOGUIA") != null ? TipoIdentificacaoGuia.valueOf(rs.getString("TIPOIDENTIFICACAOGUIA")) : null);
        guiaPagamento.setCodigoIdentificacao(rs.getString("CODIGOIDENTIFICACAO"));
        guiaPagamento.setDataPagamento(rs.getDate("DATAPAGAMENTO"));
        GuiaGRU guiaGRU = new GuiaGRU();
        guiaGRU.setId(rs.getLong("GUIAGRU_ID"));
        guiaPagamento.setGuiaGRU(guiaGRU.getId() != 0 ? guiaGRU : null);
        return guiaPagamento;
    }


    public Pessoa createNewPessoa(){
        Pessoa pessoa = new Pessoa() {
            @Override
            public String getNome() {
                return null;
            }

            @Override
            public String getNomeTratamento() {
                return null;
            }

            @Override
            public String getNomeFantasia() {
                return null;
            }

            @Override
            public String getCpf_Cnpj() {
                return null;
            }

            @Override
            public String getRg_InscricaoEstadual() {
                return null;
            }

            @Override
            public String getOrgaoExpedidor() {
                return null;
            }

            @Override
            public int compareTo(Pessoa o) {
                return 0;
            }
        };
        return pessoa;
    }
}
