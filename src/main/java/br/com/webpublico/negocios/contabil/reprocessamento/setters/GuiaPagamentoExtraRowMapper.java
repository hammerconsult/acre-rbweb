package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoGuiaPagamento;
import br.com.webpublico.enums.TipoIdentificacaoGuia;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuiaPagamentoExtraRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        GuiaPagamentoExtra guiaPagamentoExtra = new GuiaPagamentoExtra();
        guiaPagamentoExtra.setId(rs.getLong("ID"));
        PagamentoExtra pagamentoExtra = new PagamentoExtra();
        pagamentoExtra.setId(rs.getLong("PAGAMENTOEXTRA_ID"));
        guiaPagamentoExtra.setPagamentoExtra(pagamentoExtra);
        GuiaFatura guiaFatura = new GuiaFatura();
        guiaFatura.setId(rs.getLong("GUIAFATURA_ID"));
        guiaPagamentoExtra.setGuiaFatura(guiaFatura.getId() != 0 ? guiaFatura : null);
        GuiaConvenio guiaConvenio = new GuiaConvenio();
        guiaConvenio.setId(rs.getLong("GUIACONVENIO_ID"));
        guiaPagamentoExtra.setGuiaConvenio(guiaConvenio.getId() != 0 ? guiaConvenio : null);
        GuiaGPS guiaGPS = new GuiaGPS();
        guiaGPS.setId(rs.getLong("GUIAGPS_ID"));
        guiaPagamentoExtra.setGuiaGPS(guiaGPS.getId() != 0 ? guiaGPS : null);
        GuiaDARF guiaDARF = new GuiaDARF();
        guiaDARF.setId(rs.getLong("GUIADARF_ID"));
        guiaPagamentoExtra.setGuiaDARF(guiaDARF.getId() != 0 ? guiaDARF : null);
        GuiaDARFSimples guiaDARFSimples = new GuiaDARFSimples();
        guiaDARFSimples.setId(rs.getLong("GUIADARFSIMPLES_ID"));
        guiaPagamentoExtra.setGuiaDARFSimples(guiaDARFSimples.getId() != 0 ? guiaDARFSimples : null);
        guiaPagamentoExtra.setSituacaoGuiaPagamento(SituacaoGuiaPagamento.valueOf(rs.getString("SITUACAOGUIAPAGAMENTO")));
        guiaPagamentoExtra.setNumeroAutenticacao(rs.getString("NUMEROAUTENTICACAO"));
        Pessoa pessoa = createNewPessoa();
        pessoa.setId(rs.getLong("PESSOA_ID"));
        guiaPagamentoExtra.setPessoa(pessoa.getId() != 0 ? pessoa :  null);
        guiaPagamentoExtra.setTipoIdentificacaoGuia(rs.getString("TIPOIDENTIFICACAOGUIA") != null ? TipoIdentificacaoGuia.valueOf(rs.getString("TIPOIDENTIFICACAOGUIA")) : null);
        guiaPagamentoExtra.setCodigoIdentificacao(rs.getString("CODIGOIDENTIFICACAO"));
        guiaPagamentoExtra.setDataPagamento(rs.getDate("DATAPAGAMENTO"));
        GuiaGRU guiaGRU = new GuiaGRU();
        guiaGRU.setId(rs.getLong("GUIAGRU_ID"));
        guiaPagamentoExtra.setGuiaGRU(guiaGRU.getId() != 0 ? guiaGRU : null);
        return guiaPagamentoExtra;
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
