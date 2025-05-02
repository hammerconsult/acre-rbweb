create or replace trigger ajust_situacao_valordivida_after_row
after insert or update on situacaoparcelavalordivida for each row
begin
    pkg_ids_parcelavalordivida.newRows( pkg_ids_parcelavalordivida.newRows.count + 1 ) := :new.parcela_id;
end;
