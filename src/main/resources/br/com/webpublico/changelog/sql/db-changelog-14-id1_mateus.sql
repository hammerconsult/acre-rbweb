declare
    NUM number(9);
    cursor c1 is 
       select
             cf.operacaoformula as OperacaoFormula,
             fid.id as id
       from formulaitemdemonstrativo fid
       INNER JOIN COMPONENTEFORMULA CF ON FID.ID = CF.FORMULAITEMDEMONSTRATIVO_ID
       where nullif(trim(cf.operacaoformula), '''') is not null;
begin
   FOR LINHA IN C1 LOOP
     update formulaItemDemonstrativo set operacaoFormula = linha.OperacaoFormula where id = linha.id;
     commit;
  end LOOP;
end;