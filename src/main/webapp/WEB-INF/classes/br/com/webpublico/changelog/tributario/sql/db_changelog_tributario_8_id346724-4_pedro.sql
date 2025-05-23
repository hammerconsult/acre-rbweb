declare
    idsDetentores         lista_numero_19_casas;
    idsArquivosComposicao lista_numero_19_casas;
begin
    select detentorarquivocomposicao_id bulk collect into idsDetentores from controlelicenciamentoambiental;
    select id bulk collect
    into idsArquivosComposicao
    from arquivocomposicao
    where detentorarquivocomposicao_id in (select * from table(idsDetentores));
    update controlelicenciamentoambiental set detentorarquivocomposicao_id = null;
    update arquivocomposicao set arquivo_id = null where id in (select * from table(idsArquivosComposicao));
    delete from arquivocomposicao where id in (select * from table(idsArquivosComposicao));
    delete from detentorarquivocomposicao where id in (select * from table(idsDetentores));
end;
