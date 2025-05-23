create or replace trigger ajust_situacao_valordivida_before
before insert or update on situacaoparcelavalordivida
begin
    pkg_ids_parcelavalordivida.newRows := pkg_ids_parcelavalordivida.empty;
end;
