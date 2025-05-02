create or replace trigger ajust_situacao_valordivida_after
after insert or update on situacaoparcelavalordivida
begin
for i in 1 .. pkg_ids_parcelavalordivida.newRows.count
    loop
       proc_atualiza_situacao_valordivida(pkg_ids_parcelavalordivida.newRows(i));
end loop;
end;
