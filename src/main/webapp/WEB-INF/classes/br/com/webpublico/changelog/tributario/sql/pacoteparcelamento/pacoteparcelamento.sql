CREATE OR REPLACE PACKAGE pacote_parcelamento AS

TYPE cda_parcelamento_record IS RECORD(
   id_parcelamento number,
   numero_cda VARCHAR2(50),
   ano_cda number,
   numero_ajuizamento VARCHAR2(50));

TYPE cda_parcelamento_table IS TABLE OF cda_parcelamento_record;

FUNCTION get_cdas_parcelamento(id_parcelamento number)
    RETURN cda_parcelamento_table
    PIPELINED;
END pacote_parcelamento;
